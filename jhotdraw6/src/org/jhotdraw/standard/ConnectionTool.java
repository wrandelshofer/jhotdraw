/*
 * @(#)ConnectionTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.Geom;
import CH.ifa.draw.util.UndoableAdapter;
import CH.ifa.draw.util.Undoable;
import java.awt.*;

import java.util.*;

/**
 * A tool that can be used to connect figures, to split
 * connections, and to join two segments of a connection.
 * ConnectionTools turns the visibility of the Connectors
 * on when it enters a figure.
 * The connection object to be created is specified by a prototype.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld029.htm>Prototype</a></b><br>
 * ConnectionTools creates the connection by cloning a prototype.
 * <hr>
 *
 * @see ConnectionFigure
 * @see Object#clone
 *
 * @version <$CURRENT_VERSION$>
 */
public  class ConnectionTool extends AbstractTool {

	/**
	 * the anchor point of the interaction
	 */
	private Connector   myStartConnector;
	private Connector   myEndConnector;
	private Connector   myTargetConnector;

	private Figure myTarget;

	/**
	 * the currently created figure
	 */
	private ConnectionFigure  myConnection;

	/**
	 * the currently manipulated connection point
	 */
	private int  fSplitPoint;

	/**
	 * the currently edited connection
	 */
	private ConnectionFigure  fEditedConnection;

	/**
	 * the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	private Figure myAddedFigure;

	/**
	 * the prototypical figure that is used to create new
	 * connections.
	 */
	private ConnectionFigure  fPrototype;


	public ConnectionTool(DrawingEditor newDrawingEditor, ConnectionFigure newPrototype) {
		super(newDrawingEditor);
		fPrototype = newPrototype;
	}

	/**
	 * Handles mouse move events in the drawing view.
	 */
	public void mouseMove(DrawingViewMouseEvent dvme) {
		trackConnectors(dvme);
	}

	/**
	 * Manipulates connections in a context dependent way. If the
	 * mouse down hits a figure start a new connection. If the mousedown
	 * hits a connection split a segment or join two segments.
	 */
	public void mouseDown(DrawingViewMouseEvent dvme) {
		super.mouseDown(dvme);
		// use event coordinates to supress any kind of
		// transformations like constraining points to a grid
		setAnchorX( dvme.getMouseEvent().getX() );
		setAnchorY( dvme.getMouseEvent().getY() );
		setTargetFigure(findConnectionStart(getAnchorX(), getAnchorY(), drawing()));
		if (getTargetFigure() != null) {
			setStartConnector(findConnector(getAnchorX(), getAnchorY(), getTargetFigure()));
			if (getStartConnector() != null) {
				setConnection(createConnection());
				getConnection().startPoint(getAnchorX(), getAnchorY());
				getConnection().endPoint(getAnchorX(), getAnchorY());
				view().add(getConnection());
				setAddedFigure(getConnection());
			}
		}
		else {
			// Since we can't connect to the figure, see if its a Connection
			// object we can modify the appearance of.
			ConnectionFigure connection = findConnection(getAnchorX(), getAnchorY(), drawing());
			if (connection != null) {
				if (!connection.joinSegments(getAnchorX(), getAnchorY())) {
					fSplitPoint = connection.splitSegment(getAnchorX(), getAnchorY());
					fEditedConnection = connection;
				}
				else {
					fEditedConnection = null;
				}
			}
		}
	}

	/**
	 * Adjust the created connection or split segment.
	 */
	public void mouseDrag(DrawingViewMouseEvent dvme) {
		Point p = new Point(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY());
		if (getConnection() != null) {
			trackConnectors(dvme);
			if (getTargetConnector() != null) {
				p = Geom.center(getTargetConnector().displayBox());
			}
			getConnection().endPoint(p.x, p.y);
		}
		else if (fEditedConnection != null) {
			Point pp = new Point(dvme.getX(), dvme.getY());
			fEditedConnection.setPointAt(pp, fSplitPoint);
		}
	}

