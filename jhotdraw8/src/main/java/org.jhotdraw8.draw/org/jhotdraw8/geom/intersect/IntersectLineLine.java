package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;

public class IntersectLineLine {
    private IntersectLineLine() {
    }

    /**
     * Intersects line segment 'a' with line segment 'b'.
     *
     * @param a0x start x coordinate of line 'a'
     * @param a0y start y coordinate of line 'a'
     * @param a1x end x coordinate of line 'a'
     * @param a1y end y coordinate of line 'a'
     * @param b0x start x coordinate of line 'b'
     * @param b0y start y coordinate of line 'b'
     * @param b1x end x coordinate of line 'b'
     * @param b1y end y coordinate of line 'b'
     * @return computed intersection with parameters of line 'a' at the intersection point
     * @see #intersectRayLineEx(double, double, double, double, double, double, double, double, double)
     */
    @NonNull
    public static IntersectionResultEx intersectLineLineEx(
            double a0x, double a0y, double a1x, double a1y,
            double b0x, double b0y, double b1x, double b1y) {
        return IntersectLineRay.intersectRayLineEx(a0x, a0y, a1x, a1y, b0x, b0y, b1x, b1y, 1.0);
    }

    /**
     * Computes the intersection of line segment 'a' with line segment 'b'.
     *
     * @param a0 start of line segment 'a'
     * @param a1 end of line segment 'a'
     * @param b0 start of line segment 'b'
     * @param b1 end of line segment 'b'
     * @return computed intersection with parameters of line 'a' at the intersection point
     * @see #intersectRayLineEx(double, double, double, double, double, double, double, double, double, double)
     * @see #intersectLineLineEx(double, double, double, double, double, double, double, double)
     */
    @NonNull
    public static IntersectionResultEx intersectLineLineEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D b0, @NonNull Point2D b1) {
        return intersectLineLineEx(a0.getX(), a0.getY(), a1.getX(), a1.getY(),
                b0.getX(), b0.getY(), b1.getX(), b1.getY());
    }

    public static IntersectionResultEx intersectLineLineEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D b0, @NonNull Point2D b1, double epsilon) {
        return IntersectLineRay.intersectRayLineEx(
                a0.getX(), a0.getY(),
                a1.getX(), a1.getY(),
                b0.getX(), b0.getY(),
                b1.getX(), b1.getY(), 1.0, epsilon);
    }
}
