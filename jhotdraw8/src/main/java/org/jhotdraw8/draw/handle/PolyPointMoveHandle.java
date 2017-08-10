/* @(#)PolyPointMoveHandle.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
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
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

/**
 * Handle for moving (translating) a figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PolyPointMoveHandle extends AbstractHandle {

    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Rectangle REGION_SHAPE = new Rectangle(5, 5);
    private Set<Figure> groupReshapeableFigures;
    private final Region node;
    private Point2D oldPoint;
    private Point2D pickLocation;
    private final int pointIndex;
    private final MapAccessor<ImmutableObservableList<Point2D>> pointKey;
    private final String styleclass;

    public PolyPointMoveHandle(Figure figure, MapAccessor<ImmutableObservableList<Point2D>> pointKey, int pointIndex) {
        this(figure, pointKey, pointIndex, STYLECLASS_HANDLE_MOVE);
    }

    public PolyPointMoveHandle(Figure figure, MapAccessor<ImmutableObservableList<Point2D>> pointKey, int pointIndex, String styleclass) {
        super(figure);
        this.pointKey = pointKey;
        this.pointIndex = pointIndex;
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11);

        node.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        Point2D p = getLocationInView();
        return Geom.length2(x, y, p.getX(), p.getY()) <= tolerance;
    }

    @Override
    public Cursor getCursor() {
        return Cursor.OPEN_HAND;
    }

    private Point2D getLocation() {
        ImmutableObservableList<Point2D> list = owner.get(pointKey);
        return list.get(pointIndex);

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
            oldPoint = owner.getLocalToWorld().transform(loc);
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

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
        oldPoint = view.getConstrainer().constrainPoint(owner, view.viewToWorld(new Point2D(event.getX(), event.getY())));

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
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
        // FIXME fireDrawingModelEvent undoable edit
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = owner;
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = f.getBoundsInLocal();
        Point2D p = getLocation();
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        pickLocation = p = t == null ? p : t.transform(p);

        // The node is centered around the location.
        // (The value 5.5 is half of the node size, which is 11,11.
        // 0.5 is subtracted from 5.5 so that the node snaps between pixels
        // so that we get sharp lines.
        node.relocate(p.getX() - 5, p.getY() - 5);

        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
    }

    /**
     * Translates the specified figure, given the old and new position of a
     * point.
     *
     * @param f the figure to be translated
     * @param oldPoint oldPoint in world coordinates
     * @param newPoint newPoint in world coordinates
     * @param model the drawing model
     */
    public static void translateFigure(Figure f, Point2D oldPoint, Point2D newPoint, DrawingModel model) {
        Point2D npl = f.worldToParent(newPoint);
        Point2D opl = f.worldToParent(oldPoint);
        Transform tx = Transform.translate(npl.getX() - opl.getX(), npl.getY() - opl.getY());
        if (model != null) {
            model.reshapeInParent(f, tx);
        } else {
            f.reshapeInParent(tx);
        }
    }
}
