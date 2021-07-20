/*
 * @(#)ContourBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.IntArrayDeque;
import org.jhotdraw8.collection.IntArrayList;
import org.jhotdraw8.collection.OrderedPair;
import org.jhotdraw8.geom.AABB;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Points2D;
import org.jhotdraw8.geom.intersect.IntersectionResult;
import org.jhotdraw8.geom.intersect.IntersectionResultEx;
import org.jhotdraw8.util.function.QuintFunction;
import org.jhotdraw8.util.function.TriConsumer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static org.jhotdraw8.geom.contour.BulgeConversionFunctions.arcRadiusAndCenter;
import static org.jhotdraw8.geom.contour.ContourIntersections.allSelfIntersects;
import static org.jhotdraw8.geom.contour.ContourIntersections.findIntersects;
import static org.jhotdraw8.geom.contour.ContourIntersections.intrCircle2Circle2;
import static org.jhotdraw8.geom.contour.ContourIntersections.intrLineSeg2Circle2;
import static org.jhotdraw8.geom.contour.ContourIntersections.intrLineSeg2LineSeg2;
import static org.jhotdraw8.geom.contour.ContourIntersections.intrPlineSegs;
import static org.jhotdraw8.geom.contour.PlineVertex.closestPointOnSeg;
import static org.jhotdraw8.geom.contour.PlineVertex.createFastApproxBoundingBox;
import static org.jhotdraw8.geom.contour.PlineVertex.segMidpoint;
import static org.jhotdraw8.geom.contour.PlineVertex.splitAtPoint;
import static org.jhotdraw8.geom.contour.PolyArcPath.createApproxSpatialIndex;
import static org.jhotdraw8.geom.contour.Utils.angle;
import static org.jhotdraw8.geom.contour.Utils.deltaAngle;
import static org.jhotdraw8.geom.contour.Utils.pointFromParametric;
import static org.jhotdraw8.geom.contour.Utils.pointWithinArcSweepAngle;
import static org.jhotdraw8.geom.contour.Utils.realPrecision;
import static org.jhotdraw8.geom.contour.Utils.sliceJoinThreshold;
import static org.jhotdraw8.geom.contour.Utils.unitPerp;

/**
 * A factory for building an offset path (a contour) of a {@link PolyArcPath}.
 * <p>
 * This code has been derived from Cavalier Contours [1].
 * <p>
 * References:
 * <dl>
 *     <dt>[1] Cavalier Contours</dt>
 *     <dd>Cavalier Contours, Copyright (c) 2019 Jedidiah Buck McCready, MIT License.
 *     <a href="https://github.com/jbuckmccready/CavalierContours">github.com</a></dd>
 * </dl>
 */
public class ContourBuilder {


    /// Function to test if a point is a valid distance from the original polyline.
    static boolean pointValidForOffset(PolyArcPath pline, double offset,
                                       StaticSpatialIndex spatialIndex,
                                       Point2D.Double point, IntArrayDeque queryStack) {
        return pointValidForOffset(pline, offset, spatialIndex, point,
                queryStack, Utils.offsetThreshold);
    }

    static boolean pointValidForOffset(PolyArcPath pline, double offset,
                                       StaticSpatialIndex spatialIndex,
                                       Point2D.Double point, IntArrayDeque queryStack,
                                       double offsetTol) {
        final double absOffset = Math.abs(offset) - offsetTol;
        final double minDistSq = absOffset * absOffset;

        boolean[] pointValid = {true};

        IntPredicate visitor = (int i) -> {
            int j = Utils.nextWrappingIndex(i, pline);
            Point2D.Double closestPoint = closestPointOnSeg(pline.get(i), pline.get(j), point);
            double distSq = closestPoint.distanceSq(point);
            pointValid[0] = distSq > minDistSq;
            return pointValid[0];
        };

        spatialIndex.visitQuery(point.getX() - absOffset, point.getY() - absOffset, point.getX() + absOffset,
                point.getY() + absOffset, visitor, queryStack);
        return pointValid[0];
    }

    void addOrReplaceIfSamePos(PolyArcPath pline, final PlineVertex vertex) {
        addOrReplaceIfSamePos(pline, vertex, realPrecision);
    }

    void addOrReplaceIfSamePos(PolyArcPath pline, final PlineVertex vertex,
                               double epsilon) {
        if (pline.size() == 0) {
            pline.addVertex(vertex);
            return;
        }

        if (Geom.almostEqual(pline.lastVertex().pos(), vertex.pos(), epsilon)) {
            pline.lastVertex().bulge(vertex.bulge());
            return;
        }

        pline.addVertex(vertex);
    }

    void arcToArcJoin(final PlineOffsetSegment s1, final PlineOffsetSegment s2,
                      boolean connectionArcsAreCCW, PolyArcPath result) {

        final PlineVertex v1 = s1.v1;
        final PlineVertex v2 = s1.v2;
        final PlineVertex u1 = s2.v1;
        final PlineVertex u2 = s2.v2;
        assert !v1.bulgeIsZero() && !u1.bulgeIsZero() : "both segs should be arcs";

        final BulgeConversionFunctions.ArcRadiusAndCenter arc1 = arcRadiusAndCenter(v1, v2);
        final BulgeConversionFunctions.ArcRadiusAndCenter arc2 = arcRadiusAndCenter(u1, u2);

        Runnable connectUsingArc = () -> {
            final Point2D.Double arcCenter = s1.origV2Pos;
            final Point2D.Double sp = v2.pos();
            final Point2D.Double ep = u1.pos();
            double bulge = bulgeForConnection(arcCenter, sp, ep, connectionArcsAreCCW);
            addOrReplaceIfSamePos(result, new PlineVertex(sp, bulge));
            addOrReplaceIfSamePos(result, u1);
        };

        Consumer<Point2D.Double> processIntersect = (final Point2D.Double intersect) -> {
            final boolean trueArcIntersect1 =
                    pointWithinArcSweepAngle(arc1.center, v1.pos(), v2.pos(), v1.bulge(), intersect);
            final boolean trueArcIntersect2 =
                    pointWithinArcSweepAngle(arc2.center, u1.pos(), u2.pos(), u1.bulge(), intersect);

            if (trueArcIntersect1 && trueArcIntersect2) {
                // modify previous bulge and trim at intersect
                PlineVertex prevVertex = result.lastVertex();
                if (!prevVertex.bulgeIsZero()) {
                    double a1 = angle(arc1.center, intersect);
                    BulgeConversionFunctions.ArcRadiusAndCenter prevArc = arcRadiusAndCenter(prevVertex, v2);
                    double prevArcStartAngle = angle(prevArc.center, prevVertex.pos());
                    double updatedPrevTheta = deltaAngle(prevArcStartAngle, a1);

                    // ensure the sign matches (may get flipped if intersect is at the very end of the arc, in
                    // which case we do not want to update the bulge)
                    if ((updatedPrevTheta > 0.0) == prevVertex.bulgeIsPos()) {
                        result.lastVertex().bulge(Math.tan(updatedPrevTheta / 4.0));
                    }
                }

                // add the vertex at our current trim/join point
                double a2 = angle(arc2.center, intersect);
                double endAngle = angle(arc2.center, u2.pos());
                double theta = deltaAngle(a2, endAngle);

                // ensure the sign matches (may get flipped if intersect is at the very end of the arc, in
                // which case we do not want to update the bulge)
                if ((theta > 0.0) == u1.bulgeIsPos()) {
                    addOrReplaceIfSamePos(result, new PlineVertex(intersect, Math.tan(theta / 4.0)));
                } else {
                    addOrReplaceIfSamePos(result, new PlineVertex(intersect, u1.bulge()));
                }

            } else {
                connectUsingArc.run();
            }
        };

        final IntersectionResult intrResult = intrCircle2Circle2(arc1.radius, arc1.center, arc2.radius, arc2.center);
        switch (intrResult.getStatus()) {
        case NO_INTERSECTION_OUTSIDE:
        case NO_INTERSECTION_INSIDE:
            connectUsingArc.run();
            break;
        case INTERSECTION:
            if (intrResult.size() == 1) {
                processIntersect.accept(intrResult.getFirst());
            } else {
                assert intrResult.size() == 2 : "there must be 2 intersections";
                double dist1 = intrResult.getFirst().distanceSq(s1.origV2Pos);
                double dist2 = intrResult.getLast().distanceSq(s1.origV2Pos);
                if (dist1 < dist2) {
                    processIntersect.accept(intrResult.getFirst());
                } else {
                    processIntersect.accept(intrResult.getLast());
                }
            }
            break;
        case NO_INTERSECTION_COINCIDENT:
            // same constant arc radius and center, just add the vertex (nothing to trim/extend)
            addOrReplaceIfSamePos(result, u1);
            break;
        }
    }

