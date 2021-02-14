/*
 * @(#)SvgCircleFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
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
 * Represents an SVG 'circle' element.
 *
 * @author Werner Randelshofer
 */
public class SvgCircleFigure extends AbstractLeafFigure
        implements StyleableFigure, LockableFigure, SvgTransformableFigure,
        PathIterableFigure, HideableFigure, SvgPathLengthFigure, SvgDefaultableFigure,
        SvgElementFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final @NonNull String TYPE_SELECTOR = "circle";
    public static final @NonNull CssSizeStyleableKey CX = new CssSizeStyleableKey("cx", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey CY = new CssSizeStyleableKey("cy", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey R = new CssSizeStyleableKey("r", CssSize.ONE);

    @Override
    public Node createNode(RenderContext ctx) {
        Group g=new Group();
        Circle n0 = new Circle();
        Circle n1 = new Circle();
        n0.setManaged(false);
        n1.setManaged(false);
        g.getChildren().addAll(n0,n1);
        return g;
    }


    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        UnitConverter unit = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        double r = getStyledNonNull(R).getConvertedValue(unit);
        Ellipse2D.Double shape=new Ellipse2D.Double(
                getStyledNonNull(CX).getConvertedValue(unit)-r,
                getStyledNonNull(CY).getConvertedValue(unit)-r,
                r*2,r*2
        );

        return shape.getPathIterator(tx);
    }


    @Override
    public @NonNull Bounds getBoundsInLocal() {
        return getCssLayoutBounds().getConvertedBoundsValue();
    }

    @Override
    public @NonNull Bounds getLayoutBounds() {
        double r = getNonNull(R).getConvertedValue();
        double cx = getNonNull(CX).getConvertedValue();
        double cy = getNonNull(CY).getConvertedValue();
        return new BoundingBox(cx - r, cy - r, r * 2.0, r * 2.0);
    }

    @Override
    public @NonNull CssRectangle2D getCssLayoutBounds() {
        CssSize r = getNonNull(R);
        return new CssRectangle2D(getNonNull(CX).subtract(r), getNonNull(CY).subtract(r), r.multiply(2.0), r.multiply(2.0));
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        CssSize rx = CssSize.max(width.multiply(0.5), CssSize.ZERO);
        CssSize ry = CssSize.max(height.multiply(0.5), CssSize.ZERO);
        CssSize r = rx.getConvertedValue() > ry.getConvertedValue() ? ry : rx;
        set(CX, x.add(rx));
        set(CY, y.add(ry));
        set(R, r);
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        reshapeInLocal(FXTransforms.createReshapeTransform(getLayoutBounds(), x, y, width, height));
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Group g=(Group)node;
        UnitConverter unit = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        double r = getStyledNonNull(R).getConvertedValue(unit);
        if (r <= 0) {
            // r==0 disables rendering
            g.setVisible(false);
            return;
        }
        Circle n0 = (Circle) g.getChildren().get(0);
        Circle n1 = (Circle) g.getChildren().get(1);

        applyHideableFigureProperties(ctx, g);
        applyStyleableFigureProperties(ctx, g);
        applyTransformableFigureProperties(ctx, g);
        applySvgDefaultableCompositingProperties(ctx,g);
        applySvgShapeProperties(ctx,n0,n1);

        double cx = getStyledNonNull(CX).getConvertedValue(unit);
        double cy = getStyledNonNull(CY).getConvertedValue(unit);
        n0.setCenterX(cx);
        n0.setCenterY(cy);
        n0.setRadius(r);
        n0.applyCss();
        n1.setCenterX(cx);
        n1.setCenterY(cy);
        n1.setRadius(r);
        n1.applyCss();
    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
