/*
 * @(#)OffsetPathBuilder.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

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


    private void flushNewButBroken() {
        // XXX the following algorithm only works if the original path does not self-intersect:

        final Path2D.Double path = new Path2D.Double();
        for (int i = 0, n = segments.size(); i < n - 1; i++) {
            Point2D a1p = segments.get(i);
            Point2D a2p = segments.get(i + 1);
            Point2D shift = new Point2D(a2p.getY() - a1p.getY(), a1p.getX() - a2p.getX()).normalize().multiply(offset);
            Point2D a1 = a1p.subtract(shift);
            Point2D a2 = a2p.subtract(shift);
            path.moveTo(a1.getX(), a1.getY());
            path.lineTo(a2.getX(), a2.getY());
            path.lineTo(a2p.getX(), a2p.getY());
            path.lineTo(a1p.getX(), a1p.getY());
            path.closePath();
            if (i < n - 2) {
                Point2D b1p = segments.get(i + 2);
                path.moveTo(a2.getX(), a2.getY());
                path.lineTo(b1p.getX(), b1p.getY());
                path.lineTo(a2p.getX(), a2p.getY());
                path.closePath();
            }
        }


        for (int i = 0, n = segments.size(); i < n - 2; ++i) {
            Point2D a1p = segments.get(i);
            Point2D a2p = segments.get(i + 1);
            if (a1p.equals(a2p)) {
                segments.remove(i);
                n--;
                i--;
                continue;
            }

            Intersection inter = Intersections.intersectLinePathIterator(a1p, a2p, path.getPathIterator(null));

            if (inter.getStatus() == Intersection.Status.INTERSECTION) {
                if (path.contains(a1p.getX(), a1p.getY())) {
                    segments.remove(i);
                    n--;
                    i--;
                }
                for (Intersection.IntersectionPoint p : inter.getIntersections()) {
                    if (abs(p.t1) > 1e-6 && abs(p.t1 - 1.0) > 1e-6) {
                        segments.add(i + 1, p.getPoint());
                        n++;
                        break;
                    }
                }
            } else if (inter.getStatus() == Intersection.Status.NO_INTERSECTION_INSIDE) {
                segments.remove(i);
                n--;
            }

        }

    /*
        // 1. Clip line segments a1' a2' with any following offset line segments b1' b2'.
        final Path2D.Double path=new Path2D.Double();
        for (int i = 0, n = segments.size(); i < n - 2; ++i) {
            Point2D a1p = segments.get(i);
            Point2D a2p = segments.get(i + 1);
            for (int j = n - 2; j >= i + 1; --j) {
                path.reset();
                Point2D b1p = segments.get(j);
                Point2D b2p = segments.get(j + 1);
                Point2D shift = new Point2D(b2p.getY() - b1p.getY(), b1p.getX() - b2p.getX()).normalize().multiply(offset);
                Point2D b1=b1p.subtract(shift);
                Point2D b2=b2p.subtract(shift);
                path.moveTo(b1p.getX(),b1p.getY());   // 1:b1'---2:b2'---3:b2---4:b1---closePath
                path.lineTo(b2p.getX(),b2p.getY());
                path.lineTo(b2.getX(),b2.getY());
                path.lineTo(b1.getX(),b1.getY());
                path.closePath();
                Intersection inter = Intersections.intersectLinePathIterator(a1p, a2p, path.getPathIterator(null));
                if (inter.getStatus() == Intersection.Status.NO_INTERSECTION_INSIDE) {
                    // a1p a2p is completely inside, therefore a previous segment will intersect unless it is the first
                    // segment.
                    if (i==0) {
                        segments.remove(0);
                        i--;
                        n--;
                        break ;
                    }
                } else if (inter.getStatus() == Intersection.Status.INTERSECTION) {
                    final boolean ap1IsInside = path.contains(a1p.getX(), a1p.getY());
                    final boolean ap2IsInside = path.contains(a2p.getX(), a2p.getY());
                    if (ap1IsInside&&!ap2IsInside) {
                        // a1p is inside, a2p is outside.
                    } else if (!ap1IsInside&&ap2IsInside){
                        // a1p is outside, a1p is inside
                        if (inter.getFirst().getSegment2()==1) {
                            //             b2p                       b2p
                            //            /                          /
                            // a1p-----------a2p   ==>   a1p-------b1p
                            //          /
                            //        b1p
                            Point2D p = inter.getFirstPoint();
                            segments.set(j, p);
                            // delete all points between i and j
                            if (j > i + 1) {
                                segments.subList(i + 1, j).clear();
                            }
                            n -= j - 1 - i;
                            j -= j - 1 - i;
                        } else if (inter.getFirst().getSegment2()==4){
                            //             b1p----b2p                 b1p---b2p
                            //            /                          /
                            // a1p-----------a2p   ==>   a1p-------a2p
                            //          /
                            //        b1
                            segments.set(i+1,inter.getFirstPoint());

                            // delete all points between i+2 and j
                            if (j > i + 2) {
                                segments.subList(i + 2, j).clear();
                            }
                            n -= j - i-2;
                            j -= j - i -2;
                        }
                    }
                }
                /*
                Intersection inter2 = Intersections.intersectLineLine(a1p, a2p, b1p, b2p);
                if (inter2.getStatus() == Intersection.Status.INTERSECTION) {
                    // if a1' a2' is completely inside b1' b2' it will be clipped with
                    // the next segment. However here we do not make the distinction whether
                    // a1' is inside or outside b1 b1' b2' b2'.

                    Point2D p = inter2.getPoints().iterator().next();
                    segments.set(j, p);
                    // delete all points between i and j
                    if (j > i + 1) {
                        segments.subList(i +1, j).clear();
                    }
                    n -= j - 1 - i;
                    j -= j - 1 - i;
                }* /
            }
        }*/

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

        // 2. Clip offset line with any a1 a1' line. O(n^2).
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
