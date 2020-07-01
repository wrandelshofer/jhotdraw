package org.jhotdraw8.geom.offsetline;

import javafx.geometry.Point2D;
import org.jhotdraw8.collection.OrderedPair;
import org.jhotdraw8.collection.OrderedPairNonNull;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.util.TriFunction;
import org.jhotdraw8.util.function.QuadConsumer;
import org.jhotdraw8.util.function.TriConsumer;
import org.jhotdraw8.util.function.TriPredicate;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.DoubleFunction;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.jhotdraw8.geom.offsetline.BulgeConversionFunctions.arcRadiusAndCenter;
import static org.jhotdraw8.geom.offsetline.PlineVertex.createFastApproxBoundingBox;
import static org.jhotdraw8.geom.offsetline.PlineVertex.splitAtPoint;
import static org.jhotdraw8.geom.offsetline.Utils.fuzzyEqual;
import static org.jhotdraw8.geom.offsetline.Utils.perpDot;
import static org.jhotdraw8.geom.offsetline.Utils.pointFromParametric;
import static org.jhotdraw8.geom.offsetline.Utils.pointWithinArcSweepAngle;

public class Intersections {
    private Intersections() {
    }

    /**
     * Find intersect between two circles in 2D.
     */
    public static IntrCircle2Circle2Result intrCircle2Circle2(double radius1, final Point2D center1,
                                                              double radius2, final Point2D center2) {
        // Reference algorithm: http://paulbourke.net/geometry/circlesphere/

        IntrCircle2Circle2Result result = new IntrCircle2Circle2Result();
        Point2D cv = center2.subtract(center1);
        double d2 = cv.dotProduct(cv);
        double d = Math.sqrt(d2);
        if (d < Utils.realThreshold) {
            // same center position
            if (fuzzyEqual(radius1, radius2)) {
                result.intrType = Circle2Circle2IntrType.Coincident;
            } else {
                result.intrType = Circle2Circle2IntrType.NoIntersect;
            }
        } else {
            // different center position
            if (d > radius1 + radius2 + Utils.realThreshold ||
                    d + Utils.realThreshold < Math.abs(radius1 - radius2)) {
                result.intrType = Circle2Circle2IntrType.NoIntersect;
            } else {
                double rad1Sq = radius1 * radius1;
                double a = (rad1Sq - radius2 * radius2 + d2) / (2.0 * d);
                Point2D midPoint = center1.add(cv.multiply(a / d));
                double diff = rad1Sq - a * a;
                if (diff < 0.0) {
                    result.intrType = Circle2Circle2IntrType.OneIntersect;
                    result.point1 = midPoint;
                } else {
                    double h = Math.sqrt(diff);
                    double hOverD = h / d;
                    double xTerm = hOverD * cv.getY();
                    double yTerm = hOverD * cv.getX();
                    double x1 = midPoint.getX() + xTerm;
                    double y1 = midPoint.getY() - yTerm;
                    double x2 = midPoint.getX() - xTerm;
                    double y2 = midPoint.getY() + yTerm;
                    result.point1 = new Point2D(x1, y1);
                    result.point2 = new Point2D(x2, y2);
                    if (fuzzyEqual(result.point1, result.point2)) {
                        result.intrType = Circle2Circle2IntrType.OneIntersect;
                    } else {
                        result.intrType = Circle2Circle2IntrType.TwoIntersects;
                    }
                }
            }
        }

        return result;
    }

