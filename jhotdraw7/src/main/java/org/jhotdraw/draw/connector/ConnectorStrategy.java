package org.jhotdraw.draw.connector;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;


import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.geom.Geom;

/**
 *
 * <p>
 * This class (<i>protected interface</i>) determines how the start and end
 * connectors of {@link ConnectionFigure} are created and adjusted.
 *
 * <p>
 * {@link ConnectorSubTracker} creates and uses objects implementing this
 * interface. Implementing classes provide different creation and adjustment
 * behaviors to {@code ConnectorSubTracker} when handling connectors of type
 * {@link RelativeConnector}.
 *
 * <p>
 * Every {@code ConnectionFigure} has <b>two</b> associated connector strategy
 * <b>attributes</b> corresponding to the two connected end points. These
 * attributes are used by {@code ConnectorSubTracker} to instantiate paired
 * strategy objects which manipulate the connection's start and end connectors.
 * <p>
 * <i>The attribute value of a connector strategy attribute is the <b>name</b>
 * of a connector strategy class.</i>
 * <p>
 * <p>
 * The essential behavior of {@code ConnectorStrategy} objects, and in
 * particular the pairing of these objects, is their ability to control
 * connection points on the <i>Moving</i> figure <b>AND</b> on the
 * <i>Stationary</i> connected figure. The method
 * {@link #adjustConnectorsForMoving} applies to the moving figure; the
 * <b>opposite or paired</b> strategy's method
 * {@link #adjustConnectorsForMovingOpposite} dictates how the stationary figure
 * responds to the moving figure.
 * <p>
 * ConnectorStrategy classes locate connectors according to the owner figure's
 * method {@link Figure#getConnectibleShape()}. This shape is typically the
 * owner figure's shape(<i>the default</i>) or the owner figure's bounds
 * rectangle but can be redefined by sub-classes of {@link Figure}.
 * <p>
 * Successful pairing of connector strategies is dictated by the strategy
 * <code>adjust</code> methods and the shape of the connected figures.
 * {@link Figure#getConnectibleShape}
 * <p>
 * There is, by definition, no single strategy that is suitable for all
 * contexts. Each strategy has limitations or constraints that restrict it's
 * applicability.
 * <p>
 * The {@link RectilinearConnectorStrategy} is the most capable strategy for
 * rectangular shapes but is not totally suitable for non-rectangular shaped
 * figures; the {@link RotationalConnectorStrategy} is preferable for oval
 * shapes.
 * <p>
 * Implementing classes should indicate suitable opposite strategies and
 * suitable owner figures and use the <code>compatibleWith</code> methods to
 * control compatibility;
 * <p>
 * Strategy methods can <b>only</b> be called by {@link ConnectorSubTracker} or
 * {@link RelativeConnector}. All the interface methods are effectively
 * <i>hook</i> methods called by the {@code ConnectorSubTracker} or related
 * actions.
 * <p>
 * <p>
 * Implementations should be <i>Stateless</i> as all state is provided by method
 * parameters, held in RelativeConnector or retrieved from {@code
 * ConnectorSubTracker}</b> where this is passed as a method parameter.
 * <p>
 *
 * <p>
 * This requirement, however, is not imposed and any strategy with state will
 * have to consider instantiation issues and to reconcile multiple possible
 * instances of the strategy in the one context. <i>This was not the design
 * intent.</i>
 *
 * <p>
 * <b>Note:</b>
 * <p>
 * JHotDraw often uses normalized points and angles when calculating points on a
 * rectangle; a normalized angle is the angle between normalized points. See
 * {@link Geom#pointToAngle}.
 * <P>
 * Normalizing is used in much of the geometry to implement this interface.
 * {@link ConnectorGeom#normalizeTransform(
 * java.awt.geom.Point2D.Double, java.awt.geom.Rectangle2D.Double)}
 * <p>
 * {@link BoundaryConnectorStrategy#rotateNormalizedPoint(
 * Figure, java.awt.geom.Rectangle2D.Double, java.awt.geom.Point2D.Double, double)}
 * <p>
 * The opposite connector of a <b>connector</b> is the connector on the other
 * side of the connection
 * <p>
 * The opposite owner of a <b>connector</b> is the owner of the opposite
 * connector
 * <p>
 * <code>adjustConnectorsForMovingOpposite</code> is called <b>after</b>
 * <code>adjustConnectorsForMoving</code>. The connector points on the moving
 * figure are updated <b>before</b> the connector points on the stationary
 * figure.
 * <p>
 * <i>get</i> methods are used for simple 'property' type retrievals;
 * <i>find</i> methods are more complex and usually involve non-trivial
 * computations. This is a question of style and not totally rigorous; see
 * <code>getEffectiveShapeType</code>
 * <p>
 * <p>
 * <b>Note Additional:</b>
 * <p>
 * <i> There are some concepts and implementations used that are debatable. The
 * concept of a connector being on the left, right, top or bottom of it's owner
 * figure {@link BoundaryConnectorStrategy#onLeftSide(RelativeConnector)} when
 * the owner figure is non-rectangular, seems forced; the related concept of a
 * Vertex Point {@link ConnectorGeom#isVertexPoint} and the repeated usage of
 * {@link ConnectorGeom#makeNonVertex} should be questioned.
 * <p>
 * They work reasonably well and are currently justified by the central focus on
 * rectangles, the concept of {@link ConnectorStrategy#isBoundsMode()} and
 * especially the {@link RectilinearConnectorStrategy}.
 * <p>
 * The Figure interface provides {@link Figure#getConnectibleShape()}. This can
 * be overridden to provide the shape that connections connect to. This ability
 * blurs the distinction between {@code BoundaryConnectorStrategy} and {@code
 * InteriorConnectorStrategy}; the connectible shape of a Figure can be a path
 * in the interior of the figure (even exterior but this is pathological).
 * <p>
 * The method {@code Figure#getConnectibleShape()} creates an inevitable
 * increase in the usage of {@code instanceof}. This lack of polymorphism is
 * cause for reflection and should be viewed critically. </i>
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *         <i> Code line length 120 </i>
 *         <p>
 */
