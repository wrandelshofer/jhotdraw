/* @(#)SecondStrokeableFigure.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.util.Objects;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ListWrapper;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleListStyleableFigureKey;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import org.jhotdraw8.css.Paintable;

/**
 * Interface for figures which can render a second stroke.
 *
 * @design.pattern Figure Mixin, Traits.
 * 
 * FIXME most doubles should be CSS sizes!
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface SecondStrokeableFigure extends Figure {

    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static DoubleStyleableFigureKey SECOND_STROKE_DASH_OFFSET = new DoubleStyleableFigureKey("second-stroke-dashoffset", DirtyMask.of(DirtyBits.NODE), 0.0);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static EnumStyleableFigureKey<StrokeLineCap> SECOND_STROKE_LINE_CAP = new EnumStyleableFigureKey<>("second-stroke-linecap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE),false, StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static EnumStyleableFigureKey<StrokeLineJoin> SECOND_STROKE_LINE_JOIN = new EnumStyleableFigureKey<>("second-stroke-linejoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), false,StrokeLineJoin.MITER);
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
    public static DoubleStyleableFigureKey SECOND_STROKE_MITER_LIMIT = new DoubleStyleableFigureKey("second-stroke-miterlimit", DirtyMask.of(DirtyBits.NODE), 4.0);
    /**
     * Defines the paint used for filling the outline of the figure. Default
     * value: {@code Color.BLACK}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    @Nullable
    public static PaintableStyleableFigureKey SECOND_STROKE = new PaintableStyleableFigureKey("second-stroke", null);
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * <p>
     * Default value: {@code StrokeType.CENTERED}.
     */
    public static EnumStyleableFigureKey<StrokeType> SECOND_STROKE_TYPE = new EnumStyleableFigureKey<>("second-stroke-type", StrokeType.class, DirtyMask.of(DirtyBits.NODE), false,StrokeType.CENTERED);
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
    public static DoubleStyleableFigureKey SECOND_STROKE_WIDTH = new DoubleStyleableFigureKey("second-stroke-width", DirtyMask.of(DirtyBits.NODE), 1.0);
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
     * public static DoubleStyleableFigureKey STROKE_OPACITY = new
     * DoubleStyleableFigureKey("stroke-opacity", DirtyMask.of(DirtyBits.NODE),
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
    public static DoubleListStyleableFigureKey SECOND_STROKE_DASH_ARRAY = new DoubleListStyleableFigureKey("second-stroke-dasharray", DirtyMask.of(DirtyBits.NODE), ImmutableList.emptyList());

    default void applyBorderStrokeCapAndJoinProperties(@Nonnull Shape shape) {
        double d;
        StrokeLineCap slp = getStyled(SECOND_STROKE_LINE_CAP);
        if (shape.getStrokeLineCap() != slp) {
            shape.setStrokeLineCap(slp);
        }
        StrokeLineJoin slj = getStyled(SECOND_STROKE_LINE_JOIN);
        if (shape.getStrokeLineJoin() != slj) {
            shape.setStrokeLineJoin(slj);
        }
        d = getStyled(SECOND_STROKE_MITER_LIMIT);
        if (shape.getStrokeMiterLimit() != d) {
            shape.setStrokeMiterLimit(d);
        }
    }

    default void applyBorderStrokeDashProperties(@Nonnull Shape shape) {
        double d = getStyled(SECOND_STROKE_DASH_OFFSET);
        if (shape.getStrokeDashOffset() != d) {
            shape.setStrokeDashOffset(d);
        }
        ImmutableList<Double> dashArray = getStyled(SECOND_STROKE_DASH_ARRAY);
        if (!dashArray.equals(shape.getStrokeDashArray())) {
            shape.getStrokeDashArray().setAll(new ListWrapper<>(dashArray));
        }
    }

    default void applyBorderStrokeTypeProperties(@Nonnull Shape shape) {
        StrokeType st = getStyled(SECOND_STROKE_TYPE);
        if (shape.getStrokeType() != st) {
            shape.setStrokeType(st);
        }
    }

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyBorderStrokeableFigureProperties(@Nonnull Shape shape) {
         applyBorderStrokeColorProperties( shape) ;
         applyBorderStrokeWidthProperties( shape) ;
        applyBorderStrokeCapAndJoinProperties(shape);

        applyBorderStrokeTypeProperties(shape);
        applyBorderStrokeDashProperties(shape);
    }
    default void applyBorderStrokeColorProperties(@Nonnull Shape shape) {
        Paint p = Paintable.getPaint(getStyled(SECOND_STROKE));
        if (!Objects.equals(shape.getStroke(), p)) {
            shape.setStroke(p);
        }
    }
    default void applyBorderStrokeWidthProperties(@Nonnull Shape shape) {
       double d = getStyled(SECOND_STROKE_WIDTH);
        if (shape.getStrokeWidth() != d) {
            shape.setStrokeWidth(d);
        }

    }
}
