/* @(#)ClipStartPathBuilder.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;

/**
 * Clips the start of the path on the specified circle.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ClipStartPathBuilder extends AbstractPathBuilder {

    private final int radius;
    private final PathBuilder out;
    private final int cx;
    private final int cy;
    private boolean done;

    public ClipStartPathBuilder(PathBuilder out, int cx, int cy, int radius) {
        this.radius = radius;
        this.out = out;
        this.cx = cx;
        this.cy = cy;
    }

    @Override
    protected void doClosePath() {
        done = true;
        out.closePath();
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        if (done) {
            out.curveTo(x1, y1, x2, y2, x3, y3);
            return;
        }
        Intersection i = Intersection.intersectBezier3Circle(getLastX(), getLastY(), x1, y1, x2, y2, x3, y3, cx, cy, radius);
        switch (i.getStatus()) {
            case INTERSECTION:
                double t = i.getLastT();
                out.moveTo(i.getLastPoint());
                Geom.splitCubicCurve(getLastX(), getLastY(), x1, y1, x2, y2, x3, y3, t, null, out::curveTo);
                break;
            case NO_INTERSECTION_INSIDE:
                // skip lineTo
                break;
            case NO_INTERSECTION_OUTSIDE:
            case NO_INTERSECTION_TANGENT:
            default:
                out.moveTo(getLastX(), getLastY());
                done = true;
                out.curveTo(x1, y1, x2, y2, x3, y3);
                break;
        }
    }

    @Override
    protected void doFinish() {
        done = true;
        out.finish();
    }

    @Override
    protected void doLineTo(double x, double y) {
        if (done) {
            out.lineTo(x, y);
            return;
        }
        Intersection i = Intersection.intersectLineCircle(getLastX(), getLastY(), x, y, cx, cy, radius);
        switch (i.getStatus()) {
            case INTERSECTION:
                Point2D p = i.getLastPoint();
                out.moveTo(p.getX(), p.getY());
                out.lineTo(x, y);

                break;
            case NO_INTERSECTION_INSIDE:
                // skip lineTo
                break;
            case NO_INTERSECTION_OUTSIDE:
            case NO_INTERSECTION_TANGENT:
            default:
                out.moveTo(getLastX(), getLastY());
                done = true;
                out.lineTo(x, y);
                break;
        }
    }

    @Override
    protected void doMoveTo(double x, double y) {
        if (done) {
            out.moveTo(x, y);
            return;
        }
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        if (done) {
            out.quadTo(x1, y1, x2, y2);
            return;
        }
        Intersection i = Intersection.intersectBezier2Circle(getLastX(), getLastY(), x1, y1, x2, y2, cx, cy, radius);
        switch (i.getStatus()) {
            case INTERSECTION:
                double t = i.getLastT();
                out.moveTo(i.getLastPoint());
                Geom.splitQuadCurve(getLastX(), getLastY(), x1, y1, x2, y2, t, null, out::quadTo);

                break;
            case NO_INTERSECTION_INSIDE:
                // skip lineTo
                break;
            case NO_INTERSECTION_OUTSIDE:
            case NO_INTERSECTION_TANGENT:
            default:
                out.moveTo(getLastX(), getLastY());
                done = true;
                out.quadTo(x1, y1, x2, y2);
                break;
        }
    }

}
