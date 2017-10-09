/* @(#)ConnectingFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javax.annotation.Nonnull;
import org.jhotdraw8.draw.connector.Connector;
import static org.jhotdraw8.draw.figure.AbstractLineConnectionFigure.END;
import static org.jhotdraw8.draw.figure.AbstractLineConnectionFigure.END_CONNECTOR;
import static org.jhotdraw8.draw.figure.AbstractLineConnectionFigure.END_TARGET;
import static org.jhotdraw8.draw.figure.AbstractLineConnectionFigure.START;
import static org.jhotdraw8.draw.figure.AbstractLineConnectionFigure.START_CONNECTOR;
import static org.jhotdraw8.draw.figure.AbstractLineConnectionFigure.START_TARGET;

/**
 * ConnectingFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ConnectingFigure extends Figure {
    /**
     * Returns true if this figure can connect to the specified figure with the
     * specified connector.
     *
     * @param figure The figure to which we want connect
     * @param connector The connector that we want to use
     * @return true if the connection is supported
     */
    default boolean canConnect(@Nonnull Figure figure, @Nonnull Connector connector) {
        return true;
    }
    
        /**
     * This method is called from DrawingModel, when the connected state of a figure has changed. This
     * implementation is empty.
     */
    default void connectNotify() {
    }


}
