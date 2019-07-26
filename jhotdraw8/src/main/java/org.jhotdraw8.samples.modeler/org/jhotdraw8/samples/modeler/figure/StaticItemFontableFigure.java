/*
 * @(#)StaticItemFontableFigure.java
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
import org.jhotdraw8.draw.key.BooleanStyleableKey;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.EnumStyleableKey;
import org.jhotdraw8.draw.key.FontStyleableMapAccessor;
import org.jhotdraw8.draw.key.StringOrIdentStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * A figure which supports font attributes for items.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern Figure Mixin, Traits.
 */
public interface StaticItemFontableFigure extends Figure {

    // text properties
    /**
     * Defines the font used. Default value: {@code new Font("Arial",12)}
     */
    StringOrIdentStyleableKey STATIC_ITEM_FONT_FAMILY = new StringOrIdentStyleableKey("staticItemFontFamily", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), "Arial");
    CssSizeStyleableKey STATIC_ITEM_FONT_SIZE = new CssSizeStyleableKey("staticItemFontSize", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), new CssSize(12.0));
    EnumStyleableKey<FontPosture> STATIC_ITEM_FONT_STYLE = new EnumStyleableKey<>("staticItemFontStyle", FontPosture.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), FontPosture.REGULAR);
    EnumStyleableKey<FontWeight> STATIC_ITEM_FONT_WEIGHT = new EnumStyleableKey<>("staticItemFontWeight", FontWeight.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), FontWeight.NORMAL);
    FontStyleableMapAccessor STATIC_ITEM_FONT = new FontStyleableMapAccessor("staticItemFont", STATIC_ITEM_FONT_FAMILY, STATIC_ITEM_FONT_WEIGHT, STATIC_ITEM_FONT_STYLE, STATIC_ITEM_FONT_SIZE);
    /**
     * Whether to underline the text. Default value: {@code false}
     */
    BooleanStyleableKey STATIC_ITEM_UNDERLINE = new BooleanStyleableKey("staticItemUnderline", false);

    /**
     * Updates a text node with fontable properties.
     *
     * @param ctx  RenderContext, can be null
     * @param text a text node
     */
    default void applyStaticItemTextFontableFigureProperties(@Nullable RenderContext ctx, @Nonnull Text text) {
        String family = getStyledNonnull(STATIC_ITEM_FONT_FAMILY);
        FontPosture style = getStyledNonnull(STATIC_ITEM_FONT_STYLE);
        FontWeight weight = getStyledNonnull(STATIC_ITEM_FONT_WEIGHT);
        UnitConverter units = ctx == null ? DefaultUnitConverter.getInstance() : ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        CssSize cssSize = getStyledNonnull(STATIC_ITEM_FONT_SIZE);
        double size = units.convert(cssSize, UnitConverter.DEFAULT);
        CssFont f = CssFont.font(family, weight, style, size);
        Boolean underline = getStyledNonnull(STATIC_ITEM_UNDERLINE);

        text.setUnderline(underline);
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
     * Updates a Labeled node with fontable properties.
     *
     * @param ctx  context
     * @param text a text node
     */
    default void applyStaticItemTextFontableFigureProperties(RenderContext ctx, @Nonnull Labeled text) {
        UnitConverter units = ctx == null ? DefaultUnitConverter.getInstance() : ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        Font font = getStyledNonnull(STATIC_ITEM_FONT).getFont();
        if (!text.getFont().equals(font)) {
            text.setFont(font);
        }
    }
}
