/* @(#)ConnectionFigureConnectionHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.SimpleFigureKey;
import org.jhotdraw.geom.Geom;

/**
 * Handle for the point of a figure.
 *
 * @author Werner Randelshofer
 */
public class MoveHandle extends AbstractHandle {

    private double prevX, prevY;
    private Point2D startPoint;
    private Point2D unconstrainedPoint;
    private final Rectangle node;
    private final String styleclass;
    private double relativeX;
    private double relativeY;

    public MoveHandle(Figure figure, DrawingView dv, String styleclass, double relativeX, double relativeY) {
        super(figure, dv);
        this.styleclass = styleclass;
        node = new Rectangle();
        this.relativeX = Geom.clamp(relativeX, 0, 1);
        this.relativeY = Geom.clamp(relativeY, 0, 1);
        initNode(node);
    }

    protected void initNode(Rectangle r) {
        // FIXME width and height must come from stylesheet
        r.setWidth(5);
        r.setHeight(5);
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
        Bounds b = f.getBoundsInLocal();
        Point2D p = new Point2D(b.getMinX() + b.getWidth() * relativeX, b.getMinY() + b.getHeight() * relativeY);
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        p = t.transform(p);
        Rectangle r = node;
        r.setX(p.getX() - r.getWidth() / 2);
        r.setY(p.getY() - r.getHeight() / 2);
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView dv) {
        prevX = event.getX();
        prevY = event.getY();
    }

    @Override
    public void onMouseDragged(MouseEvent event, DrawingView dv) {
        // FIXME implement me properly
        double newX = event.getX();
        double newY = event.getY();

        Figure f = getFigure();
        Transform t = f.getDrawingToLocal().createConcatenation(dv.getViewToDrawing());

        Point2D delta = t.deltaTransform(newX - prevX, newY - prevY);
        Transform tx = Transform.translate(delta.getX(), delta.getY());
        /*
         Point2D newPoint = dv.getConstrainer().constrainPoint(f, unconstrainedPoint);
         */
        for (Figure selected : dv.getSelectedFigures()) {
            dv.getDrawingModel().reshape(selected, tx);
        }
        prevX = newX;
        prevY = newY;
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
        unconstrainedPoint = null;
    }
}
