
package org.jhotdraw.draw.connector;


import static java.lang.Math.atan2;

import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;

import org.jhotdraw.geom.Geom;

/**
 * This class is a set of static methods to handle the geometry used in {@code
 * ConnectorSubTracker} and {@code ConnectorStrategy}.
 * <p>
 * The dependencies with other classes should be restricted to standard
 * java library classes and {@code jhotdraw.geom}.
 * <p>
 * A number of methods are declared final to ensure consistency in overriding;
 * removal of this declaration on any method will normally demand removal on
 * other methods. (<i> for example removal of final on {@code projectX} demands
 * removal on {@code project} and {@code projectY}. Otherwise, inconsistencies
 * in class loading may occur</i>
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *         <i> Code line length 120 </i>
 *         <p>
 */
public class ConnectorGeom {
    /**
     * epsilon = 0.000005
     */
    public final static double epsilon = 0.000001;

    /**
     * used to prevent vertex points
     *
     * e2 = 0.0001
     */
    public final static double e2 = 0.0001;

    /**
     * This table holds the angles needed to <i>rotate</i> boundary connections
     * between two squares or to <i>normalize rotate</i> boundary connections
     * between two rectangles so that the connections do not cross either
     * connected figure. <i>It is assumed that all connections are on one side
     * of each figure and that the rotation is performed on all connections
     * together about one of the figures.</i>
     * <p>
     * For rectangles r1, r2 the side(s) of r1 facing r2 are given by
     * <code>outcode(r1, r2)</code>.
     * <p>
     * From r2's perspective the opposite side(s) of r1 is/are
     * <code>outcode(r1, r2)</code>;
     * <p>
     * Using the above perspective, for any rectangle pair a two dimensional
     * (4x4) table is constructed relating sides to opposite sides. Each
     * table cell entry contains the angle needed to rotate connections so that
     * the connections between sides do not cross either rectangle.
     * <p>
     * In this table the rows(LEFT,TOP,RIGHT,BOTTOM) correspond to the opposite
     * side (on the stationary rectangle) and the columns(LEFT,TOP,RIGHT,BOTTOM)
     * correspond to the connected side (on the moving rectangle).
     * <p>
     * <i>
     * <p>
     * {{Math.PI, Math.PI/2, 0, -Math.PI/2},
     * <p>
     * {-Math.PI/2, Math.PI, Math.PI/2, 0},
     * <p>
     * {0, -Math.PI/2, Math.PI, Math.PI/2},
     * <p>
     * {Math.PI/2, 0, -Math.PI/2, Math.PI}} </i>
     * <p>
     */
    public static final double[][] rotationGroup =
    {
        {Math.PI,    Math.PI/2,  0,          -Math.PI/2},
        {-Math.PI/2, Math.PI,    Math.PI/2,  0},
        {0,          -Math.PI/2, Math.PI,    Math.PI/2},
        {Math.PI/2,  0,          -Math.PI/2, Math.PI}

    };


    /**
     * This table holds the angles needed to <i>normalize rotate</i> connections
     * from one side of a rectangle to another.
     * <p>
     * In this table the rows(LEFT,TOP,RIGHT,BOTTOM) correspond to the new
     * side and the columns(LEFT,TOP,RIGHT,BOTTOM) correspond to the connected
     * side.
     * <p>
     * <i>
     * <p>
     * {{0, -Math.PI/2, Math.PI, Math.PI/2},
     * <p>
     * {Math.PI/2, 0, -Math.PI/2, Math.PI},
     * <p>
     * {Math.PI, Math.PI/2, 0, -Math.PI/2},
     * <p>
     * {-Math.PI/2, Math.PI, Math.PI/2, 0}} </i>
     * <p>
     */
    public static final double[][] rotationGroup2 =
    {
        { 0,          -Math.PI/2,   Math.PI,     Math.PI/2},
        { Math.PI/2,   0,          -Math.PI/2,   Math.PI},
        { Math.PI,     Math.PI/2,   0,          -Math.PI/2},
        {-Math.PI/2,   Math.PI,     Math.PI/2,   0}
    };


    /**
     * mapping base outcode values (LEFT,TOP,RIGHT,BOTTOM) or (1,2,4,8) to index (0,1,2,3).
     * <p>
     * outcode cannot be more than 12 ... using 15 if some weird instance creates outcode of
     * BOTTOM|RIGHT|TOP|LEFT simultaneously
     * <p>
     * see {@link ConnectorGeom#calculateRotationAngleForSide}
     */
    public static final int[] baseOutcodeIndex = {-1, 0, 1,-1, 2,-1,-1,-1, 3,-1,-1,-1,-1,-1,-1,-1};


    /**
     * Gets the point on a rectangle that corresponds to the given angle. This
     * implementation is for strict geometric use; the angleToPoint methods in
     * {@link org.jhotdraw.geom.Geom#angleToPoint} are to be used with
     * 'Normalized' angles given by pointToAngle().
     * <p>
     * Note: angleToPoint = angleToPointGeom for squares
     *
     * @param r
     * @param alpha
     * @return Point2D.Double
     */
    public static Point2D.Double angleToPointGeom(Rectangle2D.Double r, double alpha) {
        if (r == null || r.width == 0 || r.height == 0)
            return null;

        if (Math.abs(r.width - r.height) < e2)
            return Geom.angleToPoint(r, alpha);

        // make sure alpha is in range -PI to PI
        while (alpha > Math.PI) {
            alpha = alpha - 2*Math.PI;
        }
        while (alpha < - Math.PI) {
            alpha = alpha + 2*Math.PI;
        }


        Point2D.Double result = new Point2D.Double(-Double.MAX_VALUE, -Double.MAX_VALUE);

        final double w2 = r.width/2;
        final double h2 = r.height/2;
        final double cX = r.x + w2;
        final double cY = r.y + h2;


        //Eliminate 0, PI, -PI/2, PI/2
        // alpha = 0 or alpha = +/- PI
        if (Math.abs(alpha) < e2 || Math.abs(alpha - Math.PI) < e2 || Math.abs(alpha + Math.PI) < e2) {
            result.y = cY;
            result.x = Math.abs(alpha) < e2 ? cX + w2 : cX - w2;
            return result;
        }
        // alpha = +/- PI/2
        if (Math.abs(alpha - Math.PI/2) < e2 || Math.abs(alpha + Math.PI/2) < e2) {
            result.x = cX;
            result.y = Math.abs(alpha - Math.PI/2) < e2 ? cY + h2 : cY - h2;
            return result;
        }

        //Eliminate vertex points
        final double chi = Math.atan2(r.height,r.width);

        // alpha = +/- chi
        if (Math.abs(alpha - chi) < e2 || Math.abs(alpha + chi) < e2) {
            result.x = cX + w2;
            result.y = Math.abs(alpha - chi) < e2 ? cY + h2 : cY - h2;
            return result;
        }
        // alpha = PI-chi /  -PI+chi
        if (Math.abs(alpha - Math.PI + chi) < e2 || Math.abs(alpha + Math.PI - chi) < e2) {
            result.x = cX - w2;
            result.y = Math.abs(alpha - Math.PI + chi) < e2 ? cY + h2 : cY - h2;
            return result;
        }



        if (-chi < alpha && chi > alpha)
            result.x = cX + w2;
        else
            if (chi < alpha && Math.PI -chi > alpha)
                result.y = cY + h2;
            else
                if (Math.PI-chi < alpha && alpha < Math.PI )
                    result.x = cX - w2;
                else
                    if (-Math.PI +chi > alpha && -Math.PI < alpha)
                        result.x = cX - w2;
                    else
                        if (-chi > alpha && alpha > -Math.PI +chi)
                            result.y = cY - h2;


        final double sinAlpha = Math.sin(alpha);
        final double cosAlpha = Math.cos(alpha);
        if (result.x > -Double.MAX_VALUE) {
            if (Math.abs(cosAlpha) < e2)
                result.y = cY;
            else
                result.y = cY + (result.x - cX) * sinAlpha / cosAlpha;
        }
        else
            if (result.y > -Double.MAX_VALUE) {
                if (Math.abs(sinAlpha) < e2)
                    result.x = cX;
                else
                    result.x = cX + (result.y - cY) * cosAlpha / sinAlpha;
            }
            else {
                System.out.println("INCORRECT AngleToPointGeom " + result.x + "," + result.y + " alpha = " + alpha);
                result = null;
            }

        return result;
    }

