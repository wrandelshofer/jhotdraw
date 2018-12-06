/* @(#)SecondStrokableFigure.java
 * Copyright © by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.key.ListStyleableFigureKey;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import org.jhotdraw8.draw.key.StrokeStyleableMapAccessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Interface for figures which can render a second stroke.
 * <p>
 * FIXME move out of JHotDraw.
 *
 * @design.pattern Figure Mixin, Traits.
 * 
 * FIXME most doubles should be CSS sizes!
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface MarkerStrokableFigure extends Figure {

    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    CssSizeStyleableFigureKey MARKER_STROKE_DASH_OFFSET = new CssSizeStyleableFigureKey("marker-stroke-dashoffset", DirtyMask.of(DirtyBits.NODE), CssSize.ZERO);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    EnumStyleableFigureKey<StrokeLineCap> MARKER_STROKE_LINE_CAP = new EnumStyleableFigureKey<>("marker-stroke-linecap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE),false, StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    EnumStyleableFigureKey<StrokeLineJoin> MARKER_STROKE_LINE_JOIN = new EnumStyleableFigureKey<>("marker-stroke-linejoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), false,StrokeLineJoin.MITER);
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
    CssSizeStyleableFigureKey MARKER_STROKE_MITER_LIMIT = new CssSizeStyleableFigureKey("marker-stroke-miterlimit", DirtyMask.of(DirtyBits.NODE), new CssSize(4.0));
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
    PaintableStyleableFigureKey MARKER_STROKE = new PaintableStyleableFigureKey("marker-stroke", null);
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * <p>
     * Default value: {@code StrokeType.CENTERED}.
     */
    EnumStyleableFigureKey<StrokeType> MARKER_STROKE_TYPE = new EnumStyleableFigureKey<>("marker-stroke-type", StrokeType.class, DirtyMask.of(DirtyBits.NODE), false,StrokeType.CENTERED);
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
    CssSizeStyleableFigureKey MARKER_STROKE_WIDTH = new CssSizeStyleableFigureKey("marker-stroke-width", DirtyMask.of(DirtyBits.NODE), CssSize.ONE);
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
     * public static CssSizeStyleableFigureKey STROKE_OPACITY = new
     * CssSizeStyleableFigureKey("stroke-opacity", DirtyMask.of(DirtyBits.NODE),
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
    ListStyleableFigureKey<CssSize> MARKER_STROKE_DASH_ARRAY = new ListStyleableFigureKey<>("marker-stroke-dasharray", DirtyMask.of(DirtyBits.NODE),
            CssSize.class, new CssSizeConverter(false), ImmutableList.emptyList());

    /**
     * Combined map accessor for all stroke style properties.
     * <p>
     * Note: this is a non-standard composite map accessor and thus transient!
     */
    StrokeStyleableMapAccessor STROKE_STYLE = new StrokeStyleableMapAccessor("marker-stroke-style", MARKER_STROKE_WIDTH,
            MARKER_STROKE, MARKER_STROKE_TYPE, MARKER_STROKE_LINE_CAP, MARKER_STROKE_LINE_JOIN, MARKER_STROKE_MITER_LIMIT,
            MARKER_STROKE_DASH_OFFSET, MARKER_STROKE_DASH_ARRAY);

    default void applyMarkerStrokeCapAndJoinProperties(@Nonnull Shape shape) {
        double d;
        StrokeLineCap slp = getStyled(MARKER_STROKE_LINE_CAP);
        if (shape.getStrokeLineCap() != slp) {
            shape.setStrokeLineCap(slp);
        }
        StrokeLineJoin slj = getStyled(MARKER_STROKE_LINE_JOIN);
        if (shape.getStrokeLineJoin() != slj) {
            shape.setStrokeLineJoin(slj);
        }
        d = getStyledNonnull(MARKER_STROKE_MITER_LIMIT).getConvertedValue();
        if (shape.getStrokeMiterLimit() != d) {
            shape.setStrokeMiterLimit(d);
        }
    }

    default void applyMarkerStrokeDashProperties(@Nonnull Shape shape) {
        double d = getStyledNonnull(MARKER_STROKE_DASH_OFFSET).getConvertedValue();
        if (shape.getStrokeDashOffset() != d) {
            shape.setStrokeDashOffset(d);
        }
        ImmutableList<CssSize> dashArray = getStyledNonnull(MARKER_STROKE_DASH_ARRAY);
        if (dashArray.isEmpty()) {
            shape.getStrokeDashArray().clear();
        } else {
            ArrayList<Double> list = new ArrayList<>(dashArray.size());
            for (CssSize sz : dashArray) list.add(sz.getConvertedValue());
            shape.getStrokeDashArray().setAll(list);
        }
    }

    default void applyMarkerStrokeTypeProperties(@Nonnull Shape shape) {
        StrokeType st = getStyled(MARKER_STROKE_TYPE);
        if (shape.getStrokeType() != st) {
            shape.setStrokeType(st);
        }
    }

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyMarkerStrokableFigureProperties(@Nonnull Shape shape) {
         applyMarkerStrokeColorProperties( shape) ;
         applyMarkerStrokeWidthProperties( shape) ;
        applyMarkerStrokeCapAndJoinProperties(shape);

        applyMarkerStrokeTypeProperties(shape);
        applyMarkerStrokeDashProperties(shape);
    }
    default void applyMarkerStrokeColorProperties(@Nonnull Shape shape) {
        Paint p = Paintable.getPaint(getStyled(MARKER_STROKE));
        if (!Objects.equals(shape.getStroke(), p)) {
            shape.setStroke(p);
        }
    }
    default void applyMarkerStrokeWidthProperties(@Nonnull Shape shape) {
       double d = getStyledNonnull(MARKER_STROKE_WIDTH).getConvertedValue();
        if (shape.getStrokeWidth() != d) {
            shape.setStrokeWidth(d);
        }

    }
}
