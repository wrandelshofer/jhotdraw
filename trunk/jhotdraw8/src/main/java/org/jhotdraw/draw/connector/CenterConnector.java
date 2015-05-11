/* @(#)CenterConnector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import javafx.beans.InvalidationListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw.beans.SimpleObservable;
import org.jhotdraw.draw.ConnectionFigure;
import static org.jhotdraw.draw.ConnectionFigure.START_FIGURE;
import org.jhotdraw.draw.Figure;
import static org.jhotdraw.collection.Key.*;

/**
 * CenterConnector.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CenterConnector extends AbstractConnector {

    @Override
    public void updateStartPosition(ConnectionFigure connection) {
        final Point2D center;
        final Figure startFigure = connection.get(ConnectionFigure.START_FIGURE);
        if (startFigure != null) {
            final Bounds b = startFigure.getLayoutBounds();
            center = new Point2D(b.getMinX() + b.getWidth() / 2.0, (b.getMinY()
                    + b.getHeight() / 2.0));
            connection.set(ConnectionFigure.START, center);
        }
    }

    @Override
    public void updateEndPosition(ConnectionFigure connection) {
        final Point2D center;
        final Figure endFigure = connection.get(ConnectionFigure.END_FIGURE);
        if (endFigure != null) {
            final Bounds b = endFigure.getLayoutBounds();
            center = new Point2D(b.getMinX() + b.getWidth() / 2.0, (b.getMinY()
                    + b.getHeight() / 2.0));
            connection.set(ConnectionFigure.END, center);
        }
    }
}
