/*
 * @(#)BezierPathOutlineHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.BezierNodePath;
import org.jhotdraw8.geom.Intersection;

public class BezierPathEditHandle extends PathIterableOutlineHandle {
    final private MapAccessor<ImmutableList<BezierNode>> pointKey;

    public BezierPathEditHandle(PathIterableFigure figure, MapAccessor<ImmutableList<BezierNode>> pointKey) {
        super(figure, true);
        this.pointKey = pointKey;
    }

    @Override
    public void handleMousePressed(@NonNull MouseEvent event, @NonNull DrawingView view) {
        if (event.isPopupTrigger()) {
            handlePopupTriggered(event, view);
        }
    }

    private void handlePopupTriggered(@NonNull MouseEvent event, @NonNull DrawingView view) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addPoint = new MenuItem(DrawLabels.getResources().getString("handle.addPoint.text"));

        addPoint.setOnAction(actionEvent -> {
            BezierNodePath path = new BezierNodePath(owner.get(pointKey));
            Point2D pointInLocal = owner.worldToLocal(view.viewToWorld(event.getX(), event.getY()));
            Intersection intersection = path.pathIntersection(pointInLocal.getX(), pointInLocal.getY(), 10.0);// / view.getZoomFactor());// FIXME tolerance not
            if (!intersection.isEmpty()) {
                Intersection.IntersectionPoint intersectionPoint = intersection.getIntersections().get(0);
                int segment = intersectionPoint.getSegment();
                path.getNodes().add(segment, new BezierNode(
                        pointInLocal));
                view.getModel().set(owner, pointKey, ImmutableLists.ofCollection(path.getNodes()));
                view.recreateHandles();
            }
        });


        contextMenu.getItems().add(addPoint);
        contextMenu.show(getNode(view), event.getX(), event.getScreenY());
        event.consume();
    }

    @Override
    public void handleMouseReleased(MouseEvent event, DrawingView view) {
        if (event.isPopupTrigger()) {
            handlePopupTriggered(event, view);
        }
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean isEditable() {
        return true;
    }
}
