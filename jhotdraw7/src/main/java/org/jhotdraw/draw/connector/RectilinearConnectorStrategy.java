package org.jhotdraw.draw.connector;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.liner.CurvedLiner;

/**
 * The <code>RectilinearConnectorStrategy</code> maintains
 * <b>horizontal/vertical(x/y)</b> connections to figures.
 * <p>
 * The connector point on the stationary figure is the projection of the
 * connector point on the moving figure.
 * <p>
 * It is suited for connections between pairs of rectangular figures; even for
 * rectangles with markedly different dimensions.
 * <p>
 * <i>Note that if the strategy is operating in bounds mode all figures are
 * considered rectangular.</i> {@link ConnectorStrategy#isBoundsMode()},
 * <p>
 * In non-bounds mode (see {@link BoundaryConnectorStrategy}), it is <b>not</b>
 * suitable for connectors on <b>elliptical</b> figures.
 * <p>
 * The constraint in the super class {@link EdgeConnectorStrategy} that
 * connector points must be on one edge is relaxed but only to allow Vertex
 * Points {@link ConnectorGeom#isVertexPoint} which result from projecting the
 * moving connector.
 * <p>
 * Dragging a point to a non-connected side is not allowed in {@code
 * EdgeConnectorStrategy} and not allowed in this strategy.
 * <p>
 * In can be paired with other strategies.
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *         <i> Code line length 120 </i>
 *         <p>
 *
 */
public class RectilinearConnectorStrategy extends EdgeConnectorStrategy {

    // only created by ConnectorSubTracker
    protected RectilinearConnectorStrategy() {
    }

    /**
     * Adjusts connectors when the owner is moved.
     * <p>
     *
     * All connectors are checked and if a Vertex Connector
     * {@link ConnectorGeom#isVertexPoint} is detected, <i>reverse</i> is called
     * to adjust <i>(swap)</i> all owner and opposite owner connectors.
     * <p>
     *
     * All connectors are rotated when opposite sides pass.
     *
     */
    @Override
    protected void adjustConnectorsForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        // check for vertex connectors on moving figure ... reverse if any found
        if (checkForVertexConnector(connectors))
            reverse(connectorSubTracker, connectors);