    /**
     * Calculates the boundary point on <code>shape</code> given by the
     * intersection of the line <code>p1, p2</code> and <code>shape</code>.
     * <p>
     * If there are 2 or more intersection points, the one
     * <i>'nearest'</i> to the point <code>nearestTo</code> is returned.
     *
     * @param shape
     * @param p1
     * @param p2
     * @param nearestTo
     * @return boundary point
     */
    public static Point2D.Double calculateBoundaryPoint(Shape shape, Point2D.Double p1,
            Point2D.Double p2, Point2D.Double nearestTo) {
        return calculateBoundaryPoint(shape, p1, p2, nearestTo, true);
    }

    /**
     * Calculate the boundary point on <code>shape</code> given by the
     * intersection of the line <code>p1, p2</code> and <code>shape</code>.
     * <p>
     * If there are 2 or more intersection points, the one
     * <i>'nearest'</i> to the point <code>nearestTo</code> is returned.
     * <p>
     * Because multiple intersection points may be the same distance from
     * <code>nearestTo</code>, there is the additional option to filter the
     * intersection points to those with the direction <code>p1</code>,
     * <code>p2</code>
     *
     *
     * @param shape
     * @param p1
     * @param p2
     * @param nearestTo
     *            filter the intersection points to those nearest to this point
     *            (typically this point is either p1 or p2)
     * @param directionP1P2
     *            a boolean option to filter the intersection points to the one
     *            with direction of the line through p1, p2.
     * @return boundary point or null
     */
    private static Point2D.Double calculateBoundaryPoint(Shape shape,
            Point2D.Double p1,
            Point2D.Double p2,
            Point2D.Double nearestTo,
            boolean directionP1P2) {
        final Point2D.Double[] pt = findIntersectionPoints(shape, p1, p2);
        if (pt.length == 0)
            return null;

        if (directionP1P2) {
            final double initAngle = Geom.angle(p1.x, p1.y, p2.x, p2.y);
            for (int j=0; j<pt.length; j++) {
                if (pt[j] == null)
                    continue;
                final double angle = Geom.angle(p1.x, p1.y, pt[j].x, pt[j].y);
                if (Math.signum(initAngle) != Math.signum(angle) && pt[j].x != p1.x && pt[j].y != p1.y) {
                    pt[j] = null;
                }
            }
        }
        return nearestPointInArray(nearestTo, pt);
    }

    /**
     * @param shape
     * @param p
     *            a point on the bounds of <code>shape</code>
     * @param leftRight
     *            a boolean which indicates on what side a vertex point should
     *            be considered to lie
     * @return point on boundary determined by a horizontal or vertical line
     *         through p, depending on the side p lies.
     */
    public static Point2D.Double calculateBoundaryPointThruBoundsPoint(Shape shape, Point2D.Double p,
            boolean leftRight) {
        if (shape instanceof Rectangle2D.Double)
            return p;

        final Rectangle2D.Double r = (Rectangle2D.Double)shape.getBounds2D();
        final Point2D.Double p2 = new Point2D.Double();
        boolean lr = false;
        if (onLeftRightSide(p,r) && onTopBottomSide(p,r)) {
             lr = leftRight;
        }
        else {
            if (onLeftRightSide(p,r))
                lr = true;
        }

        if (lr) {
            p2.x = onLeftSide(p,r) ? r.x+r.width : r.x;
            p2.y = p.y;
        }
        else {
            p2.x = p.x;
            p2.y = onTopSide(p,r) ? r.y+r.height : r.y;
        }

        Point2D.Double p1 = calculateBoundaryPoint(shape, p2, p, p);
        if (p1 == null)
            p1 = calculateBoundaryPointThruCenter(shape, p);
        return p1;
    }

    /**
     * @param shape
     * @param p
     * @return point on boundary determined by the line through the center of
     *         shape and through p
     */
    public static Point2D.Double calculateBoundaryPointThruCenter(Shape shape, Point2D.Double p) {
        final Rectangle2D.Double r = (Rectangle2D.Double) shape.getBounds2D();
        Point2D.Double p1 = null;

        if (shape instanceof Rectangle2D.Double)
            p1 = angleToPointGeom(r, pointToAngleGeom(r, p));
        else
            p1 = calculateBoundaryPoint(shape, new Point2D.Double(r.getCenterX(), r.getCenterY()), p, p);
        return p1;
    }

    /**
     * Calculate the intersection point of the line through p1 and p2
     * and the bounds of {@code shape} nearest to the point {@code nearestTo}.
     *
     * If both points are equidistant from nearestTo, return the point that has
     * the same direction from p1 as the line through p1, p2.
     *
     *
     * @param shape
     * @param p1
     * @param p2
     * @param nearestTo
     * @return bounds point
     */
    public static Point2D.Double calculateBoundsPoint(Shape shape, Point2D.Double p1,
            Point2D.Double p2,
            Point2D.Double nearestTo) {
        final Rectangle2D.Double r = (Rectangle2D.Double) shape.getBounds2D();
        final Rectangle2D.Double rectShape = new Rectangle2D.Double(r.x, r.y, r.width, r.height);
        return ConnectorGeom.calculateBoundaryPoint(rectShape, p1, p2, nearestTo);
    }

    /**
     * The Chop Point of a shape f1 relative to a shape f2 is the the point where
     * the boundary of f1 intersects the line joining the centers of the bounds of f1
     * and the bounds of f2.
     *
     *
     * @param f1
     * @param f2
     * @return chop point on f1
     */
    public static Point2D.Double calculateChopPoint(Shape f1, Shape f2) {
        if (f1 == null || f2 == null)
            return null;
        final Rectangle2D.Double r2 = (Rectangle2D.Double)f2.getBounds2D();
        return calculateBoundaryPointThruCenter(f1,
                new Point2D.Double(r2.getCenterX(), r2.getCenterY()));
    }



    /**
     * gets the angle of the CenterLine
     *
     * @param f1
     * @param f2
     * @return the CLAngle in the range of -pi to pi
     */
    public static double calculateCLAngle(Shape f1, Shape f2) {
        assert f1 != null;
        assert f2 != null;
        final Rectangle2D.Double r1 = (Rectangle2D.Double)f1.getBounds2D();
        final Rectangle2D.Double r2 = (Rectangle2D.Double)f2.getBounds2D();
        return Geom.angle(r1.getCenterX(), r1.getCenterY(), r2.getCenterX(), r2.getCenterY());
    }




