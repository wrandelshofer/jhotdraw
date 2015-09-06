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
import javafx.beans.property.Property;
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
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.shape.LineFigure;
import org.jhotdraw.draw.shape.AbstractShapeFigure;
import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * LineConnectionFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineConnectionFigure extends AbstractShapeFigure implements ConnectionFigure {

    /** Holds a strong reference to the property. */
    private Property<Figure> startFigureProperty;
    /** Holds a strong reference to the property. */
    private Property<Figure> endFigureProperty;
    /** Holds a strong reference to the property. */
    private Property<Connector> startConnectorProperty;
    /** Holds a strong reference to the property. */
    private Property<Connector> endConnectorProperty;

    public LineConnectionFigure() {
        this(0, 0, 1, 1);
    }

    public LineConnectionFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public LineConnectionFigure(double startX, double startY, double endX, double endY) {
        set(START, new Point2D(startX, startY));
        set(END, new Point2D(endX, endY));

        // We must update the start and end point when ever one of
        // the connected figures or one of the connectors changes
        InvalidationListener ilStart = observable -> {
            invalidate();
            //updateStart();
        };
        ChangeListener<Observable> clStart = (observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeListener(ilStart);
            }
            if (newValue != null) {
                newValue.addListener(ilStart);
                invalidate();
                //updateStart();
            }
        };
        InvalidationListener ilEnd = observable -> {
            invalidate();
            //updateEnd();
        };
        ChangeListener<Observable> clEnd = (observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeListener(ilEnd);
            }
            if (newValue != null) {
                newValue.addListener(ilEnd);
                invalidate();
                //updateEnd();
            }
        };

        startFigureProperty = START_FIGURE.propertyAt(properties());
        startFigureProperty.addListener(clStart);
        endFigureProperty = END_FIGURE.propertyAt(properties());
        endFigureProperty.addListener(clEnd);
        startConnectorProperty = START_CONNECTOR.propertyAt(properties());
        startConnectorProperty.addListener(clStart);
        endConnectorProperty = END_CONNECTOR.propertyAt(properties());
        endConnectorProperty.addListener(clEnd);
    }

    @Override
    public Bounds getLayoutBounds() {
        Point2D start = get(START);
        Point2D end = get(END);
        return new BoundingBox(//
                min(start.getX(), end.getX()),//
                min(start.getY(), end.getY()),//
                abs(start.getX() - end.getX()), //
                abs(start.getY() - end.getY()));
    }

    @Override
    public void reshape(Transform transform) {
        if (get(START_FIGURE) == null) {
            set(START, transform.transform(get(START)));
        }
        if (get(END_FIGURE) == null) {
            set(END, transform.transform(get(END)));
        }
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        if (get(START_FIGURE) == null) {
            set(START, new Point2D(x, y));
        }
        if (get(END_FIGURE) == null) {
            set(END, new Point2D(x + width, y + height));
        }
    }

    @Override
    public Node createNode(DrawingView drawingView) {
        return new Line();
    }

    @Override
    public void updateNode(DrawingView drawingView, Node node) {
        Line lineNode = (Line) node;
        applyFigureProperties(lineNode);
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

    @Override
    protected void updateState() {
        updateStart();
        updateEnd();
    }

    private void updateStart() {
        get(START_CONNECTOR).updateStartPosition(this);
    }

    private void updateEnd() {
        get(END_CONNECTOR).updateEndPosition(this);
    }

}
