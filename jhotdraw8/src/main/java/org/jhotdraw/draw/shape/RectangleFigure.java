/* @(#)RectangleFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import static java.lang.Math.*;
import org.jhotdraw.draw.shape.AbstractShapeFigure;
import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.DrawingView;

/**
 * Renders a {@code javafx.scene.shape.Rectangle}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RectangleFigure extends AbstractShapeFigure {

    public final static Key<Rectangle2D> RECTANGLE = new Key<>("rectangle", Rectangle2D.class, new Rectangle2D(0, 0, 1, 1));
    public final static Key<Double> ARC_HEIGHT = new Key<>("arcHeight", Double.class, 0.0);
    public final static Key<Double> ARC_WIDTH = new Key<>("arcWidth", Double.class, 0.0);

    public RectangleFigure() {
        this(0, 0, 1, 1);
    }

    public RectangleFigure(double x, double y, double width, double height) {
        set(RECTANGLE, new Rectangle2D(x, y, width, height));
    }

    public RectangleFigure(Rectangle2D rect) {
        set(RECTANGLE, rect);
    }

    @Override
    public Bounds getLayoutBounds() {
        Rectangle2D r= get(RECTANGLE);
        return new BoundingBox(r.getMinX(),r.getMinY(),r.getWidth(),r.getHeight());
    }

    @Override
    public void reshape(Transform transform) {
        Rectangle2D r = get(RECTANGLE);
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        reshape(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        set(RECTANGLE, new Rectangle2D(x+min(width,0),y+min(height,0),abs(width),abs(height)));
    }

    @Override
    public void putNode(DrawingView drawingView) {
        drawingView.putNode(this, new Rectangle());
    }

    @Override
    public void updateNode(DrawingView drawingView, Node node) {
        Rectangle rectangleNode = (Rectangle) node;
        updateFigureProperties(rectangleNode);
        updateShapeProperties(rectangleNode);
        Rectangle2D r = get(RECTANGLE);
        rectangleNode.setX(r.getMinX());
        rectangleNode.setY(r.getMinY());
        rectangleNode.setWidth(r.getWidth());
        rectangleNode.setHeight(r.getHeight());
        rectangleNode.setArcWidth(get(ARC_WIDTH));
        rectangleNode.setArcHeight(get(ARC_HEIGHT));
    }

    public static HashMap<String, Key<?>> getFigureKeys() {
        try {
            HashMap<String, Key<?>> keys = AbstractShapeFigure.getFigureKeys();
            for (Field f : RectangleFigure.class.getDeclaredFields()) {
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