    /**
     * @param r1
     * @param pr1
     * @param r2
     * @param pr2
     * @return angle change
     */
    public static double calculateCLAngleChange(Rectangle2D.Double r1,
            Rectangle2D.Double pr1,
            Rectangle2D.Double r2,
            Rectangle2D.Double pr2) {

        //optimization .......................................................
        final double delta1X = r1.x - pr1.x;
        final double delta1Y = r1.y - pr1.y;
        final double delta2X = r2.x - pr2.x;
        final double delta2Y = r2.y - pr2.y;


        if (Math.abs(delta1X - delta2X) < e2
                && Math.abs(delta1Y - delta2Y) < e2)
            return 0.0;


        //        if (Math.abs(r1.x - r2.x) > ConnectorStrategy.e2
        //                && Math.abs(pr1.x - pr2.x) > ConnectorStrategy.e2) {
        //            double v1 = (r1.y - r2.y)/(r1.x - r2.x);
        //            double v2 = (pr1.y - pr2.y)/(pr1.x - pr2.x);
        //            if (Math.abs(v1-v2) < epsilon)
        //                return 0.0;
        //        }
        // end optimization ..................................................

        final double centerX = r1.x + r1.width/2.0;
        final double centerY = r1.y + r1.height/2.0;
        final double oppCenterX = r2.x + r2.width/2.0;
        final double oppCenterY = r2.y + r2.height/2.0;

        final double prevCenterX = pr1.x + pr1.width/2.0;
        final double prevCenterY = pr1.y + pr1.height/2.0;
        final double prevOppCenterX = pr2.x + pr2.width/2.0;
        final double prevOppCenterY = pr2.y + pr2.height/2.0;

        final double newCLAngle = Geom.angle(oppCenterX, oppCenterY, centerX, centerY);
        final double prevCLAngle = Geom.angle(prevOppCenterX, prevOppCenterY, prevCenterX, prevCenterY);

        return ConnectorGeom.phaseNormalize(newCLAngle - prevCLAngle);
    }

    /**
     * Returns the angle to <i>rotate</i> the boundary connections from one
     * side of a square figure to another side.
     * <p>
     * <i>Alternatively ...</i>
     * <p>
     * Returns the angle to <i>normalize rotate</i> the boundary connections
     * from one side of a rectangular figure to another side.
     * <p>
     * This method assumes that all connections are on just one side of the
     * figure.
     *
     *
     * @param newSide the side to which the connections will be rotated
     * @param connectedSide the side from which the connections are rotated
     * @return a value in {0, Math.PI, Math.PI/2, -Math.PI/2} see
     *         {@link ConnectorGeom#rotationGroup2}
     */

    public static double calculateRotationAngleForSide(int newSide, int connectedSide) {
          final int newSideIndex         = baseOutcodeIndex[newSide];
          final int connectedSideIndex = baseOutcodeIndex[connectedSide];
          if (connectedSideIndex == -1 || newSideIndex == -1)
                    return 0.0;
          return rotationGroup2[newSideIndex][connectedSideIndex];
    }



    /**
     * Returns the angle to <i>rotate</i> the boundary connections of a moving
     * square figure and a stationary square figure so that connections do not
     * cross either figure. The connections will be rotated about the moving
     * figure.
     * <p>
     * <i>Alternatively ...</i>
     * <p>
     * Returns the angle to <i>normalize rotate</i> the boundary connections of
     * a moving rectangular figure and a stationary rectangular figure so that
     * connections do not cross either figure. The connections will be rotated
     * about the moving figure..
     * <p>
     * This method assumes that all connections are on just one side of each
     * figure and that initially these connections are from opposite sides.
     * <i>Left to Right, Top to Bottom or vice-versa</i>
     * <p>
     * Returns +-PI/2 if the moving side has passed the opposite connected side.
     * <p>
     * Returns PI if it detects connected sides are the same side on each
     * figure; this happens when reflecting (x or y) one figure about the other,
     * <p>
     *
     *
     * @param connectedSide
     *            the side of the moving rectangle that is connected; expressed
     *            as an outcode
     * @param movingRect
     * @param stationaryRect
     * @return a value in {0, Math.PI, Math.PI/2, -Math.PI/2} see
     *         {@link ConnectorGeom#rotationGroup}
     */
    public static double calculateRotationAngleForSide(int connectedSide, Rectangle2D.Double movingRect,
            Rectangle2D.Double stationaryRect) {
        // The implementation calculates the outcode of the moving rectangle
        // relative to the stationary rectangle.
        // This gives the side(s) on the stationary figure opposite the moving
        // rectangle.
        // It retrieves the index of the outcode and the index of the connected
        // side from the table baseOutcodeIndex
        // It retrieves the rotation angle from the table rotationGroup using
        // the two indexes.
        //
        // Note: We only need to compare base outcode values, not multi-value
        // outcodes like TOP|LEFT, BOTTOM|RIGHT, etc.
        // We rotate only when opposite sides pass; the moving rectangle as it
        // passes moves out of a vertex region loses multi-value outcodes
        // and gives a single base value only.

        final int oppositeSide = Geom.outcode(stationaryRect, movingRect);
        final int oppositeSideIndex = baseOutcodeIndex[oppositeSide];
        final int connectedSideIndex = baseOutcodeIndex[connectedSide];
        if (connectedSideIndex == -1 || oppositeSideIndex == -1)
            return 0.0;

        return rotationGroup[oppositeSideIndex][connectedSideIndex];
    }

    /**
     * returns the sides of the chop point on r1
     *
     * @param r1
     * @param r2
     * @return a value in {OUT_LEFT, OUT_RIGHT, OUT_TOP, OUT_BOTTOM}
     * <p> or one of <p>
     * {OUT_LEFT|OUT_TOP, OUT_LEFT|OUT_BOTTOM, OUT_RIGHT|OUT_TOP, OUT_RIGHT|OUT_BOTTOM}
     */
    public static int findChopSide(Rectangle2D.Double r1, Rectangle2D.Double r2) {
        final Point2D.Double chopPt = calculateChopPoint(r1, r2);
        int side = 0;

        if (onLeftSide(chopPt, r1))
            side |= Geom.OUT_LEFT;
        else
            if (onRightSide(chopPt, r1))
                side |= Geom.OUT_RIGHT;

        if (onTopSide(chopPt, r1))
            side |= Geom.OUT_TOP;
        else
            if (onBottomSide(chopPt, r1))
                side |= Geom.OUT_BOTTOM;

        return side;
    }

    /**
     * @param p1
     * @param r1
     * @return side as outcode
     */
    public static int findSide(Point2D.Double p1, Rectangle2D.Double r1) {
        int side = 0;

        if (p1.x < r1.x ||
            p1.x > r1.x+r1.width ||
            p1.y < r1.y ||
            p1.y > r1.y+r1.height)
            return 0;

        if (onLeftSide(p1, r1))
            side |= Geom.OUT_LEFT;
        else
            if (onRightSide(p1, r1))
                side |= Geom.OUT_RIGHT;

        if (onTopSide(p1, r1))
            side |= Geom.OUT_TOP;
        else
            if (onBottomSide(p1, r1))
                side |= Geom.OUT_BOTTOM;

        return side;
    }


    /**
     * @param coeff
     * @param t
     * @return Point2D.Double
     */
    public static Point2D.Double findCubicCurvePoint(double[] coeff, double t) {
        final double tt = t*t;
        final double ttt = tt * t;

        final double x = coeff[3]*ttt + coeff[2]*tt + coeff[1]*t + coeff[0];
        final double y = coeff[7]*ttt + coeff[6]*tt + coeff[5]*t + coeff[4];

        return new Point2D.Double(x, y);
    }

