/* @(#)SimpleDrawingView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import static java.lang.Math.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import org.jhotdraw.beans.NonnullProperty;
import org.jhotdraw.beans.OptionalProperty;
import org.jhotdraw.draw.constrain.Constrainer;
import org.jhotdraw.draw.constrain.NullConstrainer;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.event.Listener;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.HandleEvent;
import org.jhotdraw.draw.handle.HandleLevel;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class SimpleDrawingView implements DrawingView {

    @FXML
    private BorderPane handlePane;

    @FXML
    private BorderPane backgroundPane;

    @FXML
    private Group drawingPane;

    @FXML
    private BorderPane toolPane;

    private StackPane node;

    private final NonnullProperty<Drawing> drawing = new NonnullProperty<>(this, DRAWING_PROPERTY, new SimpleDrawing());
    private final NonnullProperty<Constrainer> constrainer = new NonnullProperty<>(this, CONSTRAINER_PROPERTY, new NullConstrainer());
    private final ReadOnlySetProperty<Figure> selection = new ReadOnlySetWrapper<>(this, SELECTION_PROPERTY, FXCollections.observableSet(new LinkedHashSet<Figure>())).getReadOnlyProperty();
    private boolean handlesAreValid;
    private HandleLevel detailLevel = HandleLevel.SHAPE;

    {
        drawing.addListener((observable, oldValue, newValue) -> updateDrawing(oldValue, newValue));
    }
    private final OptionalProperty<Tool> tool = new OptionalProperty<>(this, TOOL_PROPERTY);

    {
        tool.addListener((observable, oldValue, newValue) -> updateTool(oldValue, newValue));
    }
    private final OptionalProperty<Handle> activeHandle = new OptionalProperty<>(this, ACTIVE_HANDLE_PROPERTY);

    private final ReadOnlyBooleanWrapper focusedProperty = new ReadOnlyBooleanWrapper(this, FOCUSED_PROPERTY);
    private final ReadOnlyObjectWrapper<Transform> drawingToViewProperty = new ReadOnlyObjectWrapper<Transform>(this, DRAWING_TO_VIEW_PROPERTY, new Scale());
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, SCALE_FACTOR_PROPERTY, 1.0) {

        @Override
        public void set(double newValue) {
            super.set(newValue);
            if (drawingPane != null) {
                drawingPane.setScaleX(newValue);
                drawingPane.setScaleY(newValue);
            }
            drawingToViewProperty.set(new Scale(newValue, newValue));
        }
    };

    private DrawingModel model = new SimpleDrawingModel();

    private HashMap<Node, Figure> nodeToFigureMap = new HashMap<>();
    private HashMap<Figure, Node> figureToNodeMap = new HashMap<>();
    private HashSet<Figure> dirtyFigures = new HashSet<>();

    private LinkedList<Handle> selectionHandles = new LinkedList<Handle>();
    private LinkedList<Handle> secondaryHandles = new LinkedList<Handle>();

    private Runnable repainter = null;
    private Listener<HandleEvent> eventHandler;

    private class HandleEventHandler implements Listener<HandleEvent> {

        @Override
        public void handle(HandleEvent event) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public SimpleDrawingView() {
        init();
        eventHandler = createEventHandler();
    }

    protected Listener<HandleEvent> createEventHandler() {
        return new HandleEventHandler();
    }

    private void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            node = loader.load(getClass().getResourceAsStream("SimpleDrawingView.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        model.addDrawingModelListener((Listener<DrawingModelEvent>) event -> {
            dirtyFigures.add(event.getFigure());
            if (event.getParent() != null) {
                dirtyFigures.add(event.getParent());
            }
            repaint();
        });

        node.addEventFilter(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent evt) {
                        if (!node.isFocused()) {
                            node.requestFocus();
                            if (!node.getScene().getWindow().isFocused()) {
                                evt.consume();
                            }
                        }
                    }
                ;
        });
        node.setFocusTraversable(true);
        focusedProperty.bind(node.focusedProperty());

        updateDrawing(null, drawingProperty().get());
    }

    public Node getNode() {
        return node;
    }

    @Override
    public void putNode(Figure f, Node newNode) {
        dirtyFigures.add(f);
        Node oldNode = figureToNodeMap.put(f, newNode);
        if (oldNode != newNode) {
            if (oldNode != null) {
                nodeToFigureMap.remove(oldNode);
            }
            if (newNode != null) {
                nodeToFigureMap.put(newNode, f);
            }
        }
    }

    @Override
    public Node getNode(Figure f) {
        Node n = figureToNodeMap.get(f);
        if (n == null) {
            f.putNode(this);
            n = figureToNodeMap.get(f);
            if (n == null) {
                throw new IllegalStateException("Figure.putNode() must put a node. Figure="
                        + f);
            }
        }
        return n;
    }

    @Override
    public NonnullProperty<Drawing> drawingProperty() {
        return drawing;
    }

    @Override
    public NonnullProperty<Constrainer> constrainerProperty() {
        return constrainer;
    }

    private InvalidationListener invalidationListener = new InvalidationListener() {

        @Override
        public void invalidated(Observable observable) {
            updatePreferredSize();
        }

    };

    private Property<Rectangle2D> boundsProperty;

    private void updateDrawing(Drawing oldValue, Drawing newValue) {
        if (oldValue != null) {
            nodeToFigureMap.clear();
            figureToNodeMap.clear();
            drawingPane.getChildren().clear();
            dirtyFigures.clear();
            model.setRoot(null);
            if (boundsProperty != null) {
                boundsProperty.removeListener(invalidationListener);
                boundsProperty.unbind();
                boundsProperty = null;
            }
        }
        if (newValue != null) {
            handleFigureAdded(newValue);
            drawingPane.getChildren().add(getNode(newValue));
            updateView();
            model.setRoot(newValue);
            boundsProperty = Drawing.BOUNDS.propertyAt(newValue.properties());
            boundsProperty.addListener(invalidationListener);
        }
    }

    private void handleFigureAdded(Figure f) {
        dirtyFigures.add(f);
        for (Figure child : f.childrenProperty()) {
            handleFigureAdded(child);
        }
    }

    private void handleFigureRemoved(Figure f) {
        dirtyFigures.remove(f);
        for (Figure child : f.childrenProperty()) {
            handleFigureRemoved(child);
        }
        Node oldNode = figureToNodeMap.remove(f);
        if (oldNode != null) {
            nodeToFigureMap.remove(oldNode);
        }
    }

    private void updateView() {
        try {
            LinkedList<Figure> update = new LinkedList<>(dirtyFigures);
            dirtyFigures.clear();
            for (Figure f : update) {
                f.updateNode(this, getNode(f));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void repaint() {
        if (repainter == null) {
            repainter = () -> {
                repainter = null;
                updateView();
            };
            Platform.runLater(repainter);
        }
    }

    private void updatePreferredSize() {
        Rectangle2D r = drawing.get().get(Drawing.BOUNDS);
        Rectangle2D visible = new Rectangle2D(max(r.getMinX(), 0), max(r.getMinY(), 0), (r.getWidth()
                + max(r.getMinX(), 0)),
                r.getHeight() + max(r.getMinY(), 0));
        node.setPrefHeight(visible.getHeight());
        node.setPrefHeight(visible.getWidth());
    }

    @Override
    public ReadOnlyBooleanProperty focusedProperty() {
        return focusedProperty.getReadOnlyProperty();
    }

    @Override
    public OptionalProperty<Tool> toolProperty() {
        return tool;
    }

    @Override
    public OptionalProperty<Handle> activeHandleProperty() {
        return activeHandle;
    }

    private void updateTool(Optional<Tool> oldValue, Optional<Tool> newValue) {
        if (oldValue.isPresent()) {
            Tool t = oldValue.get();
            toolPane.setCenter(null);
            t.setDrawingView(null);
        }
        if (newValue.isPresent()) {
            Tool t = newValue.get();
            toolPane.setCenter(t.getNode());
            t.setDrawingView(this);
        }
    }

    @Override
    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    public Optional<Figure> findFigure(double vx, double vy) {
        Drawing dr = drawing.get();
        Figure f = findFigure((Parent) getNode(dr), viewToDrawing(vx, vy));

        return Optional.ofNullable(f);
    }

    private Figure findFigure(Parent p, Point2D pp) {
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            Point2D pl = n.parentToLocal(pp);
            if (n.contains(pl)) {
                Figure f = nodeToFigureMap.get(n);
                if (f == null) {
                    if (n instanceof Parent) {
                        f = findFigure((Parent) n, pl);
                    }
                }
                return f;
            }
        }
        return null;
    }

    public Optional<Figure> findFigureBehind(double vx, double vy, Figure figureInWay) {
        Drawing dr = drawing.get();
        Figure f = findFigureBehind((Parent) getNode(dr), viewToDrawing(vx, vy), figureInWay);

        return Optional.ofNullable(f);
    }

    private Figure findFigureBehind(Parent p, Point2D pp, Figure figureInWay) {
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            Point2D pl = n.parentToLocal(pp);
            if (n.contains(pl)) {
                Figure f = nodeToFigureMap.get(n);
                if (f == null) {
                    if (n instanceof Parent) {
                        f = findFigureBehind((Parent) n, pl, figureInWay);
                    }
                }
                if (f == figureInWay) {
                    if (n instanceof Parent) {
                        f = findFigure((Parent) n, pl);
                    } else {
                        f = null;
                    }
                } else {
                    if (n instanceof Parent) {
                        f = findFigureBehind((Parent) n, pl, figureInWay);
                    } else {
                        f = null;
                    }
                }
                return f;
            }
        }
        return null;
    }

    @Override
    public List<Figure> findFigures(double vx, double vy, double vwidth, double vheight) {
        double sf = 1 / zoomFactor.get();
        BoundingBox r = new BoundingBox(vx * sf, vy * sf, 0, vwidth * sf, vheight
                * sf, 0);
        List<Figure> list = new LinkedList<Figure>();
        return list;
    }

    /** Finds a node at the given drawingProperty coordinates.
     *
     * @param p The parentProperty of the node, which already must contain the point!
     * @param p A point given in parentProperty coordinate system
     * @return Returns the node
     */
    private void findFigures(Parent p, Bounds pp, LinkedList<Figure> found) {
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            Bounds pl = n.parentToLocal(pp);
            if (pl.contains(n.getBoundsInLocal())) { // only drill down if the parentProperty contains the point
                Figure f = nodeToFigureMap.get(n);
                if (f == null) {
                    if (n instanceof Parent) {
                        findFigures((Parent) n, pl, found);
                    }
                } else {
                    found.add(f);
                }
            }
        }
    }

    @Override
    public ReadOnlySetProperty<Figure> selectionProperty() {
        return selection;
    }

    @Override
    public ReadOnlyObjectProperty<Transform> drawingToViewProperty() {
        return drawingToViewProperty.getReadOnlyProperty();
    }

    @Override
    public DrawingModel getDrawingModel() {
        return model;
    }

    // Handles
    /**
     * Gets compatible handles.
     * @return A collection containing the handle and all compatible handles.
     */
    @Override
    public Collection<Handle> getCompatibleHandles(Handle master) {
        validateHandles();

        HashSet<Figure> owners = new HashSet<Figure>();
        LinkedList<Handle> compatibleHandles = new LinkedList<Handle>();
        owners.add(master.getFigure());
        compatibleHandles.add(master);

        for (Handle handle : getSelectionHandles()) {
            if (!owners.contains(handle.getFigure())
                    && handle.isCombinableWith(master)) {
                owners.add(handle.getFigure());
                compatibleHandles.add(handle);
            }

        }
        return compatibleHandles;

    }

    /**
     * Gets the currently active selection handles.
     */
    private java.util.List<Handle> getSelectionHandles() {
        validateHandles();
        return Collections.unmodifiableList(selectionHandles);
    }

    /**
     * Gets the currently active secondary handles.
     */
    private java.util.List<Handle> getSecondaryHandles() {
        validateHandles();
        return Collections.unmodifiableList(secondaryHandles);
    }

    /**
     * Invalidates the handles.
     */
    private void invalidateHandles() {
        if (handlesAreValid) {
            handlesAreValid = false;

            for (Handle handle : selectionHandles) {
                handle.removeHandleListener(eventHandler);
                handle.dispose();
            }

            for (Handle handle : secondaryHandles) {
                handle.removeHandleListener(eventHandler);
                handle.dispose();
            }

            selectionHandles.clear();
            secondaryHandles.clear();
            setActiveHandle(null);
        }
    }

    /**
     * Validates the handles.
     */
    private void validateHandles() {
        // Validate handles only, if they are invalid/*, and if
        // the DrawingView has a DrawingEditor.*/
        if (!handlesAreValid /*&& getEditor() != null*/) {
            handlesAreValid = true;
            selectionHandles.clear();
            while (true) {
                for (Figure figure : getSelectedFigures()) {
                    for (Handle handle : figure.createHandles(detailLevel, this)) {
                        selectionHandles.add(handle);
                        handle.addHandleListener(eventHandler);
                    }
                }

                if (selectionHandles.size() == 0 && detailLevel
                        != HandleLevel.SHAPE) {
                    // No handles are available at the desired detail level.
                    // Retry with detail level 0.
                    detailLevel = HandleLevel.SHAPE;
                    continue;
                }
                break;
            }
        }
    }
}
