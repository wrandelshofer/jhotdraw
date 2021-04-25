/*
 * @(#)PointAndTangentUtil.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;

import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class PointAndTangentBuilder {
    private final @NonNull List<Segment> segments = new ArrayList<>();
    private final double length;

    /**
     * Creates a new PointAndTangentBuilder.
     */
    public PointAndTangentBuilder(@NonNull Shape shape, double flatness) {
        this(shape.getPathIterator(null), flatness);
    }

    /**
     * Creates a new PointAndTangentBuilder.
     */
    public PointAndTangentBuilder(@NonNull PathIterator it, double flatness) {
        final float[] coords = new float[6];
        double startX = 0, startY = 0;
        double x = 0, y = 0;
        double x1, y1, x2, y2, x3, y3, x4, y4;
        double eps = 1e-7;

        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        for (; !it.isDone(); it.next()) {
            int op = it.currentSegment(coords);
            double distanceFromStart = stats.getSum();
            switch (op) {
                case PathIterator.SEG_MOVETO:
                    startX = x = coords[0];
                    startY = y = coords[1];
                    break;
                case PathIterator.SEG_LINETO: {
                    x1 = x;
                    y1 = y;
                    x2 = x = coords[0];
                    y2 = y = coords[1];
                    double length = Geom.length(x1, y1, x2, y2);
                    if (length > eps) {
                        Line2D.Double line = new Line2D.Double(x1, y1, x2, y2);
                        stats.accept(length);
                        segments.add(new Segment(distanceFromStart, length, line));
                    }
                    break;
                }
                case PathIterator.SEG_QUADTO: {
                    x1 = x;
                    y1 = y;
                    x2 = coords[0];
                    y2 = coords[1];
                    x3 = x = coords[2];
                    y3 = y = coords[3];
                    QuadCurve2D.Double quadCurve = new QuadCurve2D.Double(x1, y1, x2, y2, x3, y3);
                    double length = computeLength(quadCurve.getPathIterator(null, flatness), flatness);
                    if (length > eps) {
                        stats.accept(length);
                        segments.add(new Segment(distanceFromStart, length, quadCurve));
                    }
                    break;
                }
                case PathIterator.SEG_CUBICTO: {
                    x1 = x;
                    y1 = y;
                    x2 = coords[0];
                    y2 = coords[1];
                    x3 = coords[2];
                    y3 = coords[3];
                    x4 = x = coords[4];
                    y4 = y = coords[5];
                    CubicCurve2D.Double cubicCurve = new CubicCurve2D.Double(x1, y1, x2, y2, x3, y3, x4, y4);
                    double length = computeLength(cubicCurve.getPathIterator(null, flatness), flatness);
                    if (length > eps) {
                        stats.accept(length);
                        segments.add(new Segment(distanceFromStart, length, cubicCurve));
                    }
                    break;
                }
                case PathIterator.SEG_CLOSE: {
                    double length = Geom.length(x, y, startX, startY);
                    if (length > eps) {
                        Line2D.Double line = new Line2D.Double(x, y, startX, startY);
                        stats.accept(length);
                        segments.add(new Segment(distanceFromStart, length, line));
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException("unsupported op-code in PathIterator: " + op);
            }
        }

        length = stats.getSum();
    }

    public PointAndTangent getPointAndTangentAt(double t) {
        double distanceFromStart = length * t;
        int searchResult = Collections.binarySearch(segments, new Segment(distanceFromStart, length, null));
        int index = (searchResult < 0) ? Math.min(-(searchResult) - 1, segments.size() - 1) : searchResult;
        Segment seg = segments.get(index);
        if (seg.shape instanceof Line2D.Double) {
            Line2D.Double line = (Line2D.Double) seg.shape;
            double tInLine = seg.length == 0 ? 0 : (distanceFromStart - seg.distanceFromStart) / seg.length;
            Point2D.Double p = Geom.lerp(line.x1, line.y1, line.x2, line.y2, tInLine);
            return new PointAndTangent(p.x, p.y, line.x2 - line.x1, line.y2 - line.y1);
        } else if (seg.shape instanceof QuadCurve2D.Double) {
            QuadCurve2D.Double quadCurve = (QuadCurve2D.Double) seg.shape;
            double tInQuadCurve = seg.length == 0 ? 0 : (distanceFromStart - seg.distanceFromStart) / seg.length;
            Point2D.Double p = BezierCurves.evalQuadCurve(quadCurve.x1, quadCurve.y1, quadCurve.ctrlx, quadCurve.ctrly,
                    quadCurve.x2, quadCurve.y2, tInQuadCurve);
            Point2D.Double tangent = BezierCurves.evalQuadCurveTangent(quadCurve.x1, quadCurve.y1, quadCurve.ctrlx, quadCurve.ctrly,
                    quadCurve.x2, quadCurve.y2, tInQuadCurve);
            return new PointAndTangent(p.x, p.y, tangent.x, tangent.y);

        } else {
            CubicCurve2D.Double cubicCurve = (CubicCurve2D.Double) seg.shape;
            double tInCubicCurve = seg.length == 0 ? 0 : (distanceFromStart - seg.distanceFromStart) / seg.length;
            Point2D.Double p = BezierCurves.evalCubicCurve(cubicCurve.x1, cubicCurve.y1,
                    cubicCurve.ctrlx1, cubicCurve.ctrly1,
                    cubicCurve.ctrlx2, cubicCurve.ctrly2,
                    cubicCurve.x2, cubicCurve.y2, tInCubicCurve);
            Point2D.Double tangent = BezierCurves.evalCubicCurveTangent(cubicCurve.x1, cubicCurve.y1,
                    cubicCurve.ctrlx1, cubicCurve.ctrly1,
                    cubicCurve.ctrlx2, cubicCurve.ctrly2,
                    cubicCurve.x2, cubicCurve.y2, tInCubicCurve);
            return new PointAndTangent(p.x, p.y, tangent.x, tangent.y);
        }
    }

    /**
     * Computes the length of the provided path.
     *
     * @param it       a path iterator
     * @param flatness the maximum distance that the line segments used to
     *                 approximate the curved segments are allowed to deviate
     *                 from any point on the original curve
     * @return the length of the path
     */
    public static double computeLength(@NonNull PathIterator it, double flatness) {
        final float[] coords = new float[6];
        double startX = 0, startY = 0;
        double x = 0, y = 0;

        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        for (; !it.isDone(); it.next()) {
            int op = it.currentSegment(coords);
            switch (op) {
                case PathIterator.SEG_MOVETO:
                    startX = x = coords[0];
                    startY = y = coords[1];
                    break;
                case PathIterator.SEG_LINETO:
                    stats.accept(Geom.length(x, y, x = coords[0], y = coords[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    stats.accept(computeLength(new QuadCurve2D.Double(x, y, coords[0], coords[1],
                            x = coords[2], y = coords[3]).getPathIterator(null, flatness), flatness));
                    break;
                case PathIterator.SEG_CUBICTO:
                    stats.accept(computeLength(new CubicCurve2D.Double(x, y, coords[0], coords[1],
                            coords[2], coords[3],
                            x = coords[4], y = coords[5]).getPathIterator(null, flatness), flatness));
                    break;
                case PathIterator.SEG_CLOSE:
                    stats.accept(Geom.length(startX, startY, x, y));
                    break;
                default:
                    throw new IllegalArgumentException("unsupported op-code in PathIterator: " + op);
            }
        }
        return stats.getSum();
    }

    private static class Segment implements Comparable<Segment> {
        private final double distanceFromStart;
        private final double length;
        private final Shape shape;

        public Segment(double distanceFromStart, double length, Shape shape) {
            this.distanceFromStart = distanceFromStart;
            this.length = length;
            this.shape = shape;
        }

        @Override
        public int compareTo(Segment o) {
            return Double.compare(this.distanceFromStart, o.distanceFromStart);
        }
    }

    public double getLength() {
        return length;
    }

    public static PointAndTangent computePointAndTangentAt(@NonNull PathIterator it, double flatness, double t) {
        return new PointAndTangentBuilder(it, flatness).getPointAndTangentAt(t);
    }
}
