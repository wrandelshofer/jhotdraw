/* @(#)RotateHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

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
import javafx.scene.shape.Circle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.geom.Geom;

/**
 * A Handle to rotate a Figure.
 *
 * @author Werner Randelshofer
 */
public class RotateHandle extends AbstractHandle {

    private Point2D oldPoint;
    private final Region node;
    private final String styleclass;
    private static final Circle REGION_SHAPE = new Circle(4);
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.PURPLE, BorderStrokeStyle.SOLID, null, null));
    private Point2D center;

    public RotateHandle(Figure figure) {
        this(figure, STYLECLASS_HANDLE_ROTATE);
    }

    public RotateHandle(Figure figure, String styleclass) {
        super(figure);
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(10, 10);
        node.getStyleClass().clear();
        node.getStyleClass().add(styleclass);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
        node.setCursor(Cursor.MOVE);
    }

    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = view.getDrawingToView().createConcatenation(f.getLocalToDrawing());
        Bounds b = f.getBoundsInLocal();
        Point2D p = getLocation();
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        p = t.transform(p);
        node.relocate(p.getX() - 5, p.getY() - 5);
        // rotates the node:
        f.applyFigureProperties(node);
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
        oldPoint = view.getConstrainer().constrainPoint(getOwner(), view.viewToDrawing(new Point2D(event.getX(), event.getY())));
        center = getOwner().getCenterInLocal();

    }

    @Override
    public void onMouseDragged(MouseEvent event, DrawingView view) {
        // FIXME implement me!
        Point2D newPoint = view.viewToDrawing(new Point2D(event.getX(), event.getY()));

        double oldRotate = 90 + 180.0 / Math.PI * Geom.angle(center.getX(), center.getY(), oldPoint.getX(), oldPoint.getY());
        double newRotate = 90 + 180.0 / Math.PI * Geom.angle(center.getX(), center.getY(), newPoint.getX(), newPoint.getY());

        double ownerAngle = getOwner().get(Figure.ROTATE);

        newRotate = newRotate % 360;
        if (newRotate < 0) {
            newRotate += 360;
        }

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newRotate = view.getConstrainer().constrainAngle(getOwner(), newRotate);
        }
        if (event.isMetaDown()) {
            // meta snaps the location of the handle to the grid
        }
        view.getModel().set(getOwner(), Figure.ROTATE, newRotate);

        oldPoint = newPoint;
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
        // FIXME fire undoable edit
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    private Point2D getLocation() {
        Figure owner = getOwner();
        Bounds bounds = owner.getBoundsInLocal();
        return new Point2D(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() - 10);
    }

}
