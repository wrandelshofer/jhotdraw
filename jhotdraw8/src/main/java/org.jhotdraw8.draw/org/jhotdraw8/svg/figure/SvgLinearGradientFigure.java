/*
 * @(#)SvgLinearGradientFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.scene.Node;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.NonNullKey;
import org.jhotdraw8.collection.SimpleNonNullKey;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.css.text.CssStop;
import org.jhotdraw8.draw.figure.AbstractCompositeFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Grouping;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.NonTransformableFigure;
import org.jhotdraw8.draw.figure.ResizableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.svg.text.SvgCssPaintableConverter;
import org.jhotdraw8.svg.text.SvgGradientUnits;

import java.util.ArrayList;

/**
 * Represents an SVG 'linearGradient' element.
 *
 * @author Werner Randelshofer
 */
public class SvgLinearGradientFigure extends AbstractCompositeFigure
        implements Grouping, ResizableFigure, NonTransformableFigure, HideableFigure, StyleableFigure, LockableFigure,
        SvgDefaultableFigure,
        SvgElementFigure, Paintable {

    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public static final String TYPE_SELECTOR = "linearGradient";
    public static final @NonNull CssSizeStyleableKey X1 = new CssSizeStyleableKey("x1", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey Y1 = new CssSizeStyleableKey("y1", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey X2 = new CssSizeStyleableKey("x2", CssSize.ONE);
    public static final @NonNull CssSizeStyleableKey Y2 = new CssSizeStyleableKey("y2", CssSize.ZERO);
    /*
    public static final @NonNull SimpleNonNullStyleableKey<SvgGradientUnits> GRADIENT_UNITS =
            new NonNullObjectKey<>("gradientUnits", SvgGradientUnits.class,
                    SvgGradientUnits.OBJECT_BOUNDING_BOX);
*/

    public static final @NonNull NonNullKey<ImmutableList<CssStop>> STOPS = new SimpleNonNullKey<ImmutableList<CssStop>>("stops",
            new TypeToken<>() {
            }, ImmutableLists.emptyList());

    public SvgLinearGradientFigure() {
        set(VISIBLE, false);
    }

    @Override
    public @NonNull Node createNode(RenderContext drawingView) {
        javafx.scene.Group g = new javafx.scene.Group();
        g.setAutoSizeChildren(false);
        g.setManaged(false);
        g.setVisible(false);
        return g;
    }

    @Override
    public @Nullable Paint getPaint() {
        return getPaint(null);
    }

    @Override
    public @Nullable Paint getPaint(@Nullable RenderContext ctx) {
        UnitConverter unit = ctx == null ? null : ctx.get(RenderContext.UNIT_CONVERTER_KEY);
        if (unit == null) {
            unit = DefaultUnitConverter.getInstance();
        }

        double x1 = getStyledNonNull(X1).getConvertedValue(unit);
        double x2 = getStyledNonNull(X2).getConvertedValue(unit);
        double y1 = getStyledNonNull(Y1).getConvertedValue(unit);
        double y2 = getStyledNonNull(Y2).getConvertedValue(unit);
        SvgGradientUnits gradientUnits = SvgGradientUnits.OBJECT_BOUNDING_BOX;// getStyledNonNull(GRADIENT_UNITS);

        ImmutableList<CssStop> cssStops = getNonNull(STOPS);
        ArrayList<Stop> stops = new ArrayList<>(cssStops.size());
        for (CssStop stop : cssStops) {
            CssColor color = stop.getColor();
            if (SvgCssPaintableConverter.CURRENT_COLOR_KEYWORD.equals(color.getName())) {
                color = getDefaultableStyledNonNull(COLOR_KEY);
            }
            Double offset = stop.getOffset();
            if (color != null && offset != null) {
                stops.add(new Stop(offset, color.getColor()));
            }
        }

        new LinearGradient(x1, y1, x2, y2, gradientUnits == SvgGradientUnits.OBJECT_BOUNDING_BOX,
                CycleMethod.NO_CYCLE, stops);
        return CssColor.BLACK.getPaint();
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
