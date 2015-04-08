/* @(#)TextHolderFigure.java
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
import org.jhotdraw.collection.Key;

/**
 * TextHolderFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface TextHolderFigure extends Figure {
    // text properties
    /** Defines the font used. 
     Default value: {@code new Font("System",12)}
     */
    public static Key<Font> FONT = new Key<>("font", Font.class, new Font("System", 12.0));
    /** The baseline offset.
     Default value: {@code GRAY}
     */
    public static Key<FontSmoothingType> FONT_SMOOTHING_TYPE = new Key<>("fontSmoothingType", FontSmoothingType.class, FontSmoothingType.GRAY);
    /** The line spacing.
     Default value: {@code 12.0}
     */
    public static Key<Double> LINE_SPACING = new Key<>("lineSpacing", Double.class, 12.0);
    /** Whether to strike through the text.
     Default value: {@code false}
     */
    public static Key<Boolean> STRIKETHROUGH = new Key<>("strikethrough", Boolean.class, false);
    /** The text alignment.
     Default value: {@code left}
     */
    public static Key<TextAlignment> TEXT_ALIGNMENT = new Key<>("textAlignment", TextAlignment.class, TextAlignment.LEFT);
    /** The text alignment.
     Default value: {@code left}
     */
    public static Key<VPos> TEXT_ORIGIN = new Key<>("textOrigin", VPos.class, VPos.BASELINE);
    /** Whether to underline the text.
     Default value: {@code false}
     */
    public static Key<Boolean> UNDERLINE = new Key<>("underline", Boolean.class, false);
    /** Text wrapping width.
     Default value: {@code 0.0} (no wrapping).
     */
    public static Key<Double> WRAPPING_WIDTH = new Key<>("wrappingWidth", Double.class, 0.0);
    // shape properties
    /**
     * Defines the paint used for filling the interior of the text.
     * Default value: {@code null}.
     */
    public static Key<Paint> TEXT_FILL = new Key<>("textFill", Paint.class, Color.BLACK);
    /**
     * Defines whether antialiasing hints are used.
     * Default value: {@code true}.
     */
    public static Key<Boolean> TEXT_SMOOTH = new Key<>("textSmooth", Boolean.class, true);
    /**
     * Defines the distance in user coordinates for the dashing pattern.
     * Default value: {@code 0}.
     */
    public static Key<Double> TEXT_STROKE_DASH_OFFSET = new Key<>("textStrokeDashOffset", Double.class, 0.0);
    /**
     * Defines the end cap style.
     * Default value: {@code SQUARE}.
     */
    public static Key<StrokeLineCap> TEXT_STROKE_LINE_CAP = new Key<>("textStrokeLineCap", StrokeLineCap.class, StrokeLineCap.SQUARE);
    /**
     * Defines the style applied where path segments meet.
     * Default value: {@code MITER}.
     */
    public static Key<StrokeLineJoin> TEXT_STROKE_LINE_JOIN = new Key<>("textStrokeLineJoin", StrokeLineJoin.class, StrokeLineJoin.MITER);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style.
     * Default value: {@code 10.0}.
     */
    public static Key<Double> TEXT_STROKE_MITER_LIMIT = new Key<>("textStrokeMiterLimit", Double.class, 10.0);
    /**
     * Defines the paint used for filling the outline of the text.
     * Default value: {@code null}.
     */
    public static Key<Paint> TEXT_STROKE = new Key<>("textStroke", Paint.class, null);
    /**
     * Defines the stroke type used for drawing the outline of the text.
     * Default value: {@code StrokeType.CENTERED}.
     */
    public static Key<StrokeType> TEXT_STROKE_TYPE = new Key<>("textStrokeType", StrokeType.class, StrokeType.CENTERED);
    /**
     * Defines the width of the outline of the text.
     * Default value: {@code 1.0}.
     */
    public static Key<Double> TEXT_STROKE_WIDTH = new Key<>("textStrokeWidth", Double.class, 1.0);
    /**
     * Defines the dash array used for the text.
     * Default value: {@code empty array}.
     */
    public static Key<double[]> TEXT_STROKE_DASH_ARRAY = new Key<>("textStrokeDashArray", double[].class, new double[0]);

    public final static Key<String> TEXT = new Key<>("text", String.class, "");

 /** Updates a text node. */
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
    public static HashMap<String, Key<?>> getFigureKeys() {
        try {
            HashMap<String, Key<?>> keys = Figure.getFigureKeys();
            for (Field f : TextHolderFigure.class.getDeclaredFields()) {
                if (Key.class.isAssignableFrom(f.getType())) {
                    Key<?> value = (Key<?>) f.get(null);
                    keys.put(value.getName(), value);
                }
            }
            return keys;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new InternalError("class can not read its own keys");
        }
    }
}
