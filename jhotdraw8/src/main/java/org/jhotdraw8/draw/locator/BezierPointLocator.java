/* @(#)BezierPointLocator.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.locator;

import javafx.geometry.Point2D;
import javax.annotation.Nonnull;
import org.jhotdraw8.draw.figure.SimpleBezierFigure;
import org.jhotdraw8.draw.figure.Figure;

/**
 * A {@link Locator} which locates a node on the bezier path of a SimpleBezierFigure.
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

    @Nonnull
    @Override
    public Point2D locate(Figure owner) {
        SimpleBezierFigure plf = (SimpleBezierFigure) owner;
        if (index < plf.getNodeCount()) {
            return plf.getPoint(index, coord);
        }
        return new Point2D(0, 0);
    }
}
