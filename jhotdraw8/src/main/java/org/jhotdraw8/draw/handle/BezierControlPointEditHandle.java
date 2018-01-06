/* @(#)BezierNodeEditHandle.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
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
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

/**
 * Handle for the point ofCollection a figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierControlPointEditHandle extends AbstractHandle {

    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Path REGION_SHAPE_COLINEAR = new Path();
    private static final Rectangle REGION_SHAPE_CUSP = new Rectangle(5, 5);
    private static final Path REGION_SHAPE_EQUIDISTANT = new Path();
    private static final Circle REGION_SHAPE_SMOOTH = new Circle(0, 0, 3);

    static {
        final ObservableList<PathElement> elements = REGION_SHAPE_COLINEAR.getElements();
        elements.add(new MoveTo(2, 0));
        elements.add(new LineTo(4, 0));
        elements.add(new LineTo(6, 2));
        elements.add(new LineTo(6, 4));
        elements.add(new LineTo(4, 6));
        elements.add(new LineTo(2, 6));
        elements.add(new LineTo(0, 4));
        elements.add(new LineTo(0, 2));
        elements.add(new ClosePath());
    }

    static {
        final ObservableList<PathElement> elements = REGION_SHAPE_EQUIDISTANT.getElements();
        elements.add(new MoveTo(0, 0));
        elements.add(new LineTo(4, -4));
        elements.add(new LineTo(8, 0));
        elements.add(new LineTo(4, 4));
        elements.add(new ClosePath());
    }
    private int controlPointMask;
    private final Region node;
    private Point2D pickLocation;
    private final int pointIndex;
    private final MapAccessor<ImmutableList<BezierNode>> pointKey;
    private final String styleclass;

    public BezierControlPointEditHandle(Figure figure, MapAccessor<ImmutableList<BezierNode>> pointKey, int pointIndex, int controlPointMask) {
        this(figure, pointKey, pointIndex, controlPointMask, STYLECLASS_HANDLE_CONTROL_POINT);
    }

    public BezierControlPointEditHandle(Figure figure, MapAccessor<ImmutableList<BezierNode>> pointKey, int pointIndex, int controlPointMask, String styleclass) {
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

        node.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        Point2D p = getLocationInView();
        return Geom.length2(x, y, p.getX(), p.getY()) <= tolerance;
    }

    private BezierNode getBezierNode() {
        ImmutableList<BezierNode> list = owner.get(pointKey);
        return list.get(pointIndex);

    }

    @Override
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    private Point2D getLocation() {
        return getBezierNode().getC(controlPointMask);

    }

    public Point2D getLocationInView() {
        return pickLocation;
    }

    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void handleMouseClicked(MouseEvent event, DrawingView dv) {
        if (pointKey != null && event.getClickCount() == 2) {
            ImmutableList<BezierNode> list = owner.get(pointKey);
            BezierNode bn = list.get(pointIndex);

            dv.getModel().set(owner, pointKey,
                    ImmutableList.set(list, pointIndex, bn.setColinear(!bn.isColinear())));
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent event, DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));
        final Figure f = getOwner();

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control switches the constrainer off
            newPoint = view.getConstrainer().constrainPoint(f, newPoint);
        }

        ImmutableList<BezierNode> list = owner.get(pointKey);
        if (pointIndex >= list.size()) {
            return;
        }
        BezierNode bn = list.get(pointIndex);
        Point2D p = f.worldToLocal(newPoint);

        if (!bn.isColinear()) {
            // move control point independently
            BezierNode newBezierNode = bn.setC(controlPointMask, p);
            view.getModel().set(f, pointKey,
                    ImmutableList.set(list, pointIndex, newBezierNode));

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
            BezierNode newBezierNode;
            if (controlPointMask == BezierNode.C1_MASK) {
                newBezierNode = bn.setC1(p).setC2(p2);

            } else {
                newBezierNode = bn.setC2(p).setC1(p2);
            }
            view.getModel().set(f, pointKey,
                    ImmutableList.set(list, pointIndex, newBezierNode));
        }
    }

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
    }

    @Override
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        ImmutableList<BezierNode> list = f.get(pointKey);
        if (pointIndex >= list.size()) {
            node.setVisible(false);
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

        node.setVisible(bn.isC(controlPointMask));

    }

}