	/**
	 * Connects the figures if the mouse is released over another
	 * figure.
	 */
	public void mouseUp(DrawingViewMouseEvent dvme) {
		Figure c = null;
		if (getStartConnector() != null) {
			c = findTarget(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY(), drawing());
		}

		if (c != null) {
			setEndConnector(findConnector(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY(), c));
			if (getEndConnector() != null) {
				getConnection().connectStart(getStartConnector());
				getConnection().connectEnd(getEndConnector());
				getConnection().updateConnection();

				setUndoActivity(createUndoActivity());
				getUndoActivity().setAffectedFigures(
					new SingleFigureEnumerator(getAddedFigure()));
			}
		}
		else if (getConnection() != null) {
			view().remove(getConnection());
		}

		setConnection(null);
		setStartConnector(null);
		setEndConnector(null);
		setAddedFigure(null);
		editor().toolDone();
	}

	public void deactivate() {
		super.deactivate();
		if (getTargetFigure() != null) {
			getTargetFigure().connectorVisibility(false, null);
		}
	}

	/**
	 * Creates the ConnectionFigure. By default the figure prototype is
	 * cloned.
	 */
	protected ConnectionFigure createConnection() {
		return (ConnectionFigure)fPrototype.clone();
	}

	/**
	 * Finds a connectable figure target.
	 */
	protected Figure findSource(int x, int y, Drawing drawing) {
		return findConnectableFigure(x, y, drawing);
	}

	/**
	 * Finds a connectable figure target at the current mouse location that can
	 * 1.  Connect to things
	 * 2.  Is not already connected to the current Connection (no self connection)
	 * 3.  The current Connection can make a connection between it and the start
	 *     figure.
	 */
	protected Figure findTarget(int x, int y, Drawing drawing) {
		Figure target = findConnectableFigure(x, y, drawing);
		Figure start = getStartConnector().owner();

		if (target != null
			 && getConnection() != null
			 && target.canConnect()
			 && !target.includes(start)
			 && getConnection().canConnect(start, target)) {
			return target;
		}
		return null;
	}

	/**
	 * Finds an existing connection figure.
	 */
	protected ConnectionFigure findConnection(int x, int y, Drawing drawing) {
		FigureEnumeration fe = drawing.figuresReverse();
		while (fe.hasNextFigure()) {
			Figure figure = fe.nextFigure();
			figure = figure.findFigureInside(x, y);
			if (figure != null && (figure instanceof ConnectionFigure)) {
				return (ConnectionFigure)figure;
			}
		}
		return null;
	}

	protected void setConnection(ConnectionFigure newConnection) {
		myConnection = newConnection;
	}

	/**
	 * Gets the connection which is created by this tool
	 */
	protected ConnectionFigure getConnection() {
		return myConnection;
	}

	/**
	 * Attempts to set the Connector to be connected to based on the current
	 * location of the mouse.
	 */
	protected void trackConnectors(DrawingViewMouseEvent dvme) {
		Figure c = null;

		// If tool is not actively looking for a target for our current
		// Connection, see if there are any connectable figures at at the
		// current location(findSource).
		if (getStartConnector() == null) {
			c = findSource(dvme.getX(), dvme.getY(), getActiveDrawing());
		}
		else {
			c = findTarget(dvme.getX(), dvme.getY(), getActiveDrawing());
		}

		// track the figure containing the mouse
		/**
		 * If the current target is not the stored target then turn off the
		 * stored targets connectors visibility and turn on the current targets
		 * connectors visibility. Then set the stored target to the current target.
		 * If the targets match then don't do anything.
		 * Note:  the target may have more than one connector.
		 */
		if (c != getTargetFigure()) {
			if (getTargetFigure() != null) {
				getTargetFigure().connectorVisibility(false, null);
			}
			setTargetFigure(c);
			if (getTargetFigure() != null) {
				getTargetFigure().connectorVisibility(true, getConnection());
			}
		}

		// Since you have a legitimate target, grab its connector and set it as
		// the new target connector.
		Connector cc = null;
		if (c != null) {
			cc = findConnector(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY(), c);
		}
		if (cc != getTargetConnector()) {
			setTargetConnector(cc);
		}

		// Clean up the views.
		getActiveView().drawing().update();
	}

