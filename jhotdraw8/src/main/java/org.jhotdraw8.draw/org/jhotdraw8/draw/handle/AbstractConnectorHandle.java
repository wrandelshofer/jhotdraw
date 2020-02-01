/*
 * @(#)AbstractConnectorHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
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

/**
 * Handle for the start or end point of a connection figure.
 * <p>
 * Pressing the alt or the control key while dragging the handle prevents
 * connecting the point.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractConnectorHandle extends AbstractHandle {

    @NonNull
    protected final MapAccessor<Connector> connectorKey;
    @Nullable
    protected Point2D connectorLocation;

    // private final Region connectorNode;
    // private final javafx.scene.Group groupNode;
    private boolean isConnected;
    private boolean isDragging;
    protected Point2D pickLocation;
    protected final NonNullMapAccessor<CssPoint2D> pointKey;
    @NonNull
    protected final MapAccessor<Figure> targetKey;
    private boolean editable = true;

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
    public boolean contains(DrawingView dv, double x, double y, double toleranceSquared) {
        boolean b = false;
        if (connectorLocation != null) {
            b = Geom.length2(x, y, connectorLocation.getX(), connectorLocation.getY()) <= toleranceSquared;
        }
        if (!b && pickLocation != null) {
            b = Geom.length2(x, y, pickLocation.getX(), pickLocation.getY()) <= toleranceSquared;
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


    @NonNull
    @Override
    public ConnectingFigure getOwner() {
        return (ConnectingFigure) super.getOwner();
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
            List<Figure> list = view.findFigures(pointInViewCoordinates, true);

            SearchLoop:
            for (Figure f1 : list) {
                for (Figure ff : f1.breadthFirstIterable()) {
                    if (this.owner != ff && (ff instanceof ConnectableFigure)) {
                        ConnectableFigure cff = (ConnectableFigure) ff;
                        Point2D pointInLocal = cff.worldToLocal(unconstrainedPoint);
                        if (ff.getBoundsInLocal().contains(pointInLocal)) {
                            newConnector = cff.findConnector(cff.worldToLocal(constrainedPoint.getConvertedValue()), o);
                            if (newConnector != null && o.canConnect(ff, newConnector)) {
                                newConnectedFigure = ff;
                                constrainedPoint = new CssPoint2D(newConnector.getPositionInLocal(o, ff));
                                isConnected = true;
                                break SearchLoop;
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

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
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
