package org.jhotdraw.draw.connector;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.geom.Geom;

/**
 * The <code>BoundaryConnectorStrategy</code> is the base class for all
 * {@link ConnectorStrategy} classes that control connectors on connectible
 * boundaries. {@link Figure#getConnectibleShape()}
 * <p>
 * Strategies of this type can operate in <b>bounds</b> mode or
 * <b>non-bounds</b> mode. {@link ConnectorStrategy#isBoundsMode}
 * <p>
 * In bounds mode, all {@link RelativeConnector} connector points are <b>on the
 * connectible bounds</b>. The strategy's <code> findConnectionPoint</code>
 * method derives the connection point by projecting this connector point onto
 * the figure's connectible shape.
 *
 * <p>
 * The method {@link Figure#getConnectibleShape()} determines the shape used in
 * calculating boundary points.
 * <p>
 * In bounds mode, the shape used is <b>always</b> RECTANGULAR.
 * {@link ConnectorStrategy#getEffectiveShape(Figure)}
 * <p>
 *
 *
 * <b> Note </b>
 * <p>
 * This class and it's subclasses use normalized point transformations
 * {@link ConnectorGeom#normalizeTransform} and normalized rotations
 * {@link #rotateNormalizedPoint}. See also {@link Geom#pointToAngle}.
 * <p>
 * Normalizing is essentially a transformation of rectangular points to unit
 * square points; ultimately allowing the transformation of points on any
 * rectangle to the points on another rectangle. Normalizing allows us to view
 * rectangles as squares(in terms of rotations, reflections etc) and to utilize
 * the symmetries inherent in squares.
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
 */
