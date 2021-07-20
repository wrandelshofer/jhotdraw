/*
 * @(#)IntersectCubicCurvePolygon.java
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
public class IntersectCubicCurvePolygon {
    private IntersectCubicCurvePolygon() {
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the given
     * closed polygon.
     *
     * @param p0     control point P0 of 'p'
     * @param p1     control point P1 of 'p'
     * @param p2     control point P2 of 'p'
     * @param p3     control point P3 of 'p'
     * @param points the points of the polygon
     * @return the computed intersection
     */
    public static @NonNull IntersectionResult intersectCubicCurvePolygon(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3, @NonNull List<Point2D.Double> points) {
        List<IntersectionPoint> result = new ArrayList<>();
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D.Double a1 = points.get(i);
            Point2D.Double a2 = points.get((i + 1) % length);
            IntersectionResult inter = IntersectCubicCurveLine.intersectCubicCurveLine(p0, p1, p2, p3, a1, a2);

            result.addAll(inter.asList());
        }

        return new IntersectionResult(
                result.isEmpty() ? IntersectionStatus.NO_INTERSECTION : IntersectionStatus.INTERSECTION,
                result);
    }
}
