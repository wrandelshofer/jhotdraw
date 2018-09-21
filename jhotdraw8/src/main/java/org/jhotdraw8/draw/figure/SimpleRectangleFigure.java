/* @(#)SimpleRectangleFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javax.annotation.Nonnull;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Rectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.SymmetricPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Shapes;

/**
 * Renders a {@code javafx.scene.shape.Rectangle}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleRectangleFigure extends AbstractLeafFigure
        implements StrokeableFigure, FillableFigure, TransformableFigure, 
        ResizableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure, 
        ConnectableFigure, PathIterableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Rectangle";

    public final static DoubleStyleableFigureKey X = new DoubleStyleableFigureKey("x", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey Y = new DoubleStyleableFigureKey("y", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey WIDTH = new DoubleStyleableFigureKey("width", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey HEIGHT = new DoubleStyleableFigureKey("height", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static Rectangle2DStyleableMapAccessor BOUNDS = new Rectangle2DStyleableMapAccessor("bounds", X, Y, WIDTH, HEIGHT);
    public final static DoubleStyleableFigureKey ARC_HEIGHT = new DoubleStyleableFigureKey("arcHeight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT_OBSERVERS), 0.0);
    public final static DoubleStyleableFigureKey ARC_WIDTH = new DoubleStyleableFigureKey("arcWidth", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT_OBSERVERS), 0.0);
    public final static SymmetricPoint2DStyleableMapAccessor ARC = new SymmetricPoint2DStyleableMapAccessor("arc", ARC_WIDTH, ARC_HEIGHT);

    public SimpleRectangleFigure() {
        this(0, 0, 1, 1);
    }

    public SimpleRectangleFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
        set(STROKE_TYPE, StrokeType.INSIDE);
    }

    public SimpleRectangleFigure(Rectangle2D rect) {
        this(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        return new BoundingBox(get(X), get(Y), get(WIDTH), get(HEIGHT));
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        Rectangle shape=new Rectangle();
       shape.setX(get(X));
        shape.setY(get(Y));
        shape.setWidth(get(WIDTH));
        shape.setHeight(get(HEIGHT));
        shape.setArcWidth(getStyled(ARC_WIDTH));
        shape.setArcHeight(getStyled(ARC_HEIGHT));        
       return Shapes.awtShapeFromFX(shape).getPathIterator(tx);
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        set(X, x + min(width, 0));
        set(Y, y + min(height, 0));
        set(WIDTH, abs(width));
        set(HEIGHT, abs(height));
    }

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Rectangle();
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
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

    @Nonnull
    @Override
    public Connector findConnector(@Nonnull Point2D p, Figure prototype) {
            return new RectangleConnector(new RelativeLocator(getBoundsInLocal(), p));
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
