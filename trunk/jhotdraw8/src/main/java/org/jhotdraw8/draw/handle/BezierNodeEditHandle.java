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
public class BezierNodeEditHandle extends AbstractHandle {

    private Point2D pickLocation;
    private final MapAccessor<ImmutableObservableList<BezierNode>> pointKey;
    private final int pointIndex;
    private final Region node;
    private final String styleclass;
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
    private static final Circle REGION_SHAPE_CUBIC = new Circle(0, 0, 4);
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));

    public BezierNodeEditHandle(Figure figure, MapAccessor<ImmutableObservableList<BezierNode>> pointKey, int pointIndex) {
        this(figure, pointKey, pointIndex, STYLECLASS_HANDLE_POINT);
    }

    public BezierNodeEditHandle(Figure figure, MapAccessor<ImmutableObservableList<BezierNode>> pointKey, int pointIndex, String styleclass) {
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
        return getBezierNode().getC0();

    }


    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t =Transforms.concat( view.getWorldToView(),f.getLocalToWorld());
        ImmutableObservableList<BezierNode> list = f.get(pointKey);
        if (pointIndex>=list.size()) return;
        BezierNode p =getBezierNode();
        Point2D c0=getLocation();
        pickLocation = c0 = t==null?c0:t.transform(c0);
        node.relocate(c0.getX() - 5, c0.getY() - 5);
        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
        
        BezierNode bn = getBezierNode();
        if ((bn.mask & BezierNode.C1C2_MASK) != 0) {
            node.setShape(REGION_SHAPE_CUBIC);
        } else if ((bn.mask & BezierNode.C1_MASK) != 0) {
            node.setShape(REGION_SHAPE_QUADRATIC);
        } else {
            node.setShape(REGION_SHAPE_LINEAR);
        } 
    }

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
    }

    @Override
    public void handleMouseDragged(MouseEvent event, DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control switches the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
        }
        
        ImmutableObservableList<BezierNode> list = owner.get(pointKey);
        if (pointIndex>=list.size()) return;
          BezierNode p = list.get(pointIndex);
        view.getModel().set(getOwner(), pointKey,
                ImmutableObservableList.set(list, pointIndex,p.setC0AndTranslateC1C2(getOwner().worldToLocal(newPoint))));
    }

    @Override
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
    }

    @Override
    public void handleMouseClicked(MouseEvent event, DrawingView dv) {
        if (pointKey != null && event.getClickCount() == 2) {
            if (owner.get(pointKey).size() > 2) {
                dv.getModel().set(owner, pointKey, ImmutableObservableList.remove(owner.get(pointKey), pointIndex));
                dv.recreateHandles();
            }
        }
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
