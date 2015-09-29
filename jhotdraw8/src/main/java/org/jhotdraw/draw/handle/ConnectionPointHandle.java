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
    private double startX, startY;
    private Point2D startPoint;
    private Point2D unconstrainedPoint;

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
    public void onMousePressed(MouseEvent event, DrawingView dv) {
        startX = event.getX();
        startY = event.getY();
        startPoint = getOwner().get(pointKey);
    }

    @Override
    public void onMouseDragged(MouseEvent event, DrawingView dv) {
        double newX = event.getX();
        double newY = event.getY();

        Figure f = getOwner();
        Transform t = f.getDrawingToLocal().createConcatenation(dv.getViewToDrawing());

        Point2D delta = t.deltaTransform(newX - startX, newY - startY);
        Point2D p = startPoint;
        unconstrainedPoint = new Point2D(p.getX() + delta.getX(), p.getY() + delta.getY());
        Point2D newPoint = dv.getConstrainer().constrainPoint(f, unconstrainedPoint);

        Connector newConnector = null;
        Figure newFigure = null;
        if (!event.isAltDown() && !event.isControlDown()) {
            List<Figure> list = dv.findFigures(newPoint, true);
            for (Figure ff : list) {
                newConnector = ff.findConnector(newPoint, f);
                if (newConnector != null) {
                    newFigure = ff;
                    break;
                }
            }
        }

        DrawingModel dm = dv.getDrawingModel();
        dm.set(f, pointKey, newPoint);
        dm.set(f, figureKey, newFigure);
        dm.set(f, connectorKey, newConnector);
        dm.layout(f);
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
        unconstrainedPoint = null;
    }
}
