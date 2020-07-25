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
                inter = IntersectLineRay.intersectRayLineEx(a0x, a0y, a1x, a1y, lastx, lasty, firstx, firsty, Double.MAX_VALUE);
                if (inter.getStatus() == IntersectionStatus.NO_INTERSECTION_COINCIDENT) {
                    hasTangent = true;
                }
                break;
            case PathIterator.SEG_CUBICTO:
                x = seg[4];
                y = seg[5];
                inter = IntersectCubicCurveLine.intersectLineCubicCurveEx(a0x, a0y, a1x, a1y, lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, Double.MAX_VALUE);
                lastx = x;
                lasty = y;
                break;
            case PathIterator.SEG_LINETO:
                x = seg[0];
                y = seg[1];
                inter = IntersectLineRay.intersectRayLineEx(a0x, a0y, a1x, a1y, lastx, lasty, x, y, Double.MAX_VALUE);
                if (inter.getStatus() == IntersectionStatus.NO_INTERSECTION_COINCIDENT) {
                    hasTangent = true;
                }
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
                inter = IntersectLineQuadraticCurve.intersectLineQuadraticCurveEx(a0x, a0y, a1x, a1y, lastx, lasty, seg[0], seg[1], x, y, Double.MAX_VALUE);
                lastx = x;
                lasty = y;
                break;
            default:
                inter = null;
                break;
            }

            if (inter != null) {
                for (final IntersectionPointEx intersection : inter.asList()) {
                    intersectionCount++;
                    if (intersection.getArgumentA() <= maxT) {

                        result.add(intersection.withSegment2(segmentIndex));
                    }
                }
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
    public static IntersectionResultEx intersectPathIteratorLineEx(@NonNull PathIterator pit, @NonNull Point2D a0, @NonNull Point2D a1) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        final double a0x, a0y, a1x, a1y;
        a0x = a0.getX();
        a0y = a0.getY();
        a1x = a1.getX();
        a1y = a1.getY();
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        for (; !pit.isDone(); pit.next()) {
            IntersectionResultEx inter;
            switch (pit.currentSegment(seg)) {
            case PathIterator.SEG_CLOSE:
                inter = IntersectLineLine.intersectLineLineEx(lastx, lasty, firstx, firsty, a0x, a0y, a1x, a1y);
                result.addAll(inter.asList());
                break;
            case PathIterator.SEG_CUBICTO:
                x = seg[4];
                y = seg[5];
                inter = IntersectCubicCurveLine.intersectCubicCurveLineEx(lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, a0x, a0y, a1x, a1y);
                result.addAll(inter.asList());
                lastx = x;
                lasty = y;
                break;
            case PathIterator.SEG_LINETO:
                x = seg[0];
                y = seg[1];
                inter = IntersectLineLine.intersectLineLineEx(lastx, lasty, x, y, a0x, a0y, a1x, a1y);
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
                inter = IntersectLineQuadraticCurve.intersectQuadraticCurveLineEx(lastx, lasty, seg[0], seg[1], x, y, a0x, a0y, a1x, a1y);
                result.addAll(inter.asList());
                lastx = x;
                lasty = y;
                break;
            }
        }

        return new IntersectionResultEx(result);
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
