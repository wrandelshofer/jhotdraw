/*
 * @(#)AbstractPathBuilder.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;

/**
 * AbstractPathBuilder.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractPathBuilder implements PathBuilder {

    private double lastX, lastY;
    private double lastCX, lastCY;

    @Override
    public void arcTo(double radiusX, double radiusY, double xAxisRotation, double x, double y, boolean largeArcFlag, boolean sweepFlag) {
        doArcTo(radiusX, radiusY, xAxisRotation, x, y, largeArcFlag, sweepFlag);
        lastX = x;
        lastY = y;
        lastCX = x;
        lastCY = y;
    }

    @Override
    public final void closePath() {
        doClosePath();
    }

    @Override
    public final void curveTo(double x1, double y1, double x2, double y2, double x, double y) {
        doCurveTo(x1, y1, x2, y2, x, y);
        lastX = x;
        lastY = y;
        lastCX = x2;
        lastCY = y2;
    }

    protected void doArcTo(double radiusX, double radiusY, double xAxisRotation, double x, double y, boolean largeArcFlag, boolean sweepFlag) {
        PathBuilder.super.arcTo(radiusX, radiusY, xAxisRotation, x, y, largeArcFlag, sweepFlag);
    }

    protected abstract void doClosePath();

    protected abstract void doPathDone();

    protected abstract void doCurveTo(double x1, double y1, double x2, double y2, double x, double y);

    protected abstract void doLineTo(double x, double y);

    protected abstract void doMoveTo(double x, double y);

    protected abstract void doQuadTo(double x1, double y1, double x, double y);

    protected void doSmoothCurveTo(double x1, double y1, double x2, double y2, double x, double y) {
        doCurveTo(x1, y1, x2, y2, x, y);
    }

    protected void doSmoothQuadTo(double x1, double y1, double x, double y) {
        doQuadTo(x1, y1, x, y);
    }

    @Override
    public void pathDone() {
        doPathDone();
    }

    @NonNull
    public Point2D getLastPoint() {
        return new Point2D(lastX, lastY);
    }

    @Override
    public double getLastX() {
        return lastX;
    }

    @Override
    public double getLastY() {
        return lastY;
    }

    @Override
    public double getLastCX() {
        return lastCX;
    }

    @Override
    public double getLastCY() {
        return lastCY;
    }

    @Override
    public final void lineTo(double x, double y) {
        doLineTo(x, y);
        lastX = x;
        lastY = y;
        lastCX = x;
        lastCY = y;
    }

    @Override
    public final void moveTo(double x, double y) {
        doMoveTo(x, y);
        lastX = x;
        lastY = y;
        lastCX = x;
        lastCY = y;
    }

    @Override
    public final void quadTo(double x1, double y1, double x, double y) {
        doQuadTo(x1, y1, x, y);
        lastX = x;
        lastY = y;
        lastCX = x1;
        lastCY = y1;
    }

    @Override
    public final void smoothCurveTo(double x2, double y2, double x, double y) {
        doSmoothCurveTo(
                lastX - lastCX + lastX, lastY - lastCY + lastY, x2, y2, x, y);
        lastX = x;
        lastY = y;
        lastCX = x2;
        lastCY = y2;
    }

    @Override
    public final void smoothQuadTo(double x, double y) {
        doSmoothQuadTo(
                lastX - lastCX + lastX, lastY - lastCY + lastY, x, y);
        lastCX = lastX - lastCX + lastX;
        lastCY = lastY - lastCY + lastY;
        lastX = x;
        lastY = y;
    }

    protected void setLastX(double lastX) {
        this.lastX = lastX;
    }

    protected void setLastY(double lastY) {
        this.lastY = lastY;
    }

    protected void setLastCX(double lastCX) {
        this.lastCX = lastCX;
    }

    protected void setLastCY(double lastCY) {
        this.lastCY = lastCY;
    }
}
