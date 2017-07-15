/* @(#)CircleFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import static java.lang.Math.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.key.Point2DStyleableMapAccessor;

/**
 * Renders a {@code javafx.scene.shape.Arc}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleArcFigure extends AbstractLeafFigure implements StrokeableFigure, FillableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Arc";

    public final static DoubleStyleableFigureKey CENTER_X = new DoubleStyleableFigureKey("centerX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey CENTER_Y = new DoubleStyleableFigureKey("centerY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey RADIUS_X = new DoubleStyleableFigureKey("radiusX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    public final static DoubleStyleableFigureKey RADIUS_Y = new DoubleStyleableFigureKey("radiusY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    public final static DoubleStyleableFigureKey START_ANGLE = new DoubleStyleableFigureKey("startAngle", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey ARC_LENGTH = new DoubleStyleableFigureKey("arcLength", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 360.0);
    public final static EnumStyleableFigureKey<ArcType> ARC_TYPE = new EnumStyleableFigureKey<ArcType>("arcType", ArcType.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), false,ArcType.ROUND);
    public final static Point2DStyleableMapAccessor CENTER = new Point2DStyleableMapAccessor("center", CENTER_X, CENTER_Y);
    public final static Point2DStyleableMapAccessor RADIUS = new Point2DStyleableMapAccessor("radius", RADIUS_X, RADIUS_Y);

    public SimpleArcFigure() {
        this(0, 0, 1, 1);
    }

    public SimpleArcFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
    }

    public SimpleArcFigure(Rectangle2D rect) {
        reshapeInLocal(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public Bounds getBoundsInLocal() {
        double rx = get(RADIUS_X);
        double ry = get(RADIUS_Y);
        return new BoundingBox(get(CENTER_X) - rx, get(CENTER_Y) - ry, rx * 2.0, ry * 2.0);
    }

    @Override
    public void reshapeInLocal(Transform transform) {
        Bounds r = getBoundsInLocal();
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        reshapeInLocal(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
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
    public Node createNode(RenderContext drawingView) {
        return new Arc();
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Arc n = (Arc) node;
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
        n.setStartAngle(getStyled(START_ANGLE));
        n.setLength(getStyled(ARC_LENGTH));
        n.setType(getStyled(ARC_TYPE));
        n.applyCss();
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
