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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.TransformableFigure;
import static org.jhotdraw.draw.TransformableFigure.ROTATE;
import static org.jhotdraw.draw.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.geom.Geom;

/**
 * A Handle to rotate a Figure.
 *
 * @author Werner Randelshofer
 */
public class RotateHandle extends AbstractHandle {

    private Point2D oldPoint;
    private final Region node;
    private final String styleclass;
    private static final Circle REGION_SHAPE = new Circle(5);
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.PURPLE, BorderStrokeStyle.SOLID, null, null));
    private Point2D center;
        protected Set<Figure> groupReshapeableFigures;
        
    public RotateHandle(TransformableFigure figure) {
        this(figure, STYLECLASS_HANDLE_ROTATE);
    }

    public RotateHandle(TransformableFigure figure, String styleclass) {
        super(figure);
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(10, 10);
        node.getStyleClass().clear();
        node.getStyleClass().add(styleclass);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
        node.setCursor(Cursor.CROSSHAIR);
    }

    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = view.getWorldToView().createConcatenation(f.getLocalToWorld());
        Bounds b = f.getBoundsInLocal();
        Point2D p = getLocation();
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        p = t.transform(p);
        node.relocate(p.getX() - 5, p.getY() - 5);
        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
        oldPoint = view.getConstrainer().constrainPoint(getOwner(), view.viewToWorld(new Point2D(event.getX(), event.getY())));
        center = getOwner().getCenterInLocal();
            // determine which figures can be reshaped together as a group
            Set<Figure> selectedFigures = view.getSelectedFigures();
            groupReshapeableFigures = new HashSet<>();
            for (Figure f :  view.getSelectedFigures()) {
                if (f.isGroupReshapeableWith(selectedFigures)) {
                    groupReshapeableFigures.add(f);
                }
            }
            groupReshapeableFigures=view.getFiguresWithCompatibleHandle(groupReshapeableFigures, this);

    }

    @Override
    public void onMouseDragged(MouseEvent event, DrawingView view) {
        // FIXME implement me!
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        double oldRotate = 90 + 180.0 / Math.PI * Geom.angle(center.getX(), center.getY(), oldPoint.getX(), oldPoint.getY());
        double newRotate = 90 + 180.0 / Math.PI * Geom.angle(center.getX(), center.getY(), newPoint.getX(), newPoint.getY());

        double ownerAngle = getOwner().get(TransformableFigure.ROTATE);

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
                if (f instanceof TransformableFigure)
                model.set(f, TransformableFigure.ROTATE, newRotate);
            }
        } else {
            model.set(getOwner(), TransformableFigure.ROTATE, newRotate);
        }

        oldPoint = newPoint;
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
        // FIXME fire undoable edit
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    private Point2D getLocation() {
        Figure owner = getOwner();
        Bounds bounds = owner.getBoundsInLocal();
        return new Point2D(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() - 10);
    }

}
