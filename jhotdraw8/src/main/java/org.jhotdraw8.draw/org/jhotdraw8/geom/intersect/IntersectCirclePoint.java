/*
 * @(#)IntersectCirclePoint.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.Geom;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.jhotdraw8.geom.Geom.lerp;

public class IntersectCirclePoint {
    private IntersectCirclePoint() {
    }

    public static @NonNull IntersectionResult intersectCirclePoint(double cx, double cy, double cr, double px, double py, double pr) {
        return intersectCirclePoint(cx, cy, cr, px, py, pr, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResultEx intersectCirclePointEx(double cx, double cy, double cr, double px, double py, double pr) {
        return intersectCirclePointEx(cx, cy, cr, px, py, pr, Geom.REAL_THRESHOLD);
    }

    public static IntersectionResult intersectCirclePoint(double cx, double cy, double cr, double px, double py, double pr, double epsilon) {
        return intersectCirclePoint(new Point2D.Double(cx, cy), cr, new Point2D.Double(px, py), pr, epsilon);
    }

    public static @NonNull IntersectionResult intersectCirclePoint(@NonNull Point2D cc, double cr, @NonNull Point2D pc, double pr, double epsilon) {
        List<IntersectionPoint> result = new ArrayList<>();

        double c_dist = cc.distance(pc);

        IntersectionStatus status;
        if (Geom.almostZero(c_dist, epsilon)) {
            status = IntersectionStatus.NO_INTERSECTION_INSIDE;
        } else {

            Point2D.Double p = lerp(cc, pc, cr / c_dist);
            final double dd = p.distanceSq(pc);
            if (dd <= pr * pr) {
                status = IntersectionStatus.INTERSECTION;
                // FIXME compute t
                result.add(new IntersectionPoint(p, Geom.atan2(p.getY() - cc.getY(), p.getX() - cc.getX())));
            } else {
                status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
            }
        }
        return new IntersectionResult(status, result);
    }

    /**
     * Computes the intersection between a point and a circle.
     * <p>
     * The code of this method has been derived from intersection.js [1].
     * <p>
     * References:
     * <dl>
     *     <dt>[1] intersection.js</dt>
     *     <dd>intersection.js, Copyright (c) 2002 Kevin Lindsey, BSD 3-clause license.
     *     <a href="http://www.kevlindev.com/gui/math/intersection/Intersection.js">kevlindev.com</a></dd>
     * </dl>
     *
     * @param point  the point
     * @param center the center of the circle
     * @param radius the radius of the circle
     * @return computed intersection
     */
    public static @NonNull IntersectionResult intersectPointCircle(@NonNull Point2D point, @NonNull Point2D center, double radius) {
        List<IntersectionPoint> result = new ArrayList<>();

        final double distance = point.distance(center);

        IntersectionStatus status;
        if (distance - radius < Intersections.EPSILON) {
            status = IntersectionStatus.INTERSECTION;
            // FIXME compute t with atan2/2*PI
            result.add(new IntersectionPoint(new Point2D.Double(point.getX(), point.getY()), Double.NaN));
        } else if (distance < radius) {
            status = IntersectionStatus.NO_INTERSECTION_INSIDE;
        } else {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        }
        return new IntersectionResult(status, result);
    }

    public static IntersectionResultEx intersectCirclePointEx(double cx, double cy, double cr, double px, double py, double pr, double epsilon) {
        IntersectionResult result = intersectCirclePoint(new Point2D.Double(cx, cy), cr, new Point2D.Double(px, py), pr, epsilon);
        List<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : result) {
            double x = ip.getX();
            double y = ip.getY();
            list.add(new IntersectionPointEx(x, y,
                    ip.getArgumentA(), y - cy, cx - x,
                    0, 1, 0
            ));
        }
        return new IntersectionResultEx(result.getStatus(), list);

    }
}
