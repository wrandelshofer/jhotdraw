/* @(#)CircleFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import static java.lang.Math.*;
import javafx.scene.shape.Ellipse;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.key.SimpleFigureKey;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.RenderContext;

/**
 * Renders a {@code javafx.scene.shape.Circle}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EllipseFigure extends AbstractShapeFigure {

    /**
     * The CSS type selector for this object is {@code "Ellipse"}.
     */
    public final static String TYPE_SELECTOR = "Ellipse";

    public final static SimpleFigureKey<Point2D> CENTER = new SimpleFigureKey<>("center", Point2D.class, false, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), new Point2D(0, 0));
    public final static SimpleFigureKey<Double> RADIUS_X = new SimpleFigureKey<>("radiusX", Double.class, false, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 1.0);
    public final static SimpleFigureKey<Double> RADIUS_Y = new SimpleFigureKey<>("radiusY", Double.class, false, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 1.0);

    public EllipseFigure() {
        this(0, 0, 1, 1);
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
    public Bounds getBoundsInLocal() {
        Point2D c = get(CENTER);
        double rx = get(RADIUS_X);
        double ry = get(RADIUS_Y);
        return new BoundingBox(c.getX() - rx, c.getY() - ry, rx * 2, ry * 2);
    }

    @Override
    public void reshape(Transform transform) {
        Bounds r = getBoundsInLocal();
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        set(CENTER, new Point2D(b.getMinX() + b.getWidth() / 2, b.getMinY() + b.getHeight() / 2));
        set(RADIUS_X, abs(b.getWidth()) / 2);
        set(RADIUS_Y, abs(b.getHeight()) / 2);
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        set(CENTER, new Point2D(x + width / 2, y + height / 2));
        set(RADIUS_X, abs(width) / 2);
        set(RADIUS_Y, abs(height) / 2);
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        return new Ellipse();
    }

    @Override
    public void updateNode(RenderContext drawingView, Node node) {
        Ellipse n = (Ellipse) node;
        applyFigureProperties(n);
        applyShapeProperties(n);
        Point2D c = get(CENTER);
        n.setCenterX(c.getX());
        n.setCenterY(c.getY());
        n.setRadiusX(get(RADIUS_X));
        n.setRadiusY(get(RADIUS_Y));
        n.applyCss();
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return new ChopEllipseConnector(this);
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
