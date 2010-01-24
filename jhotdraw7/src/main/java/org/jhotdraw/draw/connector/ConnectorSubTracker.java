package org.jhotdraw.draw.connector;

import static org.jhotdraw.draw.AttributeKeys.END_CONNECTOR_STRATEGY;
import static org.jhotdraw.draw.AttributeKeys.START_CONNECTOR_STRATEGY;

import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.geom.Geom;

/**
 * This class controls how line connectors, {@link Connector} objects, are
 * created and adjusted when owner figures change. It uses
 * {@link ConnectorStrategy} to provide different behaviors when connectors are
 * of type {@link RelativeConnector}.
 * <p>
 * It is not a tool but is used by tools.
 * <p>
 * An instance is created once in the DrawingEditor and exists for the lifetime
 * of the editor. {@code DrawingEditor#getConnectorSubTracker()}
 * <p>
 * Tools/Trackers/Handles/Actions/Edits(<i>...clients</i>) that connect figures
 * or cause figure size or position changes, request this class to create
 * connectors or to adjust connectors reflecting the figure changes.
 * <p>
 * It acts as a factory class for {@code RelativeConnector} and {@code
 * ConnectorStrategy} objects. It also provides the <i>Context and Run Time
 * Interface</i> to clients and {@code ConnectorStrategy} objects.
 * <b>ConnectorStrategy is not called by clients</b>
 * <p>
 * Clients call the <b>public</b> methods of this class; {@code
 * ConnectorStrategy} objects call <b>protected</b> methods only. <i>(excluding
 * {@link #isUsingRelativeConnectors()})</i>. The methods in the {@code
 * ConnectorStrategy} interface are essentially hook methods that provide
 * different distinct behaviors to this class.
 * <p>
 * The method
 * {@link #findConnector(java.awt.geom.Point2D.Double, Figure, ConnectionFigure)}
 * is the definitive method called by clients to create connectors or to find
 * existing connectors.
 * <p>
 * Undo edits are created for dragging connector handles, dragging line
 * connection figures and for owner resizing. Undo/Redo functionality for the
 * {@code adjustConnectorsForMoving} methods is handled by {@code TransformEdit}
 * and {@code TransformRestoreEdit} and undo/redo processing for the create
 * methods is handled by clients.
 * <p>
 * <b>Note</b>:
 * <p>
 * The tracking connecting members {@code trackingConnector1} and {@code
 * trackingConnector2} are used as prototypes when new line connections are
 * created or connection end points are dragged to a new figure. The values of
 * these variables need to be maintained from the time the mouse is pressed
 * until it is released.
 * <p>
 * The member {@code prevBoundsMap} maintains a map of selected figures and
 * their rectangular bounds. It is used to determine the <i>previous</i> bounds
 * of a selected figure. This map also needs to be populated at the time the
 * mouse is pressed and maintained until the mouse is released.
 * <i>Alternatively, for tools/trackers, we need the map to be calculated at
 * trackStart and maintained until trackEnd returns.</i>
 * <p>
 * The tracking methods in this class have a parameter {@code tracking} with
 * values of {@code ConnectorSubTracker#trackStart} {@code
 * ConnectorSubTracker#trackStep} and {@code ConnectorSubTracker#trackEnd}.
 * Instance variables are initialized and populated when {@code tracking} equals
 * {@code ConnectorSubTracker#trackStart}.
 * <p>
 *
 *
 *
 *
 * <p>
 * <b>MIGRATION</b>
 * <p>
 * This class has been implemented to use either {@link RelativeConnector}
 * objects or <i>(exclusive or)</i> <b>non</b> {@code RelativeConnector}
 * objects.
 * <p>
 * If the static method {@code ConnectorSubTracker#isUsingRelativeConnectors()}
 * returns <b>false</b>, creation of connectors will be delegated to the method
 * {@link Figure#findConnector(java.awt.geom.Point2D.Double, ConnectionFigure)}.
 * <p>
 * This is the <i>'old way'</i> of using connectors and this class will
 * effectively ignore all adjustments and manipulations of connectors; no
 * strategy will be employed in this case.
 * <p>
 * If the static method {@code isUsingRelativeConnectors} returns <b>true</b>,
 * there are two cases to consider:
 * <p>
 * Case 1 is where an application is using <b>non</b> {@code RelativeConnector}
 * objects in it's initial Storable representation but is migrating to these
 * objects. In this case, this class will, on startup, set the strategy
 * attributes of all lines to the string "ChopConnectorStrategy" and write the
 * Storable representation using {@code RelativeConnector} objects and {@code
 * ConnectorStrategy} objects.
 * <p>
 * See {@link ConnectorSubTracker#migrateToRelativeConnectors} and
 * {@link org.jhotdraw.draw.LineConnectionFigure#read}
 * <p>
 * Case 2 is where {@code RelativeConnector} objects and {@code
 * ConnectorStrategy} objects are used exclusively and there are no <b>non</b>
 * {@code RelativeConnector} objects. The full {@code ConnectorStrategy} api is
 * used in this case.
 * <p>
 * {@code RelativeConnector} objects <b>cannot</b> be mixed with <b>non</b>
 * {@code RelativeConnector} objects in an application. This class will handle
 * one type or the other but not both.
 * <p>
 *
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *         <i> Code Line Length 120 </i>
 *         <p>
 */
public class ConnectorSubTracker {
    public final static int                           trackStart         = 1;
    public final static int                           trackStep          = 2;
    public final static int                           trackEnd           = 3;

    public static int                                 GENERAL_SHAPE      = 0;
    public static int                                 RECTANGULAR_SHAPE  = 1;
    public static int                                 ELLIPTICAL_SHAPE   = 2;



    // this is set to the editor's active view when tools initially retrieve
    // this object
    private DrawingView                               view;

    // used for Connection Tool and ConnectorHandle dragging
    private final RelativeConnector                   trackingConnector1 = new RelativeConnector();
    private final RelativeConnector                   trackingConnector2 = new RelativeConnector();

    // caches the 'bounds' of all selected figures BEFORE tools or handles alter
    // these bounds
    private HashMap<Figure, Rectangle2D.Double>       prevBoundsMap;

    // clones of all connectors on the figure prior to resizing or sliding a
    // figure
    private ArrayList<Connector>                      cachedConnectors;

    // a clone of the connection prior to dragging connector handles or dragging
    // connections
    private LineConnectionFigure                      cachedConnection;

    // an optimization for looking up ConnectorStrategy
    private static HashMap<String, ConnectorStrategy> cachedStrategies   = createCachedStrategies();

    // identifies the start or end segment of a multi-point(>2) line during
    // dragging
    int                                               draggedLineSegment;


    /**
     * Checks if new connectors are allowed with a {@link ConnectorStrategy}
     * determined by {@code strategyName} and/or existing connectors are
     * compatible with changing the strategy to {@code strategyName}.
     * <p>
     * {@link ConnectorStrategy#compatibleWithOpposite}
     * {@link ConnectorStrategy#compatibleWithOwnerFigure}
     * {@link ConnectorStrategy#compatibleWithNewOppositeStrategy}
     *
     * @param connections
     * @param strategyName
     * @param isStartStrategy
     * @param compatibleMsgs
     * @return collection of incompatible connections
     */
    public static Collection<Figure> checkStrategyCompatibility(Collection<LineConnectionFigure> connections,
            String strategyName, boolean isStartStrategy, List<String> compatibleMsgs) {

        if (!isUsingRelativeConnectors())
            return new ArrayList<Figure>();

        final ArrayList<Figure> incompatibleFigures = new ArrayList<Figure>();
        for (final LineConnectionFigure connection : connections) {
            final int changeCountForOwnerPair = calculateChangesForOwnerPair(connection, connections);
            if (!checkStrategyCompatibility(connection, strategyName, isStartStrategy, compatibleMsgs,
                    changeCountForOwnerPair))
                incompatibleFigures.add(connection);
        }
        return incompatibleFigures;
    }

