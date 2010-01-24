package org.jhotdraw.draw.connector;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.geom.Geom;

/**
 *
 * The <code>RotationalConnectorStrategy</code> maintains connections to figures
 * by <b>rotating the connection by the change in Center Line(CL) Angle.</b>
 * This applies to both the moving and stationary owning figures.
 * <p>
 * In non-bounds mode it is best suited for connections between elliptical
 * figures but can be used with other figures.
 * <p>
 * The separations between connections will <b>change continuously</b> and
 * connections are <b>not</b> restricted to just one side of either figure.
 * <p>
 * It can be paired successfully with other strategies for the same connection.
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *         <i> Code line length 120 </i>
 *         <p>
 *
 *
 */
public class RotationalConnectorStrategy extends BoundaryConnectorStrategy {

    // only created by ConnectorSubTracker
    protected RotationalConnectorStrategy() {
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
        double deltaCLAngle = 0;
        for (final RelativeConnector relativeConnector : connectors) {
            final Figure owner = relativeConnector.getOwner();
            final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
            final Figure oppositeOwner = oppositeConnector.getOwner();
            if (deltaCLAngle == 0)
                deltaCLAngle = ConnectorGeom.calculateCLAngleChange(owner.getBounds(), connectorSubTracker
                        .getPrevBounds(owner), oppositeOwner.getBounds(), connectorSubTracker
                        .getPrevBounds(oppositeOwner));
            final Point2D.Double rotPt = rotateNormalizedPoint(relativeConnector, deltaCLAngle);
            updateConnectorPoint(rotPt, relativeConnector);
        }
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
        double deltaCLAngle = 0;
        for (final RelativeConnector relativeConnector : connectors) {
            final Figure owner = relativeConnector.getOwner();
            final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
            final Figure oppositeOwner = oppositeConnector.getOwner();
            if (deltaCLAngle == 0)
                deltaCLAngle = ConnectorGeom.calculateCLAngleChange(owner.getBounds(), connectorSubTracker
                        .getPrevBounds(owner), oppositeOwner.getBounds(), connectorSubTracker
                        .getPrevBounds(oppositeOwner));
            final Point2D.Double rotPt = rotateNormalizedPoint(relativeConnector, deltaCLAngle);
            updateConnectorPoint(rotPt, relativeConnector);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.AbstractConnectorStrategy#findPriorConnectorPoint
     * (org.jhotdraw.draw.connector.RelativeConnector,
     * java.awt.geom.Rectangle2D.Double)
     */
    @Override
    protected Point2D.Double findTransformedConnectorPoint(RelativeConnector relativeConnector,
            Rectangle2D.Double bounds) {
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        final Rectangle2D.Double r2 = ConnectorSubTracker.findOppositeBounds(relativeConnector);

        final double deltaCL = ConnectorGeom.calculateCLAngleChange(bounds, r1, r2, r2);
        final Point2D.Double connPt = relativeConnector.getConnectorPoint();
        final boolean leftRight = ConnectorGeom.onLeftRightSide(connPt, r1);
        Point2D.Double p1 = rotateNormalizedPoint(relativeConnector, deltaCL);
        p1.x = bounds.x + Geom.range(0, bounds.width, p1.x - r1.x);
        p1.y = bounds.y + Geom.range(0, bounds.height, p1.y - r1.y);
        if (ConnectorGeom.isVertexPoint(p1, r1))
            p1 = ConnectorGeom.makeNonVertex(p1, r1, leftRight, true);

        return p1;
    }

    /**
     * @return false
     */
    @Override
    protected boolean isBoundsMode() {
        return false;
    }
}
