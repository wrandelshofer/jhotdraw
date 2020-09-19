/*
 * @(#)PathBuilder.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;

/**
 * PathBuilder.
 *
 * @author Werner Randelshofer
 */
public interface PathBuilder {

    /**
     * Adds an elliptical arc to the path which goes to the specified end point
     * using the specified parameters.
     * <p>
     * The elliptical arc is defined by two radii, an angle from the x-axis, a
     * flag to choose the large arc or not, a flag to indicate if we increase or
     * decrease the angles and the final point of the arc.
     * <p>
     * As specified in
     * <a href=http://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands>
     * SVG elliptical arc commands</a>.
     * <p>
     * The default implementation of this method has been derived from Apache Batik
     * class org.apache.batik.ext.awt.geom.ExtendedGeneralPath#computArc.
     * The default implementation decomposes the arc into cubic curve
     * segments and invokes corresponding {@code curveTo} methods.
     *
     * @param radiusX       the x radius of the ellipse
     * @param radiusY       the y radius of the ellipse
     * @param xAxisRotation the angle from the x-axis of the current coordinate
     *                      system to the x-axis of the ellipse in degrees.
     * @param largeArcFlag  the large arc flag. If true the arc spanning more
     *                      than or equal to 180 degrees is chosen, otherwise the
     *                      arc spanning less than 180 degrees is chosen
     * @param sweepFlag     the sweep flag. If true the line joining center to arc
     *                      sweeps through decreasing angles
     *                      otherwise it sweeps through increasing
     *                      angles
     * @param x             the x coordinate of the end point
     * @param y             the y coordinate of the end point
     */
    default void arcTo(double radiusX, double radiusY,
                       double xAxisRotation,
                       double x, double y,
                       boolean largeArcFlag, boolean sweepFlag
    ) {
        ArcToCubicBezier.arcTo(getLastX(), getLastY(),
                radiusX, radiusY, xAxisRotation, x, y, largeArcFlag, sweepFlag,
                this::lineTo,
                this::curveTo);
    }


    /**
     * Closes the path by adding a straight line back to the last
     * {@link #moveTo} point.
     * <p>
     * If the path is already closed, then this method has no effect.
     */
    void closePath();

    /**
     * Adds a cubic curve going to the specified end point coordinate using the
     * specified control points.
     *
     * @param x1 the x coordinate of control point 1
     * @param y1 the y coordinate of control point 1
     * @param x2 the x coordinate of control point 2
     * @param y2 the y coordinate of control point 2
     * @param x  the x coordinate of the end point
     * @param y  the y coordinate of the end point
     */
    void curveTo(double x1, double y1, double x2, double y2, double x, double y);

    /**
     * Adds a cubic curve going to the specified point coordinate using the
     * specified control points.
     * <p>
     * The default implementation of this method calls {@link #curveTo(double, double, double, double, double, double)
     * }.
     *
     * @param c1 the control point 1
     * @param c2 the control point 2
     * @param p  the end point
     */
    default void curveTo(@NonNull Point2D c1, @NonNull Point2D c2, @NonNull Point2D p) {
        curveTo(c1.getX(), c1.getY(), c2.getX(), c2.getY(), p.getX(), p.getY());
    }

    /**
     * Getter.
     *
     * @return x coordinate of the last control point.
     */
    double getLastCX();

    /**
     * Getter.
     *
     * @return y coordinate of the last control point.
     */
    double getLastCY();

    /**
     * Getter.
     *
     * @return the last end point.
     */
    @NonNull
    default Point2D.Double getLastPoint() {
        return new Point2D.Double(getLastX(), getLastY());
    }

    /**
     * Getter.
     *
     * @return x coordinate of the last end point.
     */
    double getLastX();

    /**
     * Getter.
     *
     * @return y coordinate of the last control point.
     */
    double getLastY();

    /**
     * Adds a straight line to the path going to the specified end point.
     * <p>
     * The default implementation of this method calls
     * {@link #lineTo(double, double)}.
     *
     * @param p the end point
     */
    default void lineTo(@NonNull Point2D p) {
        lineTo(p.getX(), p.getY());
    }