public abstract class ConnectorStrategy {

    /**
     * This method is called when a figure is resized or restored.
     * <p>
     * It is triggered by the {@code DefaultHandleTracker} mouseDragged and
     * mouseReleased events.
     *
     * @param connectorSubTracker
     * @param relativeConnector
     *
     */
    protected abstract void adjustConnectorForResizing(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector);

    /**
     *
     * This method is called when a figure (non-connection figure) has moved
     * independently of a connected figure.
     * <p>
     * It is triggered by the calling tool's <code>mouseDragged</code> or
     * <code>mouseReleased</code> event.
     * <p>
     * It applies to direct connections(2-Point connections) only.
     * <p>
     * It is called once for each combination of
     * <b>owner/connectorStrategy/oppositeOwner</b> on the moving figure
     * <p>
     *
     * @param connectorSubTracker
     * @param connectors
     *            all the connectors have the same owner (<i>the moving
     *            figure</i>), owner strategy (<i>this strategy</i>), the same
     *            oppositeOwner(<i>stationary figure</i>) and the same
     *            oppositeStrategy.
     */
    protected abstract void adjustConnectorsForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors);

    /**
     * This method is called for stationary figures when the <i>opposite</i>
     * owner figure has moved.
     * <p>
     * It is triggered by the calling tool's <code>mouseDragged</code> or
     * <code>mouseReleased</code> event on the <b>opposite figure</b>.
     * <p>
     * It applies to direct connections(2-Point connections) only.
     * <p>
     * This method is called <b>after</b> the call to
     * <code>adjustConnectorsForMoving</code> for the moving figure.
     * <p>
     *
     * @param connectorSubTracker
     * @param connectors
     *            all the connectors have the same owner (the stationary
     *            figure), owner strategy(this strategy), the same opposite
     *            owner (the moving figure) and the same opposite strategy.
     */
    protected abstract void adjustConnectorsForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors);

    /**
     * This method is called when a figure (non-connection figure) has moved
     * independently of a connected figure.
     * <p>
     * It is triggered by the calling tool's <code>mouseDragged</code> or
     * <code>mouseReleased</code> event.
     * <p>
     * It applies to non-direct connections(3-points or more) only.
     * <p>
     * It is called once for each combination of
     * <b>owner/connectorStrategy/oppositeOwner</b> on the moving figure
     * <p>
     *
     * @param connectorSubTracker
     * @param connectors
     *            all the connectors have the same owner (the moving figure),
     *            owner strategy(this strategy), the same oppositeOwner(the
     *            stationary figure) and the same opposite strategy.
     */
    protected abstract void adjustConnectorsMultiForMoving(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors);

    /**
     * This method is called for stationary figures when an <i>opposite</i>
     * owner figure has moved.
     * <p>
     * It is triggered by the calling tool's <code>mouseDragged</code> or
     * <code>mouseReleased</code> event on the <b>opposite figure</b>.
     * <p>
     * It applies to non-direct connections(3-Points or more) only.
     * <p>
     * This method is called <b>after</b> the call to
     * <code>adjustConnectorsMultiForMoving</code> for the moving figure.
     * <p>
     *
     * @param connectorSubTracker
     * @param connectors
     *            all the connectors have the same owner (the stationary
     *            figure), owner strategy(this strategy), the same opposite
     *            owner (the moving figure) and the same opposite strategy.
     */
    protected abstract void adjustConnectorsMultiForMovingOpposite(ConnectorSubTracker connectorSubTracker,
            Collection<RelativeConnector> connectors);

    /**
     * Checks if this strategy is compatible with a new {@code oppositeStrategy}
     * .
     * <p>
     * All strategies <b>must</b> be defined as being compatible with
     * {@link FixedBoundaryConnectorStrategy} to ensure changes are possible.
     * <p>
     * The parameter {@code changeCount} provides the total number of similar
     * changes with which this check should be reconciled.
     *
     * @param newOppositeStrategy
     * @param relativeConnector
     *            connector
     * @param isStartConnector
     * @param compatibleMsgs
     *            a supplied list that will be appended to
     * @param changeCount
     *            the number of similar changes that it is part of
     *            {@link ConnectorSubTracker#checkStrategyCompatibility(Collection, String, boolean, List)}
     * @return true/false
     */
    protected abstract boolean compatibleWithNewOppositeStrategy(ConnectorStrategy newOppositeStrategy,
            RelativeConnector relativeConnector, boolean isStartConnector,
            List<String> compatibleMsgs, int changeCount);

    /**
     * Checks if this strategy is compatible with an existing oppositeConnector
     * and oppositeStrategy.
     * <p>
     * All strategies <b>must</b> be defined as being compatible with
     * {@link FixedBoundaryConnectorStrategy} to ensure changes are possible.
     * <p>
     * The parameter {@code changeCount} provides the total number of similar
     * changes with which this check should be reconciled.
     *
     * @param relativeConnector
     *            connector
     * @param isStartConnector
     * @param compatibleMsgs
     *            a supplied list that will be appended to
     * @param changeCount
     *            the number of similar changes that it is part of
     *            {@link ConnectorSubTracker#checkStrategyCompatibility(Collection, String, boolean, List)}
     * @return true/false
     */
    protected abstract boolean compatibleWithOpposite(RelativeConnector relativeConnector,
            boolean isStartConnector,
            List<String> compatibleMsgs, int changeCount);

    /**
     * Checks if this strategy is compatible with an owner figure of {@code
     * figure}.
     *
     * @param relativeConnector
     *            connector
     * @param ownerFigure
     *            owner Figure
     * @param compatibleMsgs
     *            a supplied list that will be appended to
     * @return true/false
     */
    protected abstract boolean compatibleWithOwnerFigure(RelativeConnector relativeConnector,
            Figure ownerFigure, List<String> compatibleMsgs);

    /**
     * @param connectorSubTracker
     * @param relativeConnector
     * @param view
     *            drawing view
     * @param isStartConnector
     * @return false if this method vetoes assigning the connector to the
     *         connection
     */
    protected abstract boolean confirmOrVetoConnector(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector, JComponent view, boolean isStartConnector);

    /**
     * @param connectorSubTracker
     * @param relativeConnector
     * @param fromPoint
     * @param toPoint
     * @param modifiersEx
     * @return the recomputed drag point from <code>findConnectorPoint</code>
     *
     */
    protected abstract Point2D.Double dragConnector(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector, Point2D.Double fromPoint,
            Point2D.Double toPoint, int modifiersEx);

    /**
     * The <b>connection</b> point is the point of connection between the
     * connecting line and the owner's connectible shape.
     * <p>
     * In non-bounds mode (see {@link ConnectorStrategy#isBoundsMode()}), the
     * default implementation returns the same point as the connector point
     * {@link RelativeConnector#getConnectorPoint()}.
     * <p>
     * In bounds mode, the default implementation places the connector point
     * <b>on</b> the bounds edges and this point is the projection of the
     * connector point onto the owner's connectible shape. (<i> If the shape is
     * Rectangular the connector point and the connection point are clearly the
     * same</i>).
     * <p>
     * If the connector point is on the left/right edge of the bounds, the
     * projection is horizontal; if the connector point is on the top/bottom
     * edge the projection is vertical.
     * <p>
     * <i>Implementing sub-classes can redefine how the connection point is
     * determined.</i>
     *
     *
     * @param relativeConnector
     * @return connection point
     */
    protected abstract Point2D.Double findConnectionPoint(RelativeConnector relativeConnector);

    /**
     * <p>
     * Returns the adjusted Start or the End Connector Point of an
     * <b>existing</b> connection; the point is adjusted to be as close to
     * {@code p} as allowed.
     * <p>
     *
     * @param relativeConnector
     *            is a Tracking Connector {@link ConnectorSubTracker} if we are
     *            switching the connection to a new figure; otherwise it is an
     *            existing connector;
     * @param p
     *            is a point on the Drawing
     * @param owner
     *            is the figure to connect to; this is different from the
     *            connection's owner if we are switching the connection to a new
     *            figure
     * @param connection
     *            is an existing connection
     * @param isStartConnector
     *            is a boolean indicating whether we are computing the Start
     *            connector point or the End connector point
     * @return the recomputed connector point
     *
     */
    protected abstract Point2D.Double findConnectorPoint(RelativeConnector relativeConnector,
            Point2D.Double p, Figure owner,
            ConnectionFigure connection, boolean isStartConnector);

    /**
     * <p>
     * Returns the Start or the End Connector Point of a <b>new</b> connection.
     * <p>
     * This is a <i>hook</i> method called by
     * {@link ConnectorSubTracker#createNewConnection}
     * <p>
     * The returned connector point is as close to {@code p} as allowed.
     * <p>
     * Note: When the Connection Tool is released a <i>real</i> connector will
     * be created subject to <code>confirmOrVetoConnection</code>. Creation uses
     * the tracking connector as a prototype.
     * {@link org.jhotdraw.draw.connector.ConnectorSubTracker#createNewConnector}
     *
     * @param relativeConnector
     *            this is a TrackingConnector acting as a proxy for a real
     *            connector.
     * @param p
     *            is a point on the Drawing
     * @param owner
     *            is the figure to connect from
     * @param oppositeOwner
     *            is the figure to connect to
     * @param isStartConnector
     *            is a boolean indicating whether we are computing the Start
     *            connector point or the End connector point
     * @return the recomputed connector point
     */
    protected abstract Point2D.Double findConnectorPointNewConnection(RelativeConnector relativeConnector,
            Point2D.Double p, Figure owner, Figure oppositeOwner, boolean isStartConnector);

    /**
     * Returns the transformed connector point for the transformed bounds of the
     * owner of {@code relativeConnector}
     *
     * @param relativeConnector
     * @param bounds
     *            transformed bounds of owner
     * @return transformed connector point
     */
    protected abstract Point2D.Double findTransformedConnectorPoint(RelativeConnector relativeConnector,
            Rectangle2D.Double bounds);

    /**
     * This method computes the bounds of the figure's effective shape.
     * <p>
     * see {@link ConnectorStrategy#getEffectiveShape(Figure)}
     *
     * @param figure
     * @return bounds rectangle
     */
    protected abstract Rectangle2D.Double getEffectiveBounds(Figure figure);

    /**
     * @param relativeConnector
     * @return bounds rectangle
     */
    protected abstract Rectangle2D.Double getEffectiveBounds(RelativeConnector relativeConnector);

    /**
     * If the strategy has set {@link ConnectorStrategy#isBoundsMode()} to true
     * the shape returned is the Rectangle determined by the <b>bounds
     * rectangle</b> of the figure's {@link Figure#getConnectibleShape()}.
     * <p>
     * Otherwise the shape returned is given by the figure's
     * {@link Figure#getConnectibleShape()}.
     *
     *
     * @param figure
     * @return either the figure's connectible shape or the bounds rectangle of
     *         this shape.
     */

    protected abstract Shape getEffectiveShape(Figure figure);

    /**
     * If the strategy has set {@link ConnectorStrategy#isBoundsMode()} to true
     * the shape returned is the Rectangle determined by the bounds of the
     * owner's {@link Figure#getConnectibleShape()}.
     * <p>
     * Otherwise the shape returned is given by the owner's
     * {@link Figure#getConnectibleShape()}.
     *
     *
     * @param relativeConnector
     * @return either the owner's connectible shape or the bounds rectangle of
     *         this shape.
     */
    protected abstract Shape getEffectiveShape(RelativeConnector relativeConnector);

    /**
     * This encapsulates checking the connectible shape of the figure <b>and</b>
     * checking if the strategy is operating in
     * {@link ConnectorStrategy#isBoundsMode()}.
     * <p>
     * Note that considerable processing is dictated by whether the shape is
     * Rectangular or not.
     *
     *
     * @param figure
     * @return int with a value in
     *         <p>
     *         {@code ConnectorSubTracker#GENERAL_SHAPE}(0), {@code
     *         ConnectorSubTracker#RECTANGULAR_SHAPE}(1), {@code
     *         ConnectorSubTracker#ELLIPTICAL_SHAPE}(2)
     */
    protected abstract int getEffectiveShapeType(Figure figure);

    /**
     * This encapsulates checking the connectible shape of the owner <b>and</b>
     * checking if the strategy is operating in
     * {@link ConnectorStrategy#isBoundsMode()}.
     * <p>
     * Note that considerable processing is dictated by whether the shape is
     * Rectangular or not.
     *
     *
     * @param relativeConnector
     * @return int with a value in
     *         <p>
     *         {@code ConnectorSubTracker#GENERAL_SHAPE}(0), {@code
     *         ConnectorSubTracker#RECTANGULAR_SHAPE}(1), {@code
     *         ConnectorSubTracker#ELLIPTICAL_SHAPE}(2)
     */
    protected abstract int getEffectiveShapeType(RelativeConnector relativeConnector);

    /**
     * @return the name of this strategy
     */
    protected abstract String getName();

    /**
     * Returns true if the strategy has just one distinct connector point.
     * <p>
     * The {@link ChopConnectorStrategy} and {@link CenterConnectorStrategy} are
     * examples where there is just one distinct connector point.
     * <p>
     * Only one connection is allowed if the paired strategies are the same and
     * have this property. <i> only one connection allowed with
     * ChopConnectorStrategy, ChopConnectorStrategy</i>
     * <p>
     * If the opposite strategy is tightly coupled
     * {@link ConnectorStrategy#isConnectorTightlyCoupled} only one connection
     * instance for the pair of strategies is allowed. Otherwise connections
     * would collapse onto one another.
     * <p>
     * e.g. <i> only one connection with paired strategies
     * ChopConnectorStrategy, RectlinearConnectorStrategy is allowed; similarly
     * for CenterConnectorStrategy, RectilinearConnectorStrategy</i>
     * <p>
     *
     *
     * @return true or false
     */
    protected abstract boolean hasSingularConnectorPoint();

    /**
     * In bounds mode the bounds rectangle of the owner's connectible shape is
     * used for all point calculations; <i>all connector points are on the
     * bounds' edges</i>.
     * <p>
     * Otherwise, the owner's connectible shape
     * {@link Figure#getConnectibleShape()} is used for point calculations
     * <p>
     * The bounds rectangle is regarded as including the bottom and right edges
     * which differs from {@code java.awt.Rectangle2D.Double.contains()}
     * <p>
     * If the strategy is not of type {@link BoundaryConnectorStrategy} this
     * property(?) is ignored.
     *
     *
     * @return true if this strategy is a BoundaryStrategy and it uses the
     *         owner's connectible bounds as the effective shape
     *         <p>
     *         false if it is not a BoundaryStrategy or if it uses the figure's
     *         connectible shape as the effective shape
     *         <p>
     *         see {@link Figure#getConnectibleShape()}
     *         <p>
     *         see {@link ConnectorStrategy#getEffectiveShapeType(Figure)}
     */
    protected abstract boolean isBoundsMode();

    /**
     * Return true if the the connector point on the <b>stationary</b> figure is
     * determined by the connector point of the <b>moving</b> figure. (<i> e.g.
     * by projecting the moving connector point onto the opposite figure</i>)
     * <p>
     * see the {@link RectilinearConnectorStrategy}
     * <p>
     *
     * @return true/false
     */
    protected abstract boolean isConnectorTightlyCoupled();

    /**
     * @param connectorSubTracker
     * @param relativeConnector
     */
    protected abstract void slideConnector(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector);

    /**
     * This method recomputes and updates the connector point of {@code
     * relativeConnector}.
     * <p>
     *
     * Touching reconciles {@code relativeConnector} with the opposite connector
     * for tightly coupled strategies <b> regardless of whether figures move or
     * not</b>
     * <p>
     *
     * @param connectorSubTracker
     * @param relativeConnector
     * @return updated connector point
     */
    protected abstract Point2D.Double touchConnector(ConnectorSubTracker connectorSubTracker,
            RelativeConnector relativeConnector);

    /**
     * see {@link RelativeConnector#getConnectorPoint()}
     *
     * @param p
     * @param relativeConnector
     * @return updated connector point
     */
    protected abstract Point2D.Double updateConnectorPoint(Point2D.Double p,
            RelativeConnector relativeConnector);

}
