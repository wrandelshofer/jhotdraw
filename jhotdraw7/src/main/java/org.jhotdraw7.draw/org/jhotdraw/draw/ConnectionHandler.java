package org.jhotdraw.draw;

import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;

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
        if (!owner.isChanging()) {
            if (e.getSource() == owner.getStartFigure()
                    || e.getSource() == owner.getEndFigure()) {
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

