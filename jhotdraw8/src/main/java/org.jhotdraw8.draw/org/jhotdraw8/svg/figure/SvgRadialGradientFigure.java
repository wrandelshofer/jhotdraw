/*
 * @(#)SvgLinearGradientFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.scene.Node;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.svg.text.SvgGradientUnits;

import java.util.ArrayList;

/**
 * Represents an SVG 'radialGradient' element.
 *
 * @author Werner Randelshofer
 */
public class SvgRadialGradientFigure extends AbstractSvgGradientFigure {

    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public static final String TYPE_SELECTOR = "radialGradient";
    public static final @NonNull CssSizeStyleableKey CX = new CssSizeStyleableKey("cx", new CssSize(0.5));
    public static final @NonNull CssSizeStyleableKey CY = new CssSizeStyleableKey("cy", new CssSize(0.5));
    public static final @NonNull CssSizeStyleableKey R = new CssSizeStyleableKey("r", new CssSize(0.5));

    @Override
    public @Nullable Paint getPaint(@Nullable RenderContext ctx) {
        UnitConverter unit = ctx == null ? null : ctx.get(RenderContext.UNIT_CONVERTER_KEY);
        if (unit == null) {
            unit = DefaultUnitConverter.getInstance();
        }

        double cx = getStyledNonNull(CX).getConvertedValue(unit);
        double r = getStyledNonNull(R).getConvertedValue(unit);
        double cy = getStyledNonNull(CY).getConvertedValue(unit);
        SvgGradientUnits gradientUnits = getStyledNonNull(GRADIENT_UNITS);

        ImmutableList<SvgStop> cssStops = getNonNull(STOPS);
        ArrayList<Stop> stops = getStops(cssStops);
        CycleMethod spreadMethod = getStyledNonNull(SPREAD_METHOD);

        if (stops.size() == 1) {
            return stops.get(0).getColor();
        }

        return new RadialGradient(0, 0, cx, cy, r, gradientUnits == SvgGradientUnits.OBJECT_BOUNDING_BOX,
                spreadMethod, stops);

    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }


    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node n) {
    }

    @Override
    public boolean isSuitableParent(@NonNull Figure newParent) {
        return true;
    }


    @Override
    public boolean isSuitableChild(@NonNull Figure newChild) {
        return true;
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        // does nothing
    }
}