    void arcToLineJoin(final PlineOffsetSegment s1, final PlineOffsetSegment s2,
                       boolean connectionArcsAreCCW, PolyArcPath result) {

        final PlineVertex v1 = s1.v1;
        final PlineVertex v2 = s1.v2;
        final PlineVertex u1 = s2.v1;
        final PlineVertex u2 = s2.v2;
        assert !v1.bulgeIsZero() && u1.bulgeIsZero() :
                "first seg should be line, second seg should be arc";

        Runnable connectUsingArc = () -> {
            final Point2D.Double arcCenter = s1.origV2Pos;
            final Point2D.Double sp = v2.pos();
            final Point2D.Double ep = u1.pos();
            double bulge = bulgeForConnection(arcCenter, sp, ep, connectionArcsAreCCW);
            addOrReplaceIfSamePos(result, new PlineVertex(sp, bulge));
            addOrReplaceIfSamePos(result, u1);
        };

        final BulgeConversionFunctions.ArcRadiusAndCenter arc = arcRadiusAndCenter(v1, v2);

        BiConsumer<Double, Point2D.Double> processIntersect = (Double t, final Point2D.Double intersect) -> {
            final boolean trueSegIntersect = !falseIntersect(t);
            final boolean trueArcIntersect =
                    pointWithinArcSweepAngle(arc.center, v1.pos(), v2.pos(), v1.bulge(), intersect);
            if (trueSegIntersect && trueArcIntersect) {
                // modify previous bulge and trim at intersect
                PlineVertex prevVertex = result.lastVertex();

                if (!prevVertex.bulgeIsZero()) {
                    double a = angle(arc.center, intersect);
                    BulgeConversionFunctions.ArcRadiusAndCenter prevArc = arcRadiusAndCenter(prevVertex, v2);
                    double prevArcStartAngle = angle(prevArc.center, prevVertex.pos());
                    double updatedPrevTheta = deltaAngle(prevArcStartAngle, a);

                    // ensure the sign matches (may get flipped if intersect is at the very end of the arc, in
                    // which case we do not want to update the bulge)
                    if ((updatedPrevTheta > 0.0) == prevVertex.bulgeIsPos()) {
                        result.lastVertex().bulge(Math.tan(updatedPrevTheta / 4.0));
                    }
                }

                addOrReplaceIfSamePos(result, new PlineVertex(intersect, 0.0));

            } else {
                connectUsingArc.run();
            }
        };

        IntersectionResult intrResult = intrLineSeg2Circle2(u1.pos(), u2.pos(), arc.radius, arc.center);
        if (intrResult.size() == 0) {
            connectUsingArc.run();
        } else if (intrResult.size() == 1) {
            processIntersect.accept(intrResult.getFirst().getArgumentA(),
                    pointFromParametric(u1.pos(), u2.pos(), intrResult.getFirst().getArgumentA()));
        } else {
            assert intrResult.size() == 2 : "should have 2 intersects here";
            final Point2D.Double origPoint = s2.collapsedArc ? u1.pos() : s1.origV2Pos;
            Point2D.Double i1 = pointFromParametric(u1.pos(), u2.pos(), intrResult.getFirst().getArgumentA());
            double dist1 = i1.distanceSq(origPoint);
            Point2D.Double i2 = pointFromParametric(u1.pos(), u2.pos(), intrResult.getLast().getArgumentA());
            double dist2 = i2.distanceSq(origPoint);

            if (dist1 < dist2) {
                processIntersect.accept(intrResult.getFirst().getArgumentA(), i1);
            } else {
                processIntersect.accept(intrResult.getLast().getArgumentA(), i2);
            }
        }
    }

    /**
     * Gets the bulge to describe the arc going from start point to end point with the given arc center
     * and curve orientation, if orientation is negative then bulge is negative otherwise it is positive
     */
    double bulgeForConnection(final Point2D.Double arcCenter, final Point2D.Double sp,
                              final Point2D.Double ep, boolean isCCW) {
        double a1 = angle(arcCenter, sp);
        double a2 = angle(arcCenter, ep);
        double absSweepAngle = Math.abs(deltaAngle(a1, a2));
        double absBulge = Math.tan(absSweepAngle / 4.0);
        if (isCCW) {
            return absBulge;
        }

        return -absBulge;
    }

