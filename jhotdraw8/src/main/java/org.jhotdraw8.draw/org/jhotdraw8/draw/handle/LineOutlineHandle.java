/*
 * @(#)LineOutlineHandle.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.LineConnectionFigure;
import org.jhotdraw8.geom.Transforms;

/**
 * Draws the {@code wireframe} of a {@code LineFigure}, but does not provide any
 * interactions.
 *
 * @author Werner Randelshofer
 */
public class LineOutlineHandle extends AbstractHandle {
    private Group node;
    private Polyline polyline2;
    private Polyline polyline1;
    private double[] points;

    public LineOutlineHandle(Figure figure) {
        super(figure);
        node = new Group();
        points = new double[4];
        polyline1 = new Polyline(points);
        polyline2 = new Polyline(points);
        node.getChildren().addAll(polyline1, polyline2);
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
        polyline1.setStroke(Color.WHITE);
        polyline2.setStroke(Paintable.getPaint(color));
        int strokeWidth = view.getEditor().getHandleStrokeWidth();
        polyline1.setStrokeWidth(strokeWidth + 2);
        polyline2.setStrokeWidth(strokeWidth);
        return node;
    }



    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void updateNode(@NonNull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = getOwner().getLayoutBounds();
        points[0] = f.getNonNull(LineConnectionFigure.START).getX().getConvertedValue();
        points[1] = f.getNonNull(LineConnectionFigure.START).getY().getConvertedValue();
        points[2] = f.getNonNull(LineConnectionFigure.END).getX().getConvertedValue();
        points[3] = f.getNonNull(LineConnectionFigure.END).getY().getConvertedValue();

        t.transform2DPoints(points, 0, points, 0, 2);
        ObservableList<Double> pp1 = polyline1.getPoints();
        ObservableList<Double> pp2 = polyline2.getPoints();
        for (int i = 0; i < points.length; i++) {
            pp1.set(i, points[i]);
            pp2.set(i, points[i]);
        }
    }

}
