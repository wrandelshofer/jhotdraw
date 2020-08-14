/*
 * @(#)LineFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Ellipse;
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
 * Represents an SVG 'ellipse' element.
 *
 * @author Werner Randelshofer
 */
public class SvgEllipseFigure extends AbstractLeafFigure
        implements StyleableFigure, LockableFigure, SvgTransformableFigure, PathIterableFigure, HideableFigure, SvgPathLengthFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final @NonNull String TYPE_SELECTOR = "ellipse";
    public static final @NonNull CssSizeStyleableKey CX = SvgCircleFigure.CX;
    public static final @NonNull CssSizeStyleableKey CY = SvgCircleFigure.CY;
    public static final @NonNull CssSizeStyleableKey RX = new CssSizeStyleableKey("rx", CssSize.ONE);
    public static final @NonNull CssSizeStyleableKey RY = new CssSizeStyleableKey("ry", CssSize.ONE);

    @Override
    public Node createNode(RenderContext ctx) {
        Ellipse n = new Ellipse();
        n.setManaged(false);
        return n;
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        Ellipse shape = new Ellipse();
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
        shape.setRadiusX(getStyledNonNull(RX).getConvertedValue());
        shape.setRadiusY(getStyledNonNull(RY).getConvertedValue());
        return Shapes.awtShapeFromFX(shape).getPathIterator(tx);
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
                rx.multiply(2.0));
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
        Ellipse n = (Ellipse) node;
        applyHideableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, node);
        n.setCenterX(getStyledNonNull(CX).getConvertedValue());
        n.setCenterY(getStyledNonNull(CY).getConvertedValue());
        n.setRadiusX(getStyledNonNull(RX).getConvertedValue());
        n.setRadiusY(getStyledNonNull(RY).getConvertedValue());
        n.applyCss();

    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
