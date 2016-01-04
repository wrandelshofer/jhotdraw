/* @(#)CircleFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import static java.lang.Math.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Ellipse;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.EnumStyleableFigureKey;
import org.jhotdraw.draw.key.Point2DStyleableMapAccessor;

/**
 * Renders a {@code javafx.scene.shape.Arc}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ArcFigure extends AbstractLeafFigure implements StrokeableFigure, FillableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for this object is {@code "Ellipse"}.
     */
    public final static String TYPE_SELECTOR = "Arc";

    public final static DoubleStyleableFigureKey CENTER_X = new DoubleStyleableFigureKey("centerX",  DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey CENTER_Y = new DoubleStyleableFigureKey("centerY",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey RADIUS_X = new DoubleStyleableFigureKey("radiusX",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 1.0);
    public final static DoubleStyleableFigureKey RADIUS_Y = new DoubleStyleableFigureKey("radiusY",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 1.0);
    public final static DoubleStyleableFigureKey START_ANGLE = new DoubleStyleableFigureKey("startAngle",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey ARC_LENGTH = new DoubleStyleableFigureKey("arcLength",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 360.0);
    public final static EnumStyleableFigureKey<ArcType> ARC_TYPE = new EnumStyleableFigureKey<ArcType>("arcType",ArcType.class,   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), ArcType.ROUND);
    public final static Point2DStyleableMapAccessor CENTER = new Point2DStyleableMapAccessor("center", CENTER_X,CENTER_Y);
    public final static Point2DStyleableMapAccessor RADIUS = new Point2DStyleableMapAccessor("radius", RADIUS_X,RADIUS_Y);

    public ArcFigure() {
        this(0, 0, 1, 1);
    }

    public ArcFigure(double x, double y, double width, double height) {
        reshape(x,y,width,height);
    }

    public ArcFigure(Rectangle2D rect) {
        reshape(rect.getMinX(),rect.getMinY(),rect.getWidth(),rect.getHeight());
    }

    @Override
    public Bounds getBoundsInLocal() {
        double rx=get(RADIUS_X);
        double ry=get(RADIUS_Y);
        return new BoundingBox(get(CENTER_X)-rx, get(CENTER_Y)-ry, rx*2.0, ry*2.0);
    }

    @Override
    public void reshape(Transform transform) {
        Bounds r = getBoundsInLocal();
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        reshape(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        double rx=max(0.0,width)/2.0;
        double ry=max(0.0,height)/2.0;
        set(CENTER_X,x+rx);
        set(CENTER_Y,y+ry);
        set(RADIUS_X,rx);
        set(RADIUS_Y,ry);
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
    public Connector findConnector(Point2D p, Figure prototype) {
        return new ChopEllipseConnector(this);
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void updateLayout() {
        // empty
    }
    
    @Override
    public boolean isLayoutable() {
        return false;
    }

}
