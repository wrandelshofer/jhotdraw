/*
 * @(#)CavalierContours.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.offsetline;

import javafx.geometry.Point2D;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.util.function.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.jhotdraw8.geom.offsetline.BulgeConversionFunctions.arcRadiusAndCenter;
import static org.jhotdraw8.geom.offsetline.Intersections.intrCircle2Circle2;
import static org.jhotdraw8.geom.offsetline.Intersections.intrLineSeg2Circle2;
import static org.jhotdraw8.geom.offsetline.Intersections.intrLineSeg2LineSeg2;
import static org.jhotdraw8.geom.offsetline.Utils.angle;
import static org.jhotdraw8.geom.offsetline.Utils.deltaAngle;
import static org.jhotdraw8.geom.offsetline.Utils.fuzzyEqual;
import static org.jhotdraw8.geom.offsetline.Utils.pointFromParametric;
import static org.jhotdraw8.geom.offsetline.Utils.pointWithinArcSweepAngle;
import static org.jhotdraw8.geom.offsetline.Utils.realPrecision;
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
public class OffsetPathBuilder {


    void addOrReplaceIfSamePos(Polyline pline, final PlineVertex vertex) {
        addOrReplaceIfSamePos(pline, vertex, realPrecision);
    }

    void addOrReplaceIfSamePos(Polyline pline, final PlineVertex vertex,
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
                      boolean connectionArcsAreCCW, Polyline result) {

        final var v1 = s1.v1;
        final var v2 = s1.v2;
        final var u1 = s2.v1;
        final var u2 = s2.v2;
        assert !v1.bulgeIsZero() && !u1.bulgeIsZero() : "both segs should be arcs";

        final var arc1 = arcRadiusAndCenter(v1, v2);
        final var arc2 = arcRadiusAndCenter(u1, u2);

        Runnable connectUsingArc = () -> {
            final var arcCenter = s1.origV2Pos;
            final var sp = v2.pos();
            final var ep = u1.pos();
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
                    var prevArc = arcRadiusAndCenter(prevVertex, v2);
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

        final var intrResult = intrCircle2Circle2(arc1.radius, arc1.center, arc2.radius, arc2.center);
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
                       boolean connectionArcsAreCCW, Polyline result) {

        final var v1 = s1.v1;
        final var v2 = s1.v2;
        final var u1 = s2.v1;
        final var u2 = s2.v2;
        assert !v1.bulgeIsZero() && u1.bulgeIsZero() :
                "first seg should be line, second seg should be arc";

        Runnable connectUsingArc = () -> {
            final var arcCenter = s1.origV2Pos;
            final var sp = v2.pos();
            final var ep = u1.pos();
            double bulge = bulgeForConnection(arcCenter, sp, ep, connectionArcsAreCCW);
            addOrReplaceIfSamePos(result, new PlineVertex(sp, bulge));
            addOrReplaceIfSamePos(result, u1);
        };

        final var arc = arcRadiusAndCenter(v1, v2);

        BiConsumer<Double, Point2D> processIntersect = (Double t, final Point2D intersect) -> {
            final boolean trueSegIntersect = !falseIntersect(t);
            final boolean trueArcIntersect =
                    pointWithinArcSweepAngle(arc.center, v1.pos(), v2.pos(), v1.bulge(), intersect);
            if (trueSegIntersect && trueArcIntersect) {
                // modify previous bulge and trim at intersect
                PlineVertex prevVertex = result.lastVertex();

                if (!prevVertex.bulgeIsZero()) {
                    double a = angle(arc.center, intersect);
                    var prevArc = arcRadiusAndCenter(prevVertex, v2);
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

        var intrResult = intrLineSeg2Circle2(u1.pos(), u2.pos(), arc.radius, arc.center);
        if (intrResult.numIntersects == 0) {
            connectUsingArc.run();
        } else if (intrResult.numIntersects == 1) {
            processIntersect.accept(intrResult.t0, pointFromParametric(u1.pos(), u2.pos(), intrResult.t0));
        } else {
            assert intrResult.numIntersects == 2 : "should have 2 intersects here";
            final var origPoint = s2.collapsedArc ? u1.pos() : s1.origV2Pos;
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
    Polyline createRawOffsetPline(Polyline pline, double offset) {

        Polyline result = new Polyline();
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

        result = new Polyline(pline.size());
        result.isClosed(pline.isClosed());

        final boolean connectionArcsAreCCW = offset < 0;

        TriConsumer<PlineOffsetSegment, PlineOffsetSegment, Polyline> joinResultVisitor = (s1, s2, presult) -> {
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

            final var seg01 = rawOffsets.get(0);
            final var seg12 = rawOffsets.get(1);
            joinResultVisitor.accept(seg01, seg12, result);
        }
        final boolean firstVertexReplaced = result.size() == 1;

        for (int i = 2; i < rawOffsets.size(); ++i) {
            final var seg1 = rawOffsets.get(i - 1);
            final var seg2 = rawOffsets.get(i);
            joinResultVisitor.accept(seg1, seg2, result);
        }

        if (pline.isClosed() && result.size() > 1) {
            // joining segments at vertex indexes (n, 0) and (0, 1)
            final var s1 = rawOffsets.get(rawOffsets.size() - 1);
            final var s2 = rawOffsets.get(0);

            // temp polyline to capture results of joining (to avoid mutating result)
            Polyline closingPartResult = new Polyline();
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
                    final var arc = arcRadiusAndCenter(result.get(0), result.get(1));
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
    List<PlineOffsetSegment> createUntrimmedOffsetSegments(Polyline pline,
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
            var arc = arcRadiusAndCenter(v1, v2);
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

    boolean falseIntersect(double t) {
        return t < 0.0 || t > 1.0;
    }

    void lineToArcJoin(final PlineOffsetSegment s1, final PlineOffsetSegment s2,
                       boolean connectionArcsAreCCW, Polyline result) {

        final var v1 = s1.v1;
        final var v2 = s1.v2;
        final var u1 = s2.v1;
        final var u2 = s2.v2;
        assert v1.bulgeIsZero() && !u1.bulgeIsZero() :
                "first seg should be arc, second seg should be line";

        Runnable connectUsingArc = () -> {
            final var arcCenter = s1.origV2Pos;
            final var sp = v2.pos();
            final var ep = u1.pos();
            double bulge = bulgeForConnection(arcCenter, sp, ep, connectionArcsAreCCW);
            addOrReplaceIfSamePos(result, new PlineVertex(sp, bulge));
            addOrReplaceIfSamePos(result, u1);
        };

        final var arc = arcRadiusAndCenter(u1, u2);

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

        var intrResult = intrLineSeg2Circle2(v1.pos(), v2.pos(), arc.radius, arc.center);
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

    void lineToLineJoin(final PlineOffsetSegment s1, final PlineOffsetSegment s2,
                        boolean connectionArcsAreCCW, Polyline result) {
        final var v1 = s1.v1;
        final var v2 = s1.v2;
        final var u1 = s2.v1;
        final var u2 = s2.v2;
        assert v1.bulgeIsZero() && u1.bulgeIsZero() : "both segs should be lines";

        Runnable connectUsingArc = () -> {
            final var arcCenter = s1.origV2Pos;
            final var sp = v2.pos();
            final var ep = u1.pos();
            double bulge = bulgeForConnection(arcCenter, sp, ep, connectionArcsAreCCW);
            addOrReplaceIfSamePos(result, new PlineVertex(sp, bulge));
            addOrReplaceIfSamePos(result, new PlineVertex(ep, 0.0));
        };

        if (s1.collapsedArc || s2.collapsedArc) {
            // connecting to/from collapsed arc, always connect using arc
            connectUsingArc.run();
        } else {
            var intrResult = intrLineSeg2LineSeg2(v1.pos(), v2.pos(), u1.pos(), u2.pos());

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

    /**
     * Creates the paralell offset polylines to the polyline given.
     *
     * @param pline  input polyline
     * @param offset offset
     * @return offset polyline
     * /
     @NonNull public List<Polyline> parallelOffset(@NonNull Polyline pline, double offset,
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
     var dualRawOffset = createRawOffsetPline(pline, -offset);
     var slices = dualSliceAtIntersectsForOffset(pline, rawOffset, dualRawOffset, offset);
     return stitchOffsetSlicesTogether(slices, pline.isClosed(), rawOffset.size() - 1);
     }
     /// Slices a raw offset polyline at all of its self intersects.

     List<OpenPolylineSlice> slicesFromRawOffset(final Polyline originalPline,
     final Polyline rawOffsetPline,
     double offset) {
     assert originalPline.isClosed() : "use dual slice at intersects for open polylines";

     List<OpenPolylineSlice> result;
     if (rawOffsetPline.size() < 2) {
     return result;
     }

     StaticSpatialIndex origPlineSpatialIndex = createApproxSpatialIndex(originalPline);
     StaticSpatialIndex rawOffsetPlineSpatialIndex = createApproxSpatialIndex(rawOffsetPline);

     List<PlineIntersect> selfIntersects;
     allSelfIntersects(rawOffsetPline, selfIntersects, rawOffsetPlineSpatialIndex);

     Deque<Integer> queryStack=new ArrayDeque<>(8);
     if (selfIntersects.size() == 0) {
     if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, rawOffsetPline[0].pos(),
     queryStack)) {
     return result;
     }
     // copy and convert raw offset into open polyline
     result.emplace_back(std::numeric_limits<int>::max(), rawOffsetPline);
     result.back().pline.isClosed() = false;
     result.back().pline.addVertex(rawOffsetPline[0]);
     result.back().pline.lastVertex().bulge() = 0.0;
     return result;
     }

     std::unordered_map<int, List<Vector2>> intersectsLookup;
     intersectsLookup.reserve(2 * selfIntersects.size());

     for (final PlineIntersect si : selfIntersects) {
     intersectsLookup[si.sIndex1].push_back(si.pos);
     intersectsLookup[si.sIndex2].push_back(si.pos);
     }

     // sort intersects by distance from start vertex
     for (var kvp : intersectsLookup) {
     Vector2 startPos = rawOffsetPline[kvp.first].pos();
     var cmp = [&](final Vector2 si1, final Vector2 si2) {
     return distSquared(si1, startPos) < distSquared(si2, startPos);
     };
     std::sort(kvp.second.begin(), kvp.second.end(), cmp);
     }

     var intersectsOrigPline = [&](final PlineVertex v1, final PlineVertex v2) {
     AABB approxBB = createFastApproxBoundingBox(v1, v2);
     boolean hasIntersect = false;
     var visitor = [&](int i) {
     using namespace internal;
     int j = utils::nextWrappingIndex(i, originalPline);
     IntrPlineSegsResult intrResult =
     intrPlineSegs(v1, v2, originalPline[i], originalPline[j]);
     hasIntersect = intrResult.intrType != PlineSegIntrType::NoIntersect;
     return !hasIntersect;
     };

     origPlineSpatialIndex.visitQuery(approxBB.xMin, approxBB.yMin, approxBB.xMax, approxBB.yMax,
     visitor, queryStack);

     return hasIntersect;
     };

     for (final var kvp : intersectsLookup) {
     // start index for the slice we're about to build
     int sIndex = kvp.first;
     // self intersect list for this start index
     List<Vector2> final &siList = kvp.second;

     final var startVertex = rawOffsetPline[sIndex];
     int nextIndex = utils::nextWrappingIndex(sIndex, rawOffsetPline);
     final var endVertex = rawOffsetPline[nextIndex];

     if (siList.size() != 1) {
     // build all the segments between the N intersects in siList (N > 1), skipping the first
     // segment (to be processed at the end)
     SplitResult firstSplit = splitAtPoint(startVertex, endVertex, siList[0]);
     var prevVertex = firstSplit.splitVertex;
     for (int i = 1; i < siList.size(); ++i) {
     SplitResult split = splitAtPoint(prevVertex, endVertex, siList[i]);
     // update prevVertex for next loop iteration
     prevVertex = split.splitVertex;
     // skip if they're ontop of each other
     if (fuzzyEqual(split.updatedStart.pos(), split.splitVertex.pos(),
     utils::realPrecision())) {
     continue;
     }

     // test start point
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
     split.updatedStart.pos(), queryStack)) {
     continue;
     }

     // test end point
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
     split.splitVertex.pos(), queryStack)) {
     continue;
     }

     // test mid point
     var midpoint = segMidpoint(split.updatedStart, split.splitVertex);
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex, midpoint,
     queryStack)) {
     continue;
     }

     // test intersection with original polyline
     if (intersectsOrigPline(split.updatedStart, split.splitVertex)) {
     continue;
     }

     result.emplace_back();
     result.back().intrStartIndex = sIndex;
     result.back().pline.addVertex(split.updatedStart);
     result.back().pline.addVertex(split.splitVertex);
     }
     }

     // build the segment between the last intersect in siList and the next intersect found

     // check that the first point is valid
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex, siList.back(),
     queryStack)) {
     continue;
     }

     SplitResult split = splitAtPoint(startVertex, endVertex, siList.back());
     Polyline currSlice;
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
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
     rawOffsetPline[index].pos(), queryStack)) {
     isValidPline = false;
     break;
     }

     // check that the segment does not intersect original polyline
     if (intersectsOrigPline(currSlice.lastVertex(), rawOffsetPline[index])) {
     isValidPline = false;
     break;
     }

     // add vertex
     internal::addOrReplaceIfSamePos(currSlice, rawOffsetPline[index]);

     // check if segment that starts at vertex we just added has an intersect
     var nextIntr = intersectsLookup.find(index);
     if (nextIntr != intersectsLookup.end()) {
     // there is an intersect, slice is done, check if final segment is valid

     // check intersect pos is valid (which will also be end vertex position)
     final Vector2 intersectPos = nextIntr->second[0];
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
     intersectPos, queryStack)) {
     isValidPline = false;
     break;
     }

     int nextIndex = utils::nextWrappingIndex(index, rawOffsetPline);
     SplitResult split =
     splitAtPoint(currSlice.lastVertex(), rawOffsetPline[nextIndex], intersectPos);

     PlineVertex sliceEndVertex = PlineVertex(intersectPos, 0.0);
     // check mid point is valid
     Vector2 mp = segMidpoint(split.updatedStart, sliceEndVertex);
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex, mp,
     queryStack)) {
     isValidPline = false;
     break;
     }

     // trim last added vertex and add final intersect position
     currSlice.lastVertex() = split.updatedStart;
     internal::addOrReplaceIfSamePos(currSlice, sliceEndVertex);

     break;
     }
     // else there is not an intersect, increment index and continue
     index = utils::nextWrappingIndex(index, rawOffsetPline);
     }

     isValidPline = isValidPline && currSlice.size() > 1;

     if (isValidPline && fuzzyEqual(currSlice[0].pos(), currSlice.lastVertex().pos())) {
     // discard very short slice loops (invalid loops may arise due to valid offset distance
     // thresholding)
     isValidPline = getPathLength(currSlice) > double(1e-2);
     }

     if (isValidPline) {
     result.emplace_back(sIndex, std::move(currSlice));
     }
     }

     return result;
     }
     /// Stitches raw offset polyline slices together, discarding any that are not valid.

     List<Polyline>
     stitchOffsetSlicesTogether(final List<OpenPolylineSlice> slices, boolean closedPolyline,
     int origMaxIndex) {
     return stitchOffsetSlicesTogether(slices,closedPolyline,origMaxIndex,sliceJoinThreshold);
     }
     List<Polyline>
     stitchOffsetSlicesTogether(final List<OpenPolylineSlice> slices, boolean closedPolyline,
     int origMaxIndex,
     double joinThreshold) {
     List<Polyline> result=new ArrayList<>();
     if (slices.size() == 0) {
     return result;
     }

     if (slices.size() == 1) {
     result.emplace_back(slices[0].pline);
     if (closedPolyline &&
     fuzzyEqual(result[0][0].pos(), result[0].lastVertex().pos(), joinThreshold)) {
     result[0].isClosed() = true;
     result[0].vertexes().pop_back();
     }

     return result;
     }

     // load spatial index with all start points
     StaticSpatialIndex spatialIndex(slices.size());

     for (final var slice : slices) {
     final var point = slice.pline[0].pos();
     spatialIndex.add(point.x() - joinThreshold, point.y() - joinThreshold,
     point.x() + joinThreshold, point.y() + joinThreshold);
     }

     spatialIndex.finish();

     List<Boolean> visitedIndexes(slices.size(), false);
     List<int> queryResults;
     List<int> queryStack;
     queryStack.reserve(8);
     for (int i = 0; i < slices.size(); ++i) {
     if (visitedIndexes[i]) {
     continue;
     }

     visitedIndexes[i] = true;

     Polyline currPline;
     int currIndex = i;
     final var initialStartPoint = slices[i].pline[0].pos();
     int loopCount = 0;
     final int maxLoopCount = slices.size();
     while (true) {
     if (loopCount++ > maxLoopCount) {
     assert false : "Bug detected, should never loop this many times!";
     // break to avoid infinite loop
     break;
     }
     final int currLoopStartIndex = slices[currIndex].intrStartIndex;
     final var currSlice = slices[currIndex].pline;
     final var currEndPoint = slices[currIndex].pline.lastVertex().pos();
     currPline.vertexes().insert(currPline.vertexes().end(), currSlice.vertexes().begin(),
     currSlice.vertexes().end());
     queryResults.clear();
     spatialIndex.query(currEndPoint.x() - joinThreshold, currEndPoint.y() - joinThreshold,
     currEndPoint.x() + joinThreshold, currEndPoint.y() + joinThreshold,
     queryResults, queryStack);

     queryResults.erase(std::remove_if(queryResults.begin(), queryResults.end(),
     [&](int index) { return visitedIndexes[index]; }),
     queryResults.end());

     var indexDistAndEqualInitial = [&](int index) {
     final var slice = slices[index];
     int indexDist;
     if (currLoopStartIndex <= slice.intrStartIndex) {
     indexDist = slice.intrStartIndex - currLoopStartIndex;
     } else {
     // forward wrapping distance (distance to end + distance to index)
     indexDist = origMaxIndex - currLoopStartIndex + slice.intrStartIndex;
     }

     boolean equalToInitial = fuzzyEqual(slice.pline.lastVertex().pos(), initialStartPoint,
     utils::realPrecision());

     return std::make_pair(indexDist, equalToInitial);
     };

     std::sort(queryResults.begin(), queryResults.end(),
     [&](int index1, int index2) {
     var distAndEqualInitial1 = indexDistAndEqualInitial(index1);
     var distAndEqualInitial2 = indexDistAndEqualInitial(index2);
     if (distAndEqualInitial1.first == distAndEqualInitial2.first) {
     // index distances are equal, compare on position being equal to initial start
     // (testing index1 < index2, we want the longest closed loop possible)
     return distAndEqualInitial1.second < distAndEqualInitial2.second;
     }

     return distAndEqualInitial1.first < distAndEqualInitial2.first;
     });

     if (queryResults.size() == 0) {
     // we're done
     if (currPline.size() > 1) {
     if (closedPolyline && fuzzyEqual(currPline[0].pos(), currPline.lastVertex().pos(),
     utils::realPrecision())) {
     currPline.vertexes().pop_back();
     currPline.isClosed() = true;
     }
     result.emplace_back(std::move(currPline));
     }
     break;
     }

     // else continue stitching
     visitedIndexes[queryResults[0]] = true;
     currPline.vertexes().pop_back();
     currIndex = queryResults[0];
     }
     }

     return result;
     }

     /// Slices a raw offset polyline at all of its self intersects and intersects with its dual.

     List<OpenPolylineSlice>
     dualSliceAtIntersectsForOffset(final Polyline originalPline,
     final Polyline rawOffsetPline,
     final Polyline dualRawOffsetPline, double offset) {
     List<OpenPolylineSlice> result;
     if (rawOffsetPline.size() < 2) {
     return result;
     }

     StaticSpatialIndex origPlineSpatialIndex = createApproxSpatialIndex(originalPline);
     StaticSpatialIndex rawOffsetPlineSpatialIndex = createApproxSpatialIndex(rawOffsetPline);

     List<PlineIntersect> selfIntersects;
     allSelfIntersects(rawOffsetPline, selfIntersects, rawOffsetPlineSpatialIndex);

     PlineIntersectsResult dualIntersects;
     findIntersects(rawOffsetPline, dualRawOffsetPline, rawOffsetPlineSpatialIndex, dualIntersects);

     std::unordered_map<int, List<Vector2>> intersectsLookup;

     if (!originalPline.isClosed()) {
     // find intersects between circles generated at original open polyline end points and raw offset
     // polyline
     List<std::pair<int, Vector2>> intersects;
     internal::offsetCircleIntersectsWithPline(rawOffsetPline, offset, originalPline[0].pos(),
     rawOffsetPlineSpatialIndex, intersects);
     internal::offsetCircleIntersectsWithPline(rawOffsetPline, offset,
     originalPline.lastVertex().pos(),
     rawOffsetPlineSpatialIndex, intersects);
     intersectsLookup.reserve(2 * selfIntersects.size() + intersects.size());
     for (final var pair : intersects) {
     intersectsLookup[pair.first].push_back(pair.second);
     }
     } else {
     intersectsLookup.reserve(2 * selfIntersects.size());
     }

     for (final PlineIntersect si : selfIntersects) {
     intersectsLookup[si.sIndex1].push_back(si.pos);
     intersectsLookup[si.sIndex2].push_back(si.pos);
     }

     for (final PlineIntersect intr : dualIntersects.intersects) {
     intersectsLookup[intr.sIndex1].push_back(intr.pos);
     }

     for (final PlineCoincidentIntersect intr : dualIntersects.coincidentIntersects) {
     intersectsLookup[intr.sIndex1].push_back(intr.point1);
     intersectsLookup[intr.sIndex1].push_back(intr.point2);
     }

     List<int> queryStack;
     queryStack.reserve(8);
     if (intersectsLookup.size() == 0) {
     if (!pointValidForOffset(originalPline, offset, origPlineSpatialIndex, rawOffsetPline[0].pos(),
     queryStack)) {
     return result;
     }
     // copy and convert raw offset into open polyline
     result.emplace_back(std::numeric_limits<int>::max(), rawOffsetPline);
     result.back().pline.isClosed() = false;
     if (originalPline.isClosed()) {
     result.back().pline.addVertex(rawOffsetPline[0]);
     result.back().pline.lastVertex().bulge() = 0.0;
     }
     return result;
     }

     // sort intersects by distance from start vertex
     for (var kvp : intersectsLookup) {
     Vector2 startPos = rawOffsetPline[kvp.first].pos();
     var cmp = [&](final Vector2 si1, final Vector2 si2) {
     return distSquared(si1, startPos) < distSquared(si2, startPos);
     };
     std::sort(kvp.second.begin(), kvp.second.end(), cmp);
     }

     var intersectsOrigPline = [&](final PlineVertex v1, final PlineVertex v2) {
     AABB approxBB = createFastApproxBoundingBox(v1, v2);
     boolean intersects = false;
     var visitor = [&](int i) {
     using namespace internal;
     int j = utils::nextWrappingIndex(i, originalPline);
     IntrPlineSegsResult intrResult =
     intrPlineSegs(v1, v2, originalPline[i], originalPline[j]);
     intersects = intrResult.intrType != PlineSegIntrType::NoIntersect;
     return !intersects;
     };

     origPlineSpatialIndex.visitQuery(approxBB.xMin, approxBB.yMin, approxBB.xMax, approxBB.yMax,
     visitor, queryStack);

     return intersects;
     };

     if (!originalPline.isClosed()) {
     // build first open polyline that ends at the first intersect since we will not wrap back to
     // capture it as in the case of a closed polyline
     Polyline firstSlice;
     int index = 0;
     int loopCount = 0;
     final int maxLoopCount = rawOffsetPline.size();
     while (true) {
     if (loopCount++ > maxLoopCount) {
     assert false : "Bug detected, should never loop this many times!";
     // break to avoid infinite loop
     break;
     }
     var iter = intersectsLookup.find(index);
     if (iter == intersectsLookup.end()) {
     // no intersect found, test segment will be valid before adding the vertex
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
     rawOffsetPline[index].pos(), queryStack)) {
     break;
     }

     // index check (only test segment if we're not adding the first vertex)
     if (index != 0 && intersectsOrigPline(firstSlice.lastVertex(), rawOffsetPline[index])) {
     break;
     }

     internal::addOrReplaceIfSamePos(firstSlice, rawOffsetPline[index]);
     } else {
     // intersect found, test segment will be valid before finishing first open polyline
     final Vector2 intersectPos = iter->second[0];
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
     intersectPos, queryStack)) {
     break;
     }

     SplitResult split =
     splitAtPoint(rawOffsetPline[index], rawOffsetPline[index + 1], intersectPos);

     PlineVertex sliceEndVertex = PlineVertex(intersectPos, 0.0);
     var midpoint = segMidpoint(split.updatedStart, sliceEndVertex);
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex, midpoint,
     queryStack)) {
     break;
     }

     if (intersectsOrigPline(split.updatedStart, sliceEndVertex)) {
     break;
     }

     internal::addOrReplaceIfSamePos(firstSlice, split.updatedStart);
     internal::addOrReplaceIfSamePos(firstSlice, sliceEndVertex);
     result.emplace_back(0, std::move(firstSlice));
     break;
     }

     index += 1;
     }
     }

     for (final var kvp : intersectsLookup) {
     // start index for the slice we're about to build
     int sIndex = kvp.first;
     // self intersect list for this start index
     List<Vector2> final &siList = kvp.second;

     final var startVertex = rawOffsetPline[sIndex];
     int nextIndex = utils::nextWrappingIndex(sIndex, rawOffsetPline);
     final var endVertex = rawOffsetPline[nextIndex];

     if (siList.size() != 1) {
     // build all the segments between the N intersects in siList (N > 1), skipping the first
     // segment (to be processed at the end)
     SplitResult firstSplit = splitAtPoint(startVertex, endVertex, siList[0]);
     var prevVertex = firstSplit.splitVertex;
     for (int i = 1; i < siList.size(); ++i) {
     SplitResult split = splitAtPoint(prevVertex, endVertex, siList[i]);
     // update prevVertex for next loop iteration
     prevVertex = split.splitVertex;
     // skip if they're ontop of each other
     if (fuzzyEqual(split.updatedStart.pos(), split.splitVertex.pos(),
     utils::realPrecision())) {
     continue;
     }

     // test start point
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
     split.updatedStart.pos(), queryStack)) {
     continue;
     }

     // test end point
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
     split.splitVertex.pos(), queryStack)) {
     continue;
     }

     // test mid point
     var midpoint = segMidpoint(split.updatedStart, split.splitVertex);
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex, midpoint,
     queryStack)) {
     continue;
     }

     // test intersection with original polyline
     if (intersectsOrigPline(split.updatedStart, split.splitVertex)) {
     continue;
     }

     result.emplace_back();
     result.back().intrStartIndex = sIndex;
     result.back().pline.addVertex(split.updatedStart);
     result.back().pline.addVertex(split.splitVertex);
     }
     }

     // build the segment between the last intersect in siList and the next intersect found

     // check that the first point is valid
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex, siList.back(),
     queryStack)) {
     continue;
     }

     SplitResult split = splitAtPoint(startVertex, endVertex, siList.back());
     Polyline currSlice;
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
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
     rawOffsetPline[index].pos(), queryStack)) {
     isValidPline = false;
     break;
     }

     // check that the segment does not intersect original polyline
     if (intersectsOrigPline(currSlice.lastVertex(), rawOffsetPline[index])) {
     isValidPline = false;
     break;
     }

     // add vertex
     internal::addOrReplaceIfSamePos(currSlice, rawOffsetPline[index]);

     // check if segment that starts at vertex we just added has an intersect
     var nextIntr = intersectsLookup.find(index);
     if (nextIntr != intersectsLookup.end()) {
     // there is an intersect, slice is done, check if final segment is valid

     // check intersect pos is valid (which will also be end vertex position)
     final Vector2 intersectPos = nextIntr->second[0];
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex,
     intersectPos, queryStack)) {
     isValidPline = false;
     break;
     }

     int nextIndex = utils::nextWrappingIndex(index, rawOffsetPline);
     SplitResult split =
     splitAtPoint(currSlice.lastVertex(), rawOffsetPline[nextIndex], intersectPos);

     PlineVertex sliceEndVertex = PlineVertex(intersectPos, 0.0);
     // check mid point is valid
     Vector2 mp = segMidpoint(split.updatedStart, sliceEndVertex);
     if (!internal::pointValidForOffset(originalPline, offset, origPlineSpatialIndex, mp,
     queryStack)) {
     isValidPline = false;
     break;
     }

     // trim last added vertex and add final intersect position
     currSlice.lastVertex() = split.updatedStart;
     internal::addOrReplaceIfSamePos(currSlice, sliceEndVertex);

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
     result.emplace_back(sIndex, std::move(currSlice));
     }
     }

     return result;
     }
     */
}
