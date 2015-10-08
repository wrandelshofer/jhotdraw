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
import javafx.scene.shape.Circle;
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
public class CircleFigure extends AbstractShapeFigure {

    /**
     * The CSS type selector for this object is {@code "Circle"}.
     */
    public final static String TYPE_SELECTOR = "Circle";

    public final static SimpleFigureKey<Point2D> CENTER = new SimpleFigureKey<>("center", Point2D.class, false, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), new Point2D(0, 0));
    public final static SimpleFigureKey<Double> RADIUS = new SimpleFigureKey<>("radius", Double.class, false, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 1.0);

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
    public Bounds getBoundsInLocal() {
        Point2D c = get(CENTER);
        double r = get(RADIUS);
        return new BoundingBox(c.getX() - r, c.getY() - r, r * 2, r * 2);
    }

    @Override
    public void reshape(Transform transform) {
        Bounds r = getBoundsInLocal();
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
    public Node createNode(RenderContext drawingView) {
        return new Circle();
    }

    @Override
    public void updateNode(RenderContext drawingView, Node node) {
        Circle circleNode = (Circle) node;
        applyFigureProperties(circleNode);
        applyShapeProperties(circleNode);
        Point2D c = get(CENTER);
        circleNode.setCenterX(c.getX());
        circleNode.setCenterY(c.getY());
        circleNode.setRadius(get(RADIUS));
        circleNode.applyCss();
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
