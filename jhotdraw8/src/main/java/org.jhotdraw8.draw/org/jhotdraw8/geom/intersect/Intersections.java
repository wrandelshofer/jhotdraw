/*
 * @(#)Intersections.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.Arrays;

class Intersections {

    /**
     * Values closer to zero than epsilon are treated as zero .
     * Machine precision for double is 2^-53.
     */
    public static final double EPSILON = 1.0 / (1L << 33);

    /**
     * Prevent instantiation.
     */
    private Intersections() {
    }

    private static @NonNull double[] addZeroAndOne(@NonNull double[] clampedRoots) {
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
    public static @NonNull Polynomial bezout(double[] e1, double[] e2) {
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
    public static @NonNull Point2D.Double bottomRight(@NonNull Point2D a, @NonNull Point2D b) {
        return new Point2D.Double(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()));
    }

    public static @NonNull Point2D.Double bottomRight(double ax, double ay, double bx, double by) {
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
    static boolean gte(@NonNull Point2D a, @NonNull Point2D b) {
        return a.getX() >= b.getX() && a.getY() >= b.getY();
    }


    /**
     * Returns true if point 'a' is less or equal to point 'b'. Compares the
     * x-coordinates first, and if they are equal compares the y-coordinates.
     *
     * @param a point a
     * @param b point b
     * @return true if a is less or equal b
     */
    static boolean lte(@NonNull Point2D a, @NonNull Point2D b) {
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
    public static @NonNull Point2D.Double topLeft(@NonNull Point2D a, @NonNull Point2D b) {
        return new Point2D.Double(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()));
    }

    public static @NonNull Point2D.Double topLeft(double ax, double ay, double bx, double by) {
        return new Point2D.Double(Math.min(ax, bx), Math.min(ay, by));
    }


}
