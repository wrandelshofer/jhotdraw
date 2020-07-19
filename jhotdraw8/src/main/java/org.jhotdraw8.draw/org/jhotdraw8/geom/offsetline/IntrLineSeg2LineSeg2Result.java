package org.jhotdraw8.geom.offsetline;

import java.awt.geom.Point2D;

/**
 * This is a struct.
 */
public class IntrLineSeg2LineSeg2Result {
    /**
     * Holds the type of intersect, if True or False then point holds the point that they intersect,
     * if True then t0 and t1 are undefined, if False then t0 is the parametric value of the first
     * segment and t1 is the parametric value of the second segment, if Coincident then point is
     * undefined and t0 holds the parametric value start of coincidence and t1 holds the parametric
     * value of the end of the coincidence for the second segment's equation.
     */
    LineSeg2LineSeg2IntrType intrType;
    double t0;
    double t1;
    Point2D.Double point;
}
