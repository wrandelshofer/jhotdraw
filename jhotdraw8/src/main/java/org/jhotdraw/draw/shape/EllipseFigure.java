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
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Ellipse;
import org.jhotdraw.draw.AbstractLeafFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.key.SimpleFigureKey;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.RenderContext;

/**
 * Renders a {@code javafx.scene.shape.Ellipse}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EllipseFigure extends AbstractLeafFigure implements StrokedShapeFigure, FilledShapeFigure {

    /**
     * The CSS type selector for this object is {@code "Ellipse"}.
     */
    public final static String TYPE_SELECTOR = "Ellipse";

    public final static SimpleFigureKey<Rectangle2D> BOUNDS = RectangleFigure.BOUNDS;

    public EllipseFigure() {
        this(0, 0, 1, 1);
    }

    public EllipseFigure(double x, double y, double width, double height) {
        set(BOUNDS, new Rectangle2D(x, y, width, height));
    }

    public EllipseFigure(Rectangle2D rect) {
        set(BOUNDS, rect);
    }

    @Override
    public Bounds getBoundsInLocal() {
        Rectangle2D r = get(BOUNDS);
        return new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
    }

    @Override
    public void reshape(Transform transform) {
        Rectangle2D r = get(BOUNDS);
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        reshape(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        set(BOUNDS, new Rectangle2D(x + min(width, 0), y + min(height, 0), abs(width), abs(height)));
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        return new Ellipse();
    }

    @Override
    public void updateNode(RenderContext drawingView, Node node) {
        Ellipse n = (Ellipse) node;
        applyTransformableFigureProperties(n);
        applyStrokedShapeProperties(n);
        applyFilledShapeProperties(n);
        Rectangle2D r = get(BOUNDS);
        n.setCenterX(r.getMinX() + r.getWidth() * 0.5);
        n.setCenterY(r.getMinY() + r.getHeight() * 0.5);
        n.setRadiusX(r.getWidth() * 0.5);
        n.setRadiusY(r.getHeight() * 0.5);
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

    @Override
    public void layout() {
        // empty
    }
    
    @Override
    public boolean isLayoutable() {
        return false;
    }

}