    /**
     * @param curve
     * @param l1X
     * @param l1Y
     * @param l2X
     * @param l2Y
     * @return Point2D.Double[]
     */
    public static Point2D.Double[] findIntersectionCubicLine(CubicCurve2D.Double curve,
            double l1X,
            double l1Y,
            double l2X,
            double l2Y) {
        Point2D.Double[] result = new Point2D.Double[0];
        if (l1X == l2X && l1Y == l2Y)
            return result;

        final double[] py   = new double[4];
        final double[] px   = new double[4];
        final double[] eqn  = new double[4];
        final double[] res  = new double[4];

        final double[] coeff = getCurveCoefficients(curve);

        px[0] = coeff[0];
        px[1] = coeff[1];
        px[2] = coeff[2];
        px[3] = coeff[3];

        py[0] = coeff[4];
        py[1] = coeff[5];
        py[2] = coeff[6];
        py[3] = coeff[7];

        px[0] = px[0] - l1X;
        py[0] = py[0] - l1Y;


        final double k1 = l2X - l1X;
        py[0] *= k1;
        py[1] *= k1;
        py[2] *= k1;
        py[3] *= k1;

        final double k2 = l2Y - l1Y;
        px[0] *= k2;
        px[1] *= k2;
        px[2] *= k2;
        px[3] *= k2;

        for (int i=0; i<4; i++) {
            eqn[i] = py[i] - px[i];
        }

        final int rootCount = CubicCurve2D.solveCubic(eqn, res);
        if (rootCount <= 0)
            return result;

        final Point2D.Double pt[] = new Point2D.Double[rootCount];
        int pointCount = 0;
        for (int i=0; i<rootCount; i++) {
            if (res[i] >= 0.0 && res[i] <= 1.0) {
                final Point2D.Double p = findCubicCurvePoint(coeff, res[i]);
                if (l2X == l1X)
                    p.setLocation(l1X, p.getY());
                if (l2Y == l1Y)
                    p.setLocation(p.getX(), l1Y);
                pt[++pointCount-1] = p;
            }
        }
        if (pointCount > 0) {
            result = new Point2D.Double[pointCount];
            for (int i=0; i<pointCount;i++) {
                result[i] = pt[i];
            }
        }
        return result;
    }



    /**
     * This is a <b>LINE</b> ... not a line segment
     *
     * x = x0 - [w(h*h - 4(y-y0)(y-y0))^1/2]/2h
     * @param r
     * @param p1X
     * @param p1Y
     * @param p2X
     * @param p2Y
     * @return Point2D.Double[]
     */
    public static Point2D.Double[] findIntersectionEllipseLine(Rectangle2D.Double r,
            double p1X,
            double p1Y,
            double p2X,
            double p2Y) {
        Point2D.Double[] result = new Point2D.Double[0];
        final double e = 0.0001;
        final double dx = p2X - p1X;
        final double dy = p2Y - p1Y;

        if (Math.abs(dx) < e && Math.abs(dy) < e)
            return result;

        //vertical
        if (Math.abs(dx) < e) {
            if (p1X < r.x || p1X > r.x+r.width)
                return result;
            result = new Point2D.Double[2];
            double a = r.width * r.width - 4 * (p1X - r.getCenterX()) * (p1X - r.getCenterX());
            a = Math.sqrt(a) * r.height / (r.width * 2);
            result[0]  = new Point2D.Double(p1X, r.getCenterY() - a);
            result[1]  = new Point2D.Double(p1X, r.getCenterY() + a);
            return result;
        }


        //horizontal
        if (Math.abs(dy) < e) {
            if (p1Y < r.y || p1Y > r.y+r.height)
                return result;
            result = new Point2D.Double[2];
            double a = r.height * r.height - 4 * (p1Y - r.getCenterY()) * (p1Y - r.getCenterY());
            a = Math.sqrt(a) * r.width / (r.height * 2);
            result[0]  = new Point2D.Double(r.getCenterX() - a, p1Y);
            result[1]  = new Point2D.Double(r.getCenterX() + a, p1Y);
            return result;
        }


        final double m = dy/dx; // slope
        final double cy = p1Y - m * p1X; // y-intercept
        final double w = r.width / 2;
        final double h = r.height / 2;
        final double Xc = r.x + w;
        final double k = cy - (r.y + h);

        final double hh = h*h;
        final double ww = w*w;
        final double mm = m*m;
        final double kk = k*k;
        final double XcXc = Xc*Xc;

        final double a = hh + ww * mm;
        final double b = 2 * (m * k * ww - hh * Xc);
        final double c = hh * XcXc + ww * kk - ww * hh;
        final double roots[] = new double[2];
        final double eqn[] = {c,b,a};

        final int rootCount = QuadCurve2D.solveQuadratic(eqn, roots);
        if (rootCount <= 0)
            result = new Point2D.Double[0];
        else {
            result = new Point2D.Double[rootCount];
            result[0] = new Point2D.Double(roots[0], m * roots[0] + cy);
            if (rootCount == 2)
                result[1] = new Point2D.Double(roots[1], m * roots[1] + cy);
        }

        return result;
    }


    /**
     * @param pathIterator
     * @param r
     * @param p1X
     * @param p1Y
     * @param p2X
     * @param p2Y
     * @return Point2D.Double[]
     */
    public static Point2D.Double[] findIntersectionPathLine(PathIterator pathIterator, Rectangle2D.Double r,
            double p1X, double p1Y, double p2X, double p2Y) {
        Point2D.Double[] result = new Point2D.Double[0];
        final double[] coords = new double[6];
        double currentX = -Double.MAX_VALUE;
        double currentY = -Double.MAX_VALUE;

        final HashSet<Point2D.Double> intersectionPointSet = new HashSet<Point2D.Double>();

        if (pathIterator == null)
            return result;

        while (!pathIterator.isDone()) {
            final int type = pathIterator.currentSegment(coords);
            switch (type) {
            case PathIterator.SEG_CLOSE :
                break;
            case PathIterator.SEG_CUBICTO :
                final CubicCurve2D.Double cubicCurve = new CubicCurve2D.Double(currentX, currentY,
                        coords[0], coords[1],
                        coords[2], coords[3],
                        coords[4], coords[5]);
                final Point2D.Double[] pts = findIntersectionCubicLine(cubicCurve, p1X, p1Y, p2X, p2Y);
                for (final java.awt.geom.Point2D.Double pt : pts) {
                    intersectionPointSet.add(pt);
                }
                currentX = coords[4];
                currentY = coords[5];
                break;
            case PathIterator.SEG_LINETO :
                final Point2D.Double pt = Geom.intersect(currentX, currentY, coords[0],
                        coords[1], p1X, p1Y, p2X, p2Y,
                        Double.MAX_VALUE);
                if (pt != null &&
                        Geom.lineContainsPoint(currentX, currentY, coords[0], coords[1],
                                pt.x, pt.y, 0.5)) {
                    intersectionPointSet.add(pt);
                }
                currentX = coords[0];
                currentY = coords[1];
                break;
            case PathIterator.SEG_MOVETO :
                currentX = coords[0];
                currentY = coords[1];
                break;
            case PathIterator.SEG_QUADTO :
                final QuadCurve2D.Double quadCurve = new QuadCurve2D.Double(currentX, currentY, coords[0], coords[1],
                        coords[2],coords[3]);
                final Point2D.Double[] ptsQ = findIntersectionQuadLine(quadCurve, p1X, p1Y, p2X, p2Y);
                for (final java.awt.geom.Point2D.Double element : ptsQ) {
                    intersectionPointSet.add(element);
                }
                currentX = coords[2];
                currentY = coords[3];

                break;
            }
            pathIterator.next();
        }
        result = intersectionPointSet.toArray(new Point2D.Double[intersectionPointSet.size()]);
        return result;
    }

