/*
 * @(#)OffsetPathBuilder.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * OffsetPathBuilder.
 * <p>
 * If we draw an offset path, then for each original path segment {@code a1, a2},
 * we draw a shifted path segment at {@code a1', a2'}.
 * <p>
 * We can visualize a shape {@code a1, a2, a1', a2'} that contains the offset
 * region.
 * <pre>
 * example: If the path segment is a lineTo, the 'offset region shape' encompasses
 * the rectangle from the original lineTo to the perpendicularly shifted
 * lineTo.
 *
 * offset lineTo:
 *
 *                     a1'                 a1'
 *  offset lineTo:     +══════════════════⇒+
 *                     ↑                   ↑
 *                     | offset            | offset
 *  original lineTo:   +------------------→+
 *                     a1                  a2
 * </pre>
 * <p>
 * The next path segment {@code b1, b2} can follow with an angle that may
 * make the next offset region shape overlap with the previous one,
 * or that may form a gap.
 * <pre>
 * example: offset region shapes overlap:       b2'
 *                                             ╱╱╲
 *                                            ╱╱  ╲
 *                                           ╱╱    ╲
 *                                          ╱╱      +b2
 *                                         ╱╱      ╱
 *                       a1'              ╱╱a1'   ╱
 *  offset lineTo's:     +═══════════════╱╱═⇒+   ╱
 *                       ↑            b1'+╲  ↑  ╱
 *                       |                 ╲ | ╱
 *  original lineTo's:   +------------------╲+╱
 *                       a1                  a2
 *                                           b1
 * </pre>
 * <pre>
 * example: offset region shapes do not overlap, forming a gap.
 *
 *                       a1'                a2'  b1'
 *  offset lineTo's:     +══════════════════⇒+
 *                       ↑                   ↑  ╱+╲╲
 *                       |                   | ╱   ╲╲
 *  original lineTo's:   +-------------------+╱     ╲╲
 *                       a1                 a2╲      + b2'
 *                                          b1 ╲    ╱
 *                                              ╲  ╱
 *                                               +╱
 *                                               b2
 * </pre>
 * The gap must be filled in with a join segment (a2/b1, b1', a2' in the
 * example above). The join segment is a new offset region shape.
 * <p>
 * We do not want to have any offset lines inside any offset region shape.
 * Therefore we clip all offset lines with all offset region shapes.
 * <p>
 * Performance: This algorithm is O(n^2).
 *
 * @author Werner Randelshofer
 */
public class OffsetPathBuilder extends AbstractPathBuilder {

    private boolean needsMoveTo = false;
    private final PathBuilder target;
    private final double offset;
    private final ArrayList<Point2D> segments = new ArrayList<>();

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
        // XXX the following algorithm only works if the original path does not self-intersect:

        List<Point2D> originalSegments = new ArrayList<>(segments);

        // 1. Clip offset line segment a1' a2' with any following offset line segments b1' b2'. O(n^2).
        for (int i = 0, n = segments.size(); i < n - 2; ++i) {
            Point2D a1p = segments.get(i);
            Point2D a2p = segments.get(i + 1);
            for (int j = n - 2; j >= i + 1; --j) {
                Point2D b1p = segments.get(j);
                Point2D b2p = segments.get(j + 1);
                Intersection inter = Intersections.intersectLineLine(a1p, a2p, b1p, b2p);
                if (inter.getStatus() == Intersection.Status.INTERSECTION) {
                    Point2D p = inter.getPoints().iterator().next();
                    segments.set(j, p);
                    // delete all points between i and j
                    if (j > i + 1) {
                        segments.subList(i + 1, j).clear();
                    }
                    n -= j - 1 - i;
                    j -= j - 1 - i;
                }
            }
        }

        // 2. Clip offset line with any a1 a1' line and with any a2 a2' line. O(n^2).
        for (int i = 0, n = originalSegments.size(); i < n - 2; i += 2) {
            Point2D a1p = originalSegments.get(i);
            Point2D a2p = originalSegments.get(i + 1);
            Point2D shift = new Point2D(a2p.getY() - a1p.getY(), a1p.getX() - a2p.getX()).normalize().multiply(offset);
            Point2D a1 = a1p.subtract(shift);
            Point2D a2 = a2p.subtract(shift);
            final double eps = 1e-6;
            for (int j = 0, m = segments.size(); j < m - 1; j++) {
                Point2D b1p = segments.get(j);
                Point2D b2p = segments.get(j + 1);
                Intersection inter = Intersections.intersectLineLine(b1p, b2p, a1, a1p);
                if (inter.getStatus() == Intersection.Status.INTERSECTION) {
                    if (inter.getFirstT() > 0.0 + eps && inter.getFirstT() < 1.0 - eps) {
                        segments.set(j + 1, b2p = inter.getFirstPoint());
                    }
                }
                inter = Intersections.intersectLineLine(b1p, b2p, a2, a2p);
                if (inter.getStatus() == Intersection.Status.INTERSECTION) {
                    if (inter.getFirstT() > 0.0 + eps && inter.getFirstT() < 1.0 - eps) {
                        final Intersection.IntersectionPoint first = inter.getFirst();
                        segments.set(j, b1p = inter.getFirstPoint());
                    }
                }
            }

        }


        // Draw segments
        for (int i = 0, n = segments.size(); i < n; i++) {
            Point2D p = segments.get(i);
            if (i == 0) {
                target.moveTo(p);
            } else {
                target.lineTo(p);
            }
        }

        segments.clear();
    }

    private void flushOld() {
        // XXX the following algorithm only works if the original path does not self-intersect:

        // 1. Clip offset line segment a1' a2' with any following offset line segments b1' b2'.
        for (int i = 0, n = segments.size(); i < n - 2; ++i) {
            Point2D a1p = segments.get(i);
            Point2D a2p = segments.get(i + 1);
            for (int j = n - 2; j >= i + 1; --j) {
                Point2D b1p = segments.get(j);
                Point2D b2p = segments.get(j + 1);
                Intersection inter = Intersections.intersectLineLine(a1p, a2p, b1p, b2p);
                if (inter.getStatus() == Intersection.Status.INTERSECTION) {
                    Point2D p = inter.getPoints().iterator().next();
                    //segments.set(i + 1, p);
                    segments.set(j, p);
                    // delete all points between i and j
                    if (j > i + 1) {
                        segments.subList(i + 1, j).clear();
                    }
                    n -= j - 1 - i;
                    j -= j - 1 - i;
                }
            }
        }

        // Draw segments
        for (int i = 0, n = segments.size(); i < n; i++) {
            Point2D p = segments.get(i);
            if (i == 0) {
                target.moveTo(p);
            } else {
                target.lineTo(p);
            }
        }

        segments.clear();
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
