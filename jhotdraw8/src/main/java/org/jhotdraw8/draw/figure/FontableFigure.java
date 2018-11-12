/* @(#)FontableFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import javafx.geometry.VPos;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.key.FontStyleableMapAccessor;
import org.jhotdraw8.draw.key.StringOrIdentStyleableFigureKey;

/**
 * A figure which supports font attributes.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FontableFigure extends Figure {

    // text properties
    /**
     * Defines the font used. Default value: {@code new Font("Arial",12)}
     */
    public static StringOrIdentStyleableFigureKey FONT_FAMILY = new StringOrIdentStyleableFigureKey("fontFamily", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), "Arial");
    public static CssSizeStyleableFigureKey FONT_SIZE = new CssSizeStyleableFigureKey("fontSize", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), new CssSize(12.0,null));
    public static EnumStyleableFigureKey<FontPosture> FONT_STYLE = new EnumStyleableFigureKey<>("fontStyle", FontPosture.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), false, FontPosture.REGULAR);
    public static EnumStyleableFigureKey<FontWeight> FONT_WEIGHT = new EnumStyleableFigureKey<>("fontWeight", FontWeight.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), false, FontWeight.NORMAL);
    public static FontStyleableMapAccessor FONT = new FontStyleableMapAccessor("font", FONT_FAMILY, FONT_WEIGHT, FONT_STYLE, FONT_SIZE);
    /**
     * The line spacing. Default value: {@code 0.0}
     */
    public static DoubleStyleableFigureKey LINE_SPACING = new DoubleStyleableFigureKey("lineSpacing", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    /**
     * Whether to strike through the text. Default value: {@code false}
     */
    public static BooleanStyleableFigureKey STRIKETHROUGH = new BooleanStyleableFigureKey("strikethrough", DirtyMask.of(DirtyBits.NODE), false);
    /**
     * The text alignment. Default value: {@code left}
     */
    public static EnumStyleableFigureKey<TextAlignment> TEXT_ALIGNMENT = new EnumStyleableFigureKey<>("textAlignment", TextAlignment.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), false,TextAlignment.LEFT);
       /**
     * The vertical position of the text. Default value: {@code baseline}
     */
    public static EnumStyleableFigureKey<VPos> TEXT_VPOS = new EnumStyleableFigureKey<>("textVPos", VPos.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), false,VPos.BASELINE);
    /**
     * Whether to underline the text. Default value: {@code false}
     */
    public static BooleanStyleableFigureKey UNDERLINE = new BooleanStyleableFigureKey("underline", DirtyMask.of(DirtyBits.NODE), false);
    /**
     * Text wrapping width. Default value: {@code 0.0} (no wrapping).
     */
    public static DoubleStyleableFigureKey WRAPPING_WIDTH = new DoubleStyleableFigureKey("wrappingWidth", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);

    /**
     * Updates a text node with fontable properties.
     * @param ctx RenderContext, can be null
     * @param text a text node
     */
    default void applyFontableFigureProperties(@Nullable RenderContext ctx, @Nonnull Text text) {
        Font font = getStyledNonnull(FONT).getFont();
        if (!text.getFont().equals(font)) {
            text.setFont(font);
        }
        double d = getStyledNonnull(LINE_SPACING);
        if (text.getLineSpacing() != d) {
            text.setLineSpacing(d);
        }
        d = getStyledNonnull(WRAPPING_WIDTH);
        if (text.getWrappingWidth() != d) {
            text.setWrappingWidth(d);
        }
        TextAlignment ta = getStyledNonnull(TEXT_ALIGNMENT);
        if (text.getTextAlignment() != ta) {
            text.setTextAlignment(ta);
        }
        boolean b = getStyledNonnull(UNDERLINE);
        if (text.isUnderline() != b) {
            text.setUnderline(b);
        }
        b = getStyledNonnull(STRIKETHROUGH);
        if (text.isStrikethrough() != b) {
            text.setStrikethrough(b);
        }
        VPos vp = getStyledNonnull(TEXT_VPOS);
        if (text.getTextOrigin() != vp) {
            text.setTextOrigin(vp);
        }

        final FontSmoothingType fst = FontSmoothingType.GRAY;
        if (text.getFontSmoothingType() != fst) {
            text.setFontSmoothingType(fst);
        }

    }

    /**
     * Updates a Laeled node with fontable properties.
     *  @param ctx context
     * @param figure
     * @param text a text node
     */
    default void applyFontableFigureProperties(RenderContext ctx, @Nonnull Labeled text) {
        Font font =getStyledNonnull(FONT).getFont();
        if (!text.getFont().equals(font)) {
            text.setFont(font);
        }
        double d = getStyledNonnull(LINE_SPACING);
        if (text.getLineSpacing() == d) {
            text.setLineSpacing(d);
        }
        TextAlignment ta = getStyledNonnull(TEXT_ALIGNMENT);
        if (text.getTextAlignment() == ta) {
            text.setTextAlignment(ta);
        }
        boolean b = getStyledNonnull(UNDERLINE);
        if (text.isUnderline() == b) {
            text.setUnderline(b);
        }
    }
}
