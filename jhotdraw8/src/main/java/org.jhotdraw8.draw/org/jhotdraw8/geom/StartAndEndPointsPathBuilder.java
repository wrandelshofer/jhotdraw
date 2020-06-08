/*
 * @(#)StartAndEndPointsPathBuilder.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class StartAndEndPointsPathBuilder extends AbstractPathBuilder {
    private final List<PointAndTangent> startPoints = new ArrayList<>();
    private final List<PointAndTangent> endPoints = new ArrayList<>();

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
        startDone = false;
    }

    @Override
    protected void doPathDone() {
        if (startDone) {
            startPoints.add(new PointAndTangent(startX, startY, startTangentX, startTangentY));
            endPoints.add(new PointAndTangent(endX, endY, endTangentX, endTangentY));
        }
        startDone = false;
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
        if (startDone) {
            startPoints.add(new PointAndTangent(startX, startY, startTangentX, startTangentY));
            endPoints.add(new PointAndTangent(endX, endY, endTangentX, endTangentY));
            startDone = false;
        }
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

    @NonNull
    public List<PointAndTangent> getStartPoints() {
        return startPoints;
    }

    @NonNull
    public List<PointAndTangent> getEndPoints() {
        return endPoints;
    }
}
