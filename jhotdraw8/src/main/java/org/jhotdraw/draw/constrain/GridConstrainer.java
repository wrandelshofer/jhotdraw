/* @(#)GridConstrainer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.constrain;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import static java.lang.Math.*;
import org.jhotdraw.draw.Figure;

/**
 * GridConstrainer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GridConstrainer implements Constrainer {

    /**
     * Up-Vector.
     */
    private final Point2D UP = new Point2D(0, 1);

    /**
     * The origin of the grid.
     */
    private double x;
    /**
     * The origin of the grid.
     */
    private double y;

    /**
     * Width of a grid cell. The value 0 turns the constrainer off for the
     * horizontal axis.
     */
    private double width;
    /**
     * Heigt of a grid cell. The value 0 turns the constrainer off for the
     * vertical axis.
     */
    private double height;
    /**
     * The theta for constrained rotations on the grid (in degrees). The value 0
     * turns the constrainer off for rotations.
     */
    private double theta;

    /**
     * Creates a grid of 10x10 pixels at origin 0,0 and 45 degree rotations.
     */
    public GridConstrainer() {
        this(0, 0, 10, 10, 45);
    }

    /**
     * Creates a grid of width x height pixels at origin 0,0 and 45 degree
     * rotations.
     *
     * @param width The width of the grid. 0 turns the grid of for the x-axis.
     * @param height The width of the grid. 0 turns the grid of for the y-axis.
     */
    public GridConstrainer(double width, double height) {
        this(0, 0, width, height, 45);
    }

    /**
     * Creates a grid with the specified constraints.
     *
     * @param x The x-origin of the grid
     * @param y The y-origin of the grid
     * @param width The width of the grid. 0 turns the grid of for the x-axis.
     * @param height The width of the grid. 0 turns the grid of for the y-axis.
     * @param theta The angle of the grid (in degrees). 0 turns the grid off for
     * rotations.
     */
    public GridConstrainer(double x, double y, double width, double height, double theta) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.theta = theta;
    }

    @Override
    public Point2D translatePoint(Figure f, Point2D p, Point2D dir) {
        double tx = (width == 0) ? p.getX() : (p.getX() - x) / width;
        double ty = (height == 0) ? p.getY() : (p.getY() - y) / height;

        if (dir.getX() > 0) {
            tx = floor(tx + 1);
        } else if (dir.getX() < 0) {
            tx = ceil(tx - 1);
        } else {
            tx = round(tx);
        }
        if (dir.getY() > 0) {
            ty = ceil(ty);
        } else if (dir.getY() < 0) {
            ty = floor(ty);
        } else {
            ty = round(ty);
        }

        return new Point2D(tx * width + x, ty * height + y);
    }

    @Override
    public Rectangle2D translateRectangle(Figure f, Rectangle2D r, Point2D dir) {
        double tx = (width == 0) ? r.getMinX() : (r.getMinX() - x) / width;
        double ty = (height == 0) ? r.getMinY() : (r.getMinY() - y) / height;
        double tmaxx = (width == 0) ? r.getMaxX() : (r.getMaxX() - x) / width;
        double tmaxy = (height == 0) ? r.getMaxY() : (r.getMaxY() - y) / height;

        if (dir.getX() > 0) {
            tx += floor(tmaxx + 1) - tmaxx;
        } else if (dir.getX() < 0) {
            tx = ceil(tx - 1);
        } else {
            tx = round(tx);
        }
        if (dir.getY() > 0) {
            ty += floor(tmaxy + 1) - tmaxy;
        } else if (dir.getY() < 0) {
            ty = ceil(ty - 1);
        } else {
            ty = round(ty);
        }

        return new Rectangle2D(tx * width + x, ty * height + y, r.getWidth(), r.getHeight());
    }

    @Override
    public double translateAngle(Figure f, double angle, double dir) {
        if (theta == 0) {
            return angle;
        }
        double ta = angle / theta;

        if (Double.isNaN(dir) || dir == 0) {
            ta = round(ta);
        } else if (dir < 0) {
            ta = floor(ta + 1);
        } else {
            ta = ceil(ta - 1);
        }

        double result = (ta * theta) % 360;
        return result < 0 ? 360 + result : result;
    }
}
