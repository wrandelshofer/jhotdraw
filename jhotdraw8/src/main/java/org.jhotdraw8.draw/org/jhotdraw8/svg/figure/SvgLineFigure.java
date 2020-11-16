/*
 * @(#)LineFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Line;
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
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

/**
 * Represents an SVG 'line' element.
 *
 * @author Werner Randelshofer
 */
public class SvgLineFigure extends AbstractLeafFigure
        implements StyleableFigure, LockableFigure, SvgTransformableFigure, PathIterableFigure, HideableFigure, SvgPathLengthFigure, SvgDefaultableFigure,
        SvgElementFigure, SvgCompositableFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final @NonNull String TYPE_SELECTOR = "line";
    public static final @NonNull CssSizeStyleableKey X1 = new CssSizeStyleableKey("x1", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey Y1 = new CssSizeStyleableKey("y1", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey X2 = new CssSizeStyleableKey("x2", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey Y2 = new CssSizeStyleableKey("y2", CssSize.ZERO);

    @Override
    public Node createNode(RenderContext ctx) {
        Line n = new Line();
        n.setManaged(false);
        return n;
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(getNonNull(X1).getConvertedValue(),
                getNonNull(Y1).getConvertedValue());
        p.lineTo(getNonNull(X2).getConvertedValue(),
                getNonNull(Y2).getConvertedValue());
        return p.getPathIterator(tx);
    }


    @Override
    public @NonNull Bounds getBoundsInLocal() {
        return getCssLayoutBounds().getConvertedBoundsValue();
    }

    @Override
    public @NonNull CssRectangle2D getCssLayoutBounds() {
        CssSize startX = getNonNull(X1);
        CssSize startY = getNonNull(Y1);
        CssSize endX = getNonNull(X2);
        CssSize endY = getNonNull(Y2);
        return new CssRectangle2D(new CssPoint2D(startX, startY), new CssPoint2D(endX, endY));
    }


    @Override
    public void reshapeInLocal(@NonNull Transform transform) {
        CssSize startX = getNonNull(X1);
        CssSize startY = getNonNull(Y1);
        CssSize endX = getNonNull(X2);
        CssSize endY = getNonNull(Y2);
        CssPoint2D start = new CssPoint2D(startX, startY);
        CssPoint2D end = new CssPoint2D(endX, endY);

        CssPoint2D tstart = new CssPoint2D(transform.transform(start.getConvertedValue()));
        CssPoint2D tend = new CssPoint2D(transform.transform(end.getConvertedValue()));
        set(X1, tstart.getX());
        set(Y1, tstart.getY());
        set(X2, tend.getX());
        set(Y2, tend.getY());

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
        Line lineNode = (Line) node;
        applyHideableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, node);
        applySvgDefaultableFigureProperties(ctx, lineNode);
        applySvgCompositableFigureProperties(ctx,lineNode);
        UnitConverter unit = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        double startX = getNonNull(X1).getConvertedValue(unit);
        double startY = getNonNull(Y1).getConvertedValue(unit);
        double endX = getNonNull(X2).getConvertedValue(unit);
        double endY = getNonNull(Y2).getConvertedValue(unit);
        lineNode.setStartX(startX);
        lineNode.setStartY(startY);
        lineNode.setEndX(endX);
        lineNode.setEndY(endY);
        lineNode.applyCss();

    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
