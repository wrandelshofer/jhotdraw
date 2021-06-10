/*
 * @(#)BulgeConversionFunctions.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;

import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.Point2D;


/**
 * Provides bulge conversion functions.
 * <p>
 * The curvature of a {@link PolyArcPath} arc segment is defined using a quantity
 * known as {@code bulge}. This unit measures the deviation of the curve from
 * the straight line (chord) joining the two vertices of the segment.
 * It is defined as the ratio of the arc sagitta (versine) to half the length
 * of the chord between the two vertices; this ratio is equal to the tangent of
 * a quarter of the included arc angle between the two polyline vertices.
 * <p>
 * In this way, a negative bulge indicates that the arc follows a clockwise
 * direction from the first vertex to the next, with a positive bulge describing
 * an anticlockwise oriented arc.
 * A bulge of 0 indicates a straight segment, and a bulge of 1 is a semicircle.
 * <p>
 * An AutoCAD Arc Entity is defined by a center, radius and start & end angle.
 * The arc is always defined to be anticlockwise oriented, that is, following
 * an anticlockwise direction from the start angle to the end angle.
 * <p>
 * References:
 * <dl>
 *     <dt>Bulge conversions</dt>
 *     <dd><a href="http://www.lee-mac.com/bulgeconversion.html">lee-mac.com</a></dd>
 * </dl>
 */
public class BulgeConversionFunctions {

    public static class ArcRadiusAndCenter {
        final double radius;
        final Point2D.Double center;

        public ArcRadiusAndCenter(double radius, Point2D.Double c) {
            this(c.getX(), c.getY(), radius);
        }

        public ArcRadiusAndCenter(double cx, double cy, double radius) {
            this.center = new Point2D.Double(cx, cy);
            this.radius = radius;
        }

        public Point2D.Double getCenter() {
            return center;
        }

        public double getCx() {
            return center.getX();
        }

        public double getCy() {
            return center.getY();
        }

        public double getRadius() {
            return radius;
        }
    }


    /**
     * This version uses the relationship between the arc sagitta and bulge
     * factor, illustrated by the following diagram:
     * <pre>
     *                .
     *         .      |    .
     *     .          |s        .
     *   .∡θ/4   d    |          .
     * p1-------------+------------p2
     *    \                      /
     *       \                / r
     *          \    ∡θ    /
     *                c
     * </pre>
     * <ul>
     *     <li>bulge = b = tan(θ/4)</li>
     *     <li>angle = θ = 4*arctan(b)</li>
     *     <li>sagitta = s = b * d</li>
     *     <li>radius = r = (s^2+d^2)/(2*s) = d*(b^2+1)/(2*b)</li>
     *     <li>half chord = d = r * sin(θ/2) = |p2 - p1|/2</li>
     *     <li>c = polar(p1, atan2(p1,p2) + (pi - θ)/2, r)   </li>
     * </ul>
     */
    public static ArcRadiusAndCenter computeCircle(double x1, double y1, double x2, double y2, double b) {
        double chord = Geom.distance(x1, y1, x2, y2);

        double theta = 4 * Math.atan(b);

        double r = chord * (b * b + 1) / (4 * b);
        double a = Geom.atan2(y2 - y1, x2 - x1) + (Math.PI - theta) / 2;
        double cx = x1 + Math.sin(a) * r;
        double cy = y1 + Math.cos(a) * r;

        return new ArcRadiusAndCenter(cx, cy, r);
    }

    /**
     * This function will return the bulge value describing an arc which starts
     * at the first supplied point pt1, passes through the second supplied point
     * pt2, and terminates at the third supplied point pt3.
     * <p>
     * The returned bulge value may be positive or negative, depending upon
     * whether the arc passing through the three points traces a clockwise or counter-clockwise path.
     *
     * @param x1 point 1 x-coordinate
     * @param y1 point 1 y coordinate
     * @param x2 point 2 x-coordinate
     * @param y2 point 2 y coordinate
     * @param x3 point 3 x-coordinate
     * @param y3 point 3 y coordinate
     * @return the bulge
     */
    public static double computeBulge(double x1, double y1, double x2, double y2, double x3, double y3) {
        double a = 0.5 * (Math.PI - Geom.atan2(y2 - y1, x2 - x1) + Geom.atan2(y2 - y3, x2 - x3));
        double cosa = Math.cos(a);
        return cosa == 0.0 ? 0 : Math.sin(a) / cosa;
    }

    /**
     * This function will return the bulge value describing an arc with the
     * given radius which starts at the first supplied point pt1,
     * and terminates at the third supplied point pt3.
     * <p>
     * sin(θ/2)=d/r;
     * θ=asin(d/r)*2;
     * bulge=b=tan(θ/4)=tan(asin(d/r)*0.5)
     * <p>
     * See {@link #computeCircle} for the formulas used.
     *
     * @param x1 point 1 x-coordinate
     * @param y1 point 1 y coordinate
     * @param x2 point 2 x-coordinate
     * @param y2 point 2 y coordinate
     * @param r  the radius of the circle
     * @return the bulge
     */
    public static double computeBulge(double x1, double y1, double x2, double y2, double r) {
        double chord = Geom.distance(x1, y1, x2, y2);
        double d = chord * 0.5;
        return r <= chord ? 0.0 : Math.tan(Math.asin(d / r) * 0.5);
    }

    /* Compute the arc radius and arc center of a arc segment defined by v1 to v2.*/
    public static BulgeConversionFunctions.ArcRadiusAndCenter arcRadiusAndCenter(PlineVertex v1,
                                                                                 PlineVertex v2) {
        assert !v1.bulgeIsZero() : "v1 to v2 must be an arc";
        assert !Geom.almostEqual(v1.pos(), v2.pos()) : "v1 must not be ontop of v2";

        // compute radius
        double b = Math.abs(v1.bulge());
        Point2D.Double v = Points2D.subtract(v2.pos(),v1.pos());
        double d = Points2D.magnitude(v);
        double r = d * (b * b + 1.0) / (4.0 * b);

        // compute center
        double s = b * d / 2.0;
        double m = r - s;
        double offsX = -m * v.getY() / d;
        double offsY = m * v.getX() / d;
        if (v1.bulgeIsNeg()) {
            offsX = -offsX;
            offsY = -offsY;
        }

        Point2D.Double c = new Point2D.Double(v1.getX() + v.getX() * 0.5 + offsX, v1.getY() + v.getY() * 0.5 + offsY);
        return new BulgeConversionFunctions.ArcRadiusAndCenter(r, c);


    }

}
