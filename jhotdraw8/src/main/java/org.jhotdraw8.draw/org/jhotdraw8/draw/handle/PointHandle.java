/* @(#)ConnectionFigureConnectionHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
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
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;

/**
 * Handle for the point of a figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PointHandle extends AbstractHandle {
    public static final BorderStrokeStyle INSIDE_STROKE = new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 1.0, 0, null);

    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    @Nullable
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Rectangle REGION_SHAPE = new Rectangle(7, 7);
    @Nonnull
    private final Region node;

    private Point2D pickLocation;
    private final NonnullMapAccessor<CssPoint2D> pointKey;
    private final String styleclass;

    public PointHandle(Figure figure, NonnullMapAccessor<CssPoint2D> pointKey) {
        this(figure, STYLECLASS_HANDLE_POINT, pointKey);
    }

    public PointHandle(Figure figure, String styleclass, NonnullMapAccessor<CssPoint2D> pointKey) {
        super(figure);
        this.pointKey = pointKey;
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(true);
        node.setCenterShape(true);
        node.resize(11, 11);

        //node.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double toleranceSquared) {
        Point2D p = getLocationInView();
        return Geom.length2(x, y, p.getX(), p.getY()) <= toleranceSquared;
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
    public Region getNode(DrawingView view) {
        double size = view.getHandleSize();
        if (node.getWidth() != size) {
            node.resize(size, size);
        }
        CssColor color = view.getHandleColor();
        BorderStroke borderStroke = node.getBorder().getStrokes().get(0);
        if (borderStroke == null || !borderStroke.getTopStroke().equals(color.getColor())) {
            node.setBorder(new Border(
                    new BorderStroke(color.getColor(), INSIDE_STROKE, null, null)
            ));
        }
        return node;
    }

    @Override
    public void handleMouseDragged(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        CssPoint2D newPoint = new CssPoint2D(view.viewToWorld(new Point2D(event.getX(), event.getY())));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control switches the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
        }

        view.getModel().set(owner, pointKey, owner.worldToLocal(newPoint));
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
        Figure f = owner;
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Point2D p = f.getNonnull(pointKey).getConvertedValue();
        pickLocation = p = t.transform(p);
        double size = node.getWidth();
        node.relocate(p.getX() - size * 0.5, p.getY() - size * 0.5);
        // rotates the node:
        node.setRotate(f.getStyledNonnull(ROTATE));
        node.setRotationAxis(f.getStyledNonnull(ROTATION_AXIS));
    }

}
