package org.jhotdraw8.geom.isect;

import java.awt.geom.Point2D;

/**
 * Describes an intersection point of two parametric functions.
 */
public class IntersectionPoint {
    /**
     * @see #getT1()
     */
    private final double t1;

    /**
     * @see #getT2()
     */
    private final double t2;

    /**
     * @see #getPoint()
     */
    private final Point2D.Double point;

    /**
     * @see #getTangent1()
     */
    private final Point2D.Double tangent1;

    /**
     * @see #getTangent2()
     */
    private final Point2D.Double tangent2;

    /**
     * @see #getSegment1()
     */
    private final int segment1;

    /**
     * @see #getSegment2()
     */
    private final int segment2;

    public IntersectionPoint(Point2D.Double point, double t1) {
        this(point, t1, new Point2D.Double(1, 0), 0, new Point2D.Double(0, -1));
    }

    public IntersectionPoint(Point2D.Double point, double t1, Point2D.Double tangent1, double t2, Point2D.Double tangent2) {
        this(point, t1, tangent1, 0, t2, tangent2, 0);
    }

    public IntersectionPoint(double px, double py, double t1, double tx1, double ty1, double t2, double tx2, double ty2) {
        this(px, py, t1, tx1, ty1, 0, t2, tx2, ty2, 0);
    }

    public IntersectionPoint(Point2D.Double point, double t1, Point2D.Double tangent1, int segment1, double t2, Point2D.Double tangent2, int segment2) {
        this.point = point;
        this.t1 = t1;
        this.tangent1 = tangent1;
        this.t2 = t2;
        this.tangent2 = tangent2;
        this.segment1 = segment1;
        this.segment2 = segment2;
    }

    public IntersectionPoint(double px, double py, double t1, double tx1, double ty1, int segment1, double t2, double tx2, double ty2, int segment2) {
        this.point = new Point2D.Double(px, py);
        this.t1 = t1;
        this.tangent1 = new Point2D.Double(tx1, ty1);
        this.t2 = t2;
        this.tangent2 = new Point2D.Double(tx2, ty2);
        this.segment1 = segment1;
        this.segment2 = segment2;
    }

    /**
     * If the first parametric function is a segment of a segmented function,
     * then this field is used to indicate to which segment the parametric
     * function belongs.
     * <p>
     * The index of the segment.
     */
    public int getSegment1() {
        return segment1;
    }

    /**
     * If the second parametric function is a segment of a segmented function,
     * then this field is used to indicate to which segment the parametric
     * function belongs.
     * <p>
     * The index of the segment.
     */
    public int getSegment2() {
        return segment2;
    }


    /**
     * The value of the argument 't' of the first parametric function at the intersection.
     */
    public double getT1() {
        return t1;
    }

    /**
     * The point of intersection.
     */
    public Point2D.Double getPoint() {
        return point;
    }

    /**
     * The tangent vector at the intersection of the first parametric function.
     * This vector is not normalized.
     */
    public Point2D.Double getTangent1() {
        return tangent1;
    }

    /**
     * The value of the argument 't' of the second parametric function at the intersection.
     */
    public double getT2() {
        return t2;
    }

    /**
     * The tangent vector at the intersection of the second parametric function.
     * This vector is not normalized.
     */
    public Point2D.Double getTangent2() {
        return tangent2;
    }

    public IntersectionPoint withSegment2(int segmentIndex) {
        return new IntersectionPoint(this.point, this.t1, this.tangent1, this.segment1, this.t2, this.tangent2, segmentIndex);
    }
}
