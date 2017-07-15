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
import org.jhotdraw8.draw.figure.SimpleLineConnectionFigure;
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
public abstract class AbstractConnectorHandle extends AbstractHandle {

    protected final MapAccessor<Connector> connectorKey;
    protected Point2D connectorLocation;

    // private final Region connectorNode;
    // private final javafx.scene.Group groupNode;
    private boolean isConnected;
    private boolean isDragging;
    protected Point2D pickLocation;
    protected final MapAccessor<Point2D> pointKey;
    protected final String styleclassConnected;
    protected final String styleclassDisconnected;
    protected final MapAccessor<Figure> targetKey;
    private boolean editable=true;

    public AbstractConnectorHandle(ConnectingFigure figure, MapAccessor<Point2D> pointKey,
            MapAccessor<Connector> connectorKey, MapAccessor<Figure> targetKey) {
        this(figure, STYLECLASS_HANDLE_CONNECTION_POINT_DISCONNECTED, STYLECLASS_HANDLE_CONNECTION_POINT_CONNECTED, pointKey,
                connectorKey, targetKey);
    }

    public AbstractConnectorHandle(ConnectingFigure figure, String styleclassDisconnected, String styleclassConnected, MapAccessor<Point2D> pointKey,
            MapAccessor<Connector> connectorKey, MapAccessor<Figure> targetKey) {
        super(figure);
        this.pointKey = pointKey;
        this.connectorKey = connectorKey;
        this.targetKey = targetKey;
        this.styleclassDisconnected = styleclassDisconnected;
        this.styleclassConnected = styleclassConnected;

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
        return isConnected && isDragging ? Cursor.NONE : Cursor.MOVE;
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }


    @Override
    public ConnectingFigure getOwner() {
        return (ConnectingFigure) super.getOwner();
    }

    @Override
    public void handleMouseDragged(MouseEvent event, DrawingView view) {
        if (!editable)return;
        isDragging = true;
        Point2D pointInViewCoordinates = new Point2D(event.getX(), event.getY());
        Point2D unconstrainedPoint = view.viewToWorld(pointInViewCoordinates);

        Point2D constrainedPoint;
        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            constrainedPoint = view.getConstrainer().constrainPoint(getOwner(), unconstrainedPoint);
        } else {
            constrainedPoint = unconstrainedPoint;
        }

        ConnectingFigure o = getOwner();
        Connector newConnector = null;
        Figure newConnectedFigure = null;
        isConnected = false;
        // must clear end target, otherwise findConnector won't work as expected
        DrawingModel model = view.getModel();
        model.set(o, targetKey, null);
        // Meta prevents connection
        if (!event.isMetaDown()) {
            List<Figure> list = view.findFigures(pointInViewCoordinates, true);
            for (Figure f1 : list) {
                for (Figure ff : f1.breadthFirstIterable()) {
                    if (owner != ff && (ff instanceof ConnectableFigure)) {
                        ConnectableFigure cff = (ConnectableFigure) ff;
                        Point2D pointInLocal = cff.worldToLocal(unconstrainedPoint);
                        if (ff.getBoundsInLocal().contains(pointInLocal)) {
                            newConnector = cff.findConnector(cff.worldToLocal(constrainedPoint), o);
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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

}
