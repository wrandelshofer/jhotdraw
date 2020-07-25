package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.Geom;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.jhotdraw8.geom.Geom.argumentOnLine;
import static org.jhotdraw8.geom.Geom.lineContainsPoint;

public class IntersectLineRay {
    private IntersectLineRay() {
    }

    /**
     * Intersects a line segment or ray 'a' with line segment 'b'.
     *
     * @see #intersectRayLineEx(double, double, double, double, double, double, double, double, double, double)
     */
    @NonNull
    public static IntersectionResultEx intersectRayLineEx(
            double a0x, double a0y, double a1x, double a1y,
            double b0x, double b0y, double b1x, double b1y, double maxT) {
        return intersectRayLineEx(
                a0x, a0y,
                a1x, a1y,
                b0x, b0y,
                b1x, b1y, 1.0, Intersections.EPSILON);
    }

    /**
     * Intersects a line segment or ray 'a' with line segment 'b'.
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
     * @param a0x  start x coordinate of line segment 'a' or of ray 'a'
     * @param a0y  start y coordinate of line segment 'a' or of ray 'a'
     * @param a1x  end x coordinate of line segment or direction 'a' or of ray 'a'
     * @param a1y  end y coordinate of line segment or direction 'a' or of ray 'a'
     * @param b0x  start x coordinate of line segment 'b'
     * @param b0y  start y coordinate of line segment 'b'
     * @param b1x  end x coordinate of line segment 'b'
     * @param b1y  end y coordinate of line segment 'b'
     * @param maxT maximal permitted value for the parameter t of 'a', if this
     *             value is {@link Double#MAX_VALUE} then 'a' is a ray
     *             starting at {@code a0x,a0y} with direction {@code a1x-a0x,a1y-a0y},
     *             <br>if this value is {@code 1.0} then 'a' is a line segment.
     * @return computed intersection with parameters t of ray 'a' at the intersection point
     */
    @NonNull
    public static IntersectionResultEx intersectRayLineEx(
            double a0x, double a0y, double a1x, double a1y,
            double b0x, double b0y, double b1x, double b1y, double maxT, double epsilon) {

        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status;

        double adx = a1x - a0x;
        double ady = a1y - a0y;
        double bdx = b1x - b0x;
        double bdy = b1y - b0y;
        Point2D.Double tangentA = new Point2D.Double(adx, ady);
        Point2D.Double tangentB = new Point2D.Double(bdx, bdy);

        double b0a0dy = a0y - b0y;
        double b0a0dx = a0x - b0x;
        double ua_t = bdx * b0a0dy - bdy * b0a0dx;
        double ub_t = adx * b0a0dy - ady * b0a0dx;
        double u_b = bdy * adx - bdx * ady;

        if (!Geom.almostZero(u_b)) {
            double ua = ua_t / u_b;
            double ub = ub_t / u_b;

            // using threshold check here to make intersect "sticky" to prefer
            // considering it an intersection.
            if (-epsilon < ua && ua < maxT + epsilon && -epsilon < ub && ub < 1 + epsilon) {
                status = IntersectionStatus.INTERSECTION;
                result.add(new IntersectionPointEx(
                        new Point2D.Double(a0x + ua * adx, a0y + ua * ady),
                        ua, tangentA, ub, tangentB
                ));
            } else {
                status = IntersectionStatus.NO_INTERSECTION;
                result.add(new IntersectionPointEx(
                        new Point2D.Double(a0x + ua * adx, a0y + ua * ady),
                        ua, tangentA, ub, tangentB
                ));
            }
        } else {
            if (Geom.almostZero(ua_t) || Geom.almostZero(ub_t)) {
                // either collinear or degenerate (segments are single points)
                boolean aIsPoint = Geom.almostZero(adx) && Geom.almostZero(ady);
                boolean bIsPoint = Geom.almostZero(bdx) && Geom.almostZero(bdy);
                if (aIsPoint && bIsPoint) {
                    // both segments are just points
                    if (Geom.almostEqual(a0x, b0x) && Geom.almostEqual(a0y, b0y)) {
                        // same point
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(a0x, a0y),
                                0, tangentA, 0, tangentB
                        ));
                    } else {
                        // distinct points
                        status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
                    }

                } else if (aIsPoint) {
                    if (lineContainsPoint(b0x, b0y, b1x, b1y, a0x, a0y)) {
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(a0x, a0y),
                                0, tangentA, argumentOnLine(b0x, b0y, b1x, b1y, a0x, a0y), tangentB
                        ));
                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
                    }

                } else if (bIsPoint) {
                    if (lineContainsPoint(a0x, a0y, a1x, a1y, b0x, b0y)) {
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(b0x, b0y),
                                argumentOnLine(a0x, a0y, a1x, a1y, b0x, b0y), tangentA, 0, tangentB
                        ));
                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
                    }
                } else {
                    // neither segment is a point, check if they overlap

                    double at0, at1;
                    if (Geom.almostZero(adx)) {
                        at0 = (b0y - a0y) / ady;
                        at1 = (b1y - a0y) / ady;
                    } else {
                        at0 = (b0x - a0x) / adx;
                        at1 = (b1x - a0x) / adx;
                    }

                    if (at0 > at1) {
                        double swap = at0;
                        at0 = at1;
                        at1 = swap;
                    }

                    if (at0 < maxT + epsilon && at1 > -epsilon) {
                        at0 = Geom.clamp(at0, 0.0, maxT);
                        at1 = Geom.clamp(at1, 0.0, maxT);
                        double bt0, bt1;
                        if (Geom.almostZero(bdx)) {
                            bt0 = (a0y + at0 * ady - b0y) / bdy;
                            bt1 = (a0y + at1 * ady - b0y) / bdy;
                        } else {
                            bt0 = (a0x + at0 * adx - b0x) / bdx;
                            bt1 = (a0x + at1 * adx - b0x) / bdx;
                        }

                        status = IntersectionStatus.NO_INTERSECTION_COINCIDENT;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(a0x + at0 * adx, a0y + at0 * ady),
                                at0, tangentA, bt0, tangentB
                        ));
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(a0x + at1 * adx, a0y + at1 * ady),
                                at1, tangentA, bt1, tangentB
                        ));

                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
                    }
                }
            } else {
                status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
            }
        }

        return new IntersectionResultEx(status, result);
    }
}
