package org.jhotdraw.draw.connector;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This strategy should be treated with caution
 *
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *         <i> Code line length 120 </i>
 *         <p>
 */
public class TestConnectorStrategy extends RotationalConnectorStrategy {

    /**
     *
     */
    public TestConnectorStrategy() {
    }

    @Override
    protected void adjustConnectorsForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        // check for vertex connectors on moving figure ... reverse if any found
        if (checkForVertexConnector(connectors))
            reverse(connectorSubTracker, connectors);
        super.adjustConnectorsForMoving(connectorSubTracker, connectors);
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.jhotdraw.draw.connector.RotationalConnectorStrategy#
     * adjustConnectorsForMovingOpposite
     * (org.jhotdraw.draw.connector.ConnectorSubTracker, java.util.Collection)
     */
    @Override
    protected void adjustConnectorsForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        for (final RelativeConnector relativeConnector : connectors) {
            final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
            updateConnectorPoint(project(oppositeConnector), relativeConnector);
        }
    }

    /*
     * This strategy is compatible only with itself or the
     * FixedBoundaryConnectorStrategy
     */
    @Override
    protected boolean compatibleWithNewOppositeStrategy(ConnectorStrategy newOppositeStrategy,
            RelativeConnector relativeConnector, boolean isStartConnector, List<String> compatibleMsgs, int changeCount) {
        boolean result = false;

        if (newOppositeStrategy.getName().equals(getName())
                || newOppositeStrategy.getName().equals("FixedBoundaryConnectorStrategy"))
            result = true;
        else {
            final String errorMsg = getName() + " is not compatible with " + newOppositeStrategy.getName();
            compatibleMsgs.add(errorMsg);
        }

        return result;

    }

    /*
     * This strategy is compatible only with itself or the
     * FixedBoundaryConnectorStrategy
     */
    @Override
    protected boolean compatibleWithOpposite(RelativeConnector relativeConnector, boolean isStartConnector,
            List<String> compatibleMsgs, int changeCount) {
        boolean result = false;
        RelativeConnector oppositeConnector = null;
        if (isStartConnector)
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getEndConnector();
        else
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getStartConnector();
        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
        if (oppositeStrategy.getName().equals(getName())
                || oppositeStrategy.getName().equals("FixedBoundaryConnectorStrategy"))
            result = true;
        else {
            final String errorMsg = getName() + " is not compatible with " + oppositeStrategy.getName();
            compatibleMsgs.add(errorMsg);
        }
        return result;

    }

    /**
     * @return true
     */
    @Override
    protected boolean isBoundsMode() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.jhotdraw.draw.connector.AbstractConnectorStrategy#
     * isConnectorTightlyCoupled()
     */
    @Override
    protected boolean isConnectorTightlyCoupled() {
        return false;
    }

    /**
     * Vertex connectors are detected on the moving figure which implies the
     * user has swapped figures.
     * <p>
     * For connectors whose opposite strategy differs from this strategy
     * transform their connector points to the 'preferred positions'.
     * {@link ConnectorGeom#findPreferredPosition}.
     * <p>
     * For connectors whose opposite strategy is the same as this strategy call
     * 'reverseProject'
     *
     * @param connectorSubTracker
     * @param connectors
     */
    protected void reverse(ConnectorSubTracker connectorSubTracker, Collection<RelativeConnector> connectors) {
        final RelativeConnector relativeConnector = connectors.iterator().next();
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);

        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
        final Rectangle2D.Double r2 = ConnectorSubTracker.findOppositeBounds(relativeConnector);

        final Collection<RelativeConnector> oppositeConnectors = new ArrayList<RelativeConnector>();
        for (final RelativeConnector c : connectors) {
            final RelativeConnector oppC = ConnectorSubTracker.findOppositeConnector(c);
            oppositeConnectors.add(oppC);
        }

        final Rectangle2D.Double r22 = ConnectorGeom.findPreferredPosition(r2, r1, true);
        for (final RelativeConnector oppC : oppositeConnectors) {
            final Point2D.Double p22 = oppositeStrategy.findTransformedConnectorPoint(oppC, r22);
            final Point2D.Double p1 = ConnectorGeom.project(p22, r1);
            final RelativeConnector c = ConnectorSubTracker.findOppositeConnector(oppC);
            updateConnectorPoint(p1, c);

            if (this.getName().equals(oppositeStrategy.getName())) {
                final double deltaCL = ConnectorGeom.calculateCLAngleChange(r1, r1, r2, r22);
                final Point2D.Double p11 = rotateNormalizedPoint(c, deltaCL);
                updateConnectorPoint(p11, c);
            }

        }
    }
}