    public static IntrLineSeg2LineSeg2Result intrLineSeg2LineSeg2(final Point2D u1, final Point2D u2, final Point2D v1,
                                                                  final Point2D v2) {
        // This implementation works by processing the segments in parametric equation form and using
        // perpendicular products
        // see: http://geomalgorithms.com/a05-_intersect-1.html and
        // http://mathworld.wolfram.com/PerpDotProduct.html

        IntrLineSeg2LineSeg2Result result = new IntrLineSeg2LineSeg2Result();
        Point2D u = u2.subtract(u1);
        Point2D v = v2.subtract(v1);
        double d = perpDot(u, v);

        Point2D w = u1.subtract(v1);

        // Test if point is inside a segment, NOTE: assumes points are aligned
        TriPredicate<Point2D, Point2D, Point2D> isInSegment = (final Point2D pt, final Point2D segStart,
                                                               final Point2D segEnd) -> {
            if (Utils.fuzzyEqual(segStart.getX(), segEnd.getX())) {
                // vertical segment, test y coordinate
                MinMax minMax = Utils.minmax(segStart.getY(), segEnd.getY());
                return Utils.fuzzyInRange(minMax.first(), pt.getY(), minMax.second);
            }

            // else just test x coordinate
            MinMax minMax = Utils.minmax(segStart.getX(), segEnd.getX());
            return Utils.fuzzyInRange(minMax.first(), pt.getX(), minMax.second);
        };

        // threshold check here to avoid almost parallel lines resulting in very distant intersection
        if (Math.abs(d) > Utils.realThreshold) {
            // segments not parallel or collinear
            result.t0 = perpDot(v, w) / d;
            result.t1 = perpDot(u, w) / d;
            result.point = v1.add(v.multiply(result.t1));
            if (result.t0 + Utils.realThreshold < 0.0 ||
                    result.t0 > 1.0 + Utils.realThreshold ||
                    result.t1 + Utils.realThreshold < 0.0 ||
                    result.t1 > 1.0 + Utils.realThreshold) {
                result.intrType = LineSeg2LineSeg2IntrType.False;
            } else {
                result.intrType = LineSeg2LineSeg2IntrType.True;
            }
        } else {
            // segments are parallel or collinear
            double a = perpDot(u, w);
            double b = perpDot(v, w);
            // threshold check here, we consider almost parallel lines to be parallel
            if (Math.abs(a) > Utils.realThreshold || Math.abs(b) > Utils.realThreshold) {
                // parallel and not collinear so no intersect
                result.intrType = LineSeg2LineSeg2IntrType.None;
            } else {
                // either collinear or degenerate (segments are single points)
                boolean uIsPoint = fuzzyEqual(u1, u2);
                boolean vIsPoint = fuzzyEqual(v1, v2);
                if (uIsPoint && vIsPoint) {
                    // both segments are just points
                    if (fuzzyEqual(u1, v1)) {
                        // same point
                        result.point = u1;
                        result.intrType = LineSeg2LineSeg2IntrType.True;
                    } else {
                        // distinct points
                        result.intrType = LineSeg2LineSeg2IntrType.None;
                    }

                } else if (uIsPoint) {
                    if (isInSegment.test(u1, v1, v2)) {
                        result.intrType = LineSeg2LineSeg2IntrType.True;
                        result.point = u1;
                    } else {
                        result.intrType = LineSeg2LineSeg2IntrType.None;
                    }

                } else if (vIsPoint) {
                    if (isInSegment.test(v1, u1, u2)) {
                        result.intrType = LineSeg2LineSeg2IntrType.True;
                        result.point = v1;
                    } else {
                        result.intrType = LineSeg2LineSeg2IntrType.None;
                    }
                } else {
                    // neither segment is a point, check if they overlap
                    Point2D w2 = u2.subtract(v1);
                    if (Math.abs(v.getX()) < Utils.realThreshold) {
                        result.t0 = w.getY() / v.getY();
                        result.t1 = w2.getY() / v.getY();
                    } else {
                        result.t0 = w.getX() / v.getX();
                        result.t1 = w2.getX() / v.getX();
                    }

                    if (result.t0 > result.t1) {
                        double swap = result.t0;
                        result.t0 = result.t1;
                        result.t1 = swap;
                    }

                    // using threshold check here to make intersect "sticky" to prefer considering it an
                    // intersect
                    if (result.t0 > 1.0 + Utils.realThreshold ||
                            result.t1 + Utils.realThreshold < 0.0) {
                        // no overlap
                        result.intrType = LineSeg2LineSeg2IntrType.None;
                    } else {
                        result.t0 = Math.max(result.t0, 0.0);
                        result.t1 = Math.min(result.t1, 1.0);
                        if (Math.abs(result.t1 - result.t0) < Utils.realThreshold) {
                            // intersect is a single point (segments line up end to end)
                            result.intrType = LineSeg2LineSeg2IntrType.True;
                            result.point = v1.add(v.multiply(result.t0));
                        } else {
                            result.intrType = LineSeg2LineSeg2IntrType.Coincident;
                        }
                    }
                }
            }
        }

        return result;
    }


