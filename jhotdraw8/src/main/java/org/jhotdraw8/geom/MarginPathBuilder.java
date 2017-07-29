/* @(#)MarginPathBuilder.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;

/**
 * MarginPathBuilder.
 * <p>
 * FIXME this implementation currently only works with straight line segments.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class MarginPathBuilder extends AbstractPathBuilder {

    private final int marginEnd, marginStart;
    private double fromX, fromY;
    private Op prevOp = null;
    private final PathBuilder target;

    public MarginPathBuilder(int marginStart, int marginEnd, PathBuilder target) {
        this.marginStart = marginStart;
        this.marginEnd = marginEnd;
        this.target = target;
    }

    @Override
    protected void doClosePath() {
        doEnd();
        prevOp = Op.ClosePath;
        target.closePath();
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        if (prevOp != null) {
            switch (prevOp) {
                case MoveTo:
                    break;
                case LineTo:
                    break;
                case ClosePath:
                    break;
                case QuadTo:
                    break;
                case CurveTo:
                    break;
                default:
                    throw new UnsupportedOperationException("prevOp:" + prevOp);
            }
        }
        prevOp = Op.CurveTo;
        target.curveTo(x1, y1, x2, y2, x3, y3);
    }

    private void doEnd() {
        if (prevOp != null) {
            switch (prevOp) {
                case MoveTo:
                    break;
                case LineTo:
                    Point2D dir = new Point2D(fromX, fromY).subtract(getLastX(), getLastY()).normalize().multiply(marginEnd);
                    target.lineTo(getLastX() - dir.getX(), getLastY() - dir.getY());
                    break;
                case ClosePath:
                    break;
                case QuadTo:
                    break;
                case CurveTo:
                    break;
                default:
                    throw new UnsupportedOperationException("prevOp:" + prevOp);
            }
            prevOp = null;
        }
    }

    @Override
    protected void doLineTo(double x, double y) {
        if (prevOp != null) {
            switch (prevOp) {
                case MoveTo:
                    Point2D dir = new Point2D(x, y).subtract(getLastX(), getLastY()).normalize().multiply(marginStart);
                    target.moveTo(getLastX() + dir.getX(), getLastY() + dir.getY());
                    break;
                case LineTo:
                    target.lineTo(getLastX(), getLastY());
                    break;
                case ClosePath:
                    break;
                case QuadTo:
                    break;
                case CurveTo:
                    break;
                default:
                    throw new UnsupportedOperationException("prevOp:" + prevOp);
            }
        }

        prevOp = Op.LineTo;
        fromX = getLastX();
        fromY = getLastY();

    }

    @Override
    protected void doMoveTo(double x, double y) {
        prevOp = Op.MoveTo;
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        prevOp = Op.QuadTo;
        target.quadTo(x1, y1, x2, y2);
    }

    @Override
    public void finish() {
        doEnd();
        target.finish();
    }

    private enum Op {
        MoveTo, LineTo, CurveTo, QuadTo, ClosePath
    }

}
