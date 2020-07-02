/*
 * @(#)CavalierContours.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.offsetline;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.IntArrayDeque;
import org.jhotdraw8.collection.OrderedPair;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.util.function.QuintFunction;
import org.jhotdraw8.util.function.TriConsumer;

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

import static org.jhotdraw8.geom.offsetline.BulgeConversionFunctions.arcRadiusAndCenter;
import static org.jhotdraw8.geom.offsetline.Intersections.allSelfIntersects;
import static org.jhotdraw8.geom.offsetline.Intersections.findIntersects;
import static org.jhotdraw8.geom.offsetline.Intersections.intrCircle2Circle2;
import static org.jhotdraw8.geom.offsetline.Intersections.intrLineSeg2Circle2;
import static org.jhotdraw8.geom.offsetline.Intersections.intrLineSeg2LineSeg2;
import static org.jhotdraw8.geom.offsetline.Intersections.intrPlineSegs;
import static org.jhotdraw8.geom.offsetline.PlineVertex.closestPointOnSeg;
import static org.jhotdraw8.geom.offsetline.PlineVertex.createFastApproxBoundingBox;
import static org.jhotdraw8.geom.offsetline.PlineVertex.segMidpoint;
import static org.jhotdraw8.geom.offsetline.PlineVertex.splitAtPoint;
import static org.jhotdraw8.geom.offsetline.PolyArcPath.createApproxSpatialIndex;
import static org.jhotdraw8.geom.offsetline.Utils.angle;
import static org.jhotdraw8.geom.offsetline.Utils.deltaAngle;
import static org.jhotdraw8.geom.offsetline.Utils.fuzzyEqual;
import static org.jhotdraw8.geom.offsetline.Utils.pointFromParametric;
import static org.jhotdraw8.geom.offsetline.Utils.pointWithinArcSweepAngle;
import static org.jhotdraw8.geom.offsetline.Utils.realPrecision;
import static org.jhotdraw8.geom.offsetline.Utils.sliceJoinThreshold;
import static org.jhotdraw8.geom.offsetline.Utils.unitPerp;

/**
 * Computes an offset path using the algorithm of the library
 * CavalierContours by Jedidiah Buck McCready <a href="#1">[1]</a>.
 * <p>
 * Algorithm:
 * <ol>
 *     <li>Generate raw offset segments from the input polyline, pline.</li>
 *     <li>Create the raw offset polyline, pline1, by trimming/joining raw
 *     offset segments acquired in step 1.</li>
 *     <li>If the input polyline, pline, has self intersections or is an open
 *     polyline then repeat steps 1 and 2 with the offset negated (e.g. if the
 *     offset was 0.5 then create raw offset polyline with offset of -0.5),
 *     this is known as pline2.</li>
 *     <li>Find all self-intersects of pline1. If step 3 was performed then
 *     also find all intersects between pline1 and pline2. If pline is an open
 *     polyline then also find intersects between pline1 and circles at the
 *     start and end vertex points of pline with radius equal to the offset.</li>
 *     <li>Create a set of open polylines by slicing pline1 at all of the
 *     intersect points found in step 4.</li>
 *     <li>Discard all open polyline slices whose minimum distance to pline is
 *     less than the offset.</li>
 *     <li>Stitch together the remaining open polyline slices found in step 6,
 *     closing the final stitched results if pline is closed.</li>
 * </ol>
 * The algorithm is mostly based on Liu et al. <a href="3">[3]</a> with some
 * differences since the algorithm they describe for GCPP (general closest
 * point pair) clippin g fails for certain inputs with large offsets.
 * <p>
 * The key clarifications/differences are:
 * <ul>
 *     <li>When raw offset segments are extended to form a raw offset polyline
 *     they are always joined by an arc to form a rounded constant distance
 *     from the input polyline.</li>
 *     <li>Dual offset clipping is only applied if input polyline is open or
 *     has self intersects, it is not required for a closed polyline with no
 *     self intersects.</li>
 *     <li>If the polyline is open then a circle is formed at each end point
 *     with radius equal to the offset, the intersects between those circles
 *     and the raw offset polyline are included when forming slices.</li>
 *     <li>GCPP (general closest point pair) clipping is never performed and
 *     instead slices are formed from intersects, then they are discarded if
 *     too close to the original polyline, and finally stitched back together.</li>
 *     <li>No special handling is done for adjacent segments that overlap
 *     (it is not required given the slice and stitch method).</li>
 *     <li>Collapsing arc segments (arcs whose radius is less than the offset
 *     value) are converted into a line and specially marked for joining purposes.</li>
 * </ul>
 * <p>
 * References
 * <ol>
 *     <li><a id="1"></a> Jedidia Buck McCready. (2019). Cavalier Contours.
 *          Copyright © 2019 MIT License.
 *     <a href="https://github.com/jbuckmccready/CavalierContours">github</a></li>
 *     <li><a id="2"></a>Bulge conversions: <a href="http://www.lee-mac.com/bulgeconversion.html">link</a></li>
 *     <li><a id="3"></a>Liu, X.-Z., Yong, J.-H., Zheng, G.-Q., & Sun, J.-G. (2007).
 *     An offset algorithm for polyline curves. Computers in Industry, 58(3),
 *     240–254. doi:10.1016/j.compind.2006.06.002</li>
 * </ol>
 * </p>
 */
