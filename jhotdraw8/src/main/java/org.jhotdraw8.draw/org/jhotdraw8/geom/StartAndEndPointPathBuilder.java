/*
 * @(#)StartAndEndPointPathBuilder.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.geom;

/**
 * StartAndEndPointPathBuilder gets the start and end point of a path and their tangents.
 *
 * @author Werner Randelshofer
 */
public class StartAndEndPointPathBuilder extends AbstractPathBuilder {
    private double startX;
    private double startY;
    private double startTangentX;
    private double startTangentY;
    private double endX;
    private double endY;
    private double endTangentX;
    private double endTangentY;
    private boolean startDone;

    @Override
    protected void doClosePath() {
        //empty
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        if (!startDone) {
            startX = getLastX();
            startY = getLastY();
            startTangentX = startX - x1;
            startTangentY = startY - y1;
            startDone = true;
        }
        endX = x3;
        endY = y3;
        endTangentX = x3 - x2;
        endTangentY = y3 - y2;
    }

    @Override
    protected void doPathDone() {
        //empty
    }

    @Override
    protected void doLineTo(double x, double y) {
        if (!startDone) {
            startX = getLastX();
            startY = getLastY();
            startTangentX = startX - x;
            startTangentY = startY - y;
            startDone = true;
        }
        endX = x;
        endY = y;
        endTangentX = x - getLastX();
        endTangentY = y - getLastY();
    }

    @Override
    protected void doMoveTo(double x, double y) {
// empty
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        if (!startDone) {
            startX = getLastX();
            startY = getLastY();
            startTangentX = startX - x1;
            startTangentY = startY - y1;
            startDone = true;
        }
        endX = x2;
        endY = y2;
        endTangentX = x2 - x1;
        endTangentY = y2 - y1;
    }

    public double getEndTangentX() {
        return endTangentX;
    }

    public double getEndTangentY() {
        return endTangentY;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    public double getStartTangentX() {
        return startTangentX;
    }

    public double getStartTangentY() {
        return startTangentY;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public boolean isIsStartDone() {
        return startDone;
    }

}
