/* @(#)TextFontableFigure.java
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
import org.jhotdraw8.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.key.FontStyleableMapAccessor;
import org.jhotdraw8.draw.key.StringOrIdentStyleableFigureKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * A figure which supports font attributes.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern Figure Mixin, Traits.
 */
public interface NameFontableFigure extends Figure {

    // text properties
    /**
     * Defines the font used. Default value: {@code new Font("Arial",12)}
     */
    StringOrIdentStyleableFigureKey NAME_FONT_FAMILY = new StringOrIdentStyleableFigureKey("nameFontFamily", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), "Arial");
    CssSizeStyleableFigureKey NAME_FONT_SIZE = new CssSizeStyleableFigureKey("nameFontSize", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), new CssSize(12.0));
    EnumStyleableFigureKey<FontPosture> NAME_FONT_STYLE = new EnumStyleableFigureKey<>("nameFontStyle", FontPosture.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), FontPosture.REGULAR);
    EnumStyleableFigureKey<FontWeight> NAME_FONT_WEIGHT = new EnumStyleableFigureKey<>("nameFontWeight", FontWeight.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), FontWeight.NORMAL);
    FontStyleableMapAccessor NAME_FONT = new FontStyleableMapAccessor("nameFont", NAME_FONT_FAMILY, NAME_FONT_WEIGHT, NAME_FONT_STYLE, NAME_FONT_SIZE);
    /**
     * Whether to strike through the text. Default value: {@code false}
     */
    BooleanStyleableFigureKey NAME_STRIKETHROUGH = new BooleanStyleableFigureKey("nameStrikethrough", DirtyMask.of(DirtyBits.NODE), false);
    /**
     * Whether to underline the text. Default value: {@code false}
     */
    BooleanStyleableFigureKey NAME_UNDERLINE = new BooleanStyleableFigureKey("nameUnderline", DirtyMask.of(DirtyBits.NODE), false);

    /**
     * Updates a text node with fontable properties.
     *
     * @param ctx  RenderContext, can be null
     * @param text a text node
     */
    default void applyNameTextFontableFigureProperties(@Nullable RenderContext ctx, @Nonnull Text text) {
        String family = getStyledNonnull(NAME_FONT_FAMILY);
        FontPosture style = getStyledNonnull(NAME_FONT_STYLE);
        FontWeight weight = getStyledNonnull(NAME_FONT_WEIGHT);
        UnitConverter units = ctx == null ? DefaultUnitConverter.getInstance() : ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        CssSize cssSize = getStyledNonnull(NAME_FONT_SIZE);
        double size = units.convert(cssSize, UnitConverter.DEFAULT);
        CssFont f = CssFont.font(family, weight, style, size);

        Font font = f.getFont();
        if (!text.getFont().equals(font)) {
            text.setFont(font);
        }
        boolean b = getStyledNonnull(NAME_UNDERLINE);
        if (text.isUnderline() != b) {
            text.setUnderline(b);
        }
        b = getStyledNonnull(NAME_STRIKETHROUGH);
        if (text.isStrikethrough() != b) {
            text.setStrikethrough(b);
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
    default void applyNameTextFontableFigureProperties(RenderContext ctx, @Nonnull Labeled text) {
        UnitConverter units = ctx == null ? DefaultUnitConverter.getInstance() : ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        Font font = getStyledNonnull(NAME_FONT).getFont();
        if (!text.getFont().equals(font)) {
            text.setFont(font);
        }
        boolean b = getStyledNonnull(NAME_UNDERLINE);
        if (text.isUnderline() == b) {
            text.setUnderline(b);
        }
    }
}
