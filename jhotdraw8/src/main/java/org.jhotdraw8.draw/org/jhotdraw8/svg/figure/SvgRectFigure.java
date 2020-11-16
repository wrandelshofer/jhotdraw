/*
 * @(#)LineFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.figure.*;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXTransforms;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.RoundRectangle2D;

/**
 * Represents an SVG 'rect' element.
 *
 * @author Werner Randelshofer
 */
public class SvgRectFigure extends AbstractLeafFigure
        implements StyleableFigure, LockableFigure, SvgTransformableFigure, PathIterableFigure, HideableFigure, SvgPathLengthFigure, SvgDefaultableFigure,
        SvgElementFigure,SvgCompositableFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final @NonNull String TYPE_SELECTOR = "rect";
    public static final @NonNull CssSizeStyleableKey X = new CssSizeStyleableKey("x", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey Y = new CssSizeStyleableKey("y", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey RX = new CssSizeStyleableKey("rx", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey RY = new CssSizeStyleableKey("ry", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey WIDTH = new CssSizeStyleableKey("width", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey HEIGHT = new CssSizeStyleableKey("height", CssSize.ZERO);

    @Override
    public Node createNode(RenderContext ctx) {
        Rectangle n = new Rectangle();
        n.setManaged(false);
        return n;
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        RoundRectangle2D.Double p = new RoundRectangle2D.Double(
                getNonNull(X).getConvertedValue(),
                getNonNull(Y).getConvertedValue(),
                getNonNull(WIDTH).getConvertedValue(),
                getNonNull(HEIGHT).getConvertedValue(),
                getNonNull(RX).getConvertedValue(),
                getNonNull(RY).getConvertedValue()
        );
        //FIXME set RX,RY
        return p.getPathIterator(tx);
    }


    @Override
    public @NonNull Bounds getBoundsInLocal() {
        return getCssLayoutBounds().getConvertedBoundsValue();
    }

    @Override
    public @NonNull CssRectangle2D getCssLayoutBounds() {
        CssSize x = getNonNull(X);
        CssSize y = getNonNull(Y);
        CssSize w = getNonNull(WIDTH);
        CssSize h = getNonNull(HEIGHT);
        return new CssRectangle2D(x, y, w, h);
    }


    @Override
    public void reshapeInLocal(@NonNull Transform transform) {
        CssSize x = getNonNull(X);
        CssSize y = getNonNull(Y);
        CssSize w = getNonNull(WIDTH);
        CssSize h = getNonNull(HEIGHT);

        CssPoint2D txy = new CssPoint2D(transform.transform(x.getConvertedValue(), y.getConvertedValue()));
        CssPoint2D twh = new CssPoint2D(transform.deltaTransform(w.getConvertedValue(), h.getConvertedValue()));
        set(X, txy.getX());
        set(Y, txy.getY());
        set(WIDTH, twh.getX());
        set(HEIGHT, twh.getY());

    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        reshapeInLocal(x.getConvertedValue(), y.getConvertedValue(), width.getConvertedValue(), height.getConvertedValue());
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        reshapeInLocal(FXTransforms.createReshapeTransform(getLayoutBounds(), x, y, width, height));
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Rectangle n = (Rectangle) node;
        applyHideableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, node);
        applySvgDefaultableFigureProperties(ctx, n);
        applySvgCompositableFigureProperties(ctx,n);
        UnitConverter unit = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        n.setX(getNonNull(X).getConvertedValue(unit));
        n.setY(getNonNull(Y).getConvertedValue(unit));
        n.setArcWidth(getNonNull(RX).getConvertedValue(unit));
        n.setArcHeight(getNonNull(RY).getConvertedValue(unit));
        n.setWidth(getNonNull(WIDTH).getConvertedValue(unit));
        n.setHeight(getNonNull(HEIGHT).getConvertedValue(unit));
        n.applyCss();
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
