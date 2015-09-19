/* @(#)ChopEllipseConnector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;

/**
 * ChopEllipseConnector.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ChopEllipseConnector extends AbstractConnector {
    @Override
    public void updateStartPosition(ConnectionFigure connection) {
        // FIXME - implement me properly
        final Point2D center;
        final Figure startFigure = connection.get(ConnectionFigure.START_FIGURE);
        if (startFigure != null) {
            final Bounds b = startFigure.getBoundsInLocal();
            center = new Point2D(b.getMinX() + b.getWidth() / 2.0, (b.getMinY()
                    + b.getHeight() / 2.0));
            connection.set(ConnectionFigure.START, center);
        }
    }

    @Override
    public void updateEndPosition(ConnectionFigure connection) {
        // FIXME - implement me properly
        final Point2D center;
        final Figure endFigure = connection.get(ConnectionFigure.END_FIGURE);
        if (endFigure != null) {
            final Bounds b = endFigure.getBoundsInLocal();
            center = new Point2D(b.getMinX() + b.getWidth() / 2.0, (b.getMinY()
                    + b.getHeight() / 2.0));
            connection.set(ConnectionFigure.END, center);
        }
    }
    protected Point2D chop(Figure target, Point2D from) {
        // FIXME - implement me
        return from;
    }

}
