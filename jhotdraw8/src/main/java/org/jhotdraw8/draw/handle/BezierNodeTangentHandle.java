/* @(#)BezierNodeEditHandle.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.Transforms;

/**
 * Handle for the point of a figure.
 *
 * @author Werner Randelshofer
 */
public class BezierNodeTangentHandle extends AbstractHandle {

    private Point2D pickLocation;
    private final MapAccessor<ImmutableObservableList<BezierNode>> pointKey;
    private final int pointIndex;
    private final Polyline node;
    private final String styleclass;

    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));

    public BezierNodeTangentHandle(Figure figure, MapAccessor<ImmutableObservableList<BezierNode>> pointKey, int pointIndex) {
        this(figure, pointKey, pointIndex, STYLECLASS_HANDLE_CONTROL_POINT_OUTLINE);
    }

    public BezierNodeTangentHandle(Figure figure, MapAccessor<ImmutableObservableList<BezierNode>> pointKey, int pointIndex, String styleclass) {
        super(figure);
        this.pointKey = pointKey;
        this.pointIndex = pointIndex;
        this.styleclass = styleclass;
        node = new Polyline();
        node.setManaged(false);
        
        node.getStyleClass().addAll(styleclass,STYLECLASS_HANDLE);
    }

    @Override
    public Cursor getCursor() {
        return null;
    }

    @Override
    public Polyline getNode() {
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
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        ImmutableObservableList<BezierNode> list = f.get(pointKey);
        if (pointIndex >= list.size()) {
            return;
        }
        BezierNode bn = getBezierNode();
        Point2D c0 = Transforms.transform(t, bn.getC0());
        Point2D c1 = Transforms.transform(t, bn.getC1());
        Point2D c2 = Transforms.transform(t, bn.getC2());

        Polyline node = getNode();
        List<Double> points = node.getPoints();
        points.clear();
        if (!bn.isMove()) {
            if ((bn.mask & BezierNode.C1_MASK) != 0) {
                points.add(c1.getX());
                points.add(c1.getY());
                points.add(c0.getX());
                points.add(c0.getY());
            }
            if ((bn.mask & BezierNode.C2_MASK) != 0) {
                if (points.isEmpty()) {
                    points.add(c0.getX());
                    points.add(c0.getY());
                }
                points.add(c2.getX());
                points.add(c2.getY());
            }
        }

    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public Point2D getLocationInView() {
        return pickLocation;
    }
}
