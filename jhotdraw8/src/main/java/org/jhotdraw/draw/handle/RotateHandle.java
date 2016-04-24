/* @(#)RotateHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

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
import javafx.scene.shape.Path;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TransformableFigure;
import static org.jhotdraw.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.geom.Geom;

/**
 * A Handle to rotate a Figure.
 *
 * @author Werner Randelshofer
 */
public class RotateHandle extends AbstractHandle {

    private Point2D pickLocation;
    private final Group group;
    private final Region node;
    private final Line line;
    private static final Circle REGION_SHAPE = new Circle(3);
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.PURPLE, BorderStrokeStyle.SOLID, null, null));
    protected Set<Figure> groupReshapeableFigures;
    private Transform startTransform;
    private double startRotation;
    /** Center of the rotation is the center of the untransformed layout bounds. */
    private Point2D center;
    private Point2D mouse;

    public RotateHandle(TransformableFigure figure) {
        this(figure, STYLECLASS_HANDLE_ROTATE, STYLECLASS_HANDLE_TRANSFORM_OUTLINE);
    }

    public RotateHandle(TransformableFigure figure, String handleStyleclass, String lineStyleclass) {
        super(figure);
        group = new Group();
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11); // size must be odd
        node.getStyleClass().clear();
        node.getStyleClass().add(handleStyleclass);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
        line = new Line();
        line.getStyleClass().clear();
        line.getStyleClass().add(lineStyleclass);
        group.getChildren().addAll(line, node);
        mouse = center = Geom.center(figure.getBoundsInLocal());
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
        Figure o = getOwner();
        Transform t = view.getWorldToView().createConcatenation(getRotateToWorld(o));
        Bounds b = o.getBoundsInLocal();
        Point2D p = getLocation(view);

        pickLocation = p = t.transform(p);
        node.relocate(p.getX() - 5, p.getY() - 5);

        // rotates the node:
        node.setRotate(o.getStyled(ROTATE));
        node.setRotationAxis(o.getStyled(ROTATION_AXIS));
        
        Point2D centerInViewCoordinates = t.transform(Geom.center(b));
        line.setStartX(centerInViewCoordinates.getX());
        line.setStartY(centerInViewCoordinates.getY());
        line.setEndX(pickLocation.getX());
        line.setEndY(pickLocation.getY());
    }

    private Transform getWorldToRotate(Figure o) {
        Transform t=o.getWorldToParent();
        if (o instanceof TransformableFigure) {
            Transform translate = Transform.translate(-o.getStyled(TransformableFigure.TRANSLATE_X), -o.getStyled(TransformableFigure.TRANSLATE_Y));
            Transform scale = Transform.scale(1.0 / o.getStyled(TransformableFigure.SCALE_X), 1.0 / o.getStyled(TransformableFigure.SCALE_Y), center.getX(), center.getY());
            Transform rotate = Transform.rotate(-o.getStyled(TransformableFigure.ROTATE), center.getX(), center.getY());

           // t = ((TransformableFigure)o).getInverseTransform().createConcatenation(rotate).createConcatenation(scale).createConcatenation(translate);
            t = t.createConcatenation(translate);
        }
        return t;
    } 
   private Transform getRotateToWorld(Figure o) {
        Transform t=o.getParentToWorld();
        if (o instanceof TransformableFigure) {
            Point2D center = Geom.center(o.getBoundsInLocal());
            Transform translate = Transform.translate(o.getStyled(TransformableFigure.TRANSLATE_X), o.getStyled(TransformableFigure.TRANSLATE_Y));
            Transform scale = Transform.scale(o.getStyled(TransformableFigure.SCALE_X), o.getStyled(TransformableFigure.SCALE_Y), center.getX(), center.getY());
            Transform rotate = Transform.rotate(o.getStyled(TransformableFigure.ROTATE), center.getX(), center.getY());

           // t = ((TransformableFigure)o).getInverseTransform().createConcatenation(rotate).createConcatenation(scale).createConcatenation(translate);
            t = rotate.createConcatenation(translate).createConcatenation(t);
        }
        return t;
    }     
    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
        Figure o = getOwner();
        mouse = new Point2D(event.getX(),event.getY());
        center = Geom.center(o.getBoundsInLocal());
        startTransform = getWorldToRotate(o).createConcatenation(view.getViewToWorld());
        Point2D newPoint = startTransform.transform(mouse);
        startRotation = 90 + 180.0 / Math.PI * Geom.angle(center.getX(), center.getY(), newPoint.getX(), newPoint.getY());
        startRotation = startRotation + o.getStyled(ROTATE);

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
    public void onMouseDragged(MouseEvent event, DrawingView view) {
        Figure o = getOwner();
        mouse = new Point2D(event.getX(),event.getY());
        // Only perform a rotation when the figure does not have a 
        // translation transform.
        /*if (ot.getTx() == 0.0 && ot.getTy() == 0.0) */{
            // The approach with Geom.angle only works if the figure does not have
            // a translation transform.
            Point2D newPoint = startTransform.transform(mouse);
            double deltaRotate = 90 + 180.0 / Math.PI * Geom.angle(center.getX(), center.getY(), newPoint.getX(), newPoint.getY());
            double newRotate = deltaRotate/* + startRotation*/;

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
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
        // FIXME fire undoable edit
    }

    @Override
    public TransformableFigure getOwner() {
        return (TransformableFigure) super.getOwner();
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    private Point2D getLocation(DrawingView view) {
        Figure owner = getOwner();
        Bounds bounds = owner.getBoundsInLocal();
        Point2D shift = view.getViewToWorld().deltaTransform(10, 10);
        return new Point2D(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() - shift.getY());
    }

    @Override
    public Point2D getLocationInView() {
        return pickLocation;
    }

}
