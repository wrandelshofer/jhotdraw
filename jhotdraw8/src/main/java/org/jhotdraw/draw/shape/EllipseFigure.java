/* @(#)CircleFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import static java.lang.Math.*;
import javafx.scene.shape.Ellipse;
import org.jhotdraw.draw.DrawingView;

/**
 * Renders a {@code javafx.scene.shape.Circle}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EllipseFigure extends ShapeFigure {

    public final static Key<Point2D> CENTER = new Key<>("center", Point2D.class, new Point2D(0, 0));
    public final static Key<Double> RADIUS_X = new Key<>("radiusX", Double.class, 1.0);
    public final static Key<Double> RADIUS_Y = new Key<>("radiusY", Double.class, 1.0);

    public EllipseFigure() {
        this(0, 0, 1,1);
    }

    public EllipseFigure(double x, double y, double radiusx, double radiusy) {
        set(CENTER, new Point2D(x, y));
        set(RADIUS_X, radiusx);
        set(RADIUS_Y, radiusy);
    }

    public EllipseFigure(Point2D center, double radiusx, double radiusy) {
        set(CENTER, center);
        set(RADIUS_X, radiusx);
        set(RADIUS_Y, radiusy);
    }

    @Override
    public Rectangle2D getLayoutBounds() {
        Point2D c = get(CENTER);
        double r = get(RADIUS_X);
        return new Rectangle2D(c.getX() - r, c.getY() - r, r * 2, r * 2);
    }

    @Override
    public void reshape(Transform transform) {
        Rectangle2D r = getLayoutBounds();
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
        b = transform.transform(b);
        set(CENTER, new Point2D(b.getMinX()+b.getWidth()/2 , b.getMinY()+b.getHeight()/2));
        set(RADIUS_X, abs(b.getWidth())/2 );
        set(RADIUS_Y, abs(b.getHeight())/2 );
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        set(CENTER, new Point2D(x+width/2, y+height/2 ));
        set(RADIUS_X, abs(width)/2 );
        set(RADIUS_Y, abs(height)/2 );
    }

    @Override
    public void putNode(DrawingView drawingView) {
        drawingView.putNode(this, new Ellipse());
    }

    @Override
    public void updateNode(DrawingView drawingView, Node node) {
        Ellipse n = (Ellipse) node;
        updateFigureProperties(n);
        updateShapeProperties(n);
        Point2D c = get(CENTER);
        n.setCenterX(c.getX());
        n.setCenterY(c.getY());
        n.setRadiusX(get(RADIUS_X));
        n.setRadiusY(get(RADIUS_Y));
    }

    public static HashMap<String, Key<?>> getFigureKeys() {
        try {
            HashMap<String, Key<?>> keys = ShapeFigure.getFigureKeys();
            for (Field f : EllipseFigure.class.getDeclaredFields()) {
                if (Key.class.isAssignableFrom(f.getType())) {
                    Key<?> value = (Key<?>) f.get(null);
                    keys.put(value.getName(), value);
                }
            }
            return keys;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new InternalError("class can not read its own keys");
        }
    }
}
