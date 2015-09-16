/* @(#)SimpleDrawingView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import static java.lang.Math.*;
import java.io.IOException;
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

/**
 * FXML Controller class
 *
 * @author werni
 */
public class SimpleDrawingView implements DrawingView {

    @FXML
    private Group handlePane;

    @FXML
    private BorderPane backgroundPane;

    @FXML
    private BorderPane gridPane;

    @FXML
    private BorderPane toolPane;

    @FXML
    private Group drawingPane;

    /**
     * This is the JavaFX Node which is used to represent this drawing view. in
     * a JavaFX scene graph.
     */
    private StackPane node;

    /**
     * The drawingProperty holds the drawing that is presented by this drawing
     * view.
     */
    private final NonnullProperty<Drawing> drawing = new NonnullProperty<>(this, DRAWING_PROPERTY, new SimpleDrawing());

    /**
     * Installs a handler for changes in the drawingProperty.
     */
    {
        drawing.addListener((observable, oldValue, newValue) -> updateDrawing(oldValue, newValue));
    }

    /**
     * The constrainerProperty holds the constrainer for this drawing view
     */
    private final NonnullProperty<Constrainer> constrainer = new NonnullProperty<>(this, CONSTRAINER_PROPERTY, new NullConstrainer());
    private boolean handlesAreValid;

    /**
     * The selectionProperty holds the list of selected figures.
     */
    private final ReadOnlySetProperty<Figure> selection = new ReadOnlySetWrapper<>(this, SELECTION_PROPERTY, FXCollections.observableSet(new LinkedHashSet<Figure>())).getReadOnlyProperty();

    /**
     * Installs a handler for changes in the seletionProperty.
     */
    {
        selection.addListener((Observable o) -> {
            invalidateHandles();
            repaint();
        });
    }

    private int detailLevel = 0;

    private final OptionalProperty<Tool> tool = new OptionalProperty<>(this, TOOL_PROPERTY);

    {
        tool.addListener((observable, oldValue, newValue) -> updateTool(oldValue, newValue));
    }
    private final OptionalProperty<Handle> activeHandle = new OptionalProperty<>(this, ACTIVE_HANDLE_PROPERTY);

