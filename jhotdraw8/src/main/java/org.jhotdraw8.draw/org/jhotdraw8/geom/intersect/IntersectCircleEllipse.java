/*
 * @(#)IntersectCircleEllipse.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;

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
public class IntersectCircleEllipse {
    private IntersectCircleEllipse() {
    }

    /**
     * Computes the intersection between a circle and an ellipse.
     *
     * @param cc the center of the circle
     * @param r  the radius of the circle
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @return computed intersection
     */
    public static @NonNull IntersectionResult intersectCircleEllipse(@NonNull Point2D cc, double r, @NonNull Point2D ec, double rx, double ry) {
        return IntersectEllipseEllipse.intersectEllipseEllipse(cc, r, r, ec, rx, ry);
    }

    public static @NonNull IntersectionResult intersectCircleEllipse(double cx1, double cy1, double r1, double cx2, double cy2, double rx2, double ry2) {
        return IntersectEllipseEllipse.intersectEllipseEllipse(cx1, cy1, r1, r1, cx2, cy2, rx2, ry2);
    }


    public static IntersectionResultEx intersectCircleEllipseEx(double acx, double acy, double ar, double bcx, double bcy, double brx, double bry) {
        return IntersectEllipseEllipse.intersectEllipseEllipseEx(acx, acy, ar, ar, bcx, bcy, brx, bry);
    }

    public static IntersectionResultEx intersectEllipseCircleEx(double acx, double acy, double arx, double ary, double bcx, double bcy, double br) {
        return IntersectEllipseEllipse.intersectEllipseEllipseEx(acx, acy, arx, ary, bcx, bcy, br, br);
    }

    public static IntersectionResult intersectEllipseCircle(double acx, double acy, double arx, double ary, double bcx, double bcy, double br) {
        return IntersectEllipseEllipse.intersectEllipseEllipse(acx, acy, arx, ary, bcx, bcy, br, br);
    }
}
