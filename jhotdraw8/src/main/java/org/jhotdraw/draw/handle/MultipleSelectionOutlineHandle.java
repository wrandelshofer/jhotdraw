/* @(#)MultipleSelectionOutlineHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.geom.Geom;

/**
 * Draws the {@code boundsInLocal} of all selected figurs, but does not provide any
 * interactions.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MultipleSelectionOutlineHandle extends AbstractHandle {

    private Polygon node;
    private double[] points;
    private String styleclass;

    public MultipleSelectionOutlineHandle() {
        this(STYLECLASS_HANDLE_MULTI_SELECT_OUTLINE);
    }
    public MultipleSelectionOutlineHandle(String styleclass) {
        super(null);

        points = new double[8];
        node = new Polygon(points);
        this.styleclass = styleclass;
        initNode(node);
    }

    protected void initNode(Polygon r) {
        r.setFill(null);
        r.setStroke(Color.BLUE);
        r.getStyleClass().add(styleclass);
    }

    @Override
    public Node getNode() {
        return node;
    }
    private Bounds getBounds(DrawingView dv) {
        Bounds b = null;
        for (Figure f:dv.getSelectedFigures()) {
            Transform l2w = f.getLocalToWorld();
            Bounds fb = l2w.transform(f.getBoundsInLocal());
            if (b == null) {
                b = fb;
            } else {
                b = Geom.add(b, fb);
            }
        }
        return b==null?null:dv.getWorldToView().transform(b);
    }

    @Override
    public void updateNode(DrawingView view) {
        Bounds b = getBounds(view);
if (b == null) return;

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
            pp.set(i, Math.round(points[i]+0.5)-0.5);
        }
    }

    @Override
    public boolean isSelectable() {
        return false;
    }
    @Override
    public Cursor getCursor() {
        return null;
    }
    @Override
    public Point2D getLocationInView() {
        return null;
    }
}
