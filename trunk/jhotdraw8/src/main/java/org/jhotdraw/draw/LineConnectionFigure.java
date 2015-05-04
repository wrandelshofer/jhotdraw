/* @(#)LineConnectionFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.transform.Transform;
import org.jhotdraw.beans.PropertyBean;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.shape.LineFigure;
import org.jhotdraw.draw.shape.AbstractShapeFigure;

/**
 * LineConnectionFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineConnectionFigure extends AbstractShapeFigure implements ConnectionFigure {

    public LineConnectionFigure() {
        this(0, 0, 1, 1);
    }

    public LineConnectionFigure(double startX, double startY, double endX, double endY) {
        set(START, new Point2D(startX, startY));
        set(END, new Point2D(endX, endY));
    }

    public LineConnectionFigure(Point2D start, Point2D end) {
        set(START, start);
        set(END, end);

        // We must update the start and end point when ever one of
        // the connected figures or one of the connectors changes
        InvalidationListener il = observable -> invalidate();
        ChangeListener<Observable> cl = (observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeListener(il);
            }
            if (newValue != null) {
                newValue.addListener(il);
            }
        };

        START_FIGURE.propertyAt(properties()).addListener(il);
        END_FIGURE.propertyAt(properties()).addListener(cl);
        START_CONNECTOR.propertyAt(properties()).addListener(il);
        END_CONNECTOR.propertyAt(properties()).addListener(cl);
    }

    @Override
    public Bounds getLayoutBounds() {
        Point2D start = get(START);
        Point2D end = get(END);
        return new BoundingBox(//
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
            HashMap<String, Key<?>> keys = AbstractShapeFigure.getFigureKeys();
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

    private void invalidate() {
    }

}
