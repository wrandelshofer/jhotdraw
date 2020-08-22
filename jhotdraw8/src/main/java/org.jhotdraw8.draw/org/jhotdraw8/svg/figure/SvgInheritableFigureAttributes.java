/*
 * @(#)SvgInheritableFigureAttributes.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.figure;

import javafx.scene.shape.StrokeType;
import org.jhotdraw8.collection.ImmutableMaps;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssDefaultableValue;
import org.jhotdraw8.css.CssDefaulting;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.css.text.CssMappedConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.key.DefaultableStyleableKey;
import org.jhotdraw8.svg.text.SvgDisplay;
import org.jhotdraw8.svg.text.SvgFontSize;
import org.jhotdraw8.svg.text.SvgFontSizeConverter;
import org.jhotdraw8.svg.text.SvgPaintableConverter;
import org.jhotdraw8.svg.text.SvgStrokeAlignmentConverter;
import org.jhotdraw8.svg.text.SvgVisibility;

/**
 * The following attributes can be defined on all SVG figures.
 */
public interface SvgInheritableFigureAttributes extends Figure {
    /**
     * stroke-alignment.
     * <a href="https://www.w3.org/TR/2015/WD-svg-strokes-20150409/#SpecifyingStrokeAlignment">link</a>
     */
    DefaultableStyleableKey<StrokeType> STROKE_ALIGNMENT_KEY = new DefaultableStyleableKey<>(
            "stroke-alignment", StrokeType.class, new SvgStrokeAlignmentConverter(false),
            new CssDefaultableValue<>(CssDefaulting.INHERIT, null), StrokeType.CENTERED);
    /**
     * fill.
     * <a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/painting.html#FillProperty">link</a>
     */
    DefaultableStyleableKey<Paintable> FILL_KEY = new DefaultableStyleableKey<>("fill", Paintable.class, new SvgPaintableConverter(true),
            new CssDefaultableValue<>(CssDefaulting.INHERIT, null), CssColor.BLACK);

    /**
     * stroke.
     * <a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/painting.html#StrokeProperty">link</a>
     */
    DefaultableStyleableKey<Paintable> STROKE_KEY = new DefaultableStyleableKey<>("stroke", Paintable.class, new SvgPaintableConverter(true),
            new CssDefaultableValue<>(CssDefaulting.INHERIT, null), null);
    /**
     * fill-opacity.
     * <a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/painting.html#FillOpacityProperty">link</a>
     */
    DefaultableStyleableKey<CssSize> FILL_OPACITY_KEY = new DefaultableStyleableKey<>("fill-opacity", CssSize.class, new CssSizeConverter(false),
            new CssDefaultableValue<>(CssDefaulting.INHERIT, null), CssSize.ONE);
    /**
     * font-size.
     * <a href="https://www.w3.org/TR/css-fonts-3/#font-size-prop">link</a>
     */
    DefaultableStyleableKey<SvgFontSize> FONT_SIZE_KEY = new DefaultableStyleableKey<>("font-size", SvgFontSize.class, new SvgFontSizeConverter(),
            new CssDefaultableValue<>(CssDefaulting.INHERIT),
            new SvgFontSize(SvgFontSize.SizeKeyword.MEDIUM, null)
    );
    /**
     * stroke-miterlimit.
     * <a href="https://www.w3.org/TR/2015/WD-svg-strokes-20150409/#LineJoin">link</a>
     */
    DefaultableStyleableKey<Double> STROKE_MITERLIMIT_KEY = new DefaultableStyleableKey<Double>("stroke-miterlimit",
            Double.class,
            new CssDoubleConverter(false),
            new CssDefaultableValue<>(CssDefaulting.INHERIT),
            4.0);
    /**
     * stroke-width.
     * <a href="https://www.w3.org/TR/2015/WD-svg-strokes-20150409/#StrokeWidth">link</a>
     */
    DefaultableStyleableKey<Double> STROKE_WIDTH_KEY = new DefaultableStyleableKey<>("stroke-width", Double.class,
            new CssDoubleConverter(false),
            new CssDefaultableValue<>(CssDefaulting.INHERIT), 1.0);
    /**
     * visibility.
     * <a href="https://www.w3.org/TR/SVGTiny12/painting.html#DisplayProperty">link</a>
     */
    DefaultableStyleableKey<SvgVisibility> VISIBILITY_KEY = new DefaultableStyleableKey<>("visiblity", SvgVisibility.class,
            new CssMappedConverter<SvgVisibility>("visiblity",
                    ImmutableMaps.of("visible", SvgVisibility.VISIBLE,
                            "hidden", SvgVisibility.HIDDEN,
                            "collapse", SvgVisibility.COLLAPSE).asMap()),
            new CssDefaultableValue<>(CssDefaulting.INHERIT), SvgVisibility.VISIBLE);

    /**
     * display.
     * <p>
     * References:
     * <dl>
     *     <dt>SVG Tiny 1.2</dt><dd><a href="https://www.w3.org/TR/SVGTiny12/painting.html#DisplayProperty">link</a></dd>
     *     <dt>SVG 2</dt><dd><a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/render.html#VisibilityControl">link</a></dd>
     * </dl>
     */
    DefaultableStyleableKey<SvgDisplay> DISPLAY_KEY = new DefaultableStyleableKey<SvgDisplay>("display", SvgDisplay.class,
            new CssMappedConverter<SvgDisplay>("display",
                    ImmutableMaps.of("inline", SvgDisplay.INLINE).asMap(), true),
            new CssDefaultableValue<>(SvgDisplay.INLINE),// not inherited by default!
            SvgDisplay.INLINE);
}