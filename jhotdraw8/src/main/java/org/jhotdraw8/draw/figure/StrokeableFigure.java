/* @(#)StrokedShapeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
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
import org.jhotdraw8.text.CssColor;
import org.jhotdraw8.text.Paintable;

/**
 * Interface for figures which render a {@code javafx.scene.shape.Shape} and can
 * be stroked.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface StrokeableFigure extends Figure {

    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static DoubleStyleableFigureKey STROKE_DASH_OFFSET = new DoubleStyleableFigureKey("stroke-dashoffset", DirtyMask.of(DirtyBits.NODE), 0.0);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static EnumStyleableFigureKey<StrokeLineCap> STROKE_LINE_CAP = new EnumStyleableFigureKey<>("stroke-linecap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE), StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static EnumStyleableFigureKey<StrokeLineJoin> STROKE_LINE_JOIN = new EnumStyleableFigureKey<>("stroke-linejoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), StrokeLineJoin.MITER);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style. Default
     * value: {@code 4.0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static DoubleStyleableFigureKey STROKE_MITER_LIMIT = new DoubleStyleableFigureKey("stroke-miterlimit", DirtyMask.of(DirtyBits.NODE), 4.0);
    /**
     * Defines the paint used for filling the outline of the figure. Default
     * value: {@code Color.BLACK}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static PaintableStyleableFigureKey STROKE_COLOR = new PaintableStyleableFigureKey("stroke", new CssColor("black", Color.BLACK));
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * <p>
     * Default value: {@code StrokeType.CENTERED}.
     */
    public static EnumStyleableFigureKey<StrokeType> STROKE_TYPE = new EnumStyleableFigureKey<>("stroke-type", StrokeType.class, DirtyMask.of(DirtyBits.NODE), StrokeType.CENTERED);
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
    public static DoubleStyleableFigureKey STROKE_WIDTH = new DoubleStyleableFigureKey("stroke-width", DirtyMask.of(DirtyBits.NODE), 1.0);
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
    public static DoubleListStyleableFigureKey STROKE_DASH_ARRAY = new DoubleListStyleableFigureKey("stroke-dasharray", DirtyMask.of(DirtyBits.NODE), ImmutableObservableList.emptyList());

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyStrokeableFigureProperties(Shape shape) {
        double d = getStyled(STROKE_DASH_OFFSET);
        if (shape.getStrokeDashOffset() != d) {
            shape.setStrokeDashOffset(d);
        }
        StrokeLineCap slp = getStyled(STROKE_LINE_CAP);
        if (shape.getStrokeLineCap() != slp) {
            shape.setStrokeLineCap(slp);
        }
        StrokeLineJoin slj = getStyled(STROKE_LINE_JOIN);
        if (shape.getStrokeLineJoin() != slj) {
            shape.setStrokeLineJoin(slj);
        }
        d = getStyled(STROKE_MITER_LIMIT);
        if (shape.getStrokeMiterLimit() != d) {
            shape.setStrokeMiterLimit(d);
        }
        Paint p = Paintable.getPaint(getStyled(STROKE_COLOR));
        if (!Objects.equals(shape.getStroke(), p)) {
            shape.setStroke(p);
        }
        StrokeType st = getStyled(STROKE_TYPE);
        if (shape.getStrokeType() != st) {
            shape.setStrokeType(st);
        }
        d = getStyled(STROKE_WIDTH);
        if (shape.getStrokeWidth() != d) {
            shape.setStrokeWidth(d);
        }

        List<Double> dashArray = getStyled(STROKE_DASH_ARRAY);
        if (!dashArray.equals(shape.getStrokeDashArray())) {
            shape.getStrokeDashArray().setAll(dashArray);
        }
    }

}
