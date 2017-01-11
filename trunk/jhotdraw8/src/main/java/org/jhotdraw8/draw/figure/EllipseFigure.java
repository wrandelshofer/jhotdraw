/* @(#)CircleFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import static java.lang.Math.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Ellipse;
import org.jhotdraw8.draw.connector.ChopEllipseConnector;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Point2DStyleableMapAccessor;

/**
 * Renders a {@code javafx.scene.shape.Ellipse}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EllipseFigure extends AbstractLeafFigure implements StrokeableFigure, ResizableFigure, FillableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    public final static DoubleStyleableFigureKey CENTER_X = new DoubleStyleableFigureKey("centerX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey CENTER_Y = new DoubleStyleableFigureKey("centerY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static Point2DStyleableMapAccessor CENTER = new Point2DStyleableMapAccessor("center", CENTER_X, CENTER_Y);
    public final static DoubleStyleableFigureKey RADIUS_X = new DoubleStyleableFigureKey("radiusX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    public final static DoubleStyleableFigureKey RADIUS_Y = new DoubleStyleableFigureKey("radiusY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    public final static Point2DStyleableMapAccessor RADIUS = new Point2DStyleableMapAccessor("radius", RADIUS_X, RADIUS_Y);
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Ellipse";

    public EllipseFigure() {
        this(0, 0, 1, 1);
    }

    public EllipseFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
    }

    public EllipseFigure(Rectangle2D rect) {
        reshapeInLocal(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        return new Ellipse();
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return new ChopEllipseConnector();
    }

    @Override
    public Bounds getBoundsInLocal() {
        double rx = get(RADIUS_X);
        double ry = get(RADIUS_Y);
        return new BoundingBox(get(CENTER_X) - rx, get(CENTER_Y) - ry, rx * 2.0, ry * 2.0);
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public boolean isLayoutable() {
        return false;
    }

    @Override
    public void layout() {
        // empty
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        double rx = max(0.0, width) / 2.0;
        double ry = max(0.0, height) / 2.0;
        set(CENTER_X, x + rx);
        set(CENTER_Y, y + ry);
        set(RADIUS_X, rx);
        set(RADIUS_Y, ry);
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Ellipse n = (Ellipse) node;
        applyHideableFigureProperties(n);
        applyTransformableFigureProperties(n);
        applyStrokeableFigureProperties(n);
        applyFillableFigureProperties(n);
        applyCompositableFigureProperties(n);
        applyStyleableFigureProperties(ctx, node);
        n.setCenterX(getStyled(CENTER_X));
        n.setCenterY(getStyled(CENTER_Y));
        n.setRadiusX(getStyled(RADIUS_X));
        n.setRadiusY(getStyled(RADIUS_Y));
        n.applyCss();
    }

}
