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

import org.jhotdraw8.css.text.CssDimension;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.DimensionRectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DimensionStyleableFigureKey;
import org.jhotdraw8.draw.key.Rectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.SymmetricDimension2DStyleableMapAccessor;
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

    public final static DimensionStyleableFigureKey X = new DimensionStyleableFigureKey("x", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssDimension.ZERO);
    public final static DimensionStyleableFigureKey Y = new DimensionStyleableFigureKey("y", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssDimension.ZERO);
    public final static DimensionStyleableFigureKey WIDTH = new DimensionStyleableFigureKey("width", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssDimension.ZERO);
    public final static DimensionStyleableFigureKey HEIGHT = new DimensionStyleableFigureKey("height", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssDimension.ZERO);
    public final static DimensionRectangle2DStyleableMapAccessor BOUNDS = new DimensionRectangle2DStyleableMapAccessor("bounds", X, Y, WIDTH, HEIGHT);
    public final static DimensionStyleableFigureKey ARC_HEIGHT = new DimensionStyleableFigureKey("arcHeight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT_OBSERVERS), CssDimension.ZERO);
    public final static DimensionStyleableFigureKey ARC_WIDTH = new DimensionStyleableFigureKey("arcWidth", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT_OBSERVERS), CssDimension.ZERO);
    public final static SymmetricDimension2DStyleableMapAccessor ARC = new SymmetricDimension2DStyleableMapAccessor("arc", ARC_WIDTH, ARC_HEIGHT);

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
        return new BoundingBox(getNonnull(X).getConvertedValue(), getNonnull(Y).getConvertedValue(), getNonnull(WIDTH).getConvertedValue(), getNonnull(HEIGHT).getConvertedValue());
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        Rectangle shape=new Rectangle();
       shape.setX(getNonnull(X).getConvertedValue());
        shape.setY(getNonnull(Y).getConvertedValue());
        shape.setWidth(getNonnull(WIDTH).getConvertedValue());
        shape.setHeight(getNonnull(HEIGHT).getConvertedValue());
        shape.setArcWidth(getStyledNonnull(ARC_WIDTH).getConvertedValue());
        shape.setArcHeight(getStyledNonnull(ARC_HEIGHT).getConvertedValue());        
       return Shapes.awtShapeFromFX(shape).getPathIterator(tx);
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        set(X, new CssDimension(x + min(width, 0),null));
        set(Y, new CssDimension(y + min(height, 0),null));
        set(WIDTH, new CssDimension(abs(width),null));
        set(HEIGHT, new CssDimension(abs(height),null));
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
        rectangleNode.setX(getNonnull(X).getConvertedValue());
        rectangleNode.setY(getNonnull(Y).getConvertedValue());
        rectangleNode.setWidth(getNonnull(WIDTH).getConvertedValue());
        rectangleNode.setHeight(getNonnull(HEIGHT).getConvertedValue());
        rectangleNode.setArcWidth(getStyledNonnull(ARC_WIDTH).getConvertedValue());
        rectangleNode.setArcHeight(getStyledNonnull(ARC_HEIGHT).getConvertedValue());
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
