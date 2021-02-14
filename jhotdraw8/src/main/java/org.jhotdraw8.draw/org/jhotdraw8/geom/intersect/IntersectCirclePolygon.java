/*
 * @(#)IntersectCirclePolygon.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectCirclePolygon {
    private IntersectCirclePolygon() {
    }

    /**
     * Computes the intersection between a circle and a polygon.
     *
     * @param c      the center of the circle
     * @param r      the radius of the circle
     * @param points the points of the polygon
     * @return computed intersection
     */
    public static @NonNull IntersectionResultEx intersectCirclePolygonEx(@NonNull Point2D c, double r, @NonNull List<Point2D.Double> points) {
        List<IntersectionPointEx> result = new ArrayList<>();
        int length = points.size();
        IntersectionResultEx inter = null;

        for (int i = 0; i < length; i++) {
            final Point2D.Double a0, a1;
            a0 = points.get(i);
            a1 = points.get((i + 1) % length);

            inter = IntersectCircleLine.intersectCircleLineEx(c, r, a0, a1);
            result.addAll(inter.asList());
        }

        IntersectionStatus status;
        if (!result.isEmpty()) {
            status = IntersectionStatus.INTERSECTION;
        } else {
            status = inter == null ? IntersectionStatus.NO_INTERSECTION : inter.getStatus();
        }

        return new IntersectionResultEx(status, result);
    }
}
