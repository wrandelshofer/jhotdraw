/* @(#)BorderableFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.util.List;
import java.util.Objects;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleListStyleableFigureKey;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.draw.key.Paintable;

/**
 * Interface for figures which can render a border.
 *
 * @design.pattern Figure Mixin, Traits.
 * 
 * FIXME most doubles should be CSS sizes!
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface BorderableFigure extends Figure {

    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static DoubleStyleableFigureKey BORDER_STROKE_DASH_OFFSET = new DoubleStyleableFigureKey("border-stroke-dashoffset", DirtyMask.of(DirtyBits.NODE), 0.0);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static EnumStyleableFigureKey<StrokeLineCap> BORDER_STROKE_LINE_CAP = new EnumStyleableFigureKey<>("border-stroke-linecap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE),false, StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static EnumStyleableFigureKey<StrokeLineJoin> BORDER_STROKE_LINE_JOIN = new EnumStyleableFigureKey<>("border-stroke-linejoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), false,StrokeLineJoin.MITER);
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
    public static DoubleStyleableFigureKey BORDER_STROKE_MITER_LIMIT = new DoubleStyleableFigureKey("border-stroke-miterlimit", DirtyMask.of(DirtyBits.NODE), 4.0);
    /**
     * Defines the paint used for filling the outline of the figure. Default
     * value: {@code Color.BLACK}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static PaintableStyleableFigureKey BORDER_STROKE = new PaintableStyleableFigureKey("border-stroke", null);
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * <p>
     * Default value: {@code StrokeType.CENTERED}.
     */
    public static EnumStyleableFigureKey<StrokeType> BORDER_STROKE_TYPE = new EnumStyleableFigureKey<>("border-stroke-type", StrokeType.class, DirtyMask.of(DirtyBits.NODE), false,StrokeType.CENTERED);
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
    public static DoubleStyleableFigureKey BORDER_STROKE_WIDTH = new DoubleStyleableFigureKey("border-stroke-width", DirtyMask.of(DirtyBits.NODE), 1.0);
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
    public static DoubleListStyleableFigureKey BORDER_STROKE_DASH_ARRAY = new DoubleListStyleableFigureKey("border-stroke-dasharray", DirtyMask.of(DirtyBits.NODE), ImmutableObservableList.emptyList());

    default void applyBorderStrokeCapAndJoinProperties(Shape shape) {
        double d;
        StrokeLineCap slp = getStyled(BORDER_STROKE_LINE_CAP);
        if (shape.getStrokeLineCap() != slp) {
            shape.setStrokeLineCap(slp);
        }
        StrokeLineJoin slj = getStyled(BORDER_STROKE_LINE_JOIN);
        if (shape.getStrokeLineJoin() != slj) {
            shape.setStrokeLineJoin(slj);
        }
        d = getStyled(BORDER_STROKE_MITER_LIMIT);
        if (shape.getStrokeMiterLimit() != d) {
            shape.setStrokeMiterLimit(d);
        }
    }

    default void applyBorderStrokeDashProperties(Shape shape) {
        double d = getStyled(BORDER_STROKE_DASH_OFFSET);
        if (shape.getStrokeDashOffset() != d) {
            shape.setStrokeDashOffset(d);
        }
        List<Double> dashArray = getStyled(BORDER_STROKE_DASH_ARRAY);
        if (!dashArray.equals(shape.getStrokeDashArray())) {
            shape.getStrokeDashArray().setAll(dashArray);
        }
    }

    default void applyBorderStrokeTypeProperties(Shape shape) {
        StrokeType st = getStyled(BORDER_STROKE_TYPE);
        if (shape.getStrokeType() != st) {
            shape.setStrokeType(st);
        }
    }

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyBorderStrokeableFigureProperties(Shape shape) {
         applyBorderStrokeColorProperties( shape) ;
         applyBorderStrokeWidthProperties( shape) ;
        applyBorderStrokeCapAndJoinProperties(shape);

        applyBorderStrokeTypeProperties(shape);
        applyBorderStrokeDashProperties(shape);
    }
    default void applyBorderStrokeColorProperties(Shape shape) {
        Paint p = Paintable.getPaint(getStyled(BORDER_STROKE));
        if (!Objects.equals(shape.getStroke(), p)) {
            shape.setStroke(p);
        }
    }
    default void applyBorderStrokeWidthProperties(Shape shape) {
       double d = getStyled(BORDER_STROKE_WIDTH);
        if (shape.getStrokeWidth() != d) {
            shape.setStrokeWidth(d);
        }

    }
}
