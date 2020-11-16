/*
 * @(#)SvgInheritableFigureAttributes.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.figure;

import javafx.scene.effect.BlendMode;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableMaps;
import org.jhotdraw8.css.*;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.css.text.CssMappedConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.figure.DefaultableFigure;
import org.jhotdraw8.draw.key.DefaultableStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.svg.text.*;

/**
 * The following attributes can be defined on all SVG figures using the "defaulting"
 * mechanism.
 */
public interface SvgDefaultableFigure extends DefaultableFigure {
    /**
     * stroke-alignment.
     * <a href="https://www.w3.org/TR/2015/WD-svg-strokes-20150409/#SpecifyingStrokeAlignment">link</a>
     */
    DefaultableStyleableKey<StrokeType> STROKE_ALIGNMENT_KEY = new DefaultableStyleableKey<>(
            "stroke-alignment",
            new TypeToken<CssDefaultableValue<StrokeType>>() {
            },
            new SvgStrokeAlignmentConverter(false),
            new CssDefaultableValue<>(CssDefaulting.INHERIT, null), StrokeType.CENTERED);
    /**
     * fill.
     * <a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/painting.html#FillProperty">link</a>
     */
    DefaultableStyleableKey<Paintable> FILL_KEY = new DefaultableStyleableKey<>("fill",
            new TypeToken<CssDefaultableValue<Paintable>>() {
            }, new SvgCssPaintableConverter(true),
            new CssDefaultableValue<>(CssDefaulting.INHERIT, null), CssColor.BLACK);

    /**
     * stroke.
     * <a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/painting.html#StrokeProperty">link</a>
     */
    DefaultableStyleableKey<Paintable> STROKE_KEY = new DefaultableStyleableKey<>("stroke",
            new TypeToken<CssDefaultableValue<Paintable>>() {
            }, new SvgCssPaintableConverter(true),
            new CssDefaultableValue<>(CssDefaulting.INHERIT, null), null);
    /**
     * fill-opacity.
     * <a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/painting.html#FillOpacityProperty">link</a>
     */
    DefaultableStyleableKey<CssSize> FILL_OPACITY_KEY = new DefaultableStyleableKey<>("fill-opacity",
            new TypeToken<CssDefaultableValue<CssSize>>() {
            }, new CssSizeConverter(false),
            new CssDefaultableValue<>(CssDefaulting.INHERIT, null), CssSize.ONE);
    /**
     * font-size.
     * <a href="https://www.w3.org/TR/css-fonts-3/#font-size-prop">link</a>
     */
    DefaultableStyleableKey<SvgFontSize> FONT_SIZE_KEY = new DefaultableStyleableKey<>("font-size",
            new TypeToken<CssDefaultableValue<SvgFontSize>>() {
            }, new SvgFontSizeConverter(),
            new CssDefaultableValue<>(CssDefaulting.INHERIT),
            new SvgFontSize(SvgFontSize.SizeKeyword.MEDIUM, null)
    );
    /**
     * stroke-miterlimit.
     * <a href="https://www.w3.org/TR/2015/WD-svg-strokes-20150409/#LineJoin">link</a>
     */
    DefaultableStyleableKey<Double> STROKE_MITERLIMIT_KEY = new DefaultableStyleableKey<Double>("stroke-miterlimit",
            new TypeToken<CssDefaultableValue<Double>>() {
            },
            new CssDoubleConverter(false),
            new CssDefaultableValue<>(CssDefaulting.INHERIT),
            4.0);
    /**
     * stroke-width.
     * <a href="https://www.w3.org/TR/2015/WD-svg-strokes-20150409/#StrokeWidth">link</a>
     */
    DefaultableStyleableKey<CssSize> STROKE_WIDTH_KEY = new DefaultableStyleableKey<CssSize>(
            "stroke-width",
            new TypeToken<CssDefaultableValue<CssSize>>() {
            },
            new CssSizeConverter(false),
            new CssDefaultableValue<>(CssDefaulting.INHERIT), CssSize.ONE);
    /**
     * visibility.
     * <a href="https://www.w3.org/TR/SVGTiny12/painting.html#DisplayProperty">link</a>
     */
    DefaultableStyleableKey<SvgVisibility> VISIBILITY_KEY = new DefaultableStyleableKey<>("visiblity",
            new TypeToken<CssDefaultableValue<SvgVisibility>>() {
            },
            new CssMappedConverter<SvgVisibility>("visiblity",
                    ImmutableMaps.of("visible", SvgVisibility.VISIBLE,
                            "hidden", SvgVisibility.HIDDEN,
                            "collapse", SvgVisibility.COLLAPSE).asMap()),
            new CssDefaultableValue<>(CssDefaulting.INHERIT), SvgVisibility.VISIBLE);
    /**
     * mix-blend-mode.
     * <a href="https://developer.mozilla.org/de/docs/Web/CSS/mix-blend-mode">link</a>
     */
    DefaultableStyleableKey<BlendMode> MIX_BLEND_MODE_KEY = new DefaultableStyleableKey<>("mix-blend-mode",
            new TypeToken<CssDefaultableValue<BlendMode>>() {
            },
            new CssMappedConverter<BlendMode>("mix-blend-mode",
                    ImmutableMaps.ofEntries(
                            ImmutableMaps.entry("normal", BlendMode.SRC_OVER),
                            ImmutableMaps.entry("mulitply", BlendMode.MULTIPLY),
                            ImmutableMaps.entry("screen", BlendMode.SCREEN),
                            ImmutableMaps.entry("overlay", BlendMode.OVERLAY),
                            ImmutableMaps.entry("darken", BlendMode.DARKEN),
                            ImmutableMaps.entry("lighten", BlendMode.LIGHTEN),
                            ImmutableMaps.entry("color-dodge", BlendMode.COLOR_DODGE),
                            ImmutableMaps.entry("color-burn", BlendMode.COLOR_BURN),
                            ImmutableMaps.entry("hard-light", BlendMode.HARD_LIGHT),
                            ImmutableMaps.entry("soft-light", BlendMode.SOFT_LIGHT),
                            ImmutableMaps.entry("difference", BlendMode.DIFFERENCE),
                            ImmutableMaps.entry("exclusion", BlendMode.EXCLUSION),
                            ImmutableMaps.entry("hue", BlendMode.SRC_OVER),//FIXME
                            ImmutableMaps.entry("saturation", BlendMode.SRC_OVER),//FIXME
                            ImmutableMaps.entry("color", BlendMode.SRC_OVER),//FIXME
                            ImmutableMaps.entry("luminosity", BlendMode.SRC_OVER)// FIXME
                    ).asMap()),
            new CssDefaultableValue<>(CssDefaulting.INHERIT), BlendMode.SRC_OVER);

