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
import javafx.scene.shape.Circle;
import org.jhotdraw.draw.DrawingView;

/**
 * Renders a {@code javafx.scene.shape.Circle}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CircleFigure extends AbstractShapeFigure {

    public final static Key<Point2D> CENTER = new Key<>("center", Point2D.class, new Point2D(0, 0));
    public final static Key<Double> RADIUS = new Key<>("radius", Double.class, 1.0);

    public CircleFigure() {
        this(0, 0, 1);
    }

    public CircleFigure(double x, double y, double radius) {
        set(CENTER, new Point2D(x, y));
        set(RADIUS, radius);
    }

    public CircleFigure(Point2D center, double radius) {
        set(CENTER, center);
        set(RADIUS, radius);
    }

    @Override
    public Bounds getLayoutBounds() {
        Point2D c = get(CENTER);
        double r = get(RADIUS);
        return new BoundingBox(c.getX() - r, c.getY() - r, r * 2, r * 2);
    }

    @Override
    public void reshape(Transform transform) {
        Bounds r = getLayoutBounds();
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        set(CENTER, new Point2D(b.getMinX() + b.getWidth() / 2, b.getMinY() + b.getHeight() / 2));
        set(RADIUS, min(abs(b.getWidth()), abs(b.getHeight())) / 2);
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        set(CENTER, new Point2D(x + width / 2, y + height / 2));
        set(RADIUS, min(abs(width), abs(height)) / 2);
    }

    @Override
    public void putNode(DrawingView drawingView) {
        drawingView.putNode(this, new Circle());
    }

    @Override
    public void updateNode(DrawingView drawingView, Node node) {
        Circle circleNode = (Circle) node;
        updateFigureProperties(circleNode);
        updateShapeProperties(circleNode);
        Point2D c = get(CENTER);
        circleNode.setCenterX(c.getX());
        circleNode.setCenterY(c.getY());
        circleNode.setRadius(get(RADIUS));
    }

    public static HashMap<String, Key<?>> getFigureKeys() {
        try {
            HashMap<String, Key<?>> keys = AbstractShapeFigure.getFigureKeys();
            for (Field f : CircleFigure.class.getDeclaredFields()) {
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
