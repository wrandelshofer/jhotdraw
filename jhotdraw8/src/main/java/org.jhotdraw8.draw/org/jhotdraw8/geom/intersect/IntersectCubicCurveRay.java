package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.BezierCurves;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.jhotdraw8.geom.Geom.lerp;
import static org.jhotdraw8.geom.intersect.IntersectLinePoint.argumentOnLine;

public class IntersectCubicCurveRay {
    private IntersectCubicCurveRay() {
    }

    @NonNull
    public static IntersectionResult intersectCubicCurveRay(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
            double box, double boy, double bdx, double bdy,
            double maxT,
            double epsilon) {
        return intersectCubicCurveRay(new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y), new Point2D.Double(a2x, a2y), new Point2D.Double(a3x, a3y),
                new Point2D.Double(box, boy), new Point2D.Double(bdx, bdy), maxT, epsilon);
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the line
     * 'a'.
     *
     * @param a0 control point P0 of 'p'
     * @param a1 control point P1 of 'p'
     * @param a2 control point P2 of 'p'
     * @param a3 control point P3 of 'p'
     * @param bo point 0 of ray 'b'
     * @param bd direction of ray 'b'
     * @return the computed intersection
     */
    @NonNull
    public static IntersectionResult intersectCubicCurveRay(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D a2, @NonNull Point2D a3, @NonNull Point2D bo, @NonNull Point2D bd) {
        return intersectCubicCurveRay(a0, a1, a2, a3, bo, bd, Double.MAX_VALUE, Geom.REAL_THRESHOLD);
    }

    /**
     * The code of this method has been derived from intersection.js by
     * Kevin Lindsey, copyright 2002 Kevin Lindsey, BSD 3-clause license.
     * http://www.kevlindev.com/gui/math/intersection/Intersection.js.
     *
     * @param p0
     * @param p1
     * @param p2
     * @param p3
     * @param ao
     * @param ad
     * @param maxT
     * @param epsilon
     * @return
     */

    public static IntersectionResult intersectCubicCurveRay(@NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3,
                                                            @NonNull Point2D ao, @NonNull Point2D ad, double maxT, double epsilon) {
        final Point2D.Double topLeft = Intersections.topLeft(ao, Points2D.add(ao, ad)); // used to determine if point is on line segment
        final Point2D.Double bottomRight = Intersections.bottomRight(ao, Points2D.add(ao, ad)); // used to determine if point is on line segment
        List<IntersectionPoint> result = new ArrayList<>();

        // Start with Bezier using Bernstein polynomials for weighting functions:
        //     (1-t^3)P0 + 3t(1-t)^2P1 + 3t^2(1-t)P2 + t^3P3
        //
        // Expand and collect terms to form linear combinations of original Bezier
        // controls.  This ends up with a vector cubic in t:
        //     (-P0+3P1-3P2+P3)t^3 + (3P0-6P1+3P2)t^2 + (-3P0+3P1)t + P0
        //             /\                  /\                /\       /\
        //             ||                  ||                ||       ||
        //             c3                  c2                c1       c0
        // Calculate the coefficients
        final Point2D c3, c2, c1, c0;   // coefficients of cubic
        c3 = Points2D.sum(Points2D.multiply(p0, -1), Points2D.multiply(p1, 3), Points2D.multiply(p2, -3), p3);
        c2 = Points2D.sum(Points2D.multiply(p0, 3), Points2D.multiply(p1, -6), Points2D.multiply(p2, 3));
        c1 = Points2D.add(Points2D.multiply(p0, -3), Points2D.multiply(p1, 3));
        c0 = p0;

        final double aox, aoy, a1x, a1y;
        aoy = ao.getY();
        a1y = aoy + ad.getY();
        aox = ao.getX();
        a1x = aox + ad.getX();

        // Convert line to normal form: ax + by + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D.Double n;                // normal for normal form of line
        n = new Point2D.Double(ad.getY(), -ad.getX());

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = aox * a1y - a1x * aoy;

        // ?Rotate each cubic coefficient using line for new coordinate system?
        // Find roots of rotated cubic
        double[] roots = new Polynomial(
                Points2D.dotProduct(n, c3),
                Points2D.dotProduct(n, c2),
                Points2D.dotProduct(n, c1),
                Points2D.dotProduct(n, c0) + cl
        ).getRoots();

        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        for (int i = 0; i < roots.length; i++) {
            final double t = roots[i];

            if (-epsilon <= t && t <= 1 + epsilon) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D.Double p5, p6, p7, p8, p9, p10;
                p5 = lerp(p0, p1, t);
                p6 = lerp(p1, p2, t);
                p7 = lerp(p2, p3, t);
                p8 = lerp(p5, p6, t);
                p9 = lerp(p6, p7, t);
                p10 = lerp(p8, p9, t);

                double rayT = IntersectPointRay.projectedPointOnRay(aox, aoy, ad.getX(), ad.getY(), p10.getX(), p10.getY());

                if (-epsilon <= rayT && rayT <= maxT) {
                    status = IntersectionStatus.INTERSECTION;
                    result.add(new IntersectionPoint(p10, t));
                }
            }
        }