    /**
     * Gets the intersect between a segment and a circle, returning the parametric solution t to the
     * segment equation P(t) = v1 + t * (v2 - v1) for t = 0 to t = 1, if t < 0 or t > 1 then intersect
     * occurs only when extending the segment out past the points given (if t < 0 intersect nearest v1,
     * if t > 0 then intersect nearest v2), intersects are "sticky" and "snap" to tangent points, e.g. a
     * segment very close to being a tangent will be returned as a single intersect point.
     */
    public static IntrLineSeg2Circle2Result intrLineSeg2Circle2(final Point2D p0,
                                                                final Point2D p1, double radius,
                                                                final Point2D circleCenter) {
        // This function solves for t by substituting the parametric equations for the segment x = v1.X +
        // t * (v2.X - v1.X) and y = v1.Y + t * (v2.Y - v1.Y) for t = 0 to t = 1 into the circle equation
        // (x-h)^2 + (y-k)^2 = r^2 and then solving the resulting equation in the form a*t^2 + b*t + c = 0
        // using the quadratic formula
        IntrLineSeg2Circle2Result result = new IntrLineSeg2Circle2Result();
        double dx = p1.getX() - p0.getX();
        double dy = p1.getY() - p0.getY();
        double h = circleCenter.getX();
        double k = circleCenter.getY();

        double a = dx * dx + dy * dy;
        if (Math.abs(a) < Utils.realThreshold) {
            // v1 = v2, test if point is on the circle
            double xh = p0.getX() - h;
            double yk = p0.getY() - k;
            if (Utils.fuzzyEqual(xh * xh + yk * yk, radius * radius)) {
                result.numIntersects = 1;
                result.t0 = 0.0;
            } else {
                result.numIntersects = 0;
            }
        } else {
            double b = 2.0 * (dx * (p0.getX() - h) + dy * (p0.getY() - k));
            double c = (p0.getX() * p0.getX() - 2.0 * h * p0.getX() + h * h) +
                    (p0.getY() * p0.getY() - 2.0 * k * p0.getY() + k * k) - radius * radius;
            double discr = b * b - 4.0 * a * c;

            if (Math.abs(discr) < Utils.realThreshold) {
                // 1 solution (tangent line)
                result.numIntersects = 1;
                result.t0 = -b / (2.0 * a);
            } else if (discr < 0.0) {
                result.numIntersects = 0;
            } else {
                result.numIntersects = 2;
                MinMax sols = Utils.quadraticSolutions(a, b, c, discr);
                result.t0 = sols.first();
                result.t1 = sols.second();
            }
        }

        assert result.numIntersects >= 0 && result.numIntersects <= 2 : "invalid intersect count";
        return result;
    }

