/* @(#)CenterConnector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.Figure;

/**
 * CenterConnector.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CenterConnector extends AbstractConnector {

    public CenterConnector(Figure target) {
        super(target);
    }

    @Override
    public Point2D getPosition(Figure connection) {
        // FIXME implement me properly
        final Bounds b = target.getBoundsInLocal();
        return target.localToDrawing(new Point2D(b.getMinX() + b.getWidth() / 2.0, (b.getMinY()
                + b.getHeight() / 2.0)));
    }

    @Override
    public Point2D chopStart(Figure connection, double startX, double startY, double endX, double endY) {
        return getPosition(connection);
    }
}
