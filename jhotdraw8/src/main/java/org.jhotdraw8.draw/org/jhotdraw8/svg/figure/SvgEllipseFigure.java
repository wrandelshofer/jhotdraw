/*
 * @(#)SvgEllipseFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Ellipse;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.figure.AbstractLeafFigure;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXTransforms;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;

/**
 * Represents an SVG 'ellipse' element.
 *
 * @author Werner Randelshofer
 */
public class SvgEllipseFigure extends AbstractLeafFigure
        implements StyleableFigure, LockableFigure, SvgTransformableFigure,
        PathIterableFigure, HideableFigure, SvgPathLengthFigure,
        SvgElementFigure, SvgDefaultableFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final @NonNull String TYPE_SELECTOR = "ellipse";
    public static final @NonNull CssSizeStyleableKey CX = SvgCircleFigure.CX;
    public static final @NonNull CssSizeStyleableKey CY = SvgCircleFigure.CY;
    public static final @NonNull CssSizeStyleableKey RX = new CssSizeStyleableKey("rx", CssSize.ONE);
    public static final @NonNull CssSizeStyleableKey RY = new CssSizeStyleableKey("ry", CssSize.ONE);

    @Override
    public @NonNull Node createNode(@NonNull RenderContext ctx) {
        Group g = new Group();
        Ellipse n0 = new Ellipse();
        Ellipse n1 = new Ellipse();
        n0.setManaged(false);
        n1.setManaged(false);
        g.getChildren().addAll(n0, n1);
        return g;
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        UnitConverter unit = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        double rx = getStyledNonNull(RX).getConvertedValue(unit);
        double ry = getStyledNonNull(RY).getConvertedValue(unit);
        Ellipse2D.Double shape=new Ellipse2D.Double(
                getStyledNonNull(CX).getConvertedValue(unit)-rx,
                getStyledNonNull(CY).getConvertedValue(unit)-ry,
                rx*2,ry*2
        );
        return shape.getPathIterator(tx);
    }


    @Override
    public @NonNull Bounds getBoundsInLocal() {
        return getCssLayoutBounds().getConvertedBoundsValue();
    }

    @Override
    public @NonNull Bounds getLayoutBounds() {
        double rx = getNonNull(RX).getConvertedValue();
        double ry = getNonNull(RY).getConvertedValue();
        double cx = getNonNull(CX).getConvertedValue();
        double cy = getNonNull(CY).getConvertedValue();
        return new BoundingBox(cx - rx, cy - ry, rx * 2.0, ry * 2.0);
    }

    @Override
    public @NonNull CssRectangle2D getCssLayoutBounds() {
        CssSize rx = getNonNull(RX);
        CssSize ry = getNonNull(RY);
        return new CssRectangle2D(
                getNonNull(CX).subtract(rx),
                getNonNull(CY).subtract(ry),
                rx.multiply(2.0),
                ry.multiply(2.0));
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        CssSize rx = CssSize.max(width.multiply(0.5), CssSize.ZERO);
        CssSize ry = CssSize.max(height.multiply(0.5), CssSize.ZERO);
        set(CX, x.add(rx));
        set(CY, y.add(ry));
        set(RX, rx);
        set(RY, ry);
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        reshapeInLocal(FXTransforms.createReshapeTransform(getLayoutBounds(), x, y, width, height));
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Group g=(Group)node;
        UnitConverter unit = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        double rx = getStyledNonNull(RX).getConvertedValue(unit);
        double ry = getStyledNonNull(RY).getConvertedValue(unit);
        if (rx <= 0||ry<=0) {
            g.setVisible(false);
            return;
        }
        Ellipse n0 = (Ellipse) g.getChildren().get(0);
        Ellipse n1 = (Ellipse) g.getChildren().get(1);

        applyHideableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, node);
        applySvgDefaultableCompositingProperties(ctx,node);
        applySvgShapeProperties(ctx,n0,n1);

        double cx = getStyledNonNull(CX).getConvertedValue(unit);
        double cy = getStyledNonNull(CY).getConvertedValue(unit);
        n0.setCenterX(cx);
        n0.setCenterY(cy);
        n0.setRadiusX(rx);
        n0.setRadiusY(ry);
        n0.applyCss();
        n1.setCenterX(cx);
        n1.setCenterY(cy);
        n1.setRadiusX(rx);
        n1.setRadiusY(ry);
        n1.applyCss();
    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
