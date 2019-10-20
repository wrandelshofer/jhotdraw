/*
 * @(#)RotateHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

import java.util.HashSet;
import java.util.Set;

import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import static org.jhotdraw8.draw.figure.TransformableFigure.SCALE_Y;

/**
 * A Handle to rotate a TransformableFigure around the center of its bounds in
 * local.
 *
 * @author Werner Randelshofer
 */
public class RotateHandle extends AbstractHandle {
    public static final BorderStrokeStyle INSIDE_STROKE = new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 1.0, 0, null);

    @Nullable
    private static final Background HANDLE_REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    @Nullable
    private static final Border HANDLE_REGION_BORDER = new Border(new BorderStroke(Color.PURPLE, BorderStrokeStyle.SOLID, null, null));

    private static final Circle PICK_NODE_SHAPE = new Circle(3);
    private static final SVGPath PIVOT_NODE_SHAPE = new SVGPath();

    @Nullable
    private static final Background PIVOT_REGION_BACKGROUND = new Background(new BackgroundFill(Color.PURPLE, null, null));
    @Nullable
    private static final Border PIVOT_REGION_BORDER = null;

    static {
        PIVOT_NODE_SHAPE.setContent("M-5,-1 L -1,-1 -1,-5 1,-5 1,-1 5,-1 5 1 1,1 1,5 -1,5 -1,1 -5,1 Z");
    }

    @Nonnull
    private final Group group;

    private Set<Figure> groupReshapeableFigures;
    @Nonnull
    private final Line line;
    private double lineLength = 10.0;
    private Point2D pickLocation;
    @Nonnull
    private final Region pickNode;
    @Nonnull
    private final Region pivotNode;

    public RotateHandle(TransformableFigure figure) {
        super(figure);
        group = new Group();
        pickNode = new Region();
        pickNode.setShape(PICK_NODE_SHAPE);
        pickNode.setManaged(false);
        pickNode.setScaleShape(true);
        pickNode.setCenterShape(true);
        pickNode.resize(11, 11); // size must be odd
        pickNode.getStyleClass().clear();
        // pickNode.getStyleClass().add(handleStyleclass);
        pickNode.setBorder(HANDLE_REGION_BORDER);
        pickNode.setBackground(HANDLE_REGION_BACKGROUND);

        pivotNode = new Region();
        pivotNode.setShape(PIVOT_NODE_SHAPE);
        pivotNode.setManaged(false);
        pivotNode.setScaleShape(true);
        pivotNode.setCenterShape(true);
        pivotNode.resize(11, 11); // size must be odd
        pivotNode.getStyleClass().clear();
        // pivotNode.getStyleClass().add(pivotStyleclass);
        pivotNode.setBorder(PIVOT_REGION_BORDER);
        pivotNode.setBackground(PIVOT_REGION_BACKGROUND);
        pivotNode.setVisible(false);

        line = new Line();
        line.getStyleClass().clear();
        //line.getStyleClass().add(lineStyleclass);
        group.getChildren().addAll(line, pickNode, pivotNode);
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
    public Group getNode(DrawingView view) {
        double size = view.getEditor().getHandleSize();
        lineLength = size * 1.5;
        if (pickNode.getWidth() != size) {
            pickNode.resize(size, size);
        }
        Paint color = Paintable.getPaint(view.getEditor().getHandleColor());
        line.setStroke(color);
        BorderStroke borderStroke = pickNode.getBorder().getStrokes().get(0);
        if (borderStroke == null || !borderStroke.getTopStroke().equals(color)) {
            Border border = new Border(
                    new BorderStroke(color, INSIDE_STROKE, null, null)
            );
            pickNode.setBorder(border);
            pivotNode.setBorder(border);
        }
        return group;
    }

    @Nonnull
    @Override
    public TransformableFigure getOwner() {
        return (TransformableFigure) super.getOwner();
    }

    @Nullable
    private Transform getRotateToWorld() {
        TransformableFigure o = getOwner();
        Transform t = o.getParentToWorld();

        Point2D center = Geom.center(o.getBoundsInLocal());
        Transform translate = Transform.translate(o.getStyled(TransformableFigure.TRANSLATE_X), o.getStyled(TransformableFigure.TRANSLATE_Y));
        Transform scale = Transform.scale(o.getStyled(TransformableFigure.SCALE_X), o.getStyled(TransformableFigure.SCALE_Y), center.getX(), center.getY());
        Transform rotate = Transform.rotate(o.getStyled(TransformableFigure.ROTATE), center.getX(), center.getY());

        // t = ((TransformableFigure)o).getInverseTransform().createConcatenation(rotate).createConcatenation(scale).createConcatenation(translate);
        t = Transforms.concat(t, Transforms.concat(translate, rotate));//.createConcatenation(translate).createConcatenation(t);

        return t;
    }

    @Nullable
    private Transform getWorldToRotate() {
        TransformableFigure o = getOwner();
        Transform t = o.getWorldToParent();
        Point2D center = Geom.center(o.getBoundsInLocal());

        Transform translate = Transform.translate(-o.getStyled(TransformableFigure.TRANSLATE_X), -o.getStyled(TransformableFigure.TRANSLATE_Y));
        Transform scale = Transform.scale(1.0 / o.getStyled(TransformableFigure.SCALE_X), 1.0 / o.getStyled(TransformableFigure.SCALE_Y), center.getX(), center.getY());
        Transform rotate = Transform.rotate(-o.getStyled(TransformableFigure.ROTATE), center.getX(), center.getY());

        // t = ((TransformableFigure)o).getInverseTransform().createConcatenation(rotate).createConcatenation(scale).createConcatenation(translate);
        t = Transforms.concat(t, translate);

        return t;
    }

    @Override
    public void handleMouseDragged(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        TransformableFigure o = getOwner();
        Point2D center = Geom.center(o.getBoundsInLocal());
        Transform t = Transforms.concat(getWorldToRotate(), view.getViewToWorld());
        Point2D newPoint = (t == null) ? new Point2D(event.getX(), event.getY()) : t.transform(new Point2D(event.getX(), event.getY()));
        double newRotate = 90 + 180.0 / Math.PI * Geom.angle(center.getX(), center.getY(), newPoint.getX(), newPoint.getY());

        newRotate = newRotate % 360;
        if (newRotate < 0) {
            newRotate += 360;
        }

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newRotate = view.getConstrainer().constrainAngle(getOwner(), newRotate);
        }
        if (event.isMetaDown()) {
            // meta snaps the location of the handle to the grid
        }

        DrawingModel model = view.getModel();
        if (event.isShiftDown()) {
            // shift transforms all selected figures
            for (Figure f : groupReshapeableFigures) {
                if (f instanceof TransformableFigure) {
                    model.set(f, TransformableFigure.ROTATE, newRotate);
                }
            }
        } else {
            model.set(getOwner(), TransformableFigure.ROTATE, newRotate);
        }
    }

    @Override
    public void handleMousePressed(MouseEvent event, @Nonnull DrawingView view) {
        pivotNode.setVisible(true);
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
        pivotNode.setVisible(false);
        // FIXME fireDrawingModelEvent undoable edit event
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        TransformableFigure o = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), getRotateToWorld());
        Bounds b = o.getBoundsInLocal();
        Point2D centerInLocal = Geom.center(b);
        double scaleY = o.getStyled(SCALE_Y);

        Point2D p = new Point2D(centerInLocal.getX(), centerInLocal.getY() - b.getHeight() * 0.5 * scaleY);

        p = t == null ? p : t.transform(p);

        // rotates the node:
        pickNode.setRotate(o.getStyled(ROTATE));
        pickNode.setRotationAxis(o.getStyled(ROTATION_AXIS));
        pivotNode.setRotate(o.getStyled(ROTATE));
        pivotNode.setRotationAxis(o.getStyled(ROTATION_AXIS));

        Point2D centerInView = t.transform(centerInLocal);
        Point2D vector = new Point2D(p.getX() - centerInView.getX(), p.getY() - centerInView.getY());
        double size = pickNode.getWidth();
        vector = vector.normalize();
        pickLocation = new Point2D(p.getX() + vector.getX() * lineLength, p.getY() + vector.getY() * lineLength);
        pickNode.relocate(pickLocation.getX() - size * 0.5, pickLocation.getY() - size * 0.5);
        pivotNode.relocate(centerInView.getX() - size * 0.5, centerInView.getY() - size * 0.5);
        line.setStartX(pickLocation.getX());
        line.setStartY(pickLocation.getY());
        line.setEndX(p.getX());
        line.setEndY(p.getY());
    }

}
