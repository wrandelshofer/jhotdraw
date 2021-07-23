/*
 * @(#)BezierPathEditHandle.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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
import org.jhotdraw8.geom.intersect.IntersectionPoint;
import org.jhotdraw8.geom.intersect.IntersectionResult;

public class BezierPathEditHandle extends PathIterableOutlineHandle {
    private final MapAccessor<ImmutableList<BezierNode>> pointKey;

    public BezierPathEditHandle(PathIterableFigure figure, MapAccessor<ImmutableList<BezierNode>> pointKey) {
        super(figure, true);
        this.pointKey = pointKey;
    }

    @Override
    public void onMousePressed(@NonNull MouseEvent event, @NonNull DrawingView view) {
        if (event.isPopupTrigger()) {
            onPopupTriggered(event, view);
        }
    }

    private void onPopupTriggered(@NonNull MouseEvent event, @NonNull DrawingView view) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addPoint = new MenuItem(DrawLabels.getResources().getString("handle.addPoint.text"));

        addPoint.setOnAction(actionEvent -> {
            BezierNodePath path = new BezierNodePath(owner.get(pointKey));
            Point2D pointInLocal = owner.worldToLocal(view.viewToWorld(event.getX(), event.getY()));
            IntersectionResult intersectionResultEx = path.pathIntersection(pointInLocal.getX(), pointInLocal.getY(), 10.0);// / view.getZoomFactor());// FIXME tolerance not
            if (!intersectionResultEx.isEmpty()) {
                IntersectionPoint intersectionPointEx = intersectionResultEx.get(0);
                int segment = intersectionPointEx.getSegmentA();
                path.getNodes().add(segment, new BezierNode(
                        pointInLocal));
                view.getModel().set(owner, pointKey, ImmutableLists.ofCollection(path.getNodes()));
                view.recreateHandles();
            }
        });


        contextMenu.getItems().add(addPoint);
        contextMenu.show(getNode(view), event.getScreenX(), event.getScreenY());
        event.consume();
    }

    @Override
    public void onMouseReleased(@NonNull MouseEvent event, @NonNull DrawingView view) {
        if (event.isPopupTrigger()) {
            onPopupTriggered(event, view);
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
