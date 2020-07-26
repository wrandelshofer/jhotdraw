package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.DoubleArrayList;
import org.jhotdraw8.geom.BezierCurves;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectCubicCurveEllipse {
    private IntersectCubicCurveEllipse() {
    }

    @NonNull
    public static IntersectionResult intersectCubicCurveEllipse(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy, double rx, double ry) {
        return intersectCubicCurveEllipse(
                new Point2D.Double(x0, y0), new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(x3, y3),
                new Point2D.Double(cx, cy), rx, ry);

    }

    @NonNull
    public static IntersectionResult intersectCubicCurveEllipse(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy, double rx, double ry, double epsilon) {
        return intersectCubicCurveEllipse(
                new Point2D.Double(x0, y0), new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(x3, y3),
                new Point2D.Double(cx, cy), rx, ry, epsilon);

    }

    @NonNull
    public static IntersectionResultEx intersectCubicCurveEllipseEx(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy, double rx, double ry) {
        return intersectCubicCurveEllipseEx(
                x0, y0, x1, y1, x2, y2, x3, y3,
                cx, cy, rx, ry, Geom.REAL_THRESHOLD);

    }

    @NonNull
    public static IntersectionResultEx intersectCubicCurveEllipseEx(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy, double rx, double ry, double epsilon) {
        IntersectionResult result = intersectCubicCurveEllipse(
                new Point2D.Double(x0, y0), new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(x3, y3),
                new Point2D.Double(cx, cy), rx, ry, epsilon);
        ArrayList<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : result) {
            double x = ip.getX();
            double y = ip.getY();
            Point2D.Double tangentA = BezierCurves.evalCubicCurveTangent(x0, y0, x1, y1, x2, y2, x3, y3, ip.getArgumentA());
            list.add(new IntersectionPointEx(
                    x, y,
                    ip.getArgumentA(), tangentA.getX(), tangentA.getY(),
                    Geom.atan2Ellipse(cx, cy, rx, ry, x, y), y - cy, cx - x
            ));
        }

        return new IntersectionResultEx(result.getStatus(), list);

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
    public static IntersectionResult intersectCubicCurveEllipse(
            @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3,
            @NonNull Point2D ec, double rx, double ry) {
        return intersectCubicCurveEllipse(p0, p1, p2, p3, ec, rx, ry, Geom.REAL_THRESHOLD);
    }

    @NonNull
    public static IntersectionResult intersectCubicCurveEllipse(
            @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3,
            @NonNull Point2D ec, double rx, double ry, double epsilon) {
        Point2D.Double a, b, c, d;       // temporary variables
        List<IntersectionPoint> result = new ArrayList<>();

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
        DoubleArrayList roots = poly.getRootsInInterval(-epsilon, 1 + epsilon);

        for (int i = 0; i < roots.size(); i++) {
            double t = Geom.clamp(roots.get(i), 0, 1);

            result.add(new IntersectionPoint(
                    Points2D.sum(Points2D.multiply(c3, t * t * t), Points2D.multiply(c2, t * t), Points2D.multiply(c1, t), c0), t));
        }

        if (result.size() > 0) {
            return new IntersectionResult(
                    IntersectionStatus.INTERSECTION,
                    result);
        } else {
            return IntersectEllipsePoint.intersectPointEllipse(p0, ec, rx, ry);// Computes inside/outside status
        }

    }
}
