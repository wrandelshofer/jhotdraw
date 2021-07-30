/*
 * @(#)OffsetPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;


import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.geom.intersect.IntersectLineLine;
import org.jhotdraw8.geom.intersect.IntersectionPointEx;
import org.jhotdraw8.geom.intersect.IntersectionResultEx;
import org.jhotdraw8.geom.intersect.IntersectionStatus;

import java.awt.geom.Point2D;
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
public class OffsetPathBuilder<T> extends AbstractPathBuilder<T> {

    private boolean needsMoveTo = true;
    private final PathBuilder<T> target;
    private final double offset;
    private double moveX, moveY;
    /**
     * Line segments.
     * <p>
     * Each element is a double array.
     * The length of the array depends on the type of the line segment:
     * <dl>
     *     <dd>2</dd><dt>line to x,y</dt>
     *     <dd>4</dd><dt>quad to cx,cy, x,y</dt>
     *     <dd>6</dd><dt>curve to cx1,cy1, cx2,cy2, x,y</dt>
     * </dl>
     */
    private final ArrayList<double[]> segments = new ArrayList<>();

    public OffsetPathBuilder(PathBuilder<T> target, double offset) {
        this.target = target;
        this.offset = offset;
    }

    @Override
    protected void doClosePath() {
        if (!needsMoveTo) {
            segments.add(new double[]{moveX, moveY});
        }
        flush();
        target.closePath();
        needsMoveTo = true;
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        // FIXME should compute offset curve
    }

    @Override
    protected void doLineTo(double x, double y) {
        Point2D.Double shift =
                Points2D.multiply(Points2D.normalize(new Point2D.Double(y - getLastY(), getLastX() - x)), offset);
        if (needsMoveTo) {
            segments.add(new double[]{getLastX() + shift.getX(), getLastY() + shift.getY()});
            needsMoveTo = false;
        } else {
            segments.add(new double[]{getLastX() + shift.getX(), getLastY() + shift.getY()});// bevel joint
        }
        segments.add(new double[]{x + shift.getX(), y + shift.getY()});
    }

    @Override
    protected void doMoveTo(double x, double y) {
        flush();
        moveX = x;
        moveY = y;
        needsMoveTo = true;
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        // FIXME should compute offset curve

    }


    private void flush() {
        // XXX the following algorithm only works if the original path does not self-intersect:

        List<double[]> originalSegments = new ArrayList<>(segments);

        // 1. Clip offset line segment a1' a2' with any following offset line segments b1' b2'. O(n^2).
        for (int i = 0, n = segments.size(); i < n - 2; ++i) {
            Point2D.Double a1p = getXY(segments.get(i));
            Point2D.Double a2p = getXY(segments.get(i + 1));
            for (int j = n - 2; j >= i + 1; --j) {
                Point2D.Double b1p = getXY(segments.get(j));
                Point2D.Double b2p = getXY(segments.get(j + 1));
                IntersectionResultEx inter = IntersectLineLine.intersectLineLineEx(a1p, a2p, b1p, b2p);
                if (inter.getStatus() == IntersectionStatus.INTERSECTION) {
                    Point2D.Double p = inter.getFirst();
                    segments.set(j, new double[]{p.getX(), p.getY()});
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
            Point2D.Double a1p = getXY(originalSegments.get(i));
            Point2D.Double a2p = getXY(originalSegments.get(i + 1));
            Point2D.Double shift =
                    Points2D.multiply(Points2D.normalize(new Point2D.Double(a2p.getY() - a1p.getY(), a1p.getX() - a2p.getX())), offset);
            Point2D.Double a1 = Points2D.subtract(a1p, shift);
            Point2D.Double a2 = Points2D.subtract(a2p, shift);
            final double eps = 1e-6;
            for (int j = 0, m = segments.size(); j < m - 1; j++) {
                Point2D.Double b1p = getXY(segments.get(j));
                Point2D.Double b2p = getXY(segments.get(j + 1));
                IntersectionResultEx inter = IntersectLineLine.intersectLineLineEx(b1p, b2p, a1, a1p);
                if (inter.getStatus() == IntersectionStatus.INTERSECTION) {
                    if (inter.getFirst().getArgumentA() > 0.0 + eps && inter.getFirst().getArgumentA() < 1.0 - eps) {
                        b2p = inter.getFirst();
                        segments.set(j + 1, new double[]{b2p.getX(), b2p.getY()});
                    }
                }
                inter = IntersectLineLine.intersectLineLineEx(b1p, b2p, a2, a2p);
                if (inter.getStatus() == IntersectionStatus.INTERSECTION) {
                    if (inter.getFirst().getArgumentA() > 0.0 + eps && inter.getFirst().getArgumentA() < 1.0 - eps) {
                        final IntersectionPointEx first = inter.getFirst();
                        b1p = inter.getFirst();
                        segments.set(j, new double[]{b1p.getX(), b1p.getY()});
                    }
                }
            }
        }

        // Draw segments
        for (int i = 0, n = segments.size(); i < n; i++) {
            Point2D.Double p = getXY(segments.get(i));
            if (i == 0) {
                target.moveTo(p.getX(), p.getY());
            } else {
                target.lineTo(p.getX(), p.getY());
            }
        }

        segments.clear();
    }

    private Point2D.Double getXY(double[] segment) {
        return new Point2D.Double(segment[segment.length - 2], segment[segment.length - 1]);
    }

    private void flushOld() {
        // XXX the following algorithm only works if the original path does not self-intersect:

        // 1. Clip offset line segment a1' a2' with any following offset line segments b1' b2'.
        for (int i = 0, n = segments.size(); i < n - 2; ++i) {
            Point2D.Double a1p = getXY(segments.get(i));
            Point2D.Double a2p = getXY(segments.get(i + 1));
            for (int j = n - 2; j >= i + 1; --j) {
                Point2D.Double b1p = getXY(segments.get(j));
                Point2D.Double b2p = getXY(segments.get(j + 1));
                IntersectionResultEx inter = IntersectLineLine.intersectLineLineEx(a1p, a2p, b1p, b2p);
                if (inter.getStatus() == IntersectionStatus.INTERSECTION) {
                    Point2D.Double p = inter.iterator().next();
                    //segments.set(i + 1, p);
                    segments.set(j, new double[]{p.getX(), p.getY()});
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
            Point2D.Double p = getXY(segments.get(i));
            if (i == 0) {
                target.moveTo(p.getX(), p.getY());
            } else {
                target.lineTo(p.getX(), p.getY());
            }
        }

        segments.clear();
    }

    @Override
    protected void doPathDone() {
        if (needsMoveTo) {
            target.moveTo(getLastX(), getLastY());
        }
        needsMoveTo = true;
        flush();
        target.pathDone();
    }

    @Override
    public @Nullable T build() {
        return target.build();
    }
}
