package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectEllipseQuadraticCurve {
    private IntersectEllipseQuadraticCurve() {
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
            return IntersectEllipsePoint.intersectPointEllipseEx(p0, c, rx, ry);
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
}
