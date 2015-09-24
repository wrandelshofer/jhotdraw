/* @(#)SimpleHiglightHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;

/**
 * SimpleHiglightHandle.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleHighlightHandle extends AbstractHandle {

    private Polygon node;
    private double[] points;

    public SimpleHighlightHandle(Figure figure, DrawingView dv) {
        super(figure, dv);

        points = new double[8];
        node = new Polygon(points);
        node.setFill(null);
        node.setStroke(Color.BLUE);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void updateNode() {
        Bounds b=getFigure().getBoundsInLocal();
        points[0]=b.getMinX();
        points[1]=b.getMinY();
        points[2]=b.getMaxX();
        points[3]=b.getMinY();
        points[4]=b.getMaxX();
        points[5]=b.getMaxY();
        points[6]=b.getMinX();
        points[7]=b.getMaxY();
        
        Transform t = getFigure().getLocalToDrawing();
        t.transform2DPoints(points, 0, points, 0, 4);
        ObservableList<Double> pp = node.getPoints();
        for (int i=0;i<points.length;i++) {
        pp.set(i,points[i]);
        }
    }
}
