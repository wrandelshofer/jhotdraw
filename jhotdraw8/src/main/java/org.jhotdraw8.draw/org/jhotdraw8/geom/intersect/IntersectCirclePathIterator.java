/*
 * @(#)IntersectCirclePathIterator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.PathIterator;
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
public class IntersectCirclePathIterator {
    private IntersectCirclePathIterator() {
    }

    public static @NonNull IntersectionResultEx intersectPathIteratorCircleEx(@NonNull PathIterator pit, double cx, double cy, double r) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        for (; !pit.isDone(); pit.next()) {
            IntersectionResultEx inter;
            switch (pit.currentSegment(seg)) {
            case PathIterator.SEG_CLOSE:
                inter = IntersectCircleLine.intersectLineCircleEx(lastx, lasty, firstx, firsty, cx, cy, r);
                // FIXME add segment number to t
                result.addAll(inter.asList());
                break;
            case PathIterator.SEG_CUBICTO:
                x = seg[4];
                y = seg[5];
                inter = IntersectCircleCubicCurve.intersectCubicCurveCircleEx(lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, cx, cy, r);
                // FIXME add segment number to t
                result.addAll(inter.asList());
                lastx = x;
                lasty = y;
                break;
            case PathIterator.SEG_LINETO:
                x = seg[0];
                y = seg[1];
                inter = IntersectCircleLine.intersectLineCircleEx(lastx, lasty, x, y, cx, cy, r);
                // FIXME add segment number to t
                result.addAll(inter.asList());
                lastx = x;
                lasty = y;
                break;
            case PathIterator.SEG_MOVETO:
                lastx = firstx = seg[0];
                lasty = firsty = seg[1];
                break;
            case PathIterator.SEG_QUADTO:
                x = seg[2];
                y = seg[3];
                inter = IntersectCircleQuadCurve.intersectQuadCurveCircleEx(lastx, lasty, seg[0], seg[1], x, y, cx, cy, r);
                // FIXME add segment number to t
                result.addAll(inter.asList());
                lastx = x;
                lasty = y;
                break;
            }
        }

        return new IntersectionResultEx(result);
    }
}
