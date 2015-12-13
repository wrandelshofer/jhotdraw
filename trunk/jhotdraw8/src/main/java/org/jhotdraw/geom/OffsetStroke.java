/* @(#)OffsetStroke.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.geom;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

/**
 * Strokes a shape along a perpendicular offset.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class OffsetStroke implements Stroke {

    /**
     * The offset width.
     */
    private double width;
    /**
     * How to join path segments. BasicStroke.
     */
    private int join;
    /**
     * Miter limit.
     */
    private double miterLimit;

    // Variables used during stroking
    private transient Path2D.Double out;
    private transient double firstX;
    private transient double firstY;
    private transient double lastX;
    private transient double lastY;
    private transient boolean inSubpath;
    private boolean isClosed;

    @Override
    public Shape createStrokedShape(Shape s) {
        out = new Path2D.Double();
        beginPath();
        double[] coords = new double[6];
        for (PathIterator i = s.getPathIterator(null); !i.isDone(); i.next()) {
            int type = i.currentSegment(coords);

            switch (type) {
                case PathIterator.SEG_MOVETO:
                    beginSubpath(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    appendLine(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    appendQuadratic(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    appendCubic(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case PathIterator.SEG_CLOSE:
                    closedSubpath();
                    break;
            }
        }
        endPath();
        return out;
    }

    /**
     * beginSubpath.
     */
    private void beginSubpath(double x0, double y0) {
        if (inSubpath) {
            endOfSubpath();
        }
        inSubpath = true;
        isClosed=false;
        firstX = lastX = x0;
        firstY = lastY = y0;
    }

    /**
     * appendLine.
     */
    private void appendLine(double x1, double y1) {
        if (!inSubpath) {
            throw new IllegalArgumentException("initial moveTo missing");
        }
        lastX = x1;
        lastY = y1;
    }

    /**
     * appendQuadratic.
     */
    private void appendQuadratic(double x1, double y1, double x2, double y2) {
    }

    /**
     * appendCubic.
     */
    private void appendCubic(double x1, double y1, double x2, double y2, double x3, double y3) {
    }

    /**
     * closedSubpath.
     */
    private void closedSubpath() {
    }

    private void endOfSubpath() {
        inSubpath = false;
    }

    private void beginPath() {
        inSubpath = false;
        isClosed = false;
    }

    private void endPath() {
        if (inSubpath) {
            endOfSubpath();
            isClosed = false;
        }
    }
}