    public static CoincidentSlicesResult
    sortAndjoinCoincidentSlices(List<PlineCoincidentIntersect> coincidentIntrs,
                                Polyline pline1, Polyline pline2) {
        CoincidentSlicesResult result = new CoincidentSlicesResult();

        if (coincidentIntrs.size() == 0) {
            return result;
        }

        for (PlineCoincidentIntersect intr : coincidentIntrs) {
            Point2D sp = pline1.get(intr.sIndex1).pos();
            double dist1 = Geom.squaredDistance(sp, intr.point1);
            double dist2 = Geom.squaredDistance(sp, intr.point2);
            if (dist1 > dist2) {
                Point2D swap = intr.point1;
                intr.point1 = intr.point2;
                intr.point2 = swap;
            }
        }

        coincidentIntrs.sort((intr1, intr2) -> {
            if (intr1.sIndex1 != intr2.sIndex1) {
                return intr1.sIndex1 - intr2.sIndex1;
            }
            // equal index so sort distance from start
            final Point2D sp = pline1.get(intr1.sIndex1).pos();
            double dist1 = Geom.squaredDistance(sp, intr1.point1);
            double dist2 = Geom.squaredDistance(sp, intr2.point1);
            return Double.compare(dist1, dist2);
        });

        Deque<PlineIntersect> sliceStartPoints = result.sliceStartPoints;
        Deque<PlineIntersect> sliceEndPoints = result.sliceEndPoints;
        Deque<Polyline> coincidentSlices = result.coincidentSlices;

        Polyline[] currCoincidentSlice = new Polyline[]{new Polyline()};

        IntConsumer startCoincidentSliceAt = (int intrIndex) -> {
            final PlineCoincidentIntersect intr = coincidentIntrs.get(intrIndex);
            final PlineVertex v1 = pline1.get(intr.sIndex1);
            final PlineVertex v2 = pline1.get(Utils.nextWrappingIndex(intr.sIndex1, pline1));
            final PlineVertex u1 = pline2.get(intr.sIndex2);

            SplitResult split1 = splitAtPoint(v1, v2, intr.point1);
            currCoincidentSlice[0].addVertex(split1.splitVertex);
            SplitResult split2 = splitAtPoint(v1, v2, intr.point2);
            currCoincidentSlice[0].addVertex(split2.splitVertex);

            PlineIntersect sliceStart = new PlineIntersect();
            sliceStart.pos = split1.splitVertex.pos();

            if (fuzzyEqual(v1.pos(), intr.point1, Utils.realPrecision)) {
                // coincidence starts at beginning of segment, report as starting at end of previous index
                sliceStart.sIndex1 = Utils.prevWrappingIndex(intr.sIndex1, pline1);
            } else {
                sliceStart.sIndex1 = intr.sIndex1;
            }

            if (fuzzyEqual(u1.pos(), sliceStart.pos, Utils.realPrecision)) {
                sliceStart.sIndex2 = Utils.prevWrappingIndex(intr.sIndex2, pline2);
            } else {
                sliceStart.sIndex2 = intr.sIndex2;
            }

            sliceStartPoints.add(sliceStart);
        };

        IntConsumer endCoincidentSliceAt = (int intrIndex) -> {
            final PlineCoincidentIntersect intr = coincidentIntrs.get(intrIndex);
            final PlineVertex u1 = pline2.get(intr.sIndex2);

            coincidentSlices.add(currCoincidentSlice[0]);
            currCoincidentSlice[0] = new Polyline();
            PlineIntersect sliceEnd = new PlineIntersect();
            sliceEnd.pos = intr.point2;
            sliceEnd.sIndex1 = intr.sIndex1;
            if (fuzzyEqual(u1.pos(), sliceEnd.pos, Utils.realPrecision)) {
                sliceEnd.sIndex2 = Utils.prevWrappingIndex(intr.sIndex2, pline2);
            } else {
                sliceEnd.sIndex2 = intr.sIndex2;
            }

            sliceEndPoints.add(sliceEnd);
        };

        startCoincidentSliceAt.accept(0);
        for (int i = 1; i < coincidentIntrs.size(); ++i) {
            final PlineCoincidentIntersect intr = coincidentIntrs.get(i);
            final PlineVertex v1 = pline1.get(intr.sIndex1);
            final PlineVertex v2 = pline1.get(Utils.nextWrappingIndex(intr.sIndex1, pline1));

            if (fuzzyEqual(intr.point1, currCoincidentSlice[0].lastVertex().pos(),
                    Utils.realPrecision)) {
                // continue coincident slice
                currCoincidentSlice[0].pop_back();
                SplitResult split1 = splitAtPoint(v1, v2, intr.point1);
                currCoincidentSlice[0].addVertex(split1.splitVertex);
                SplitResult split2 = splitAtPoint(v1, v2, intr.point2);
                currCoincidentSlice[0].addVertex(split2.splitVertex);

            } else {
                // end coincident slice and start new
                endCoincidentSliceAt.accept(i - 1);
                startCoincidentSliceAt.accept(i);
            }
        }

        // cap off last slice
        endCoincidentSliceAt.accept(coincidentIntrs.size() - 1);

        if (coincidentSlices.size() > 1) {
            // check if last coincident slice connects with first()
            final Point2D lastSliceEnd = coincidentSlices.getLast().lastVertex().pos();
            final Point2D firstSliceBegin = coincidentSlices.getFirst().get(0).pos();
            if (fuzzyEqual(lastSliceEnd, firstSliceBegin, Utils.realPrecision)) {
                // they do connect, join them together
                final Polyline lastSlice = coincidentSlices.getLast();
                lastSlice.pop_back();
                lastSlice.addAll(coincidentSlices.getFirst());

                // cleanup
                sliceEndPoints.removeLast();
                sliceEndPoints.addLast(sliceEndPoints.getFirst());
                sliceEndPoints.removeFirst();
                sliceStartPoints.removeFirst();
                coincidentSlices.removeFirst();
            }
        }

        return result;
    }

    /// Finds all local self intersects of the polyline, local self intersects are defined as between
    /// two polyline segments that share a vertex. NOTES:
    /// - Singularities (repeating vertexes) are returned as coincident intersects
    public static void localSelfIntersects(final Polyline pline, final List<PlineIntersect> output) {
        if (pline.size() < 2) {
            return;
        }

        if (pline.size() == 2) {
            if (pline.isClosed()) {
                // check if overlaps on itself from vertex 1 to vertex 2
                if (Utils.fuzzyEqual(pline.get(0).bulge(), -pline.get(1).bulge())) {
                    // coincident
                    output.add(new PlineIntersect(0, 1, pline.get(1).pos()));
                    output.add(new PlineIntersect(1, 0, pline.get(0).pos()));
                }
            }
            return;
        }

        TriConsumer<Integer, Integer, Integer> testAndAddIntersect = (Integer i, Integer j, Integer k) -> {
            final PlineVertex v1 = pline.get(i);
            final PlineVertex v2 = pline.get(j);
            final PlineVertex v3 = pline.get(k);
            // testing intersection between v1->v2 and v2->v3 segments

            if (fuzzyEqual(v1.pos(), v2.pos(), Utils.realPrecision)) {
                // singularity
                // coincident
                output.add(new PlineIntersect(i, j, v1.pos()));
            } else {
                IntrPlineSegsResult intrResult = intrPlineSegs(v1, v2, v2, v3);
                switch (intrResult.intrType) {
                case NoIntersect:
                    break;
                case TangentIntersect:
                case OneIntersect:
                    if (!fuzzyEqual(intrResult.point1, v2.pos(), Utils.realPrecision)) {
                        output.add(new PlineIntersect(i, j, intrResult.point1));
                    }
                    break;
                case TwoIntersects:
                    if (!fuzzyEqual(intrResult.point1, v2.pos(), Utils.realPrecision)) {
                        output.add(new PlineIntersect(i, j, intrResult.point1));
                    }
                    if (!fuzzyEqual(intrResult.point2, v2.pos(), Utils.realPrecision)) {
                        output.add(new PlineIntersect(i, j, intrResult.point2));
                    }
                    break;
                case SegmentOverlap:
                case ArcOverlap:
                    // coincident
                    output.add(new PlineIntersect(i, j, intrResult.point1));
                    break;
                }
            }
        };

        for (int i = 2; i < pline.size(); ++i) {
            testAndAddIntersect.accept(i - 2, i - 1, i);
        }

        if (pline.isClosed()) {
            // we tested for intersect between segments at indexes 0->1, 1->2 and everything up to and
            // including (count-3)->(count-2), (count-2)->(count-1), polyline is closed so now test
            // [(count-2)->(count-1), (count-1)->0] and [(count-1)->0, 0->1]
            testAndAddIntersect.accept(pline.size() - 2, pline.size() - 1, 0);
            testAndAddIntersect.accept(pline.size() - 1, 0, 1);
        }
    }

