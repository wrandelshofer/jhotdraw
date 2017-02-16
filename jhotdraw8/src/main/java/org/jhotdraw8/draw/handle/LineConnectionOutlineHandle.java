/* @(#)BoundsInLocalHandle.java
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
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.LineConnectingFigure;
import org.jhotdraw8.geom.Transforms;

/**
 * Draws the {@code wireframe} of a {@code LineFigure}, but does not provide any
 * interactions.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineConnectionOutlineHandle extends AbstractHandle {

    private Polyline node;
    private double[] points;
    private String styleclass;

    public LineConnectionOutlineHandle(LineConnectingFigure figure) {
        this(figure, STYLECLASS_HANDLE_MOVE_OUTLINE);
    }

    public LineConnectionOutlineHandle(LineConnectingFigure figure, String styleclass) {
        super(figure);

        points = new double[4];
        node = new Polyline(points);
        this.styleclass = styleclass;
        initNode(node);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        return false;
    }

    @Override
    public Cursor getCursor() {
        return null;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public LineConnectingFigure getOwner() {
        return (LineConnectingFigure)super.getOwner();
    }

    protected void initNode(Polyline r) {
        r.setFill(null);
        r.setStroke(Color.BLUE);
        r.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void updateNode(DrawingView view) {
        LineConnectingFigure f =getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = f.getBoundsInLocal();
       Point2D start=f.getStartTargetPoint();
       Point2D end=f.getEndTargetPoint();
        points[0] =start.getX();
        points[1] = start.getY();
        points[2] = end.getX();
        points[3] =end.getY();

        if (t != null) {
            t.transform2DPoints(points, 0, points, 0, 2);
        }
        ObservableList<Double> pp = node.getPoints();
        for (int i = 0; i < points.length; i++) {
            pp.set(i, points[i]);
        }
    }

}
