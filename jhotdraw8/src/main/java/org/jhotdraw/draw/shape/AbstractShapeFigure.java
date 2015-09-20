/* @(#)AbstractShapeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw.draw.AbstractLeafFigure;
import org.jhotdraw.draw.DirtyBits;
import org.jhotdraw.draw.DirtyMask;
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
    public static FigureKey<Paint> FILL_PAINT = new FigureKey<>("fillPaint", Paint.class, DirtyMask.of(DirtyBits.NODE), Color.WHITE);
    /**
     * Defines whether anti aliasing hints are used. Default value:
     * {@code true}.
     */
    public static FigureKey<Boolean> SMOOTH = new FigureKey<>("smooth", Boolean.class, DirtyMask.of(DirtyBits.NODE), true);
    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     */
    public static FigureKey<Double> STROKE_DASH_OFFSET = new FigureKey<>("strokeDashOffset", Double.class, DirtyMask.of(DirtyBits.NODE), 0.0);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     */
    public static FigureKey<StrokeLineCap> STROKE_LINE_CAP = new FigureKey<>("strokeLineCap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE), StrokeLineCap.SQUARE);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     */
    public static FigureKey<StrokeLineJoin> STROKE_LINE_JOIN = new FigureKey<>("strokeLineJoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), StrokeLineJoin.MITER);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style. Default
     * value: {@code 10.0}.
     */
    public static FigureKey<Double> STROKE_MITER_LIMIT = new FigureKey<>("strokeMiterLimit", Double.class, DirtyMask.of(DirtyBits.NODE), 10.0);
    /**
     * Defines the paint used for filling the outline of the figure. Default
     * value: {@code Color.BLACK}.
     */
    public static FigureKey<Paint> STROKE_PAINT = new FigureKey<>("strokePaint", Paint.class, DirtyMask.of(DirtyBits.NODE), Color.BLACK);
    /**
     * Defines the stroke type used for drawing outline of the figure. Default
     * value: {@code StrokeType.CENTERED}.
     */
    public static FigureKey<StrokeType> STROKE_TYPE = new FigureKey<>("strokeType", StrokeType.class, DirtyMask.of(DirtyBits.NODE), StrokeType.CENTERED);
    /**
     * Defines the width of the outline of the figure. Default value:
     * {@code 1.0}.
     */
    public static FigureKey<Double> STROKE_WIDTH = new FigureKey<>("strokeWidth", Double.class, DirtyMask.of(DirtyBits.NODE), 1.0);
    /**
     * Defines the dash array used. Default value: {@code empty array}.
     */
    public static FigureKey<double[]> STROKE_DASH_ARRAY = new FigureKey<>("strokeDashArray", double[].class, DirtyMask.of(DirtyBits.NODE), new double[0]);

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    protected void updateShapeProperties(Shape shape) {
        shape.setFill(get(FILL_PAINT));
        shape.setSmooth(get(SMOOTH));
        shape.setStrokeDashOffset(get(STROKE_DASH_OFFSET));
        shape.setStrokeLineCap(get(STROKE_LINE_CAP));
        shape.setStrokeLineJoin(get(STROKE_LINE_JOIN));
        shape.setStrokeMiterLimit(get(STROKE_MITER_LIMIT));
        shape.setStroke(get(STROKE_PAINT));
        shape.setStrokeType(get(STROKE_TYPE));
        shape.setStrokeWidth(get(STROKE_WIDTH));
        shape.getStrokeDashArray().clear();
        for (double dash : get(STROKE_DASH_ARRAY)) {
            shape.getStrokeDashArray().add(dash);
        }
    }

    @Override
    public void layout() {//empty

    }

    @Override
    public boolean isLayoutable() {
        return false;
    }

}
