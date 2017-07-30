/* @(#)Path2DDoubleBuilder.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.geom;

import java.awt.geom.Path2D;

/**
 * Path2DDoubleBuilder.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Path2DDoubleBuilder extends AbstractPathBuilder {
private Path2D.Double path= new Path2D.Double();
    @Override
    protected void doClosePath() {
        path.closePath();
    }

    @Override
    protected void doCurveTo(double x, double y, double x0, double y0, double x1, double y1) {
        path.curveTo(x, y, x0, y0, x1, y1);
    }

    @Override
    protected void doLineTo(double x, double y) {
        path.lineTo(x, y);
    }

    @Override
    protected void doMoveTo(double x, double y) {
        path.moveTo(x, y);        
    }

    @Override
    protected void doQuadTo(double x, double y, double x0, double y0) {
        path.quadTo(x, y, x0, y0);
    }

    public Path2D.Double get() {
        return path;
    }
    @Override
    protected void doFinish() {
      // empty
    }

}
