package org.jhotdraw.draw.liner;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.connector.ConnectorGeom;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
import org.jhotdraw.xml.DOMStorable;

/**
 * @author cfm1
 *
 */
public class SelfConnectionLiner implements Liner, DOMStorable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public SelfConnectionLiner() {
    }


    /* (non-Javadoc)
     * @see org.jhotdraw.draw.liner.Liner#createHandles(org.jhotdraw.geom.BezierPath)
     */
    public Collection<Handle> createHandles(BezierPath path) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Liner clone() {
        try {
            return (Liner) super.clone();
        } catch (CloneNotSupportedException ex) {
            InternalError error = new InternalError(ex.getMessage());
            error.initCause(ex);
            throw error;
        }
    }


    /* (non-Javadoc)
     * @see org.jhotdraw.draw.liner.Liner#lineout(org.jhotdraw.draw.ConnectionFigure)
     */
    public void lineout(ConnectionFigure conn) {
        LineConnectionFigure connection = (LineConnectionFigure)conn;
        BezierPath path = connection.getBezierPath();
        Connector startConnector = connection.getStartConnector();
        Connector endConnector = connection.getEndConnector();
        if (startConnector == null || endConnector == null) {
            return;
        }
        if (connection.getStartFigure() != connection.getEndFigure())
            return;

        Figure owner = connection.getStartFigure();
        final Rectangle2D.Double r1 = owner.getBounds();
        final Point2D.Double p1 = ConnectorGeom.angleToPointGeom(r1,
                ConnectorGeom.pointToAngleGeom(r1, connection.getPoint(1)));

        connection.removeAllNodes();
        path.moveTo(p1.x, p1.y);
        double deltaX = Math.min(64, r1.width);
        double deltaY = Math.min(64, r1.height);

        if (r1.x + r1.width / 2 > p1.x)
            deltaX = -deltaX;
        if (r1.y + r1.height / 2 > p1.y)
            deltaY = -deltaY;

        path.curveTo(p1.x + deltaX, p1.y - deltaY, p1.x + deltaX,
                p1.y + deltaY, p1.x, p1.y);

        path.invalidatePath();
    }

    public void read(DOMInput in) {
    }

    public void write(DOMOutput out) {
    }
}
