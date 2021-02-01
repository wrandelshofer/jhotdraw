package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.Geom;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.jhotdraw8.geom.Geom.lerp;

public class IntersectCircleLine {
    private IntersectCircleLine() {
    }

    @NonNull
    public static IntersectionResultEx intersectCircleLineEx(double cx, double cy, double r, double a0x, double a0y, double a1x, double a1y) {
        return intersectCircleLineEx(new Point2D.Double(cx, cy), r, new Point2D.Double(a0x, a0y), new Point2D.Double(a1x, a1y));
    }

    /**
     * Computes the intersection between a circle and a line.
     * <p>
     * FIXME actually computes line intersection with parameter t of line, and
     * not t of circle.
     *
     * @param c  the center of the circle
     * @param r  the radius of the circle
     * @param a0 point 1 of the line
     * @param a1 point 2 of the line
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectCircleLineEx(@NonNull Point2D c, double r, @NonNull Point2D a0, @NonNull Point2D a1) {
        IntersectionResultEx inter = intersectLineCircleEx(a0, a1, c, r);
        // FIXME compute t of circle!
        return inter;
    }

    /**
     * Computes the intersection between a line and a circle.
     * <p>
     * The intersection will contain the parameters 't' of the line in range
     * [0,1].
     *
     * @param c  the center of the circle
     * @param r  the radius of the circle
     * @param a0 point 0 of the line
     * @param a1 point 1 of the line
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectLineCircleEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D c, double r) {
        return intersectLineCircleEx(a0.getX(), a0.getY(), a1.getX(), a1.getY(), c.getX(), c.getY(), r);
    }

    public static IntersectionResultEx intersectLineCircleEx(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D c, double r, double epsilon) {
        return intersectLineCircleEx(a0.getX(), a0.getY(), a1.getX(), a1.getY(), c.getX(), c.getY(), r, epsilon);
    }

    public static IntersectionResult intersectLineCircle(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D c, double r, double epsilon) {
        return intersectLineCircle(a0.getX(), a0.getY(), a1.getX(), a1.getY(), c.getX(), c.getY(), r, epsilon);
    }

    /**
     * Computes the intersection between a line and a circle.
     * <p>
     * The intersection will contain the parameters 't' of the line in range
     * [0 - epsilon, 1 + epsilon].
     *
     * <p>
     * The intersection will have one of the following status:
     * <ul>
     * <li>{@link IntersectionStatus#INTERSECTION}</li>
     * <li>{@link IntersectionStatus#NO_INTERSECTION_INSIDE}</li>
     * <li>{@link IntersectionStatus#NO_INTERSECTION_OUTSIDE}</li>
     * </ul>
     * <p>
     * This method solves the following equation:
     * <pre>
     * {@literal x0 + (x1 - x0) · t, y0 + (y1 - y0) · t, with t in range [0,1] : line equation}
     * {@literal (x - cx)² + (y - cy)² = r²} : circle equation
     * {@literal (x0 + (x1 - x0) · t - cx)² + (y0 + (y1 - y0) · t - cy)² - r² =0} : intersection equation
     * {@literal (x0 + x1·t - x0·t - cx)² + (y0 + y1· t - y0· t - cy)² - r² =0}
     * {@literal -2·x0·x1·t²  + 2·x0·(cx+x1)·t - 2·x0*cx +(x0²+x1²)·t² - 2·(x0² - x1·cx)·t + x0² + cx²  ...+same for y...   - r² =0}
     * {@literal (x0²+-2·x0·x1+x1²)·t²  + (2·x0·(cx+x1)- 2·(x0² - x1·cx))·t  - 2·x0*cx + x0² + cx²  ...+same for y...   - r² =0}
     * {@literal (x1 - x0)²·t²  + 2·((x1 - x0)·(x0 - cx))·t  - 2·x0*cx + x0² + cx²  ...+same for y...   - r² =0}
     * {@literal (x1 - x0)²·(y1 - y0)²·t²  + 2·((x1 - x0)·(x0 - cx)+(y1 - y0)·(y0 - cy))·t - 2·(x0·cx + y0·cy) + cx² + cy² + x0² + y0²  - r² =0}
     * {@literal Δx²·Δy²·t²  + 2·(Δx·(x0 - cx)+Δy·(y0 - cy))·t - 2·(x0·cx + y0·cy) + cx² + cy² + x0² + y0²  - r² =0}
     * {@literal a·t² + b·t + c = 0 : quadratic polynomial, with t in range [0,1]}
     * </pre>
     *
     * @param x0 point 0 of the line
     * @param y0 point 0 of the line
     * @param x1 point 1 of the line
     * @param y1 point 1 of the line
     * @param cx the center of the circle
     * @param cy the center of the circle
     * @param r  the radius of the circle
     * @return computed intersection
     */
    @NonNull
    public static IntersectionResultEx intersectLineCircleEx(double x0, double y0, double x1, double y1, double cx, double cy, double r) {
        return intersectLineCircleEx(x0, y0, x1, y1, cx, cy, r, Intersections.EPSILON);
    }

