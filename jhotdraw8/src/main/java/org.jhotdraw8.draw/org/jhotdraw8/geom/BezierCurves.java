/*
 * @(#)Beziers.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.function.Double2Consumer;
import org.jhotdraw8.util.function.Double4Consumer;
import org.jhotdraw8.util.function.Double6Consumer;

/**
 * Provides utility methods for Bézier curves.
 * <p>
 * See {@link Intersections} intersection methods with bézier curves.
 *
 * @author Werner Randelshofer
 */
public class BezierCurves {

    /**
     * Prevent instantiation.
     */
    private BezierCurves() {
    }

    /**
     * Evaluates the given curve at the specified time.
     *
     * @param x0 point P0 of the curve
     * @param y0 point P0 of the curve
     * @param x1 point P1 of the curve
     * @param y1 point P1 of the curve
     * @param x2 point P2 of the curve
     * @param y2 point P2 of the curve
     * @param x3 point P3 of the curve
     * @param y3 point P3 of the curve
     * @param t  the time
     * @return the point at time t
     */
    @NonNull
    public static Point2D evalCubicCurve(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
                                         double t) {
        final double x01, y01, x12, y12, x23, y23, x012, y012, x123, y123, x0123, y0123;
        x01 = lerp(x0, x1, t);
        y01 = lerp(y0, y1, t);

        x12 = lerp(x1, x2, t);
        y12 = lerp(y1, y2, t);

        x23 = lerp(x2, x3, t);
        y23 = lerp(y2, y3, t);

        x012 = lerp(x01, x12, t);
        y012 = lerp(y01, y12, t);

        x123 = lerp(x12, x23, t);
        y123 = lerp(y12, y23, t);

        x0123 = lerp(x012, x123, t);
        y0123 = lerp(y012, y123, t);

        return new Point2D(x0123, y0123);
    }

    /**
     * Linear interpolation from {@code a} to {@code b} at {@code t}.
     *
     * @param a a
     * @param b b
     * @param t a value in the range [0, 1]
     * @return the interpolated value
     */
    private static double lerp(double a, double b, double t) {
        return (b - a) * t + a;
    }

    /**
     * Evaluates the given curve at the specified time.
     *
     * @param x0 point P0 of the curve
     * @param y0 point P0 of the curve
     * @param x1 point P1 of the curve
     * @param y1 point P1 of the curve
     * @param t  the time
     * @return the point at time t
     */
    @NonNull
    public static Point2D evalLine(double x0, double y0, double x1, double y1, double t) {
        return new Point2D(lerp(x0, x1, t), lerp(y0, y1, t));
    }

    /**
     * Evaluates the given curve at the specified time.
     *
     * @param x0 point P0 of the curve
     * @param y0 point P0 of the curve
     * @param x1 point P1 of the curve
     * @param y1 point P1 of the curve
     * @param x2 point P2 of the curve
     * @param y2 point P2 of the curve
     * @param t  the time
     * @return the point at time t
     */
    @NonNull
    public static Point2D evalQuadCurve(double x0, double y0, double x1, double y1, double x2, double y2, double t) {
        final double x01, y01, x12, y12, x012, y012;
        x01 = lerp(x0, x1, t);
        y01 = lerp(y0, y1, t);

        x12 = lerp(x1, x2, t);
        y12 = lerp(y1, y2, t);

        x012 = lerp(x01, x12, t);
        y012 = lerp(y01, y12, t);

        return new Point2D(x012, y012);
    }

    /**
     * Tries to merge two bézier curves. Returns the new control point.
     *
     * @param x0        point P0 of the first curve
     * @param y0        point P0 of the first curve
     * @param x01       point P1 of the first curve
     * @param y01       point P1 of the first curve
     * @param x012      point P2 of the first curve
     * @param y012      point P2 of the first curve
     * @param x0123     point P3 of the first curve or point p0 of the second curve respectively
     * @param y0123     point P3 of the first curve or point p0 of the second curve respectively
     * @param x123      point P1 of the second curve
     * @param y123      point P1 of the second curve
     * @param x23       point P2 of the second curve
     * @param y23       point P2 of the second curve
     * @param x3        point P3 of the second curve
     * @param y3        point P3 of the second curve
     * @param tolerance distance (radius) at which the joined point may be off from x0123,y0123.
     * @return the control points of the new curve (x0,y0)(x1,y1)(x2,y2)(x3,y3), null if joining failed
     */
    @Nullable
    public static double[] mergeCubicCurve(final double x0, final double y0, final double x01, final double y01,
                                           final double x012, final double y012, final double x0123, final double y0123,
                                           final double x123, final double y123,
                                           final double x23, final double y23, final double x3, final double y3,
                                           double tolerance) {

        final double t = (x012 - x123 == 0) ? (y012 - y0123) / (y012 - y123) : (x012 - x0123) / (x012 - x123);
        final Point2D ctrl1, ctrl2;
        if (t == 0 || t == 1) {
            ctrl1 = new Point2D(x01, y01);
            ctrl2 = new Point2D(x23, y23);
        } else {
            ctrl1 = Geom.divide(new Point2D(x01, y01).subtract(x0, y0), t).add(x0, y0);
            ctrl2 = Geom.divide(new Point2D(x23, y23).subtract(x3, y3), 1 - t).add(x3, y3);
        }

        final Point2D joint0123 = evalCubicCurve(x0, y0, ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), x3, y3, t);

