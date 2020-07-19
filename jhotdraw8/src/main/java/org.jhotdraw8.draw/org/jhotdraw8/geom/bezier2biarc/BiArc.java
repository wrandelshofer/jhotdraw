package org.jhotdraw8.geom.bezier2biarc;

import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Points2D;

import java.awt.geom.Point2D;


public class BiArc {
    public final Arc a1;
    public final Arc a2;

    /**
     * Creates a new instance.
     *
     * @param p1 Start point
     * @param t1 Tangent vector at P1
     * @param p2 End point
     * @param t2 Tangent vector at P2
     * @param tp Transition point
     */
    public BiArc(Point2D.Double p1, Point2D.Double t1, Point2D.Double p2, Point2D.Double t2, Point2D.Double tp) {
        // Calculate the orientation
        // https://en.wikipedia.org/wiki/Curve_orientation

        var sum = 0d;
        sum += (tp.getX() - p1.getX()) * (tp.getY() + p1.getY());
        sum += (p2.getX() - tp.getX()) * (p2.getY() + tp.getY());
        sum += (p1.getX() - p2.getX()) * (p1.getY() + p2.getY());
        var cw = sum < 0;

        // Calculate perpendicular lines to the tangent at P1 and P2
        var tl1 = Line.createPerpendicularAt(p1, Points2D.add(p1,t1));
        var tl2 = Line.createPerpendicularAt(p2, Points2D.add(p2,t2));

        // Calculate the perpendicular bisector of P1T and P2T
        var P1T2 = Points2D.multiply((Points2D.add(p1,tp)),0.5);
        var pbP1T = Line.createPerpendicularAt(P1T2, tp);

        var P2T2 = Points2D.multiply((Points2D.add(p2,tp)),0.5);
        var pbP2T = Line.createPerpendicularAt(P2T2, tp);

        // The origo of the circles are at the intersection points
        var C1 = tl1.Intersection(pbP1T);
        var C2 = tl2.Intersection(pbP2T);

        // Calculate the radii
        var r1 = Points2D.magnitude((Points2D.subtract(C1,p1)));
        var r2 = Points2D.magnitude((Points2D.subtract(C2,p2)));

        // Calculate start and sweep angles
        var startVector1 = Points2D.subtract(p1,C1);
        var endVector1 = Points2D.subtract(tp,C1);
        var startAngle1 = Geom.almostZero(startVector1)
                ? 0.0 : Geom.atan2(startVector1.getY(), startVector1.getX());
        var sweepAngle1 = Geom.almostZero(endVector1)
                ? 0.0 : Geom.atan2(endVector1.getY(), endVector1.getX()) - startAngle1;

        var startVector2 = Points2D.subtract(tp,C2);
        var endVector2 = Points2D.subtract(p2,C2);
        var startAngle2 = Geom.atan2(startVector2.getY(), startVector2.getX());
        var sweepAngle2 = Geom.atan2(endVector2.getY(), endVector2.getX()) - startAngle2;

        // Adjust angles according to the orientation of the curve
        if (cw && sweepAngle1 < 0) sweepAngle1 = 2 * Math.PI + sweepAngle1;
        if (!cw && sweepAngle1 > 0) sweepAngle1 = sweepAngle1 - 2 * Math.PI;
        if (cw && sweepAngle2 < 0) sweepAngle2 = 2 * Math.PI + sweepAngle2;
        if (!cw && sweepAngle2 > 0) sweepAngle2 = sweepAngle2 - 2 * Math.PI;

        a1 = new Arc(C1, r1, startAngle1, sweepAngle1, p1, tp);
        a2 = new Arc(C2, r2, startAngle2, sweepAngle2, tp, p2);
    }

    /**
     * Implements the parametric equation.
     *
     * @param t Parameter of the curve. Must be in [0,1]
     * @return the point at t
     */
    public Point2D.Double pointAt(double t) {
        var s = a1.length() / (a1.length() + a2.length());

        if (t <= s) {
            return a1.pointAt(t / s);
        } else {
            return a2.pointAt((t - s) / (1 - s));
        }
    }

    public double length() {
        return a1.length() + a2.length();
    }
}