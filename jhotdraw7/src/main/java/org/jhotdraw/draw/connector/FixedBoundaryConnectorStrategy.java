package org.jhotdraw.draw.connector;

import java.util.Collection;

/**
 * This strategy keeps connector points fixed unless they are deliberately dragged.
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
 *
 *
 */
public class FixedBoundaryConnectorStrategy extends BoundaryConnectorStrategy {

    // only created by ConnectorSubTracker
    protected FixedBoundaryConnectorStrategy() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#adjustConnectorForMoving(org.jhotdraw
     * .draw.RelativeConnector, double[], java.util.Collection,
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
     * org.jhotdraw.draw.AbstractConnectorStrategy#adjustOppositeConnectorForMoving
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
     * org.jhotdraw.draw.AbstractConnectorStrategy#adjustConnectorForDragging
     * (org.jhotdraw.draw.RelativeConnector)
     */
//    @Override
//    protected Point2D.Double dragConnector(ConnectorSubTracker connectorSubTracker,
//            RelativeConnector relativeConnector, Point2D.Double fromPoint,
//            Point2D.Double toPoint, int modifiersEx) {
//        return toPoint;
//    }

    /**
     * @return false
     */
    @Override
    protected boolean isBoundsMode() {
        return false;
    }
}
