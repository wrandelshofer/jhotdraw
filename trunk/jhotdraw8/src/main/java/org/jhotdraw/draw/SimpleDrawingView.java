/* @(#)SimpleDrawingView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class SimpleDrawingView implements DrawingView {

    @FXML
    private Pane handlePane;

    @FXML
    private Pane backgroundPane;

    @FXML
    private Pane drawingPane;

    @FXML
    private Pane glassPane;

    private Node node;

    private final ObjectProperty<Drawing> drawing = new SimpleObjectProperty<Drawing>(null);

    {
        drawing.addListener((observable, oldValue, newValue) -> updateDrawing(oldValue, newValue));
    }

    private DrawingModel model = new SimpleDrawingModel();

    private HashMap<Node, Figure> nodeToFigureMap = new HashMap<>();
    private HashMap<Figure, Node> figureToNodeMap = new HashMap<>();
    private HashSet<Figure> dirtyFigures = new HashSet<>();

    private Runnable repainter = null;

    public void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            node = loader.load(getClass().getResourceAsStream("SimpleDrawingView.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        setDrawing(new SimpleDrawing());
        model.addListener(event -> {
            dirtyFigures.add(event.getFigure());
            repaint();
        });
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
    public ObjectProperty<Drawing> drawing() {
        return drawing;
    }

    private void updateDrawing(Drawing oldValue, Drawing newValue) {
        if (oldValue != null) {
            Drawing d = oldValue;
            nodeToFigureMap.clear();
            figureToNodeMap.clear();
            drawingPane.getChildren().clear();
            dirtyFigures.clear();
            model.setRoot(null);
        }
        if (newValue != null) {
            Drawing d = newValue;
            handleFigureAdded(d);
            drawingPane.getChildren().add(getNode(d));
            updateView();
            model.setRoot(d);
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
}