    /**
     * This is just a wrapper around the focusedProperty of the JavaFX Node
     * which is used to render this view.
     */
    private final ReadOnlyBooleanWrapper focused = new ReadOnlyBooleanWrapper(this, FOCUSED_PROPERTY);
    /**
     * Holds the transformation from drawing coordinates to view coordinates.
     */
    private final ReadOnlyObjectWrapper<Transform> drawingToView = new ReadOnlyObjectWrapper<>(this, DRAWING_TO_VIEW_PROPERTY, new Scale());
    /**
     * The zoom factor.
     */
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, ZOOM_FACTOR_PROPERTY, 1.0) {

        @Override
        public void set(double newValue) {
            super.set(newValue);
            Scale st = new Scale(newValue, newValue);
            if (drawingPane != null) {
                if (drawingPane.getTransforms().isEmpty()) {
                    drawingPane.getTransforms().add(st);
                } else {
                    drawingPane.getTransforms().set(0, st);
                }
            }
            drawingToView.set(st);
            updateHandles();
        }
    };

    private final DrawingModel model = new SimpleDrawingModel();

    /**
     * Maps each figure in the drawing to a JavaFX node.
     */
    private final HashMap<Node, Figure> nodeToFigureMap = new HashMap<>();
    /**
     * Maps JavaFX nodes to a figure. Note that the DrawingView may contain
     * JavaFX nodes which have no mapping. this is usually the case, when a
     * Figure is represented by multiple nodes. Then only the parent of these
     * nodes is associated with the figure.
     */
    private final HashMap<Figure, Node> figureToNodeMap = new HashMap<>();
    /**
     * This is the set of figures which are out of sync with their JavaFX node.
     */
    private final HashSet<Figure> dirtyFigures = new HashSet<>();

    /**
     * The set of all handles which were produced by selected figures.
     */
    private final LinkedList<Handle> selectionHandles = new LinkedList<>();
    /**
     * The set of all secondary handles. One handle at a time may create
     * secondary handles.
     */
    private final LinkedList<Handle> secondaryHandles = new LinkedList<>();

    private Runnable repainter = null;
    private final Listener<HandleEvent> eventHandler;

    private void invalidateFigure(Figure f) {
        dirtyFigures.add(f);
    }

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
            switch (event.getEventType()) {
            case FIGURE_ADDED:
                handleFigureAdded(event.getFigure());
                break;
            case FIGURE_REMOVED:
                handleFigureRemoved(event.getFigure());
                break;
            case FIGURE_INVALIDATED:
                handleFigureInvalidated(event.getFigure());
                break;
            case FIGURE_REQUEST_REMOVE:
                // it is not our job to remove the figure
                break;
            case PROPERTY_CHANGED:
                // the figure looks still the same
                break;
            default:
                throw new UnsupportedOperationException(event.getEventType()
                        + "not supported");
            }
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
        focused.bind(node.focusedProperty());

        drawingPane.setScaleX(zoomFactor.get());
        drawingPane.setScaleY(zoomFactor.get());

        updateDrawing(null, drawingProperty().get());
    }

    public Node getNode() {
        return node;
    }

    private void removeNode(Figure f) {
        Node oldNode = figureToNodeMap.remove(f);
        if (oldNode != null) {
            nodeToFigureMap.remove(oldNode);
        }
    }

    private void clearNodes() {
        figureToNodeMap.clear();
        nodeToFigureMap.clear();
    }

    @Override
    public Node getNode(Figure f) {
        Node n = figureToNodeMap.get(f);
        if (n == null) {
            n = f.createNode(this);
            figureToNodeMap.put(f, n);
            nodeToFigureMap.put(n, f);
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
            clearNodes();
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
            drawingPane.getChildren().add(getNode(newValue));
            model.setRoot(newValue);
            boundsProperty = Drawing.BOUNDS.propertyAt(newValue.properties());
            boundsProperty.addListener(invalidationListener);
            handleFigureAdded(newValue);
        }
    }

    private void handleFigureAdded(Figure f) {
        handleFigureAdded0(f);
        repaint();
    }

    private void handleFigureAdded0(Figure f) {
        invalidateFigure(f);
        for (Figure child : f.childrenProperty()) {
            handleFigureAdded0(child);
        }
    }

    private void handleFigureRemoved(Figure f) {
        handleFigureRemoved0(f);
        repaint();
    }

    private void handleFigureRemoved0(Figure f) {
        for (Figure child : f.childrenProperty()) {
            handleFigureRemoved0(child);
        }
        removeNode(f);
    }

    private void handleFigureInvalidated(Figure f) {
        invalidateFigure(f);
        repaint();
    }

    private void updateView() {
        LinkedList<Figure> update = new LinkedList<>(dirtyFigures);
        dirtyFigures.clear();
        for (Figure f : update) {
            f.validate();
            f.updateNode(this, getNode(f));
        }
        for (Handle h : selectionHandles) {
            h.updateNode();
        }
        for (Handle h : secondaryHandles) {
            h.updateNode();
        }
    }

    private void repaint() {
        if (repainter == null) {
            repainter = () -> {
                repainter = null;
                updateView();
                validateHandles();
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
        return focused.getReadOnlyProperty();
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
            if (!n.isVisible()) {
                continue;
            }
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
            if (!n.isVisible()) {
                continue;
            }
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
                } else if (n instanceof Parent) {
                    f = findFigureBehind((Parent) n, pl, figureInWay);
                } else {
                    f = null;
                }
                return f;
            }
        }
        return null;
    }

    @Override
    public List<Figure> findFiguresInside(double vx, double vy, double vwidth, double vheight) {
        double sf = 1 / zoomFactor.get();
        BoundingBox r = new BoundingBox(vx * sf, vy * sf, 0, vwidth * sf, vheight
                * sf, 0);
        List<Figure> list = new LinkedList<Figure>();
        findFiguresInside((Parent) figureToNodeMap.get(getDrawing()), r, list);
        return list;
    }
    @Override
    public List<Figure> findFiguresIntersecting(double vx, double vy, double vwidth, double vheight) {
        return findFiguresInside(vx,vy,vwidth,vheight);
    }

    /**
     * Finds a node at the given drawing coordinates.
     *
     * @param p The parentProperty of the node, which already must contain the
     * point!
     * @param p A point given in the drawing coordinate system
     */
    private void findFiguresInside(Parent p, Bounds pp, List<Figure> found) {
        ObservableList<Node> list = p.getChildrenUnmodifiable();
        for (int i = list.size() - 1; i >= 0; i--) {// front to back
            Node n = list.get(i);
            Bounds pl = n.parentToLocal(pp);
            if (pl.contains(n.getBoundsInLocal())) { // only drill down if the parent contains the point
                Figure f = nodeToFigureMap.get(n);
                if (f == null) {
                    if (n instanceof Parent) {
                        findFiguresInside((Parent) n, pl, found);
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
        return drawingToView.getReadOnlyProperty();
    }

    @Override
    public DrawingModel getDrawingModel() {
        return model;
    }

    // Handles
    /**
     * Gets compatible handles.
     *
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
            if (!owners.contains(handle.getFigure()) /* &&
                     * handle.isCombinableWith(master) */) {
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
        }
    }

    /**
     * Validates the handles.
     */
    private void validateHandles() {
        // Validate handles only, if they are invalid/*, and if
        // the DrawingView has a DrawingEditor.*/
        if (!handlesAreValid /*
                 * && getEditor() != null
                 */) {
            handlesAreValid = true;
            updateHandles();
        }
    }

    private void updateHandles() {
        for (Handle h : selectionHandles) {
            h.dispose();
        }
        selectionHandles.clear();
        handlePane.getChildren().clear();
        while (true) {
            for (Figure figure : getSelectedFigures()) {
                for (Handle handle : figure.createHandles(detailLevel, this)) {
                    selectionHandles.add(handle);
                    handlePane.getChildren().add(handle.getNode());
                    handle.updateNode();
//                        handle.addHandleListener(eventHandler);
                }
            }

            if (selectionHandles.isEmpty() && detailLevel != 0) {
                // No handles are available at the desired detail level.
                // Retry with detail level 0.
                detailLevel = 0;
                continue;
            }
            break;
        }
    }
}
