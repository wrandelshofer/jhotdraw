package org.jhotdraw.draw.connector;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

/**
 * This connector locates connector points relative to the top-left vertex of
 * the <b>owner's bounds rectangle</b>.
 * <p>
 * It is used by and uses {@link ConnectorSubTracker} and
 * {@link ConnectorStrategy} objects to create and adjust connector points.
 * <p>
 * RelativeConnector objects are created by {@link ConnectorSubTracker}
 * <p>
 * <i>Note: The relative offsets are relative to the owner's bounds regardless
 * of the strategy's bounds mode setting.</i>
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *         <i> Code line length 120 </i>
 *         <p>
 */
public class RelativeConnector extends AbstractConnector {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    protected transient ConnectionFigure connection;
    protected double relativeX;
    protected double relativeY;

    /**
     * only called by ConnectorSubTracker and used only for tracking connectors
     *
     */
    public RelativeConnector() {
        this(null, 0, 0);
    }


    /**
     * Constructs a RelativeConnector with the given owner and given location.
     *
     * @param owner
     * @param relativeX
     * @param relativeY
     */
    public RelativeConnector(Figure owner, double relativeX, double relativeY) {
        super(owner);
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }


    /**
     * Called by clone only
     *
     * @param owner
     * @param relativeX
     * @param relativeY
     * @param connection
     */
    private RelativeConnector(Figure owner, double relativeX, double relativeY, ConnectionFigure connection) {
        super(owner);
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.connection = connection;
    }

    /* (non-Javadoc)
     * @see org.jhotdraw.draw.AbstractConnector#clone()
     */
    @Override
    public RelativeConnector clone() {
        RelativeConnector that = (RelativeConnector) super.clone();
        that = new RelativeConnector(getOwner(), getRelativeX(), getRelativeY(), getLineConnection());
        return that;
    }

    /* (non-Javadoc)
     * @see org.jhotdraw.draw.connector.AbstractConnector#findEnd(org.jhotdraw.draw.ConnectionFigure)
     */
    @Override
    public Point2D.Double findEnd(ConnectionFigure connection) {
        if (connection.findEndConnectorStrategyName() == null) {
            return getConnectorPoint();
        }
        return findPoint(connection);
    }



    /*
     * @see org.jhotdraw.draw.connector.ConnectorStrategy#findConnectionPoint
     *
     *
     * @see org.jhotdraw.draw.AbstractConnector#findPoint(org.jhotdraw.draw.
     * ConnectionFigure)
     * <p>
     *
     */
    @Override
    protected Point2D.Double findPoint(ConnectionFigure connection) {
        final ConnectorStrategy strategy = ConnectorSubTracker.findConnectorStrategy(this);
        if (strategy == null)
            return getConnectorPoint();
        return strategy.findConnectionPoint(this);
    }

    /* (non-Javadoc)
     * @see org.jhotdraw.draw.connector.AbstractConnector#findStart(org.jhotdraw.draw.ConnectionFigure)
     */
    @Override
    public Point2D.Double findStart(ConnectionFigure connection) {
        if (connection.findStartConnectorStrategyName() == null) {
            return getConnectorPoint();
        }
        return findPoint(connection);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jhotdraw.draw.AbstractConnector#getAnchor()
     */
    @Override
    public Point2D.Double getAnchor() {
        return getConnectorPoint();
    }

    /**
     *
     * This method is final(see note) and should not be overridden by
     * sub-classes
     * <p>
     * {@code relativeX} and {@code relativeY} are always relative to the
     * top left vertex of the <b>owner bounds</b>.
     * <p>
     * The calculated point is a point <i>in</i> or <i>on</i> the bounds
     * rectangle; it is not allowed to lie outside the bounds rectangle.
     * <p>
     * (<i>include all edges ... we do not exclude the bottom or
     * right edges</i> so java.awt.Rectangle2D.Double.contains() can return
     * false).
     * <p>
     *
     * @return A point that has offsets of {@code relativeX} and {@code
     *         relative} from the top left vertex of the owner bounds and
     *         lies inside or on the bounds rectangle.
     *
     */
    public final Point2D.Double getConnectorPoint() {
        final Rectangle2D.Double r = getOwner().getBounds();
        return new Point2D.Double(r.x+relativeX, r.y+relativeY);
    }

    /**
     * @return the connection associated with this connector
     */
    public LineConnectionFigure getLineConnection() {
        return (LineConnectionFigure)connection;
    }

    /**
     * @return owner bounds
     */
    public Rectangle2D.Double getOwnerBounds() {
        return getOwner().getBounds();
    }

    /**
     * @return relativeY
     */
    public double getRelativeX() {
        return relativeX;
    }

    /**
     * @return relativeX
     */
    public double getRelativeY() {
        return relativeY;
    }

    /**
     * @return true if start connector
     */
    public boolean isStartConnector() {
        boolean result = false;

        if (getLineConnection() != null) {
            if (connection.getStartConnector() != null
               && this == connection.getStartConnector())
            result = true;
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.jhotdraw.draw.AbstractConnector#read(org.jhotdraw.xml.DOMInput)
     */
    @Override
    public void read(DOMInput in) throws IOException {
        super.read(in);
        relativeX = in.getAttribute("relativeX", 0.5);
        relativeY = in.getAttribute("relativeY", 0.5);

    }


     /**
     *
     * @param connection
     */
    public void setLineConnection(ConnectionFigure connection) {
        this.connection = connection;
    }


    /**
     * @param x
     */
    public void setRelativeX(double x) {
        relativeX = x;
    }


    /**
     * @param y
     */
    public void setRelativeY(double y) {
        relativeY = y;
    }


    /**
     *
     *
     * @param relativeX
     * @param relativeY
     * @return updated RelativeConnector
     */
    protected RelativeConnector update(double relativeX, double relativeY) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        return this;
    }

    /* (non-Javadoc)
     * @see org.jhotdraw.draw.AbstractConnector#write(org.jhotdraw.xml.DOMOutput)
     */
    @Override
    public void write(DOMOutput out) throws IOException {
        super.write(out);
        out.addAttribute("relativeX", relativeX, 0.5);
        out.addAttribute("relativeY", relativeY, 0.5);

    }

}