    /**
     * /// Finds all global self intersects of the polyline, global self intersects are defined as all
     * /// intersects between polyline segments that DO NOT share a vertex (use the localSelfIntersects
     * /// function to find those). A spatial index is used to minimize the intersect comparisons required,
     * /// the spatial index should hold bounding boxes for all of the polyline's segments.
     * /// NOTES:
     * /// - We never include intersects at a segment's start point, the matching intersect from the
     * /// previous segment's end point is included (no sense in including both)
     */
    static void globalSelfIntersects(final Polyline pline, final List<PlineIntersect> output,
                                     final StaticSpatialIndex spatialIndex) {
        if (pline.size() < 3) {
            return;
        }

        Set<OrderedPair<Integer, Integer>>
                visitedSegmentPairs = new HashSet<>(pline.size());

        Deque<Integer> queryStack = new ArrayDeque<>(8);

        StaticSpatialIndex.Visitor visitor = (int i, double minX, double minY, double maxX, double maxY) -> {
            int j = Utils.nextWrappingIndex(i, pline);
            final PlineVertex v1 = pline.get(i);
            final PlineVertex v2 = pline.get(j);
            AABB envelope = new AABB(minX, minY, maxX, maxY);
            envelope.expand(Utils.realThreshold);
            IntPredicate indexVisitor = (int hitIndexStart) -> {
                int hitIndexEnd = Utils.nextWrappingIndex(hitIndexStart, pline);
                // skip/filter already visited intersects
                // skip local segments
                if (i == hitIndexStart || i == hitIndexEnd || j == hitIndexStart || j == hitIndexEnd) {
                    return true;
                }
                // skip reversed segment order (would end up comparing the same segments)
                if (visitedSegmentPairs.contains(new OrderedPair<>(hitIndexStart, i))) {
                    return true;
                }

                // add the segment pair we're visiting now
                visitedSegmentPairs.add(new OrderedPair<>(i, hitIndexStart));

                final PlineVertex u1 = pline.get(hitIndexStart);
                final PlineVertex u2 = pline.get(hitIndexEnd);

                Predicate<Point2D> intrAtStartPt = (final Point2D intr) -> {
                    return fuzzyEqual(v1.pos(), intr) || fuzzyEqual(u1.pos(), intr);
                };

                IntrPlineSegsResult intrResult = intrPlineSegs(v1, v2, u1, u2);
                switch (intrResult.intrType) {
                case NoIntersect:
                    break;
                case TangentIntersect:
                case OneIntersect:
                    if (!intrAtStartPt.test(intrResult.point1)) {
                        output.add(new PlineIntersect(i, hitIndexStart, intrResult.point1));
                    }
                    break;
                case TwoIntersects:
                case SegmentOverlap:
                case ArcOverlap:
                    if (!intrAtStartPt.test(intrResult.point1)) {
                        output.add(new PlineIntersect(i, hitIndexStart, intrResult.point1));
                    }
                    if (!intrAtStartPt.test(intrResult.point2)) {
                        output.add(new PlineIntersect(i, hitIndexStart, intrResult.point2));
                    }
                    break;
                }

                // visit the entire query
                return true;
            };

            spatialIndex.visitQuery(envelope.xMin, envelope.yMin, envelope.xMax, envelope.yMax,
                    indexVisitor, queryStack);

            // visit all pline indexes
            return true;
        };

        spatialIndex.visitItemBoxes(visitor);
    }

