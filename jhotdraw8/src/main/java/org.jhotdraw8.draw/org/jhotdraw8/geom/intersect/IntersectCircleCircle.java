package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.Geom;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.jhotdraw8.geom.Geom.lerp;

public class IntersectCircleCircle {
    private IntersectCircleCircle() {
    }

    /**
     * Computes the intersection between circle 1 and circle 2.
     *
     * @param c1 the center of circle 1
     * @param r1 the radius of circle 1
     * @param c2 the center of circle 2
     * @param r2 the radius of circle 2
     * @return computed intersection with parameters of circle 1 at the intersection point
     */
    @NonNull
    public static IntersectionResultEx intersectCircleCircleEx(@NonNull Point2D c1, double r1, @NonNull Point2D c2, double r2) {
        return intersectCircleCircleEx(c1.getX(), c1.getY(), r1, c2.getX(), c2.getY(), r2, Intersections.EPSILON);
    }

    @NonNull
    public static IntersectionResult intersectCircleCircle(@NonNull Point2D c1, double r1, @NonNull Point2D c2, double r2, double epsilon) {
        return intersectCircleCircle(c1.getX(), c1.getY(), r1, c2.getX(), c2.getY(), r2, epsilon);
    }

    @NonNull
    public static IntersectionResultEx intersectCircleCircleEx(double c1x, double c1y, double r1, double c2x, double c2y, double r2) {
        return intersectCircleCircleEx(c1x, c1y, r1, c2x, c2y, r2, Intersections.EPSILON);
    }

    /**
     * Computes the intersection between the given circles 1 and 2.
     *
     * @param c1x the center of circle 1
     * @param c1y the center of circle 1
     * @param r1  the radius of circle 1
     * @param c2x the center of circle 2
     * @param c2y the center of circle 2
     * @param r2  the radius of circle 2
     * @return computed intersection with parameters of circle 1 at the intersection point
     */
    @NonNull
    public static IntersectionResultEx intersectCircleCircleEx(double c1x, double c1y, double r1, double c2x, double c2y, double r2, double epsilon) {
        List<IntersectionPointEx> result = new ArrayList<>();

        // Determine minimum and maximum radii where circles can intersect
        double r_max = r1 + r2;
        double r_min = Math.abs(r1 - r2);

        // Determine actual distance between the two circles
        double c_dist = Geom.distance(c1x, c1y, c2x, c2y);

        IntersectionStatus status;

        if (c_dist > r_max) {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else if (c_dist < r_min) {
            status = r1 < r2 ? IntersectionStatus.NO_INTERSECTION_INSIDE : IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else if (Geom.almostZero(c_dist, epsilon) && Geom.almostEqual(r1, r2, epsilon)) {
            status = IntersectionStatus.NO_INTERSECTION_COINCIDENT;
        } else {
            status = IntersectionStatus.INTERSECTION;

            double a = (r1 * r1 - r2 * r2 + c_dist * c_dist) / (2 * c_dist);
            double h = Math.sqrt(r1 * r1 - a * a);
            Point2D.Double p = lerp(c1x, c1y, c2x, c2y, a / c_dist);
            double b = h / c_dist;

            double dy = c2y - c1y;
            double dx = c2x - c1x;
            double p1x = p.getX() - b * dy;
            double p1y = p.getY() + b * dx;
            result.add(new IntersectionPointEx(new Point2D.Double(p1x, p1y),
                    Geom.atan2(p1y - c1y, p1x - c1x), Geom.perp(p1x - c1x, p1y - c1y),
                    Geom.atan2(p1y - c2y, p1x - c2x), Geom.perp(p1x - c2x, p1y - c2y)
            ));
            double p2x = p.getX() + b * dy;
            double p2y = p.getY() - b * dx;

            if (!Geom.almostEqual(c_dist, r_max, epsilon)) {
                result.add(new IntersectionPointEx(new Point2D.Double(p2x, p2y),
                        Geom.atan2(p2y - c1y, p2x - c1x), Geom.perp(p2x - c1x, p2y - c1y),
                        Geom.atan2(p2y - c2y, p2x - c2x), Geom.perp(p2x - c2x, p2y - c2y)

                ));
            }
        }
        return new IntersectionResultEx(status, result);
    }

    /**
     * Computes the intersection between the given circles 1 and 2.
     * <p>
     * This method can produce the following {@link IntersectionStatus} codes:
     * <dl>
     *     <dt>{@link IntersectionStatus#INTERSECTION}</dt><dd>
     *         The circles intersect at the {@link IntersectionPoint}s given
     *         in the result.
     *     </dd>
     *     <dt>{@link IntersectionStatus#NO_INTERSECTION_INSIDE}</dt><dd>
     *         Circle 1 is inside circle 2.
     *     </dd>
     *     <dt>{@link IntersectionStatus#NO_INTERSECTION_OUTSIDE}</dt><dd>
     *         Circle 1 is outside circle 2.
     *     </dd>
     *     <dt>{@link IntersectionStatus#NO_INTERSECTION_COINCIDENT}</dt><dd>
     *         Circle 1 is same as circle 2 up to the given epsilon.
     *     </dd>
     * </dl>
     * The code of this method has been derived from intersection.js by
     * Kevin Lindsey, copyright 2002 Kevin Lindsey, BSD 3-clause license.
     * http://www.kevlindev.com/gui/math/intersection/Intersection.js.
     *
     * @param c1x the center of circle 1
     * @param c1y the center of circle 1
     * @param r1  the radius of circle 1
     * @param c2x the center of circle 2
     * @param c2y the center of circle 2
     * @param r2  the radius of circle 2
     * @return computed intersection with parameters of circle 1 at the intersection point
     */
    @NonNull
    public static IntersectionResult intersectCircleCircle(double c1x, double c1y, double r1, double c2x, double c2y, double r2, double epsilon) {
        List<IntersectionPoint> result = new ArrayList<>();

        // Determine minimum and maximum radii where circles can intersect
        double r_max = r1 + r2;
        double r_min = Math.abs(r1 - r2);

        // Determine actual distance between the two circles
        double c_distSq = Geom.distanceSq(c1x, c1y, c2x, c2y);
        double c_dist = Math.sqrt(c_distSq);

        IntersectionStatus status;

        if (c_dist > r_max) {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else if (c_dist < r_min) {
            status = r1 < r2 ? IntersectionStatus.NO_INTERSECTION_INSIDE : IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else if (Geom.almostZero(c_dist, epsilon) && Geom.almostEqual(r1, r2, epsilon)) {
            status = IntersectionStatus.NO_INTERSECTION_COINCIDENT;
        } else {
            status = IntersectionStatus.INTERSECTION;

            double a = (r1 * r1 - r2 * r2 + c_distSq) / (2 * c_dist);
            double h = Math.sqrt(r1 * r1 - a * a);
            Point2D.Double p = lerp(c1x, c1y, c2x, c2y, a / c_dist);
            double b = h / c_dist;

            double dy = c2y - c1y;
            double dx = c2x - c1x;
            double p1x = p.getX() - b * dy;
            double p1y = p.getY() + b * dx;
            result.add(new IntersectionPoint(p1x, p1y,
                    Geom.atan2(p1y - c1y, p1x - c1x)
            ));
            double p2x = p.getX() + b * dy;
            double p2y = p.getY() - b * dx;

            if (!Geom.almostEqual(c_dist, r_max, epsilon)) {
                result.add(new IntersectionPoint(p2x, p2y,
                        Geom.atan2(p2y - c1y, p2x - c1x)

                ));
            }
        }
        return new IntersectionResult(status, result);
    }
}
