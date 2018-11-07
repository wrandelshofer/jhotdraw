/* @(#)TextableFigure.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.util.Objects;

import ch.systransis.util.ArrayList;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ListWrapper;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleListStyleableFigureKey;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.key.ListStyleableFigureKey;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import org.jhotdraw8.draw.key.StrokeStyleableMapAccessor;

/**
 * {@code TextStrokeableFigure} allows to change the stroke ofCollection the
 * text.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface TextStrokeableFigure extends Figure {

    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    CssSizeStyleableFigureKey TEXT_STROKE_DASH_OFFSET = new CssSizeStyleableFigureKey("text-stroke-dashoffset", DirtyMask.of(DirtyBits.NODE), CssSize.ZERO);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    EnumStyleableFigureKey<StrokeLineCap> TEXT_STROKE_LINE_CAP = new EnumStyleableFigureKey<>("text-stroke-linecap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE), false, StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    EnumStyleableFigureKey<StrokeLineJoin> TEXT_STROKE_LINE_JOIN = new EnumStyleableFigureKey<>("text-stroke-linejoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), false, StrokeLineJoin.MITER);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style. Default
     * value: {@code 4.0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    CssSizeStyleableFigureKey TEXT_STROKE_MITER_LIMIT = new CssSizeStyleableFigureKey("text-stroke-miterlimit", DirtyMask.of(DirtyBits.NODE), new CssSize(10.0));
    /**
     * Defines the paint used for filling the outline of the figure. Default
     * value: {@code null}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    @Nullable
    PaintableStyleableFigureKey TEXT_STROKE = new PaintableStyleableFigureKey("text-stroke", null);
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * <p>
     * Default value: {@code StrokeType.OUTSIDE}.
     */
    EnumStyleableFigureKey<StrokeType> TEXT_STROKE_TYPE = new EnumStyleableFigureKey<>("text-stroke-type", StrokeType.class, DirtyMask.of(DirtyBits.NODE), false, StrokeType.OUTSIDE);
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
    CssSizeStyleableFigureKey TEXT_STROKE_WIDTH = new CssSizeStyleableFigureKey("text-stroke-width", DirtyMask.of(DirtyBits.NODE), CssSize.ONE);

    /**
     * Defines the dash array used. Default value: {@code empty array}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    ListStyleableFigureKey<CssSize> TEXT_STROKE_DASH_ARRAY = new ListStyleableFigureKey<>("text-stroke-dasharray",
            DirtyMask.of(DirtyBits.NODE),CssSize.class,new CssSizeConverter(false), ImmutableList.emptyList());

    /**
     * Combined map accessor for all stroke style properties.
     */
    StrokeStyleableMapAccessor TEXT_STROKE_STYLE = new StrokeStyleableMapAccessor("stroke-style", TEXT_STROKE_WIDTH,
            TEXT_STROKE, TEXT_STROKE_TYPE, TEXT_STROKE_LINE_CAP, TEXT_STROKE_LINE_JOIN, TEXT_STROKE_MITER_LIMIT, TEXT_STROKE_DASH_OFFSET, TEXT_STROKE_DASH_ARRAY);

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyTextStrokeableFigureProperties(@Nonnull Shape shape) {
        Paint paint = Paintable.getPaint(getStyled(TEXT_STROKE));
        double strokeWidth = getStyled(TEXT_STROKE_WIDTH).getConvertedValue();
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
        double d = getStyled(TEXT_STROKE_MITER_LIMIT).getConvertedValue();
        if (shape.getStrokeMiterLimit() != d) {
            shape.setStrokeMiterLimit(d);
        }
        StrokeType st = getStyled(TEXT_STROKE_TYPE);
        if (shape.getStrokeType() != st) {
            shape.setStrokeType(st);
        }
        applyTextStrokeDashProperties(shape);

    }


    default void applyTextStrokeDashProperties(@Nonnull Shape shape) {
        double d = getStyledNonnull(TEXT_STROKE_DASH_OFFSET).getConvertedValue();
        if (shape.getStrokeDashOffset() != d) {
            shape.setStrokeDashOffset(d);
        }
        ImmutableList<CssSize> dashArray = getStyledNonnull(TEXT_STROKE_DASH_ARRAY);
        if (dashArray.isEmpty()) {
            shape.getStrokeDashArray().clear();
        } else {
            ArrayList<Double> list = new ArrayList<>(dashArray.size());
            for (CssSize sz : dashArray) list.add(sz.getConvertedValue());
            shape.getStrokeDashArray().setAll(list);
        }
    }

}
