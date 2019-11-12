/*
 * @(#)MarkerStrokableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
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
    @Nullable CssSizeStyleableKey MARKER_STROKE_DASH_OFFSET = new CssSizeStyleableKey("marker-stroke-dashoffset", CssSize.ZERO);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    EnumStyleableKey<StrokeLineCap> MARKER_STROKE_LINE_CAP = new EnumStyleableKey<>("marker-stroke-linecap", StrokeLineCap.class, StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     * <p>
     * References:
     * <p>
     * <a href="http://www.w3.org/TR/SVG/painting.html#StrokeProperties">SVG
     * Stroke Properties</a>
     */
    EnumStyleableKey<StrokeLineJoin> MARKER_STROKE_LINE_JOIN = new EnumStyleableKey<>("marker-stroke-linejoin", StrokeLineJoin.class, StrokeLineJoin.MITER);
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
    CssSizeStyleableKey MARKER_STROKE_MITER_LIMIT = new CssSizeStyleableKey("marker-stroke-miterlimit", new CssSize(4.0));
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
    NullablePaintableStyleableKey MARKER_STROKE = new NullablePaintableStyleableKey("marker-stroke", null);
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * <p>
     * Default value: {@code StrokeType.CENTERED}.
     */
    EnumStyleableKey<StrokeType> MARKER_STROKE_TYPE = new EnumStyleableKey<>("marker-stroke-type", StrokeType.class, StrokeType.CENTERED);
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
    CssSizeStyleableKey MARKER_STROKE_WIDTH = new CssSizeStyleableKey("marker-stroke-width", CssSize.ONE);
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
    ListStyleableKey<CssSize> MARKER_STROKE_DASH_ARRAY = new ListStyleableKey<>("marker-stroke-dasharray",
            CssSize.class, new CssSizeConverter(false), ImmutableLists.emptyList());

    /**
     * Combined map accessor for all stroke style properties.
     * <p>
     * Note: this is a non-standard composite map accessor and thus transient!
     */
    @Nullable StrokeStyleableMapAccessor STROKE_STYLE = new StrokeStyleableMapAccessor("marker-stroke-style", MARKER_STROKE_WIDTH,
            MARKER_STROKE, MARKER_STROKE_TYPE, MARKER_STROKE_LINE_CAP, MARKER_STROKE_LINE_JOIN, MARKER_STROKE_MITER_LIMIT,
            MARKER_STROKE_DASH_OFFSET, MARKER_STROKE_DASH_ARRAY);

    default void applyMarkerStrokeCapAndJoinProperties(@NonNull Shape shape) {
        double d;
        StrokeLineCap slp = getStyled(MARKER_STROKE_LINE_CAP);
        if (shape.getStrokeLineCap() != slp) {
            shape.setStrokeLineCap(slp);
        }
        StrokeLineJoin slj = getStyled(MARKER_STROKE_LINE_JOIN);
        if (shape.getStrokeLineJoin() != slj) {
            shape.setStrokeLineJoin(slj);
        }
        d = getStyledNonNull(MARKER_STROKE_MITER_LIMIT).getConvertedValue();
        if (shape.getStrokeMiterLimit() != d) {
            shape.setStrokeMiterLimit(d);
        }
    }

    default void applyMarkerStrokeDashProperties(@NonNull Shape shape) {
        double d = getStyledNonNull(MARKER_STROKE_DASH_OFFSET).getConvertedValue();
        if (shape.getStrokeDashOffset() != d) {
            shape.setStrokeDashOffset(d);
        }
        ImmutableList<CssSize> dashArray = getStyledNonNull(MARKER_STROKE_DASH_ARRAY);
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

    default void applyMarkerStrokeTypeProperties(@NonNull Shape shape) {
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
    default void applyMarkerStrokableFigureProperties(@NonNull Shape shape) {
        applyMarkerStrokeColorProperties(shape);
        applyMarkerStrokeWidthProperties(shape);
        applyMarkerStrokeCapAndJoinProperties(shape);

        applyMarkerStrokeTypeProperties(shape);
        applyMarkerStrokeDashProperties(shape);
    }

    default void applyMarkerStrokeColorProperties(@NonNull Shape shape) {
        Paint p = Paintable.getPaint(getStyled(MARKER_STROKE));
        if (!Objects.equals(shape.getStroke(), p)) {
            shape.setStroke(p);
        }
    }

    default void applyMarkerStrokeWidthProperties(@NonNull Shape shape) {
        double d = getStyledNonNull(MARKER_STROKE_WIDTH).getConvertedValue();
        if (shape.getStrokeWidth() != d) {
            shape.setStrokeWidth(d);
        }

    }
}
