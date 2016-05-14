/* @(#)ConnectionPointHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.util.List;
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
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import static org.jhotdraw.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.model.DrawingModel;

/**
 * Handle for the start or end point of a connection figure.
 * <p>
 * Pressing the alt or the control key while dragging the handle prevents
 * connecting the point.
 * <p>
 * This handle is drawn using a {@code Region}, which can be styled using
 * {@code styleclassDisconnected} and {@code styleclassConnected} given in the
 * constructor.
 *
 * @author werni
 */
public class ConnectionPointHandle extends AbstractHandle {

    private final MapAccessor<Point2D> pointKey;
    private final MapAccessor<Connector> connectorKey;

    private final Region node;
    private final String styleclassDisconnected;
    private final String styleclassConnected;

    private static final Circle REGION_SHAPE = new Circle(3);
    private Point2D pickLocation;
    private static final Background REGION_BACKGROUND_DISCONNECTED = new Background(new BackgroundFill(Color.WHITE, null, null));
    private static final Background REGION_BACKGROUND_CONNECTED = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));

    public ConnectionPointHandle(Figure figure, MapAccessor<Point2D> pointKey,
            MapAccessor<Connector> connectorKey) {
        this(figure, STYLECLASS_HANDLE_CONNECTION_POINT_DISCONNECTED, STYLECLASS_HANDLE_CONNECTION_POINT_CONNECTED, pointKey,
                connectorKey);
    }

    public ConnectionPointHandle(Figure figure, String styleclassDisconnected, String styleclassConnected, MapAccessor<Point2D> pointKey,
            MapAccessor<Connector> connectorKey) {
        super(figure);
        this.pointKey = pointKey;
        this.connectorKey = connectorKey;
        this.styleclassDisconnected = styleclassDisconnected;
        this.styleclassConnected = styleclassConnected;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(10, 10);
        node.getStyleClass().clear();
        node.getStyleClass().add(styleclassDisconnected);
        node.setBorder(REGION_BORDER);
    }

    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = view.getWorldToView().createConcatenation(f.getLocalToWorld());
        Point2D p = f.get(pointKey);
        pickLocation = p = t.transform(p);
        boolean isConnected = f.get(connectorKey) != null;
        node.setBackground(isConnected ? REGION_BACKGROUND_CONNECTED : REGION_BACKGROUND_DISCONNECTED);
        node.getStyleClass().set(0, isConnected ? styleclassConnected : styleclassDisconnected);
        node.relocate(p.getX() - 5, p.getY() - 5);
        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
    }

    @Override
    public void onMouseDragged(MouseEvent event, DrawingView view) {
        Point2D pointInViewCoordinates = new Point2D(event.getX(), event.getY());
        Point2D newPoint = view.viewToWorld(pointInViewCoordinates);

        Point2D constrainedPoint;
        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            constrainedPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
        } else {
            constrainedPoint = newPoint;
        }

        Figure o = getOwner();
        Connector newConnector = null;
        Figure newConnectedFigure = null;
        if (!event.isMetaDown()) {
            List<Figure> list = view.findFigures(pointInViewCoordinates, true);
            for (Figure ff : list) {
                Point2D pointInLocal = ff.worldToLocal(newPoint);
                newConnector = ff.findConnector(pointInLocal, o);
                if (newConnector != null) {
                    newConnectedFigure = ff;
                    break;
                }
            }
        }

        DrawingModel model = view.getModel();
        model.set(o, pointKey, getOwner().worldToLocal(constrainedPoint));
        model.set(o, connectorKey, newConnector);
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    @Override
    public Point2D getLocationInView() {
        return pickLocation;
    }

}
