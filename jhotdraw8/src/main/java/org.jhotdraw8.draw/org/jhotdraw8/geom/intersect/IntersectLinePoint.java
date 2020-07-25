package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class IntersectLinePoint {
    private IntersectLinePoint() {
    }

    /**
     * Computes the intersection between a line and a point with tolerance
     * radius r.
     * <p>
     * The returned intersection contain the parameters 't1' of the line in range
     * [0,1].
     * <p>
     * The intersection will have one of the following status:
     * <ul>
     * <li>{@link IntersectionStatus#INTERSECTION}</li>
     * <li>{@link IntersectionStatus#NO_INTERSECTION}</li>
     * </ul>
     * <p>
     * This method solves the last equation shown in the list below.
     * <ol>
     * <li>{@literal p0 + (p1 - p0) · t1 , 0 ≤ t1 ≤ 1}<br>
     * : line equation in vector form</li>
     * <li>{@literal x0 + (x1 - x0) · t1, y0 + (y1 - y0) · t1 }<br>
     * : line equation in matrix form</li>
     * <li>{@literal x0 + Δx · t1, y0 + Δy · t1 }<br>
     * : partially compacted coefficients</li>
     * <li>{@literal fx, fy }<br>
     * : compacted coefficients in matrix form</li>
     * <li>{@literal (fx - cx)² + (fy - cy)² = 0}<br>
     * : distance to point equation with fx, fy coefficients inserted</li>
     * <li>{@literal Δx²·Δy²·t1² }<br>
     * {@literal + 2·(Δx·(x0 - cx)+Δy·(y0 - cy))·t1 }<br>
     * {@literal - 2·(x0·cx + y0·cy) + cx² + cy² + x0² + y0²  = 0 }<br>
     * : fx, fy coefficients expanded and equation reordered</li>
     * <li>{@literal a·t1² + b·t1 + c = 0, 0 ≤ t1 ≤ 1 }<br>
     * : final quadratic polynomial
     * </li>
     * <li>{@literal 2·a·t1 + b = 0, 0 ≤ t1 ≤ 1 }<br>
     * : derivative</li>
     * </ol>
     *
     * @param x0 point 0 of the line
     * @param y0 point 0 of the line
     * @param x1 point 1 of the line
     * @param y1 point 1 of the line
     * @param cx the center of the point p.x
     * @param cy the center of the point p.y
     * @param r  the tolerance radius
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectLinePointEx(double x0, double y0, double x1, double y1, double cx, double cy, double r) {
        List<IntersectionPointEx> result = new ArrayList<>();
        // Build polynomial
        final double Δx, Δy, a, b;
        Δx = x1 - x0;
        Δy = y1 - y0;
        a = Δx * Δx + Δy * Δy;
        b = 2 * (Δx * (x0 - cx) + Δy * (y0 - cy));

        // Solve for roots in derivative
        double[] roots = new Polynomial(2 * a, b).getRoots();

        if (roots.length > 0) {
            double t = max(0, min(roots[0], 1));
            double x = x0 + t * Δx;
            double y = y0 + t * Δy;
            double dd = (x - cx) * (x - cx) + (y - cy) * (y - cy);
            if (dd <= r * r) {
                result.add(new IntersectionPointEx(new Point2D.Double(x, y), t));
            }
        }

        return new IntersectionResultEx(result);
    }
}
