/*
 * @(#)IntersectEllipseQuadCurve.java
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

public class IntersectEllipseQuadCurve {
    private IntersectEllipseQuadCurve() {
    }

    public static @NonNull IntersectionResult intersectQuadCurveEllipse(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double bcx, double bcy, double brx, double bry) {
        return intersectQuadCurveEllipse(a0x, a0y, a1x, a1y, a2x, a2y, bcx, bcy, brx, bry, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResult intersectQuadCurveEllipse(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double bcx, double bcy, double brx, double bry, double epsilon) {
        return intersectQuadCurveEllipse(new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), new Point2D.Double(a2x, a2y), new Point2D.Double(bcx, bcy), brx, bry, epsilon);
    }

    public static @NonNull IntersectionResultEx intersectQuadCurveEllipseEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double bcx, double bcy, double brx, double bry) {
        return intersectQuadCurveEllipseEx(a0x, a0y, a1x, a1y, a2x, a2y, bcx, bcy, brx, bry, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResultEx intersectQuadCurveEllipseEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y,
            double bcx, double bcy, double brx, double bry, double epsilon) {
        IntersectionResult result = intersectQuadCurveEllipse(new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), new Point2D.Double(a2x, a2y), new Point2D.Double(bcx, bcy), brx, bry, epsilon);
        ArrayList<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : result) {
            double x = ip.getX();
            double y = ip.getY();
            Point2D.Double tangentA = BezierCurves.evalQuadCurveTangent(a0x, a0y, a1x, a1y, a2x, a2y, ip.getArgumentA());
            double argumentB = Geom.atan2Ellipse(bcx, bcy, brx, bry, x, y);
            list.add(new IntersectionPointEx(
                    x, y,
                    ip.getArgumentA(), tangentA.getX(), tangentA.getY(),
                    argumentB, x - bcx, bcy - y
            ));
        }

        return new IntersectionResultEx(result.getStatus(), list);
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
    public static @NonNull IntersectionResult intersectQuadCurveEllipse(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D c, double rx, double ry) {
        return intersectQuadCurveEllipse(p0, p1, p2, c, rx, ry, Geom.REAL_THRESHOLD);
    }

    /**
     * The code of this method has been derived from intersection.js by
     * Kevin Lindsey, copyright 2002 Kevin Lindsey, BSD 3-clause license.
     * http://www.kevlindev.com/gui/math/intersection/Intersection.js.
     *
     * @param p0
     * @param p1
     * @param p2
     * @param c
     * @param rx
     * @param ry
     * @param epsilon
     * @return
     */
    public static @NonNull IntersectionResult intersectQuadCurveEllipse(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D c, double rx, double ry, double epsilon) {
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

        List<IntersectionPoint> result = new ArrayList<>();
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (-epsilon <= t && t <= 1 + epsilon) {
                result.add(new IntersectionPoint(
                        Points2D.sum(Points2D.multiply(c2, t * t), Points2D.multiply(c1, t), c0), t));
            }
        }

        IntersectionStatus status;
        if (result.size() > 0) {
            status = IntersectionStatus.INTERSECTION;
        } else {
            return IntersectEllipsePoint.intersectPointEllipse(p0, c, rx, ry);
        }

        return new IntersectionResult(status, result);
    }

    public static @NonNull IntersectionResult intersectEllipseQuadCurve(
            double acx, double acy, double arx, double ary,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y) {
        IntersectionResult resultB = intersectQuadCurveEllipse(new Point2D.Double(b0x, b0y), new Point2D.Double(b1x, b1y), new Point2D.Double(b2x, b2y), new Point2D.Double(acx, acy), arx, ary);
        ArrayList<IntersectionPoint> list = new ArrayList<>();
        for (IntersectionPoint ip : resultB) {
            double x = ip.getX();
            double y = ip.getY();
            list.add(new IntersectionPoint(
                    x, y,
                    Geom.atan2Ellipse(acx, acy, arx, ary, x, y)
            ));
        }

        return new IntersectionResult(resultB.getStatus(), list);
    }

    public static @NonNull IntersectionResultEx intersectEllipseQuadCurveEx(
            double acx, double acy, double arx, double ary,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y) {
        return intersectEllipseQuadCurveEx(acx, acy, arx, ary, b0x, b0y, b1x, b1y, b2x, b2y, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResultEx intersectEllipseQuadCurveEx(
            double acx, double acy, double arx, double ary,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double epsilon) {
        IntersectionResultEx resultB = intersectQuadCurveEllipseEx(
                b0x, b0y, b1x, b1y, b2x, b2y, acx, acy, arx, ary, epsilon);
        ArrayList<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : resultB) {
            double x = ip.getX();
            double y = ip.getY();
            Point2D.Double tangentB = BezierCurves.evalQuadCurveTangent(b0x, b0y, b1x, b1y, b2x, b2y, ip.getArgumentA());
            list.add(new IntersectionPointEx(
                    x, y,
                    Geom.atan2Ellipse(acx, acy, arx, ary, x, y), x - acx, acy - y,
                    ip.getArgumentA(), tangentB.getX(), tangentB.getY()
            ));
        }

        return new IntersectionResultEx(resultB.getStatus(), list);
    }
}
