/*
 * @(#)IntersectCubicCurvePoint.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.DoubleArrayList;
import org.jhotdraw8.geom.BezierCurves;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class IntersectCubicCurvePoint {
    private IntersectCubicCurvePoint() {
    }


    public static @NonNull IntersectionResultEx intersectCubicCurvePointEx(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy) {
        return intersectCubicCurvePointEx(x0, y0, x1, y1, x2, y2, x3, y3, cx, cy, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResultEx intersectCubicCurvePointEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
            double cx, double cy, double epsilon) {
        IntersectionResult result = intersectCubicCurvePoint(a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, cx, cy, epsilon);

        ArrayList<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : result) {
            double x = ip.getX();
            double y = ip.getY();
            Point2D.Double tangentA = BezierCurves.evalCubicCurveTangent(a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, ip.getArgumentA());
            list.add(new IntersectionPointEx(
                    x, y,
                    ip.getArgumentA(), tangentA.getX(), tangentA.getY(),
                    0, 1, 0
            ));
        }

        return new IntersectionResultEx(result.getStatus(), list);
    }

    /**
     * Computes the intersection between a quadratic bezier curve and a point
     * with a tolerance radius.
     * <p>
     * This method solves the last equation shown in the list below.
     * <ol>
     * <li>{@literal (1 - t)³·p0 + 3·(1 - t)²·t·p1 + 3·(1 - t)·t²·p2 + t³·p3 , 0 ≤ t ≤ 1
     * }<br>
     * : cubic bezier equation, vector form
     * </li>
     * <li>{@literal  -(p0 - 3*p1 + 3*p2 - p3)*t^3 + 3*(p0 - 2*p1 + p2)*t^2 - 3*(p0 - p1)*t + p0 }<br>
     * : expanded, and then collected for t
     * </li>
     * <li>{@literal c3·t³ + c2·t² + c1·t + c0 }<br>
     * : coefficients compacted
     * </li>
     * <li>{@literal c3x·t³ + c2x·t² + c1x·t + c0x , c3y·t³ + c2y·t² + c1y·t + c0y }<br>
     * : bezier equation in matrix form
     * </li>
     * <li>{@literal fx , fy }<br>
     * : compacted matrix form
     * </li>
     * <li>{@literal (fx - cx)² + (fy - cy)² = 0 }<br>
     * : distance to point equation, with fx, fy inserted from matrix form
     * </li>
     * <li>{@literal c3x^2*t^6 + 2*c2x*c3x*t^5 + (c2x^2 + 2*c1x*c3x)*t^4 + 2*(c1x*c2x
     * + c0x*c3x - c3x*cx)*t^3 + (c1x^2 + 2*c0x*c2x - 2*c2x*cx)*t^2 + c0x^2 -
     * 2*c0x*cx + cx^2 + 2*(c0x*c1x - c1x*cx)*t }<br>
     * {@literal + ..same for y-axis... }<br>
     * : coefficients expanded
     * </li>
     * <li>{@literal (c3x^2 + c3y^2)*t^6 }<br>
     * {@literal + 2*(c2x*c3x + c2y*c3y)*t^5 }<br>
     * {@literal + (c2x^2 + c2y^2 + 2*c1x*c3x + 2*c1y*c3y)*t^4 }<br>
     * {@literal + 2*(c1x*c2x + c1y*c2y + c0x*c3x + c0y*c3y - c3x*cx - c3y*cy)*t^3 }<br>
     * {@literal + (c1x^2 + c1y^2 + 2*c0x*c2x + 2*c0y*c2y - 2*c2x*cx - 2*c2y*cy)*t^2 }<br>
     * {@literal + 2*(c0x*c1x + c0y*c1y - c1x*cx - c1y*cy)*t }<br>
     * {@literal + c0x^2 + c0y^2 - 2*c0x*cx + cx^2 - 2*c0y*cy + cy^2 }<br>
     * : coefficients collected for t</li>
     * <li>{@literal a·t⁶ + b·t⁵ + c·t⁴ + d·t³ + e·t² + f·t + g = 0, 0 ≤ t ≤ 1 }<br>
     * : final polynomial equation
     * </li>
     * <li>{@literal 6·a·t⁵ + 5·b·t⁴ + 4·c·t³ + 3·d·t² + 2·e·t + f = 0, 0 ≤ t ≤ 1 }<br>
     * : derivative
     * </li>
     * </ol>
     * <p>
     * The code of this method has been derived from intersection.js by
     * Kevin Lindsey, copyright 2002 Kevin Lindsey, BSD 3-clause license.
     * http://www.kevlindev.com/gui/math/intersection/Intersection.js.
     *
     * @param x0 x-coordinate of control point P0 of the bezier curve
     * @param y0 y-coordinate of control point P0 of the bezier curve
     * @param x1 x-coordinate of control point P1 of the bezier curve
     * @param y1 y-coordinate of control point P1 of the bezier curve
     * @param x2 x-coordinate of control point P2 of the bezier curve
     * @param y2 y-coordinate of control point P2 of the bezier curve
     * @param x3 x-coordinate of control point P3 of the bezier curve
     * @param y3 y-coordinate of control point P3 of the bezier curve
     * @param cx x-coordinate of the point
     * @param cy y-coordinate of the point
     * @param epsilon  the tolerance radius
     * @return the intersection
     */
    public static @NonNull IntersectionResult intersectCubicCurvePoint(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy, double epsilon) {
        // Build polynomial
        final double c3x, c3y, c2x, c2y, c1x, c1y, c0x, c0y;
        c3x = -(x0 - 3 * x1 + 3 * x2 - x3);
        c3y = -(y0 - 3 * y1 + 3 * y2 - y3);
        c2x = 3 * (x0 - 2 * x1 + x2);
        c2y = 3 * (y0 - 2 * y1 + y2);
        c1x = -3 * (x0 - x1);
        c1y = -3 * (y0 - y1);
        c0x = x0;
        c0y = y0;

        final double a, b, c, d, e, f;
        a = (c3x * c3x + c3y * c3y);
        b = 2 * (c2x * c3x + c2y * c3y);
        c = (c2x * c2x + c2y * c2y + 2 * c1x * c3x + 2 * c1y * c3y);
        d = 2 * (c1x * c2x + c1y * c2y + c0x * c3x + c0y * c3y - c3x * cx - c3y * cy);
        e = (c1x * c1x + c1y * c1y + 2 * c0x * c2x + 2 * c0y * c2y - 2 * c2x * cx - 2 * c2y * cy);
        f = 2 * (c0x * c1x + c0y * c1y - c1x * cx - c1y * cy);

        // Solve for roots in derivative
        final DoubleArrayList clampedRoots = new Polynomial(6 * a, 5 * b, 4 * c, 3 * d, 2 * e, f).getRootsInInterval(0, 1);
        // Add zero and one, because we have clamped the roots
        final DoubleArrayList roots = new DoubleArrayList();
        roots.addAll(clampedRoots);
        roots.add(0.0);
        roots.add(1.0);

        // Select roots with closest distance to point
        final List<IntersectionPoint> result = new ArrayList<>();
        final Point2D.Double p0, p1, p2, p3;
        p0 = new Point2D.Double(x0, y0);
        p1 = new Point2D.Double(x1, y1);
        p2 = new Point2D.Double(x2, y2);
        p3 = new Point2D.Double(x3, y3);
        final double rr = epsilon * epsilon;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (double t : roots) {
            final Point2D.Double p;
            p = Points2D.sum(Points2D.multiply(p0, (1 - t) * (1 - t) * (1 - t)),
                    Points2D.multiply(p1, 3 * (1 - t) * (1 - t) * t),
                    Points2D.multiply(p2, 3 * (1 - t) * t * t),
                    Points2D.multiply(p3, t * t * t));

            double dd = (p.getX() - cx) * (p.getX() - cx) + (p.getY() - cy) * (p.getY() - cy);
            if (dd < rr) {
                if (abs(dd - bestDistance) < Geom.REAL_THRESHOLD) {
                    result.add(new IntersectionPoint(p, t));
                } else if (dd < bestDistance) {
                    bestDistance = dd;
                    result.clear();
                    result.add(new IntersectionPoint(p, t));
                }
            }
        }

        return new IntersectionResult(
                result.isEmpty() ? IntersectionStatus.NO_INTERSECTION : IntersectionStatus.INTERSECTION,
                result);
    }
}
