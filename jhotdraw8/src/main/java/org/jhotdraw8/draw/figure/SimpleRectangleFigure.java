/* @(#)SimpleRectangleFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import javax.annotation.Nonnull;

import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.CssRectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SymmetricCssPoint2DStyleableMapAccessor;
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

    public final static CssSizeStyleableFigureKey X = new CssSizeStyleableFigureKey("x", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey Y = new CssSizeStyleableFigureKey("y", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey WIDTH = new CssSizeStyleableFigureKey("width", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey HEIGHT = new CssSizeStyleableFigureKey("height", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssRectangle2DStyleableMapAccessor BOUNDS = new CssRectangle2DStyleableMapAccessor("bounds", X, Y, WIDTH, HEIGHT);
    public final static CssSizeStyleableFigureKey ARC_HEIGHT = new CssSizeStyleableFigureKey("arcHeight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT_OBSERVERS), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey ARC_WIDTH = new CssSizeStyleableFigureKey("arcWidth", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT_OBSERVERS), CssSize.ZERO);
    public final static SymmetricCssPoint2DStyleableMapAccessor ARC = new SymmetricCssPoint2DStyleableMapAccessor("arc", ARC_WIDTH, ARC_HEIGHT);

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
    public CssRectangle2D getCssBoundsInLocal() {
        return getNonnull(BOUNDS);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        Rectangle shape = new Rectangle();
        shape.setX(getNonnull(X).getConvertedValue());
        shape.setY(getNonnull(Y).getConvertedValue());
        shape.setWidth(getNonnull(WIDTH).getConvertedValue());
        shape.setHeight(getNonnull(HEIGHT).getConvertedValue());
        shape.setArcWidth(getStyledNonnull(ARC_WIDTH).getConvertedValue());
        shape.setArcHeight(getStyledNonnull(ARC_HEIGHT).getConvertedValue());
        return Shapes.awtShapeFromFX(shape).getPathIterator(tx);
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        set(X, width.getValue() < 0 ? x.add(width) : x);
        set(Y, height.getValue() < 0 ? y.add(height) : y);
        set(WIDTH, width.abs());
        set(HEIGHT, height.abs());
    }

    @Override
    public void translateInLocal(CssPoint2D t) {
        set(X, getNonnull(X).add(t.getX()));
        set(Y, getNonnull(Y).add(t.getY()));
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
