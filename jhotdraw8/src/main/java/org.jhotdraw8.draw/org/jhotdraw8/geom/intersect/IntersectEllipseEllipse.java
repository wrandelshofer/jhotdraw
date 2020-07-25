package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectEllipseEllipse {
    private IntersectEllipseEllipse() {
    }

    /**
     * Computes the intersection between two ellipses.
     *
     * @param c1  the center of ellipse 1
     * @param rx1 the x-radius of ellipse 1
     * @param ry1 the y-radius of ellipse 1
     * @param c2  the center of ellipse 2
     * @param rx2 the x-radius of ellipse 2
     * @param ry2 the y-radius of ellipse 2
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectEllipseEllipseEx(@NonNull Point2D c1, double rx1, double ry1, @NonNull Point2D c2, double rx2, double ry2) {
        return intersectEllipseEllipseEx(c1.getX(), c1.getY(), rx1, ry1, c2.getX(), c2.getY(), rx2, ry2);
    }

    /**
     * Computes the intersection between two ellipses.
     *
     * @param cx1 the center of ellipse 1
     * @param cy1 the center of ellipse 1
     * @param rx1 the x-radius of ellipse 1
     * @param ry1 the y-radius of ellipse 1
     * @param cx2 the center of ellipse 2
     * @param cy2 the center of ellipse 2
     * @param rx2 the x-radius of ellipse 2
     * @param ry2 the y-radius of ellipse 2
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectEllipseEllipseEx(double cx1, double cy1, double rx1, double ry1, double cx2, double cy2, double rx2, double ry2) {
        double[] a = {
                ry1 * ry1,
                0,
                rx1 * rx1,
                -2 * ry1 * ry1 * cx1,
                -2 * rx1 * rx1 * cy1,
                ry1 * ry1 * cx1 * cx1 + rx1 * rx1 * cy1 * cy1 - rx1 * rx1 * ry1 * ry1
        };
        double[] b = {
                ry2 * ry2,
                0,
                rx2 * rx2,
                -2 * ry2 * ry2 * cx2,
                -2 * rx2 * rx2 * cy2,
                ry2 * ry2 * cx2 * cx2 + rx2 * rx2 * cy2 * cy2 - rx2 * rx2 * ry2 * ry2
        };

        Polynomial yPoly = Intersections.bezout(a, b);
        double[] yRoots = yPoly.getRoots();
        double norm0 = (a[0] * a[0] + 2 * a[1] * a[1] + a[2] * a[2]) * Intersections.EPSILON;
        double norm1 = (b[0] * b[0] + 2 * b[1] * b[1] + b[2] * b[2]) * Intersections.EPSILON;
        List<IntersectionPointEx> result = new ArrayList<>();

        for (int y = 0; y < yRoots.length; y++) {
            Polynomial xPoly = new Polynomial(
                    a[0],
                    a[3] + yRoots[y] * a[1],
                    a[5] + yRoots[y] * (a[4] + yRoots[y] * a[2])
            );
            double[] xRoots = xPoly.getRoots();

            for (int x = 0; x < xRoots.length; x++) {
                double test = (a[0] * xRoots[x] + a[1] * yRoots[y] + a[3]) * xRoots[x]
                        + (a[2] * yRoots[y] + a[4]) * yRoots[y] + a[5];
                if (Math.abs(test) < norm0) {
                    test = (b[0] * xRoots[x] + b[1] * yRoots[y] + b[3]) * xRoots[x]
                            + (b[2] * yRoots[y] + b[4]) * yRoots[y] + b[5];
                    if (Math.abs(test) < norm1) {
                        // FIXME compute angle in radians
                        result.add(new IntersectionPointEx(new Point2D.Double(xRoots[x], yRoots[y]), Double.NaN));
                    }
                }
            }
        }

        return new IntersectionResultEx(result);
    }
}
