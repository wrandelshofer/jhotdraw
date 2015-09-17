/*
 * @(#)TextHolderFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Transform;

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
    public static FigureKey<Font> FONT = new FigureKey<>("font", Font.class, new Font("System", 12.0), DirtyBits.NODE, DirtyBits.GEOMETRY, DirtyBits.LAYOUT_BOUNDS, DirtyBits.VISUAL_BOUNDS);
    /**
     * The smoothing type. Default value: {@code GRAY}.
     */
    public static FigureKey<FontSmoothingType> FONT_SMOOTHING_TYPE = new FigureKey<>("fontSmoothingType", FontSmoothingType.class, FontSmoothingType.GRAY, DirtyBits.NODE);
    /**
     * The line spacing. Default value: {@code 12.0}
     */
    public static FigureKey<Double> LINE_SPACING = new FigureKey<>("lineSpacing", Double.class, 12.0, DirtyBits.NODE, DirtyBits.GEOMETRY, DirtyBits.LAYOUT_BOUNDS, DirtyBits.VISUAL_BOUNDS);
    /**
     * Whether to strike through the text. Default value: {@code false}
     */
    public static FigureKey<Boolean> STRIKETHROUGH = new FigureKey<>("strikethrough", Boolean.class, false, DirtyBits.NODE);
    /**
     * The text alignment. Default value: {@code left}
     */
    public static FigureKey<TextAlignment> TEXT_ALIGNMENT = new FigureKey<>("textAlignment", TextAlignment.class, TextAlignment.LEFT, DirtyBits.NODE, DirtyBits.GEOMETRY, DirtyBits.VISUAL_BOUNDS);
    /**
     * The text origin. Default value: {@code baseline}
     */
    public static FigureKey<VPos> TEXT_ORIGIN = new FigureKey<>("textOrigin", VPos.class, VPos.BASELINE, DirtyBits.NODE, DirtyBits.GEOMETRY, DirtyBits.VISUAL_BOUNDS);
    /**
     * Whether to underline the text. Default value: {@code false}
     */
    public static FigureKey<Boolean> UNDERLINE = new FigureKey<>("underline", Boolean.class, false, DirtyBits.NODE);
    /**
     * Text wrapping width. Default value: {@code 0.0} (no wrapping).
     */
    public static FigureKey<Double> WRAPPING_WIDTH = new FigureKey<>("wrappingWidth", Double.class, 0.0, DirtyBits.NODE, DirtyBits.GEOMETRY, DirtyBits.LAYOUT_BOUNDS, DirtyBits.VISUAL_BOUNDS);
    // shape properties
    /**
     * Defines the paint used for filling the interior of the text. Default
     * value: {@code null}.
     */
    public static FigureKey<Paint> TEXT_FILL = new FigureKey<>("textFill", Paint.class, Color.BLACK, DirtyBits.NODE);
    /**
     * Defines whether antia liasing hints are used. Default value: {@code true}.
     * Note: This should be off for printing!
     */
    public static FigureKey<Boolean> TEXT_SMOOTH = new FigureKey<>("textSmooth", Boolean.class, true, DirtyBits.NODE);
    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     */
    public static FigureKey<Double> TEXT_STROKE_DASH_OFFSET = new FigureKey<>("textStrokeDashOffset", Double.class, 0.0, DirtyBits.NODE);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     */
    public static FigureKey<StrokeLineCap> TEXT_STROKE_LINE_CAP = new FigureKey<>("textStrokeLineCap", StrokeLineCap.class, StrokeLineCap.SQUARE, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     */
    public static FigureKey<StrokeLineJoin> TEXT_STROKE_LINE_JOIN = new FigureKey<>("textStrokeLineJoin", StrokeLineJoin.class, StrokeLineJoin.MITER, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style. Default
     * value: {@code 10.0}.
     */
    public static FigureKey<Double> TEXT_STROKE_MITER_LIMIT = new FigureKey<>("textStrokeMiterLimit", Double.class, 10.0, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the paint used for filling the outline of the text. Default
     * value: {@code null}.
     */
    public static FigureKey<Paint> TEXT_STROKE = new FigureKey<>("textStroke", Paint.class, null, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the stroke type used for drawing the outline of the text. Default
     * value: {@code StrokeType.CENTERED}.
     */
    public static FigureKey<StrokeType> TEXT_STROKE_TYPE = new FigureKey<>("textStrokeType", StrokeType.class, StrokeType.CENTERED, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the width of the outline of the text. Default value: {@code 1.0}.
     */
    public static FigureKey<Double> TEXT_STROKE_WIDTH = new FigureKey<>("textStrokeWidth", Double.class, 1.0, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the dash array used for the text. Default value:
     * {@code empty array}.
     */
    public static FigureKey<double[]> TEXT_STROKE_DASH_ARRAY = new FigureKey<>("textStrokeDashArray", double[].class, new double[0], DirtyBits.NODE);

    public final static FigureKey<String> TEXT = new FigureKey<>("text", String.class, "", DirtyBits.NODE, DirtyBits.GEOMETRY, DirtyBits.LAYOUT_BOUNDS, DirtyBits.VISUAL_BOUNDS);

    /**
     * Updates a text node.
     *
     * @param text a text node
     */
    default void updateTextProperties(Text text) {
        text.setFont(get(FONT));
        text.setFontSmoothingType(get(FONT_SMOOTHING_TYPE));
        text.setLineSpacing(get(LINE_SPACING));
        text.setStrikethrough(get(STRIKETHROUGH));
        text.setTextAlignment(get(TEXT_ALIGNMENT));
        text.setTextOrigin(get(TEXT_ORIGIN));
        text.setUnderline(get(UNDERLINE));
        text.setWrappingWidth(get(WRAPPING_WIDTH));

        text.setFill(get(TEXT_FILL));
        text.setSmooth(get(TEXT_SMOOTH));
        text.setStrokeDashOffset(get(TEXT_STROKE_DASH_OFFSET));
        text.setStrokeLineCap(get(TEXT_STROKE_LINE_CAP));
        text.setStrokeLineJoin(get(TEXT_STROKE_LINE_JOIN));
        text.setStrokeMiterLimit(get(TEXT_STROKE_MITER_LIMIT));
        text.setStroke(get(TEXT_STROKE));
        text.setStrokeType(get(TEXT_STROKE_TYPE));
        text.setStrokeWidth(get(TEXT_STROKE_WIDTH));
        text.getStrokeDashArray().clear();
        for (double dash : get(TEXT_STROKE_DASH_ARRAY)) {
            text.getStrokeDashArray().add(dash);
        }
    }
}