    /**
     * @param shape
     * @param p1
     * @param p2
     * @return array of intersection points
     */
    public static Point2D.Double[] findIntersectionPoints(Shape shape,
            Point2D.Double p1, Point2D.Double p2) {
        Point2D.Double[] pt = new Point2D.Double[0];
        if (Math.abs(p1.x - p2.x) < epsilon && Math.abs(p1.y - p2.y) < epsilon) {
            return pt;
        }

        final Rectangle2D.Double r = (Rectangle2D.Double) shape.getBounds2D();
        if (shape instanceof Rectangle2D.Double)
            pt = findIntersectionRectLine(r, p1.x, p1.y, p2.x, p2.y);
        else
            if (shape instanceof Ellipse2D.Double)
                pt = findIntersectionEllipseLine(r, p1.x, p1.y, p2.x, p2.y);
            else {
                final PathIterator pathIterator = shape.getPathIterator(null);
                pt = findIntersectionPathLine(pathIterator, r, p1.x, p1.y, p2.x, p2.y);
            }
        return pt;
    }


    /**
     * @param curve
     * @param l1X
     * @param l1Y
     * @param l2X
     * @param l2Y
     * @return Point2D.Double[]
     */
    public static Point2D.Double[] findIntersectionQuadLine(QuadCurve2D.Double curve,
            double l1X,
            double l1Y,
            double l2X,
            double l2Y) {
        Point2D.Double[] result = new Point2D.Double[0];
        if (l1X == l2X && l1Y == l2Y)
            return result;

        final double[] y   = new double[3];
        final double[] x   = new double[3];
        final double[] eqn = new double[3];
        final double[] res = new double[3];

        final double[] coeff = getCurveCoefficients(curve);

        x[0] = coeff[0];
        x[1] = coeff[1];
        x[2] = coeff[2];

        y[0] = coeff[3];
        y[1] = coeff[4];
        y[2] = coeff[5];

        x[0] = x[0] - l1X;
        y[0] = y[0] - l1Y;

        final double k1 = l2X-l1X;
        y[0] = y[0] * k1;
        y[1] = y[1] * k1;
        y[2] = y[2] * k1;

        final double k2 = l2Y-l1Y;
        x[0] = x[0] * k2;
        x[1] = x[1] * k2;
        x[2] = x[2] * k2;

        for (int i=0; i<3; i++) {
            eqn[i] = y[i] - x[i];
        }

        final int rootCount = QuadCurve2D.solveQuadratic(eqn, res);
        if (rootCount == 0)
            return result;


        final Point2D.Double pt[] = new Point2D.Double[rootCount];
        int pointCount = 0;
        for (int i=0; i<rootCount; i++) {
            if (res[i] >= 0.0 && res[i] <= 1.0) {
                final Point2D.Double p = findQuadCurvePoint(coeff, res[i]);
                if (l2X == l1X)
                    p.setLocation(l1X, p.getY());
                if (l2Y == l1Y)
                    p.setLocation(p.getX(), l1Y);
                pt[++pointCount-1] = p;
            }
        }
        if (pointCount > 0) {
            result = new Point2D.Double[pointCount];
            for (int i=0; i<pointCount;i++) {
                result[i] = pt[i];
            }
        }
        return result;
    }

    /**
     * This is a <b>LINE</b> ... not a line segment
     * <p>
     * TODO check if more efficient than calling intersect with rectangle edges or using parametric form
     * @param r
     * @param p1X
     * @param p1Y
     * @param p2X
     * @param p2Y
     * @return Point2D.Double[]
     */
    public static Point2D.Double[] findIntersectionRectLine(Rectangle2D.Double r, double p1X, double p1Y, double p2X,
            double p2Y) {
        final double e = 0.000001;
        final Point2D.Double[] pt = new Point2D.Double[2];
        final double maxX = r.x+r.width;
        final double maxY = r.y+r.height;
        final double dx   = p1X - p2X;
        final double dy   = p1Y - p2Y;


        if (Math.abs(dx) < e && Math.abs(dy) < e) {
            //System.out.println("findIntersectionRectLine exiting ... supplied points are the same");
            return new Point2D.Double[0];
        }

        // vertical
        if (Math.abs(dx) < e) {
            if (p1X < r.x || p1X > maxX) {
                //System.out.println("findIntersectionRectLine exiting ... Vertical outside Rectangle");
                return new Point2D.Double[0];
            }
            pt[0] = new Point2D.Double(p1X, r.y);
            pt[1] = new Point2D.Double(p1X, maxY);
            return pt;
        }

        // horizontal
        if (Math.abs(dy) < e) {
            if (p1Y < r.y || p1Y > maxY) {
                //System.out.println("findIntersectionRectLine exiting ... Horizontal outside Rectangle");
                return new Point2D.Double[0];
            }
            pt[0] = new Point2D.Double(r.x, p1Y);
            pt[1] = new Point2D.Double(maxX, p1Y);
            return pt;
        }

        // max of 2 intersecting points with line out of a possible 4 (left, right, top, bottom)
        // vertex points could be counted twice so return if you have 2 points ... this is possible because
        // evaluating order is (left, right), (top, bottom)
        int k = 0;
        final double m = dy/dx; // slope
        final double cy = p2Y - m * p2X; // y-intercept

        //left
        double calc = m * r.x + cy;
        if (r.y <= calc && calc <= maxY) {
            pt[k++] = new Point2D.Double(r.x, calc);
        }
        //right
        calc += m * r.width;
        if (r.y <= calc && calc <= maxY) {
            pt[k++] = new Point2D.Double(maxX, calc);
        }
        if (k==2)
            return pt;

        //top
        calc = (r.y - cy) / m;
        if (r.x <= calc && calc <= maxX) {
            pt[k++] = new Point2D.Double(calc, r.y);
        }
        if (k==2)
            return pt;

        //bottom
        calc += r.height / m;
        if (r.x <= calc && calc <= maxX) {
            pt[k++] = new Point2D.Double(calc, maxY);
        }

        if (k==0)
            return new Point2D.Double[0];

        return pt;
    }


    /**
     * returns the preferred side on r1 to connect to r2
     * <p>
     * If r1 is completely in the vertex region of r2 there two sides to choose from
     * (TOP/LEFT, BOTTOM/LEFT, TOP/RIGHT or BOTTOM/RIGHT).
     * <p>
     * The side chosen is determined by the parameter leftRight. If this is true
     * the left or right side is preferred; otherwise the top or bottom side is
     * preferred.
     *
     *
     * @param r1
     * @param r2
     * @param leftRight
     * @return preferred side (LEFT, TOP, RIGHT, BOTTOM)
     */
    public static int findPreferredConnectingSide(Rectangle2D.Double r1, Rectangle2D.Double r2,
            boolean leftRight) {
        // get sides of r1 facing r2
        int result = Geom.outcode(r1, r2);

        if (leftRight) {
            if ((result & Geom.OUT_LEFT) != 0)
                result = Geom.OUT_LEFT;
            else
                if ((result & Geom.OUT_RIGHT) != 0)
                    result = Geom.OUT_RIGHT;
        }
        else {
            if ((result & Geom.OUT_TOP) != 0)
                result = Geom.OUT_TOP;
            else
                if ((result & Geom.OUT_BOTTOM) != 0)
                    result = Geom.OUT_BOTTOM;

        }
        return result;
    }


