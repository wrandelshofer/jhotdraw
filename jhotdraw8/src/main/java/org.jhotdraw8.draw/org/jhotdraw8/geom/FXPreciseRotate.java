/*
 * @(#)FXPreciseRotate.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;

/**
 * Same as class {@link Rotate} but treats 180 degree angles
 * specially for better numeric precision.
 */
public class FXPreciseRotate extends Rotate {
    public FXPreciseRotate(double r, double x, double y) {
        super(r, x, y);
    }

    public FXPreciseRotate(double r) {
        this(r, 0, 0);
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
            cos = Geom.cosDegrees(angle);
            sin = Geom.sinDegrees(angle);

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
    public double getMxx() {
        return computeMatrix().mxx;
    }

    @Override
    public double getMxy() {
        return computeMatrix().mxy;
    }

    @Override
    public double getTx() {
        return computeMatrix().tx;
    }

    @Override
    public double getMyx() {
        return computeMatrix().myx;
    }

    @Override
    public double getMyy() {
        return computeMatrix().myy;
    }

    @Override
    public double getTy() {
        return computeMatrix().ty;
    }


    private static class Matrix {
        final double mxx, mxy, tx, myx, myy, ty;

        public Matrix(double mxx, double mxy, double tx, double myx, double myy, double ty) {
            this.mxx = mxx;
            this.mxy = mxy;
            this.tx = tx;
            this.myx = myx;
            this.myy = myy;
            this.ty = ty;
        }
    }

    /**
     * @return mxx, mxy, tx, myx, myy, ty
     */
    private Matrix computeMatrix() {
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
                return new Matrix(super.getMxx(),
                        super.getMxy(),
                        super.getTx(),
                        super.getMyx(),
                        super.getMyy(),
                        super.getTy());
            }

            mxx = cos;
            mxy = -sin;
            tx = px * (1 - cos) + py * sin;
            myx = sin;
            myy = cos;
            ty = py * (1 - cos) - px * sin;

            return new Matrix(mxx, mxy, tx, myx, myy, ty);
        }

        return new Matrix(super.getMxx(),
                super.getMxy(),
                super.getTx(),
                super.getMyx(),
                super.getMyy(),
                super.getTy());
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
