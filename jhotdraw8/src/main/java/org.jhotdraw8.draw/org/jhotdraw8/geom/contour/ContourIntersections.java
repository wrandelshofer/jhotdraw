/*
 * @(#)ContourIntersections.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.IntArrayDeque;
import org.jhotdraw8.collection.IntArrayList;
import org.jhotdraw8.collection.OrderedPair;
import org.jhotdraw8.collection.OrderedPairNonNull;
import org.jhotdraw8.geom.AABB;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.intersect.IntersectCircleCircle;
import org.jhotdraw8.geom.intersect.IntersectCircleLine;
import org.jhotdraw8.geom.intersect.IntersectLineLine;
import org.jhotdraw8.geom.intersect.IntersectionResult;
import org.jhotdraw8.geom.intersect.IntersectionResultEx;
import org.jhotdraw8.util.TriFunction;
import org.jhotdraw8.util.function.QuadConsumer;
import org.jhotdraw8.util.function.TriConsumer;

import java.awt.geom.Point2D;
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

import static org.jhotdraw8.geom.contour.BulgeConversionFunctions.arcRadiusAndCenter;
import static org.jhotdraw8.geom.contour.PlineVertex.createFastApproxBoundingBox;
import static org.jhotdraw8.geom.contour.PlineVertex.splitAtPoint;
import static org.jhotdraw8.geom.contour.Utils.pointFromParametric;
import static org.jhotdraw8.geom.contour.Utils.pointWithinArcSweepAngle;

public class ContourIntersections {
    private ContourIntersections() {
    }

    /**
     * Find intersect between two circles in 2D.
     */
    public static IntersectionResult intrCircle2Circle2(double radius1, final Point2D.Double center1,
                                                        double radius2, final Point2D.Double center2) {
        return IntersectCircleCircle.intersectCircleCircle(center1, radius1, center2, radius2, Geom.REAL_THRESHOLD);
    }


    public static final double REAL_THRESHOLD = 1e-8;

    public static @NonNull IntersectionResultEx intrLineSeg2LineSeg2(final Point2D.Double u1, final Point2D.Double u2, final Point2D.Double v1,
                                                                     final Point2D.Double v2) {
        return IntersectLineLine.intersectLineLineEx(u1, u2, v1, v2, REAL_THRESHOLD);
    }


    /**
     * Gets the intersect between a segment and a circle, returning the
     * parametric solution t to the segment equation
     * P(t) = v1 + t * (v2 - v1) for t = 0 to t = 1,
     * if {@literal t < 0} or {@literal t > 1} then intersect
     * occurs only when extending the segment out past the points given (if
     * {@literal t < 0} intersect nearest v1,
     * if {@literal t > 0} then intersect nearest v2),
     * intersects are "sticky" and "snap" to tangent points, e.g. a
     * segment very close to being a tangent will be returned as a single
     * intersect point.
     */
    public static IntersectionResult intrLineSeg2Circle2(final Point2D.Double p0,
                                                         final Point2D.Double p1, double radius,
                                                         final Point2D.Double circleCenter) {
        return IntersectCircleLine.intersectLineCircle(p0, p1, circleCenter, radius, REAL_THRESHOLD);
    }


    public static CoincidentSlicesResult
    sortAndJoinCoincidentSlices(List<PlineCoincidentIntersect> coincidentIntrs,
                                PolyArcPath pline1, PolyArcPath pline2) {
        CoincidentSlicesResult result = new CoincidentSlicesResult();

        if (coincidentIntrs.size() == 0) {
            return result;
        }

        for (PlineCoincidentIntersect intr : coincidentIntrs) {
            Point2D.Double sp = pline1.get(intr.sIndex1).pos();
            double dist1 = sp.distanceSq(intr.point1);
            double dist2 = sp.distanceSq(intr.point2);
            if (dist1 > dist2) {
                Point2D.Double swap = intr.point1;
                intr.point1 = intr.point2;
                intr.point2 = swap;
            }
        }

        coincidentIntrs.sort((intr1, intr2) -> {
            if (intr1.sIndex1 != intr2.sIndex1) {
                return intr1.sIndex1 - intr2.sIndex1;
            }
            // equal index so sort distance from start
            final Point2D.Double sp = pline1.get(intr1.sIndex1).pos();
            double dist1 = sp.distanceSq(intr1.point1);
            double dist2 = sp.distanceSq(intr2.point1);
            return Double.compare(dist1, dist2);
        });

        Deque<PlineIntersect> sliceStartPoints = result.sliceStartPoints;
        Deque<PlineIntersect> sliceEndPoints = result.sliceEndPoints;
        Deque<PolyArcPath> coincidentSlices = result.coincidentSlices;

        PolyArcPath[] currCoincidentSlice = new PolyArcPath[]{new PolyArcPath()};

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

            if (Geom.almostEqual(v1.pos(), intr.point1, Utils.realPrecision)) {
                // coincidence starts at beginning of segment, report as starting at end of previous index
                sliceStart.sIndex1 = Utils.prevWrappingIndex(intr.sIndex1, pline1);
            } else {
                sliceStart.sIndex1 = intr.sIndex1;
            }

            if (Geom.almostEqual(u1.pos(), sliceStart.pos, Utils.realPrecision)) {
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
            currCoincidentSlice[0] = new PolyArcPath();
            PlineIntersect sliceEnd = new PlineIntersect();
            sliceEnd.pos = intr.point2;
            sliceEnd.sIndex1 = intr.sIndex1;
            if (Geom.almostEqual(u1.pos(), sliceEnd.pos, Utils.realPrecision)) {
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

            if (Geom.almostEqual(intr.point1, currCoincidentSlice[0].lastVertex().pos(),
                    Utils.realPrecision)) {
                // continue coincident slice
                currCoincidentSlice[0].removeLast();
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
            final Point2D.Double lastSliceEnd = coincidentSlices.getLast().lastVertex().pos();
            final Point2D.Double firstSliceBegin = coincidentSlices.getFirst().get(0).pos();
            if (Geom.almostEqual(lastSliceEnd, firstSliceBegin, Utils.realPrecision)) {
                // they do connect, join them together
                final PolyArcPath lastSlice = coincidentSlices.getLast();
                lastSlice.removeLast();
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

    /**
     * Finds all local self intersects of the polyline, local self intersects are defined as between
     * two polyline segments that share a vertex. NOTES:
     * - Singularities (repeating vertexes) are returned as coincident intersects
     */
    public static void localSelfIntersects(final PolyArcPath pline, final List<PlineIntersect> output) {
        if (pline.size() < 2) {
            return;
        }

        if (pline.size() == 2) {
            if (pline.isClosed()) {
                // check if overlaps on itself from vertex 1 to vertex 2
                if (Geom.almostEqual(pline.get(0).bulge(), -pline.get(1).bulge())) {
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

            if (Geom.almostEqual(v1.pos(), v2.pos(), Utils.realPrecision)) {
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
                        if (!Geom.almostEqual(intrResult.point1, v2.pos(), Utils.realPrecision)) {
                            output.add(new PlineIntersect(i, j, intrResult.point1));
                        }
                        break;
                    case TwoIntersects:
                        if (!Geom.almostEqual(intrResult.point1, v2.pos(), Utils.realPrecision)) {
                            output.add(new PlineIntersect(i, j, intrResult.point1));
                        }
                        if (!Geom.almostEqual(intrResult.point2, v2.pos(), Utils.realPrecision)) {
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
    static void globalSelfIntersects(final PolyArcPath pline, final List<PlineIntersect> output,
                                     final StaticSpatialIndex spatialIndex) {
        if (pline.size() < 3) {
            return;
        }

        Set<OrderedPair<Integer, Integer>>
                visitedSegmentPairs = new HashSet<>(pline.size());

        IntArrayDeque queryStack = new IntArrayDeque(8);

        StaticSpatialIndex.Visitor visitor = (int i, double minX, double minY, double maxX, double maxY) -> {
            int j = Utils.nextWrappingIndex(i, pline);
            final PlineVertex v1 = pline.get(i);
            final PlineVertex v2 = pline.get(j);
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

                Predicate<Point2D.Double> intrAtStartPt = (final Point2D.Double intr) ->
                        Geom.almostEqual(v1.pos(), intr) || Geom.almostEqual(u1.pos(), intr);

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

            spatialIndex.visitQuery(
                    minX - Geom.REAL_THRESHOLD, minY - Geom.REAL_THRESHOLD, maxX + Geom.REAL_THRESHOLD, maxY + Geom.REAL_THRESHOLD,
                    indexVisitor, queryStack);

            // visit all pline indexes
            return true;
        };

        spatialIndex.visitItemBoxes(visitor);
    }

    /// Finds all self intersects of the polyline (equivalent to calling localSelfIntersects and
    /// globalSelfIntersects).
    public static void allSelfIntersects(final PolyArcPath pline, final List<PlineIntersect> output,
                                         final StaticSpatialIndex spatialIndex) {
        localSelfIntersects(pline, output);
        globalSelfIntersects(pline, output, spatialIndex);
    }

    /**
     * Finds all intersects between pline1 and pline2.
     */
    static void findIntersects(final PolyArcPath pline1, final PolyArcPath pline2,
                               final StaticSpatialIndex pline1SpatialIndex,
                               final PlineIntersectsResult output) {
        IntArrayList queryResults = new IntArrayList();
        IntArrayDeque queryStack = new IntArrayDeque(8);

        Set<OrderedPair<Integer, Integer>> possibleDuplicates = new HashSet<>();

        final List<PlineIntersect> intrs = output.intersects;
        final List<PlineCoincidentIntersect> coincidentIntrs = output.coincidentIntersects;

        BiPredicate<Integer, Integer> pline2SegVisitor = (Integer i2, Integer j2) -> {
            final PlineVertex p2v1 = pline2.get(i2);
            final PlineVertex p2v2 = pline2.get(j2);

            queryResults.clear();
            AABB bb = createFastApproxBoundingBox(p2v1, p2v2);
            pline1SpatialIndex.query(bb.getMinX(), bb.getMinY(), bb.getMaxX(), bb.getMaxY(), queryResults, queryStack);
            for (int i1 : queryResults) {
                int j1 = Utils.nextWrappingIndex(i1, pline1);
                final PlineVertex p1v1 = pline1.get(i1);
                final PlineVertex p1v2 = pline1.get(j1);

                Predicate<Point2D.Double> intrAtStartPt = (final Point2D.Double intr) ->
                        Geom.almostEqual(p1v1.pos(), intr) || Geom.almostEqual(p2v1.pos(), intr);

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
                        if (Geom.almostEqual(p1v1.pos(), intrResult.point1) ||
                                Geom.almostEqual(p1v1.pos(), intrResult.point2)) {
                            possibleDuplicates.add(new OrderedPair<>(Utils.prevWrappingIndex(i1, pline1), i2));
                        }
                        if (Geom.almostEqual(p2v1.pos(), intrResult.point1) ||
                                Geom.almostEqual(p2v1.pos(), intrResult.point2)) {
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

            final Point2D.Double endPt1 =
                    pline1.get(Utils.nextWrappingIndex(intr.sIndex1, pline1)).pos();
            if (Geom.almostEqual(intr.pos, endPt1)) {
                return true;
            }

            final Point2D.Double endPt2 =
                    pline2.get(Utils.nextWrappingIndex(intr.sIndex2, pline2)).pos();
            return Geom.almostEqual(intr.pos, endPt2);
        });
    }

    static IntrPlineSegsResult intrPlineSegs(final PlineVertex v1, final PlineVertex v2,
                                             final PlineVertex u1, final PlineVertex u2) {
        IntrPlineSegsResult result = new IntrPlineSegsResult();
        final boolean vIsLine = v1.bulgeIsZero();
        final boolean uIsLine = u1.bulgeIsZero();

        // helper function to process line arc intersect
        QuadConsumer<Point2D.Double, Point2D.Double, PlineVertex, PlineVertex> processLineArcIntr
                = (final Point2D.Double p0, final Point2D.Double p1,
                   final PlineVertex a1, final PlineVertex a2) -> {
            BulgeConversionFunctions.ArcRadiusAndCenter arc = arcRadiusAndCenter(a1, a2);
            IntersectionResult intrResult = intrLineSeg2Circle2(p0, p1, arc.radius, arc.center);

            // helper function to test and get point within arc sweep
            DoubleFunction<OrderedPairNonNull<Boolean, Point2D.Double>> pointInSweep = (double t) -> {
                if (t + Geom.REAL_THRESHOLD < 0.0 ||
                        t > 1.0 + Geom.REAL_THRESHOLD) {
                    return new OrderedPairNonNull<>(false, new Point2D.Double(0, 0));
                }

                Point2D.Double p = pointFromParametric(p0, p1, t);
                boolean withinSweep = pointWithinArcSweepAngle(arc.center, a1.pos(), a2.pos(), a1.bulge(), p);
                return new OrderedPairNonNull<>(withinSweep, p);
            };

            if (intrResult.size() == 0) {
                result.intrType = PlineSegIntrType.NoIntersect;
            } else if (intrResult.size() == 1) {
                OrderedPairNonNull<Boolean, Point2D.Double> p = pointInSweep.apply(intrResult.getFirst().getArgumentA());
                if (p.first()) {
                    result.intrType = PlineSegIntrType.OneIntersect;
                    result.point1 = p.second();
                } else {
                    result.intrType = PlineSegIntrType.NoIntersect;
                }
            } else {
                assert intrResult.size() == 2 : "shouldn't get here without 2 intersects";
                OrderedPairNonNull<Boolean, Point2D.Double> p1_ = pointInSweep.apply(intrResult.getFirst().getArgumentA());
                OrderedPairNonNull<Boolean, Point2D.Double> p2_ = pointInSweep.apply(intrResult.getLast().getArgumentA());

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
            IntersectionResultEx intrResult = intrLineSeg2LineSeg2(v1.pos(), v2.pos(), u1.pos(), u2.pos());
            switch (intrResult.getStatus()) {
                case NO_INTERSECTION_PARALLEL:
                    result.intrType = PlineSegIntrType.NoIntersect;
                    break;
                case INTERSECTION:
                    result.intrType = PlineSegIntrType.OneIntersect;
                    result.point1 = intrResult.getFirst();
                    break;
                case NO_INTERSECTION_COINCIDENT:
                    result.intrType = PlineSegIntrType.SegmentOverlap;
                    // build points from parametric parameters (using second() segment as defined by the function)
                    double firstB = intrResult.get(0).getArgumentB();
                    double secondB = intrResult.get(1).getArgumentB();
                    if (firstB < secondB) {
                        result.point1 = pointFromParametric(u1.pos(), u2.pos(), firstB);
                        result.point2 = pointFromParametric(u1.pos(), u2.pos(), secondB);
                    } else {
                        result.point1 = pointFromParametric(u1.pos(), u2.pos(), secondB);
                        result.point2 = pointFromParametric(u1.pos(), u2.pos(), firstB);
                    }
                    break;
                case NO_INTERSECTION:
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

            TriFunction<Point2D.Double, Point2D.Double, Double, OrderedPairNonNull<Double, Double>> startAndSweepAngle = (final Point2D.Double sp, final Point2D.Double center, Double bulge) -> {
                double startAngle = Utils.normalizeRadians(Utils.angle(center, sp));
                double sweepAngle = 4.0 * Math.atan(bulge);
                return new OrderedPairNonNull<>(startAngle, sweepAngle);
            };

            Predicate<Point2D.Double> bothArcsSweepPoint = (final Point2D.Double pt) ->
                    pointWithinArcSweepAngle(arc1.center, v1.pos(), v2.pos(), v1.bulge(), pt) &&
                            pointWithinArcSweepAngle(arc2.center, u1.pos(), u2.pos(), u1.bulge(), pt);

            IntersectionResult intrResult = intrCircle2Circle2(arc1.radius, arc1.center, arc2.radius, arc2.center);

            switch (intrResult.getStatus()) {
                case NO_INTERSECTION_OUTSIDE:
                case NO_INTERSECTION_INSIDE:
                    result.intrType = PlineSegIntrType.NoIntersect;
                    break;
                case INTERSECTION:
                    if (intrResult.size() == 1) {
                        if (bothArcsSweepPoint.test(intrResult.getFirst())) {
                            result.intrType = PlineSegIntrType.OneIntersect;
                            result.point1 = intrResult.getFirst();
                        } else {
                            result.intrType = PlineSegIntrType.NoIntersect;
                        }
                    } else {
                        assert intrResult.size() == 2 : "there must be 2 intersections";
                        final boolean pt1InSweep = bothArcsSweepPoint.test(intrResult.getFirst());
                        final boolean pt2InSweep = bothArcsSweepPoint.test(intrResult.getLast());
                        if (pt1InSweep && pt2InSweep) {
                            result.intrType = PlineSegIntrType.TwoIntersects;
                            result.point1 = intrResult.getFirst();
                            result.point2 = intrResult.getLast();
                        } else if (pt1InSweep) {
                            result.intrType = PlineSegIntrType.OneIntersect;
                            result.point1 = intrResult.getFirst();
                        } else if (pt2InSweep) {
                            result.intrType = PlineSegIntrType.OneIntersect;
                            result.point1 = intrResult.getLast();
                        } else {
                            result.intrType = PlineSegIntrType.NoIntersect;
                        }
                    }
                    break;
                case NO_INTERSECTION_COINCIDENT:
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

                    if (Geom.almostEqual(arc1StartAndSweep.first(), arc2End)) {
                        // only end points touch at start of arc1
                        result.intrType = PlineSegIntrType.OneIntersect;
                        result.point1 = v1.pos();
                    } else if (Geom.almostEqual(arc2StartAndSweep.first(), arc1End)) {
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