        return new IntersectionResult(status, result);
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the line
     * 'a'.
     *
     * @param p0x control point P0 of 'p'
     * @param p1x control point P1 of 'p'
     * @param p2x control point P2 of 'p'
     * @param p3x control point P3 of 'p'
     * @param a0x point 1 of 'a'
     * @param a1x point 2 of 'a'
     * @param p0y control point P0 of 'p'
     * @param p1y control point P1 of 'p'
     * @param p2y control point P2 of 'p'
     * @param p3y control point P3 of 'p'
     * @param a0y point 1 of 'a'
     * @param a1y point 2 of 'a'
     * @return the computed intersection
     */
    @NonNull
    public static IntersectionResult intersectRayCubicCurve(
            double a0x, double a0y, double a1x, double a1y, double maxT,
            double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double p3x, double p3y,
            double epsilon) {

        Point2D.Double a0 = new Point2D.Double(a0x, a0y);
        Point2D.Double a1 = new Point2D.Double(a1x, a1y);
        Point2D.Double p0 = new Point2D.Double(p0x, p0y);
        Point2D.Double p1 = new Point2D.Double(p1x, p1y);
        Point2D.Double p2 = new Point2D.Double(p2x, p2y);
        Point2D.Double p3 = new Point2D.Double(p3x, p3y);
        return intersectRayCubicCurve(a0, a1, maxT, p0, p1, p2, p3, Geom.REAL_THRESHOLD);
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the line
     * 'a'.
     *
     * @param ao      origin of ray 'a'
     * @param ad      direction of ray 'a'
     * @param p0      control point P0 of 'p'
     * @param p1      control point P1 of 'p'
     * @param p2      control point P2 of 'p'
     * @param p3      control point P3 of 'p'
     * @param epsilon
     * @return the computed intersection
     */
    @NonNull
    public static IntersectionResult intersectRayCubicCurve(@NonNull Point2D ao, @NonNull Point2D ad, double maxT, @NonNull Point2D p0, @NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3, double epsilon) {
        final double a0x, a0y, a1x, a1y;
        a0x = ao.getX();
        a0y = ao.getY();
        a1x = a0x + ad.getX();
        a1y = a0y + ad.getY();

        List<IntersectionPoint> result = new ArrayList<>();

        // Start with Bezier using Bernstein polynomials for weighting functions:
        //     (1-t^3)P0 + 3t(1-t)^2P1 + 3t^2(1-t)P2 + t^3P3
        //
        // Expand and collect terms to form linear combinations of original Bezier
        // controls.  This ends up with a vector cubic in t:
        //     (-P0+3P1-3P2+P3)t^3 + (3P0-6P1+3P2)t^2 + (-3P0+3P1)t + P0
        //             /\                  /\                /\       /\
        //             ||                  ||                ||       ||
        //             c3                  c2                c1       c0
        // Calculate the coefficients
        final Point2D c3, c2, c1, c0;   // coefficients of cubic
        c3 = Points2D.sum(Points2D.multiply(p0, -1), Points2D.multiply(p1, 3), Points2D.multiply(p2, -3), p3);
        c2 = Points2D.sum(Points2D.multiply(p0, 3), Points2D.multiply(p1, -6), Points2D.multiply(p2, 3));
        c1 = Points2D.add(Points2D.multiply(p0, -3), Points2D.multiply(p1, 3));
        c0 = p0;

        // Convert line to normal form: ax + by + c = 0
        // Find normal to line: negative inverse of original line's slope
        final Point2D.Double n;                // normal for normal form of line
        n = new Point2D.Double(a0y - a1y, a1x - a0x);

        // Determine new c coefficient
        final double cl;               // c coefficient for normal form of line
        cl = a0x * a1y - a1x * a0y;

        // Rotate each cubic coefficient using line for new coordinate system
        // Find roots of rotated cubic
        final Polynomial polynomial = new Polynomial(
                Points2D.dotProduct(n, c3),
                Points2D.dotProduct(n, c2),
                Points2D.dotProduct(n, c1),
                Points2D.dotProduct(n, c0) + cl
        );
        double[] roots = polynomial.getRoots();

        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        IntersectionStatus status = IntersectionStatus.NO_INTERSECTION;
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (-epsilon <= t && t <= 1 + epsilon) {
                // We're within the Bezier curve
                // Find point on Bezier
                final Point2D.Double p5, p6, p7, p8, p9, p10;
                p5 = lerp(p0, p1, t);
                p6 = lerp(p1, p2, t);
                p7 = lerp(p2, p3, t);

                p8 = lerp(p5, p6, t);
                p9 = lerp(p6, p7, t);

                p10 = lerp(p8, p9, t);

                // See if point is on ray
                double t1 = argumentOnLine(a0x, a0y, a1x, a1y, p10.getX(), p10.getY());
                if (-epsilon <= t1 && t1 <= maxT) {
                    status = IntersectionStatus.INTERSECTION;
                    result.add(new IntersectionPoint(p10, t1));
                }
            }
        }

        return new IntersectionResult(status, result);
    }

