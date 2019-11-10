/*
 * @(#)TransformHandleKit.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
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
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Transforms;

import java.util.Collection;
import java.util.function.Function;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.jhotdraw8.draw.figure.TransformableFigure.TRANSFORMS;

/**
 * A set of utility methods to create handles which transform a Figure by using
 * its {@code transform} method, if the Figure is transformable.
 * <p>
 * FIXME implement me
 *
 * @author Werner Randelshofer
 */
public class TransformHandleKit {

    protected static final SVGPath NORTH_SHAPE = new SVGPath();
    protected static final SVGPath EAST_SHAPE = new SVGPath();
    protected static final SVGPath WEST_SHAPE = new SVGPath();
    protected static final SVGPath SOUTH_SHAPE = new SVGPath();//new Rectangle(9, 5);
    protected static final SVGPath NORTH_EAST_SHAPE = new SVGPath();
    protected static final SVGPath NORTH_WEST_SHAPE = new SVGPath();
    protected static final SVGPath SOUTH_EAST_SHAPE = new SVGPath();
    protected static final SVGPath SOUTH_WEST_SHAPE = new SVGPath();

    static {
        final String circle = "M 9,4.5 A 4.5,4.5 0 1 0 0,4.5 A 4.5,4.5 0 1 0 9,4.5 Z ";
        NORTH_EAST_SHAPE.setContent(circle + "M 4.5,9 4.5,4.5 0,4.5 ");
        NORTH_WEST_SHAPE.setContent(circle + "M 9,4.5 4.5,4.5 4.5,9");
        SOUTH_EAST_SHAPE.setContent(circle + "M 0,4.5 4.5,4.5 4.5,0");
        SOUTH_WEST_SHAPE.setContent(circle + "M 4.5,0 4.5,4.5 9,4.5 ");
        SOUTH_SHAPE.setContent(circle + "M 0,4.5 9,4.5");
        NORTH_SHAPE.setContent(circle + "M 0,4.5 9,4.5");
        EAST_SHAPE.setContent(circle + "M 4.5,0 4.5,9");
        WEST_SHAPE.setContent(circle + "M 4.5,0 4.5,9");
    }

    @NonNull
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    @NonNull
    private static final Function<Color, Border> REGION_BORDER = color -> new Border(
            new BorderStroke(color, BorderStrokeStyle.SOLID, null, null)
    );

    /**
     * Prevent instance creation.
     */
    private TransformHandleKit() {

    }

    /**
     * Creates handles for each corner of a figure and adds them to the provided
     * collection.
     *
     * @param f       the figure which will own the handles
     * @param handles the list to which the handles should be added
     */
    static public void addCornerTransformHandles(TransformableFigure f, @NonNull Collection<Handle> handles) {
        handles.add(southEast(f));
        handles.add(southWest(f));
        handles.add(northEast(f));
        handles.add(northWest(f));
    }

    /**
     * Fills the given collection with handles at each the north, south, east,
     * and west of the figure.
     *
     * @param f       the figure which will own the handles
     * @param handles the list to which the handles should be added
     */
    static public void addEdgeTransformHandles(TransformableFigure f, @NonNull Collection<Handle> handles) {
        handles.add(south(f));
        handles.add(north(f));
        handles.add(east(f));
        handles.add(west(f));
    }

