package org.jhotdraw8.geom.offsetline;


import javafx.geometry.Point2D;

import static org.jhotdraw8.geom.offsetline.BulgeConversionFunctions.arcRadiusAndCenter;
import static org.jhotdraw8.geom.offsetline.Utils.angle;
import static org.jhotdraw8.geom.offsetline.Utils.fuzzyEqual;

/**
 * Vertex of a Polyline Arc.
 */
public class PlineVertex {
    private double x;
    private double y;
    private double bulge;

    public PlineVertex(Point2D p, double bulge) {
        this(p.getX(), p.getY(), bulge);
    }

    public PlineVertex(double x, double y) {
        this(x, y, 0.0);
    }

    public PlineVertex(double x, double y, double bulge) {
        this.x = x;
        this.y = y;
        this.bulge = bulge;
    }

    public boolean bulgeIsNeg() {
        return bulge < 0;
    }

    public boolean bulgeIsPos() {
        return bulge > 0;
    }

    public boolean bulgeIsZero() {
        return bulgeIsZero(Utils.realPrecision);
    }

    public boolean bulgeIsZero(double epsilon) {
        return Math.abs(bulge) < epsilon;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double bulge() {
        return bulge;
    }

    public void bulge(double bulge) {
        this.bulge = bulge;
    }

    public Point2D pos() {
        return new Point2D(x, y);
    }

    public void pos(Point2D p) {
        x = p.getX();
        y = p.getY();
    }

    /// Computes a fast approximate AABB of a segment described by v1 to v2, bounding box may be larger
    /// than the true bounding box for the segment

    static AABB createFastApproxBoundingBox(final PlineVertex v1, final PlineVertex v2) {
        AABB result = new AABB();
        if (v1.bulgeIsZero()) {
            if (v1.getX() < v2.getX()) {
                result.xMin = v1.getX();
                result.xMax = v2.getX();
            } else {
                result.xMin = v2.getX();
                result.xMax = v1.getX();
            }

            if (v1.getY() < v2.getY()) {
                result.yMin = v1.getY();
                result.yMax = v2.getY();
            } else {
                result.yMin = v2.getY();
                result.yMax = v1.getY();
            }

            return result;
        }

        // For arcs we don't compute the actual extents which is slower, instead we create an approximate
        // bounding box from the rectangle formed by extending the chord by the sagitta, NOTE: this
        // approximate bounding box is always equal to or bigger than the true bounding box
        double b = v1.bulge();
        double offsX = b * (v2.getY() - v1.getY()) / 2.0;
        double offsY = -b * (v2.getX() - v1.getX()) / 2.0;

        double pt1X = v1.getX() + offsX;
        double pt2X = v2.getX() + offsX;
        double pt1Y = v1.getY() + offsY;
        double pt2Y = v2.getY() + offsY;

        double endPointXMin, endPointXMax;
        if (v1.getX() < v2.getX()) {
            endPointXMin = v1.getX();
            endPointXMax = v2.getX();
        } else {
            endPointXMin = v2.getX();
            endPointXMax = v1.getX();
        }

        double ptXMin, ptXMax;
        if (pt1X < pt2X) {
            ptXMin = pt1X;
            ptXMax = pt2X;
        } else {
            ptXMin = pt2X;
            ptXMax = pt1X;
        }

        double endPointYMin, endPointYMax;
        if (v1.getY() < v2.getY()) {
            endPointYMin = v1.getY();
            endPointYMax = v2.getY();
        } else {
            endPointYMin = v2.getY();
            endPointYMax = v1.getY();
        }

        double ptYMin, ptYMax;
        if (pt1Y < pt2Y) {
            ptYMin = pt1Y;
            ptYMax = pt2Y;
        } else {
            ptYMin = pt2Y;
            ptYMax = pt1Y;
        }

        result.xMin = Math.min(endPointXMin, ptXMin);
        result.yMin = Math.min(endPointYMin, ptYMin);
        result.xMax = Math.max(endPointXMax, ptXMax);
        result.yMax = Math.max(endPointYMax, ptYMax);
        return result;
    }
    /// Split the segment defined by v1 to v2 at some point defined along it.

    public static SplitResult splitAtPoint(final PlineVertex v1, final PlineVertex v2,
                                           final Point2D point) {
        SplitResult result = new SplitResult();
        if (v1.bulgeIsZero()) {
            result.updatedStart = v1;
            result.splitVertex = new PlineVertex(point, 0.0);
        } else if (fuzzyEqual(v1.pos(), v2.pos(), Utils.realPrecision) ||
                fuzzyEqual(v1.pos(), point, Utils.realPrecision)) {
            result.updatedStart = new PlineVertex(point, 0.0);
            result.splitVertex = new PlineVertex(point, v1.bulge());
        } else if (fuzzyEqual(v2.pos(), point, Utils.realPrecision)) {
            result.updatedStart = v1;
            result.splitVertex = new PlineVertex(v2.pos(), 0.0);
        } else {
            BulgeConversionFunctions.ArcRadiusAndCenter radiusAndCenter = arcRadiusAndCenter(v1, v2);
            Point2D arcCenter = radiusAndCenter.center;
            double a = angle(arcCenter, point);
            double arcStartAngle = angle(arcCenter, v1.pos());
            double theta1 = Utils.deltaAngle(arcStartAngle, a);
            double bulge1 = Math.tan(theta1 / 4.0);
            double arcEndAngle = angle(arcCenter, v2.pos());
            double theta2 = Utils.deltaAngle(a, arcEndAngle);
            double bulge2 = Math.tan(theta2 / 4.0);

            result.updatedStart = new PlineVertex(v1.pos(), bulge1);
            result.splitVertex = new PlineVertex(point, bulge2);
        }

        return result;
    }
}
