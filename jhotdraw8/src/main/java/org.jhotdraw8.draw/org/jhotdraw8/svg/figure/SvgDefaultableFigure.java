/*
 * @(#)SvgDefaultableFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.figure;

import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ImmutableMaps;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssDefaultableValue;
import org.jhotdraw8.css.CssDefaulting;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.NamedCssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.CssMappedConverter;
import org.jhotdraw8.css.text.CssPercentageConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.figure.DefaultableFigure;
import org.jhotdraw8.draw.key.DefaultableStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.svg.io.SvgFontFamilyConverter;
import org.jhotdraw8.svg.text.SvgCssPaintableConverter;
import org.jhotdraw8.svg.text.SvgDisplay;
import org.jhotdraw8.svg.text.SvgFontSize;
import org.jhotdraw8.svg.text.SvgFontSizeConverter;
import org.jhotdraw8.svg.text.SvgShapeRendering;
import org.jhotdraw8.svg.text.SvgStrokeAlignmentConverter;
import org.jhotdraw8.svg.text.SvgTextAnchor;
import org.jhotdraw8.svg.text.SvgVisibility;

import static org.jhotdraw8.svg.io.SvgFontFamilyConverter.GENERIC_FONT_FAMILY_SANS_SERIF;

/**
 * The following attributes can be defined on all SVG figures using the "defaulting"
 * mechanism.
 */
public interface SvgDefaultableFigure extends DefaultableFigure {
    /**
     * color.
     * <a href="https://www.w3.org/TR/SVGTiny12/painting.html#ColorProperty">link</a>
     */
    DefaultableStyleableKey<CssColor> COLOR_KEY = new DefaultableStyleableKey<>("color",
            new TypeToken<CssDefaultableValue<CssColor>>() {
            }, new CssColorConverter(true),
            new CssDefaultableValue<>(CssDefaulting.INHERIT, null), NamedCssColor.BLACK);
    /**
     * fill.
     * <a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/painting.html#FillProperty">link</a>
     */
    DefaultableStyleableKey<Paintable> FILL_KEY = new DefaultableStyleableKey<>("fill",
            new TypeToken<CssDefaultableValue<Paintable>>() {
            }, new SvgCssPaintableConverter(true),
            new CssDefaultableValue<>(CssDefaulting.INHERIT, null), NamedCssColor.BLACK);
    /**
     * fill-rule.
     * <p>
     * <a href="https://www.w3.org/TR/SVG11/painting.html#FillRuleProperty">">
     * SVG Tiny 1.2, The 'fill-rule' property</a>
     */
    @NonNull DefaultableStyleableKey<FillRule> FILL_RULE_KEY =
            new DefaultableStyleableKey<FillRule>("fill-rule",
                    new TypeToken<CssDefaultableValue<FillRule>>() {
                    },
                    new CssMappedConverter<>("fill-rule",
                            ImmutableMaps.of("nonzero", FillRule.NON_ZERO,
                                    "evenodd", FillRule.EVEN_ODD
                            )),
                    new CssDefaultableValue<>(CssDefaulting.INHERIT), FillRule.NON_ZERO
            );

    /**
     * font-family.
     * <p>
     * <a href="https://www.w3.org/TR/SVGTiny12/text.html#FontPropertiesUsedBySVG">link</a>
     */
    DefaultableStyleableKey<ImmutableList<String>> FONT_FAMILY_KEY = new DefaultableStyleableKey<>("font-family",
            new TypeToken<CssDefaultableValue<ImmutableList<String>>>() {
            }, new SvgFontFamilyConverter(),
            new CssDefaultableValue<>(CssDefaulting.INHERIT),
            ImmutableLists.of(GENERIC_FONT_FAMILY_SANS_SERIF)
    );

