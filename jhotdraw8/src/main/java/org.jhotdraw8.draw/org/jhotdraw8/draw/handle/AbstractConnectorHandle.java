/*
 * @(#)AbstractConnectorHandle.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.figure.ConnectableFigure;
import org.jhotdraw8.draw.figure.ConnectingFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Geom;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handle for the start or end point of a connection figure.
 * <p>
 * Pressing the alt or the control key while dragging the handle prevents
 * connecting the point.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractConnectorHandle extends AbstractHandle {
    protected static class ConnectorAndConnectedFigure {
        final @NonNull Connector connector;
        final @NonNull Figure connectedFigure;

        public ConnectorAndConnectedFigure(@NonNull Connector connector, @NonNull Figure connectedFigure) {
            this.connector = connector;
            this.connectedFigure = connectedFigure;
        }

        public Connector getConnector() {
            return connector;
        }

        public Figure getConnectedFigure() {
            return connectedFigure;
        }
    }

    protected final @NonNull MapAccessor<Connector> connectorKey;
    protected final NonNullMapAccessor<CssPoint2D> pointKey;
    protected final @NonNull MapAccessor<Figure> targetKey;
    protected @Nullable Point2D connectorLocation;
    protected Point2D pickLocation;
    // private final Region connectorNode;
    // private final javafx.scene.Group groupNode;
    private boolean isConnected;
    private boolean isDragging;
    private boolean editable = true;
    private @Nullable Figure prevTarget;

    public AbstractConnectorHandle(@NonNull ConnectingFigure figure,
                                   NonNullMapAccessor<CssPoint2D> pointKey,
                                   @NonNull MapAccessor<Connector> connectorKey,
                                   @NonNull MapAccessor<Figure> targetKey) {
        super(figure);
        this.pointKey = pointKey;
        this.connectorKey = connectorKey;
        this.targetKey = targetKey;

        isConnected = figure.get(connectorKey) != null && figure.get(targetKey) != null;
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        boolean b = false;
        if (connectorLocation != null) {
            b = Geom.lengthSquared(x, y, connectorLocation.getX(), connectorLocation.getY()) <= tolerance * tolerance;
        }
        if (!b && pickLocation != null) {
            b = Geom.lengthSquared(x, y, pickLocation.getX(), pickLocation.getY()) <= tolerance * tolerance;
        }
        return b;
    }

    @Override
    public Cursor getCursor() {
        return isConnected && isDragging ? Cursor.HAND : Cursor.HAND;
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }

    @Override
    public @NonNull ConnectingFigure getOwner() {
        return (ConnectingFigure) super.getOwner();
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

    @Override
    public void onMouseDragged(@NonNull MouseEvent event, @NonNull DrawingView view) {
        if (!editable) {
            return;
        }
        isDragging = true;
        Point2D pointInViewCoordinates = new Point2D(event.getX(), event.getY());
        Point2D unconstrainedPoint = view.viewToWorld(pointInViewCoordinates);

        CssPoint2D constrainedPoint;
        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            constrainedPoint = view.getConstrainer().constrainPoint(owner, new CssPoint2D(unconstrainedPoint));
        } else {
            constrainedPoint = new CssPoint2D(unconstrainedPoint);
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
            // Shift prevents search for another target figure
            if (event.isShiftDown()) {
                newConnectedFigure = prevTarget;
                ConnectableFigure cff = (ConnectableFigure) prevTarget;
                Point2D pointInLocal = cff.worldToLocal(unconstrainedPoint);
                final ConnectorAndConnectedFigure connectorAndConnectedFigure = find(constrainedPoint, o, cff, event);
                newConnector = connectorAndConnectedFigure == null ? null : connectorAndConnectedFigure.getConnector();
                if (newConnector != null && o.canConnect(cff, newConnector)) {
                    newConnectedFigure = connectorAndConnectedFigure.getConnectedFigure();
                    constrainedPoint = new CssPoint2D(newConnector.getPositionInLocal(o, cff));
                    isConnected = true;
                }
            } else {
                List<Figure> list;
                list = view.findFigures(pointInViewCoordinates, true)
                .stream().map(Map.Entry::getKey).collect(Collectors.toList());

                SearchLoop:
                for (Figure f1 : list) {
                    for (Figure ff : f1.breadthFirstIterable()) {
                        if (this.owner != ff && (ff instanceof ConnectableFigure)) {
                            ConnectableFigure cff = (ConnectableFigure) ff;
                            Point2D pointInLocal = cff.worldToLocal(unconstrainedPoint);
                            if (ff.getBoundsInLocal().contains(pointInLocal)) {
                                final ConnectorAndConnectedFigure connectorAndConnectedFigure = find(constrainedPoint, o, cff, event);
                                newConnector = connectorAndConnectedFigure == null ? null : connectorAndConnectedFigure.getConnector();
                                if (newConnector != null && o.canConnect(ff, newConnector)) {
                                    newConnectedFigure = connectorAndConnectedFigure.getConnectedFigure();
                                    constrainedPoint = new CssPoint2D(newConnector.getPositionInLocal(o, ff));
                                    isConnected = true;
                                    break SearchLoop;
                                }
                            }
                        }
                    }
                }
            }
        }

        model.set(o, pointKey, owner.worldToLocal(constrainedPoint));
        model.set(o, connectorKey, newConnector);
        model.set(o, targetKey, newConnectedFigure);
    }

    protected ConnectorAndConnectedFigure find(CssPoint2D constrainedPoint, ConnectingFigure o, ConnectableFigure cff, MouseEvent mouseEvent) {
        final Connector connector = cff.findConnector(cff.worldToLocal(constrainedPoint.getConvertedValue()), o);
        return connector == null ? null : new ConnectorAndConnectedFigure(connector, cff);
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
        prevTarget = owner.get(targetKey);
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
        isDragging = false;
    }

}
