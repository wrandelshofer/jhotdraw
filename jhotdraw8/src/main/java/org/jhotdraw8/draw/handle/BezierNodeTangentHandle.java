/* @(#)BezierNodeEditHandle.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

/**
 * Handle for the point ofCollection a figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierNodeTangentHandle extends AbstractHandle {

    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    @Nullable
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    @Nonnull
    private final Polyline node;

    private Point2D pickLocation;
    private final int pointIndex;
    private final MapAccessor<ImmutableList<BezierNode>> pointKey;
    private final String styleclass;

    public BezierNodeTangentHandle(Figure figure, MapAccessor<ImmutableList<BezierNode>> pointKey, int pointIndex) {
        this(figure, pointKey, pointIndex, STYLECLASS_HANDLE_CONTROL_POINT_OUTLINE);
    }

    public BezierNodeTangentHandle(Figure figure, MapAccessor<ImmutableList<BezierNode>> pointKey, int pointIndex, String styleclass) {
        super(figure);
        this.pointKey = pointKey;
        this.pointIndex = pointIndex;
        this.styleclass = styleclass;
        node = new Polyline();
        node.setManaged(false);

        node.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
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

    @Nullable
    @Override
    public Cursor getCursor() {
        return null;
    }

    private Point2D getLocation() {
        return getBezierNode().getC0();

    }

    public Point2D getLocationInView() {
        return pickLocation;
    }

    @Nonnull
    @Override
    public Polyline getNode() {
        return node;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        ImmutableList<BezierNode> list = f.get(pointKey);
        if (pointIndex >= list.size()) {
            return;
        }
        BezierNode bn = getBezierNode();
        BezierNode prev = list.get((pointIndex + list.size() - 1) % list.size());
        BezierNode next = list.get((pointIndex + 1) % list.size());
        Point2D c0 = Transforms.transform(t, bn.getC0());
        Point2D c1 = Transforms.transform(t, bn.getC1());
        Point2D c2 = Transforms.transform(t, bn.getC2());

        Polyline node = getNode();
        List<Double> points = node.getPoints();
        points.clear();
        {
            if (bn.isC1()) {
                points.add(c1.getX());
                points.add(c1.getY());
                points.add(c0.getX());
                points.add(c0.getY());
            } else if (prev.isC2()) {
                /*
                c1 = Transforms.transform(t, prev.getC2());

                points.add(c1.getX());
                points.add(c1.getY());
                points.add(c0.getX());
                points.add(c0.getY());*/
            }
            if (bn.isC2()) {
                if (points.isEmpty()) {
                    points.add(c0.getX());
                    points.add(c0.getY());
                }
                points.add(c2.getX());
                points.add(c2.getY());
            } else if (next.isC1()) {
                /*
                c2 = Transforms.transform(t, next.getC1());

                if (points.isEmpty()) {
                    points.add(c0.getX());
                    points.add(c0.getY());
                }
                points.add(c2.getX());
                points.add(c2.getY());
*/

            }
        }

    }

}
