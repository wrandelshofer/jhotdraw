/*
 * @(#)IntersectionPointEx.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import java.awt.geom.Point2D;

/**
 * Describes an intersection point of two parametric functions 'a' and 'b'.
 */
public class IntersectionPointEx extends IntersectionPoint {
    private final static long serialVersionUID = 0L;

    /**
     * @see #getArgumentB()
     */
    private final double argumentB;


    /**
     * @see #getTangentA()
     */
    private final Point2D.Double tangentA;

    /**
     * @see #getTangentB()
     */
    private final Point2D.Double tangentB;

    /**
     * @see #getSegmentB()
     */
    private final int segmentB;

    public IntersectionPointEx(Point2D.Double point, double argumentA, Point2D.Double tangentA, double argumentB, Point2D.Double tangentB) {
        this(point, argumentA, tangentA, 0, argumentB, tangentB, 0);
    }

    public IntersectionPointEx(double px, double py, double argumentA, double tangentAX, double tangentAY, double argumentB, double tangentBX, double tangentBY) {
        this(px, py, argumentA, tangentAX, tangentAY, 0, argumentB, tangentBX, tangentBY, 0);
    }

    public IntersectionPointEx(Point2D.Double point, double argumentA, Point2D.Double tangentA, int segmentA, double argumentB, Point2D.Double tangentB, int segmentB) {
        super(point.getX(), point.getY(), argumentA, segmentA);
        this.tangentA = tangentA;
        this.argumentB = argumentB;
        this.tangentB = tangentB;
        this.segmentB = segmentB;
    }

    public IntersectionPointEx(double px, double py, double argumentA, double tx1, double ty1, int segmentA, double t2, double tx2, double ty2, int segment2) {
        super(px, py, argumentA, segmentA);
        this.tangentA = new Point2D.Double(tx1, ty1);
        this.argumentB = t2;
        this.tangentB = new Point2D.Double(tx2, ty2);
        this.segmentB = segment2;
    }


    /**
     * If parametric function 'b' is a segment of a segmented function,
     * then this field is used to indicate to which segment the parametric
     * function belongs.
     * <p>
     * The index of the segment.
     */
    public int getSegmentB() {
        return segmentB;
    }


    /**
     * The tangent vector at the intersection of the parametric function 'a'.
     * This vector is not normalized.
     */
    public Point2D.Double getTangentA() {
        return tangentA;
    }

    /**
     * The value of the argument of the parametric function 'b' at the intersection.
     */
    public double getArgumentB() {
        return argumentB;
    }

    /**
     * The tangent vector at the intersection of the second parametric function.
     * This vector is not normalized.
     */
    public Point2D.Double getTangentB() {
        return tangentB;
    }

    public IntersectionPointEx withSegment2(int segmentIndex) {
        return new IntersectionPointEx(this, this.argumentA, this.tangentA, this.segmentA, this.argumentB, this.tangentB, segmentIndex);
    }

    @Override
    public String toString() {
        return "IntersectionPoint{" +
                "t1=" + argumentA +
                ", t2=" + argumentB +
                ", point=" + getX() + ", " + getY() +
                ", tangent1=" + tangentA +
                ", tangent2=" + tangentB +
                ", segment1=" + segmentA +
                ", segment2=" + segmentB +
                '}';
    }

}
