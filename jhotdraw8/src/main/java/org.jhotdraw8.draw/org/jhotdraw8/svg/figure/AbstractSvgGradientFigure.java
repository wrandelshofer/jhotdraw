/*
 * @(#)SvgLinearGradientFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ImmutableMaps;
import org.jhotdraw8.collection.NonNullKey;
import org.jhotdraw8.collection.SimpleNonNullKey;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssDefaultableValue;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.NamedCssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.figure.AbstractCompositeFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Grouping;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.NonTransformableFigure;
import org.jhotdraw8.draw.figure.ResizableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.key.SimpleNonNullStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.svg.css.SvgDefaultablePaint;
import org.jhotdraw8.svg.text.SvgGradientUnits;
import org.jhotdraw8.text.MappedConverter;

import java.util.ArrayList;

/**
 * Represents an SVG 'linearGradient' element.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractSvgGradientFigure extends AbstractCompositeFigure
        implements Grouping, ResizableFigure, NonTransformableFigure, HideableFigure, StyleableFigure, LockableFigure,
        SvgDefaultableFigure,
        SvgElementFigure, Paintable {

    /**
     * <a href="https://www.w3.org/TR/SVG11/pservers.html#LinearGradientElementGradientUnitsAttribute">w3.org</a>
     */
    public static final @NonNull SimpleNonNullStyleableKey<SvgGradientUnits> GRADIENT_UNITS =
            new SimpleNonNullStyleableKey<>("gradientUnits", SvgGradientUnits.class,
                    SvgGradientUnits.OBJECT_BOUNDING_BOX,
                    new MappedConverter<SvgGradientUnits>(ImmutableMaps.of(
                            "userSpaceOnUse", SvgGradientUnits.USER_SPACE_ON_USE,
                            "objectBoundingBox", SvgGradientUnits.OBJECT_BOUNDING_BOX
                    ).asMap()));

    /**
     * <a href="https://www.w3.org/TR/SVG11/pservers.html#LinearGradientElementSpreadMethodAttribute">w3.org</a>
     */
    public static final @NonNull SimpleNonNullStyleableKey<CycleMethod> SPREAD_METHOD =
            new SimpleNonNullStyleableKey<>("spreadMethod", CycleMethod.class,
                    CycleMethod.NO_CYCLE,
                    new MappedConverter<CycleMethod>(ImmutableMaps.of(
                            "pad", CycleMethod.NO_CYCLE,
                            "reflect", CycleMethod.REFLECT,
                            "repeat", CycleMethod.REPEAT
                    ).asMap()));


    public static final @NonNull NonNullKey<ImmutableList<SvgStop>> STOPS = new SimpleNonNullKey<ImmutableList<SvgStop>>("stops",
            new TypeToken<ImmutableList<SvgStop>>() {
            }, ImmutableLists.emptyList());

    public AbstractSvgGradientFigure() {
        set(VISIBLE, false);
    }

    @Override
    public @NonNull Node createNode(@NonNull RenderContext drawingView) {
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


    @NonNull
    protected ArrayList<Stop> getStops(ImmutableList<SvgStop> cssStops) {
        ArrayList<Stop> stops = new ArrayList<>(cssStops.size());
        double maxOffset = 0;
        for (SvgStop cssStop : cssStops) {
            SvgDefaultablePaint<CssColor> colorDef = cssStop.getColor();
            CssColor cssColor;
            if (colorDef == null) {
                cssColor = NamedCssColor.BLACK;
            } else if (colorDef.getDefaulting() == null) {
                cssColor = colorDef.getValue();
            } else {
                switch (colorDef.getDefaulting()) {
                default:
                case INHERIT:
                    cssColor = getDefaultableStyled(STOP_COLOR_KEY);
                    break;
                case CURRENT_COLOR:
                    cssColor = getDefaultableStyled(COLOR_KEY);
                    break;
                }
            }
            Double offset = cssStop.getOffset();
            CssDefaultableValue<CssSize> opacityDef = cssStop.getOpacity();
            CssSize opacity;
            if (opacityDef.getDefaulting() != null) {
                switch (opacityDef.getDefaulting()) {
                case INHERIT:
                    opacity = getDefaultableStyled(STOP_OPACITY_KEY);
                    break;
                default:
                    opacity = CssSize.ONE;
                    break;
                }
            } else {
                opacity = opacityDef.getValue();
            }
            Color color;
            color = cssColor == null ? Color.BLACK : cssColor.getColor();
            if (opacity != null && opacity.getConvertedValue() != 1.0) {
                color = Color.color(color.getRed(), color.getGreen(), color.getBlue(), opacity.getConvertedValue());
            }
            if (offset != null) {
                maxOffset = Math.max(offset, maxOffset);
                stops.add(new Stop(maxOffset, color));
            }
        }
        return stops;
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
