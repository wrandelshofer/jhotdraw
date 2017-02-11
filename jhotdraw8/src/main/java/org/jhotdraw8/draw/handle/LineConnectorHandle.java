/* @(#)LineConnectorHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

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
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.figure.ConnectableFigure;
import org.jhotdraw8.draw.figure.ConnectingFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.LineConnectionFigure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

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
public class LineConnectorHandle extends AbstractHandle {

    private static final SVGPath PIVOT_NODE_SHAPE = new SVGPath();
    private static final Background REGION_BACKGROUND_CONNECTED = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final Background REGION_BACKGROUND_DISCONNECTED = new Background(new BackgroundFill(Color.WHITE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Circle REGION_SHAPE = new Circle(4);

    static {
        PIVOT_NODE_SHAPE.setContent("M-5,-1 L -1,-1 -1,-5 1,-5 1,-1 5,-1 5 1 1,1 1,5 -1,5 -1,1 -5,1 Z");
    }
    private final MapAccessor<Connector> connectorKey;
    private Point2D connectorLocation;

    private final Region connectorNode;
    private final javafx.scene.Group groupNode;
    private boolean isConnected;
    private boolean isDragging;
    private final Region lineNode;
    private Point2D pickLocation;
    private final MapAccessor<Point2D> pointKey;
    private final String styleclassConnected;
    private final String styleclassDisconnected;
    private final MapAccessor<Figure> targetKey;

    public LineConnectorHandle(ConnectingFigure figure, MapAccessor<Point2D> pointKey,
            MapAccessor<Connector> connectorKey, MapAccessor<Figure> targetKey) {
        this(figure, STYLECLASS_HANDLE_CONNECTION_POINT_DISCONNECTED, STYLECLASS_HANDLE_CONNECTION_POINT_CONNECTED, pointKey,
                connectorKey, targetKey);
    }

    public LineConnectorHandle(ConnectingFigure figure, String styleclassDisconnected, String styleclassConnected, MapAccessor<Point2D> pointKey,
            MapAccessor<Connector> connectorKey, MapAccessor<Figure> targetKey) {
        super(figure);
        this.pointKey = pointKey;
        this.connectorKey = connectorKey;
        this.targetKey = targetKey;
        this.styleclassDisconnected = styleclassDisconnected;
        this.styleclassConnected = styleclassConnected;
        lineNode = new Region();
        lineNode.setShape(REGION_SHAPE);
        lineNode.setManaged(false);
        lineNode.setScaleShape(false);
        lineNode.setCenterShape(true);
        lineNode.resize(10, 10);
        lineNode.getStyleClass().setAll(styleclassDisconnected, STYLECLASS_HANDLE);
        lineNode.setBorder(REGION_BORDER);

        connectorNode = new Region();
        connectorNode.setShape(PIVOT_NODE_SHAPE);
        connectorNode.setManaged(false);
        connectorNode.setScaleShape(false);
        connectorNode.setCenterShape(true);
        connectorNode.resize(10, 10);
        connectorNode.getStyleClass().setAll(styleclassDisconnected, STYLECLASS_HANDLE);
        connectorNode.setBorder(REGION_BORDER);
        connectorNode.setBackground(REGION_BACKGROUND_CONNECTED);
        groupNode = new javafx.scene.Group();
        groupNode.getChildren().addAll(connectorNode, lineNode);

        isConnected = figure.get(connectorKey) != null && figure.get(targetKey) != null;
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        boolean b = false;
        if (connectorLocation != null) {
            b = Geom.length2(x, y, connectorLocation.getX(), connectorLocation.getY()) <= tolerance;
        }
        if (!b && pickLocation != null) {
            b = Geom.length2(x, y, pickLocation.getX(), pickLocation.getY()) <= tolerance;
        }
        return b;
    }

    @Override
    public Cursor getCursor() {
        return isConnected || !isDragging ? Cursor.CROSSHAIR : Cursor.MOVE;
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }

    @Override
    public javafx.scene.Group getNode() {
        return groupNode;
    }

    @Override
    public ConnectingFigure getOwner() {
        return (ConnectingFigure) super.getOwner();
    }

    @Override
    public void handleMouseDragged(MouseEvent event, DrawingView view) {
        isDragging = true;
        Point2D pointInViewCoordinates = new Point2D(event.getX(), event.getY());
        Point2D newPoint = view.viewToWorld(pointInViewCoordinates);

        Point2D constrainedPoint;
        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            constrainedPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
        } else {
            constrainedPoint = newPoint;
        }

        ConnectingFigure o = getOwner();
        Connector newConnector = null;
        Figure newConnectedFigure = null;
        isConnected = false;
            // must clear end target, otherwise findConnector won't work as expected
        DrawingModel model = view.getModel();
            model.set(o, targetKey, null);
        if (!event.isMetaDown()) {
            List<Figure> list = view.findFigures(pointInViewCoordinates, true);
            for (Figure f1 : list) {
                for (Figure ff : f1.breadthFirstIterable()) {
                    if (ff instanceof ConnectableFigure) {
                        ConnectableFigure cff = (ConnectableFigure) ff;
                        Point2D pointInLocal = cff.worldToLocal(constrainedPoint);
                        if (ff.getBoundsInLocal().contains(pointInLocal)) {
                            newConnector = cff.findConnector(pointInLocal, o);
                            if (newConnector != null && o.canConnect(ff, newConnector)) {
                                newConnectedFigure = ff;
                                constrainedPoint = newConnector.getPositionInLocal(o, ff);
                                isConnected = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        model.set(o, pointKey, getOwner().worldToLocal(constrainedPoint));
        model.set(o, connectorKey, newConnector);
        model.set(o, targetKey, newConnectedFigure);
    }

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
    }

    @Override
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
        isDragging = false;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Point2D p = f.get(pointKey);
        pickLocation = p = t == null ? p : t.transform(p);
        boolean isConnected = f.get(connectorKey) != null && f.get(targetKey) != null;
        lineNode.setBackground(isConnected ? REGION_BACKGROUND_CONNECTED : REGION_BACKGROUND_DISCONNECTED);
        lineNode.getStyleClass().set(0, isConnected ? styleclassConnected : styleclassDisconnected);
        lineNode.relocate(p.getX() - 5, p.getY() - 5);
        // rotates the node:
        lineNode.setRotate(f.getStyled(ROTATE));
        lineNode.setRotationAxis(f.getStyled(ROTATION_AXIS));

        connectorNode.setVisible(isConnected);
        if (isConnected) {
            connectorLocation = view.worldToView(f.get(connectorKey).getPositionInWorld(owner, f.get(targetKey)));
            if (isConnected) {
                connectorNode.relocate(connectorLocation.getX() - 5, connectorLocation.getY() - 5);
            }
        } else {
            connectorLocation = null;
        }

        groupNode.getChildren().clear();
        groupNode.getChildren().addAll(connectorNode, lineNode);
    }

}
