/*
 * @(#)TextStrokeableFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.EnumStyleableKey;
import org.jhotdraw8.draw.key.ListStyleableKey;
import org.jhotdraw8.draw.key.NullablePaintableStyleableKey;
import org.jhotdraw8.draw.key.StrokeStyleableMapAccessor;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

/**
 * {@code TextStrokeableFigure} allows to change the stroke ofCollection the
 * text.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface TextStrokeableFigure extends Figure {

    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     * <p>
     * References:
     * <dl>
     * <dt>SVG Stroke Properties</dt>
     * <dd><a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">w3.org</a></dd>
     * </dl>
     */
    @Nullable CssSizeStyleableKey TEXT_STROKE_DASH_OFFSET = new CssSizeStyleableKey("text-stroke-dashoffset", CssSize.ZERO);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     * <p>
     * References:
     * <dl>
     * <dt>SVG Stroke Properties</dt>
     * <dd><a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">w3.org</a></dd>
     * </dl>
     */
    EnumStyleableKey<StrokeLineCap> TEXT_STROKE_LINE_CAP = new EnumStyleableKey<>("text-stroke-linecap", StrokeLineCap.class, StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     * <p>
     * References:
     * <dl>
     * <dt>SVG Stroke Properties</dt>
     * <dd><a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">w3.org</a></dd>
     * </dl>
     */
    EnumStyleableKey<StrokeLineJoin> TEXT_STROKE_LINE_JOIN = new EnumStyleableKey<>("text-stroke-linejoin", StrokeLineJoin.class, StrokeLineJoin.MITER);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style. Default
     * value: {@code 4.0}.
     * <p>
     * References:
     * <dl>
     * <dt>SVG Stroke Properties</dt>
     * <dd><a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">w3.org</a></dd>
     * </dl>
     */
    CssSizeStyleableKey TEXT_STROKE_MITER_LIMIT = new CssSizeStyleableKey("text-stroke-miterlimit", new CssSize(10.0));
    /**
     * Defines the paint used for filling the outline of the figure. Default
     * value: {@code null}.
     * <p>
     * References:
     * <dl>
     * <dt>SVG Stroke Properties</dt>
     * <dd><a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">w3.org</a></dd>
     * </dl>
     */
    @Nullable NullablePaintableStyleableKey TEXT_STROKE = new NullablePaintableStyleableKey("text-stroke", null);
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * <p>
     * Default value: {@code StrokeType.OUTSIDE}.
     */
    EnumStyleableKey<StrokeType> TEXT_STROKE_TYPE = new EnumStyleableKey<>("text-stroke-type", StrokeType.class, StrokeType.OUTSIDE);
    /**
     * Defines the width of the outline of the figure.
     * <p>
     * Default value: {@code 1.0}.
     * <p>
     * References:
     * <dl>
     * <dt>SVG Stroke Properties</dt>
     * <dd><a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">w3.org</a></dd>
     * </dl>
     */
    CssSizeStyleableKey TEXT_STROKE_WIDTH = new CssSizeStyleableKey("text-stroke-width", CssSize.ONE);

    /**
     * Defines the dash array used. Default value: {@code empty array}.
     * <p>
     * References:
     * <dl>
     * <dt>SVG Stroke Properties</dt>
     * <dd><a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">w3.org</a></dd>
     * </dl>
     */
    ListStyleableKey<CssSize> TEXT_STROKE_DASH_ARRAY = new ListStyleableKey<>("text-stroke-dasharray",
            new TypeToken<ImmutableList<CssSize>>() {
            },
            new CssSizeConverter(false), ImmutableLists.emptyList());

    /**
     * Combined map accessor for all stroke style properties.
     */
    @Nullable StrokeStyleableMapAccessor TEXT_STROKE_STYLE = new StrokeStyleableMapAccessor("text-stroke-style",
            TEXT_STROKE_TYPE, TEXT_STROKE_LINE_CAP, TEXT_STROKE_LINE_JOIN, TEXT_STROKE_MITER_LIMIT, TEXT_STROKE_DASH_OFFSET, TEXT_STROKE_DASH_ARRAY);

    /**
     * Updates a shape node.
     *
     * @param ctx   the render context
     * @param shape a shape node
     */
    default void applyTextStrokeableFigureProperties(@Nullable RenderContext ctx, @NonNull Shape shape) {
        Paint paint = Paintable.getPaint(getStyled(TEXT_STROKE));
        UnitConverter units = ctx == null ? DefaultUnitConverter.getInstance() : ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);

        double strokeWidth = units.convert(getStyledNonNull(TEXT_STROKE_WIDTH), UnitConverter.DEFAULT);
        if (!Objects.equals(shape.getStroke(), paint)) {
            shape.setStroke(paint);
        }
        if (paint == null) {
            return;
        }
        if (shape.getStrokeWidth() != strokeWidth) {
            shape.setStrokeWidth(strokeWidth);
        }
        StrokeLineCap slp = getStyled(TEXT_STROKE_LINE_CAP);
        if (shape.getStrokeLineCap() != slp) {
            shape.setStrokeLineCap(slp);
        }
        StrokeLineJoin slj = getStyled(TEXT_STROKE_LINE_JOIN);
        if (shape.getStrokeLineJoin() != slj) {
            shape.setStrokeLineJoin(slj);
        }
        double d = units.convert(getStyledNonNull(TEXT_STROKE_MITER_LIMIT), UnitConverter.DEFAULT);
        if (shape.getStrokeMiterLimit() != d) {
            shape.setStrokeMiterLimit(d);
        }
        StrokeType st = getStyled(TEXT_STROKE_TYPE);
        if (shape.getStrokeType() != st) {
            shape.setStrokeType(st);
        }
        applyTextStrokeDashProperties(shape);

    }


    default void applyTextStrokeDashProperties(@NonNull Shape shape) {
        double d = getStyledNonNull(TEXT_STROKE_DASH_OFFSET).getConvertedValue();
        if (shape.getStrokeDashOffset() != d) {
            shape.setStrokeDashOffset(d);
        }
        ImmutableList<CssSize> dashArray = getStyledNonNull(TEXT_STROKE_DASH_ARRAY);
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

}
