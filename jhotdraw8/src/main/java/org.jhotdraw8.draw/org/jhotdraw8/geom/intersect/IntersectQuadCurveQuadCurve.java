/*
 * @(#)IntersectQuadCurveQuadCurve.java
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

public class IntersectQuadCurveQuadCurve {
    private static final double CURVE_A_B_TOLERANCE = 1e-3;
    private static final double ROOT_X_Y_TOLERANCE = 1e-4;

    private IntersectQuadCurveQuadCurve() {
    }

    public static @NonNull IntersectionResult intersectQuadCurveQuadCurve(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y) {
        return intersectQuadCurveQuadCurve(a0x, a0y, a1x, a1y, a2x, a2y, b0x, b0y, b1x, b1y, b2x, b2y, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResultEx intersectQuadCurveQuadCurveEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y) {
        return intersectQuadCurveQuadCurveEx(a0x, a0y, a1x, a1y, a2x, a2y, b0x, b0y, b1x, b1y, b2x, b2y, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResultEx intersectQuadCurveQuadCurveEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double epsilon) {
        IntersectionResult resultA = intersectQuadCurveQuadCurve(a0x, a0y, a1x, a1y, a2x, a2y, b0x, b0y, b1x, b1y, b2x, b2y, epsilon);
        IntersectionResult resultB = intersectQuadCurveQuadCurve(b0x, b0y, b1x, b1y, b2x, b2y, a0x, a0y, a1x, a1y, a2x, a2y, epsilon);

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
            // argumentB should always exist, but if it does not we rather have no intersection instead of a crash.
            if (!Double.isNaN(argumentB)) {
                Point2D.Double tangentA = BezierCurves.evalQuadCurveTangent(a0x, a0y, a1x, a1y, a2x, a2y, ipA.getArgumentA());
                Point2D.Double tangentB = BezierCurves.evalQuadCurveTangent(b0x, b0y, b1x, b1y, b2x, b2y, argumentB);
                list.add(new IntersectionPointEx(
                        x, y,
                        ipA.getArgumentA(), tangentA.getX(), tangentA.getY(),
                        argumentB, tangentB.getX(), tangentB.getY()
                ));
            }
        }

        return new IntersectionResultEx(resultA.getStatus(), list);
    }

    public static @NonNull IntersectionResult intersectQuadCurveQuadCurve(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double epsilon) {
        return intersectQuadCurveQuadCurve(new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), new Point2D.Double(a2x, a2y),
                new Point2D.Double(b0x, b0y), new Point2D.Double(b1x, b1y), new Point2D.Double(b2x, b2y), epsilon);

    }

    /**
     * Computes the intersection between quadratic bezier curve 'a' and
     * quadratic bezier curve 'b'.
     * <p>
     * The intersection will contain the parameters 't1' of curve 'a' in range
     * [0,1].
     * <p>
     * The code of this method has been derived from intersection.js by
     * Kevin Lindsey, copyright 2002 Kevin Lindsey, BSD 3-clause license.
     * http://www.kevlindev.com/gui/math/intersection/Intersection.js.
     *
     * @param a0 control point P0 of 'a'
     * @param a1 control point P1 of 'a'
     * @param a2 control point P2 of 'a'
     * @param b0 control point P0 of 'b'
     * @param b1 control point P1 of 'b'
     * @param b2 control point P2 of 'b'
     * @return the computed result
     */
    public static @NonNull IntersectionResult intersectQuadCurveQuadCurve(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2, @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2,
                                                                          double epsilon) {
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

        List<IntersectionPoint> result = new ArrayList<>();
        for (int i = 0; i < roots.length; i++) {
            double s = roots[i];

            if (-epsilon <= s && s <= 1 + epsilon) {
                double[] xRoots = new Polynomial(
                        c12x, c11x,
                        c10x - c20x - s * c21x - s * s * c22x
                ).getRoots();
                double[] yRoots = new Polynomial(
                        c12y, c11y,
                        c10y - c20y - s * c21y - s * s * c22y
                ).getRoots();

                if (xRoots.length > 0 && yRoots.length > 0) {
                    checkRoots:
                    for (int j = 0; j < xRoots.length; j++) {
                        double xRoot = xRoots[j];

                        if (0 <= xRoot && xRoot <= 1) {
                            for (int k = 0; k < yRoots.length; k++) {
                                if (Math.abs(xRoot - yRoots[k]) < ROOT_X_Y_TOLERANCE) {
                                    result.add(new IntersectionPoint(
                                            Points2D.add(Points2D.multiply(c22, s * s), Points2D.add(Points2D.multiply(c21, s), c20)),
                                            xRoot));
                                    break checkRoots;
                                }
                            }
                        }
                    }
                }
            }
        }

        return new IntersectionResult(result.isEmpty() ? IntersectionStatus.NO_INTERSECTION : IntersectionStatus.INTERSECTION,
                result);
    }
}