    /// Finds all self intersects of the polyline (equivalent to calling localSelfIntersects and
    /// globalSelfIntersects).
    public static void allSelfIntersects(final Polyline pline, final List<PlineIntersect> output,
                                         final StaticSpatialIndex spatialIndex) {
        localSelfIntersects(pline, output);
        globalSelfIntersects(pline, output, spatialIndex);
    }

    /// Finds all intersects between pline1 and pline2.

    static void findIntersects(final Polyline pline1, final Polyline pline2,
                               final StaticSpatialIndex pline1SpatialIndex,
                               final PlineIntersectsResult output) {
        List<Integer> queryResults = new ArrayList<>();
        Deque<Integer> queryStack = new ArrayDeque<>(8);

        Set<OrderedPair<Integer, Integer>> possibleDuplicates = new HashSet<>();

        final List<PlineIntersect> intrs = output.intersects;
        final List<PlineCoincidentIntersect> coincidentIntrs = output.coincidentIntersects;

        BiPredicate<Integer, Integer> pline2SegVisitor = (Integer i2, Integer j2) -> {
            final PlineVertex p2v1 = pline2.get(i2);
            final PlineVertex p2v2 = pline2.get(j2);

            queryResults.clear();

            AABB bb = createFastApproxBoundingBox(p2v1, p2v2);
            pline1SpatialIndex.query(bb.xMin, bb.yMin, bb.xMax, bb.yMax, queryResults, queryStack);

            for (int i1 : queryResults) {
                int j1 = Utils.nextWrappingIndex(i1, pline1);
                final PlineVertex p1v1 = pline1.get(i1);
                final PlineVertex p1v2 = pline1.get(j1);

                Predicate<Point2D> intrAtStartPt = (final Point2D intr) -> {
                    return fuzzyEqual(p1v1.pos(), intr) || fuzzyEqual(p2v1.pos(), intr);
                };

                IntrPlineSegsResult intrResult = intrPlineSegs(p1v1, p1v2, p2v1, p2v2);
                switch (intrResult.intrType) {
                case NoIntersect:
                    break;
                case TangentIntersect:
                case OneIntersect:
                    if (!intrAtStartPt.test(intrResult.point1)) {
                        intrs.add(new PlineIntersect(i1, i2, intrResult.point1));
                    }
                    break;
                case TwoIntersects:
                    if (!intrAtStartPt.test(intrResult.point1)) {
                        intrs.add(new PlineIntersect(i1, i2, intrResult.point1));
                    }
                    if (!intrAtStartPt.test(intrResult.point2)) {
                        intrs.add(new PlineIntersect(i1, i2, intrResult.point2));
                    }
                    break;
                case SegmentOverlap:
                case ArcOverlap:
                    coincidentIntrs.add(new PlineCoincidentIntersect(i1, i2, intrResult.point1, intrResult.point2));
                    if (fuzzyEqual(p1v1.pos(), intrResult.point1) ||
                            fuzzyEqual(p1v1.pos(), intrResult.point2)) {
                        possibleDuplicates.add(new OrderedPair<>(Utils.prevWrappingIndex(i1, pline1), i2));
                    }
                    if (fuzzyEqual(p2v1.pos(), intrResult.point1) ||
                            fuzzyEqual(p2v1.pos(), intrResult.point2)) {
                        possibleDuplicates.add(new OrderedPair<>(i1, Utils.prevWrappingIndex(i2, pline2)));
                    }
                    break;
                }
            }

            // visit all indexes
            return true;
        };

        pline2.visitSegIndices(pline2SegVisitor);

        // remove duplicate points caused by the coincident intersect definition
        intrs.removeIf((final PlineIntersect intr) -> {
            boolean found = possibleDuplicates.contains(new OrderedPair<>(intr.sIndex1, intr.sIndex2));
            if (!found) {
                return false;
            }

            final Point2D endPt1 =
                    pline1.get(Utils.nextWrappingIndex(intr.sIndex1, pline1)).pos();
            if (fuzzyEqual(intr.pos, endPt1)) {
                return true;
            }

            final Point2D endPt2 =
                    pline2.get(Utils.nextWrappingIndex(intr.sIndex2, pline2)).pos();
            return fuzzyEqual(intr.pos, endPt2);
        });
    }

