/*
 * @(#)MultipleSelectionOutlineHandle.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.FXGeom;

/**
 * Draws the {@code boundsInLocal} of all selected figurs, but does not provide
 * any interactions.
 *
 * @author Werner Randelshofer
 */
public class MultipleSelectionOutlineHandle extends AbstractHandle {

    private Polygon node;
    private double[] points;

    public MultipleSelectionOutlineHandle() {
        super(null);

        points = new double[8];
        node = new Polygon(points);
        initNode(node);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        return false;
    }

    private @Nullable Bounds getBounds(@NonNull DrawingView dv) {
        Bounds b = null;
        for (Figure f : dv.getSelectedFigures()) {
            Transform l2w = f.getLocalToWorld();
            Bounds fb = l2w.transform(f.getLayoutBounds());
            if (b == null) {
                b = fb;
            } else {
                b = FXGeom.add(b, fb);
            }
        }
        return b == null ? null : dv.getWorldToView().transform(b);
    }

    @Override
    public @Nullable Cursor getCursor() {
        return null;
    }

    @Override
    public Node getNode(@NonNull DrawingView view) {
        CssColor color = view.getEditor().getHandleColor();
        node.setStroke(color.getColor());
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
        Bounds b = getBounds(view);
        if (b == null) {
            return;
        }

        points[0] = b.getMinX();
        points[1] = b.getMinY();
        points[2] = b.getMaxX();
        points[3] = b.getMinY();
        points[4] = b.getMaxX();
        points[5] = b.getMaxY();
        points[6] = b.getMinX();
        points[7] = b.getMaxY();
        ObservableList<Double> pp = node.getPoints();
        for (int i = 0; i < points.length; i++) {
            pp.set(i, Math.round(points[i] + 0.5) - 0.5);
        }
    }

}
