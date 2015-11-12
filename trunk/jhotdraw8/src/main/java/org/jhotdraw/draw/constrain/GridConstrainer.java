/* @(#)GridConstrainer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.constrain;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import static java.lang.Math.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;

/**
 * GridConstrainer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GridConstrainer extends AbstractConstrainer {

    private final Path node = new Path();

    /**
     * Up-Vector.
     */
    private final Point2D UP = new Point2D(0, 1);

    /**
     * The x-origin of the grid.
     */
    private final DoubleProperty x = new SimpleDoubleProperty(this, "x") {

        @Override
        public void invalidated() {
            fireInvalidated();
        }
    };

    /**
     * The y-origin of the grid.
     */
    private final DoubleProperty y = new SimpleDoubleProperty(this, "y") {

        @Override
        public void invalidated() {
            fireInvalidated();
        }
    };

    /**
     * Width of a grid cell. The value 0 turns the constrainer off for the
     * horizontal axis.
     */
    private final DoubleProperty width = new SimpleDoubleProperty(this, "width") {

        @Override
        public void invalidated() {
            fireInvalidated();
        }
    };
    /**
     * Height of a grid cell. The value 0 turns the constrainer off for the
     * vertical axis.
     */
    private final DoubleProperty height = new SimpleDoubleProperty(this, "height") {

        @Override
        public void invalidated() {
            fireInvalidated();
        }
    };
    /**
     * The angle for constrained rotations on the grid (in degrees). The value 0
     * turns the constrainer off for rotations.
     */
    private final DoubleProperty angle = new SimpleDoubleProperty(this, "angle") {

        @Override
        public void invalidated() {
            fireInvalidated();
        }
    };
    /**
     * Whether to draw the grid.
     */
    private final BooleanProperty drawGrid = new SimpleBooleanProperty(this, "drawGrid") {

        @Override
        public void invalidated() {
            fireInvalidated();
        }
    };
    /**
     * Whether to snap to the grid.
     */
    private final BooleanProperty snapToGrid = new SimpleBooleanProperty(this, "snapToGrid", true) {

        @Override
        public void invalidated() {
            fireInvalidated();
        }
    };

    /**
     * Creates a grid of 10x10 pixels at origin 0,0 and 22.5 degree rotations.
     */
    public GridConstrainer() {
        this(0, 0, 10, 10, 22.5);
    }

    /**
     * Creates a grid of width x height pixels at origin 0,0 and 22.5 degree
     * rotations.
     *
     * @param width The width of the grid. 0 turns the grid of for the x-axis.
     * @param height The width of the grid. 0 turns the grid of for the y-axis.
     */
    public GridConstrainer(double width, double height) {
        this(0, 0, width, height, 22.5);
    }

    /**
     * Creates a grid with the specified constraints.
     *
     * @param x The x-origin of the grid
     * @param y The y-origin of the grid
     * @param width The width of the grid. 0 turns the grid of for the x-axis.
     * @param height The width of the grid. 0 turns the grid of for the y-axis.
     * @param angle The angular grid (in degrees). 0 turns the grid off for
     * rotations.
     */
    public GridConstrainer(double x, double y, double width, double height, double angle) {
        this.x.set(x);
        this.y.set(y);
        this.width.set(width);
        this.height.set(height);
        this.angle.set(angle);
        this.node.getStyleClass().setAll(STYLECLASS_CONSTRAINER_GRID);
    }

    @Override
    public Point2D translatePoint(Figure f, Point2D p, Point2D dir) {
        if (!snapToGrid.get()) {
            return p;
        }
        double cx = this.x.get();
        double cy = this.y.get();
        double cwidth = this.width.get();
        double cheight = this.height.get();

        double tx = (cwidth == 0) ? p.getX() : (p.getX() - cx) / cwidth;
        double ty = (cheight == 0) ? p.getY() : (p.getY() - cy) / cheight;

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

        return new Point2D(tx * cwidth + cx, ty * cheight + cy);
    }

    @Override
    public Rectangle2D translateRectangle(Figure f, Rectangle2D r, Point2D dir) {
        if (!snapToGrid.get()) {
            return r;
        }

        double cx = this.x.get();
        double cy = this.y.get();
        double cwidth = this.width.get();
        double cheight = this.height.get();

        double tx = (cwidth == 0) ? r.getMinX() : (r.getMinX() - cx) / cwidth;
        double ty = (cheight == 0) ? r.getMinY() : (r.getMinY() - cy) / cheight;
        double tmaxx = (cwidth == 0) ? r.getMaxX() : (r.getMaxX() - cx) / cwidth;
        double tmaxy = (cheight == 0) ? r.getMaxY() : (r.getMaxY() - cy) / cheight;

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

        return new Rectangle2D(tx * cwidth + cx, ty * cheight + cy, r.getWidth(), r.getHeight());
    }

    @Override
    public double translateAngle(Figure f, double angle, double dir) {
        if (!snapToGrid.get()) {
            return angle;
        }

        double cAngle = this.angle.get();

        if (cAngle == 0) {
            return angle;
        }

        double ta = angle / cAngle;

        if (Double.isNaN(dir) || dir == 0) {
            ta = round(ta);
        } else if (dir < 0) {
            ta = floor(ta + 1);
        } else {
            ta = ceil(ta - 1);
        }

        double result = (ta * cAngle) % 360;
        return result < 0 ? 360 + result : result;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView drawingView) {
        ObservableList<PathElement> elements = node.getElements();
        elements.clear();
        if (drawGrid.get()) {
            Drawing drawing = drawingView.getDrawing();
            Transform t = drawingView.getDrawingToView();
            Point2D gxy = t.transform(x.get(), y.get());
            Point2D gwh = t.transform(width.get(), height.get());
            Point2D dwh = t.transform(drawing.get(Drawing.WIDTH), drawing.get(Drawing.HEIGHT));
            Point2D zxy = t.transform(0, 0);
            double zx = zxy.getX();
            double zy = zxy.getY();
            double gx = gxy.getX();
            double gy = gxy.getY();
            double gw = gwh.getX();
            double gh = gwh.getY();
            double dw = dwh.getX();
            double dh = dwh.getY();

            if (gh > 1) {
                for (double iy = zy; iy < dh; iy += gh) {
                    double yrounded = Math.round(iy + gy) + 0.5;
                    elements.add(new MoveTo(zx, yrounded));
                    elements.add(new LineTo(dw, yrounded));
                }
            }
            if (gw > 1) {
                for (double ix = zx; ix < dw; ix += gw) {
                    double xrounded = Math.round(ix + gx) + 0.5;
                    elements.add(new MoveTo(xrounded, zy));
                    elements.add(new LineTo(xrounded, dh));
                }
            }
        }
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public DoubleProperty angleProperty() {
        return angle;
    }

    public BooleanProperty drawGridProperty() {
        return drawGrid;
    }

    public BooleanProperty snapToGridProperty() {
        return snapToGrid;
    }

}
