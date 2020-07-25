package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectRayRay {
    /**
     * Computes the intersection between two infinitely long rays 'a' and 'b'.
     * <p>
     * The intersection will contain the parameters 't' of ray 'a' in range
     * [-MAX_VALUE,MAX_VALUE].
     * <p>
     * The computed intersection will have one of the states
     * {@link IntersectionStatus#INTERSECTION},
     * {@link IntersectionStatus#NO_INTERSECTION_COINCIDENT},
     * {@link IntersectionStatus#NO_INTERSECTION_PARALLEL},
     *
     * @param a0 point 0 of ray 'a'
     * @param a1 point 1 of ray 'a'
     * @param b0 point 0 of ray 'a'
     * @param b1 point 1 of ray 'b'
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectRayRayEx(@NonNull java.awt.geom.Point2D a0,
                                                         @NonNull java.awt.geom.Point2D a1,
                                                         @NonNull java.awt.geom.Point2D b0,
                                                         @NonNull java.awt.geom.Point2D b1) {
        final double
                a0y = a0.getY(),
                b1x = b1.getX(),
                b0x = b0.getX(),
                b0y = b0.getY(),
                b1y = b1.getY(),
                a0x = a0.getX(),
                a1x = a1.getX(),
                a1y = a1.getY();
        return intersectRayRayEx(a0x, a0y, a1x, a1y, b0x, b0y, b1x, b1y);

    }

    /**
     * Computes the intersection between two infinitely long rays 'a' and 'b'.
     * <p>
     * The intersection will contain the parameters 't' of ray 'a' in range
     * [-MAX_VALUE,MAX_VALUE].
     * <p>
     * The computed intersection will have one of the states
     * {@link IntersectionStatus#INTERSECTION},
     * {@link IntersectionStatus#NO_INTERSECTION_COINCIDENT},
     * {@link IntersectionStatus#NO_INTERSECTION_PARALLEL},
     *
     * @param a0x point 0 of ray 'a'
     * @param a0y point 0 of ray 'a'
     * @param a1x point 1 of ray 'a'
     * @param a1y point 1 of ray 'a'
     * @param b0x point 0 of ray 'a'
     * @param b0y point 0 of ray 'a'
     * @param b1x point 1 of ray 'b'
     * @param b1y point 1 of ray 'b'
     * @return computed intersection
     */
    public static IntersectionResultEx intersectRayRayEx(double a0x, double a0y, double a1x, double a1y, double b0x, double b0y, double b1x, double b1y) {
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status;


        double ua_t = (b1x - b0x) * (a0y - b0y) - (b1y - b0y) * (a0x - b0x);
        double ub_t = (a1x - a0x) * (a0y - b0y) - (a1y - a0y) * (a0x - b0x);
        double u_b = (b1y - b0y) * (a1x - a0x) - (b1x - b0x) * (a1y - a0y);

        if (u_b != 0) {
            double ua = ua_t / u_b;
            status = IntersectionStatus.INTERSECTION;
            result.add(new IntersectionPointEx(
                    new Point2D.Double(
                            a0x + ua * (a1x - a0x),
                            a0y + ua * (a1y - a0y)),
                    ua));
        } else {
            if (ua_t == 0 || ub_t == 0) {
                status = IntersectionStatus.NO_INTERSECTION_COINCIDENT;
            } else {
                status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
            }
        }

        return new IntersectionResultEx(status, result);
    }
}
