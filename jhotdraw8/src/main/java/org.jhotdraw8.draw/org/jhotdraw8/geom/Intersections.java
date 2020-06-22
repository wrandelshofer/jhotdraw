/*
 * @(#)Intersections.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.Intersection.Status;

import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.jhotdraw8.geom.Geom.argumentOnLine;
import static org.jhotdraw8.geom.Geom.lerp;

public class Intersections {

    /**
     * Values closer to zero than epsilon are treated as zero .
     * Machine precision for double is 2^-53.
     */
    private final static double EPSILON = 1.0 / (1L << 33);

    /**
     * Prevent instantiation.
     */
    private Intersections() {
    }

    @NonNull
    private static double[] addZeroAndOne(@NonNull double[] clampedRoots) {
        double[] roots = new double[clampedRoots.length + 2];
        int numRoots = 0;
        Arrays.sort(clampedRoots);
        if (clampedRoots.length == 0 || clampedRoots[0] > 0) {
            roots[numRoots++] = 0.0;
        }
        for (int i = 0; i < clampedRoots.length; i++) {
            roots[numRoots++] = clampedRoots[i];
        }
        if (clampedRoots.length == 0 || clampedRoots[clampedRoots.length - 1] < 1) {
            roots[numRoots++] = 1;
        }
        return Polynomial.trim(numRoots, roots);
    }

    /**
     * Constructs Bézout determinant polynomial given two polynomials e1 and e2.
     *
     * @param e1 polynomial e1 of degree 5
     * @param e2 polynomial e2 of degree 5
     * @return the Bézout determinant polynomial
     */
    @NonNull
    public static Polynomial bezout(double[] e1, double[] e2) {
        double AB = e1[0] * e2[1] - e2[0] * e1[1];
        double AC = e1[0] * e2[2] - e2[0] * e1[2];
        double AD = e1[0] * e2[3] - e2[0] * e1[3];
        double AE = e1[0] * e2[4] - e2[0] * e1[4];
        double AF = e1[0] * e2[5] - e2[0] * e1[5];
        double BC = e1[1] * e2[2] - e2[1] * e1[2];
        double BE = e1[1] * e2[4] - e2[1] * e1[4];
        double BF = e1[1] * e2[5] - e2[1] * e1[5];
        double CD = e1[2] * e2[3] - e2[2] * e1[3];
        double DE = e1[3] * e2[4] - e2[3] * e1[4];
        double DF = e1[3] * e2[5] - e2[3] * e1[5];
        double BFpDE = BF + DE;
        double BEmCD = BE - CD;

        return new Polynomial(
                AB * BC - AC * AC,
                AB * BEmCD + AD * BC - 2 * AC * AE,
                AB * BFpDE + AD * BEmCD - AE * AE - 2 * AC * AF,
                AB * DF + AD * BFpDE - 2 * AE * AF,
                AD * DF - AF * AF
        );

    }

    /**
     * Computes the coordinates of the bottom right corner of a rectangle given
     * two corner points defining the extrema of the rectangle.
     *
     * @param a corner point a
     * @param b corner point b
     * @return the bottom right corner
     */
    @NonNull
    private static Point2D bottomRight(@NonNull Point2D a, @NonNull Point2D b) {
        return new Point2D(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()));
    }

    @NonNull
    private static Point2D bottomRight(double ax, double ay, double bx, double by) {
        return new Point2D(Math.max(ax, bx), Math.max(ay, by));
    }

    /**
     * Returns true if point 'a' is greater or equal to point 'b'. Compares the
     * x-coordinates first, and if they are equal compares the y-coordinates.
     *
     * @param a point a
     * @param b point b
     * @return true if a is greater or equal b
     */
    private static boolean gte(@NonNull Point2D a, @NonNull Point2D b) {
        return a.getX() >= b.getX() && a.getY() >= b.getY();
    }

    @NonNull
    public static Intersection intersectQuadraticCurveQuadraticCurve(double ax0, double ay0, double ax1, double ay1, double ax2, double ay2,
                                                                     double bx0, double by0, double bx1, double by1, double bx2, double by2) {
        return intersectQuadraticCurveQuadraticCurve(new Point2D(ax0, ay0), new Point2D(ax1, ay1), new Point2D(ax2, ay2),
                new Point2D(bx0, by0), new Point2D(bx1, by1), new Point2D(bx2, by2));

    }

    /**
     * Computes the intersection between quadratic bezier curve 'a' and
     * quadratic bezier curve 'b'.
     * <p>
     * The intersection will contain the parameters 't1' of curve 'a' in range
     * [0,1].
     *
     * @param a0 control point P0 of 'a'
     * @param a1 control point P1 of 'a'
     * @param a2 control point P2 of 'a'
     * @param b0 control point P0 of 'b'
     * @param b1 control point P1 of 'b'
     * @param b2 control point P2 of 'b'
     * @return the computed result
     */
    @NonNull
    public static Intersection intersectQuadraticCurveQuadraticCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2, @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2) {
        final Point2D c12, c11, c10;
        final Point2D c22, c21, c20;
        final Polynomial poly;

        c12 = a0.add(a1.multiply(-2).add(a2));
        c11 = a0.multiply(-2).add(a1.multiply(2));
        c10 = a0;
        c22 = b0.add(b1.multiply(-2).add(b2));
        c21 = b0.multiply(-2).add(b1.multiply(2));
        c20 = b0;

        final double c12x, c10y, c20y, c11x, c11y, c22y, c21y, c21x, c22x, c10x, c20x, c12y;
        c12x = c12.getX();
        c10y = c10.getY();
        c20y = c20.getY();
        c11x = c11.getX();
        c11y = c11.getY();
        c22y = c22.getY();
        c21y = c21.getY();
        c21x = c21.getX();
        c22x = c22.getX();
        c10x = c10.getX();
        c20x = c20.getX();
        c12y = c12.getY();

        if (c12y == 0) {
            double v0 = c12x * (c10y - c20y);
            double v1 = v0 - c11x * c11y;
            double v3 = c11y * c11y;

            poly = new Polynomial(
                    c12x * c22y * c22y,
                    2 * c12x * c21y * c22y,
                    c12x * c21y * c21y - c22x * v3 - c22y * v0 - c22y * v1,
                    -c21x * v3 - c21y * v0 - c21y * v1,
                    (c10x - c20x) * v3 + (c10y - c20y) * v1
            );
        } else {
            double v0 = c12x * c22y - c12y * c22x;
            double v1 = c12x * c21y - c21x * c12y;
            double v2 = c11x * c12y - c11y * c12x;
            double v3 = c10y - c20y;
            double v4 = c12y * (c10x - c20x) - c12x * v3;
            double v5 = -c11y * v2 + c12y * v4;
            double v6 = v2 * v2;

            poly = new Polynomial(
                    v0 * v0,
                    2 * v0 * v1,
                    (-c22y * v6 + c12y * v1 * v1 + c12y * v0 * v4 + v0 * v5) / c12y,
                    (-c21y * v6 + c12y * v1 * v4 + v1 * v5) / c12y,
                    (v3 * v6 + v4 * v5) / c12y
            );
        }

        double[] roots = poly.getRoots();

        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        for (int i = 0; i < roots.length; i++) {
            double s = roots[i];

            if (0 <= s && s <= 1) {
                double[] xRoots = new Polynomial(
                        c12x, c11x,
                        c10x - c20x - s * c21x - s * s * c22x
                ).getRoots();
                double[] yRoots = new Polynomial(
                        c12y, c11y,
                        c10y - c20y - s * c21y - s * s * c22y
                ).getRoots();

                if (xRoots.length > 0 && yRoots.length > 0) {
                    double TOLERANCE = 1e-4;

                    checkRoots:
                    for (int j = 0; j < xRoots.length; j++) {
                        double xRoot = xRoots[j];

                        if (0 <= xRoot && xRoot <= 1) {
                            for (int k = 0; k < yRoots.length; k++) {
                                if (Math.abs(xRoot - yRoots[k]) < TOLERANCE) {
                                    result.add(new Intersection.IntersectionPoint(c22.multiply(s * s).add(c21.multiply(s).add(c20)), xRoot));
                                    break checkRoots;
                                }
                            }
                        }
                    }
                }
            }
        }

        return new Intersection(result);
    }

    @NonNull
    public static Intersection intersectQuadraticCurveCubicCurve(double ax0, double ay0, double ax1, double ay1, double ax2, double ay2,
                                                                 double bx0, double by0, double bx1, double by1, double bx2, double by2, double bx3, double by3) {
        return intersectQuadraticCurveCubicCurve(new Point2D(ax0, ay0), new Point2D(ax1, ay1), new Point2D(ax2, ay2),
                new Point2D(bx0, by0), new Point2D(bx1, by1), new Point2D(bx2, by2), new Point2D(bx3, by3));

    }

    /**
     * Computes the intersection between a quadratic bezier curve 'a' and cubic
     * bezier curve 'b'.
     * <p>
     * The intersection will contain the parameters 't1' of curve 'a' in range
     * [0,1].
     *
     * @param a0 control point P0 of 'a'
     * @param a1 control point P1 of 'a'
     * @param a2 control point P2 of 'a'
     * @param b0 control point P0 of 'b'
     * @param b1 control point P1 of 'b'
     * @param b2 control point P2 of 'b'
     * @param b3 control point P3 of 'b'
     * @return the computed result
     */
    @NonNull
    public static Intersection intersectQuadraticCurveCubicCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2,
                                                                 @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2, @NonNull Point2D b3) {
        final Point2D c12, c11, c10;
        final Point2D c23, c22, c21, c20;
        c12 = a0.add(a1.multiply(-2).add(a2));
        c11 = a0.multiply(-2).add(a1.multiply(2));
        c10 = new Point2D(a0.getX(), a0.getY());
        c23 = b0.multiply(-1).add(b1.multiply(3).add(b2.multiply(-3).add(b3)));
        c22 = b0.multiply(3).add(b1.multiply(-6).add(b2.multiply(3)));
        c21 = b0.multiply(-3).add(b1.multiply(3));
        c20 = b0;

        final double c10x, c10y, c11x, c11y, c12x, c12y, c20x, c20y, c21x, c21y, c22x, c22y, c23x, c23y;
        c10x = c10.getX();
        c10y = c10.getY();
        c11x = c11.getX();
        c11y = c11.getY();
        c12x = c12.getX();
        c12y = c12.getY();
        c20x = c20.getX();
        c20y = c20.getY();
        c21x = c21.getX();
        c21y = c21.getY();
        c22x = c22.getX();
        c22y = c22.getY();
        c23x = c23.getX();
        c23y = c23.getY();

        final double c10x2, c10y2, c11x2, c11y2, c12x2, c12y2;
        final double c20x2, c20y2, c21x2, c21y2, c22x2, c22y2, c23x2, c23y2;
        c10x2 = c10x * c10x;
        c10y2 = c10y * c10y;
        c11x2 = c11x * c11x;
        c11y2 = c11y * c11y;
        c12x2 = c12x * c12x;
        c12y2 = c12y * c12y;
        c20x2 = c20x * c20x;
        c20y2 = c20y * c20y;
        c21x2 = c21x * c21x;
        c21y2 = c21y * c21y;
        c22x2 = c22x * c22x;
        c22y2 = c22y * c22y;
        c23x2 = c23x * c23x;
        c23y2 = c23y * c23y;

        Polynomial poly = new Polynomial(
                -2 * c12x * c12y * c23x * c23y + c12x2 * c23y2 + c12y2 * c23x2,
                -2 * c12x * c12y * c22x * c23y - 2 * c12x * c12y * c22y * c23x + 2 * c12y2 * c22x * c23x
                        + 2 * c12x2 * c22y * c23y,
                -2 * c12x * c21x * c12y * c23y - 2 * c12x * c12y * c21y * c23x - 2 * c12x * c12y * c22x * c22y
                        + 2 * c21x * c12y2 * c23x + c12y2 * c22x2 + c12x2 * (2 * c21y * c23y + c22y2),
                2 * c10x * c12x * c12y * c23y + 2 * c10y * c12x * c12y * c23x + c11x * c11y * c12x * c23y
                        + c11x * c11y * c12y * c23x - 2 * c20x * c12x * c12y * c23y - 2 * c12x * c20y * c12y * c23x
                        - 2 * c12x * c21x * c12y * c22y - 2 * c12x * c12y * c21y * c22x - 2 * c10x * c12y2 * c23x
                        - 2 * c10y * c12x2 * c23y + 2 * c20x * c12y2 * c23x + 2 * c21x * c12y2 * c22x
                        - c11y2 * c12x * c23x - c11x2 * c12y * c23y + c12x2 * (2 * c20y * c23y + 2 * c21y * c22y),
                2 * c10x * c12x * c12y * c22y + 2 * c10y * c12x * c12y * c22x + c11x * c11y * c12x * c22y
                        + c11x * c11y * c12y * c22x - 2 * c20x * c12x * c12y * c22y - 2 * c12x * c20y * c12y * c22x
                        - 2 * c12x * c21x * c12y * c21y - 2 * c10x * c12y2 * c22x - 2 * c10y * c12x2 * c22y
                        + 2 * c20x * c12y2 * c22x - c11y2 * c12x * c22x - c11x2 * c12y * c22y + c21x2 * c12y2
                        + c12x2 * (2 * c20y * c22y + c21y2),
                2 * c10x * c12x * c12y * c21y + 2 * c10y * c12x * c21x * c12y + c11x * c11y * c12x * c21y
                        + c11x * c11y * c21x * c12y - 2 * c20x * c12x * c12y * c21y - 2 * c12x * c20y * c21x * c12y
                        - 2 * c10x * c21x * c12y2 - 2 * c10y * c12x2 * c21y + 2 * c20x * c21x * c12y2
                        - c11y2 * c12x * c21x - c11x2 * c12y * c21y + 2 * c12x2 * c20y * c21y,
                -2 * c10x * c10y * c12x * c12y - c10x * c11x * c11y * c12y - c10y * c11x * c11y * c12x
                        + 2 * c10x * c12x * c20y * c12y + 2 * c10y * c20x * c12x * c12y + c11x * c20x * c11y * c12y
                        + c11x * c11y * c12x * c20y - 2 * c20x * c12x * c20y * c12y - 2 * c10x * c20x * c12y2
                        + c10x * c11y2 * c12x + c10y * c11x2 * c12y - 2 * c10y * c12x2 * c20y
                        - c20x * c11y2 * c12x - c11x2 * c20y * c12y + c10x2 * c12y2 + c10y2 * c12x2
                        + c20x2 * c12y2 + c12x2 * c20y2
        );
        final double[] roots = poly.getRootsInInterval(0, 1);

        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        for (int i = 0; i < roots.length; i++) {
            double s = roots[i];
            double[] xRoots = new Polynomial(
                    c12x, c11x,
                    c10x - c20x - s * c21x - s * s * c22x - s * s * s * c23x
            ).getRoots();
            double[] yRoots = new Polynomial(
                    c12y, c11y,
                    c10y - c20y - s * c21y - s * s * c22y - s * s * s * c23y
            ).getRoots();

            if (xRoots.length > 0 && yRoots.length > 0) {
                double TOLERANCE = 1e-4;

                checkRoots:
                for (int j = 0; j < xRoots.length; j++) {
                    double xRoot = xRoots[j];

                    if (0 <= xRoot && xRoot <= 1) {
                        for (int k = 0; k < yRoots.length; k++) {
                            if (Math.abs(xRoot - yRoots[k]) < TOLERANCE) {
                                result.add(new Intersection.IntersectionPoint(c23.multiply(s * s * s).add(c22.multiply(s * s).add(c21.multiply(s).add(c20))), xRoot));
                                break checkRoots;
                            }
                        }
                    }
                }
            }
        }

        return new Intersection(result);
    }

    /**
     * Computes the intersection between quadratic bezier curve 'p' and the
     * given circle.
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P3 of 'p'
     * @param c  the center of the circle
     * @param r  the radius of the circle
     * @return the computed result
     */
    @NonNull
    public static Intersection intersectQuadraticCurveCircle(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D c, double r) {
        return Intersections.intersectQuadraticCurveEllipse(p0, p1, p2, c, r, r);
    }

    @NonNull
    public static Intersection intersectQuadraticCurveCircle(
            double x0, double y0, double x1, double y1, double x2, double y2,
            double cx, double cy, double r) {
        return Intersections.intersectQuadraticCurveEllipse(new Point2D(x0, y0), new Point2D(x1, y1), new Point2D(x2, y2), new Point2D(cx, cy), r, r);
    }

    @NonNull
    public static Intersection intersectQuadraticCurveEllipse(
            double x0, double y0, double x1, double y1, double x2, double y2,
            double cx, double cy, double rx, double ry) {
        return intersectQuadraticCurveEllipse(new Point2D(x0, y0), new Point2D(x1, y1), new Point2D(x2, y2), new Point2D(cx, cy), rx, ry);
    }

    /**
     * Computes the intersection between quadratic bezier curve 'p' and the
     * given ellipse.
     * <p>
     * The intersection will contain the parameters 't1' of curve 'p' in range
     * [0,1].
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P2 of 'p'
     * @param c  the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @return the computed result. Status can be{@link Status#INTERSECTION},
     * Status#NO_INTERSECTION_INSIDE or Status#NO_INTERSECTION_OUTSIDE}.
     */
    @NonNull
    public static Intersection intersectQuadraticCurveEllipse(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D c, double rx, double ry) {
        final Point2D c2, c1, c0; // coefficients of quadratic
        c2 = p0.add(p1.multiply(-2).add(p2));
        c1 = p0.multiply(-2).add(p1.multiply(2));
        c0 = new Point2D(p0.getX(), p0.getY());

        final double rxrx, ryry, cx2, cy2, cx1, cy1, cx0, cy0, ecx, ecy;
        rxrx = rx * rx;
        ryry = ry * ry;
        cx2 = c2.getX();
        cy2 = c2.getY();
        cx1 = c1.getX();
        cy1 = c1.getY();
        cx0 = c0.getX();
        cy0 = c0.getY();
        ecx = c.getX();
        ecy = c.getY();

        final double[] roots = new Polynomial(
                ryry * cx2 * cx2 + rxrx * cy2 * cy2,
                2 * (ryry * cx2 * cx1 + rxrx * cy2 * cy1),
                ryry * (2 * cx2 * cx0 + cx1 * cx1) + rxrx * (2 * cy2 * cy0 + cy1 * cy1)
                        - 2 * (ryry * ecx * cx2 + rxrx * ecy * cy2),
                2 * (ryry * cx1 * (cx0 - ecx) + rxrx * cy1 * (cy0 - ecy)),
                ryry * (cx0 * cx0 + ecx * ecx) + rxrx * (cy0 * cy0 + ecy * ecy)
                        - 2 * (ryry * ecx * cx0 + rxrx * ecy * cy0) - rxrx * ryry
        ).getRoots();

        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                result.add(new Intersection.IntersectionPoint(c2.multiply(t * t).add(c1.multiply(t).add(c0)), t));
            }
        }

        Intersection.Status status;
        if (result.size() > 0) {
            status = Intersection.Status.INTERSECTION;
        } else {
            return intersectPointEllipse(p0, c, rx, ry);
        }

        return new Intersection(status, result);
    }

    /**
     * Computes the intersection between quadratic bezier curve 'p' and the line
     * 'a'.
     * <p>
     * The intersection will contain the parameters 't1' of curve 'a' in range
     * [0,1].
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P2 of 'p'
     * @param a0 point 0 of 'a'
     * @param a1 point 1 of 'a'
     * @return the computed intersection
     */
    @NonNull
    public static Intersection intersectQuadraticCurveLine(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D a0, @NonNull Point2D a1) {

        // Bezier curve:
        //   (1 - t)²·p0 + 2·(1 - t)·t·p1 + t²·p2 , 0 ≤ t ≤ 1
        //   (p0 - 2·p1 + p2)·t² - 2·(p0 - p1)·t + p0
        //   c2·t² + c1·t + c0
        final Point2D c2, c1, c0;       // coefficients of quadratic
        c2 = p0.add(p1.multiply(-2).add(p2));
        c1 = p0.subtract(p1).multiply(-2);
        c0 = p0;

        final double a0x, a0y, a1x, a1y;
        a0x = a0.getX();
        a1x = a1.getX();
        a0y = a0.getY();
        a1y = a1.getY();

        // Convert line to normal form: a·x + b·y + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D n;                // normal for normal form of line
        n = new Point2D(a0y - a1y, a1x - a0x);

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = a0x * a1y - a1x * a0y;

        // Transform cubic coefficients to line's coordinate system and find roots
        // of cubic
        final double[] roots = new Polynomial(
                n.dotProduct(c2),
                n.dotProduct(c1),
                n.dotProduct(c0) + cl
        ).getRoots();

        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;
        final Point2D topLeft, bottomRight;
        topLeft = topLeft(a0, a1); // used to determine if point is on line segment
        bottomRight = bottomRight(a0, a1); // used to determine if point is on line segment
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D p4, p5, p6;
                p4 = lerp(p0, p1, t);
                p5 = lerp(p1, p2, t);
                p6 = lerp(p4, p5, t);

                // See if point is on line segment
                // Had to make special cases for vertical and horizontal lines due
                // to slight errors in calculation of p6
                if (a0x == a1x) {
                    if (topLeft.getY() <= p6.getY() && p6.getY() <= bottomRight.getY()) {
                        status = Intersection.Status.INTERSECTION;
                        result.add(new Intersection.IntersectionPoint(p6, t));
                    }
                } else if (a0y == a1y) {
                    if (topLeft.getX() <= p6.getX() && p6.getX() <= bottomRight.getX()) {
                        status = Intersection.Status.INTERSECTION;
                        result.add(new Intersection.IntersectionPoint(p6, t));
                    }
                } else if (gte(p6, topLeft) && lte(p6, bottomRight)) {
                    status = Intersection.Status.INTERSECTION;
                    result.add(new Intersection.IntersectionPoint(p6, t));
                }
            }
        }

        return new Intersection(status, result);
    }

    @NonNull
    public static Intersection intersectQuadraticCurveLine(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2,
            double bx0, double by0, double bx1, double by1) {
        return intersectQuadraticCurveLine(new Point2D(ax0, ay0), new Point2D(ax1, ay1), new Point2D(ax2, ay2),
                new Point2D(bx0, by0), new Point2D(bx1, by1));
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
    @NonNull
    public static Intersection intersectQuadraticCurvePoint(
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
        final List<Intersection.IntersectionPoint> result = new ArrayList<>();
        final Point2D p1, p2, p3;
        p1 = new Point2D(x0, y0);
        p2 = new Point2D(x1, y1);
        p3 = new Point2D(x2, y2);
        final double rr = r * r;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (double tt : roots) {
            final Point2D p;
            final double t;
            if (tt < 0) {
                p = p1;
            } else if (tt > 1) {
                p = p3;
            } else {
                t = tt;
                p = p1.multiply((1 - t) * (1 - t))
                        .add(p2.multiply(2 * (1 - t) * t))
                        .add(p3.multiply(t * t));
            }

            double dd = (p.getX() - cx) * (p.getX() - cx) + (p.getY() - cy) * (p.getY() - cy);
            if (dd < rr) {
                if (abs(dd - bestDistance) < EPSILON) {
                    result.add(new Intersection.IntersectionPoint(p, tt));
                } else if (dd < bestDistance) {
                    bestDistance = dd;
                    result.clear();
                    result.add(new Intersection.IntersectionPoint(p, tt));
                }
            }
        }

        return new Intersection(result);
    }

    /**
     * Computes the intersection between quadratic bezier curve 'p' and the
     * given closed polygon.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range
     * [0,1].
     *
     * @param p0     control point P0 of 'p'
     * @param p1     control point P1 of 'p'
     * @param p2     control point P2 of 'p'
     * @param points the points of the polygon
     * @return the computed intersection
     */
    @NonNull
    public static Intersection intersectQuadraticCurvePolygon(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull List<Point2D> points) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        int length = points.size();

        for (int i = 0; i < length; i++) {
            final Point2D a0, a1;
            a0 = points.get(i);
            a1 = points.get((i + 1) % length);
            Intersection inter = Intersections.intersectQuadraticCurveLine(p0, p1, p2, a0, a1);

            result.addAll(inter.getIntersections());
        }

        return new Intersection(result);
    }

    /**
     * Computes the intersection between quadratic bezier curve 'p' and the
     * provided rectangle.
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P2 of 'p'
     * @param r0 corner point 0 of the rectangle
     * @param r1 corner point 1 of the rectangle
     * @return the computed intersection
     */
    @NonNull
    public static Intersection intersectQuadraticCurveRectangle(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D r0, @NonNull Point2D r1) {
        final Point2D topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(r0, r1);
        bottomRight = bottomRight(r0, r1);
        topRight = new Point2D(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D(topLeft.getX(), bottomRight.getY());

        final Intersection inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectQuadraticCurveLine(p0, p1, p2, topLeft, topRight);
        inter2 = Intersections.intersectQuadraticCurveLine(p0, p1, p2, topRight, bottomRight);
        inter3 = Intersections.intersectQuadraticCurveLine(p0, p1, p2, bottomRight, bottomLeft);
        inter4 = Intersections.intersectQuadraticCurveLine(p0, p1, p2, bottomLeft, topLeft);

        final List<Intersection.IntersectionPoint> result = new ArrayList<>();
        result.addAll(inter1.getIntersections());
        result.addAll(inter2.getIntersections());
        result.addAll(inter3.getIntersections());
        result.addAll(inter4.getIntersections());

        return new Intersection(result);
    }

    @NonNull
    public static Intersection intersectCubicCurveQuadraticCurve(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3,
            double bx0, double by0, double bx1, double by1, double bx2, double by2) {
        Intersection isect = intersectQuadraticCurveCubicCurve(
                new Point2D(bx0, by0), new Point2D(bx1, by1), new Point2D(bx2, by2),
                new Point2D(ax0, ay0), new Point2D(ax1, ay1), new Point2D(ax2, ay2),
                new Point2D(ax3, ay3));
        // FIXME compute t for a instead for b
        return isect;

    }

    @NonNull
    public static Intersection intersectCubicCurveCubicCurve(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3,
            double bx0, double by0, double bx1, double by1, double bx2, double by2, double bx3, double by3) {
        return intersectCubicCurveCubicCurve(new Point2D(ax0, ay0), new Point2D(ax1, ay1), new Point2D(ax2, ay2), new Point2D(ax3, ay3),
                new Point2D(bx0, by0), new Point2D(bx1, by1), new Point2D(bx2, by2), new Point2D(bx3, by3));

    }

    @NonNull
    public static Intersection intersectCubicCurveCubicCurve(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3,
            double bx0, double by0, double bx1, double by1, double bx2, double by2, double bx3, double by3,
            double tMin, double tMax) {
        return intersectCubicCurveCubicCurve(new Point2D(ax0, ay0), new Point2D(ax1, ay1), new Point2D(ax2, ay2), new Point2D(ax3, ay3),
                new Point2D(bx0, by0), new Point2D(bx1, by1), new Point2D(bx2, by2), new Point2D(bx3, by3),
                tMin, tMax);

    }

    /**
     * Computes the intersection between cubic bezier curve 'a' and cubic bezier
     * curve 'b'.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range
     * [0,1].
     *
     * @param a0 control point P0 of 'a'
     * @param a1 control point P1 of 'a'
     * @param a2 control point P2 of 'a'
     * @param a3 control point P3 of 'a'
     * @param b0 control point P0 of 'b'
     * @param b1 control point P1 of 'b'
     * @param b2 control point P2 of 'b'
     * @param b3 control point P3 of 'b'
     * @return the computed result
     */
    @NonNull
    public static Intersection intersectCubicCurveCubicCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2, @NonNull Point2D a3,
                                                             @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2, @NonNull Point2D b3) {
        return intersectCubicCurveCubicCurve(a0, a1, a2, a3, b0, b1, b2, b3, 0, 1);
    }

    /**
     * Computes the intersection between cubic bezier curve 'a' and cubic bezier
     * curve 'b'.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range
     * [tMin,tMax].
     *
     * @param a0   control point P0 of 'a'
     * @param a1   control point P1 of 'a'
     * @param a2   control point P2 of 'a'
     * @param a3   control point P3 of 'a'
     * @param b0   control point P0 of 'b'
     * @param b1   control point P1 of 'b'
     * @param b2   control point P2 of 'b'
     * @param b3   control point P3 of 'b'
     * @param tMin minimal value for t
     * @param tMax maximal value for t
     * @return the computed result
     */
    @NonNull
    public static Intersection intersectCubicCurveCubicCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2, @NonNull Point2D a3,
                                                             @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2, @NonNull Point2D b3, double tMin, double tMax) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        // Calculate the coefficients of cubic polynomial
        final Point2D c13, c12, c11, c10;
        final Point2D c23, c22, c21, c20;
        c13 = a0.multiply(-1).add(a1.multiply(3).add(a2.multiply(-3).add(a3)));
        c12 = a0.multiply(3).add(a1.multiply(-6).add(a2.multiply(3)));
        c11 = a0.multiply(-3).add(a1.multiply(3));
        c10 = a0;
        c23 = b0.multiply(-1).add(b1.multiply(3).add(b2.multiply(-3).add(b3)));
        c22 = b0.multiply(3).add(b1.multiply(-6).add(b2.multiply(3)));
        c21 = b0.multiply(-3).add(b1.multiply(3));
        c20 = b0;

        final double c10x, c10y, c11x, c11y, c12x, c12y, c13x, c13y, c20x, c20y, c21x, c21y, c22x, c22y, c23y, c23x;
        c10x = c10.getX();
        c10y = c10.getY();
        c11x = c11.getX();
        c11y = c11.getY();
        c12x = c12.getX();
        c12y = c12.getY();
        c13x = c13.getX();
        c13y = c13.getY();
        c20x = c20.getX();
        c20y = c20.getY();
        c21x = c21.getX();
        c21y = c21.getY();
        c22x = c22.getX();
        c22y = c22.getY();
        c23y = c23.getY();
        c23x = c23.getX();

        final double c10x2, c10x3, c10y2, c10y3, c11x2, c11x3, c11y2, c11y3, c12x2, c12x3, c12y2, c12y3, c13x2, c13x3, c13y2, c13y3, c20x2, c20x3, c20y2, c20y3, c21x2, c21x3, c21y2, c22x2, c22x3, c22y2, c23x2, c23x3, c23y2, c23y3;
        c10x2 = c10x * c10x;
        c10x3 = c10x * c10x * c10x;
        c10y2 = c10y * c10y;
        c10y3 = c10y * c10y * c10y;
        c11x2 = c11x * c11x;
        c11x3 = c11x * c11x * c11x;
        c11y2 = c11y * c11y;
        c11y3 = c11y * c11y * c11y;
        c12x2 = c12x * c12x;
        c12x3 = c12x * c12x * c12x;
        c12y2 = c12y * c12y;
        c12y3 = c12y * c12y * c12y;
        c13x2 = c13x * c13x;
        c13x3 = c13x * c13x * c13x;
        c13y2 = c13y * c13y;
        c13y3 = c13y * c13y * c13y;
        c20x2 = c20x * c20x;
        c20x3 = c20x * c20x * c20x;
        c20y2 = c20y * c20y;
        c20y3 = c20y * c20y * c20y;
        c21x2 = c21x * c21x;
        c21x3 = c21x * c21x * c21x;
        c21y2 = c21y * c21y;
        c22x2 = c22x * c22x;
        c22x3 = c22x * c22x * c22x;
        c22y2 = c22y * c22y;
        c23x2 = c23x * c23x;
        c23x3 = c23x * c23x * c23x;
        c23y2 = c23y * c23y;
        c23y3 = c23y * c23y * c23y;

        Polynomial poly = new Polynomial(
                -c13x3 * c23y3 + c13y3 * c23x3 - 3 * c13x * c13y2 * c23x2 * c23y
                        + 3 * c13x2 * c13y * c23x * c23y2,
                -6 * c13x * c22x * c13y2 * c23x * c23y + 6 * c13x2 * c13y * c22y * c23x * c23y + 3 * c22x * c13y3 * c23x2
                        - 3 * c13x3 * c22y * c23y2 - 3 * c13x * c13y2 * c22y * c23x2 + 3 * c13x2 * c22x * c13y * c23y2,
                -6 * c21x * c13x * c13y2 * c23x * c23y - 6 * c13x * c22x * c13y2 * c22y * c23x + 6 * c13x2 * c22x * c13y * c22y * c23y
                        + 3 * c21x * c13y3 * c23x2 + 3 * c22x2 * c13y3 * c23x + 3 * c21x * c13x2 * c13y * c23y2 - 3 * c13x * c21y * c13y2 * c23x2
                        - 3 * c13x * c22x2 * c13y2 * c23y + c13x2 * c13y * c23x * (6 * c21y * c23y + 3 * c22y2) + c13x3 * (-c21y * c23y2
                        - 2 * c22y2 * c23y - c23y * (2 * c21y * c23y + c22y2)),
                c11x * c12y * c13x * c13y * c23x * c23y - c11y * c12x * c13x * c13y * c23x * c23y + 6 * c21x * c22x * c13y3 * c23x
                        + 3 * c11x * c12x * c13x * c13y * c23y2 + 6 * c10x * c13x * c13y2 * c23x * c23y - 3 * c11x * c12x * c13y2 * c23x * c23y
                        - 3 * c11y * c12y * c13x * c13y * c23x2 - 6 * c10y * c13x2 * c13y * c23x * c23y - 6 * c20x * c13x * c13y2 * c23x * c23y
                        + 3 * c11y * c12y * c13x2 * c23x * c23y - 2 * c12x * c12y2 * c13x * c23x * c23y - 6 * c21x * c13x * c22x * c13y2 * c23y
                        - 6 * c21x * c13x * c13y2 * c22y * c23x - 6 * c13x * c21y * c22x * c13y2 * c23x + 6 * c21x * c13x2 * c13y * c22y * c23y
                        + 2 * c12x2 * c12y * c13y * c23x * c23y + c22x3 * c13y3 - 3 * c10x * c13y3 * c23x2 + 3 * c10y * c13x3 * c23y2
                        + 3 * c20x * c13y3 * c23x2 + c12y3 * c13x * c23x2 - c12x3 * c13y * c23y2 - 3 * c10x * c13x2 * c13y * c23y2
                        + 3 * c10y * c13x * c13y2 * c23x2 - 2 * c11x * c12y * c13x2 * c23y2 + c11x * c12y * c13y2 * c23x2 - c11y * c12x * c13x2 * c23y2
                        + 2 * c11y * c12x * c13y2 * c23x2 + 3 * c20x * c13x2 * c13y * c23y2 - c12x * c12y2 * c13y * c23x2
                        - 3 * c20y * c13x * c13y2 * c23x2 + c12x2 * c12y * c13x * c23y2 - 3 * c13x * c22x2 * c13y2 * c22y
                        + c13x2 * c13y * c23x * (6 * c20y * c23y + 6 * c21y * c22y) + c13x2 * c22x * c13y * (6 * c21y * c23y + 3 * c22y2)
                        + c13x3 * (-2 * c21y * c22y * c23y - c20y * c23y2 - c22y * (2 * c21y * c23y + c22y2) - c23y * (2 * c20y * c23y + 2 * c21y * c22y)),
                6 * c11x * c12x * c13x * c13y * c22y * c23y + c11x * c12y * c13x * c22x * c13y * c23y + c11x * c12y * c13x * c13y * c22y * c23x
                        - c11y * c12x * c13x * c22x * c13y * c23y - c11y * c12x * c13x * c13y * c22y * c23x - 6 * c11y * c12y * c13x * c22x * c13y * c23x
                        - 6 * c10x * c22x * c13y3 * c23x + 6 * c20x * c22x * c13y3 * c23x + 6 * c10y * c13x3 * c22y * c23y + 2 * c12y3 * c13x * c22x * c23x
                        - 2 * c12x3 * c13y * c22y * c23y + 6 * c10x * c13x * c22x * c13y2 * c23y + 6 * c10x * c13x * c13y2 * c22y * c23x
                        + 6 * c10y * c13x * c22x * c13y2 * c23x - 3 * c11x * c12x * c22x * c13y2 * c23y - 3 * c11x * c12x * c13y2 * c22y * c23x
                        + 2 * c11x * c12y * c22x * c13y2 * c23x + 4 * c11y * c12x * c22x * c13y2 * c23x - 6 * c10x * c13x2 * c13y * c22y * c23y
                        - 6 * c10y * c13x2 * c22x * c13y * c23y - 6 * c10y * c13x2 * c13y * c22y * c23x - 4 * c11x * c12y * c13x2 * c22y * c23y
                        - 6 * c20x * c13x * c22x * c13y2 * c23y - 6 * c20x * c13x * c13y2 * c22y * c23x - 2 * c11y * c12x * c13x2 * c22y * c23y
                        + 3 * c11y * c12y * c13x2 * c22x * c23y + 3 * c11y * c12y * c13x2 * c22y * c23x - 2 * c12x * c12y2 * c13x * c22x * c23y
                        - 2 * c12x * c12y2 * c13x * c22y * c23x - 2 * c12x * c12y2 * c22x * c13y * c23x - 6 * c20y * c13x * c22x * c13y2 * c23x
                        - 6 * c21x * c13x * c21y * c13y2 * c23x - 6 * c21x * c13x * c22x * c13y2 * c22y + 6 * c20x * c13x2 * c13y * c22y * c23y
                        + 2 * c12x2 * c12y * c13x * c22y * c23y + 2 * c12x2 * c12y * c22x * c13y * c23y + 2 * c12x2 * c12y * c13y * c22y * c23x
                        + 3 * c21x * c22x2 * c13y3 + 3 * c21x2 * c13y3 * c23x - 3 * c13x * c21y * c22x2 * c13y2 - 3 * c21x2 * c13x * c13y2 * c23y
                        + c13x2 * c22x * c13y * (6 * c20y * c23y + 6 * c21y * c22y) + c13x2 * c13y * c23x * (6 * c20y * c22y + 3 * c21y2)
                        + c21x * c13x2 * c13y * (6 * c21y * c23y + 3 * c22y2) + c13x3 * (-2 * c20y * c22y * c23y - c23y * (2 * c20y * c22y + c21y2)
                        - c21y * (2 * c21y * c23y + c22y2) - c22y * (2 * c20y * c23y + 2 * c21y * c22y)),
                c11x * c21x * c12y * c13x * c13y * c23y + c11x * c12y * c13x * c21y * c13y * c23x + c11x * c12y * c13x * c22x * c13y * c22y
                        - c11y * c12x * c21x * c13x * c13y * c23y - c11y * c12x * c13x * c21y * c13y * c23x - c11y * c12x * c13x * c22x * c13y * c22y
                        - 6 * c11y * c21x * c12y * c13x * c13y * c23x - 6 * c10x * c21x * c13y3 * c23x + 6 * c20x * c21x * c13y3 * c23x
                        + 2 * c21x * c12y3 * c13x * c23x + 6 * c10x * c21x * c13x * c13y2 * c23y + 6 * c10x * c13x * c21y * c13y2 * c23x
                        + 6 * c10x * c13x * c22x * c13y2 * c22y + 6 * c10y * c21x * c13x * c13y2 * c23x - 3 * c11x * c12x * c21x * c13y2 * c23y
                        - 3 * c11x * c12x * c21y * c13y2 * c23x - 3 * c11x * c12x * c22x * c13y2 * c22y + 2 * c11x * c21x * c12y * c13y2 * c23x
                        + 4 * c11y * c12x * c21x * c13y2 * c23x - 6 * c10y * c21x * c13x2 * c13y * c23y - 6 * c10y * c13x2 * c21y * c13y * c23x
                        - 6 * c10y * c13x2 * c22x * c13y * c22y - 6 * c20x * c21x * c13x * c13y2 * c23y - 6 * c20x * c13x * c21y * c13y2 * c23x
                        - 6 * c20x * c13x * c22x * c13y2 * c22y + 3 * c11y * c21x * c12y * c13x2 * c23y - 3 * c11y * c12y * c13x * c22x2 * c13y
                        + 3 * c11y * c12y * c13x2 * c21y * c23x + 3 * c11y * c12y * c13x2 * c22x * c22y - 2 * c12x * c21x * c12y2 * c13x * c23y
                        - 2 * c12x * c21x * c12y2 * c13y * c23x - 2 * c12x * c12y2 * c13x * c21y * c23x - 2 * c12x * c12y2 * c13x * c22x * c22y
                        - 6 * c20y * c21x * c13x * c13y2 * c23x - 6 * c21x * c13x * c21y * c22x * c13y2 + 6 * c20y * c13x2 * c21y * c13y * c23x
                        + 2 * c12x2 * c21x * c12y * c13y * c23y + 2 * c12x2 * c12y * c21y * c13y * c23x + 2 * c12x2 * c12y * c22x * c13y * c22y
                        - 3 * c10x * c22x2 * c13y3 + 3 * c20x * c22x2 * c13y3 + 3 * c21x2 * c22x * c13y3 + c12y3 * c13x * c22x2
                        + 3 * c10y * c13x * c22x2 * c13y2 + c11x * c12y * c22x2 * c13y2 + 2 * c11y * c12x * c22x2 * c13y2
                        - c12x * c12y2 * c22x2 * c13y - 3 * c20y * c13x * c22x2 * c13y2 - 3 * c21x2 * c13x * c13y2 * c22y
                        + c12x2 * c12y * c13x * (2 * c21y * c23y + c22y2) + c11x * c12x * c13x * c13y * (6 * c21y * c23y + 3 * c22y2)
                        + c21x * c13x2 * c13y * (6 * c20y * c23y + 6 * c21y * c22y) + c12x3 * c13y * (-2 * c21y * c23y - c22y2)
                        + c10y * c13x3 * (6 * c21y * c23y + 3 * c22y2) + c11y * c12x * c13x2 * (-2 * c21y * c23y - c22y2)
                        + c11x * c12y * c13x2 * (-4 * c21y * c23y - 2 * c22y2) + c10x * c13x2 * c13y * (-6 * c21y * c23y - 3 * c22y2)
                        + c13x2 * c22x * c13y * (6 * c20y * c22y + 3 * c21y2) + c20x * c13x2 * c13y * (6 * c21y * c23y + 3 * c22y2)
                        + c13x3 * (-2 * c20y * c21y * c23y - c22y * (2 * c20y * c22y + c21y2) - c20y * (2 * c21y * c23y + c22y2)
                        - c21y * (2 * c20y * c23y + 2 * c21y * c22y)),
                -c10x * c11x * c12y * c13x * c13y * c23y + c10x * c11y * c12x * c13x * c13y * c23y + 6 * c10x * c11y * c12y * c13x * c13y * c23x
                        - 6 * c10y * c11x * c12x * c13x * c13y * c23y - c10y * c11x * c12y * c13x * c13y * c23x + c10y * c11y * c12x * c13x * c13y * c23x
                        + c11x * c11y * c12x * c12y * c13x * c23y - c11x * c11y * c12x * c12y * c13y * c23x + c11x * c20x * c12y * c13x * c13y * c23y
                        + c11x * c20y * c12y * c13x * c13y * c23x + c11x * c21x * c12y * c13x * c13y * c22y + c11x * c12y * c13x * c21y * c22x * c13y
                        - c20x * c11y * c12x * c13x * c13y * c23y - 6 * c20x * c11y * c12y * c13x * c13y * c23x - c11y * c12x * c20y * c13x * c13y * c23x
                        - c11y * c12x * c21x * c13x * c13y * c22y - c11y * c12x * c13x * c21y * c22x * c13y - 6 * c11y * c21x * c12y * c13x * c22x * c13y
                        - 6 * c10x * c20x * c13y3 * c23x - 6 * c10x * c21x * c22x * c13y3 - 2 * c10x * c12y3 * c13x * c23x + 6 * c20x * c21x * c22x * c13y3
                        + 2 * c20x * c12y3 * c13x * c23x + 2 * c21x * c12y3 * c13x * c22x + 2 * c10y * c12x3 * c13y * c23y - 6 * c10x * c10y * c13x * c13y2 * c23x
                        + 3 * c10x * c11x * c12x * c13y2 * c23y - 2 * c10x * c11x * c12y * c13y2 * c23x - 4 * c10x * c11y * c12x * c13y2 * c23x
                        + 3 * c10y * c11x * c12x * c13y2 * c23x + 6 * c10x * c10y * c13x2 * c13y * c23y + 6 * c10x * c20x * c13x * c13y2 * c23y
                        - 3 * c10x * c11y * c12y * c13x2 * c23y + 2 * c10x * c12x * c12y2 * c13x * c23y + 2 * c10x * c12x * c12y2 * c13y * c23x
                        + 6 * c10x * c20y * c13x * c13y2 * c23x + 6 * c10x * c21x * c13x * c13y2 * c22y + 6 * c10x * c13x * c21y * c22x * c13y2
                        + 4 * c10y * c11x * c12y * c13x2 * c23y + 6 * c10y * c20x * c13x * c13y2 * c23x + 2 * c10y * c11y * c12x * c13x2 * c23y
                        - 3 * c10y * c11y * c12y * c13x2 * c23x + 2 * c10y * c12x * c12y2 * c13x * c23x + 6 * c10y * c21x * c13x * c22x * c13y2
                        - 3 * c11x * c20x * c12x * c13y2 * c23y + 2 * c11x * c20x * c12y * c13y2 * c23x + c11x * c11y * c12y2 * c13x * c23x
                        - 3 * c11x * c12x * c20y * c13y2 * c23x - 3 * c11x * c12x * c21x * c13y2 * c22y - 3 * c11x * c12x * c21y * c22x * c13y2
                        + 2 * c11x * c21x * c12y * c22x * c13y2 + 4 * c20x * c11y * c12x * c13y2 * c23x + 4 * c11y * c12x * c21x * c22x * c13y2
                        - 2 * c10x * c12x2 * c12y * c13y * c23y - 6 * c10y * c20x * c13x2 * c13y * c23y - 6 * c10y * c20y * c13x2 * c13y * c23x
                        - 6 * c10y * c21x * c13x2 * c13y * c22y - 2 * c10y * c12x2 * c12y * c13x * c23y - 2 * c10y * c12x2 * c12y * c13y * c23x
                        - 6 * c10y * c13x2 * c21y * c22x * c13y - c11x * c11y * c12x2 * c13y * c23y - 2 * c11x * c11y2 * c13x * c13y * c23x
                        + 3 * c20x * c11y * c12y * c13x2 * c23y - 2 * c20x * c12x * c12y2 * c13x * c23y - 2 * c20x * c12x * c12y2 * c13y * c23x
                        - 6 * c20x * c20y * c13x * c13y2 * c23x - 6 * c20x * c21x * c13x * c13y2 * c22y - 6 * c20x * c13x * c21y * c22x * c13y2
                        + 3 * c11y * c20y * c12y * c13x2 * c23x + 3 * c11y * c21x * c12y * c13x2 * c22y + 3 * c11y * c12y * c13x2 * c21y * c22x
                        - 2 * c12x * c20y * c12y2 * c13x * c23x - 2 * c12x * c21x * c12y2 * c13x * c22y - 2 * c12x * c21x * c12y2 * c22x * c13y
                        - 2 * c12x * c12y2 * c13x * c21y * c22x - 6 * c20y * c21x * c13x * c22x * c13y2 - c11y2 * c12x * c12y * c13x * c23x
                        + 2 * c20x * c12x2 * c12y * c13y * c23y + 6 * c20y * c13x2 * c21y * c22x * c13y + 2 * c11x2 * c11y * c13x * c13y * c23y
                        + c11x2 * c12x * c12y * c13y * c23y + 2 * c12x2 * c20y * c12y * c13y * c23x + 2 * c12x2 * c21x * c12y * c13y * c22y
                        + 2 * c12x2 * c12y * c21y * c22x * c13y + c21x3 * c13y3 + 3 * c10x2 * c13y3 * c23x - 3 * c10y2 * c13x3 * c23y
                        + 3 * c20x2 * c13y3 * c23x + c11y3 * c13x2 * c23x - c11x3 * c13y2 * c23y - c11x * c11y2 * c13x2 * c23y
                        + c11x2 * c11y * c13y2 * c23x - 3 * c10x2 * c13x * c13y2 * c23y + 3 * c10y2 * c13x2 * c13y * c23x - c11x2 * c12y2 * c13x * c23y
                        + c11y2 * c12x2 * c13y * c23x - 3 * c21x2 * c13x * c21y * c13y2 - 3 * c20x2 * c13x * c13y2 * c23y + 3 * c20y2 * c13x2 * c13y * c23x
                        + c11x * c12x * c13x * c13y * (6 * c20y * c23y + 6 * c21y * c22y) + c12x3 * c13y * (-2 * c20y * c23y - 2 * c21y * c22y)
                        + c10y * c13x3 * (6 * c20y * c23y + 6 * c21y * c22y) + c11y * c12x * c13x2 * (-2 * c20y * c23y - 2 * c21y * c22y)
                        + c12x2 * c12y * c13x * (2 * c20y * c23y + 2 * c21y * c22y) + c11x * c12y * c13x2 * (-4 * c20y * c23y - 4 * c21y * c22y)
                        + c10x * c13x2 * c13y * (-6 * c20y * c23y - 6 * c21y * c22y) + c20x * c13x2 * c13y * (6 * c20y * c23y + 6 * c21y * c22y)
                        + c21x * c13x2 * c13y * (6 * c20y * c22y + 3 * c21y2) + c13x3 * (-2 * c20y * c21y * c22y - c20y2 * c23y
                        - c21y * (2 * c20y * c22y + c21y2) - c20y * (2 * c20y * c23y + 2 * c21y * c22y)),
                -c10x * c11x * c12y * c13x * c13y * c22y + c10x * c11y * c12x * c13x * c13y * c22y + 6 * c10x * c11y * c12y * c13x * c22x * c13y
                        - 6 * c10y * c11x * c12x * c13x * c13y * c22y - c10y * c11x * c12y * c13x * c22x * c13y + c10y * c11y * c12x * c13x * c22x * c13y
                        + c11x * c11y * c12x * c12y * c13x * c22y - c11x * c11y * c12x * c12y * c22x * c13y + c11x * c20x * c12y * c13x * c13y * c22y
                        + c11x * c20y * c12y * c13x * c22x * c13y + c11x * c21x * c12y * c13x * c21y * c13y - c20x * c11y * c12x * c13x * c13y * c22y
                        - 6 * c20x * c11y * c12y * c13x * c22x * c13y - c11y * c12x * c20y * c13x * c22x * c13y - c11y * c12x * c21x * c13x * c21y * c13y
                        - 6 * c10x * c20x * c22x * c13y3 - 2 * c10x * c12y3 * c13x * c22x + 2 * c20x * c12y3 * c13x * c22x + 2 * c10y * c12x3 * c13y * c22y
                        - 6 * c10x * c10y * c13x * c22x * c13y2 + 3 * c10x * c11x * c12x * c13y2 * c22y - 2 * c10x * c11x * c12y * c22x * c13y2
                        - 4 * c10x * c11y * c12x * c22x * c13y2 + 3 * c10y * c11x * c12x * c22x * c13y2 + 6 * c10x * c10y * c13x2 * c13y * c22y
                        + 6 * c10x * c20x * c13x * c13y2 * c22y - 3 * c10x * c11y * c12y * c13x2 * c22y + 2 * c10x * c12x * c12y2 * c13x * c22y
                        + 2 * c10x * c12x * c12y2 * c22x * c13y + 6 * c10x * c20y * c13x * c22x * c13y2 + 6 * c10x * c21x * c13x * c21y * c13y2
                        + 4 * c10y * c11x * c12y * c13x2 * c22y + 6 * c10y * c20x * c13x * c22x * c13y2 + 2 * c10y * c11y * c12x * c13x2 * c22y
                        - 3 * c10y * c11y * c12y * c13x2 * c22x + 2 * c10y * c12x * c12y2 * c13x * c22x - 3 * c11x * c20x * c12x * c13y2 * c22y
                        + 2 * c11x * c20x * c12y * c22x * c13y2 + c11x * c11y * c12y2 * c13x * c22x - 3 * c11x * c12x * c20y * c22x * c13y2
                        - 3 * c11x * c12x * c21x * c21y * c13y2 + 4 * c20x * c11y * c12x * c22x * c13y2 - 2 * c10x * c12x2 * c12y * c13y * c22y
                        - 6 * c10y * c20x * c13x2 * c13y * c22y - 6 * c10y * c20y * c13x2 * c22x * c13y - 6 * c10y * c21x * c13x2 * c21y * c13y
                        - 2 * c10y * c12x2 * c12y * c13x * c22y - 2 * c10y * c12x2 * c12y * c22x * c13y - c11x * c11y * c12x2 * c13y * c22y
                        - 2 * c11x * c11y2 * c13x * c22x * c13y + 3 * c20x * c11y * c12y * c13x2 * c22y - 2 * c20x * c12x * c12y2 * c13x * c22y
                        - 2 * c20x * c12x * c12y2 * c22x * c13y - 6 * c20x * c20y * c13x * c22x * c13y2 - 6 * c20x * c21x * c13x * c21y * c13y2
                        + 3 * c11y * c20y * c12y * c13x2 * c22x + 3 * c11y * c21x * c12y * c13x2 * c21y - 2 * c12x * c20y * c12y2 * c13x * c22x
                        - 2 * c12x * c21x * c12y2 * c13x * c21y - c11y2 * c12x * c12y * c13x * c22x + 2 * c20x * c12x2 * c12y * c13y * c22y
                        - 3 * c11y * c21x2 * c12y * c13x * c13y + 6 * c20y * c21x * c13x2 * c21y * c13y + 2 * c11x2 * c11y * c13x * c13y * c22y
                        + c11x2 * c12x * c12y * c13y * c22y + 2 * c12x2 * c20y * c12y * c22x * c13y + 2 * c12x2 * c21x * c12y * c21y * c13y
                        - 3 * c10x * c21x2 * c13y3 + 3 * c20x * c21x2 * c13y3 + 3 * c10x2 * c22x * c13y3 - 3 * c10y2 * c13x3 * c22y + 3 * c20x2 * c22x * c13y3
                        + c21x2 * c12y3 * c13x + c11y3 * c13x2 * c22x - c11x3 * c13y2 * c22y + 3 * c10y * c21x2 * c13x * c13y2
                        - c11x * c11y2 * c13x2 * c22y + c11x * c21x2 * c12y * c13y2 + 2 * c11y * c12x * c21x2 * c13y2 + c11x2 * c11y * c22x * c13y2
                        - c12x * c21x2 * c12y2 * c13y - 3 * c20y * c21x2 * c13x * c13y2 - 3 * c10x2 * c13x * c13y2 * c22y + 3 * c10y2 * c13x2 * c22x * c13y
                        - c11x2 * c12y2 * c13x * c22y + c11y2 * c12x2 * c22x * c13y - 3 * c20x2 * c13x * c13y2 * c22y + 3 * c20y2 * c13x2 * c22x * c13y
                        + c12x2 * c12y * c13x * (2 * c20y * c22y + c21y2) + c11x * c12x * c13x * c13y * (6 * c20y * c22y + 3 * c21y2)
                        + c12x3 * c13y * (-2 * c20y * c22y - c21y2) + c10y * c13x3 * (6 * c20y * c22y + 3 * c21y2)
                        + c11y * c12x * c13x2 * (-2 * c20y * c22y - c21y2) + c11x * c12y * c13x2 * (-4 * c20y * c22y - 2 * c21y2)
                        + c10x * c13x2 * c13y * (-6 * c20y * c22y - 3 * c21y2) + c20x * c13x2 * c13y * (6 * c20y * c22y + 3 * c21y2)
                        + c13x3 * (-2 * c20y * c21y2 - c20y2 * c22y - c20y * (2 * c20y * c22y + c21y2)),
                -c10x * c11x * c12y * c13x * c21y * c13y + c10x * c11y * c12x * c13x * c21y * c13y + 6 * c10x * c11y * c21x * c12y * c13x * c13y
                        - 6 * c10y * c11x * c12x * c13x * c21y * c13y - c10y * c11x * c21x * c12y * c13x * c13y + c10y * c11y * c12x * c21x * c13x * c13y
                        - c11x * c11y * c12x * c21x * c12y * c13y + c11x * c11y * c12x * c12y * c13x * c21y + c11x * c20x * c12y * c13x * c21y * c13y
                        + 6 * c11x * c12x * c20y * c13x * c21y * c13y + c11x * c20y * c21x * c12y * c13x * c13y - c20x * c11y * c12x * c13x * c21y * c13y
                        - 6 * c20x * c11y * c21x * c12y * c13x * c13y - c11y * c12x * c20y * c21x * c13x * c13y - 6 * c10x * c20x * c21x * c13y3
                        - 2 * c10x * c21x * c12y3 * c13x + 6 * c10y * c20y * c13x3 * c21y + 2 * c20x * c21x * c12y3 * c13x + 2 * c10y * c12x3 * c21y * c13y
                        - 2 * c12x3 * c20y * c21y * c13y - 6 * c10x * c10y * c21x * c13x * c13y2 + 3 * c10x * c11x * c12x * c21y * c13y2
                        - 2 * c10x * c11x * c21x * c12y * c13y2 - 4 * c10x * c11y * c12x * c21x * c13y2 + 3 * c10y * c11x * c12x * c21x * c13y2
                        + 6 * c10x * c10y * c13x2 * c21y * c13y + 6 * c10x * c20x * c13x * c21y * c13y2 - 3 * c10x * c11y * c12y * c13x2 * c21y
                        + 2 * c10x * c12x * c21x * c12y2 * c13y + 2 * c10x * c12x * c12y2 * c13x * c21y + 6 * c10x * c20y * c21x * c13x * c13y2
                        + 4 * c10y * c11x * c12y * c13x2 * c21y + 6 * c10y * c20x * c21x * c13x * c13y2 + 2 * c10y * c11y * c12x * c13x2 * c21y
                        - 3 * c10y * c11y * c21x * c12y * c13x2 + 2 * c10y * c12x * c21x * c12y2 * c13x - 3 * c11x * c20x * c12x * c21y * c13y2
                        + 2 * c11x * c20x * c21x * c12y * c13y2 + c11x * c11y * c21x * c12y2 * c13x - 3 * c11x * c12x * c20y * c21x * c13y2
                        + 4 * c20x * c11y * c12x * c21x * c13y2 - 6 * c10x * c20y * c13x2 * c21y * c13y - 2 * c10x * c12x2 * c12y * c21y * c13y
                        - 6 * c10y * c20x * c13x2 * c21y * c13y - 6 * c10y * c20y * c21x * c13x2 * c13y - 2 * c10y * c12x2 * c21x * c12y * c13y
                        - 2 * c10y * c12x2 * c12y * c13x * c21y - c11x * c11y * c12x2 * c21y * c13y - 4 * c11x * c20y * c12y * c13x2 * c21y
                        - 2 * c11x * c11y2 * c21x * c13x * c13y + 3 * c20x * c11y * c12y * c13x2 * c21y - 2 * c20x * c12x * c21x * c12y2 * c13y
                        - 2 * c20x * c12x * c12y2 * c13x * c21y - 6 * c20x * c20y * c21x * c13x * c13y2 - 2 * c11y * c12x * c20y * c13x2 * c21y
                        + 3 * c11y * c20y * c21x * c12y * c13x2 - 2 * c12x * c20y * c21x * c12y2 * c13x - c11y2 * c12x * c21x * c12y * c13x
                        + 6 * c20x * c20y * c13x2 * c21y * c13y + 2 * c20x * c12x2 * c12y * c21y * c13y + 2 * c11x2 * c11y * c13x * c21y * c13y
                        + c11x2 * c12x * c12y * c21y * c13y + 2 * c12x2 * c20y * c21x * c12y * c13y + 2 * c12x2 * c20y * c12y * c13x * c21y
                        + 3 * c10x2 * c21x * c13y3 - 3 * c10y2 * c13x3 * c21y + 3 * c20x2 * c21x * c13y3 + c11y3 * c21x * c13x2 - c11x3 * c21y * c13y2
                        - 3 * c20y2 * c13x3 * c21y - c11x * c11y2 * c13x2 * c21y + c11x2 * c11y * c21x * c13y2 - 3 * c10x2 * c13x * c21y * c13y2
                        + 3 * c10y2 * c21x * c13x2 * c13y - c11x2 * c12y2 * c13x * c21y + c11y2 * c12x2 * c21x * c13y - 3 * c20x2 * c13x * c21y * c13y2
                        + 3 * c20y2 * c21x * c13x2 * c13y,
                c10x * c10y * c11x * c12y * c13x * c13y - c10x * c10y * c11y * c12x * c13x * c13y + c10x * c11x * c11y * c12x * c12y * c13y
                        - c10y * c11x * c11y * c12x * c12y * c13x - c10x * c11x * c20y * c12y * c13x * c13y + 6 * c10x * c20x * c11y * c12y * c13x * c13y
                        + c10x * c11y * c12x * c20y * c13x * c13y - c10y * c11x * c20x * c12y * c13x * c13y - 6 * c10y * c11x * c12x * c20y * c13x * c13y
                        + c10y * c20x * c11y * c12x * c13x * c13y - c11x * c20x * c11y * c12x * c12y * c13y + c11x * c11y * c12x * c20y * c12y * c13x
                        + c11x * c20x * c20y * c12y * c13x * c13y - c20x * c11y * c12x * c20y * c13x * c13y - 2 * c10x * c20x * c12y3 * c13x
                        + 2 * c10y * c12x3 * c20y * c13y - 3 * c10x * c10y * c11x * c12x * c13y2 - 6 * c10x * c10y * c20x * c13x * c13y2
                        + 3 * c10x * c10y * c11y * c12y * c13x2 - 2 * c10x * c10y * c12x * c12y2 * c13x - 2 * c10x * c11x * c20x * c12y * c13y2
                        - c10x * c11x * c11y * c12y2 * c13x + 3 * c10x * c11x * c12x * c20y * c13y2 - 4 * c10x * c20x * c11y * c12x * c13y2
                        + 3 * c10y * c11x * c20x * c12x * c13y2 + 6 * c10x * c10y * c20y * c13x2 * c13y + 2 * c10x * c10y * c12x2 * c12y * c13y
                        + 2 * c10x * c11x * c11y2 * c13x * c13y + 2 * c10x * c20x * c12x * c12y2 * c13y + 6 * c10x * c20x * c20y * c13x * c13y2
                        - 3 * c10x * c11y * c20y * c12y * c13x2 + 2 * c10x * c12x * c20y * c12y2 * c13x + c10x * c11y2 * c12x * c12y * c13x
                        + c10y * c11x * c11y * c12x2 * c13y + 4 * c10y * c11x * c20y * c12y * c13x2 - 3 * c10y * c20x * c11y * c12y * c13x2
                        + 2 * c10y * c20x * c12x * c12y2 * c13x + 2 * c10y * c11y * c12x * c20y * c13x2 + c11x * c20x * c11y * c12y2 * c13x
                        - 3 * c11x * c20x * c12x * c20y * c13y2 - 2 * c10x * c12x2 * c20y * c12y * c13y - 6 * c10y * c20x * c20y * c13x2 * c13y
                        - 2 * c10y * c20x * c12x2 * c12y * c13y - 2 * c10y * c11x2 * c11y * c13x * c13y - c10y * c11x2 * c12x * c12y * c13y
                        - 2 * c10y * c12x2 * c20y * c12y * c13x - 2 * c11x * c20x * c11y2 * c13x * c13y - c11x * c11y * c12x2 * c20y * c13y
                        + 3 * c20x * c11y * c20y * c12y * c13x2 - 2 * c20x * c12x * c20y * c12y2 * c13x - c20x * c11y2 * c12x * c12y * c13x
                        + 3 * c10y2 * c11x * c12x * c13x * c13y + 3 * c11x * c12x * c20y2 * c13x * c13y + 2 * c20x * c12x2 * c20y * c12y * c13y
                        - 3 * c10x2 * c11y * c12y * c13x * c13y + 2 * c11x2 * c11y * c20y * c13x * c13y + c11x2 * c12x * c20y * c12y * c13y
                        - 3 * c20x2 * c11y * c12y * c13x * c13y - c10x3 * c13y3 + c10y3 * c13x3 + c20x3 * c13y3 - c20y3 * c13x3
                        - 3 * c10x * c20x2 * c13y3 - c10x * c11y3 * c13x2 + 3 * c10x2 * c20x * c13y3 + c10y * c11x3 * c13y2
                        + 3 * c10y * c20y2 * c13x3 + c20x * c11y3 * c13x2 + c10x2 * c12y3 * c13x - 3 * c10y2 * c20y * c13x3 - c10y2 * c12x3 * c13y
                        + c20x2 * c12y3 * c13x - c11x3 * c20y * c13y2 - c12x3 * c20y2 * c13y - c10x * c11x2 * c11y * c13y2
                        + c10y * c11x * c11y2 * c13x2 - 3 * c10x * c10y2 * c13x2 * c13y - c10x * c11y2 * c12x2 * c13y + c10y * c11x2 * c12y2 * c13x
                        - c11x * c11y2 * c20y * c13x2 + 3 * c10x2 * c10y * c13x * c13y2 + c10x2 * c11x * c12y * c13y2
                        + 2 * c10x2 * c11y * c12x * c13y2 - 2 * c10y2 * c11x * c12y * c13x2 - c10y2 * c11y * c12x * c13x2 + c11x2 * c20x * c11y * c13y2
                        - 3 * c10x * c20y2 * c13x2 * c13y + 3 * c10y * c20x2 * c13x * c13y2 + c11x * c20x2 * c12y * c13y2 - 2 * c11x * c20y2 * c12y * c13x2
                        + c20x * c11y2 * c12x2 * c13y - c11y * c12x * c20y2 * c13x2 - c10x2 * c12x * c12y2 * c13y - 3 * c10x2 * c20y * c13x * c13y2
                        + 3 * c10y2 * c20x * c13x2 * c13y + c10y2 * c12x2 * c12y * c13x - c11x2 * c20y * c12y2 * c13x + 2 * c20x2 * c11y * c12x * c13y2
                        + 3 * c20x * c20y2 * c13x2 * c13y - c20x2 * c12x * c12y2 * c13y - 3 * c20x2 * c20y * c13x * c13y2 + c12x2 * c20y2 * c12y * c13x
        );

        final double[] roots = poly.getRootsInInterval(tMin, tMax);

        for (double s : roots) {
            double[] xRoots = new Polynomial(
                    c13x, c12x, c11x,
                    c10x - c20x - s * c21x - s * s * c22x - s * s * s * c23x
            ).getRoots();
            double[] yRoots = new Polynomial(
                    c13y, c12y, c11y,
                    c10y - c20y - s * c21y - s * s * c22y - s * s * s * c23y
            ).getRoots();

            if (xRoots.length > 0 && yRoots.length > 0) {
                final double TOLERANCE = 1e-4;

                checkRoots:
                for (double xRoot : xRoots) {
                    if (tMin <= xRoot && xRoot <= tMax) {
                        for (double yRoot : yRoots) {
                            if (abs(xRoot - yRoot) < TOLERANCE) {
                                result.add(new Intersection.IntersectionPoint(
                                        c23.multiply(s * s * s).add(c22.multiply(s * s).add(c21.multiply(s).add(c20))),
                                        xRoot));
                                break checkRoots;
                            }
                        }
                    }
                }
            }
        }

        return new Intersection(result);
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the given
     * circle.
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P2 of 'p'
     * @param p3 control point P3 of 'p'
     * @param c  the center of the circle
     * @param r  the radius of the circle
     * @return the computed result
     */
    @NonNull
    public static Intersection intersectCubicCurveCircle(
            @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3,
            @NonNull Point2D c, double r) {
        return Intersections.intersectCubicCurveEllipse(p0, p1, p2, p3, c, r, r);
    }

    @NonNull
    public static Intersection intersectCubicCurveCircle(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy, double r) {
        return Intersections.intersectCubicCurveEllipse(new Point2D(x0, y0), new Point2D(x1, y1), new Point2D(x2, y2), new Point2D(x3, y3), new Point2D(cx, cy), r, r);
    }

    @NonNull
    public static Intersection intersectCubicCurveEllipse(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy, double rx, double ry) {
        return intersectCubicCurveEllipse(
                new Point2D(x0, y0), new Point2D(x1, y1), new Point2D(x2, y2), new Point2D(x3, y3),
                new Point2D(cx, cy), rx, ry);

    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the given
     * ellipse.
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P2 of 'p'
     * @param p3 control point P3 of 'p'
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @return the computed result. Status can be{@link Status#INTERSECTION},
     * Status#NO_INTERSECTION_INSIDE or Status#NO_INTERSECTION_OUTSIDE}.
     */
    @NonNull
    public static Intersection intersectCubicCurveEllipse(
            @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3,
            @NonNull Point2D ec, double rx, double ry) {
        Point2D a, b, c, d;       // temporary variables
        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        // Calculate the coefficients of cubic polynomial
        final Point2D c3, c2, c1, c0;
        c3 = p0.multiply(-1).add(p1.multiply(3).add(p2.multiply(-3).add(p3)));
        c2 = p0.multiply(3).add(p1.multiply(-6).add(p2.multiply(3)));
        c1 = p0.multiply(-3).add(p1.multiply(3));
        c0 = p0;

        final double rxrx, ryry, c3x, c3y, c2x, c1x, c2y, c1y, ecx, c0x, c0y, ecy;
        rxrx = rx * rx;
        ryry = ry * ry;
        c3x = c3.getX();
        c3y = c3.getY();
        c2x = c2.getX();
        c1x = c1.getX();
        c2y = c2.getY();
        c1y = c1.getY();
        ecx = ec.getX();
        c0x = c0.getX();
        c0y = c0.getY();
        ecy = ec.getY();

        Polynomial poly = new Polynomial(
                c3x * c3x * ryry + c3y * c3y * rxrx,
                2 * (c3x * c2x * ryry + c3y * c2y * rxrx),
                2 * (c3x * c1x * ryry + c3y * c1y * rxrx) + c2x * c2x * ryry + c2y * c2y * rxrx,
                2 * c3x * ryry * (c0x - ecx) + 2 * c3y * rxrx * (c0y - ecy)
                        + 2 * (c2x * c1x * ryry + c2y * c1y * rxrx),
                2 * c2x * ryry * (c0x - ecx) + 2 * c2y * rxrx * (c0y - ecy)
                        + c1x * c1x * ryry + c1y * c1y * rxrx,
                2 * c1x * ryry * (c0x - ecx) + 2 * c1y * rxrx * (c0y - ecy),
                c0x * c0x * ryry - 2 * c0y * ecy * rxrx - 2 * c0x * ecx * ryry
                        + c0y * c0y * rxrx + ecx * ecx * ryry + ecy * ecy * rxrx - rxrx * ryry
        );
        double[] roots = poly.getRootsInInterval(0, 1);

        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            result.add(new Intersection.IntersectionPoint(c3.multiply(t * t * t).add(c2.multiply(t * t).add(c1.multiply(t).add(c0))), t));
        }

        if (result.size() > 0) {
            return new Intersection(result);
        } else {
            return intersectPointEllipse(p0, ec, rx, ry);// Computes inside/outside status
        }

    }

    @NonNull
    public static Intersection intersectCubicCurveLine(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3,
            double bx0, double by0, double bx1, double by1) {
        return intersectCubicCurveLine(new Point2D(ax0, ay0), new Point2D(ax1, ay1), new Point2D(ax2, ay2), new Point2D(ax3, ay3),
                new Point2D(bx0, by0), new Point2D(bx1, by1));
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the line
     * 'a'.
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P2 of 'p'
     * @param p3 control point P3 of 'p'
     * @param a0 point 0 of 'a'
     * @param a1 point 1 of 'a'
     * @return the computed intersection
     */
    @NonNull
    public static Intersection intersectCubicCurveLine(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3, @NonNull Point2D a0, @NonNull Point2D a1) {
        final Point2D topLeft = topLeft(a0, a1); // used to determine if point is on line segment
        final Point2D bottomRight = bottomRight(a0, a1); // used to determine if point is on line segment
        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        // Start with Bezier using Bernstein polynomials for weighting functions:
        //     (1-t^3)P0 + 3t(1-t)^2P1 + 3t^2(1-t)P2 + t^3P3
        //
        // Expand and collect terms to form linear combinations of original Bezier
        // controls.  This ends up with a vector cubic in t:
        //     (-P0+3P1-3P2+P3)t^3 + (3P0-6P1+3P2)t^2 + (-3P0+3P1)t + P0
        //             /\                  /\                /\       /\
        //             ||                  ||                ||       ||
        //             c3                  c2                c1       c0
        // Calculate the coefficients
        final Point2D c3, c2, c1, c0;   // coefficients of cubic
        c3 = p0.multiply(-1).add(p1.multiply(3).add(p2.multiply(-3).add(p3)));
        c2 = p0.multiply(3).add(p1.multiply(-6).add(p2.multiply(3)));
        c1 = p0.multiply(-3).add(p1.multiply(3));
        c0 = p0;

        final double a0x, a0y, a1x, a1y;
        a0y = a0.getY();
        a1y = a1.getY();
        a1x = a1.getX();
        a0x = a0.getX();

        // Convert line to normal form: ax + by + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D n;                // normal for normal form of line
        n = new Point2D(a0y - a1y, a1x - a0x);

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = a0x * a1y - a1x * a0y;

        // ?Rotate each cubic coefficient using line for new coordinate system?
        // Find roots of rotated cubic
        double[] roots = new Polynomial(
                n.dotProduct(c3),
                n.dotProduct(c2),
                n.dotProduct(c1),
                n.dotProduct(c0) + cl
        ).getRoots();

        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;
        for (int i = 0; i < roots.length; i++) {
            final double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D p5, p6, p7, p8, p9, p10;
                p5 = lerp(p0, p1, t);
                p6 = lerp(p1, p2, t);
                p7 = lerp(p2, p3, t);
                p8 = lerp(p5, p6, t);
                p9 = lerp(p6, p7, t);
                p10 = lerp(p8, p9, t);

                // See if point is on line segment
                // Had to make special cases for vertical and horizontal lines due
                // to slight errors in calculation of p10
                if (a0x == a1x) {
                    if (topLeft.getY() <= p10.getY() && p10.getY() <= bottomRight.getY()) {
                        status = Intersection.Status.INTERSECTION;
                        result.add(new Intersection.IntersectionPoint(p10, t));
                    }
                } else if (a0y == a1y) {
                    if (topLeft.getX() <= p10.getX() && p10.getX() <= bottomRight.getX()) {
                        status = Intersection.Status.INTERSECTION;
                        result.add(new Intersection.IntersectionPoint(p10, t));
                    }
                } else if (gte(p10, topLeft) && lte(p10, bottomRight)) {
                    status = Intersection.Status.INTERSECTION;
                    result.add(new Intersection.IntersectionPoint(p10, t));
                }
            }
        }

        return new Intersection(status, result);
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
     * @param r  the tolerance radius
     * @return the intersection
     */
    @NonNull
    public static Intersection intersectCubicCurvePoint(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy, double r) {
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
        final double[] clampedRoots = new Polynomial(6 * a, 5 * b, 4 * c, 3 * d, 2 * e, f).getRootsInInterval(0, 1);
        // Add zero and one, because we have clamped the roots
        final double[] roots = addZeroAndOne(clampedRoots);

        // Select roots with closest distance to point
        final List<Intersection.IntersectionPoint> result = new ArrayList<>();
        final Point2D p0, p1, p2, p3;
        p0 = new Point2D(x0, y0);
        p1 = new Point2D(x1, y1);
        p2 = new Point2D(x2, y2);
        p3 = new Point2D(x3, y3);
        final double rr = r * r;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (double t : roots) {
            final Point2D p;
            p = p0.multiply((1 - t) * (1 - t) * (1 - t))
                    .add(p1.multiply(3 * (1 - t) * (1 - t) * t))
                    .add(p2.multiply(3 * (1 - t) * t * t))
                    .add(p3.multiply(t * t * t));

            double dd = (p.getX() - cx) * (p.getX() - cx) + (p.getY() - cy) * (p.getY() - cy);
            if (dd < rr) {
                if (abs(dd - bestDistance) < EPSILON) {
                    result.add(new Intersection.IntersectionPoint(p, t));
                } else if (dd < bestDistance) {
                    bestDistance = dd;
                    result.clear();
                    result.add(new Intersection.IntersectionPoint(p, t));
                }
            }
        }

        return new Intersection(result);
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the given
     * closed polygon.
     *
     * @param p0     control point P0 of 'p'
     * @param p1     control point P1 of 'p'
     * @param p2     control point P2 of 'p'
     * @param p3     control point P3 of 'p'
     * @param points the points of the polygon
     * @return the computed intersection
     */
    @NonNull
    public static Intersection intersectCubicCurvePolygon(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3, @NonNull List<Point2D> points) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D a1 = points.get(i);
            Point2D a2 = points.get((i + 1) % length);
            Intersection inter = Intersections.intersectCubicCurveLine(p0, p1, p2, p3, a1, a2);

            result.addAll(inter.getIntersections());
        }

        return new Intersection(result);
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the provided
     * rectangle.
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P2 of 'p'
     * @param p3 control point P3 of 'p'
     * @param r0 corner point 0 of the rectangle
     * @param r1 corner point 1 of the rectangle
     * @return the computed intersection
     */
    @NonNull
    public static Intersection intersectCubicCurveRectangle(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3, @NonNull Point2D r0, @NonNull Point2D r1) {
        final Point2D topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(r0, r1);
        bottomRight = bottomRight(r0, r1);
        topRight = new Point2D(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D(topLeft.getX(), bottomRight.getY());

        final Intersection inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectCubicCurveLine(p0, p1, p2, p3, topLeft, topRight);
        inter2 = Intersections.intersectCubicCurveLine(p0, p1, p2, p3, topRight, bottomRight);
        inter3 = Intersections.intersectCubicCurveLine(p0, p1, p2, p3, bottomRight, bottomLeft);
        inter4 = Intersections.intersectCubicCurveLine(p0, p1, p2, p3, bottomLeft, topLeft);

        final List<Intersection.IntersectionPoint> result = new ArrayList<>();

        result.addAll(inter1.getIntersections());
        result.addAll(inter2.getIntersections());
        result.addAll(inter3.getIntersections());
        result.addAll(inter4.getIntersections());

        // FIXME compute inside/outside
        return new Intersection(result);
    }

    @NonNull
    public static Intersection intersectCircleCircle(double c1x, double c1y, double r1, double c2x, double c2y, double r2) {
        return intersectCircleCircle(new Point2D(c1x, c1y), r1, new Point2D(c2x, c2y), r2);
    }

    /**
     * Computes the intersection between circle 1 and circle 2.
     *
     * @param c1 the center of circle 1
     * @param r1 the radius of circle 1
     * @param c2 the center of circle 2
     * @param r2 the radius of circle 2
     * @return computed intersection with parameters of circle 1 at the intersection point
     */
    @NonNull
    public static Intersection intersectCircleCircle(@NonNull Point2D c1, double r1, @NonNull Point2D c2, double r2) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        // Determine minimum and maximum radii where circles can intersect
        double r_max = r1 + r2;
        double r_min = Math.abs(r1 - r2);

        // Determine actual distance between circle circles
        double c_dist = c1.distance(c2);

        Intersection.Status status;

        if (c_dist > r_max) {
            status = Intersection.Status.NO_INTERSECTION_OUTSIDE;
        } else if (c_dist < r_min) {
            status = Intersection.Status.NO_INTERSECTION_INSIDE;
        } else {
            status = Intersection.Status.INTERSECTION;

            double a = (r1 * r1 - r2 * r2 + c_dist * c_dist) / (2 * c_dist);
            double h = Math.sqrt(r1 * r1 - a * a);
            Point2D p = lerp(c1, c2, a / c_dist);
            double b = h / c_dist;

            // FIXME compute t of circle 1
            result.add(new Intersection.IntersectionPoint(new Point2D(
                    p.getX() - b * (c2.getY() - c1.getY()),
                    p.getY() + b * (c2.getX() - c1.getX())
            ), Double.NaN
            ));
            result.add(new Intersection.IntersectionPoint(new Point2D(
                    p.getX() + b * (c2.getY() - c1.getY()),
                    p.getY() - b * (c2.getX() - c1.getX())
            ), Double.NaN
            ));
        }
        return new Intersection(status, result);
    }

    /**
     * Computes the intersection between a circle and an ellipse.
     *
     * @param cc the center of the circle
     * @param r  the radius of the circle
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectCircleEllipse(@NonNull Point2D cc, double r, @NonNull Point2D ec, double rx, double ry) {
        return Intersections.intersectEllipseEllipse(cc, r, r, ec, rx, ry);
    }

    @NonNull
    public static Intersection intersectCircleEllipse(double cx1, double cy1, double r1, double cx2, double cy2, double rx2, double ry2) {
        return intersectEllipseEllipse(cx1, cy1, r1, r1, cx2, cy2, rx2, ry2);
    }

    @NonNull
    public static Intersection intersectCircleLine(double cx, double cy, double r, double a0x, double a0y, double a1x, double a1y) {
        return intersectCircleLine(new Point2D(cx, cy), r, new Point2D(a0x, a0y), new Point2D(a1x, a1y));
    }

    /**
     * Computes the intersection between a circle and a line.
     * <p>
     * FIXME actually computes line intersection with parameter t of line, and
     * not t of circle.
     *
     * @param c  the center of the circle
     * @param r  the radius of the circle
     * @param a0 point 1 of the line
     * @param a1 point 2 of the line
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectCircleLine(@NonNull Point2D c, double r, @NonNull Point2D a0, @NonNull Point2D a1) {
        Intersection inter = intersectLineCircle(a0, a1, c, r);
        // FIXME compute t of circle!
        return inter;
    }

    @NonNull
    public static Intersection intersectCirclePoint(double cx, double cy, double cr, double px, double py, double pr) {
        return intersectCirclePoint(new Point2D(cx, cy), cr, new Point2D(px, py), pr);
    }

    @NonNull
    public static Intersection intersectCirclePoint(@NonNull Point2D cc, double cr, @NonNull Point2D pc, double pr) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        double c_dist = cc.distance(pc);

        Intersection.Status status;
        if (abs(c_dist) < EPSILON) {
            status = Intersection.Status.NO_INTERSECTION_INSIDE;
        } else {

            Point2D p = lerp(cc, pc, cr / c_dist);
            final double dd = Geom.squaredDistance(p, pc);
            if (dd <= pr * pr) {
                status = Intersection.Status.INTERSECTION;
                // FIXME compute t
                result.add(new Intersection.IntersectionPoint(p, Double.NaN));
            } else {
                status = Intersection.Status.NO_INTERSECTION_OUTSIDE;
            }
        }
        return new Intersection(status, result);
    }

    /**
     * Computes the intersection between a circle and a polygon.
     *
     * @param c      the center of the circle
     * @param r      the radius of the circle
     * @param points the points of the polygon
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectCirclePolygon(@NonNull Point2D c, double r, @NonNull List<Point2D> points) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        int length = points.size();
        Intersection inter = null;

        for (int i = 0; i < length; i++) {
            final Point2D a0, a1;
            a0 = points.get(i);
            a1 = points.get((i + 1) % length);

            inter = Intersections.intersectCircleLine(c, r, a0, a1);
            result.addAll(inter.getIntersections());
        }

        Intersection.Status status;
        if (!result.isEmpty()) {
            status = Intersection.Status.INTERSECTION;
        } else {
            status = inter == null ? Intersection.Status.NO_INTERSECTION : inter.getStatus();
        }

        return new Intersection(status, result);
    }

    @NonNull
    public static Intersection intersectCircleRectangle(double c1x, double c1y, double r1, double x, double y, double w, double h) {
        return intersectCircleRectangle(new Point2D(c1x, c1y), r1, new Point2D(x, y), new Point2D(x + w, y + h));
    }

    /**
     * Computes the intersection between a circle and a rectangle.
     *
     * @param c  the center of the circle
     * @param r  the radius of the circle
     * @param r0 corner point 0 of the rectangle
     * @param r1 corner point 1 of the rectangle
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectCircleRectangle(@NonNull Point2D c, double r, @NonNull Point2D r0, @NonNull Point2D r1) {
        final Point2D topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(r0, r1);
        bottomRight = bottomRight(r0, r1);
        topRight = new Point2D(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D(topLeft.getX(), bottomRight.getY());

        final Intersection inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectCircleLine(c, r, topLeft, topRight);
        inter2 = Intersections.intersectCircleLine(c, r, topRight, bottomRight);
        inter3 = Intersections.intersectCircleLine(c, r, bottomRight, bottomLeft);
        inter4 = Intersections.intersectCircleLine(c, r, bottomLeft, topLeft);

        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        result.addAll(inter1.getIntersections());
        result.addAll(inter2.getIntersections());
        result.addAll(inter3.getIntersections());
        result.addAll(inter4.getIntersections());

        Intersection.Status status;
        if (!result.isEmpty()) {
            status = Intersection.Status.INTERSECTION;
        } else {
            status = inter1.getStatus();
        }

        return new Intersection(status, result);
    }

    @NonNull
    public static Intersection intersectEllipseQuadraticCurve(
            double cx, double cy, double rx, double ry,
            double x0, double y0, double x1, double y1, double x2, double y3) {
        // FIXME compute t of Ellipse!
        return intersectQuadraticCurveEllipse(new Point2D(x0, y0), new Point2D(x1, y1), new Point2D(x2, y3), new Point2D(cx, cy), rx, ry);
    }

    @NonNull
    public static Intersection intersectEllipseCircle(double cx1, double cy1, double rx1, double ry1, double cx2, double cy2, double r2) {
        return intersectEllipseEllipse(cx1, cy1, rx1, ry1, cx2, cy2, r2, r2);
    }

    /**
     * Computes the intersection between two ellipses.
     *
     * @param c1  the center of ellipse 1
     * @param rx1 the x-radius of ellipse 1
     * @param ry1 the y-radius of ellipse 1
     * @param c2  the center of ellipse 2
     * @param rx2 the x-radius of ellipse 2
     * @param ry2 the y-radius of ellipse 2
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectEllipseEllipse(@NonNull Point2D c1, double rx1, double ry1, @NonNull Point2D c2, double rx2, double ry2) {
        return intersectEllipseEllipse(c1.getX(), c1.getY(), rx1, ry1, c2.getX(), c2.getY(), rx2, ry2);
    }

    /**
     * Computes the intersection between two ellipses.
     *
     * @param cx1 the center of ellipse 1
     * @param cy1 the center of ellipse 1
     * @param rx1 the x-radius of ellipse 1
     * @param ry1 the y-radius of ellipse 1
     * @param cx2 the center of ellipse 2
     * @param cy2 the center of ellipse 2
     * @param rx2 the x-radius of ellipse 2
     * @param ry2 the y-radius of ellipse 2
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectEllipseEllipse(double cx1, double cy1, double rx1, double ry1, double cx2, double cy2, double rx2, double ry2) {
        double[] a = {
                ry1 * ry1,
                0,
                rx1 * rx1,
                -2 * ry1 * ry1 * cx1,
                -2 * rx1 * rx1 * cy1,
                ry1 * ry1 * cx1 * cx1 + rx1 * rx1 * cy1 * cy1 - rx1 * rx1 * ry1 * ry1
        };
        double[] b = {
                ry2 * ry2,
                0,
                rx2 * rx2,
                -2 * ry2 * ry2 * cx2,
                -2 * rx2 * rx2 * cy2,
                ry2 * ry2 * cx2 * cx2 + rx2 * rx2 * cy2 * cy2 - rx2 * rx2 * ry2 * ry2
        };

        Polynomial yPoly = Intersections.bezout(a, b);
        double[] yRoots = yPoly.getRoots();
        double norm0 = (a[0] * a[0] + 2 * a[1] * a[1] + a[2] * a[2]) * EPSILON;
        double norm1 = (b[0] * b[0] + 2 * b[1] * b[1] + b[2] * b[2]) * EPSILON;
        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        for (int y = 0; y < yRoots.length; y++) {
            Polynomial xPoly = new Polynomial(
                    a[0],
                    a[3] + yRoots[y] * a[1],
                    a[5] + yRoots[y] * (a[4] + yRoots[y] * a[2])
            );
            double[] xRoots = xPoly.getRoots();

            for (int x = 0; x < xRoots.length; x++) {
                double test = (a[0] * xRoots[x] + a[1] * yRoots[y] + a[3]) * xRoots[x]
                        + (a[2] * yRoots[y] + a[4]) * yRoots[y] + a[5];
                if (Math.abs(test) < norm0) {
                    test = (b[0] * xRoots[x] + b[1] * yRoots[y] + b[3]) * xRoots[x]
                            + (b[2] * yRoots[y] + b[4]) * yRoots[y] + b[5];
                    if (Math.abs(test) < norm1) {
                        // FIXME compute angle in radians
                        result.add(new Intersection.IntersectionPoint(new Point2D(xRoots[x], yRoots[y]), Double.NaN));
                    }
                }
            }
        }

        return new Intersection(result);
    }

    /**
     * Computes the intersection between an ellipse and a line.
     *
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @param a0 point 0 of the line
     * @param a1 point 1 of the line
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectEllipseLine(@NonNull Point2D ec, double rx, double ry, @NonNull Point2D a0, @NonNull Point2D a1) {
        Intersection result = intersectLineEllipse(a0, a1, ec, rx, ry);
        // FIXME compute t for Ellipse instead for Line!
        return result;
    }

    @NonNull
    public static Intersection intersectEllipseLine(double cx, double cy, double rx, double ry,
                                                    double x0, double y0, double x1, double y1) {
        Intersection result = intersectLineEllipse(x0, y0, x1, y1, cx, cy, rx, ry);
        // FIXME compute t for Ellipse instead for Line!
        return result;
    }

    /**
     * Computes the intersection between a circle and a polygon.
     *
     * @param c      the center of the ellipse
     * @param rx     the x-radius of the ellipse
     * @param ry     the y-radius of the ellipse
     * @param points the points of the polygon
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectEllipsePolygon(@NonNull Point2D c, double rx, double ry, @NonNull List<Point2D> points) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D b1 = points.get(i);
            Point2D b2 = points.get((i + 1) % length);
            Intersection inter = Intersections.intersectEllipseLine(c, rx, ry, b1, b2);

            result.addAll(inter.getIntersections());
        }

        return new Intersection(result);
    }

    /**
     * Computes the intersection between an ellipse and a rectangle.
     *
     * @param c  the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @param r1 corner point 1 of the rectangle
     * @param r2 corner point 2 of the rectangle
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectEllipseRectangle(@NonNull Point2D c, double rx, double ry, @NonNull Point2D r1, @NonNull Point2D r2) {
        final Point2D topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(r1, r2);
        bottomRight = bottomRight(r1, r2);
        topRight = new Point2D(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D(topLeft.getX(), bottomRight.getY());

        final Intersection inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectEllipseLine(c, rx, ry, topLeft, topRight);
        inter2 = Intersections.intersectEllipseLine(c, rx, ry, topRight, bottomRight);
        inter3 = Intersections.intersectEllipseLine(c, rx, ry, bottomRight, bottomLeft);
        inter4 = Intersections.intersectEllipseLine(c, rx, ry, bottomLeft, topLeft);

        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        result.addAll(inter1.getIntersections());
        result.addAll(inter2.getIntersections());
        result.addAll(inter3.getIntersections());
        result.addAll(inter4.getIntersections());

        return new Intersection(result);
    }

    /**
     * Computes the intersection between quadratic bezier curve 'p' and the line
     * 'a'.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range
     * [0,1].
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P2 of 'p'
     * @param a0 point 0 of 'a'
     * @param a1 point 0 of 'a'
     * @return the computed intersection
     */
    @NonNull
    public static Intersection intersectLineQuadraticCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2) {
        return intersectLineQuadraticCurve(
                a0.getX(), a0.getY(),
                a1.getX(), a1.getY(),
                p0.getX(), p0.getY(),
                p1.getX(), p1.getY(),
                p2.getX(), p2.getY());
    }

    @NonNull
    public static Intersection intersectLineQuadraticCurve(double a0x, double a0y, double a1x, double a1y,
                                                           double p0x, double p0y, double p1x, double p1y, double p2x, double p2y) {
        return intersectLineQuadraticCurve(
                a0x, a0y,
                a1x, a1y,
                p0x, p0y,
                p1x, p1y,
                p2x, p2y, 1.0);
    }

    @NonNull
    public static Intersection intersectLineQuadraticCurve(double a0x, double a0y, double a1x, double a1y,
                                                           double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double maxT) {
        /* steps:
         * 1. Rotate the bezier curve so that the line coincides with the x-axis.
         *    This will position the curve in a way that makes it cross the line at points where its y-function is zero.
         * 2. Insert the control points of the rotated bezier curve in the polynomial equation.
         * 3. Find the roots of the polynomial equation.
         */

        Point2D topLeft = topLeft(a0x, a0y, a1x, a1y); // used to determine if point is on line segment
        Point2D bottomRight = bottomRight(a0x, a0y, a1x, a1y); // used to determine if point is on line segment
        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        final Point2D p0, p1;
        p0 = new Point2D(p0x, p0y);
        p1 = new Point2D(p1x, p1y);

        final Point2D c2, c1, c0;       // coefficients of quadratic
        c2 = p0.add(p1.multiply(-2).add(p2x, p2y));
        c1 = p0.multiply(-2).add(p1.multiply(2));
        c0 = p0;

        // Convert line to normal form: ax + by + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D n;                // normal for normal form of line
        n = new Point2D(a0y - a1y, a1x - a0x);

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = a0x * a1y - a1x * a0y;

        // Transform cubic coefficients to line's coordinate system and find roots
        // of cubic
        double[] roots = new Polynomial(
                n.dotProduct(c2),
                n.dotProduct(c1),
                n.dotProduct(c0) + cl
        ).getRoots();
        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D p4, p5, p6;
                p4 = lerp(p0, p1, t);
                p5 = lerp(p1x, p1y, p2x, p2y, t);
                p6 = lerp(p4, p5, t);

                // See if point is on line segment
                double t1 = argumentOnLine(p6.getX(), p6.getY(), a0x, a0y, a1x, a1y);
                if (t1 >= 0 && t1 <= maxT) {
                    status = Intersection.Status.INTERSECTION;
                    result.add(new Intersection.IntersectionPoint(p6, t1));
                }
            }
        }

        return new Intersection(status, result);
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the line
     * 'a'.
     *
     * @param p0x control point P0 of 'p'
     * @param p1x control point P1 of 'p'
     * @param p2x control point P2 of 'p'
     * @param p3x control point P3 of 'p'
     * @param a0x point 1 of 'a'
     * @param a1x point 2 of 'a'
     * @param p0y control point P0 of 'p'
     * @param p1y control point P1 of 'p'
     * @param p2y control point P2 of 'p'
     * @param p3y control point P3 of 'p'
     * @param a0y point 1 of 'a'
     * @param a1y point 2 of 'a'
     * @return the computed intersection
     */
    @NonNull
    public static Intersection intersectLineCubicCurve(
            double a0x, double a0y, double a1x, double a1y,
            double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double p3x, double p3y) {

        Point2D a0 = new Point2D(a0x, a0y);
        Point2D a1 = new Point2D(a1x, a1y);
        Point2D p0 = new Point2D(p0x, p0y);
        Point2D p1 = new Point2D(p1x, p1y);
        Point2D p2 = new Point2D(p2x, p2y);
        Point2D p3 = new Point2D(p3x, p3y);
        return intersectLineCubicCurve(a0, a1, p0, p1, p2, p3);
    }

    @NonNull
    public static Intersection intersectLineCubicCurve(
            double a0x, double a0y, double a1x, double a1y,
            double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double p3x, double p3y, double maxT) {

        Point2D a0 = new Point2D(a0x, a0y);
        Point2D a1 = new Point2D(a1x, a1y);
        Point2D p0 = new Point2D(p0x, p0y);
        Point2D p1 = new Point2D(p1x, p1y);
        Point2D p2 = new Point2D(p2x, p2y);
        Point2D p3 = new Point2D(p3x, p3y);
        return intersectLineCubicCurve(a0, a1, p0, p1, p2, p3, maxT);
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the line
     * 'a'.
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P2 of 'p'
     * @param p3 control point P3 of 'p'
     * @param a0 point 1 of 'a'
     * @param a1 point 2 of 'a'
     * @return the computed intersection
     */
    @NonNull
    public static Intersection intersectLineCubicCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3) {
        return intersectLineCubicCurve(a0, a1, p0, p1, p2, p3, 1.0);
    }

    @NonNull
    public static Intersection intersectLineCubicCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3, double maxT) {
        final double a0x, a0y, a1x, a1y;
        a0x = a0.getX();
        a0y = a0.getY();
        a1x = a1.getX();
        a1y = a1.getY();

        final Point2D amin = topLeft(a0, a1); // used to determine if point is on line segment
        final Point2D amax = bottomRight(a0, a1); // used to determine if point is on line segment
        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        // Start with Bezier using Bernstein polynomials for weighting functions:
        //     (1-t^3)P0 + 3t(1-t)^2P1 + 3t^2(1-t)P2 + t^3P3
        //
        // Expand and collect terms to form linear combinations of original Bezier
        // controls.  This ends up with a vector cubic in t:
        //     (-P0+3P1-3P2+P3)t^3 + (3P0-6P1+3P2)t^2 + (-3P0+3P1)t + P0
        //             /\                  /\                /\       /\
        //             ||                  ||                ||       ||
        //             c3                  c2                c1       c0
        // Calculate the coefficients
        final Point2D c3, c2, c1, c0;   // coefficients of cubic
        c3 = p0.multiply(-1).add(p1.multiply(3).add(p2.multiply(-3).add(p3)));
        c2 = p0.multiply(3).add(p1.multiply(-6).add(p2.multiply(3)));
        c1 = p0.multiply(-3).add(p1.multiply(3));
        c0 = p0;

        // Convert line to normal form: ax + by + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D n;                // normal for normal form of line
        n = new Point2D(a0y - a1y, a1x - a0x);

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = a0x * a1y - a1x * a0y;

        // Rotate each cubic coefficient using line for new coordinate system
        // Find roots of rotated cubic
        final Polynomial polynomial = new Polynomial(
                n.dotProduct(c3),
                n.dotProduct(c2),
                n.dotProduct(c1),
                n.dotProduct(c0) + cl
        );
        double[] roots = polynomial.getRoots();

        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D p5, p6, p7, p8, p9, p10;
                p5 = lerp(p0, p1, t);
                p6 = lerp(p1, p2, t);
                p7 = lerp(p2, p3, t);

                p8 = lerp(p5, p6, t);
                p9 = lerp(p6, p7, t);

                p10 = lerp(p8, p9, t);

                // See if point is on line segment
                // Had to make special cases for vertical and horizontal lines due
                // to slight errors in calculation of p10
                double t1 = argumentOnLine(p10.getX(), p10.getY(), a0x, a0y, a1x, a1y);
                if (t1 >= 0 && t1 <= maxT) {
                    status = Intersection.Status.INTERSECTION;
                    result.add(new Intersection.IntersectionPoint(p10, t1));
                }
            }
        }

        return new Intersection(status, result);
    }

    /**
     * Computes the intersection between a line and a circle.
     * <p>
     * The intersection will contain the parameters 't' of the line in range
     * [0,1].
     *
     * @param c  the center of the circle
     * @param r  the radius of the circle
     * @param a0 point 0 of the line
     * @param a1 point 1 of the line
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectLineCircle(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D c, double r) {
        return intersectLineCircle(a0.getX(), a0.getY(), a1.getX(), a1.getY(), c.getX(), c.getY(), r);
    }

    /**
     * Computes the intersection between a line and a circle.
     * <p>
     * The intersection will contain the parameters 't' of the line in range
     * [0,1].
     * <p>
     * The intersection will have one of the following status:
     * <ul>
     * <li>{@link Status#INTERSECTION}</li>
     * <li>{@link Status#NO_INTERSECTION_INSIDE}</li>
     * <li>{@link Status#NO_INTERSECTION_OUTSIDE}</li>
     * </ul>
     * <p>
     * This method solves the following equation:
     * <pre>
     * {@literal x0 + (x1 - x0) · t, y0 + (y1 - y0) · t, with t in range [0,1] : line equation}
     * {@literal (x - cx)² + (y - cy)² = r²} : circle equation
     * {@literal (x0 + (x1 - x0) · t - cx)² + (y0 + (y1 - y0) · t - cy)² - r² =0} : intersection equation
     * {@literal (x0 + x1·t - x0·t - cx)² + (y0 + y1· t - y0· t - cy)² - r² =0}
     * {@literal -2·x0·x1·t²  + 2·x0·(cx+x1)·t - 2·x0*cx +(x0²+x1²)·t² - 2·(x0² - x1·cx)·t + x0² + cx²  ...+same for y...   - r² =0}
     * {@literal (x0²+-2·x0·x1+x1²)·t²  + (2·x0·(cx+x1)- 2·(x0² - x1·cx))·t  - 2·x0*cx + x0² + cx²  ...+same for y...   - r² =0}
     * {@literal (x1 - x0)²·t²  + 2·((x1 - x0)·(x0 - cx))·t  - 2·x0*cx + x0² + cx²  ...+same for y...   - r² =0}
     * {@literal (x1 - x0)²·(y1 - y0)²·t²  + 2·((x1 - x0)·(x0 - cx)+(y1 - y0)·(y0 - cy))·t - 2·(x0·cx + y0·cy) + cx² + cy² + x0² + y0²  - r² =0}
     * {@literal Δx²·Δy²·t²  + 2·(Δx·(x0 - cx)+Δy·(y0 - cy))·t - 2·(x0·cx + y0·cy) + cx² + cy² + x0² + y0²  - r² =0}
     * {@literal a·t² + b·t + c = 0 : quadratic polynomial, with t in range [0,1]}
     * </pre>
     *
     * @param x0 point 0 of the line
     * @param y0 point 0 of the line
     * @param x1 point 1 of the line
     * @param y1 point 1 of the line
     * @param cx the center of the circle
     * @param cy the center of the circle
     * @param r  the radius of the circle
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectLineCircle(double x0, double y0, double x1, double y1, double cx, double cy, double r) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        final double Δx, Δy;
        Δx = x1 - x0;
        Δy = y1 - y0;
        final double a, b, c, deter;
        a = Δx * Δx + Δy * Δy;
        b = 2 * (Δx * (x0 - cx) + Δy * (y0 - cy));
        c = cx * cx + cy * cy + x0 * x0 + y0 * y0 - 2 * (cx * x0 + cy * y0) - r * r;
        deter = b * b - 4 * a * c;

        Intersection.Status status;
        if (deter < -EPSILON) {
            status = Intersection.Status.NO_INTERSECTION_OUTSIDE;
        } else if (deter > 0) {
            final double e, t1, t2;
            e = Math.sqrt(deter);
            t1 = (-b + e) / (2 * a);
            t2 = (-b - e) / (2 * a);

            if ((t1 < 0 || t1 > 1) && (t2 < 0 || t2 > 1)) {
                if ((t1 < 0 && t2 < 0) || (t1 > 1 && t2 > 1)) {
                    status = Intersection.Status.NO_INTERSECTION_OUTSIDE;
                } else {
                    status = Intersection.Status.NO_INTERSECTION_INSIDE;
                }
            } else {
                status = Intersection.Status.INTERSECTION;
                if (0 <= t1 && t1 <= 1) {
                    result.add(new Intersection.IntersectionPoint(lerp(x0, y0, x1, y1, t1), t1));
                }
                if (0 <= t2 && t2 <= 1) {
                    result.add(new Intersection.IntersectionPoint(lerp(x0, y0, x1, y1, t2), t2));
                }
            }
        } else {
            double t = (-b) / (2 * a);
            if (0 <= t && t <= 1) {
                status = Intersection.Status.INTERSECTION;
                result.add(new Intersection.IntersectionPoint(lerp(x0, y0, x1, y1, t), t));
            } else {
                status = Intersection.Status.NO_INTERSECTION_OUTSIDE;
            }
        }

        return new Intersection(status, result);
    }

    /**
     * Computes the intersection between a line and an ellipse.
     * <p>
     * The intersection will contain the parameters 't1' of the line in range
     * [0,1].
     *
     * @param e  the bounds of the ellipse
     * @param a0 point 0 of the line
     * @param a1 point 1 of the line
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectLineEllipse(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Bounds e) {
        double rx = e.getWidth() * 0.5;
        double ry = e.getHeight() * 0.5;
        return intersectLineEllipse(a0, a1, new Point2D(e.getMinX() + rx, e.getMinY() + ry), rx, ry);
    }

    /**
     * Computes the intersection between a line and an ellipse.
     * <p>
     * The intersection will contain the parameters 't1' of the line in range
     * [0,1].
     *
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @param a0 point 0 of the line
     * @param a1 point 1 of the line
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectLineEllipse(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D ec, double rx, double ry) {
        return intersectLineEllipse(a0.getX(), a0.getY(), a1.getX(), a1.getY(), ec.getX(), ec.getY(), rx, ry);
    }

    @NonNull
    public static Intersection intersectLineEllipse(double x0, double y0, double x1, double y1,
                                                    double cx, double cy, double rx, double ry) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        final Point2D origin, dir, center, diff, mDir, mDiff;
        origin = new Point2D(x0, y0);
        dir = new Point2D(x1, y1).subtract(x0, y0);
        center = new Point2D(cx, cy);
        diff = origin.subtract(center);
        mDir = new Point2D(dir.getX() / (rx * rx), dir.getY() / (ry * ry));
        mDiff = new Point2D(diff.getX() / (rx * rx), diff.getY() / (ry * ry));

        final double a, b, c, d;
        a = dir.dotProduct(mDir);
        b = dir.dotProduct(mDiff);
        c = diff.dotProduct(mDiff) - 1.0;
        d = b * b - a * c;

        Intersection.Status status = Intersection.Status.NO_INTERSECTION;
        if (d < -EPSILON) {
            status = Intersection.Status.NO_INTERSECTION_OUTSIDE;
        } else if (d > 0) {
            final double root, t0, t1;
            root = Math.sqrt(d);
            t0 = (-b - root) / a;
            t1 = (-b + root) / a;

            if ((t0 < 0 || 1 < t0) && (t1 < 0 || 1 < t1)) {
                if ((t0 < 0 && t1 < 0) || (t0 > 1 && t1 > 1)) {
                    status = Intersection.Status.NO_INTERSECTION_OUTSIDE;
                } else {
                    status = Intersection.Status.NO_INTERSECTION_INSIDE;
                }
            } else {
                status = Intersection.Status.INTERSECTION;
                if (0 <= t0 && t0 <= 1) {
                    result.add(new Intersection.IntersectionPoint(lerp(x0, y0, x1, y1, t0), t0));
                }
                if (0 <= t1 && t1 <= 1) {
                    result.add(new Intersection.IntersectionPoint(lerp(x0, y0, x1, y1, t1), t1));
                }
            }
        } else {
            final double t = -b / a;
            if (0 <= t && t <= 1) {
                status = Intersection.Status.INTERSECTION;
                result.add(new Intersection.IntersectionPoint(lerp(x0, y0, x1, y1, t), t));
            } else {
                status = Intersection.Status.NO_INTERSECTION_OUTSIDE;
            }
        }

        return new Intersection(status, result);
    }


    /**
     * Intersects line 'a' with line 'b'.
     *
     * @param a0x start x coordinate of line 'a'
     * @param a0y start y coordinate of line 'a'
     * @param a1x end x coordinate of line 'a'
     * @param a1y end y coordinate of line 'a'
     * @param b0x start x coordinate of line 'b'
     * @param b0y start y coordinate of line 'b'
     * @param b1x end x coordinate of line 'b'
     * @param b1y end y coordinate of line 'b'
     * @return computed intersection with parameters of line 'a' at the intersection point
     */
    @NonNull
    public static Intersection intersectLineLine(
            double a0x, double a0y, double a1x, double a1y,
            double b0x, double b0y, double b1x, double b1y) {
        return intersectLineLine(a0x, a0y, a1x, a1y, b0x, b0y, b1x, b1y, 1.0);
    }

    /**
     * Computes the intersection of line 'a' with line 'b'.
     *
     * @param a0 start of line 'a'
     * @param a1 end of line 'a'
     * @param b0 start of line 'b'
     * @param b1 end of line 'b'
     * @return computed intersection with parameters of line 'a' at the intersection point
     */
    @NonNull
    public static Intersection intersectLineLine(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D b0, @NonNull Point2D b1) {
        return intersectLineLine(
                a0.getX(), a0.getY(),
                a1.getX(), a1.getY(),
                b0.getX(), b0.getY(),
                b1.getX(), b1.getY(), 1.0);
    }

    /**
     * Intersects ray 'a' with line 'b'.
     *
     * @param a0x  start x coordinate of line 'a'
     * @param a0y  start y coordinate of line 'a'
     * @param a1x  end x coordinate of line 'a'
     * @param a1y  end y coordinate of line 'a'
     * @param b0x  start x coordinate of line 'b'
     * @param b0y  start y coordinate of line 'b'
     * @param b1x  end x coordinate of line 'b'
     * @param b1y  end y coordinate of line 'b'
     * @param maxT maximal permitted value for the parameter t of ray 'a'
     * @return computed intersection with parameters t of ray 'a' at the intersection point
     */
    @NonNull
    public static Intersection intersectLineLine(
            double a0x, double a0y, double a1x, double a1y,
            double b0x, double b0y, double b1x, double b1y, double maxT) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status;

        double ua_t = (b1x - b0x) * (a0y - b0y) - (b1y - b0y) * (a0x - b0x);
        double ub_t = (a1x - a0x) * (a0y - b0y) - (a1y - a0y) * (a0x - b0x);
        double u_b = (b1y - b0y) * (a1x - a0x) - (b1x - b0x) * (a1y - a0y);

        if (u_b != 0) {
            double ua = ua_t / u_b;
            double ub = ub_t / u_b;

            if (0 <= ua && ua <= maxT && 0 <= ub && ub <= 1) {
                status = Intersection.Status.INTERSECTION;
                result.add(new Intersection.IntersectionPoint(new Point2D(
                        a0x + ua * (a1x - a0x),
                        a0y + ua * (a1y - a0y)
                ),
                        ua, new Point2D(a1x - a0x, a1y - a0y),
                        ub, new Point2D(b1x - b0x, b1y - b0y)
                ));
            } else {
                status = Intersection.Status.NO_INTERSECTION;
            }
        } else {
            if (ua_t == 0 || ub_t == 0) {
                status = Intersection.Status.NO_INTERSECTION_COINCIDENT;
            } else {
                status = Intersection.Status.NO_INTERSECTION_PARALLEL;
            }
        }

        return new Intersection(status, result);
    }

    @NonNull
    public static Intersection intersectLinePathIterator(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull PathIterator pit) {
        Intersection i = intersectLinePathIterator(a0, a1, pit, 1.0);
        if (i.getStatus() == Status.INTERSECTION && i.getFirstT() > 1) {
            return new Intersection(Status.NO_INTERSECTION, new ArrayList<>());
        } else {// FIXME remove intersections with t>1
            return i;
        }
    }

    @NonNull
    public static Intersection intersectLinePathIterator(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull PathIterator pit, double maxT) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        final double a0x, a0y, a1x, a1y;
        a0x = a0.getX();
        a0y = a0.getY();
        a1x = a1.getX();
        a1y = a1.getY();
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        int segmentIndex = 0;
        int intersectionCount = 0;
        boolean hasTangent = false;
        for (; !pit.isDone(); pit.next()) {
            Intersection inter;
            switch (pit.currentSegment(seg)) {
                case PathIterator.SEG_CLOSE:
                    inter = Intersections.intersectLineLine(a0x, a0y, a1x, a1y, lastx, lasty, firstx, firsty, Double.MAX_VALUE);
                    if (inter.getStatus() == Status.NO_INTERSECTION_COINCIDENT) hasTangent = true;
                    break;
                case PathIterator.SEG_CUBICTO:
                    x = seg[4];
                    y = seg[5];
                    inter = Intersections.intersectLineCubicCurve(a0x, a0y, a1x, a1y, lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, Double.MAX_VALUE);
                    lastx = x;
                    lasty = y;
                    break;
                case PathIterator.SEG_LINETO:
                    x = seg[0];
                    y = seg[1];
                    inter = Intersections.intersectLineLine(a0x, a0y, a1x, a1y, lastx, lasty, x, y, Double.MAX_VALUE);
                    if (inter.getStatus() == Status.NO_INTERSECTION_COINCIDENT) hasTangent = true;
                    lastx = x;
                    lasty = y;
                    break;
                case PathIterator.SEG_MOVETO:
                    inter = null;
                    lastx = firstx = seg[0];
                    lasty = firsty = seg[1];
                    break;
                case PathIterator.SEG_QUADTO:
                    x = seg[2];
                    y = seg[3];
                    inter = Intersections.intersectLineQuadraticCurve(a0x, a0y, a1x, a1y, lastx, lasty, seg[0], seg[1], x, y, Double.MAX_VALUE);
                    lastx = x;
                    lasty = y;
                    break;
                default:
                    inter = null;
                    break;
            }

            if (inter != null)
                for (final Intersection.IntersectionPoint intersection : inter.getIntersections()) {
                    intersectionCount++;
                    if (intersection.t1 <= maxT) {
                        intersection.setSegment2(segmentIndex);
                        result.add(intersection);
                    }
                }

            segmentIndex++;
        }

        Intersection.Status status;
        if (result.isEmpty()) {
            status = intersectionCount == 0 ? (hasTangent ? Status.NO_INTERSECTION_TANGENT : Status.NO_INTERSECTION_OUTSIDE) : Status.NO_INTERSECTION_INSIDE;
        } else {
            status = Status.INTERSECTION;
        }

        return new Intersection(status, result);
    }

    @NonNull
    public static Intersection intersectPathIteratorLine(@NonNull PathIterator pit, @NonNull Point2D a0, @NonNull Point2D a1) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;
        final double a0x, a0y, a1x, a1y;
        a0x = a0.getX();
        a0y = a0.getY();
        a1x = a1.getX();
        a1y = a1.getY();
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        for (; !pit.isDone(); pit.next()) {
            Intersection inter;
            switch (pit.currentSegment(seg)) {
                case PathIterator.SEG_CLOSE:
                    inter = Intersections.intersectLineLine(lastx, lasty, firstx, firsty, a0x, a0y, a1x, a1y);
                    result.addAll(inter.getIntersections());
                    break;
                case PathIterator.SEG_CUBICTO:
                    x = seg[4];
                    y = seg[5];
                    inter = Intersections.intersectCubicCurveLine(lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, a0x, a0y, a1x, a1y);
                    result.addAll(inter.getIntersections());
                    lastx = x;
                    lasty = y;
                    break;
                case PathIterator.SEG_LINETO:
                    x = seg[0];
                    y = seg[1];
                    inter = Intersections.intersectLineLine(lastx, lasty, x, y, a0x, a0y, a1x, a1y);
                    result.addAll(inter.getIntersections());
                    lastx = x;
                    lasty = y;
                    break;
                case PathIterator.SEG_MOVETO:
                    lastx = firstx = seg[0];
                    lasty = firsty = seg[1];
                    break;
                case PathIterator.SEG_QUADTO:
                    x = seg[2];
                    y = seg[3];
                    inter = Intersections.intersectQuadraticCurveLine(lastx, lasty, seg[0], seg[1], x, y, a0x, a0y, a1x, a1y);
                    result.addAll(inter.getIntersections());
                    lastx = x;
                    lasty = y;
                    break;
            }
        }

        return new Intersection(result);
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
     * <li>{@link Status#INTERSECTION}</li>
     * <li>{@link Status#NO_INTERSECTION}</li>
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
    @NonNull
    public static Intersection intersectLinePoint(double x0, double y0, double x1, double y1, double cx, double cy, double r) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
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
                result.add(new Intersection.IntersectionPoint(new Point2D(x, y), t));
            }
        }

        return new Intersection(result);
    }

    /**
     * Computes the intersection between a line and a polygon.
     * <p>
     * The intersection will contain the parameters 't1' of the line in range
     * [0,1].
     *
     * @param a0     point 0 of the line
     * @param a1     point 1 of the line
     * @param points the points of the polygon
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectLinePolygon(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull List<Point2D> points) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D b0 = points.get(i);
            Point2D b1 = points.get((i + 1) % length);
            Intersection inter = Intersections.intersectLineLine(a0, a1, b0, b1);

            result.addAll(inter.getIntersections());
        }

        return new Intersection(result);
    }

    /**
     * Computes the intersection between a line and a rectangle.
     * <p>
     * The intersection will contain the parameters 't1' of the line in range
     * [0,1].
     *
     * @param a0 point 0 of the line
     * @param a1 point 1 of the line
     * @param r0 corner point 0 of the rectangle
     * @param r1 corner point 1 of the rectangle
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectLineRectangle(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D r0, @NonNull Point2D r1) {
        return intersectLineRectangle(a0, a1,
                Math.min(r0.getX(), r1.getX()),
                Math.min(r0.getY(), r1.getY()),
                Math.max(r0.getX(), r1.getX()),
                Math.max(r0.getY(), r1.getY()));
    }


    @NonNull
    public static Intersection intersectLineRectangle(@NonNull Point2D a0, @NonNull Point2D a1,
                                                      double rminx, double rminy, double rmaxx, double rmaxy) {

        final Point2D topLeft, bottomRight, topRight, bottomLeft;
        topLeft = new Point2D(rminx, rminy);
        bottomRight = new Point2D(rmaxx, rmaxy);
        topRight = new Point2D(rmaxx, rminy);
        bottomLeft = new Point2D(rminx, rmaxy);

        final Intersection inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectLineLine(a0, a1, topLeft, topRight);
        inter2 = Intersections.intersectLineLine(a0, a1, topRight, bottomRight);
        inter3 = Intersections.intersectLineLine(a0, a1, bottomRight, bottomLeft);
        inter4 = Intersections.intersectLineLine(a0, a1, bottomLeft, topLeft);

        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;

        result.addAll(inter1.getIntersections());
        result.addAll(inter2.getIntersections());
        result.addAll(inter3.getIntersections());
        result.addAll(inter4.getIntersections());

        return new Intersection(result);
    }

    @NonNull
    public static Intersection intersectLineRectangle(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Bounds r) {
        return intersectLineRectangle(a0, a1, r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
    }


    @NonNull
    public static Intersection intersectRectangleLine(@NonNull Bounds r, @NonNull Point2D a0, @NonNull Point2D a1) {
        return intersectRectangleLine(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY(), a0, a1);
    }

    @NonNull
    public static Intersection intersectRectangleLine(
            double rminx, double rminy, double rmaxx, double rmaxy,
            @NonNull Point2D a0, @NonNull Point2D a1) {

        final Point2D topLeft, bottomRight, topRight, bottomLeft;
        topLeft = new Point2D(rminx, rminy);
        bottomRight = new Point2D(rmaxx, rmaxy);
        topRight = new Point2D(rmaxx, rminy);
        bottomLeft = new Point2D(rminx, rmaxy);

        final Intersection inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectLineLine(topRight, topLeft, a0, a1);
        inter2 = Intersections.intersectLineLine(bottomRight, topRight, a0, a1);
        inter3 = Intersections.intersectLineLine(bottomLeft, bottomRight, a0, a1);
        inter4 = Intersections.intersectLineLine(topLeft, bottomLeft, a0, a1);

        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;

        result.addAll(inter1.getIntersections());
        result.addAll(inter2.getIntersections());
        result.addAll(inter3.getIntersections());
        result.addAll(inter4.getIntersections());

        return new Intersection(result);
    }

    @NonNull
    public static Intersection intersectPathIteratorCircle(@NonNull PathIterator pit, double cx, double cy, double r) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        for (; !pit.isDone(); pit.next()) {
            Intersection inter;
            switch (pit.currentSegment(seg)) {
                case PathIterator.SEG_CLOSE:
                    inter = Intersections.intersectLineCircle(lastx, lasty, firstx, firsty, cx, cy, r);
                    // FIXME add segment number to t
                    result.addAll(inter.getIntersections());
                    break;
                case PathIterator.SEG_CUBICTO:
                    x = seg[4];
                    y = seg[5];
                    inter = Intersections.intersectCubicCurveCircle(lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, cx, cy, r);
                    // FIXME add segment number to t
                    result.addAll(inter.getIntersections());
                    lastx = x;
                    lasty = y;
                    break;
                case PathIterator.SEG_LINETO:
                    x = seg[0];
                    y = seg[1];
                    inter = Intersections.intersectLineCircle(lastx, lasty, x, y, cx, cy, r);
                    // FIXME add segment number to t
                    result.addAll(inter.getIntersections());
                    lastx = x;
                    lasty = y;
                    break;
                case PathIterator.SEG_MOVETO:
                    lastx = firstx = seg[0];
                    lasty = firsty = seg[1];
                    break;
                case PathIterator.SEG_QUADTO:
                    x = seg[2];
                    y = seg[3];
                    inter = Intersections.intersectQuadraticCurveCircle(lastx, lasty, seg[0], seg[1], x, y, cx, cy, r);
                    // FIXME add segment number to t
                    result.addAll(inter.getIntersections());
                    lastx = x;
                    lasty = y;
                    break;
            }
        }

        return new Intersection(result);
    }

    /**
     * Intersects the given path iterator with the given point.
     *
     * @param pit       the path iterator
     * @param px        the x-coordinate of the point
     * @param py        the y-coordinate of the point
     * @param tolerance radius around the point which counts as hit.
     * @return the intersection
     */
    @NonNull
    public static Intersection intersectPathIteratorPoint(@NonNull PathIterator pit, double px, double py, double tolerance) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        int i = 0;
        double closestDistance = Double.POSITIVE_INFINITY;
        for (; !pit.isDone(); pit.next(), i++) {
            Intersection inter;
            switch (pit.currentSegment(seg)) {
                case PathIterator.SEG_CLOSE:
                    inter = Intersections.intersectLinePoint(lastx, lasty, firstx, firsty, px, py, tolerance);
                    break;
                case PathIterator.SEG_CUBICTO:
                    x = seg[4];
                    y = seg[5];
                    inter = Intersections.intersectCubicCurvePoint(lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, px, py, tolerance);
                    lastx = x;
                    lasty = y;
                    break;
                case PathIterator.SEG_LINETO:
                    x = seg[0];
                    y = seg[1];
                    inter = Intersections.intersectLinePoint(lastx, lasty, x, y, px, py, tolerance);
                    lastx = x;
                    lasty = y;
                    break;
                case PathIterator.SEG_MOVETO:
                    lastx = firstx = seg[0];
                    lasty = firsty = seg[1];
                    inter = null;
                    break;
                case PathIterator.SEG_QUADTO:
                    x = seg[2];
                    y = seg[3];
                    inter = Intersections.intersectQuadraticCurvePoint(lastx, lasty, seg[0], seg[1], x, y, px, py, tolerance);
                    lastx = x;
                    lasty = y;
                    break;
                default:
                    inter = null;
                    break;
            }
            if (inter != null) {
                for (Intersection.IntersectionPoint entry : inter.getIntersections()) {
                    final double dd = Geom.squaredDistance(entry.getPoint(), px, py);
                    Intersection.IntersectionPoint newPoint = new Intersection.IntersectionPoint(entry.getPoint(), entry.getT1() + i);
                    newPoint.setSegment1(i);
                    if (abs(dd - closestDistance) < EPSILON) {
                        result.add(newPoint);
                    } else if (dd < closestDistance) {
                        result.clear();
                        closestDistance = dd;
                        result.add(newPoint);
                    }
                }
            }

        }

        // FIXME the result should contain only one point
        return new Intersection(result);
    }

    /**
     * Computes the intersection between a point and a circle.
     *
     * @param point  the point
     * @param center the center of the circle
     * @param radius the radius of the circle
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectPointCircle(@NonNull Point2D point, @NonNull Point2D center, double radius) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        final double distance = point.distance(center);

        Intersection.Status status;
        if (distance - radius < EPSILON) {
            status = Intersection.Status.INTERSECTION;
            // FIXME compute t with atan2/2*PI
            result.add(new Intersection.IntersectionPoint(point, 0.0));
        } else if (distance < radius) {
            status = Intersection.Status.NO_INTERSECTION_INSIDE;
        } else {
            status = Intersection.Status.NO_INTERSECTION_OUTSIDE;
        }
        return new Intersection(status, result);
    }

    /**
     * Computes the intersection between a point and an ellipse.
     *
     * @param point  the point
     * @param center the center of the ellipse
     * @param rx     the x-radius of ellipse
     * @param ry     the y-radius of ellipse
     * @return computed intersection. Status can be{@link Status#INTERSECTION},
     * Status#NO_INTERSECTION_INSIDE or Status#NO_INTERSECTION_OUTSIDE}.
     */
    @NonNull
    public static Intersection intersectPointEllipse(@NonNull Point2D point, @NonNull Point2D center, double rx, double ry) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();

        double px = point.getX();
        double py = point.getY();
        double cx = center.getX();
        double cy = center.getY();

        double det = (px - cx) * (px - cx) / (rx * rx) + (py - py) * (py - py) / (ry * ry);
        Intersection.Status status;
        if (abs(det) - 1 == EPSILON) {
            status = Intersection.Status.INTERSECTION;
            result.add(new Intersection.IntersectionPoint(point, 0.0));
        } else if (det < 1) {
            status = Intersection.Status.NO_INTERSECTION_INSIDE;
        } else {
            status = Intersection.Status.NO_INTERSECTION_OUTSIDE;
        }

        return new Intersection(status, result);
    }

    /**
     * Computes the intersection between two polygons.
     *
     * @param points1 the points of the first polygon
     * @param points2 the points of the second polygon
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectPolygonPolygon(@NonNull List<Point2D> points1, @NonNull List<Point2D> points2) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;
        int length = points1.size();

        for (int i = 0; i < length; i++) {
            Point2D a1 = points1.get(i);
            Point2D a2 = points1.get((i + 1) % length);
            Intersection inter = Intersections.intersectLinePolygon(a1, a2, points2);

            result.addAll(inter.getIntersections());
        }

        return new Intersection(result);

    }

    /**
     * Computes the intersection between a polygon and a rectangle.
     *
     * @param points the points of the polygon
     * @param r0     corner point 0 of the rectangle
     * @param r1     corner point 1 of the rectangle
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectPolygonRectangle(@NonNull List<Point2D> points, @NonNull Point2D r0, @NonNull Point2D r1) {
        final Point2D topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(r0, r1);
        bottomRight = bottomRight(r0, r1);
        topRight = new Point2D(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D(topLeft.getX(), bottomRight.getY());

        final Intersection inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectLinePolygon(topLeft, topRight, points);
        inter2 = Intersections.intersectLinePolygon(topRight, bottomRight, points);
        inter3 = Intersections.intersectLinePolygon(bottomRight, bottomLeft, points);
        inter4 = Intersections.intersectLinePolygon(bottomLeft, topLeft, points);

        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;

        result.addAll(inter1.getIntersections());
        result.addAll(inter2.getIntersections());
        result.addAll(inter3.getIntersections());
        result.addAll(inter4.getIntersections());

        return new Intersection(result);
    }

    /**
     * Computes the intersection between two infinitely long rays 'a' and 'b'.
     * <p>
     * The intersection will contain the parameters 't' of ray 'a' in range
     * [0,MAX_VALUE].
     *
     * @param a0 point 0 of ray 'a'
     * @param a1 point 1 of ray 'a'
     * @param b0 point 0 of ray 'a'
     * @param b1 point 1 of ray 'b'
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectRayRay(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D b0, @NonNull Point2D b1) {
        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status;

        final double a0y, b1x, b0x, b0y, b1y, a0x, a1x, a1y;
        a0y = a0.getY();
        b1x = b1.getX();
        b0x = b0.getX();
        b0y = b0.getY();
        b1y = b1.getY();
        a0x = a0.getX();
        a1x = a1.getX();
        a1y = a1.getY();

        double ua_t = (b1x - b0x) * (a0y - b0y) - (b1y - b0y) * (a0x - b0x);
        double ub_t = (a1x - a0x) * (a0y - b0y) - (a1y - a0y) * (a0x - b0x);
        double u_b = (b1y - b0y) * (a1x - a0x) - (b1x - b0x) * (a1y - a0y);

        if (u_b != 0) {
            double ua = ua_t / u_b;

            status = Intersection.Status.INTERSECTION;
            result.add(new Intersection.IntersectionPoint(new Point2D(
                    a0x + ua * (a1x - a0x),
                    a0y + ua * (a1y - a0y)
            ), ua
            ));
        } else {
            if (ua_t == 0 || ub_t == 0) {
                status = Intersection.Status.NO_INTERSECTION_COINCIDENT;
            } else {
                status = Intersection.Status.NO_INTERSECTION_PARALLEL;
            }
        }

        return new Intersection(status, result);
    }

    @NonNull
    public static Intersection intersectRectangleRectangle(double ax, double ay, double aw, double ah,
                                                           double bx, double by, double bw, double bh) {
        return intersectRectangleRectangle(
                new Point2D(ax, ay), new Point2D(ax + aw, ay + ah),
                new Point2D(bx, by), new Point2D(bx + bw, by + bh));

    }

    /**
     * Computes the intersection between two rectangles 'a' and 'b'.
     *
     * @param a0 corner point 0 of rectangle 'a'
     * @param a1 corner point 1 of rectangle 'a'
     * @param b0 corner point 0 of rectangle 'b'
     * @param b1 corner point 1 of rectangle 'b'
     * @return computed intersection
     */
    @NonNull
    public static Intersection intersectRectangleRectangle(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D b0, @NonNull Point2D b1) {
        final Point2D topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(a0, a1);
        bottomRight = bottomRight(a0, a1);
        topRight = new Point2D(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D(topLeft.getX(), bottomRight.getY());

        final Intersection inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectLineRectangle(topLeft, topRight, b0, b1);
        inter2 = Intersections.intersectLineRectangle(topRight, bottomRight, b0, b1);
        inter3 = Intersections.intersectLineRectangle(bottomRight, bottomLeft, b0, b1);
        inter4 = Intersections.intersectLineRectangle(bottomLeft, topLeft, b0, b1);

        List<Intersection.IntersectionPoint> result = new ArrayList<>();
        Intersection.Status status = Intersection.Status.NO_INTERSECTION;

        result.addAll(inter1.getIntersections());
        result.addAll(inter2.getIntersections());
        result.addAll(inter3.getIntersections());
        result.addAll(inter4.getIntersections());

        return new Intersection(result);
    }


    /**
     * Returns true if point 'a' is less or equal to point 'b'. Compares the
     * x-coordinates first, and if they are equal compares the y-coordinates.
     *
     * @param a point a
     * @param b point b
     * @return true if a is less or equal b
     */
    private static boolean lte(@NonNull Point2D a, @NonNull Point2D b) {
        return a.getX() <= b.getX() && a.getY() <= b.getY();
    }

    /**
     * Computes the coordinates of the top left corner of a rectangle given two
     * corner points defining the extrema of the rectangle.
     *
     * @param a corner point a
     * @param b corner point b
     * @return the top left corner
     */
    @NonNull
    private static Point2D topLeft(@NonNull Point2D a, @NonNull Point2D b) {
        return new Point2D(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()));
    }

    @NonNull
    private static Point2D topLeft(double ax, double ay, double bx, double by) {
        return new Point2D(Math.min(ax, bx), Math.min(ay, by));
    }


}