    /**
     * Creates the raw offset polyline.
     */
    public PolyArcPath createRawOffsetPline(PolyArcPath pline, double offset) {
        //final Polyline pline = removeCoincidentPoints(pline0);
        PolyArcPath result = new PolyArcPath();
        if (pline.size() < 2) {
            return result;
        }

        List<PlineOffsetSegment> rawOffsets = createUntrimmedOffsetSegments(pline, offset);
        if (rawOffsets.size() == 0) {
            return result;
        }

        // detect single collapsed arc segment (this may be removed in the future if invalid segments are
        // tracked in join functions to be pruned at slice creation)
        if (rawOffsets.size() == 1 && rawOffsets.get(0).collapsedArc) {
            return result;
        }

        result = new PolyArcPath(pline.size());
        result.isClosed(pline.isClosed());

        final boolean connectionArcsAreCCW = offset < 0;

        TriConsumer<PlineOffsetSegment, PlineOffsetSegment, PolyArcPath> joinResultVisitor = (s1, s2, presult) -> {
            final boolean s1IsLine = s1.v1.bulgeIsZero();
            final boolean s2IsLine = s2.v1.bulgeIsZero();
            if (s1IsLine && s2IsLine) {
                lineToLineJoin(s1, s2, connectionArcsAreCCW, presult);
            } else if (s1IsLine) {
                lineToArcJoin(s1, s2, connectionArcsAreCCW, presult);
            } else if (s2IsLine) {
                arcToLineJoin(s1, s2, connectionArcsAreCCW, presult);
            } else {
                arcToArcJoin(s1, s2, connectionArcsAreCCW, presult);
            }
        };

        result.addVertex(rawOffsets.get(0).v1);

        // join first two segments and determine if first vertex was replaced (to know how to handle last
        // two segment joins for closed polyline)
        if (rawOffsets.size() > 1) {

            final PlineOffsetSegment seg01 = rawOffsets.get(0);
            final PlineOffsetSegment seg12 = rawOffsets.get(1);
            joinResultVisitor.accept(seg01, seg12, result);
        }
        final boolean firstVertexReplaced = result.size() == 1;

        for (int i = 2; i < rawOffsets.size(); ++i) {
            final PlineOffsetSegment seg1 = rawOffsets.get(i - 1);
            final PlineOffsetSegment seg2 = rawOffsets.get(i);
            joinResultVisitor.accept(seg1, seg2, result);
        }

        if (pline.isClosed() && result.size() > 1) {
            // joining segments at vertex indexes (n, 0) and (0, 1)
            final PlineOffsetSegment s1 = rawOffsets.get(rawOffsets.size() - 1);
            final PlineOffsetSegment s2 = rawOffsets.get(0);

            // temp polyline to capture results of joining (to avoid mutating result)
            PolyArcPath closingPartResult = new PolyArcPath();
            closingPartResult.addVertex(result.lastVertex());
            joinResultVisitor.accept(s1, s2, closingPartResult);

            // update last vertexes
            result.set(result.size() - 1, closingPartResult.get(0));
            for (int i = 1; i < closingPartResult.size(); ++i) {
                result.addVertex(closingPartResult.get(i));
            }
            result.removeLast();

            // update first vertex (only if it has not already been updated/replaced)
            if (!firstVertexReplaced) {
                final Point2D.Double updatedFirstPos = closingPartResult.lastVertex().pos();
                if (result.get(0).bulgeIsZero()) {
                    // just update position
                    result.set(0, new PlineVertex(updatedFirstPos, 0.0));
                } else if (result.size() > 1) {
                    // update position and bulge
                    final BulgeConversionFunctions.ArcRadiusAndCenter arc = arcRadiusAndCenter(result.get(0), result.get(1));
                    final double a1 = angle(arc.center, updatedFirstPos);
                    final double a2 = angle(arc.center, result.get(1).pos());
                    final double updatedTheta = deltaAngle(a1, a2);
                    if ((updatedTheta < 0.0 && result.get(0).bulgeIsPos()) ||
                            (updatedTheta > 0.0 && result.get(0).bulgeIsNeg())) {
                        // first vertex not valid, just update its position to be removed later
                        result.set(0, new PlineVertex(updatedFirstPos, result.get(0).bulge()));
                    } else {
                        // update position and bulge
                        result.set(0, new PlineVertex(updatedFirstPos, Math.tan(updatedTheta / 4.0)));
                    }
                }
            }

            // must do final singularity prune between first and second vertex after joining curves (n, 0)
            // and (0, 1)
            if (result.size() > 1) {
                if (Geom.almostEqual(result.get(0).pos(), result.get(1).pos(), realPrecision)) {
                    result.remove(0);
                }
            }
        } else {
            addOrReplaceIfSamePos(result, rawOffsets.get(rawOffsets.size() - 1).v2);
        }

        // if due to joining of segments we are left with only 1 vertex then return no raw offset (empty
        // polyline)
        if (result.size() == 1) {
            result.clear();
        }

        return result;
    }

    /**
     * Creates all the raw polyline offset segments.
     */
    List<PlineOffsetSegment> createUntrimmedOffsetSegments(@NonNull PolyArcPath pline,
                                                           double offset) {
        int size = pline.size();
        int segmentCount = pline.isClosed() ? size : size - 1;

        List<PlineOffsetSegment> result = new ArrayList<>(segmentCount);

        BiConsumer<PlineVertex, PlineVertex> lineVisitor = (v1, v2) -> {
            Point2D.Double edge = Points2D.subtract(v2.pos(),v1.pos());
            Point2D.Double offsetV = Points2D.multiply(unitPerp(edge),offset);
            PlineOffsetSegment seg = new PlineOffsetSegment(
                    new PlineVertex(Points2D.add(v1.pos(),offsetV), 0.0),
                    new PlineVertex(Points2D.add(v2.pos(),offsetV), 0.0),
                    v2.pos(), false);
            result.add(seg);
        };

        BiConsumer<PlineVertex, PlineVertex> arcVisitor = (v1, v2) -> {
            BulgeConversionFunctions.ArcRadiusAndCenter arc = arcRadiusAndCenter(v1, v2);
            double offs = v1.bulgeIsNeg() ? offset : -offset;
            double radiusAfterOffset = arc.radius + offs;
            Point2D.Double v1ToCenter = Points2D.normalize(Points2D.subtract(v1.pos(),arc.center));
            Point2D.Double v2ToCenter = Points2D.normalize(Points2D.subtract(v2.pos(),arc.center));

            // collapsed arc, offset arc start and end points towards arc center and turn into line
            // handles case where offset vertexes are equal and simplifies path for clipping algorithm
            boolean isCollapsedArc = radiusAfterOffset < Geom.REAL_THRESHOLD;

            PlineOffsetSegment seg = new PlineOffsetSegment(
                    new PlineVertex(Points2D.add(Points2D.multiply(v1ToCenter,offs),v1.pos()),
                            isCollapsedArc ? 0.0 : v1.bulge()),
                    new PlineVertex(Points2D.add(Points2D.multiply(v2ToCenter,offs),v2.pos()), v2.bulge()),
                    v2.pos(), isCollapsedArc);
            result.add(seg);
        };

        BiConsumer<PlineVertex, PlineVertex> offsetVisitor = (v1, v2) -> {
            double dx = v1.getX() - v2.getX();
            double dy = v1.getY() - v2.getY();
            double squaredDistance = dx * dx + dy * dy;
            if (squaredDistance <= Utils.realPrecision) {
                return;
            }

            if (v1.bulgeIsZero()) {
                lineVisitor.accept(v1, v2);
            } else {
                arcVisitor.accept(v1, v2);
            }
        };

        for (int i = 0; i < segmentCount; ++i) {
            offsetVisitor.accept(pline.get(i), pline.get((i + 1) % size));
        }

        return result;
    }

