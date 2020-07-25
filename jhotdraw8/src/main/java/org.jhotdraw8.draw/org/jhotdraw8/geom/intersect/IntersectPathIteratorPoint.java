package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class IntersectPathIteratorPoint {
    private IntersectPathIteratorPoint() {
    }

    /**
     * Intersects the given path iterator with the given point.
     *
     * @param pit       the path iterator
     * @param px        the x-coordinate of the point
     * @param py        the y-coordinate of the point
     * @param tolerance radius around the point which counts as hit.
     * @return the intersection
     */
    @NonNull
    public static IntersectionResultEx intersectPathIteratorPointEx(@NonNull PathIterator pit, double px, double py, double tolerance) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        int i = 0;
        double closestDistance = Double.POSITIVE_INFINITY;
        for (; !pit.isDone(); pit.next(), i++) {
            IntersectionResultEx inter;
            switch (pit.currentSegment(seg)) {
            case PathIterator.SEG_CLOSE:
                inter = IntersectLinePoint.intersectLinePointEx(lastx, lasty, firstx, firsty, px, py, tolerance);
                break;
            case PathIterator.SEG_CUBICTO:
                x = seg[4];
                y = seg[5];
                inter = IntersectCubicCurvePoint.intersectCubicCurvePointEx(lastx, lasty, seg[0], seg[1], seg[2], seg[3], x, y, px, py, tolerance);
                lastx = x;
                lasty = y;
                break;
            case PathIterator.SEG_LINETO:
                x = seg[0];
                y = seg[1];
                inter = IntersectLinePoint.intersectLinePointEx(lastx, lasty, x, y, px, py, tolerance);
                lastx = x;
                lasty = y;
                break;
            case PathIterator.SEG_MOVETO:
                lastx = firstx = seg[0];
                lasty = firsty = seg[1];
                inter = null;
                break;
            case PathIterator.SEG_QUADTO:
                x = seg[2];
                y = seg[3];
                inter = IntersectPointQuadraticCurve.intersectQuadraticCurvePointEx(lastx, lasty, seg[0], seg[1], x, y, px, py, tolerance);
                lastx = x;
                lasty = y;
                break;
            default:
                inter = null;
                break;
            }
            if (inter != null) {
                for (IntersectionPointEx entry : inter.asList()) {
                    final double dd = entry.distanceSq(px, py);
                    IntersectionPointEx newPoint = new IntersectionPointEx(
                            entry, entry.getArgumentA() + i, new Point2D.Double(0, 0), i, 0.0, new Point2D.Double(0, 0), 0);
                    if (abs(dd - closestDistance) < Intersections.EPSILON) {
                        result.add(newPoint);
                    } else if (dd < closestDistance) {
                        result.clear();
                        closestDistance = dd;
                        result.add(newPoint);
                    }
                }
            }

        }

        // FIXME the result should contain only one point
        return new IntersectionResultEx(result);
    }
}
