/* @(#)BoundsInLocalHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.SimpleDrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.PolygonFigure;
import static org.jhotdraw8.draw.handle.Handle.STYLECLASS_HANDLE_MOVE_OUTLINE;
import org.jhotdraw8.draw.key.Point2DListStyleableFigureKey;
import org.jhotdraw8.geom.Geom;

/**
 * Draws the {@code wireframe} of a {@code PolygonFigure}.
 * <p>
 * The user can insert a new point by double clicking the line.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PolygonOutlineHandle extends AbstractHandle {

    private Polygon node;
    private String styleclass;
    private final MapAccessor<ImmutableObservableList<Point2D>> key;

    public PolygonOutlineHandle(Figure figure, MapAccessor<ImmutableObservableList<Point2D>> key) {
        this(figure, key, STYLECLASS_HANDLE_MOVE_OUTLINE);
    }

    public PolygonOutlineHandle(Figure figure, MapAccessor<ImmutableObservableList<Point2D>> key, String styleclass) {
        super(figure);
        this.key = key;
        node = new Polygon();
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

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = view.getWorldToView().createConcatenation(f.getLocalToWorld());
        Bounds b = getOwner().getBoundsInLocal();
        double[] points = PolygonFigure.toPointArray(f);
        t.transform2DPoints(points, 0, points, 0, points.length / 2);
        ObservableList<Double> pp = node.getPoints();
        pp.clear();
        for (int i = 0; i < points.length; i++) {
            pp.add(i, points[i]);
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

    @Override
    public void handleMouseClicked(MouseEvent event, DrawingView dv) {
        if (key!=null && event.getClickCount() == 2) {
            double px = event.getX();
            double py = event.getY();
            double tolerance = SimpleDrawingView.TOLERANCE;
            List<Double> points = node.getPoints();
            int insertAt = -1;
            for (int i = 0, n = points.size(); i < n; i += 2) {
                double x1 = points.get((n+i - 2)%n);
                double y1 = points.get((n+i - 1)%n);
                double x2 = points.get(i);
                double y2 = points.get(i + 1);
                if (Geom.lineContainsPoint(x1, y1, x2, y2, px, py, tolerance)) {
                    insertAt = i / 2;
                    break;
                }
            }
            if (insertAt != -1) {
                Point2D pInDrawing = dv.viewToWorld(new Point2D(px, py));
                pInDrawing = dv.getConstrainer().constrainPoint(owner, pInDrawing);
                Point2D pInLocal = owner.worldToLocal(pInDrawing);
                dv.getModel().set(owner, key, ImmutableObservableList.add(owner.get(key), insertAt, pInLocal));
                dv.recreateHandles();
            }
        }
    }
}
