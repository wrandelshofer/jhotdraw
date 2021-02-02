/*
 * @(#)LocatorConnector.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXGeom;
import org.jhotdraw8.geom.intersect.IntersectionPointEx;

/**
 * LocatorConnector uses a {@link Locator} to compute its position.
 *
 * @author Werner Randelshofer
 */
public class LocatorConnector extends AbstractConnector {

    private final Locator locator;

    /**
     * Creates a new instance
     *
     * @param locator the locator that should be used
     */
    public LocatorConnector(Locator locator) {
        this.locator = locator;
    }

    /**
     * Returns the locator used to compute the position of the connector.
     *
     * @return the locator
     */
    public Locator getLocator() {
        return locator;
    }

    @Nullable
    @Override
    public Point2D getPositionInLocal(Figure connection, @NonNull Figure target) {
        return locator.locate(target);
    }

    @Override
    public IntersectionPointEx chopStart(RenderContext ctx, Figure connection, @NonNull Figure target, double startX, double startY, double endX, double endY) {
        final Bounds b = target.getLayoutBounds();
        Point2D center = new Point2D(b.getMinX() + b.getWidth() * 0.5, b.getMinY() + b.getHeight() * 0.5);
        Point2D location = locator.locate(target);
        Point2D direction = location.subtract(center);
        Point2D tangent1 = new Point2D(direction.getY(), -direction.getX());
        Point2D tangent2 = new Point2D(direction.getX(), direction.getY());
        if (FXGeom.squaredMagnitude(tangent1) < 1e-6) {
            tangent1 = new Point2D(1, 0);
            tangent2 = new Point2D(0, -1);
        }

        Transform localToWorld = target.getLocalToWorld();
        Point2D targetP = target.localToWorld(location);
        Point2D t1p = localToWorld == null ? tangent1 : localToWorld.deltaTransform(tangent1);
        Point2D t2p = localToWorld == null ? tangent2 : localToWorld.deltaTransform(tangent2);
        return new IntersectionPointEx(
                targetP.getX(), targetP.getY(),
                0, t1p.getX(), t1p.getY(),
                0, t2p.getX(), t2p.getY());
    }
}
