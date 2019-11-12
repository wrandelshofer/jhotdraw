/*
 * @(#)ArcFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DoubleStyleableKey;
import org.jhotdraw8.draw.key.EnumStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * Renders a {@code javafx.scene.shape.Arc}.
 *
 * @author Werner Randelshofer
 */
public class ArcFigure extends AbstractLeafFigure implements StrokableFigure, FillableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Arc";

    @Nullable
    public final static CssSizeStyleableKey CENTER_X = new CssSizeStyleableKey("centerX", CssSize.ZERO);
    @Nullable
    public final static CssSizeStyleableKey CENTER_Y = new CssSizeStyleableKey("centerY", CssSize.ZERO);
    public final static CssSizeStyleableKey RADIUS_X = new CssSizeStyleableKey("radiusX", CssSize.ONE);
    public final static CssSizeStyleableKey RADIUS_Y = new CssSizeStyleableKey("radiusY", CssSize.ONE);
    public final static DoubleStyleableKey START_ANGLE = new DoubleStyleableKey("startAngle", 0.0);
    public final static DoubleStyleableKey ARC_LENGTH = new DoubleStyleableKey("arcLength", 360.0);
    public final static EnumStyleableKey<ArcType> ARC_TYPE = new EnumStyleableKey<>("arcType", ArcType.class, ArcType.ROUND);
    @Nullable
    public final static CssPoint2DStyleableMapAccessor CENTER = new CssPoint2DStyleableMapAccessor("center", CENTER_X, CENTER_Y);
    public final static CssPoint2DStyleableMapAccessor RADIUS = new CssPoint2DStyleableMapAccessor("radius", RADIUS_X, RADIUS_Y);

    public ArcFigure() {
        this(0, 0, 1, 1);
    }

    public ArcFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
    }

    public ArcFigure(@NonNull Rectangle2D rect) {
        reshapeInLocal(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @NonNull
    @Override
    public Bounds getBoundsInLocal() {
        double rx = getNonNull(RADIUS_X).getConvertedValue();
        double ry = getNonNull(RADIUS_Y).getConvertedValue();
        double cx = getNonNull(CENTER_X).getConvertedValue();
        double cy = getNonNull(CENTER_Y).getConvertedValue();
        return new BoundingBox(cx - rx, cy - ry, rx * 2.0, ry * 2.0);
    }

    @NonNull
    @Override
    public CssRectangle2D getCssBoundsInLocal() {
        CssSize rx = getNonNull(RADIUS_X);
        CssSize ry = getNonNull(RADIUS_Y);
        CssSize cx = getNonNull(CENTER_X);
        CssSize cy = getNonNull(CENTER_Y);
        return new CssRectangle2D(cx.subtract(rx), cy.subtract(ry), rx.multiply(2.0), ry.multiply(2.0));
    }

    @Override
    public void reshapeInLocal(@NonNull Transform transform) {
        Bounds r = getBoundsInLocal();
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        reshapeInLocal(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        CssSize rx = width.getValue() > 0 ? width.multiply(0.5) : CssSize.ZERO;
        CssSize ry = height.getValue() > 0 ? height.multiply(0.5) : CssSize.ZERO;
        set(CENTER_X, x.add(rx));
        set(CENTER_Y, y.add(ry));
        set(RADIUS_X, rx);
        set(RADIUS_Y, ry);
    }

    @NonNull
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Arc();
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Arc n = (Arc) node;
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
        n.setStartAngle(getStyledNonNull(START_ANGLE));
        n.setLength(getStyledNonNull(ARC_LENGTH));
        n.setType(getStyled(ARC_TYPE));
        n.applyCss();
    }

    @NonNull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
