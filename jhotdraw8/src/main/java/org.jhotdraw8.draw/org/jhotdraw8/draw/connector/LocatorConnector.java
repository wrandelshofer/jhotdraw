/*
 * @(#)LocatorConnector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Intersection;

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

    @Override
    public Point2D getPositionInLocal(Figure connection, @Nonnull Figure target) {
        return locator.locate(target);
    }

    @Override
    public Intersection.IntersectionPoint chopStart(Figure connection, @Nonnull Figure target, double startX, double startY, double endX, double endY) {
        final Bounds b = target.getBoundsInLocal();
        Point2D center = new Point2D(b.getMinX() + b.getWidth() * 0.5, b.getMinY() + b.getHeight() * 0.5);
        Point2D location = locator.locate(target);
        Point2D direction = location.subtract(center);
        Point2D tangent1 = new Point2D(direction.getY(), -direction.getX());
        Point2D tangent2 = new Point2D(direction.getX(), direction.getY());
        if (Geom.squaredMagnitude(tangent1) < 1e-6) {
            tangent1 = new Point2D(1, 0);
            tangent2 = new Point2D(0, -1);
        }

        Transform localToWorld = target.getLocalToWorld();
        return new Intersection.IntersectionPoint(
                target.localToWorld(location),
                0, localToWorld == null ? tangent1 : localToWorld.deltaTransform(tangent1),
                0, localToWorld == null ? tangent2 : localToWorld.deltaTransform(tangent2));
    }
}
