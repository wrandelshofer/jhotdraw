/* @(#)AbstractResizeTransformHandle.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Transforms;

/**
 * AbstractResizeTransformHandle.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
abstract class AbstractResizeTransformHandle extends LocatorHandle {

    private final Region node;
    private Point2D oldPoint;
    private Point2D pickLocation;
    /**
     * The height divided by the width.
     */
    protected double preferredAspectRatio;
    protected Bounds startBounds;
    private Transform startWorldToLocal;
    private final String styleclass;

    public AbstractResizeTransformHandle(Figure owner, String styleclass, Locator locator, Shape shape, Background bg, Border border) {
        super(owner, locator);
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(shape);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11);

        node.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
        node.setBorder(border);
        node.setBackground(bg);
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }

    @Override
    public Region getNode() {
        return node;
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

        resize(t == null ? newPoint : t.transform(newPoint), owner, startBounds, view.getModel(), keepAspect);
    }

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
        oldPoint = view.getConstrainer().constrainPoint(owner, view.viewToWorld(new Point2D(event.getX(), event.getY())));
        startBounds = owner.getBoundsInLocal();
        startWorldToLocal = owner.getWorldToLocal();
        preferredAspectRatio = owner.getPreferredAspectRatio();
    }

    @Override
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
        // FIXME fire undoable edit
    }

    @Override
    public boolean isSelectable() {
        return true;
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

    @Override
    public void updateNode(DrawingView view) {
        Figure f = owner;
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = f.getBoundsInLocal();
        Point2D p = getLocation();
        pickLocation = p = t == null ? p : t.transform(p);
        node.relocate(p.getX() - 5, p.getY() - 5);
        // rotates the node:
        // f.applyTransformableFigureProperties(node);
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
    }
}
