/*
 * @(#)RelativePointHandle.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.geom.Geom;

import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;

/**
 * Handle for the point of a figure which is relative to the top left corner of the figure.
 *
 * @author Werner Randelshofer
 */
public class RelativePointHandle extends AbstractHandle {
    public static final @Nullable BorderStrokeStyle INSIDE_STROKE = new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 1.0, 0, null);

    private static final @Nullable Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final @Nullable Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Rectangle REGION_SHAPE = new Rectangle(7, 7);
    private final @NonNull Region node;
    /**
     * Relative origin.
     * <ul>
     * <li>0,0=top left</li>
     * <li>1,0=top right</li>
     * <li>0,1=bottom left</li>
     * <li>1,1=bottom right</li>
     * </ul>
     */
    private @NonNull Point2D origin = new Point2D(0, 0);
    private Point2D pickLocation;
    private final MapAccessor<CssPoint2D> pointKey;

    public RelativePointHandle(Figure figure, MapAccessor<CssPoint2D> pointKey) {
        super(figure);
        this.pointKey = pointKey;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(true);
        node.setCenterShape(true);
        node.resize(11, 11);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        Point2D p = getLocationInView();
        return Geom.squaredDistance(x, y, p.getX(), p.getY()) <= tolerance * tolerance;
    }

    @Override
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }

    @Override
    public @NonNull Region getNode(@NonNull DrawingView view) {
        double size = view.getEditor().getHandleSize();
        if (node.getWidth() != size) {
            node.resize(size, size);
        }
        CssColor color = view.getEditor().getHandleColor();
        BorderStroke borderStroke = node.getBorder().getStrokes().get(0);
        if (borderStroke == null || !borderStroke.getTopStroke().equals(color.getColor())) {
            node.setBorder(new Border(
                    new BorderStroke(color.getColor(), INSIDE_STROKE, null, null)
            ));
        }
        return node;
    }

    @Override
    public void onMouseDragged(@NonNull MouseEvent event, @NonNull DrawingView view) {
        CssPoint2D newPoint = new CssPoint2D(view.viewToWorld(new Point2D(event.getX(), event.getY())));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control switches the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
        }
        final CssPoint2D localPoint = getOwner().worldToLocal(newPoint);
        CssRectangle2D bounds = getOwner().getCssLayoutBounds();

        view.getModel().set(getOwner(), pointKey, localPoint.subtract(bounds.getTopLeft()));
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(@NonNull DrawingView view) {
        Figure f = getOwner();
        Bounds bounds = f.getLayoutBounds();
        Transform t = FXTransforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Point2D p = f.get(pointKey).getConvertedValue().add(bounds.getMinX(), bounds.getMinY());
        pickLocation = p = t == null ? p : t.transform(p);
        double size = node.getWidth();
        node.relocate(p.getX() - size * 0.5, p.getY() - size * 0.5);
        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
    }

}
