/* @(#)LineFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import static java.lang.Math.*;
import javafx.scene.shape.Line;
import org.jhotdraw.draw.DrawingView;

/**
 * Renders a {@code javafx.scene.shape.Line}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineFigure extends ShapeFigure {

    public final static Key<Point2D> START = new Key<>("start", Point2D.class, new Point2D(0, 0));
    public final static Key<Point2D> END = new Key<>("end", Point2D.class, new Point2D(0, 0));

    public LineFigure() {
        this(0, 0, 1, 1);
    }

    public LineFigure(double startX, double startY, double endX, double endY) {
        set(START, new Point2D(startX, startY));
        set(END, new Point2D(endX, endY));
    }

    public LineFigure(Point2D start, Point2D end) {
        set(START, start);
        set(END, end);
    }

    @Override
    public Rectangle2D getLayoutBounds() {
        Point2D start = get(START);
        Point2D end = get(END);
        return new Rectangle2D(//
                min(start.getX(), end.getX()),//
                min(end.getX(), end.getY()),//
                abs(start.getX() - end.getX()), //
                abs(start.getY() - end.getY()));
    }

    @Override
    public void reshape(Transform transform) {
        set(START, transform.transform(get(START)));
        set(END, transform.transform(get(END)));
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        set(START, new Point2D(x, y));
        set(END, new Point2D(x + width, y + height));
    }

    @Override
    public void putNode(DrawingView drawingView) {
        drawingView.putNode(this, new Line());
    }

    @Override
    public void updateNode(DrawingView drawingView, Node node) {
        Line lineNode = (Line) node;
        updateFigureProperties(lineNode);
        updateShapeProperties(lineNode);
        Point2D start = get(START);
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        Point2D end = get(END);
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());
    }

    public static HashMap<String, Key<?>> getFigureKeys() {
        try {
            HashMap<String, Key<?>> keys = ShapeFigure.getFigureKeys();
            for (Field f : LineFigure.class.getDeclaredFields()) {
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
