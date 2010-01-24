/**
 *
 */
package org.jhotdraw.draw.connector;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;

/**
 *
 * The <code>ChopConnectorStrategy</code> connects to the Chop Point of the
 * owner figure.
 * <p>
 * The Chop Point is the the point where the boundary intersects the line
 * joining the centers of the connected figures. see
 * {@link ConnectorGeom#calculateChopPoint(java.awt.Shape, java.awt.Shape)}
 * <p>
 * It can be paired successfully with other strategies but note that it has only
 * a single distinct connector point. (
 * {@link ConnectorStrategy#hasSingularConnectorPoint}).
 * <p>
 * If the paired strategy {@link ConnectorStrategy#isConnectorTightlyCoupled}
 * and does not have extra logic to handle multiple connections, it is suitable
 * for one connection only between the figures.
 * <p>
 * Clearly only one connection is viable if the opposite connector also has this
 * strategy.
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *         <i> Code Line Length 120 </i>
 *         <p>
 *
 */
public class ChopConnectorStrategy extends BoundaryConnectorStrategy {

    // only created by ConnectorSubTracker
    protected ChopConnectorStrategy() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.AbstractConnectorStrategy#adjustConnectorForResizing
     * (org.jhotdraw.draw.RelativeConnector, org.jhotdraw.draw.ConnectionFigure,
     * java.awt.geom.Rectangle2D.Double)
     */
    @Override
    protected void adjustConnectorForResizing(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector) {
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
        final Point2D.Double p = ConnectorGeom.calculateChopPoint(getEffectiveShape(relativeConnector),
                oppositeStrategy.getEffectiveShape(oppositeConnector));

        final Point2D.Double p1 = ConnectorGeom.calculateChopPoint(oppositeStrategy
                .getEffectiveShape(oppositeConnector), getEffectiveShape(relativeConnector));
        updateConnectorPoint(p, relativeConnector);
        updateConnectorPoint(p1, oppositeConnector);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.BoundaryConnectorStrategy#adjustConnectionForPositionChange
     * (org.jhotdraw.draw.RelativeConnector, org.jhotdraw.draw.ConnectionFigure)
     */
    @Override
    protected void adjustConnectorsForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        for (final RelativeConnector relativeConnector : connectors) {
            final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
            final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
            final Point2D.Double p = ConnectorGeom.calculateChopPoint(getEffectiveShape(relativeConnector),
                    oppositeStrategy.getEffectiveShape(oppositeConnector));
            updateConnectorPoint(p, relativeConnector);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.BoundaryConnectorStrategy#adjustConnectionForPositionChange
     * (org.jhotdraw.draw.RelativeConnector, org.jhotdraw.draw.ConnectionFigure)
     */
    @Override
    protected void adjustConnectorsForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        for (final RelativeConnector relativeConnector : connectors) {
            final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
            final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
            final Point2D.Double p = ConnectorGeom.calculateChopPoint(getEffectiveShape(relativeConnector),
                    oppositeStrategy.getEffectiveShape(oppositeConnector));
            updateConnectorPoint(p, relativeConnector);
        }
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
        for (final RelativeConnector relativeConnector : connectors) {
            final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
            final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
            final Point2D.Double p = ConnectorGeom.calculateChopPoint(getEffectiveShape(relativeConnector),
                    oppositeStrategy.getEffectiveShape(oppositeConnector));
            updateConnectorPoint(p, relativeConnector);
            modifyOppositeConnectionPointMulti(relativeConnector);
        }
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
        for (final RelativeConnector relativeConnector : connectors) {
            final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
            final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
            final Point2D.Double p = ConnectorGeom.calculateChopPoint(getEffectiveShape(relativeConnector),
                    oppositeStrategy.getEffectiveShape(oppositeConnector));
            updateConnectorPoint(p, relativeConnector);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.AbstractConnectorStrategy#adjustConnectorForDragging
     * (org.jhotdraw.draw.RelativeConnector)
     */
    @Override
    protected Point2D.Double dragConnector(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector, Point2D.Double fromPoint, Point2D.Double toPoint, int modifiersEx) {
        return relativeConnector.getConnectorPoint();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.BoundaryConnectorStrategy#findConnectorPoint(org.jhotdraw
     * .draw.RelativeConnector, java.awt.geom.Point2D.Double,
     * org.jhotdraw.draw.Figure, org.jhotdraw.draw.ConnectionFigure, boolean)
     */
    @Override
    protected Point2D.Double findConnectorPoint(RelativeConnector relativeConnector, Point2D.Double p, Figure owner,
            ConnectionFigure connection, boolean isStartConnector) {
        Point2D.Double pt = super.findConnectorPoint(relativeConnector, p, owner, connection, isStartConnector);
        RelativeConnector oppositeConnector = null;
        if (isStartConnector)
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getEndConnector();
        else
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getStartConnector();

        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
        if (oppositeConnector != null)
            pt = ConnectorGeom.calculateChopPoint(getEffectiveShape(owner), oppositeStrategy
                    .getEffectiveShape(oppositeConnector));
        return pt;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.BoundaryConnectorStrategy#findConnectorPointNewConnection
     * (org.jhotdraw.draw.RelativeConnector, java.awt.geom.Point2D.Double,
     * org.jhotdraw.draw.Figure, org.jhotdraw.draw.ConnectionFigure, boolean)
     */
    @Override
    protected Point2D.Double findConnectorPointNewConnection(RelativeConnector relativeConnector, Point2D.Double p,
            Figure owner, Figure oppositeOwner, boolean start) {
        Point2D.Double pt = super.findConnectorPointNewConnection(relativeConnector, p, owner, oppositeOwner, start);
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
        pt = ConnectorGeom.calculateChopPoint(getEffectiveShape(relativeConnector), oppositeStrategy
                .getEffectiveShape(oppositeConnector));
        return pt;
    }

    @Override
    protected Point2D.Double findTransformedConnectorPoint(RelativeConnector relativeConnector,
            Rectangle2D.Double bounds) {
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
        return ConnectorGeom.calculateChopPoint(bounds, oppositeStrategy.getEffectiveShape(oppositeConnector));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jhotdraw.draw.ConnectorStrategy#isSingularConnectorPoint()
     */
    @Override
    protected boolean hasSingularConnectorPoint() {
        return true;
    }

    /**
     * @return false
     */
    @Override
    protected boolean isBoundsMode() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.BoundaryConnectorStrategy#adjustConnectorForResize(
     * org.jhotdraw.draw.RelativeConnector, org.jhotdraw.draw.ConnectionFigure)
     */
    @Override
    protected void slideConnector(ConnectorSubTracker connectorSubTracker, RelativeConnector relativeConnector) {
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
        final Point2D.Double p = ConnectorGeom.calculateChopPoint(getEffectiveShape(relativeConnector),
                oppositeStrategy.getEffectiveShape(oppositeConnector));
        updateConnectorPoint(p, relativeConnector);
    }
}
