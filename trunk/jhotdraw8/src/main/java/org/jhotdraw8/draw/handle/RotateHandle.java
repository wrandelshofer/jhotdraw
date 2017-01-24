/* @(#)RotateHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

import java.util.HashSet;
import java.util.Set;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.SCALE_Y;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

/**
 * A Handle to rotate a TransformableFigure around the center of its bounds in
 * local.
 *
 * @author Werner Randelshofer
 */
public class RotateHandle extends AbstractHandle {

    private Point2D pickLocation;
    private final Group group;
    private final Region pickNode;
    private final Region pivotNode;
    private final Line line;

    private double lineLength = 10.0;

    private static final Circle PICK_NODE_SHAPE = new Circle(3);
    private static final SVGPath PIVOT_NODE_SHAPE = new SVGPath();

    static {
        PIVOT_NODE_SHAPE.setContent("M-5,-1 L -1,-1 -1,-5 1,-5 1,-1 5,-1 5 1 1,1 1,5 -1,5 -1,1 -5,1 Z");
    }
    private static final Background HANDLE_REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    private static final Border HANDLE_REGION_BORDER = new Border(new BorderStroke(Color.PURPLE, BorderStrokeStyle.SOLID, null, null));
    private static final Background PIVOT_REGION_BACKGROUND = new Background(new BackgroundFill(Color.PURPLE, null, null));
    private static final Border PIVOT_REGION_BORDER = null;

    private Set<Figure> groupReshapeableFigures;

    public RotateHandle(TransformableFigure figure) {
        this(figure, STYLECLASS_HANDLE_ROTATE, STYLECLASS_HANDLE_TRANSFORM_OUTLINE, STYLECLASS_HANDLE_PIVOT);
    }

    public RotateHandle(TransformableFigure figure, String handleStyleclass, String lineStyleclass, String pivotStyleclass) {
        super(figure);
        group = new Group();
        pickNode = new Region();
        pickNode.setShape(PICK_NODE_SHAPE);
        pickNode.setManaged(false);
        pickNode.setScaleShape(false);
        pickNode.setCenterShape(true);
        pickNode.resize(11, 11); // size must be odd
        pickNode.getStyleClass().clear();
        pickNode.getStyleClass().add(handleStyleclass);
        pickNode.setBorder(HANDLE_REGION_BORDER);
        pickNode.setBackground(HANDLE_REGION_BACKGROUND);

        pivotNode = new Region();
        pivotNode.setShape(PIVOT_NODE_SHAPE);
        pivotNode.setManaged(false);
        pivotNode.setScaleShape(false);
        pivotNode.setCenterShape(true);
        pivotNode.resize(11, 11); // size must be odd
        pivotNode.getStyleClass().clear();
        pivotNode.getStyleClass().add(pivotStyleclass);
        pivotNode.setBorder(PIVOT_REGION_BORDER);
        pivotNode.setBackground(PIVOT_REGION_BACKGROUND);
        pivotNode.setVisible(false);

        line = new Line();
        line.getStyleClass().clear();
        line.getStyleClass().add(lineStyleclass);
        group.getChildren().addAll(line, pickNode, pivotNode);
    }

    @Override
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    @Override
    public Group getNode() {
        return group;
    }

    @Override
    public void updateNode(DrawingView view) {
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
        vector = vector.normalize();
        pickLocation = new Point2D(p.getX() + vector.getX() * lineLength, p.getY() + vector.getY() * lineLength);
        pickNode.relocate(pickLocation.getX() - 5, pickLocation.getY() - 5);
        pivotNode.relocate(centerInView.getX() - 5, centerInView.getY() - 5);
        line.setStartX(pickLocation.getX());
        line.setStartY(pickLocation.getY());
        line.setEndX(p.getX());
        line.setEndY(p.getY());
    }

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

    private Transform getRotateToWorld() {
        TransformableFigure o = getOwner();
        Transform t = o.getParentToWorld();

        Point2D center = Geom.center(o.getBoundsInLocal());
        Transform translate = Transform.translate(o.getStyled(TransformableFigure.TRANSLATE_X), o.getStyled(TransformableFigure.TRANSLATE_Y));
        Transform scale = Transform.scale(o.getStyled(TransformableFigure.SCALE_X), o.getStyled(TransformableFigure.SCALE_Y), center.getX(), center.getY());
        Transform rotate = Transform.rotate(o.getStyled(TransformableFigure.ROTATE), center.getX(), center.getY());

        // t = ((TransformableFigure)o).getInverseTransform().createConcatenation(rotate).createConcatenation(scale).createConcatenation(translate);
        t = Transforms.concat(t,Transforms.concat(translate,rotate));//.createConcatenation(translate).createConcatenation(t);

        return t;
    }

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
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
    public void handleMouseDragged(MouseEvent event, DrawingView view) {
        TransformableFigure o = getOwner();
        Point2D center = Geom.center(o.getBoundsInLocal());
        Transform t = Transforms.concat(getWorldToRotate(),view.getViewToWorld());
        Point2D newPoint =  (t==null)?new Point2D(event.getX(), event.getY()): t.transform(new Point2D(event.getX(), event.getY()));
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
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
        pivotNode.setVisible(false);
        // FIXME fire undoable edit event
    }

    @Override
    public TransformableFigure getOwner() {
        return (TransformableFigure) super.getOwner();
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean contains(double x, double y, double tolerance) {
        Point2D p = getLocationInView();
       return Geom.length2(x, y, p.getX(), p.getY()) <= tolerance;
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }
}
