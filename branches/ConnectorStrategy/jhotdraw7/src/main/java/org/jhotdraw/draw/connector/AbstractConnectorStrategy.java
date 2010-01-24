package org.jhotdraw.draw.connector;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.geom.Geom;

/**
 * The <code>AbstractConnectorStrategy</code> is the base implementation class
 * for all {@link ConnectorStrategy} classes.
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *         <i> Code line length 120 </i>
 *         <p>
 */


public abstract class AbstractConnectorStrategy extends ConnectorStrategy {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#adjustConnectorForResizing(org.jhotdraw
     * .draw.RelativeConnector, java.awt.geom.Rectangle2D.Double)
     */
    @Override
    protected void adjustConnectorForResizing(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector) {
        preserveConnection(connectorSubTracker, relativeConnector);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#adjustConnectorForMoving(org.jhotdraw
     * .draw.RelativeConnector, boolean, java.util.Collection)
     */
    @Override
    protected abstract void adjustConnectorsForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors);

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#adjustOppositeConnectorForMoving(
     * org.jhotdraw.draw.RelativeConnector, double[], java.util.Collection,
     * java.util.HashMap)
     */

    @Override
    protected abstract void adjustConnectorsForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors);

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#adjustConnectorForMoving(org.jhotdraw
     * .draw.RelativeConnector, boolean, java.util.Collection)
     */
    @Override
    protected abstract void adjustConnectorsMultiForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors);

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#adjustOppositeConnectorForMoving(
     * org.jhotdraw.draw.RelativeConnector, double[], java.util.Collection,
     * java.util.HashMap)
     */

    @Override
    protected abstract void adjustConnectorsMultiForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors);

    /**
     * Used in implementation for dragConnector and touchConnector
     *
     * @param connectorSubTracker
     * @param relativeConnector
     * @param fromPoint
     * @param toPoint
     */
    protected void changeConnectorPoint(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector, Point2D.Double fromPoint, Point2D.Double toPoint) {
        final LineConnectionFigure connection = relativeConnector.getLineConnection();
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        final Point2D.Double connPt = relativeConnector.getConnectorPoint();
        final boolean onLeftRight = ConnectorGeom.onLeftRightSide(connPt, r1);
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);

        Point2D.Double toP = findConnectorPoint(relativeConnector, toPoint, relativeConnector.getOwner(), connection,
                relativeConnector.isStartConnector());
        if (ConnectorGeom.isVertexPoint(toP, r1))
            toP = ConnectorGeom.makeNonVertex(toP, r1, onLeftRight, true);

        updateConnectorPoint(toP, relativeConnector);

        if (connection.getNodeCount() == 2) {
            if (oppositeStrategy.isConnectorTightlyCoupled() && !this.hasSingularConnectorPoint()) {
                final Rectangle2D.Double r2 = oppositeStrategy.getEffectiveBounds(oppositeConnector);
                final ArrayList<RelativeConnector> oppConnectorList = new ArrayList<RelativeConnector>();
                oppConnectorList.add(oppositeConnector);
                oppositeStrategy.adjustConnectorsForMovingOpposite(connectorSubTracker, oppConnectorList);

                Point2D.Double p = oppositeConnector.getConnectorPoint();
                if (ConnectorGeom.isVertexPoint(p, r2))
                    p = ConnectorGeom.makeNonVertex(p, r2, onLeftRight, true);
                updateConnectorPoint(p, oppositeConnector);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#compatibleWith(org.jhotdraw.draw.
     * ConnectorStrategy)
     */
    @Override
    protected boolean compatibleWithOpposite(RelativeConnector relativeConnector, boolean isStartConnector,
            List<String> compatibleMsgs, int changeCount) {
        boolean answer = true;
        final Figure owner = relativeConnector.getOwner();
        final LineConnectionFigure connection = relativeConnector.getLineConnection();
        final int nodeCount = connection.getNodeCount();
        if (nodeCount > 2)
            return true;

        RelativeConnector oppositeConnector = null;
        if (isStartConnector)
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getEndConnector();
        else
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getStartConnector();
        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);

        final Figure oppositeOwner = oppositeConnector.getOwner();
        final Collection<RelativeConnector> existingConnectors =
            findConnectors(owner, oppositeOwner, this.getName(), oppositeStrategy.getName(), false);

        final int strategyCount = changeCount + existingConnectors.size();

        boolean multipleConnectionError = false;

        if (this.hasSingularConnectorPoint() && oppositeStrategy.hasSingularConnectorPoint() && strategyCount > 1) {
            multipleConnectionError = true;
        }

        if (this.hasSingularConnectorPoint() && oppositeStrategy.isConnectorTightlyCoupled() && strategyCount > 1) {
            multipleConnectionError = true;
        }

        if (this.isConnectorTightlyCoupled() && oppositeStrategy.hasSingularConnectorPoint() && strategyCount > 1) {
            multipleConnectionError = true;
        }

        if (multipleConnectionError) {
            answer = false;
            final String singularStrategyName = this.hasSingularConnectorPoint() ? getName() : oppositeStrategy
                    .getName();
            final String errorMsg = getName() + " is not compatible with " + oppositeStrategy.getName()
                    + " for MULTIPLE direct connections between two figures \n " + singularStrategyName
                    + " has a SINGLE Connector Point only";
            compatibleMsgs.add(errorMsg);
        }
        else
            answer = oppositeStrategy.compatibleWithNewOppositeStrategy(this, oppositeConnector,
                    !isStartConnector,
                    compatibleMsgs, changeCount);

        return answer;
    }

    /**
     * @param newOppositeStrategy
     * @param relativeConnector
     * @param isStartConnector
     * @param compatibleMsgs
     * @param changeCount
     * @return boolean
     */
    @Override
    protected boolean compatibleWithNewOppositeStrategy(ConnectorStrategy newOppositeStrategy,
            RelativeConnector relativeConnector,
            boolean isStartConnector, List<String> compatibleMsgs, int changeCount) {

        return true;
    }



    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#compatibleWith(org.jhotdraw.draw.
     * Figure)
     */
    @Override
    protected boolean compatibleWithOwnerFigure(RelativeConnector relativeConnector, Figure ownerFigure,
            List<String> compatibleMsgs) {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#confirmConnector(org.jhotdraw.draw
     * .RelativeConnector)
     */
    @Override
    protected boolean confirmOrVetoConnector(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector, JComponent view, boolean isStartConnector) {
        boolean result = true;
        RelativeConnector oppositeConnector = null;
        if (isStartConnector)
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getEndConnector();
        else
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getStartConnector();

        final Figure owner = relativeConnector.getOwner();
        final ArrayList<String> incompatibleMsgs = new ArrayList<String>();
        final int changeCount = connectorSubTracker.isTrackingConnector(oppositeConnector) ? 0 : 1;

        result = compatibleWithOpposite(relativeConnector, isStartConnector, incompatibleMsgs, changeCount);
        result = result && compatibleWithOwnerFigure(relativeConnector, owner, incompatibleMsgs);

        if (!result) {
            final StringBuffer buff = new StringBuffer("NEW CONNECTOR VETOED");
            buff.append("\n\n");
            for (final String msg : incompatibleMsgs) {
                buff.append(msg).append("\n");
            }

            final String title = "CREATE NEW CONNECTION";
            JOptionPane.showMessageDialog(view, buff.toString(), title, JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.ConnectorStrategy#dragConnector(org.jhotdraw
     * .draw.connector.ConnectorSubTracker,
     * org.jhotdraw.draw.connector.RelativeConnector,
     * java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
     */
    @Override
    protected Point2D.Double dragConnector(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector, Point2D.Double fromPoint,
            Point2D.Double toPoint, int modifiersEx) {
        changeConnectorPoint(connectorSubTracker, relativeConnector, fromPoint, toPoint);
        return relativeConnector.getConnectorPoint();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#findConnectionPoint(org.jhotdraw.
     * draw.RelativeConnector)
     */
    @Override
    protected Point2D.Double findConnectionPoint(RelativeConnector relativeConnector) {
        return relativeConnector.getConnectorPoint();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#findConnectorPoint(org.jhotdraw.draw
     * .RelativeConnector, java.awt.geom.Point2D.Double,
     * org.jhotdraw.draw.Figure, org.jhotdraw.draw.ConnectionFigure, boolean)
     */
    @Override
    protected Point2D.Double findConnectorPoint(RelativeConnector relativeConnector, Point2D.Double p, Figure owner,
            ConnectionFigure connection, boolean isStartConnector) {
        final Point2D.Double result = new Point2D.Double(p.x, p.y);
        final Point2D.Double connPt = relativeConnector.getConnectorPoint();
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        if (!owner.contains(p) || owner != relativeConnector.getOwner()) {
            result.x = connPt.x;
            result.y = connPt.y;
        }
        else {
            result.x = Geom.range(r1.x, r1.x + r1.width, result.x);
            result.y = Geom.range(r1.y, r1.y + r1.height, result.y);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#findConnectorPointNewConnection(org
     * .jhotdraw.draw.RelativeConnector, java.awt.geom.Point2D.Double,
     * org.jhotdraw.draw.Figure, org.jhotdraw.draw.ConnectionFigure, boolean)
     */
    @Override
    protected Point2D.Double findConnectorPointNewConnection(RelativeConnector relativeConnector, Point2D.Double p,
            Figure owner, Figure oppositeOwner, boolean start) {
        Point2D.Double pt = relativeConnector.getConnectorPoint();
        if (owner.contains(p))
            pt = p;
        return pt;
    }

    /**
     * Find the connectors for all connections to/from the owner with opposite
     * owner equal to <code>oppositeOwner</code>
     * <p>
     * Only connections with the <b>same strategy</b> as
     * <code>strategyName</code> and the <b>same opposite strategy</b> as
     * <code>oppStrategyName</code> are considered
     *
     * @param owner
     * @param oppositeOwner
     * @param strategyName
     * @param oppStrategyName
     * @param includeMultiPointLines
     *            true if connections with multiple points are included
     * @return collection of connectors
     */
    protected Collection<RelativeConnector> findConnectors(Figure owner, Figure oppositeOwner, String strategyName,
            String oppStrategyName, boolean includeMultiPointLines) {
        final Collection<RelativeConnector> result = new ArrayList<RelativeConnector>();
        if (strategyName == null || strategyName.length() == 0) {
            throw new IllegalArgumentException("Strategy Name ???");
        }

        final Collection<LineConnectionFigure> connections = owner.getConnections();
        for (final LineConnectionFigure line : connections) {
            if (!includeMultiPointLines && line.getNodeCount() > 2)
                continue;

            RelativeConnector relativeConnector = (RelativeConnector) line.getStartConnector();
            RelativeConnector oppositeConnector = (RelativeConnector) line.getEndConnector();
            String connectorStrategyName = line.findStartConnectorStrategyName();
            String oppositeStrategyName = line.findEndConnectorStrategyName();
            if (relativeConnector.getOwner() != owner) {
                relativeConnector = (RelativeConnector) line.getEndConnector();
                oppositeConnector = (RelativeConnector) line.getStartConnector();
                connectorStrategyName = line.findEndConnectorStrategyName();
                oppositeStrategyName = line.findStartConnectorStrategyName();
            }
            if (connectorStrategyName == null || oppositeStrategyName == null)
                continue;

            if (!strategyName.equals(connectorStrategyName))
                continue;

            if (!oppStrategyName.equals(oppositeStrategyName))
                continue;

            if (oppositeConnector.getOwner() != oppositeOwner)
                continue;

            result.add(relativeConnector);
        }
        return result;
    }

    /**
     * Gets the max/min connector points in <code>connectors</code>. All
     * Connectors should have the same owner, strategy, oppositeOwner and
     * oppositeStrategy.
     * <p>
     *
     * @param connectors
     * @return array 1x4 containing minX, minY, maxX, maxY
     */
    protected double[] findMaxMinConnectorPoints(Collection<RelativeConnector> connectors) {
        if (connectors.size() == 0) {
            throw new IllegalArgumentException("findMaxMinConnectorPoints - NO connectors");
        }

        final RelativeConnector relativeConnector = connectors.iterator().next();
        final Figure owner = relativeConnector.getOwner();
        final Rectangle2D.Double r1 = getEffectiveBounds(owner);

        double minX = r1.x + r1.width;
        double minY = r1.y + r1.height;
        double maxX = r1.x;
        double maxY = r1.y;

        for (final RelativeConnector c : connectors) {
            if (c.getOwner() != owner) {
                throw new IllegalArgumentException(
                        "findMaxMinConnectorPoints - all connectors do NOT have the same owner");
            }
            final Point2D.Double p = c.getConnectorPoint();
            minX = p.x < minX ? p.x : minX;
            minY = p.y < minY ? p.y : minY;
            maxX = p.x > maxX ? p.x : maxX;
            maxY = p.y > maxY ? p.y : maxY;
        }

        final double[] arr = new double[4];
        arr[0] = minX;
        arr[1] = minY;
        arr[2] = maxX;
        arr[3] = maxY;
        return arr;
    }

    /**
     * Find the connectors, on the owner of {@code relativeConnector}, for all
     * connections with the same owner, oppositeOwner, strategy and
     * oppositeStrategy as {@code relativeConnector}
     * <p>
     *
     * @param relativeConnector
     * @return collection of connectors
     */
    protected Collection<RelativeConnector> findRelatedConnectors(RelativeConnector relativeConnector) {
        final Figure owner = relativeConnector.getOwner();
        final ConnectorStrategy strategy = ConnectorSubTracker.findConnectorStrategy(relativeConnector);
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final Figure oppositeOwner = oppositeConnector.getOwner();
        final ConnectorStrategy oppStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);

        return findConnectors(owner, oppositeOwner, strategy.getName(), oppStrategy.getName(), false);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.ConnectorStrategy#findPriorConnectorPoint
     * (org.jhotdraw.draw.connector.RelativeConnector,
     * java.awt.geom.Rectangle2D.Double)
     */
    @Override
    protected Point2D.Double findTransformedConnectorPoint(RelativeConnector relativeConnector,
            Rectangle2D.Double bounds) {
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        final Point2D.Double p2 = relativeConnector.getConnectorPoint();
        p2.x = bounds.x + Geom.range(0, bounds.width, p2.x - r1.x);
        p2.y = bounds.y + Geom.range(0, bounds.height, p2.y - r1.y);
        return p2;
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.ConnectorStrategy#getEffectiveBounds(org.
     * jhotdraw.draw.Figure)
     */
    @Override
    protected Rectangle2D.Double getEffectiveBounds(Figure figure) {
        return (Rectangle2D.Double) getEffectiveShape(figure).getBounds2D();
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.ConnectorStrategy#getEffectiveBounds(org.
     * jhotdraw.draw.connector.RelativeConnector)
     */
    @Override
    protected Rectangle2D.Double getEffectiveBounds(RelativeConnector relativeConnector) {
        return getEffectiveBounds(relativeConnector.getOwner());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.ConnectorStrategy#getEffectiveShape(org.jhotdraw
     * .draw.Figure)
     */
    @Override
    protected Shape getEffectiveShape(Figure figure) {
        if (isBoundsMode())
            return figure.getConnectibleShape().getBounds2D();
        return figure.getConnectibleShape();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.ConnectorStrategy#getEffectiveShape(org.jhotdraw
     * .draw.connector.RelativeConnector)
     */
    @Override
    protected Shape getEffectiveShape(RelativeConnector relativeConnector) {
        return getEffectiveShape(relativeConnector.getOwner());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.ConnectorStrategy#getEffectiveShapeType(org
     * .jhotdraw.draw.Figure)
     */
    @Override
    protected int getEffectiveShapeType(Figure figure) {
        if (isBoundsMode())
            return ConnectorSubTracker.RECTANGULAR_SHAPE;

        if (getEffectiveShape(figure) instanceof Rectangle2D.Double)
            return ConnectorSubTracker.RECTANGULAR_SHAPE;

        if (getEffectiveShape(figure) instanceof Ellipse2D.Double)
            return ConnectorSubTracker.ELLIPTICAL_SHAPE;

        return ConnectorSubTracker.GENERAL_SHAPE;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.ConnectorStrategy#getEffectiveShapeType(org
     * .jhotdraw.draw.connector.RelativeConnector)
     */
    @Override
    protected int getEffectiveShapeType(RelativeConnector relativeConnector) {
        return getEffectiveShapeType(relativeConnector.getOwner());
    }

    /**
     * @return the name of this strategy
     */
    @Override
    protected String getName() {
        String strategyName = this.getClass().getName();
        if (strategyName.lastIndexOf('.') > 0)
            strategyName = strategyName.substring(strategyName.lastIndexOf('.') + 1);
        return strategyName;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jhotdraw.draw.ConnectorStrategy#isSingularConnectorPoint()
     */
    @Override
    protected boolean hasSingularConnectorPoint() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jhotdraw.draw.ConnectorStrategy#isBoundsMode()
     */
    @Override
    protected boolean isBoundsMode() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jhotdraw.draw.connector.ConnectorStrategy#isTightlyCoupled()
     */
    @Override
    protected boolean isConnectorTightlyCoupled() {
        return false;
    }

    /**
     * Modifies the opposite <i>connection</i> point for connections with more
     * than 2 points.
     * <p>
     * Note that this is <b>not</b> the opposite <i>connector</i> point
     *
     *
     * @param relativeConnector
     */
    protected void modifyOppositeConnectionPointMulti(RelativeConnector relativeConnector) {
        final Figure owner = relativeConnector.getOwner();
        final Rectangle2D.Double r1 = getEffectiveBounds(owner);
        final Point2D.Double connPt = relativeConnector.getConnectorPoint();
        final LineConnectionFigure connection = relativeConnector.getLineConnection();
        final Point2D.Double oppositePoint = ConnectorSubTracker.findOppositeConnectionPoint(relativeConnector);
        final Point2D.Double newOppositePoint = new Point2D.Double(oppositePoint.x, oppositePoint.y);

        if (ConnectorGeom.onLeftRightSide(connPt, r1))
            newOppositePoint.y = connPt.y;
        else
            newOppositePoint.x = connPt.x;

        int oppositeIndex = 1;
        if (!relativeConnector.isStartConnector())
            oppositeIndex = connection.getNodeCount() - 2;
        connection.willChange();
        connection.setPoint(oppositeIndex, newOppositePoint);
        connection.changed();
    }

    /**
     * Resizing should preserve, if possible, the visual appearance of the
     * connections
     *
     * @param relativeConnector
     * @return updated relativeConnector
     */
    protected abstract RelativeConnector preserveConnection(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector);

    /**
     * If the 'Alt-key' is down when dragging a figure the connections slide
     * along the figure.
     * @param relativeConnector
     */
    @Override
    protected void slideConnector(ConnectorSubTracker connectorSubTracker, RelativeConnector relativeConnector) {
        preserveConnection(connectorSubTracker, relativeConnector);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.ConnectorStrategy#touchConnector(org.jhotdraw
     * .draw.connector.RelativeConnector)
     */
    @Override
    protected Point2D.Double touchConnector(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector) {
        Point2D.Double connPt = relativeConnector.getConnectorPoint();
        final LineConnectionFigure connection = relativeConnector.getLineConnection();
        connection.willChange();
        changeConnectorPoint(connectorSubTracker, relativeConnector, connPt, connPt);
        connection.updateConnection();
        connection.changed();
        return relativeConnector.getConnectorPoint();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.ConnectorStrategy#updateConnectorPoint(java.awt.geom
     * .Point2D.Double, org.jhotdraw.draw.RelativeConnector)
     */
    @Override
    protected final Point2D.Double updateConnectorPoint(Point2D.Double p, RelativeConnector relativeConnector) {
        if (p == null) {
            System.out.println("Cannot Update Connector Point to NULL");
            return relativeConnector.getConnectorPoint();
        }

        final Rectangle2D.Double r1 = relativeConnector.getOwner().getBounds();
        double rX = p.x - r1.x;
        double rY = p.y - r1.y;

        rX = Geom.range(0, r1.width, rX);
        rY = Geom.range(0, r1.height, rY);
        relativeConnector.update(rX, rY);
        return relativeConnector.getConnectorPoint();
    }
}