    static IntrPlineSegsResult intrPlineSegs(final PlineVertex v1, final PlineVertex v2,
                                             final PlineVertex u1, final PlineVertex u2) {
        IntrPlineSegsResult result = new IntrPlineSegsResult();
        final boolean vIsLine = v1.bulgeIsZero();
        final boolean uIsLine = u1.bulgeIsZero();

        // helper function to process line arc intersect
        QuadConsumer<Point2D, Point2D, PlineVertex, PlineVertex> processLineArcIntr
                = (final Point2D p0, final Point2D p1,
                   final PlineVertex a1, final PlineVertex a2) -> {
            BulgeConversionFunctions.ArcRadiusAndCenter arc = arcRadiusAndCenter(a1, a2);
            IntrLineSeg2Circle2Result intrResult = intrLineSeg2Circle2(p0, p1, arc.radius, arc.center);

            // helper function to test and get point within arc sweep
            DoubleFunction<OrderedPairNonNull<Boolean, Point2D>> pointInSweep = (double t) -> {
                if (t + Utils.realThreshold < 0.0 ||
                        t > 1.0 + Utils.realThreshold) {
                    return new OrderedPairNonNull<Boolean, Point2D>(false, new Point2D(0, 0));
                }

                Point2D p = pointFromParametric(p0, p1, t);
                boolean withinSweep = pointWithinArcSweepAngle(arc.center, a1.pos(), a2.pos(), a1.bulge(), p);
                return new OrderedPairNonNull<Boolean, Point2D>(withinSweep, p);
            };

            if (intrResult.numIntersects == 0) {
                result.intrType = PlineSegIntrType.NoIntersect;
            } else if (intrResult.numIntersects == 1) {
                OrderedPairNonNull<Boolean, Point2D> p = pointInSweep.apply(intrResult.t0);
                if (p.first()) {
                    result.intrType = PlineSegIntrType.OneIntersect;
                    result.point1 = p.second();
                } else {
                    result.intrType = PlineSegIntrType.NoIntersect;
                }
            } else {
                assert intrResult.numIntersects == 2 : "shouldn't get here without 2 intersects";
                OrderedPairNonNull<Boolean, Point2D> p1_ = pointInSweep.apply(intrResult.t0);
                OrderedPairNonNull<Boolean, Point2D> p2_ = pointInSweep.apply(intrResult.t1);

                if (p1_.first() && p2_.first()) {
                    result.intrType = PlineSegIntrType.TwoIntersects;
                    result.point1 = p1_.second();
                    result.point2 = p2_.second();
                } else if (p1_.first()) {
                    result.intrType = PlineSegIntrType.OneIntersect;
                    result.point1 = p1_.second();
                } else if (p2_.first()) {
                    result.intrType = PlineSegIntrType.OneIntersect;
                    result.point1 = p2_.second();
                } else {
                    result.intrType = PlineSegIntrType.NoIntersect;
                }
            }
        };

        if (vIsLine && uIsLine) {
            IntrLineSeg2LineSeg2Result intrResult = intrLineSeg2LineSeg2(v1.pos(), v2.pos(), u1.pos(), u2.pos());
            switch (intrResult.intrType) {
            case None:
                result.intrType = PlineSegIntrType.NoIntersect;
                break;
            case True:
                result.intrType = PlineSegIntrType.OneIntersect;
                result.point1 = intrResult.point;
                break;
            case Coincident:
                result.intrType = PlineSegIntrType.SegmentOverlap;
                // build points from parametric parameters (using second() segment as defined by the function)
                result.point1 = pointFromParametric(u1.pos(), u2.pos(), intrResult.t0);
                result.point2 = pointFromParametric(u1.pos(), u2.pos(), intrResult.t1);
                break;
            case False:
                result.intrType = PlineSegIntrType.NoIntersect;
                break;
            }

        } else if (vIsLine) {
            processLineArcIntr.accept(v1.pos(), v2.pos(), u1, u2);
        } else if (uIsLine) {
            processLineArcIntr.accept(u1.pos(), u2.pos(), v1, v2);
        } else {
            BulgeConversionFunctions.ArcRadiusAndCenter arc1 = arcRadiusAndCenter(v1, v2);
            BulgeConversionFunctions.ArcRadiusAndCenter arc2 = arcRadiusAndCenter(u1, u2);

            TriFunction<Point2D, Point2D, Double, OrderedPairNonNull<Double, Double>> startAndSweepAngle = (final Point2D sp, final Point2D center, Double bulge) -> {
                double startAngle = Utils.normalizeRadians(Utils.angle(center, sp));
                double sweepAngle = 4.0 * Math.atan(bulge);
                return new OrderedPairNonNull<>(startAngle, sweepAngle);
            };

            Predicate<Point2D> bothArcsSweepPoint = (final Point2D pt) -> {
                return pointWithinArcSweepAngle(arc1.center, v1.pos(), v2.pos(), v1.bulge(), pt) &&
                        pointWithinArcSweepAngle(arc2.center, u1.pos(), u2.pos(), u1.bulge(), pt);
            };

            IntrCircle2Circle2Result intrResult = intrCircle2Circle2(arc1.radius, arc1.center, arc2.radius, arc2.center);

            switch (intrResult.intrType) {
            case NoIntersect:
                result.intrType = PlineSegIntrType.NoIntersect;
                break;
            case OneIntersect:
                if (bothArcsSweepPoint.test(intrResult.point1)) {
                    result.intrType = PlineSegIntrType.OneIntersect;
                    result.point1 = intrResult.point1;
                } else {
                    result.intrType = PlineSegIntrType.NoIntersect;
                }
                break;
            case TwoIntersects: {
                final boolean pt1InSweep = bothArcsSweepPoint.test(intrResult.point1);
                final boolean pt2InSweep = bothArcsSweepPoint.test(intrResult.point2);
                if (pt1InSweep && pt2InSweep) {
                    result.intrType = PlineSegIntrType.TwoIntersects;
                    result.point1 = intrResult.point1;
                    result.point2 = intrResult.point2;
                } else if (pt1InSweep) {
                    result.intrType = PlineSegIntrType.OneIntersect;
                    result.point1 = intrResult.point1;
                } else if (pt2InSweep) {
                    result.intrType = PlineSegIntrType.OneIntersect;
                    result.point1 = intrResult.point2;
                } else {
                    result.intrType = PlineSegIntrType.NoIntersect;
                }
            }
            break;
            case Coincident:
                // determine if arcs overlap along their sweep
                // start and sweep angles
                OrderedPairNonNull<Double, Double> arc1StartAndSweep = startAndSweepAngle.apply(v1.pos(), arc1.center, v1.bulge());
                // we have the arcs go the same direction to simplify checks
                OrderedPairNonNull<Double, Double> arc2StartAndSweep = ((Supplier<OrderedPairNonNull<Double, Double>>) () -> {
                    if (v1.bulgeIsNeg() == u1.bulgeIsNeg()) {
                        return startAndSweepAngle.apply(u1.pos(), arc2.center, u1.bulge());
                    }
                    return startAndSweepAngle.apply(u2.pos(), arc2.center, -u1.bulge());
                }).get();
                // end angles (start + sweep)
                double arc1End = arc1StartAndSweep.first() + arc1StartAndSweep.second();
                double arc2End = arc2StartAndSweep.first() + arc2StartAndSweep.second();

                if (Utils.fuzzyEqual(arc1StartAndSweep.first(), arc2End)) {
                    // only end points touch at start of arc1
                    result.intrType = PlineSegIntrType.OneIntersect;
                    result.point1 = v1.pos();
                } else if (Utils.fuzzyEqual(arc2StartAndSweep.first(), arc1End)) {
                    // only end points touch at start of arc2
                    result.intrType = PlineSegIntrType.OneIntersect;
                    result.point1 = u1.pos();
                } else {
                    final boolean arc2StartsInArc1Sweep = Utils.angleIsWithinSweep(
                            arc1StartAndSweep.first(), arc1StartAndSweep.second(), arc2StartAndSweep.first());
                    final boolean arc2EndsInArc1Sweep =
                            Utils.angleIsWithinSweep(arc1StartAndSweep.first(), arc1StartAndSweep.second(), arc2End);
                    if (arc2StartsInArc1Sweep && arc2EndsInArc1Sweep) {
                        // arc2 is fully overlapped by arc1
                        result.intrType = PlineSegIntrType.ArcOverlap;
                        result.point1 = u1.pos();
                        result.point2 = u2.pos();
                    } else if (arc2StartsInArc1Sweep) {
                        // overlap from arc2 start to arc1 end
                        result.intrType = PlineSegIntrType.ArcOverlap;
                        result.point1 = u1.pos();
                        result.point2 = v2.pos();
                    } else if (arc2EndsInArc1Sweep) {
                        // overlap from arc1 start to arc2 end
                        result.intrType = PlineSegIntrType.ArcOverlap;
                        result.point1 = v1.pos();
                        result.point2 = u2.pos();
                    } else {
                        final boolean arc1StartsInArc2Sweep = Utils.angleIsWithinSweep(
                                arc2StartAndSweep.first(), arc2StartAndSweep.second(), arc1StartAndSweep.first());
                        if (arc1StartsInArc2Sweep) {
                            result.intrType = PlineSegIntrType.ArcOverlap;
                            result.point1 = v1.pos();
                            result.point2 = v2.pos();
                        } else {
                            result.intrType = PlineSegIntrType.NoIntersect;
                        }
                    }
                }

                break;
            }
        }

        return result;
    }

}
