/*
 * @(#)IntersectPolygonRectangle.java
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
public class IntersectPolygonRectangle {
    private IntersectPolygonRectangle() {
    }

    /**
     * Computes the intersection between a polygon and a rectangle.
     *
     * @param points the points of the polygon
     * @param r0     corner point 0 of the rectangle
     * @param r1     corner point 1 of the rectangle
     * @return computed intersection
     */
    public static @NonNull IntersectionResultEx intersectPolygonRectangleEx(@NonNull List<Point2D.Double> points, @NonNull Point2D r0, @NonNull Point2D r1) {
        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = Intersections.topLeft(r0, r1);
        bottomRight = Intersections.bottomRight(r0, r1);
        topRight = new Point2D.Double(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D.Double(topLeft.getX(), bottomRight.getY());

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = IntersectLinePolygon.intersectLinePolygonEx(topLeft, topRight, points);
        inter2 = IntersectLinePolygon.intersectLinePolygonEx(topRight, bottomRight, points);
        inter3 = IntersectLinePolygon.intersectLinePolygonEx(bottomRight, bottomLeft, points);
        inter4 = IntersectLinePolygon.intersectLinePolygonEx(bottomLeft, topLeft, points);

        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        return new IntersectionResultEx(result);
    }
}
