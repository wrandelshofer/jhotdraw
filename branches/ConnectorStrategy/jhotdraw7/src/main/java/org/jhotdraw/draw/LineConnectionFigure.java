/*
 * @(#)BezierBezierLineConnection.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and
 * contributors of the JHotDraw project ("the copyright holders").
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * the copyright holders. For details see accompanying license terms.
 */
package org.jhotdraw.draw;

import static org.jhotdraw.draw.AttributeKeys.END_CONNECTOR_STRATEGY;
import static org.jhotdraw.draw.AttributeKeys.END_DECORATION;
import static org.jhotdraw.draw.AttributeKeys.START_CONNECTOR_STRATEGY;

import org.jhotdraw.draw.liner.Liner;
import org.jhotdraw.draw.liner.SelfConnectionLiner;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.handle.BezierOutlineHandle;
import org.jhotdraw.draw.handle.BezierNodeHandle;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.connector.ConnectorSubTracker;
import org.jhotdraw.draw.connector.RelativeConnector;

import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import java.io.*;
import org.jhotdraw.draw.handle.ConnectionEndHandle;
import org.jhotdraw.draw.handle.ConnectionStartHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

/**
 * A {@link ConnectionFigure} which connects two figures using a bezier path.
 * <p>
 * The bezier path can be laid out manually using bezier handles provided
 * by this figure, or automatically using a {@link Liner} which can be
 * set using the JavaBeans property {@code liner}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineConnectionFigure extends LineFigure
        implements ConnectionFigure {

    /** The name of the JaveBeans property {@code liner}. */
    public final static String LINER_PROPERTY = "liner";
    private Connector startConnector;
    private Connector endConnector;
    private Liner liner;
    /**
     * Handles figure changes in the start and the
     * end figure.
     */
    private ConnectionHandler connectionHandler = new ConnectionHandler(this);

    protected static class ConnectionHandler extends FigureAdapter implements Serializable {
        private static final long serialVersionUID = 1L;
        protected LineConnectionFigure connection;
        private Drawing drawing;

        private ConnectionHandler(LineConnectionFigure connection) {
            this.connection = connection;
        }

        @Override
        public void figureRemoved(FigureEvent evt) {
            // We need the drawing in figureAdded below.
            if (connection.getDrawing() != null)
                drawing = connection.getDrawing();

            connection.fireFigureRequestRemove();
            // ConnectionHandler is removed as a listener on start and end figures
            // by fireFigureRequestRemove calling removeNotify on connection.
            // If the start or end figure is reinstated (and both figures exist)
            // then the connection should be reinstated simultaneously.
            // Without the handler nothing is going to inform the connection to re-connect.
            // So we add ConnectionHandler back as a listener on the REMOVED figure
            // and the 'other' figure.
            connection.registerConnectionHandler(connection.getStartFigure());
            connection.registerConnectionHandler(connection.getEndFigure());
        }


        // this will only be called when the start or end figure is re-instated
        // after a delete
        @Override
        public void figureAdded(FigureEvent e) {
            if (drawing != null)
                drawing.add(connection);
        }


        @Override
        public void figureChanged(FigureEvent e) {
            if (e.getSource() == connection.getStartFigure() ||
                    e.getSource() == connection.getEndFigure()) {
                connection.willChange();
                connection.updateConnection();
                connection.changed();
            }
        }
    };

    /** Creates a new instance. */
    public LineConnectionFigure() {
    }
    // DRAWING
    // SHAPE AND BOUNDS

    /**
     * Add the embedded {@link #connectionHandler} as a listener to {@code
     * figure} if it is not already a listener.
     *
     * @param figure
     */
    public void registerConnectionHandler(Figure figure) {
        Collection<LineConnectionFigure> connections = figure.getConnections();
        if (!(connections.contains(this)))
            figure.addFigureListener(connectionHandler);
    }


    /**
     * Removes the embedded {@link #connectionHandler} as a listener to {@code
     * figure} if it is already a listener.
     * <p>
     * This permits fully cloned connection figures (and their cloned
     * connectors) to exist independently of the drawing.
     *
     * @param figure
     */
    public void unRegisterConnectionHandler(Figure figure) {
        Collection<LineConnectionFigure> connections = figure.getConnections();
        if (connections.contains(this))
            figure.removeFigureListener(connectionHandler);
    }


    /**
     * Ensures that a connection is updated if the connection
     * was moved.
     */
    @Override
    public void transform(AffineTransform tx) {
        super.transform(tx);
        updateConnection(); // make sure that we are still connected
    }
    // ATTRIBUTES
    // EDITING

    /**
     * Gets the handles of the figure. It returns the normal
     * PolylineHandles but adds ChangeConnectionHandles at the
     * start and end.
     */
    @Override
    public Collection<Handle> createHandles(int detailLevel) {
        ArrayList<Handle> handles = new ArrayList<Handle>(getNodeCount());
        switch (detailLevel) {
            case -1: // Mouse hover handles
                handles.add(new BezierOutlineHandle(this, true));
                break;
            case 0:
                handles.add(new BezierOutlineHandle(this));
                if (getLiner() == null) {
                    for (int i = 1, n = getNodeCount() - 1; i < n; i++) {
                        handles.add(new BezierNodeHandle(this, i));
                    }
                }
                handles.add(new ConnectionStartHandle(this));
                handles.add(new ConnectionEndHandle(this));
                break;
        }
        return handles;
    }