    /**
     * Returns the preferred position of r1 for connections between r1 and r2.
     * <p>
     * This is determined by aligning the center points of both rectangles
     * either horizontally or vertically depending on the value returned by
     * {@link ConnectorGeom#findPreferredConnectingSide}.
     * <p>
     * The parameter {@code leftRight} decides the preferred side if the latter
     * has two sides to choose from.
     *
     *
     * @param r2
     * @param r1
     * @param leftRight
     * @return a rectangle identifying the preferred position of r1.
     */
    public static Rectangle2D.Double findPreferredPosition(Rectangle2D.Double r1, Rectangle2D.Double r2,
            boolean leftRight) {
        // the preferred side of r1 for connections to r2
        final int preferredSide = ConnectorGeom.findPreferredConnectingSide(r1, r2, leftRight);
        double pX = r1.x;
        double pY = r1.y;
        if (onLeftRightSide(preferredSide)) {
            pY = Math.max(0, r2.y + r2.height/2 - r1.height/2);
        }
        if (onTopBottomSide(preferredSide)) {
            pX = Math.max(0, r2.x + r2.width/2 - r1.width/2);
        }
        return new Rectangle2D.Double(pX, pY, r1.width, r1.height);
    }

    /**
     * @param coeff
     * @param t
     * @return Point2D.Double
     */
    public static Point2D.Double findQuadCurvePoint(double[] coeff, double t) {
        final double tt = t*t;

        final double x = coeff[2]*tt + coeff[1]*t + coeff[0];
        final double y = coeff[5]*tt + coeff[4]*t + coeff[3];

        return new Point2D.Double(x, y);
    }


    /**
     * @param curve
     * @return double[]
     */
    public static double[] getCurveCoefficients(CubicCurve2D.Double curve) {
        // bx = 3x1 - 3x0
        // ax = 3x2 - 6x1 + 3x0 = 3(x2 - x1) - (3x1 -  3x0) = 3(x2 - x1) - bx
        //  ......... ax + bx = = 3x2 - 3x1
        // dx = x3 - 3x2 + 3x1 - x0 = x3 - x0  - 3x2 + 3x1 = x3 - x0 - ax - bx

        final double cx  = curve.x1;
        final double cy  = curve.y1;
        final double bx  = 3.0 * (curve.ctrlx1 - curve.x1);
        final double by  = 3.0 * (curve.ctrly1 - curve.y1);
        final double ax  = 3.0 * (curve.ctrlx2 - curve.ctrlx1) - bx;
        final double ay  = 3.0 * (curve.ctrly2 - curve.ctrly1) - by;
        final double dx  = curve.x2 - curve.x1 - ax - bx;
        final double dy  = curve.y2 - curve.y1 - ay - by;

        final double[] coeff = {cx, bx, ax, dx, cy, by, ay, dy};
        return coeff;
    }




    /**
     * @param curve
     * @return double[]
     */
    public static double[] getCurveCoefficients(QuadCurve2D.Double curve) {
        // cx = curve.x1
        // bx = 2x1 - 2x0
        // ax = (x2 - 2 * x1 + x0);

        final double cx  = curve.x1;
        final double cy  = curve.y1;
        final double bx  = 2.0 * (curve.ctrlx - curve.x1);
        final double by  = 2.0 * (curve.ctrly - curve.y1);
        final double ax  = curve.x2 - 2 * curve.ctrlx + cx;
        final double ay  = curve.y2 - 2 * curve.ctrly + cy;

        final double[] coeff = {cx, bx, ax, cy, by, ay};
        return coeff;
    }


    /**
     * returns the denormalized equivalent for a rectangle r of a point p on the unit square .<p>
     * p is assumed to be a point on the unit square.<p>
     * <b> if p is not on the unit square, the result will not be a point on r </b>
     * <p>
     * This is a scaling transformation
     *
     *
     * @param p
     * @param r
     * @return Point2D.Double
     */
    public final static Point2D.Double inverseNormalizeTransform(Point2D.Double p, Rectangle2D.Double r) {
        final double pX = r.x + r.width/2 + p.x * r.width;
        final double pY = r.y + r.height/2 + p.y * r.height;
        return new Point2D.Double(pX, pY);
    }

    /**
     * A distinction is drawn between a point on a rectangle vertex and a Vertex Point.
     * <p>
     * A Vertex Point is <b>defined</b> as one whose X and Y values are <b>both</b> within <i>epsilon</i>
     * distance from vertex coordinates.
     * <p>
     * A Vertex Point, by definition, is on two sides.
     *
     *
     * @param p point
     * @param r rectangle
     * @return true/false
     */
    public static boolean isVertexPoint(Point2D.Double p, Rectangle2D.Double r) {
        if (ConnectorGeom.onLeftRightSide(p.x, r) && ConnectorGeom.onTopBottomSide(p.y, r))
            return true;
        return false;
    }

    /**
     * /** A distinction is drawn between a point on a rectangle vertex and a
     * Vertex Point.
     * <p>
     * A Vertex Point is <b>defined</b> as one whose X and Y values are
     * <b>both</b> within <i>epsilon</i> distance from vertex coordinates.
     * <p>
     * A Vertex Point, by definition, is on two sides.
     *
     * @param sides
     * @return true/false
     */
    public static boolean isVertexPoint(int sides) {
        if (ConnectorGeom.onLeftRightSide(sides) && ConnectorGeom.onTopBottomSide(sides))
            return true;
        return false;
    }

    /**
     * Changes a Vertex point to a non-vertex point.
     * {@link ConnectorGeom#isVertexPoint}
     * <p>
     * Except for rectilinear projections each point should be on one side only.
     * <p>
     * <i> Note that for {@link EdgeConnectorStrategy} and it's sub-classes the
     * <b>side</b> should be the same for all points</i>
     * <p>
     *
     * @param p
     *            a vertex point
     * @param r1
     *            the bounds rectangle
     * @param onLeftRight
     *            true if on left or right side
     * @param keepSide
     *            true to keep the side indicated by onLeftRight; false if to
     *            drop that side
     * @return a point that is only on one side.
     *         <p>
     *         This differs from p by a coordinate (x or y) change of not more
     *         than Math.abs(e2 - epsilon).
     *         <p>
     *         This will not materially affect the rendering of p.
     */
    public static Point2D.Double makeNonVertex(Point2D.Double p, Rectangle2D.Double r1,
                                                boolean onLeftRight, boolean keepSide) {
        if (keepSide) {
            if (onLeftRight)
                // keep on left/Right side & drop top/bottom side
                p.y = Geom.range(r1.y + e2, r1.y + r1.height - e2, p.y);
            else
                p.x = Geom.range(r1.x + e2, r1.x + r1.width - e2, p.x);
        }
        else {
            if (onLeftRight)
                // drop left/right side & keep top/bottom side
                p.x = Geom.range(r1.x + e2, r1.x + r1.width - e2, p.x);
            else
                p.y = Geom.range(r1.y + e2, r1.y + r1.height - e2, p.y);
        }
        return new Point2D.Double(p.x, p.y);
    }


    /**
     * @param nearestTo
     * @param pArray
     * @return point in pArray nearest to the point <i>nearestTo</i>
     */
    public static Point2D.Double nearestPointInArray(Point2D.Double nearestTo, Point2D.Double[] pArray) {
        if (pArray == null || pArray.length == 0)
            return null;

        if (pArray.length == 1)
            return pArray[0];

        Point2D.Double result = new Point2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);
        double dMin = Double.MAX_VALUE;
        for (final Point2D.Double element : pArray) {
            if (element == null)
                continue;
            final double dist = Point2D.distance(element.x, element.y, nearestTo.x, nearestTo.y);
            if (dist < dMin) {
                dMin = dist;
                result.x = element.x;
                result.y = element.y;
            }
        }
        if (result.x == Double.MAX_VALUE  && result.y == Double.MAX_VALUE)
            result = null;