    /**
     * This method computes the argument of the circle function with atan2
     * and thus may be unnecessarily slow if you only need the argument
     * of the line function.
     * <p>
     * The code of this method has been derived from intersection.js by
     * Kevin Lindsey, copyright 2002 Kevin Lindsey, BSD 3-clause license.
     * http://www.kevlindev.com/gui/math/intersection/Intersection.js.
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param cx
     * @param cy
     * @param r
     * @param epsilon
     * @return
     */
    @NonNull
    public static IntersectionResultEx intersectLineCircleEx(double x0, double y0, double x1, double y1, double cx, double cy, double r, double epsilon) {
        List<IntersectionPointEx> result = new ArrayList<>(2);
        final double Δx, Δy;
        Δx = x1 - x0;
        Δy = y1 - y0;
        final double a, b, c, deter;
        a = Δx * Δx + Δy * Δy;
        b = 2 * (Δx * (x0 - cx) + Δy * (y0 - cy));
        c = cx * cx + cy * cy + x0 * x0 + y0 * y0 - 2 * (cx * x0 + cy * y0) - r * r;
        deter = b * b - 4 * a * c;

        IntersectionStatus status;
        double minT = -epsilon;
        double maxT = 1 + epsilon;
        if (deter < minT) {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else {
            if (deter > epsilon) {
                double e, t1, t2;
                e = Math.sqrt(deter);
                t1 = (-b + e) / (2 * a);
                t2 = (-b - e) / (2 * a);

                if ((t1 < minT || t1 > maxT) && (t2 < minT || t2 > maxT)) {
                    if ((t1 <= minT && t2 <= minT) || (t1 > maxT && t2 > maxT)) {
                        status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_INSIDE;
                    }
                } else {
                    status = IntersectionStatus.INTERSECTION;
                    if (t2 < t1) {
                        double swap = t2;
                        t2 = t1;
                        t1 = swap;
                    }
                    if (minT <= t1 && t1 <= maxT) {
                        Point2D.Double p = lerp(x0, y0, x1, y1, t1);
                        result.add(new IntersectionPointEx(p,
                                t1, new Point2D.Double(x1 - x0, y1 - y0),
                                Geom.atan2(p.getY() - cy, p.getX() - cx),
                                new Point2D.Double(p.getY() - cy, -p.getX() - cx)
                        ));
                    }
                    if (minT <= t2 && t2 <= maxT) {
                        Point2D.Double p = lerp(x0, y0, x1, y1, t2);
                        result.add(new IntersectionPointEx(p,
                                t2, new Point2D.Double(x1 - x0, y1 - y0),
                                Geom.atan2(p.getY() - cy, p.getX() - cx),
                                new Point2D.Double(p.getY() - cy, -p.getX() - cx)
                        ));
                    }
                }
            } else {
                double t = (-b) / (2 * a);
                if (minT <= t && t <= maxT) {
                    status = IntersectionStatus.INTERSECTION;
                    Point2D.Double p = lerp(x0, y0, x1, y1, t);
                    result.add(new IntersectionPointEx(p,
                            t, new Point2D.Double(x1 - x0, y1 - y0),
                            Geom.atan2(p.getY() - cy, p.getX() - cx),
                            new Point2D.Double(p.getY() - cy, -p.getX() - cx)
                    ));
                } else {
                    status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
                }
            }
        }

        return new IntersectionResultEx(status, result);
    }

    @NonNull
    public static IntersectionResult intersectLineCircle(double x0, double y0, double x1, double y1, double cx, double cy, double r) {
        return intersectLineCircle(x0, y0, x1, y1, cx, cy, r, Geom.REAL_THRESHOLD);
    }

    @NonNull
    public static IntersectionResult intersectLineCircle(double x0, double y0, double x1, double y1, double cx, double cy, double r, double epsilon) {
        List<IntersectionPoint> result = new ArrayList<>(2);
        final double Δx, Δy;
        Δx = x1 - x0;
        Δy = y1 - y0;
        final double a, b, c, deter;
        a = Δx * Δx + Δy * Δy;
        b = 2 * (Δx * (x0 - cx) + Δy * (y0 - cy));
        c = cx * cx + cy * cy + x0 * x0 + y0 * y0 - 2 * (cx * x0 + cy * y0) - r * r;
        deter = b * b - 4 * a * c;

        IntersectionStatus status;
        double minT = -epsilon;
        double maxT = 1 + epsilon;
        if (deter < minT) {
            status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
        } else {
            if (deter > epsilon) {
                double e, t1, t2;
                e = Math.sqrt(deter);
                t1 = (-b + e) / (2 * a);
                t2 = (-b - e) / (2 * a);

                if ((t1 < minT || t1 > maxT) && (t2 < minT || t2 > maxT)) {
                    if ((t1 <= minT && t2 <= minT) || (t1 > maxT && t2 > maxT)) {
                        status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
                    } else {
                        status = IntersectionStatus.NO_INTERSECTION_INSIDE;
                    }
                } else {
                    status = IntersectionStatus.INTERSECTION;
                    if (t2 < t1) {
                        double swap = t2;
                        t2 = t1;
                        t1 = swap;
                    }
                    if (minT <= t1 && t1 <= maxT) {
                        result.add(
                                new IntersectionPoint(lerp(x0, y0, x1, y1, t1).getX(), lerp(x0, y0, x1, y1, t1).getY(), t1));
                    }
                    if (minT <= t2 && t2 <= maxT) {
                        result.add(new IntersectionPoint(lerp(x0, y0, x1, y1, t2).getX(), lerp(x0, y0, x1, y1, t2).getY(), t2));
                    }
                }
            } else {
                double t = (-b) / (2 * a);
                if (minT <= t && t <= maxT) {
                    status = IntersectionStatus.INTERSECTION;
                    Point2D.Double p = lerp(x0, y0, x1, y1, t);
                    result.add(new IntersectionPoint(p.getX(), p.getY(), t));
                } else {
                    status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
                }
            }
        }

        return new IntersectionResult(status, result);
    }
}
