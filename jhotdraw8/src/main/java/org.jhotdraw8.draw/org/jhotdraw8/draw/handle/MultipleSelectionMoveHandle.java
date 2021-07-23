/*
 * @(#)MultipleSelectionMoveHandle.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.FXGeom;
import org.jhotdraw8.geom.Geom;

/**
 * Handle for moving all selected figures.
 *
 * @author Werner Randelshofer
 */
public class MultipleSelectionMoveHandle extends AbstractHandle {

    private static final @Nullable Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final @Nullable Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Rectangle REGION_SHAPE = new Rectangle(5, 5);
    private @Nullable Point2D locationInDrawing;
    private final @NonNull Region node;
    private Point2D oldPoint;
    private @Nullable Point2D pickLocation;
    private double relativeX;
    private double relativeY;

    public MultipleSelectionMoveHandle(double relativeX, double relativeY) {
        super(null);
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11);

        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        Point2D p = getLocationInView();
        return Geom.squaredDistance(x, y, p.getX(), p.getY()) <= tolerance * tolerance;
    }

    @Override
    public Cursor getCursor() {
        return Cursor.OPEN_HAND;
    }

    private @Nullable Point2D getLocation(@NonNull DrawingView dv) {
        return locationInDrawing == null ? null : dv.worldToView(locationInDrawing);
    }

    public @Nullable Point2D getLocationInView() {
        return pickLocation;
    }

    @Override
    public @NonNull Region getNode(@NonNull DrawingView view) {
        return node;
    }

    @Override
    public void onMouseDragged(@NonNull MouseEvent event, @NonNull DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), new CssPoint2D(newPoint)).getConvertedValue();
        }

        if (event.isMetaDown()) {
            // meta snaps the location of the handle to the grid
            Point2D loc = getLocation(view);
            oldPoint = getOwner().localToWorld(loc);
        }

        if (oldPoint.equals(newPoint)) {
            return;
        }

        //Transform tx = Transform.translate(newPoint.getX() - oldPoint.getX(), newPoint.getY() - oldPoint.getY());
        DrawingModel model = view.getModel();

        // shift transforms all selected figures
        for (Figure f : view.getSelectedFigures()) {
            Point2D npl = f.worldToParent(newPoint);
            Point2D opl = f.worldToParent(oldPoint);
            if (f instanceof TransformableFigure) {
                Transform tt = ((TransformableFigure) f).getInverseTransform();
                npl = tt.transform(npl);
                opl = tt.transform(opl);
            }
            Transform tx = Transform.translate(npl.getX() - opl.getX(), npl.getY() - opl.getY());
            //tx = f.getWorldToParent().createConcatenation(tx);

            model.reshapeInLocal(f, tx);
        }

        oldPoint = newPoint;
    }

    @Override
    public void onMousePressed(@NonNull MouseEvent event, @NonNull DrawingView view) {
        oldPoint = view.getConstrainer().constrainPoint(getOwner(),
                new CssPoint2D(view.viewToWorld(new Point2D(event.getX(), event.getY())))).getConvertedValue();

    }

    @Override
    public void onMouseReleased(@NonNull MouseEvent event, @NonNull DrawingView dv) {
        // FIXME fireDrawingModelEvent undoable edit
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    private void updateLocation(@NonNull DrawingView dv) {
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
        locationInDrawing = b == null ? null : new Point2D(b.getMinX() + relativeX * b.getWidth(), b.getMinY() + relativeY * b.getHeight());
    }

    @Override
    public void updateNode(@NonNull DrawingView view) {
        updateLocation(view);
        Point2D p = getLocation(view);
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        pickLocation = p;

        // The node is centered around the location.
        // (The value 5.5 is half of the node size, which is 11,11.
        // 0.5 is subtracted from 5.5 so that the node snaps between pixels
        // so that we get sharp lines.
        if (p != null) {
            node.relocate(p.getX() - 5, p.getY() - 5);
        }
    }

    public static @NonNull MultipleSelectionMoveHandle northEast() {
        return new MultipleSelectionMoveHandle(1.0, 0.0);
    }

    public static @NonNull MultipleSelectionMoveHandle northWest() {
        return new MultipleSelectionMoveHandle(0.0, 0.0);
    }

    public static @NonNull MultipleSelectionMoveHandle southEast() {
        return new MultipleSelectionMoveHandle(1.0, 1.0);
    }

    public static @NonNull MultipleSelectionMoveHandle southWest() {
        return new MultipleSelectionMoveHandle(0.0, 1.0);
    }
}
