/* @(#)SimpleDrawingView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import static java.lang.Math.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SetProperty;
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
import org.jhotdraw.beans.NonnullProperty;
import org.jhotdraw.beans.OptionalProperty;
import static org.jhotdraw.draw.Figure.CHILDREN_PROPERTY;
import org.jhotdraw.draw.constrain.Constrainer;
import org.jhotdraw.draw.constrain.NullConstrainer;
import org.jhotdraw.draw.tool.Tool;

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
    private final ReadOnlySetProperty<Figure> selection = new ReadOnlySetWrapper<>(this,CHILDREN_PROPERTY,FXCollections.observableSet(new HashSet<Figure>())).getReadOnlyProperty();

    {
        drawing.addListener((observable, oldValue, newValue) -> updateDrawing(oldValue, newValue));
    }
    private final OptionalProperty<Tool> tool = new OptionalProperty<>(this, TOOL_PROPERTY);

    {
        tool.addListener((observable, oldValue, newValue) -> updateTool(oldValue, newValue));
    }

    private final ReadOnlyBooleanWrapper focusedProperty = new ReadOnlyBooleanWrapper(this, FOCUSED_PROPERTY);
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, SCALE_FACTOR_PROPERTY, 1.0) {

        @Override
        public void set(double newValue) {
            super.set(newValue);
            if (drawingPane != null) {
                drawingPane.setScaleX(newValue);
                drawingPane.setScaleY(newValue);
            }
        }
    };

    private DrawingModel model = new SimpleDrawingModel();

    private HashMap<Node, Figure> nodeToFigureMap = new HashMap<>();
    private HashMap<Figure, Node> figureToNodeMap = new HashMap<>();
    private HashSet<Figure> dirtyFigures = new HashSet<>();

    private Runnable repainter = null;

    public SimpleDrawingView() {
        init();
    }

    private void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            node = loader.load(getClass().getResourceAsStream("SimpleDrawingView.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        model.addListener(event -> {
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
                throw new IllegalStateException("Figure.putNode() must put a node. Figure=" + f);
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
        Rectangle2D visible = new Rectangle2D(max(r.getMinX(), 0), max(r.getMinY(), 0), (r.getWidth() + max(r.getMinX(), 0)),
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

    @Override
    public List<Figure> findFigures(double vx, double vy, double vwidth, double vheight) {
        double sf = 1 / zoomFactor.get();
        BoundingBox r = new BoundingBox(vx * sf, vy * sf, 0, vwidth * sf, vheight * sf, 0);
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
                }else{
                    found.add(f);
                }
            }
        }
    }    

    @Override
    public ReadOnlySetProperty<Figure> selectionProperty() {
       return selection;
    }
}
