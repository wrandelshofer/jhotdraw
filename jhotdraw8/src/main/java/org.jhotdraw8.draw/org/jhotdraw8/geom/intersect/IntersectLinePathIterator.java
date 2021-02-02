/*
 * @(#)IntersectLinePathIterator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectLinePathIterator {
    private IntersectLinePathIterator() {
    }

    @NonNull
    public static IntersectionResultEx intersectLinePathIteratorEx(double a0x, double a0y, double a1x, double a1y, @NonNull PathIterator pit) {
        return intersectLinePathIteratorEx(a0x, a0y, a1x, a1y, pit, 1.0);
    }

    @NonNull
    public static IntersectionResultEx intersectLinePathIteratorEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull PathIterator pit, double maxT) {
        return intersectLinePathIteratorEx(a0.getX(), a0.getY(), a1.getX(), a1.getY(), pit, maxT);
    }

    @NonNull
    public static IntersectionResultEx intersectLinePathIteratorEx(double a0x, double a0y, double a1x, double a1y, @NonNull PathIterator pit, double maxT) {
        List<IntersectionPointEx> result = new ArrayList<>();
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        int segmentIndex = 0;
        int intersectionCount = 0;
        boolean hasTangent = false;
        for (; !pit.isDone(); pit.next()) {
            IntersectionResultEx inter;
            switch (pit.currentSegment(seg)) {
                case PathIterator.SEG_CLOSE:
                    inter = IntersectLineLine.intersectLineLineEx(a0x, a0y, a1x, a1y, lastx, lasty, firstx, firsty);
                    break;
                case PathIterator.SEG_CUBICTO:
                    x = seg[4];
                    y = seg[5];
                    inter = IntersectCubicCurveLine.intersectLineCubicCurveEx(a0x, a0y, a1x, a1y, lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y);
                    lastx = x;
                    lasty = y;
                    break;
                case PathIterator.SEG_LINETO:
                    x = seg[0];
                    y = seg[1];
                    inter = IntersectLineLine.intersectLineLineEx(a0x, a0y, a1x, a1y, lastx, lasty, x, y);
                    lastx = x;
                    lasty = y;
                    break;
                case PathIterator.SEG_MOVETO:
                    inter = null;
                    lastx = firstx = seg[0];
                    lasty = firsty = seg[1];
                    break;
                case PathIterator.SEG_QUADTO:
                    x = seg[2];
                    y = seg[3];
                    inter = IntersectLineQuadCurve.intersectLineQuadCurveEx(a0x, a0y, a1x, a1y, lastx, lasty, seg[0], seg[1], x, y);
                    lastx = x;
                    lasty = y;
                    break;
                default:
                    inter = null;
                    break;
            }

            if (inter != null && inter.getStatus() == IntersectionStatus.INTERSECTION) {
                result.addAll(inter.asList());
            }

            segmentIndex++;
        }

        IntersectionStatus status;
        if (result.isEmpty()) {
            status = intersectionCount == 0 ? (hasTangent ? IntersectionStatus.NO_INTERSECTION_TANGENT : IntersectionStatus.NO_INTERSECTION_OUTSIDE) : IntersectionStatus.NO_INTERSECTION_INSIDE;
        } else {
            status = IntersectionStatus.INTERSECTION;
        }

        return new IntersectionResultEx(status, result);
    }


    @NonNull
    public static IntersectionResultEx intersectLinePathIteratorEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull PathIterator pit) {
        IntersectionResultEx i = intersectLinePathIteratorEx(a0, a1, pit, 1.0);
        if (i.getStatus() == IntersectionStatus.INTERSECTION && i.getFirst().getArgumentA() > 1) {
            return new IntersectionResultEx(IntersectionStatus.NO_INTERSECTION, new ArrayList<>());
        } else {// FIXME remove intersections with t>1
            return i;
        }
    }
}
