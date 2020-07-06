package org.jhotdraw8.geom.offsetline;

import org.jhotdraw8.geom.AWTPathBuilder;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiPredicate;

import static org.jhotdraw8.geom.offsetline.PlineVertex.createFastApproxBoundingBox;
import static org.jhotdraw8.geom.offsetline.PlineVertex.segLength;

/**
 * A PolyArcPath is defined by a sequence of vertexes and a bool indicating
 * whether the polyline is closed or open. Each vertex has a 2D position
 * (x and y) as well as a bulge value. Bulge is used to define arcs,
 * where bulge = tan(theta/4). theta is the arc sweep angle from the starting
 * vertex position to the next vertex position. If the polyline is closed then
 * the last vertex connects to the first vertex, otherwise it does not
 * (and the last vertex bulge value is unused). See [2] for more details
 * regarding bulge calculations.
 * <p>
 * References:
 * <ul>
 *  <li>Bulge conversions: http://www.lee-mac.com/bulgeconversion.html</li>
 * </ul>
 * </p>
 */
public class Polyline extends ArrayList<PlineVertex> implements Cloneable {
    private final static long serialVersionUID = 1L;

    @Override
    public Polyline clone() {
        return (Polyline) super.clone();
    }

    private boolean closed;
    private int windingRule = PathIterator.WIND_EVEN_ODD;

    public Polyline() {
        super();
    }

    public Polyline(int initialCapacity) {
        super(initialCapacity);
    }

    public void addVertex(PlineVertex v) {
        add(v);
    }

    public void addVertex(double x, double y) {
        addVertex(x, y, 0.0);
    }

    public void addVertex(double x, double y, double bulge) {
        add(new PlineVertex(x, y, bulge));
    }

    public boolean isClosed() {
        return closed;
    }

    public PlineVertex lastVertex() {
        return get(size() - 1);
    }

    public void lastVertex(PlineVertex newValue) {
        set(size() - 1, newValue);
    }

    public void isClosed(boolean closed) {
        this.closed = closed;
    }

    public int getWindingRule() {
        return windingRule;
    }

    public void pop_back() {
        remove(size() - 1);
    }

    /// Iterate the segement indices of the polyline. visitor function is invoked for each segment
    /// index pair, stops when all indices have been visited or visitor returns false. visitor
    /// signature is bool(int, int).
    public void visitSegIndices(BiPredicate<Integer, Integer> visitor) {
        if (size() < 2) {
            return;
        }
        int i;
        int j;
        if (isClosed()) {
            i = 0;
            j = size() - 1;
        } else {
            i = 1;
            j = 0;
        }

        while (i < size() && visitor.test(j, i)) {
            j = i;
            i = i + 1;
        }
    }

    public void setWindingRule(int windingRule) {
        this.windingRule = windingRule;
    }

    public PathIterator getPathIterator(AffineTransform at) {
        AWTPathBuilder b = new AWTPathBuilder();
        PlineVertex prev = Polyline.this.get(Polyline.this.size() - 1);
        boolean first = true;
        for (PlineVertex vertex : Polyline.this) {
            double bulge = prev.bulge();
            if (bulge == 0.0) {
                if (first) {
                    first = false;
                    b.moveTo(vertex.getX(), vertex.getY());
                } else {
                    b.lineTo(vertex.getX(), vertex.getY());
                }
            } else {
                BulgeConversionFunctions.ArcRadiusAndCenter circle = BulgeConversionFunctions.computeCircle(
                        prev.getX(), prev.getY(),
                        vertex.getX(), vertex.getY(),
                        bulge);
                if (first) {
                    first = false;
                    b.moveTo(prev.getX(), prev.getY());
                }
                b.arcTo(circle.getRadius(), circle.getRadius(), 0,
                        vertex.getX(), vertex.getY(), false, bulge > 0);
            }
            prev = vertex;
        }
        if (Polyline.this.isClosed()) {
            b.closePath();
        }
        Path2D path = b.build();
        path.setWindingRule(getWindingRule());
        return path.getPathIterator(at);
    }

    /// Creates an approximate spatial index for all the segments in the polyline given using
    /// createFastApproxBoundingBox.
    public static StaticSpatialIndex createApproxSpatialIndex(final Polyline pline) {
        assert pline.size() > 1 : "need at least 2 vertexes to form segments for spatial index";

        int segmentCount = pline.isClosed() ? pline.size() : pline.size() - 1;
        StaticSpatialIndex result = new StaticSpatialIndex(segmentCount);

        for (int i = 0; i < pline.size() - 1; ++i) {
            AABB approxBB = createFastApproxBoundingBox(pline.get(i), pline.get(i + 1));
            result.add(approxBB.xMin, approxBB.yMin, approxBB.xMax, approxBB.yMax);
        }

        if (pline.isClosed()) {
            // add final segment from last to first
            AABB approxBB = createFastApproxBoundingBox(pline.lastVertex(), pline.get(0));
            result.add(approxBB.xMin, approxBB.yMin, approxBB.xMax, approxBB.yMax);
        }

        result.finish();

        return result;
    }

    /// becomes the end vertex and the end vertex becomes the starting vertex.
    public static void invertDirection(Polyline pline) {
        if (pline.size() < 2) {
            return;
        }

        Collections.reverse(pline);

        // shift and negate bulge (to maintain same geometric path)
        double firstBulge = pline.get(0).bulge();

        for (int i = 1; i < pline.size(); ++i) {
            pline.get(i - 1).bulge(-pline.get(i).bulge());
        }

        pline.lastVertex().bulge(-firstBulge);
    }


    /// Calculate the total path length of a polyline.
    public double getPathLength() {
        if (size() < 2) {
            return 0.0;
        }
        double[] result = new double[]{0.0};
        BiPredicate<Integer, Integer> visitor = (Integer i, Integer j) -> {
            result[0] += segLength(get(i), get(j));
            return true;
        };

        visitSegIndices(visitor);

        return result[0];
    }


}

