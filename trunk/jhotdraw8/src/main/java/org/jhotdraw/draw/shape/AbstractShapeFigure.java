/*
 * @(#)AbstractShapeFigure.java
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
import org.jhotdraw.draw.AbstractFigure;
import org.jhotdraw.draw.AbstractLeafFigure;
import org.jhotdraw.draw.DirtyBits;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.FigureKey;

/**
 * Base class for all figures which render a {@code javafx.scene.shape.Shape}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractShapeFigure extends AbstractLeafFigure {

    /**
     * Defines the paint used for filling the interior of the figure. Default
     * value: {@code Color.WHITE}.
     */
    public static FigureKey<Paint> FILL = new FigureKey<>("fill", Paint.class, Color.WHITE, DirtyBits.NODE);
    /**
     * Defines whether anti aliasing hints are used. Default value: {@code true}.
     */
    public static FigureKey<Boolean> SMOOTH = new FigureKey<>("smooth", Boolean.class, true, DirtyBits.NODE);
    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     */
    public static FigureKey<Double> STROKE_DASH_OFFSET = new FigureKey<>("strokeDashOffset", Double.class, 0.0, DirtyBits.NODE);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     */
    public static FigureKey<StrokeLineCap> STROKE_LINE_CAP = new FigureKey<>("strokeLineCap", StrokeLineCap.class, StrokeLineCap.SQUARE, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     */
    public static FigureKey<StrokeLineJoin> STROKE_LINE_JOIN = new FigureKey<>("strokeLineJoin", StrokeLineJoin.class, StrokeLineJoin.MITER, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style. Default
     * value: {@code 10.0}.
     */
    public static FigureKey<Double> STROKE_MITER_LIMIT = new FigureKey<>("strokeMiterLimit", Double.class, 10.0, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the paint used for filling the outline of the figure. Default
     * value: {@code Color.BLACK}.
     */
    public static FigureKey<Paint> STROKE = new FigureKey<>("stroke", Paint.class, Color.BLACK, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the stroke type used for drawing outline of the figure. Default
     * value: {@code StrokeType.CENTERED}.
     */
    public static FigureKey<StrokeType> STROKE_TYPE = new FigureKey<>("strokeType", StrokeType.class, StrokeType.CENTERED, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the width of the outline of the figure. Default value:
     * {@code 1.0}.
     */
    public static FigureKey<Double> STROKE_WIDTH = new FigureKey<>("strokeWidth", Double.class, 1.0, DirtyBits.NODE, DirtyBits.VISUAL_BOUNDS);
    /**
     * Defines the dash array used. Default value: {@code empty array}.
     */
    public static FigureKey<double[]> STROKE_DASH_ARRAY = new FigureKey<>("strokeDashArray", double[].class, new double[0], DirtyBits.NODE);

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
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
}
