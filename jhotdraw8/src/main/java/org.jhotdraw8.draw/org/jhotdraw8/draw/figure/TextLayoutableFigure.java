/*
 * @(#)TextLayoutableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.VPos;
import javafx.scene.control.Labeled;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableKey;
import org.jhotdraw8.draw.key.EnumStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;

/**
 * A figure which supports font attributes.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface TextLayoutableFigure extends Figure {

    /**
     * The line spacing. Default value: {@code 0.0}
     */
    CssSizeStyleableKey LINE_SPACING = new CssSizeStyleableKey("lineSpacing", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    /**
     * The text alignment. Default value: {@code left}
     */
    EnumStyleableKey<TextAlignment> TEXT_ALIGNMENT = new EnumStyleableKey<>("textAlignment", TextAlignment.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), TextAlignment.LEFT);
    /**
     * The vertical position of the text. Default value: {@code baseline}
     */
    EnumStyleableKey<VPos> TEXT_VPOS = new EnumStyleableKey<>("textVPos", VPos.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), VPos.BASELINE);
    /**
     * Text wrapping width. Default value: {@code 0.0} (no wrapping).
     */
    DoubleStyleableKey WRAPPING_WIDTH = new DoubleStyleableKey("wrappingWidth", 0.0);

    /**
     * Updates a text node with fontable properties.
     *
     * @param ctx  RenderContext, can be null
     * @param text a text node
     */
    default void applyTextLayoutableFigureProperties(@Nullable RenderContext ctx, @Nonnull Text text) {
        UnitConverter units = ctx == null ? DefaultUnitConverter.getInstance() : ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);

        double d = units.convert(getStyledNonnull(LINE_SPACING), UnitConverter.DEFAULT);
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
        VPos vp = getStyledNonnull(TEXT_VPOS);
        if (text.getTextOrigin() != vp) {
            text.setTextOrigin(vp);
        }


        final FontSmoothingType fst = ctx == null || ctx.getNonnull(RenderContext.RENDERING_INTENT) == RenderingIntent.EDITOR
                ? FontSmoothingType.LCD : FontSmoothingType.GRAY;
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
    default void applyTextLayoutableFigureProperties(RenderContext ctx, @Nonnull Labeled text) {
        UnitConverter units = ctx == null ? DefaultUnitConverter.getInstance() : ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        double d = units.convert(getStyledNonnull(LINE_SPACING), UnitConverter.DEFAULT);
        if (text.getLineSpacing() == d) {
            text.setLineSpacing(d);
        }
        TextAlignment ta = getStyledNonnull(TEXT_ALIGNMENT);
        if (text.getTextAlignment() == ta) {
            text.setTextAlignment(ta);
        }
    }
}