    /**
     * Fills the given collection with handles at each the north, south, east,
     * and west of the figure.
     *
     * @param f       the figure which will own the handles
     * @param handles the list to which the handles should be added
     */
    static public void addTransformHandles(TransformableFigure f, @NonNull Collection<Handle> handles) {
        addCornerTransformHandles(f, handles);
        addEdgeTransformHandles(f, handles);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    @NonNull
    static public Handle south(TransformableFigure owner) {
        return new SouthHandle(owner);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    @NonNull
    static public Handle southEast(TransformableFigure owner) {
        return new SouthEastHandle(owner);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    @NonNull
    static public Handle southWest(TransformableFigure owner) {
        return new SouthWestHandle(owner);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    @NonNull
    static public Handle north(TransformableFigure owner) {
        return new NorthHandle(owner);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    @NonNull
    static public Handle northEast(TransformableFigure owner) {
        return new NorthEastHandle(owner);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    @NonNull
    static public Handle northWest(TransformableFigure owner) {
        return new NorthWestHandle(owner);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    @NonNull
    static public Handle east(TransformableFigure owner) {
        return new EastHandle(owner);
    }

    /**
     * Creates a handle for the specified figure.
     *
     * @param owner the figure which will own the handle
     * @return the handle
     */
    @NonNull
    static public Handle west(TransformableFigure owner) {
        return new WestHandle(owner);
    }

    private abstract static class AbstractTransformHandle extends AbstractResizeTransformHandle {

        @Nullable ImmutableList<Transform> startTransforms;

        public AbstractTransformHandle(Figure owner, Locator locator, Shape shape, Background bg, Function<Color, Border> border) {
            super(owner, locator, shape, bg, border);
        }

        @Override
        public void handleMousePressed(@NonNull MouseEvent event, @NonNull DrawingView view) {
            super.handleMousePressed(event, view); //To change body of generated methods, choose Tools | Templates.
            startTransforms = owner.get(TRANSFORMS);
        }

        protected void transform(@NonNull DrawingModel model, Figure o, double x, double y, double width, double height) {
            if (width == 0 || height == 0) {
                return;
            }
            TransformableFigure owner = (TransformableFigure) o;
            Bounds oldBounds = startBounds.getConvertedBoundsValue();
            ImmutableList<Transform> oldTransforms = startTransforms;

            double sx = width / oldBounds.getWidth();
            double sy = height / oldBounds.getHeight();
            double tx = x - oldBounds.getMinX();
            double ty = y - oldBounds.getMinY();
            Transform transform = new Translate(tx, ty);
            if (!Double.isNaN(sx) && !Double.isNaN(sy)
                    && !Double.isInfinite(sx) && !Double.isInfinite(sy)
                    && (sx != 1d || sy != 1d)) {
                transform = Transforms.concat(transform, new Scale(sx, sy, oldBounds.getMinX(), oldBounds.getMinY()));
            }
            switch (oldTransforms.size()) {
                case 0:
                    model.set(owner, TRANSFORMS, ImmutableLists.of(transform));
                    break;
                default:
                    int last = oldTransforms.size() - 1;
                    model.set(owner, TRANSFORMS, ImmutableLists.set(oldTransforms, last, Transforms.concat(oldTransforms.get(last), transform)));
                    break;
            }
        }

        @Override
        protected void resize(@NonNull CssPoint2D newPoint, Figure owner, @NonNull CssRectangle2D bounds, @NonNull DrawingModel model, boolean keepAspect) {
            // FIXME remove this method
            resize(newPoint.getConvertedValue(), owner, bounds.getConvertedBoundsValue(), model, keepAspect);
        }

    }

    private static class NorthEastHandle extends AbstractTransformHandle {

        NorthEastHandle(TransformableFigure owner) {
            super(owner, BoundsLocator.NORTH_EAST, NORTH_EAST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.CROSSHAIR;
        }

        @Override
        protected void resize(@NonNull Point2D newPoint, Figure owner, @NonNull Bounds bounds, @NonNull DrawingModel model, boolean keepAspect) {
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

            transform(model, owner, bounds.getMinX(), bounds.getMaxY() - newHeight, newWidth, newHeight);
        }

    }

    private static class EastHandle extends AbstractTransformHandle {

        EastHandle(TransformableFigure owner) {
            super(owner, BoundsLocator.EAST, EAST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.CROSSHAIR;
        }

        @Override
        protected void resize(@NonNull Point2D newPoint, Figure owner, @NonNull Bounds bounds, @NonNull DrawingModel model, boolean keepAspect) {
            double newWidth = max(newPoint.getX(), bounds.getMinX()) - bounds.getMinX();
            double newHeight = bounds.getMaxY() - bounds.getMinY();
            if (keepAspect) {
                double newRatio = newHeight / newWidth;
                newHeight = newWidth * preferredAspectRatio;
            }
            transform(model, owner, bounds.getMinX(), (bounds.getMinY() + bounds.getMaxY() - newHeight) * 0.5, newWidth, newHeight);
        }

    }

    private static class NorthHandle extends AbstractTransformHandle {

        NorthHandle(TransformableFigure owner) {
            super(owner, BoundsLocator.NORTH, NORTH_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.CROSSHAIR;
        }

        @Override
        protected void resize(@NonNull Point2D newPoint, Figure owner, @NonNull Bounds bounds, @NonNull DrawingModel model, boolean keepAspect) {
            double newY = min(bounds.getMaxY(), newPoint.getY());
            double newWidth = bounds.getMaxX() - bounds.getMinX();
            double newHeight = bounds.getMaxY() - newY;
            if (keepAspect) {
                double newRatio = newHeight / newWidth;
                newWidth = newHeight / preferredAspectRatio;
            }
            transform(model, owner, (bounds.getMinX() + bounds.getMaxX() - newWidth) * 0.5, newY, newWidth, newHeight);
        }
    }

    private static class NorthWestHandle extends AbstractTransformHandle {

        NorthWestHandle(TransformableFigure owner) {
            super(owner, BoundsLocator.NORTH_WEST, NORTH_WEST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.CROSSHAIR;
        }

        @Override
        protected void resize(@NonNull Point2D newPoint, Figure owner, @NonNull Bounds bounds, @NonNull DrawingModel model, boolean keepAspect) {
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

            transform(model, owner, bounds.getMaxX() - newWidth, bounds.getMaxY() - newHeight, newWidth, newHeight);
        }
    }

    private static class SouthEastHandle extends AbstractTransformHandle {

        SouthEastHandle(TransformableFigure owner) {
            super(owner, BoundsLocator.SOUTH_EAST, SOUTH_EAST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.SE_RESIZE;
        }

        @Override
        protected void resize(@NonNull Point2D newPoint, Figure owner, @NonNull Bounds bounds, @NonNull DrawingModel model, boolean keepAspect) {
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
            transform(model, owner, bounds.getMinX(), bounds.getMinY(), newWidth, newHeight);
        }
    }

    private static class SouthHandle extends AbstractTransformHandle {

        SouthHandle(TransformableFigure owner) {
            super(owner, BoundsLocator.SOUTH, SOUTH_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.S_RESIZE;
        }

        @Override
        protected void resize(@NonNull Point2D newPoint, Figure owner, @NonNull Bounds bounds, @NonNull DrawingModel model, boolean keepAspect) {
            double newY = max(bounds.getMinY(), newPoint.getY());
            double newWidth = bounds.getMaxX() - bounds.getMinX();
            double newHeight = newY - bounds.getMinY();
            if (keepAspect) {
                newWidth = newHeight / preferredAspectRatio;
            }
            transform(model, owner, (bounds.getMinX() + bounds.getMaxX() - newWidth) * 0.5, bounds.getMinY(), newWidth, newHeight);
        }
    }

    private static class SouthWestHandle extends AbstractTransformHandle {

        SouthWestHandle(TransformableFigure owner) {
            super(owner, BoundsLocator.SOUTH_WEST, SOUTH_WEST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.CROSSHAIR;
        }

        @Override
        protected void resize(@NonNull Point2D newPoint, Figure owner, @NonNull Bounds bounds, @NonNull DrawingModel model, boolean keepAspect) {
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
            transform(model, owner, bounds.getMaxX() - newWidth, bounds.getMinY(), newWidth, newHeight);
        }
    }

    private static class WestHandle extends AbstractTransformHandle {

        WestHandle(TransformableFigure owner) {
            super(owner, BoundsLocator.WEST, WEST_SHAPE, REGION_BACKGROUND, REGION_BORDER);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.CROSSHAIR;
        }

        @Override
        protected void resize(@NonNull Point2D newPoint, Figure owner, @NonNull Bounds bounds, @NonNull DrawingModel model, boolean keepAspect) {
            double newX = min(bounds.getMaxX(), newPoint.getX());
            double newWidth = bounds.getMaxX() - newX;
            double newHeight = bounds.getHeight();
            if (keepAspect) {
                double newRatio = newHeight / newWidth;
                newHeight = newWidth * preferredAspectRatio;
            }
            transform(model, owner, newX, (bounds.getMinY() + bounds.getMaxY() - newHeight) * 0.5, newWidth, newHeight);
        }
    }
}
