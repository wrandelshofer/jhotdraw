/* @(#)RectangleFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import static java.lang.Math.*;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.Point2DStyleableMapAccessor;
import org.jhotdraw.draw.key.Rectangle2DStyleableMapAccessor;

/**
 * Renders a {@code javafx.scene.shape.Rectangle}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RectangleFigure extends AbstractLeafFigure implements StrokeableFigure, FillableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for this object is {@code "Rectangle"}.
     */
    public final static String TYPE_SELECTOR = "Rectangle";

    public final static DoubleStyleableFigureKey X = new DoubleStyleableFigureKey("boundsX",  DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey Y = new DoubleStyleableFigureKey("boundsY",  DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey WIDTH = new DoubleStyleableFigureKey("boundsWidth",  DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey HEIGHT = new DoubleStyleableFigureKey("boundsHeight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static Rectangle2DStyleableMapAccessor BOUNDS = new Rectangle2DStyleableMapAccessor("bounds", X,Y,WIDTH,HEIGHT);
    public final static DoubleStyleableFigureKey ARC_HEIGHT = new DoubleStyleableFigureKey("arcHeight", DirtyMask.of(DirtyBits.NODE, DirtyBits.DEPENDENT_LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey ARC_WIDTH = new DoubleStyleableFigureKey("arcWidth", DirtyMask.of(DirtyBits.NODE, DirtyBits.DEPENDENT_LAYOUT), 0.0);
    public final static Point2DStyleableMapAccessor ARC = new Point2DStyleableMapAccessor("arc", ARC_WIDTH,ARC_HEIGHT);

    public RectangleFigure() {
        this(0, 0, 1, 1);
    }

    public RectangleFigure(double x, double y, double width, double height) {
        reshape(x, y, width, height);
    }

    public RectangleFigure(Rectangle2D rect) {
        reshape(rect.getMinX(),rect.getMinY(),rect.getWidth(),rect.getHeight());
    }

    @Override
    public Bounds getBoundsInLocal() {
        return new BoundingBox(get(X), get(Y), get(WIDTH), get(HEIGHT));
    }

    @Override
    public void reshape(Transform transform) {
        Bounds b = getBoundsInLocal();
        b = transform.transform(b);
        reshape(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        set(X, x + min(width, 0));
        set(Y, y + min(height, 0));
        set(WIDTH, abs(width));
        set(HEIGHT, abs(height));
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        return new Rectangle();
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Rectangle rectangleNode = (Rectangle) node;
        applyHideableFigureProperties(node);
        applyTransformableFigureProperties(rectangleNode);
        applyFillableFigureProperties(rectangleNode);
        applyStrokeableFigureProperties(rectangleNode);
        applyCompositableFigureProperties(rectangleNode);
        applyStyleableFigureProperties(ctx, node);
        rectangleNode.setX(get(X));
        rectangleNode.setY(get(Y));
        rectangleNode.setWidth(get(WIDTH));
        rectangleNode.setHeight(get(HEIGHT));
        rectangleNode.setArcWidth(getStyled(ARC_WIDTH));
        rectangleNode.setArcHeight(getStyled(ARC_HEIGHT));
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return new ChopRectangleConnector();
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
