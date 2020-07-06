/*
 * @(#)LocatorHandle.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.geom.Geom;

/**
 * A LocatorHandle implements a Handle by delegating the location requests to a
 * Locator object.
 *
 * @author Werner Randelshofer
 * @see Locator
 */
public abstract class LocatorHandle extends AbstractHandle {

    private Locator locator;

    /**
     * Initializes the LocatorHandle with the given Locator.
     *
     * @param owner the figure which owns the handle
     * @param l     the location
     */
    public LocatorHandle(Figure owner, Locator l) {
        super(owner);
        locator = l;
    }

    /**
     * Returns the location in local figure coordinates.
     *
     * @return the location
     */
    protected Point2D getLocation() {
        return locator.locate(owner);
    }

    protected Point2D getLocation(@NonNull DrawingView dv) {
        return dv.worldToView(owner.localToWorld(getLocation()));
    }


    @Override
    public boolean contains(@NonNull DrawingView dv, double x, double y, double tolerance) {
        Point2D p = getLocation(dv);
        return Geom.lengthSquared(x, y, p.getX(), p.getY()) <= tolerance * tolerance;
    }
}