        return joint0123.distance(x0123, y0123) <= tolerance
                ? new double[]{x0, y0, ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), x3, y3} : null;
    }

    /**
     * Tries to join two bézier curves. Returns the new control point.
     *
     * @param x0        point P0 of the first curve
     * @param y0        point P0 of the first curve
     * @param x01       point P1 of the first curve
     * @param y01       point P1 of the first curve
     * @param x012      point P2 of the first curve or point p0 of the second curve respectively
     * @param y012      point P2 of the first curve or point p0 of the second curve respectively
     * @param x12       point P1 of the second curve
     * @param y12       point P1 of the second curve
     * @param x2        point P2 of the second curve
     * @param y2        point P2 of the second curve
     * @param tolerance distance (radius) at which the joined point may be off from x012,y012.
     * @return the control points of the new curve (x0,y0)(x1,y1)(x2,y2), null if joining failed
     */
    @Nullable
    public static double[] mergeQuadCurve(final double x0, final double y0, final double x01, final double y01,
                                          final double x012, final double y012, final double x12, final double y12, final double x2, final double y2,
                                          double tolerance) {
        final Point2D start = new Point2D(x0, y0);
        final Intersection isect = Intersections.intersectRayRay(start, new Point2D(x01, y01), new Point2D(x2, y2), new Point2D(x12, y12));
        if (isect.isEmpty()) {
            return null;
        }
        final Point2D ctrl = isect.getLastPoint();

        final double t = start.distance(x01, y01) / start.distance(ctrl);
        final Point2D joint01 = evalQuadCurve(x0, y0, ctrl.getX(), ctrl.getY(), x2, y2, t);

        return (joint01.distance(x012, y012) <= tolerance) ?
                new double[]{x0, y0, ctrl.getX(), ctrl.getY(), x2, y2} : null;
    }

    /**
     * Splits the provided bezier curve into two parts.
     * <p>
     * Reference:
     * <a href="https://stackoverflow.com/questions/8369488/splitting-a-bezier-curve">splitting-a-bezier-curve</a>.
     *
     * @param x0           point P0 of the curve
     * @param y0           point P0 of the curve
     * @param x1           point P1 of the curve
     * @param y1           point P1 of the curve
     * @param x2           point P2 of the curve
     * @param y2           point P2 of the curve
     * @param x3           point P3 of the curve
     * @param y3           point P3 of the curve
     * @param t            where to split
     * @param leftCurveTo  if not null, accepts the curve from x1,y1 to t
     * @param rightCurveTo if not null, accepts the curve from t to x4,y4
     */
    public static void splitCubicCurve(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
                                       double t,
                                       @Nullable Double6Consumer leftCurveTo,
                                       @Nullable Double6Consumer rightCurveTo) {
        final double x01, y01, x12, y12, x23, y23, x012, y012, x123, y123, x0123, y0123;
        x01 = lerp(x0, x1, t);
        y01 = lerp(y0, y1, t);
        x12 = lerp(x1, x2, t);
        y12 = lerp(y1, y2, t);
        x23 = lerp(x2, x3, t);
        y23 = lerp(y2, y3, t);
        x012 = lerp(x01, x12, t);
        y012 = lerp(y01, y12, t);
        x123 = lerp(x12, x23, t);
        y123 = lerp(y12, y23, t);
        x0123 = lerp(x012, x123, t);
        y0123 = lerp(y012, y123, t);

        if (leftCurveTo != null) {
            leftCurveTo.accept(x01, y01, x012, y012, x0123, y0123);
        }
        if (rightCurveTo != null) {
            rightCurveTo.accept(x123, y123, x23, y23, x3, y3);
        }
    }

    /**
     * Splits the provided bezier curve into two parts.
     * <p>
     * Reference:
     * <a href="https://stackoverflow.com/questions/8369488/splitting-a-bezier-curve">splitting-a-bezier-curve</a>.
     *
     * @param x0    point P0 of the curve
     * @param y0    point P0 of the curve
     * @param x1    point P1 of the curve
     * @param y1    point P1 of the curve
     * @param x2    point P2 of the curve
     * @param y2    point P2 of the curve
     * @param x3    point P3 of the curve
     * @param y3    point P3 of the curve
     * @param t     where to split
     * @param left  if not null, accepts the curve from x1,y1 to t
     * @param right if not null, accepts the curve from t to x4,y4
     */
    public static void splitCubicCurve(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, double t,
                                       double[] left,
                                       double[] right) {
        splitCubicCurve(x0, y0, x1, y1, x2, y2, x3, y3, t,
                left == null ? null : (a, b, c, d, e, f) -> {
                    left[0] = a;
                    left[1] = b;
                    left[2] = c;
                    left[3] = d;
                    left[4] = e;
                    left[5] = f;
                },
                right == null ? null : (a, b, c, d, e, f) -> {
                    right[0] = a;
                    right[1] = b;
                    right[2] = c;
                    right[3] = d;
                    right[4] = e;
                    right[5] = f;
                });

    }

    /**
     * Splits the provided line into two parts.
     *
     * @param x0          point 1 of the line
     * @param y0          point 1 of the line
     * @param x1          point 2 of the line
     * @param y1          point 2 of the line
     * @param t           where to split
     * @param leftLineTo  if not null, accepts the curve from x1,y1 to t
     * @param rightLineTo if not null, accepts the curve from t to x2,y2
     */
    public static void splitLine(double x0, double y0, double x1, double y1, double t,
                                 Double2Consumer leftLineTo,
                                 Double2Consumer rightLineTo) {
        Geom.splitLine(x0, y0, x1, y1, t, leftLineTo, rightLineTo);
    }

    public static void splitQuadCurve(double x0, double y0, double x1, double y1, double x2, double y2, double t,
                                      double[] left,
                                      double[] right) {
        splitQuadCurve(x0, y0, x1, y1, x2, y2, t,
                (a, b, c, d) -> {
                    left[0] = a;
                    left[1] = b;
                    left[2] = c;
                    left[3] = d;
                },
                (a, b, c, d) -> {
                    right[0] = a;
                    right[1] = b;
                    right[2] = c;
                    right[3] = d;
                });

    }

    /**
     * Splits the provided bezier curve into two parts.
     *
     * @param x0           point 1 of the curve
     * @param y0           point 1 of the curve
     * @param x1           point 2 of the curve
     * @param y1           point 2 of the curve
     * @param x2           point 3 of the curve
     * @param y2           point 3 of the curve
     * @param t            where to split
     * @param leftCurveTo  if not null, accepts the curve from x1,y1 to t
     * @param rightCurveTo if not null, accepts the curve from t to x3,y3
     */
    public static void splitQuadCurve(double x0, double y0, double x1, double y1, double x2, double y2, double t,
                                      @Nullable Double4Consumer leftCurveTo,
                                      @Nullable Double4Consumer rightCurveTo) {
        final double x01, y01, x12, y12, x012, y012;
        x01 = lerp(x0, x1, t);
        y01 = lerp(y0, y1, t);
        x12 = lerp(x1, x2, t);
        y12 = lerp(y1, y2, t);
        x012 = lerp(x01, x12, t);
        y012 = lerp(y01, y12, t);

        if (leftCurveTo != null) {
            leftCurveTo.accept(x01, y01, x012, y012);
        }
        if (rightCurveTo != null) {
            rightCurveTo.accept(x12, y12, x2, y2);
        }
    }

    /**
     * Extracts the specified segment from the given cubic curve.
     *
     * @param x0 point P0 of the curve
     * @param y0 point P0 of the curve
     * @param x1 point P1 of the curve
     * @param y1 point P1 of the curve
     * @param x2 point P2 of the curve
     * @param y2 point P2 of the curve
     * @param x3 point P3 of the curve
     * @param y3 point P3 of the curve
     * @param ta where to split
     * @param tb where to split
     */
    public static double[] segmentOfCubicCurve(double x0, double y0,
                                               double x1, double y1,
                                               double x2, double y2,
                                               double x3, double y3,
                                               double ta, double tb) {
        double[] left = new double[6];
        double[] right = new double[6];
        double tab = ta / tb;
        splitCubicCurve(x0, y0,
                x1, y1, x2, y2, x3, y3, tb, left, null);
        splitCubicCurve(x0, y0,
                left[0], left[1], left[2], left[3], left[4], left[5], tab, left, right);
        return new double[]{left[4], left[5],
                right[0], right[1], right[2], right[3], right[4], right[5]};
    }

    /**
     * Extracts the specified segment from the given quad curve.
     *
     * @param x0 point P0 of the curve
     * @param y0 point P0 of the curve
     * @param x1 point P1 of the curve
     * @param y1 point P1 of the curve
     * @param x2 point P2 of the curve
     * @param y2 point P2 of the curve
     * @param ta where to split
     * @param tb where to split
     */
    public static double[] segmentOfQuadCurve(double x0, double y0,
                                              double x1, double y1,
                                              double x2, double y2,
                                              double ta, double tb) {
        double[] left = new double[4];
        double[] right = new double[4];
        double tab = ta / tb;
        splitQuadCurve(x0, y0,
                x1, y1, x2, y2, tb, left, null);
        splitQuadCurve(x0, y0,
                left[0], left[1], left[2], left[3], tab, left, right);
        return new double[]{left[2], left[3],
                right[0], right[1], right[2], right[3]};
    }
}
