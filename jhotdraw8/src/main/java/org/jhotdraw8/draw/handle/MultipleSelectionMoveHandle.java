/* @(#)ConnectionFigureConnectionHandle.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Geom;

/**
 * Handle for moving all selected figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MultipleSelectionMoveHandle extends AbstractHandle {

    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    @Nullable
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Rectangle REGION_SHAPE = new Rectangle(5, 5);
    @Nullable
    private Point2D locationInDrawing;
    @Nonnull
    private final Region node;
    private Point2D oldPoint;
    @Nullable
    private Point2D pickLocation;
    private double relativeX;
    private double relativeY;
    private final String styleclass;

    public MultipleSelectionMoveHandle(double relativeX, double relativeY) {
        this(relativeX, relativeY, STYLECLASS_HANDLE_MULTI_MOVE);
    }

    public MultipleSelectionMoveHandle(double relativeX, double relativeY, String styleclass) {
        super(null);
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11);

        node.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        Point2D p = getLocationInView();
        return Geom.length2(x, y, p.getX(), p.getY()) <= tolerance;
    }

    @Override
    public Cursor getCursor() {
        return Cursor.OPEN_HAND;
    }

    private Point2D getLocation(@Nonnull DrawingView dv) {
        return locationInDrawing == null ? null : dv.worldToView(locationInDrawing);
    }

    @Nullable
    public Point2D getLocationInView() {
        return pickLocation;
    }

    @Nonnull
    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void handleMouseDragged(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(),new CssPoint2D( newPoint)).getConvertedValue();
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
    public void handleMousePressed(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        oldPoint = view.getConstrainer().constrainPoint(getOwner(),
                new CssPoint2D(view.viewToWorld(new Point2D(event.getX(), event.getY())))).getConvertedValue();

    }

    @Override
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
        // FIXME fireDrawingModelEvent undoable edit
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    private void updateLocation(DrawingView dv) {
        Bounds b = null;
        for (Figure f : dv.getSelectedFigures()) {
            Transform l2w = f.getLocalToWorld();
            Bounds fb = l2w.transform(f.getBoundsInLocal());
            if (b == null) {
                b = fb;
            } else {
                b = Geom.add(b, fb);
            }
        }
        locationInDrawing = b == null ? null : new Point2D(b.getMinX() + relativeX * b.getWidth(), b.getMinY() + relativeY * b.getHeight());
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
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

    public static MultipleSelectionMoveHandle northEast() {
        return new MultipleSelectionMoveHandle(1.0, 0.0);
    }

    public static MultipleSelectionMoveHandle northWest() {
        return new MultipleSelectionMoveHandle(0.0, 0.0);
    }

    public static MultipleSelectionMoveHandle southEast() {
        return new MultipleSelectionMoveHandle(1.0, 1.0);
    }

    public static MultipleSelectionMoveHandle southWest() {
        return new MultipleSelectionMoveHandle(0.0, 1.0);
    }
}
