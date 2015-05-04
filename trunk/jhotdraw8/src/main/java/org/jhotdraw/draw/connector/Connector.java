/* @(#)Connector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import javafx.beans.Observable;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.ConnectionFigure;

/**
 * A <em>connector</em> encapsulates the strategy for locating the
 * start or end point of a {@code ConnectionFigure}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Connector extends Observable {
    /**
     * Updates the start position of the specified connection figure.
     */
    public void updateStartPosition(ConnectionFigure connection);
    
    /**
     * Updates the end position of the specified connection figure.
     */
    public void updateEndPosition(ConnectionFigure connection);

}
