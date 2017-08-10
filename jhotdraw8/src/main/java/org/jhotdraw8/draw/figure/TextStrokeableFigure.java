/* @(#)TextableFigure.java
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
 * {@code TextStrokeableFigure} allows to change the stroke of the text.
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
    public static DoubleStyleableFigureKey TEXT_STROKE_DASH_OFFSET = new DoubleStyleableFigureKey("text-stroke-dashoffset", DirtyMask.of(DirtyBits.NODE), 0.0);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static EnumStyleableFigureKey<StrokeLineCap> TEXT_STROKE_LINE_CAP = new EnumStyleableFigureKey<>("text-stroke-linecap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE), false,StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static EnumStyleableFigureKey<StrokeLineJoin> TEXT_STROKE_LINE_JOIN = new EnumStyleableFigureKey<>("text-stroke-linejoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), false,StrokeLineJoin.MITER);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style. Default
     * value: {@code 4.0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static DoubleStyleableFigureKey TEXT_STROKE_MITER_LIMIT = new DoubleStyleableFigureKey("text-stroke-miterlimit", DirtyMask.of(DirtyBits.NODE), 4.0);
    /**
     * Defines the paint used for filling the outline of the figure. Default
     * value: {@code null}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static PaintableStyleableFigureKey TEXT_STROKE_COLOR = new PaintableStyleableFigureKey("text-stroke", null);
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * <p>
     * Default value: {@code StrokeType.OUTSIDE}.
     */
    public static EnumStyleableFigureKey<StrokeType> TEXT_STROKE_TYPE = new EnumStyleableFigureKey<>("text-stroke-type", StrokeType.class, DirtyMask.of(DirtyBits.NODE), false,StrokeType.OUTSIDE);
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
    public static DoubleStyleableFigureKey TEXT_STROKE_WIDTH = new DoubleStyleableFigureKey("text-stroke-width", DirtyMask.of(DirtyBits.NODE), 1.0);
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
    public static DoubleListStyleableFigureKey TEXT_STROKE_DASH_ARRAY = new DoubleListStyleableFigureKey("text-stroke-dasharray", DirtyMask.of(DirtyBits.NODE), ImmutableObservableList.emptyList());

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyTextStrokeableFigureProperties(Shape shape) {
        double d = getStyled(TEXT_STROKE_DASH_OFFSET);
        if (shape.getStrokeDashOffset() != d) {
            shape.setStrokeDashOffset(d);
        }
        StrokeLineCap slp = getStyled(TEXT_STROKE_LINE_CAP);
        if (shape.getStrokeLineCap() != slp) {
            shape.setStrokeLineCap(slp);
        }
        StrokeLineJoin slj = getStyled(TEXT_STROKE_LINE_JOIN);
        if (shape.getStrokeLineJoin() != slj) {
            shape.setStrokeLineJoin(slj);
        }
        d = getStyled(TEXT_STROKE_MITER_LIMIT);
        if (shape.getStrokeMiterLimit() != d) {
            shape.setStrokeMiterLimit(d);
        }
        Paint p = Paintable.getPaint(getStyled(TEXT_STROKE_COLOR));
        if (!Objects.equals(shape.getStroke(), p)) {
            shape.setStroke(p);
        }
        StrokeType st = getStyled(TEXT_STROKE_TYPE);
        if (shape.getStrokeType() != st) {
            shape.setStrokeType(st);
        }
        d = getStyled(TEXT_STROKE_WIDTH);
        if (shape.getStrokeWidth() != d) {
            shape.setStrokeWidth(d);
        }

        List<Double> dashArray = getStyled(TEXT_STROKE_DASH_ARRAY);
        if (!dashArray.equals(shape.getStrokeDashArray())) {
            shape.getStrokeDashArray().setAll(dashArray);
        }
    }

}
