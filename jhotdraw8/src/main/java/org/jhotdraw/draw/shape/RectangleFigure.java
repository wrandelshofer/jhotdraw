/* @(#)RectangleFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import static java.lang.Math.*;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.AbstractLeafFigure;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.key.SimpleFigureKey;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;

/**
 * Renders a {@code javafx.scene.shape.Rectangle}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RectangleFigure extends AbstractLeafFigure implements StrokedShapeFigure, FilledShapeFigure {

    /**
     * The CSS type selector for this object is {@code "Rectangle"}.
     */
    public final static String TYPE_SELECTOR = "Rectangle";

    public final static SimpleFigureKey<Rectangle2D> BOUNDS = new SimpleFigureKey<>("bounds", Rectangle2D.class, false, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), new Rectangle2D(0, 0, 1, 1));
    public final static DoubleStyleableFigureKey ARC_HEIGHT = new DoubleStyleableFigureKey("arcHeight",  DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey ARC_WIDTH = new DoubleStyleableFigureKey("arcWidth",  DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT), 0.0);

    public RectangleFigure() {
        this(0, 0, 1, 1);
    }

    public RectangleFigure(double x, double y, double width, double height) {
        set(BOUNDS, new Rectangle2D(x, y, width, height));
    }

    public RectangleFigure(Rectangle2D rect) {
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
        return new Rectangle();
    }

    @Override
    public void updateNode(RenderContext drawingView, Node node) {
        Rectangle rectangleNode = (Rectangle) node;
        applyFigureProperties(rectangleNode);
        applyFilledShapeProperties(rectangleNode);
        applyStrokedShapeProperties(rectangleNode);
        Rectangle2D r = get(BOUNDS);
        rectangleNode.setX(r.getMinX());
        rectangleNode.setY(r.getMinY());
        rectangleNode.setWidth(r.getWidth());
        rectangleNode.setHeight(r.getHeight());
        rectangleNode.setArcWidth(get(ARC_WIDTH));
        rectangleNode.setArcHeight(get(ARC_HEIGHT));
        rectangleNode.applyCss();
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return new ChopRectangleConnector(this);
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
