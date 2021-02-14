/*
 * @(#)Utils.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.OrderedPair;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.Point2D;
import java.util.List;

public class Utils {
    /**
     * absolute threshold to be used for reals in common geometric computation (e.g. to check for
     * singularities).
     */
    public static final double realPrecision = 1e-5;
    /**
     * absolute threshold to be used for joining slices together at end points
     */
    public static final double sliceJoinThreshold = 1e-4;

    public static final double tau = 2.0 * Math.PI;
    // absolute threshold to be used for pruning invalid slices for offset
    public static final double offsetThreshold = 1e-4;


    /// Perpendicular dot product. Equivalent to dot(v0, perp(v1)).
    public static double perpDot(final Point2D.Double v0, final Point2D.Double v1) {
        return v0.getX() * v1.getY() - v0.getY() * v1.getX();
    }

    public static OrderedPair<Double, Double> minmax(double a, double b) {
        return a < b ? new OrderedPair<>(a, b) : new OrderedPair<>(b, a);
    }

    public static boolean fuzzyInRange(double minValue, double value, double maxValue) {
        return fuzzyInRange(minValue, value, maxValue, Geom.REAL_THRESHOLD);
    }

    public static boolean fuzzyInRange(double minValue, double value, double maxValue, double epsilon) {
        return (value + epsilon > minValue) && (value < maxValue + epsilon);
    }

    /**
     * Test if a point is within a arc sweep angle region defined by center, start, end, and bulge.
     */
    static boolean pointWithinArcSweepAngle(final Point2D.Double center, final Point2D.Double arcStart,
                                            final Point2D.Double arcEnd, double bulge, final Point2D.Double point) {
        assert Math.abs(bulge) > Geom.REAL_THRESHOLD : "expected arc";
        assert Math.abs(bulge) <= 1.0 : "bulge should always be between -1 and 1";

        if (bulge > 0.0) {
            return isLeftOrCoincident(center, arcStart, point) &&
                    isRightOrCoincident(center, arcEnd, point);
        }

        return isRightOrCoincident(center, arcStart, point) && isLeftOrCoincident(center, arcEnd, point);
    }

    /**
     * Returns true if point is left or fuzzy coincident with the line pointing in the direction of the
     * vector (p1 - p0).
     */
    static boolean isLeftOrCoincident(final Point2D.Double p0, final Point2D.Double p1,
                                      final Point2D.Double point) {
        return isLeftOrCoincident(p0, p1, point, Geom.REAL_THRESHOLD);
    }

    static boolean isLeftOrCoincident(final Point2D.Double p0, final Point2D.Double p1,
                                      final Point2D.Double point, double epsilon) {
        return (p1.getX() - p0.getX()) * (point.getY() - p0.getY()) - (p1.getY() - p0.getY()) * (point.getX() - p0.getX()) >
                -epsilon;
    }

    /**
     * Returns true if point is right or fuzzy coincident with the line pointing in the direction of
     * the vector (p1 - p0).
     */
    static boolean isRightOrCoincident(final Point2D.Double p0, final Point2D.Double p1,
                                       final Point2D.Double point) {
        return isRightOrCoincident(p0, p1, point, Geom.REAL_THRESHOLD);
    }

    static boolean isRightOrCoincident(final Point2D.Double p0, final Point2D.Double p1,
                                       final Point2D.Double point, double epsilon) {
        return (p1.getX() - p0.getX()) * (point.getY() - p0.getY()) - (p1.getY() - p0.getY()) * (point.getX() - p0.getX()) <
                epsilon;
    }

    /**
     * Returns the solutions to for the quadratic equation -b +/- sqrt (b * b - 4 * a * c) / (2 * a).
     */
    static OrderedPair<Double, Double> quadraticSolutions(double a, double b, double c, double discr) {
        // Function avoids loss in precision due to taking the difference of two floating point values
        // that are very near each other in value.
        // See:
        // https://math.stackexchange.com/questions/311382/solving-a-quadratic-equation-with-precision-when-using-floating-point-variables
        assert Geom.almostEqual(b * b - 4.0 * a * c, discr, Geom.REAL_THRESHOLD) : "discriminate is not correct";
        double sqrtDiscr = Math.sqrt(discr);
        double denom = 2.0 * a;
        double sol1;
        if (b < 0.0) {
            sol1 = (-b + sqrtDiscr) / denom;
        } else {
            sol1 = (-b - sqrtDiscr) / denom;
        }

        double sol2 = (c / a) / sol1;
        return minmax(sol1, sol2);
    }

    /**
     * Return the point on the segment going from p0 to p1 at parametric value t.
     */
    public static Point2D.Double pointFromParametric(final Point2D.Double p0, final Point2D.Double p1, double t) {
        return Geom.lerp(p0, p1, t);
        //return Points2D.add(p0,Points2D.multiply(Points2D.subtract(p1,p0),t));
    }

    /**
     * Counter clockwise angle of the vector going from p0 to p1.
     */
    public static double angle(final Point2D.Double p0, final Point2D.Double p1) {
        return Geom.atan2(p1.getY() - p0.getY(), p1.getX() - p0.getX());
    }

    /**
     * Returns the smaller difference between two angles, result is negative if angle2 < angle1.
     */
    public static double deltaAngle(double angle1, double angle2) {
        double diff = normalizeRadians(angle2 - angle1);
        if (diff > Math.PI) {
            diff -= tau;
        }

        return diff;
    }

    /**
     * Normalize radius to be between 0 and 2PI, e.g. -PI/4 becomes 7PI/8 and 5PI becomes PI.
     */
    public static double normalizeRadians(double angle) {
        if (angle >= 0.0 && angle <= tau) {
            return angle;
        }

        return angle - Math.floor(angle / tau) * tau;
    }

    /**
     * Normalized perpendicular vector to v (rotating counter clockwise).
     */
    public static Point2D.Double unitPerp(Point2D.Double v) {
        Point2D.Double result = new Point2D.Double(-v.getY(), v.getX());
        return Points2D.normalize(result);
    }

    static <T> int nextWrappingIndex(int index, @NonNull List<T> container) {
        return index == container.size() - 1 ? 0 : index + 1;

    }

    static <T> int prevWrappingIndex(int index, @NonNull List<T> container) {
        return index == 0 ? container.size() - 1 : index - 1;

    }

    static boolean angleIsWithinSweep(double startAngle, double sweepAngle, double testAngle) {
        return angleIsWithinSweep(startAngle, sweepAngle, testAngle, Geom.REAL_THRESHOLD);
    }

    static boolean angleIsWithinSweep(double startAngle, double sweepAngle, double testAngle,
                                      double epsilon) {
        double endAngle = startAngle + sweepAngle;
        if (sweepAngle < 0.0) {
            return angleIsBetween(endAngle, startAngle, testAngle, epsilon);
        }

        return angleIsBetween(startAngle, endAngle, testAngle, epsilon);
    }

    static boolean angleIsBetween(double startAngle, double endAngle, double testAngle) {
        return angleIsBetween(startAngle, endAngle, testAngle, Geom.REAL_THRESHOLD);
    }

    static boolean angleIsBetween(double startAngle, double endAngle, double testAngle,
                                  double epsilon) {
        double endSweep = normalizeRadians(endAngle - startAngle);
        double midSweep = normalizeRadians(testAngle - startAngle);

        return midSweep < endSweep + epsilon;
    }

    /// Returns the closest point that lies on the line segment from p0 to p1 to the point given.

    static Point2D.Double closestPointOnLineSeg(Point2D.Double p0, Point2D.Double p1,
                                                Point2D.Double point) {
        // Dot product used to find angles
        // See: http://geomalgorithms.com/a02-_lines.html
        Point2D.Double v = Points2D.subtract(p1,p0);
        Point2D.Double w = Points2D.subtract(point,p0);
        double c1 = Points2D.dotProduct(w,v);
        if (c1 < Geom.REAL_THRESHOLD) {
            return p0;
        }

        double c2 = Points2D.dotProduct(v,v);
        if (c2 < c1 + Geom.REAL_THRESHOLD) {
            return p1;
        }

        double b = c1 / c2;
        return Points2D.add(p0,Points2D.multiply(v,b));
    }
}
