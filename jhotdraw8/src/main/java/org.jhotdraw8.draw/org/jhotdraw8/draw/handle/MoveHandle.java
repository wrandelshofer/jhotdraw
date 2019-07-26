/*
 * @(#)MoveHandle.java
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
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Transforms;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;

/**
 * Handle for moving (translating) a figure.
 *
 * @author Werner Randelshofer
 */
public class MoveHandle extends LocatorHandle {

    private Point2D pickLocation;
    private CssPoint2D oldPoint;
    @Nonnull
    private final Region node;
    private final String styleclass;
    private static final Rectangle REGION_SHAPE = new Rectangle(5, 5);
    @Nullable
    private static final Function<Color, Background> REGION_BACKGROUND = color -> new Background(new BackgroundFill(color, null, null));
    @Nullable
    private static final Function<Color, Border> REGION_BORDER = color -> new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, null, null));
    private Set<Figure> groupReshapeableFigures;
    private boolean pressed;

    public MoveHandle(Figure figure, Locator locator) {
        this(figure, locator, STYLECLASS_HANDLE_MOVE);
    }

    public MoveHandle(Figure figure, Locator locator, String styleclass) {
        super(figure, locator);
        this.styleclass = styleclass;
        node = new Region();

        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(true);
        node.setCenterShape(true);
        node.resize(11, 11);
    }

    @Override
    public Cursor getCursor() {
        return pressed ? Cursor.CLOSED_HAND : Cursor.HAND;
    }

    @Nonnull
    @Override
    public Region getNode(DrawingView view) {
        double size = view.getEditor().getHandleSize();
        node.resize(size, size);
        CssColor color = view.getEditor().getHandleColor();
        node.setBorder(REGION_BORDER.apply(Color.WHITE));
        node.setBackground(REGION_BACKGROUND.apply(color.getColor()));
        return node;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = owner;
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = f.getBoundsInLocal();
        Point2D p = getLocation();
        pickLocation = p = t.transform(p);

        // Place the center of the node at the location.
        double size = node.getWidth();
        node.relocate(p.getX() - size * 0.5, p.getY() - size * 0.5);

        // Rotate the node.
        node.setRotate(f.getStyledNonnull(ROTATE));
        node.setRotationAxis(f.getStyledNonnull(ROTATION_AXIS));
    }

    @Override
    public void handleMousePressed(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        pressed = true;
        oldPoint = view.getConstrainer().constrainPoint(owner, new CssPoint2D(view.viewToWorld(new Point2D(event.getX(), event.getY()))));

        // determine which figures can be reshaped together as a group
        Set<Figure> selectedFigures = view.getSelectedFigures();
        groupReshapeableFigures = new HashSet<>();
        for (Figure f : view.getSelectedFigures()) {
            if (f.isGroupReshapeableWith(selectedFigures)) {
                groupReshapeableFigures.add(f);
            }
        }
        groupReshapeableFigures = view.getFiguresWithCompatibleHandle(groupReshapeableFigures, this);
    }

    @Override
    public void handleMouseDragged(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        CssPoint2D newPoint = new CssPoint2D(view.viewToWorld(new Point2D(event.getX(), event.getY())));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newPoint = view.getConstrainer().constrainPoint(owner, newPoint);
        }

        if (event.isMetaDown()) {
            // meta snaps the location of the handle to the grid
            Point2D loc = getLocation();
            final Transform localToWorld = owner.getLocalToWorld();
            oldPoint = new CssPoint2D(Transforms.transform(localToWorld, loc));
        }

        if (oldPoint.equals(newPoint)) {
            return;
        }

        //Transform tx = Transform.translate(newPoint.getX() - oldPoint.getX(), newPoint.getY() - oldPoint.getY());
        DrawingModel model = view.getModel();

        if (event.isShiftDown()) {
            // shift transforms all selected figures
            for (Figure f : groupReshapeableFigures) {
                translateFigure(f, oldPoint, newPoint, model);
            }
        } else {
            Figure f = owner;
            translateFigure(f, oldPoint, newPoint, model);
        }
        oldPoint = newPoint;
    }

    /**
     * Translates the specified figure, given the old and new position of a
     * point.
     *
     * @param f        the figure to be translated
     * @param oldPoint oldPoint in world coordinates
     * @param newPoint newPoint in world coordinates
     * @param model    the drawing model
     */
    public static void translateFigure(Figure f, @Nonnull Point2D oldPoint, @Nonnull Point2D newPoint, @Nullable DrawingModel model) {
        Point2D delta = newPoint.subtract(oldPoint);
        if (model != null) {
            model.translateInParent(f, new CssPoint2D(delta));
        } else {
            f.translateInParent(new CssPoint2D(delta));
        }
    }

    /**
     * Translates the specified figure, given the old and new position of a
     * point.
     *
     * @param f        the figure to be translated
     * @param oldPoint oldPoint in world coordinates
     * @param newPoint newPoint in world coordinates
     * @param model    the drawing model
     */
    public static void translateFigure(Figure f, @Nonnull CssPoint2D oldPoint, @Nonnull CssPoint2D newPoint, @Nullable DrawingModel model) {
        CssPoint2D delta = newPoint.subtract(oldPoint);
        if (model != null) {
            model.translateInParent(f, delta);
        } else {
            f.translateInParent(delta);
        }
    }


    @Override
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
        pressed = false;
        // FIXME create undoable edit
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }
}
