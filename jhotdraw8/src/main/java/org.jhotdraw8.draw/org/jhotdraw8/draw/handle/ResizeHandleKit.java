/* @(#)ResizeHandleKit.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.model.DrawingModel;

import java.util.Collection;
import java.util.function.Function;

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
     * @param f          the figure which will own the handles
     * @param handles    the list to which the handles should be added
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
     * @param f          the figure which will own the handles
     * @param handles    the list to which the handles should be added
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
     * @param f          the figure which will own the handles
     * @param handles    the list to which the handles should be added
     * @param styleclass the style class that should be assigned to the handles
     */
    static public void addResizeHandles(Figure f, @Nonnull Collection<Handle> handles, String styleclass) {
        addCornerResizeHandles(f, handles, styleclass);
        addEdgeResizeHandles(f, handles, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner      the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle south(Figure owner, String styleclass) {
        return new SouthHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner      the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle southEast(Figure owner, String styleclass) {
        return new SouthEastHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner      the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle southWest(Figure owner, String styleclass) {
        return new SouthWestHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner      the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle north(Figure owner, String styleclass) {
        return new NorthHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner      the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle northEast(Figure owner, String styleclass) {
        return new NorthEastHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner      the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle northWest(Figure owner, String styleclass) {
        return new NorthWestHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner      the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle east(Figure owner, String styleclass) {
        return new EastHandle(owner, styleclass);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner      the figure which will own the handle
     * @param styleclass the style class that should be assigned to the handles
     * @return the handle
     */
    static public Handle west(Figure owner, String styleclass) {
        return new WestHandle(owner, styleclass);
    }

    protected static final SVGPath NORTH_SHAPE = new SVGPath();
    protected static final SVGPath EAST_SHAPE = new SVGPath();
    protected static final SVGPath WEST_SHAPE = new SVGPath();
    protected static final SVGPath SOUTH_SHAPE = new SVGPath();
    protected static final SVGPath NORTH_EAST_SHAPE = new SVGPath();
    protected static final SVGPath NORTH_WEST_SHAPE = new SVGPath();
    protected static final SVGPath SOUTH_EAST_SHAPE = new SVGPath();
    protected static final SVGPath SOUTH_WEST_SHAPE = new SVGPath();

    static {
        final String square = "M 0,0 9,0 9,9 0,9 Z ";
        NORTH_EAST_SHAPE.setContent(square + "M 0,4.5 4.5,4.5 4.5,9");
        NORTH_WEST_SHAPE.setContent(square + "M 4.5,9 4.5,4.5 9,4.5 ");
        SOUTH_EAST_SHAPE.setContent(square + "M 4.5,0 4.5,4.5 0,4.5 ");
        SOUTH_WEST_SHAPE.setContent(square + "M 9,4.5 4.5,4.5 4.5,0");
        SOUTH_SHAPE.setContent(square + "M 0,4.5 9,4.5");
        NORTH_SHAPE.setContent(square + "M 0,4.5 9,4.5");
        EAST_SHAPE.setContent(square + "M 4.5,0 4.5,9");
        WEST_SHAPE.setContent(square + "M 4.5,0 4.5,9");
    }

    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    @Nonnull
    private static final Function<Color, Border> REGION_BORDER = color -> new Border(
            new BorderStroke(color, BorderStrokeStyle.SOLID, null, null)
    );

    private static class NorthEastHandle extends AbstractResizeTransformHandle {

        NorthEastHandle(Figure owner, String styleclass) {
            super(owner, styleclass, RelativeLocator.NORTH_EAST, NORTH_EAST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.NE_RESIZE;
        }

        @Override
        protected void resize(@Nonnull CssPoint2D newPoint, Figure owner, @Nonnull CssRectangle2D bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            CssSize newX = CssSize.max(newPoint.getX(), bounds.getMinX());
            CssSize newY = CssSize.min(newPoint.getY(), bounds.getMaxY());
            CssSize newWidth = newX.subtract(bounds.getMinX());
            CssSize newHeight = bounds.getMaxY().subtract(newY);
            if (keepAspect) {
                double newRatio = newHeight.getConvertedValue() / newWidth.getConvertedValue();
                if (newRatio > preferredAspectRatio) {
                    newHeight = newWidth.multiply(preferredAspectRatio);
                } else {
                    newWidth = newHeight.divide(preferredAspectRatio);
                }
            }

            model.reshapeInLocal(owner, bounds.getMinX(), newY, newWidth, newHeight);
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
        protected void resize(@Nonnull CssPoint2D newPoint, Figure owner, @Nonnull CssRectangle2D bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            CssSize newWidth = CssSize.max(newPoint.getX(), bounds.getMinX()).subtract(bounds.getMinX());
            CssSize newHeight = bounds.getMaxY().subtract(bounds.getMinY());
            if (keepAspect) {
                newHeight = newWidth.multiply(preferredAspectRatio);
            }
            model.reshapeInLocal(owner, bounds.getMinX(), (bounds.getMinY().add(bounds.getMaxY()).subtract(newHeight)).multiply(0.5), newWidth, newHeight);
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
        protected void resize(@Nonnull CssPoint2D newPoint, Figure owner, @Nonnull CssRectangle2D bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            CssSize newY = CssSize.min(bounds.getMaxY(), newPoint.getY());
            CssSize newWidth = bounds.getMaxX().subtract(bounds.getMinX());
            CssSize newHeight = bounds.getMaxY().subtract(newY);
            if (keepAspect) {
                newWidth = newHeight.divide(preferredAspectRatio);
            }
            model.reshapeInLocal(owner, (bounds.getMinX().add(bounds.getMaxX()).subtract(newWidth)).multiply(0.5), newY, newWidth, newHeight);
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
        protected void resize(@Nonnull CssPoint2D newPoint, Figure owner, @Nonnull CssRectangle2D bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            CssSize newX = CssSize.min(bounds.getMaxX(), newPoint.getX());
            CssSize newY = CssSize.min(bounds.getMaxY(), newPoint.getY());
            CssSize newWidth = bounds.getMaxX().subtract(newX);
            CssSize newHeight = bounds.getMaxY().subtract(newY);
            if (keepAspect) {
                double newRatio = newHeight.getConvertedValue() / newWidth.getConvertedValue();
                if (newRatio > preferredAspectRatio) {
                    newHeight = newWidth.multiply(preferredAspectRatio);
                } else {
                    newWidth = newHeight.divide(preferredAspectRatio);
                }
            }

            model.reshapeInLocal(owner, newPoint.getX(), newPoint.getY(), newWidth, newHeight);
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
        protected void resize(@Nonnull CssPoint2D newPoint, Figure owner, @Nonnull CssRectangle2D bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            CssSize newX = CssSize.max(newPoint.getX(), bounds.getMinX());
            CssSize newY = CssSize.max(newPoint.getY(), bounds.getMinY());
            CssSize newWidth = newX.subtract(bounds.getMinX());
            CssSize newHeight = newY.subtract(bounds.getMinY());
            if (keepAspect) {
                double newRatio = newHeight.getConvertedValue() / newWidth.getConvertedValue();
                if (newRatio > preferredAspectRatio) {
                    newHeight = newWidth.multiply(preferredAspectRatio);
                } else {
                    newWidth = newHeight.divide(preferredAspectRatio);
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
        protected void resize(@Nonnull CssPoint2D newPoint, Figure owner, @Nonnull CssRectangle2D bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            CssSize newY = CssSize.max(bounds.getMinY(), newPoint.getY());
            CssSize newWidth = bounds.getWidth();
            CssSize newHeight = newY.subtract(bounds.getMinY());
            if (keepAspect) {
                newWidth = newHeight.divide(preferredAspectRatio);
            }
            model.reshapeInLocal(owner, (bounds.getMinX().add(bounds.getMaxX()).subtract(newWidth)).multiply(0.5), bounds.getMinY(), newWidth, newHeight);
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
        protected void resize(@Nonnull CssPoint2D newPoint, Figure owner, @Nonnull CssRectangle2D bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            CssSize newX = CssSize.min(newPoint.getX(), bounds.getMaxX());
            CssSize newY = CssSize.max(newPoint.getY(), bounds.getMinY());
            CssSize newWidth = bounds.getMaxX().subtract(newX);
            CssSize newHeight = newY.subtract(bounds.getMinY());
            if (keepAspect) {
                double newRatio = newHeight.getConvertedValue() / newWidth.getConvertedValue();
                if (newRatio > preferredAspectRatio) {
                    newHeight = newWidth.multiply(preferredAspectRatio);
                } else {
                    newWidth = newHeight.divide(preferredAspectRatio);
                }
            }
            model.reshapeInLocal(owner, newX, bounds.getMinY(), newWidth, newHeight);
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
        protected void resize(@Nonnull CssPoint2D newPoint, Figure owner, @Nonnull CssRectangle2D bounds, @Nonnull DrawingModel model, boolean keepAspect) {
            CssSize newX = CssSize.min(bounds.getMaxX(), newPoint.getX());
            CssSize newWidth = bounds.getMaxX().subtract(newX);
            CssSize newHeight = bounds.getHeight();
            if (keepAspect) {
                newHeight = newWidth.multiply(preferredAspectRatio);
            }
            model.reshapeInLocal(owner, newX, (bounds.getMinY().add(bounds.getMaxY()).subtract(newHeight)).multiply(0.5), newWidth, newHeight);
        }
    }
}
