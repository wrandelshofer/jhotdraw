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
import org.jhotdraw.draw.key.SimpleFigureKey;

/**
 * Handle for the point of a figure.
 *
 * @author Werner Randelshofer
 */
public class PointHandle extends AbstractHandle {

    private final SimpleFigureKey<Point2D> pointKey;
    private Point2D oldPoint;
    private Point2D anchor;
    private final Rectangle node;
    private final String styleclass;

    public PointHandle(Figure figure, String styleclass, SimpleFigureKey<Point2D> pointKey) {
        super(figure);
        this.pointKey = pointKey;
        this.styleclass = styleclass;
        node = new Rectangle();
        initNode(node);
    }

    protected void initNode(Rectangle r) {
        // FIXME width and height must come from stylesheet
        r.setWidth(7);
        r.setHeight(7);
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
        Figure f = getOwner();
        Transform t = view.getDrawingToView().createConcatenation(f.getLocalToDrawing());
        Point2D p = f.get(pointKey);
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        p = t.transform(p);
        Rectangle r = node;
        r.setX(p.getX() - r.getWidth() / 2);
        r.setY(p.getY() - r.getHeight() / 2);
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
        oldPoint = anchor = view.getConstrainer().constrainPoint(getOwner(),view.viewToDrawing(new Point2D(event.getX(),event.getY())));
    }

    @Override
    public void onMouseDragged(MouseEvent event, DrawingView view) {
        Point2D newPoint = view.viewToDrawing(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control switches the grid off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
        }
        
        view.getModel().set(getOwner(), pointKey, getOwner().drawingToLocal(newPoint));
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
    }
    @Override
    public boolean isSelectable() {
        return true;
    }
}
