/*
 * @(#)RectangleFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
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
 */
public class RectangleFigure extends AbstractLeafFigure
        implements StrokableFigure, FillableFigure, TransformableFigure,
        ResizableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        ConnectableFigure, PathIterableFigure, RectangularFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final String TYPE_SELECTOR = "Rectangle";

    public static final @NonNull CssSizeStyleableKey ARC_HEIGHT = new CssSizeStyleableKey("arcHeight", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey ARC_WIDTH = new CssSizeStyleableKey("arcWidth", CssSize.ZERO);
    public static final @Nullable SymmetricCssPoint2DStyleableMapAccessor ARC = new SymmetricCssPoint2DStyleableMapAccessor("arc", ARC_WIDTH, ARC_HEIGHT);

    public RectangleFigure() {
        this(0, 0, 1, 1);
    }

    public RectangleFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
    }

    public RectangleFigure(@NonNull Rectangle2D rect) {
        this(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        Rectangle shape = new Rectangle();
        shape.setX(getNonNull(X).getConvertedValue());
        shape.setY(getNonNull(Y).getConvertedValue());
        shape.setWidth(getNonNull(WIDTH).getConvertedValue());
        shape.setHeight(getNonNull(HEIGHT).getConvertedValue());
        shape.setArcWidth(getStyledNonNull(ARC_WIDTH).getConvertedValue());
        shape.setArcHeight(getStyledNonNull(ARC_HEIGHT).getConvertedValue());
        applyFillableFigureProperties(ctx, shape);
        applyStrokableFigureProperties(ctx, shape);

        return Shapes.awtShapeFromFX(shape).getPathIterator(tx);
    }


    @Override
    public @NonNull Node createNode(@NonNull RenderContext drawingView) {
        Rectangle n = new Rectangle();
        n.setManaged(false);
        return n;
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Rectangle rectangleNode = (Rectangle) node;
        applyHideableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, rectangleNode);
        applyFillableFigureProperties(ctx, rectangleNode);
        applyStrokableFigureProperties(ctx, rectangleNode);
        applyCompositableFigureProperties(ctx, rectangleNode);
        applyStyleableFigureProperties(ctx, node);
        rectangleNode.setX(getNonNull(X).getConvertedValue());
        rectangleNode.setY(getNonNull(Y).getConvertedValue());
        rectangleNode.setWidth(getNonNull(WIDTH).getConvertedValue());
        rectangleNode.setHeight(getNonNull(HEIGHT).getConvertedValue());
        rectangleNode.setArcWidth(getStyledNonNull(ARC_WIDTH).getConvertedValue());
        rectangleNode.setArcHeight(getStyledNonNull(ARC_HEIGHT).getConvertedValue());
    }

    @Override
    public @NonNull Connector findConnector(@NonNull Point2D p, Figure prototype, double tolerance) {
        return new RectangleConnector(new BoundsLocator(getLayoutBounds(), p));
    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
