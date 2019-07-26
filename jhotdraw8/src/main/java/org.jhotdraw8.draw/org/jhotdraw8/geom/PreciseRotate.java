/*
 * @(#)PreciseRotate.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;

/**
 * Same as class {@link Rotate} but treats 180 degree angles
 * specially for better numeric stability.
 */
public class PreciseRotate extends Rotate {
    public PreciseRotate(double r, double x, double y) {
        super(r, x, y);
    }

    @Override
    public Point2D inverseTransform(double x, double y) {
        Point3D axis = getAxis();
        if (axis == Z_AXIS ||
                (axis.getX() == 0.0 &&
                        axis.getY() == 0.0 &&
                        axis.getZ() > 0.0)) {

            double mxx, mxy, tx, myx, myy, ty, cos, sin;
            double px = getPivotX();
            double py = getPivotY();

            // 2D case
            double angle = getAngle();
            if (angle == 180 || angle == -180) {
                cos = -1.0;
                sin = 0.0;
            } else if (angle == 90) {
                cos = 0.0;
                sin = -1;
            } else if (angle == -90 || angle == 270) {
                cos = 0.0;
                sin = 1;
            } else {
                return super.transform(x, y);
            }

            mxx = cos;
            mxy = -sin;
            tx = px * (1 - cos) + py * sin;
            myx = sin;
            myy = cos;
            ty = py * (1 - cos) - px * sin;

            return new Point2D(
                    mxx * x + mxy * y + tx,
                    myx * x + myy * y + ty);
        }
        return super.transform(x, y);
    }


    @Override
    public Point2D transform(double x, double y) {
        Point3D axis = getAxis();
        if (axis == Z_AXIS ||
                (axis.getX() == 0.0 &&
                        axis.getY() == 0.0 &&
                        axis.getZ() > 0.0)) {

            double mxx, mxy, tx, myx, myy, ty, cos, sin;
            double px = getPivotX();
            double py = getPivotY();

            // 2D case
            double angle = getAngle();
            if (angle == 180 || angle == -180) {
                cos = -1.0;
                sin = 0.0;
            } else if (angle == 90) {
                cos = 0.0;
                sin = 1;
            } else if (angle == -90 || angle == 270) {
                cos = 0.0;
                sin = -1;
            } else {
                return super.transform(x, y);
            }

            mxx = cos;
            mxy = -sin;
            tx = px * (1 - cos) + py * sin;
            myx = sin;
            myy = cos;
            ty = py * (1 - cos) - px * sin;

            return new Point2D(
                    mxx * x + mxy * y + tx,
                    myx * x + myy * y + ty);
        }
        return super.transform(x, y);
    }
}
