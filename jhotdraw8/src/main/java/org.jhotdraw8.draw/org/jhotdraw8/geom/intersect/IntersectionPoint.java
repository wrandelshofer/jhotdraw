/*
 * @(#)IntersectionPoint.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import java.awt.geom.Point2D;

/**
 * Provides the coordinates and the argument value of one intersecting
 * function at an intersection point.
 * <p>
 * This class extends Point2D.Double rather than aggregating it to reduce
 * pointer chasing. As a consequence, IntersectionPoint only uses the x and y
 * coordinates for equals and hashCode.
 */
public class IntersectionPoint extends Point2D.Double {
    private static final long serialVersionUID = 0L;
    protected final double argumentA;
    protected final int segmentA;

    public IntersectionPoint(double x, double y, double argumentA) {
        this(x, y, argumentA, 0);
    }

    public IntersectionPoint(double x, double y, double argumentA, int segmentA) {
        super(x, y);
        this.argumentA = argumentA;
        this.segmentA = segmentA;
    }


    public IntersectionPoint(Point2D p, double argumentA) {
        this(p.getX(), p.getY(), argumentA, 0);
    }

    public IntersectionPoint(Point2D p, double argumentA, int segmentA) {
        this(p.getX(), p.getY(), argumentA, segmentA);
    }

    public double getArgumentA() {
        return argumentA;
    }

    /**
     * If parametric function 'a' is a segment of a segmented function,
     * then this field is used to indicate to which segment the parametric
     * function belongs.
     * <p>
     * The index of the segment.
     */
    public int getSegmentA() {
        return segmentA;
    }

    @Override
    public String toString() {
        return "IntersectionPoint{" +
                "x=" + x +
                ", y=" + y +
                ", a=" + argumentA +
                '}';
    }
}
