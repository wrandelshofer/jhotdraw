/* @(#)SimpleDrawingView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import static java.lang.Math.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.jhotdraw.beans.ClampedDoubleProperty;
import org.jhotdraw.beans.NonnullProperty;
import org.jhotdraw.beans.OptionalProperty;
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
    private BorderPane drawingPane;

    @FXML
    private BorderPane toolPane;

    private StackPane node;

    private final NonnullProperty<Drawing> drawing = new NonnullProperty<>(this, DRAWING_PROPERTY, new SimpleDrawing());
    private final NonnullProperty<Constrainer> constrainer = new NonnullProperty<>(this, CONSTRAINER_PROPERTY, new NullConstrainer());

    {
        drawing.addListener((observable, oldValue, newValue) -> updateDrawing(oldValue, newValue));
    }
    private final OptionalProperty<Tool> tool = new OptionalProperty<>(this, TOOL_PROPERTY);

    {
        tool.addListener((observable, oldValue, newValue) -> updateTool(oldValue, newValue));
    }

    private final ReadOnlyBooleanWrapper focusedProperty = new ReadOnlyBooleanWrapper(this, FOCUSED_PROPERTY);
    private final DoubleProperty scaleFactor = new ClampedDoubleProperty(this, SCALE_FACTOR_PROPERTY, 1.0, 0.1, 10.0);

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
                            if (! node.getScene().getWindow().isFocused()) {
                            evt.consume();
                            }
                        }
                    }
                ;
        });
        node.setFocusTraversable(true);
        focusedProperty.bind(node.focusedProperty());

        updateDrawing(null, drawing().get());
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
    public NonnullProperty<Drawing> drawing() {
        return drawing;
    }
    @Override
    public NonnullProperty<Constrainer> constrainer() {
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
            drawingPane.setCenter(null);
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
            drawingPane.setCenter(getNode(newValue));
            updateView();
            model.setRoot(newValue);
            boundsProperty = Drawing.BOUNDS.propertyAt(newValue.properties());
            boundsProperty.addListener(invalidationListener);
        }
    }

    private void handleFigureAdded(Figure f) {
        dirtyFigures.add(f);
        for (Figure child : f.children()) {
            handleFigureAdded(child);
        }
    }

    private void handleFigureRemoved(Figure f) {
        dirtyFigures.remove(f);
        for (Figure child : f.children()) {
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
    public OptionalProperty<Tool> tool() {
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
    public DoubleProperty scaleFactor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
