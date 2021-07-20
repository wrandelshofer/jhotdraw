/*
 * @(#)IntersectCircleRectangle.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectCircleRectangle {
    private IntersectCircleRectangle() {
    }

    /**
     * Computes the intersection between a line and a rectangle.
     * <p>
     * The intersection will contain the parameters 't1' of the line in range
     * [0,1].
     *
     * @param a0 point 0 of the line
     * @param a1 point 1 of the line
     * @param r0 corner point 0 of the rectangle
     * @param r1 corner point 1 of the rectangle
     * @return computed intersection
     */
    public static @NonNull IntersectionResultEx intersectLineRectangleEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D r0, @NonNull Point2D r1) {
        return IntersectAABBLine.intersectLineAABBEx(a0, a1,
                Math.min(r0.getX(), r1.getX()),
                Math.min(r0.getY(), r1.getY()),
                Math.max(r0.getX(), r1.getX()),
                Math.max(r0.getY(), r1.getY()));
    }

    public static @NonNull IntersectionResultEx intersectLineRectangleEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Rectangle2D.Double r) {
        return IntersectAABBLine.intersectLineAABBEx(a0, a1, r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
    }

    public static @NonNull IntersectionResultEx intersectRectangleLineEx(@NonNull Rectangle2D.Double r, @NonNull Point2D a0, @NonNull Point2D a1) {
        return IntersectAABBLine.intersectAABBLineEx(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY(), a0, a1);
    }

    public static @NonNull IntersectionResultEx intersectCircleRectangleEx(double c1x, double c1y, double r1, double x, double y, double w, double h) {
        return intersectCircleRectangleEx(new Point2D.Double(c1x, c1y), r1, new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
    }

    /**
     * Computes the intersection between a circle and a rectangle.
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
     * @param c  the center of the circle
     * @param r  the radius of the circle
     * @param r0 corner point 0 of the rectangle
     * @param r1 corner point 1 of the rectangle
     * @return computed intersection
     */
    public static @NonNull IntersectionResultEx intersectCircleRectangleEx(@NonNull Point2D c, double r, @NonNull Point2D r0, @NonNull Point2D r1) {
        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = Intersections.topLeft(r0, r1);
        bottomRight = Intersections.bottomRight(r0, r1);
        topRight = new Point2D.Double(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D.Double(topLeft.getX(), bottomRight.getY());

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = IntersectCircleLine.intersectCircleLineEx(c, r, topLeft, topRight);
        inter2 = IntersectCircleLine.intersectCircleLineEx(c, r, topRight, bottomRight);
        inter3 = IntersectCircleLine.intersectCircleLineEx(c, r, bottomRight, bottomLeft);
        inter4 = IntersectCircleLine.intersectCircleLineEx(c, r, bottomLeft, topLeft);

        List<IntersectionPointEx> result = new ArrayList<>();

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        IntersectionStatus status;
        if (!result.isEmpty()) {
            status = IntersectionStatus.INTERSECTION;
        } else {
            status = inter1.getStatus();
        }

        return new IntersectionResultEx(status, result);
    }
}
