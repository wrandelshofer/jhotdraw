/*
 * @(#)TextHolderFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.SimpleFigureKey;
import java.util.Collections;
import java.util.List;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jhotdraw.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw.draw.key.DoubleListStyleableFigureKey;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.EnumStyleableFigureKey;
import org.jhotdraw.draw.key.FontStyleableFigureKey;
import org.jhotdraw.draw.key.PaintStyleableFigureKey;

/**
 * TextHolderFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface TextHolderFigure extends Figure {

    // text properties
    /**
     * Defines the font used. Default value: {@code new Font("System",12)}
     */
    public static FontStyleableFigureKey FONT = new FontStyleableFigureKey("font", new Font("System", 12.0));
    /**
     * The smoothing type. Default value: {@code GRAY}.
     */
    public static EnumStyleableFigureKey<FontSmoothingType> FONT_SMOOTHING_TYPE = new EnumStyleableFigureKey<>("fontSmoothingType", FontSmoothingType.class, DirtyMask.of(DirtyBits.NODE), FontSmoothingType.GRAY);
    /**
     * The line spacing. Default value: {@code 12.0}
     */
    public static DoubleStyleableFigureKey LINE_SPACING = new DoubleStyleableFigureKey("lineSpacing", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 12.0);
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

    /** The text. Default value: {@code ""}. */
    public final static SimpleFigureKey<String> TEXT = new SimpleFigureKey<>("text", String.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), "");

    /**
     * Updates a text node with text properties except {@code TEXT}.
     *
     * @param text a text node
     */
    default void applyTextProperties(Text text) {
        text.setFont(getStyled(FONT));
        text.setFontSmoothingType(getStyled(FONT_SMOOTHING_TYPE));
        text.setLineSpacing(getStyled(LINE_SPACING));
        text.setStrikethrough(getStyled(STRIKETHROUGH));
        text.setTextAlignment(getStyled(TEXT_ALIGNMENT));
        text.setTextOrigin(getStyled(TEXT_ORIGIN));
        text.setUnderline(getStyled(UNDERLINE));
        text.setWrappingWidth(getStyled(WRAPPING_WIDTH));
    }
}
