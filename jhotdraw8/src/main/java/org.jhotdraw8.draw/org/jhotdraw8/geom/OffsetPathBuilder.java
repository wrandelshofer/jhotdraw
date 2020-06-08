/*
 * @(#)OffsetPathBuilder.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;

import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 * OffsetPathBuilder.
 * <p>
 * If we draw an offset path, then for each original line segment {@code a, b},
 * we draw a line segment at {@code a', b'}. Since the offset is perpendicular,
 * we can visualize a rectangle {@code a, b, b', a'} that contains the offset
 * region.
 * <pre>
 * offset region rectangle encompasses the rectangle from original lineTo to
 * offset lineTo:
 *
 *                     a1'                 a1'
 *  offset lineTo:     +------------------→+
 *                     ↑                   ↑
 *                     | offset            | offset
 *  original lineTo:   +------------------→+
 *                     a1                  a2
 * </pre>
 * <p>
 * The next line segment can follow with an angle that may
 * make the next offset region rectangle overlap with the previous one.
 * For every {@code b'} point, we get a second {@code b2'} point that
 * belongs to the next line segment.
 * <pre>
 * offset region rectangles overlap:           b2'
 *                                             ╱+╲
 *                                            ╱   ╲
 *                                           ╱     ╲
 *                                          ╱       +b2
 *                                         ╱       ╱
 *                       a1'              ╱ a1'   ╱
 *  offset lineTo's:     +---------------╱--→+   ╱
 *                       ↑            b1'+╲  ↑  ╱
 *                       |                 ╲ | ╱
 *  original lineTo's:   +------------------╲+╱
 *                       a1                  a2
 *                                           b1
 * </pre>
 * <pre>
 * offset region rectangles do not overlap:
 *
 *                       a1'                a2'  b1'
 *  offset lineTo's:     +------------------→+
 *                       ↑                   ↑  ╱+╲
 *                       |                   | ╱   ╲
 *  original lineTo's:   +-------------------+╱     ╲
 *                       a1                 a2╲      + b2'
 *                                          b1 ╲    ╱
 *                                              ╲  ╱
 *                                               +╱
 *                                               b2
 * </pre>
 * We do not want to have any lines inside any offset region rectangle,
 * unless the original path self-intersects.
 * <p>
 * The following algorithm works if the original path does not self-intersect:
 * <ol>
 *     <li>Remove last contained point:
 *     while the last point {@code b2'} is inside the previous rectangle {@code a1 a2 a2' a'}
 *     then remove it.</li>
 *     <li>Clip offset line segments: if {@code a1' a2'} intersects with any following {@code b1' b2'}, then
 *     remove all line segments in between, and clip the two line segments
 *     at the intersection point {@code i'}, giving {@code a1' i'}, {@code i' b2'}.</li>
 * </ol>
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

        // 1. Remove last contained point b2' while it is contained in previous rectangle
        // a1 a2 a2' a1'
        final Path2D.Double path = new Path2D.Double();
        for (int i = segments.size() - 1; i >= 3; --i) {
            Point2D b2p = segments.get(i);
            Point2D a1p = segments.get(i - 3);
            Point2D a2p = segments.get(i - 2);
            Point2D shift = new Point2D(a2p.getY() - a1p.getY(), a1p.getX() - a2p.getX()).normalize().multiply(offset);
            Point2D a1 = a1p.subtract(shift);
            Point2D a2 = a2p.subtract(shift);
            path.reset();
            path.moveTo(a1.getX(), a1.getY());
            path.lineTo(a2.getX(), a2.getY());
            path.lineTo(a2p.getX(), a2p.getY());
            path.lineTo(a1p.getX(), a1p.getY());
            path.closePath();
            if (path.contains(b2p.getX(), b2p.getY())) {
                segments.remove(i);
                segments.remove(i - 1);
                i--;
            } else {
                break;
            }
        }


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
