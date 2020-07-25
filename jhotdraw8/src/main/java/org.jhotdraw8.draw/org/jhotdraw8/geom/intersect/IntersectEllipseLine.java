package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.AABB;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.jhotdraw8.geom.Geom.lerp;

public class IntersectEllipseLine {
    private IntersectEllipseLine() {
    }

    /**
     * Computes the intersection between an ellipse and a line.
     *
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @param a0 point 0 of the line
     * @param a1 point 1 of the line
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectEllipseLineEx(@NonNull Point2D ec, double rx, double ry, @NonNull Point2D a0, @NonNull Point2D a1) {
        IntersectionResultEx result = intersectLineEllipseEx(a0, a1, ec, rx, ry);
        // FIXME compute t for Ellipse instead for Line!
        return result;
    }

    @NonNull
    public static IntersectionResultEx intersectEllipseLineEx(double cx, double cy, double rx, double ry,
                                                              double x0, double y0, double x1, double y1) {
        IntersectionResultEx result = intersectLineEllipseEx(x0, y0, x1, y1, cx, cy, rx, ry);
        // FIXME compute t for Ellipse instead for Line!
        return result;
    }

    /**
     * Computes the intersection between a line and an ellipse.
     * <p>
     * The intersection will contain the parameters 't1' of the line in range
     * [0,1].
     *
     * @param e  the bounds of the ellipse
     * @param a0 point 0 of the line
     * @param a1 point 1 of the line
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectLineEllipseEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull AABB e) {
        double rx = e.getWidth() * 0.5;
        double ry = e.getHeight() * 0.5;
        return intersectLineEllipseEx(a0, a1, new Point2D.Double(e.getMinX() + rx, e.getMinY() + ry), rx, ry);
    }

    /**
     * Computes the intersection between a line and an ellipse.
     * <p>
     * The intersection will contain the parameters 't1' of the line in range
     * [0,1].
     *
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @param a0 point 0 of the line
     * @param a1 point 1 of the line
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectLineEllipseEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D ec, double rx, double ry) {
        return intersectLineEllipseEx(a0.getX(), a0.getY(), a1.getX(), a1.getY(), ec.getX(), ec.getY(), rx, ry);
    }

    @NonNull
    public static IntersectionResultEx intersectLineEllipseEx(double x0, double y0, double x1, double y1,
                                                              double cx, double cy, double rx, double ry) {
        List<IntersectionPointEx> result = new ArrayList<>();

        final Point2D.Double origin, dir, center, diff, mDir, mDiff;
        origin = new Point2D.Double(x0, y0);
        dir = Points2D.subtract(x1, y1, x0, y0);
        center = new Point2D.Double(cx, cy);
        diff = Points2D.subtract(origin, center);
        mDir = new Point2D.Double(dir.getX() / (rx * rx), dir.getY() / (ry * ry));
        mDiff = new Point2D.Double(diff.getX() / (rx * rx), diff.getY() / (ry * ry));

        final double a, b, c, d;
        a = Points2D.dotProduct(dir, mDir);
        b = Points2D.dotProduct(dir, mDiff);
        c = Points2D.dotProduct(diff, mDiff) - 1.0;
        d = b * b - a * c;

        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        if (d < -Intersections.EPSILON) {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else if (d > 0) {
            final double root, t0, t1;
            root = Math.sqrt(d);
            t0 = (-b - root) / a;
            t1 = (-b + root) / a;

            if ((t0 < 0 || 1 < t0) && (t1 < 0 || 1 < t1)) {
                if ((t0 < 0 && t1 < 0) || (t0 > 1 && t1 > 1)) {
                    status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
                } else {
                    status = IntersectionStatus.NO_INTERSECTION_INSIDE;
                }
            } else {
                status = IntersectionStatus.INTERSECTION;
                if (0 <= t0 && t0 <= 1) {
                    result.add(new IntersectionPointEx(lerp(x0, y0, x1, y1, t0), t0));
                }
                if (0 <= t1 && t1 <= 1) {
                    result.add(new IntersectionPointEx(lerp(x0, y0, x1, y1, t1), t1));
                }
            }
        } else {
            final double t = -b / a;
            if (0 <= t && t <= 1) {
                status = IntersectionStatus.INTERSECTION;
                result.add(new IntersectionPointEx(lerp(x0, y0, x1, y1, t), t));
            } else {
                status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
            }
        }

        return new IntersectionResultEx(status, result);
    }
}
