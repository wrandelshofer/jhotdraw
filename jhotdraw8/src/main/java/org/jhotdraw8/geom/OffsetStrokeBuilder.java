/* @(#)OffsetStrokeBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.awt.*;
import java.awt.geom.*;

/**
 * Offsets the stroke by the specified distance.
 * <p>
 * FIXME should implement the PathBuilder interface.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class OffsetStrokeBuilder extends DoubleStroke {

    private double grow;

    public OffsetStrokeBuilder(double grow, double miterLimit) {
        super(grow * 2d, 1d, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, miterLimit, null, 0f);
        this.grow = grow;
    }

    @Override
    public Shape createStrokedShape(Shape s) {

        BezierPath bp = new BezierPath();
        Path2D.Double left = new Path2D.Double();
        Path2D.Double right = new Path2D.Double();

        if (s instanceof Path2D.Double) {
            left.setWindingRule(((Path2D.Double) s).getWindingRule());
            right.setWindingRule(((Path2D.Double) s).getWindingRule());
        } else if (s instanceof BezierPath) {
            left.setWindingRule(((BezierPath) s).getWindingRule());
            right.setWindingRule(((BezierPath) s).getWindingRule());
        }

        double[] coords = new double[6];
        // FIXME - We only do a flattened path
        for (PathIterator i = s.getPathIterator(null, 0.1d); !i.isDone(); i.next()) {
            int type = i.currentSegment(coords);

            switch (type) {
                case PathIterator.SEG_MOVETO:
                    if (bp.size() != 0) {
                        traceStroke(bp, left, right);
                    }
                    bp.clear();
                    bp.moveTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    if (coords[0] != bp.get(bp.size() - 1).x[0]
                            || coords[1] != bp.get(bp.size() - 1).y[0]) {
                        bp.lineTo(coords[0], coords[1]);
                    }
                    break;
                case PathIterator.SEG_QUADTO:
                    bp.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    bp.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case PathIterator.SEG_CLOSE:
                    bp.setClosed(true);
                    break;
            }
        }
        if (bp.size() > 1) {
            traceStroke(bp, left, right);
        }

            return left;
    }
}