	protected Connector findConnector(int x, int y, Figure f) {
		return f.connectorAt(x, y);
	}

	/**
	 * Finds a connection start figure.
	 */
	protected Figure findConnectionStart(int x, int y, Drawing drawing) {
		Figure target = findConnectableFigure(x, y, drawing);
		if ((target != null) && target.canConnect()) {
			return target;
		}
		return null;
	}

	/**
	 * Returns the topmost? figure that can connect and is at the current mouse
	 * location.
	 */
	protected Figure findConnectableFigure(int x, int y, Drawing drawing) {
		FigureEnumeration fe = drawing.figuresReverse();
		while (fe.hasNextFigure()) {
			Figure figure = fe.nextFigure();
			if (!figure.includes(getConnection()) && figure.canConnect()
				&& figure.containsPoint(x, y)) {
				return figure;
			}
		}
		return null;
	}

	protected void setStartConnector(Connector newStartConnector) {
		myStartConnector = newStartConnector;
	}

	protected Connector getStartConnector() {
		return myStartConnector;
	}

	protected void setEndConnector(Connector newEndConnector) {
		myEndConnector = newEndConnector;
	}

	protected Connector getEndConnector() {
		return myEndConnector;
	}

	protected void setTargetConnector(Connector newTargetConnector) {
		myTargetConnector = newTargetConnector;
	}

	protected Connector getTargetConnector() {
		return myTargetConnector;
	}

	protected void setTargetFigure(Figure newTarget) {
		myTarget = newTarget;
	}

	protected Figure getTargetFigure() {
		return myTarget;
	}

	/**
	 * Gets the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	protected Figure getAddedFigure() {
		return myAddedFigure;
	}

	protected void setAddedFigure(Figure newAddedFigure) {
		myAddedFigure = newAddedFigure;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new ConnectionTool.UndoActivity(view(), getConnection());
	}

	public static class UndoActivity extends UndoableAdapter {

		private ConnectionFigure  myConnection;
		private Connector   myStartConnector;
		private Connector   myEndConnector;

		public UndoActivity(DrawingView newDrawingView, ConnectionFigure newConnection) {
			super(newDrawingView);
			setConnection(newConnection);
			myStartConnector = getConnection().getStartConnector();
			myEndConnector = getConnection().getEndConnector();
	        setUndoable(true);
			setRedoable(true);
		}

		/*
		 * Undo the activity
		 * @return true if the activity could be undone, false otherwise
		 */
		public boolean undo() {
			if (!super.undo()) {
				return false;
			}

			getConnection().disconnectStart();
			getConnection().disconnectEnd();

			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				//This tool must now release these figures when it is ready
				getDrawingView().drawing().orphan(fe.nextFigure());
			}

			getDrawingView().clearSelection();

			return true;
		}

		/*
		 * Redo the activity
		 * @return true if the activity could be redone, false otherwise
		 */
		public boolean redo() {
			if (!super.redo()) {
				return false;
			}

			getConnection().connectStart(myStartConnector);
			getConnection().connectEnd(myEndConnector);
			getConnection().updateConnection();

			getDrawingView().insertFigures(getAffectedFigures(), 0, 0, false);

			return true;
		}

		protected void setConnection(ConnectionFigure newConnection) {
			myConnection = newConnection;
		}

		/**
		 * Gets the currently created figure
		 */
		protected ConnectionFigure getConnection() {
			return myConnection;
		}
	}
}
