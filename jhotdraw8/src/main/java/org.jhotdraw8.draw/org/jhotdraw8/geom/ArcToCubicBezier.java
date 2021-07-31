/*
 * @(#)ArcToCubicBezier.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.util.function.Double2Consumer;
import org.jhotdraw8.util.function.Double6Consumer;

import java.util.ArrayList;

public class ArcToCubicBezier {
    private static final double TAU = Math.PI * 2.0;

    /**
     * Approximates one unit arc segment with a bézier curve.
     * <p>
     * See discussion in
     * <a href="http://math.stackexchange.com/questions/873224">math.stackexchange.com</a>
     * on how to calculate control points of cubic bezier curve approximating a
     * part of a circle.
     *
     * @param theta1      the start angle in radians of the arc on the circle.
     * @param delta_theta the length of the arc segment in radians,
     *                    *               must be less than π/2 = 90°.
     * @return
     */
    private static double[] approximateUnitArc(double theta1, double delta_theta) {
        double alpha = (4 / 3.0) * Math.tan(delta_theta * 0.25);

        double x1 = Math.cos(theta1);
        double y1 = Math.sin(theta1);
        double x2 = Math.cos(theta1 + delta_theta);
        double y2 = Math.sin(theta1 + delta_theta);

        return new double[]{x1, y1, x1 - y1 * alpha, y1 + x1 * alpha, x2 + y2 * alpha, y2 - x2 * alpha, x2, y2};
    }

    /**
     * Converts an arcTo into a sequence of curveTo,
     * or - if the arcTo is degenerate - to a lineTo or to nothing.
     * <p>
     * As specified in
     * <a href="http://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands">w3.org</a>
     * <p>
     * This code has been derived from svgpath library [1].
     * <p>
     * See <a href="http://math.stackexchange.com/questions/873224">math.stackexchange.com</a>
     * for a discussion on how to calculate control points of cubic bezier curve
     * approximating a part of a circle.
     * <p>
     * References:
     * <dl>
     *     <dt>[1] svgpath library</dt>
     *     <dd>svgpath. Copyright (C) 2013-2015 by Vitaly Puzrin, MIT License.
     *     <a href="https://github.com/fontello/svgpath/blob/master/lib/a2c.js>github.com</a></dd>
     * </dl>
     *
     *
     * @param lastX         the last x coordinate
     * @param lastY         the last y coordinate
     * @param radiusX       the x radius of the arc
     * @param radiusY       the y radius of the arc
     * @param xAxisRotation the x-axis rotation of the arc in degrees
     * @param x             the x to coordinate
     * @param y             the y to coordinate
     * @param largeArcFlag  the large-arc flag
     * @param sweepFlag     the sweep-arc flag
     * @param lineTo        the consumer for lineTo (can be called 0 to 1 times
     * @param curveTo       the consumer for curveTo (can be called 0 to 4 times)
     */
    public static void arcTo(
            double lastX, double lastY,
            double radiusX, double radiusY,
            double xAxisRotation,
            double x, double y,
            boolean largeArcFlag, boolean sweepFlag,
            @NonNull Double2Consumer lineTo,
            @NonNull Double6Consumer curveTo
    ) {
        final double x1, y1, x2, y2, phi;
        double rx, ry;
        x1 = lastX;
        y1 = lastY;
        x2 = x;
        y2 = y;
        rx = radiusX;
        ry = radiusY;
        phi = xAxisRotation;

        double sin_phi = Geom.sinDegrees(phi);
        double cos_phi = Geom.cosDegrees(phi);

        // Make sure radii are valid.
        double x1p = cos_phi * (x1 - x2) * 0.5 + sin_phi * (y1 - y2) * 0.5;
        double y1p = -sin_phi * (x1 - x2) * 0.5 + cos_phi * (y1 - y2) * 0.5;

        if (x1p == 0 && y1p == 0) {
            // We're asked to draw line to itself.
            lineTo.accept(x, y);
            return;
        }

        if (rx == 0 || ry == 0) {
            // One of the radii is zero.
            lineTo.accept(x, y);
            return;
        }


        // Compensate out-of-range radii.
        rx = Math.abs(rx);
        ry = Math.abs(ry);

        double lambda = (x1p * x1p) / (rx * rx) + (y1p * y1p) / (ry * ry);
        if (lambda > 1) {
            double sqrtLambda = Math.sqrt(lambda);
            rx *= sqrtLambda;
            ry *= sqrtLambda;
        }


        // Get center parameters (cx, cy, theta1, delta_theta).
        ArcCenter cc = getArcCenter(x1, y1, x2, y2, largeArcFlag, sweepFlag, rx, ry, sin_phi, cos_phi);

        ArrayList<double[]> result = new ArrayList<double[]>();
        double theta1 = cc.theta1;
        double delta_theta = cc.delta_theta;

        // Split an arc to multiple segments, so each segment
        // will be less than τ/4 (= 90° = π/2).
        double segments = Math.max(Math.ceil(Math.abs(delta_theta) / (Math.PI * 0.5)), 1.0);
        delta_theta /= segments;

        for (int i = 0; i < segments; i++) {
            result.add(approximateUnitArc(theta1, delta_theta));
            theta1 += delta_theta;
        }

        // We have a bezier approximation of a unit circle,
        // now need to transform back to the original ellipse.
        for (int k = 0, n = result.size(); k < n; k++) {
            double[] curve = result.get(k);
            for (int i = 2; i < curve.length; i += 2) {
                double x_ = curve[i];
                double y_ = curve[i + 1];

                // scale
                x_ *= rx;
                y_ *= ry;

                // rotate
                double xp = cos_phi * x_ - sin_phi * y_;
                double yp = sin_phi * x_ + cos_phi * y_;

                // translate
                curve[i] = xp + cc.cx;
                curve[i + 1] = yp + cc.cy;
            }
            if (k == n - 1) {
                // Make sure that we get to x,y despite rounding errors.
                curveTo.accept(curve[2], curve[3], curve[4], curve[5], x, y);
            } else {
                curveTo.accept(curve[2], curve[3], curve[4], curve[5], curve[6], curve[7]);
            }
        }
    }

    /**
     * Converts from endpoint to center parameterization.
     * <p>
     * See <a href="http://www.w3.org/TR/SVG11/implnote.html#ArcImplementationNotes">
     *     w3.org</a>
     * <p>
     * Return [cx, cy, theta1, delta_theta]
     */
    private static ArcCenter getArcCenter(double x1, double y1, double x2, double y2, boolean fa, boolean fs,
                                          double rx, double ry, double sin_phi, double cos_phi) {
        // Step 1.
        //
        // Moving an ellipse so origin will be the middlepoint between our two
        // points. After that, rotate it to line up ellipse axes with coordinate
        // axes.
        double x1p = cos_phi * (x1 - x2) * 0.5 + sin_phi * (y1 - y2) * 0.5;
        double y1p = -sin_phi * (x1 - x2) * 0.5 + cos_phi * (y1 - y2) * 0.5;

        double rx_sq = rx * rx;
        double ry_sq = ry * ry;
        double x1p_sq = x1p * x1p;
        double y1p_sq = y1p * y1p;

        // Step 2.
        //
        // Compute coordinates of the centre of this ellipse (cx', cy')
        // in the new coordinate system.
        double radicand = (rx_sq * ry_sq) - (rx_sq * y1p_sq) - (ry_sq * x1p_sq);

        if (radicand < 0) {
            // due to rounding errors it might be e.g. -1.3877787807814457e-17
            radicand = 0;
        }

        radicand /= (rx_sq * y1p_sq) + (ry_sq * x1p_sq);
        radicand = Math.sqrt(radicand) * (fa == fs ? -1 : 1);

        double cxp = radicand * rx / ry * y1p;
        double cyp = radicand * -ry / rx * x1p;

        // Step 3.
        //
        // Transform back to get centre coordinates (cx, cy) in the original
        // coordinate system.
        double cx = cos_phi * cxp - sin_phi * cyp + (x1 + x2) * 0.5;
        double cy = sin_phi * cxp + cos_phi * cyp + (y1 + y2) * 0.5;

        // Step 4.
        //
        // Compute angles (theta1, delta_theta).
        double v1x = (x1p - cxp) / rx;
        double v1y = (y1p - cyp) / ry;
        double v2x = (-x1p - cxp) / rx;
        double v2y = (-y1p - cyp) / ry;

        double theta1 = unitVectorAngle(1, 0, v1x, v1y);
        double delta_theta = unitVectorAngle(v1x, v1y, v2x, v2y);

        if (!fs && delta_theta > 0) {
            delta_theta -= TAU;
        }
        if (fs && delta_theta < 0) {
            delta_theta += TAU;
        }

        return new ArcCenter(cx, cy, theta1, delta_theta);
    }

    /**
     * Returns the angle in radians between two unit vectors {@code u} and
     * {@code v}.
     * <pre>{@literal
     *  angle = sign * acos( <u,v> )
     * }</pre>
     * Since we measure angle between radii of circular arcs,
     * we can use simplified math (without length normalization).
     *
     * @param ux x-coordinate of unit vector u
     * @param uy y-coordinate of unit vector u
     * @param vx x-coordinate of unit vector v
     * @param vy y-coordinate of unit vector v
     * @return angle in radians
     */
    private static double unitVectorAngle(double ux, double uy, double vx, double vy) {
        int sign = (ux * vy - uy * vx < 0) ? -1 : 1;

        // Rounding errors, e.g. -1.0000000000000002 can screw up this.
        double dot = Geom.clamp(ux * vx + uy * vy,-1,1);

        return sign * Math.acos(dot);
    }

    private static class ArcCenter {
        final double cx, cy, theta1, delta_theta;

        private ArcCenter(double cx, double cy, double theta1, double delta_theta) {
            this.cx = cx;
            this.cy = cy;
            this.theta1 = theta1;
            this.delta_theta = delta_theta;
        }
    }

}
