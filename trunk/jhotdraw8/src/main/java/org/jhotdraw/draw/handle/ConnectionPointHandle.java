/* @(#)ConnectionPointHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.key.SimpleFigureKey;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.draw.model.DrawingModelEvent;

/**
 * Handle for the start or end point of a connection figure.
 * <p>
 * Pressing the alt or the control key while dragging the handle prevents
 * connecting the point.
 * <p>
 * This handle is drawn using a {@code Region}, which can be styled using
 * {@code styleclassDisconnected} and {@code styleclassConnected} given in the
 * constructor.
 *
 * @author werni
 */
public class ConnectionPointHandle extends AbstractHandle {

    private final SimpleFigureKey<Point2D> pointKey;
    private final SimpleFigureKey<Figure> figureKey;
    private final SimpleFigureKey<Connector> connectorKey;

    private final Region node;
    private final String styleclassDisconnected;
    private final String styleclassConnected;

    private static final Circle REGION_SHAPE = new Circle(4);

    private static final Background REGION_BACKGROUND_DISCONNECTED = new Background(new BackgroundFill(Color.WHITE, null, null));
    private static final Background REGION_BACKGROUND_CONNECTED = new Background(new BackgroundFill(Color.CYAN, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.CYAN, null, null, null));

    public ConnectionPointHandle(Figure figure, String styleclass, String styleclassConnected, SimpleFigureKey<Point2D> pointKey,
            SimpleFigureKey<Figure> figureKey, SimpleFigureKey<Connector> connectorKey) {
        super(figure);
        this.pointKey = pointKey;
        this.figureKey = figureKey;
        this.connectorKey = connectorKey;
        this.styleclassDisconnected = styleclass;
        this.styleclassConnected = styleclassConnected;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(10, 10);
        node.getStyleClass().clear();
        node.getStyleClass().add(styleclassDisconnected);
        node.setBorder(REGION_BORDER);
    }

    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = view.getDrawingToView().createConcatenation(f.getLocalToDrawing());
        Point2D p = f.get(pointKey);
        p = t.transform(p);
        boolean isConnected = f.get(figureKey) != null && f.get(connectorKey) != null;
        node.setBackground(isConnected ? REGION_BACKGROUND_CONNECTED : REGION_BACKGROUND_DISCONNECTED);
        node.getStyleClass().set(0, isConnected ? styleclassConnected : styleclassDisconnected);
        node.relocate(p.getX() - 5, p.getY() - 5);
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
    }

    @Override
    public void onMouseDragged(MouseEvent event, DrawingView view) {
        Point2D newPoint = view.viewToDrawing(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
        }

        Figure o = getOwner();
        Connector newConnector = null;
        Figure newConnectedFigure = null;
        if (!event.isMetaDown()) {
            List<Figure> list = view.findFigures(newPoint, true);
            for (Figure ff : list) {
                newConnector = ff.findConnector(newPoint, o);
                if (newConnector != null) {
                    newConnectedFigure = ff;
                    break;
                }
            }
        }

        DrawingModel model = view.getModel();
        model.set(o, pointKey, getOwner().drawingToLocal(newPoint));
        Figure oldConnectedFigure = model.set(o, figureKey, newConnectedFigure);
        model.set(o, connectorKey, newConnector);
        if (oldConnectedFigure != null) {
            model.fire(DrawingModelEvent.nodeInvalidated(model, oldConnectedFigure));
        }
        if (newConnectedFigure != null) {
            model.fire(DrawingModelEvent.nodeInvalidated(model, newConnectedFigure));
        }
        model.layout(o);
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
    }

    @Override
    public boolean isSelectable() {
        return true;
    }
}
