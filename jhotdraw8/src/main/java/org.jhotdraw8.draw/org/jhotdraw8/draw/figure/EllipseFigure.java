/*
 * @(#)EllipseFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Ellipse;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.EllipseConnector;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * Renders a {@code javafx.scene.shape.Ellipse}.
 *
 * @author Werner Randelshofer
 */
public class EllipseFigure extends AbstractLeafFigure
        implements StrokableFigure, ResizableFigure, FillableFigure, TransformableFigure, HideableFigure, StyleableFigure,
        LockableFigure, CompositableFigure, ConnectableFigure, PathIterableFigure {

    public static final @NonNull CssSizeStyleableKey CENTER_X = new CssSizeStyleableKey("centerX", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey CENTER_Y = new CssSizeStyleableKey("centerY", CssSize.ZERO);
    public static final @NonNull CssPoint2DStyleableMapAccessor CENTER = new CssPoint2DStyleableMapAccessor("center", CENTER_X, CENTER_Y);
    public static final @NonNull CssSizeStyleableKey RADIUS_X = new CssSizeStyleableKey("radiusX", CssSize.ONE);
    public static final @NonNull CssSizeStyleableKey RADIUS_Y = new CssSizeStyleableKey("radiusY", CssSize.ONE);
    public static final @NonNull CssPoint2DStyleableMapAccessor RADIUS = new CssPoint2DStyleableMapAccessor("radius", RADIUS_X, RADIUS_Y);
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final String TYPE_SELECTOR = "Ellipse";

    public EllipseFigure() {
        this(0, 0, 2, 2);// the values must correspond to the default values of the property keys
    }

    public EllipseFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
    }

    public EllipseFigure(@NonNull Rectangle2D rect) {
        reshapeInLocal(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public @NonNull Node createNode(@NonNull RenderContext drawingView) {
        Ellipse n = new Ellipse();
        n.setManaged(false);
        return n;
    }

    @Override
    public @NonNull Connector findConnector(@NonNull Point2D p, Figure prototype, double tolerance) {
        return new EllipseConnector(new BoundsLocator(getLayoutBounds(), p));
    }

    @Override
    public @NonNull Bounds getLayoutBounds() {
        double rx = getNonNull(RADIUS_X).getConvertedValue();
        double ry = getNonNull(RADIUS_Y).getConvertedValue();
        double cx = getNonNull(CENTER_X).getConvertedValue();
        double cy = getNonNull(CENTER_Y).getConvertedValue();
        return new BoundingBox(cx - rx, cy - ry, rx * 2.0, ry * 2.0);
    }

    @Override
    public @NonNull CssRectangle2D getCssLayoutBounds() {
        CssSize rx = getNonNull(RADIUS_X);
        CssSize ry = getNonNull(RADIUS_Y);
        return new CssRectangle2D(getNonNull(CENTER_X).subtract(rx), getNonNull(CENTER_Y).subtract(ry), rx.multiply(2.0), ry.multiply(2.0));
    }


    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        Ellipse shape = new Ellipse();
        shape.setCenterX(getStyledNonNull(CENTER_X).getConvertedValue());
        shape.setCenterY(getStyledNonNull(CENTER_Y).getConvertedValue());

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
        return Shapes.awtShapeFromFX(shape).getPathIterator(tx);
    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        CssSize rx = CssSize.max(width.multiply(0.5), CssSize.ZERO);
        CssSize ry = CssSize.max(height.multiply(0.5), CssSize.ZERO);
        set(CENTER_X, x.add(rx));
        set(CENTER_Y, y.add(ry));
        set(RADIUS_X, rx);
        set(RADIUS_Y, ry);
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Ellipse n = (Ellipse) node;
        applyHideableFigureProperties(ctx, n);
        applyTransformableFigureProperties(ctx, n);
        applyStrokableFigureProperties(ctx, n);
        applyFillableFigureProperties(ctx, n);
        applyCompositableFigureProperties(ctx, n);
        applyStyleableFigureProperties(ctx, node);
        n.setCenterX(getStyledNonNull(CENTER_X).getConvertedValue());
        n.setCenterY(getStyledNonNull(CENTER_Y).getConvertedValue());
        n.setRadiusX(getStyledNonNull(RADIUS_X).getConvertedValue());
        n.setRadiusY(getStyledNonNull(RADIUS_Y).getConvertedValue());
        n.applyCss();
    }

}