    /**
     * font-size.
     * <p>
     * <a href="https://www.w3.org/TR/SVGTiny12/text.html#FontPropertiesUsedBySVG">link</a>
     */
    DefaultableStyleableKey<SvgFontSize> FONT_SIZE_KEY = new DefaultableStyleableKey<>("font-size",
            new TypeToken<CssDefaultableValue<SvgFontSize>>() {
            }, new SvgFontSizeConverter(),
            new CssDefaultableValue<>(CssDefaulting.INHERIT),
            new SvgFontSize(SvgFontSize.SizeKeyword.MEDIUM, null)
    );
    /**
     * stroke.
     * <a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/painting.html#StrokeProperty">link</a>
     */
    DefaultableStyleableKey<Paintable> STROKE_KEY = new DefaultableStyleableKey<>("stroke",
            new TypeToken<CssDefaultableValue<Paintable>>() {
            }, new SvgCssPaintableConverter(true),
            new CssDefaultableValue<>(CssDefaulting.INHERIT, null), null);

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
     * stroke-dasharray.
     * <a href="https://www.w3.org/TR/SVGMobile12/painting.html#StrokeDasharrayProperty">link</a>
     */
    DefaultableStyleableKey<ImmutableList<Double>> STROKE_DASHARRAY_KEY = new DefaultableStyleableKey<>("stroke-dasharray",
            new TypeToken<CssDefaultableValue<ImmutableList<Double>>>() {
            }, new CssListConverter<Double>(new CssDoubleConverter(false), ", "),
            new CssDefaultableValue<ImmutableList<Double>>(CssDefaulting.INHERIT, null), null);
    /**
     * stroke-dashoffset.
     * <a href="https://www.w3.org/TR/SVGMobile12/painting.html#StrokeDashoffsetProperty">link</a>
     */
    DefaultableStyleableKey<Double> STROKE_DASHOFFSET_KEY =
            new DefaultableStyleableKey<Double>("stroke-dashoffset",
                    new TypeToken<CssDefaultableValue<Double>>() {
                    },
                    new CssPercentageConverter(false),
                    new CssDefaultableValue<>(CssDefaulting.INHERIT), 0.0);
    /**
     * fill-opacity.
     * <a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/painting.html#FillOpacityProperty">link</a>
     */
    DefaultableStyleableKey<Double> FILL_OPACITY_KEY =
            new DefaultableStyleableKey<Double>("fill-opacity",
                    new TypeToken<CssDefaultableValue<Double>>() {
                    },
                    new CssPercentageConverter(false),
                    new CssDefaultableValue<>(CssDefaulting.INHERIT), 1.0);
    /**
     * stroke-opacity.
     * <a href="https://www.w3.org/TR/SVGMobile12/painting.html#StrokeOpacityProperty">link</a>
     */
    DefaultableStyleableKey<Double> STROKE_OPACITY_KEY =
            new DefaultableStyleableKey<Double>("stroke-opacity",
                    new TypeToken<CssDefaultableValue<Double>>() {
                    },
                    new CssPercentageConverter(false),
                    new CssDefaultableValue<>(CssDefaulting.INHERIT), 1.0);
    /**
     * text-anchor.
     * <p>
     * <a href="https://www.w3.org/TR/SVGTiny12/text.html#TextAlignmentProperties">
     * SVG Tiny 1.2, Text Alignment Properties</a>
     */
    @NonNull DefaultableStyleableKey<SvgTextAnchor> TEXT_ANCHOR_KEY =
            new DefaultableStyleableKey<SvgTextAnchor>("text-anchor",
                    new TypeToken<CssDefaultableValue<SvgTextAnchor>>() {
                    },
                    new CssEnumConverter<>(SvgTextAnchor.class),
                    new CssDefaultableValue<>(CssDefaulting.INHERIT), SvgTextAnchor.START
            );
    /**
     * shape-rendering.
     * <p>
     * <a href="https://www.w3.org/TR/SVGMobile12/painting.html#ShapeRenderingProperty">">
     * SVG Tiny 1.2, The 'shape-rendering' property</a>
     */
    @NonNull DefaultableStyleableKey<SvgShapeRendering> SHAPE_RENDERING_KEY =
            new DefaultableStyleableKey<SvgShapeRendering>("shape-rendering",
                    new TypeToken<CssDefaultableValue<SvgShapeRendering>>() {
                    },
                    new CssMappedConverter<>("shape-rendering",
                            ImmutableMaps.of("auto", SvgShapeRendering.AUTO,
                                    "optimizeSpeed", SvgShapeRendering.OPTIMIZE_SPEED,
                                    "crispEdges", SvgShapeRendering.CRISP_EDGES,
                                    "geometricPrecision", SvgShapeRendering.GEOMETRIC_PRECISION)),
                    new CssDefaultableValue<>(SvgShapeRendering.GEOMETRIC_PRECISION), SvgShapeRendering.AUTO
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
     * stroke-linecap.
     * <a href="https://www.w3.org/TR/2015/WD-svg-strokes-20150409/#LineJoin">link</a>
     */
    DefaultableStyleableKey<StrokeLineCap> STROKE_LINECAP_KEY = new DefaultableStyleableKey<StrokeLineCap>("stroke-linecap",
            new TypeToken<CssDefaultableValue<StrokeLineCap>>() {
            },
            new CssMappedConverter<>("stroke-linecap",
                    ImmutableMaps.of("butt", StrokeLineCap.BUTT,
                            "round", StrokeLineCap.ROUND,
                            "square", StrokeLineCap.SQUARE)),
            new CssDefaultableValue<>(CssDefaulting.INHERIT),
            StrokeLineCap.BUTT);
    /**
     * stroke-linejoin.
     * <a href="https://www.w3.org/TR/2015/WD-svg-strokes-20150409/#LineJoin">link</a>
     */
    DefaultableStyleableKey<StrokeLineJoin> STROKE_LINEJOIN_KEY = new DefaultableStyleableKey<StrokeLineJoin>("stroke-linejoin",
            new TypeToken<CssDefaultableValue<StrokeLineJoin>>() {
            },
            new CssMappedConverter<>("stroke-linejoin",
                    ImmutableMaps.of("miter", StrokeLineJoin.MITER,
                            "round", StrokeLineJoin.ROUND,
                            "bevel", StrokeLineJoin.BEVEL)),
            new CssDefaultableValue<>(CssDefaulting.INHERIT),
            StrokeLineJoin.MITER);
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
     *     <dt>SVG Tiny 1.2</dt><dd><a href="https://www.w3.org/TR/SVGTiny12/painting.html#DisplayProperty">w3.org</a></dd>
     *     <dt>SVG 2</dt><dd><a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/render.html#VisibilityControl">w3.org</a></dd>
     * </dl>
     */
    @NonNull DefaultableStyleableKey<SvgDisplay> DISPLAY_KEY = new DefaultableStyleableKey<SvgDisplay>("display",
            new TypeToken<CssDefaultableValue<SvgDisplay>>() {
            },
            new CssMappedConverter<SvgDisplay>("display",
                    ImmutableMaps.of("inline", SvgDisplay.INLINE).asMap(), true),
            new CssDefaultableValue<>(SvgDisplay.INLINE),// not inherited by default!
            SvgDisplay.INLINE);
    /**
     * opacity.
     * <a href="https://www.w3.org/TR/2011/REC-SVG11-20110816/masking.html#ObjectAndGroupOpacityProperties">link</a>
     */
    @NonNull DefaultableStyleableKey<Double> OPACITY_KEY =
            new DefaultableStyleableKey<Double>("opacity",
                    new TypeToken<CssDefaultableValue<Double>>() {
                    },
                    new CssPercentageConverter(false),
                    new CssDefaultableValue<>(CssDefaulting.INHERIT), 1.0);

    /**
     * Updates a figure node with all effect properties defined in this
     * interface.
     * <p>
     * Applies the following properties:
     * {@link #OPACITY_KEY}.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param ctx  the render context
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applySvgDefaultableCompositingProperties(RenderContext ctx, @NonNull Node node) {
        node.setOpacity(getDefaultableStyledNonNull(OPACITY_KEY));
        BlendMode bmValue = getDefaultableStyledNonNull(MIX_BLEND_MODE_KEY);
        if (bmValue == BlendMode.SRC_OVER) {// Workaround: set SRC_OVER to null
            bmValue = null;
        }
        if (node.getBlendMode() != bmValue) {// Workaround: only set value if different
            node.setBlendMode(bmValue);
        }
    }

    /**
     * Applies fill properties to a {@link Shape} node.
     *
     * @param ctx   the render context
     * @param shape a shape node
     */
    default void applySvgDefaultableFillProperties(@NonNull RenderContext ctx, @NonNull Shape shape) {
        Paintable fill = getDefaultableStyled(FILL_KEY);
        if ((fill instanceof CssColor) && ("currentColor".equals(((CssColor) fill).getName()))) {
            fill = getDefaultableStyled(COLOR_KEY);
        }
        shape.setFill(Paintable.getPaint(fill, ctx));

        double fillOpacity = getDefaultableStyledNonNull(FILL_OPACITY_KEY);
        shape.setOpacity(fillOpacity);
    }

    /**
     * Applies stroke properties to a {@link Shape} node.
     *
     * @param ctx   the render context
     * @param shape a shape node
     */
    default void applySvgDefaultableStrokeProperties(@NonNull RenderContext ctx, @NonNull Shape shape) {
        Paintable stroke = getDefaultableStyled(STROKE_KEY);
        if ((stroke instanceof CssColor) && ("currentColor".equals(((CssColor) stroke).getName()))) {
            stroke = getDefaultableStyled(COLOR_KEY);
        }
        shape.setStroke(Paintable.getPaint(stroke, ctx));

        CssSize sw = getDefaultableStyledNonNull(STROKE_WIDTH_KEY);
        shape.setStrokeWidth(sw.getConvertedValue(ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY)));

        shape.setOpacity(getDefaultableStyledNonNull(STROKE_OPACITY_KEY));

        SvgShapeRendering shapeRendering = getDefaultableStyled(SHAPE_RENDERING_KEY);
        if (shapeRendering == SvgShapeRendering.CRISP_EDGES) {
            // stroke is translated by 0.5 pixels down right
            shape.setTranslateX(0.5);
            shape.setTranslateY(0.5);
        }
        shape.setStrokeLineCap(getDefaultableStyledNonNull(STROKE_LINECAP_KEY));
        shape.setStrokeLineJoin(getDefaultableStyledNonNull(STROKE_LINEJOIN_KEY));
        shape.setStrokeMiterLimit(getDefaultableStyledNonNull(STROKE_MITERLIMIT_KEY));
        shape.setStrokeDashOffset(getDefaultableStyledNonNull(STROKE_DASHOFFSET_KEY));
        ImmutableList<Double> dasharray = getDefaultableStyled(STROKE_DASHARRAY_KEY);
        if (dasharray == null) {
            shape.getStrokeDashArray().clear();
        } else {
            boolean allZeros = true;
            for (Double value : dasharray) {
                if (value > 0) {
                    allZeros = false;
                    break;
                }
            }
            if (allZeros) {
                shape.getStrokeDashArray().clear();
            } else {
                shape.getStrokeDashArray().setAll(dasharray.asCollection());
            }
        }

    }

    default void applySvgShapeProperties(RenderContext ctx, Shape fillShape, Shape strokeShape) {
        double strokeOpacity = getDefaultableStyledNonNull(STROKE_OPACITY_KEY);
        double fillOpacity = getDefaultableStyledNonNull(FILL_OPACITY_KEY);
        if (strokeOpacity == fillOpacity) {
            applySvgDefaultableFillProperties(ctx, fillShape);
            applySvgDefaultableStrokeProperties(ctx, fillShape);
            fillShape.setVisible(true);
            strokeShape.setVisible(false);
        } else {
            fillShape.setStroke(null);
            strokeShape.setFill(null);
            applySvgDefaultableFillProperties(ctx, fillShape);
            applySvgDefaultableStrokeProperties(ctx, strokeShape);
            fillShape.setVisible(true);
            strokeShape.setVisible(true);
        }
    }
}
