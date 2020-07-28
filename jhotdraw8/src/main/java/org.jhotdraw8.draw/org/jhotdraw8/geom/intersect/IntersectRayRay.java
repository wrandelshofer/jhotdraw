package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.Geom;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.jhotdraw8.geom.Geom.REAL_THRESHOLD;

public class IntersectRayRay {

    /**
     * Computes the intersection between two infinitely long rays 'a' and 'b'.
     * <p>
     * The intersection will contain the parameters 't' of ray 'a' in range
     * [0,MAX_VALUE].
     * <p>
     * The computed intersection will have one of the states
     * {@link IntersectionStatus#INTERSECTION},
     * {@link IntersectionStatus#NO_INTERSECTION_COINCIDENT},
     * {@link IntersectionStatus#NO_INTERSECTION_PARALLEL},
     *
     * @param ao origin of ray 'a'
     * @param ad direction of ray 'a'
     * @param bo origin of ray 'b'
     * @param bd directoin of ray 'b'
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectRayRayEx(@NonNull Point2D ao,
                                                         @NonNull Point2D ad,
                                                         @NonNull Point2D bo,
                                                         @NonNull Point2D bd) {
        return intersectRayRayEx(ao.getX(), ao.getY(), ad.getX(), ad.getY(), bo.getX(), bo.getY(), bd.getX(), bd.getY());

    }

    /**
     * Computes the intersection between two infinitely long rays 'a' and 'b'.
     * <p>
     * The intersection will contain the parameters 't' of ray 'a' and ray
     * 'b' in range [0,MAX_VALUE].
     *
     * @param aox x origin of ray 'a'
     * @param aoy y origin of ray 'a'
     * @param adx x direction of ray 'a'
     * @param ady y direction of ray 'a'
     * @param box x origin of ray 'b'
     * @param boy y origin of ray 'b'
     * @param bdx x direction of ray 'b'
     * @param bdy y direction of ray 'b'
     * @return computed intersection
     */
    public static IntersectionResultEx intersectRayRayEx(
            double aox, double aoy, double adx, double ady,
            double box, double boy, double bdx, double bdy) {
        return intersectRayRayEx(aox, aoy, adx, ady, Double.MAX_VALUE,
                box, boy, bdx, bdy, Double.MAX_VALUE, REAL_THRESHOLD);
    }

    /**
     * Computes the intersection between two infinitely long rays 'a' and 'b'.
     * <p>
     * The intersection will contain the parameters 't' of ray 'a' in range
     * [0,maxT].
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
     * </dl>     *
     *
     * @param aox  x origin of ray 'a'
     * @param aoy  y origin of ray 'a'
     * @param adx  x direction of ray 'a'
     * @param ady  y direction of ray 'a'
     * @param amax maximal parameter value of ray 'a'
     * @param box  x origin of ray 'b'
     * @param boy  y origin of ray 'b'
     * @param bdx  x direction of ray 'b'
     * @param bdy  y direction of ray 'b'
     * @param bmax maximal parameter value of ray 'b'
     * @return computed intersection
     */
    public static IntersectionResultEx intersectRayRayEx(
            double aox, double aoy, double adx, double ady, double amax,
            double box, double boy, double bdx, double bdy, double bmax,
            double epsilon) {

        Point2D.Double tangentA = new Point2D.Double(adx, ady);
        Point2D.Double tangentB = new Point2D.Double(bdx, bdy);
        List<IntersectionPointEx> result = new ArrayList<>();
        IntersectionStatus status;

        double doy = aoy - boy;
        double dox = aox - box;
        double ua_t = bdx * doy - bdy * dox;
        double ub_t = adx * doy - ady * dox;
        double u_b = bdy * adx - bdx * ady;

        if (!Geom.almostZero(u_b, epsilon)) {
            double ua = ua_t / u_b;
            double ub = ub_t / u_b;

            // using threshold check here to make intersect "sticky" to prefer
            // considering it an intersection.
            if (-epsilon <= ua && ua <= amax && -epsilon <= ub && ub <= bmax) {
                status = IntersectionStatus.INTERSECTION;
                result.add(new IntersectionPointEx(
                        new Point2D.Double(aox + ua * adx, aoy + ua * ady),
                        ua, tangentA, ub, tangentB
                ));
            } else {
                status = IntersectionStatus.NO_INTERSECTION;
                result.add(new IntersectionPointEx(
                        new Point2D.Double(aox + ua * adx, aoy + ua * ady),
                        ua, tangentA, ub, tangentB
                ));
            }
        } else {
            if (Geom.almostZero(ua_t) || Geom.almostZero(ub_t)) {
                // either collinear or degenerate (segments are single points)
                boolean aIsPoint = Geom.almostZero(amax * adx, epsilon) && Geom.almostZero(amax * ady, epsilon);
                boolean bIsPoint = Geom.almostZero(bmax * bdx, epsilon) && Geom.almostZero(bmax * bdy, epsilon);
                if (aIsPoint && bIsPoint) {
                    // both segments are just points
                    if (Geom.almostEqual(aox, box) && Geom.almostEqual(aoy, boy)) {
                        // same point
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(aox, aoy),
                                0, tangentA, 0, tangentB
                        ));
                    } else {
                        // distinct points
                        status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
                    }

                } else if (aIsPoint) {
                    Double argB = IntersectPointRay.argumentOnRay(box, boy, bdx, bdy, bmax, aox, aoy, epsilon);
                    if (argB != null) {
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(aox, aoy),
                                0, tangentA,
                                Geom.clamp(argB, 0, bmax), tangentB
                        ));
                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
                    }
                } else if (bIsPoint) {
                    Double argA = IntersectPointRay.argumentOnRay(aox, aoy, adx, ady, bmax, box, boy, epsilon);
                    if (argA != null) {
                        status = IntersectionStatus.INTERSECTION;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(box, boy),
                                Geom.clamp(argA, 0, amax), tangentA, 0, tangentB
                        ));
                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_PARALLEL;
                    }
                } else {
                    // neither segment is a point, check if they overlap
                    double at0, at1;
                    if (Geom.almostZero(adx)) {
                        at0 = (boy - aoy) / ady;
                        at1 = (bdy + boy - aoy) / ady;
                    } else {
                        at0 = (box - aox) / adx;
                        at1 = (bdx + box - aox) / adx;
                    }

                    if (at0 > at1) {
                        double swap = at0;
                        at0 = at1;
                        at1 = swap;
                    }

                    if (at0 < 1 + epsilon && at1 > -epsilon) {
                        at0 = Geom.clamp(at0, 0.0, amax);
                        at1 = Geom.clamp(at1, 0.0, bmax);
                        double bt0, bt1;
                        if (Geom.almostZero(bdx)) {
                            bt0 = (aoy + at0 * ady - boy) / bdy;
                            bt1 = (aoy + at1 * ady - boy) / bdy;
                        } else {
                            bt0 = (aox + at0 * adx - box) / bdx;
                            bt1 = (aox + at1 * adx - box) / bdx;
                        }

                        status = IntersectionStatus.NO_INTERSECTION_COINCIDENT;
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(aox + at0 * adx, aoy + at0 * ady),
                                at0, tangentA, bt0, tangentB
                        ));
                        result.add(new IntersectionPointEx(
                                new Point2D.Double(aox + at1 * adx, aoy + at1 * ady),
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
