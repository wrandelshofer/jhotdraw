/* @(#)AbstractResizeTransformHandle.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import static org.jhotdraw8.draw.handle.Handle.STYLECLASS_HANDLE_RESIZE;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.model.DrawingModel;

/**
 * AbstractResizeTransformHandle.
 *
 * @author Werner Randelshofer
 * @version $$Id: AbstractResizeTransformHandle.java 1219 2016-12-18 13:24:24Z
 * rawcoder $$
 */
abstract class AbstractResizeTransformHandle extends LocatorHandle {

    private Point2D pickLocation;
    private Point2D oldPoint;
    private final Region node;
    private final String styleclass;
    protected Bounds startBounds;
    private Transform startWorldToLocal;

    /**
     * The height divided by the width.
     */
    protected double preferredAspectRatio;

    public AbstractResizeTransformHandle(Figure owner, String styleclass, Locator locator, Shape shape, Background bg, Border border) {
        super(owner, locator);
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(shape);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11);
        node.getStyleClass().clear();
        node.getStyleClass().add(styleclass);
        node.setBorder(border);
        node.setBackground(bg);
    }

    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = owner;
        Transform t = view.getWorldToView().createConcatenation(f.getLocalToWorld());
        Bounds b = f.getBoundsInLocal();
        Point2D p = getLocation();
        pickLocation = p = t.transform(p);
        node.relocate(p.getX() - 5, p.getY() - 5);
        // rotates the node:
        // f.applyTransformableFigureProperties(node);
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
    }

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
        oldPoint = view.getConstrainer().constrainPoint(owner, view.viewToWorld(new Point2D(event.getX(), event.getY())));
        startBounds = owner.getBoundsInLocal();
        startWorldToLocal = owner.getWorldToLocal();
        preferredAspectRatio = owner.getPreferredAspectRatio();
    }

    @Override
    public void handleMouseDragged(MouseEvent event, DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newPoint = view.getConstrainer().constrainPoint(owner, newPoint);
        }
        if (event.isMetaDown()) {
            // meta snaps the location of the handle to the grid
            Point2D loc = getLocation();
            oldPoint = loc;
        }
        // shift keeps the aspect ratio
        boolean keepAspect = event.isShiftDown();

        Transform t = startWorldToLocal;//owner.getWorldToLocal();

        resize(t.transform(newPoint), owner, startBounds, view.getModel(), keepAspect);
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
    public Point2D getLocationInView() {
        return pickLocation;
    }

    /**
     * Resizes the figure.
     *
     * @param newPoint new point in local coordinates
     * @param owner the figure
     * @param bounds the bounds of the figure on mouse pressed
     * @param model the drawing model
     * @param keepAspect whether the aspect should be preserved. The bounds of
     * the figure on mouse pressed can be used as a reference.
     */
    protected abstract void resize(Point2D newPoint, Figure owner, Bounds bounds, DrawingModel model, boolean keepAspect);
}
