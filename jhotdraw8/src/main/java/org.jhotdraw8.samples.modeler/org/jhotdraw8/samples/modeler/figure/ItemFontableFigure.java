/*
 * @(#)ItemFontableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.figure;

import javafx.scene.control.Labeled;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.key.BooleanStyleableKey;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.EnumStyleableKey;
import org.jhotdraw8.draw.key.FontStyleableMapAccessor;
import org.jhotdraw8.draw.key.StringOrIdentStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;

/**
 * A figure which supports font attributes for items.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface ItemFontableFigure extends Figure {

    // text properties
    /**
     * Defines the font used. Default value: {@code new Font("Arial",12)}
     */
    StringOrIdentStyleableKey ITEM_FONT_FAMILY = new StringOrIdentStyleableKey("itemFontFamily", "Arial");
    CssSizeStyleableKey ITEM_FONT_SIZE = new CssSizeStyleableKey("itemFontSize", new CssSize(12.0));
    EnumStyleableKey<FontPosture> ITEM_FONT_STYLE = new EnumStyleableKey<>("itemFontStyle", FontPosture.class, FontPosture.REGULAR);
    EnumStyleableKey<FontWeight> ITEM_FONT_WEIGHT = new EnumStyleableKey<>("itemFontWeight", FontWeight.class, FontWeight.NORMAL);
    FontStyleableMapAccessor ITEM_FONT = new FontStyleableMapAccessor("itemFont", ITEM_FONT_FAMILY, ITEM_FONT_WEIGHT, ITEM_FONT_STYLE, ITEM_FONT_SIZE);
    /**
     * Whether to underline the text. Default value: {@code false}
     */
    BooleanStyleableKey ITEM_UNDERLINE = new BooleanStyleableKey("itemUnderline", false);

    /**
     * Updates a text node with fontable properties.
     *
     * @param ctx  RenderContext, can be null
     * @param text a text node
     */
    default void applyItemTextFontableFigureProperties(@Nullable RenderContext ctx, @NonNull Text text) {
        String family = getStyledNonNull(ITEM_FONT_FAMILY);
        FontPosture style = getStyledNonNull(ITEM_FONT_STYLE);
        FontWeight weight = getStyledNonNull(ITEM_FONT_WEIGHT);
        UnitConverter units = ctx == null ? DefaultUnitConverter.getInstance() : ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        CssSize cssSize = getStyledNonNull(ITEM_FONT_SIZE);
        double size = units.convert(cssSize, UnitConverter.DEFAULT);
        CssFont f = CssFont.font(family, weight, style, size);
        Boolean underline = getStyledNonNull(ITEM_UNDERLINE);

        text.setUnderline(underline);
        Font font = f.getFont();
        if (!text.getFont().equals(font)) {
            text.setFont(font);
        }

        final FontSmoothingType fst = ctx == null || ctx.getNonNull(RenderContext.RENDERING_INTENT) == RenderingIntent.EDITOR
                ? FontSmoothingType.LCD : FontSmoothingType.GRAY;
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
    default void applyItemTextFontableFigureProperties(@Nullable RenderContext ctx, @NonNull Labeled text) {
        UnitConverter units = ctx == null ? DefaultUnitConverter.getInstance() : ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        Font font = getStyledNonNull(ITEM_FONT).getFont();
        if (!text.getFont().equals(font)) {
            text.setFont(font);
        }
    }
}
