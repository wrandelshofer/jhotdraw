/* @(#)Geom.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import static java.lang.Math.*;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

/**
 * Some geometric utilities.
 *
 * @version $Id$
 */
public class Geom {

    /**
     * The bitmask that indicates that a point lies below the rectangle.
     */
    public static final int OUT_BOTTOM = 8;
    /**
     * The bitmask that indicates that a point lies to the left of the
     * rectangle.
     */
    public static final int OUT_LEFT = 1;
    /**
     * The bitmask that indicates that a point lies to the right of the
     * rectangle.
     */
    public static final int OUT_RIGHT = 4;
    /**
     * The bitmask that indicates that a point lies above the rectangle.
     */
    public static final int OUT_TOP = 2;

    private Geom() {
    } // never instantiated

    public static BoundingBox add(Bounds a, Bounds b) {
        double x = min(a.getMinX(), b.getMinX());
        double y = min(a.getMinY(), b.getMinY());
        return new BoundingBox(x, y, max(a.getMaxX(), b.getMaxX()) - x, max(a.getMaxY(), b.getMaxY()) - y);
    }

    private static Rectangle2D add(Rectangle2D r, double newx, double newy) {
        double x1 = Math.min(r.getMinX(), newx);
        double x2 = Math.max(r.getMaxX(), newx);
        double y1 = Math.min(r.getMinY(), newy);
        double y2 = Math.max(r.getMaxY(), newy);
        return new Rectangle2D(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Gets the angle of the specified line.
     *
     * @param x1 the x-coordinate of point 1 on the line
     * @param y1 the y-coordinate of point 1 on the line
     * @param x2 the x-coordinate of point 2 on the line
     * @param y2 the y-coordinate of point 2 on the line
     * @return the angle in radians
     */
    public static double angle(double x1, double y1, double x2, double y2) {
        return atan2(y2 - y1, x2 - x1);
    }

    /**
     * Signed difference of two angles.
     *
     * @param from angle 0
     * @param to angle 1
     * @return -PI &lt;= diff &lt;= PI.
     */
    public static double angleSubtract(double from, double to) {
        double diff = from - to;
        if (diff < -2 * PI) {
            diff += 2 * PI;
        }
        return diff;
    }

    /**
     * Gets the point on a rectangle that corresponds to the given angle.
     *
     * @param r the rectangle
     * @param angle the angle of the ray starting at the center of the rectangle
     * @return a point on the rectangle
     */
    public static Point2D angleToPoint(Rectangle2D r, double angle) {
        double si = sin(angle);
        double co = cos(angle);
        double e = 0.0001;

        double x = 0, y = 0;
        if (abs(si) > e) {
            x = (1.0 + co / abs(si)) / 2.0 * r.getWidth();
            x = clamp(x, 0, r.getWidth());
        } else if (co >= 0.0) {
            x = r.getWidth();
        }
        if (abs(co) > e) {
            y = (1.0 + si / abs(co)) / 2.0 * r.getHeight();
            y = clamp(y, 0, r.getHeight());
        } else if (si >= 0.0) {
            y = r.getHeight();
        }
        return new Point2D(r.getMinX() + x, r.getMinY() + y);
    }

    /**
     * Signed shortest distance between two angles.
     *
     * @param from angle 0
     * @param to angle 1
     * @return -PI &lt;= diff &lt;= PI.
     */
    public static double anglesSignedSpan(double from, double to) {
        double diff = to - from;
        if (diff > PI) {
            diff = diff - PI;
        } else if (diff < -PI) {
            diff = diff + 2 * PI;
        }
        return diff;
    }

    /**
     * Unsigned shortest distance between two angles.
     *
     * @param from angle 0
     * @param to angle 1
     * @return 0 &lt;= diff &lt;= PI.
     */
    public static double anglesUnsignedSpan(double from, double to) {
        return from > to ? from - to : to - from;
    }

    /**
     * Caps the line defined by p1 and p2 by the number of units specified by
     * radius.
     *
     * @param p1 point 1, the start point
     * @param p2 point 2, the end point
     * @param radius the radius
     * @return A new end point for the line.
     */
    public static Point2D cap(Point2D p1, Point2D p2, double radius) {
        double angle = PI / 2 - atan2(p2.getX() - p1.getX(), p2.getY()
                - p1.getY());
        Point2D p3 = new Point2D(
                p2.getX() + radius * cos(angle),
                p2.getY() + radius * sin(angle));
        return p3;
    }

    public static Point2D center(java.awt.geom.Rectangle2D r) {
        return new Point2D(r.getCenterX(), r.getCenterY());
    }

    /**
     * Calculate the center of the given bounds
     *
     * @param r the bounds
     * @return the center
     */
    public static Point2D center(Rectangle2D r) {
        return new Point2D(r.getMinX() + r.getWidth() * 0.5, r.getMinY()
                + r.getHeight() * 0.5);
    }

    /**
     * Calculate the center of the given bounds
     *
     * @param r the bounds
     * @return the center
     */
    public static Point2D center(Bounds r) {
        return new Point2D(
                r.getMinX() + r.getWidth() * 0.5,
                r.getMinY() + r.getHeight() * 0.5
        );
    }

    /**
     * Returns a point on the edge of the shape which crosses the line from the
     * center of the shape to the specified point. If no edge crosses of the
     * shape crosses the line, the nearest control point of the shape is
     * returned.
     *
     * @param shape the shape
     * @param p the point
     * @return a point on the shape
     */
    public static Point2D chop(Shape shape, Point2D p) {
        java.awt.geom.Rectangle2D bounds = shape.getBounds2D();
        java.awt.geom.Point2D.Double ctr = new java.awt.geom.Point2D.Double(bounds.getCenterX(), bounds.getCenterY());

        // Chopped point
        double cx = -1;
        double cy = -1;
        double len = Double.MAX_VALUE;

        // Try for points along edge
        PathIterator i = shape.getPathIterator(new AffineTransform(), 1);
        double[] coords = new double[6];
        double prevX = coords[0];
        double prevY = coords[1];
        double moveToX = prevX;
        double moveToY = prevY;
        i.next();
        for (; !i.isDone(); i.next()) {
            switch (i.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    moveToX = coords[0];
                    moveToY = coords[1];
                    break;
                case PathIterator.SEG_CLOSE:
                    coords[0] = moveToX;
                    coords[1] = moveToY;
                    break;
            }
            Point2D chop = Geom.intersect(
                    prevX, prevY,
                    coords[0], coords[1],
                    p.getX(), p.getY(),
                    ctr.getX(), ctr.getY());

            if (chop != null) {
                double cl = Geom.length2(chop.getX(), chop.getY(), p.getX(), p.getY());
                if (cl < len) {
                    len = cl;
                    cx = chop.getX();
                    cy = chop.getY();
                }
            }

            prevX = coords[0];
            prevY = coords[1];
        }

        /*
        if (isClosed() && size() > 1) {
        Node first = get(0);
        Node last = get(size() - 1);
        Point2D.Double chop = Geom.intersect(
        first.x[0], first.y[0],
        last.x[0], last.y[0],
        p.x, p.y,
        ctr.x, ctr.y
        );
        if (chop != null) {
        double cl = Geom.length2(chop.x, chop.y, p.x, p.y);
        if (cl < len) {
        len = cl;
        cx = chop.x;
        cy = chop.y;
        }
        }
        }*/
        // if none found, pick closest vertex
        if (len == Double.MAX_VALUE) {
            i = shape.getPathIterator(new AffineTransform(), 1);
            for (; !i.isDone(); i.next()) {
                i.currentSegment(coords);

                double l = Geom.length2(ctr.x, ctr.y, coords[0], coords[1]);
                if (l < len) {
                    len = l;
                    cx = coords[0];
                    cy = coords[1];
                }
            }
        }
        return new Point2D(cx, cy);
    }

    /**
     * Clamps a value to the given range.
     *
     * @param value the value
     * @param min the lower bound of the range
     * @param max the upper bound of the range
     * @return the constrained value
     */
    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * Clamps a value to the given range.
     *
     * @param value the value
     * @param min the lower bound of the range
     * @param max the upper bound of the range
     * @return the constrained value
     */
    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * Returns true if the bounds contain the specified point within the given
     * tolerance.
     *
     * @param r the bounds
     * @param p the point
     * @param tolerance the tolerance
     * @return true if inside
     */
    public static boolean contains(Bounds r, Point2D p, double tolerance) {
        return contains(r, p.getX(), p.getY(), tolerance);
    }

    /**
     * Returns true if the bounds contain the specified point within the given
     * tolerance.
     *
     * @param r the bounds
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param tolerance the tolerance
     * @return true if inside
     */
    public static boolean contains(Bounds r, double x, double y, double tolerance) {
        return r.getMinX() - tolerance <= x && x <= r.getMaxX() + tolerance
                && r.getMinY() - tolerance <= y && y <= r.getMaxY() + tolerance;
    }

    /**
     * Returns true, if rectangle 1 contains rectangle 2.
     * <p>
     * This method is similar to Rectangle2D.contains, but also returns true,
     * when rectangle1 contains rectangle2 and either or both of them are empty.
     *
     * @param r1 Rectangle2D 1.
     * @param r2 Rectangle2D 2.
     * @return true if r1 contains r2.
     */
    public static boolean contains(Rectangle2D r1, Rectangle2D r2) {
        return (r2.getMinX() >= r1.getMinX()
                && r2.getMinY() >= r1.getMinY()
                && (r2.getMinX() + max(0, r2.getWidth())) <= r1.getMinX()
                + max(0, r1.getWidth())
                && (r2.getMinY() + max(0, r2.getHeight())) <= r1.getMinY()
                + max(0, r1.getHeight()));
    }

    /**
     * Returns true, if rectangle 1 contains rectangle 2.
     * <p>
     * This method is similar to Rectangle2D.contains, but also returns true,
     * when rectangle1 contains rectangle2 and either or both of them are empty.
     *
     * @param r1 Rectangle 1.
     * @param r2 Rectangle 2.
     * @return true if r1 contains r2.
     */
    public static boolean containsAWT(java.awt.geom.Rectangle2D r1, java.awt.geom.Rectangle2D r2) {
        return (r2.getX()) >= r1.getX()
                && r2.getY() >= r1.getY()
                && (r2.getX() + max(0, r2.getWidth())) <= r1.getX() + max(0, r1.getWidth())
                && (r2.getY() + max(0, r2.getHeight())) <= r1.getY() + max(0, r1.getHeight());
    }

    /**
     * Returns the direction OUT_TOP, OUT_BOTTOM, OUT_LEFT, OUT_RIGHT from one
     * point to another one.
     *
     * @param x1 the x coordinate of point 1
     * @param y1 the y coordinate of point 1
     * @param x2 the x coordinate of point 2
     * @param y2 the y coordinate of point 2
     * @return the direction
     */
    public static int direction(double x1, double y1, double x2, double y2) {
        int direction = 0;
        double vx = x2 - x1;
        double vy = y2 - y1;

        if (vy < vx && vx > -vy) {
            direction = OUT_RIGHT;
        } else if (vy > vx && vy > -vx) {
            direction = OUT_TOP;
        } else if (vx < vy && vx < -vy) {
            direction = OUT_LEFT;
        } else {
            direction = OUT_BOTTOM;
        }
        return direction;
    }

    /**
     * compute distance of point from line segment, or Double.MAX_VALUE if
     * perpendicular projection is outside segment; or If pts on line are same,
     * return distance from point
     *
     * @param xa the x-coordinate of point a on the line
     * @param ya the y-coordinate of point a on the line
     * @param xb the x-coordinate of point b on the line
     * @param yb the y-coordinate of point b on the line
     * @param xc the x-coordinate of the point c
     * @param yc the y-coordinate of the point c
     * @return the distance from the line
     */
    public static double distanceFromLine(double xa, double ya,
            double xb, double yb,
            double xc, double yc) {

        // from Doug Lea's PolygonFigure
        // source:http://vision.dai.ed.ac.uk/andrewfg/c-g-a-faq.html#q7
        //Let the point be C (XC,YC) and the line be AB (XA,YA) to (XB,YB).
        //The length of the
        //      line segment AB is L:
        //
        //                    ___________________
        //                   |        2         2
        //              L = \| (XB-XA) + (YB-YA)
        //and
        //
        //                  (YA-YC)(YA-YB)-(XA-XC)(XB-XA)
        //              r = -----------------------------
        //                              L**2
        //
        //                  (YA-YC)(XB-XA)-(XA-XC)(YB-YA)
        //              s = -----------------------------
        //                              L**2
        //
        //      Let I be the point of perpendicular projection of C onto AB, the
        //
        //              XI=XA+r(XB-XA)
        //              YI=YA+r(YB-YA)
        //
        //      Distance from A to I = r*L
        //      Distance from C to I = s*L
        //
        //      If r < 0 I is on backward extension of AB
        //      If r>1 I is on ahead extension of AB
        //      If 0<=r<=1 I is on AB
        //
        //      If s < 0 C is left of AB (you can just check the numerator)
        //      If s>0 C is right of AB
        //      If s=0 C is on AB
        double xdiff = xb - xa;
        double ydiff = yb - ya;
        double l2 = xdiff * xdiff + ydiff * ydiff;

        if (l2 == 0) {
            return Geom.length(xa, ya, xc, yc);
        }

        double rnum = (ya - yc) * (ya - yb) - (xa - xc) * (xb - xa);
        double r = rnum / l2;

        if (r < 0.0 || r > 1.0) {
            return Double.MAX_VALUE;
        }

        double xi = xa + r * xdiff;
        double yi = ya + r * ydiff;
        double xd = xc - xi;
        double yd = yc - yi;
        return sqrt(xd * xd + yd * yd);

        /*
        * for directional version, instead use
        * double snum = (ya-yc) * (xb-xa) - (xa-xc) * (yb-ya);
        * double s = snum / l2;
        *
        * double l = sqrt((double)l2);
        * return = s * l;
         */
    }

    public static Point2D east(Rectangle2D r) {
        return new Point2D(r.getMinX() + r.getWidth(), r.getMinY()
                + r.getHeight() / 2);
    }

    /**
     * Gets the bounds of the specified shape.
     *
     * @param shape an AWT shape
     * @return JavaFX bounds
     */
    public static Bounds getBounds(java.awt.Shape shape) {
        java.awt.geom.Rectangle2D r = shape.getBounds2D();
        return new BoundingBox(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * Resizes the <code>Rectangle2D</code> both horizontally and vertically.
     * <p>
     * This method returns a new <code>Rectangle2D</code> so that it is
     * <code>h</code> units larger on both the left and right side, and
     * <code>v</code> units larger at both the top and bottom.
     * <p>
     * The new <code>Rectangle2D</code> has (<code>x&nbsp;-&nbsp;h</code>,
     * <code>y&nbsp;-&nbsp;v</code>) as its top-left corner, a width of
     * <code>width</code>&nbsp;<code>+</code>&nbsp;<code>2h</code>, and a height
     * of <code>height</code>&nbsp;<code>+</code>&nbsp;<code>2v</code>.
     * <p>
     * If negative values are supplied for <code>h</code> and <code>v</code>,
     * the size of the <code>Rectangle2D</code> decreases accordingly. The
     * <code>grow</code> method does not check whether the resulting values of
     * <code>width</code> and <code>height</code> are non-negative.
     *
     * @param r the rectangle
     * @param h the horizontal expansion
     * @param v the vertical expansion
     * @return the new rectangle
     */
    public static Rectangle2D grow(Rectangle2D r, double h, double v) {
        return new Rectangle2D(
                r.getMinX() - h,
                r.getMinY() - v,
                r.getWidth() + h * 2d,
                r.getHeight() + v * 2d);
    }

    /**
     * Resizes the <code>Bounds</code> both horizontally and vertically.
     * <p>
     * This method returns a new <code>Bounds</code> so that it is
     * <code>h</code> units larger on both the left and right side, and
     * <code>v</code> units larger at both the top and bottom.
     * <p>
     * The new <code>Bounds</code> has (<code>x&nbsp;-&nbsp;h</code>,
     * <code>y&nbsp;-&nbsp;v</code>) as its top-left corner, a width of
     * <code>width</code>&nbsp;<code>+</code>&nbsp;<code>2h</code>, and a height
     * of <code>height</code>&nbsp;<code>+</code>&nbsp;<code>2v</code>.
     * <p>
     * If negative values are supplied for <code>h</code> and <code>v</code>,
     * the size of the <code>Rectangle2D</code> decreases accordingly. The
     * <code>grow</code> method does not check whether the resulting values of
     * <code>width</code> and <code>height</code> are non-negative.
     *
     * @param r the bounds
     * @param h the horizontal expansion
     * @param v the vertical expansion
     * @return the new rectangle
     */
    public static Bounds grow(Bounds r, double h, double v) {
        return new BoundingBox(
                r.getMinX() - h,
                r.getMinY() - v,
                r.getWidth() + h * 2d,
                r.getHeight() + v * 2d);
    }

    /**
     * Computes the cross product of the homogenous vectors (x1,y1,1) x
     * (x2,y2,1).
     *
     * <pre>
     *   ( y1 * z2 - z1 * y2,
     *     z1 * x2 - x1 * z2,
     *     x1 * y2 - y1 * x2  )
     * </pre> With z1=1 and z2=1;
     *
     * @param p1 point 1
     * @param p2 point 2
     * @return the cross product
     */
    public static Point3D hcross(Point2D p1, Point2D p2) {
        return hcross(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    /**
     * Computes the cross product of the homogenous vectors (x1,y1,1) x
     * (x2,y2,1).
     *
     * <pre>
     *   ( y1 * z2 - z1 * y2,
     *     z1 * x2 - x1 * z2,
     *     x1 * y2 - y1 * x2  )
     * </pre> With z1=1 and z2=1;
     *
     * @param x1 x1
     * @param y1 y1
     * @param x2 x2
     * @param y2 y2
     * @return the cross product
     */
    public static Point3D hcross(double x1, double y1, double x2, double y2) {
        return new Point3D(//
                y1 * 1 - 1 * y2,//
                1 * x2 - x1 * 1,//
                x1 * y2 - y1 * x2 //
        );
    }

    /**
     * Homogenizes the vector (x1,y1,z1) to (x1,y1,1).
     *
     * <pre>
     *   ( y1 / z1,
     *     z1 / z1,
     *     1  )
     * </pre> With z1=1 and z2=1;
     *
     * @param p the vector
     * @return the normalized vector
     */
    public static Point3D homogenize(Point3D p) {
        return new Point3D(//
                p.getX() / p.getZ(),//
                p.getY() / p.getZ(),
                1
        );
    }

    /**
     * Homogenizes the vector (x1,y1,z1) to (x1,y1,1).
     *
     * <pre>
     *   ( y1 / z1,
     *     z1 / z1,
     *     1  )
     * </pre> With z1=1 and z2=1;
     *
     * @param p the vector
     * @return the normalized vector
     */
    public static Point2D homogenize2D(Point3D p) {
        double z = p.getZ();
        return new Point2D(//
                p.getX() / z,//
                p.getY() / z//
        );
    }

    /**
     * Standard line intersection algorithm Return the point of intersection if
     * it exists, else null
     *
     * from Doug Lea's PolygonFigure
     *
     *
     * @param xa the x-coordinate of point a on line 1
     * @param ya the y-coordinate of point a on line 1
     * @param xb the x-coordinate of point b on line 1
     * @param yb the y-coordinate of point b on line 1
     * @param xc the x-coordinate of point c on line 2
     * @param yc the y-coordinate of point c on line 2
     * @param xd the x-coordinate of point d on line 2
     * @param yd the y-coordinate of point d on line 2
     * @return the intersection point or null
     */
    public static Point2D intersect(double xa, // line 1 point 1 x
            double ya, // line 1 point 1 y
            double xb, // line 1 point 2 x
            double yb, // line 1 point 2 y
            double xc, // line 2 point 1 x
            double yc, // line 2 point 1 y
            double xd, // line 2 point 2 x
            double yd) { // line 2 point 2 y

        // source: http://vision.dai.ed.ac.uk/andrewfg/c-g-a-faq.html
        // eq: for lines AB and CD
        //     (YA-YC)(XD-XC)-(XA-XC)(YD-YC)
        // r = -----------------------------  (eqn 1)
        //     (XB-XA)(YD-YC)-(YB-YA)(XD-XC)
        //
        //     (YA-YC)(XB-XA)-(XA-XC)(YB-YA)
        // s = -----------------------------  (eqn 2)
        //     (XB-XA)(YD-YC)-(YB-YA)(XD-XC)
        //  XI = XA + r(XB-XA)
        //  YI = YA + r(YB-YA)
        double denom = ((xb - xa) * (yd - yc) - (yb - ya) * (xd - xc));

        double rnum = ((ya - yc) * (xd - xc) - (xa - xc) * (yd - yc));

        if (denom == 0.0) { // parallel
            if (rnum == 0.0) { // coincident; pick one end of first line
                if ((xa < xb && (xb < xc || xb < xd))
                        || (xa > xb && (xb > xc || xb > xd))) {
                    return new Point2D(xb, yb);
                } else {
                    return new Point2D(xa, ya);
                }
            } else {
                return null;
            }
        }

        double r = rnum / denom;
        double snum = ((ya - yc) * (xb - xa) - (xa - xc) * (yb - ya));
        double s = snum / denom;

        if (0.0 <= r && r <= 1.0 && 0.0 <= s && s <= 1.0) {
            double px = xa + (xb - xa) * r;
            double py = ya + (yb - ya) * r;
            return new Point2D(px, py);
        } else {
            return null;
        }
    }

    /**
     * Line intersection algorithm Return the point of intersection if it
     * exists, else null.
     *
     *
     * @param xa the x-coordinate of point a on line 1
     * @param ya the y-coordinate of point a on line 1
     * @param xb the x-coordinate of point b on line 1
     * @param yb the y-coordinate of point b on line 1
     * @param xc the x-coordinate of point c on line 2
     * @param yc the y-coordinate of point c on line 2
     * @param xd the x-coordinate of point d on line 2
     * @param yd the y-coordinate of point d on line 2
     * @param limit the lines are extend by up to limit units in order to meet
     * at the intersection point
     * @return the intersection point or null
     */
    public static Point2D intersect(
            double xa, // line 1 point 1 x
            double ya, // line 1 point 1 y
            double xb, // line 1 point 2 x
            double yb, // line 1 point 2 y
            double xc, // line 2 point 1 x
            double yc, // line 2 point 1 y
            double xd, // line 2 point 2 x
            double yd,
            double limit) { // line 2 point 2 y

        double limit2 = limit * limit;

        // source: http://vision.dai.ed.ac.uk/andrewfg/c-g-a-faq.html
        // eq: for lines AB and CD
        //     (YA-YC)(XD-XC)-(XA-XC)(YD-YC)
        // r = -----------------------------  (eqn 1)
        //     (XB-XA)(YD-YC)-(YB-YA)(XD-XC)
        //
        //     (YA-YC)(XB-XA)-(XA-XC)(YB-YA)
        // s = -----------------------------  (eqn 2)
        //     (XB-XA)(YD-YC)-(YB-YA)(XD-XC)
        //  XI = XA + r(XB-XA)
        //  YI = YA + r(YB-YA)
        double denom = ((xb - xa) * (yd - yc) - (yb - ya) * (xd - xc));

        double rnum = ((ya - yc) * (xd - xc) - (xa - xc) * (yd - yc));

        if (denom == 0.0) { // parallel
            if (rnum == 0.0) { // coincident; pick one end of first line
                if ((xa < xb && (xb < xc || xb < xd))
                        || (xa > xb && (xb > xc || xb > xd))) {
                    return new Point2D(xb, yb);
                } else {
                    return new Point2D(xa, ya);
                }
            } else {
                return null;
            }
        }

        double r = rnum / denom;
        double snum = ((ya - yc) * (xb - xa) - (xa - xc) * (yb - ya));
        double s = snum / denom;

        if (0.0 <= r && r <= 1.0 && 0.0 <= s && s <= 1.0) {
            double px = xa + (xb - xa) * r;
            double py = ya + (yb - ya) * r;
            return new Point2D(px, py);
        } else {
            double px = xa + (xb - xa) * r;
            double py = ya + (yb - ya) * r;

            if (length2(xa, ya, px, py) <= limit2
                    || length2(xb, yb, px, py) <= limit2
                    || length2(xc, yc, px, py) <= limit2
                    || length2(xd, yd, px, py) <= limit2) {
                return new Point2D(px, py);
            }

            return null;
        }
    }

    /**
     * Line intersection algorithm Return the point of intersection if it
     * exists, else null.
     *
     *
     * @param xa the x-coordinate of point a on line 1
     * @param ya the y-coordinate of point a on line 1
     * @param xb the x-coordinate of point b on line 1
     * @param yb the y-coordinate of point b on line 1
     * @param xc the x-coordinate of point c on line 2
     * @param yc the y-coordinate of point c on line 2
     * @param xd the x-coordinate of point d on line 2
     * @param yd the y-coordinate of point d on line 2
     * @param limit the lines are extend by up to limit units in order to meet
     * at the intersection point
     * @return the intersection point or null
     */
    public static java.awt.geom.Point2D.Double intersectAWT(
            double xa, // line 1 point 1 x
            double ya, // line 1 point 1 y
            double xb, // line 1 point 2 x
            double yb, // line 1 point 2 y
            double xc, // line 2 point 1 x
            double yc, // line 2 point 1 y
            double xd, // line 2 point 2 x
            double yd,
            double limit) { // line 2 point 2 y
        Point2D p = intersect(xa, ya, xb, yb, xc, yc, xd, yd, limit);
        return p == null ? null : new java.awt.geom.Point2D.Double(p.getX(), p.getY());
    }

    public static Bounds intersection(Bounds a, Bounds b) {
        double minx = Math.max(a.getMinX(), b.getMinX());
        double miny = Math.max(a.getMinY(), b.getMinY());
        double maxx = Math.min(a.getMaxX(), b.getMaxX());
        double maxy = Math.min(a.getMaxY(), b.getMaxY());
        return new BoundingBox(minx, miny, maxx - minx, maxy - miny);
    }

    /**
     * Gets the distance between to points
     *
     * @param x1 the x coordinate of point 1
     * @param y1 the y coordinate of point 1
     * @param x2 the x coordinate of point 2
     * @param y2 the y coordinate of point 2
     * @return the distance between the two points
     */
    public static double length(double x1, double y1, double x2, double y2) {
        return sqrt(length2(x1, y1, x2, y2));
    }

    /**
     * Gets the distance between to points
     *
     * @param p1 point 1
     * @param p2 point 2
     * @return the distance between the two points
     */
    public static double length(Point2D p1, Point2D p2) {
        return sqrt(length2(p1.getX(), p1.getY(), p2.getX(), p2.getY()));
    }

    /**
     * Gets the square distance between two points.
     *
     * @param x1 the x coordinate of point 1
     * @param y1 the y coordinate of point 1
     * @param x2 the x coordinate of point 2
     * @param y2 the y coordinate of point 2
     * @return the square distance between the two points
     */
    public static double length2(double x1, double y1, double x2, double y2) {
        return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
    }

    /**
     * Tests if a point is on a line.
     *
     * @param x1 the x coordinate of point 1 on the line
     * @param y1 the y coordinate of point 1 on the line
     * @param x2 the x coordinate of point 2 on the line
     * @param y2 the y coordinate of point 2 on the line
     * @param px the x coordinate of the point
     * @param py the y coordinate of the point
     * @param tolerance the maximal distance that the point may stray from the
     * line
     * @return true if the line contains the point within the given tolerance
     */
    public static boolean lineContainsPoint(double x1, double y1,
            double x2, double y2,
            double px, double py, double tolerance) {
        Rectangle2D r = new Rectangle2D(x1, y1, 0, 0);
        r = Geom.add(r, x2, y2);
        double grow = max(2, (int) ceil(tolerance));
        r = new Rectangle2D(r.getMinX() - grow,
                r.getMinY() - grow,
                r.getWidth() + grow * 2,
                r.getHeight() + grow * 2);
        if (!r.contains(px, py)) {
            return false;
        }

        double a, b, x, y;

        if (x1 == x2) {
            return (abs(px - x1) <= tolerance);
        }
        if (y1 == y2) {
            return (abs(py - y1) <= tolerance);
        }

        a = (y1 - y2) / (x1 - x2);
        b = y1 - a * x1;
        x = (py - b) / a;
        y = a * px + b;

        return (min(abs(x - px), abs(y - py)) <= tolerance);
    }

    public static Point2D north(Rectangle2D r) {
        return new Point2D(r.getMinX() + r.getWidth() / 2, r.getMinY());
    }

    /**
     * This method computes a binary OR of the appropriate mask values
     * indicating, for each side of Rectangle2D r1, whether or not the
     * Rectangle2D r2 is on the same side of the edge as the rest of this
     * Rectangle2D.
     *
     * @param r1 rectangle 1
     * @param r2 rectangle 2
     * @return the logical OR of all appropriate out codes OUT_RIGHT, OUT_LEFT,
     * OUT_BOTTOM, OUT_TOP.
     */
    public static int outcode(Rectangle2D r1, Rectangle2D r2) {
        int outcode = 0;

        if (r2.getMinX() > r1.getMinX() + r1.getWidth()) {
            outcode = OUT_RIGHT;
        } else if (r2.getMinX() + r2.getWidth() < r1.getMinX()) {
            outcode = OUT_LEFT;
        }

        if (r2.getMinY() > r1.getMinY() + r1.getHeight()) {
            outcode |= OUT_BOTTOM;
        } else if (r2.getMinY() + r2.getHeight() < r1.getMinY()) {
            outcode |= OUT_TOP;
        }

        return outcode;
    }

    /**
     * Gets the point on an oval that corresponds to the given angle.
     *
     * @param r the bounds of the oval
     * @param angle the angle
     * @return a point on the oval
     */
    public static Point2D ovalAngleToPoint(Rectangle2D r, double angle) {
        Point2D center = Geom.center(r);
        Point2D p = Geom.polarToPoint(angle, r.getWidth() / 2.0, r.getHeight() / 2.0);
        return new Point2D(center.getX() + p.getX(), center.getY() + p.getY());
    }

    /**
     * Gets a unit vector which is perpendicular to the given line.
     *
     * @param l1x point 1 on the line
     * @param l1y point 1 on the line
     * @param l2x point 2 on the line
     * @param l2y point 2 on the line
     * @return the perpendicular vector of length {@code 1}
     */
    public static Point2D perp(double l1x, double l1y, double l2x, double l2y) {
        // matlab: v    = p2 - p1
        //         cv    = cross([v 1] * [0 0 1])
        //         m     = distance/norm(cv);
        //         result = cv*m
        double vx = l2x - l1x;
        double vy = l2y - l1y;

        double cvx = -vy;
        double cvy = vx;
        double norm = sqrt(cvx * cvx + cvy * cvy);
        double m = norm == 0 ? 0 : 1 / norm;

        return new Point2D(cvx * m, cvy * m);
    }

    /**
     * Gets a unit vector which is perpendicular to the given tangent vector.
     *
     * @param tangent tangent vector
     * @return the perpendicular vector of length {@code 1}
     */
    public static Point2D perp(Point2D tangent) {
        return new Point2D(tangent.getY(), -tangent.getX());
    }

    /**
     * Gets a vector which is perpendicular to the given line.
     *
     * @param l1x point 1 on the line
     * @param l1y point 1 on the line
     * @param l2x point 2 on the line
     * @param l2y point 2 on the line
     * @param length the desired length of the vector
     * @return the perpendicular vector of length {@code vectorLength}
     */
    public static Point2D perp(double l1x, double l1y, double l2x, double l2y, double length) {
        return perp(l1x, l1y, l2x, l2y).multiply(length);
    }

    /**
     * Gets the angle of a point relative to a rectangle.
     *
     * @param r the rectangle
     * @param p the point
     * @return the angle
     */
    public static double pointToAngle(Rectangle2D r, Point2D p) {
        double px = p.getX() - (r.getMinX() + r.getWidth() * 0.5);
        double py = p.getY() - (r.getMinY() + r.getHeight() * 0.5);
        return atan2(py * r.getWidth(), px * r.getHeight());
    }

    /**
     * Converts a polar to a point
     *
     * @param angle the angle of the point in polar coordinates
     * @param fx the x coordinate of the point in polar coordinates
     * @param fy the y coordinate of the point in polar coordinates
     * @return the point in Cartesian coordinates
     */
    public static Point2D polarToPoint(double angle, double fx, double fy) {
        double si = sin(angle);
        double co = cos(angle);
        return new Point2D((int) (fx * co), (int) (fy * si));
    }

    /**
     * Shifts the point perpendicular to the line by the given distance
     *
     * @param l1 point 1 on the line
     * @param l2 point 2 on the line
     * @param p the point to be shifted
     * @param distance the shifting distance
     * @return the shifted point
     */
    public static Point2D shiftPerp(Point2D l1, Point2D l2, Point2D p, double distance) {

        return shiftPerp(l1.getX(), l1.getY(), l2.getX(), l2.getY(), p.getX(), p.getY(), distance);
    }

    /**
     * Shifts the point perpendicular to the line by the given distance
     *
     * @param l1x point 1 on the line
     * @param l1y point 1 on the line
     * @param l2x point 2 on the line
     * @param l2y point 2 on the line
     * @param px the point to be shifted
     * @param py the point to be shifted
     * @param distance the shifting distance
     * @return the shifted point
     */
    public static Point2D shiftPerp(double l1x, double l1y, double l2x, double l2y, double px, double py, double distance) {
        // matlab: v    = p2 - p1
        //         line = cross([v 1] * [0 0 1])
        //         m     = distance/norm(cv);
        //         result = p+cv*m
        double vx = l2x - l1x;
        double vy = l2y - l1y;

        double perpX = -vy;
        double perpY = vx;
        double m = distance / sqrt(perpX * perpY);

        return new Point2D(px + perpX * m, py + perpY * m);
    }

    public static Point2D south(Rectangle2D r) {
        return new Point2D(r.getMinX() + r.getWidth() * 0.2, r.getMinY()
                + r.getHeight());
    }

    public static double squaredDistance(Point2D p, double x, double y) {
        double a = p.getX() - x;
        double b = p.getY() - y;
        return a * a + b * b;
    }

    public static double squaredDistance(double x1, double y1, double x2, double y2) {
        double a = x1 - x2;
        double b = x2 - y2;
        return a * a + b * b;
    }

    public static Transform toDeltaTransform(Transform t) {
        Transform d = new Affine(t.getMxx(), t.getMxy(), 0.0,
                t.getMyx(), t.getMyy(), 0.0);
        return d;
    }

    public static String toString(Bounds b) {
        return b.getMinX() + "," + b.getMinY() + "," + b.getWidth() + "," + b.getHeight();
    }

    public static Bounds union(Bounds a, Bounds b) {
        double minx = Math.min(a.getMinX(), b.getMinX());
        double miny = Math.min(a.getMinY(), b.getMinY());
        double maxx = Math.max(a.getMaxX(), b.getMaxX());
        double maxy = Math.max(a.getMaxY(), b.getMaxY());
        return new BoundingBox(minx, miny, maxx - minx, maxy - miny);
    }

    public static Point2D west(Rectangle2D r) {
        return new Point2D(r.getMinX(), r.getMinY() + r.getHeight() / 2);
    }

    /**
     * Computes the linear interpolation/extrapolation between two points.
     *
     * @param a point a
     * @param b point b
     * @param t a value between [0, 1] defines the interpolation between a and
     * b. Values outside this range yield an extrapolation.
     * @return the interpolated or extrapolated value
     */
    public static Point2D lerp(Point2D a, Point2D b, double t) {
        return lerp(a.getX(), a.getY(), b.getX(), b.getY(), t);
    }

    public static Point2D lerp(double x1, double y1, double x2, double y2, double t) {
        return new Point2D(x1 + (x2 - x1) * t,
                y1 + (y2 - y1) * t);
    }

    /**
     * Given a point p on a line, computes t.
     *
     * @param px point
     * @param py point
     * @param x1 start of line
     * @param y1 start of line
     * @param x2 end of line
     * @param y2 end of line
     * @return t
     */
    public static double pointOnLine(double px, double py, double x1, double y1, double x2, double y2) {
        double w = x2 - x1;
        double h = y2 - y1;
        if (Math.abs(w) > Math.abs(h)) {
            return (px - x1) / w;
        } else {
            return (py - y1) / h;
        }
    }
}