    List<OpenPolylineSlice> dualSliceAtIntersectsForOffset(final PolyArcPath originalPline,
                                                           final PolyArcPath rawOffsetPline,
                                                           final PolyArcPath dualRawOffsetPline, double offset) {
        List<OpenPolylineSlice> result = new ArrayList<>();
        if (rawOffsetPline.size() < 2) {
            return result;
        }

        StaticSpatialIndex origPlineSpatialIndex = createApproxSpatialIndex(originalPline);

        Map<Integer, List<Point2D.Double>> intersectsLookup = computeIntersectionsOfRawWithSelfWithDualRawAndAtEndPoints(originalPline, rawOffsetPline, dualRawOffsetPline, offset);

        IntArrayDeque queryStack = new IntArrayDeque(8);
        if (intersectsLookup.size() == 0) {
            if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, rawOffsetPline.get(0).pos(),
                    queryStack)) {
                return result;
            }
            // copy and convert raw offset into open polyline
            OpenPolylineSlice back = new OpenPolylineSlice(Integer.MAX_VALUE, rawOffsetPline);
            back.pline.isClosed(false);
            if (originalPline.isClosed()) {
                back.pline.addVertex(rawOffsetPline.get(0));
                back.pline.lastVertex().bulge(0.0);
            }
            result.add(back);
            return result;
        }

        // sort intersects by distance from start vertex
        for (Map.Entry<Integer, List<Point2D.Double>> entry : intersectsLookup.entrySet()) {
            Point2D.Double startPos = rawOffsetPline.get(entry.getKey()).pos();
            Comparator<Point2D.Double> cmp = Comparator.comparingDouble((Point2D.Double si) -> si.distanceSq(startPos));
            entry.getValue().sort(cmp);
        }

        BiPredicate<PlineVertex, PlineVertex> intersectsOrigPline = (final PlineVertex v1, final PlineVertex v2) -> {
            AABB approxBB = createFastApproxBoundingBox(v1, v2);
            boolean[] intersects = {false};
            IntPredicate visitor = (int i) -> {
                int j = Utils.nextWrappingIndex(i, originalPline);
                IntrPlineSegsResult intrResult =
                        intrPlineSegs(v1, v2, originalPline.get(i), originalPline.get(j));
                intersects[0] = intrResult.intrType != PlineSegIntrType.NoIntersect;
                return !intersects[0];
            };

            origPlineSpatialIndex.visitQuery(approxBB.getMinX(), approxBB.getMinY(),
                    approxBB.getMaxX(), approxBB.getMaxY(),
                    visitor, queryStack);

            return intersects[0];
        };

        if (!originalPline.isClosed()) {
            // build first open polyline that ends at the first intersect since we will not wrap back to
            // capture it as in the case of a closed polyline
            PolyArcPath firstSlice = new PolyArcPath();
            int index = 0;
            int loopCount = 0;
            final int maxLoopCount = rawOffsetPline.size();
            while (true) {
                if (loopCount++ > maxLoopCount) {
                    assert false : "Bug detected, should never loop this many times!";
                    // break to avoid infinite loop
                    break;
                }
                List<Point2D.Double> iter = intersectsLookup.get(index);
                if (iter == null) {
                    // no intersect found, test segment will be valid before adding the vertex
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
                            rawOffsetPline.get(index).pos(), queryStack)) {
                        break;
                    }

                    // index check (only test segment if we're not adding the first vertex)
                    if (index != 0 && intersectsOrigPline.test(firstSlice.lastVertex(), rawOffsetPline.get(index))) {
                        break;
                    }

                    addOrReplaceIfSamePos(firstSlice, rawOffsetPline.get(index));
                } else {
                    // intersect found, test segment will be valid before finishing first open polyline
                    final Point2D.Double intersectPos = iter.get(0);
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
                            intersectPos, queryStack)) {
                        break;
                    }

                    SplitResult split =
                            splitAtPoint(rawOffsetPline.get(index), rawOffsetPline.get(index + 1), intersectPos);

                    PlineVertex sliceEndVertex = new PlineVertex(intersectPos, 0.0);
                    Point2D.Double midpoint = segMidpoint(split.updatedStart, sliceEndVertex);
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, midpoint,
                            queryStack)) {
                        break;
                    }

                    if (intersectsOrigPline.test(split.updatedStart, sliceEndVertex)) {
                        break;
                    }

                    addOrReplaceIfSamePos(firstSlice, split.updatedStart);
                    addOrReplaceIfSamePos(firstSlice, sliceEndVertex);
                    result.add(new OpenPolylineSlice(0, firstSlice));
                    break;
                }

                index += 1;
            }
        }

        for (final Map.Entry<Integer, List<Point2D.Double>> kvp : intersectsLookup.entrySet()) {
            // start index for the slice we're about to build
            int sIndex = kvp.getKey();
            // self intersect list for this start index
            List<Point2D.Double> siList = kvp.getValue();

            final PlineVertex startVertex = rawOffsetPline.get(sIndex);
            int nextIndex = Utils.nextWrappingIndex(sIndex, rawOffsetPline);
            final PlineVertex endVertex = rawOffsetPline.get(nextIndex);

            if (siList.size() != 1) {
                // build all the segments between the N intersects in siList (N > 1), skipping the first
                // segment (to be processed at the end)
                SplitResult firstSplit = splitAtPoint(startVertex, endVertex, siList.get(0));
                PlineVertex prevVertex = firstSplit.splitVertex;
                for (int i = 1; i < siList.size(); ++i) {
                    SplitResult split = splitAtPoint(prevVertex, endVertex, siList.get(i));
                    // update prevVertex for next loop iteration
                    prevVertex = split.splitVertex;
                    // skip if they're ontop of each other
                    if (Geom.almostEqual(split.updatedStart.pos(), split.splitVertex.pos(),
                            Utils.realPrecision)) {
                        continue;
                    }

                    // test start point
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
                            split.updatedStart.pos(), queryStack)) {
                        continue;
                    }

                    // test end point
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
                            split.splitVertex.pos(), queryStack)) {
                        continue;
                    }

                    // test mid point
                    Point2D.Double midpoint = segMidpoint(split.updatedStart, split.splitVertex);
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, midpoint,
                            queryStack)) {
                        continue;
                    }

                    // test intersection with original polyline
                    if (intersectsOrigPline.test(split.updatedStart, split.splitVertex)) {
                        continue;
                    }
                    OpenPolylineSlice back = new OpenPolylineSlice(sIndex);
                    back.pline.addVertex(split.updatedStart);
                    back.pline.addVertex(split.splitVertex);
                    result.add(back);
                }
            }

            // build the segment between the last intersect in siList and the next intersect found

            // check that the first point is valid
            if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, siList.get(siList.size() - 1),
                    queryStack)) {
                continue;
            }

            SplitResult split = splitAtPoint(startVertex, endVertex, siList.get(siList.size() - 1));
            PolyArcPath currSlice = new PolyArcPath();
            currSlice.addVertex(split.splitVertex.clone());

            int index = nextIndex;
            boolean isValidPline = true;
            int loopCount = 0;
            final int maxLoopCount = rawOffsetPline.size();
            while (true) {
                if (loopCount++ > maxLoopCount) {
                    assert false : "Bug detected, should never loop this many times!";
                    // break to avoid infinite loop
                    break;
                }
                // check that vertex point is valid
                if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
                        rawOffsetPline.get(index).pos(), queryStack)) {
                    isValidPline = false;
                    break;
                }

                // check that the segment does not intersect original polyline
                if (intersectsOrigPline.test(currSlice.lastVertex(), rawOffsetPline.get(index))) {
                    isValidPline = false;
                    break;
                }

                // add vertex
                addOrReplaceIfSamePos(currSlice, rawOffsetPline.get(index));

                // check if segment that starts at vertex we just added has an intersect
                List<Point2D.Double> nextIntr = intersectsLookup.get(index);
                if (nextIntr != null) {
                    // there is an intersect, slice is done, check if final segment is valid

                    // check intersect pos is valid (which will also be end vertex position)
                    final Point2D.Double intersectPos = nextIntr.get(0);
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
                            intersectPos, queryStack)) {
                        isValidPline = false;
                        break;
                    }

                    nextIndex = Utils.nextWrappingIndex(index, rawOffsetPline);
                    split =
                            splitAtPoint(currSlice.lastVertex(), rawOffsetPline.get(nextIndex), intersectPos);

                    PlineVertex sliceEndVertex = new PlineVertex(intersectPos, 0.0);
                    // check mid point is valid
                    Point2D.Double mp = segMidpoint(split.updatedStart, sliceEndVertex);
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, mp,
                            queryStack)) {
                        isValidPline = false;
                        break;
                    }

                    // trim last added vertex and add final intersect position
                    currSlice.lastVertex(split.updatedStart);
                    addOrReplaceIfSamePos(currSlice, sliceEndVertex);

                    break;
                }
                // else there is not an intersect, increment index and continue
                if (index == rawOffsetPline.size() - 1) {
                    if (originalPline.isClosed()) {
                        // wrap index
                        index = 0;
                    } else {
                        // open polyline, we're done
                        break;
                    }
                } else {
                    index += 1;
                }
            }

            if (isValidPline && currSlice.size() > 1) {
                result.add(new OpenPolylineSlice(sIndex, currSlice));
            }
        }

        return result;
    }

    private @NonNull Map<Integer, List<Point2D.Double>> computeIntersectionsOfRawWithSelfWithDualRawAndAtEndPoints(
            @NonNull PolyArcPath originalPline,
            @NonNull PolyArcPath rawOffsetPline,
            @NonNull PolyArcPath dualRawOffsetPline,
            double offset) {

        StaticSpatialIndex rawOffsetPlineSpatialIndex = createApproxSpatialIndex(rawOffsetPline);

        List<PlineIntersect> selfIntersects = new ArrayList<>();
        allSelfIntersects(rawOffsetPline, selfIntersects, rawOffsetPlineSpatialIndex);

        PlineIntersectsResult dualIntersects = new PlineIntersectsResult();
        findIntersects(rawOffsetPline, dualRawOffsetPline, rawOffsetPlineSpatialIndex, dualIntersects);

        Map<Integer, List<Point2D.Double>> intersectsLookup;
        if (!originalPline.isClosed()) {
            // find intersects between circles generated at original open polyline end points and raw offset
            // polyline
            List<OrderedPair<Integer, List<Point2D.Double>>> intersects = new ArrayList<>();
            offsetCircleIntersectsWithPline(rawOffsetPline, offset, originalPline.get(0).pos(),
                    rawOffsetPlineSpatialIndex, intersects);
            offsetCircleIntersectsWithPline(rawOffsetPline, offset,
                    originalPline.lastVertex().pos(),
                    rawOffsetPlineSpatialIndex, intersects);
            intersectsLookup = new HashMap<>(2 * selfIntersects.size() + intersects.size());
            for (final OrderedPair<Integer, List<Point2D.Double>> pair : intersects) {
                intersectsLookup.computeIfAbsent(pair.first(), k -> new ArrayList<>()).addAll(pair.second());
            }
        } else {
            intersectsLookup = new HashMap<>(2 * selfIntersects.size());
        }

        for (final PlineIntersect si : selfIntersects) {
            intersectsLookup.computeIfAbsent(si.sIndex1, k -> new ArrayList<>()).add(si.pos);
            intersectsLookup.computeIfAbsent(si.sIndex2, k -> new ArrayList<>()).add(si.pos);
        }
        for (final PlineIntersect intr : dualIntersects.intersects) {
            intersectsLookup.computeIfAbsent(intr.sIndex1, k -> new ArrayList<>()).add(intr.pos);
        }
        for (final PlineCoincidentIntersect intr : dualIntersects.coincidentIntersects) {
            intersectsLookup.computeIfAbsent(intr.sIndex1, k -> new ArrayList<>()).add(intr.point1);
            intersectsLookup.computeIfAbsent(intr.sIndex1, k -> new ArrayList<>()).add(intr.point2);
        }
        return intersectsLookup;
    }

    boolean falseIntersect(double t) {
        return t < 0.0 || t > 1.0;
    }
    /// Slices a raw offset polyline at all of its self intersects.

    void lineToArcJoin(final PlineOffsetSegment s1, final PlineOffsetSegment s2,
                       boolean connectionArcsAreCCW, PolyArcPath result) {

        final PlineVertex v1 = s1.v1;
        final PlineVertex v2 = s1.v2;
        final PlineVertex u1 = s2.v1;
        final PlineVertex u2 = s2.v2;
        assert v1.bulgeIsZero() && !u1.bulgeIsZero() :
                "first seg should be arc, second seg should be line";

        Runnable connectUsingArc = () -> {
            final Point2D.Double arcCenter = s1.origV2Pos;
            final Point2D.Double sp = v2.pos();
            final Point2D.Double ep = u1.pos();
            double bulge = bulgeForConnection(arcCenter, sp, ep, connectionArcsAreCCW);
            addOrReplaceIfSamePos(result, new PlineVertex(sp, bulge));
            addOrReplaceIfSamePos(result, u1);
        };

        final BulgeConversionFunctions.ArcRadiusAndCenter arc = arcRadiusAndCenter(u1, u2);

        BiConsumer<Double, Point2D.Double> processIntersect = (Double t, final Point2D.Double intersect) -> {
            final boolean trueSegIntersect = !falseIntersect(t);
            final boolean trueArcIntersect =
                    pointWithinArcSweepAngle(arc.center, u1.pos(), u2.pos(), u1.bulge(), intersect);
            if (trueSegIntersect && trueArcIntersect) {
                // trim at intersect
                double a = angle(arc.center, intersect);
                double arcEndAngle = angle(arc.center, u2.pos());
                double theta = deltaAngle(a, arcEndAngle);
                // ensure the sign matches (may get flipped if intersect is at the very end of the arc, in
                // which case we do not want to update the bulge)
                if ((theta > 0.0) == u1.bulgeIsPos()) {
                    addOrReplaceIfSamePos(result, new PlineVertex(intersect, Math.tan(theta / 4.0)));
                } else {
                    addOrReplaceIfSamePos(result, new PlineVertex(intersect, u1.bulge()));
                }
            } else if (t > 1.0 && !trueArcIntersect) {
                connectUsingArc.run();
            } else if (s1.collapsedArc) {
                // collapsed arc connecting to arc, connect using arc
                connectUsingArc.run();
            } else {
                // connect using line
                addOrReplaceIfSamePos(result, new PlineVertex(v2.pos(), 0.0));
                addOrReplaceIfSamePos(result, u1);
            }
        };

        IntersectionResult intrResult = intrLineSeg2Circle2(v1.pos(), v2.pos(), arc.radius, arc.center);
        if (intrResult.size() == 0) {
            connectUsingArc.run();
        } else if (intrResult.size() == 1) {
            processIntersect.accept(intrResult.getFirst().getArgumentA(),
                    intrResult.getFirst());
        } else {
            assert intrResult.size() == 2 : "should have 2 intersects here";
            // always use intersect closest to original point
            Point2D.Double i1 = intrResult.getFirst();
            double dist1 = i1.distanceSq(s1.origV2Pos);
            Point2D.Double i2 = intrResult.getLast();
            double dist2 = i2.distanceSq(s1.origV2Pos);

            if (dist1 < dist2) {
                processIntersect.accept(intrResult.getFirst().getArgumentA(), i1);
            } else {
                processIntersect.accept(intrResult.getLast().getArgumentA(), i2);
            }
        }
    }
    /* Stitches raw offset polyline slices together, discarding any that are not valid. */

    void lineToLineJoin(final PlineOffsetSegment s1, final PlineOffsetSegment s2,
                        boolean connectionArcsAreCCW, PolyArcPath result) {
        final PlineVertex v1 = s1.v1;
        final PlineVertex v2 = s1.v2;
        final PlineVertex u1 = s2.v1;
        final PlineVertex u2 = s2.v2;
        assert v1.bulgeIsZero() && u1.bulgeIsZero() : "both segs should be lines";

        Runnable connectUsingArc = () -> {
            final Point2D.Double arcCenter = s1.origV2Pos;
            final Point2D.Double sp = v2.pos();
            final Point2D.Double ep = u1.pos();
            double bulge = bulgeForConnection(arcCenter, sp, ep, connectionArcsAreCCW);
            addOrReplaceIfSamePos(result, new PlineVertex(sp, bulge));
            addOrReplaceIfSamePos(result, new PlineVertex(ep, 0.0));
        };

        if (s1.collapsedArc || s2.collapsedArc) {
            // connecting to/from collapsed arc, always connect using arc
            connectUsingArc.run();
        } else {
            IntersectionResultEx intrResult = intrLineSeg2LineSeg2(v1.pos(), v2.pos(), u1.pos(), u2.pos());

            switch (intrResult.getStatus()) {
            case NO_INTERSECTION_PARALLEL:
                // PATCH WR: If the path turns back on itself, we must join it
                //           with an arc, to prevent that the path slice is
                //           removed, because without the arc, the intersection
                //           point of the segment may lie inside the shape.
                if (true) {
                    connectUsingArc.run();
                } else {
                    // just join with straight line
                    addOrReplaceIfSamePos(result, new PlineVertex(v2.pos(), 0.0));
                    addOrReplaceIfSamePos(result, u1);
                }
                break;
            case INTERSECTION:
                addOrReplaceIfSamePos(result, new PlineVertex(intrResult.getFirst(), 0.0));
                break;
            case NO_INTERSECTION_COINCIDENT:
                addOrReplaceIfSamePos(result, new PlineVertex(v2.pos(), 0.0));
                break;
            case NO_INTERSECTION:
                // PATCH WR: If the path turns by more than 180 degrees, we must
                //           also join it with an arc, to prevent that the path
                //           segment is removed, because without the arc,
                //           the intersection point of the slice may lie
                //           inside the shape.
                if (true || intrResult.getFirst().getArgumentA() > 1.0 && falseIntersect(intrResult.getFirst().getArgumentB())) {
                    // extend and join the lines together using an arc
                    connectUsingArc.run();
                } else {
                    addOrReplaceIfSamePos(result, new PlineVertex(v2.pos(), 0.0));
                    addOrReplaceIfSamePos(result, u1);
                }
                break;
            }
        }
    }

    void offsetCircleIntersectsWithPline(PolyArcPath pline, double offset,
                                         Point2D.Double circleCenter,
                                         StaticSpatialIndex spatialIndex,
                                         List<OrderedPair<Integer, List<Point2D.Double>>> output) {

        final double circleRadius = Math.abs(offset);

        IntArrayList queryResults = new IntArrayList();

        spatialIndex.query(circleCenter.getX() - circleRadius, circleCenter.getY() - circleRadius,
                circleCenter.getX() + circleRadius, circleCenter.getY() + circleRadius,
                queryResults);

        Predicate<Double> validLineSegIntersect = (Double t) -> !falseIntersect(t) && Math.abs(t) > Utils.realPrecision;

        QuintFunction<Point2D.Double, Point2D.Double, Point2D.Double, Double, Point2D.Double, Boolean>
                validArcSegIntersect = (Point2D.Double arcCenter, Point2D.Double arcStart,
                                        Point2D.Double arcEnd, Double bulge,
                                        Point2D.Double intrPoint) -> !Geom.almostEqual(arcStart, intrPoint, Utils.realPrecision) &&
                pointWithinArcSweepAngle(arcCenter, arcStart, arcEnd, bulge, intrPoint);

        for (int sIndex : queryResults) {
            PlineVertex v1 = pline.get(sIndex);
            PlineVertex v2 = pline.get(sIndex + 1);
            if (v1.bulgeIsZero()) {
                IntersectionResult intrResult =
                        intrLineSeg2Circle2(v1.pos(), v2.pos(), circleRadius, circleCenter);
                if (intrResult.size() == 0) {
                    continue;
                } else if (intrResult.size() == 1) {
                    if (validLineSegIntersect.test(intrResult.getFirst().getArgumentA())) {
                        output.add(new OrderedPair<>(sIndex,
                                Arrays.asList(intrResult.getFirst())));
                    }
                } else {
                    assert intrResult.size() == 2 : "should be two intersects here";
                    if (validLineSegIntersect.test(intrResult.getFirst().getArgumentA())) {
                        output.add(new OrderedPair<>(sIndex,
                                Arrays.asList(intrResult.getFirst())));
                    }
                    if (validLineSegIntersect.test(intrResult.getLast().getArgumentA())) {
                        output.add(new OrderedPair<>(sIndex,
                                Arrays.asList(intrResult.getLast())));
                    }
                }
            } else {
                BulgeConversionFunctions.ArcRadiusAndCenter arc = arcRadiusAndCenter(v1, v2);
                IntersectionResult intrResult =
                        intrCircle2Circle2(arc.radius, arc.center, circleRadius, circleCenter);
                switch (intrResult.getStatus()) {
                case NO_INTERSECTION_OUTSIDE:
                case NO_INTERSECTION_INSIDE:
                    break;
                case INTERSECTION:
                    if (intrResult.size() == 1) {
                        if (validArcSegIntersect.apply(arc.center, v1.pos(), v2.pos(), v1.bulge(), intrResult.getFirst())) {
                            output.add(new OrderedPair<>(sIndex, Arrays.asList(intrResult.getFirst())));
                        }
                    } else {
                        assert intrResult.size() == 2 : "there must be 2 intersections";

                        if (validArcSegIntersect.apply(arc.center, v1.pos(), v2.pos(), v1.bulge(), intrResult.getFirst())) {
                            output.add(new OrderedPair<>(sIndex, Arrays.asList(intrResult.getFirst())));
                        }
                        if (validArcSegIntersect.apply(arc.center, v1.pos(), v2.pos(), v1.bulge(), intrResult.getLast())) {
                            output.add(new OrderedPair<>(sIndex, Arrays.asList(intrResult.getLast())));
                        }
                    }
                    break;
                case NO_INTERSECTION_COINCIDENT:
                    break;
                }
            }
        }
    }

    /// Slices a raw offset polyline at all of its self intersects and intersects with its dual.

    /**
     * Creates the parallel offset polylines to the polyline given.
     *
     * @param pline  input polyline
     * @param offset offset
     * @return offset polyline
     */
    public @NonNull List<PolyArcPath> parallelOffset(@NonNull PolyArcPath pline, double offset) {
        return parallelOffset(pline, offset, true);
    }

    /**
     * Creates the parallel offset polylines to the polyline given.
     *
     * @param pline                 input polyline
     * @param offset                offset
     * @param mayHaveSelfIntersects true if the polyline may have self-intersects
     * @return list of offset polylines
     */
    private @NonNull List<PolyArcPath> parallelOffset(@NonNull PolyArcPath pline, double offset,
                                                      boolean mayHaveSelfIntersects) {

        if (pline.size() < 2) {
            return new ArrayList<>();
        }
        PolyArcPath rawOffset = createRawOffsetPline(pline, offset);
        if (pline.isClosed() && !mayHaveSelfIntersects) {
            List<OpenPolylineSlice> slices = slicesFromRawOffset(pline, rawOffset, offset);
            return stitchOffsetSlicesTogether(slices, pline.isClosed(), rawOffset.size() - 1);
        }

        // not closed polyline or has self intersects, must apply dual clipping
        PolyArcPath dualRawOffset = createRawOffsetPline(pline, -offset);
        List<OpenPolylineSlice> slices = dualSliceAtIntersectsForOffset(pline, rawOffset, dualRawOffset, offset);
        return stitchOffsetSlicesTogether(slices, pline.isClosed(), rawOffset.size() - 1);
    }

    List<OpenPolylineSlice> slicesFromRawOffset(final PolyArcPath originalPline,
                                                final PolyArcPath rawOffsetPline,
                                                double offset) {
        assert originalPline.isClosed() : "use dual slice at intersects for open polylines";

        List<OpenPolylineSlice> result = new ArrayList<>();
        if (rawOffsetPline.size() < 2) {
            return result;
        }

        StaticSpatialIndex origPlineSpatialIndex = createApproxSpatialIndex(originalPline);
        StaticSpatialIndex rawOffsetPlineSpatialIndex = createApproxSpatialIndex(rawOffsetPline);

        List<PlineIntersect> selfIntersects = new ArrayList<>();
        allSelfIntersects(rawOffsetPline, selfIntersects, rawOffsetPlineSpatialIndex);

        IntArrayDeque queryStack = new IntArrayDeque(8);
        if (selfIntersects.size() == 0) {
            if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, rawOffsetPline.get(0).pos(),
                    queryStack)) {
                return result;
            }
            // copy and convert raw offset into open polyline
            OpenPolylineSlice back = new OpenPolylineSlice(Integer.MAX_VALUE, rawOffsetPline);
            result.add(back);
            back.pline.isClosed(false);
            back.pline.addVertex(rawOffsetPline.get(0));
            back.pline.lastVertex().bulge(0.0);
            return result;
        }

        Map<Integer, List<Point2D.Double>> intersectsLookup = new HashMap<>(2 * selfIntersects.size());

        for (final PlineIntersect si : selfIntersects) {
            intersectsLookup.computeIfAbsent(si.sIndex1, k -> new ArrayList<>()).add(si.pos);
            intersectsLookup.computeIfAbsent(si.sIndex2, k -> new ArrayList<>()).add(si.pos);
        }

        // sort intersects by distance from start vertex
        for (Map.Entry<Integer, List<Point2D.Double>> kvp : intersectsLookup.entrySet()) {
            Point2D.Double startPos = rawOffsetPline.get(kvp.getKey()).pos();
            Comparator<Point2D.Double> cmp = Comparator.comparingDouble((Point2D.Double si) -> si.distanceSq(startPos));
            kvp.getValue().sort(cmp);
        }

        BiPredicate<PlineVertex, PlineVertex> intersectsOrigPline = (final PlineVertex v1, final PlineVertex v2) -> {
            AABB approxBB = createFastApproxBoundingBox(v1, v2);
            boolean[] hasIntersect = new boolean[]{false};
            IntPredicate visitor = (int i) -> {
                int j = Utils.nextWrappingIndex(i, originalPline);
                IntrPlineSegsResult intrResult =
                        intrPlineSegs(v1, v2, originalPline.get(i), originalPline.get(j));
                hasIntersect[0] = intrResult.intrType != PlineSegIntrType.NoIntersect;
                return !hasIntersect[0];
            };

            origPlineSpatialIndex.visitQuery(approxBB.getMinX(), approxBB.getMinY(),
                    approxBB.getMaxX(), approxBB.getMaxY(),
                    visitor, queryStack);

            return hasIntersect[0];
        };

        for (final Map.Entry<Integer, List<Point2D.Double>> kvp : intersectsLookup.entrySet()) {
            // start index for the slice we're about to build
            int sIndex = kvp.getKey();
            // self intersect list for this start index
            List<Point2D.Double> siList = kvp.getValue();

            final PlineVertex startVertex = rawOffsetPline.get(sIndex);
            int nextIndex = Utils.nextWrappingIndex(sIndex, rawOffsetPline);
            final PlineVertex endVertex = rawOffsetPline.get(nextIndex);

            if (siList.size() != 1) {
                // build all the segments between the N intersects in siList (N > 1), skipping the first
                // segment (to be processed at the end)
                SplitResult firstSplit = splitAtPoint(startVertex, endVertex, siList.get(0));
                PlineVertex prevVertex = firstSplit.splitVertex;
                for (int i = 1; i < siList.size(); ++i) {
                    SplitResult split = splitAtPoint(prevVertex, endVertex, siList.get(i));
                    // update prevVertex for next loop iteration
                    prevVertex = split.splitVertex;
                    // skip if they're ontop of each other
                    if (Geom.almostEqual(split.updatedStart.pos(), split.splitVertex.pos(),
                            Utils.realPrecision)) {
                        continue;
                    }

                    // test start point
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
                            split.updatedStart.pos(), queryStack)) {
                        continue;
                    }

                    // test end point
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
                            split.splitVertex.pos(), queryStack)) {
                        continue;
                    }

                    // test mid point
                    Point2D.Double midpoint = segMidpoint(split.updatedStart, split.splitVertex);
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, midpoint,
                            queryStack)) {
                        continue;
                    }

                    // test intersection with original polyline
                    if (intersectsOrigPline.test(split.updatedStart, split.splitVertex)) {
                        continue;
                    }

                    OpenPolylineSlice back = new OpenPolylineSlice(sIndex);
                    back.pline.addVertex(split.updatedStart);
                    back.pline.addVertex(split.splitVertex);
                    result.add(back);
                }
            }

            // build the segment between the last intersect in siList and the next intersect found

            // check that the first point is valid
            if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, siList.get(siList.size() - 1),
                    queryStack)) {
                continue;
            }

            SplitResult split = splitAtPoint(startVertex, endVertex, siList.get(siList.size() - 1));
            PolyArcPath currSlice = new PolyArcPath();
            currSlice.addVertex(split.splitVertex);

            int index = nextIndex;
            boolean isValidPline = true;
            int loopCount = 0;
            final int maxLoopCount = rawOffsetPline.size();
            while (true) {
                if (loopCount++ > maxLoopCount) {
                    assert false : "Bug detected, should never loop this many times!";
                    // break to avoid infinite loop
                    break;
                }
                // check that vertex point is valid
                if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
                        rawOffsetPline.get(index).pos(), queryStack)) {
                    isValidPline = false;
                    break;
                }

                // check that the segment does not intersect original polyline
                if (intersectsOrigPline.test(currSlice.lastVertex(), rawOffsetPline.get(index))) {
                    isValidPline = false;
                    break;
                }

                // add vertex
                addOrReplaceIfSamePos(currSlice, rawOffsetPline.get(index));

                // check if segment that starts at vertex we just added has an intersect
                List<Point2D.Double> nextIntr = intersectsLookup.get(index);
                if (nextIntr != null) {
                    // there is an intersect, slice is done, check if final segment is valid

                    // check intersect pos is valid (which will also be end vertex position)
                    final Point2D.Double intersectPos = nextIntr.get(0);
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
                            intersectPos, queryStack)) {
                        isValidPline = false;
                        break;
                    }

                    nextIndex = Utils.nextWrappingIndex(index, rawOffsetPline);
                    split =
                            splitAtPoint(currSlice.lastVertex(), rawOffsetPline.get(nextIndex), intersectPos);

                    PlineVertex sliceEndVertex = new PlineVertex(intersectPos, 0.0);
                    // check mid point is valid
                    Point2D.Double mp = segMidpoint(split.updatedStart, sliceEndVertex);
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, mp,
                            queryStack)) {
                        isValidPline = false;
                        break;
                    }

                    // trim last added vertex and add final intersect position
                    currSlice.lastVertex(split.updatedStart);
                    addOrReplaceIfSamePos(currSlice, sliceEndVertex);

                    break;
                }
                // else there is not an intersect, increment index and continue
                index = Utils.nextWrappingIndex(index, rawOffsetPline);
            }

            isValidPline = isValidPline && currSlice.size() > 1;

            if (isValidPline && Geom.almostEqual(currSlice.get(0).pos(), currSlice.lastVertex().pos())) {
                // discard very short slice loops (invalid loops may arise due to valid offset distance
                // thresholding)
                isValidPline = currSlice.getPathLength() > 1e-2;
            }

            if (isValidPline) {
                result.add(new OpenPolylineSlice(sIndex, currSlice));
            }
        }

        return result;
    }

    protected List<PolyArcPath> stitchOffsetSlicesTogether(
            final List<OpenPolylineSlice> slices, boolean closedPolyline, int origMaxIndex) {
        return stitchOffsetSlicesTogether(slices, closedPolyline, origMaxIndex, sliceJoinThreshold);
    }

    protected List<PolyArcPath> stitchOffsetSlicesTogether(
            final @NonNull List<OpenPolylineSlice> slices,
            boolean closedPolyline,
            int origMaxIndex,
            double joinThreshold) {

        List<PolyArcPath> result = new ArrayList<>();
        if (slices.size() == 0) {
            return result;
        }

        if (slices.size() == 1) {
            result.add(slices.get(0).pline);
            if (closedPolyline &&
                    Geom.almostEqual(result.get(0).get(0).pos(), result.get(0).lastVertex().pos(), joinThreshold)) {
                result.get(0).isClosed(true);
                result.get(0).removeLast();
            }

            return result;
        }

        // load spatial index with all start points
        StaticSpatialIndex spatialIndex = new StaticSpatialIndex(slices.size());
        for (final OpenPolylineSlice slice : slices) {
            final Point2D.Double point = slice.pline.get(0).pos();
            spatialIndex.add(point.getX() - joinThreshold, point.getY() - joinThreshold,
                    point.getX() + joinThreshold, point.getY() + joinThreshold);
        }
        spatialIndex.finish();

        BitSet visitedIndexes = new BitSet(slices.size());
        IntArrayList queryResults = new IntArrayList();
        IntArrayDeque queryStack = new IntArrayDeque(8);
        for (int i = 0; i < slices.size(); ++i) {
            if (visitedIndexes.get(i)) {
                continue;
            }

            visitedIndexes.set(i, true);

            PolyArcPath currPline = new PolyArcPath();
            int currIndex = i;
            final Point2D.Double initialStartPoint = slices.get(i).pline.get(0).pos();
            int loopCount = 0;
            final int maxLoopCount = slices.size();
            while (true) {
                if (loopCount++ > maxLoopCount) {
                    assert false : "Bug detected, should never loop this many times!";
                    // break to avoid infinite loop
                    break;
                }
                final int currLoopStartIndex = slices.get(currIndex).intrStartIndex;
                final PolyArcPath currSlice = slices.get(currIndex).pline;
                final Point2D.Double currEndPoint = slices.get(currIndex).pline.lastVertex().pos();
                currPline.addAll(currSlice);
                queryResults.clear();
                spatialIndex.query(currEndPoint.getX() - joinThreshold, currEndPoint.getY() - joinThreshold,
                        currEndPoint.getX() + joinThreshold, currEndPoint.getY() + joinThreshold,
                        queryResults, queryStack);
                queryResults.removeIf(visitedIndexes::get);

                Function<Integer, OrderedPair<Integer, Boolean>> indexDistAndEqualInitial = (Integer index) -> {
                    final OpenPolylineSlice slice = slices.get(index);
                    int indexDist;
                    if (currLoopStartIndex <= slice.intrStartIndex) {
                        indexDist = slice.intrStartIndex - currLoopStartIndex;
                    } else {
                        // forward wrapping distance (distance to end + distance to index)
                        indexDist = origMaxIndex - currLoopStartIndex + slice.intrStartIndex;
                    }

                    boolean equalToInitial = Geom.almostEqual(slice.pline.lastVertex().pos(), initialStartPoint,
                            Utils.realPrecision);

                    return new OrderedPair<>(indexDist, equalToInitial);
                };

                queryResults.sort(
                        (Integer index1, Integer index2) -> {
                            OrderedPair<Integer, Boolean> distAndEqualInitial1 = indexDistAndEqualInitial.apply(index1);
                            OrderedPair<Integer, Boolean> distAndEqualInitial2 = indexDistAndEqualInitial.apply(index2);
                            if (distAndEqualInitial1.first().equals(distAndEqualInitial2.first())) {
                                // index distances are equal, compare on position being equal to initial start
                                // (testing index1 < index2, we want the longest closed loop possible)
                                return (distAndEqualInitial1.second() ? 1 : 0) - (distAndEqualInitial2.second() ? 1 : 0);
                            }

                            return distAndEqualInitial2.first() - distAndEqualInitial1.first();
                        });

                if (queryResults.size() == 0) {
                    // we're done
                    if (currPline.size() > 1) {
                        if (closedPolyline && Geom.almostEqual(currPline.get(0).pos(), currPline.lastVertex().pos(),
                                Utils.realPrecision)) {
                            currPline.removeLast();
                            currPline.isClosed(true);
                        }
                        result.add(currPline);
                    }
                    break;
                }

                // else continue stitching
                visitedIndexes.set(queryResults.get(0), true);
                currPline.removeLast();
                currIndex = queryResults.get(0);
            }
        }

        return result;
    }
}
