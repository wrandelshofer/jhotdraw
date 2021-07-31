/*
 * @(#)SvgPath2D.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg;

import org.jhotdraw8.geom.ArcToCubicBezier;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * {@code SvgPath2D} adds an {@code arcTo} method to {@code Path2D.Double}.
 *
 * @author Werner Randelshofer
 */
public class SvgPath2D extends Path2D.Double {

    private static final long serialVersionUID = 1L;

    /**
     * Adds an elliptical arc, defined by two radii, an angle from the x-axis, a
     * flag to choose the large arc or not, a flag to indicate if we increase or
     * decrease the angles and the final point of the arc.
     * <p>
     * As specified in
     * <a href="http://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands">w3.org</a>
     *
     * @param rx            the x radius of the ellipse
     * @param ry            the y radius of the ellipse
     * @param xAxisRotation the angle from the x-axis of the current coordinate
     *                      system to the x-axis of the ellipse in degrees.
     * @param largeArcFlag  the large arc flag. If true the arc spanning less
     *                      than or equal to 180 degrees is chosen, otherwise the arc spanning
     *                      greater than 180 degrees is chosen
     * @param sweepFlag     the sweep flag. If true the line joining center to arc
     *                      sweeps through decreasing angles otherwise it sweeps through increasing
     *                      angles
     * @param x             the absolute x coordinate of the final point of the arc.
     * @param y             the absolute y coordinate of the final point of the arc.
     */
    public void arcTo(double rx, double ry,
                      double xAxisRotation,
                      boolean largeArcFlag, boolean sweepFlag,
                      double x, double y) {

        // Get the current (x, y) coordinates of the path
        Point2D.Double lastPoint = (Point2D.Double) getCurrentPoint();
        double lastX = lastPoint.getX();
        double lastY = lastPoint.getY();

        ArcToCubicBezier.arcTo(lastX, lastY,
                rx, ry,
                xAxisRotation,
                x, y,
                largeArcFlag, sweepFlag,
                this::lineTo,
                this::curveTo);
    }
}
