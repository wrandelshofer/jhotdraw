/*
 * @(#)IntersectEllipsePolygon.java
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
public class IntersectEllipsePolygon {
    private IntersectEllipsePolygon() {
    }

    /**
     * Computes the intersection between a circle and a polygon.
     *
     * @param c      the center of the ellipse
     * @param rx     the x-radius of the ellipse
     * @param ry     the y-radius of the ellipse
     * @param points the points of the polygon
     * @return computed intersection
     */
    public static @NonNull IntersectionResult intersectEllipsePolygon(@NonNull Point2D c, double rx, double ry, @NonNull List<Point2D.Double> points) {
        List<IntersectionPoint> result = new ArrayList<>();
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D.Double b1 = points.get(i);
            Point2D.Double b2 = points.get((i + 1) % length);
            IntersectionResult inter = IntersectEllipseLine.intersectEllipseLine(c, rx, ry, b1, b2);

            result.addAll(inter.asList());
        }

        return new IntersectionResult(result.isEmpty() ? IntersectionStatus.NO_INTERSECTION : IntersectionStatus.INTERSECTION,
                result);
    }
}