public abstract class BoundaryConnectorStrategy extends AbstractConnectorStrategy {

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
            modifyOppositeConnectionPointMulti(relativeConnector);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.jhotdraw.draw.connector.AbstractConnectorStrategy#
     * adjustConnectorsMultiForMovingOpposite
     * (org.jhotdraw.draw.connector.ConnectorSubTracker, java.util.Collection)
     */
    @Override
    protected void adjustConnectorsMultiForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors) {
        return;
    }

    /**
     * If the connector point lies on a bounds edge already, return the
     * connector point.
     *
     * Otherwise, return the point where the connecting line cuts the bounds
     * rectangle.
     *
     *
     * @param relativeConnector
     * @return
     */
    private Point2D.Double calculateBoundsPoint(RelativeConnector relativeConnector) {
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);
        final Point2D.Double connPt = relativeConnector.getConnectorPoint();
        if (ConnectorGeom.onLeftRightSide(connPt, r1) || ConnectorGeom.onTopBottomSide(connPt, r1))
            return connPt;

        final Point2D.Double oppositePt = ConnectorSubTracker.findOppositeConnectionPoint(relativeConnector);
        final Rectangle2D.Double r = new Rectangle2D.Double(r1.x, r1.y, r1.width, r1.height);
        Point2D.Double p = ConnectorGeom.calculateBoundaryPoint(r, oppositePt, connPt, oppositePt);
        if (p == null) {
            p = ConnectorGeom.calculateBoundaryPointThruCenter(r, connPt);
        }
        return p;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.AbstractConnectorStrategy#changeConnectorPoint
     * (org.jhotdraw.draw.connector.ConnectorSubTracker,
     * org.jhotdraw.draw.connector.RelativeConnector,
     * java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
     */
    @Override
    protected void changeConnectorPoint(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector, Point2D.Double fromPoint, Point2D.Double toPoint) {
        final int origSide = findSides(relativeConnector);
        super.changeConnectorPoint(connectorSubTracker, relativeConnector, fromPoint, toPoint);

        final LineConnectionFigure connection = relativeConnector.getLineConnection();
        final Rectangle2D.Double r1 = getEffectiveBounds(relativeConnector);

        if (connection.getNodeCount() > 2) {
            final Point2D.Double oppositePoint = ConnectorSubTracker.findOppositeConnectionPoint(relativeConnector);
            if (r1.outcode(oppositePoint) == origSide && r1.outcode(oppositePoint) == findSides(relativeConnector))
                modifyOppositeConnectionPointMulti(relativeConnector);
        }
    }

    /**
     *
     * @param connectors
     * @return true if any connector point is a Vertex Point
     *         {@link ConnectorGeom#isVertexPoint}
     */
    protected boolean checkForVertexConnector(Collection<RelativeConnector> connectors) {
        for (final RelativeConnector relativeConnector : connectors) {
            if (ConnectorGeom.isVertexPoint(relativeConnector.getConnectorPoint(),
                    getEffectiveBounds(relativeConnector)))
                return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.AbstractConnectorStrategy#findConnectionPoint
     * (org.jhotdraw.draw.connector.RelativeConnector)
     */
    @Override
    protected Point2D.Double findConnectionPoint(RelativeConnector relativeConnector) {
        final Figure owner = relativeConnector.getOwner();
        Point2D.Double p = relativeConnector.getConnectorPoint();
        if (isBoundsMode() && !(owner.getConnectibleShape() instanceof Rectangle2D.Double)) {
            p = ConnectorGeom.calculateBoundaryPointThruBoundsPoint(owner.getConnectibleShape(), p,
                    onLeftRightSide(relativeConnector));
        }
        return p;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.connector.AbstractConnectorStrategy#findConnectorPoint
     * (org.jhotdraw.draw.connector.RelativeConnector,
     * java.awt.geom.Point2D.Double, org.jhotdraw.draw.Figure,
     * org.jhotdraw.draw.ConnectionFigure, boolean)
     */
    @Override
    protected Point2D.Double findConnectorPoint(RelativeConnector relativeConnector, Point2D.Double p, Figure owner,
            ConnectionFigure connection, boolean isStartConnector) {
        Point2D.Double result = super.findConnectorPoint(relativeConnector, p, owner, connection, isStartConnector);

        RelativeConnector oppositeConnector = null;
        // do not use ConnectorSubTracker.findOppositeConnector here
        // we may be dragging to a new figure and relativeConnector could be a tracking connector
        if (isStartConnector)
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getEndConnector();
        else
            oppositeConnector = (RelativeConnector) relativeConnector.getLineConnection().getStartConnector();

        if (relativeConnector.getOwner() == oppositeConnector.getOwner())
            return result;

        final Point2D.Double oppConnPt = oppositeConnector.getConnectorPoint();
        if (connection.getNodeCount() == 2)
            result = ConnectorGeom.calculateBoundaryPoint(getEffectiveShape(owner), oppConnPt, p, oppConnPt);
        else
            result = ConnectorGeom.calculateBoundaryPointThruCenter(getEffectiveShape(owner), result);

        if (result == null) {
            final Point2D.Double pt = ConnectorGeom.project(p, getEffectiveBounds(owner));
            result = ConnectorGeom.calculateBoundaryPointThruCenter(getEffectiveShape(owner), pt);
        }

        // make sure the dragged point is not a vertex point;
        final Rectangle2D.Double r1 = getEffectiveBounds(owner);
        final Point2D.Double connPt = relativeConnector.getConnectorPoint();
        if (ConnectorGeom.isVertexPoint(result, r1))
            result = ConnectorGeom.makeNonVertex(result, r1, ConnectorGeom.onLeftRightSide(connPt, r1), true);
        return result;
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
        Point2D.Double result = super
                .findConnectorPointNewConnection(relativeConnector, p, owner, oppositeOwner, start);
        final ConnectionFigure connection = relativeConnector.getLineConnection();

        if (start || owner == oppositeOwner) {
            final Point2D.Double endConnectionPt = connection.getEndPoint();
            final Point2D.Double p1 = ConnectorGeom.calculateBoundaryPoint(getEffectiveShape(owner), result,
                    endConnectionPt, endConnectionPt);
            if (p1 != null)
                result = p1;
            return result;
        }

        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final Point2D.Double startConnectorPt = oppositeConnector.getConnectorPoint();
        result = ConnectorGeom.project(startConnectorPt, getEffectiveBounds(relativeConnector));
        if (getEffectiveShapeType(relativeConnector) != ConnectorSubTracker.RECTANGULAR_SHAPE)
            result = ConnectorGeom.calculateBoundaryPoint(getEffectiveShape(owner), result, p, result);

        if (result == null) {
            final Point2D.Double pt = ConnectorGeom.project(p, getEffectiveBounds(owner));
            result = ConnectorGeom.calculateBoundaryPointThruCenter(getEffectiveShape(owner), pt);
        }
        return result;
    }

    /**
     * @param relativeConnector
     * @return int the logical OR of out codes for each side
     */
    protected int findSides(RelativeConnector relativeConnector) {
        int result = 0;
        if (onLeftSide(relativeConnector))
            result |= Geom.OUT_LEFT;
        else
            if (onRightSide(relativeConnector))
                result |= Geom.OUT_RIGHT;

        if (onTopSide(relativeConnector))
            result |= Geom.OUT_TOP;
        else
            if (onBottomSide(relativeConnector))
                result |= Geom.OUT_BOTTOM;
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jhotdraw.draw.AbstractConnectorStrategy#isBoundsMode()
     */
    @Override
    protected boolean isBoundsMode() {
        return false;
    }

    /**
     * This method returns <i>true</i> if the connector point is on the BOTTOM
     * side of the owner's bounds rectangle and the owner has a rectangular
     * effective shape type. {@link ConnectorStrategy#getEffectiveShapeType}
     * <p>
     * If the effective shape type is not rectangular, this method returns
     * <i>true</i> if the intersection of the connecting line and the bounds is
     * a point on the BOTTOM of the bound's rectangle.
     *
     * @param relativeConnector
     * @return true/false
     */
    protected boolean onBottomSide(RelativeConnector relativeConnector) {
        final Rectangle2D.Double r = getEffectiveBounds(relativeConnector);
        Point2D.Double p = relativeConnector.getConnectorPoint();
        if (getEffectiveShapeType(relativeConnector) != ConnectorSubTracker.RECTANGULAR_SHAPE)
            p = calculateBoundsPoint(relativeConnector);
        return ConnectorGeom.onBottomSide(p.y, r);
    }

    /**
     * @param relativeConnector
     * @return true/false
     */
    protected final boolean onLeftRightSide(RelativeConnector relativeConnector) {
        return onLeftSide(relativeConnector) || onRightSide(relativeConnector);
    }

    /**
     * This method returns <i>true</i> if the connector point is on the LEFT
     * side of the owner's bounds rectangle and the owner has a rectangular
     * effective shape type. {@link ConnectorStrategy#getEffectiveShapeType}
     * <p>
     * If the effective shape type is not rectangular, this method returns
     * <i>true</i> if the intersection of the connecting line and the bounds is
     * a point on the LEFT of the bound's rectangle.
     *
     * @param relativeConnector
     * @return true/false
     */
    protected boolean onLeftSide(RelativeConnector relativeConnector) {
        final Rectangle2D.Double r = getEffectiveBounds(relativeConnector);
        Point2D.Double p = relativeConnector.getConnectorPoint();
        if (getEffectiveShapeType(relativeConnector) != ConnectorSubTracker.RECTANGULAR_SHAPE)
            p = calculateBoundsPoint(relativeConnector);
        return ConnectorGeom.onLeftSide(p.x, r);
    }

    /**
     * This method returns <i>true</i> if the connector point is on the RIGHT
     * side of the owner's bounds rectangle and the owner has a rectangular
     * effective shape type. {@link ConnectorStrategy#getEffectiveShapeType}
     * <p>
     * If the effective shape type is not rectangular, this method returns
     * <i>true</i> if the intersection of the connecting line and the bounds is
     * a point on the RIGHT of the bound's rectangle.
     *
     * @param relativeConnector
     * @return true/false
     */
    protected boolean onRightSide(RelativeConnector relativeConnector) {
        final Rectangle2D.Double r = getEffectiveBounds(relativeConnector);
        Point2D.Double p = relativeConnector.getConnectorPoint();
        if (getEffectiveShapeType(relativeConnector) != ConnectorSubTracker.RECTANGULAR_SHAPE)
            p = calculateBoundsPoint(relativeConnector);
        return ConnectorGeom.onRightSide(p.x, r);
    }

    /**
     *
     * @param relativeConnector
     * @return true/false
     */
    protected final boolean onTopBottomSide(RelativeConnector relativeConnector) {
        return onTopSide(relativeConnector) || onBottomSide(relativeConnector);
    }

    /**
     * This method returns <i>true</i> if the connector point is on the TOP side
     * of the owner's bounds rectangle and the owner has a rectangular effective
     * shape type. {@link ConnectorStrategy#getEffectiveShapeType}
     * <p>
     * If the effective shape type is not rectangular, this method returns
     * <i>true</i> if the intersection of the connecting line and the bounds is
     * a point on the TOP of the bound's rectangle.
     *
     * @param relativeConnector
     * @return true/false
     */
    protected boolean onTopSide(RelativeConnector relativeConnector) {
        final Rectangle2D.Double r = getEffectiveBounds(relativeConnector);
        Point2D.Double p = relativeConnector.getConnectorPoint();
        if (getEffectiveShapeType(relativeConnector) != ConnectorSubTracker.RECTANGULAR_SHAPE)
            p = calculateBoundsPoint(relativeConnector);
        return ConnectorGeom.onTopSide(p.y, r);
    }

    /**
     * Resizing should preserve, if possible, the visual appearance of the
     * connections
     * <p>
     * Resizing, especially to a smaller size, can cause connections to collapse
     * into one another or other unpredictable behavior.
     * <p>
     * No attempt is made to address these situations. The user must undo/redo.
     *
     * @param relativeConnector
     */
    @Override
    protected RelativeConnector preserveConnection(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector) {
        Point2D.Double newPt = new Point2D.Double();
        final Figure owner = relativeConnector.getOwner();
        final Rectangle2D.Double r1 = getEffectiveBounds(owner);
        final ConnectionFigure connection = relativeConnector.getLineConnection();

        // do self-connections separately
        if (connection.getStartFigure() == connection.getEndFigure()) {
            newPt.x = Geom.range(r1.x, r1.x + r1.width, r1.x + relativeConnector.getRelativeX());
            newPt.y = Geom.range(r1.y, r1.y + r1.height, r1.y + relativeConnector.getRelativeY());
            newPt = ConnectorGeom.calculateBoundaryPointThruCenter(getEffectiveShape(owner), newPt);
            updateConnectorPoint(newPt, relativeConnector);
            return relativeConnector;
        }

        final Point2D.Double prevPt = connectorSubTracker.getPrevPoint(relativeConnector);
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final Point2D.Double oppConnPt = oppositeConnector.getConnectorPoint();
        final Point2D.Double[] pt = ConnectorGeom.findIntersectionPoints(getEffectiveShape(owner), oppConnPt, prevPt);
        if (pt.length == 0) {
            newPt = ConnectorGeom.calculateBoundaryPointThruCenter(getEffectiveShape(owner), relativeConnector
                    .getConnectorPoint());
            updateConnectorPoint(newPt, relativeConnector);
            return relativeConnector;
        }
        newPt = ConnectorGeom.nearestPointInArray(oppConnPt, pt);
        updateConnectorPoint(newPt, relativeConnector);
        return relativeConnector;
    }

    /**
     * Projects the connector point onto the opposite figure
     *
     * @param relativeConnector
     * @return the projected point
     */
    protected final Point2D.Double project(RelativeConnector relativeConnector) {
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final ConnectorStrategy oppositeStrategy = ConnectorSubTracker.findConnectorStrategy(oppositeConnector);
        final Shape oppositeShape = oppositeStrategy.getEffectiveShape(oppositeConnector);
        final int oppositeShapeType = oppositeStrategy.getEffectiveShapeType(oppositeConnector);
        final Rectangle2D.Double r2 = oppositeStrategy.getEffectiveBounds(oppositeConnector);

        final Point2D.Double p1 = relativeConnector.getConnectorPoint();
        Point2D.Double p2 = ConnectorGeom.project(p1, r2);
        if (oppositeShapeType != ConnectorSubTracker.RECTANGULAR_SHAPE) {
            p2 = ConnectorGeom.calculateBoundaryPointThruBoundsPoint(oppositeShape, p2,
                    onLeftRightSide(relativeConnector));
        }
        return p2;
    }

    /**
     * gets the reflection of the connector point in owner's center
     *
     * @param relativeConnector
     * @return the reflected point
     */
    protected final Point2D.Double reflect(RelativeConnector relativeConnector) {
        return ConnectorGeom.
        reflect(relativeConnector.getConnectorPoint(), getEffectiveBounds(relativeConnector));
    }

    /**
     * Normalize rotates point {@code p} about {@code owner} by {@code angle}.
     * <p>
     * If the owner's effective shape type is <b>not</b> Rectangular, {@code r1}
     * is always the owner's effective bounds.
     * <p>
     * If the owner's effective shape type is Rectangular, {@code r1} may be a
     * sub-rectangle of the owner's effective bounds.
     * <p>
     * {@link EdgeConnectorStrategy#buildMinimumSubRectangle}
     * <p>
     *
     * @param owner
     * @param r1
     *            is not necessarily the owner's effective bounds <i>for
     *            rectangular shapes</i> but must be a sub-rectangle of these
     *            bounds. This sub-rectangle must be chosen such that the
     *            rotated point is also on the owner's effective bounds edges.
     *            {@link EdgeConnectorStrategy#buildMinimumSubRectangle}
     * @param p a boundary point of r1
     * @param angle
     * @return the rotated point
     */
    protected Point2D.Double rotateNormalizedPoint(Figure owner, Rectangle2D.Double r1,
            Point2D.Double p, double angle) {
        Point2D.Double p1 = new Point2D.Double(p.x, p.y);
        final Shape shape = getEffectiveShape(owner);
        final int shapeType = getEffectiveShapeType(owner);

        if (shapeType == ConnectorSubTracker.ELLIPTICAL_SHAPE) {
            return ConnectorGeom.rotateNormalizedEllipsePoint(angle, r1, p1.x, p1.y);
            //return ConnectorGeom.rotateEllipsePoint(angle, r1, p1.x, p1.y);
        }

        p1 = ConnectorGeom.rotateNormalizedRectPoint(angle, r1, p1.x, p1.y);

        if (shapeType != ConnectorSubTracker.RECTANGULAR_SHAPE)
            p1 = ConnectorGeom.calculateBoundaryPointThruCenter(shape, p1);
        return p1;
    }

    /**
     * Normalize rotates the connection by <code>angle</code> about the owner's
     * effective bounds.
     * <p>
     *
     * @param relativeConnector
     * @param angle
     * @return the rotated point
     */
    protected Point2D.Double rotateNormalizedPoint(RelativeConnector relativeConnector, double angle) {
        return rotateNormalizedPoint(relativeConnector, angle, null);
    }

    /**
     *
     * Rotates the connection by <code>angle</code> for the owner figure
     * <p>
     *
     * @param relativeConnector
     * @param angle
     * @param rotationRect
     *            can be a sub-rectangle of the owner's effective bounds only if
     *            the effective shape type is rectangular.
     *            <p>
     *            if the effective shape type is not rectangular rotationRect is
     *            the effective bounds (or null and assumed to be the owner's
     *            effective bounds)
     * @return the rotated point
     */
    protected Point2D.Double rotateNormalizedPoint(RelativeConnector relativeConnector, double angle,
            Rectangle2D.Double rotationRect) {
        Point2D.Double rotPt = null;
        final Figure owner = relativeConnector.getOwner();
        final Rectangle2D.Double r1 = getEffectiveBounds(owner);
        final int shapeType = getEffectiveShapeType(owner);
        final Point2D.Double connPt = relativeConnector.getConnectorPoint();

        if (rotationRect == null || shapeType != ConnectorSubTracker.RECTANGULAR_SHAPE)
            rotationRect = r1;

        rotPt = rotateNormalizedPoint(owner, rotationRect, connPt, angle);

        if (ConnectorGeom.isVertexPoint(rotPt, rotationRect))
            rotPt = ConnectorGeom.makeNonVertex(rotPt, rotationRect, onLeftRightSide(relativeConnector), false);

        return rotPt;
    }

    // protected Point2D.Double rotatePoint(RelativeConnector relativeConnector,
    // double angle) {
    // return rotatePoint(relativeConnector, angle, null);
    // }

    // protected Point2D.Double rotatePoint(RelativeConnector relativeConnector,
    // double angle, Rectangle2D.Double rotationRect) {
    // Point2D.Double rotPt = null;
    // final Figure owner = relativeConnector.getOwner();
    // final Rectangle2D.Double r1 = getConnectibleBounds(owner);
    // final Point2D.Double connPt = relativeConnector.getConnectorPoint();
    //
    // if (rotationRect == null) {
    // rotPt = rotatePoint(owner, r1, connPt, angle);
    // }
    // else {
    // rotPt = rotatePoint(owner, rotationRect, connPt, angle);
    // }
    //
    // if (ConnectorGeom.isVertexPoint(rotPt, r1))
    // ConnectorGeom.makeNonVertex(rotPt, r1,
    // onLeftRightSide(relativeConnector), false);
    //
    // return rotPt;
    // }

    // protected Point2D.Double rotatePoint(Figure owner, Rectangle2D.Double r1,
    // Point2D.Double p, double angle) {
    // Point2D.Double p1 = new Point2D.Double(p.x, p.y);
    // final int effectiveShapeType =
    // ConnectorSubTracker.getEffectiveShapeType(owner, this);
    //
    // if (effectiveShapeType == ConnectorStrategy.ELLIPTICAL) {
    // p1 = ConnectorGeom.rotateEllipsePoint(angle, r1, p1.x,p1.y);
    // return p1;
    // }
    //
    // p1 = ConnectorGeom.rotateRectPoint(angle, r1, p1.x, p1.y);
    // if (effectiveShapeType != ConnectorStrategy.RECTANGULAR)
    // p1 = ConnectorGeom.calculateBoundaryPointThruCenter(owner,
    // effectiveShapeType, p1);
    // return p1;
    // }

}
