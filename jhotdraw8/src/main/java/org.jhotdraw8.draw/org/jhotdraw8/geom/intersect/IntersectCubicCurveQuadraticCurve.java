package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.DoubleArrayList;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectCubicCurveQuadraticCurve {
    private IntersectCubicCurveQuadraticCurve() {
    }

    @NonNull
    public static IntersectionResultEx intersectQuadraticCurveCubicCurveEx(double ax0, double ay0, double ax1, double ay1, double ax2, double ay2,
                                                                           double bx0, double by0, double bx1, double by1, double bx2, double by2, double bx3, double by3) {
        return intersectQuadraticCurveCubicCurveEx(new Point2D.Double(ax0, ay0), new Point2D.Double(ax1, ay1), new Point2D.Double(ax2, ay2),
                new Point2D.Double(bx0, by0), new Point2D.Double(bx1, by1), new Point2D.Double(bx2, by2), new Point2D.Double(bx3, by3));

    }

    /**
     * Computes the intersection between a quadratic bezier curve 'a' and cubic
     * bezier curve 'b'.
     * <p>
     * The intersection will contain the parameters 't1' of curve 'a' in range
     * [0,1].
     *
     * @param a0 control point P0 of 'a'
     * @param a1 control point P1 of 'a'
     * @param a2 control point P2 of 'a'
     * @param b0 control point P0 of 'b'
     * @param b1 control point P1 of 'b'
     * @param b2 control point P2 of 'b'
     * @param b3 control point P3 of 'b'
     * @return the computed result
     */
    @NonNull
    public static IntersectionResultEx intersectQuadraticCurveCubicCurveEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2,
                                                                           @NonNull Point2D b0, @NonNull Point2D b1, @NonNull Point2D b2, @NonNull Point2D b3) {
        final Point2D c12, c11, c10;
        final Point2D c23, c22, c21, c20;
        c12 = Points2D.add(a0, Points2D.add(Points2D.multiply(a1, -2), a2));
        c11 = Points2D.add(Points2D.multiply(a0, -2), Points2D.multiply(a1, 2));
        c10 = new Point2D.Double(a0.getX(), a0.getY());
        c23 = Points2D.sum(Points2D.multiply(b0, -1), Points2D.multiply(b1, 3), Points2D.multiply(b2, -3), b3);
        c22 = Points2D.sum(Points2D.multiply(b0, 3), Points2D.multiply(b1, -6), Points2D.multiply(b2, 3));
        c21 = Points2D.add(Points2D.multiply(b0, -3), Points2D.multiply(b1, 3));
        c20 = b0;

        final double c10x, c10y, c11x, c11y, c12x, c12y, c20x, c20y, c21x, c21y, c22x, c22y, c23x, c23y;
        c10x = c10.getX();
        c10y = c10.getY();
        c11x = c11.getX();
        c11y = c11.getY();
        c12x = c12.getX();
        c12y = c12.getY();
        c20x = c20.getX();
        c20y = c20.getY();
        c21x = c21.getX();
        c21y = c21.getY();
        c22x = c22.getX();
        c22y = c22.getY();
        c23x = c23.getX();
        c23y = c23.getY();

        final double c10x2, c10y2, c11x2, c11y2, c12x2, c12y2;
        final double c20x2, c20y2, c21x2, c21y2, c22x2, c22y2, c23x2, c23y2;
        c10x2 = c10x * c10x;
        c10y2 = c10y * c10y;
        c11x2 = c11x * c11x;
        c11y2 = c11y * c11y;
        c12x2 = c12x * c12x;
        c12y2 = c12y * c12y;
        c20x2 = c20x * c20x;
        c20y2 = c20y * c20y;
        c21x2 = c21x * c21x;
        c21y2 = c21y * c21y;
        c22x2 = c22x * c22x;
        c22y2 = c22y * c22y;
        c23x2 = c23x * c23x;
        c23y2 = c23y * c23y;

        Polynomial poly = new Polynomial(
                -2 * c12x * c12y * c23x * c23y + c12x2 * c23y2 + c12y2 * c23x2,
                -2 * c12x * c12y * c22x * c23y - 2 * c12x * c12y * c22y * c23x + 2 * c12y2 * c22x * c23x
                        + 2 * c12x2 * c22y * c23y,
                -2 * c12x * c21x * c12y * c23y - 2 * c12x * c12y * c21y * c23x - 2 * c12x * c12y * c22x * c22y
                        + 2 * c21x * c12y2 * c23x + c12y2 * c22x2 + c12x2 * (2 * c21y * c23y + c22y2),
                2 * c10x * c12x * c12y * c23y + 2 * c10y * c12x * c12y * c23x + c11x * c11y * c12x * c23y
                        + c11x * c11y * c12y * c23x - 2 * c20x * c12x * c12y * c23y - 2 * c12x * c20y * c12y * c23x
                        - 2 * c12x * c21x * c12y * c22y - 2 * c12x * c12y * c21y * c22x - 2 * c10x * c12y2 * c23x
                        - 2 * c10y * c12x2 * c23y + 2 * c20x * c12y2 * c23x + 2 * c21x * c12y2 * c22x
                        - c11y2 * c12x * c23x - c11x2 * c12y * c23y + c12x2 * (2 * c20y * c23y + 2 * c21y * c22y),
                2 * c10x * c12x * c12y * c22y + 2 * c10y * c12x * c12y * c22x + c11x * c11y * c12x * c22y
                        + c11x * c11y * c12y * c22x - 2 * c20x * c12x * c12y * c22y - 2 * c12x * c20y * c12y * c22x
                        - 2 * c12x * c21x * c12y * c21y - 2 * c10x * c12y2 * c22x - 2 * c10y * c12x2 * c22y
                        + 2 * c20x * c12y2 * c22x - c11y2 * c12x * c22x - c11x2 * c12y * c22y + c21x2 * c12y2
                        + c12x2 * (2 * c20y * c22y + c21y2),
                2 * c10x * c12x * c12y * c21y + 2 * c10y * c12x * c21x * c12y + c11x * c11y * c12x * c21y
                        + c11x * c11y * c21x * c12y - 2 * c20x * c12x * c12y * c21y - 2 * c12x * c20y * c21x * c12y
                        - 2 * c10x * c21x * c12y2 - 2 * c10y * c12x2 * c21y + 2 * c20x * c21x * c12y2
                        - c11y2 * c12x * c21x - c11x2 * c12y * c21y + 2 * c12x2 * c20y * c21y,
                -2 * c10x * c10y * c12x * c12y - c10x * c11x * c11y * c12y - c10y * c11x * c11y * c12x
                        + 2 * c10x * c12x * c20y * c12y + 2 * c10y * c20x * c12x * c12y + c11x * c20x * c11y * c12y
                        + c11x * c11y * c12x * c20y - 2 * c20x * c12x * c20y * c12y - 2 * c10x * c20x * c12y2
                        + c10x * c11y2 * c12x + c10y * c11x2 * c12y - 2 * c10y * c12x2 * c20y
                        - c20x * c11y2 * c12x - c11x2 * c20y * c12y + c10x2 * c12y2 + c10y2 * c12x2
                        + c20x2 * c12y2 + c12x2 * c20y2
        );
        final DoubleArrayList roots = poly.getRootsInInterval(0, 1);

        List<IntersectionPointEx> result = new ArrayList<>();
        for (int i = 0; i < roots.size(); i++) {
            double s = roots.get(i);
            double[] xRoots = new Polynomial(
                    c12x, c11x,
                    c10x - c20x - s * c21x - s * s * c22x - s * s * s * c23x
            ).getRoots();
            double[] yRoots = new Polynomial(
                    c12y, c11y,
                    c10y - c20y - s * c21y - s * s * c22y - s * s * s * c23y
            ).getRoots();

            if (xRoots.length > 0 && yRoots.length > 0) {
                double TOLERANCE = 1e-4;

                checkRoots:
                for (int j = 0; j < xRoots.length; j++) {
                    double xRoot = xRoots[j];

                    if (0 <= xRoot && xRoot <= 1) {
                        for (int k = 0; k < yRoots.length; k++) {
                            if (Math.abs(xRoot - yRoots[k]) < TOLERANCE) {
                                result.add(
                                        new IntersectionPointEx(
                                                Points2D.sum(
                                                        Points2D.multiply(c23, s * s * s),
                                                        Points2D.multiply(c22, s * s),
                                                        Points2D.multiply(c21, s), c20), xRoot));
                                break checkRoots;
                            }
                        }
                    }
                }
            }
        }

        return new IntersectionResultEx(result);
    }

    @NonNull
    public static IntersectionResultEx intersectCubicCurveQuadraticCurveEx(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3,
            double bx0, double by0, double bx1, double by1, double bx2, double by2) {
        IntersectionResultEx isect = intersectQuadraticCurveCubicCurveEx(
                new Point2D.Double(bx0, by0), new Point2D.Double(bx1, by1), new Point2D.Double(bx2, by2),
                new Point2D.Double(ax0, ay0), new Point2D.Double(ax1, ay1), new Point2D.Double(ax2, ay2),
                new Point2D.Double(ax3, ay3));
        // FIXME compute t for a instead for b
        return isect;

    }
}