        return result;
    }


    /**
     * returns the normalized point for a point p on rectangle r.
     * <p>
     * The normalized point is a point on the unit square.
     * <p>
     * This is a scaling transformation.
     * (More accurately the composition of Translation and Scaling transformations)
     * <p>
     * If p lies outside the rectangle the result is outside the unit square.
     *
     * @param p
     * @param r
     * @return normalized Point
     */
    public final static Point2D.Double normalizeTransform(Point2D.Double p, Rectangle2D.Double r) {
        final double pX = (p.x - r.x - r.width/2) / r.width;
        final double pY = (p.y - r.y - r.height/2) / r.height;
        return new Point2D.Double(pX, pY);
    }


    /**
     * returns the normalized equivalent on rectangle <code>to</code> of a point p on rectangle <code>from</code>.
     * <p>
     * This is a scaling transformation
     *
     * @param p
     * @param from
     * @param to
     * @return normalized point
     */
    public final static Point2D.Double normalizeTransform(Point2D.Double p, Rectangle2D.Double from,
            Rectangle2D.Double to) {
        return inverseNormalizeTransform(normalizeTransform(p, from), to);
    }


    /**
     * @param pY
     * @param r
     * @return boolean
     */
    public  static final boolean onBottomSide(double pY, Rectangle2D.Double r) {
        return Math.abs(r.y + r.height - pY) < epsilon;
    }

    /**
     * @param sides outcode
     * @return boolean
     */
    public static final boolean onBottomSide(int sides) {
        return (sides & Geom.OUT_BOTTOM) != 0;
    }

    /**
     * @param p
     * @param r
     * @return boolean
     */
    public static final boolean onBottomSide(Point2D.Double p, Rectangle2D.Double r) {
        return onBottomSide(p.y, r);
    }

    /**
     * @param pX
     * @param r
     * @return boolean
     */
    public static final boolean onLeftRightSide(double pX, Rectangle2D.Double r) {
        return onLeftSide(pX,r) || onRightSide(pX,r);
    }

    /**
     * @param sides outcode
     * @return boolean
     */
    public static final boolean onLeftRightSide(int sides) {
        return (sides & Geom.OUT_LEFT) != 0 || (sides & Geom.OUT_RIGHT) != 0;
    }


    /**
     * @param p
     * @param r
     * @return true/false boolean
     */
    public static final boolean onLeftRightSide(Point2D.Double p, Rectangle2D.Double r) {
        return onLeftSide(p,r) || onRightSide(p,r);
    }



    /**
     * @param pX
     * @param r
     * @return boolean
     */
    public static final boolean onLeftSide(double pX, Rectangle2D.Double r) {
        return Math.abs(r.x - pX) < epsilon;
    }



    /**
     * @param sides outcode
     * @return boolean
     */
    public static final boolean onLeftSide(int sides) {
        return (sides & Geom.OUT_LEFT) != 0;
    }


    /**
     * @param p
     * @param r
     * @return true/false
     */
    public static final boolean onLeftSide(Point2D.Double p, Rectangle2D.Double r) {
        return onLeftSide(p.x, r);
    }

    /**
     * @param pX
     * @param r
     * @return boolean
     */
    public static final boolean onRightSide(double pX, Rectangle2D.Double r) {
        return Math.abs(r.x + r.width - pX) < epsilon;
    }

    /**
     * @param sides outcode
     * @return boolean
     */
    public static final boolean onRightSide(int sides) {
        return (sides & Geom.OUT_RIGHT) != 0;
    }

    /**
     * @param p
     * @param r
     * @return true/false boolean
     */
    public static final boolean onRightSide(Point2D.Double p, Rectangle2D.Double r) {
        return onRightSide(p.x, r);
    }


    /**
     * @param pY
     * @param r
     * @return boolean
     */
    public static final boolean onTopBottomSide(double pY, Rectangle2D.Double r) {
        return onTopSide(pY,r) || onBottomSide(pY,r);
    }




    /**
     * @param sides outcode
     * @return boolean
     */
    public static final boolean onTopBottomSide(int sides) {
        return (sides & Geom.OUT_BOTTOM) != 0 || (sides & Geom.OUT_TOP) != 0;
    }


    /**
     * @param p
     * @param r
     * @return boolean
     */
    public static final boolean onTopBottomSide(Point2D.Double p, Rectangle2D.Double r) {
        return onTopSide(p,r) || onBottomSide(p,r);
    }


    /**
     * @param pY
     * @param r
     * @return boolean
     */
    public static final boolean onTopSide(double pY, Rectangle2D.Double r) {
        return Math.abs(r.y - pY) < epsilon;
    }

    /**
     * @param sides outcode
     * @return boolean
     */
    public static final boolean onTopSide(int sides) {
        return (sides & Geom.OUT_TOP) != 0;
    }


    /**
     * @param p
     * @param r
     * @return boolean
     */
    public static final boolean onTopSide(Point2D.Double p, Rectangle2D.Double r) {
        return onTopSide(p.y, r);
    }

    /**
     * @param gamma
     * @return an angle in the range -pi to pi
     */
    public static double phaseNormalize(double gamma) {
        if (Math.abs(gamma - Math.PI) < epsilon || Math.abs(gamma + Math.PI) < epsilon)
            return gamma;
        while (gamma > Math.PI) {
            gamma = gamma - 2*Math.PI;
        }
        while (gamma < - Math.PI) {
            gamma = gamma + 2*Math.PI;
        }
        return gamma;
    }

    /**
     * Gets the angle of a point relative to a rectangle.
     * The returned angle is the strict 'Geometric' angle
     *
     * @param r
     * @param p
     * @return angle
     */
    public static double pointToAngleGeom(Rectangle2D.Double r, Point2D.Double p) {
        final double px = p.x - (r.x + r.width/2);
        final double py = p.y - (r.y + r.height/2);
        return atan2(py, px);
    }

    /**
     *
     * calculate the projection of point p onto rectangle r2,
     *
     * @param p
     * @param r1
     * @return Point2D.Double
     */
    public static final Point2D.Double project(Point2D.Double p, Rectangle2D.Double r1) {
        return new Point2D.Double(projectX(p, r1), projectY(p, r1));
    }


    /**
     *
     * calculate the x-projection of point p onto rectangle r1,
     *
     *
     * @param p
     * @param r1
     * @return x-projection
     */
    public static final double projectX(Point2D.Double p, Rectangle2D.Double r1) {
        return Geom.range(r1.x, r1.x+r1.width, p.x);
    }

    /**
     *
     * calculate the y-projection of point p onto rectangle r1,
     *
     *
     * @param p
     * @param r1
     * @return y-projection
     */
    public static final double projectY(Point2D.Double p, Rectangle2D.Double r1) {
        return Geom.range(r1.y, r1.y+r1.height, p.y);
    }


    /**
     * get the reflection of p in the center of r1.
     * <p>
     *
     * @param p
     * @param r1
     * @return reflected point
     */
    public static final Point2D.Double reflect(Point2D.Double p, Rectangle2D.Double r1) {
        return reflectX(reflectY(p, r1), r1);
    }


    /**
     * determines the reflection of p in the vertical line through the center of r1
     *
     * @param p
     * @param r1
     * @return reflection on x-axis
     */
    public static final Point2D.Double reflectX(Point2D.Double p, Rectangle2D.Double r1) {
        final Point2D.Double p2 = new Point2D.Double(p.x, p.y);
        if (ConnectorGeom.onLeftRightSide(p2, r1))
            p2.x = r1.x + r1.width + r1.x - project(p, r1).x;
        return p2;
    }


    /**
     * determines the reflection of p in the horizontal line through the center of r1
     *
     * @param p
     * @param r1
     * @return reflection on y-axis
     */
    public static final Point2D.Double reflectY(Point2D.Double p, Rectangle2D.Double r1) {
        final Point2D.Double p2 = new Point2D.Double(p.x, p.y);
        if (ConnectorGeom.onTopBottomSide(p2, r1))
            p2.y = r1.y + r1.height + r1.y - project(p, r1).y;
        return p2;
    }

    /**
     * @param angle
     * @param r1
     * @param pX
     * @param pY
     * @return Point2D.Double rotated point
     */
    public static Point2D.Double rotateEllipsePoint(double angle, Rectangle2D.Double r1,
            double pX, double pY) {
        if (r1 == null || r1.width == 0 || r1.height == 0)
            return new Point2D.Double(pX, pY);

        //angle is relative to center of figure
        final double cX = r1.x + r1.width/2;
        final double cY = r1.y + r1.height/2;

        final double alpha = Math.atan2(pY - cY, pX - cX) + angle;
        final double cosAlpha = Math.cos(alpha);
        final double sinAlpha = Math.sin(alpha);

        // if r is a square ... circular point
        if (Math.abs(r1.width - r1.height) < 0.00005) {
            return new Point2D.Double(cX + r1.width/2*cosAlpha,  cY + r1.width/2*sinAlpha);
        }

        final double a = r1.width/2;
        final double b = r1.height/2;

        final double aSq = a * a;
        final double bSq = b * b;
        final double eccSq = (aSq - bSq) / aSq;
        final double r = b / Math.sqrt(1 - eccSq * cosAlpha * cosAlpha);

        return new Point2D.Double(cX + r*cosAlpha,  cY + r*sinAlpha);
    }

    /**
     * @param angle
     * @param r
     * @param pX
     * @param pY
     * @return rotated point
     */
    public static Point2D.Double rotateNormalizedEllipsePoint(double angle,
            Rectangle2D.Double r, double pX, double pY) {
        if (Math.abs(r.width - r.height) < epsilon)
            return rotateEllipsePoint(angle, r, pX, pY);

        Point2D.Double p = ConnectorGeom.normalizeTransform(new Point2D.Double(pX, pY), r);
        p = rotateEllipsePoint(angle, new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0), p.x, p.y);
        return ConnectorGeom.inverseNormalizeTransform(p, r);
    }
    /**
     * Normalizes a point <code>(pX, pY)</code> on rectangle <code>r</code>,
     * rotates the normalized point by the angle <code>angle</code> and returns
     * the denormalized point on <code>r</code>.
     * <p>
     *
     * @param angle
     * @param r
     * @param pX
     * @param pY
     * @return Point2D.Double
     */
    public static Point2D.Double rotateNormalizedRectPoint(double angle,
            Rectangle2D.Double r, double pX, double pY) {
        if (Math.abs(r.width - r.height) < epsilon)
            return rotateRectPoint(angle, r, pX, pY);

        Point2D.Double p = ConnectorGeom.normalizeTransform(new Point2D.Double(pX, pY), r);
        p = rotateRectPoint(angle, new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0), p.x, p.y);
        return ConnectorGeom.inverseNormalizeTransform(p, r);
    }

    /**
     * Rotates a point on a rectangle<p>
     *
     * @param angle
     * @param r
     * @param pX
     * @param pY
     * @return Point2D.Double rotated point
     */
    public static Point2D.Double rotateRectPoint(double angle, Rectangle2D.Double r,
            double pX, double pY) {
        final Point2D.Double p = new Point2D.Double(pX, pY);
        if (r == null || r.width < 0.0000005 || r.height < 0.0000005)
            return p;

        final double theta = pointToAngleGeom(r, new Point2D.Double(pX, pY));
        return angleToPointGeom(r, theta+angle);
    }

    /**
     * calculates the CenterLine Angle
     *
     * @param p1
     * @param f1
     * @param f2
     * @return angle
     */
