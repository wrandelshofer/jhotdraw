/**
 *
 */
package org.jhotdraw.draw.connector;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.geom.Geom;

/**
 * This strategy maintains connector points in the interior of the figure.
 * <p>
 * It is suited for connections on all figures.
 * <p>
 * It can be paired with other strategies.
 * <p>
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 */
public class InteriorConnectorStrategy extends AbstractConnectorStrategy {

    // only created by ConnectorSubTracker
    protected InteriorConnectorStrategy() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.AbstractConnectorStrategy#adjustConnectorForMoving(
     * org.jhotdraw.draw.RelativeConnector, double[], java.util.Collection,
     * java.util.HashMap)
     */
    @Override
    protected void adjustConnectorsForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        return;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.AbstractConnectorStrategy#adjustConnectorForMovingOpposite
     * (org.jhotdraw.draw.RelativeConnector, double[], java.util.Collection,
     * java.util.HashMap)
     */
    @Override
    protected void adjustConnectorsForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        return;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.AbstractConnectorStrategy#adjustConnectorMultiForMoving
     * (org.jhotdraw.draw.RelativeConnector, java.util.Collection,
     * java.util.HashMap)
     */
    @Override
    protected void adjustConnectorsMultiForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        return;
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.jhotdraw.draw.AbstractConnectorStrategy#
     * adjustConnectorMultiForMovingOpposite
     * (org.jhotdraw.draw.RelativeConnector, java.util.Collection,
     * java.util.HashMap)
     */
    @Override
    protected void adjustConnectorsMultiForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        return;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.AbstractConnectorStrategy#findConnectorPoint(org.jhotdraw
     * .draw.RelativeConnector, java.awt.geom.Point2D.Double,
     * org.jhotdraw.draw.Figure, org.jhotdraw.draw.ConnectionFigure, boolean)
     */
    @Override
    protected Point2D.Double findConnectorPoint(RelativeConnector relativeConnector, Point2D.Double p, Figure owner,
            ConnectionFigure connection, boolean isStartConnector) {
        final Rectangle2D.Double r1 = getEffectiveBounds(owner);
        final Point2D.Double pt = new Point2D.Double();
        pt.x = Geom.range(r1.x, r1.x + r1.width, p.x);
        pt.y = Geom.range(r1.y, r1.y + r1.height, p.y);
        return new Point2D.Double(pt.x, pt.y);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.AbstractConnectorStrategy#findConnectorPointNewConnection
     * (org.jhotdraw.draw.RelativeConnector, java.awt.geom.Point2D.Double,
     * org.jhotdraw.draw.Figure, org.jhotdraw.draw.ConnectionFigure, boolean)
     */
    @Override
    protected Point2D.Double findConnectorPointNewConnection(RelativeConnector relativeConnector, Point2D.Double p,
            Figure owner, Figure oppositeOwner, boolean start) {
        final Rectangle2D.Double r1 = getEffectiveBounds(owner);
        final Point2D.Double pt = new Point2D.Double();
        pt.x = Geom.range(r1.x, r1.x + r1.width, p.x);
        pt.y = Geom.range(r1.y, r1.y + r1.height, p.y);
        return new Point2D.Double(pt.x, pt.y);
    }

    /**
     * @return false
     */
    @Override
    protected boolean isBoundsMode() {
        return false;
    }

    /**
     * Resizing should preserve, if possible, the visual appearance of the
     * connections
     *
     * @param relativeConnector
     */
    @Override
    protected RelativeConnector preserveConnection(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector) {
        final Figure owner = relativeConnector.getOwner();
        Point2D.Double newPt = new Point2D.Double();
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final Point2D.Double oppPt = oppositeConnector.getConnectorPoint();
        final Point2D.Double prevPt = connectorSubTracker.getPrevPoint(relativeConnector);

        final Point2D.Double[] pt = ConnectorGeom.findIntersectionPoints(getEffectiveShape(owner), oppPt, prevPt);
        if (pt.length == 0) {
            return relativeConnector;
        }
        else
            if (pt.length == 1) {
                newPt = pt[0];
            }
            else {
                if (Geom.lineContainsPoint(pt[0].x, pt[0].y, pt[1].x, pt[1].y, prevPt.x, prevPt.y, 0.1))
                    newPt.setLocation(prevPt.x, prevPt.y);
                else {
                    final double d1 = Point2D.distance(prevPt.x, prevPt.y, pt[0].x, pt[0].y);
                    final double d2 = Point2D.distance(prevPt.x, prevPt.y, pt[1].x, pt[1].y);
                    newPt = d1 < d2 ? pt[0] : pt[1];
                }
            }
        if (newPt != null)
            updateConnectorPoint(newPt, relativeConnector);

        return relativeConnector;
    }

}
