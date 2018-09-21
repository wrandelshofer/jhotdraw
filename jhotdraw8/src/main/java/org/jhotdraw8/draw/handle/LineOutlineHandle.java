/* @(#)BoundsInLocalHandle.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.SimpleLineConnectionFigure;
import org.jhotdraw8.geom.Transforms;

/**
 * Draws the {@code wireframe} of a {@code LineFigure}, but does not provide any
 * interactions.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineOutlineHandle extends AbstractHandle {

    private Polyline node;
    private double[] points;
    private String styleclass;

    public LineOutlineHandle(Figure figure) {
        this(figure, STYLECLASS_HANDLE_MOVE_OUTLINE);
    }

    public LineOutlineHandle(Figure figure, String styleclass) {
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

    @Nullable
    @Override
    public Cursor getCursor() {
        return null;
    }

    @Override
    public Node getNode() {
        return node;
    }

    protected void initNode(@Nonnull Polyline r) {
        r.setFill(null);
        r.setStroke(Color.BLUE);
        r.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = getOwner().getBoundsInLocal();
        points[0] = f.get(SimpleLineConnectionFigure.START).getX();
        points[1] = f.get(SimpleLineConnectionFigure.START).getY();
        points[2] = f.get(SimpleLineConnectionFigure.END).getX();
        points[3] = f.get(SimpleLineConnectionFigure.END).getY();

        if (t != null) {
            t.transform2DPoints(points, 0, points, 0, 2);
        }
        ObservableList<Double> pp = node.getPoints();
        for (int i = 0; i < points.length; i++) {
            pp.set(i, points[i]);
        }
    }

}
