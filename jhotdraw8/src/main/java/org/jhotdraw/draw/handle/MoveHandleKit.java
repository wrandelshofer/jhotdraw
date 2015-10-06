/* @(#)ConnectionFigureConnectionHandle.java
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
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.locator.Locator;
import org.jhotdraw.draw.locator.RelativeLocator;

/**
 * Handle for moving (translating) a figure.
 *
 * @author Werner Randelshofer
 */
public class MoveHandleKit {

    public static class MoveHandle extends LocatorHandle {

        private Point2D oldPoint;
        private final Region node;
        private final String styleclass;
        private static final Rectangle REGION_SHAPE = new Rectangle(7, 7);
        private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
        private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));

        public MoveHandle(Figure figure, Locator locator) {
            this(figure, STYLECLASS_HANDLE_MOVE, locator);
        }

        public MoveHandle(Figure figure, String styleclass, Locator locator) {
            super(figure, locator);
            this.styleclass = styleclass;
            node = new Region();
            node.setShape(REGION_SHAPE);
            node.setManaged(false);
            node.setScaleShape(false);
            node.setCenterShape(true);
            node.resize(11, 11);
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
        }

        @Override
        public void onMouseDragged(MouseEvent event, DrawingView view) {
            Point2D newPoint = view.viewToDrawing(new Point2D(event.getX(), event.getY()));

            if (!event.isAltDown() && !event.isControlDown()) {
                // alt or control turns the constrainer off
                newPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
            }
            if (event.isMetaDown()) {
                // meta snaps the location of the handle to the grid
                Point2D loc = getLocation();
                oldPoint = getOwner().localToDrawing(loc);
            }

            Transform tx = Transform.translate(newPoint.getX() - oldPoint.getX(), newPoint.getY() - oldPoint.getY());
            if (!tx.isIdentity()) {
                tx = getOwner().getDrawingToParent().createConcatenation(tx);
                view.getModel().reshape(getOwner(), tx);
            }
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
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    static public Handle south(Figure owner) {
        return south(owner, Handle.STYLECLASS_HANDLE_MOVE);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    static public Handle southEast(Figure owner) {
        return southEast(owner, Handle.STYLECLASS_HANDLE_MOVE);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    static public Handle southWest(Figure owner) {
        return southWest(owner, Handle.STYLECLASS_HANDLE_MOVE);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    static public Handle north(Figure owner) {
        return north(owner, Handle.STYLECLASS_HANDLE_MOVE);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    static public Handle northEast(Figure owner) {
        return northEast(owner, Handle.STYLECLASS_HANDLE_MOVE);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    static public Handle northWest(Figure owner) {
        return northWest(owner, Handle.STYLECLASS_HANDLE_MOVE);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    static public Handle east(Figure owner) {
        return northEast(owner, Handle.STYLECLASS_HANDLE_MOVE);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    static public Handle west(Figure owner) {
        return northWest(owner, Handle.STYLECLASS_HANDLE_MOVE);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the styleclass used for styling the JavaFX Node of the
     * handle
     * @return the handle
     */
    static public Handle south(Figure owner, String styleclass) {
        return new MoveHandle(owner, styleclass, RelativeLocator.south());
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the styleclass used for styling the JavaFX Node of the
     * handle
     * @return the handle
     */
    static public Handle southEast(Figure owner, String styleclass) {
        return new MoveHandle(owner, styleclass, RelativeLocator.southEast());
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the styleclass used for styling the JavaFX Node of the
     * handle
     * @return the handle
     */
    static public Handle southWest(Figure owner, String styleclass) {
        return new MoveHandle(owner, styleclass, RelativeLocator.southWest());
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the styleclass used for styling the JavaFX Node of the
     * handle
     * @return the handle
     */
    static public Handle north(Figure owner, String styleclass) {
        return new MoveHandle(owner, styleclass, RelativeLocator.north());
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the styleclass used for styling the JavaFX Node of the
     * handle
     * @return the handle
     */
    static public Handle northEast(Figure owner, String styleclass) {
        return new MoveHandle(owner, styleclass, RelativeLocator.northEast());
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the styleclass used for styling the JavaFX Node of the
     * handle
     * @return the handle
     */
    static public Handle northWest(Figure owner, String styleclass) {
        return new MoveHandle(owner, styleclass, RelativeLocator.northWest());
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the styleclass used for styling the JavaFX Node of the
     * handle
     * @return the handle
     */
    static public Handle east(Figure owner, String styleclass) {
        return new MoveHandle(owner, styleclass, RelativeLocator.east());
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the styleclass used for styling the JavaFX Node of the
     * handle
     * @return the handle
     */
    static public Handle west(
            Figure owner, String styleclass) {
        return new MoveHandle(owner, styleclass, RelativeLocator.west());
    }

}
