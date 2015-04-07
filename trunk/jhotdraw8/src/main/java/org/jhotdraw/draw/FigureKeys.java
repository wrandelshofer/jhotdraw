/* @(#)FigureKeys.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point3D;
import javafx.geometry.VPos;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;
import org.jhotdraw.collection.Key;

/**
 * FigureKeys.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigureKeys {

    /**
     * Specifies a blend mode applied to the figure.
     * The {@code null} value is interpreted as {@code SRC_OVER}.
     * <p>
     * Default value: {@code SRC_OVER}.
     */
    public static Key<BlendMode> BLEND_MODE = new Key<>("blendMode", BlendMode.class, BlendMode.SRC_OVER);
    /**
     * Specifies an effect applied to the figure.
     * The {@code null} means that no effect is applied.
     * <p>
     * Default value: {@code null}.
     */
    public static Key<Effect> EFFECT = new Key<>("effect", Effect.class, null);
    /**
     * Specifies the opacity of the figure.
     * A figure with {@code 0} opacity is completely translucent.
     * A figure with {@code 1} opacity is completely opaque.
     * <p>
     * Values smaller than {@code 0} are treated as {@code 0}. 
     * Values larger than {@code 1} are treated as {@code 1}. 
     * <p>
     * Default value: {@code 1}.
     */
    public static Key<Double> OPACITY = new Key<>("opacity", Double.class, 1.0);
    /**
     * Defines the angle of rotation around the center of the figure in degrees.
     * Default value: {@code 0}.
     */
    public static Key<Double> ROTATE = new Key<>("rotate", Double.class, 0.0);
    /**
     * Defines the rotation axis used.
     * Default value: {@code Rotate.Z_AXIS}.
     */
    public static Key<Point3D> ROTATION_AXIS = new Key<>("rotationAxis", Point3D.class, Rotate.Z_AXIS);
    /**
     * Defines the scale factor by which coordinates are scaled on the x axis
     * about the center of the figure.
     * Default value: {@code 1}.
     */
    public static Key<Double> SCALE_X = new Key<>("scaleX", Double.class, 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the y axis
     * about the center of the figure.
     * Default value: {@code 1}.
     */
    public static Key<Double> SCALE_Y = new Key<>("scaleY", Double.class, 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the z axis
     * about the center of the figure.
     * Default value: {@code 1}.
     */
    public static Key<Double> SCALE_Z = new Key<>("scaleZ", Double.class, 1.0);

    // shape properties
    /**
     * Defines the paint used for filling the interior of the figure.
     * Default value: {@code Color.WHITE}.
     */
    public static Key<Paint> FILL = new Key<>("fill", Paint.class, Color.WHITE);
    /**
     * Defines whether antialiasing hints are used.
     * Default value: {@code true}.
     */
    public static Key<Boolean> SMOOTH = new Key<>("smooth", Boolean.class, true);
    /**
     * Defines the distance in user coordinates for the dashing pattern.
     * Default value: {@code 0}.
     */
    public static Key<Double> STROKE_DASH_OFFSET = new Key<>("strokeDashOffset", Double.class, 0.0);
    /**
     * Defines the end cap style.
     * Default value: {@code SQUARE}.
     */
    public static Key<StrokeLineCap> STROKE_LINE_CAP = new Key<>("strokeLineCap", StrokeLineCap.class, StrokeLineCap.SQUARE);
    /**
     * Defines the style applied where path segments meet.
     * Default value: {@code MITER}.
     */
    public static Key<StrokeLineJoin> STROKE_LINE_JOIN = new Key<>("strokeLineJoin", StrokeLineJoin.class, StrokeLineJoin.MITER);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style.
     * Default value: {@code 10.0}.
     */
    public static Key<Double> STROKE_MITER_LIMIT = new Key<>("strokeMiterLimit", Double.class, 10.0);
    /**
     * Defines the paint used for filling the outline of the figure.
     * Default value: {@code Color.BLACK}.
     */
    public static Key<Paint> STROKE = new Key<>("stroke", Paint.class, Color.BLACK);
    /**
     * Defines the stroke type used for drawing outline of the figure.
     * Default value: {@code StrokeType.CENTERED}.
     */
    public static Key<StrokeType> STROKE_TYPE = new Key<>("strokeType", StrokeType.class, StrokeType.OUTSIDE);
    /**
     * Defines the width of the outline of the figure.
     * Default value: {@code 1.0}.
     */
    public static Key<Double> STROKE_WIDTH = new Key<>("strokeWidth", Double.class, 1.0);
    /**
     * Defines the dash array used.
     * Default value: {@code empty array}.
     */
    public static Key<double[]> STROKE_DASH_ARRAY = new Key<>("strokeDashArray", double[].class, new double[0]);

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

    /** Prevent instantiation */
    private FigureKeys() {
    }

    public static HashMap<String, Key<?>> getFigureKeys() {
        try {
            HashMap<String, Key<?>> keys = new HashMap<>();
            for (Field f : FigureKeys.class.getDeclaredFields()) {
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
