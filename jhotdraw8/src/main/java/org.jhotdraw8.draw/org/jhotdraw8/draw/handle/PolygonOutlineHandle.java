/*
 * @(#)PolygonOutlineHandle.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.PolylineFigure;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Intersection;
import org.jhotdraw8.geom.Intersections;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;

import java.awt.geom.PathIterator;

/**
 * Draws the {@code wireframe} ofCollection a {@code PolygonFigure}.
 * <p>
 * The user can insert a new point by double clicking the line.
 *
 * @author Werner Randelshofer
 */
public class PolygonOutlineHandle extends AbstractHandle {

    private boolean editable;
    private final NonNullMapAccessor<ImmutableList<Point2D>> key;

    private Group node;
    private Polygon poly1;
    private Polygon poly2;

    public PolygonOutlineHandle(Figure figure, NonNullMapAccessor<ImmutableList<Point2D>> key) {
        this(figure, key, true);
    }

    public PolygonOutlineHandle(Figure figure, NonNullMapAccessor<ImmutableList<Point2D>> key, boolean editable) {
        super(figure);
        this.key = key;
        this.editable = editable;
        node = new Group();
        poly1 = new Polygon();
        poly2 = new Polygon();
        poly1.setFill(null);
        poly2.setFill(null);
        node.getChildren().addAll(poly1, poly2);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        if (Geom.contains(poly2.getBoundsInParent(), x, y, tolerance)) {
            Intersection i = Intersections.intersectPathIteratorPoint(
                    Shapes.pathIteratorFromPointCoords(poly2.getPoints(), true, PathIterator.WIND_EVEN_ODD, null),
                    x, y, tolerance);
            return i.getStatus() == Intersection.Status.INTERSECTION;
        }
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
        poly1.setStroke(Color.WHITE);
        poly2.setStroke(Paintable.getPaint(color));
        int strokeWidth = view.getEditor().getHandleStrokeWidth();
        poly1.setStrokeWidth(strokeWidth + 2);
        poly2.setStrokeWidth(strokeWidth);
        return node;
    }

    @Override
    public void onMousePressed(@NonNull MouseEvent event, @NonNull DrawingView dv) {
        if (editable && event.isPopupTrigger()) {
            onPopupTriggered(event, dv);
        }
    }

    @Override
    public void onMouseReleased(@NonNull MouseEvent event, @NonNull DrawingView dv) {
        if (editable && event.isPopupTrigger()) {
            onPopupTriggered(event, dv);
        }
    }

    protected void onPopupTriggered(@NonNull MouseEvent event, @NonNull DrawingView dv) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addPoint = new MenuItem(DrawLabels.getResources().getString("handle.addPoint.text"));
        addPoint.setOnAction(actionEvent -> addPoint(event, dv));
        contextMenu.getItems().add(addPoint);
        contextMenu.show(node, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    @Override
    public void onMouseClicked(@NonNull MouseEvent event, @NonNull DrawingView dv) {
        if (editable && key != null && event.getClickCount() == 2) {
            addPoint(event, dv);
        }
    }

    private void addPoint(@NonNull MouseEvent event, @NonNull DrawingView dv) {
        ImmutableList<Point2D> points = owner.get(key);
        Point2D pInDrawing = dv.viewToWorld(new Point2D(event.getX(), event.getY()));
        Point2D pInLocal = owner.worldToLocal(pInDrawing);

        double tolerance = Transforms.deltaTransform(owner.getWorldToLocal(), Transforms.deltaTransform(dv.getViewToWorld(),
                dv.getEditor().getTolerance(), dv.getEditor().getTolerance())).getX();
        double px = pInLocal.getX();
        double py = pInLocal.getY();

        int insertAt = -1;
        Point2D insertLocation = null;
        for (int i = 0, n = points.size(); i < n; i++) {
            Point2D p1 = points.get((n + i - 1) % n);
            Point2D p2 = points.get(i);

            Intersection result = Intersections.intersectLineCircle(p1.getX(), p1.getY(), p2.getX(), p2.getY(), px, py, tolerance);
            if (result.getTs().size() == 2) {
                insertLocation = Geom.lerp(p1, p2, (result.getFirstT() + result.getLastT()) / 2);
                insertAt = i;
                break;
            }
        }
        if (insertAt != -1) {
            dv.getModel().set(owner, key, ImmutableLists.add(owner.get(key), insertAt, insertLocation));
            dv.recreateHandles();
        }
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(@NonNull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = getOwner().getLayoutBounds();
        double[] points = PolylineFigure.toPointArray(f, key);
        if (t != null) {
            t.transform2DPoints(points, 0, points, 0, points.length / 2);
        }
        ObservableList<Double> pp1 = poly1.getPoints();
        ObservableList<Double> pp2 = poly2.getPoints();
        pp2.clear();
        pp1.clear();
        for (int i = 0; i < points.length; i++) {
            pp1.add(points[i]);
            pp2.add(points[i]);
        }
    }
}
