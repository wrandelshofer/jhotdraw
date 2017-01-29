/* @(#)LocatorHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Point2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.geom.Geom;

/**
 * A LocatorHandle implements a Handle by delegating the location requests to a
 * Locator object.
 *
 * @see Locator
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class LocatorHandle extends AbstractHandle {

    private Locator locator;

    /**
     * Initializes the LocatorHandle with the given Locator.
     *
     * @param owner the figure which owns the handle
     * @param l the location
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
    
    protected Point2D getLocation(DrawingView dv) {
return dv.worldToView(       owner.localToWorld(getLocation()));
    }
    
    
        @Override
    public boolean contains(DrawingView dv,double x, double y, double tolerance) {
        Point2D p = getLocation(dv);
       return Geom.length2(x, y, p.getX(), p.getY()) <= tolerance;
    }
}
