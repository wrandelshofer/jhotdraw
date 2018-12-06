/* @(#)ConnectionFigureConnectionHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
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
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Transform;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;

import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;

import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Transforms;

/**
 * Handle for moving (translating) a figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MoveHandle extends LocatorHandle {

    public static final BorderStrokeStyle INSIDE_STROKE = new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 1.0, 0, null);
    private Point2D pickLocation;
    private CssPoint2D oldPoint;
    @Nonnull
    private final Region node;
    private final String styleclass;
    private static final Rectangle REGION_SHAPE = new Rectangle(5, 5);
    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    public static final BorderWidths WIDTH_2 = new BorderWidths(2, 2, 2, 2, false, false, false, false);
    @Nullable
    private static final Border REGION_BORDER = new Border(
            new BorderStroke(Color.BLUE, INSIDE_STROKE, null, null),
            new BorderStroke(Color.WHITE, INSIDE_STROKE, null, WIDTH_2)
    );
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
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11);

       // node.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public Cursor getCursor() {
        return pressed ? Cursor.CLOSED_HAND : Cursor.HAND;
    }

    @Nonnull
    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = owner;
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = f.getBoundsInLocal();
        Point2D p = getLocation();
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        pickLocation = p = t.transform(p);

        // The node is centered around the location. 
        // (The value 5.5 is half of the node size, which is 11,11.
        // 0.5 is subtracted from 5.5 so that the node snaps between pixels
        // so that we get sharp lines.
        node.relocate(p.getX() - 5, p.getY() - 5);

        CssColor color=view.getHandleColor();
        BorderStroke borderStroke = node.getBorder().getStrokes().size()<2?null:(BorderStroke)node.getBorder().getStrokes().get(1);
        if (borderStroke==null||!borderStroke.getTopStroke().equals(color.getColor())) {
            node.setBorder(new Border(
                    new BorderStroke(color.getColor(), INSIDE_STROKE, null, new BorderWidths(2, 2, 2, 2, false, false, false, false)),
                    new BorderStroke(Color.WHITE, INSIDE_STROKE, null, null)
            ));
        }

        double size=view.getHandleSize();
        if (REGION_SHAPE.getWidth()!=size) {
            REGION_SHAPE.setWidth(size);
            REGION_SHAPE.setHeight(size);
        }


        // rotates the node:
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
