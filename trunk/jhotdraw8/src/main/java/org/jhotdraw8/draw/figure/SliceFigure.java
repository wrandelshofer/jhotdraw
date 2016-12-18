/* @(#)RectangleFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import static java.lang.Math.*;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jhotdraw8.draw.RenderContext;
import org.jhotdraw8.draw.RenderingIntent;
import org.jhotdraw8.draw.connector.ChopRectangleConnector;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Rectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.StringStyleableFigureKey;

/**
 * This is a special figure which is used to segment a drawing into tiles, when exporting it using the 
 * {@link BitmapExportOutputFormat}.
 * <p>
 * This figure renders only with rendering intent {@link RenderingIntent#EDITOR}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SliceFigure extends AbstractLeafFigure implements TransformableFigure, ResizableFigure, HideableFigure,LockableFigure, StyleableFigure {

    /**
     * The CSS type selector for this object is {@code "Rectangle"}.
     */
    public final static String TYPE_SELECTOR = "Slice";

    public final static DoubleStyleableFigureKey X = new DoubleStyleableFigureKey("x",  DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey Y = new DoubleStyleableFigureKey("y",  DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey WIDTH = new DoubleStyleableFigureKey("width",  DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey HEIGHT = new DoubleStyleableFigureKey("height", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static Rectangle2DStyleableMapAccessor BOUNDS = new Rectangle2DStyleableMapAccessor("bounds", X,Y,WIDTH,HEIGHT);

    public SliceFigure() {
        this(0, 0, 1, 1);
    }

    public SliceFigure(double x, double y, double width, double height) {
        reshape(x, y, width, height);
    }

    public SliceFigure(Rectangle2D rect) {
        this(rect.getMinX(),rect.getMinY(),rect.getWidth(),rect.getHeight());
    }

    @Override
    public Bounds getBoundsInLocal() {
        return new BoundingBox(get(X), get(Y), get(WIDTH), get(HEIGHT));
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
        Rectangle node= new Rectangle();
        node.setFill(new Color(0,1.0,0,0.5)) ;
        return node;
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Rectangle rectangleNode = (Rectangle) node;
        applyHideableFigureProperties(node);
        if (ctx.get(RenderContext.RENDERING_INTENT)!=RenderingIntent.EDITOR)rectangleNode.setVisible(false);
        applyTransformableFigureProperties(rectangleNode);
        rectangleNode.setX(get(X));
        rectangleNode.setY(get(Y));
        rectangleNode.setWidth(get(WIDTH));
        rectangleNode.setHeight(get(HEIGHT));
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
