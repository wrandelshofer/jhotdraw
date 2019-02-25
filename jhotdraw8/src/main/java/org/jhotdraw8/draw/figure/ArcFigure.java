/* @(#)CircleFigure.java
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
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * Renders a {@code javafx.scene.shape.Arc}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ArcFigure extends AbstractLeafFigure implements StrokableFigure, FillableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Arc";

    public final static CssSizeStyleableFigureKey CENTER_X = new CssSizeStyleableFigureKey("centerX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey CENTER_Y = new CssSizeStyleableFigureKey("centerY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT),CssSize.ZERO);
    public final static CssSizeStyleableFigureKey RADIUS_X = new CssSizeStyleableFigureKey("radiusX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ONE);
    public final static CssSizeStyleableFigureKey RADIUS_Y = new CssSizeStyleableFigureKey("radiusY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ONE);
    public final static DoubleStyleableFigureKey START_ANGLE = new DoubleStyleableFigureKey("startAngle", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey ARC_LENGTH = new DoubleStyleableFigureKey("arcLength", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 360.0);
    public final static EnumStyleableFigureKey<ArcType> ARC_TYPE = new EnumStyleableFigureKey<>("arcType", ArcType.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), ArcType.ROUND);
    public final static CssPoint2DStyleableMapAccessor CENTER = new CssPoint2DStyleableMapAccessor("center", CENTER_X, CENTER_Y);
    public final static CssPoint2DStyleableMapAccessor RADIUS = new CssPoint2DStyleableMapAccessor("radius", RADIUS_X, RADIUS_Y);

    public ArcFigure() {
        this(0, 0, 1, 1);
    }

    public ArcFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
    }

    public ArcFigure(Rectangle2D rect) {
        reshapeInLocal(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        double rx = getNonnull(RADIUS_X).getConvertedValue();
        double ry = getNonnull(RADIUS_Y).getConvertedValue();
        double cx = getNonnull(CENTER_X).getConvertedValue();
        double cy = getNonnull(CENTER_Y).getConvertedValue();
        return new BoundingBox(cx - rx, cy - ry, rx * 2.0, ry * 2.0);
    }
    @Nonnull
    @Override
    public CssRectangle2D getCssBoundsInLocal() {
        CssSize rx = getNonnull(RADIUS_X);
        CssSize ry = getNonnull(RADIUS_Y);
        CssSize cx = getNonnull(CENTER_X);
        CssSize cy = getNonnull(CENTER_Y);
        return new CssRectangle2D(cx .subtract( rx), cy.subtract( ry), rx .multiply( 2.0), ry .multiply( 2.0));
    }

    @Override
    public void reshapeInLocal(@Nonnull Transform transform) {
        Bounds r = getBoundsInLocal();
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        reshapeInLocal(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        CssSize rx = width.getValue()>0? width.multiply(0.5):CssSize.ZERO;
        CssSize ry = height.getValue()>0? height.multiply(0.5):CssSize.ZERO;
        set(CENTER_X, x .add( rx));
        set(CENTER_Y, y .add( ry));
        set(RADIUS_X, rx);
        set(RADIUS_Y, ry);
    }

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Arc();
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Arc n = (Arc) node;
        applyHideableFigureProperties(ctx, n);
        applyTransformableFigureProperties(ctx, n);
        applyStrokableFigureProperties(ctx, n);
        applyFillableFigureProperties(ctx, n);
        applyCompositableFigureProperties(ctx, n);
        applyStyleableFigureProperties(ctx, node);
        n.setCenterX(getStyledNonnull(CENTER_X).getConvertedValue());
        n.setCenterY(getStyledNonnull(CENTER_Y).getConvertedValue());
        n.setRadiusX(getStyledNonnull(RADIUS_X).getConvertedValue());
        n.setRadiusY(getStyledNonnull(RADIUS_Y).getConvertedValue());
        n.setStartAngle(getStyledNonnull(START_ANGLE));
        n.setLength(getStyledNonnull(ARC_LENGTH));
        n.setType(getStyled(ARC_TYPE));
        n.applyCss();
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
