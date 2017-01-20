/* @(#)BezierNodePathIterator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.geom;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import static java.awt.geom.PathIterator.SEG_CLOSE;
import static java.awt.geom.PathIterator.SEG_CUBICTO;
import static java.awt.geom.PathIterator.SEG_LINETO;
import static java.awt.geom.PathIterator.SEG_MOVETO;
import static java.awt.geom.PathIterator.SEG_QUADTO;
import java.util.List;

/**
 * BezierNodePathIterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class BezierNodePathIterator implements PathIterator {
private final List<BezierNode> nodes;
private  int index;
private final boolean closed;
private final AffineTransform affine;
private int windingRule;

    public BezierNodePathIterator(List<BezierNode>nodes, boolean closed, int windingRule, AffineTransform affine) {
        this.nodes = nodes;
       this.closed = closed;
        this.windingRule=windingRule;
        this.affine=affine;
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
        return (index >= nodes.size() + (closed ? 2 : 0));
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
        int numCoords = 0;
        int type = 0;
        if (index == nodes.size()) {
            // We only get here for closed paths
            if (nodes.size() > 1) {
                BezierNode previous = nodes.get(nodes.size() - 1);
                BezierNode current = nodes.get(0);

                if ((previous.mask & BezierNode.C2_MASK) == 0) {
                    if ((current.mask & BezierNode.C1_MASK) == 0) {
                        numCoords = 1;
                        type = SEG_LINETO;
                        coords[0] = (float) current.x0;
                        coords[1] = (float) current.y0;
                    } else {
                        numCoords = 2;
                        type = SEG_QUADTO;
                        coords[0] = (float) current.x1;
                        coords[1] = (float) current.y1;
                        coords[2] = (float) current.x0;
                        coords[3] = (float) current.y0;
                    }
                } else {
                    if ((current.mask & BezierNode.C1_MASK) == 0) {
                        numCoords = 2;
                        type = SEG_QUADTO;
                        coords[0] = (float) previous.x2;
                        coords[1] = (float) previous.y2;
                        coords[2] = (float) current.x0;
                        coords[3] = (float) current.y0;
                    } else {
                        numCoords = 3;
                        type = SEG_CUBICTO;
                        coords[0] = (float) previous.x2;
                        coords[1] = (float) previous.y2;
                        coords[2] = (float) current.x1;
                        coords[3] = (float) current.y1;
                        coords[4] = (float) current.x0;
                        coords[5] = (float) current.y0;
                    }
                }
            }
        } else if (index > nodes.size()) {
            // We only get here for closed paths
            return SEG_CLOSE;
        } else if (index == 0) {
            BezierNode current = nodes.get(index);
            coords[0] = (float) current.x0;
            coords[1] = (float) current.y0;
            numCoords = 1;
            type = SEG_MOVETO;

        } else if (index < nodes.size()) {
            BezierNode current = nodes.get(index);
            BezierNode previous = nodes.get(index - 1);

            if ((current.mask & BezierNode.MOVE_MASK) == BezierNode.MOVE_MASK) {
                numCoords = 1;
                    type = SEG_MOVETO;
                    coords[0] = (float) current.x0;
                    coords[1] = (float) current.y0;

        }else if ((previous.mask & BezierNode.C2_MASK) == 0) {
                if ((current.mask & BezierNode.C1_MASK) == 0) {
                    numCoords = 1;
                    type = SEG_LINETO;
                    coords[0] = (float) current.x0;
                    coords[1] = (float) current.y0;

                } else {
                    numCoords = 2;
                    type = SEG_QUADTO;
                    coords[0] = (float) current.x1;
                    coords[1] = (float) current.y1;
                    coords[2] = (float) current.x0;
                    coords[3] = (float) current.y0;
                }
            } else {
                if ((current.mask & BezierNode.C1_MASK) == 0) {
                    numCoords = 2;
                    type = SEG_QUADTO;
                    coords[0] = (float) previous.x2;
                    coords[1] = (float) previous.y2;
                    coords[2] = (float) current.x0;
                    coords[3] = (float) current.y0;
                } else {
                    numCoords = 3;
                    type = SEG_CUBICTO;
                    coords[0] = (float) previous.x2;
                    coords[1] = (float) previous.y2;
                    coords[2] = (float) current.x1;
                    coords[3] = (float) current.y1;
                    coords[4] = (float) current.x0;
                    coords[5] = (float) current.y0;
                }
            }
        }

        if (affine != null) {
            affine.transform(coords, 0, coords, 0, numCoords);
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
        if (index == nodes.size()) {
            // We only get here for closed paths
            if (nodes.size() > 1) {
                BezierNode previous = nodes.get(nodes.size() - 1);
                BezierNode current = nodes.get(0);

                if ((previous.mask & BezierNode.C2_MASK) == 0) {
                    if ((current.mask & BezierNode.C1_MASK) == 0) {
                        numCoords = 1;
                        type = SEG_LINETO;
                        coords[0] = current.x0;
                        coords[1] = current.y0;
                    } else {
                        numCoords = 2;
                        type = SEG_QUADTO;
                        coords[0] = current.x1;
                        coords[1] = current.y1;
                        coords[2] = current.x0;
                        coords[3] = current.y0;
                    }
                } else {
                    if ((current.mask & BezierNode.C1_MASK) == 0) {
                        numCoords = 2;
                        type = SEG_QUADTO;
                        coords[0] = previous.x2;
                        coords[1] = previous.y2;
                        coords[2] = current.x0;
                        coords[3] = current.y0;
                    } else {
                        numCoords = 3;
                        type = SEG_CUBICTO;
                        coords[0] = previous.x2;
                        coords[1] = previous.y2;
                        coords[2] = current.x1;
                        coords[3] = current.y1;
                        coords[4] = current.x0;
                        coords[5] = current.y0;
                    }
                }
            }
        } else if (index > nodes.size()) {
            // We only get here for closed paths
            return SEG_CLOSE;
        } else if (index == 0) {
            BezierNode current = nodes.get(index);
            coords[0] = current.x0;
            coords[1] = current.y0;
            numCoords = 1;
            type = SEG_MOVETO;

        } else if (index < nodes.size()) {
            BezierNode current = nodes.get(index);
            BezierNode previous = nodes.get(index - 1);

            if ((current.mask & BezierNode.MOVE_MASK) == BezierNode.MOVE_MASK) {
                numCoords = 1;
                    type = SEG_MOVETO;
                    coords[0] = (float) current.x0;
                    coords[1] = (float) current.y0;

        }else if ((previous.mask & BezierNode.C2_MASK) == 0) {
                if ((current.mask & BezierNode.C1_MASK) == 0) {
                    numCoords = 1;
                    type = SEG_LINETO;
                    coords[0] = current.x0;
                    coords[1] = current.y0;

                } else {
                    numCoords = 2;
                    type = SEG_QUADTO;
                    coords[0] = current.x1;
                    coords[1] = current.y1;
                    coords[2] = current.x0;
                    coords[3] = current.y0;
                }
            } else {
                if ((current.mask & BezierNode.C1_MASK) == 0) {
                    numCoords = 2;
                    type = SEG_QUADTO;
                    coords[0] = previous.x2;
                    coords[1] = previous.y2;
                    coords[2] = current.x0;
                    coords[3] = current.y0;
                } else {
                    numCoords = 3;
                    type = SEG_CUBICTO;
                    coords[0] = previous.x2;
                    coords[1] = previous.y2;
                    coords[2] = current.x1;
                    coords[3] = current.y1;
                    coords[4] = current.x0;
                    coords[5] = current.y0;
                }
            }
        }

        if (affine != null) {
            affine.transform(coords, 0, coords, 0, numCoords);
        }
        return type;
    }

}
