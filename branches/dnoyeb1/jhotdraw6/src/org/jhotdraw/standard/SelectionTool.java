/*
 * @(#)SelectionTool.java
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
import CH.ifa.draw.util.UndoableTool;
import CH.ifa.draw.util.UndoableHandle;
import CH.ifa.draw.contrib.dnd.DragNDropTool;


/**
 * Tool to select and manipulate figures.
 * A selection tool is in one of three states, e.g., background
 * selection, figure selection, handle manipulation. The different
 * states are handled by different child tools.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld032.htm>State</a></b><br>
 * SelectionTool is the StateContext and child is the State.
 * The SelectionTool delegates state specific
 * behavior to its current child tool.
 * <hr>
 *
 * @version <$CURRENT_VERSION$>
 */

public class SelectionTool extends AbstractTool {

	private Tool fChild = null;

	public SelectionTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	/**
	 * Handles mouse down events and starts the corresponding tracker.
	 */
	public void mouseDown(DrawingViewMouseEvent dvme) {
		super.mouseDown(dvme);
		// use event coordinates to supress any kind of
		// transformations like constraining points to a grid
		setAnchorX( dvme.getMouseEvent().getX() );
		setAnchorY( dvme.getMouseEvent().getY() );
		// on MS-Windows NT: AWT generates additional mouse down events
		// when the left button is down && right button is clicked.
		// To avoid dead locks we ignore such events
		if (fChild != null) {
			return;
		}

		view().freezeView();

		Handle handle = view().findHandle(getAnchorX(), getAnchorY());
		if (handle != null) {
			fChild = createHandleTracker(view(), handle);
		}
		else {
			Figure figure = drawing().findFigure(getAnchorX(), getAnchorY());
			
			if (figure != null) {
				fChild = createDragTracker(figure);
			}
			else {
				if (!dvme.getMouseEvent().isShiftDown()) {
					view().clearSelection();
				}
				fChild = createAreaTracker();
			}
		}
		fChild.activate();
		fChild.mouseDown(dvme);
	}

	/**
	 * Handles mouse moves (if the mouse button is up).
	 * Switches the cursors depending on whats under them.
	 */
	public void mouseMove(DrawingViewMouseEvent dvme) {
		if (dvme.getDrawingView() == getActiveView() ) {
			DragNDropTool.setCursor(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY(), getActiveView());
		}
	}

	/**
	 * Handles mouse drag events. The events are forwarded to the
	 * current tracker.
	 */
	public void mouseDrag(DrawingViewMouseEvent dvme) {
		if (fChild != null) { // JDK1.1 doesn't guarantee mouseDown, mouseDrag, mouseUp
			fChild.mouseDrag(dvme);
		}
	}

	/**
	 * Handles mouse up events. The events are forwarded to the
	 * current tracker.
	 */
	public void mouseUp(DrawingViewMouseEvent dvme) {
		if (fChild != null) { // JDK1.1 doesn't guarantee mouseDown, mouseDrag, mouseUp
			fChild.mouseUp(dvme);
			fChild.deactivate();
			fChild = null;
		}
		if (view() != null) {
			view().unfreezeView();
		}
	}

	/**
	 * Factory method to create a Handle tracker. It is used to track a handle.
	 */
	protected Tool createHandleTracker(DrawingView view, Handle handle) {
		return new HandleTracker(editor(), new UndoableHandle(handle, view));
	}

	/**
	 * Factory method to create a Drag tracker. It is used to drag a figure.
	 */
	protected Tool createDragTracker(Figure f) {
		return new UndoableTool(new DragTracker(editor(), f));
	}

	/**
	 * Factory method to create an area tracker. It is used to select an
	 * area.
	 */
	protected Tool createAreaTracker() {
		return new SelectAreaTracker(editor());
	}
}
