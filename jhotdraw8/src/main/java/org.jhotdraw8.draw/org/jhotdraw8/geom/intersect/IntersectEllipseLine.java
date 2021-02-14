/*
 * @(#)IntersectEllipseLine.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.AABB;
import org.jhotdraw8.geom.Geom;
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
     * @param ac  the center of the ellipse
     * @param arx the x-radius of the ellipse
     * @param ary the y-radius of the ellipse
     * @param b0  point 0 of the line
     * @param b1  point 1 of the line
     * @return computed intersection
     */
    public static @NonNull IntersectionResult intersectEllipseLine(@NonNull Point2D ac, double arx, double ary,
                                                                   @NonNull Point2D b0, @NonNull Point2D b1) {
        IntersectionResult result = intersectLineEllipse(b0, b1, ac, arx, ary);
        ArrayList<IntersectionPoint> list = new ArrayList<>();
        for (IntersectionPoint ip : result) {
            double x = ip.getX();
            double y = ip.getY();
            list.add(new IntersectionPoint(
                    x, y,
                    Geom.atan2Ellipse(ac.getX(), ac.getY(), arx, ary, x, y)
            ));
        }

        return new IntersectionResult(result.getStatus(), list);
    }

    public static @NonNull IntersectionResultEx intersectEllipseLineEx(@NonNull Point2D ac, double arx, double ary,
                                                                       @NonNull Point2D b0, @NonNull Point2D b1) {
        return intersectEllipseLineEx(ac.getX(), ac.getY(), arx, ary, b0.getX(), b0.getY(), b1.getX(), b1.getY());
    }

    public static @NonNull IntersectionResult intersectEllipseLine(double acx, double acy, double arx, double ary,
                                                                   double b0x, double b0y, double b1x, double b1y) {
        return intersectEllipseLine(acx, acy, arx, ary, b0x, b0y, b1x, b1y, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResultEx intersectEllipseLineEx(double acx, double acy, double arx, double ary,
                                                                       double b0x, double b0y, double b1x, double b1y) {
        return intersectEllipseLineEx(acx, acy, arx, ary, b0x, b0y, b1x, b1y, Geom.REAL_THRESHOLD);
    }

    public static @NonNull IntersectionResultEx intersectEllipseLineEx(double acx, double acy, double arx, double ary,
                                                                       double b0x, double b0y, double b1x, double b1y, double epsilon) {
        IntersectionResult result = intersectEllipseLine(acx, acy, arx, ary, b0x, b0y, b1x, b1y, epsilon);
        ArrayList<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : result) {
            double x = ip.getX();
            double y = ip.getY();
            list.add(new IntersectionPointEx(
                    x, y,
                    ip.getArgumentA(), y - acy, acx - x,
                    IntersectLinePoint.argumentOnLine(b0x, b0y, b1x, b1y, x, y), b1x - b0x, b1y - b0y
            ));
        }

        return new IntersectionResultEx(result.getStatus(), list);
    }

    public static @NonNull IntersectionResult intersectEllipseLine(double cx, double cy, double rx, double ry,
                                                                   double x0, double y0, double x1, double y1, double epsilon) {
        IntersectionResult result = intersectLineEllipse(x0, y0, x1, y1, cx, cy, rx, ry, epsilon);
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
    public static @NonNull IntersectionResult intersectLineEllipse(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull AABB e) {
        double rx = e.getWidth() * 0.5;
        double ry = e.getHeight() * 0.5;
        return intersectLineEllipse(a0, a1, new Point2D.Double(e.getMinX() + rx, e.getMinY() + ry), rx, ry);
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
    public static @NonNull IntersectionResult intersectLineEllipse(@NonNull Point2D a0, @NonNull Point2D a1, @NonNull Point2D ec, double rx, double ry) {
        return intersectLineEllipse(a0.getX(), a0.getY(), a1.getX(), a1.getY(), ec.getX(), ec.getY(), rx, ry, Geom.REAL_THRESHOLD);
    }

    /**
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
     * @param rx
     * @param ry
     * @param epsilon
     * @return
     */
    public static @NonNull IntersectionResult intersectLineEllipse(double x0, double y0, double x1, double y1,
                                                                   double cx, double cy, double rx, double ry,
                                                                   double epsilon) {
        List<IntersectionPoint> result = new ArrayList<>();

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
        if (d < -epsilon) {
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
                    result.add(new IntersectionPoint(Geom.lerp(x0, y0, x1, y1, t0), t0));
                }
                if (0 <= t1 && t1 <= 1) {
                    result.add(new IntersectionPoint(lerp(x0, y0, x1, y1, t1), t1));
                }
            }
        } else {
            final double t = -b / a;
            if (0 <= t && t <= 1) {
                status = IntersectionStatus.INTERSECTION;
                result.add(new IntersectionPoint(lerp(x0, y0, x1, y1, t), t));
            } else {
                status = IntersectionStatus.NO_INTERSECTION_OUTSIDE;
            }
        }

        return new IntersectionResult(status, result);
    }

    public static IntersectionResultEx intersectLineEllipseEx(double x0, double y0, double x1, double y1,
                                                              double cx, double cy, double rx, double ry) {

        return intersectLineEllipseEx(x0, y0, x1, y1, cx, cy, rx, ry, Geom.REAL_THRESHOLD);
    }

    public static IntersectionResultEx intersectLineEllipseEx(double x0, double y0, double x1, double y1,
                                                              double cx, double cy, double rx, double ry,
                                                              double epsilon) {
        IntersectionResult result = intersectLineEllipse(x0, y0, x1, y1, cx, cy, rx, ry, epsilon);
        double atx = x1 - x0, aty = y1 - y0;
        ArrayList<IntersectionPointEx> list = new ArrayList<>();
        for (IntersectionPoint ip : result) {
            double barg = Geom.atan2Ellipse(cx, cy, rx, ry, ip.getX(), ip.getY());
            list.add(new IntersectionPointEx(ip.getX(), ip.getY(),
                    ip.getArgumentA(), atx, aty,
                    barg, ip.getY() - cy, cx - ip.getX()
            ));
        }
        return new IntersectionResultEx(result.getStatus(), list);
    }
}