// CONNECTING
    /**
     *
     * ConnectionFigures cannot be connected and always sets connectable to false.
     */
    @Override
    public void setConnectable(boolean newValue) {
        super.setConnectable(false);
    }

    public void updateConnection() {
        willChange();
        if (getStartConnector() != null) {
            Point2D.Double start = getStartConnector().findStart(this);
            if (start != null) {
                setStartPoint(start);
            }
        }
        if (getEndConnector() != null) {
            Point2D.Double end = getEndConnector().findEnd(this);

            if (end != null) {
                setEndPoint(end);
            }
        }
        changed();
    }

    @Override
    public void validate() {
        super.validate();
        lineout();
    }

    public boolean canConnect(Connector start, Connector end) {
        return start.getOwner().isConnectable() && end.getOwner().isConnectable();
    }

    public Connector getEndConnector() {
        return endConnector;
    }

    public Figure getEndFigure() {
        return (endConnector == null) ? null : endConnector.getOwner();
    }

    public Connector getStartConnector() {
        return startConnector;
    }

    public Figure getStartFigure() {
        return (startConnector == null) ? null : startConnector.getOwner();
    }

    public void setEndConnector(Connector newEnd) {
        if (newEnd != endConnector) {
            if (endConnector != null) {
                getEndFigure().removeFigureListener(connectionHandler);
                if (getStartFigure() != null) {
                    if (getDrawing() != null) {
                        handleDisconnect(getStartConnector(), getEndConnector());
                    }
                }
            }
            endConnector = newEnd;
            if (endConnector != null) {
                if (endConnector instanceof RelativeConnector)
                    ((RelativeConnector)endConnector).setLineConnection(this);
                getEndFigure().addFigureListener(connectionHandler);
                if (getStartFigure() != null && getEndFigure() != null) {
                    if (getDrawing() != null) {
                        handleConnect(getStartConnector(), getEndConnector());
                        updateConnection();
                    }
                }
            }
        }
    }

    public void setStartConnector(Connector newStart) {
        if (newStart != startConnector) {
            if (startConnector != null) {
                getStartFigure().removeFigureListener(connectionHandler);
                if (getEndFigure() != null) {
                    handleDisconnect(getStartConnector(), getEndConnector());
                }
            }
            startConnector = newStart;
            if (startConnector != null) {
                if (startConnector instanceof RelativeConnector)
                    ((RelativeConnector)startConnector).setLineConnection(this);
                getStartFigure().addFigureListener(connectionHandler);
                if (getStartFigure() != null && getEndFigure() != null) {
                    handleConnect(getStartConnector(), getEndConnector());
                    updateConnection();
                }
            }
        }
    }

    // COMPOSITE FIGURES
    // LAYOUT
    /*
    public Liner getBezierPathLayouter() {
    return (Liner) get(BEZIER_PATH_LAYOUTER);
    }
    public void setBezierPathLayouter(Liner newValue) {
    set(BEZIER_PATH_LAYOUTER, newValue);
    }
    /**
     * Lays out the connection. This is called when the connection
     * itself changes. By default the connection is recalculated
     * /
    public void layoutConnection() {
    if (getStartConnector() != null && getEndConnector() != null) {
    willChange();
    Liner bpl = getBezierPathLayouter();
    if (bpl != null) {
    bpl.lineout(this);
    } else {
    if (getStartConnector() != null) {
    Point2D.Double start = getStartConnector().findStart(this);
    if(start != null) {
    basicSetStartPoint(start);
    }
    }
    if (getEndConnector() != null) {
    Point2D.Double end = getEndConnector().findEnd(this);

    if(end != null) {
    basicSetEndPoint(end);
    }
    }
    }
    changed();
    }
    }
     */
    // CLONING
    // EVENT HANDLING
    /**
     * This method is invoked, when the Figure is being added to a Drawing.
     * This method invokes handleConnect, if the Figure is connected.
     *
     * @see #handleConnect
     */
    @Override
    public void addNotify(Drawing drawing) {
        super.addNotify(drawing);

        if (getStartConnector() != null && getEndConnector() != null) {
            handleConnect(getStartConnector(), getEndConnector());
            updateConnection();
        }
        // When a connection is removed ConnectionHandler is removed as a
        // listener on both the start and end figures. ConnectionHandler
        // gets added back as a listener to both figures here.
        // This method is also called when loading diagrams and
        // reinstating the start or end figures. In those cases the
        // ConnectionHandler has not been removed. The register
        // method will not add ConnectionHandler as a listener if
        // it is already present.

        if (getStartConnector() != null)
            registerConnectionHandler(getStartFigure());
        if (getEndConnector() != null)
            registerConnectionHandler(getEndFigure());
    }

    /**
     * This method is invoked, when the Figure is being removed from a Drawing.
     * This method invokes handleDisconnect, if the Figure is connected.
     *
     * @see #handleDisconnect
     */
    @Override
    public void removeNotify(Drawing drawing) {
        if (getStartConnector() != null && getEndConnector() != null) {
            handleDisconnect(getStartConnector(), getEndConnector());
        }
        // Note: we do not set the connectors to null here, because we
        // need them when adding the connection back to a drawing. For example,
        // when undoing a LineConnectiondeletion.

        if (getStartConnector() != null)
            getStartFigure().removeFigureListener(this.connectionHandler);
        if (getEndConnector() != null)
            getEndFigure().removeFigureListener(this.connectionHandler);

        super.removeNotify(drawing);
    }

    /**
     * Handles the disconnection of a connection.
     * Override this method to handle this event.
     * <p>
     * Note: This method is only invoked, when the Figure is part of a
     * Drawing. If the Figure is removed from a Drawing, this method is
     * invoked on behalf of the removeNotify call to the Figure.
     *
     * @see #removeNotify
     */
    protected void handleDisconnect(Connector start, Connector end) {
    }

    /**
     * Handles the connection of a connection.
     * Override this method to handle this event.
     * <p>
     * Note: This method is only invoked, when the Figure is part of a
     * Drawing. If the Figure is added to a Drawing this method is invoked
     * on behalf of the addNotify call to the Figure.
     */
    protected void handleConnect(Connector start, Connector end) {
    }

    @Override
    public LineConnectionFigure clone() {
        LineConnectionFigure that = (LineConnectionFigure) super.clone();
        that.connectionHandler = new ConnectionHandler(that);
        if (this.liner != null) {
            that.liner = (Liner) this.liner.clone();
        }
        // FIXME - For safety reasons, we clone the connectors, but they would
        // work, if we continued to use them. Maybe we should state somewhere
        // whether connectors should be reusable, or not.
        // To work properly, that must be registered as a figure listener
        // to the connected figures.
        if (this.startConnector != null) {
            that.startConnector = (Connector) this.startConnector.clone();
            that.getStartFigure().addFigureListener(that.connectionHandler);
        }
        if (this.endConnector != null) {
            that.endConnector = (Connector) this.endConnector.clone();
            that.getEndFigure().addFigureListener(that.connectionHandler);
        }
        if (that.startConnector != null && that.endConnector != null) {
            //that.handleConnect(that.getStartConnector(), that.getEndConnector());
            that.updateConnection();
        }
        return that;
    }

    @Override
    public void remap(Map<Figure, Figure> oldToNew, boolean disconnectIfNotInMap) {
        willChange();
        super.remap(oldToNew, disconnectIfNotInMap);
        Figure newStartFigure = null;
        Figure newEndFigure = null;
        if (getStartFigure() != null) {
            newStartFigure = (Figure) oldToNew.get(getStartFigure());
            if (newStartFigure == null && !disconnectIfNotInMap) {
                newStartFigure = getStartFigure();
            }
        }
        if (getEndFigure() != null) {
            newEndFigure = (Figure) oldToNew.get(getEndFigure());
            if (newEndFigure == null && !disconnectIfNotInMap) {
                newEndFigure = getEndFigure();
            }
        }

        if (newStartFigure != null) {
            setStartConnector(newStartFigure.findCompatibleConnector(getStartConnector(), true));
        } else {
            if (disconnectIfNotInMap) {
                setStartConnector(null);
            }
        }
        if (newEndFigure != null) {
            setEndConnector(newEndFigure.findCompatibleConnector(getEndConnector(), false));
        } else {
            if (disconnectIfNotInMap) {
                setEndConnector(null);
            }
        }

        updateConnection();
        changed();

        // a quick hack... must be changed
        if (ConnectorSubTracker.isUsingRelativeConnectors()) {
            String startStrategyName = get(START_CONNECTOR_STRATEGY);
            String endStrategyName = get(END_CONNECTOR_STRATEGY);
            set(START_CONNECTOR_STRATEGY, null);
            set(END_CONNECTOR_STRATEGY, null);
            ConnectorSubTracker.migrateToRelativeConnectors(this);
            set(START_CONNECTOR_STRATEGY, startStrategyName);
            set(END_CONNECTOR_STRATEGY, endStrategyName);
        }
    }

    public boolean canConnect(Connector start) {
        return start.getOwner().isConnectable();
    }

    /**
     * Handles a mouse click.
     */
    @Override
    public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
        if (getLiner() == null &&
                evt.getClickCount() == 2) {
            willChange();
            final int index = splitSegment(p, (float) (5f / view.getScaleFactor()));
            if (index != -1) {
                final BezierPath.Node newNode = getNode(index);
                fireUndoableEditHappened(new AbstractUndoableEdit() {

                    @Override
                    public void redo() throws CannotRedoException {
                        super.redo();
                        willChange();
                        addNode(index, newNode);
                        changed();
                    }

                    @Override
                    public void undo() throws CannotUndoException {
                        super.undo();
                        willChange();
                        removeNode(index);
                        changed();
                    }
                });
                changed();
                return true;
            }
        }
        return false;
    }
    // PERSISTENCE

    @Override
    protected void readPoints(DOMInput in) throws IOException {
        super.readPoints(in);
        in.openElement("startConnector");
        Connector startConnector = (Connector) in.readObject();
        setStartConnector(startConnector);
        in.closeElement();
        in.openElement("endConnector");
        Connector endConnector = (Connector) in.readObject();
        setEndConnector(endConnector);
        in.closeElement();
        if (findStartConnectorStrategyName() != null)
            ((RelativeConnector)startConnector).setLineConnection(this);
        if (findEndConnectorStrategyName() != null)
            ((RelativeConnector)endConnector).setLineConnection(this);

    }

    @Override
    public void read(DOMInput in) throws IOException {
        readAttributes(in);
        readLiner(in);

        // Note: Points must be read after Liner, because Liner influences
        // the location of the points.
        readPoints(in);

        if (ConnectorSubTracker.isUsingRelativeConnectors()) {
            if (findStartConnectorStrategyName() == null ||
                    findEndConnectorStrategyName() == null )
                ConnectorSubTracker.migrateToRelativeConnectors(this);
        }
    }

    protected void readLiner(DOMInput in) throws IOException {
        if (in.getElementCount("liner") > 0) {
            in.openElement("liner");
            liner = (Liner) in.readObject();
            in.closeElement();
        } else {
            liner = null;
        }

    }

    @Override
    public void write(DOMOutput out) throws IOException {
        writePoints(out);
        writeAttributes(out);
        writeLiner(out);
    }

    protected void writeLiner(DOMOutput out) throws IOException {
        if (liner != null) {
            out.openElement("liner");
            out.writeObject(liner);
            out.closeElement();
        }
    }

    @Override
    protected void writePoints(DOMOutput out) throws IOException {
        super.writePoints(out);
        out.openElement("startConnector");
        out.writeObject(getStartConnector());
        out.closeElement();
        out.openElement("endConnector");
        out.writeObject(getEndConnector());
        out.closeElement();
    }

    public void setLiner(Liner newValue) {
        Liner oldValue = liner;
        this.liner = newValue;
        firePropertyChange(LINER_PROPERTY, oldValue, newValue);
    }

    @Override
    public void setNode(int index, BezierPath.Node p) {
        if (index != 0 && index != getNodeCount() - 1) {
            if (getStartConnector() != null) {
                Point2D.Double start = getStartConnector().findStart(this);
                if (start != null) {
                    setStartPoint(start);
                }
            }
            if (getEndConnector() != null) {
                Point2D.Double end = getEndConnector().findEnd(this);

                if (end != null) {
                    setEndPoint(end);
                }
            }
        }
        super.setNode(index, p);
    }
    /*
    public void basicSetPoint(int index, Point2D.Double p) {
    if (index != 0 && index != getNodeCount() - 1) {
    if (getStartConnector() != null) {
    Point2D.Double start = getStartConnector().findStart(this);
    if(start != null) {
    basicSetStartPoint(start);
    }
    }
    if (getEndConnector() != null) {
    Point2D.Double end = getEndConnector().findEnd(this);

    if(end != null) {
    basicSetEndPoint(end);
    }
    }
    }
    super.basicSetPoint(index, p);
    }
     */

    public void lineout() {
        if (liner != null) {
            liner.lineout(this);
        }
    }

    /**
     * FIXME - Liner must work with API of LineConnection!
     */
    @Override
    public BezierPath getBezierPath() {
        return path;
    }

    public Liner getLiner() {
        return liner;
    }

    @Override
    public void setStartPoint(Point2D.Double p) {
        setPoint(0, p);
    }

    @Override
    public void setPoint(int index, Point2D.Double p) {
        setPoint(index, 0, p);
    }

    @Override
    public void setEndPoint(Point2D.Double p) {
        setPoint(getNodeCount() - 1, p);
    }

    public void reverseConnection() {
        if (startConnector != null && endConnector != null) {
            handleDisconnect(startConnector, endConnector);
            Connector tmpC = startConnector;
            startConnector = endConnector;
            endConnector = tmpC;
            Point2D.Double tmpP = getStartPoint();
            setStartPoint(getEndPoint());
            setEndPoint(tmpP);
            handleConnect(startConnector, endConnector);
            updateConnection();
        }
    }
    /* (non-Javadoc)
     * @see org.jhotdraw.draw.ConnectionFigure#findStartConnectorStrategyName()
     */
    public String findStartConnectorStrategyName() {
        String strategyName = get(AttributeKeys.START_CONNECTOR_STRATEGY);
        if (strategyName == null || strategyName.length() == 0)
            strategyName = get(AttributeKeys.END_CONNECTOR_STRATEGY);
        return strategyName;
    }

    /* (non-Javadoc)
     * @see org.jhotdraw.draw.ConnectionFigure#findEndConnectorStrategyName()
     */
    public String findEndConnectorStrategyName() {
        String strategyName = get(AttributeKeys.END_CONNECTOR_STRATEGY);
        if (strategyName == null || strategyName.length() == 0)
            strategyName = get(AttributeKeys.START_CONNECTOR_STRATEGY);
        return strategyName;
    }

    /**
     * set liner and attributes for a self-connection
     */
    public void setSelfConnection() {
         setLiner(new SelfConnectionLiner());
         set(START_CONNECTOR_STRATEGY, "EdgeConnectorStrategy");
         set(END_CONNECTOR_STRATEGY, "EdgeConnectorStrategy");
         setAttributeEnabled(AttributeKeys.START_CONNECTOR_STRATEGY, true);
         setAttributeEnabled(AttributeKeys.END_CONNECTOR_STRATEGY, true);
         set(END_DECORATION, new ArrowTip());
    }


    /**
     * Returns a tooltip for the specified location on the figure.
     *
     * The tooltip identifes the connector strategy for the
     * specified line end.
     *
     */
    @Override
    public String getToolTipText(Point2D.Double p) {
        String answer = "";
        if (p.distance(getPoint(0)) < 12)
            answer = findStartConnectorStrategyName();
        else
            if (p.distance(getPoint(getNodeCount()-1)) < 12)
                answer = findEndConnectorStrategyName();
        return answer;
    }



}