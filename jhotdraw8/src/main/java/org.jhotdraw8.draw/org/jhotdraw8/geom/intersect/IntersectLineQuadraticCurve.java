package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.jhotdraw8.geom.Geom.argumentOnLine;
import static org.jhotdraw8.geom.Geom.lerp;

public class IntersectLineQuadraticCurve {
    private IntersectLineQuadraticCurve() {
    }

    /**
     * Computes the intersection between quadratic bezier curve 'p' and the line
     * 'a'.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range
     * [0,1].
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P2 of 'p'
     * @param a0 point 0 of 'a'
     * @param a1 point 0 of 'a'
     * @return the computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectLineQuadraticCurveEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2) {
        return intersectLineQuadraticCurveEx(
                a0.getX(), a0.getY(),
                a1.getX(), a1.getY(),
                p0.getX(), p0.getY(),
                p1.getX(), p1.getY(),
                p2.getX(), p2.getY());
    }

    @NonNull
    public static IntersectionResultEx intersectLineQuadraticCurveEx(double a0x, double a0y, double a1x, double a1y,
                                                                     double p0x, double p0y, double p1x, double p1y, double p2x, double p2y) {
        return intersectLineQuadraticCurveEx(
                a0x, a0y,
                a1x, a1y,
                p0x, p0y,
                p1x, p1y,
                p2x, p2y, 1.0);
    }

    @NonNull
    public static IntersectionResultEx intersectLineQuadraticCurveEx(double a0x, double a0y, double a1x, double a1y,
                                                                     double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double maxT) {
        /* steps:
         * 1. Rotate the bezier curve so that the line coincides with the x-axis.
         *    This will position the curve in a way that makes it cross the line at points where its y-function is zero.
         * 2. Insert the control points of the rotated bezier curve in the polynomial equation.
         * 3. Find the roots of the polynomial equation.
         */

        Point2D.Double topLeft = Intersections.topLeft(a0x, a0y, a1x, a1y); // used to determine if point is on line segment
        Point2D.Double bottomRight = Intersections.bottomRight(a0x, a0y, a1x, a1y); // used to determine if point is on line segment
        List<IntersectionPointEx> result = new ArrayList<>();

        final Point2D.Double p0, p1;
        p0 = new Point2D.Double(p0x, p0y);
        p1 = new Point2D.Double(p1x, p1y);

        final Point2D.Double c2, c1, c0;       // coefficients of quadratic
        c2 = Points2D.add(Points2D.add(p0, Points2D.multiply(p1, -2)), p2x, p2y);
        c1 = Points2D.add(Points2D.multiply(p0, -2), Points2D.multiply(p1, 2));
        c0 = p0;

        // Convert line to normal form: ax + by + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D.Double n;                // normal for normal form of line
        n = new Point2D.Double(a0y - a1y, a1x - a0x);

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = a0x * a1y - a1x * a0y;

        // Transform cubic coefficients to line's coordinate system and find roots
        // of cubic
        double[] roots = new Polynomial(
                Points2D.dotProduct(n, c2),
                Points2D.dotProduct(n, c1),
                Points2D.dotProduct(n, c0) + cl
        ).getRoots();
        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D.Double p4, p5, p6;
                p4 = lerp(p0, p1, t);
                p5 = lerp(p1x, p1y, p2x, p2y, t);
                p6 = lerp(p4, p5, t);

                // See if point is on line segment
                double t1 = argumentOnLine(a0x, a0y, a1x, a1y, p6.getX(), p6.getY());
                if (t1 >= 0 && t1 <= maxT) {
                    status = IntersectionStatus.INTERSECTION;
                    result.add(new IntersectionPointEx(p6, t1));
                }
            }
        }

        return new IntersectionResultEx(status, result);
    }

    /**
     * Computes the intersection between quadratic bezier curve 'p' and the line
     * 'a'.
     * <p>
     * The intersection will contain the parameters 't1' of curve 'a' in range
     * [0,1].
     *
     * @param p0 control point P0 of 'p'
     * @param p1 control point P1 of 'p'
     * @param p2 control point P2 of 'p'
     * @param a0 point 0 of 'a'
     * @param a1 point 1 of 'a'
     * @return the computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectQuadraticCurveLineEx(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D a0, @NonNull Point2D a1) {

        // Bezier curve:
        //   (1 - t)²·p0 + 2·(1 - t)·t·p1 + t²·p2 , 0 ≤ t ≤ 1
        //   (p0 - 2·p1 + p2)·t² - 2·(p0 - p1)·t + p0
        //   c2·t² + c1·t + c0
        final Point2D c2, c1, c0;       // coefficients of quadratic
        c2 = Points2D.sum(p0, Points2D.multiply(p1, -2), p2);
        c1 = Points2D.multiply(Points2D.subtract(p0, p1), -2);
        c0 = p0;

        final double a0x, a0y, a1x, a1y;
        a0x = a0.getX();
        a1x = a1.getX();
        a0y = a0.getY();
        a1y = a1.getY();

        // Convert line to normal form: a·x + b·y + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D.Double n;                // normal for normal form of line
        n = new Point2D.Double(a0y - a1y, a1x - a0x);

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = a0x * a1y - a1x * a0y;

        // Transform cubic coefficients to line's coordinate system and find roots
        // of cubic
        final double[] roots = new Polynomial(
                Points2D.dotProduct(n, c2),
                Points2D.dotProduct(n, c1),
                Points2D.dotProduct(n, c0) + cl
        ).getRoots();

        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        final Point2D.Double topLeft, bottomRight;
        topLeft = Intersections.topLeft(a0, a1); // used to determine if point is on line segment
        bottomRight = Intersections.bottomRight(a0, a1); // used to determine if point is on line segment
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D.Double p4, p5, p6;
                p4 = lerp(p0, p1, t);
                p5 = lerp(p1, p2, t);
                p6 = lerp(p4, p5, t);

                // See if point is on line segment
                // Had to make special cases for vertical and horizontal lines due
                // to slight errors in calculation of p6
                if (a0x == a1x) {
                    if (topLeft.getY() <= p6.getY() && p6.getY() <= bottomRight.getY()) {
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(p6, t));
                    }
                } else if (a0y == a1y) {
                    if (topLeft.getX() <= p6.getX() && p6.getX() <= bottomRight.getX()) {
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(p6, t));
                    }
                } else if (Intersections.gte(p6, topLeft) && Intersections.lte(p6, bottomRight)) {
                    status = IntersectionStatus.INTERSECTION;
                    result.add(new IntersectionPointEx(p6, t));
                }
            }
        }

        return new IntersectionResultEx(status, result);
    }

    @NonNull
    public static IntersectionResultEx intersectQuadraticCurveLineEx(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2,
            double bx0, double by0, double bx1, double by1) {
        return intersectQuadraticCurveLineEx(new Point2D.Double(ax0, ay0), new Point2D.Double(ax1, ay1), new Point2D.Double(ax2, ay2),
                new Point2D.Double(bx0, by0), new Point2D.Double(bx1, by1));
    }
}
