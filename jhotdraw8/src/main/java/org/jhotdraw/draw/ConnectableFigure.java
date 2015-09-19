/* @(#)ConnectableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.connector.Connector;

/**
 * Figures which can be connected with a {@code ConnectionFigure} implement
 * this interface.
 * <p>
 * If you remove a {@code ConnectableFigure} from a drawing, you must
 * set the corresponding {@code START_FIGURE} and {@code END_FIGURE} 
 * properties of the {@code ConnectionFigure}s in the connections list 
 * to null.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ConnectableFigure extends Figure {

    // ----
    // property names
    // ----

    /**
     * The name of the children property.
     */
    public final static String CONNECTIONS_PROPERTY = "connections";

    // ----
    // property fields
    // ----

    /**
     * The connection figures.
     * <p>
     * By convention this list is maintained by {@code ConnectionFigure}.
     * <p>
     * To remove a {@code ConnectionFigure} from this list set its corresponding
     * {@code START_FIGURE} or {@code END_FIGURE} property to null.
     *
     * @return the connections property, with {@code getBean()} returning this
     * figure, and {@code getName()} returning {@code CONNECTIONS_PROPERTY}.
     */
    ReadOnlyListProperty<ConnectionFigure> connectionsProperty();

    /**
     * Returns all connections of the figure.
     *
     * @return a list of the children
     */
    default ObservableList<ConnectionFigure> connections() {
        return connectionsProperty().get();
    }

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