public class PapOffsetPathBuilder {


    /// Function to test if a point is a valid distance from the original polyline.
    public static boolean pointValidForOffset(PolyArcPath pline, double offset,
                                              StaticSpatialIndex spatialIndex,
                                              Point2D point, IntArrayDeque queryStack) {
        return pointValidForOffset(pline, offset, spatialIndex, point,
                queryStack, Utils.offsetThreshold);
    }

    public static boolean pointValidForOffset(PolyArcPath pline, double offset,
                                              StaticSpatialIndex spatialIndex,
                                              Point2D point, IntArrayDeque queryStack,
                                              double offsetTol) {
        final double absOffset = Math.abs(offset) - offsetTol;
        final double minDist = absOffset * absOffset;

        boolean[] pointValid = {true};

        IntPredicate visitor = (int i) -> {
            int j = Utils.nextWrappingIndex(i, pline);
            var closestPoint = closestPointOnSeg(pline.get(i), pline.get(j), point);
            double dist = Geom.squaredDistance(closestPoint, point);
            pointValid[0] = dist > minDist;
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

        if (fuzzyEqual(pline.lastVertex().pos(), vertex.pos(), epsilon)) {
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
            final Point2D arcCenter = s1.origV2Pos;
            final Point2D sp = v2.pos();
            final Point2D ep = u1.pos();
            double bulge = bulgeForConnection(arcCenter, sp, ep, connectionArcsAreCCW);
            addOrReplaceIfSamePos(result, new PlineVertex(sp, bulge));
            addOrReplaceIfSamePos(result, u1);
        };

        Consumer<Point2D> processIntersect = (final Point2D intersect) -> {
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

        final IntrCircle2Circle2Result intrResult = intrCircle2Circle2(arc1.radius, arc1.center, arc2.radius, arc2.center);
        switch (intrResult.intrType) {
        case NoIntersect:
            connectUsingArc.run();
            break;
        case OneIntersect:
            processIntersect.accept(intrResult.point1);
            break;
        case TwoIntersects: {
            double dist1 = Geom.squaredDistance(intrResult.point1, s1.origV2Pos);
            double dist2 = Geom.squaredDistance(intrResult.point2, s1.origV2Pos);
            if (dist1 < dist2) {
                processIntersect.accept(intrResult.point1);
            } else {
                processIntersect.accept(intrResult.point2);
            }
        }
        break;
        case Coincident:
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
            final Point2D arcCenter = s1.origV2Pos;
            final Point2D sp = v2.pos();
            final Point2D ep = u1.pos();
            double bulge = bulgeForConnection(arcCenter, sp, ep, connectionArcsAreCCW);
            addOrReplaceIfSamePos(result, new PlineVertex(sp, bulge));
            addOrReplaceIfSamePos(result, u1);
        };

        final BulgeConversionFunctions.ArcRadiusAndCenter arc = arcRadiusAndCenter(v1, v2);

        BiConsumer<Double, Point2D> processIntersect = (Double t, final Point2D intersect) -> {
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

        IntrLineSeg2Circle2Result intrResult = intrLineSeg2Circle2(u1.pos(), u2.pos(), arc.radius, arc.center);
        if (intrResult.numIntersects == 0) {
            connectUsingArc.run();
        } else if (intrResult.numIntersects == 1) {
            processIntersect.accept(intrResult.t0, pointFromParametric(u1.pos(), u2.pos(), intrResult.t0));
        } else {
            assert intrResult.numIntersects == 2 : "should have 2 intersects here";
            final Point2D origPoint = s2.collapsedArc ? u1.pos() : s1.origV2Pos;
            Point2D i1 = pointFromParametric(u1.pos(), u2.pos(), intrResult.t0);
            double dist1 = Geom.squaredDistance(i1, origPoint);
            Point2D i2 = pointFromParametric(u1.pos(), u2.pos(), intrResult.t1);
            double dist2 = Geom.squaredDistance(i2, origPoint);

            if (dist1 < dist2) {
                processIntersect.accept(intrResult.t0, i1);
            } else {
                processIntersect.accept(intrResult.t1, i2);
            }
        }
    }

    /**
     * Gets the bulge to describe the arc going from start point to end point with the given arc center
     * and curve orientation, if orientation is negative then bulge is negative otherwise it is positive
     */
    double bulgeForConnection(final Point2D arcCenter, final Point2D sp,
                              final Point2D ep, boolean isCCW) {
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
            result.pop_back();

            // update first vertex (only if it has not already been updated/replaced)
            if (!firstVertexReplaced) {
                final Point2D updatedFirstPos = closingPartResult.lastVertex().pos();
                if (result.get(0).bulgeIsZero()) {
                    // just update position
                    result.get(0).pos(updatedFirstPos);
                } else if (result.size() > 1) {
                    // update position and bulge
                    final BulgeConversionFunctions.ArcRadiusAndCenter arc = arcRadiusAndCenter(result.get(0), result.get(1));
                    final double a1 = angle(arc.center, updatedFirstPos);
                    final double a2 = angle(arc.center, result.get(1).pos());
                    final double updatedTheta = deltaAngle(a1, a2);
                    if ((updatedTheta < 0.0 && result.get(0).bulgeIsPos()) ||
                            (updatedTheta > 0.0 && result.get(0).bulgeIsNeg())) {
                        // first vertex not valid, just update its position to be removed later
                        result.get(0).pos(updatedFirstPos);
                    } else {
                        // update position and bulge
                        result.get(0).pos(updatedFirstPos);
                        result.get(0).bulge(Math.tan(updatedTheta / 4.0));
                    }
                }
            }

            // must do final singularity prune between first and second vertex after joining curves (n, 0)
            // and (0, 1)
            if (result.size() > 1) {
                if (fuzzyEqual(result.get(0).pos(), result.get(1).pos(), realPrecision)) {
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
    List<PlineOffsetSegment> createUntrimmedOffsetSegments(PolyArcPath pline,
                                                           double offset) {
        int size = pline.size();
        int segmentCount = pline.isClosed() ? size : size - 1;

        List<PlineOffsetSegment> result = new ArrayList<>(segmentCount);

        BiConsumer<PlineVertex, PlineVertex> lineVisitor = (v1, v2) -> {
            Point2D edge = v2.pos().subtract(v1.pos());
            Point2D offsetV = unitPerp(edge).multiply(offset);
            PlineOffsetSegment seg = new PlineOffsetSegment(
                    new PlineVertex(v1.pos().add(offsetV), 0.0),
                    new PlineVertex(v2.pos().add(offsetV), 0.0),
                    v2.pos(), false);
            result.add(seg);
        };

        BiConsumer<PlineVertex, PlineVertex> arcVisitor = (v1, v2) -> {
            BulgeConversionFunctions.ArcRadiusAndCenter arc = arcRadiusAndCenter(v1, v2);
            double offs = v1.bulgeIsNeg() ? offset : -offset;
            double radiusAfterOffset = arc.radius + offs;
            Point2D v1ToCenter = v1.pos().subtract(arc.center).normalize();
            Point2D v2ToCenter = v2.pos().subtract(arc.center).normalize();

            boolean isCollapsedArc = radiusAfterOffset < Utils.realThreshold;
            PlineOffsetSegment seg = new PlineOffsetSegment(
                    new PlineVertex(v1ToCenter.multiply(offs).add(v1.pos()),
                            isCollapsedArc ? 0.0 : v1.bulge()),
                    new PlineVertex(v2ToCenter.multiply(offs).add(v2.pos()), v2.bulge()),
                    v2.pos(), isCollapsedArc);
            result.add(seg);
        };

        BiConsumer<PlineVertex, PlineVertex> offsetVisitor = (v1, v2) -> {
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

    List<OpenPolylineSlice>
    dualSliceAtIntersectsForOffset(final PolyArcPath originalPline,
                                   final PolyArcPath rawOffsetPline,
                                   final PolyArcPath dualRawOffsetPline, double offset) {
        List<OpenPolylineSlice> result = new ArrayList<>();
        if (rawOffsetPline.size() < 2) {
            return result;
        }

        StaticSpatialIndex origPlineSpatialIndex = createApproxSpatialIndex(originalPline);
        StaticSpatialIndex rawOffsetPlineSpatialIndex = createApproxSpatialIndex(rawOffsetPline);

        List<PlineIntersect> selfIntersects = new ArrayList<>();
        allSelfIntersects(rawOffsetPline, selfIntersects, rawOffsetPlineSpatialIndex);

        PlineIntersectsResult dualIntersects = new PlineIntersectsResult();
        findIntersects(rawOffsetPline, dualRawOffsetPline, rawOffsetPlineSpatialIndex, dualIntersects);


        Map<Integer, List<Point2D>> intersectsLookup;
        if (!originalPline.isClosed()) {
            // find intersects between circles generated at original open polyline end points and raw offset
            // polyline
            List<OrderedPair<Integer, List<Point2D>>> intersects = new ArrayList<>();
            offsetCircleIntersectsWithPline(rawOffsetPline, offset, originalPline.get(0).pos(),
                    rawOffsetPlineSpatialIndex, intersects);
            offsetCircleIntersectsWithPline(rawOffsetPline, offset,
                    originalPline.lastVertex().pos(),
                    rawOffsetPlineSpatialIndex, intersects);
            intersectsLookup = new HashMap<>(2 * selfIntersects.size() + intersects.size());
            for (final var pair : intersects) {
                intersectsLookup.put(pair.first(), new ArrayList<>(pair.second()));
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

        IntArrayDeque queryStack = new IntArrayDeque();
        if (intersectsLookup.size() == 0) {
            if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, rawOffsetPline.get(0).pos(),
                    queryStack)) {
                return result;
            }
            // copy and convert raw offset into open polyline
            OpenPolylineSlice back = new OpenPolylineSlice(Integer.MAX_VALUE, rawOffsetPline);
            result.add(back);
            back.pline.isClosed(false);
            if (originalPline.isClosed()) {
                back.pline.addVertex(rawOffsetPline.get(0));
                back.pline.lastVertex().bulge(0.0);
            }
            return result;
        }

        // sort intersects by distance from start vertex
        for (var kvp : intersectsLookup.entrySet()) {
            Point2D startPos = rawOffsetPline.get(kvp.getKey()).pos();
            Comparator<Point2D> cmp = Comparator.comparingDouble((Point2D si) -> Geom.squaredDistance(si, startPos));
            kvp.getValue().sort(cmp);
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

            origPlineSpatialIndex.visitQuery(approxBB.xMin, approxBB.yMin, approxBB.xMax, approxBB.yMax,
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
                var iter = intersectsLookup.get(index);
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
                    final Point2D intersectPos = iter.get(0);
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
                            intersectPos, queryStack)) {
                        break;
                    }

                    SplitResult split =
                            splitAtPoint(rawOffsetPline.get(index), rawOffsetPline.get(index + 1), intersectPos);

                    PlineVertex sliceEndVertex = new PlineVertex(intersectPos, 0.0);
                    var midpoint = segMidpoint(split.updatedStart, sliceEndVertex);
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

        for (final var kvp : intersectsLookup.entrySet()) {
            // start index for the slice we're about to build
            int sIndex = kvp.getKey();
            // self intersect list for this start index
            List<Point2D> siList = kvp.getValue();

            final var startVertex = rawOffsetPline.get(sIndex);
            int nextIndex = Utils.nextWrappingIndex(sIndex, rawOffsetPline);
            final var endVertex = rawOffsetPline.get(nextIndex);

            if (siList.size() != 1) {
                // build all the segments between the N intersects in siList (N > 1), skipping the first
                // segment (to be processed at the end)
                SplitResult firstSplit = splitAtPoint(startVertex, endVertex, siList.get(0));
                var prevVertex = firstSplit.splitVertex;
                for (int i = 1; i < siList.size(); ++i) {
                    SplitResult split = splitAtPoint(prevVertex, endVertex, siList.get(i));
                    // update prevVertex for next loop iteration
                    prevVertex = split.splitVertex;
                    // skip if they're ontop of each other
                    if (fuzzyEqual(split.updatedStart.pos(), split.splitVertex.pos(),
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
                    var midpoint = segMidpoint(split.updatedStart, split.splitVertex);
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, midpoint,
                            queryStack)) {
                        continue;
                    }

                    // test intersection with original polyline
                    if (intersectsOrigPline.test(split.updatedStart, split.splitVertex)) {
                        continue;
                    }
                    OpenPolylineSlice back = new OpenPolylineSlice();
                    result.add(back);
                    back.intrStartIndex = sIndex;
                    back.pline.addVertex(split.updatedStart);
                    back.pline.addVertex(split.splitVertex);
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
                var nextIntr = intersectsLookup.get(index);
                if (nextIntr != null) {
                    // there is an intersect, slice is done, check if final segment is valid

                    // check intersect pos is valid (which will also be end vertex position)
                    final Point2D intersectPos = nextIntr.get(0);
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
                    Point2D mp = segMidpoint(split.updatedStart, sliceEndVertex);
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
            final Point2D arcCenter = s1.origV2Pos;
            final Point2D sp = v2.pos();
            final Point2D ep = u1.pos();
            double bulge = bulgeForConnection(arcCenter, sp, ep, connectionArcsAreCCW);
            addOrReplaceIfSamePos(result, new PlineVertex(sp, bulge));
            addOrReplaceIfSamePos(result, u1);
        };

        final BulgeConversionFunctions.ArcRadiusAndCenter arc = arcRadiusAndCenter(u1, u2);

        BiConsumer<Double, Point2D> processIntersect = (Double t, final Point2D intersect) -> {
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

        IntrLineSeg2Circle2Result intrResult = intrLineSeg2Circle2(v1.pos(), v2.pos(), arc.radius, arc.center);
        if (intrResult.numIntersects == 0) {
            connectUsingArc.run();
        } else if (intrResult.numIntersects == 1) {
            processIntersect.accept(intrResult.t0, pointFromParametric(v1.pos(), v2.pos(), intrResult.t0));
        } else {
            assert intrResult.numIntersects == 2 : "should have 2 intersects here";
            // always use intersect closest to original point
            Point2D i1 = pointFromParametric(v1.pos(), v2.pos(), intrResult.t0);
            double dist1 = Geom.squaredDistance(i1, s1.origV2Pos);
            Point2D i2 = pointFromParametric(v1.pos(), v2.pos(), intrResult.t1);
            double dist2 = Geom.squaredDistance(i2, s1.origV2Pos);

            if (dist1 < dist2) {
                processIntersect.accept(intrResult.t0, i1);
            } else {
                processIntersect.accept(intrResult.t1, i2);
            }
        }
    }
    /// Stitches raw offset polyline slices together, discarding any that are not valid.

    void lineToLineJoin(final PlineOffsetSegment s1, final PlineOffsetSegment s2,
                        boolean connectionArcsAreCCW, PolyArcPath result) {
        final PlineVertex v1 = s1.v1;
        final PlineVertex v2 = s1.v2;
        final PlineVertex u1 = s2.v1;
        final PlineVertex u2 = s2.v2;
        assert v1.bulgeIsZero() && u1.bulgeIsZero() : "both segs should be lines";

        Runnable connectUsingArc = () -> {
            final Point2D arcCenter = s1.origV2Pos;
            final Point2D sp = v2.pos();
            final Point2D ep = u1.pos();
            double bulge = bulgeForConnection(arcCenter, sp, ep, connectionArcsAreCCW);
            addOrReplaceIfSamePos(result, new PlineVertex(sp, bulge));
            addOrReplaceIfSamePos(result, new PlineVertex(ep, 0.0));
        };

        if (s1.collapsedArc || s2.collapsedArc) {
            // connecting to/from collapsed arc, always connect using arc
            connectUsingArc.run();
        } else {
            IntrLineSeg2LineSeg2Result intrResult = intrLineSeg2LineSeg2(v1.pos(), v2.pos(), u1.pos(), u2.pos());

            switch (intrResult.intrType) {
            case None:
                // just join with straight line
                addOrReplaceIfSamePos(result, new PlineVertex(v2.pos(), 0.0));
                addOrReplaceIfSamePos(result, u1);
                break;
            case True:
                addOrReplaceIfSamePos(result, new PlineVertex(intrResult.point, 0.0));
                break;
            case Coincident:
                addOrReplaceIfSamePos(result, new PlineVertex(v2.pos(), 0.0));
                break;
            case False:
                if (intrResult.t0 > 1.0 && falseIntersect(intrResult.t1)) {
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
                                         Point2D circleCenter,
                                         StaticSpatialIndex spatialIndex,
                                         List<OrderedPair<Integer, List<Point2D>>> output) {

        final double circleRadius = Math.abs(offset);

        List<Integer> queryResults = new ArrayList<>();

        spatialIndex.query(circleCenter.getX() - circleRadius, circleCenter.getY() - circleRadius,
                circleCenter.getX() + circleRadius, circleCenter.getY() + circleRadius,
                queryResults);

        Predicate<Double> validLineSegIntersect = (Double t) -> {
            return !falseIntersect(t) && Math.abs(t) > Utils.realPrecision;
        };

        QuintFunction<Point2D, Point2D, Point2D, Double, Point2D, Boolean>
                validArcSegIntersect = (Point2D arcCenter, Point2D arcStart,
                                        Point2D arcEnd, Double bulge,
                                        Point2D intrPoint) -> {
            return !fuzzyEqual(arcStart, intrPoint, Utils.realPrecision) &&
                    pointWithinArcSweepAngle(arcCenter, arcStart, arcEnd, bulge, intrPoint);
        };

        for (int sIndex : queryResults) {
            PlineVertex v1 = pline.get(sIndex);
            PlineVertex v2 = pline.get(sIndex + 1);
            if (v1.bulgeIsZero()) {
                IntrLineSeg2Circle2Result intrResult =
                        intrLineSeg2Circle2(v1.pos(), v2.pos(), circleRadius, circleCenter);
                if (intrResult.numIntersects == 0) {
                    continue;
                } else if (intrResult.numIntersects == 1) {
                    if (validLineSegIntersect.test(intrResult.t0)) {
                        output.add(new OrderedPair<>(sIndex, Arrays.asList(pointFromParametric(v1.pos(), v2.pos(), intrResult.t0))));
                    }
                } else {
                    assert intrResult.numIntersects == 2 : "should be two intersects here";
                    if (validLineSegIntersect.test(intrResult.t0)) {
                        output.add(new OrderedPair<>(sIndex, Arrays.asList(pointFromParametric(v1.pos(), v2.pos(), intrResult.t0))));
                    }
                    if (validLineSegIntersect.test(intrResult.t1)) {
                        output.add(new OrderedPair<>(sIndex, Arrays.asList(pointFromParametric(v1.pos(), v2.pos(), intrResult.t1))));
                    }
                }
            } else {
                var arc = arcRadiusAndCenter(v1, v2);
                IntrCircle2Circle2Result intrResult =
                        intrCircle2Circle2(arc.radius, arc.center, circleRadius, circleCenter);
                switch (intrResult.intrType) {
                case NoIntersect:
                    break;
                case OneIntersect:
                    if (validArcSegIntersect.apply(arc.center, v1.pos(), v2.pos(), v1.bulge(), intrResult.point1)) {
                        output.add(new OrderedPair<>(sIndex, Arrays.asList(intrResult.point1)));
                    }
                    break;
                case TwoIntersects:
                    if (validArcSegIntersect.apply(arc.center, v1.pos(), v2.pos(), v1.bulge(), intrResult.point1)) {
                        output.add(new OrderedPair<>(sIndex, Arrays.asList(intrResult.point1)));
                    }
                    if (validArcSegIntersect.apply(arc.center, v1.pos(), v2.pos(), v1.bulge(), intrResult.point2)) {
                        output.add(new OrderedPair<>(sIndex, Arrays.asList(intrResult.point2)));
                    }
                    break;
                case Coincident:
                    break;
                }
            }
        }
    }

    /// Slices a raw offset polyline at all of its self intersects and intersects with its dual.

    /**
     * Creates the paralell offset polylines to the polyline given.
     *
     * @param pline  input polyline
     * @param offset offset
     * @return offset polyline
     */
    @NonNull
    public List<PolyArcPath> parallelOffset(@NonNull PolyArcPath pline, double offset,
                                            boolean hasSelfIntersects) {

        if (pline.size() < 2) {
            return new ArrayList<>();
        }
        var rawOffset = createRawOffsetPline(pline, offset);
        if (pline.isClosed() && !hasSelfIntersects) {
            var slices = slicesFromRawOffset(pline, rawOffset, offset);
            return stitchOffsetSlicesTogether(slices, pline.isClosed(), rawOffset.size() - 1);
        }

        // not closed polyline or has self intersects, must apply dual clipping
        var dualRawOffset = createRawOffsetPline(pline, offset);
        var slices = dualSliceAtIntersectsForOffset(pline, rawOffset, dualRawOffset, offset);
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

        Map<Integer, List<Point2D>> intersectsLookup = new HashMap<>(2 * selfIntersects.size());

        for (final PlineIntersect si : selfIntersects) {
            intersectsLookup.computeIfAbsent(si.sIndex1, k -> new ArrayList<>()).add(si.pos);
            intersectsLookup.computeIfAbsent(si.sIndex2, k -> new ArrayList<>()).add(si.pos);
        }

        // sort intersects by distance from start vertex
        for (var kvp : intersectsLookup.entrySet()) {
            Point2D startPos = rawOffsetPline.get(kvp.getKey()).pos();
            Comparator<Point2D> cmp = Comparator.comparingDouble((Point2D si) -> Geom.squaredDistance(si, startPos));
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

            origPlineSpatialIndex.visitQuery(approxBB.xMin, approxBB.yMin, approxBB.xMax, approxBB.yMax,
                    visitor, queryStack);

            return hasIntersect[0];
        };

        for (final var kvp : intersectsLookup.entrySet()) {
            // start index for the slice we're about to build
            int sIndex = kvp.getKey();
            // self intersect list for this start index
            List<Point2D> siList = kvp.getValue();

            final var startVertex = rawOffsetPline.get(sIndex);
            int nextIndex = Utils.nextWrappingIndex(sIndex, rawOffsetPline);
            final var endVertex = rawOffsetPline.get(nextIndex);

            if (siList.size() != 1) {
                // build all the segments between the N intersects in siList (N > 1), skipping the first
                // segment (to be processed at the end)
                SplitResult firstSplit = splitAtPoint(startVertex, endVertex, siList.get(0));
                var prevVertex = firstSplit.splitVertex;
                for (int i = 1; i < siList.size(); ++i) {
                    SplitResult split = splitAtPoint(prevVertex, endVertex, siList.get(i));
                    // update prevVertex for next loop iteration
                    prevVertex = split.splitVertex;
                    // skip if they're ontop of each other
                    if (fuzzyEqual(split.updatedStart.pos(), split.splitVertex.pos(),
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
                    var midpoint = segMidpoint(split.updatedStart, split.splitVertex);
                    if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, midpoint,
                            queryStack)) {
                        continue;
                    }

                    // test intersection with original polyline
                    if (intersectsOrigPline.test(split.updatedStart, split.splitVertex)) {
                        continue;
                    }

                    OpenPolylineSlice back = new OpenPolylineSlice();
                    result.add(back);
                    back.intrStartIndex = sIndex;
                    back.pline.addVertex(split.updatedStart);
                    back.pline.addVertex(split.splitVertex);
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
                var nextIntr = intersectsLookup.get(index);
                if (nextIntr != null) {
                    // there is an intersect, slice is done, check if final segment is valid

                    // check intersect pos is valid (which will also be end vertex position)
                    final Point2D intersectPos = nextIntr.get(0);
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
                    Point2D mp = segMidpoint(split.updatedStart, sliceEndVertex);
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

            if (isValidPline && fuzzyEqual(currSlice.get(0).pos(), currSlice.lastVertex().pos())) {
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

    List<PolyArcPath>
    stitchOffsetSlicesTogether(final List<OpenPolylineSlice> slices, boolean closedPolyline,
                               int origMaxIndex) {
        return stitchOffsetSlicesTogether(slices, closedPolyline, origMaxIndex,
                sliceJoinThreshold);
    }

    List<PolyArcPath>
    stitchOffsetSlicesTogether(final List<OpenPolylineSlice> slices, boolean closedPolyline,
                               int origMaxIndex,
                               double joinThreshold) {
        List<PolyArcPath> result = new ArrayList<>();
        if (slices.size() == 0) {
            return result;
        }

        if (slices.size() == 1) {
            result.add(slices.get(0).pline);
            if (closedPolyline &&
                    fuzzyEqual(result.get(0).get(0).pos(), result.get(0).lastVertex().pos(), joinThreshold)) {
                result.get(0).isClosed(true);
                result.get(0).pop_back();
            }

            return result;
        }

        // load spatial index with all start points
        StaticSpatialIndex spatialIndex = new StaticSpatialIndex(slices.size());

        for (final var slice : slices) {
            final var point = slice.pline.get(0).pos();
            spatialIndex.add(point.getX() - joinThreshold, point.getY() - joinThreshold,
                    point.getX() + joinThreshold, point.getY() + joinThreshold);
        }

        spatialIndex.finish();

        BitSet visitedIndexes = new BitSet(slices.size());
        List<Integer> queryResults = new ArrayList<>();
        IntArrayDeque queryStack = new IntArrayDeque(8);
        for (int i = 0; i < slices.size(); ++i) {
            if (visitedIndexes.get(i)) {
                continue;
            }

            visitedIndexes.set(i, true);

            PolyArcPath currPline = new PolyArcPath();
            int currIndex = i;
            final var initialStartPoint = slices.get(i).pline.get(0).pos();
            int loopCount = 0;
            final int maxLoopCount = slices.size();
            while (true) {
                if (loopCount++ > maxLoopCount) {
                    assert false : "Bug detected, should never loop this many times!";
                    // break to avoid infinite loop
                    break;
                }
                final int currLoopStartIndex = slices.get(currIndex).intrStartIndex;
                final var currSlice = slices.get(currIndex).pline;
                final var currEndPoint = slices.get(currIndex).pline.lastVertex().pos();
                currPline.addAll(currSlice);
                queryResults.clear();
                spatialIndex.query(currEndPoint.getX() - joinThreshold, currEndPoint.getY() - joinThreshold,
                        currEndPoint.getX() + joinThreshold, currEndPoint.getY() + joinThreshold,
                        queryResults, queryStack);

                queryResults.removeIf(visitedIndexes::get);

                Function<Integer, OrderedPair<Integer, Boolean>> indexDistAndEqualInitial = (Integer index) -> {
                    final var slice = slices.get(index);
                    int indexDist;
                    if (currLoopStartIndex <= slice.intrStartIndex) {
                        indexDist = slice.intrStartIndex - currLoopStartIndex;
                    } else {
                        // forward wrapping distance (distance to end + distance to index)
                        indexDist = origMaxIndex - currLoopStartIndex + slice.intrStartIndex;
                    }

                    boolean equalToInitial = fuzzyEqual(slice.pline.lastVertex().pos(), initialStartPoint,
                            Utils.realPrecision);

                    return new OrderedPair<>(indexDist, equalToInitial);
                };

                queryResults.sort(
                        (Integer index1, Integer index2) -> {
                            var distAndEqualInitial1 = indexDistAndEqualInitial.apply(index1);
                            var distAndEqualInitial2 = indexDistAndEqualInitial.apply(index2);
                            if (distAndEqualInitial1.first() == distAndEqualInitial2.first()) {
                                // index distances are equal, compare on position being equal to initial start
                                // (testing index1 < index2, we want the longest closed loop possible)
                                return (distAndEqualInitial1.second() ? 1 : 0) - (distAndEqualInitial2.second() ? 1 : 0);
                            }

                            return distAndEqualInitial2.first() - distAndEqualInitial1.first();
                        });

                if (queryResults.size() == 0) {
                    // we're done
                    if (currPline.size() > 1) {
                        if (closedPolyline && fuzzyEqual(currPline.get(0).pos(), currPline.lastVertex().pos(),
                                Utils.realPrecision)) {
                            currPline.pop_back();
                            currPline.isClosed(true);
                        }
                        result.add(currPline);
                    }
                    break;
                }

                // else continue stitching
                visitedIndexes.set(queryResults.get(0), true);
                currPline.pop_back();
                currIndex = queryResults.get(0);
            }
        }

        return result;
    }
}
