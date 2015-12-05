/* @(#)BezierPointLocator.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */


package org.jhotdraw.draw.locator;

import javafx.geometry.Point2D;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.draw.Figure;

/**
 * A {@link Locator} which locates a node on a point of a Figure.
 * 
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PointLocator extends AbstractLocator {
    private static final long serialVersionUID = 1L;
    private MapAccessor<Point2D> key;
    
    public PointLocator(MapAccessor<Point2D> key) {
        this.key=key;
    }
    
    @Override
    public Point2D locate(Figure owner) {
        return owner.get(key);
    }
}
