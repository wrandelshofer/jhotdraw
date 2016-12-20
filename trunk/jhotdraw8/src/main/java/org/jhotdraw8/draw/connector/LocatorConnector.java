/* @(#)LocatorConnector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.locator.Locator;

/**
 * LocatorConnector uses a {@link Locator} to compute its position.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LocatorConnector extends AbstractConnector {

    private Locator locator;

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

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public Point2D getPositionInLocal(Figure connection, Figure target) {
        final Bounds b = target.getBoundsInLocal();
        return locator.locate(target);
    }

    @Override
    public Point2D chopStart(Figure connection, Figure target, double startX, double startY, double endX, double endY) {
        return getPositionInWorld(connection, target);
    }
}
