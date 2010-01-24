package org.jhotdraw.draw.connector;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;

/**
 * The <code>CenterConnectorStrategy</code> connects to the Center Point of the
 * owner figure.
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
 *
 */
public class CenterConnectorStrategy extends InteriorConnectorStrategy {

    // only created by ConnectorSubTracker
    protected CenterConnectorStrategy() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.AbstractConnectorStrategy#adjustConnectorForResizing
     * (org.jhotdraw.draw.RelativeConnector, java.awt.geom.Rectangle2D.Double)
     */
    @Override
    protected void adjustConnectorForResizing(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector) {
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        final double pX = r1.getCenterX();
        final double pY = r1.getCenterY();
        updateConnectorPoint(new Point2D.Double(pX, pY), relativeConnector);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.BoundaryConnectorStrategy#adjustConnectorForMovingHook
     * (org.jhotdraw.draw.RelativeConnector, java.util.Collection)
     */
    @Override
    protected void adjustConnectorsForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        for (final RelativeConnector relativeConnector : connectors) {
            final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
            final double pX = r1.getCenterX();
            final double pY = r1.getCenterY();
            updateConnectorPoint(new Point2D.Double(pX, pY), relativeConnector);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.AbstractConnectorStrategy#adjustConnectorForMovingHook
     * (org.jhotdraw.draw.RelativeConnector, java.util.Collection)
     */
    @Override
    protected void adjustConnectorsForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        for (final RelativeConnector relativeConnector : connectors) {
            final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
            final double pX = r1.getCenterX();
            final double pY = r1.getCenterY();
            updateConnectorPoint(new Point2D.Double(pX, pY), relativeConnector);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#adjustConnectorForMoving(org.jhotdraw
     * .draw.RelativeConnector, boolean, java.util.Collection)
     */
    @Override
    protected void adjustConnectorsMultiForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        for (final RelativeConnector relativeConnector : connectors) {
            final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
            final double pX = r1.getCenterX();
            final double pY = r1.getCenterY();
            updateConnectorPoint(new Point2D.Double(pX, pY), relativeConnector);
        }
    }

    @Override
    protected void adjustConnectorsMultiForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        for (final RelativeConnector relativeConnector : connectors) {
            final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
            final double pX = r1.getCenterX();
            final double pY = r1.getCenterY();
            updateConnectorPoint(new Point2D.Double(pX, pY), relativeConnector);
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
        final Point2D.Double pt = super.findConnectorPoint(relativeConnector, p, owner, connection, isStartConnector);
        pt.x = getEffectiveBounds(owner).getCenterX();
        pt.y = getEffectiveBounds(owner).getCenterY();
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
        final Point2D.Double pt = super.findConnectorPointNewConnection(relativeConnector, p, owner, oppositeOwner,
                start);
        pt.x = getEffectiveBounds(owner).getCenterX();
        pt.y = getEffectiveBounds(owner).getCenterY();
        return pt;
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.jhotdraw.draw.connector.AbstractConnectorStrategy#
     * findTransformedConnectorPoint
     * (org.jhotdraw.draw.connector.RelativeConnector,
     * java.awt.geom.Rectangle2D.Double)
     */
    @Override
    protected Point2D.Double findTransformedConnectorPoint(RelativeConnector relativeConnector,
            Rectangle2D.Double bounds) {
        final double pX = bounds.getCenterX();
        final double pY = bounds.getCenterY();
        return new Point2D.Double(pX, pY);
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
     * @return true/false
     */
    @Override
    protected boolean isBoundsMode() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.AbstractConnectorStrategy#slideConnector(org.jhotdraw
     * .draw.RelativeConnector, org.jhotdraw.draw.ConnectionFigure)
     */
    @Override
    protected void slideConnector(ConnectorSubTracker connectorSubTracker, RelativeConnector relativeConnector) {
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        final double pX = r1.getCenterX();
        final double pY = r1.getCenterY();
        final Point2D.Double p = new Point2D.Double(pX, pY);
        updateConnectorPoint(p, relativeConnector);
    }
}
