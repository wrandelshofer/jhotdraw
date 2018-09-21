/* @(#)ResizeHandleKit.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import java.util.Collection;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.model.DrawingModel;
import static java.lang.Math.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

/**
 * /**
 * A set of utility methods to create handles which resize a Figure by using its
 * {@code reshapeInLocal} method, if the Figure is transformable.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ResizeHandleKit {

    /**
     * Prevent instance creation.
     */
    private ResizeHandleKit() {

    }

    /**
     * Creates handles for each corner of a figure and adds them to the provided
     * collection.
     *
     * @param f the figure which will own the handles
     * @param handles the list to which the handles should be added
     * @param styleclass the style class that should be assigned to the handles
     */
    static public void addCornerResizeHandles(Figure f, Collection<Handle> handles, String styleclass) {
        handles.add(southEast(f, styleclass));
        handles.add(southWest(f, styleclass));
        handles.add(northEast(f, styleclass));
        handles.add(northWest(f, styleclass));
    }

    /**
     * Fills the given collection with handles at each the north, south, east,
     * and west of the figure.
     *
     * @param f the figure which will own the handles
     * @param handles the list to which the handles should be added
     * @param styleclass the style class that should be assigned to the handles
     */
    static public void addEdgeResizeHandles(Figure f, Collection<Handle> handles, String styleclass) {
        handles.add(south(f, styleclass));
        handles.add(north(f, styleclass));
        handles.add(east(f, styleclass));
        handles.add(west(f, styleclass));
    }

    /**
     * Fills the given collection with handles at each the north, south, east,
     * and west of the figure.
     *
     * @param f the figure which will own the handles
     * @param handles the list to which the handles should be added
     * @param styleclass the style class that should be assigned to the handles
     */
    static public void addResizeHandles(Figure f, @Nonnull Collection<Handle> handles, String styleclass) {
        addCornerResizeHandles(f, handles, styleclass);
        addEdgeResizeHandles(f, handles, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle south(Figure owner, String styleclass) {
        return new SouthHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle southEast(Figure owner, String styleclass) {
        return new SouthEastHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle southWest(Figure owner, String styleclass) {
        return new SouthWestHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle north(Figure owner, String styleclass) {
        return new NorthHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle northEast(Figure owner, String styleclass) {
        return new NorthEastHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle northWest(Figure owner, String styleclass) {
        return new NorthWestHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle east(Figure owner, String styleclass) {
        return new EastHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle west(Figure owner, String styleclass) {
        return new WestHandle(owner, styleclass);
    }
    protected static final Shape NORTH_SHAPE = new Rectangle(9, 5);
    protected static final Shape EAST_SHAPE = new Rectangle(5, 9);
    protected static final Shape WEST_SHAPE = new Rectangle(5, 9);
    protected static final Shape SOUTH_SHAPE = new Rectangle(9, 5);
    protected static final SVGPath NORTH_EAST_SHAPE = new SVGPath();
    protected static final SVGPath NORTH_WEST_SHAPE = new SVGPath();
    protected static final SVGPath SOUTH_EAST_SHAPE = new SVGPath();
    protected static final SVGPath SOUTH_WEST_SHAPE = new SVGPath();

    static {
        NORTH_EAST_SHAPE.setContent("M -5.5,-2.5 L 2.5,-2.5 2.5,5.5 -2.5,5.5 -2.5,2.5 -5.5,2.5 Z M 5.5,-5.5");
        NORTH_WEST_SHAPE.setContent("M -2.5,-2.5 L 5.5,-2.5 5.5,2.5 2.5,2.5 2.5,5.5 -2.5,5.5 Z M-5.5,-5.5");
        SOUTH_EAST_SHAPE.setContent("M -2.5,-5 L 2.5,-5 2.5,2.5 -5.5,2.5 -5.5,-2.5 -2.5,-2.5 Z M 5.5,5.5");
        SOUTH_WEST_SHAPE.setContent("M -2.5,-5 L 2.5,-5 2.5,-2.5 5.5,-2.5 5.5,2.5 -2.5,2.5 Z M -5.5,5.5");
    }
    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    @Nullable
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, null, null));

    private static class NorthEastHandle extends AbstractResizeTransformHandle {

        NorthEastHandle(Figure owner, String styleclass) {
            super(owner, styleclass, RelativeLocator.NORTH_EAST, NORTH_EAST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.NE_RESIZE;
        }

        @Override
        protected void resize(@Nonnull Point2D newPoint, Figure owner, @Nonnull Bounds bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            double newX = max(bounds.getMinX(), newPoint.getX());
            double newY = min(bounds.getMaxY(), newPoint.getY());
            double newWidth = newX - bounds.getMinX();
            double newHeight = bounds.getMaxY() - newY;
            if (keepAspect) {
                double newRatio = newHeight / newWidth;
                if (newRatio > preferredAspectRatio) {
                    newHeight = newWidth * preferredAspectRatio;
                } else {
                    newWidth = newHeight / preferredAspectRatio;
                }
            }

            model.reshapeInLocal(owner, bounds.getMinX(), bounds.getMaxY() - newHeight, newWidth, newHeight);
        }
    }

    private static class EastHandle extends AbstractResizeTransformHandle {

        EastHandle(Figure owner, String styleclass) {
            super(owner, styleclass, RelativeLocator.EAST, EAST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.E_RESIZE;
        }

        @Override
        protected void resize(@Nonnull Point2D newPoint, Figure owner, @Nonnull Bounds bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            double newWidth = max(newPoint.getX(), bounds.getMinX()) - bounds.getMinX();
            double newHeight = bounds.getMaxY() - bounds.getMinY();
            if (keepAspect) {
                double newRatio = newHeight / newWidth;
                newHeight = newWidth * preferredAspectRatio;
            }
            model.reshapeInLocal(owner, bounds.getMinX(), (bounds.getMinY() + bounds.getMaxY() - newHeight) * 0.5, newWidth, newHeight);
        }
    }

    private static class NorthHandle extends AbstractResizeTransformHandle {

        NorthHandle(Figure owner, String styleclass) {
            super(owner, styleclass, RelativeLocator.NORTH, NORTH_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.N_RESIZE;
        }

        @Override
        protected void resize(@Nonnull Point2D newPoint, Figure owner, @Nonnull Bounds bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            double newY = min(bounds.getMaxY(), newPoint.getY());
            double newWidth = bounds.getMaxX() - bounds.getMinX();
            double newHeight = bounds.getMaxY() - newY;
            if (keepAspect) {
                double newRatio = newHeight / newWidth;
                newWidth = newHeight / preferredAspectRatio;
            }
            model.reshapeInLocal(owner, (bounds.getMinX() + bounds.getMaxX() - newWidth) * 0.5, newY, newWidth, newHeight);
        }
    }

    private static class NorthWestHandle extends AbstractResizeTransformHandle {

        NorthWestHandle(Figure owner, String styleclass) {
            super(owner, styleclass, RelativeLocator.NORTH_WEST, NORTH_WEST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.NW_RESIZE;
        }

        @Override
        protected void resize(@Nonnull Point2D newPoint, Figure owner, @Nonnull Bounds bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            double newX = min(bounds.getMaxX(), newPoint.getX());
            double newY = min(bounds.getMaxY(), newPoint.getY());
            double newWidth = bounds.getMaxX() - newX;
            double newHeight = bounds.getMaxY() - newY;
            if (keepAspect) {
                double newRatio = newHeight / newWidth;
                if (newRatio > preferredAspectRatio) {
                    newHeight = newWidth * preferredAspectRatio;
                } else {
                    newWidth = newHeight / preferredAspectRatio;
                }
            }

            model.reshapeInLocal(owner, bounds.getMaxX() - newWidth, bounds.getMaxY() - newHeight, newWidth, newHeight);
        }
    }

    private static class SouthEastHandle extends AbstractResizeTransformHandle {

        SouthEastHandle(Figure owner, String styleclass) {
            super(owner, styleclass, RelativeLocator.SOUTH_EAST, SOUTH_EAST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.SE_RESIZE;
        }

        @Override
        protected void resize(@Nonnull Point2D newPoint, Figure owner, @Nonnull Bounds bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            double newX = max(bounds.getMinX(), newPoint.getX());
            double newY = max(bounds.getMinY(), newPoint.getY());
            double newWidth = newX - bounds.getMinX();
            double newHeight = newY - bounds.getMinY();
            if (keepAspect) {
                double newRatio = newHeight / newWidth;
                if (newRatio > preferredAspectRatio) {
                    newHeight = newWidth * preferredAspectRatio;
                } else {
                    newWidth = newHeight / preferredAspectRatio;
                }
            }
            model.reshapeInLocal(owner, bounds.getMinX(), bounds.getMinY(), newWidth, newHeight);
        }
    }

    private static class SouthHandle extends AbstractResizeTransformHandle {

        SouthHandle(Figure owner, String styleclass) {
            super(owner, styleclass, RelativeLocator.SOUTH, SOUTH_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.S_RESIZE;
        }

        @Override
        protected void resize(@Nonnull Point2D newPoint, Figure owner, @Nonnull Bounds bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            double newY = max(bounds.getMinY(), newPoint.getY());
            double newWidth = bounds.getWidth();
            double newHeight = newY - bounds.getMinY();
            if (keepAspect) {
                newWidth = newHeight / preferredAspectRatio;
            }
            model.reshapeInLocal(owner, (bounds.getMinX() + bounds.getMaxX() - newWidth) * 0.5, bounds.getMinY(), newWidth, newHeight);
        }
    }

    private static class SouthWestHandle extends AbstractResizeTransformHandle {

        SouthWestHandle(Figure owner, String styleclass) {
            super(owner, styleclass, RelativeLocator.SOUTH_WEST, SOUTH_WEST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.SW_RESIZE;
        }

        @Override
        protected void resize(@Nonnull Point2D newPoint, Figure owner, @Nonnull Bounds bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            double newX = min(bounds.getMaxX(), newPoint.getX());
            double newY = max(bounds.getMinY(), newPoint.getY());
            double newWidth = bounds.getMaxX() - min(bounds.getMaxX(), newX);
            double newHeight = newY - bounds.getMinY();
            if (keepAspect) {
                double newRatio = newHeight / newWidth;
                if (newRatio > preferredAspectRatio) {
                    newHeight = newWidth * preferredAspectRatio;
                } else {
                    newWidth = newHeight / preferredAspectRatio;
                }
            }
            model.reshapeInLocal(owner, bounds.getMaxX() - newWidth, bounds.getMinY(), newWidth, newHeight);
        }
    }

    private static class WestHandle extends AbstractResizeTransformHandle {

        WestHandle(Figure owner, String styleclass) {
            super(owner, styleclass, RelativeLocator.WEST, WEST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.W_RESIZE;
        }

        @Override
        protected void resize(@Nonnull Point2D newPoint, Figure owner, @Nonnull Bounds bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            double newX = min(bounds.getMaxX(), newPoint.getX());
            double newWidth = bounds.getMaxX() - newX;
            double newHeight = bounds.getHeight();
            if (keepAspect) {
                double newRatio = newHeight / newWidth;
                newHeight = newWidth * preferredAspectRatio;
            }
            model.reshapeInLocal(owner, newX, (bounds.getMinY() + bounds.getMaxY() - newHeight) * 0.5, newWidth, newHeight);
        }
    }
}
