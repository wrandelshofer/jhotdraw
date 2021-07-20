/*
 * @(#)IntersectCubicCurveQuadCurve.java
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

public class IntersectCubicCurveQuadCurve {

    private static final double CURVE_A_B_TOLERANCE = 1e-3;
    private static final double ROOT_X_Y_TOLERANCE = 1e-4;

    private IntersectCubicCurveQuadCurve() {
    }

    public static @NonNull IntersectionResult intersectQuadCurveCubicCurve(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double b3x, double b3y) {
        return intersectQuadCurveCubicCurve(a0x, a0y, a1x, a1y, a2x, a2y, b0x, b0y, b1x, b1y, b2x, b2y, b3x, b3y, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResultEx intersectQuadCurveCubicCurveEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double b3x, double b3y) {
        return intersectQuadCurveCubicCurveEx(a0x, a0y, a1x, a1y, a2x, a2y, b0x, b0y, b1x, b1y, b2x, b2y, b3x, b3y, Geom.REAL_THRESHOLD);
    }

    public static IntersectionResult intersectQuadCurveCubicCurve(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double b3x, double b3y, double epsilon) {
        return intersectQuadCurveCubicCurve(new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), new Point2D.Double(a2x, a2y),
                new Point2D.Double(b0x, b0y), new Point2D.Double(b1x, b1y), new Point2D.Double(b2x, b2y), new Point2D.Double(b3x, b3y));

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
    public static @NonNull IntersectionResult intersectQuadCurveCubicCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2,
                                                                           @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2, @NonNull Point2D b3) {
        return intersectQuadCurveCubicCurve(a0, a1, a2, b0, b1, b2, b3, Geom.REAL_THRESHOLD);
    }

    /**
     * The code of this method has been derived from intersection.js [1].
     * <p>
     * References:
     * <dl>
     *     <dt>[1] intersection.js</dt>
     *     <dd>intersection.js, Copyright (c) 2002 Kevin Lindsey, BSD 3-clause license.
     *     <a href="http://www.kevlindev.com/gui/math/intersection/Intersection.js">kevlindev.com</a></dd>
     * </dl>
     *
     * @param a0
     * @param a1
     * @param a2
     * @param b0
     * @param b1
     * @param b2
     * @param b3
     * @param epsilon
     * @return
     */
    public static IntersectionResult intersectQuadCurveCubicCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2,
                                                                  @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2, @NonNull Point2D b3, double epsilon) {
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

        List<IntersectionPoint> result = new ArrayList<>();
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

                checkRoots:
                for (int j = 0; j < xRoots.length; j++) {
                    double xRoot = xRoots[j];

                    if (0 <= xRoot && xRoot <= 1) {
                        for (int k = 0; k < yRoots.length; k++) {
                            if (Math.abs(xRoot - yRoots[k]) < ROOT_X_Y_TOLERANCE) {
                                result.add(
                                        new IntersectionPoint(
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

        return new IntersectionResult(result.isEmpty() ? IntersectionStatus.NO_INTERSECTION : IntersectionStatus.INTERSECTION,
                result);
    }

    public static @NonNull IntersectionResult intersectCubicCurveQuadCurve(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y) {
        return intersectCubicCurveQuadCurve(a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, b0x, b0y, b1x, b1y, b2x, b2y, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResult intersectCubicCurveQuadCurve(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double epsilon) {
        IntersectionResult resultB = intersectQuadCurveCubicCurve(
                new Point2D.Double(b0x, b0y), new Point2D.Double(b1x, b1y), new Point2D.Double(b2x, b2y),
                new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), new Point2D.Double(a2x, a2y),
                new Point2D.Double(a3x, a3y), epsilon);
        List<IntersectionPoint> list = new ArrayList<>();
        for (IntersectionPoint ip : resultB) {
            double x = ip.getX();
            double y = ip.getY();
            IntersectionResult resultA = IntersectCubicCurvePoint.intersectCubicCurvePoint(a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, x, y, epsilon);
            list.add(new IntersectionPoint(x, y, resultA.getFirst().getArgumentA()));
        }

        return new IntersectionResult(resultB.getStatus(), list);
    }

    public static @NonNull IntersectionResultEx intersectCubicCurveQuadCurveEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y) {
        return intersectCubicCurveQuadCurveEx(a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, b0x, b0y, b1x, b1y, b2x, b2y, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResultEx intersectCubicCurveQuadCurveEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double epsilon) {
        IntersectionResult resultB = intersectQuadCurveCubicCurve(
                new Point2D.Double(b0x, b0y), new Point2D.Double(b1x, b1y), new Point2D.Double(b2x, b2y),
                new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), new Point2D.Double(a2x, a2y),
                new Point2D.Double(a3x, a3y), epsilon);
        List<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : resultB) {
            double x = ip.getX();
            double y = ip.getY();
            IntersectionResultEx resultA = IntersectCubicCurvePoint.intersectCubicCurvePointEx(a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, x, y, CURVE_A_B_TOLERANCE);
            // resultA should never by empty, but if this happen we rather have no intersection than a crash.
            if (!resultA.isEmpty()) {
                IntersectionPointEx firstA = resultA.getFirst();
                list.add(new IntersectionPointEx(ip, firstA.getArgumentA(), firstA.getTangentA(),
                        ip.getArgumentA(), BezierCurves.evalQuadCurveTangent(b0x, b0y, b1x, b1y, b2x, b2y,
                        ip.getArgumentA())));
            }
        }

        return new IntersectionResultEx(resultB.getStatus(), list);
    }

    public static @NonNull IntersectionResultEx intersectQuadCurveCubicCurveEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double b3x, double b3y, double epsilon) {
        IntersectionResult resultA = intersectQuadCurveCubicCurve(
                new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), new Point2D.Double(a2x, a2y),
                new Point2D.Double(b0x, b0y), new Point2D.Double(b1x, b1y), new Point2D.Double(b2x, b2y),
                new Point2D.Double(b3x, b3y), epsilon);
        List<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : resultA) {
            double x = ip.getX();
            double y = ip.getY();
            IntersectionResultEx resultB = IntersectCubicCurvePoint.intersectCubicCurvePointEx(b0x, b0y, b1x, b1y, b2x, b2y, b3x, b3y, x, y, epsilon);
            IntersectionPointEx firstB = resultB.getFirst();
            list.add(new IntersectionPointEx(ip,
                    ip.getArgumentA(), BezierCurves.evalQuadCurveTangent(b0x, b0y, b1x, b1y, b2x, b2y, ip.getArgumentA()),
                    firstB.getArgumentA(), firstB.getTangentA()
            ));
        }

        return new IntersectionResultEx(resultA.getStatus(), list);
    }
}
