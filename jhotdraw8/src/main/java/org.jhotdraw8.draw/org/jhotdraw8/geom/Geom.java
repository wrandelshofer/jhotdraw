/*
 * @(#)Geom.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.function.Double2Consumer;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;

/**
 * Some geometric utilities.
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
    /**
     * Absolute threshold to be used for comparing reals generally.
     */
    public static final double REAL_THRESHOLD = 1e-8;

    /**
     * Don't let anyone instantiate this class.
     */
    private Geom() {
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
        double dy = y2 - y1;
        double dx = x2 - x1;
        return atan2(dy, dx);
    }

    /**
     * Computes atan2 if dy and dx are large enough.
     * <p>
     * Math.atan2 can go into an infinite loop if dy and dx are almost zero.
     *
     * @param dy the dy
     * @param dx the dx
     * @return atan2 of dy, dx or 0.
     */
    public static double atan2(double dy, double dx) {
        return almostZero(dy) && almostZero(dx) ? 0.0 : Math.atan2(dy, dx);
    }

    /**
     * Signed difference of two angles.
     *
     * @param from angle 0
     * @param to   angle 1
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
     * Signed shortest distance between two angles.
     *
     * @param from angle 0
     * @param to   angle 1
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
     * @param to   angle 1
     * @return 0 &lt;= diff &lt;= PI.
     */
    public static double anglesUnsignedSpan(double from, double to) {
        return from > to ? from - to : to - from;
    }

    /**
     * Returns true if the three points are collinear.
     *
     * @param a x-coordinate of point 0
     * @param b y-coordinate of point 0
     * @param m x-coordinate of point 1
     * @param n y-coordinate of point 1
     * @param x x-coordinate of point 2
     * @param y y-coordinate of point 2
     * @return true if collinear
     */
    public static boolean isCollinear(double a, double b, double m, double n, double x, double y) {
        return abs(a * (n - y) + m * (y - b) + x * (b - n)) < 1e-6;
    }

    /**
     * Clamps a value to the given range.
     *
     * @param value the value
     * @param min   the lower bound of the range
     * @param max   the upper bound of the range
     * @return the constrained value
     */
    public static double clamp(double value, double min, double max) {
        if (Double.isNaN(value) || value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * Clamps a value to the given range.
     *
     * @param value the value
     * @param min   the lower bound of the range
     * @param max   the upper bound of the range
     * @return the constrained value
     */
    public static float clamp(float value, float min, float max) {
        if (Float.isNaN(value) || value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * Clamps a value to the given range.
     *
     * @param value the value
     * @param min   the lower bound of the range
     * @param max   the upper bound of the range
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
     * @param rx        the bounds x-coordinate
     * @param ry        the bounds y-coordinate
     * @param rw        the bounds width
     * @param rh        the bounds height
     * @param x         the x-coordinate of the point
     * @param y         the y-coordinate of the point
     * @param tolerance the tolerance
     * @return true if inside
     */
    public static boolean contains(@NonNull double rx, double ry, double rw, double rh, double x, double y, double tolerance) {
        return rx - tolerance <= x && x <= (rx + rw) + tolerance
                && ry - tolerance <= y && y <= (ry + rh) + tolerance;
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
    public static boolean containsAWT(@NonNull java.awt.geom.Rectangle2D r1, @NonNull java.awt.geom.Rectangle2D r2) {
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

    /**
     * Linear interpolation from {@code a} to {@code b} at {@code t}.
     *
     * @param a a
     * @param b b
     * @param t a value in the range [0, 1]
     * @return the interpolated value
     */
    public static double lerp(double a, double b, double t) {
        return (b - a) * t + a;
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
        return sqrt(lengthSquared(x1, y1, x2, y2));
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
    public static double lengthSquared(double x1, double y1, double x2, double y2) {
        return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
    }

    /**
     * Gets the perpendicular vector to the given vector.
     *
     * @param x the x value of the vector
     * @param y the x value of the vector
     * @return the perpendicular vector
     */
    public static @NonNull java.awt.geom.Point2D.Double perp(double x, double y) {
        return new java.awt.geom.Point2D.Double(y, -x);
    }

    public static double distanceSq(double x1, double y1, double x2, double y2) {
        double Δx = x1 - x2;
        double Δy = y1 - y2;
        return Δx * Δx + Δy * Δy;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double Δx = x1 - x2;
        double Δy = y1 - y2;
        return sqrt(Δx * Δx + Δy * Δy);
    }


    public static @NonNull java.awt.geom.Point2D.Double lerp(double x0, double y0, double x1, double y1, double t) {
        return new java.awt.geom.Point2D.Double(x0 + (x1 - x0) * t, y0 + (y1 - y0) * t);
    }

    /**
     * Splits the provided line into two parts.
     *
     * @param x0          point 1 of the line
     * @param y0          point 1 of the line
     * @param x1          point 2 of the line
     * @param y1          point 2 of the line
     * @param t           where to split
     * @param leftLineTo  if not null, accepts the curve from x1,y1 to t1
     * @param rightLineTo if not null, accepts the curve from t1 to x2,y2
     */
    public static void splitLine(double x0, double y0, double x1, double y1, double t,
                                 @Nullable Double2Consumer leftLineTo,
                                 @Nullable Double2Consumer rightLineTo) {
        final double x12 = (x1 - x0) * t + x0;
        final double y12 = (y1 - y0) * t + y0;

        if (leftLineTo != null) {
            leftLineTo.accept(x12, y12);
        }
        if (rightLineTo != null) {
            rightLineTo.accept(x1, y1);
        }
    }

    public static boolean almostEqual(java.awt.geom.Point2D v1, java.awt.geom.Point2D v2) {
        return almostEqual(v1, v2, REAL_THRESHOLD);
    }

    public static boolean almostEqual(java.awt.geom.Point2D v1, java.awt.geom.Point2D v2, double epsilon) {
        return v1.distanceSq(v2) < epsilon * epsilon;
    }

    public static boolean almostEqual(double x0, double y0, double x1, double y1) {
        return almostEqual(x0, y0, x1, y1, REAL_THRESHOLD);
    }

    public static boolean almostEqual(double x0, double y0, double x1, double y1, double epsilon) {
        return distanceSq(x0, y0, x1, y1) < epsilon * epsilon;
    }

    public static boolean almostZero(java.awt.geom.Point2D.Double v) {
        return almostZero(v, REAL_THRESHOLD);
    }

    public static boolean almostZero(java.awt.geom.Point2D.Double v, double epsilon) {
        return Points2D.magnitudeSq(v) < epsilon * epsilon;
    }

    public static boolean almostEqual(double a, double b) {
        return almostEqual(a, b, REAL_THRESHOLD);
    }

    public static boolean almostEqual(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }


    public static boolean almostZero(double a) {
        return almostZero(a, REAL_THRESHOLD);
    }

    public static boolean almostZero(double a, double epsilon) {
        return Math.abs(a) < epsilon;
    }


    /**
     * Computes the linear interpolation/extrapolation between two points.
     *
     * @param start point a
     * @param end   point b
     * @param t     a value between [0, 1] defines the interpolation between a and
     *              b. Values outside this range yield an extrapolation.
     * @return the interpolated or extrapolated value
     */
    public static @NonNull java.awt.geom.Point2D.Double lerp(@NonNull java.awt.geom.Point2D start, @NonNull java.awt.geom.Point2D end, double t) {
        return Geom.lerp(start.getX(), start.getY(), end.getX(), end.getY(), t);
    }

    public static double atan2Ellipse(double cx, double cy, double rx, double ry, double x, double y) {
        return atan2(y, x);//FIXME implement me
    }



    /**
     * Returns the trigonometric sine of an angle in degrees.
     * <p>
     * References:
     * <dl>
     *     <dt>Values of Trigonometric ratios for 0, 30, 45, 60 and 90 degrees</dt>
     *     <dd><a href="https://mathinstructor.net/2012/08/values-of-trigonometric-ratios-for-0-30-45-60-and-90-degrees/">mathinstructor.net</a></dd>
     * </dl>
     *
     * @param aDeg an angle in degrees
     * @return the sine of the argument
     */
    public static double sinDegrees(double aDeg) {
        int aDegInt = (int) aDeg;
        if (aDeg == aDegInt) {
            switch (aDegInt % 360) {
                case 0:
                    return 0.0;// = sqrt(0/4)
                case 30:
                case 150:
                case -210:
                case -330:
                    return 0.5;// = sqrt(1/4)
                case -30:
                case -150:
                case 210:
                case 330:
                    return -0.5;// = sqrt(1/4)
                case 45:
                case 135:
                case -315:
                case -225:
                    return Math.sqrt(0.5);// = sqrt(2/4)
                case -45:
                case -135:
                case 315:
                case 225:
                    return -Math.sqrt(0.5);// = sqrt(2/4)
                case 60:
                case 120:
                case -300:
                case -240:
                    return Math.sqrt(0.75);// = sqrt(3/4)
                case -60:
                case -120:
                case 300:
                case 240:
                    return -Math.sqrt(0.75);// = sqrt(3/4)
                case 90:
                case -270:
                    return 1;// = sqrt(4/4)
                case -90:
                case 270:
                    return -1;// = sqrt(4/4)

            }
        }
        return Math.sin(Math.toRadians(aDeg));
    }

    /**
     * Returns the trigonometric cosine of an angle in degrees.
     * <p>
     * References:
     * <dl>
     *     <dt>Values of Trigonometric ratios for 0, 30, 45, 60 and 90 degrees</dt>
     *     <dd><a href="https://mathinstructor.net/2012/08/values-of-trigonometric-ratios-for-0-30-45-60-and-90-degrees/">mathinstructor.net</a></dd>
     * </dl>
     *
     * @param aDeg an angle in degrees
     * @return the cosine of the argument
     */
    public static double cosDegrees(double aDeg) {
        return sinDegrees(aDeg + 90);
    }

    /**
     * Returns {@code a * b + c}.
     * <p>
     * This method is here for backwards-compatibility with Java SE 8 only.
     * Once we are on Java SE 11 or above, this method should be replaced
     * with {@link Math#fma}.
     *
     * @param a a value
     * @param b a value
     * @param c a value
     * @return a * b + c
     */
    public static double fma(double a, double b, double c) {
        return a *b+c;
    }
}