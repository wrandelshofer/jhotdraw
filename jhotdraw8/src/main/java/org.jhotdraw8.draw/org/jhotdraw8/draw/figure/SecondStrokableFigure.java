/*
 * @(#)SecondStrokableFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.EnumStyleableKey;
import org.jhotdraw8.draw.key.ListStyleableKey;
import org.jhotdraw8.draw.key.NullablePaintableStyleableKey;
import org.jhotdraw8.draw.key.StrokeStyleableMapAccessor;
import org.jhotdraw8.draw.render.RenderContext;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Interface for figures which can render a second stroke.
 * <p>
 * FIXME move out of JHotDraw.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 * <p>
 * FIXME most doubles should be CSS sizes!
 */
public interface SecondStrokableFigure extends Figure {

    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    @Nullable CssSizeStyleableKey SECOND_STROKE_DASH_OFFSET = new CssSizeStyleableKey("second-stroke-dashoffset", CssSize.ZERO);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    EnumStyleableKey<StrokeLineCap> SECOND_STROKE_LINE_CAP = new EnumStyleableKey<>("second-stroke-linecap", StrokeLineCap.class, StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    EnumStyleableKey<StrokeLineJoin> SECOND_STROKE_LINE_JOIN = new EnumStyleableKey<>("second-stroke-linejoin", StrokeLineJoin.class, StrokeLineJoin.MITER);
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
    CssSizeStyleableKey SECOND_STROKE_MITER_LIMIT = new CssSizeStyleableKey("second-stroke-miterlimit", new CssSize(4.0));
    /**
     * Defines the paint used for filling the outline of the figure. Default
     * value: {@code Color.BLACK}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    @NonNull
    NullablePaintableStyleableKey SECOND_STROKE = new NullablePaintableStyleableKey("second-stroke", null);
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * <p>
     * Default value: {@code StrokeType.CENTERED}.
     */
    EnumStyleableKey<StrokeType> SECOND_STROKE_TYPE = new EnumStyleableKey<>("second-stroke-type", StrokeType.class, StrokeType.CENTERED);
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
    CssSizeStyleableKey SECOND_STROKE_WIDTH = new CssSizeStyleableKey("second-stroke-width", CssSize.ONE);
    /**
     * Defines the opacity of the outline of the figure.
     * <p>
     * Default value: {@code 1.0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     * /
     * public static NullableCssSizeStyleableKey STROKE_OPACITY = new
     * NullableCssSizeStyleableKey("stroke-opacity", DirtyMask.of(DirtyBits.NODE),
     * 1.0);
     */
    /**
     * Defines the dash array used. Default value: {@code empty array}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    ListStyleableKey<CssSize> SECOND_STROKE_DASH_ARRAY = new ListStyleableKey<>("second-stroke-dasharray",
            CssSize.class, new CssSizeConverter(false), ImmutableLists.emptyList());

    /**
     * Combined map accessor for all stroke style properties.
     * <p>
     * Note: this is a non-standard composite map accessor and thus transient!
     */
    @Nullable StrokeStyleableMapAccessor STROKE_STYLE = new StrokeStyleableMapAccessor("second-stroke-style",
            SECOND_STROKE_TYPE, SECOND_STROKE_LINE_CAP, SECOND_STROKE_LINE_JOIN, SECOND_STROKE_MITER_LIMIT,
            SECOND_STROKE_DASH_OFFSET, SECOND_STROKE_DASH_ARRAY);

    default void applySecondStrokeCapAndJoinProperties(RenderContext ctx, @NonNull Shape shape) {
        double d;
        StrokeLineCap slp = getStyled(SECOND_STROKE_LINE_CAP);
        if (shape.getStrokeLineCap() != slp) {
            shape.setStrokeLineCap(slp);
        }
        StrokeLineJoin slj = getStyled(SECOND_STROKE_LINE_JOIN);
        if (shape.getStrokeLineJoin() != slj) {
            shape.setStrokeLineJoin(slj);
        }
        d = getStyledNonNull(SECOND_STROKE_MITER_LIMIT).getConvertedValue();
        if (shape.getStrokeMiterLimit() != d) {
            shape.setStrokeMiterLimit(d);
        }
    }

    default void applySecondStrokeDashProperties(RenderContext ctx, @NonNull Shape shape) {
        double d = getStyledNonNull(SECOND_STROKE_DASH_OFFSET).getConvertedValue();
        if (shape.getStrokeDashOffset() != d) {
            shape.setStrokeDashOffset(d);
        }
        ImmutableList<CssSize> dashArray = getStyledNonNull(SECOND_STROKE_DASH_ARRAY);
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

    default void applySecondStrokeTypeProperties(RenderContext ctx, @NonNull Shape shape) {
        StrokeType st = getStyled(SECOND_STROKE_TYPE);
        if (shape.getStrokeType() != st) {
            shape.setStrokeType(st);
        }
    }

    /**
     * Updates a shape node.
     *
     * @param ctx
     * @param shape a shape node
     */
    default void applySecondStrokeableFigureProperties(RenderContext ctx, @NonNull Shape shape) {
        applySecondStrokeColorProperties(ctx, shape);
        applySecondStrokeWidthProperties(ctx, shape);
        applySecondStrokeCapAndJoinProperties(ctx, shape);

        applySecondStrokeTypeProperties(ctx, shape);
        applySecondStrokeDashProperties(ctx, shape);
    }

    default void applySecondStrokeColorProperties(RenderContext ctx, @NonNull Shape shape) {
        Paint p = Paintable.getPaint(getStyled(SECOND_STROKE));
        if (!Objects.equals(shape.getStroke(), p)) {
            shape.setStroke(p);
        }
    }

    default void applySecondStrokeWidthProperties(RenderContext ctx, @NonNull Shape shape) {
        double d = getStyledNonNull(SECOND_STROKE_WIDTH).getConvertedValue();
        if (shape.getStrokeWidth() != d) {
            shape.setStrokeWidth(d);
        }

    }
}
