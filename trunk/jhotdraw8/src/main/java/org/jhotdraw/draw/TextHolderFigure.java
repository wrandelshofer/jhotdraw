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
    // shape properties
    /**
     * Defines the paint used for filling the interior of the text. Default
     * value: {@code null}.
     */
    public static PaintStyleableFigureKey TEXT_FILL = new PaintStyleableFigureKey("textFill", Color.BLACK);
    /**
     * Defines whether anti-aliasing hints are used. Default value:
     * {@code true}. Note: This should be off for printing!
     */
    public static BooleanStyleableFigureKey TEXT_SMOOTH = new BooleanStyleableFigureKey("textSmooth", DirtyMask.of(DirtyBits.NODE), true);
    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TEXT_STROKE_DASH_OFFSET = new DoubleStyleableFigureKey("textStrokeDashOffset", DirtyMask.of(DirtyBits.NODE), 0.0);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     */
    public static EnumStyleableFigureKey<StrokeLineCap> TEXT_STROKE_LINE_CAP = new EnumStyleableFigureKey("textStrokeLineCap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE), StrokeLineCap.SQUARE);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     */
    public static EnumStyleableFigureKey<StrokeLineJoin> TEXT_STROKE_LINE_JOIN = new EnumStyleableFigureKey<>("textStrokeLineJoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), StrokeLineJoin.MITER);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style. Default
     * value: {@code 10.0}.
     */
    public static DoubleStyleableFigureKey TEXT_STROKE_MITER_LIMIT = new DoubleStyleableFigureKey("textStrokeMiterLimit", DirtyMask.of(DirtyBits.NODE), 10.0);
    /**
     * Defines the paint used for filling the outline of the text. Default
     * value: {@code null}.
     */
    public static PaintStyleableFigureKey TEXT_STROKE = new PaintStyleableFigureKey("textStroke", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT), null);
    /**
     * Defines the stroke type used for drawing the outline of the text. Default
     * value: {@code StrokeType.CENTERED}.
     */
    public static EnumStyleableFigureKey<StrokeType> TEXT_STROKE_TYPE = new EnumStyleableFigureKey<>("textStrokeType", StrokeType.class, DirtyMask.of(DirtyBits.NODE), StrokeType.CENTERED);
    /**
     * Defines the width of the outline of the text. Default value: {@code 1.0}.
     */
    public static DoubleStyleableFigureKey TEXT_STROKE_WIDTH = new DoubleStyleableFigureKey("textStrokeWidth", DirtyMask.of(DirtyBits.NODE), 1.0);
    /**
     * Defines the dash array used for the text. Default value:
     * {@code empty list}.
     */
    public static DoubleListStyleableFigureKey TEXT_STROKE_DASH_ARRAY = new DoubleListStyleableFigureKey("textStrokeDashArray", DirtyMask.of(DirtyBits.NODE), Collections.emptyList());

    public final static SimpleFigureKey<String> TEXT = new SimpleFigureKey<>("text", String.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), "");

    /**
     * Updates a text node.
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

        text.setFill(getStyled(TEXT_FILL));
        text.setSmooth(getStyled(TEXT_SMOOTH));
        text.setStrokeDashOffset(getStyled(TEXT_STROKE_DASH_OFFSET));
        text.setStrokeLineCap(getStyled(TEXT_STROKE_LINE_CAP));
        text.setStrokeLineJoin(getStyled(TEXT_STROKE_LINE_JOIN));
        text.setStrokeMiterLimit(getStyled(TEXT_STROKE_MITER_LIMIT));
        text.setStroke(getStyled(TEXT_STROKE));
        text.setStrokeType(getStyled(TEXT_STROKE_TYPE));
        text.setStrokeWidth(getStyled(TEXT_STROKE_WIDTH));
        text.getStrokeDashArray().setAll(getStyled(TEXT_STROKE_DASH_ARRAY));
    }
}
