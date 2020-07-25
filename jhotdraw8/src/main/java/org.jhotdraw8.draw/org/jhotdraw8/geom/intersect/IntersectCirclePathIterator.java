package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

public class IntersectCirclePathIterator {
    private IntersectCirclePathIterator() {
    }

    @NonNull
    public static IntersectionResultEx intersectPathIteratorCircleEx(@NonNull PathIterator pit, double cx, double cy, double r) {
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
                inter = IntersectCircleQuadraticCurve.intersectQuadraticCurveCircleEx(lastx, lasty, seg[0], seg[1], x, y, cx, cy, r);
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
