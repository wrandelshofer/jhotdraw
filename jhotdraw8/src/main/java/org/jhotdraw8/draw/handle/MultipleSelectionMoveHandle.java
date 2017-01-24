/* @(#)ConnectionFigureConnectionHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

import java.util.HashSet;
import java.util.Set;
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
import javafx.scene.transform.Translate;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Geom;

/**
 * Handle for moving all selected figures.
 *
 * @author Werner Randelshofer
 */
public class MultipleSelectionMoveHandle extends AbstractHandle {

    private double relativeX;
    private double relativeY;
    private Point2D pickLocation;
    private Point2D oldPoint;
    private final Region node;
    private final String styleclass;
    private static final Rectangle REGION_SHAPE = new Rectangle(5, 5);
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private Point2D locationInDrawing;

    public MultipleSelectionMoveHandle(double relativeX, double relativeY) {
        this(relativeX, relativeY, STYLECLASS_HANDLE_MULTI_MOVE);
    }

    public static MultipleSelectionMoveHandle northWest() {
        return new MultipleSelectionMoveHandle(0.0, 0.0);
    }

    public static MultipleSelectionMoveHandle northEast() {
        return new MultipleSelectionMoveHandle(1.0, 0.0);
    }

    public static MultipleSelectionMoveHandle southWest() {
        return new MultipleSelectionMoveHandle(0.0, 1.0);
    }

    public static MultipleSelectionMoveHandle southEast() {
        return new MultipleSelectionMoveHandle(1.0, 1.0);
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
        
        node.getStyleClass().addAll(styleclass,STYLECLASS_HANDLE);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public Cursor getCursor() {
        return Cursor.OPEN_HAND;
    }

    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
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

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
        oldPoint = view.getConstrainer().constrainPoint(getOwner(), view.viewToWorld(new Point2D(event.getX(), event.getY())));

    }

    @Override
    public void handleMouseDragged(MouseEvent event, DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
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
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
        // FIXME fire undoable edit
    }

    @Override
    public boolean isSelectable() {
        return true;
    }
    @Override
    public boolean contains(double x, double y, double tolerance) {
        Point2D p = getLocationInView();
       return Geom.length2(x, y, p.getX(), p.getY()) <= tolerance;
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }

    private Point2D getLocation(DrawingView dv) {
        return locationInDrawing == null ? null : dv.drawingToView(locationInDrawing);
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
}
