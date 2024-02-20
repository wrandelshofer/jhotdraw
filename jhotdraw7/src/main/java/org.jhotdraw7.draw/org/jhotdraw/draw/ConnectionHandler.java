package org.jhotdraw.draw;

import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class ConnectionHandler extends FigureAdapter implements Serializable {
    private static final long serialVersionUID = 1L;

    private LineConnectionFigure owner;

    public ConnectionHandler(LineConnectionFigure owner) {
        this.owner = owner;
    }

    @Override
    public void figureRemoved(FigureEvent evt) {
        // The commented lines below must stay commented out.
        // This is because, we must not set our connectors to null,
        // in order to support reconnection using redo.
            /*
            if (evt.getFigure() == owner.getStartFigure()
            || evt.getFigure() == owner.getEndFigure()) {
            owner.setStartConnector(null);
            owner.setEndConnector(null);
            }*/
        owner.fireFigureRequestRemove();
    }

    @Override
    public void figureChanged(FigureEvent e) {
        // Fixing the start point or end point depending on which figure was moved
        // Adding logic to make the owner connection to follow e.getFigure()
        // Depending on connection specifics, you may want to choose another point.

        // Fixing the start point or end point depending on which figure was moved
        if(e.getFigure() == owner.getStartFigure()){
            Connector connector = owner.getStartConnector();
            owner.willChange();
            owner.setStartPoint(connector.getAnchor());
            owner.updateConnection();
            owner.changed();
        }else if(e.getFigure() == owner.getEndFigure()){
            Connector connector = owner.getEndConnector();
            owner.willChange();
            owner.setEndPoint(connector.getAnchor());
            owner.updateConnection();
            owner.changed();
        }

        if (!owner.isChanging()) {
            System.out.println("figure changed 2: " + e.getFigure());
            if (e.getFigure() == owner.getStartFigure()
                    || e.getSource() == owner.getEndFigure()) {
                System.out.println("figure changed 3: " + e.getFigure());
                owner.willChange();
                owner.updateConnection();

                owner.changed();
            }
        }

    }
    public LineConnectionFigure getOwner() {
        return owner;
    }
};

