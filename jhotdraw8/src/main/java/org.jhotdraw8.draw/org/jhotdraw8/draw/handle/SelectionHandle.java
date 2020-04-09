/*
 * @(#)SelectionHandle.java
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
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.geom.Transforms;

import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;

/**
 * Handle for showing that a figure is selected.
 *
 * @author Werner Randelshofer
 */
public class SelectionHandle extends LocatorHandle {
    @Nullable
    public static final BorderStrokeStyle INSIDE_STROKE = new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 1.0, 0, null);

    private Point2D pickLocation;
    @NonNull
    private final Region node;
    private static final Rectangle REGION_SHAPE = new Rectangle(5, 5);
    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    @Nullable
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, null));

    public SelectionHandle(Figure figure, Locator locator) {
        super(figure, locator);
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
    public Cursor getCursor() {
        return Cursor.DEFAULT;
    }

    @NonNull
    @Override
    public Region getNode(@NonNull DrawingView view) {
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
    public void updateNode(@NonNull DrawingView view) {
        Figure f = owner;
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = f.getLayoutBounds();
        Point2D p = getLocation();
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        pickLocation = p = t == null ? p : t.transform(p);

        // Place the center of the node at the location.
        double size = node.getWidth();
        node.relocate(p.getX() - size * 0.5, p.getY() - size * 0.5);

        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {

    }

    @Override
    public void onMouseDragged(MouseEvent event, DrawingView view) {

    }


    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {

    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }
}
