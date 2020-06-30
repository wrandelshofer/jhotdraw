/*
 * @(#)CavalierContours.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.offsetline;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.Geom;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

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
    public final static double realPrecision = 1e-5;
    public final static double realThreshold = 1e-8;

    /**
     * Normalized perpendicular vector to v (rotating counter clockwise).
     */
    private static Point2D unitPerp(Point2D v) {
        Point2D result = new Point2D(-v.getY(), v.getX());
        return result.normalize();
    }


    /* Compute the arc radius and arc center of a arc segment defined by v1 to v2.*/
    public static BulgeConversionFunctions.ArcRadiusAndCenter arcRadiusAndCenter(PlineVertex v1,
                                                                                 PlineVertex v2) {
        assert !v1.bulgeIsZero() : "v1 to v2 must be an arc";
        assert !fuzzyEqual(v1.pos(), v2.pos()) : "v1 must not be ontop of v2";

        // compute radius
        double b = Math.abs(v1.bulge());
        Point2D v = v2.pos().subtract(v1.pos());
        double d = v.magnitude();
        double r = d * (b * b + 1.0) / (4.0 * b);

        // compute center
        double s = b * d / 2.0;
        double m = r - s;
        double offsX = -m * v.getY() / d;
        double offsY = m * v.getX() / d;
        if (v1.bulgeIsNeg()) {
            offsX = -offsX;
            offsY = -offsY;
        }

        Point2D c = new Point2D(v1.getX() + v.getX() * 0.5 + offsX, v1.getY() + v.getY() * 0.5 + offsY);
        return new BulgeConversionFunctions.ArcRadiusAndCenter(r, c);


    }

    private static boolean fuzzyEqual(Point2D v1, Point2D v2) {
        return fuzzyEqual(v1, v2, realThreshold);
    }

    private static boolean fuzzyEqual(Point2D v1, Point2D v2, double epsilon) {
        return Geom.squaredDistance(v1, v2) < epsilon * epsilon;
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

            boolean isCollapsedArc = radiusAfterOffset < realThreshold;
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

    /**
     * Computes the resulting polyline.
     *
     * @param pline  input polyline
     * @param offset offset
     * @return offset polyline
     */
    @NonNull
    public Polyline parallelOffset(@NonNull Polyline pline, double offset) {
        throw new UnsupportedOperationException();
    }
/*
    /** Creates the raw offset polyline. * /
    Polyline createRawOffsetPline(Polyline pline, double offset) {

        Polyline result;
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

        result=new Polyline(pline.size());
        result.isClosed( pline.isClosed());

  final boolean connectionArcsAreCCW = offset < 0;

        BiConsumer<PlineOffsetSegment,PlineOffsetSegment> joinResultVisitor =(s1,s2)-> {
    final boolean s1IsLine = s1.v1.bulgeIsZero();
    final boolean s2IsLine = s2.v1.bulgeIsZero();
            if (s1IsLine && s2IsLine) {
               lineToLineJoin(s1, s2, connectionArcsAreCCW, result);
            } else if (s1IsLine) {
               lineToArcJoin(s1, s2, connectionArcsAreCCW, result);
            } else if (s2IsLine) {
               arcToLineJoin(s1, s2, connectionArcsAreCCW, result);
            } else {
               arcToArcJoin(s1, s2, connectionArcsAreCCW, result);
            }
        };

        result.addVertex(rawOffsets[0].v1);

        // join first two segments and determine if first vertex was replaced (to know how to handle last
        // two segment joins for closed polyline)
        if (rawOffsets.size() > 1) {

            final var seg01 = rawOffsets[0];
            final var seg12 = rawOffsets[1];
            joinResultVisitor(seg01, seg12, result);
        }
  final boolean firstVertexReplaced = result.size() == 1;

        for (int i = 2; i < rawOffsets.size(); ++i) {
    final var seg1 = rawOffsets[i - 1];
    final var seg2 = rawOffsets[i];
            joinResultVisitor(seg1, seg2, result);
        }

        if (pline.isClosed() && result.size() > 1) {
            // joining segments at vertex indexes (n, 0) and (0, 1)
    final var s1 = rawOffsets.back();
    final var s2 = rawOffsets[0];

            // temp polyline to capture results of joining (to avoid mutating result)
            Polyline closingPartResult;
            closingPartResult.addVertex(result.lastVertex());
            joinResultVisitor(s1, s2, closingPartResult);

            // update last vertexes
            result.lastVertex() = closingPartResult[0];
            for (int i = 1; i < closingPartResult.size(); ++i) {
                result.addVertex(closingPartResult[i]);
            }
            result.vertexes().pop_back();

            // update first vertex (only if it has not already been updated/replaced)
            if (!firstVertexReplaced) {
      const Vector2<double> &updatedFirstPos = closingPartResult.lastVertex().pos();
                if (result[0].bulgeIsZero()) {
                    // just update position
                    result[0].pos() = updatedFirstPos;
                } else if (result.size() > 1) {
                    // update position and bulge
        final var arc = arcRadiusAndCenter(result[0], result[1]);
        const double a1 = angle(arc.center, updatedFirstPos);
        const double a2 = angle(arc.center, result[1].pos());
        const double updatedTheta = utils::deltaAngle(a1, a2);
                    if ((updatedTheta < double(0) && result[0].bulgeIsPos()) ||
                            (updatedTheta > double(0) && result[0].bulgeIsNeg())) {
                        // first vertex not valid, just update its position to be removed later
                        result[0].pos() = updatedFirstPos;
                    } else {
                        // update position and bulge
                        result[0].pos() = updatedFirstPos;
                        result[0].bulge() = std::tan(updatedTheta / double(4));
                    }
                }
            }

            // must do final singularity prune between first and second vertex after joining curves (n, 0)
            // and (0, 1)
            if (result.size() > 1) {
                if (fuzzyEqual(result[0].pos(), result[1].pos(), utils::realPrecision<double>())) {
                    result.vertexes().erase(result.vertexes().begin());
                }
            }
        } else {
            internal::addOrReplaceIfSamePos(result, rawOffsets.back().v2);
        }

        // if due to joining of segments we are left with only 1 vertex then return no raw offset (empty
        // polyline)
        if (result.size() == 1) {
            result.vertexes().clear();
        }

        return result;
    }
    */
}