    /**
     * display.
     * <p>
     * References:
     * <dl>
     *     <dt>SVG Tiny 1.2</dt><dd><a href="https://www.w3.org/TR/SVGTiny12/painting.html#DisplayProperty">link</a></dd>
     *     <dt>SVG 2</dt><dd><a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/render.html#VisibilityControl">link</a></dd>
     * </dl>
     */
    DefaultableStyleableKey<SvgDisplay> DISPLAY_KEY = new DefaultableStyleableKey<SvgDisplay>("display",
            new TypeToken<CssDefaultableValue<SvgDisplay>>() {
            },
            new CssMappedConverter<SvgDisplay>("display",
                    ImmutableMaps.of("inline", SvgDisplay.INLINE).asMap(), true),
            new CssDefaultableValue<>(SvgDisplay.INLINE),// not inherited by default!
            SvgDisplay.INLINE);


    /**
     * Updates a shape node.
     *
     * @param ctx   the render context
     * @param shape a shape node
     */
    default void applySvgDefaultableFigureProperties(@NonNull RenderContext ctx, @NonNull Shape shape) {
        Paintable fill = getDefaultableStyledNonNull(FILL_KEY);
        shape.setFill(Paintable.getPaint(fill, ctx));

        Paintable stroke = getDefaultableStyled(STROKE_KEY);
        shape.setStroke(Paintable.getPaint(stroke, ctx));

        BlendMode bmValue = getDefaultableStyledNonNull(MIX_BLEND_MODE_KEY);
        if (bmValue == BlendMode.SRC_OVER) {// Workaround: set SRC_OVER to nul
            bmValue = null;
        }
        if (shape.getBlendMode() != bmValue) {// Workaround: only set value if different
            shape.setBlendMode(bmValue);
        }

        CssSize sw = getDefaultableStyledNonNull(STROKE_WIDTH_KEY);
        shape.setStrokeWidth(sw.getConvertedValue(ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY)));
    }
}
