/*
 * @(#)AnchorOutlineHandle.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.geom.Geom;

/**
 * Draws the {@code boundsInLocal} of a {@code Figure}, but does not provide any
 * interactions.
 *
 * @author Werner Randelshofer
 */
public class AnchorOutlineHandle extends AbstractHandle {

    private final static double invsqrt2 = 1 / Math.sqrt(2);
    private final double growInView = 8.0;

    private Polygon node;
    private double[] points;

    public AnchorOutlineHandle(Figure figure) {
        super(figure);

        points = new double[8];
        node = new Polygon(points);
        initNode(node);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        return false;
    }

    @Nullable
    @Override
    public Cursor getCursor() {
        return null;
    }

    @Override
    public Node getNode(@NonNull DrawingView view) {
        CssColor color = view.getEditor().getHandleColor();
        node.setStroke(Paintable.getPaint(color));
        node.setStrokeWidth(view.getEditor().getHandleStrokeWidth());
        return node;
    }

    protected void initNode(@NonNull Polygon r) {
        r.setFill(null);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void updateNode(@NonNull DrawingView view) {
        Figure f = getOwner();
        Transform t = FXTransforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Transform tinv = FXTransforms.concat(f.getWorldToLocal(), view.getViewToWorld());
        t = FXTransforms.concat(Transform.translate(0.5, 0.5), t);
        Bounds b = f.getLayoutBounds();
        // FIXME we should perform the grow in view coordinates on the transformed shape
        //            instead of growing in local
        double growInLocal = tinv.deltaTransform(new Point2D(growInView * invsqrt2, growInView * invsqrt2)).magnitude();
        b = Geom.grow(b, growInLocal, growInLocal);
        points[0] = b.getMinX();
        points[1] = b.getMinY();
        points[2] = b.getMaxX();
        points[3] = b.getMinY();
        points[4] = b.getMaxX();
        points[5] = b.getMaxY();
        points[6] = b.getMinX();
        points[7] = b.getMaxY();
        if (t != null && t.isType2D()) {
            t.transform2DPoints(points, 0, points, 0, 4);
        }

        ObservableList<Double> pp = node.getPoints();
        for (int i = 0; i < points.length; i++) {
            pp.set(i, points[i]);
        }
    }

}
