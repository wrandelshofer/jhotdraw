/* @(#)CenterConnector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw8.draw.figure.Figure;

/**
 * CenterConnector.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CenterConnector extends AbstractConnector {

    @Override
    public Point2D getPositionInLocal(Figure connection, Figure target) {
        // FIXME implement me properly
        final Bounds b = target.getBoundsInLocal();
        return new Point2D(b.getMinX() + b.getWidth() / 2.0, (b.getMinY()
                + b.getHeight() / 2.0));
    }

    @Override
    public Point2D chopStart(Figure connection, Figure target, double startX, double startY, double endX, double endY) {
        return getPositionInWorld(connection, target);
    }
}