    /**
     * Checks if a new connector is allowed with a {@link ConnectorStrategy}
     * determined by {@code strategyName} and/or existing connectors are
     * compatible with changing the strategy to {@code strategyName}.
     * <p>
     * {@link ConnectorStrategy#compatibleWithOpposite(RelativeConnector, boolean, List, int)}
     * {@link ConnectorStrategy#compatibleWithOwnerFigure(RelativeConnector, Figure, List)}
     *
     * @param connection
     * @param strategyName
     * @param isStartStrategy
     * @param compatibleMsgs
     * @param changeCount
     * @return true/false
     */
    protected static boolean checkStrategyCompatibility(LineConnectionFigure connection, String strategyName,
            boolean isStartStrategy, List<String> compatibleMsgs, int changeCount) {
        final ConnectorStrategy newStrategy = findConnectorStrategy(strategyName);
        if (newStrategy == null)
            return false;

        final ConnectorStrategy startStrategy = findConnectorStrategy(connection.findStartConnectorStrategyName());
        final ConnectorStrategy endStrategy = findConnectorStrategy(connection.findEndConnectorStrategyName());

        RelativeConnector relativeConnector = (RelativeConnector) connection.getStartConnector();
        ConnectorStrategy oppositeStrategy = endStrategy;
        if (!isStartStrategy) {
            relativeConnector = (RelativeConnector) connection.getEndConnector();
            oppositeStrategy = startStrategy;
        }

        final Figure owner = relativeConnector.getOwner();
        boolean answer = true;
        if (oppositeStrategy != null) {
            answer = newStrategy
                    .compatibleWithOpposite(relativeConnector, isStartStrategy, compatibleMsgs, changeCount);
        }
        if (answer)
            answer = newStrategy.compatibleWithOwnerFigure(relativeConnector, owner, compatibleMsgs);

        return answer;
    }
    /**
     * @return cached map of strategies
     */
    private static HashMap<String, ConnectorStrategy> createCachedStrategies() {
        final HashMap<String, ConnectorStrategy> strategies = new HashMap<String, ConnectorStrategy>();
        strategies.put("FixedBoundaryConnectorStrategy", new FixedBoundaryConnectorStrategy());
        strategies.put("EdgeConnectorStrategy", new EdgeConnectorStrategy());
        strategies.put("RotationalConnectorStrategy", new RotationalConnectorStrategy());
        strategies.put("ChopConnectorStrategy", new ChopConnectorStrategy());
        strategies.put("RectilinearConnectorStrategy", new RectilinearConnectorStrategy());
        strategies.put("InteriorConnectorStrategy()", new InteriorConnectorStrategy());
        strategies.put("CenterConnectorStrategy", new CenterConnectorStrategy());
        return strategies;
    }

    /**
     * @param relativeConnector
     * @return ConnectorStrategy object
     */
    protected static ConnectorStrategy findConnectorStrategy(RelativeConnector relativeConnector) {
        ConnectorStrategy strategy = null;
        final String strategyName = findConnectorStrategyName(relativeConnector);
        if (strategyName != null)
            strategy = findConnectorStrategy(findConnectorStrategyName(relativeConnector));
        return strategy;
    }

