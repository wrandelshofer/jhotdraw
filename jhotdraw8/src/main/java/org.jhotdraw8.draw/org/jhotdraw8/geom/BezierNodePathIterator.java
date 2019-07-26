/*
 * @(#)BezierNodePathIterator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.Nonnull;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * BezierNodePathIterator.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierNodePathIterator implements PathIterator {

    @Nonnull
    private final List<BezierNode> nodes;
    private int index;
    private final AffineTransform affine;
    private final int size;
    private int windingRule;

    private final BezierNode CLOSE_PATH = new BezierNode(0, 0);

    public BezierNodePathIterator(List<BezierNode> nodes, boolean closed, int windingRule, AffineTransform affine) {
        this.nodes = new ArrayList<BezierNode>();
        for (BezierNode n : nodes) {
            this.nodes.add(n);
            if ((n.getMask() & BezierNode.CLOSE_MASK) == BezierNode.CLOSE_MASK) {
                this.nodes.add(CLOSE_PATH);
            }
        }
        if (closed && !nodes.isEmpty()) {
            this.nodes.add(nodes.get(0));
            this.nodes.add(CLOSE_PATH);
        }
        size = this.nodes.size();
        this.windingRule = windingRule;
        this.affine = affine;
    }

    /**
     * Return the winding rule for determining the interior of the path.
     *
     * @see PathIterator#WIND_EVEN_ODD
     * @see PathIterator#WIND_NON_ZERO
     */
    @Override
    public int getWindingRule() {
        return windingRule;
    }

    /**
     * Tests if there are more points to read.
     *
     * @return true if there are more points to read
     */
    @Override
    public boolean isDone() {
        // open path: we need one additional segment for the initial moveTo
        // closed path: we need two additional segments: one for the initial moveTo and one for the closePath
        return index >= size;
    }

    /**
     * Moves the iterator to the next segment of the path forwards along the
     * primary direction of traversal as long as there are more points in that
     * direction.
     */
    @Override
    public void next() {
        if (!isDone()) {
            index++;
        }
    }

    private double[] temp_double = new double[6];

    /**
     * Returns the coordinates and type of the current path segment in the
     * iteration. The return value is the path segment type: SEG_MOVETO,
     * SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE. A float array of
     * length 6 must be passed in and may be used to store the coordinates of
     * the point(s). Each point is stored as a pair of float x,y coordinates.
     * SEG_MOVETO and SEG_LINETO types will return one point, SEG_QUADTO will
     * return two points, SEG_CUBICTO will return 3 points and SEG_CLOSE will
     * not return any points.
     *
     * @see PathIterator#SEG_MOVETO
     * @see PathIterator#SEG_LINETO
     * @see PathIterator#SEG_QUADTO
     * @see PathIterator#SEG_CUBICTO
     * @see PathIterator#SEG_CLOSE
     */
    @Override
    public int currentSegment(float[] coords) {
        int type = currentSegment(temp_double);
        for (int i = 0; i < temp_double.length; i++) {
            coords[i] = (float) temp_double[i];
        }
        return type;
    }

    /**
     * Returns the coordinates and type of the current path segment in the
     * iteration. The return value is the path segment type: SEG_MOVETO,
     * SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE. A double array of
     * length 6 must be passed in and may be used to store the coordinates of
     * the point(s). Each point is stored as a pair of double x,y coordinates.
     * SEG_MOVETO and SEG_LINETO types will return one point, SEG_QUADTO will
     * return two points, SEG_CUBICTO will return 3 points and SEG_CLOSE will
     * not return any points.
     *
     * @see PathIterator#SEG_MOVETO
     * @see PathIterator#SEG_LINETO
     * @see PathIterator#SEG_QUADTO
     * @see PathIterator#SEG_CUBICTO
     * @see PathIterator#SEG_CLOSE
     */
    @Override
    public int currentSegment(double[] coords) {
        int numCoords = 0;
        int type = 0;

        if (index == 0) {
            BezierNode current = nodes.get(index);
            coords[0] = current.getX0();
            coords[1] = current.getY0();
            numCoords = 1;
            type = SEG_MOVETO;
        } else {
            BezierNode current = nodes.get((index) % size);
            BezierNode previous = nodes.get((index + size - 1) % size);

            if (current == CLOSE_PATH) {
                type = SEG_CLOSE;
            } else {

                if (current.isMoveTo()) {
                    numCoords = 1;
                    type = SEG_MOVETO;
                    coords[0] = (float) current.getX0();
                    coords[1] = (float) current.getY0();

                } else if (!previous.isC2()) {
                    if (!current.isC1()) {
                        numCoords = 1;
                        type = SEG_LINETO;
                        coords[0] = current.getX0();
                        coords[1] = current.getY0();

                    } else {
                        numCoords = 2;
                        type = SEG_QUADTO;
                        coords[0] = current.getX1();
                        coords[1] = current.getY1();
                        coords[2] = current.getX0();
                        coords[3] = current.getY0();
                    }
                } else {
                    if (!current.isC1()) {
                        numCoords = 2;
                        type = SEG_QUADTO;
                        coords[0] = previous.getX2();
                        coords[1] = previous.getY2();
                        coords[2] = current.getX0();
                        coords[3] = current.getY0();
                    } else {
                        numCoords = 3;
                        type = SEG_CUBICTO;
                        coords[0] = previous.getX2();
                        coords[1] = previous.getY2();
                        coords[2] = current.getX1();
                        coords[3] = current.getY1();
                        coords[4] = current.getX0();
                        coords[5] = current.getY0();
                    }
                }
            }
        }

        if (affine != null) {
            affine.transform(coords, 0, coords, 0, numCoords);
        }

        return type;
    }

}
