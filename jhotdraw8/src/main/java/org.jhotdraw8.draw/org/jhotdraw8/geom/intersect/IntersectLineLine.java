package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;

import static org.jhotdraw8.geom.Geom.REAL_THRESHOLD;

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
     * @see IntersectLineLine#intersectLineLineEx(double, double, double, double, double, double, double, double, double)
     */
    @NonNull
    public static IntersectionResultEx intersectLineLineEx(
            double a0x, double a0y, double a1x, double a1y,
            double b0x, double b0y, double b1x, double b1y) {
        return intersectLineLineEx(a0x, a0y, a1x, a1y, b0x, b0y, b1x, b1y, REAL_THRESHOLD);
    }

    /**
     * Computes the intersection of line segment 'a' with line segment 'b'.
     *
     * @param a0 start of line segment 'a'
     * @param a1 end of line segment 'a'
     * @param b0 start of line segment 'b'
     * @param b1 end of line segment 'b'
     * @return computed intersection with parameters of line 'a' at the intersection point
     *
     * @see #intersectLineLineEx(double, double, double, double, double, double, double, double)
     */
    @NonNull
    public static IntersectionResultEx intersectLineLineEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D b0, @NonNull Point2D b1) {
        return intersectLineLineEx(a0.getX(), a0.getY(), a1.getX(), a1.getY(),
                b0.getX(), b0.getY(), b1.getX(), b1.getY());
    }

    @NonNull
    public static IntersectionResultEx intersectLineLineEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D b0, @NonNull Point2D b1, double epsilon) {
        return intersectLineLineEx(a0.getX(), a0.getY(), a1.getX(), a1.getY(),
                b0.getX(), b0.getY(), b1.getX(), b1.getY(), epsilon);
    }

    /**
     * Intersects a line segment 'a' with line segment 'b'.
     * <p>
     * This method can produce the following {@link IntersectionStatus} codes:
     * <dl>
     *     <dt>{@link IntersectionStatus#INTERSECTION}</dt><dd>
     *         The line segments intersect at the {@link IntersectionPointEx} given
     *         in the result.
     *     </dd>
     *     <dt>{@link IntersectionStatus#NO_INTERSECTION}</dt><dd>
     *         The line segments do not intersect, but lines of infinite length,
     *         will intersect at the {@link IntersectionPointEx} given
     *         in the result.
     *     </dd>
     *     <dt>{@link IntersectionStatus#NO_INTERSECTION_COINCIDENT}</dt><dd>
     *         The lines segments do not intersect because they are
     *         coincident. Coincidence starts and ends at the two
     *         {@link IntersectionPointEx}s given in the result.
     *     </dd>
     *     <dt>{@link IntersectionStatus#NO_INTERSECTION_PARALLEL}</dt><dd>
     *         The lines segments do not intersect because they are parallel.
     *     </dd>
     * </dl>
     *
     * @param a0x start x coordinate of line segment 'a'
     * @param a0y start y coordinate of line segment 'a'
     * @param a1x end x coordinate of line segment 'a'
     * @param a1y end y coordinate of line segment 'a'
     * @param b0x start x coordinate of line segment 'b'
     * @param b0y start y coordinate of line segment 'b'
     * @param b1x end x coordinate of line segment 'b'
     * @param b1y end y coordinate of line segment 'b'
     * @return computed intersection with parameters t of 'a' at the intersection point
     */
    @NonNull
    public static IntersectionResultEx intersectLineLineEx(
            double a0x, double a0y, double a1x, double a1y,
            double b0x, double b0y, double b1x, double b1y, double epsilon) {

        return IntersectRayRay.intersectRayRayEx(
                a0x, a0y, a1x - a0x, a1y - a0y, 1,
                b0x, b0y, b1x - b0x, b1y - b0y, 1, epsilon
        );
    }

    public static IntersectionResult intersectLineLine(
            double a0x, double a0y, double a1x, double a1y,
            double b0x, double b0y, double b1x, double b1y) {
        IntersectionResultEx resultEx = intersectLineLineEx(a0x, a0y, a1x, a1y, b0x, b0y, b1x, b1y, REAL_THRESHOLD);
        return new IntersectionResult(resultEx.getStatus(),
                resultEx);
    }
}
