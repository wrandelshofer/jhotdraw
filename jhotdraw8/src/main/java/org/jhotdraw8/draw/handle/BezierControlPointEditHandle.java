/* @(#)BezierNodeEditHandle.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.Transforms;

/**
 * Handle for the point of a figure.
 *
 * @author Werner Randelshofer
 */
public class BezierControlPointEditHandle extends AbstractHandle {

    private Point2D pickLocation;
    private final MapAccessor<ImmutableObservableList<BezierNode>> pointKey;
    private final int pointIndex;
    private final Region node;
    private final String styleclass;
    private static final Rectangle REGION_SHAPE_CUSP = new Rectangle(7, 7);
    private int controlPointMask;
    private static final Path REGION_SHAPE_COLINEAR = new Path();

    static {
        final ObservableList<PathElement> elements = REGION_SHAPE_COLINEAR.getElements();
        elements.add(new MoveTo(2, 0));
        elements.add(new LineTo(5, 0));
        elements.add(new LineTo(7, 2));
        elements.add(new LineTo(7, 5));
        elements.add(new LineTo(5, 7));
        elements.add(new LineTo(2, 7));
        elements.add(new LineTo(0, 5));
        elements.add(new LineTo(0, 2));
        elements.add(new ClosePath());
    }
    private static final Path REGION_SHAPE_EQUIDISTANT = new Path();

    static {
        final ObservableList<PathElement> elements = REGION_SHAPE_EQUIDISTANT.getElements();
        elements.add(new MoveTo(0, 0));
        elements.add(new LineTo(4, -4));
        elements.add(new LineTo(8, 0));
        elements.add(new LineTo(4, 4));
        elements.add(new ClosePath());
    }
    private static final Circle REGION_SHAPE_SMOOTH = new Circle(0, 0, 4);
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));

    public BezierControlPointEditHandle(Figure figure, MapAccessor<ImmutableObservableList<BezierNode>> pointKey, int pointIndex, int controlPointMask) {
        this(figure, pointKey, pointIndex, controlPointMask, STYLECLASS_HANDLE_CONTROL_POINT);
    }

    public BezierControlPointEditHandle(Figure figure, MapAccessor<ImmutableObservableList<BezierNode>> pointKey, int pointIndex, int controlPointMask, String styleclass) {
        super(figure);
        this.pointKey = pointKey;
        this.pointIndex = pointIndex;
        this.styleclass = styleclass;
        this.controlPointMask = controlPointMask;
        if (this.controlPointMask != BezierNode.C1_MASK && this.controlPointMask != BezierNode.C2_MASK) {
            throw new IllegalArgumentException("controlPoint:" + controlPointMask);
        }
        node = new Region();
        node.setShape(REGION_SHAPE_CUSP);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11);
        
        node.getStyleClass().addAll(styleclass,STYLECLASS_HANDLE);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    @Override
    public Region getNode() {
        return node;
    }

    private BezierNode getBezierNode() {
        ImmutableObservableList<BezierNode> list = owner.get(pointKey);
        return list.get(pointIndex);

    }

    private Point2D getLocation() {
        return getBezierNode().getC(controlPointMask);

    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        ImmutableObservableList<BezierNode> list = f.get(pointKey);
        if (pointIndex >= list.size()) {
            return;
        }
        BezierNode p = getBezierNode();
        Point2D cp = getLocation();
        pickLocation = cp = t == null ? cp : t.transform(cp);
        node.relocate(cp.getX() - 5, cp.getY() - 5);
        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));

        BezierNode bn = getBezierNode();
        if (bn.isColinear()) {
            if (bn.isEquidistant()) {
                node.setShape(REGION_SHAPE_SMOOTH);
            } else {
                node.setShape(REGION_SHAPE_COLINEAR);
            }
        } else if (bn.isEquidistant()) {
            node.setShape(REGION_SHAPE_EQUIDISTANT);
        } else {
            node.setShape(REGION_SHAPE_CUSP);
        }

        node.setVisible(!bn.isMoveTo() && (bn.isC(controlPointMask)));

    }

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
    }

    @Override
    public void handleMouseDragged(MouseEvent event, DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));
        final Figure f = getOwner();

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control switches the constrainer off
            newPoint = view.getConstrainer().constrainPoint(f, newPoint);
        }

        ImmutableObservableList<BezierNode> list = owner.get(pointKey);
        if (pointIndex >= list.size()) {
            return;
        }
        BezierNode bn = list.get(pointIndex);
        Point2D p = f.worldToLocal(newPoint);

        if (!bn.isColinear()) {
            // move control point independently
            if (controlPointMask == BezierNode.C1_MASK) {
                view.getModel().set(f, pointKey,
                        ImmutableObservableList.set(list, pointIndex, bn.setC1(p)));
            } else {
                view.getModel().set(f, pointKey,
                        ImmutableObservableList.set(list, pointIndex, bn.setC2(p)));
            }

        } else {
            Point2D c0 = bn.getC0();

            // move control point and opposite control point on same line
            double a = Math.PI + Math.atan2(p.getY() - c0.getY(), p.getX() - c0.getX());
            Point2D p2;
            if (controlPointMask == BezierNode.C1_MASK) {
                p2 = bn.getC2();
            } else {
                p2 = bn.getC1();
            }

            double r = Math.sqrt((p2.getX() - c0.getX()) * (p2.getX() - c0.getX())
                    + (p2.getY() - c0.getY()) * (p2.getY() - c0.getY()));
            double sina = Math.sin(a);
            double cosa = Math.cos(a);

            p2 = new Point2D(
                    r * cosa + c0.getX(),
                    r * sina + c0.getY());
            if (controlPointMask == BezierNode.C1_MASK) {
                view.getModel().set(f, pointKey,
                        ImmutableObservableList.set(list, pointIndex, bn.setC1(p).setC2(p2)));

            } else {
                view.getModel().set(f, pointKey,
                        ImmutableObservableList.set(list, pointIndex, bn.setC2(p).setC1(p2)));
            }
        }
    }

    @Override
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
    }

    @Override
    public void handleMouseClicked(MouseEvent event, DrawingView dv) {

    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public Point2D getLocationInView() {
        return pickLocation;
    }
}