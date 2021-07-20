/*
 * @(#)IntersectLinePolygon.java
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
public class IntersectLinePolygon {
    private IntersectLinePolygon() {
    }

    /**
     * Computes the intersection between a line and a polygon.
     * <p>
     * The intersection will contain the parameters 't1' of the line in range
     * [0,1].
     *
     * @param a0     point 0 of the line
     * @param a1     point 1 of the line
     * @param points the points of the polygon
     * @return computed intersection
     */
    public static @NonNull IntersectionResultEx intersectLinePolygonEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull List<Point2D.Double> points) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D.Double b0 = points.get(i);
            Point2D.Double b1 = points.get((i + 1) % length);
            IntersectionResultEx inter = IntersectLineLine.intersectLineLineEx(a0, a1, b0, b1);

            result.addAll(inter.asList());
        }

        return new IntersectionResultEx(result);
    }
}
