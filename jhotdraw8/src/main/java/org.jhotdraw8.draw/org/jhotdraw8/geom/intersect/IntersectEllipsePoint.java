package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class IntersectEllipsePoint {
    private IntersectEllipsePoint() {
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
        if (abs(det) - 1 == Intersections.EPSILON) {
            status = IntersectionStatus.INTERSECTION;
            result.add(new IntersectionPointEx(new Point2D.Double(px, py), 0.0));
        } else if (det < 1) {
            status = IntersectionStatus.NO_INTERSECTION_INSIDE;
        } else {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        }

        return new IntersectionResultEx(status, result);
    }
}
