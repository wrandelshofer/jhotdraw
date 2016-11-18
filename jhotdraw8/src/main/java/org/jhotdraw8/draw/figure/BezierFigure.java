/* @(#)BezierFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;

/**
 * BezierFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class BezierFigure implements Figure {

    public Point2D getPointOnPath(float f, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
// FIXME implement me

    public int getNodeCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Point2D getPoint(int index, int coord) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
