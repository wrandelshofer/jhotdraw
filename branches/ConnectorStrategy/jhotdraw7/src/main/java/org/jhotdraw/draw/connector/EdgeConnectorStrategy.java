package org.jhotdraw.draw.connector;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.geom.Geom;

/**
 * Connections are from one edge of a rectangular figure to a single edge of an
 * opposite rectangular figure.
 * <p>
 * Connector points can be on one edge <b>only</b> and dragging to another edge
 * is not allowed.
 * <p>
 * For Rectangular figures this class simply rotates all connections when a
 * connected side passes it's opposite. This applies to both the moving and
 * stationary owner figures.
 * <p>
 * For Non-Rectangular figures connectors are not automatically adjusted on
 * either figure. <i>Note that if the strategy is operating in bounds mode all
 * figures are considered rectangular.</i>
 * {@link ConnectorStrategy#isBoundsMode()},
 * <p>
 *
 * In <b>non-bounds</b> mode, this strategy is only suitable for connectors on
 * rectangular figures but can be combined successfully with the Rotational
 * strategy to connect rectangular figures with elliptical or polygonal figures.
 * <p>
 * This strategy can be paired successfully with other strategies for the same
 * connection.
 * <p>
 * <p>
 *
 * <b> Note </b>
 * <p>
 * This class, like its parent class {@link BoundaryConnectorStrategy}, uses
 * normalized point transformations and normalized rotations.
 * <p>
 * Normalized rotations are particularly advantageous when all connections are
 * from one side to a single opposite side. Rotating by (+-) PI/2 will move and
 * scale all the connections on one side of a rectangle completely onto an
 * adjacent side.
 *
 * The main drawback to normalization is the different scaling involved with
 * each pair of dissimilar rectangles. Because the scaling for dissimilar
 * rectangles is different the two normalized rotation transformations (for the
 * same rotation angle) are different. This causes 'inconsistent' separation
 * intervals between connections if the figures that rotate are alternated.
 * <p>
 * Of course, unless we are dealing with actual squares, the x,y scaling and the
 * x,y separations between connections are ALWAYS going to be different. We
 * simply want the horizontal(x) connection SEPARATIONS on one rectangle to
 * always be the same SIZE as the horizontal(x) connection SEPARATIONS on the
 * paired rectangle no matter what rectangle we rotate about; similarly for the
 * vertical(y) connection separations.
 * <p>
 * This class and it's subclasses ({@link RectilinearConnectorStrategy}) use
 * 'minimum sub-rectangles' to provide uniform scaling for a pair of connected
 * figures so that users see consistent connection intervals whatever figure
 * they move or rotate. Rotating about the minimum sub-rectangle means that we
 * are using the same rotation transformation for both rectangles
 * <p>
 * The minimum sub-rectangle must be positioned appropriately within each paired
 * rectangle (using minimum connection points) and rotation points on this
 * sub-rectangle must be mapped back to the parent rectangle;
 * <p>
 * Implicit in the above is that we are aiming to align the connections along
 * the minimum width or across the minimum height which seems reasonable from a
 * GUI perspective. If this is not the case, subclasses can override
 * buildMinimumSubRectangle to return the owner bounds or override the methods
 * that use buildMinimumSubRectangle.
 * <p>
 *
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
public class EdgeConnectorStrategy extends BoundaryConnectorStrategy {

    // only created by ConnectorSubTracker
    protected EdgeConnectorStrategy() {
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
        final RelativeConnector relativeConnector = connectors.iterator().next();
        final Figure owner = relativeConnector.getOwner();
        if (getEffectiveShapeType(owner) == ConnectorSubTracker.RECTANGULAR_SHAPE) {
            final double angle = checkRotationAngleForSide(connectors);
            if (Math.abs(angle) > 0) {
                rotateNormalizedAllConnections(connectors, angle);
            }
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
        final RelativeConnector relativeConnector = connectors.iterator().next();
        final Figure owner = relativeConnector.getOwner();
        if (getEffectiveShapeType(owner) == ConnectorSubTracker.RECTANGULAR_SHAPE) {
            final double angle = checkRotationAngleForSide(connectors);
            if (Math.abs(angle) > 0) {
                rotateNormalizedAllConnections(connectors, angle);
            }
        }
    }


    /*
     * (non-Javadoc)
     *
     * @seeorg.jhotdraw.draw.connector.BoundaryConnectorStrategy#
     * adjustConnectorsMultiForMoving
     * (org.jhotdraw.draw.connector.ConnectorSubTracker, java.util.Collection)
     */
    @Override
    protected void adjustConnectorsMultiForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        super.adjustConnectorsMultiForMoving(connectorSubTracker, connectors);
        for (final RelativeConnector relativeConnector : connectors) {
            alignConnectorSide(relativeConnector);
       }
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.jhotdraw.draw.connector.BoundaryConnectorStrategy#
     * adjustConnectorsMultiForMovingOpposite
     * (org.jhotdraw.draw.connector.ConnectorSubTracker, java.util.Collection)
     */
    @Override
    protected void adjustConnectorsMultiForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        super.adjustConnectorsMultiForMovingOpposite(connectorSubTracker, connectors);
        for (final RelativeConnector relativeConnector : connectors) {
             alignConnectorSide(relativeConnector);
        }
    }

    /**
     * @param relativeConnector
     */
    private void alignConnectorSide(RelativeConnector relativeConnector) {
        final Point2D.Double oppositePoint =
            ConnectorSubTracker.findOppositeConnectionPoint(relativeConnector);
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        Point2D.Double connPt = relativeConnector.getConnectorPoint();
        final int connectorSide = ConnectorGeom.findSide(connPt, r1);
        final int oppositeConnectorSide =
            connectorSide < 4 ? connectorSide << 2 : connectorSide >> 2;

        if (r1.outcode(oppositePoint) == oppositeConnectorSide) {
            if (ConnectorGeom.onLeftRightSide(connectorSide))
                connPt = ConnectorGeom.reflectX(connPt, r1);
            else
                connPt = ConnectorGeom.reflectY(connPt, r1);
            updateConnectorPoint(connPt, relativeConnector);
        }
    }

    /**
     * connectors have the same owner, oppositeOwner, owner strategy, opposite
     * owner strategy
     *
     *
     * @param connectors
     */
    protected void arrangeConnectedSide(Figure owner,
            Collection<RelativeConnector> connectors, int connectedSide) {
        if (connectors.size() == 0)
            return;

        final RelativeConnector relativeConnector = connectors.iterator().next();
        final Rectangle2D.Double r1 = getEffectiveBounds(owner);
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final Rectangle2D.Double r2 = getEffectiveBounds(oppositeConnector);

        double length = Math.min(r1.height, r2.height);
        boolean leftRight = true;
        if (connectedSide == Geom.OUT_BOTTOM || connectedSide == Geom.OUT_TOP) {
            length = Math.min(r1.width, r2.width);
            leftRight = false;
        }
        final double d = length / (connectors.size() + 1);
        Point2D.Double connPt = new Point2D.Double(r1.x, r1.y);
        if (connectedSide == Geom.OUT_RIGHT)
            connPt.x = r1.x + r1.width;
        if (connectedSide == Geom.OUT_BOTTOM)
            connPt.y = r1.y + r1.height;

        for (final RelativeConnector c : connectors) {
            ConnectorSubTracker.findOppositeConnector(c);
            if (leftRight)
                connPt.y += d;
            else
                connPt.x += d;
            if (getEffectiveShapeType(relativeConnector) != ConnectorSubTracker.RECTANGULAR_SHAPE)
                connPt = ConnectorGeom.calculateBoundaryPointThruBoundsPoint(getEffectiveShape(owner), connPt,
                        leftRight);

            updateConnectorPoint(connPt, c);
            // update the opposite point
            // final ConnectorStrategy oppositeStrategy =
            // ConnectorSubTracker.findConnectorStrategy(cOpp);
            // final Rectangle2D.Double r2 =
            // oppositeStrategy.getEffectiveBounds(cOpp);
            // final int oppositeShapeType =
            // oppositeStrategy.getEffectiveShapeType(cOpp);
            // final Shape oppositeShape =
            // oppositeStrategy.getEffectiveShape(cOpp);
            // Point2D.Double p =
            // ConnectorGeom.reflect(ConnectorGeom.normalizeTransform(connPt,
            // r1, r2), r2);
            //
            // if (oppositeShapeType != ConnectorSubTracker.RECTANGULAR_SHAPE)
            // p =
            // ConnectorGeom.calculateBoundaryPointThruBoundsPoint(oppositeShape,
            // p, leftRight);

            // updateConnectorPoint(p, cOpp);
        }
    }


    /**
     * Builds a (sub)rectangle with dimensions equal to the minimum dimensions
     * of the owner's and opposite owner's connectible bounds.
     * <p>
     * Rotations are performed about this (sub)rectangle to ensure consistent
     * interConnection intervals for the connected pair of figures.
     * <p>
     *
     * @param relativeConnector
     * @param maxMinConnectionPoints
     * @return the 'virtual' sub-rectangle used for rotation
     *
     *         <p>
     *         <i> <b>Note </b>
     *         <p>
     *         The method normalizedRotate is essentially a composition of
     *         Transformations, N*(R(N))
     *         <p>
     *         where
     *         <p>
     *         < N is normalizeTransform, R is a rotation, N* is
     *         inverseNormalizeTransform
     *         <p>
     *         Because N and N* are scaling transforms, the distances between
     *         connectors after rotation differ(for rectangles that are not
     *         squares). The normalizedRotate composite transformation is not an
     *         isometry.
     *         <p>
     *         More importantly, for two dissimilar rectangles (which have
     *         different scaling) the normalizeRotate transformations for the
     *         same angle are different transformations. This results in
     *         different distance changes after rotation for each rectangle. The
     *         inconsistent changes in distance are visually unsatisfactory when
     *         users alternate rotating figures.
     *         <p>
     *
     *         The minimum (sub)rectangle is used to provide a common
     *         normalizeRotate transformation for each rectangle in a connected
     *         pair. The rotations for each rectangle apply this common
     *         transformation to ensure consistent distances between connectors.
     *         <p>
     *         The minimum sub-rectangle must be positioned appropriately within
     *         each paired rectangle (using minimum connection points) and
     *         rotation points on this sub-rectangle must be mapped back to the
     *         parent rectangle after rotation.
     *         {@link EdgeConnectorStrategy#mapSubRectangle}
     *         <p>
     *         <b>Note(+):</b>
     *         <p>
     *         In the method rotateNormalizedAllConnections, all points to be
     *         rotated are assumed to be on one side only prior to rotation. As
     *         we 'normalize rotate' only by PI/2 or PI, rotated points will
     *         also lie on one side only; an adjacent side for PI/2 and an
     *         opposite side for PI. ........... all connections are to one side
     *         and from one side
     *         <p>
     *         If the points to be rotated lie on the Right or Bottom side, the
     *         rotation rectangle lies against the Right or Bottom side of the
     *         owner rectangle.
     *
     *         </i>
     */
    protected Rectangle2D.Double buildMinimumSubRectangle(RelativeConnector relativeConnector,
            double[] maxMinConnectionPoints) {
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        if (getEffectiveShapeType(relativeConnector) != ConnectorSubTracker.RECTANGULAR_SHAPE)
            return r1;

        final Point2D.Double connPt = relativeConnector.getConnectorPoint();
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
        if (oppositeStrategy.getEffectiveShapeType(oppositeConnector) != ConnectorSubTracker.RECTANGULAR_SHAPE)
            return r1;

        final Rectangle2D.Double r2 = oppositeStrategy.getEffectiveBounds(oppositeConnector);
        if (Math.abs(r1.width - r2.width) < ConnectorGeom.epsilon
                && Math.abs(r1.height - r2.height) < ConnectorGeom.epsilon)
            return r1;

        // use minimum dimensions
        final double w = Math.min(r1.width, r2.width);
        final double h = Math.min(r1.height, r2.height);

        if (Math.abs(r1.width - w) < ConnectorGeom.epsilon && Math.abs(r1.height - h) < ConnectorGeom.epsilon)
            return r1;

        // now position the rectangle
        final double minX = maxMinConnectionPoints[0];
        final double minY = maxMinConnectionPoints[1];
        final double maxX = maxMinConnectionPoints[2];
        final double maxY = maxMinConnectionPoints[3];

        // give up on minimum rectangle ... user has probably dragged points
        // outside dimensions
        if (maxX - minX > w || maxY - minY > h) {
            // System.out.println("giving up on min rect");
            return r1;
        }
        // Right/Bottom needs the rotation rectangle to lie against the
        // right/bottom sides
        double r3X = ConnectorGeom.onRightSide(connPt, r1) ? r1.x + r1.width - w : r1.x;
        double r3Y = ConnectorGeom.onBottomSide(connPt, r1) ? r1.y + r1.height - h : r1.y;

        if (ConnectorGeom.onLeftRightSide(connPt, r1) && r1.height > h) {
            // e2 used to prevent vertex points
            r3Y = minY + (maxY - minY) / 2 - h / 2;
            if (r3Y < r1.y)
                r3Y = minY - ConnectorGeom.e2;
            if (r3Y + h > r1.y + r1.height)
                r3Y -= r3Y + h - r1.y - r1.height;
        }
        if (ConnectorGeom.onTopBottomSide(connPt, r1) && r1.width > w) {
            // e2 used to prevent vertex points
            r3X = minX + (maxX - minX) / 2 - w / 2;
            if (r3X < r1.x)
                r3X = minX - ConnectorGeom.e2;
            if (r3X + w > r1.x + r1.width)
                r3X -= r3X + w - r1.x - r1.width;
        }
        return new Rectangle2D.Double(r3X, r3Y, w, h);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.BoundaryConnectorStrategy#changeConnectorPoint
     * (org.jhotdraw.draw.connector.ConnectorSubTracker,
     * org.jhotdraw.draw.connector.RelativeConnector,
     * java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
     */
    @Override
    protected void changeConnectorPoint(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector, Point2D.Double fromPoint, Point2D.Double toPoint) {
        super.changeConnectorPoint(connectorSubTracker, relativeConnector, fromPoint, toPoint);
        if (relativeConnector.getLineConnection().getNodeCount()> 2)
            alignConnectorSide(relativeConnector);
    }

    /**
     * Returns an angle to rotate the connectors/connections on the connected
     * side.
     * <p>
     * This method assumes that all connectors are on just one side of a
     * connected figure.
     * <p>
     * Returns +-PI/2 if the moving side has passed the opposite connected side.
     * <p>
     * Returns PI if it detects connected sides are the same side on each
     * figure; this happens when reflecting (x or y) one figure about the other,
     *
     * @param connectors
     *            all connectors on one figure for all connections to another
     *            figure that have the same start and end connector strategies.
     *
     * @return the angle to rotate by
     *         <p>
     *         see {@link ConnectorGeom#calculateRotationAngleForSide}
     */
    protected double checkRotationAngleForSide(Collection<RelativeConnector> connectors) {
        final RelativeConnector relativeConnector = connectors.iterator().next();
        final int connectedSide = findConnectedSide(connectors);
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        final Rectangle2D.Double r2 = ConnectorSubTracker.findOppositeBounds(relativeConnector);
        return ConnectorGeom.calculateRotationAngleForSide(connectedSide, r1, r2);
    }

    /**
     * Returns the outcode for the connected side of connectors.
     * <p>
     * This should only be called when it can be assumed that all connectors are
     * on <b>one side only</b>. (this class and subclasses)
     * <p>
     * <code>connectors</code> have the same owner, opposite owner, owner
     * strategy and the same opposite owner strategy <i>(this need not be the
     * owner strategy)</i>.
     * <p>
     * Vertex connectors are examined relative to their opposite connectors to
     * identify the connected side.
     *
     * @param connectors
     *            all connectors on one figure for all connections to another
     *            figure that have the same start and and the same end connector
     *            strategies.
     *
     * @return an outcode indicating the connected side. If the connected side
     *         cannot be determined <i>for example when a vertex connector is
     *         connected to a vertex connector</i> then the side of the chop
     *         point is returned.
     */
    protected int findConnectedSide(Collection<RelativeConnector> connectors) {
        RelativeConnector oppositeNonVertex = null;
        ConnectorStrategy oppStrategy = null;
        // the existence of a non-vertex connector gives us the side immediately
        for (final RelativeConnector c : connectors) {
            // can only be one side for a non-vertex point
            if (!ConnectorGeom.isVertexPoint(c.getConnectorPoint(), getEffectiveBounds(c)))
                return findSides(c);

            if (oppositeNonVertex == null) {
                final RelativeConnector oppC = ConnectorSubTracker.findOppositeConnector(c);
                oppStrategy = ConnectorSubTracker.findConnectorStrategy(oppC);
                if (!ConnectorGeom.isVertexPoint(oppC.getConnectorPoint(), oppStrategy.getEffectiveBounds(oppC)))
                    oppositeNonVertex = oppC;
            }
        }

        // we've only got vertexConnectors on the owner...
        if (oppositeNonVertex != null) {
            int result = findSides(oppositeNonVertex);
            // the opposite side should have the reverse outcode
            result = result < 4 ? result << 2 : result >> 2;
            return result;
        }

        final RelativeConnector relativeConnector = connectors.iterator().next();
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        final Rectangle2D.Double r2 = ConnectorSubTracker.findOppositeBounds(relativeConnector);
        int connectedSide = ConnectorGeom.findChopSide(r1, r2);
        if (ConnectorGeom.isVertexPoint(connectedSide))
            connectedSide = (connectedSide & Geom.OUT_LEFT) + (connectedSide & Geom.OUT_RIGHT);

        return connectedSide;
    }

    /**
     * Returns the outcode for the connected side of relativeConnector.
     * <p>
     * All connectors are on <b>one side only</b>. (this class and subclasses)
     * <p>
     *
     * @param relativeConnector
     *
     * @return an outcode indicating the connected side. If the connected side
     *         cannot be determined <i>for example when a vertex connector is
     *         connected to a vertex connector</i> then the side of the chop
     *         point is returned.
     */
    protected int findConnectedSide(RelativeConnector relativeConnector) {
        int connectedSide = findSides(relativeConnector);
        if (ConnectorGeom.isVertexPoint(connectedSide)) {
            final Collection<RelativeConnector> connectors = findRelatedConnectors(relativeConnector);
            connectedSide = findConnectedSide(connectors);
        }
        return connectedSide;
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
        Point2D.Double result = new Point2D.Double(p.x, p.y);
        RelativeConnector oppositeConnector = null;
        if (isStartConnector)
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getEndConnector();
        else
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getStartConnector();

        if (relativeConnector.getOwner() == oppositeConnector.getOwner())
            return result;

        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
        result = super.findConnectorPoint(relativeConnector, p, owner, connection, isStartConnector);

        final int shapeType = getEffectiveShapeType(relativeConnector);
        final int oppositeShapeType = oppositeStrategy.getEffectiveShapeType(oppositeConnector);

        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        final Point2D.Double connPt = relativeConnector.getConnectorPoint();

        if (connection.getNodeCount() > 2) {
            result.x = Geom.range(r1.x, r1.x + r1.width, result.x);
            result.y = Geom.range(r1.y, r1.y + r1.height, result.y);
        }

        if (connection.getNodeCount() > 2 || this.getClass() != oppositeStrategy.getClass()
                || shapeType != ConnectorSubTracker.RECTANGULAR_SHAPE
                || oppositeShapeType != ConnectorSubTracker.RECTANGULAR_SHAPE) {
            if (ConnectorGeom.isVertexPoint(result, r1))
                result = ConnectorGeom.makeNonVertex(result, r1, ConnectorGeom.onLeftRightSide(connPt, r1), true);
            return result;
        }

        // from here on the opposite figure is Rectangular and opposite
        // connectors have THIS strategy

        // ensure that dragged connector and all other connectors are on the
        // SAME side

        if (onLeftRightSide(relativeConnector) && onLeftRightSide(oppositeConnector)) {
            result.x = onLeftSide(relativeConnector) ? r1.x : r1.x + r1.width;
            result.y = Geom.range(r1.y, r1.y + r1.height, result.y);
        }
        else
            if (onTopBottomSide(relativeConnector) && onTopBottomSide(oppositeConnector)) {
                result.y = onTopSide(relativeConnector) ? r1.y : r1.y + r1.height;
                result.x = Geom.range(r1.x, r1.x + r1.width, result.x);
            }
            else {
                result.x = Geom.range(r1.x, r1.x + r1.width, result.x);
                result.y = Geom.range(r1.y, r1.y + r1.height, result.y);
            }

        // make sure the dragged point is not a vertex point;
        if (ConnectorGeom.isVertexPoint(result, r1))
            ConnectorGeom.makeNonVertex(result, r1, ConnectorGeom.onLeftRightSide(connPt, r1), true);

        return result;
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
        if (start || owner == oppositeOwner)
            return pt;

        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        // returning null for new connections will veto the connection
        if (oppositeConnector == null)
            return null;

        // project always returns a point consistent with onLeftRightSide or
        // onTopBottomSide
        pt = project(oppositeConnector);
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
        final int connectedSide = findConnectedSide(relativeConnector);
        final Rectangle2D.Double r2 = ConnectorSubTracker.findOppositeBounds(relativeConnector);

        // the preferred side of bounds for connections to r2
        final boolean leftRight = ConnectorGeom.onLeftRightSide(ConnectorGeom.findChopSide(bounds, r2));
        final int preferredSide = ConnectorGeom.findPreferredConnectingSide(bounds, r2, leftRight);

        final double angle = ConnectorGeom.calculateRotationAngleForSide(preferredSide, connectedSide);
        final Point2D.Double p1 = rotateNormalizedPoint(relativeConnector, angle);
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        p1.x = bounds.x + Geom.range(0, bounds.width, p1.x - r1.x);
        p1.y = bounds.y + Geom.range(0, bounds.height, p1.y - r1.y);
        return p1;
    }

    /**
     * @return true
     */
    @Override
    protected boolean isBoundsMode() {
        return true;
    }

    /**
     * Non-scaling transform that maps <b>boundary</b> points on a subRectangle
     * back to the parent rectangle.
     * <p>
     * The point p <b>must not</b> be a Vertex Point
     * {@link ConnectorGeom#isVertexPoint} of {@code subRect}
     * <p>
     * This is the logical inverse of {@link #buildMinimumSubRectangle}
     *
     * @param p
     * @param subRect
     * @param parentRect
     * @return the mapped point
     */
    protected Point2D.Double mapSubRectangle(Point2D.Double p, Rectangle2D.Double subRect,
            Rectangle2D.Double parentRect) {
        // no mapping back is needed if the rectangles are the same
        if (parentRect.equals(subRect))
            return p;

        double pX = p.x;
        double pY = p.y;

        if (ConnectorGeom.onLeftRightSide(p, subRect)) {
            final double dY = parentRect.y + parentRect.height / 2 - subRect.y - subRect.height / 2;
            pY += dY;
            if (ConnectorGeom.onLeftSide(p, subRect))
                pX = parentRect.x;
            else
                pX = parentRect.x + parentRect.width;
        }
        else {
            final double dX = parentRect.x + parentRect.width / 2 - subRect.x - subRect.width / 2;
            pX += dX;
            if (ConnectorGeom.onTopSide(p, subRect))
                pY = parentRect.y;
            else
                pY = parentRect.y + parentRect.height;
        }
        return new Point2D.Double(pX, pY);
    }

    /**
     * All <code>connectors</code> are rotated by <code>angle</code>.
     * <p>
     * This method is used when all connections are on side of each paired
     * figure and when the rotation angle is in {0, PI/2, -PI/2, PI}. Both of
     * these conditions are assumed in {@link RectilinearConnectorStrategy} and
     * in {@link EdgeConnectorStrategy}. These conditions do not hold, however,
     * for {@link RotationalConnectorStrategy}.
     * <p>
     * This method is called when a connected side 'passes' it's opposite
     *
     * @param connectors
     *            all the connectors have the same owner, owner strategy(this
     *            strategy), the same oppositeOwner and the same
     *            oppositeStrategy.
     * @param angle
     */
    protected void rotateNormalizedAllConnections(Collection<RelativeConnector> connectors, double angle) {
        final RelativeConnector relativeConnector = connectors.iterator().next();
        final Figure owner = relativeConnector.getOwner();
        final Rectangle2D.Double r1 = getEffectiveBounds(owner);

        if (getEffectiveShapeType(relativeConnector) != ConnectorSubTracker.RECTANGULAR_SHAPE) {
            for (final RelativeConnector c : connectors) {
                final Point2D.Double rotPt = rotateNormalizedPoint(c, angle);
                updateConnectorPoint(rotPt, c);
            }
            return;
        }

        // Note: build MinimumSubRectangle is paired with mapSubRectangle
        Rectangle2D.Double minimumRect = getEffectiveBounds(owner);
        if (connectors.size() > 1) {
            final double[] maxMinConnectionPoints = findMaxMinConnectorPoints(connectors);
            minimumRect = buildMinimumSubRectangle(relativeConnector, maxMinConnectionPoints);
        }

        for (final RelativeConnector c : connectors) {
            Point2D.Double rotPt = rotateNormalizedPoint(c, angle, minimumRect);
            if (connectors.size() > 1)
                rotPt = mapSubRectangle(rotPt, minimumRect, r1);
            updateConnectorPoint(rotPt, c);
        }
    }
}
