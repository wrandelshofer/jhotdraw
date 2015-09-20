/* @(#)LineConnectionFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.shape.AbstractShapeFigure;
import static java.lang.Math.*;
import org.jhotdraw.draw.connector.CenterConnector;

/**
 * LineConnectionFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineConnectionFigure extends AbstractShapeFigure {

    /** Holds a strong reference to the property. */
    private Property<Figure> startFigureProperty;
    /** Holds a strong reference to the property. */
    private Property<Figure> endFigureProperty;
    /**
     * The start position of the line.
     */
    public static FigureKey<Point2D> START = new FigureKey<>("start", Point2D.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), new Point2D(0, 0));
    /**
     * The end position of the line.
     */
    public static FigureKey<Point2D> END = new FigureKey<>("end", Point2D.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), new Point2D(0, 0));
    /**
     * The start figure.
     * Is null if the figure is not connected at the start.
     * <p>
     * If the value is changed. This figure must add or remove itself from
     * the list of connections on the {@code ConnectableFigure}.</p>
     */
    public static FigureKey<Figure> START_FIGURE = new FigureKey<>("startFigure", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), null);
    /**
     * The end figure.
     * Is null if the figure is not connected at the end.
     * <p>
     * If the value is changed. This figure must add or remove itself from
     * the list of connections on the {@code ConnectableFigure}.</p>
     */
    public static FigureKey<Figure> END_FIGURE = new FigureKey<>("endFigure", Figure.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), null);
    /**
     * The start connector.
     */
    public static FigureKey<Connector> START_CONNECTOR = new FigureKey<>("startConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), new CenterConnector());
    /**
     * The end connector.
     */
    public static FigureKey<Connector> END_CONNECTOR = new FigureKey<>("endConnector", Connector.class, DirtyMask.of(DirtyBits.STATE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), new CenterConnector());

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
        ChangeListener<Figure> clStart = (observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.connections().remove(LineConnectionFigure.this);
            }
            if (newValue != null) {
                newValue.connections().add(LineConnectionFigure.this);
            }
        };
        ChangeListener<Figure> clEnd = (observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.connections().remove(LineConnectionFigure.this);
            }
            if (newValue != null) {
                newValue.connections().add(LineConnectionFigure.this);
            }
        };

        startFigureProperty = START_FIGURE.propertyAt(properties());
        startFigureProperty.addListener(clStart);
        endFigureProperty = END_FIGURE.propertyAt(properties());
        endFigureProperty.addListener(clEnd);
    }

    @Override
    public Bounds getBoundsInLocal() {
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
    public Node createNode(DrawingRenderer drawingView) {
        return new Line();
    }

    @Override
    public void updateNode(DrawingRenderer drawingView, Node node) {
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

    public void layout() {
        Point2D start = get(START);
        Point2D end = get(END);
        Figure startFigure = get(START_FIGURE);
        Figure endFigure = get(END_FIGURE);
        Connector startConnector = get(START_CONNECTOR);
        Connector endConnector = get(END_CONNECTOR);
        if (startFigure != null && startConnector != null) {
            start = startConnector.getPosition(startFigure, this);
        }
        if (endFigure != null && endConnector != null) {
            end = endConnector.getPosition(endFigure, this);
        }
        if (startFigure != null && startConnector != null) {
            set(START, drawingToLocal(startConnector.chopStart(startFigure, this, start, end)));
        }
        if (endFigure != null && endConnector != null) {
            set(END, drawingToLocal(startConnector.chopEnd(endFigure, this, start, end)));
        }
    }

    @Override
    public boolean isLayoutable() {
        return true;
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return null;
    }
}
