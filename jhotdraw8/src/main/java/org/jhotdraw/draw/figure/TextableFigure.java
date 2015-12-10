/*
 * @(#)TextableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import javafx.geometry.VPos;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jhotdraw.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.EnumStyleableFigureKey;
import org.jhotdraw.draw.key.FontStyleableMapAccessor;
import org.jhotdraw.draw.key.StringOrIdentStyleableFigureKey;
import org.jhotdraw.draw.key.StringStyleableFigureKey;

/**
 * TextableFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface TextableFigure extends Figure {

    // text properties
    /**
     * Defines the font used. Default value: {@code new Font("Arial",12)}
     */
    public static StringOrIdentStyleableFigureKey FONT_FAMILY = new StringOrIdentStyleableFigureKey("fontFamily", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), "Arial");
    public static DoubleStyleableFigureKey FONT_SIZE = new DoubleStyleableFigureKey("fontSize", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 12.0);
    public static EnumStyleableFigureKey<FontWeight> FONT_WEIGHT = new EnumStyleableFigureKey<FontWeight>("fontWeight", FontWeight.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), FontWeight.NORMAL);
    public static EnumStyleableFigureKey<FontPosture> FONT_STYLE = new EnumStyleableFigureKey<FontPosture>("fontStyle", FontPosture.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), FontPosture.REGULAR);
    public static FontStyleableMapAccessor FONT = new FontStyleableMapAccessor("font", FONT_FAMILY, FONT_WEIGHT, FONT_STYLE, FONT_SIZE);
    /**
     * The line spacing. Default value: {@code 0.0}
     */
    public static DoubleStyleableFigureKey LINE_SPACING = new DoubleStyleableFigureKey("lineSpacing", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    /**
     * Whether to strike through the text. Default value: {@code false}
     */
    public static BooleanStyleableFigureKey STRIKETHROUGH = new BooleanStyleableFigureKey("strikethrough", DirtyMask.of(DirtyBits.NODE), false);
    /**
     * The text alignment. Default value: {@code left}
     */
    public static EnumStyleableFigureKey<TextAlignment> TEXT_ALIGNMENT = new EnumStyleableFigureKey<>("textAlignment", TextAlignment.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT), TextAlignment.LEFT);
    /**
     * The text origin. Default value: {@code baseline}
     */
    public static EnumStyleableFigureKey<VPos> TEXT_ORIGIN = new EnumStyleableFigureKey<>("textOrigin", VPos.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT), VPos.BASELINE);
    /**
     * Whether to underline the text. Default value: {@code false}
     */
    public static BooleanStyleableFigureKey UNDERLINE = new BooleanStyleableFigureKey("underline", DirtyMask.of(DirtyBits.NODE), false);
    /**
     * Text wrapping width. Default value: {@code 0.0} (no wrapping).
     */
    public static DoubleStyleableFigureKey WRAPPING_WIDTH = new DoubleStyleableFigureKey("wrappingWidth", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);

    /**
     * Updates a text node with text properties except {@code TEXT}.
     *
     * @param text a text node
     */
    default void applyTextableFigureProperties(Text text) {
        Font font = getStyled(FONT).getFont();
        if (!text.getFont().equals(font)) {
            text.setFont(font);
        }
        double d = getStyled(LINE_SPACING);
        if (text.getLineSpacing() != d) {
            text.setLineSpacing(d);
        }
        d = getStyled(WRAPPING_WIDTH);
        if (text.getWrappingWidth() != d) {
            text.setWrappingWidth(d);
        }
        TextAlignment ta = getStyled(TEXT_ALIGNMENT);
        if (text.getTextAlignment() != ta) {
            text.setTextAlignment(ta);
        }
        boolean b = getStyled(UNDERLINE);
        if (text.isUnderline() != b) {
            text.setUnderline(b);
        }
        b = getStyled(STRIKETHROUGH);
        if (text.isStrikethrough() != b) {
            text.setStrikethrough(b);
        }
        VPos vp = getStyled(TEXT_ORIGIN);
        if (text.getTextOrigin() != vp) {
            text.setTextOrigin(vp);
        }
    }

    /**
     * Updates a text node with text properties except {@code TEXT}.
     *
     * @param text a text node
     */
    default void applyTextableFigureProperties(Labeled text) {
        Font font = getStyled(FONT).getFont();
        if (!text.getFont().equals(font)) {
            text.setFont(font);
        }
        double d = getStyled(LINE_SPACING);
        if (text.getLineSpacing() == d) {
            text.setLineSpacing(d);
        }
        TextAlignment ta = getStyled(TEXT_ALIGNMENT);
        if (text.getTextAlignment() == ta) {
            text.setTextAlignment(ta);
        }
        boolean b = getStyled(UNDERLINE);
        if (text.isUnderline() == b) {
            text.setUnderline(b);
        }
    }
}