//    public static Double calculateCLAngle(Point2D.Double p1, Figure f1, Figure f2) {
//        final Rectangle2D.Double r1 = f1.getBounds();
//        final Rectangle2D.Double r2 = f2.getBounds();
//
//        final double c1X = r1.x + r1.width/2.0;
//        final double c1Y = r1.y + r1.height/2.0;
//        final double c2X = r2.x + r2.width/2.0;
//        final double c2Y = r2.y + r2.height/2.0;
//
//        final double p1Angle = Geom.angle(c1X, c1Y, p1.x, p1.y);
//        final double clAngle = Geom.angle(c1X, c1Y, c2X, c2Y);
//
//        return phaseNormalize(p1Angle - clAngle);
//
//    }

    /**
     * This calculates the change in the centerLine angle, <i>for the centerLine
     * between r1 and r2</i>, when r1 is translated by deltaX,deltaY.
     * <p>
     *
     * <b>cos(deltaCLAngle) = [d21*d21 + (Xc2 - Xc1)*deltaX + (Yc2-Yc1)*deltaY]
     * / (d21*dd21)
     * <p>
     * sin(deltaCLAngle) = [(Xc2 - Xc1)*deltaY + (Yc2-Yc1)*deltaX] / (d21*dd21)
     * <p>
     * </b> also note that ...
     * <p>
     * <b> deltaX =
     * dd21/d21*[(Xc1-Xc2)*cos(deltaCLAngle)-(Yc1-Yc2)*sin(deltaCLAngle)] -
     * (Xc1-Xc2)
     * <p>
     * deltaY =
     * dd21/d21*[(Yc1-Yc2)*cos(deltaCLAngle)+(Xc1-Xc2)*sin(deltaCLAngle)] -
     * (Yc1-Yc2) </b>
     *
     * <p>
     * where ...
     * <p>
     *
     * <p>
     * Xc1, Yc1 are the coordinates of the center of r1
     * <p>
     * Xc2, Yc2 are the coordinates of the center of r2
     *
     * <p>
     * d21 is the distance between the center of r2 and the center of r1
     * <p>
     * dd21 is the distance between the center of r2 and the center of r1 AFTER
     * r1 is translated by deltaX,deltaY
     *
     * @param r1
     * @param r2
     * @param deltaX
     * @param deltaY
     * @return angle change
     */
//    public static double calculateCLAngleChange(Rectangle2D.Double r1, Rectangle2D.Double r2,
//            double deltaX, double deltaY) {
//        final Rectangle2D.Double nr1 = new Rectangle2D.Double(r1.x+deltaX, r1.y+deltaY, r1.width, r1.height);
//
//        final double prevDist21 = Point2D.distance(r2.getCenterX(), r2.getCenterY(), r1.getCenterX(), r1.getCenterY());
//        final double dist21 = Point2D.distance(r2.getCenterX(), r2.getCenterY(), nr1.getCenterX(), nr1.getCenterY());
//        final double xSep = r1.getCenterX() - r2.getCenterX();
//        final double ySep = r1.getCenterY() - r2.getCenterY();
//
//        final double sinDeltaCLAngle = (xSep * deltaY - ySep * deltaX) / (prevDist21 * dist21);
//
//        return Math.asin(sinDeltaCLAngle);
//    }

}
