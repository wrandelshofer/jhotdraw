package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;

public class IntersectCircleCubicCurve {
    private IntersectCircleCubicCurve() {
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
        return IntersectCubicCurveEllipse.intersectCubicCurveEllipseEx(p0, p1, p2, p3, c, r, r);
    }

    @NonNull
    public static IntersectionResultEx intersectCubicCurveCircleEx(
            double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3,
            double cx, double cy, double r) {
        return IntersectCubicCurveEllipse.intersectCubicCurveEllipseEx(new Point2D.Double(x0, y0), new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(x3, y3), new Point2D.Double(cx, cy), r, r);
    }
}