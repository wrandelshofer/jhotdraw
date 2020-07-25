/*
 * @(#)CutStartPathBuilder.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;


import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.intersect.IntersectCircleCubicCurve;
import org.jhotdraw8.geom.intersect.IntersectCircleLine;
import org.jhotdraw8.geom.intersect.IntersectCircleQuadraticCurve;
import org.jhotdraw8.geom.intersect.IntersectionResultEx;

import java.awt.geom.Point2D;

/**
 * Clips the start of the path on the specified circle.
 *
 * @author Werner Randelshofer
 */
public class CutStartPathBuilder extends AbstractPathBuilder {
    /**
     * We need this state machine, so that we can properly
     * handle a path which does not start with a MOVE_TO.
     */
    private enum State {
        EXPECTING_INITIAL_MOVETO,
        CUTTING_START,
        CUT_DONE
    }

    private final double radius;
    private final PathBuilder out;
    private double cx;
    private double cy;
    @NonNull
    private State state = State.EXPECTING_INITIAL_MOVETO;

    public CutStartPathBuilder(PathBuilder out, double radius) {
        this.radius = radius;
        this.out = out;
    }

    @Override
    protected void doClosePath() {
        state = State.CUT_DONE;
        out.closePath();
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        if (state == State.CUT_DONE) {
            out.curveTo(x1, y1, x2, y2, x3, y3);
            return;
        }
        IntersectionResultEx i = IntersectCircleCubicCurve.intersectCubicCurveCircleEx(getLastX(), getLastY(), x1, y1, x2, y2, x3, y3, cx, cy, radius);
        switch (i.getStatus()) {
        case INTERSECTION:
            double t = i.getLast().getArgumentA();
            out.moveTo(i.getLast().getX(), i.getLast().getY());
            BezierCurves.splitCubicCurveTo(getLastX(), getLastY(), x1, y1, x2, y2, x3, y3, t, null, out::curveTo);
            break;
        case NO_INTERSECTION_INSIDE:
            cx = x3;
            cy = y3;
            break;
        case NO_INTERSECTION_OUTSIDE:
        case NO_INTERSECTION_TANGENT:
        default:
            out.moveTo(getLastX(), getLastY());
                state = State.CUT_DONE;
                out.curveTo(x1, y1, x2, y2, x3, y3);
                break;
        }
    }

    @Override
    protected void doPathDone() {
        state = State.CUT_DONE;
        out.pathDone();
    }

    @Override
    protected void doLineTo(double x, double y) {
        if (state != State.CUTTING_START) {
            out.lineTo(x, y);
            return;
        }
        IntersectionResultEx i = IntersectCircleLine.intersectLineCircleEx(getLastX(), getLastY(), x, y, cx, cy, radius);
        switch (i.getStatus()) {
            case INTERSECTION:
                Point2D p = i.getLast();
                out.moveTo(p.getX(), p.getY());
                out.lineTo(x, y);
                state = State.CUT_DONE;
                break;
            case NO_INTERSECTION_INSIDE:
                // skip lineTo
                break;
            case NO_INTERSECTION_OUTSIDE:
            case NO_INTERSECTION_TANGENT:
            default:
                out.moveTo(getLastX(), getLastY());
                state = State.CUT_DONE;
                out.lineTo(x, y);
                break;
        }
    }

    @Override
    protected void doMoveTo(double x, double y) {
        if (state == State.EXPECTING_INITIAL_MOVETO) {
            state = State.CUTTING_START;
        }
        if (state != State.CUTTING_START) {
            out.moveTo(x, y);
        } else {
            cx = x;
            cy = y;
        }
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        if (state != State.CUTTING_START) {
            out.quadTo(x1, y1, x2, y2);
            return;
        }
        IntersectionResultEx i = IntersectCircleQuadraticCurve.intersectQuadraticCurveCircleEx(getLastX(), getLastY(), x1, y1, x2, y2, cx, cy, radius);
        switch (i.getStatus()) {
        case INTERSECTION:
            double t = i.getLast().getArgumentA();
            out.moveTo(i.getLast().getX(), i.getLast().getY());
            BezierCurves.splitQuadCurveTo(getLastX(), getLastY(), x1, y1, x2, y2, t, null, out::quadTo);
            state = State.CUT_DONE;
            break;
        case NO_INTERSECTION_INSIDE:
            cx = x2;
            cy = y2;
            break;
        case NO_INTERSECTION_OUTSIDE:
        case NO_INTERSECTION_TANGENT:
        default:
                out.moveTo(getLastX(), getLastY());
                state = State.CUT_DONE;
                out.quadTo(x1, y1, x2, y2);
                break;
        }
    }

}