    public static IntersectionResultEx intersectRayCubicCurveEx(double aox, double aoy, double adx, double ady, double maxT,
                                                                double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double p3x, double p3y,
                                                                double epsilon) {
        IntersectionResult result = intersectCubicCurveRay(
                p0x, p0y, p1x, p1y, p2x, p2y, p3x, p3y,
                aox, aoy, adx, ady, maxT, epsilon);
        ArrayList<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : result) {
            double x = ip.getX();
            double y = ip.getY();
            Point2D.Double tangentA = BezierCurves.evalCubicCurveTangent(p0x, p0y, p1x, p1y, p2x, p2y, p3x, p3y, ip.getArgumentA());
            list.add(new IntersectionPointEx(
                    x, y,
                    IntersectLinePoint.argumentOnLine(aox, aoy, adx, ady, x, y), adx, ady,
                    ip.getArgumentA(), tangentA.getX(), tangentA.getY()
            ));
        }

        return new IntersectionResultEx(result.getStatus(), list);
    }

    public static IntersectionResultEx intersectRayCubicCurveEx(
            double aox, double aoy, double adx, double ady, double maxT,
            double b0x, double b0y, double b1x, double b1y, double b2x, double b2y, double b3x, double b3y) {
        return intersectRayCubicCurveEx(aox, aoy, adx, ady, maxT, b0x, b0y, b1x, b1y, b2x, b2y, b3x, b3y, Geom.REAL_THRESHOLD);
    }

    public static IntersectionResultEx intersectCubicCurveRayEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
            double b0x, double b0y, double b1x, double b1y, double maxT,
            double epsilon) {
        IntersectionResult result = intersectCubicCurveRay(
                a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y,
                b0x, b0y, b1x, b1y, maxT,
                epsilon);
        ArrayList<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : result) {
            double x = ip.getX();
            double y = ip.getY();
            Point2D.Double tangentA = BezierCurves.evalCubicCurveTangent(a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, ip.getArgumentA());
            list.add(new IntersectionPointEx(
                    x, y,
                    ip.getArgumentA(), tangentA.getX(), tangentA.getY(),
                    IntersectLinePoint.argumentOnLine(b0x, b0y, b1x, b1y, x, y), b1x - b0x, b1y - b0y
            ));
        }

        return new IntersectionResultEx(result.getStatus(), list);
    }

    public static IntersectionResultEx intersectCubicCurveRayEx(
            double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y,
            double b0x, double b0y, double b1x, double b1y, double maxT) {
        return intersectCubicCurveRayEx(a0x, a0y, a1x, a1y, a2x, a2y, a3x, a3y, b0x, b0y, b1x, b1y, maxT, Geom.REAL_THRESHOLD);
    }
}
