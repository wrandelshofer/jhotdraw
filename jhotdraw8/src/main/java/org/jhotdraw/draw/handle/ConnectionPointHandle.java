/* @(#)ConnectionFigureConnectionHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
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
 *
 * @author werni
 */
public class ConnectionPointHandle extends AbstractHandle {

    private final SimpleFigureKey<Point2D> pointKey;
    private final SimpleFigureKey<Figure> figureKey;
    private final SimpleFigureKey<Connector> connectorKey;
    private Point2D oldPoint;
    private Point2D anchor;

    private final Circle node;
    private final String styleclass;
    private final String styleclassConnected;

    public ConnectionPointHandle(Figure figure, String styleclass, String styleclassConnected, SimpleFigureKey<Point2D> pointKey,
            SimpleFigureKey<Figure> figureKey, SimpleFigureKey<Connector> connectorKey) {
        super(figure);
        this.pointKey = pointKey;
        this.figureKey = figureKey;
        this.connectorKey = connectorKey;
        this.styleclass = styleclass;
        this.styleclassConnected = styleclassConnected;
        node = new Circle();
        initNode(node);
    }

    protected void initNode(Circle r) {
        r.setFill(Color.WHITE);
        r.setStroke(Color.BLUE);
        r.getStyleClass().add(styleclass);
        // FIXME Value must come from stylesheet
        r.setRadius(4);
    }

    @Override
    public Circle getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = view.getDrawingToView().createConcatenation(f.getLocalToDrawing());
        Point2D p = f.get(pointKey);
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        p = t.transform(p);
        Circle r = node;
        r.getStyleClass().clear();
        boolean isConnected = f.get(figureKey) != null && f.get(connectorKey) != null;
        r.setFill(isConnected ? Color.LIGHTGREEN : Color.RED);
        r.getStyleClass().add(isConnected ? styleclassConnected : styleclass);
        r.setCenterX(p.getX());
        r.setCenterY(p.getY());
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
        oldPoint = anchor = view.getConstrainer().constrainPoint(getOwner(), view.viewToDrawing(new Point2D(event.getX(), event.getY())));
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
