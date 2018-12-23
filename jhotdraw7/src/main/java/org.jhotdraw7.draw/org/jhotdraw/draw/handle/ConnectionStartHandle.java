/* @(#)ConnectionStartHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.handle;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.connector.Connector;

import java.awt.geom.Point2D;

/**
 * A {@link Handle} which allows to connect the start of a
 * {@link ConnectionFigure} to another figure.
 *
 * @author Werner Randelshofer
 * @version $Id: ConnectionStartHandle.java -1   $
 */
public class ConnectionStartHandle extends AbstractConnectionHandle {
    
    /**
     * Constructs the connection handle for the given start figure.
     */
    public ConnectionStartHandle(ConnectionFigure owner) {
        super(owner);
    }
    
    /**
     * Sets the start of the connection.
     */
    @Override
    protected void connect(Connector c) {
        getOwner().setStartConnector(c);
    }
    
    /**
     * Disconnects the start figure.
     */
    @Override
    protected void disconnect() {
        getOwner().setStartConnector(null);
    }
    
    
    @Override
    protected Connector getTarget() {
        return getOwner().getStartConnector();
    }
    
    /**
     * Sets the start point of the connection.
     */
    @Override
    protected void setLocation(Point2D.Double p) {
        getOwner().willChange();
        getOwner().setStartPoint(p);
        getOwner().changed();
    }
    
    /**
     * Returns the start point of the connection.
     */
    @Override
    protected Point2D.Double getLocation() {
        return getOwner().getStartPoint();
    }
    
    @Override
    protected boolean canConnect(Connector existingEnd, Connector targetEnd) {
        return getOwner().canConnect(targetEnd, existingEnd);
    }

    @Override
    protected int getBezierNodeIndex() {
        return 0;
    }
    
}