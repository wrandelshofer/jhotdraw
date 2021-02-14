/*
 * @(#)IntersectPointQuadCurve.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.BezierCurves;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class IntersectPointQuadCurve {
    private IntersectPointQuadCurve() {
    }

    /**
     * Computes the intersection between a quadratic bezier curve and a point
     * with a tolerance radius.
     * <p>
     * This method solves the last equation shown in the list below.
     * <ol>
     * <li>{@literal (1 - t)²·p0 + 2·(1 - t)·t·p1 + t²·p2 , 0 ≤ t ≤ 1 }<br>
     * : quadratic bezier equation, vector form
     * </li>
     * <li>{@literal  (p0 - 2·p1 + p2)·t² - 2·(p0 - p1)·t + p0 }<br>
     * : expanded, and then collected for t
     * </li>
     * <li>{@literal c2·t² + c1·t + c0 }<br>
     * : coefficients compacted
     * </li>
     * <li>{@literal c2x·t² + c1x·t + c0x , c2y·t² + c1y·t + c0y }<br>
     * : bezier equation in matrix form
     * </li>
     * <li>{@literal fx , fy }<br>
     * : compacted matrix form
     * </li>
     * <li>{@literal (fx - cx)² + (fy - cy)² = 0 }<br>
     * : distance to point equation, with fx, fy inserted from matrix form
     * </li>
     * <li>{@literal c2x²·t⁴ + 2·c1x·c2x·t³ + (c1x² - 2·cx·c2x + 2·c0x·c2x)·t² + cx² - 2·cx·c0x + c0x² - 2·(cx·c1x - c0x·c1x)·t
     * }<br> {@literal + ..same for y-axis... }<br>
     * : coefficients expanded
     * </li>
     * <li>{@literal (c2x^2 + c2y^2)*t^4 }<br>
     * {@literal + 2*(c1x*c2x + c1y*c2y)*t^3 }<br>
     * {@literal + (c1x ^ 2 + c1y ^ 2 + 2 * c0x * c2x + 2 * c0y * c2y - 2 * c2x * cx - 2 * c2y * cy)*t^2 }<br>
     * {@literal + c0x^2 + c0y^2 - 2*c0x*cx + cx^2 - 2*c0y*cy + cy^2 + 2*(c0x*c1x + c0y*c1y - c1x*cx - c1y*cy)*t}<br>
     * : coefficients collected for t</li>
     * <li>{@literal a·t⁴ + b·t³ + c·t² + d·t + e = 0, 0 ≤ t ≤ 1 }<br>
     * : final polynomial equation
     * </li>
     * <li>{@literal 4·a·t³ + 3·b·t² + 2·c·t + d = 0, 0 ≤ t ≤ 1 }<br>
     * : derivative
     * </li>
     * </ol>
     * The code of this method has been derived from intersection.js by
     * Kevin Lindsey, copyright 2002 Kevin Lindsey, BSD 3-clause license.
     * http://www.kevlindev.com/gui/math/intersection/Intersection.js.
     *
     * @param x0 x-coordinate of control point 0 of the bezier curve
     * @param y0 y-coordinate of control point 0 of the bezier curve
     * @param x1 x-coordinate of control point P0 of the bezier curve
     * @param y1 y-coordinate of control point P0 of the bezier curve
     * @param x2 x-coordinate of control point P1 of the bezier curve
     * @param y2 y-coordinate of control point P1 of the bezier curve
     * @param cx x-coordinate of the point
     * @param cy y-coordinate of the point
     * @param r  the tolerance radius
     * @return the intersection
     */
    public static @NonNull IntersectionResult intersectQuadCurvePoint(
            double x0, double y0, double x1, double y1, double x2, double y2,
            double cx, double cy, double r) {

        // Build polynomial
        final double c2x, c2y, c1x, c1y, c0x, c0y;
        c2x = x0 - 2 * x1 + x2;
        c2y = y0 - 2 * y1 + y2;
        c1x = -2 * (x0 - x1);
        c1y = -2 * (y0 - y1);
        c0x = x0;
        c0y = y0;

        final double a, b, c, d;
        a = (c2x * c2x + c2y * c2y);
        b = 2 * (c1x * c2x + c1y * c2y);
        c = (c1x * c1x + c1y * c1y + 2 * c0x * c2x + 2 * c0y * c2y - 2 * c2x * cx - 2 * c2y * cy);
        d = 2 * (c0x * c1x + c0y * c1y - c1x * cx - c1y * cy);
        //final double e=c0x*c0x + c0y*c0y - 2*c0x*cx + cx*cx - 2*c0y*cy + cy*cy;

        // Solve for roots in derivative
        final double[] roots = new Polynomial(4 * a, 3 * b, 2 * c, d).getRoots();

        // Select roots with closest distance to point
        final List<IntersectionPoint> result = new ArrayList<>();
        final Point2D.Double p1, p2, p3;
        p1 = new Point2D.Double(x0, y0);
        p2 = new Point2D.Double(x1, y1);
        p3 = new Point2D.Double(x2, y2);
        final double rr = r * r;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (double tt : roots) {
            final Point2D.Double p;
            final double t;
            if (tt < 0) {
                p = p1;
            } else if (tt > 1) {
                p = p3;
            } else {
                t = tt;
                p = Points2D.sum(Points2D.multiply(p1, (1 - t) * (1 - t)),
                        Points2D.multiply(p2, 2 * (1 - t) * t),
                        Points2D.multiply(p3, t * t));
            }

            double dd = (p.getX() - cx) * (p.getX() - cx) + (p.getY() - cy) * (p.getY() - cy);
            if (dd < rr) {
                if (abs(dd - bestDistance) < Geom.REAL_THRESHOLD) {
                    result.add(new IntersectionPoint(p, tt));
                } else if (dd < bestDistance) {
                    bestDistance = dd;
                    result.clear();
                    result.add(new IntersectionPoint(p, tt));
                }
            }
        }

        return new IntersectionResult(result.isEmpty() ? IntersectionStatus.NO_INTERSECTION : IntersectionStatus.INTERSECTION,
                result);
    }

    public static @NonNull IntersectionResultEx intersectQuadCurvePointEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double cx, double cy, double epsilon) {
        IntersectionResult result = intersectQuadCurvePoint(a0x, a0y, a1x, a1y, a2x, a2y, cx, cy, epsilon);

        ArrayList<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : result) {
            double x = ip.getX();
            double y = ip.getY();
            Point2D.Double tangentA = BezierCurves.evalQuadCurveTangent(a0x, a0y, a1x, a1y, a2x, a2y, ip.getArgumentA());
            list.add(new IntersectionPointEx(
                    x, y,
                    ip.getArgumentA(), tangentA.getX(), tangentA.getY(),
                    0, 1, 0
            ));
        }

        return new IntersectionResultEx(result.getStatus(), list);
    }

}