    /**
     * Adds a straight line to the path going to the specified end point.
     *
     * @param x the x coordinate of the end point
     * @param y the y coordinate of the end point
     */
    void lineTo(double x, double y);

    /**
     * Adds a point to the path by moving to the specified end point.
     * <p>
     * The default implementation of this method calls
     * {@link #moveTo(double, double)}.
     *
     * @param p the end point
     */
    default void moveTo(@NonNull Point2D p) {
        moveTo(p.getX(), p.getY());
    }

    /**
     * Adds a point to the path by moving to the specified point coordinates.
     *
     * @param x the x coordinate of the end point
     * @param y the y coordinate of the end point
     */
    void moveTo(double x, double y);

    /**
     * Performs path processing after all the path segments have been added to
     * the builder.
     */
    default void pathDone() {

    }

    /**
     * Adds a quadratic curve going to the specified point coordinate using the
     * specified control point.
     * <p>
     * The default implementation of this method calls {@link #quadTo(double, double, double, double)
     * }.
     *
     * @param x1 the x coordinate of the control point
     * @param y1 the y coordinate of the control point
     * @param x  the x coordinate of the end point
     * @param y  the y coordinate of the end point
     */
    void quadTo(double x1, double y1, double x, double y);

    /**
     * Adds a quadratic curve going to the specified point coordinate using the
     * specified control point.
     * <p>
     * The default implementation of this method calls {@link #quadTo(double, double, double, double)
     * }.
     *
     * @param c the control point
     * @param p the end point
     */
    default void quadTo(@NonNull Point2D c, @NonNull Point2D p) {
        quadTo(c.getX(), c.getY(), p.getX(), p.getY());
    }

    /**
     * Adds a smooth cubic curve going to the specified end point coordinate
     * using the specified control point.
     * <p>
     * The coordinates of control point 1 is the coordinate of the last control
     * point mirrored along the last end point.
     * <p>
     * The default implementation of this method calls {@link #smoothCurveTo(double, double, double, double)
     * }.
     *
     * @param c2 the control point 2
     * @param p  the end point
     */
    default void smoothCurveTo(@NonNull Point2D c2, @NonNull Point2D p) {
        smoothCurveTo(c2.getX(), c2.getY(), p.getX(), p.getY());
    }

    /**
     * Adds a smooth cubic curve going to the specified end point coordinate
     * using the specified control point.
     * <p>
     * The coordinates of control point 1 is the coordinate of the last control
     * point mirrored along the last end point.
     *
     * @param x2 the x coordinate of control point 2
     * @param y2 the y coordinate of control point 2
     * @param x  the x coordinate of the end point
     * @param y  the y coordinate of the end point
     */
    default void smoothCurveTo(double x2, double y2, double x, double y) {
        curveTo(getLastX() - getLastCX() + getLastX(), getLastY() - getLastCY() + getLastY(), x2, y2, x, y);
    }

    /**
     * Adds a smooth quadratic curve going to the specified end point
     * coordinate.
     * <p>
     * The coordinates of the control point is the coordinate of the last
     * control point mirrored along the last end point.
     * <p>
     * The default implementation of this method calls {@link #smoothQuadTo(double, double)
     * }.
     *
     * @param p the end point
     */
    default void smoothQuadTo(@NonNull Point2D p) {
        smoothQuadTo(p.getX(), p.getY());
    }

    /**
     * Adds a smooth quadratic curve going to the specified end point
     * coordinate.
     * <p>
     * The coordinates of the control point is the coordinate of the last
     * control point mirrored along the last end point.
     *
     * @param x the x coordinate of the end point
     * @param y the y coordinate of the end point
     */
    default void smoothQuadTo(double x, double y) {
        quadTo(getLastX() - getLastCX() + getLastX(), getLastY() - getLastCY() + getLastY(), x, y);
    }

    boolean needsMoveTo();
}
