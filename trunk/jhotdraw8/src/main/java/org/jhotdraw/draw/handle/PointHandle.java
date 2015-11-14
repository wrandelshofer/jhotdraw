/* @(#)ConnectionFigureConnectionHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

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
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import static org.jhotdraw.draw.Figure.ROTATE;
import static org.jhotdraw.draw.Figure.ROTATION_AXIS;
import org.jhotdraw.draw.key.SimpleFigureKey;

/**
 * Handle for the point of a figure.
 *
 * @author Werner Randelshofer
 */
public class PointHandle extends AbstractHandle {

    private final SimpleFigureKey<Point2D> pointKey;
    private final Region node;
    private final String styleclass;
    private static final Rectangle REGION_SHAPE = new Rectangle(7, 7);
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE,  BorderStrokeStyle.SOLID, null, null));

    public PointHandle(Figure figure, SimpleFigureKey<Point2D> pointKey) {
        this(figure,STYLECLASS_HANDLE_POINT,pointKey);
    }
    public PointHandle(Figure figure, String styleclass, SimpleFigureKey<Point2D> pointKey) {
        super(figure);
        this.pointKey = pointKey;
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11);
        node.getStyleClass().clear();
        node.getStyleClass().add(styleclass);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
        node.setCursor(Cursor.MOVE);
        // rotates the node:
        node.setRotate(figure.getStyled(ROTATE));
        node.setRotationAxis(figure.getStyled(ROTATION_AXIS));
    }

    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = view.getDrawingToView().createConcatenation(f.getLocalToDrawing());
        Point2D p = f.get(pointKey);
        p = t.transform(p);
        node.relocate(p.getX() - 5, p.getY() - 5);
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
    }

    @Override
    public void onMouseDragged(MouseEvent event, DrawingView view) {
        Point2D newPoint = view.viewToDrawing(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control switches the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
        }
        
        view.getModel().set(getOwner(), pointKey, getOwner().drawingToLocal(newPoint));
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
    }
    @Override
    public boolean isSelectable() {
        return true;
    }
}
