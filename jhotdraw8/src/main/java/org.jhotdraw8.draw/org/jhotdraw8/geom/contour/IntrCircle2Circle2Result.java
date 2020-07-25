package org.jhotdraw8.geom.contour;


import java.awt.geom.Point2D;

/**
 * This is a struct.
 */
public class IntrCircle2Circle2Result {
    // type of intersect
    public Circle2Circle2IntrType intrType;
    // first intersect point if intrType is OneIntersect or TwoIntersects, undefined otherwise
    public Point2D.Double point1;
    // second intersect point if intrType is TwoIntersects, undefined otherwise
    public Point2D.Double point2;

    @Override
    public String toString() {
        return "IntrCircle2Circle2Result{" +
                "intrType=" + intrType +
                ", point1=" + point1 +
                ", point2=" + point2 +
                '}';
    }
}
