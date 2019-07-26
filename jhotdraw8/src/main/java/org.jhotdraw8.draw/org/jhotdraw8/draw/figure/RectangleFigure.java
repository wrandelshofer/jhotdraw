/*
 * @(#)RectangleFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SymmetricCssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * Renders a {@code javafx.scene.shape.Rectangle}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RectangleFigure extends AbstractLeafFigure
        implements StrokableFigure, FillableFigure, TransformableFigure,
        ResizableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        ConnectableFigure, PathIterableFigure, RectangularFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Rectangle";

    public final static CssSizeStyleableKey ARC_HEIGHT = new CssSizeStyleableKey("arcHeight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT_OBSERVERS), CssSize.ZERO);
    public final static CssSizeStyleableKey ARC_WIDTH = new CssSizeStyleableKey("arcWidth", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT_OBSERVERS), CssSize.ZERO);
    public final static SymmetricCssPoint2DStyleableMapAccessor ARC = new SymmetricCssPoint2DStyleableMapAccessor("arc", ARC_WIDTH, ARC_HEIGHT);

    public RectangleFigure() {
        this(0, 0, 1, 1);
    }

    public RectangleFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
    }

    public RectangleFigure(Rectangle2D rect) {
        this(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
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


    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Rectangle();
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Rectangle rectangleNode = (Rectangle) node;
        applyHideableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, rectangleNode);
        applyFillableFigureProperties(ctx, rectangleNode);
        applyStrokableFigureProperties(ctx, rectangleNode);
        applyCompositableFigureProperties(ctx, rectangleNode);
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
        return new RectangleConnector(new BoundsLocator(getBoundsInLocal(), p));
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
