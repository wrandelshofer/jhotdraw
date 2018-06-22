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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.BezierNodePath;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

/**
 * Handle for the point ofCollection a figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierNodeEditHandle extends AbstractHandle {

    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    @Nullable
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Circle REGION_SHAPE_CUBIC = new Circle(0, 0, 4);
    private static final Rectangle REGION_SHAPE_LINEAR = new Rectangle(7, 7);
    private static final Path REGION_SHAPE_QUADRATIC = new Path();

    static {
        final ObservableList<PathElement> elements = REGION_SHAPE_QUADRATIC.getElements();
        elements.add(new MoveTo(0, 0));
        elements.add(new LineTo(4, -4));
        elements.add(new LineTo(8, 0));
        elements.add(new LineTo(4, 4));
        elements.add(new ClosePath());
    }
    @NonNull
    private final Region node;
    private Point2D pickLocation;
    private final int pointIndex;
    private final MapAccessor<ImmutableList<BezierNode>> pointKey;
    private final String styleclass;

    public BezierNodeEditHandle(Figure figure, MapAccessor<ImmutableList<BezierNode>> pointKey, int pointIndex) {
        this(figure, pointKey, pointIndex, STYLECLASS_HANDLE_POINT);
    }

    public BezierNodeEditHandle(Figure figure, MapAccessor<ImmutableList<BezierNode>> pointKey, int pointIndex, String styleclass) {
        super(figure);
        this.pointKey = pointKey;
        this.pointIndex = pointIndex;
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(REGION_SHAPE_LINEAR);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11);
        node.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public boolean contains(DrawingView drawingView, double x, double y, double tolerance) {
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
        return getBezierNode().getC0();

    }

    public Point2D getLocationInView() {
        return pickLocation;
    }

    @NonNull
    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void handleMouseClicked(@NonNull MouseEvent event, @NonNull DrawingView dv) {
        if (pointKey != null && event.getClickCount() == 2) {
            if (owner.get(pointKey).size() > 2) {
                BezierNodePath path = new BezierNodePath(owner.get(pointKey));
                path.join(pointIndex, 1.0);
                dv.getModel().set(owner, pointKey, ImmutableList.ofCollection(path.getNodes()));
                dv.recreateHandles();
            }
        }
    }

    @Override
    public void handleMouseDragged(@NonNull MouseEvent event, @NonNull DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control switches the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
        }

        ImmutableList<BezierNode> list = owner.get(pointKey);
        if (pointIndex >= list.size()) {
            return;
        }
        BezierNode p = list.get(pointIndex);
        view.getModel().set(getOwner(), pointKey,
                ImmutableList.set(list, pointIndex, p.setC0AndTranslateC1C2(getOwner().worldToLocal(newPoint))));
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
    public void updateNode(@NonNull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        ImmutableList<BezierNode> list = f.get(pointKey);
        if (pointIndex >= list.size()) {
            return;
        }
        BezierNode p = getBezierNode();
        Point2D c0 = getLocation();
        pickLocation = c0 = t == null ? c0 : t.transform(c0);
        node.relocate(c0.getX() - 5, c0.getY() - 5);
        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));

        BezierNode bn = getBezierNode();
        if (bn.isC1() && bn.isC2()) {
            node.setShape(REGION_SHAPE_CUBIC);// FIXME this is not correct
        } else if (bn.isC1()) {
            node.setShape(REGION_SHAPE_QUADRATIC);// FIXME this is not correct
        } else {
            node.setShape(REGION_SHAPE_LINEAR);
        }
    }

}
