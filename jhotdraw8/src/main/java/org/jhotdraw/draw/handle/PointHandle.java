/* @(#)ConnectionFigureConnectionHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.SimpleFigureKey;

/**
 * Handle for the point of a figure.
 *
 * @author Werner Randelshofer
 */
public class PointHandle extends AbstractHandle {

    private final SimpleFigureKey<Point2D> pointKey;
    private double startX, startY;
    private Point2D startPoint;
    private Point2D unconstrainedPoint;
    private final Rectangle node;
    private final String styleclass;

    public PointHandle(Figure figure, DrawingView dv, String styleclass, SimpleFigureKey<Point2D> pointKey) {
        super(figure, dv);
        this.pointKey = pointKey;
        this.styleclass = styleclass;
        node = new Rectangle();
        initNode(node);
    }

    protected void initNode(Rectangle r) {
        // FIXME width and height must come from stylesheet
        r.setWidth(4);
        r.setHeight(4);
        r.setFill(Color.WHITE);
        r.setStroke(Color.BLUE);
        r.getStyleClass().add(styleclass);
    }

    @Override
    public Rectangle getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getFigure();
        Transform t = view.getDrawingToView().createConcatenation(f.getLocalToDrawing());
        Point2D p = f.get(pointKey);
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        p = t.transform(p);
        Rectangle r = node;
        r.setX(p.getX() - r.getWidth() / 2);
        r.setY(p.getY() - r.getHeight() / 2);
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView dv) {
        startX = event.getX();
        startY = event.getY();
        startPoint = getFigure().get(pointKey);
    }

    @Override
    public void onMouseDragged(MouseEvent event, DrawingView dv) {
        double newX = event.getX();
        double newY = event.getY();

        Figure f = getFigure();
        Transform t = f.getDrawingToLocal().createConcatenation(dv.getViewToDrawing());

        Point2D delta = t.deltaTransform(newX - startX, newY - startY);
        Point2D p = startPoint;
        unconstrainedPoint = new Point2D(p.getX() + delta.getX(), p.getY() + delta.getY());
        Point2D newPoint = dv.getConstrainer().constrainPoint(f, unconstrainedPoint);
        dv.getDrawingModel().set(f, pointKey, newPoint);
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
        unconstrainedPoint = null;
    }
}
