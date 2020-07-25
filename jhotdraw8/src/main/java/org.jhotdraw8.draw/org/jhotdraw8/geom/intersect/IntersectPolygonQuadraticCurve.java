package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectPolygonQuadraticCurve {
    /**
     * Computes the intersection between quadratic bezier curve 'p' and the
     * given closed polygon.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range
     * [0,1].
     *
     * @param p0     control point P0 of 'p'
     * @param p1     control point P1 of 'p'
     * @param p2     control point P2 of 'p'
     * @param points the points of the polygon
     * @return the computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectQuadraticCurvePolygonEx(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull List<Point2D.Double> points) {
        List<IntersectionPointEx> result = new ArrayList<>();
        int length = points.size();

        for (int i = 0; i < length; i++) {
            final Point2D.Double a0, a1;
            a0 = points.get(i);
            a1 = points.get((i + 1) % length);
            IntersectionResultEx inter = IntersectLineQuadraticCurve.intersectQuadraticCurveLineEx(p0, p1, p2, a0, a1);

            result.addAll(inter.asList());
        }

        return new IntersectionResultEx(result);
    }
}
