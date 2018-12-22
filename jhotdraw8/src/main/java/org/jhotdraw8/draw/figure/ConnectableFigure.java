/* @(#)ConnectableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.connector.Connector;

/**
 * ConnectableFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ConnectableFigure extends Figure {

    /**
     * Gets a connector for this figure at the given location.
     *
     * @param pointInLocal the location of the connector in local coordinates.
     * @param connectingFigure The connecting figure or null if unknown. This
     * allows for specific connectors for different connection figures.
     * @return Returns the connector. Returns null if there is no connector at
     * the given location.
     */
    @Nullable
    Connector findConnector( Point2D pointInLocal,  Figure connectingFigure);

    /**
     * Gets a connector for this figure at the given location.
     *
     * @param x the location of the connector in local coordinates.
     * @param y the location of the connector in local coordinates.
     * @param prototype The prototype used to create a connection or null if
     * unknown. This allows for specific connectors for different connection
     * figures.
     * @return Returns the connector. Returns null if there is no connector at
     * the given location.
     */
    @Nullable
    default Connector findConnector(double x, double y, @Nullable Figure prototype) {
        return findConnector(new Point2D(x, y), prototype);
    }

}
