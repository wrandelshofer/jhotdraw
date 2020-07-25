package org.jhotdraw8.geom.biarc;

import org.jhotdraw8.geom.BezierCurves;
import org.jhotdraw8.geom.Points2D;
import org.jhotdraw8.geom.intersect.IntersectRayRay;
import org.jhotdraw8.geom.intersect.IntersectionResultEx;
import org.jhotdraw8.geom.intersect.IntersectionStatus;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Bezier2BiArc {
    private Bezier2BiArc() {
    }


    /**
     * Algorithm to approximate a bezier curve with bi-arcs.
     *
     * @param bezier          The bezier curve to be approximated.
     * @param nrPointsToCheck The number of points used for calculating the approximation error
     * @param tolerance       The approximation is accepted if the maximum deviation at the sampling points is smaller than this number.
     * @return the approximated biarcs
     */
    public static List<BiArc> approxCubicBezier(CubicCurve2D.Double bezier, int nrPointsToCheck, double tolerance) {
        // The result will be put here
        List<BiArc> biarcs = new ArrayList<>();

        int maxCurves = 1024;

        // The bezier curves to approximate
        var stack = new ArrayDeque<CubicCurve2D.Double>();

        // ---------------------------------------------------------------------------
        // First, calculate the inflection points and split the bezier at them (if any)
        splitAtInflectionPoints(bezier, stack);

        // ---------------------------------------------------------------------------
        // Second, approximate the curves until we run out of them
        while (!stack.isEmpty()) {
            bezier = stack.pop();

            // ---------------------------------------------------------------------------
            // Calculate the transition point for the BiArc

            // V: Intersection point of tangent lines
            var C1 = bezier.getP1().equals(bezier.getCtrlP1()) ? bezier.getCtrlP2() : bezier.getCtrlP1();
            var C2 = bezier.getP2().equals(bezier.getCtrlP2()) ? bezier.getCtrlP1() : bezier.getCtrlP2();

            IntersectionResultEx intersectionResultEx = IntersectRayRay.intersectRayRayEx(bezier.getP1(), C1, bezier.getP2(), C2);

            // Edge case: control lines are parallel
            if (intersectionResultEx.getStatus() == IntersectionStatus.NO_INTERSECTION_PARALLEL) {
                CubicCurve2D.Double first = new CubicCurve2D.Double();
                CubicCurve2D.Double second = new CubicCurve2D.Double();
                bezier.subdivide(first, second);
                stack.push(second);
                stack.push(first);
                continue;
            } else if (intersectionResultEx.getStatus() != IntersectionStatus.INTERSECTION) {
                // Edge case: control lines are coincident
                continue;
            }

            var V = intersectionResultEx.getFirst();

            Point2D.Double P1 = new Point2D.Double(bezier.getX1(), bezier.getY1());
            Point2D.Double P2 = new Point2D.Double(bezier.getX2(), bezier.getY2());
            Point2D.Double G = computeIncenterPoint(V, P1, P2);

            // ---------------------------------------------------------------------------
            // Calculate the BiArc
            BiArc biarc = new BiArc(P1,
                    Points2D.subtract(P1,C1), P2,
                    Points2D.subtract(P2,C2), G);

            // ---------------------------------------------------------------------------
            double tMaxError = getParamWithMaxErrorOverTolerance(bezier, nrPointsToCheck, tolerance, biarc);

            // Check if the two curves are close enough
            if (tMaxError != -1d && stack.size() + biarcs.size() < maxCurves) {
                // If not, split the bezier curve the point where the distance is the maximum
                // and try again with the two halves
                var bs = BezierCurves.split(bezier, tMaxError);
                stack.push(bs.second());
                stack.push(bs.first());
            } else {
                // Otherwise we are done with the current bezier
                biarcs.add(biarc);
            }
        }

        return biarcs;
    }

    /**
     * G: incenter point of the triangle (P1, V, P2).
     * <p>
     * Reference:<br>
     * <a href="http://www.mathopenref.com/coordincenter.html">mathopenref.com</a>
     *
     * @param a point A of the triangle
     * @param b point B of the triangle
     * @param c point C of the triangle
     * @return the incenter point G
     */
    public static Point2D.Double computeIncenterPoint(Point2D.Double a, Point2D.Double b, Point2D.Double c) {
        var dac = c.distance(a);
        var dab = b.distance(a);
        var dbc = b.distance(c);
        return Points2D.divide(
                Points2D.sum(Points2D.multiply(b,dac),
                Points2D.multiply(c,dab),
                        Points2D.multiply(a,dbc)),
                dac + dab + dbc);
    }

    /**
     * Calculate the parameter value with maximum error > tolerance,
     * returns -1d if all checked points are within tolerance
     *
     * @param bezier the bezier curve
     * @param nrPointsToCheck the number of points to check
     * @param tolerance the tolerated distance
     * @param biarc the approximated bi-arc
     * @return the parameter with maximum error or -1d
     */
    private static double getParamWithMaxErrorOverTolerance(CubicCurve2D.Double bezier, int nrPointsToCheck, double tolerance, BiArc biarc) {
        var maxDistance = tolerance;
        var maxDistanceAt = -1d;
        var parameterStep = 1d / nrPointsToCheck;
        for (int i = 0; i <= nrPointsToCheck; i++) {
            var t = parameterStep * i;
            var u1 = biarc.pointAt(t);
            var u2 = BezierCurves.evalCubicCurve(bezier, t);
            var distance = u1.distance(u2);

            if (distance > maxDistance) {
                maxDistance = distance;
                maxDistanceAt = t;
            }
        }
        return maxDistanceAt;
    }

    public static void splitAtInflectionPoints(CubicCurve2D.Double bezier, ArrayDeque<CubicCurve2D.Double> stack) {
        var toSplit = bezier;

        // Edge case: P1 == P2 -> Split bezier
        if (bezier.getP1().equals(bezier.getP2())) {
            CubicCurve2D.Double first = new CubicCurve2D.Double();
            CubicCurve2D.Double second = new CubicCurve2D.Double();
            bezier.subdivide(first, second);
            stack.push(second);
            stack.push(first);
        }
        // Edge case -> no inflection points
        else if (toSplit.getP1().equals(toSplit.getCtrlP1()) || toSplit.getP2().equals(toSplit.getCtrlP2())) {
            stack.push(toSplit);
        } else {
            var inflex = BezierCurves.inflectionPoints(toSplit);

            if (inflex.size() == 1) {
                var splitted = BezierCurves.split(toSplit, inflex.get(0));
                stack.push(splitted.second());
                stack.push(splitted.first());
            } else if (inflex.size() == 2) {
                var t1 = inflex.get(0);
                var t2 = inflex.get(1);

                // I'm not sure if I need, but it does not hurt to order them
                if (t1 > t2) {
                    var tmp = t1;
                    t1 = t2;
                    t2 = tmp;
                }

                // Make the first split and save the first new curve.
                // The second one has to be split again
                // at the recalculated t2 (it is on a new curve)
                var splitted1 = BezierCurves.split(toSplit, t1);
                t2 = (1 - t1) * t2;
                var splitted2 = BezierCurves.split(splitted1.second(), t2);
                stack.push(splitted2.second());
                stack.push(splitted2.first());
                stack.push(splitted1.first());
            } else {
                stack.push(toSplit);
            }
        }
    }
}
