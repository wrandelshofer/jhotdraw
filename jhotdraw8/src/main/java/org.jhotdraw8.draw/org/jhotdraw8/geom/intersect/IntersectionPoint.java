package org.jhotdraw8.geom.intersect;

import java.awt.geom.Point2D;

/**
 * Provides the coordinates and the argument value of one intersecting
 * function at an intersection point.
 * <p>
 * This class extends Point2D.Double rather than aggregating it to reduce
 * pointer chasing.
 */
public class IntersectionPoint extends Point2D.Double {
    private final double argument;

    public IntersectionPoint(double x, double y, double argument) {
        super(x, y);
        this.argument = argument;
    }

    public IntersectionPoint(Point2D p, double argument) {
        super(p.getX(), p.getY());
        this.argument = argument;
    }

    public double getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        return "IntersectionPoint{" +
                "x=" + x +
                ", y=" + y +
                ", a=" + argument +
                '}';
    }
}
