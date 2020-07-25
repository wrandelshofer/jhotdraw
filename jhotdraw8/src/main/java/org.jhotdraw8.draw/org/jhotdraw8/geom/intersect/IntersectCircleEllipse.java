package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;

public class IntersectCircleEllipse {
    private IntersectCircleEllipse() {
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
        return IntersectEllipseEllipse.intersectEllipseEllipseEx(cc, r, r, ec, rx, ry);
    }

    @NonNull
    public static IntersectionResultEx intersectCircleEllipseEx(double cx1, double cy1, double r1, double cx2, double cy2, double rx2, double ry2) {
        return IntersectEllipseEllipse.intersectEllipseEllipseEx(cx1, cy1, r1, r1, cx2, cy2, rx2, ry2);
    }

    @NonNull
    public static IntersectionResultEx intersectEllipseCircleEx(double cx1, double cy1, double rx1, double ry1, double cx2, double cy2, double r2) {
        return IntersectEllipseEllipse.intersectEllipseEllipseEx(cx1, cy1, rx1, ry1, cx2, cy2, r2, r2);
    }
}
