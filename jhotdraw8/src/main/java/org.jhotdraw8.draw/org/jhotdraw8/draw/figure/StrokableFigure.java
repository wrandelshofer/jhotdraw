/*
 * @(#)StrokableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.EnumStyleableKey;
import org.jhotdraw8.draw.key.ListStyleableKey;
import org.jhotdraw8.draw.key.NullablePaintableStyleableKey;
import org.jhotdraw8.draw.key.StrokeStyleableMapAccessor;
import org.jhotdraw8.draw.render.RenderContext;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Interface for figures which render a {@code javafx.scene.shape.Shape} and can
 * be stroked.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern Figure Mixin, Traits.
 * <p>
 * FIXME most doubles should be CSS sizes!
 */
public interface StrokableFigure extends Figure {

    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    CssSizeStyleableKey STROKE_DASH_OFFSET = new CssSizeStyleableKey("stroke-dashoffset", DirtyMask.of(DirtyBits.NODE), CssSize.ZERO);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    EnumStyleableKey<StrokeLineCap> STROKE_LINE_CAP = new EnumStyleableKey<>("stroke-linecap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE), StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    EnumStyleableKey<StrokeLineJoin> STROKE_LINE_JOIN = new EnumStyleableKey<>("stroke-linejoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), StrokeLineJoin.MITER);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style.
     * <p>
     * Default value: {@code 4.0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    CssSizeStyleableKey STROKE_MITER_LIMIT = new CssSizeStyleableKey("stroke-miterlimit", DirtyMask.of(DirtyBits.NODE), new CssSize(4.0));
    /**
     * Defines the paint used for filling the outline of the figure. Default
     * value: {@code Color.BLACK}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    NullablePaintableStyleableKey STROKE = new NullablePaintableStyleableKey("stroke", new CssColor("black", Color.BLACK));
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * <p>
     * Default value: {@code StrokeType.CENTERED}.
     */
    EnumStyleableKey<StrokeType> STROKE_TYPE = new EnumStyleableKey<>("stroke-type", StrokeType.class, DirtyMask.of(DirtyBits.NODE), StrokeType.CENTERED);
    /**
     * Defines the width of the outline of the figure.
     * <p>
     * Default value: {@code 1.0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    CssSizeStyleableKey STROKE_WIDTH = new CssSizeStyleableKey("stroke-width", DirtyMask.of(DirtyBits.NODE), CssSize.ONE);
    /**
     * Defines the dash array used. Default value: {@code empty array}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    ListStyleableKey<CssSize> STROKE_DASH_ARRAY = new ListStyleableKey<>("stroke-dasharray", DirtyMask.of(DirtyBits.NODE),
            CssSize.class, new CssSizeConverter(false), ImmutableLists.emptyList());

    /**
     * Combined map accessor for all stroke style properties.
     * <p>
     * Note: this is a non-standard composite map accessor and thus transient!
     */
    StrokeStyleableMapAccessor STROKE_STYLE = new StrokeStyleableMapAccessor("stroke-style", STROKE_WIDTH,
            STROKE, STROKE_TYPE, STROKE_LINE_CAP, STROKE_LINE_JOIN, STROKE_MITER_LIMIT, STROKE_DASH_OFFSET, STROKE_DASH_ARRAY);

    default void applyStrokeCapAndJoinProperties(RenderContext ctx, @Nonnull Shape shape) {
        double d;
        StrokeLineCap slp = getStyled(STROKE_LINE_CAP);
        if (shape.getStrokeLineCap() != slp) {
            shape.setStrokeLineCap(slp);
        }
        StrokeLineJoin slj = getStyled(STROKE_LINE_JOIN);
        if (shape.getStrokeLineJoin() != slj) {
            shape.setStrokeLineJoin(slj);
        }
        d = getStyledNonnull(STROKE_MITER_LIMIT).getConvertedValue();
        if (shape.getStrokeMiterLimit() != d) {
            shape.setStrokeMiterLimit(d);
        }
    }

    default void applyStrokeDashProperties(RenderContext ctx, @Nonnull Shape shape) {
        double d = getStyledNonnull(STROKE_DASH_OFFSET).getConvertedValue();
        if (shape.getStrokeDashOffset() != d) {
            shape.setStrokeDashOffset(d);
        }
        ImmutableList<CssSize> dashArray = getStyledNonnull(STROKE_DASH_ARRAY);
        if (dashArray.isEmpty()) {
            shape.getStrokeDashArray().clear();
        } else {
            ArrayList<Double> list = new ArrayList<>(dashArray.size());
            for (CssSize sz : dashArray) {
                list.add(sz.getConvertedValue());
            }
            shape.getStrokeDashArray().setAll(list);
        }
    }

    default void applyStrokeTypeProperties(RenderContext ctx, @Nonnull Shape shape) {
        StrokeType st = getStyled(STROKE_TYPE);
        if (shape.getStrokeType() != st) {
            shape.setStrokeType(st);
        }
    }

    /**
     * Updates a shape node.
     *
     * @param ctx   the render context
     * @param shape a shape node
     */
    default void applyStrokableFigureProperties(@Nullable RenderContext ctx, @Nonnull Shape shape) {
        Paint p = Paintable.getPaint(getStyled(STROKE));
        applyStrokeColorProperties(ctx, shape);
        if (p == null) {
            return;
        }
        applyStrokeWidthProperties(ctx, shape);
        applyStrokeCapAndJoinProperties(ctx, shape);

        applyStrokeTypeProperties(ctx, shape);
        applyStrokeDashProperties(ctx, shape);
    }

    default void applyStrokeColorProperties(@Nullable RenderContext ctx, @Nonnull Shape shape) {
        Paint p = Paintable.getPaint(getStyled(STROKE));
        if (!Objects.equals(shape.getStroke(), p)) {
            shape.setStroke(p);
        }
    }

    default void applyStrokeWidthProperties(@Nullable RenderContext ctx, @Nonnull Shape shape) {
        CssSize cssSize = getStyledNonnull(STROKE_WIDTH);
        double width = ctx == null ? cssSize.getConvertedValue()
                : ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY).convert(cssSize, UnitConverter.DEFAULT);
        if (shape.getStrokeWidth() != width) {
            shape.setStrokeWidth(width);
        }

    }

    @Nonnull
    default BasicStroke getStyledStroke(@Nullable RenderContext ctx) {
        CssSize cssSize = getStyledNonnull(STROKE_WIDTH);
        double width = ctx == null ? cssSize.getConvertedValue()
                : ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY).convert(cssSize, UnitConverter.DEFAULT);
        final StrokeLineCap cap = getStyled(STROKE_LINE_CAP);
        final int basicCap;
        switch (cap) {
            case BUTT:
            default:
                basicCap = BasicStroke.CAP_BUTT;
                break;
            case ROUND:
                basicCap = BasicStroke.CAP_ROUND;
                break;
            case SQUARE:
                basicCap = BasicStroke.CAP_SQUARE;
                break;
        }
        final ImmutableList<CssSize> dashlist = getStyledNonnull(STROKE_DASH_ARRAY);
        float[] dasharray;
        if (dashlist.isEmpty()) {
            dasharray = null;
        } else {
            dasharray = new float[dashlist.size()];
            int i = 0;
            for (CssSize sz : dashlist) {
                dasharray[i++] = (float) sz.getConvertedValue();
            }
        }
        final double dashoffset = getStyledNonnull(STROKE_DASH_OFFSET).getConvertedValue();
        final StrokeLineJoin join = getStyledNonnull(STROKE_LINE_JOIN);
        final int basicJoin;
        switch (join) {
            case BEVEL:
            default:
                basicJoin = BasicStroke.JOIN_BEVEL;
                break;
            case MITER:
                basicJoin = BasicStroke.JOIN_MITER;
                break;
            case ROUND:
                basicJoin = BasicStroke.JOIN_ROUND;
                break;
        }
        final double miterlimit = getStyledNonnull(STROKE_MITER_LIMIT).getConvertedValue();

        return new BasicStroke((float) width, basicCap, basicJoin, (float) miterlimit, dasharray, (float) dashoffset);

    }
}
