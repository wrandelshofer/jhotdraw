package org.jhotdraw8.geom.biarc;


import java.awt.geom.Point2D;

/**
 * Definition of an Arc. It contains redundant information.
 */
public class Arc {

    /**
     * Center point.
     */
    public final Point2D.Double c;
    /**
     * Radius.
     */
    public final double r;
    /**
     * Start angle in radian.
     */
    public final double startAngle;
    /**
     * Sweep angle in radian.
     */
    public final double sweepAngle;
    /**
     * Start point of the arc.
     */
    public final Point2D.Double p1;
    /**
     * End point of the arc.
     */
    public final Point2D.Double p2;

    public Arc(Point2D.Double c, double r, double startAngle, double sweepAngle, Point2D.Double p1, Point2D.Double p2) {
        this.c = c;
        this.r = r;
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
        this.p1 = p1;
        this.p2 = p2;
    }

    /**
     * Orientation of the arc.
     */
    public boolean isClockwise() {
        return sweepAngle > 0;
    }

    /**
     * Implements the parametric equation.
     *
     * @param t Parameter of the curve. Must be in [0,1]
     * @return the point at t
     */
    public Point2D.Double pointAt(double t) {
        double x = c.getX() + r * Math.cos(startAngle + t * sweepAngle);
        double y = c.getY() + r * Math.sin(startAngle + t * sweepAngle);
        return new Point2D.Double(x, y);
    }

    public double length() {
        return r * Math.abs(sweepAngle);
    }
}