/* @(#)BoundsInLocalHandle.java
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
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LineConnectionFigure;

/**
 * Draws the {@code wireframe} of a {@code LineFigure}, but does not
 * provide any interactions.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineWireframeHandle extends AbstractHandle<Figure> {

    private Polyline node;
    private double[] points;
    private String styleclass;

    public LineWireframeHandle(Figure figure, DrawingView dv, String styleclass) {
        super(figure, dv);

        points = new double[4];
        node = new Polyline(points);
        this.styleclass=styleclass;
        initNode(node);
    }
    protected void initNode(Polyline r) {
        r.setFill(null);
        r.setStroke(Color.CYAN);
        r.getStyleClass().add(styleclass);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getFigure();
        Transform t = view.getDrawingToView().createConcatenation(f.getLocalToDrawing());
        Bounds b = getFigure().getBoundsInLocal();
        points[0] = f.get(LineConnectionFigure.START).getX();
        points[1] = f.get(LineConnectionFigure.START).getY();
        points[2] = f.get(LineConnectionFigure.END).getX();
        points[3] = f.get(LineConnectionFigure.END).getY();

        t.transform2DPoints(points, 0, points, 0, 2);
        ObservableList<Double> pp = node.getPoints();
        for (int i = 0; i < points.length; i++) {
            pp.set(i, points[i]);
        }
    }
}
