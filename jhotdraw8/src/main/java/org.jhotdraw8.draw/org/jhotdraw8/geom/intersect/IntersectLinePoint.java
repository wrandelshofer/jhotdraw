/*
 * @(#)IntersectLinePoint.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.Geom;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class IntersectLinePoint {
    private IntersectLinePoint() {
    }

    /**
     * Computes the intersection between a line and a point with tolerance
     * radius r.
     * <p>
     * The returned intersection contain the parameters 't1' of the line in range
     * [0,1].
     * <p>
     * The intersection will have one of the following status:
     * <ul>
     * <li>{@link IntersectionStatus#INTERSECTION}</li>
     * <li>{@link IntersectionStatus#NO_INTERSECTION}</li>
     * </ul>
     * <p>
     * This method solves the last equation shown in the list below.
     * <ol>
     * <li>{@literal p0 + (p1 - p0) · t1 , 0 ≤ t1 ≤ 1}<br>
     * : line equation in vector form</li>
     * <li>{@literal x0 + (x1 - x0) · t1, y0 + (y1 - y0) · t1 }<br>
     * : line equation in matrix form</li>
     * <li>{@literal x0 + Δx · t1, y0 + Δy · t1 }<br>
     * : partially compacted coefficients</li>
     * <li>{@literal fx, fy }<br>
     * : compacted coefficients in matrix form</li>
     * <li>{@literal (fx - cx)² + (fy - cy)² = 0}<br>
     * : distance to point equation with fx, fy coefficients inserted</li>
     * <li>{@literal Δx²·Δy²·t1² }<br>
     * {@literal + 2·(Δx·(x0 - cx)+Δy·(y0 - cy))·t1 }<br>
     * {@literal - 2·(x0·cx + y0·cy) + cx² + cy² + x0² + y0²  = 0 }<br>
     * : fx, fy coefficients expanded and equation reordered</li>
     * <li>{@literal a·t1² + b·t1 + c = 0, 0 ≤ t1 ≤ 1 }<br>
     * : final quadratic polynomial
     * </li>
     * <li>{@literal 2·a·t1 + b = 0, 0 ≤ t1 ≤ 1 }<br>
     * : derivative</li>
     * </ol>
     * <p>
     * The code of this method has been derived from intersection.js by
     * Kevin Lindsey, copyright 2002 Kevin Lindsey, BSD 3-clause license.
     * http://www.kevlindev.com/gui/math/intersection/Intersection.js.
     *
     * @param x0 point 0 of the line
     * @param y0 point 0 of the line
     * @param x1 point 1 of the line
     * @param y1 point 1 of the line
     * @param cx the center of the point p.x
     * @param cy the center of the point p.y
     * @param r  the tolerance radius
     * @return computed intersection
     */
    public static @NonNull IntersectionResult intersectLinePoint(double x0, double y0, double x1, double y1, double cx, double cy, double r) {
        List<IntersectionPoint> result = new ArrayList<>();
        // Build polynomial
        final double Δx, Δy, a, b;
        Δx = x1 - x0;
        Δy = y1 - y0;
        a = Δx * Δx + Δy * Δy;
        b = 2 * (Δx * (x0 - cx) + Δy * (y0 - cy));

        // Solve for roots in derivative
        double[] roots = new Polynomial(2 * a, b).getRoots();

        if (roots.length > 0) {
            double t = max(0, min(roots[0], 1));
            double x = x0 + t * Δx;
            double y = y0 + t * Δy;
            double dd = (x - cx) * (x - cx) + (y - cy) * (y - cy);
            if (dd <= r * r) {
                result.add(new IntersectionPoint(new Point2D.Double(x, y), t));
            }
        }

        return new IntersectionResult(
                result.isEmpty() ? IntersectionStatus.NO_INTERSECTION : IntersectionStatus.INTERSECTION,
                result);
    }

    public static @NonNull IntersectionResultEx intersectLinePointEx(double x0, double y0, double x1, double y1, double cx, double cy, double r) {
        IntersectionResult result = intersectLinePoint(x0, y0, x1, y1, cx, cy, r);
        ArrayList<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : result) {
            double x = ip.getX();
            double y = ip.getY();
            list.add(new IntersectionPointEx(
                    x, y,
                    ip.getArgumentA(), x1 - x0, y1 - y0,
                    0, 1, 0
            ));
        }

        return new IntersectionResultEx(result.getStatus(), list);
    }

    public static boolean lineContainsPoint(double x1, double y1,
                                            double x2, double y2,
                                            double px, double py) {
        return lineContainsPoint(x1, y1, x2, y2, px, py, Geom.REAL_THRESHOLD);
    }

    /**
     * Tests if a point is inside a line segment.
     *
     * @param x1        the x coordinate of point 1 on the line
     * @param y1        the y coordinate of point 1 on the line
     * @param x2        the x coordinate of point 2 on the line
     * @param y2        the y coordinate of point 2 on the line
     * @param px        the x coordinate of the point
     * @param py        the y coordinate of the point
     * @param tolerance the maximal distance that the point may stray from the
     *                  line
     * @return true if the line contains the point within the given tolerance
     */
    public static boolean lineContainsPoint(double x1, double y1,
                                            double x2, double y2,
                                            double px, double py, double tolerance) {
        if (!Geom.contains(min(x1, x2), min(y1, y2), abs(x2 - x1), abs(y2 - y1), px, py, tolerance)) {
            return false;
        }

        double a, b, x, y;

        if (Geom.almostEqual(x1, x2, tolerance)) {
            return (abs(px - x1) <= tolerance);
        }
        if (Geom.almostEqual(y1, y2, tolerance)) {
            return (abs(py - y1) <= tolerance);
        }

        a = (y1 - y2) / (x1 - x2);
        b = y1 - a * x1;
        x = (py - b) / a;
        y = a * px + b;

        return (min(abs(x - px), abs(y - py)) <= tolerance);
    }

    /**
     * Given a point p on a line, computes the value of the argument 't'.
     * <p>
     * If the point p is not on the line it is projected perpendicularly
     * on the line. In this case 't' may be outside of the range [0,1].
     *
     * @param x1 start of line
     * @param y1 start of line
     * @param x2 end of line
     * @param y2 end of line
     * @param px point
     * @param py point
     * @return argument 't' at point px,py on the line.
     */
    public static double argumentOnLine(double x1, double y1, double x2, double y2, double px, double py) {
        double w = x2 - x1;
        double h = y2 - y1;
        if (Math.abs(w) > Math.abs(h)) {
            return (px - x1) / w;
        } else {
            return (py - y1) / h;
        }
    }
}
