/*
 * @(#)GrowStroke.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.geom;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import static java.lang.Math.*;

/**
 * GrowStroke can be used to grow/shrink a figure by a specified line width.
 * This only works with closed convex paths having edges in clockwise direction.
 * <p>
 * Note: Although this is a Stroke object, it does not actually create a stroked
 * shape, but one that can be used for filling.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class GrowStroke implements Stroke {

    private final int join;
    private final double grow;
    private final double sqMiterLimit;

    private transient Path2D.Double out;
    private transient double prevX, prevY;
    private transient boolean movePending;
    private transient Point3D prevTangent;

    private final static double EPSILON = 1e-6;

    private static class Subpath {

        int type;
        double lastX;
        double lastY;
        double firstX;
        double firstY;
        boolean closed;

        public Subpath(int type, double firstX, double firstY, double lastX, double lastY, boolean closed) {
            this.type = type;
            this.firstX = firstX;
            this.firstY = firstY;
            this.lastX = lastX;
            this.lastY = lastY;
            this.closed = closed;
        }

    }

    public GrowStroke(double grow, double miterLimit) {
        this(grow, BasicStroke.JOIN_MITER, 20);
    }

    public GrowStroke(double grow, int join, double miterLimit) {
        this.join = join;
        this.grow = grow;
        this.sqMiterLimit = miterLimit * miterLimit;
    }

    @Override
    public Shape createStrokedShape(Shape s) {
        double[] coords = new double[6];
        ArrayList<Subpath> subpaths = new ArrayList<>();
        { // pass 1: collect the last path segment of all subpaths
            int prevType = -1;
            double lastX = 0;
            double lastY = 0;
            double firstX = 0;
            double firstY = 0;
            boolean inSubpath = false;
            for (PathIterator i = s.getPathIterator(null); !i.isDone(); i.next()) {
                int type = i.currentSegment(coords);
                switch (type) {
                    case PathIterator.SEG_MOVETO:
                        if (inSubpath) {
                            // process unclosed subpath
                            subpaths.add(new Subpath(prevType, firstX, firstY, lastX, lastY, false));
                        }
                        lastX = coords[0];
                        lastY = coords[1];
                        firstX = lastX;
                        firstY = lastY;
                        inSubpath = true;
                        break;
                    case PathIterator.SEG_LINETO:
                        lastX = coords[0];
                        lastY = coords[1];
                        break;
                    case PathIterator.SEG_QUADTO:
                        lastX = coords[2];
                        lastY = coords[3];
                        break;
                    case PathIterator.SEG_CUBICTO:
                        lastX = coords[4];
                        lastY = coords[5];
                        break;
                    case PathIterator.SEG_CLOSE:
                        if (inSubpath) {
                            // process closed subpath
                            subpaths.add(new Subpath(prevType, firstX, firstY, lastX, lastY, true));
                            inSubpath = false;
                        }
                        break;
                }
                prevType = type;
            }
            if (inSubpath) {
                // process unclosed subpath
                subpaths.add(new Subpath(prevType, firstX, firstY, lastX, lastY, false));
            }
        }

        out = new Path2D.Double();
        { // pass 2: process the sub paths
            boolean inSubpath = false;
            int si = 0; // subpathindex
            double x = 0, c1x, c2x;
            double y = 0, c1y, c2y;
            for (PathIterator i = s.getPathIterator(null); !i.isDone(); i.next()) {
                int type = i.currentSegment(coords);
                Subpath sp = subpaths.get(si);
                switch (type) {
                    case PathIterator.SEG_MOVETO:
                        x = coords[0];
                        y = coords[1];
                        if (sp.closed) {
                            prevX = sp.lastX;
                            prevY = sp.lastY;
                            prevTangent = Geom.hcrossProduct(sp.lastX, sp.lastY, sp.firstX, sp.firstY);
                        } else {
                            prevTangent = null;
                        }
                        movePending = true;
                        moveTo(x, y);
                        if (inSubpath) {
                            si++;
                        }
                        inSubpath = true;
                        break;
                    case PathIterator.SEG_LINETO:
                        x = coords[0];
                        y = coords[1];
                        lineTo(x, y);
                        break;
                    case PathIterator.SEG_QUADTO:
                        c1x = coords[0];
                        c1y = coords[1];
                        x = coords[2];
                        y = coords[3];
                        quadTo(c1x, c1y, x, y);
                        break;
                    case PathIterator.SEG_CUBICTO:
                        c1x = coords[0];
                        c1y = coords[1];
                        c2x = coords[2];
                        c2y = coords[3];
                        x = coords[4];
                        y = coords[5];
                        curveTo(c1x, c1y, c2x, c2y, x, y);
                        break;
                    case PathIterator.SEG_CLOSE:
                        closePath(sp.firstX, sp.lastY);
                        if (inSubpath) {
                            si++;
                            inSubpath = false;
                        }
                        break;
                }

                prevX = x;
                prevY = y;
            }
        }
        return out;
    }

    private void moveTo(double x, double y) {
        if ((x - prevX) * (x - prevX) + (y - prevY) * (y - prevY) < EPSILON) {
            return;
        }

        Point2D s = Geom.perpendicularVector(prevX, prevY, x, y, grow);
        double sx = s.getX();
        double sy = s.getY();

        Point3D myTangent = Geom.hcrossProduct(prevX + sx, prevY + sy, x + sx, y + sy);
        join(myTangent, x + sx, y + sy);
        prevTangent = myTangent;
    }

    private void lineTo(double x, double y) {
        Point2D s = Geom.perpendicularVector(prevX, prevY, x, y, grow);
        double sx = s.getX();
        double sy = s.getY();

        Point3D myTangent = Geom.hcrossProduct(prevX + sx, prevY + sy, x + sx, y + sy);
        join(myTangent, prevX + sx, prevY + sy);
        out.lineTo(x + sx, y + sy);
        prevTangent = myTangent;
    }

    private void join(Point3D myTangent, double x, double y) {
        if (join == BasicStroke.JOIN_MITER) {
            if (prevTangent != null) {
                Point3D intersectionh = myTangent.crossProduct(prevTangent);
                if (abs(intersectionh.getZ()) > EPSILON) {
                    Point2D intersection = Geom.homogenize2D(intersectionh);
                    double sqdist = Geom.squaredDistance(intersection, x, y);
                    if (sqdist > EPSILON && sqdist < sqMiterLimit) {
                        if (movePending) {
                            out.moveTo(intersection.getX(), intersection.getY());
                            movePending = false;
                        } else {
                            out.lineTo(intersection.getX(), intersection.getY());
                        }
                        return;
                    }
                }
            }
        }
        if (movePending) {
            out.moveTo(x, y);
            movePending = false;
        } else {
            out.lineTo(x, y);
        }

    }

    private void quadTo(double c1x, double c1y, double x, double y) {
        Point2D s0 = Geom.perpendicularVector(prevX, prevY, c1x, c1y, grow);
        Point2D s1 = Geom.perpendicularVector(prevX, prevY, x, y, grow);
        Point2D s2 = Geom.perpendicularVector(c1x, c1y, x, y, grow);
        Point3D myTangent = Geom.hcrossProduct(prevX + s0.getX(), prevY + s0.getY(), c1x + s1.getX(), c1y + s1.getY());
        join(myTangent, prevX + s0.getX(), prevY + s0.getY());
        out.quadTo(c1x + s1.getX(), c1y + s1.getY(), x + s2.getX(), y + s2.getY());
        prevTangent = Geom.hcrossProduct(c1x + s1.getX(), c1y + s1.getY(), x + s2.getX(), y + s2.getY());
    }

    private void curveTo(double c1x, double c1y, double c2x, double c2y, double x, double y) {
        Point2D s0 = Geom.perpendicularVector(prevX, prevY, c1x, c1y, grow);
        Point2D s1 = Geom.perpendicularVector(prevX, prevY, c2x, c2y, grow);
        Point2D s2 = Geom.perpendicularVector(c1x, c1y, x, y, grow);
        Point2D s3 = Geom.perpendicularVector(c2x, c2y, x, y, grow);
        Point3D myTangent = Geom.hcrossProduct(prevX + s0.getX(), prevY + s0.getY(), c1x + s1.getX(), c1y + s1.getY());
        join(myTangent, prevX + s0.getX(), prevY + s0.getY());
        out.curveTo(c1x + s1.getX(), c1y + s1.getY(),
                c2x + s2.getX(), c2y + s2.getY(),
                x + s3.getX(), y + s3.getY());
        prevTangent = Geom.hcrossProduct(c2x + s2.getX(), c2y + s2.getY(), x + s3.getX(), y + s3.getY());
    }

    private void closePath(double x, double y) {
        if ((prevX - x) * (prevX - x) + (prevY - y) * (prevY - y) < EPSILON) {
            out.closePath();
            return;
        }

        Point2D s = Geom.perpendicularVector(prevX, prevY, x, y, grow);
        double sx = s.getX();
        double sy = s.getY();

        Point3D myTangent = Geom.hcrossProduct(prevX + sx, prevY + sy, x + sx, y + sy);
        join(myTangent, prevX + sx, prevY + sy);
        out.lineTo(x + sx, y + sy);
        out.closePath();
    }
}
