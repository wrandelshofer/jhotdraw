/*
 * @(#)Intersections.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.isect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.DoubleArrayList;
import org.jhotdraw8.geom.AABB;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.jhotdraw8.geom.Geom.argumentOnLine;
import static org.jhotdraw8.geom.Geom.lerp;
import static org.jhotdraw8.geom.Geom.lineContainsPoint;

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
    private static Point2D.Double bottomRight(@NonNull Point2D a, @NonNull Point2D b) {
        return new Point2D.Double(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()));
    }

    @NonNull
    private static Point2D.Double bottomRight(double ax, double ay, double bx, double by) {
        return new Point2D.Double(Math.max(ax, bx), Math.max(ay, by));
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
    public static IntersectionResultEx intersectQuadraticCurveQuadraticCurveEx(double ax0, double ay0, double ax1, double ay1, double ax2, double ay2,
                                                                               double bx0, double by0, double bx1, double by1, double bx2, double by2) {
        return intersectQuadraticCurveQuadraticCurveEx(new Point2D.Double(ax0, ay0), new Point2D.Double(ax1, ay1), new Point2D.Double(ax2, ay2),
                new Point2D.Double(bx0, by0), new Point2D.Double(bx1, by1), new Point2D.Double(bx2, by2));

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
    public static IntersectionResultEx intersectQuadraticCurveQuadraticCurveEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2, @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2) {
        final Point2D c12, c11, c10;
        final Point2D c22, c21, c20;
        final Polynomial poly;

        c12 = Points2D.add(a0, Points2D.add(Points2D.multiply(a1, -2), a2));
        c11 = Points2D.add(Points2D.multiply(a0, -2), Points2D.multiply(a1, 2));
        c10 = a0;
        c22 = Points2D.add(b0, Points2D.add(Points2D.multiply(b1, -2), b2));
        c21 = Points2D.add(Points2D.multiply(b0, -2), Points2D.multiply(b1, 2));
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

        List<IntersectionPointEx> result = new ArrayList<>();
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
                                    result.add(new IntersectionPointEx(
                                            Points2D.add(Points2D.multiply(c22, s * s), Points2D.add(Points2D.multiply(c21, s), c20)), xRoot));
                                    break checkRoots;
                                }
                            }
                        }
                    }
                }
            }
        }

        return new IntersectionResultEx(result);
    }

    @NonNull
    public static IntersectionResultEx intersectQuadraticCurveCubicCurveEx(double ax0, double ay0, double ax1, double ay1, double ax2, double ay2,
                                                                           double bx0, double by0, double bx1, double by1, double bx2, double by2, double bx3, double by3) {
        return intersectQuadraticCurveCubicCurveEx(new Point2D.Double(ax0, ay0), new Point2D.Double(ax1, ay1), new Point2D.Double(ax2, ay2),
                new Point2D.Double(bx0, by0), new Point2D.Double(bx1, by1), new Point2D.Double(bx2, by2), new Point2D.Double(bx3, by3));

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
    public static IntersectionResultEx intersectQuadraticCurveCubicCurveEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2,
                                                                           @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2, @NonNull Point2D b3) {
        final Point2D c12, c11, c10;
        final Point2D c23, c22, c21, c20;
        c12 = Points2D.add(a0, Points2D.add(Points2D.multiply(a1, -2), a2));
        c11 = Points2D.add(Points2D.multiply(a0, -2), Points2D.multiply(a1, 2));
        c10 = new Point2D.Double(a0.getX(), a0.getY());
        c23 = Points2D.sum(Points2D.multiply(b0, -1), Points2D.multiply(b1, 3), Points2D.multiply(b2, -3), b3);
        c22 = Points2D.sum(Points2D.multiply(b0, 3), Points2D.multiply(b1, -6), Points2D.multiply(b2, 3));
        c21 = Points2D.add(Points2D.multiply(b0, -3), Points2D.multiply(b1, 3));
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
        final DoubleArrayList roots = poly.getRootsInInterval(0, 1);

        List<IntersectionPointEx> result = new ArrayList<>();
        for (int i = 0; i < roots.size(); i++) {
            double s = roots.get(i);
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
                                result.add(
                                        new IntersectionPointEx(
                                                Points2D.sum(
                                                        Points2D.multiply(c23, s * s * s),
                                                        Points2D.multiply(c22, s * s),
                                                        Points2D.multiply(c21, s), c20), xRoot));
                                break checkRoots;
                            }
                        }
                    }
                }
            }
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectQuadraticCurveCircleEx(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D c, double r) {
        return Intersections.intersectQuadraticCurveEllipseEx(p0, p1, p2, c, r, r);
    }

    @NonNull
    public static IntersectionResultEx intersectQuadraticCurveCircleEx(
            double x0, double y0, double x1, double y1, double x2, double y2,
            double cx, double cy, double r) {
        return Intersections.intersectQuadraticCurveEllipseEx(new Point2D.Double(x0, y0), new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(cx, cy), r, r);
    }

    @NonNull
    public static IntersectionResultEx intersectQuadraticCurveEllipseEx(
            double x0, double y0, double x1, double y1, double x2, double y2,
            double cx, double cy, double rx, double ry) {
        return intersectQuadraticCurveEllipseEx(new Point2D.Double(x0, y0), new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(cx, cy), rx, ry);
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
     * @return the computed result. Status can be{@link IntersectionStatus#INTERSECTION},
     * Status#NO_INTERSECTION_INSIDE or Status#NO_INTERSECTION_OUTSIDE}.
     */
    @NonNull
    public static IntersectionResultEx intersectQuadraticCurveEllipseEx(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D c, double rx, double ry) {
        final Point2D.Double c2, c1, c0; // coefficients of quadratic
        c2 = Points2D.sum(p0, Points2D.multiply(p1, -2), p2);
        c1 = Points2D.add(Points2D.multiply(p0, -2), Points2D.multiply(p1, 2));
        c0 = new Point2D.Double(p0.getX(), p0.getY());

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

        List<IntersectionPointEx> result = new ArrayList<>();
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                result.add(new IntersectionPointEx(
                        Points2D.sum(Points2D.multiply(c2, t * t), Points2D.multiply(c1, t), c0), t));
            }
        }

        IntersectionStatus status;
        if (result.size() > 0) {
            status = IntersectionStatus.INTERSECTION;
        } else {
            return intersectPointEllipseEx(p0, c, rx, ry);
        }

        return new IntersectionResultEx(status, result);
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
    public static IntersectionResultEx intersectQuadraticCurveLineEx(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D a0, @NonNull Point2D a1) {

        // Bezier curve:
        //   (1 - t)²·p0 + 2·(1 - t)·t·p1 + t²·p2 , 0 ≤ t ≤ 1
        //   (p0 - 2·p1 + p2)·t² - 2·(p0 - p1)·t + p0
        //   c2·t² + c1·t + c0
        final Point2D c2, c1, c0;       // coefficients of quadratic
        c2 = Points2D.sum(p0, Points2D.multiply(p1, -2), p2);
        c1 = Points2D.multiply(Points2D.subtract(p0, p1), -2);
        c0 = p0;

        final double a0x, a0y, a1x, a1y;
        a0x = a0.getX();
        a1x = a1.getX();
        a0y = a0.getY();
        a1y = a1.getY();

        // Convert line to normal form: a·x + b·y + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D.Double n;                // normal for normal form of line
        n = new Point2D.Double(a0y - a1y, a1x - a0x);

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = a0x * a1y - a1x * a0y;

        // Transform cubic coefficients to line's coordinate system and find roots
        // of cubic
        final double[] roots = new Polynomial(
                Points2D.dotProduct(n, c2),
                Points2D.dotProduct(n, c1),
                Points2D.dotProduct(n, c0) + cl
        ).getRoots();

        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        final Point2D.Double topLeft, bottomRight;
        topLeft = topLeft(a0, a1); // used to determine if point is on line segment
        bottomRight = bottomRight(a0, a1); // used to determine if point is on line segment
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D.Double p4, p5, p6;
                p4 = lerp(p0, p1, t);
                p5 = lerp(p1, p2, t);
                p6 = lerp(p4, p5, t);

                // See if point is on line segment
                // Had to make special cases for vertical and horizontal lines due
                // to slight errors in calculation of p6
                if (a0x == a1x) {
                    if (topLeft.getY() <= p6.getY() && p6.getY() <= bottomRight.getY()) {
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(p6, t));
                    }
                } else if (a0y == a1y) {
                    if (topLeft.getX() <= p6.getX() && p6.getX() <= bottomRight.getX()) {
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(p6, t));
                    }
                } else if (gte(p6, topLeft) && lte(p6, bottomRight)) {
                    status = IntersectionStatus.INTERSECTION;
                    result.add(new IntersectionPointEx(p6, t));
                }
            }
        }

        return new IntersectionResultEx(status, result);
    }

    @NonNull
    public static IntersectionResultEx intersectQuadraticCurveLineEx(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2,
            double bx0, double by0, double bx1, double by1) {
        return intersectQuadraticCurveLineEx(new Point2D.Double(ax0, ay0), new Point2D.Double(ax1, ay1), new Point2D.Double(ax2, ay2),
                new Point2D.Double(bx0, by0), new Point2D.Double(bx1, by1));
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
    public static IntersectionResultEx intersectQuadraticCurvePointEx(
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
        final List<IntersectionPointEx> result = new ArrayList<>();
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
                if (abs(dd - bestDistance) < EPSILON) {
                    result.add(new IntersectionPointEx(p, tt));
                } else if (dd < bestDistance) {
                    bestDistance = dd;
                    result.clear();
                    result.add(new IntersectionPointEx(p, tt));
                }
            }
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectQuadraticCurvePolygonEx(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull List<Point2D.Double> points) {
        List<IntersectionPointEx> result = new ArrayList<>();
        int length = points.size();

        for (int i = 0; i < length; i++) {
            final Point2D.Double a0, a1;
            a0 = points.get(i);
            a1 = points.get((i + 1) % length);
            IntersectionResultEx inter = Intersections.intersectQuadraticCurveLineEx(p0, p1, p2, a0, a1);

            result.addAll(inter.asList());
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectQuadraticCurveRectangleEx(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D r0, @NonNull Point2D r1) {
        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(r0, r1);
        bottomRight = bottomRight(r0, r1);
        topRight = new Point2D.Double(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D.Double(topLeft.getX(), bottomRight.getY());

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectQuadraticCurveLineEx(p0, p1, p2, topLeft, topRight);
        inter2 = Intersections.intersectQuadraticCurveLineEx(p0, p1, p2, topRight, bottomRight);
        inter3 = Intersections.intersectQuadraticCurveLineEx(p0, p1, p2, bottomRight, bottomLeft);
        inter4 = Intersections.intersectQuadraticCurveLineEx(p0, p1, p2, bottomLeft, topLeft);

        final List<IntersectionPointEx> result = new ArrayList<>();
        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        return new IntersectionResultEx(result);
    }

    @NonNull
    public static IntersectionResultEx intersectCubicCurveQuadraticCurveEx(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3,
            double bx0, double by0, double bx1, double by1, double bx2, double by2) {
        IntersectionResultEx isect = intersectQuadraticCurveCubicCurveEx(
                new Point2D.Double(bx0, by0), new Point2D.Double(bx1, by1), new Point2D.Double(bx2, by2),
                new Point2D.Double(ax0, ay0), new Point2D.Double(ax1, ay1), new Point2D.Double(ax2, ay2),
                new Point2D.Double(ax3, ay3));
        // FIXME compute t for a instead for b
        return isect;

    }

    @NonNull
    public static IntersectionResultEx intersectCubicCurveCubicCurveEx(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3,
            double bx0, double by0, double bx1, double by1, double bx2, double by2, double bx3, double by3) {
        return intersectCubicCurveCubicCurveEx(new Point2D.Double(ax0, ay0), new Point2D.Double(ax1, ay1), new Point2D.Double(ax2, ay2), new Point2D.Double(ax3, ay3),
                new Point2D.Double(bx0, by0), new Point2D.Double(bx1, by1), new Point2D.Double(bx2, by2), new Point2D.Double(bx3, by3));

    }

    @NonNull
    public static IntersectionResultEx intersectCubicCurveCubicCurveEx(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3,
            double bx0, double by0, double bx1, double by1, double bx2, double by2, double bx3, double by3,
            double tMin, double tMax) {
        return intersectCubicCurveCubicCurveEx(new Point2D.Double(ax0, ay0), new Point2D.Double(ax1, ay1), new Point2D.Double(ax2, ay2), new Point2D.Double(ax3, ay3),
                new Point2D.Double(bx0, by0), new Point2D.Double(bx1, by1), new Point2D.Double(bx2, by2), new Point2D.Double(bx3, by3),
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
    public static IntersectionResultEx intersectCubicCurveCubicCurveEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2, @NonNull Point2D a3,
                                                                       @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2, @NonNull Point2D b3) {
        return intersectCubicCurveCubicCurveEx(a0, a1, a2, a3, b0, b1, b2, b3, 0, 1);
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
    public static IntersectionResultEx intersectCubicCurveCubicCurveEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2, @NonNull Point2D a3,
                                                                       @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2, @NonNull Point2D b3, double tMin, double tMax) {
        List<IntersectionPointEx> result = new ArrayList<>();

        // Calculate the coefficients of cubic polynomial
        final Point2D c13, c12, c11, c10;
        final Point2D c23, c22, c21, c20;
        c13 = Points2D.sum(Points2D.multiply(a0, -1), Points2D.multiply(a1, 3), Points2D.multiply(a2, -3), a3);
        c12 = Points2D.sum(Points2D.multiply(a0, 3), Points2D.multiply(a1, -6), Points2D.multiply(a2, 3));
        c11 = Points2D.add(Points2D.multiply(a0, -3), Points2D.multiply(a1, 3));
        c10 = a0;
        c23 = Points2D.sum(Points2D.multiply(b0, -1), Points2D.multiply(b1, 3), Points2D.multiply(b2, -3), b3);
        c22 = Points2D.sum(Points2D.multiply(b0, 3), Points2D.multiply(b1, -6), Points2D.multiply(b2, 3));
        c21 = Points2D.add(Points2D.multiply(b0, -3), Points2D.multiply(b1, 3));
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

        final DoubleArrayList roots = poly.getRootsInInterval(tMin, tMax);

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
                                result.add(new IntersectionPointEx(
                                        Points2D.sum(
                                                Points2D.multiply(c23, s * s * s),
                                                Points2D.multiply(c22, s * s),
                                                Points2D.multiply(c21, s), c20),
                                        xRoot));
                                break checkRoots;
                            }
                        }
                    }
                }
            }
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectCubicCurveCircleEx(
            @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3,
            @NonNull Point2D c, double r) {
        return Intersections.intersectCubicCurveEllipseEx(p0, p1, p2, p3, c, r, r);
    }

    @NonNull
    public static IntersectionResultEx intersectCubicCurveCircleEx(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy, double r) {
        return Intersections.intersectCubicCurveEllipseEx(new Point2D.Double(x0, y0), new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(x3, y3), new Point2D.Double(cx, cy), r, r);
    }

    @NonNull
    public static IntersectionResultEx intersectCubicCurveEllipseEx(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy, double rx, double ry) {
        return intersectCubicCurveEllipseEx(
                new Point2D.Double(x0, y0), new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(x3, y3),
                new Point2D.Double(cx, cy), rx, ry);

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
     * @return the computed result. Status can be{@link IntersectionStatus#INTERSECTION},
     * Status#NO_INTERSECTION_INSIDE or Status#NO_INTERSECTION_OUTSIDE}.
     */
    @NonNull
    public static IntersectionResultEx intersectCubicCurveEllipseEx(
            @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3,
            @NonNull Point2D ec, double rx, double ry) {
        Point2D.Double a, b, c, d;       // temporary variables
        List<IntersectionPointEx> result = new ArrayList<>();

        // Calculate the coefficients of cubic polynomial
        final Point2D c3, c2, c1, c0;
        c3 = Points2D.sum(Points2D.multiply(p0, -1), Points2D.multiply(p1, 3), Points2D.multiply(p2, -3), p3);
        c2 = Points2D.sum(Points2D.multiply(p0, 3), Points2D.multiply(p1, -6), Points2D.multiply(p2, 3));
        c1 = Points2D.add(Points2D.multiply(p0, -3), Points2D.multiply(p1, 3));
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
        DoubleArrayList roots = poly.getRootsInInterval(0, 1);

        for (int i = 0; i < roots.size(); i++) {
            double t = roots.get(i);

            result.add(new IntersectionPointEx(
                    Points2D.sum(Points2D.multiply(c3, t * t * t), Points2D.multiply(c2, t * t), Points2D.multiply(c1, t), c0), t));
        }

        if (result.size() > 0) {
            return new IntersectionResultEx(result);
        } else {
            return intersectPointEllipseEx(p0, ec, rx, ry);// Computes inside/outside status
        }

    }

    @NonNull
    public static IntersectionResultEx intersectCubicCurveLineEx(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3,
            double bx0, double by0, double bx1, double by1) {
        return intersectCubicCurveLineEx(new Point2D.Double(ax0, ay0), new Point2D.Double(ax1, ay1), new Point2D.Double(ax2, ay2), new Point2D.Double(ax3, ay3),
                new Point2D.Double(bx0, by0), new Point2D.Double(bx1, by1));
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
    public static IntersectionResultEx intersectCubicCurveLineEx(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3, @NonNull Point2D a0, @NonNull Point2D a1) {
        final Point2D.Double topLeft = topLeft(a0, a1); // used to determine if point is on line segment
        final Point2D.Double bottomRight = bottomRight(a0, a1); // used to determine if point is on line segment
        List<IntersectionPointEx> result = new ArrayList<>();

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
        c3 = Points2D.sum(Points2D.multiply(p0, -1), Points2D.multiply(p1, 3), Points2D.multiply(p2, -3), p3);
        c2 = Points2D.sum(Points2D.multiply(p0, 3), Points2D.multiply(p1, -6), Points2D.multiply(p2, 3));
        c1 = Points2D.add(Points2D.multiply(p0, -3), Points2D.multiply(p1, 3));
        c0 = p0;

        final double a0x, a0y, a1x, a1y;
        a0y = a0.getY();
        a1y = a1.getY();
        a1x = a1.getX();
        a0x = a0.getX();

        // Convert line to normal form: ax + by + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D.Double n;                // normal for normal form of line
        n = new Point2D.Double(a0y - a1y, a1x - a0x);

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = a0x * a1y - a1x * a0y;

        // ?Rotate each cubic coefficient using line for new coordinate system?
        // Find roots of rotated cubic
        double[] roots = new Polynomial(
                Points2D.dotProduct(n, c3),
                Points2D.dotProduct(n, c2),
                Points2D.dotProduct(n, c1),
                Points2D.dotProduct(n, c0) + cl
        ).getRoots();

        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        for (int i = 0; i < roots.length; i++) {
            final double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D.Double p5, p6, p7, p8, p9, p10;
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
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(p10, t));
                    }
                } else if (a0y == a1y) {
                    if (topLeft.getX() <= p10.getX() && p10.getX() <= bottomRight.getX()) {
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(p10, t));
                    }
                } else if (gte(p10, topLeft) && lte(p10, bottomRight)) {
                    status = IntersectionStatus.INTERSECTION;
                    result.add(new IntersectionPointEx(p10, t));
                }
            }
        }

        return new IntersectionResultEx(status, result);
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
    public static IntersectionResultEx intersectCubicCurvePointEx(
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
        final DoubleArrayList clampedRoots = new Polynomial(6 * a, 5 * b, 4 * c, 3 * d, 2 * e, f).getRootsInInterval(0, 1);
        // Add zero and one, because we have clamped the roots
        final DoubleArrayList roots = new DoubleArrayList();
        roots.addAll(clampedRoots);
        roots.add(0.0);
        roots.add(1.0);

        // Select roots with closest distance to point
        final List<IntersectionPointEx> result = new ArrayList<>();
        final Point2D.Double p0, p1, p2, p3;
        p0 = new Point2D.Double(x0, y0);
        p1 = new Point2D.Double(x1, y1);
        p2 = new Point2D.Double(x2, y2);
        p3 = new Point2D.Double(x3, y3);
        final double rr = r * r;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (double t : roots) {
            final Point2D.Double p;
            p = Points2D.sum(Points2D.multiply(p0, (1 - t) * (1 - t) * (1 - t)),
                    Points2D.multiply(p1, 3 * (1 - t) * (1 - t) * t),
                    Points2D.multiply(p2, 3 * (1 - t) * t * t),
                    Points2D.multiply(p3, t * t * t));

            double dd = (p.getX() - cx) * (p.getX() - cx) + (p.getY() - cy) * (p.getY() - cy);
            if (dd < rr) {
                if (abs(dd - bestDistance) < EPSILON) {
                    result.add(new IntersectionPointEx(p, t));
                } else if (dd < bestDistance) {
                    bestDistance = dd;
                    result.clear();
                    result.add(new IntersectionPointEx(p, t));
                }
            }
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectCubicCurvePolygonEx(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3, @NonNull List<Point2D.Double> points) {
        List<IntersectionPointEx> result = new ArrayList<>();
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D.Double a1 = points.get(i);
            Point2D.Double a2 = points.get((i + 1) % length);
            IntersectionResultEx inter = Intersections.intersectCubicCurveLineEx(p0, p1, p2, p3, a1, a2);

            result.addAll(inter.asList());
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectCubicCurveRectangleEx(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3, @NonNull Point2D r0, @NonNull Point2D r1) {
        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(r0, r1);
        bottomRight = bottomRight(r0, r1);
        topRight = new Point2D.Double(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D.Double(topLeft.getX(), bottomRight.getY());

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectCubicCurveLineEx(p0, p1, p2, p3, topLeft, topRight);
        inter2 = Intersections.intersectCubicCurveLineEx(p0, p1, p2, p3, topRight, bottomRight);
        inter3 = Intersections.intersectCubicCurveLineEx(p0, p1, p2, p3, bottomRight, bottomLeft);
        inter4 = Intersections.intersectCubicCurveLineEx(p0, p1, p2, p3, bottomLeft, topLeft);

        final List<IntersectionPointEx> result = new ArrayList<>();

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        // FIXME compute inside/outside
        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectCircleCircleEx(@NonNull Point2D c1, double r1, @NonNull Point2D c2, double r2) {
        return intersectCircleCircleEx(c1.getX(), c1.getY(), r1, c2.getX(), c2.getY(), r2);
    }


    /**
     * Computes the intersection between circle 1 and circle 2.
     *
     * @param c1x the center of circle 1
     * @param c1y the center of circle 1
     * @param r1  the radius of circle 1
     * @param c2x the center of circle 2
     * @param c2y the center of circle 2
     * @param r2  the radius of circle 2
     * @return computed intersection with parameters of circle 1 at the intersection point
     */
    @NonNull
    public static IntersectionResultEx intersectCircleCircleEx(double c1x, double c1y, double r1, double c2x, double c2y, double r2) {
        List<IntersectionPointEx> result = new ArrayList<>();

        // Determine minimum and maximum radii where circles can intersect
        double r_max = r1 + r2;
        double r_min = Math.abs(r1 - r2);

        // Determine actual distance between the two circles
        double c_dist = Geom.distance(c1x, c1y, c2x, c2y);

        IntersectionStatus status;

        if (c_dist > r_max) {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else if (c_dist < r_min) {
            status = r1 < r2 ? IntersectionStatus.NO_INTERSECTION_INSIDE : IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else if (Geom.almostZero(c_dist) && Geom.almostEqual(r1, r2)) {
            status = IntersectionStatus.NO_INTERSECTION_COINCIDENT;
        } else {
            status = IntersectionStatus.INTERSECTION;

            double a = (r1 * r1 - r2 * r2 + c_dist * c_dist) / (2 * c_dist);
            double h = Math.sqrt(r1 * r1 - a * a);
            Point2D.Double p = lerp(c1x, c1y, c2x, c2y, a / c_dist);
            double b = h / c_dist;

            double dy = c2y - c1y;
            double dx = c2x - c1y;
            double p1x = p.getX() - b * dy;
            double p1y = p.getY() + b * dx;
            result.add(new IntersectionPointEx(new Point2D.Double(p1x, p1y
            ),
                    Geom.atan2(p1y - c1y, p1x - c1x), Geom.perp(p1x - c1x, p1y - c1y),
                    Geom.atan2(p1y - c2y, p1x - c2x), Geom.perp(p1x - c2x, p1y - c2y)
            ));
            double p2x = p.getX() + b * dy;
            double p2y = p.getY() - b * dx;

            if (!Geom.almostEqual(c_dist, r_max)) {
                result.add(new IntersectionPointEx(new Point2D.Double(p2x, p2y
                ),
                        Geom.atan2(p2y - c1y, p2x - c1x), Geom.perp(p2x - c1x, p2y - c1y),
                        Geom.atan2(p2y - c2y, p2x - c2x), Geom.perp(p2x - c2x, p2y - c2y)

                ));
            }
        }
        return new IntersectionResultEx(status, result);
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
    public static IntersectionResultEx intersectCircleEllipseEx(@NonNull Point2D cc, double r, @NonNull Point2D ec, double rx, double ry) {
        return Intersections.intersectEllipseEllipseEx(cc, r, r, ec, rx, ry);
    }

    @NonNull
    public static IntersectionResultEx intersectCircleEllipseEx(double cx1, double cy1, double r1, double cx2, double cy2, double rx2, double ry2) {
        return intersectEllipseEllipseEx(cx1, cy1, r1, r1, cx2, cy2, rx2, ry2);
    }

    @NonNull
    public static IntersectionResultEx intersectCircleLineEx(double cx, double cy, double r, double a0x, double a0y, double a1x, double a1y) {
        return intersectCircleLineEx(new Point2D.Double(cx, cy), r, new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y));
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
    public static IntersectionResultEx intersectCircleLineEx(@NonNull Point2D c, double r, @NonNull Point2D a0, @NonNull Point2D a1) {
        IntersectionResultEx inter = intersectLineCircleEx(a0, a1, c, r);
        // FIXME compute t of circle!
        return inter;
    }

    @NonNull
    public static IntersectionResultEx intersectCirclePointEx(double cx, double cy, double cr, double px, double py, double pr) {
        return intersectCirclePointEx(new Point2D.Double(cx, cy), cr, new Point2D.Double(px, py), pr);
    }

    @NonNull
    public static IntersectionResultEx intersectCirclePointEx(@NonNull Point2D cc, double cr, @NonNull Point2D pc, double pr) {
        List<IntersectionPointEx> result = new ArrayList<>();

        double c_dist = cc.distance(pc);

        IntersectionStatus status;
        if (abs(c_dist) < EPSILON) {
            status = IntersectionStatus.NO_INTERSECTION_INSIDE;
        } else {

            Point2D.Double p = lerp(cc, pc, cr / c_dist);
            final double dd = p.distanceSq(pc);
            if (dd <= pr * pr) {
                status = IntersectionStatus.INTERSECTION;
                // FIXME compute t
                result.add(new IntersectionPointEx(p, Double.NaN));
            } else {
                status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
            }
        }
        return new IntersectionResultEx(status, result);
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
    public static IntersectionResultEx intersectCirclePolygonEx(@NonNull Point2D c, double r, @NonNull List<Point2D.Double> points) {
        List<IntersectionPointEx> result = new ArrayList<>();
        int length = points.size();
        IntersectionResultEx inter = null;

        for (int i = 0; i < length; i++) {
            final Point2D.Double a0, a1;
            a0 = points.get(i);
            a1 = points.get((i + 1) % length);

            inter = Intersections.intersectCircleLineEx(c, r, a0, a1);
            result.addAll(inter.asList());
        }

        IntersectionStatus status;
        if (!result.isEmpty()) {
            status = IntersectionStatus.INTERSECTION;
        } else {
            status = inter == null ? IntersectionStatus.NO_INTERSECTION : inter.getStatus();
        }

        return new IntersectionResultEx(status, result);
    }

    @NonNull
    public static IntersectionResultEx intersectCircleRectangleEx(double c1x, double c1y, double r1, double x, double y, double w, double h) {
        return intersectCircleRectangleEx(new Point2D.Double(c1x, c1y), r1, new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
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
    public static IntersectionResultEx intersectCircleRectangleEx(@NonNull Point2D c, double r, @NonNull Point2D r0, @NonNull Point2D r1) {
        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(r0, r1);
        bottomRight = bottomRight(r0, r1);
        topRight = new Point2D.Double(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D.Double(topLeft.getX(), bottomRight.getY());

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectCircleLineEx(c, r, topLeft, topRight);
        inter2 = Intersections.intersectCircleLineEx(c, r, topRight, bottomRight);
        inter3 = Intersections.intersectCircleLineEx(c, r, bottomRight, bottomLeft);
        inter4 = Intersections.intersectCircleLineEx(c, r, bottomLeft, topLeft);

        List<IntersectionPointEx> result = new ArrayList<>();

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        IntersectionStatus status;
        if (!result.isEmpty()) {
            status = IntersectionStatus.INTERSECTION;
        } else {
            status = inter1.getStatus();
        }

        return new IntersectionResultEx(status, result);
    }

    @NonNull
    public static IntersectionResultEx intersectEllipseQuadraticCurveEx(
            double cx, double cy, double rx, double ry,
            double x0, double y0, double x1, double y1, double x2, double y3) {
        // FIXME compute t of Ellipse!
        return intersectQuadraticCurveEllipseEx(new Point2D.Double(x0, y0), new Point2D.Double(x1, y1), new Point2D.Double(x2, y3), new Point2D.Double(cx, cy), rx, ry);
    }

    @NonNull
    public static IntersectionResultEx intersectEllipseCircleEx(double cx1, double cy1, double rx1, double ry1, double cx2, double cy2, double r2) {
        return intersectEllipseEllipseEx(cx1, cy1, rx1, ry1, cx2, cy2, r2, r2);
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
    public static IntersectionResultEx intersectEllipseEllipseEx(@NonNull Point2D c1, double rx1, double ry1, @NonNull Point2D c2, double rx2, double ry2) {
        return intersectEllipseEllipseEx(c1.getX(), c1.getY(), rx1, ry1, c2.getX(), c2.getY(), rx2, ry2);
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
    public static IntersectionResultEx intersectEllipseEllipseEx(double cx1, double cy1, double rx1, double ry1, double cx2, double cy2, double rx2, double ry2) {
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
        List<IntersectionPointEx> result = new ArrayList<>();

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
                        result.add(new IntersectionPointEx(new Point2D.Double(xRoots[x], yRoots[y]), Double.NaN));
                    }
                }
            }
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectEllipseLineEx(@NonNull Point2D ec, double rx, double ry, @NonNull Point2D a0, @NonNull Point2D a1) {
        IntersectionResultEx result = intersectLineEllipseEx(a0, a1, ec, rx, ry);
        // FIXME compute t for Ellipse instead for Line!
        return result;
    }

    @NonNull
    public static IntersectionResultEx intersectEllipseLineEx(double cx, double cy, double rx, double ry,
                                                              double x0, double y0, double x1, double y1) {
        IntersectionResultEx result = intersectLineEllipseEx(x0, y0, x1, y1, cx, cy, rx, ry);
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
    public static IntersectionResultEx intersectEllipsePolygonEx(@NonNull Point2D c, double rx, double ry, @NonNull List<Point2D.Double> points) {
        List<IntersectionPointEx> result = new ArrayList<>();
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D.Double b1 = points.get(i);
            Point2D.Double b2 = points.get((i + 1) % length);
            IntersectionResultEx inter = Intersections.intersectEllipseLineEx(c, rx, ry, b1, b2);

            result.addAll(inter.asList());
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectEllipseRectangleEx(@NonNull Point2D c, double rx, double ry, @NonNull Point2D r1, @NonNull Point2D r2) {
        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(r1, r2);
        bottomRight = bottomRight(r1, r2);
        topRight = new Point2D.Double(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D.Double(topLeft.getX(), bottomRight.getY());

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectEllipseLineEx(c, rx, ry, topLeft, topRight);
        inter2 = Intersections.intersectEllipseLineEx(c, rx, ry, topRight, bottomRight);
        inter3 = Intersections.intersectEllipseLineEx(c, rx, ry, bottomRight, bottomLeft);
        inter4 = Intersections.intersectEllipseLineEx(c, rx, ry, bottomLeft, topLeft);

        List<IntersectionPointEx> result = new ArrayList<>();

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectLineQuadraticCurveEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2) {
        return intersectLineQuadraticCurveEx(
                a0.getX(), a0.getY(),
                a1.getX(), a1.getY(),
                p0.getX(), p0.getY(),
                p1.getX(), p1.getY(),
                p2.getX(), p2.getY());
    }

    @NonNull
    public static IntersectionResultEx intersectLineQuadraticCurveEx(double a0x, double a0y, double a1x, double a1y,
                                                                     double p0x, double p0y, double p1x, double p1y, double p2x, double p2y) {
        return intersectLineQuadraticCurveEx(
                a0x, a0y,
                a1x, a1y,
                p0x, p0y,
                p1x, p1y,
                p2x, p2y, 1.0);
    }

    @NonNull
    public static IntersectionResultEx intersectLineQuadraticCurveEx(double a0x, double a0y, double a1x, double a1y,
                                                                     double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double maxT) {
        /* steps:
         * 1. Rotate the bezier curve so that the line coincides with the x-axis.
         *    This will position the curve in a way that makes it cross the line at points where its y-function is zero.
         * 2. Insert the control points of the rotated bezier curve in the polynomial equation.
         * 3. Find the roots of the polynomial equation.
         */

        Point2D.Double topLeft = topLeft(a0x, a0y, a1x, a1y); // used to determine if point is on line segment
        Point2D.Double bottomRight = bottomRight(a0x, a0y, a1x, a1y); // used to determine if point is on line segment
        List<IntersectionPointEx> result = new ArrayList<>();

        final Point2D.Double p0, p1;
        p0 = new Point2D.Double(p0x, p0y);
        p1 = new Point2D.Double(p1x, p1y);

        final Point2D.Double c2, c1, c0;       // coefficients of quadratic
        c2 = Points2D.add(Points2D.add(p0, Points2D.multiply(p1, -2)), p2x, p2y);
        c1 = Points2D.add(Points2D.multiply(p0, -2), Points2D.multiply(p1, 2));
        c0 = p0;

        // Convert line to normal form: ax + by + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D.Double n;                // normal for normal form of line
        n = new Point2D.Double(a0y - a1y, a1x - a0x);

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = a0x * a1y - a1x * a0y;

        // Transform cubic coefficients to line's coordinate system and find roots
        // of cubic
        double[] roots = new Polynomial(
                Points2D.dotProduct(n, c2),
                Points2D.dotProduct(n, c1),
                Points2D.dotProduct(n, c0) + cl
        ).getRoots();
        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D.Double p4, p5, p6;
                p4 = lerp(p0, p1, t);
                p5 = lerp(p1x, p1y, p2x, p2y, t);
                p6 = lerp(p4, p5, t);

                // See if point is on line segment
                double t1 = argumentOnLine(a0x, a0y, a1x, a1y, p6.getX(), p6.getY());
                if (t1 >= 0 && t1 <= maxT) {
                    status = IntersectionStatus.INTERSECTION;
                    result.add(new IntersectionPointEx(p6, t1));
                }
            }
        }

        return new IntersectionResultEx(status, result);
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
    public static IntersectionResultEx intersectLineCubicCurveEx(
            double a0x, double a0y, double a1x, double a1y,
            double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double p3x, double p3y) {

        Point2D.Double a0 = new Point2D.Double(a0x, a0y);
        Point2D.Double a1 = new Point2D.Double(a1x, a1y);
        Point2D.Double p0 = new Point2D.Double(p0x, p0y);
        Point2D.Double p1 = new Point2D.Double(p1x, p1y);
        Point2D.Double p2 = new Point2D.Double(p2x, p2y);
        Point2D.Double p3 = new Point2D.Double(p3x, p3y);
        return intersectLineCubicCurveEx(a0, a1, p0, p1, p2, p3);
    }

    @NonNull
    public static IntersectionResultEx intersectLineCubicCurveEx(
            double a0x, double a0y, double a1x, double a1y,
            double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double p3x, double p3y, double maxT) {

        Point2D.Double a0 = new Point2D.Double(a0x, a0y);
        Point2D.Double a1 = new Point2D.Double(a1x, a1y);
        Point2D.Double p0 = new Point2D.Double(p0x, p0y);
        Point2D.Double p1 = new Point2D.Double(p1x, p1y);
        Point2D.Double p2 = new Point2D.Double(p2x, p2y);
        Point2D.Double p3 = new Point2D.Double(p3x, p3y);
        return intersectLineCubicCurveEx(a0, a1, p0, p1, p2, p3, maxT);
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
    public static IntersectionResultEx intersectLineCubicCurveEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3) {
        return intersectLineCubicCurveEx(a0, a1, p0, p1, p2, p3, 1.0);
    }

    @NonNull
    public static IntersectionResultEx intersectLineCubicCurveEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3, double maxT) {
        final double a0x, a0y, a1x, a1y;
        a0x = a0.getX();
        a0y = a0.getY();
        a1x = a1.getX();
        a1y = a1.getY();

        final Point2D.Double amin = topLeft(a0, a1); // used to determine if point is on line segment
        final Point2D.Double amax = bottomRight(a0, a1); // used to determine if point is on line segment
        List<IntersectionPointEx> result = new ArrayList<>();

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
        c3 = Points2D.sum(Points2D.multiply(p0, -1), Points2D.multiply(p1, 3), Points2D.multiply(p2, -3), p3);
        c2 = Points2D.sum(Points2D.multiply(p0, 3), Points2D.multiply(p1, -6), Points2D.multiply(p2, 3));
        c1 = Points2D.add(Points2D.multiply(p0, -3), Points2D.multiply(p1, 3));
        c0 = p0;

        // Convert line to normal form: ax + by + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D.Double n;                // normal for normal form of line
        n = new Point2D.Double(a0y - a1y, a1x - a0x);

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = a0x * a1y - a1x * a0y;

        // Rotate each cubic coefficient using line for new coordinate system
        // Find roots of rotated cubic
        final Polynomial polynomial = new Polynomial(
                Points2D.dotProduct(n, c3),
                Points2D.dotProduct(n, c2),
                Points2D.dotProduct(n, c1),
                Points2D.dotProduct(n, c0) + cl
        );
        double[] roots = polynomial.getRoots();

        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D.Double p5, p6, p7, p8, p9, p10;
                p5 = lerp(p0, p1, t);
                p6 = lerp(p1, p2, t);
                p7 = lerp(p2, p3, t);

                p8 = lerp(p5, p6, t);
                p9 = lerp(p6, p7, t);

                p10 = lerp(p8, p9, t);

                // See if point is on line segment
                // Had to make special cases for vertical and horizontal lines due
                // to slight errors in calculation of p10
                double t1 = argumentOnLine(a0x, a0y, a1x, a1y, p10.getX(), p10.getY());
                if (t1 >= 0 && t1 <= maxT) {
                    status = IntersectionStatus.INTERSECTION;
                    result.add(new IntersectionPointEx(p10, t1));
                }
            }
        }

        return new IntersectionResultEx(status, result);
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
    public static IntersectionResultEx intersectLineCircleEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D c, double r) {
        return intersectLineCircleEx(a0.getX(), a0.getY(), a1.getX(), a1.getY(), c.getX(), c.getY(), r);
    }

    public static IntersectionResultEx intersectLineCircleEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D c, double r, double epsilon) {
        return intersectLineCircleEx(a0.getX(), a0.getY(), a1.getX(), a1.getY(), c.getX(), c.getY(), r, epsilon);
    }

    public static IntersectionResult intersectLineCircle(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D c, double r, double epsilon) {
        return intersectLineCircle(a0.getX(), a0.getY(), a1.getX(), a1.getY(), c.getX(), c.getY(), r, epsilon);
    }

    /**
     * Computes the intersection between a line and a circle.
     * <p>
     * The intersection will contain the parameters 't' of the line in range
     * [0 - epsilon, 1 + epsilon].
     *
     * <p>
     * The intersection will have one of the following status:
     * <ul>
     * <li>{@link IntersectionStatus#INTERSECTION}</li>
     * <li>{@link IntersectionStatus#NO_INTERSECTION_INSIDE}</li>
     * <li>{@link IntersectionStatus#NO_INTERSECTION_OUTSIDE}</li>
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
    public static IntersectionResultEx intersectLineCircleEx(double x0, double y0, double x1, double y1, double cx, double cy, double r) {
        return intersectLineCircleEx(x0, y0, x1, y1, cx, cy, r, EPSILON);
    }

    /**
     * This method computes the argument of the circle function with atan2
     * and thus may be unnecessarily slow if you only need the argument
     * of the line function.
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param cx
     * @param cy
     * @param r
     * @param epsilon
     * @return
     */
    @NonNull
    public static IntersectionResultEx intersectLineCircleEx(double x0, double y0, double x1, double y1, double cx, double cy, double r, double epsilon) {
        List<IntersectionPointEx> result = new ArrayList<>(2);
        final double Δx, Δy;
        Δx = x1 - x0;
        Δy = y1 - y0;
        final double a, b, c, deter;
        a = Δx * Δx + Δy * Δy;
        b = 2 * (Δx * (x0 - cx) + Δy * (y0 - cy));
        c = cx * cx + cy * cy + x0 * x0 + y0 * y0 - 2 * (cx * x0 + cy * y0) - r * r;
        deter = b * b - 4 * a * c;

        IntersectionStatus status;
        double minT = -epsilon;
        double maxT = 1 + epsilon;
        if (deter < minT) {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else {
            if (deter > epsilon) {
                double e, t1, t2;
                e = Math.sqrt(deter);
                t1 = (-b + e) / (2 * a);
                t2 = (-b - e) / (2 * a);

                if ((t1 < minT || t1 > maxT) && (t2 < minT || t2 > maxT)) {
                    if ((t1 <= minT && t2 <= minT) || (t1 > maxT && t2 > maxT)) {
                        status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_INSIDE;
                    }
                } else {
                    status = IntersectionStatus.INTERSECTION;
                    if (t2 < t1) {
                        double swap = t2;
                        t2 = t1;
                        t1 = swap;
                    }
                    if (minT <= t1 && t1 <= maxT) {
                        Point2D.Double p = lerp(x0, y0, x1, y1, t1);
                        result.add(new IntersectionPointEx(p,
                                t1, new Point2D.Double(x1 - x0, y1 - y0),
                                Geom.atan2(p.getY() - cy, p.getX() - cx),
                                new Point2D.Double(p.getY() - cy, -p.getX() - cx)
                        ));
                    }
                    if (minT <= t2 && t2 <= maxT) {
                        Point2D.Double p = lerp(x0, y0, x1, y1, t2);
                        result.add(new IntersectionPointEx(p,
                                t2, new Point2D.Double(x1 - x0, y1 - y0),
                                Geom.atan2(p.getY() - cy, p.getX() - cx),
                                new Point2D.Double(p.getY() - cy, -p.getX() - cx)
                        ));
                    }
                }
            } else {
                double t = (-b) / (2 * a);
                if (minT <= t && t <= maxT) {
                    status = IntersectionStatus.INTERSECTION;
                    Point2D.Double p = lerp(x0, y0, x1, y1, t);
                    result.add(new IntersectionPointEx(p,
                            t, new Point2D.Double(x1 - x0, y1 - y0),
                            Geom.atan2(p.getY() - cy, p.getX() - cx),
                            new Point2D.Double(p.getY() - cy, -p.getX() - cx)
                    ));
                } else {
                    status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
                }
            }
        }

        return new IntersectionResultEx(status, result);
    }

    @NonNull
    public static IntersectionResult intersectLineCircle(double x0, double y0, double x1, double y1, double cx, double cy, double r, double epsilon) {
        List<IntersectionPoint> result = new ArrayList<>(2);
        final double Δx, Δy;
        Δx = x1 - x0;
        Δy = y1 - y0;
        final double a, b, c, deter;
        a = Δx * Δx + Δy * Δy;
        b = 2 * (Δx * (x0 - cx) + Δy * (y0 - cy));
        c = cx * cx + cy * cy + x0 * x0 + y0 * y0 - 2 * (cx * x0 + cy * y0) - r * r;
        deter = b * b - 4 * a * c;

        IntersectionStatus status;
        double minT = -epsilon;
        double maxT = 1 + epsilon;
        if (deter < minT) {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else {
            if (deter > epsilon) {
                double e, t1, t2;
                e = Math.sqrt(deter);
                t1 = (-b + e) / (2 * a);
                t2 = (-b - e) / (2 * a);

                if ((t1 < minT || t1 > maxT) && (t2 < minT || t2 > maxT)) {
                    if ((t1 <= minT && t2 <= minT) || (t1 > maxT && t2 > maxT)) {
                        status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_INSIDE;
                    }
                } else {
                    status = IntersectionStatus.INTERSECTION;
                    if (t2 < t1) {
                        double swap = t2;
                        t2 = t1;
                        t1 = swap;
                    }
                    if (minT <= t1 && t1 <= maxT) {
                        result.add(
                                new IntersectionPoint(lerp(x0, y0, x1, y1, t1).getX(), lerp(x0, y0, x1, y1, t1).getY(), t1));
                    }
                    if (minT <= t2 && t2 <= maxT) {
                        result.add(new IntersectionPoint(lerp(x0, y0, x1, y1, t2).getX(), lerp(x0, y0, x1, y1, t2).getY(), t2));
                    }
                }
            } else {
                double t = (-b) / (2 * a);
                if (minT <= t && t <= maxT) {
                    status = IntersectionStatus.INTERSECTION;
                    Point2D.Double p = lerp(x0, y0, x1, y1, t);
                    result.add(new IntersectionPoint(p.getX(), p.getY(), t));
                } else {
                    status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
                }
            }
        }

        return new IntersectionResult(status, result);
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
    public static IntersectionResultEx intersectLineEllipseEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull AABB e) {
        double rx = e.getWidth() * 0.5;
        double ry = e.getHeight() * 0.5;
        return intersectLineEllipseEx(a0, a1, new Point2D.Double(e.getMinX() + rx, e.getMinY() + ry), rx, ry);
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
    public static IntersectionResultEx intersectLineEllipseEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D ec, double rx, double ry) {
        return intersectLineEllipseEx(a0.getX(), a0.getY(), a1.getX(), a1.getY(), ec.getX(), ec.getY(), rx, ry);
    }

    @NonNull
    public static IntersectionResultEx intersectLineEllipseEx(double x0, double y0, double x1, double y1,
                                                              double cx, double cy, double rx, double ry) {
        List<IntersectionPointEx> result = new ArrayList<>();

        final Point2D.Double origin, dir, center, diff, mDir, mDiff;
        origin = new Point2D.Double(x0, y0);
        dir = Points2D.subtract(x1, y1, x0, y0);
        center = new Point2D.Double(cx, cy);
        diff = Points2D.subtract(origin, center);
        mDir = new Point2D.Double(dir.getX() / (rx * rx), dir.getY() / (ry * ry));
        mDiff = new Point2D.Double(diff.getX() / (rx * rx), diff.getY() / (ry * ry));

        final double a, b, c, d;
        a = Points2D.dotProduct(dir, mDir);
        b = Points2D.dotProduct(dir, mDiff);
        c = Points2D.dotProduct(diff, mDiff) - 1.0;
        d = b * b - a * c;

        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        if (d < -EPSILON) {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else if (d > 0) {
            final double root, t0, t1;
            root = Math.sqrt(d);
            t0 = (-b - root) / a;
            t1 = (-b + root) / a;

            if ((t0 < 0 || 1 < t0) && (t1 < 0 || 1 < t1)) {
                if ((t0 < 0 && t1 < 0) || (t0 > 1 && t1 > 1)) {
                    status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
                } else {
                    status = IntersectionStatus.NO_INTERSECTION_INSIDE;
                }
            } else {
                status = IntersectionStatus.INTERSECTION;
                if (0 <= t0 && t0 <= 1) {
                    result.add(new IntersectionPointEx(lerp(x0, y0, x1, y1, t0), t0));
                }
                if (0 <= t1 && t1 <= 1) {
                    result.add(new IntersectionPointEx(lerp(x0, y0, x1, y1, t1), t1));
                }
            }
        } else {
            final double t = -b / a;
            if (0 <= t && t <= 1) {
                status = IntersectionStatus.INTERSECTION;
                result.add(new IntersectionPointEx(lerp(x0, y0, x1, y1, t), t));
            } else {
                status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
            }
        }

        return new IntersectionResultEx(status, result);
    }


    /**
     * Intersects line segment 'a' with line segment 'b'.
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
     * @see #intersectRayLineEx(double, double, double, double, double, double, double, double, double)
     */
    @NonNull
    public static IntersectionResultEx intersectLineLineEx(
            double a0x, double a0y, double a1x, double a1y,
            double b0x, double b0y, double b1x, double b1y) {
        return intersectRayLineEx(a0x, a0y, a1x, a1y, b0x, b0y, b1x, b1y, 1.0);
    }

    /**
     * Computes the intersection of line segment 'a' with line segment 'b'.
     *
     * @see #intersectRayLineEx(double, double, double, double, double, double, double, double, double, double)
     *
     * @param a0 start of line segment 'a'
     * @param a1 end of line segment 'a'
     * @param b0 start of line segment 'b'
     * @param b1 end of line segment 'b'
     * @return computed intersection with parameters of line 'a' at the intersection point
     * @see #intersectLineLineEx(double, double, double, double, double, double, double, double)
     */
    @NonNull
    public static IntersectionResultEx intersectLineLineEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D b0, @NonNull Point2D b1) {
        return intersectLineLineEx(a0.getX(), a1.getY(), b0.getX(), b0.getY(), b1.getX(), b1.getY(), 1.0, EPSILON);
    }

    public static IntersectionResultEx intersectLineLineEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D b0, @NonNull Point2D b1, double epsilon) {
        return intersectRayLineEx(
                a0.getX(), a0.getY(),
                a1.getX(), a1.getY(),
                b0.getX(), b0.getY(),
                b1.getX(), b1.getY(), 1.0, epsilon);
    }

    /**
     * Intersects a line segment or ray 'a' with line segment 'b'.
     *
     * @see #intersectRayLineEx(double, double, double, double, double, double, double, double, double, double)
     */
    @NonNull
    public static IntersectionResultEx intersectRayLineEx(
            double a0x, double a0y, double a1x, double a1y,
            double b0x, double b0y, double b1x, double b1y, double maxT) {
        return intersectRayLineEx(
                a0x, a0y,
                a1x, a1y,
                b0x, b0y,
                b1x, b1y, 1.0, EPSILON);
    }

    /**
     * Intersects a line segment or ray 'a' with line segment 'b'.
     * <p>
     * This method can produce the following {@link IntersectionStatus} codes:
     * <dl>
     *     <dt>{@link IntersectionStatus#INTERSECTION}</dt><dd>
     *         The line segments intersect at the {@link IntersectionPointEx} given
     *         in the result.
     *     </dd>
     *     <dt>{@link IntersectionStatus#NO_INTERSECTION}</dt><dd>
     *         The line segments do not intersect, but lines of infinite length,
     *         will intersect at the {@link IntersectionPointEx} given
     *         in the result.
     *     </dd>
     *     <dt>{@link IntersectionStatus#NO_INTERSECTION_COINCIDENT}</dt><dd>
     *         The lines segments do not intersect because they are
     *         coincident. Coincidence starts and ends at the two
     *         {@link IntersectionPointEx}s given in the result.
     *     </dd>
     *     <dt>{@link IntersectionStatus#NO_INTERSECTION_PARALLEL}</dt><dd>
     *         The lines segments do not intersect because they are parallel.
     *     </dd>
     * </dl>
     *
     * @param a0x  start x coordinate of line segment 'a' or of ray 'a'
     * @param a0y  start y coordinate of line segment 'a' or of ray 'a'
     * @param a1x  end x coordinate of line segment or direction 'a' or of ray 'a'
     * @param a1y  end y coordinate of line segment or direction 'a' or of ray 'a'
     * @param b0x  start x coordinate of line segment 'b'
     * @param b0y  start y coordinate of line segment 'b'
     * @param b1x  end x coordinate of line segment 'b'
     * @param b1y  end y coordinate of line segment 'b'
     * @param maxT maximal permitted value for the parameter t of 'a', if this
     *             value is {@link Double#MAX_VALUE} then 'a' is a ray
     *             starting at {@code a0x,a0y} with direction {@code a1x-a0x,a1y-a0y},
     *             <br>if this value is {@code 1.0} then 'a' is a line segment.
     * @return computed intersection with parameters t of ray 'a' at the intersection point
     */
    @NonNull
    public static IntersectionResultEx intersectRayLineEx(
            double a0x, double a0y, double a1x, double a1y,
            double b0x, double b0y, double b1x, double b1y, double maxT, double epsilon) {

        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status;

        double adx = a1x - a0x;
        double ady = a1y - a0y;
        double bdx = b1x - b0x;
        double bdy = b1y - b0y;
        Point2D.Double tangentA = new Point2D.Double(adx, ady);
        Point2D.Double tangentB = new Point2D.Double(bdx, bdy);

        double b0a0dy = a0y - b0y;
        double b0a0dx = a0x - b0x;
        double ua_t = bdx * b0a0dy - bdy * b0a0dx;
        double ub_t = adx * b0a0dy - ady * b0a0dx;
        double u_b = bdy * adx - bdx * ady;

        if (!Geom.almostZero(u_b)) {
            double ua = ua_t / u_b;
            double ub = ub_t / u_b;

            // using threshold check here to make intersect "sticky" to prefer
            // considering it an intersection.
            if (-EPSILON < ua && ua < maxT + EPSILON && -EPSILON < ub && ub < 1 + EPSILON) {
                status = IntersectionStatus.INTERSECTION;
                result.add(new IntersectionPointEx(
                        new Point2D.Double(a0x + ua * adx, a0y + ua * ady),
                        ua, tangentA, ub, tangentB
                ));
            } else {
                status = IntersectionStatus.NO_INTERSECTION;
                result.add(new IntersectionPointEx(
                        new Point2D.Double(a0x + ua * adx, a0y + ua * ady),
                        ua, tangentA, ub, tangentB
                ));
            }
        } else {
            if (Geom.almostZero(ua_t) || Geom.almostZero(ub_t)) {
                // either collinear or degenerate (segments are single points)
                boolean aIsPoint = Geom.almostZero(adx) && Geom.almostZero(ady);
                boolean bIsPoint = Geom.almostZero(bdx) && Geom.almostZero(bdy);
                if (aIsPoint && bIsPoint) {
                    // both segments are just points
                    if (Geom.almostEqual(a0x, b0x) && Geom.almostEqual(a0y, b0y)) {
                        // same point
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(a0x, a0y),
                                0, tangentA, 0, tangentB
                        ));
                    } else {
                        // distinct points
                        status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
                    }

                } else if (aIsPoint) {
                    if (lineContainsPoint(b0x, b0y, b1x, b1y, a0x, a0y)) {
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(a0x, a0y),
                                0, tangentA, argumentOnLine(b0x, b0y, b1x, b1y, a0x, a0y), tangentB
                        ));
                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
                    }

                } else if (bIsPoint) {
                    if (lineContainsPoint(a0x, a0y, a1x, a1y, b0x, b0y)) {
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(b0x, b0y),
                                argumentOnLine(a0x, a0y, a1x, a1y, b0x, b0y), tangentA, 0, tangentB
                        ));
                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
                    }
                } else {
                    // neither segment is a point, check if they overlap

                    double at0, at1;
                    if (Geom.almostZero(adx)) {
                        at0 = (b0y - a0y) / ady;
                        at1 = (b1y - a0y) / ady;
                    } else {
                        at0 = (b0x - a0x) / adx;
                        at1 = (b1x - a0x) / adx;
                    }

                    if (at0 > at1) {
                        double swap = at0;
                        at0 = at1;
                        at1 = swap;
                    }

                    if (at0 < maxT + EPSILON && at1 > -EPSILON) {
                        at0 = Geom.clamp(at0, 0.0, maxT);
                        at1 = Geom.clamp(at1, 0.0, maxT);
                        double bt0, bt1;
                        if (Geom.almostZero(bdx)) {
                            bt0 = (a0y + at0 * ady - b0y) / bdy;
                            bt1 = (a0y + at1 * ady - b0y) / bdy;
                        } else {
                            bt0 = (a0x + at0 * adx - b0x) / bdx;
                            bt1 = (a0x + at1 * adx - b0x) / bdx;
                        }

                        status = IntersectionStatus.NO_INTERSECTION_COINCIDENT;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(a0x + at0 * adx, a0y + at0 * ady),
                                at0, tangentA, bt0, tangentB
                        ));
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(a0x + at1 * adx, a0y + at1 * ady),
                                at1, tangentA, bt1, tangentB
                        ));

                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
                    }
                }
            } else {
                status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
            }
        }

        return new IntersectionResultEx(status, result);
    }

    @NonNull
    public static IntersectionResultEx intersectLinePathIteratorEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull PathIterator pit) {
        IntersectionResultEx i = intersectLinePathIteratorEx(a0, a1, pit, 1.0);
        if (i.getStatus() == IntersectionStatus.INTERSECTION && i.getFirst().getArgumentA() > 1) {
            return new IntersectionResultEx(IntersectionStatus.NO_INTERSECTION, new ArrayList<>());
        } else {// FIXME remove intersections with t>1
            return i;
        }
    }

    @NonNull
    public static IntersectionResultEx intersectLinePathIteratorEx(double a0x, double a0y, double a1x, double a1y, @NonNull PathIterator pit) {
        return intersectLinePathIteratorEx(a0x, a0y, a1x, a1y, pit, 1.0);
    }

    @NonNull
    public static IntersectionResultEx intersectLinePathIteratorEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull PathIterator pit, double maxT) {
        return intersectLinePathIteratorEx(a0.getX(), a0.getY(), a1.getX(), a1.getY(), pit, maxT);
    }

    @NonNull
    public static IntersectionResultEx intersectLinePathIteratorEx(double a0x, double a0y, double a1x, double a1y, @NonNull PathIterator pit, double maxT) {
        List<IntersectionPointEx> result = new ArrayList<>();
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        int segmentIndex = 0;
        int intersectionCount = 0;
        boolean hasTangent = false;
        for (; !pit.isDone(); pit.next()) {
            IntersectionResultEx inter;
            switch (pit.currentSegment(seg)) {
            case PathIterator.SEG_CLOSE:
                inter = Intersections.intersectRayLineEx(a0x, a0y, a1x, a1y, lastx, lasty, firstx, firsty, Double.MAX_VALUE);
                if (inter.getStatus() == IntersectionStatus.NO_INTERSECTION_COINCIDENT) {
                    hasTangent = true;
                }
                break;
            case PathIterator.SEG_CUBICTO:
                x = seg[4];
                y = seg[5];
                inter = Intersections.intersectLineCubicCurveEx(a0x, a0y, a1x, a1y, lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, Double.MAX_VALUE);
                lastx = x;
                lasty = y;
                break;
            case PathIterator.SEG_LINETO:
                x = seg[0];
                y = seg[1];
                inter = Intersections.intersectRayLineEx(a0x, a0y, a1x, a1y, lastx, lasty, x, y, Double.MAX_VALUE);
                if (inter.getStatus() == IntersectionStatus.NO_INTERSECTION_COINCIDENT) {
                    hasTangent = true;
                }
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
                inter = Intersections.intersectLineQuadraticCurveEx(a0x, a0y, a1x, a1y, lastx, lasty, seg[0], seg[1], x, y, Double.MAX_VALUE);
                lastx = x;
                lasty = y;
                break;
            default:
                inter = null;
                break;
            }

            if (inter != null) {
                for (final IntersectionPointEx intersection : inter.asList()) {
                    intersectionCount++;
                    if (intersection.getArgumentA() <= maxT) {

                        result.add(intersection.withSegment2(segmentIndex));
                    }
                }
            }

            segmentIndex++;
        }

        IntersectionStatus status;
        if (result.isEmpty()) {
            status = intersectionCount == 0 ? (hasTangent ? IntersectionStatus.NO_INTERSECTION_TANGENT : IntersectionStatus.NO_INTERSECTION_OUTSIDE) : IntersectionStatus.NO_INTERSECTION_INSIDE;
        } else {
            status = IntersectionStatus.INTERSECTION;
        }

        return new IntersectionResultEx(status, result);
    }

    @NonNull
    public static IntersectionResultEx intersectPathIteratorLineEx(@NonNull PathIterator pit, @NonNull Point2D a0, @NonNull Point2D a1) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
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
            IntersectionResultEx inter;
            switch (pit.currentSegment(seg)) {
            case PathIterator.SEG_CLOSE:
                inter = Intersections.intersectLineLineEx(lastx, lasty, firstx, firsty, a0x, a0y, a1x, a1y);
                result.addAll(inter.asList());
                break;
            case PathIterator.SEG_CUBICTO:
                x = seg[4];
                y = seg[5];
                inter = Intersections.intersectCubicCurveLineEx(lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, a0x, a0y, a1x, a1y);
                result.addAll(inter.asList());
                lastx = x;
                lasty = y;
                break;
            case PathIterator.SEG_LINETO:
                x = seg[0];
                y = seg[1];
                inter = Intersections.intersectLineLineEx(lastx, lasty, x, y, a0x, a0y, a1x, a1y);
                result.addAll(inter.asList());
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
                inter = Intersections.intersectQuadraticCurveLineEx(lastx, lasty, seg[0], seg[1], x, y, a0x, a0y, a1x, a1y);
                result.addAll(inter.asList());
                lastx = x;
                lasty = y;
                break;
            }
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectLinePointEx(double x0, double y0, double x1, double y1, double cx, double cy, double r) {
        List<IntersectionPointEx> result = new ArrayList<>();
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
                result.add(new IntersectionPointEx(new Point2D.Double(x, y), t));
            }
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectLinePolygonEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull List<Point2D.Double> points) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D.Double b0 = points.get(i);
            Point2D.Double b1 = points.get((i + 1) % length);
            IntersectionResultEx inter = Intersections.intersectLineLineEx(a0, a1, b0, b1);

            result.addAll(inter.asList());
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectLineRectangleEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D r0, @NonNull Point2D r1) {
        return intersectLineAABBEx(a0, a1,
                Math.min(r0.getX(), r1.getX()),
                Math.min(r0.getY(), r1.getY()),
                Math.max(r0.getX(), r1.getX()),
                Math.max(r0.getY(), r1.getY()));
    }


    @NonNull
    public static IntersectionResultEx intersectLineAABBEx(double a0x, double a0y, double a1x, double a1y,
                                                           double rminx, double rminy, double rmaxx, double rmaxy) {
        return intersectLineAABBEx(new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), rminx, rminy, rmaxx, rmaxy);
    }

    @NonNull
    public static IntersectionResultEx intersectLineAABBEx(@NonNull Point2D a0, @NonNull Point2D a1,
                                                           double rminx, double rminy, double rmaxx, double rmaxy) {

        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = new Point2D.Double(rminx, rminy);
        bottomRight = new Point2D.Double(rmaxx, rmaxy);
        topRight = new Point2D.Double(rmaxx, rminy);
        bottomLeft = new Point2D.Double(rminx, rmaxy);

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectLineLineEx(a0, a1, topLeft, topRight);
        inter2 = Intersections.intersectLineLineEx(a0, a1, topRight, bottomRight);
        inter3 = Intersections.intersectLineLineEx(a0, a1, bottomRight, bottomLeft);
        inter4 = Intersections.intersectLineLineEx(a0, a1, bottomLeft, topLeft);

        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        return new IntersectionResultEx(result);
    }

    @NonNull
    public static IntersectionResultEx intersectLineRectangleEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Rectangle2D.Double r) {
        return intersectLineAABBEx(a0, a1, r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
    }


    @NonNull
    public static IntersectionResultEx intersectRectangleLineEx(@NonNull Rectangle2D.Double r, @NonNull Point2D a0, @NonNull Point2D a1) {
        return intersectAABBLineEx(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY(), a0, a1);
    }

    @NonNull
    public static IntersectionResultEx intersectAABBLineEx(
            double rminx, double rminy, double rmaxx, double rmaxy,
            @NonNull Point2D a0, @NonNull Point2D a1) {

        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = new Point2D.Double(rminx, rminy);
        bottomRight = new Point2D.Double(rmaxx, rmaxy);
        topRight = new Point2D.Double(rmaxx, rminy);
        bottomLeft = new Point2D.Double(rminx, rmaxy);

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectLineLineEx(topRight, topLeft, a0, a1);
        inter2 = Intersections.intersectLineLineEx(bottomRight, topRight, a0, a1);
        inter3 = Intersections.intersectLineLineEx(bottomLeft, bottomRight, a0, a1);
        inter4 = Intersections.intersectLineLineEx(topLeft, bottomLeft, a0, a1);

        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        return new IntersectionResultEx(result);
    }

    @NonNull
    public static IntersectionResultEx intersectPathIteratorCircleEx(@NonNull PathIterator pit, double cx, double cy, double r) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        for (; !pit.isDone(); pit.next()) {
            IntersectionResultEx inter;
            switch (pit.currentSegment(seg)) {
            case PathIterator.SEG_CLOSE:
                inter = Intersections.intersectLineCircleEx(lastx, lasty, firstx, firsty, cx, cy, r);
                // FIXME add segment number to t
                result.addAll(inter.asList());
                break;
            case PathIterator.SEG_CUBICTO:
                x = seg[4];
                y = seg[5];
                inter = Intersections.intersectCubicCurveCircleEx(lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, cx, cy, r);
                // FIXME add segment number to t
                result.addAll(inter.asList());
                lastx = x;
                lasty = y;
                break;
            case PathIterator.SEG_LINETO:
                x = seg[0];
                y = seg[1];
                inter = Intersections.intersectLineCircleEx(lastx, lasty, x, y, cx, cy, r);
                // FIXME add segment number to t
                result.addAll(inter.asList());
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
                inter = Intersections.intersectQuadraticCurveCircleEx(lastx, lasty, seg[0], seg[1], x, y, cx, cy, r);
                // FIXME add segment number to t
                result.addAll(inter.asList());
                lastx = x;
                lasty = y;
                break;
            }
        }

        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectPathIteratorPointEx(@NonNull PathIterator pit, double px, double py, double tolerance) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        int i = 0;
        double closestDistance = Double.POSITIVE_INFINITY;
        for (; !pit.isDone(); pit.next(), i++) {
            IntersectionResultEx inter;
            switch (pit.currentSegment(seg)) {
            case PathIterator.SEG_CLOSE:
                inter = Intersections.intersectLinePointEx(lastx, lasty, firstx, firsty, px, py, tolerance);
                break;
            case PathIterator.SEG_CUBICTO:
                x = seg[4];
                y = seg[5];
                inter = Intersections.intersectCubicCurvePointEx(lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, px, py, tolerance);
                lastx = x;
                lasty = y;
                break;
            case PathIterator.SEG_LINETO:
                x = seg[0];
                y = seg[1];
                inter = Intersections.intersectLinePointEx(lastx, lasty, x, y, px, py, tolerance);
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
                inter = Intersections.intersectQuadraticCurvePointEx(lastx, lasty, seg[0], seg[1], x, y, px, py, tolerance);
                lastx = x;
                lasty = y;
                break;
            default:
                inter = null;
                break;
            }
            if (inter != null) {
                for (IntersectionPointEx entry : inter.asList()) {
                    final double dd = entry.distanceSq(px, py);
                    IntersectionPointEx newPoint = new IntersectionPointEx(
                            entry, entry.getArgumentA() + i, new Point2D.Double(0, 0), i, 0.0, new Point2D.Double(0, 0), 0);
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
        return new IntersectionResultEx(result);
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
    public static IntersectionResultEx intersectPointCircleEx(@NonNull Point2D point, @NonNull Point2D center, double radius) {
        List<IntersectionPointEx> result = new ArrayList<>();

        final double distance = point.distance(center);

        IntersectionStatus status;
        if (distance - radius < EPSILON) {
            status = IntersectionStatus.INTERSECTION;
            // FIXME compute t with atan2/2*PI
            result.add(new IntersectionPointEx(new Point2D.Double(point.getX(), point.getY()), 0.0));
        } else if (distance < radius) {
            status = IntersectionStatus.NO_INTERSECTION_INSIDE;
        } else {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        }
        return new IntersectionResultEx(status, result);
    }

    /**
     * Computes the intersection between a point and an ellipse.
     *
     * @param point  the point
     * @param center the center of the ellipse
     * @param rx     the x-radius of ellipse
     * @param ry     the y-radius of ellipse
     * @return computed intersection. Status can be{@link IntersectionStatus#INTERSECTION},
     * Status#NO_INTERSECTION_INSIDE or Status#NO_INTERSECTION_OUTSIDE}.
     */
    @NonNull
    public static IntersectionResultEx intersectPointEllipseEx(@NonNull Point2D point, @NonNull Point2D center, double rx, double ry) {
        List<IntersectionPointEx> result = new ArrayList<>();

        double px = point.getX();
        double py = point.getY();
        double cx = center.getX();
        double cy = center.getY();

        double det = (px - cx) * (px - cx) / (rx * rx) + (py - py) * (py - py) / (ry * ry);
        IntersectionStatus status;
        if (abs(det) - 1 == EPSILON) {
            status = IntersectionStatus.INTERSECTION;
            result.add(new IntersectionPointEx(new Point2D.Double(px, py), 0.0));
        } else if (det < 1) {
            status = IntersectionStatus.NO_INTERSECTION_INSIDE;
        } else {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        }

        return new IntersectionResultEx(status, result);
    }

    /**
     * Computes the intersection between two polygons.
     *
     * @param points1 the points of the first polygon
     * @param points2 the points of the second polygon
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectPolygonPolygonEx(@NonNull List<Point2D.Double> points1, @NonNull List<Point2D.Double> points2) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        int length = points1.size();

        for (int i = 0; i < length; i++) {
            Point2D.Double a1 = points1.get(i);
            Point2D.Double a2 = points1.get((i + 1) % length);
            IntersectionResultEx inter = Intersections.intersectLinePolygonEx(a1, a2, points2);

            result.addAll(inter.asList());
        }

        return new IntersectionResultEx(result);

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
    public static IntersectionResultEx intersectPolygonRectangleEx(@NonNull List<Point2D.Double> points, @NonNull Point2D r0, @NonNull Point2D r1) {
        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(r0, r1);
        bottomRight = bottomRight(r0, r1);
        topRight = new Point2D.Double(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D.Double(topLeft.getX(), bottomRight.getY());

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectLinePolygonEx(topLeft, topRight, points);
        inter2 = Intersections.intersectLinePolygonEx(topRight, bottomRight, points);
        inter3 = Intersections.intersectLinePolygonEx(bottomRight, bottomLeft, points);
        inter4 = Intersections.intersectLinePolygonEx(bottomLeft, topLeft, points);

        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        return new IntersectionResultEx(result);
    }

    /**
     * Computes the intersection between two infinitely long rays 'a' and 'b'.
     * <p>
     * The intersection will contain the parameters 't' of ray 'a' in range
     * [-MAX_VALUE,MAX_VALUE].
     * <p>
     * The computed intersection will have one of the states
     * {@link IntersectionStatus#INTERSECTION},
     * {@link IntersectionStatus#NO_INTERSECTION_COINCIDENT},
     * {@link IntersectionStatus#NO_INTERSECTION_PARALLEL},
     *
     * @param a0 point 0 of ray 'a'
     * @param a1 point 1 of ray 'a'
     * @param b0 point 0 of ray 'a'
     * @param b1 point 1 of ray 'b'
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectRayRayEx(@NonNull java.awt.geom.Point2D a0,
                                                         @NonNull java.awt.geom.Point2D a1,
                                                         @NonNull java.awt.geom.Point2D b0,
                                                         @NonNull java.awt.geom.Point2D b1) {
        final double
                a0y = a0.getY(),
                b1x = b1.getX(),
                b0x = b0.getX(),
                b0y = b0.getY(),
                b1y = b1.getY(),
                a0x = a0.getX(),
                a1x = a1.getX(),
                a1y = a1.getY();
        return intersectRayRayEx(a0x, a0y, a1x, a1y, b0x, b0y, b1x, b1y);

    }

    /**
     * Computes the intersection between two infinitely long rays 'a' and 'b'.
     * <p>
     * The intersection will contain the parameters 't' of ray 'a' in range
     * [-MAX_VALUE,MAX_VALUE].
     * <p>
     * The computed intersection will have one of the states
     * {@link IntersectionStatus#INTERSECTION},
     * {@link IntersectionStatus#NO_INTERSECTION_COINCIDENT},
     * {@link IntersectionStatus#NO_INTERSECTION_PARALLEL},
     *
     * @param a0x point 0 of ray 'a'
     * @param a0y point 0 of ray 'a'
     * @param a1x point 1 of ray 'a'
     * @param a1y point 1 of ray 'a'
     * @param b0x point 0 of ray 'a'
     * @param b0y point 0 of ray 'a'
     * @param b1x point 1 of ray 'b'
     * @param b1y point 1 of ray 'b'
     * @return computed intersection
     */
    public static IntersectionResultEx intersectRayRayEx(double a0x, double a0y, double a1x, double a1y, double b0x, double b0y, double b1x, double b1y) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status;


        double ua_t = (b1x - b0x) * (a0y - b0y) - (b1y - b0y) * (a0x - b0x);
        double ub_t = (a1x - a0x) * (a0y - b0y) - (a1y - a0y) * (a0x - b0x);
        double u_b = (b1y - b0y) * (a1x - a0x) - (b1x - b0x) * (a1y - a0y);

        if (u_b != 0) {
            double ua = ua_t / u_b;
            status = IntersectionStatus.INTERSECTION;
            result.add(new IntersectionPointEx(
                    new Point2D.Double(
                            a0x + ua * (a1x - a0x),
                            a0y + ua * (a1y - a0y)),
                    ua));
        } else {
            if (ua_t == 0 || ub_t == 0) {
                status = IntersectionStatus.NO_INTERSECTION_COINCIDENT;
            } else {
                status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
            }
        }

        return new IntersectionResultEx(status, result);
    }

    @NonNull
    public static IntersectionResultEx intersectRectangleRectangleEx(double ax, double ay, double aw, double ah,
                                                                     double bx, double by, double bw, double bh) {
        return intersectRectangleRectangleEx(
                new Point2D.Double(ax, ay), new Point2D.Double(ax + aw, ay + ah),
                new Point2D.Double(bx, by), new Point2D.Double(bx + bw, by + bh));

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
    public static IntersectionResultEx intersectRectangleRectangleEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D b0, @NonNull Point2D b1) {
        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = topLeft(a0, a1);
        bottomRight = bottomRight(a0, a1);
        topRight = new Point2D.Double(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D.Double(topLeft.getX(), bottomRight.getY());

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = Intersections.intersectLineRectangleEx(topLeft, topRight, b0, b1);
        inter2 = Intersections.intersectLineRectangleEx(topRight, bottomRight, b0, b1);
        inter3 = Intersections.intersectLineRectangleEx(bottomRight, bottomLeft, b0, b1);
        inter4 = Intersections.intersectLineRectangleEx(bottomLeft, topLeft, b0, b1);

        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        return new IntersectionResultEx(result);
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
    private static Point2D.Double topLeft(@NonNull Point2D a, @NonNull Point2D b) {
        return new Point2D.Double(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()));
    }

    @NonNull
    private static Point2D.Double topLeft(double ax, double ay, double bx, double by) {
        return new Point2D.Double(Math.min(ax, bx), Math.min(ay, by));
    }


}
