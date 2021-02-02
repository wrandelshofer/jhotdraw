/*
 * @(#)IntersectCubicCurveCubicCurve.java
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

public class IntersectCubicCurveCubicCurve {
    private static final double CURVE_A_B_TOLERANCE = 1e-3;
    private static final double ROOT_X_Y_TOLERANCE = 1e-4;

    private IntersectCubicCurveCubicCurve() {
    }

    @NonNull
    public static IntersectionResult intersectCubicCurveCubicCurve(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double b3x, double b3y) {
        return intersectCubicCurveCubicCurve(new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), new Point2D.Double(a2x, a2y), new Point2D.Double(a3x, a3y),
                new Point2D.Double(b0x, b0y), new Point2D.Double(b1x, b1y), new Point2D.Double(b2x, b2y), new Point2D.Double(b3x, b3y), Geom.REAL_THRESHOLD);

    }

    @NonNull
    public static IntersectionResult intersectCubicCurveCubicCurve(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double b3x, double b3y, double epsilon) {
        return intersectCubicCurveCubicCurve(new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), new Point2D.Double(a2x, a2y), new Point2D.Double(a3x, a3y),
                new Point2D.Double(b0x, b0y), new Point2D.Double(b1x, b1y), new Point2D.Double(b2x, b2y), new Point2D.Double(b3x, b3y), epsilon);

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
    public static IntersectionResult intersectCubicCurveCubicCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2, @NonNull Point2D a3,
                                                                   @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2, @NonNull Point2D b3) {
        return intersectCubicCurveCubicCurve(a0, a1, a2, a3, b0, b1, b2, b3, Geom.REAL_THRESHOLD);
    }

    /**
     * Computes the intersection between cubic bezier curve 'a' and cubic bezier
     * curve 'b'.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range
     * [tMin,tMax].
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
    public static IntersectionResult intersectCubicCurveCubicCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2, @NonNull Point2D a3,
                                                                   @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2, @NonNull Point2D b3,
                                                                   double epsilon) {
        List<IntersectionPoint> result = new ArrayList<>();

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
        double tMin = -epsilon;
        double tMax = 1 + epsilon;
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

                checkRoots:
                for (double xRoot : xRoots) {
                    if (tMin < xRoot && xRoot <= tMax) {
                        for (double yRoot : yRoots) {
                            if (Geom.almostEqual(xRoot, yRoot, ROOT_X_Y_TOLERANCE)) {
                                result.add(new IntersectionPoint(
                                        Points2D.sum(
                                                Points2D.multiply(c23, s * s * s),
                                                Points2D.multiply(c22, s * s),
                                                Points2D.multiply(c21, s), c20),
                                        Geom.clamp(xRoot, 0, 1)));
                                break checkRoots;
                            }
                        }
                    }
                }
            }
        }

        return new IntersectionResult(
                result.isEmpty() ? IntersectionStatus.NO_INTERSECTION : IntersectionStatus.INTERSECTION,
                result);
    }

    public static IntersectionResultEx intersectCubicCurveCubicCurveEx(double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
                                                                       double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double b3x, double b3y
    ) {
        return intersectCubicCurveCubicCurveEx(a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, b0x, b0y, b1x, b1y, b2x, b2y, b3x, b3y, Geom.REAL_THRESHOLD);

    }

    /**
     * The code of this method has been derived from intersection.js by
     * Kevin Lindsey, copyright 2002 Kevin Lindsey, BSD 3-clause license.
     * http://www.kevlindev.com/gui/math/intersection/Intersection.js.
     *
     * @param a0x
     * @param a0y
     * @param a1x
     * @param a1y
     * @param a2x
     * @param a2y
     * @param a3x
     * @param a3y
     * @param b0x
     * @param b0y
     * @param b1x
     * @param b1y
     * @param b2x
     * @param b2y
     * @param b3x
     * @param b3y
     * @param epsilon
     * @return
     */
    public static IntersectionResultEx intersectCubicCurveCubicCurveEx(double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
                                                                       double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double b3x, double b3y,
                                                                       double epsilon) {
        IntersectionResult resultA = intersectCubicCurveCubicCurve(a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, b0x, b0y, b1x, b1y, b2x, b2y, b3x, b3y, epsilon);
        IntersectionResult resultB = intersectCubicCurveCubicCurve(b0x, b0y, b1x, b1y, b2x, b2y, b3x, b3y, a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, epsilon);

        ArrayList<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ipA : resultA) {
            double x = ipA.getX();
            double y = ipA.getY();
            double argumentB = Double.NaN;
            for (IntersectionPoint ipB : resultB) {
                if (Geom.almostEqual(ipA, ipB, CURVE_A_B_TOLERANCE)) {
                    argumentB = ipB.argumentA;
                    break;
                }
            }
            assert !Double.isNaN(argumentB) : "argumentB must exist";
            Point2D.Double tangentA = BezierCurves.evalCubicCurveTangent(a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, ipA.getArgumentA());
            Point2D.Double tangentB = BezierCurves.evalCubicCurveTangent(b0x, b0y, b1x, b1y, b2x, b2y, b3x, b3y, argumentB);
            list.add(new IntersectionPointEx(
                    x, y,
                    ipA.getArgumentA(), tangentA.getX(), tangentA.getY(),
                    argumentB, tangentB.getX(), tangentB.getY()
            ));
        }

        return new IntersectionResultEx(resultA.getStatus(), list);
    }
}
