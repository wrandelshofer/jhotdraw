/*
 * @(#)KindFontableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.figure;

import javafx.scene.control.Labeled;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.EnumStyleableKey;
import org.jhotdraw8.draw.key.FontStyleableMapAccessor;
import org.jhotdraw8.draw.key.StringOrIdentStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * A figure which supports font attributes.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern Figure Mixin, Traits.
 */
public interface KindFontableFigure extends Figure {

    // text properties
    /**
     * Defines the font used. Default value: {@code new Font("Arial",12)}
     */
    StringOrIdentStyleableKey KIND_FONT_FAMILY = new StringOrIdentStyleableKey("kindFontFamily", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), "Arial");
    CssSizeStyleableKey KIND_FONT_SIZE = new CssSizeStyleableKey("kindFontSize", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), new CssSize(12.0));
    EnumStyleableKey<FontPosture> KIND_FONT_STYLE = new EnumStyleableKey<>("kindFontStyle", FontPosture.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), FontPosture.REGULAR);
    EnumStyleableKey<FontWeight> KIND_FONT_WEIGHT = new EnumStyleableKey<>("kindFontWeight", FontWeight.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), FontWeight.NORMAL);
    FontStyleableMapAccessor KIND_FONT = new FontStyleableMapAccessor("kindFont", KIND_FONT_FAMILY, KIND_FONT_WEIGHT, KIND_FONT_STYLE, KIND_FONT_SIZE);

    /**
     * Updates a text node with fontable properties.
     *
     * @param ctx  RenderContext, can be null
     * @param text a text node
     */
    default void applyKindTextFontableFigureProperties(@Nullable RenderContext ctx, @Nonnull Text text) {
        String family = getStyledNonnull(KIND_FONT_FAMILY);
        FontPosture style = getStyledNonnull(KIND_FONT_STYLE);
        FontWeight weight = getStyledNonnull(KIND_FONT_WEIGHT);
        UnitConverter units = ctx == null ? DefaultUnitConverter.getInstance() : ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        CssSize cssSize = getStyledNonnull(KIND_FONT_SIZE);
        double size = units.convert(cssSize, UnitConverter.DEFAULT);
        CssFont f = CssFont.font(family, weight, style, size);

        Font font = f.getFont();
        if (!text.getFont().equals(font)) {
            text.setFont(font);
        }

        final FontSmoothingType fst = FontSmoothingType.LCD;
        if (text.getFontSmoothingType() != fst) {
            text.setFontSmoothingType(fst);
        }

    }

    /**
     * Updates a Laeled node with fontable properties.
     *
     * @param ctx  context
     * @param text a text node
     */
    default void applyKindTextFontableFigureProperties(RenderContext ctx, @Nonnull Labeled text) {
        UnitConverter units = ctx == null ? DefaultUnitConverter.getInstance() : ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        Font font = getStyledNonnull(KIND_FONT).getFont();
        if (!text.getFont().equals(font)) {
            text.setFont(font);
        }
    }
}
