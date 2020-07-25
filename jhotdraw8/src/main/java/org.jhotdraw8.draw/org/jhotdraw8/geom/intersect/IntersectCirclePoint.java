package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static org.jhotdraw8.geom.Geom.lerp;

public class IntersectCirclePoint {
    private IntersectCirclePoint() {
    }

    @NonNull
    public static IntersectionResultEx intersectCirclePointEx(double cx, double cy, double cr, double px, double py, double pr) {
        return intersectCirclePointEx(new Point2D.Double(cx, cy), cr, new Point2D.Double(px, py), pr);
    }

    @NonNull
    public static IntersectionResultEx intersectCirclePointEx(@NonNull Point2D cc, double cr, @NonNull Point2D pc, double pr) {
        List<IntersectionPointEx> result = new ArrayList<>();

        double c_dist = cc.distance(pc);

        IntersectionStatus status;
        if (abs(c_dist) < Intersections.EPSILON) {
            status = IntersectionStatus.NO_INTERSECTION_INSIDE;
        } else {

            Point2D.Double p = lerp(cc, pc, cr / c_dist);
            final double dd = p.distanceSq(pc);
            if (dd <= pr * pr) {
                status = IntersectionStatus.INTERSECTION;
                // FIXME compute t
                result.add(new IntersectionPointEx(p, Double.NaN));
            } else {
                status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
            }
        }
        return new IntersectionResultEx(status, result);
    }

    /**
     * Computes the intersection between a point and a circle.
     *
     * @param point  the point
     * @param center the center of the circle
     * @param radius the radius of the circle
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectPointCircleEx(@NonNull Point2D point, @NonNull Point2D center, double radius) {
        List<IntersectionPointEx> result = new ArrayList<>();

        final double distance = point.distance(center);

        IntersectionStatus status;
        if (distance - radius < Intersections.EPSILON) {
            status = IntersectionStatus.INTERSECTION;
            // FIXME compute t with atan2/2*PI
            result.add(new IntersectionPointEx(new Point2D.Double(point.getX(), point.getY()), 0.0));
        } else if (distance < radius) {
            status = IntersectionStatus.NO_INTERSECTION_INSIDE;
        } else {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        }
        return new IntersectionResultEx(status, result);
    }
}