    /**
     * returns either an initial cached strategy object or a newly instantiated
     * strategy
     *
     * @param strategyName
     * @return ConnectorStrategy object
     */
    private static ConnectorStrategy findConnectorStrategy(String strategyName) {
        ConnectorStrategy result = null;
        if (strategyName == null || strategyName.length() == 0)
            return null;

        result = cachedStrategies.get(strategyName);
        if (result != null)
            return result;

        try {
            final Class<?> t = Class.forName("org.jhotdraw.draw.connector." + strategyName);
            result = (ConnectorStrategy) t.newInstance();
            cachedStrategies.put(strategyName, result);
        }
        catch (final ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (final IllegalAccessException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (final InstantiationException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return result;
    }

    /**
     * Returns the strategy name for this connector. (more precisely for this
     * end of the connection)
     *
     * @param relativeConnector
     * @return strategy name
     */
    private static String findConnectorStrategyName(RelativeConnector relativeConnector) {
        final ConnectionFigure connection = relativeConnector.getLineConnection();
        final Connector startConnector = connection.getStartConnector();
        if (startConnector == null || startConnector == relativeConnector)
            return connection.findStartConnectorStrategyName();
        return connection.findEndConnectorStrategyName();
    }

    /**
     * Utility method to find the bounds of the opposite connector
     *
     * @param relativeConnector
     * @return bounds rectangle
     */
    protected static Rectangle2D.Double findOppositeBounds(RelativeConnector relativeConnector) {
        final RelativeConnector oppositeConnector = ConnectorSubTracker.findOppositeConnector(relativeConnector);
        final ConnectorStrategy oppositeStrategy = findConnectorStrategy(oppositeConnector);
        return oppositeStrategy.getEffectiveBounds(oppositeConnector);
    }

    /**
     * Utility method to find the opposite <b>connection</b> point
     *
     * @param relativeConnector
     * @return opposite connection point
     */
    protected static Point2D.Double.Double findOppositeConnectionPoint(RelativeConnector relativeConnector) {
        final LineConnectionFigure connection = relativeConnector.getLineConnection();
        Point2D.Double answer = connection.getPoint(1);
        if (!relativeConnector.isStartConnector())
            answer = connection.getPoint(connection.getNodeCount() - 2);
        return answer;
    }

    /**
     * Retrieves the EndConnector if <i>relativeConnector</i> is the
     * StartConnector and vice-versa.
     * <p>
     * Note that start/end Connectors can not be determined while a connection
     * is being swapped to a different figure. This method should not be called
     * if that can happen; use the {@code isStartConnector} parameter in the
     * method {@link ConnectorStrategy#findConnectorPoint} in this situation.
     *
     * @param relativeConnector
     * @return oppositeConnector or null
     */
    protected static RelativeConnector findOppositeConnector(RelativeConnector relativeConnector) {
        final LineConnectionFigure connection = relativeConnector.getLineConnection();
        final RelativeConnector startConnector = (RelativeConnector) connection.getStartConnector();
        final RelativeConnector endConnector = (RelativeConnector) connection.getEndConnector();

        if (relativeConnector == startConnector)
            return endConnector;
        else
            if (relativeConnector == endConnector)
                return startConnector;
            // new connection ... the start/end are still tracking connectors
            else
                if (relativeConnector.getOwner() == startConnector.getOwner())
                    return endConnector;
                else
                    if (relativeConnector.getOwner() == endConnector.getOwner())
                        return startConnector;

        return null;
    }

    /**
     * @param conn
     * @return true/false
     */
    private static boolean includeConnection(LineConnectionFigure conn) {
        if (conn.findStartConnectorStrategyName() == null || conn.findStartConnectorStrategyName().length() == 0)
            return false;
        if (conn.getStartFigure() == conn.getEndFigure())
            return false;
        return true;
    }

    /**
     * @return true/false
     */
    public static final boolean isUsingRelativeConnectors() {
        return true;
    }

    /**
     * This method converts <b>NON Relative Connectors</b> to
     * {@link RelativeConnector} objects and sets the connection's strategies to
     * {@link ChopConnectorStrategy}
     * <p>
     * This should provide a seamless transition to Connector Strategies.
     * <p>
     * This method is called only in
     * {@link org.jhotdraw.draw.LineConnectionFigure#read}
     *
     * @param connection
     */
    public static void migrateToRelativeConnectors(LineConnectionFigure connection) {
        if (!isUsingRelativeConnectors())
            return;

        final String startConnectorStrategyName = connection.findStartConnectorStrategyName();
        final String endConnectorStrategyName = connection.findEndConnectorStrategyName();

        if (startConnectorStrategyName == null && endConnectorStrategyName == null) {
            final Connector startConnector = connection.getStartConnector();
            final Figure startOwner = startConnector.getOwner();
            final Connector endConnector = connection.getEndConnector();
            final Figure endOwner = endConnector.getOwner();
            final Shape startShape = startOwner.getConnectibleShape();
            final Shape endShape = endOwner.getConnectibleShape();

            final Rectangle2D.Double r1 = (Rectangle2D.Double) startShape.getBounds2D();
            final Point2D.Double p1 = ConnectorGeom.calculateChopPoint(startShape, endShape);
            final double startRelativeX = p1.x - r1.x;
            final double startRelativeY = p1.y - r1.y;
            final RelativeConnector newStartConnector = new RelativeConnector(startOwner, startRelativeX,
                    startRelativeY);
            newStartConnector.setLineConnection(connection);

            final Rectangle2D.Double r2 = (Rectangle2D.Double) endShape.getBounds2D();
            final Point2D.Double p2 = ConnectorGeom.calculateChopPoint(endShape, startShape);
            final double endRelativeX = p2.x - r2.x;
            final double endRelativeY = p2.y - r2.y;
            final RelativeConnector newEndConnector = new RelativeConnector(endOwner, endRelativeX, endRelativeY);
            newEndConnector.setLineConnection(connection);

            connection.willChange();
            connection.set(START_CONNECTOR_STRATEGY, "ChopConnectorStrategy");
            connection.set(END_CONNECTOR_STRATEGY, "ChopConnectorStrategy");
            connection.setStartConnector(newStartConnector);
            connection.setEndConnector(newEndConnector);
            connection.updateConnection();
            connection.changed();
        }
    }

    /**
     * @param connection
     * @param connections
     * @return
     */
    private static int calculateChangesForOwnerPair(LineConnectionFigure connection,
            Collection<LineConnectionFigure> connections) {
        int result = 0;
        final Figure startOwner = connection.getStartFigure();
        final Figure endOwner = connection.getEndFigure();
        for (final LineConnectionFigure conn : connections) {
            if (startOwner == conn.getStartFigure() && endOwner == conn.getEndFigure())
                result++;
        }
        return result;
    }

    /**
     * lazily called once in
     * {@code org.jhotdraw.draw.DefaultDrawingEditor#getConnectorSubTracker()}.
     * The resultant instance lasts for the lifetime of the editor.
     * <p>
     */
    public ConnectorSubTracker() {
    }

    /**
     * Call the strategy's adjust methods for all the connectors of the
     * <b>selected figure</b>
     * <p>
     * These connectors are organized by strategy/oppositeStrategy/opposite
     * owner in the map <code>connectors</code>
     * <p>
     * The strategy's adjust opposite methods are called immediately after this.
     * <i>(for each connected opposite figure)</i>
     * <p>
     * Finally all involved connections are updated
     *
     * @param selectedFigure
     * @param connectors
     * @param selectedFiguresAndChildren
     */
    private void adjustConnectorsForFigureImpl(Figure selectedFigure,
            HashMap<String, HashSet<RelativeConnector>> connectors,
            Collection<Figure> selectedFiguresAndChildren) {
        final HashSet<LineConnectionFigure> connectionsToUpdate = new HashSet<LineConnectionFigure>();
        final Set<String> strategyKeySet = connectors.keySet();
        for (final String strategyKey : strategyKeySet) {
            final HashSet<RelativeConnector> connectorsByKey = connectors.get(strategyKey);
            final int index1 = strategyKey.indexOf('^');
            final int index2 = strategyKey.lastIndexOf('^');
            final String strategyName = strategyKey.substring(0, index1);
            final String oppStrategyName = strategyKey.substring(index1 + 1, index2);
            final ConnectorStrategy connectorStrategy = findConnectorStrategy(strategyName);
            final ConnectorStrategy oppositeStrategy = findConnectorStrategy(oppStrategyName);
            final ArrayList<RelativeConnector> directConnectors = new ArrayList<RelativeConnector>();
            final ArrayList<RelativeConnector> multiConnectors = new ArrayList<RelativeConnector>();
            final ArrayList<RelativeConnector> directOppConnectors = new ArrayList<RelativeConnector>();
            final ArrayList<RelativeConnector> multiOppConnectors = new ArrayList<RelativeConnector>();

            // split each strategy's set of connectors into direct and
            // multipoint sets

            for (final RelativeConnector relativeConnector : connectorsByKey) {
                final RelativeConnector oppositeConnector = findOppositeConnector(relativeConnector);
                final LineConnectionFigure connection = relativeConnector.getLineConnection();
                connectionsToUpdate.add(connection);
                if (connection.getNodeCount() <= 2) {
                    directConnectors.add(relativeConnector);
                    directOppConnectors.add(oppositeConnector);
                }
                else {
                    multiConnectors.add(relativeConnector);
                    multiOppConnectors.add(oppositeConnector);
                }
            }
            // it does not matter if the strategy changes local variables
            // directConnectors, multiConnectors, etc.
            if (directConnectors.size() > 0) {
                connectorStrategy.adjustConnectorsForMoving(this, directConnectors);
                oppositeStrategy.adjustConnectorsForMovingOpposite(this, directOppConnectors);
            }
            if (multiConnectors.size() > 0) {
                connectorStrategy.adjustConnectorsMultiForMoving(this, multiConnectors);
                oppositeStrategy.adjustConnectorsMultiForMovingOpposite(this, multiOppConnectors);
            }
        }

        for (final LineConnectionFigure connection : connectionsToUpdate) {
            connection.updateConnection();
        }
    }

    /**
     * Call the strategy's <i>adjustConnectorforMoving</i> for every connector
     * of each figure in {@code selectedFigures}.
     * <p>
     * Note:
     * <p>
     * A child figure is deemed selected if it's owner (<i>
     * {@link org.jhotdraw.draw.AbstractCompositeFigure}</i>) is selected.
     * <p>
     * LineConnection figures are not dragged see
     * {@link ConnectorSubTracker#adjustConnectorsForMovingV(int,
     * int, java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)}
     *
     * @param selectedFigures
     * @param tracking
     *            value in {@link ConnectorSubTracker#trackStart},
     *            {@link ConnectorSubTracker#trackStep} and
     *            {@link ConnectorSubTracker#trackEnd}
     * @param modifiersEx
     *            extended modifier mask from originating event
     */
    public void adjustConnectorsForMoving(Collection<Figure> selectedFigures, int tracking, int modifiersEx) {
        if (!isUsingRelativeConnectors())
            return;

        switch (tracking) {
            case ConnectorSubTracker.trackStart: {
                final Collection<Figure> selectedFiguresAndChildren = start(selectedFigures, false);
                createSelectedPrevBoundsMap(selectedFiguresAndChildren);
                break;
            }

            case ConnectorSubTracker.trackStep: {
                final Collection<Figure> selectedFiguresAndChildren = prevBoundsMap.keySet();
                for (final Figure f : selectedFiguresAndChildren) {
                    adjustSelectedFigure(f, selectedFiguresAndChildren, modifiersEx);
                }
                createSelectedPrevBoundsMap(selectedFiguresAndChildren);
                break;
            }

            case ConnectorSubTracker.trackEnd: {
                prevBoundsMap = new HashMap<Figure, Rectangle2D.Double>();
                cachedConnectors = new ArrayList<Connector>();
                cachedConnection = null;
                break;
            }
        } // end switch
    }

    /**
     * Call the strategy's <i>adjustConnectorforMoving</i> for every connector
     * of each selected <b>non-connection</b> figure <b>in the view</b>.
     * <p>
     * Note:
     * <p>
     * A child figure is deemed selected if it's owner (<i>
     * {@link org.jhotdraw.draw.CompositeFigure}</i>) is selected.
     * <p>
     * This method has no fromPoint or toPoint so there is no connection line
     * dragging.
     *
     * @param tracking
     *            value in {@link ConnectorSubTracker#trackStart},
     *            {@link ConnectorSubTracker#trackStep} and
     *            {@link ConnectorSubTracker#trackEnd}
     * @param modifiersEx
     *            extended modifier mask from originating event
     */
    public void adjustConnectorsForMovingV(int tracking, int modifiersEx) {
        if (!isUsingRelativeConnectors())
            return;
        adjustConnectorsForMovingV(tracking, modifiersEx, null, null);
    }

    /**
     * Call the strategy's <i>adjustConnectorforMoving</i> for every connector
     * of each selected figure <b>in the view</b>.
     * <p>
     * Note:
     * <p>
     * A child figure is deemed selected if it's owner (<i>
     * {@link org.jhotdraw.draw.AbstractCompositeFigure}</i>) is selected.
     * <p>
     * Selected lines are dragged if the fromPoint/ToPoint are not null.
     * <p>
     * <i>Currently only one selected line at a time can be dragged. This is
     * dictated by undo/redo complexity in dragging multiple lines and the
     * potential for dragged lines to run into one another.</i>
     *
     * @param tracking
     *            value in {@link ConnectorSubTracker#trackStart},
     *            {@link ConnectorSubTracker#trackStep} and
     *            {@link ConnectorSubTracker#trackEnd}
     * @param modifiersEx
     *            extended modifier mask from originating event
     * @param fromPoint
     * @param toPoint
     */
    public void adjustConnectorsForMovingV(int tracking, int modifiersEx, Point2D.Double fromPoint,
            Point2D.Double toPoint) {
        if (!isUsingRelativeConnectors())
            return;

        LineConnectionFigure draggedLine = null;
        if (view.getSelectedFigures().size() == 1 && fromPoint != null && toPoint != null) {
            final Figure f = view.getSelectedFigures().iterator().next();
            if (f instanceof LineConnectionFigure) {
                draggedLine = (LineConnectionFigure) f;
            }
        }

        switch (tracking) {
            case ConnectorSubTracker.trackStart: {
                // sliding is going to happen so automatically cache connectors
                if ((modifiersEx & InputEvent.ALT_DOWN_MASK) != 0)
                    start(view, false);
                else
                    if (fromPoint == null || toPoint == null)
                        start(view, false);
                    else
                        start(view, true);

                if (draggedLine != null && draggedLine.getNodeCount() > 2) {
                    final int segment = draggedLine.findSegment(toPoint, 2.0);
                    final int segmentCount = draggedLine.getNodeCount() / 2 + 1;
                    if (segment == 0 || segment == segmentCount - 1)
                        draggedLineSegment = segment;
                }
                break;
            }

            case ConnectorSubTracker.trackStep: {
                if (draggedLine != null) {
                    if (draggedLineSegment != -1)
                        dragLineSegment(draggedLine, tracking, fromPoint, toPoint, modifiersEx);
                    else
                        dragConnectionFigure(draggedLine, tracking, fromPoint, toPoint, modifiersEx);
                }
                else
                    adjustConnectorsForMoving(view.getSelectedFigures(), ConnectorSubTracker.trackStep, modifiersEx);

                break;
            }

            case ConnectorSubTracker.trackEnd: {
                if (draggedLine != null)
                    fireUndoableConnectionEdit(draggedLine, cachedConnection);
                break;
            }
        } // end switch
    }

    /**
     * Call the strategy's <i>adjustConnectorForResizing</i> for every selected
     * figure.
     *
     * @param selectedFigures
     * @param tracking
     *            value in {@link ConnectorSubTracker#trackStart},
     *            {@link ConnectorSubTracker#trackStep} and
     *            {@link ConnectorSubTracker#trackEnd}
     * @param modifiersEx
     *            extended modifier mask from originating event
     */
    public void adjustConnectorsForResizingRestoring(Collection<Figure> selectedFigures, int tracking, int modifiersEx) {
        if (!isUsingRelativeConnectors())
            return;

        switch (tracking) {
            case ConnectorSubTracker.trackStart: {
                final Collection<Figure> selectedFiguresAndChildren = start(selectedFigures, false);
                createSelectedPrevBoundsMap(selectedFiguresAndChildren);
                break;
            }

            case ConnectorSubTracker.trackStep: {
                final Collection<Figure> selectedFiguresAndChildren = prevBoundsMap.keySet();
                adjustResizedFigures(selectedFiguresAndChildren, tracking, modifiersEx);
                createSelectedPrevBoundsMap(selectedFiguresAndChildren);
                break;
            }

            case ConnectorSubTracker.trackEnd: {
                prevBoundsMap = new HashMap<Figure, Rectangle2D.Double>();
                cachedConnectors = new ArrayList<Connector>();
                cachedConnection = null;
                break;
            }
        } // end switch

    }

    /**
     * Call the strategy's <i>adjustConnectorForResizing</i> for every selected
     * Figure in the view
     *
     * @param tracking
     *            value in {@link ConnectorSubTracker#trackStart},
     *            {@link ConnectorSubTracker#trackStep} and
     *            {@link ConnectorSubTracker#trackEnd}
     * @param modifiersEx
     *            extended modifier mask from originating event
     */
    public void adjustConnectorsForResizingV(int tracking, int modifiersEx) {
        if (!isUsingRelativeConnectors())
            return;

        switch (tracking) {
            case ConnectorSubTracker.trackStart: {
                // always cache connectors for resizing
                start(view, false);
                break;
            }

            case ConnectorSubTracker.trackStep: {
                adjustConnectorsForResizingRestoring(view.getSelectedFigures(), ConnectorSubTracker.trackStep,
                        modifiersEx);
                break;
            }

            case ConnectorSubTracker.trackEnd: {
                adjustConnectorsForResizingRestoring(view.getSelectedFigures(), ConnectorSubTracker.trackEnd,
                        modifiersEx);
                break;
            }
        }// end switch

    }

    /**
     * @param selectedFiguresAndChildren
     * @param tracking
     *            value in {@link ConnectorSubTracker#trackStart},
     *            {@link ConnectorSubTracker#trackStep} and
     *            {@link ConnectorSubTracker#trackEnd}
     * @param modifiersEx
     *            extended modifier mask from originating event
     */
    private void adjustResizedFigures(Collection<Figure> selectedFiguresAndChildren, int tracking, int modifiersEx) {
        final Collection<RelativeConnector> connectors = new HashSet<RelativeConnector>();
        for (final Figure f : selectedFiguresAndChildren) {
            final Collection<LineConnectionFigure> connections = f.getConnections();
            for (final LineConnectionFigure conn : connections) {
                if (!includeConnection(conn))
                    continue;
                final RelativeConnector startConnector = (RelativeConnector) conn.getStartConnector();
                final RelativeConnector endConnector = (RelativeConnector) conn.getEndConnector();
                if (startConnector.getOwner() == f)
                    connectors.add(startConnector);
                else
                    connectors.add(endConnector);
            }
        }
        for (final RelativeConnector relativeConnector : connectors) {
            final ConnectorStrategy connectorStrategy = findConnectorStrategy(relativeConnector);
            connectorStrategy.adjustConnectorForResizing(this, relativeConnector);
            relativeConnector.getLineConnection().updateConnection();
        }
    }

    /**
     * For each selected/moving figure, build a map of connectors by connector
     * strategy/opposite owner. Call the adjust methods for the connectors in
     * this map
     *
     * @param f
     *            a selected figure. (non-connection figure)
     * @param selectedFiguresAndChildren
     * @param modifiersEx
     *            extended modifier mask from originating event
     */
    private void adjustSelectedFigure(Figure f, Collection<Figure> selectedFiguresAndChildren,
            int modifiersEx) {
        final HashMap<String, HashSet<RelativeConnector>> connectors =
            new HashMap<String, HashSet<RelativeConnector>>();
        final Collection<Figure> thisFigureAndChildren = f.getDecomposition();
        for (final LineConnectionFigure conn : f.getConnections()) {
            if (!includeConnection(conn))
                continue;
            final RelativeConnector startConnector = (RelativeConnector) conn.getStartConnector();
            final RelativeConnector endConnector = (RelativeConnector) conn.getEndConnector();
            RelativeConnector relativeConnector = startConnector;
            if (!thisFigureAndChildren.contains(relativeConnector.getOwner()))
                relativeConnector = endConnector;

            final ConnectorStrategy connectorStrategy = findConnectorStrategy(relativeConnector);

            if ((modifiersEx & InputEvent.ALT_DOWN_MASK) != 0) {
                connectorStrategy.slideConnector(this, relativeConnector);
                continue;
            }

            final RelativeConnector oppositeConnector = findOppositeConnector(relativeConnector);
            final Figure owner = relativeConnector.getOwner();
            final Figure oppositeOwner = oppositeConnector.getOwner();
            final double deltaCLAngle = ConnectorGeom.calculateCLAngleChange(owner.getBounds(), getPrevBounds(owner),
                    oppositeOwner.getBounds(), getPrevBounds(oppositeOwner));
            // if no change in CLAngle the figures have not moved relative to
            // one another ... ignore
            if (Math.abs(deltaCLAngle) < ConnectorGeom.epsilon && conn.getNodeCount() == 2)
                continue;

            final String strategyName = findConnectorStrategyName(relativeConnector);
            final String oppStrategyName = findConnectorStrategyName(oppositeConnector);
            final String strategyKey = strategyName + "^" + oppStrategyName + "^"
                    + getOwnerPairKey(owner, oppositeOwner);

            HashSet<RelativeConnector> connectorsByKey = connectors.get(strategyKey);
            if (connectorsByKey == null) {
                connectorsByKey = new HashSet<RelativeConnector>();
                connectors.put(strategyKey, connectorsByKey);
            }
            connectorsByKey.add(relativeConnector);
        }

        if (connectors.size() > 0)
            adjustConnectorsForFigureImpl(f, connectors, selectedFiguresAndChildren);
    }

    /**
     * @param owner
     * @param relativeX
     * @param relativeY
     * @return new RelativeConnector
     */
    protected RelativeConnector createConnector(Figure owner, double relativeX, double relativeY) {
        return new RelativeConnector(owner, relativeX, relativeY);
    }

    /**
     * @param prototypeConnector
     * @return RelativeConnector or null if vetoed
     */
    protected RelativeConnector createFinalConnector(RelativeConnector prototypeConnector, boolean isStartConnector) {
        final Figure owner = prototypeConnector.getOwner();
        final ConnectionFigure connection = prototypeConnector.getLineConnection();
        final ConnectorStrategy connectorStrategy = findConnectorStrategy(prototypeConnector);

        RelativeConnector newConnector = createConnector(owner, prototypeConnector.getRelativeX(), prototypeConnector
                .getRelativeY());
        newConnector.setLineConnection(connection);

        // give strategies a chance to veto connector/connection
        // end connector should only be null for self connections
        if (connection.getEndConnector() != null)
            if (!connectorStrategy.confirmOrVetoConnector(this, newConnector, (JComponent) this.view, isStartConnector))
                newConnector = null;

        // return final connector
        return newConnector;
    }

    /**
     * @param startConnector
     * @param endConnector
     * @param connection
     *            the created connection
     * @param tracking
     * @return array of 2; startConnector and endConnector
     */
    public Connector[] createNewConnection(Connector startConnector, Connector endConnector,
            ConnectionFigure connection, int tracking) {
        final Connector[] startEndConnectors = { startConnector, endConnector };
        if (!isUsingRelativeConnectors())
            return startEndConnectors;

        switch (tracking) {
            case ConnectorSubTracker.trackStart: {
                start(view, false);
                break;
            }
            case ConnectorSubTracker.trackStep: {
                break;
            }
            case ConnectorSubTracker.trackEnd: {
                if (connection.findStartConnectorStrategyName() != null && startConnector != null
                        && endConnector != null) {
                    // give strategy a chance to confirm/veto the
                    // connection/connector
                    startConnector = createNewConnector(startConnector, connection, true, true);
                    endConnector = createNewConnector(endConnector, connection, false, true);
                }
                if (startConnector != null && endConnector != null
                        && startConnector.getOwner() == endConnector.getOwner()) {
                    createSelfConnection((RelativeConnector) startConnector, (RelativeConnector) endConnector);
                }
                startEndConnectors[0] = startConnector;
                startEndConnectors[1] = endConnector;
                break;
            }
        } // end switch
        return startEndConnectors;
    }

    /**
     * @param connector
     *            this is always a tracking connector
     * @param connection
     * @param isStartConnector
     * @param isNewConnection
     * @return new connector or null if vetoed
     */
    private Connector createNewConnector(Connector connector, ConnectionFigure connection, boolean isStartConnector,
            boolean isNewConnection) {
        if (!isTrackingConnector(connector))
            return connector;

        final RelativeConnector trackingConnector = (RelativeConnector) connector;
        final Figure owner = trackingConnector.getOwner();
        Figure oppositeOwner = null;
        if (isNewConnection) {
            oppositeOwner = trackingConnector == trackingConnector1 ? trackingConnector2.getOwner()
                    : trackingConnector1.getOwner();
            // self connection
            if (oppositeOwner == null && trackingConnector == trackingConnector1)
                oppositeOwner = owner;
        }
        else {
            oppositeOwner = isStartConnector ? connection.getEndFigure() : connection.getStartFigure();
        }

        trackingConnector.setLineConnection(connection);
        if (isNewConnection && trackingConnector == trackingConnector1) {
            connection.setStartConnector(trackingConnector1);
            trackingConnector1.setLineConnection(connection);
            // set connection on trackingConnector2 when trackingConnector1 is
            // encountered
            // ... call to findConnectorPoint below can depend on both being set
            if (trackingConnector2.getLineConnection() != null) {
                connection.setEndConnector(trackingConnector2);
                trackingConnector2.setLineConnection(connection);
            }
        }

        Point2D.Double p = null;
        final ConnectorStrategy connectorStrategy = findConnectorStrategy(trackingConnector);

        final Point2D.Double trackPt = trackingConnector.getConnectorPoint();
        if (isNewConnection)
            p = connectorStrategy.findConnectorPointNewConnection(trackingConnector, trackPt, owner, oppositeOwner,
                    isStartConnector);
        else {
            // in this method, a call to findConnectorPoint can only mean
            // a change of owner figure
            // however we disallow dragging a connector point for a child of
            // composite figure
            RelativeConnector origConnector = (RelativeConnector) connection.getStartConnector();
            if (!isStartConnector)
                origConnector = (RelativeConnector) connection.getEndConnector();
            final Figure origOwner = origConnector.getOwner();
            if (owner != origOwner) {
                final Collection<Figure> children = owner.getDecomposition();
                if (children.size() > 1 && children.contains(origOwner))
                    return null;
            }
            p = connectorStrategy.findConnectorPoint(trackingConnector, trackPt, owner, connection, isStartConnector);
        }

        if (p == null)
            return null;
        connectorStrategy.updateConnectorPoint(p, trackingConnector);
        return createFinalConnector(trackingConnector, isStartConnector);
    }

    /**
     * @param selectedFigures
     */
    private void createSelectedPrevBoundsMap(Collection<Figure> selectedFigures) {
        prevBoundsMap = new HashMap<Figure, Rectangle2D.Double>(selectedFigures.size());
        for (final Figure f : selectedFigures) {
            prevBoundsMap.put(f, f.getBounds());
            final Collection<Figure> children = f.getDecomposition();
            for (final Figure child : children) {
                prevBoundsMap.put(child, child.getBounds());
            }
        }
    }

    /**
     * @param startConnector
     * @param endConnector
     */
    private void createSelfConnection(RelativeConnector startConnector, RelativeConnector endConnector) {
        final ConnectorStrategy connectorStrategy = findConnectorStrategy(startConnector);
        final Figure owner = startConnector.getOwner();
        final LineConnectionFigure conn = startConnector.getLineConnection();
        final Rectangle2D.Double r1 = connectorStrategy.getEffectiveBounds(owner);
        final Point2D.Double p1 = ConnectorGeom.angleToPointGeom(r1, ConnectorGeom.pointToAngleGeom(r1, conn
                .getPoint(1)));

        conn.setSelfConnection();

        connectorStrategy.updateConnectorPoint(p1, startConnector);
        connectorStrategy.updateConnectorPoint(p1, endConnector);
    }

    /**
     * Drag a connection figure and adjust the start and end connectors.
     * <p>
     * The start and end connector strategies control the dragging;
     *
     * @param conn
     * @param tracking
     *            value in {@link ConnectorSubTracker#trackStart},
     *            {@link ConnectorSubTracker#trackStep} and
     *            {@link ConnectorSubTracker#trackEnd}
     * @param fromPoint
     * @param toPoint
     * @param modifiersEx
     * @return true if the line is dragged
     */
    public boolean dragConnectionFigure(LineConnectionFigure conn, int tracking, Point2D.Double fromPoint,
            Point2D.Double toPoint, int modifiersEx) {
        if (!isUsingRelativeConnectors())
            return false;

        if (fromPoint == null || toPoint == null || view == null)
            return false;

        if (conn.getNodeCount() != 2)
            return false;

        final RelativeConnector startConnector = (RelativeConnector) conn.getStartConnector();
        final Figure startOwner = startConnector.getOwner();
        final RelativeConnector endConnector = (RelativeConnector) conn.getEndConnector();
        final Figure endOwner = endConnector.getOwner();
        final ConnectorStrategy startConnectorStrategy = findConnectorStrategy(startConnector);
        final ConnectorStrategy endConnectorStrategy = findConnectorStrategy(endConnector);
        final Rectangle2D.Double rStart = startConnectorStrategy.getEffectiveBounds(startOwner);
        final Rectangle2D.Double rEnd = endConnectorStrategy.getEffectiveBounds(endOwner);

        if (view.isFigureSelected(startOwner) || view.isFigureSelected(endOwner))
            return false;

        double deltaX = toPoint.x - fromPoint.x;
        double deltaY = toPoint.y - fromPoint.y;
        if (Math.abs(deltaX) < ConnectorGeom.epsilon && Math.abs(deltaY) < ConnectorGeom.epsilon)
            return false;

        final Point2D.Double origStartPt = startConnector.getConnectorPoint();
        final Point2D.Double origEndPt = endConnector.getConnectorPoint();
        final Point2D.Double startPt = new Point2D.Double(origStartPt.x, origStartPt.y);
        final Point2D.Double endPt = new Point2D.Double(origEndPt.x, origEndPt.y);

        // determine suitable deltaX, deltaY for both ends
        final Point2D.Double p = new Point2D.Double();
        p.x = Geom.range(rStart.x, rStart.x + rStart.width, startPt.x + deltaX);
        p.y = Geom.range(rStart.y, rStart.y + rStart.height, startPt.y + deltaY);
        deltaX = p.x - startPt.x;
        deltaY = p.y - startPt.y;

        p.x = Geom.range(rEnd.x, rEnd.x + rEnd.width, endPt.x + deltaX);
        p.y = Geom.range(rEnd.y, rEnd.y + rEnd.height, endPt.y + deltaY);
        deltaX = p.x - endPt.x;
        deltaY = p.y - endPt.y;
        // deltaX, deltaY suitable for both ends

        startPt.x = startPt.x + deltaX;
        startPt.y = startPt.y + deltaY;
        endPt.x = endPt.x + deltaX;
        endPt.y = endPt.y + deltaY;

        conn.willChange();
        startConnectorStrategy.dragConnector(this, startConnector, origStartPt, startPt, modifiersEx);
        endConnectorStrategy.dragConnector(this, endConnector, origEndPt, endPt, modifiersEx);
        conn.changed();

        return true;
    }

    /**
     * @param connector
     * @param tracking
     *            value in {@link ConnectorSubTracker#trackStart},
     *            {@link ConnectorSubTracker#trackStep} and
     *            {@link ConnectorSubTracker#trackEnd}
     * @param isStartConnector
     * @param toPoint
     * @param modifiersEx
     * @return the dragged connector
     */
    public Connector dragConnector(Connector connector, int tracking, boolean isStartConnector,
            Point2D.Double toPoint, int modifiersEx) {
        if (!isUsingRelativeConnectors())
            return connector;

        final RelativeConnector relativeConnector = (RelativeConnector) connector;
        RelativeConnector result = relativeConnector;
        final ConnectorStrategy connectorStrategy = findConnectorStrategy(relativeConnector);
        final LineConnectionFigure connection = relativeConnector.getLineConnection();

        switch (tracking) {
            case ConnectorSubTracker.trackStart: {
                start(view, true);
                break;
            }
            case ConnectorSubTracker.trackStep: {
                if (!isTrackingConnector(relativeConnector)) {
                    final Point2D.Double fromPoint = relativeConnector.getConnectorPoint();
                    connection.willChange();
                    connectorStrategy.dragConnector(this, relativeConnector, fromPoint, toPoint, modifiersEx);
                    connection.changed();
                    fireUndoableConnectionEdit(relativeConnector.getLineConnection(), cachedConnection);
                }
                break;
            }
            case ConnectorSubTracker.trackEnd: {
                // change connection to another figure
                if (isTrackingConnector(relativeConnector)) {
                    result = (RelativeConnector) createNewConnector(relativeConnector, connection, isStartConnector,
                            false);
                    if (result != null)
                        fireUndoableConnectionEdit(connection, cachedConnection);
                }
                break;
            }
        } // end switch

        return result;
    }

    /**
     * @param conn
     * @param tracking
     * @param fromPoint
     * @param toPoint
     * @param modifiersEx
     * @return boolean
     */
    protected boolean dragLineSegment(LineConnectionFigure conn, int tracking, Point2D.Double fromPoint,
            Point2D.Double toPoint, int modifiersEx) {
        boolean result = false;

        RelativeConnector relativeConnector = (RelativeConnector) conn.getStartConnector();
        boolean isStartConnector = true;
        final int segmentCount = conn.getNodeCount() / 2 + 1;
        if (draggedLineSegment == segmentCount - 1) {
            relativeConnector = (RelativeConnector) conn.getEndConnector();
            isStartConnector = false;
        }
        final Figure owner = relativeConnector.getOwner();
        final ConnectorStrategy strategy = findConnectorStrategy(relativeConnector);
        final Rectangle2D.Double r1 = strategy.getEffectiveBounds(relativeConnector);
        final Point2D.Double connPt = relativeConnector.getConnectorPoint();

        final Point2D.Double oppositePoint = ConnectorSubTracker.findOppositeConnectionPoint(relativeConnector);
        final int connectorSide = ConnectorGeom.findSide(connPt, r1);
        if (r1.outcode(oppositePoint) == connectorSide) {
            Point2D.Double newPt = new Point2D.Double(connPt.x, connPt.y);
            if (ConnectorGeom.onLeftRightSide(connectorSide))
                newPt.y = oppositePoint.y;
            if (ConnectorGeom.onTopBottomSide(connectorSide))
                newPt.x = oppositePoint.x;

            conn.willChange();
            newPt = strategy.findConnectorPoint(relativeConnector, newPt, owner, conn, isStartConnector);
            strategy.updateConnectorPoint(newPt, relativeConnector);
            final RelativeConnector oppositeConnector = findOppositeConnector(relativeConnector);
            touchConnector(oppositeConnector);
            conn.updateConnection();
            conn.changed();

            result = true;
        }

        return result;
    }

    /**
     * @param owner
     * @param connection
     * @return connector with owner figure = {@code owner} and attached to
     *         connection
     */
    private Connector findConnector(Figure owner, ConnectionFigure connection) {
        Connector connector = null;
        if (connection.getStartConnector() != null) {
            if (connection.getStartConnector().getOwner() == owner)
                connector = connection.getStartConnector();
        }

        if (connector == null && connection.getEndConnector() != null) {
            if (connection.getEndConnector().getOwner() == owner)
                connector = connection.getEndConnector();
        }
        return connector;
    }

    /**
     * This method returns an existing connector or a connector determined by
     * the owner(<i> if not using {@code RelativeConnector} objects</i>) or one
     * of the two tracking connectors in this class.
     * <p>
     * For applications that do not use {@code RelativeConnector} objects, this
     * method delegates to
     * {@link Figure#findConnector(java.awt.geom.Point2D.Double, ConnectionFigure)}
     * <p>
     * The boolean attribute {@code usingRelativeConnectors} must be set
     * according to the connector types an application uses.
     * <p>
     * <b> {@code RelativeConnector} objects and other connector type objects
     * can NOT be mixed in an application.</b>
     * <p>
     * If the application is <b>not</b> using {@code RelativeConnector} objects
     * the {@code ConnectorSubTracker} will <b>not</b> use
     * {@link ConnectorStrategy}.
     *
     *
     *
     * @param p
     * @param owner
     * @param connection
     * @return a connector at point p for {@code owner} and {@code connection}
     */
    public Connector findConnector(Point2D.Double p, Figure owner, ConnectionFigure connection) {
        if (owner == null || !owner.contains(p))
            return null;

        Connector connector = findConnector(owner, connection);
        // existing connections/connectors .... when dragging connection handles
        // and NOT onto a different figure
        if (connector != null)
            return connector;

        // if not using RealtiveConnectors/ConnectorStrategy use 'old' calls
        if (!isUsingRelativeConnectors()) {
            connector = owner.findConnector(p, connection);
            return connector;
        }

        // NO CONNECTOR for owner figure .... use a Tracking Connector
        // ... either new connection or changing connector to a DIFFERENT figure
        final RelativeConnector trackingConnector = findTrackingConnector(p, owner, connection);

        boolean newConnection = false;
        if (connection.getStartConnector() == null && connection.getEndConnector() == null)
            newConnection = true;
        else
            if (isTrackingConnector(connection.getStartConnector())
                    && isTrackingConnector(connection.getEndConnector()))
                newConnection = true;
            else
                if (isTrackingConnector(connection.getStartConnector()) && connection.getEndConnector() == null)
                    newConnection = true;

        // if it is a new InteriorConnector on the source figure, use the
        // initial pressed point
        final String startConnectorStrategyName = connection.findStartConnectorStrategyName();
        final ConnectorStrategy startConnectorStrategy = findConnectorStrategy(startConnectorStrategyName);
        if (trackingConnector == trackingConnector1 && newConnection
                && !(startConnectorStrategy instanceof BoundaryConnectorStrategy))
            return trackingConnector;

        // trackingConnector1 or trackingConnector2 .... constrain last dragged
        // point to figure
        final ConnectorStrategy strategy = findConnectorStrategy(trackingConnector);
        final Rectangle2D.Double r1 = strategy.getEffectiveBounds(owner);
        final Point2D.Double pt = new Point2D.Double();
        pt.x = Geom.range(r1.x, r1.x + r1.width, p.x);
        pt.y = Geom.range(r1.y, r1.y + r1.height, p.y);
        strategy.updateConnectorPoint(pt, trackingConnector);

        return trackingConnector;
    }

    /**
     * @param p
     * @param owner
     * @param connection
     * @return a tracking connector
     */
    private RelativeConnector findTrackingConnector(Point2D.Double p, Figure owner, ConnectionFigure connection) {
        RelativeConnector trackingConnector = trackingConnector1;
        if (trackingConnector1.getOwner() != null && trackingConnector1.getOwner() != owner)
            trackingConnector = trackingConnector2;

        trackingConnector.setLineConnection(connection);
        if (trackingConnector.getOwner() == null || trackingConnector.getOwner() != owner) {
            trackingConnector.setOwner(owner);
            final ConnectorStrategy strategy = findConnectorStrategy(trackingConnector);
            strategy.updateConnectorPoint(p, trackingConnector);
        }
        return trackingConnector;
    }

    /**
     * @param connection
     * @param beforeClone
     */
    protected void fireUndoableConnectionEdit(LineConnectionFigure connection, LineConnectionFigure beforeClone) {
        final LineConnectionFigure afterClone = connection.clone();
        afterClone.unRegisterConnectionHandler(connection.getStartFigure());
        afterClone.unRegisterConnectionHandler(connection.getEndFigure());
        final Drawing drawing = view.getDrawing();
        drawing.fireUndoableEditHappened(new DragConnectionEdit(this, connection, afterClone, beforeClone));
    }

    /**
     * @param owner1
     * @param owner2
     * @return a key for mapping(<i> "hashCode1-hashCode2" </i>)
     */
    private String getOwnerPairKey(Figure owner1, Figure owner2) {
        return owner1.hashCode() + "-" + owner2.hashCode();
    }

    /**
     * Returns the previous bounds of {@code figure}
     *
     * @param figure
     * @return previous bounds rectangle
     */
    protected Rectangle2D.Double getPrevBounds(Figure figure) {
        final Rectangle2D.Double r = prevBoundsMap.get(figure);
        if (r != null)
            return new Rectangle2D.Double(r.x, r.y, r.width, r.height);
        return figure.getBounds();
    }

    /**
     * @param relativeConnector
     * @return returns the connector point prior to this (tracking) session
     */
    protected Point2D.Double getPrevPoint(RelativeConnector relativeConnector) {
        final Rectangle2D.Double pr1 = getPrevBounds(relativeConnector.getOwner());
        return new Point2D.Double(pr1.x + relativeConnector.getRelativeX(), pr1.y + relativeConnector.getRelativeY());
    }

    /**
     * Returns clones of the connectors prior to resizing a figure or dragging a
     * connection line.
     *
     * @return cached connectors
     */
    public Collection<Connector> getPriorConnectors() {
        return cachedConnectors;
    }

    /**
     * @return view
     */
    public final DrawingView getView() {
        return view;
    }

    /**
     * returns true if the connector is one of the two tracking connectors held
     * by this object.
     * <p>
     * This occurs during the creation of a new line connection.
     *
     * @return a boolean
     */
    protected boolean isTrackingConnector(Connector connector) {
        if (connector == trackingConnector1 || connector == trackingConnector2)
            return true;
        return false;
    }

    /**
     * @param oldConnector
     * @param currentConnector
     * @return connector point of restored connector
     */
    private Point2D.Double restore(RelativeConnector oldConnector, RelativeConnector currentConnector) {
        final LineConnectionFigure connection = currentConnector.getLineConnection();
        connection.willChange();
        currentConnector.relativeX = oldConnector.relativeX;
        currentConnector.relativeY = oldConnector.relativeY;
        connection.updateConnection();
        connection.changed();
        return currentConnector.getConnectorPoint();
    }

    /**
     * @param currentConnections
     * @param oldConnectors
     */
    private void restoreConnectorPoints(Collection<LineConnectionFigure> currentConnections,
            Collection<Connector> oldConnectors) {
        for (final Connector connector : oldConnectors) {
            final RelativeConnector oldConnector = (RelativeConnector) connector;
            LineConnectionFigure matchingNewConnection = null;
            for (final LineConnectionFigure newConnection : currentConnections) {
                if (oldConnector.getLineConnection() == newConnection) {
                    matchingNewConnection = newConnection;
                    break;
                }
            }
            if (matchingNewConnection == null) {
                System.out.println("No matching current Connection");
                continue;
            }
            RelativeConnector matchingConnector = (RelativeConnector) matchingNewConnection.getStartConnector();
            if (matchingConnector.getOwner() != oldConnector.getOwner())
                matchingConnector = (RelativeConnector) matchingNewConnection.getEndConnector();
            restore(oldConnector, matchingConnector);
        }
    }

    /**
     * @param figures
     * @param oldConnectors
     */
    public  void restoreConnectors(Collection<Figure> figures, Collection<Connector> oldConnectors) {
        if (!isUsingRelativeConnectors())
            return;

        final Collection<LineConnectionFigure> currentConnections = new ArrayList<LineConnectionFigure>();
        for (final Figure f : figures) {
            currentConnections.addAll(f.getConnections());
        }
        restoreConnectorPoints(currentConnections, oldConnectors);
    }

    /**
     * @param f
     * @param oldConnectors
     */
    public void restoreConnectors(Figure f, Collection<Connector> oldConnectors) {
        final Collection<LineConnectionFigure> currentConnections = f.getConnections();
        restoreConnectorPoints(currentConnections, oldConnectors);
    }

    /**
     * CAUTION: This method should only be called by the editor
     *
     * @param view
     */
    public final void setView(DrawingView view) {
        this.view = view;
    }

    /**
     * This method is called when a 'client' begins adjustment tracking.
     * <p>
     * It populates <code>prevBoundsMap</code> with the bounds of all selected
     * figures.
     * <p>
     * It initializes the tracking connectors used to create new connectors and
     * sets the cached ConnectionFigures;
     *
     * @param selectedFigures
     * @param dragging
     *            true if dragging a connector or a figure
     * @return selectedFigures AND children (if any selected figure is a
     *         composite)
     */
    private Collection<Figure> start(Collection<Figure> selectedFigures, boolean dragging) {
        trackingConnector1.connection = null;
        trackingConnector2.connection = null;
        trackingConnector1.setOwner(null);
        trackingConnector2.setOwner(null);
        cachedConnectors = new ArrayList<Connector>();
        cachedConnection = null;
        draggedLineSegment = -1;

        final LinkedHashSet<Figure> selectedFiguresAndChildren = new LinkedHashSet<Figure>();
        // children of selected figures are considered selected
        for (final Figure f : selectedFigures) {
            selectedFiguresAndChildren.addAll(f.getDecomposition());
        }

        // check if resizing and check if dragging a connection line or
        // connector
        // each situation will have only 1 figure selected
        LineConnectionFigure singleSelectedLine = null;
        Figure singleSelectedFigure = null;
        if (selectedFigures.size() == 1) {
            final Figure f = selectedFigures.iterator().next();
            if (f instanceof ConnectionFigure)
                singleSelectedLine = (LineConnectionFigure) f;
            else
                singleSelectedFigure = f;
        }

        // dragging a connector handle or a connection
        if (singleSelectedLine != null && dragging) {
            final RelativeConnector startConnector = (RelativeConnector) singleSelectedLine.getStartConnector();
            final RelativeConnector endConnector = (RelativeConnector) singleSelectedLine.getEndConnector();
            cachedConnectors = new ArrayList<Connector>();
            if (!isTrackingConnector(endConnector)) {
                cachedConnection = singleSelectedLine.clone();
                cachedConnection.unRegisterConnectionHandler(startConnector.getOwner());
                cachedConnection.unRegisterConnectionHandler(endConnector.getOwner());
            }
        }

        // resizing a figure or sliding a figure over it's connections
        if (singleSelectedFigure != null && !dragging) {
            final Collection<LineConnectionFigure> connections = singleSelectedFigure.getConnections();
            cachedConnectors = new ArrayList<Connector>(connections.size());
            for (final LineConnectionFigure connection : connections) {
                RelativeConnector relativeConnector = (RelativeConnector) connection.getStartConnector();
                if (relativeConnector.getOwner() != singleSelectedFigure)
                    relativeConnector = (RelativeConnector) connection.getEndConnector();
                final RelativeConnector clonedConnector = relativeConnector.clone();
                cachedConnectors.add(clonedConnector);
            }
        }

        return selectedFiguresAndChildren;
    }

    /**
     * This method is called when a 'client' begins adjustment tracking.
     * <p>
     * It is also called by {@link org.jhotdraw.draw.tool.ConnectionTool} prior
     * to creating connections.
     * <p>
     * It populates <code>prevBoundsMap</code> with the bounds of all selected
     * figures.
     * <p>
     * It initializes the tracking connectors used to create new connectors and
     * sets the cached ConnectionFigure to null;
     *
     * @param view
     * @param dragging
     *            true if dragging a connector or a figure
     */
    private void start(DrawingView view, boolean dragging) {
        final Collection<Figure> selectedFiguresAndChildren = start(view.getSelectedFigures(), dragging);
        createSelectedPrevBoundsMap(selectedFiguresAndChildren);
    }

    /**
     * @param connector
     * @return new connector point
     */
    public Point2D.Double touchConnector(Connector connector) {
        if (!isUsingRelativeConnectors())
            return connector.getAnchor();
        final RelativeConnector relativeConnector = (RelativeConnector) connector;
        final ConnectorStrategy connectorStrategy = findConnectorStrategy(relativeConnector);
        final Point2D.Double toPoint = connectorStrategy.touchConnector(this, relativeConnector);
        return toPoint;
    }

    /**
     * @param owner
     * @param start
     *            true for StartConnector
     * @param connectorStrategy
     * @return the connector point of the cached connection
     */
    // private Point2D.Double findConnectorPoint(Figure owner,
    // LineConnectionFigure connection,
    // boolean start, ConnectorStrategy connectorStrategy) {
    // Point2D.Double result = null;
    // Point2D.Double connPt1 = connection.getPoint(0);
    // Point2D.Double connPt2 = connection.getPoint(1);
    // if (!start) {
    // connPt1 = connection.getPoint(connection.getNodeCount()-1);
    // connPt2 = connection.getPoint(connection.getNodeCount()-2);
    // }
    //
    // if (!(connectorStrategy.isBoundsMode()))
    // result = connPt1;
    // else {
    // if (owner.getRestrictedShapeType() ==
    // ConnectorStrategy.RECTANGULAR_SHAPE)
    // result = connPt1;
    // else {
    // // the connector point is the projection of the bounds point
    // // the bounds point is the intersection of the connectorPoint
    // // & it's opposite point with the bounds
    // result =
    // ConnectorGeom.calculateBoundsPoint(owner, connPt1, connPt2, connPt1);
    // }
    // }
    // return result;
    // }
}
