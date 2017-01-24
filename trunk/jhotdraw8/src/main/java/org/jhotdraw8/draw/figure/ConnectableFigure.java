/* @(#)ConnectableFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import org.jhotdraw8.draw.connector.Connector;

/**
 * ConnectableFigure.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface ConnectableFigure extends Figure {

    /**
     * Gets a connector for this figure at the given location.
     *
     * @param pointInLocal the location of the connector in local coordinates.
     * @param prototype The prototype used to create a connection or null if
     * unknown. This allows for specific connectors for different connection
     * figures.
     * @return Returns the connector. Returns null if there is no connector at
     * the given location.
     */
    Connector findConnector(Point2D pointInLocal, Figure prototype);

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
    default Connector findConnector(double x, double y, Figure prototype) {
        return findConnector(new Point2D(x,y),prototype);
    }


}
