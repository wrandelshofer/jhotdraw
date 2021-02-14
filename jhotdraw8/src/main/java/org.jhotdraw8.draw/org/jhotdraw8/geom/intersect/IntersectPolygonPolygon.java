/*
 * @(#)IntersectPolygonPolygon.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectPolygonPolygon {
    private IntersectPolygonPolygon() {
    }

    /**
     * Computes the intersection between two polygons.
     *
     * @param points1 the points of the first polygon
     * @param points2 the points of the second polygon
     * @return computed intersection
     */
    public static @NonNull IntersectionResultEx intersectPolygonPolygonEx(@NonNull List<Point2D.Double> points1, @NonNull List<Point2D.Double> points2) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        int length = points1.size();

        for (int i = 0; i < length; i++) {
            Point2D.Double a1 = points1.get(i);
            Point2D.Double a2 = points1.get((i + 1) % length);
            IntersectionResultEx inter = IntersectLinePolygon.intersectLinePolygonEx(a1, a2, points2);

            result.addAll(inter.asList());
        }

        return new IntersectionResultEx(result);

    }
}
