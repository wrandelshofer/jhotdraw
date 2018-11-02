/* @(#)PolyPointEditHandle.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

/**
 * Handle for the point ofCollection a figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PolyPointEditHandle extends AbstractHandle {

    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    @Nullable
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Rectangle REGION_SHAPE = new Rectangle(7, 7);
    @Nonnull
    private final Region node;

    private Point2D pickLocation;
    private final int pointIndex;
    private final MapAccessor<ImmutableList<Point2D>> pointKey;
    private final String styleclass;

    public PolyPointEditHandle(Figure figure, MapAccessor<ImmutableList<Point2D>> pointKey, int pointIndex) {
        this(figure, pointKey, pointIndex, STYLECLASS_HANDLE_POINT);
    }

    public PolyPointEditHandle(Figure figure, MapAccessor<ImmutableList<Point2D>> pointKey, int pointIndex, String styleclass) {
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
        return Cursor.CROSSHAIR;
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }

    @Nonnull
    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void handleMouseClicked(@Nonnull MouseEvent event, @Nonnull DrawingView dv) {
        if (pointKey != null && event.getClickCount() == 2) {
            if (owner.get(pointKey).size() > 2) {
                dv.getModel().set(owner, pointKey, ImmutableList.remove(owner.get(pointKey), pointIndex));
                dv.recreateHandles();
            }
        }
    }

    @Override
    public void handleMouseDragged(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control switches the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), new CssPoint2D(newPoint)).getConvertedValue();
        }

        ImmutableList<Point2D> list = owner.get(pointKey);
        view.getModel().set(getOwner(), pointKey, ImmutableList.set(list, pointIndex, getOwner().worldToLocal(newPoint)));
    }

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
    }

    @Override
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        ImmutableList<Point2D> list = f.get(pointKey);
        Point2D p = list.get(pointIndex);
        pickLocation = p = t == null ? p : t.transform(p);
        node.relocate(p.getX() - 5, p.getY() - 5);
        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
    }

}
