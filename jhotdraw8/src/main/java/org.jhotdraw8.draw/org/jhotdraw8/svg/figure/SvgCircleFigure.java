/*
 * @(#)LineFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.figure.AbstractLeafFigure;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.geom.Shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * Represents an SVG 'circle' element.
 *
 * @author Werner Randelshofer
 */
public class SvgCircleFigure extends AbstractLeafFigure
        implements StyleableFigure, LockableFigure, SvgTransformableFigure,
        PathIterableFigure, HideableFigure, SvgPathLengthFigure, SvgInheritableFigure,
        SvgElementFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final @NonNull String TYPE_SELECTOR = "circle";
    public static final @NonNull CssSizeStyleableKey CX = new CssSizeStyleableKey("cx", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey CY = new CssSizeStyleableKey("cy", CssSize.ZERO);
    public final static @NonNull CssSizeStyleableKey R = new CssSizeStyleableKey("r", CssSize.ONE);

    @Override
    public Node createNode(RenderContext ctx) {
        Circle n = new Circle();
        n.setManaged(false);
        return n;
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        Circle shape = new Circle();
        shape.setCenterX(getStyledNonNull(CX).getConvertedValue());
        shape.setCenterY(getStyledNonNull(CY).getConvertedValue());
        /*
        double strokeWidth = getStyledNonNull(STROKE_WIDTH).getConvertedValue();
        double offset;
        switch (getStyledNonNull(STROKE_TYPE)) {
        case CENTERED:
        default:
            offset = 0;
            break;
        case INSIDE:
            offset = -strokeWidth * 0.5;
            break;
        case OUTSIDE:
            offset = strokeWidth * 0.5;
            break;
        }
        shape.setRadiusX(getStyledNonNull(RADIUS_X).getConvertedValue() + offset);
        shape.setRadiusY(getStyledNonNull(RADIUS_Y).getConvertedValue() + offset);

         */
        shape.setRadius(getStyledNonNull(R).getConvertedValue());
        return Shapes.awtShapeFromFX(shape).getPathIterator(tx);
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
        Circle n = (Circle) node;
        double r = getStyledNonNull(R).getConvertedValue();
        if (r == 0) {
            // r==0 disables rendering
            n.setVisible(false);
            return;
        }
        applyHideableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, node);
        applyInheritableFigureProperties(ctx, n);
        n.setCenterX(getStyledNonNull(CX).getConvertedValue());
        n.setCenterY(getStyledNonNull(CY).getConvertedValue());
        n.setRadius(r);
        n.applyCss();


    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
