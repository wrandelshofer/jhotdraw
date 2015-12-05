/* @(#)CircleFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure.shape;

import org.jhotdraw.draw.FillableFigure;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import static java.lang.Math.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Ellipse;
import org.jhotdraw.draw.AbstractLeafFigure;
import org.jhotdraw.draw.CompositableFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.HideableFigure;
import org.jhotdraw.draw.LockableFigure;
import org.jhotdraw.draw.key.SimpleFigureKey;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.draw.TransformableFigure;
import org.jhotdraw.draw.StrokeableFigure;
import org.jhotdraw.draw.StyleableFigure;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.Point2DStyleableMapAccessor;

/**
 * Renders a {@code javafx.scene.shape.Ellipse}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EllipseFigure extends AbstractLeafFigure implements StrokeableFigure, FillableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for this object is {@code "Ellipse"}.
     */
    public final static String TYPE_SELECTOR = "Ellipse";

    public final static DoubleStyleableFigureKey CENTER_X = new DoubleStyleableFigureKey("centerX",  DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey CENTER_Y = new DoubleStyleableFigureKey("centerY",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey RADIUS_X = new DoubleStyleableFigureKey("radiusX",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 1.0);
    public final static DoubleStyleableFigureKey RADIUS_Y = new DoubleStyleableFigureKey("radiusY",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 1.0);
    public final static Point2DStyleableMapAccessor CENTER = new Point2DStyleableMapAccessor("center", CENTER_X,CENTER_Y);
    public final static Point2DStyleableMapAccessor RADIUS = new Point2DStyleableMapAccessor("radius", RADIUS_X,RADIUS_Y);

    public EllipseFigure() {
        this(0, 0, 1, 1);
    }

    public EllipseFigure(double x, double y, double width, double height) {
        reshape(x,y,width,height);
    }

    public EllipseFigure(Rectangle2D rect) {
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
        return new Ellipse();
    }

    @Override
    public void updateNode(RenderContext drawingView, Node node) {
        Ellipse n = (Ellipse) node;
        applyHideableFigureProperties(n);
        applyTransformableFigureProperties(n);
        applyStrokeableFigureProperties(n);
        applyFillableFigureProperties(n);
        applyCompositableFigureProperties(n);
        n.setCenterX(get(CENTER_X));
        n.setCenterY(get(CENTER_Y));
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

    @Override
    public void layout() {
        // empty
    }
    
    @Override
    public boolean isLayoutable() {
        return false;
    }

}
