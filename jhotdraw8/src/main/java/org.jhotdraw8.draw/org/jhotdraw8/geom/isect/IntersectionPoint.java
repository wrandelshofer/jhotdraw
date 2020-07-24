package org.jhotdraw8.geom.isect;

import java.awt.geom.Point2D;

/**
 * Describes an intersection point of two parametric functions 'a' and 'b'.
 */
public class IntersectionPoint {
    /**
     * @see #getParameterA()
     */
    private final double parameterA;

    /**
     * @see #getParameterB()
     */
    private final double parameterB;

    /**
     * @see #getPoint()
     */
    private final Point2D.Double point;

    /**
     * @see #getTangentA()
     */
    private final Point2D.Double tangentA;

    /**
     * @see #getTangentB()
     */
    private final Point2D.Double tangentB;

    /**
     * @see #getSegmentA()
     */
    private final int segmentA;

    /**
     * @see #getSegmentB()
     */
    private final int segmentB;

    public IntersectionPoint(Point2D.Double point, double parameterA) {
        this(point, parameterA, new Point2D.Double(1, 0), 0, new Point2D.Double(0, -1));
    }

    public IntersectionPoint(Point2D.Double point, double parameterA, Point2D.Double tangentA, double parameterB, Point2D.Double tangentB) {
        this(point, parameterA, tangentA, 0, parameterB, tangentB, 0);
    }

    public IntersectionPoint(double px, double py, double parameterA, double tangentAX, double tangentAY, double parameterB, double tangentBX, double tangentBY) {
        this(px, py, parameterA, tangentAX, tangentAY, 0, parameterB, tangentBX, tangentBY, 0);
    }

    public IntersectionPoint(Point2D.Double point, double parameterA, Point2D.Double tangentA, int segmentA, double parameterB, Point2D.Double tangentB, int segmentB) {
        this.point = point;
        this.parameterA = parameterA;
        this.tangentA = tangentA;
        this.parameterB = parameterB;
        this.tangentB = tangentB;
        this.segmentA = segmentA;
        this.segmentB = segmentB;
    }

    public IntersectionPoint(double px, double py, double parameterA, double tx1, double ty1, int segmentA, double t2, double tx2, double ty2, int segment2) {
        this.point = new Point2D.Double(px, py);
        this.parameterA = parameterA;
        this.tangentA = new Point2D.Double(tx1, ty1);
        this.parameterB = t2;
        this.tangentB = new Point2D.Double(tx2, ty2);
        this.segmentA = segmentA;
        this.segmentB = segment2;
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
     * The value of the argument of the parametric function 'a' at the intersection.
     */
    public double getParameterA() {
        return parameterA;
    }

    /**
     * The point of intersection.
     */
    public Point2D.Double getPoint() {
        return point;
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
    public double getParameterB() {
        return parameterB;
    }

    /**
     * The tangent vector at the intersection of the second parametric function.
     * This vector is not normalized.
     */
    public Point2D.Double getTangentB() {
        return tangentB;
    }

    public IntersectionPoint withSegment2(int segmentIndex) {
        return new IntersectionPoint(this.point, this.parameterA, this.tangentA, this.segmentA, this.parameterB, this.tangentB, segmentIndex);
    }

    @Override
    public String toString() {
        return "IntersectionPoint{" +
                "t1=" + parameterA +
                ", t2=" + parameterB +
                ", point=" + point +
                ", tangent1=" + tangentA +
                ", tangent2=" + tangentB +
                ", segment1=" + segmentA +
                ", segment2=" + segmentB +
                '}';
    }
}
