/* @(#)AbstractShapeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.AbstractFigure;
import org.jhotdraw.draw.AbstractLeafFigure;
import org.jhotdraw.draw.Figure;

/**
 * Base class for all figures which render a {@code javafx.scene.shape.Shape}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractShapeFigure extends AbstractLeafFigure {
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
    public static Key<StrokeType> STROKE_TYPE = new Key<>("strokeType", StrokeType.class, StrokeType.CENTERED);
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
    /** Updates a shape node. */
    protected void updateShapeProperties(Shape shape) {
        shape.setFill(get(FILL));
        shape.setSmooth(get(SMOOTH));
        shape.setStrokeDashOffset(get(STROKE_DASH_OFFSET));
        shape.setStrokeLineCap(get(STROKE_LINE_CAP));
        shape.setStrokeLineJoin(get(STROKE_LINE_JOIN));
        shape.setStrokeMiterLimit(get(STROKE_MITER_LIMIT));
        shape.setStroke(get(STROKE));
        shape.setStrokeType(get(STROKE_TYPE));
        shape.setStrokeWidth(get(STROKE_WIDTH));
        shape.getStrokeDashArray().clear();
        for (double dash : get(STROKE_DASH_ARRAY)) {
            shape.getStrokeDashArray().add(dash);
        }
    }

    public static HashMap<String, Key<?>> getFigureKeys() {
        try {
            HashMap<String, Key<?>> keys = Figure.getFigureKeys();
            for (Field f : AbstractShapeFigure.class.getDeclaredFields()) {
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
