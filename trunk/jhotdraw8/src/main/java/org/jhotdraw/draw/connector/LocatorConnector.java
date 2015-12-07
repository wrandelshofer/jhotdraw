/* @(#)LocatorConnector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.locator.Locator;

/**
 * LocatorConnector uses a {@link Locator} to compute its position.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class LocatorConnector extends AbstractConnector {

    private Locator locator;

    /**
     * Creates a new instance
     *
     * @param target the target figure
     * @param locator the locator that should be used
     */
    public LocatorConnector(Figure target, Locator locator) {
        super(target);
        this.locator = locator;
    }

    /** Returns the locator used to compute the position of the connector.
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
    public Point2D getPositionInLocal(Figure connection) {
        final Bounds b = target.getBoundsInLocal();
        return locator.locate(target);
    }

    @Override
    public Point2D chopStart(Figure connection, double startX, double startY, double endX, double endY) {
        return getPositionInDrawing(connection);
    }
}
