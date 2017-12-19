/* @(#)OffsetStrokeBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.util.ArrayList;
import javafx.geometry.Point2D;

/**
 * OffsetPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class OffsetPathBuilder extends AbstractPathBuilder {

    private boolean needsMoveTo = false;
    private final PathBuilder target;
    private final double offset;
    private final ArrayList<Point2D> segments = new ArrayList<>();
    private final ArrayList<Point2D> disks = new ArrayList<>();

    public OffsetPathBuilder(PathBuilder target, double offset) {
        this.target = target;
        this.offset = offset;
    }

    @Override
    protected void doClosePath() {
        if (needsMoveTo) {
            target.moveTo(getLastX(), getLastY());
            needsMoveTo = false;
        } else {
            target.lineTo(getLastX(), getLastY());// bbvel joint
        }
        flush();
        target.closePath();
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        // FIXME should flatten curve
    }

    @Override
    protected void doLineTo(double x, double y) {
        Point2D shift = new Point2D(y - getLastY(), getLastX() - x).normalize().multiply(offset);
        if (needsMoveTo) {
            segments.add(new Point2D(getLastX() + shift.getX(), getLastY() + shift.getY()));
            needsMoveTo = false;
        } else {
            segments.add(new Point2D(getLastX() + shift.getX(), getLastY() + shift.getY()));// bevel joint
        }
        segments.add(new Point2D(x + shift.getX(), y + shift.getY()));
    }

    @Override
    protected void doMoveTo(double x, double y) {
        flush();
        needsMoveTo = true;
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        // FIXME should flatten curve

    }

    private void flush() {
        // clip lines
        for (int i = 0, n = segments.size(); i < n - 2; ++i) {
            Point2D a1 = segments.get(i);
            Point2D a2 = segments.get(i + 1);
            for (int j = n-2;j>=i + 1; --j) {
                Point2D b1 = segments.get(j);
                Point2D b2 = segments.get(j + 1);
                Intersection inter = Intersections.intersectLineLine(a1, a2, b1, b2);
                if (inter.getStatus() == Intersection.Status.INTERSECTION) {
                    Point2D p = inter.getPoints().iterator().next();
                    segments.set(i + 1, p);
                    segments.set(j, p);
                    // delete all points between i and j
                    for (int k = i + 1; k < j; k++) {
                        segments.remove(i + 1);
                    }
                    n -= j - 1 - i;
                    j -= j - 1 - i;
                }
            }
        }

        for (int i = 0, n = segments.size(); i < n; i++) {
            Point2D p = segments.get(i);
            if (i == 0) {
                target.moveTo(p);
            } else {
                target.lineTo(p);
            }
        }

        segments.clear();
        disks.clear();
    }

    @Override
    protected void doPathDone() {
        if (needsMoveTo) {
            target.moveTo(getLastX(), getLastY());
            needsMoveTo = false;
        }
        flush();
        target.pathDone();
    }
}
