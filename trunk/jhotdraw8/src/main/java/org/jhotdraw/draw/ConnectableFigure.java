/* @(#)ConnectableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.geometry.Point2D;
import org.jhotdraw.draw.connector.Connector;

/**
 * Figures which can be connected with a {@code ConnectionFigure} implement
 * this interface.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ConnectableFigure extends Figure {
    /**
     * Gets a connector for this figure at the given location.
     * A figure can have different connectors at different locations.
     *
     * @param p the location of the connector.
     * @param prototype The prototype used to create a connection or null if 
     * unknown. This allows for specific connectors for different 
     * connection figures.
     * @return Returns the connector. Returns null if there is no connector
     * at the given location.
     */
    Connector findConnector(Point2D p, ConnectionFigure prototype);

    
}