        final double angle = checkRotationAngleForSide(connectors);
        if (Math.abs(angle) > 0)
            rotateNormalizedAllConnections(connectors, angle);
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.jhotdraw.draw.connector.EdgeConnectorStrategy#
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
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.AbstractConnectorStrategy#compatibleWithOpposite
     * (org.jhotdraw.draw.connector.RelativeConnector, boolean, java.util.List,
     * int)
     */
    @Override
    protected boolean compatibleWithOpposite(RelativeConnector relativeConnector, boolean isStartConnector,
            List<String> compatibleMsgs, int changeCount) {
        boolean result = true;
        final LineConnectionFigure connection = relativeConnector.getLineConnection();

        if (connection.getLiner() != null && connection.getLiner() instanceof CurvedLiner) {
            result = false;
            final String msg = getName() + " incompatible with CurvedLiner(BugFix!) ";
            compatibleMsgs.add(msg);
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
     * @see
     * org.jhotdraw.draw.connector.AbstractConnectorStrategy#isTightlyCoupled()
     */
    @Override
    protected boolean isConnectorTightlyCoupled() {
        return true;
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

        if (!oppositeStrategy.getName().equals(getName())) {
            final Rectangle2D.Double r22 = ConnectorGeom.findPreferredPosition(r2, r1, true);
            for (final RelativeConnector oppC : oppositeConnectors) {
                final Point2D.Double p22 = oppositeStrategy.findTransformedConnectorPoint(oppC, r22);
                final Point2D.Double p1 = ConnectorGeom.project(p22, r1);
                final RelativeConnector c = ConnectorSubTracker.findOppositeConnector(oppC);
                updateConnectorPoint(p1, c);
            }
        }
        else
            reverseProject(connectorSubTracker, connectors);
    }

    /**
     * Swaps, or reverse projects, the relative positions of the connector and
     * the opposite connector <b> if both owners have Effective Rectangular
     * Shapes and both connector and opposite connector have this strategy.</b>
     * <p>
     * Rotating, dragging and resizing are never allowed to create new Vertex
     * Points {@link ConnectorGeom#isVertexPoint}. If necessary the x or y
     * coordinate of a point is changed, by an amount not greater than
     * Math.abs(e2 - epsilon), so that the point will not be considered a Vertex
     * Point. {@link ConnectorGeom#e2} {@link ConnectorGeom#epsilon}
     * <p>
     * Vertex Points/Connectors <b>should</b> result from projections only and
     * projections are <b>always</b> to the stationary figure. If Vertex Points
     * are found on a moving figure it indicates that the user has switched
     * figures.
     * <p>
     * To properly manipulate the newly switched figure the relative positions
     * of the connector and opposite connector must be 'swapped' or reversed;
     * the Vertex connector must become the non-vertex connector and vice versa.
     * Otherwise projecting onto the opposite figure in
     * <code>adjustConnectorsForMovingOpposite</code> will not work.
     * <p>
     * This reverseProjection must recognize the fact that there can be a
     * mixture of Vertex and non-vertex connectors on the same figure. <i> (The
     * user has switched figures while the selected figure is partly in a vertex
     * region and partly in a projection region of the opposite figure)</i>
     * <p>
     * In this strategy the opposite point is the projection of the moving point
     * onto the stationary figure.<i> The moving point stays fixed relative to
     * the moving figure until it needs to be rotated. </i>For identical squares
     * and comparing relative positions, the opposite point and the moving point
     * are reflections (in the center point) of one another.
     * <p>
     * Alternatively, for any two rectangular figures, the opposite point is the
     * reflection, <b>under normalizeTransform</b> of the moving point in the
     * stationary figure.
     * <p>
     * The swapped positions can be determined from the composition of the
     * <code>reflection</code> and <code>normalizeTransform</code>
     * transformations of the opposite point. It does not matter in what order
     * these transformations are applied;
     *
     * <p>
     * Note: This method assumes that there is only one distinct Vertex
     * Point(<i>not to be confused with vertex connector of which there can be
     * multiple for the same Vertex Point</i>) to which some, or all, of the
     * connections are attached. Typically this is true but not always
     * especially when resizing or sliding; these methods can cause multiple
     * vertex points.
     * <p>
     * No attempt is made to control situations where there are multiple Vertex
     * Points or Vertex Point to Vertex Point connections.
     * <p>
     * TODO Investigate whether the reverse method using <i>preferred
     * positions</i> is sufficient and if this method can be dropped.
     *
     * @param connectors
     *            all the connectors have the same owner, owner strategy(this
     *            strategy), the same oppositeOwner and the same
     *            oppositeStrategy(this strategy).
     */
    protected void reverseProject(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        final RelativeConnector relativeConnector = connectors.iterator().next();
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        final int shapeType = getEffectiveShapeType(relativeConnector);

        RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
        final Rectangle2D.Double r2 = oppositeStrategy.getEffectiveBounds(oppositeConnector);
        final Shape oppositeShape = oppositeStrategy.getEffectiveShape(oppositeConnector);
        final int oppositeShapeType = oppositeStrategy.getEffectiveShapeType(oppositeConnector);

        final Collection<RelativeConnector> oppositeConnectors = new ArrayList<RelativeConnector>();
        for (final RelativeConnector c : connectors) {
            final RelativeConnector oppC = ConnectorSubTracker.findOppositeConnector(c);
            oppositeConnectors.add(oppC);
        }

        if (shapeType != ConnectorSubTracker.RECTANGULAR_SHAPE)
            return;

        // Build rotation rectangles r11 and r22
        Rectangle2D.Double r22 = r2;
        Rectangle2D.Double r11 = r1;
        if (connectors.size() > 1) {
            final double[] oppMaxMinConnectionPoints = findMaxMinConnectorPoints(oppositeConnectors);
            oppositeConnector = oppositeConnectors.iterator().next();
            r22 = buildMinimumSubRectangle(oppositeConnector, oppMaxMinConnectionPoints);
            // build rotation rectangle for r1 ... note how it is paired with
            // mapSubRectangle below
            r11 = new Rectangle2D.Double(r1.x, r1.y, Math.min(r1.width, r22.width),
                                                         Math.min(r1.height, r22.height));
            if (ConnectorGeom.onLeftSide(oppositeConnector.getConnectorPoint(), r2))
                r11.x = r1.x + r1.width - r11.width;
            if (ConnectorGeom.onTopSide(oppositeConnector.getConnectorPoint(), r2))
                r11.y = r1.y + r1.height - r11.height;
        }

        for (final RelativeConnector c : connectors) {
            final Point2D.Double origPt = c.getConnectorPoint();
            final RelativeConnector cOpp = ConnectorSubTracker.findOppositeConnector(c);
            final Point2D.Double cOppPt = cOpp.getConnectorPoint();
            Point2D.Double connPt = ConnectorGeom.
            normalizeTransform(ConnectorGeom.reflect(cOppPt, r22), r22, r11);
            connPt = mapSubRectangle(connPt, r11, r1);
            // ensure that we do not end up with another vertex point

            if (ConnectorGeom.isVertexPoint(connPt, r1))
                connPt = ConnectorGeom.makeNonVertex(connPt, r1,
                        ConnectorGeom.onLeftRightSide(origPt, r1), true);

            updateConnectorPoint(connPt, c);

            Point2D.Double p = ConnectorGeom.reflect(ConnectorGeom.normalizeTransform(origPt, r1, r2), r2);
            if (oppositeShapeType != ConnectorSubTracker.RECTANGULAR_SHAPE) {
                p = ConnectorGeom.
                calculateBoundaryPointThruBoundsPoint(oppositeShape, p, onLeftRightSide(cOpp));
            }
            updateConnectorPoint(p, cOpp);
        }
    }
}
