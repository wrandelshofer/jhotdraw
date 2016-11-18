/* @(#)BezierPointLocator.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */


package org.jhotdraw8.draw.locator;

import javafx.geometry.Point2D;
import org.jhotdraw8.draw.figure.BezierFigure;
import org.jhotdraw8.draw.figure.Figure;

/**
 * A {@link Locator} which locates a node on the bezier path of a BezierFigure.
 * 
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierPointLocator extends AbstractLocator {
    private static final long serialVersionUID = 1L;
    private int index;
    private int coord;
    
    public BezierPointLocator(int index) {
        this.index = index;
        this.coord = 0;
    }
    public BezierPointLocator(int index, int coord) {
        this.index = index;
        this.coord = index;
    }
    
    @Override
    public Point2D locate(Figure owner) {
        BezierFigure plf = (BezierFigure) owner;
        if (index < plf.getNodeCount()) {
            return plf.getPoint(index, coord);
        }
        return new Point2D(0, 0);
    }
}
