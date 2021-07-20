/*
 * @(#)IntersectEllipseRectangle.java
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
public class IntersectEllipseRectangle {
    private IntersectEllipseRectangle() {
    }

    /**
     * Computes the intersection between an ellipse and a rectangle.
     *
     * @param c  the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @param r1 corner point 1 of the rectangle
     * @param r2 corner point 2 of the rectangle
     * @return computed intersection
     */
    public static @NonNull IntersectionResult intersectEllipseRectangle(@NonNull Point2D c, double rx, double ry, @NonNull Point2D r1, @NonNull Point2D r2) {
        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = Intersections.topLeft(r1, r2);
        bottomRight = Intersections.bottomRight(r1, r2);
        topRight = new Point2D.Double(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D.Double(topLeft.getX(), bottomRight.getY());

        final IntersectionResult inter1, inter2, inter3, inter4;
        inter1 = IntersectEllipseLine.intersectEllipseLine(c, rx, ry, topLeft, topRight);
        inter2 = IntersectEllipseLine.intersectEllipseLine(c, rx, ry, topRight, bottomRight);
        inter3 = IntersectEllipseLine.intersectEllipseLine(c, rx, ry, bottomRight, bottomLeft);
        inter4 = IntersectEllipseLine.intersectEllipseLine(c, rx, ry, bottomLeft, topLeft);

        List<IntersectionPoint> result = new ArrayList<>();

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        return new IntersectionResult(result);
    }

    public static IntersectionResultEx intersectEllipseRectangleEx(@NonNull Point2D c, double rx, double ry, @NonNull Point2D r1, @NonNull Point2D r2) {
        final Point2D.Double topLeft, bottomRight, topRight, bottomLeft;
        topLeft = Intersections.topLeft(r1, r2);
        bottomRight = Intersections.bottomRight(r1, r2);
        topRight = new Point2D.Double(bottomRight.getX(), topLeft.getY());
        bottomLeft = new Point2D.Double(topLeft.getX(), bottomRight.getY());

        final IntersectionResultEx inter1, inter2, inter3, inter4;
        inter1 = IntersectEllipseLine.intersectEllipseLineEx(c, rx, ry, topLeft, topRight);
        inter2 = IntersectEllipseLine.intersectEllipseLineEx(c, rx, ry, topRight, bottomRight);
        inter3 = IntersectEllipseLine.intersectEllipseLineEx(c, rx, ry, bottomRight, bottomLeft);
        inter4 = IntersectEllipseLine.intersectEllipseLineEx(c, rx, ry, bottomLeft, topLeft);

        List<IntersectionPointEx> result = new ArrayList<>();

        result.addAll(inter1.asList());
        result.addAll(inter2.asList());
        result.addAll(inter3.asList());
        result.addAll(inter4.asList());

        return new IntersectionResultEx(result);
    }
}
