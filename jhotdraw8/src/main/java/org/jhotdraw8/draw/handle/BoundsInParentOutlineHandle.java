/* @(#)BoundsInParentOutlineHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.Transforms;

/**
 * Draws the {@code boundsInLocal} of a {@code Figure}, but does not provide any
 * interactions.
 *
 * @author Werner Randelshofer
 * @version $Id: BoundsInParentOutlineHandle.java 1149 2016-11-18 11:00:10Z
 * rawcoder $
 */
public class BoundsInParentOutlineHandle extends AbstractHandle {

    private Polygon node;
    private double[] points;
    private String styleclass;

    public BoundsInParentOutlineHandle(Figure figure) {
        this(figure, STYLECLASS_HANDLE_SELECT_OUTLINE);
    }

    public BoundsInParentOutlineHandle(Figure figure, String styleclass) {
        super(figure);

        points = new double[8];
        node = new Polygon(points);
        this.styleclass = styleclass;
        initNode(node);
    }

    protected void initNode(Polygon r) {
        r.setFill(null);
        r.setStroke(Color.BLUE);
        r.getStyleClass().addAll(styleclass,STYLECLASS_HANDLE);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getParentToWorld());

        Bounds b = f.getBoundsInParent();
        points[0] = b.getMinX();
        points[1] = b.getMinY();
        points[2] = b.getMaxX();
        points[3] = b.getMinY();
        points[4] = b.getMaxX();
        points[5] = b.getMaxY();
        points[6] = b.getMinX();
        points[7] = b.getMaxY();
        if (t != null) {
            t.transform2DPoints(points, 0, points, 0, 4);
        }

        ObservableList<Double> pp = node.getPoints();
        for (int i = 0; i < points.length; i++) {
            pp.set(i, points[i]);
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
