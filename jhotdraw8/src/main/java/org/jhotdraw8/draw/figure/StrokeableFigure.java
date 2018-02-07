/* @(#)StrokedShapeFigure.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.BasicStroke;
import static java.lang.Math.abs;
import java.util.List;
import java.util.Objects;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleListStyleableFigureKey;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.draw.key.Paintable;

/**
 * Interface for figures which render a {@code javafx.scene.shape.Shape} and can
 * be stroked.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * FIXME most doubles should be CSS sizes!
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
    public static EnumStyleableFigureKey<StrokeLineCap> STROKE_LINE_CAP = new EnumStyleableFigureKey<>("stroke-linecap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE), false, StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    public static EnumStyleableFigureKey<StrokeLineJoin> STROKE_LINE_JOIN = new EnumStyleableFigureKey<>("stroke-linejoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), false, StrokeLineJoin.MITER);
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
    public static PaintableStyleableFigureKey STROKE = new PaintableStyleableFigureKey("stroke", new CssColor("black", Color.BLACK));
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * <p>
     * Default value: {@code StrokeType.CENTERED}.
     */
    public static EnumStyleableFigureKey<StrokeType> STROKE_TYPE = new EnumStyleableFigureKey<>("stroke-type", StrokeType.class, DirtyMask.of(DirtyBits.NODE), false, StrokeType.CENTERED);
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
    public static DoubleListStyleableFigureKey STROKE_DASH_ARRAY = new DoubleListStyleableFigureKey("stroke-dasharray", DirtyMask.of(DirtyBits.NODE), ImmutableList.emptyList());

    default void applyStrokeCapAndJoinProperties( Shape shape) {
        double d;
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
    }

    default void applyStrokeDashProperties( Shape shape) {
        double d = getStyled(STROKE_DASH_OFFSET);
        if (shape.getStrokeDashOffset() != d) {
            shape.setStrokeDashOffset(d);
        }
        List<Double> dashArray = getStyled(STROKE_DASH_ARRAY);
        if (!dashArray.equals(shape.getStrokeDashArray())) {
            shape.getStrokeDashArray().setAll(dashArray);
        }
    }

    default void applyStrokeTypeProperties( Shape shape) {
        StrokeType st = getStyled(STROKE_TYPE);
        if (shape.getStrokeType() != st) {
            shape.setStrokeType(st);
        }
    }

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyStrokeableFigureProperties( Shape shape) {
        Paint p = Paintable.getPaint(getStyled(STROKE));
        applyStrokeColorProperties(shape);
        if (p == null) {
            return;
        }
        applyStrokeWidthProperties(shape);
        applyStrokeCapAndJoinProperties(shape);

        applyStrokeTypeProperties(shape);
        applyStrokeDashProperties(shape);
    }

    default void applyStrokeColorProperties( Shape shape) {
        Paint p = Paintable.getPaint(getStyled(STROKE));
        if (!Objects.equals(shape.getStroke(), p)) {
            shape.setStroke(p);
        }
    }

    default void applyStrokeWidthProperties( Shape shape) {
        double d = getStyled(STROKE_WIDTH);
        if (shape.getStrokeWidth() != d) {
            shape.setStrokeWidth(d);
        }

    }

        default BasicStroke getStyledStroke() {
        final double width = getStyled(STROKE_WIDTH);
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
        final ImmutableList<Double> dashlist = getStyled(STROKE_DASH_ARRAY);
        float[] dasharray;
        if (dashlist.isEmpty()) {
            dasharray = null;
        } else {
            dasharray = new float[dashlist.size()];
            boolean allZero = true;
            for (int i = 0; i < dasharray.length; i++) {
                dasharray[i] = abs(dashlist.get(i).floatValue());
                allZero &= dasharray[i] == 0f;
            }
            if (allZero) {
                dasharray = null;
            }
        }
        final double dashoffset = getStyled(STROKE_DASH_OFFSET);
        final StrokeLineJoin join = getStyled(STROKE_LINE_JOIN);
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
        final double miterlimit = getStyled(STROKE_MITER_LIMIT);

        return new BasicStroke((float) width, basicCap, basicJoin, (float) miterlimit, dasharray, (float) dashoffset);

    }
}
