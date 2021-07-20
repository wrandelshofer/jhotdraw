/*
 * @(#)IntersectPolygonPolygon.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The code of this class has been derived from intersection.js [1].
 * <p>
 * References:
 * <dl>
 *     <dt>[1] intersection.js</dt>
 *     <dd>intersection.js, Copyright (c) 2002 Kevin Lindsey, BSD 3-clause license.
 *     <a href="http://www.kevlindev.com/gui/math/intersection/Intersection.js">kevlindev.com</a></dd>
 * </dl>
 */
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
