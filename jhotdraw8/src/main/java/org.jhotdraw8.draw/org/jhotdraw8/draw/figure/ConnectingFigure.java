/*
 * @(#)ConnectingFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.draw.connector.Connector;

/**
 * ConnectingFigure.
 *
 * @author Werner Randelshofer
 */
public interface ConnectingFigure extends Figure {
    /**
     * Returns true if this figure can connect to the specified figure with the
     * specified connector.
     *
     * @param figure    The figure to which we want connect
     * @param connector The connector that we want to use
     * @return true if the connection is supported
     */
    default boolean canConnect(Figure figure, Connector connector) {
        return true;
    }


}
