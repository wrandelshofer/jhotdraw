/*
 * @(#)StandardDrawing.java
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
import CH.ifa.draw.util.CollectionsFactory;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.io.*;

/**
 * The standard implementation of the Drawing interface.
 * Fix this to aggregrate CompositeFigure instead of extending it.
 *
 * @see Drawing
 *
 * @version <$CURRENT_VERSION$>
 */

public class StandardDrawing extends CompositeFigure implements Drawing {


	/**
	 * the registered listeners
	 */
	private transient List              fListeners;

	/**
	 * boolean that serves as a condition variable
	 * to lock the access to the drawing.
	 * The lock is recursive and we keep track of the current
	 * lock holder.
	 */
	private transient Thread    fDrawingLockHolder = null;
	private String				myTitle;

	/*
	 * Serialization support
	 */
	private static final long serialVersionUID = -2602151437447962046L;
	private int drawingSerializedDataVersion = 1;

	/**
	 * Constructs the Drawing.
	 */
	public StandardDrawing() {
		super();
		fListeners = CollectionsFactory.current().createList(2);
		init(new Rectangle(-500, -500, 2000, 2000));
	}

	/**
	 * Adds a listener for this drawing.
	 */
	public void addDrawingChangeListener(DrawingChangeListener listener) {
		if (fListeners == null) {
			fListeners = CollectionsFactory.current().createList(2);
		}
		fListeners.add(listener);
	}

	/**
	 * Removes a listener from this drawing.
	 */
	public void removeDrawingChangeListener(DrawingChangeListener listener) {
		fListeners.remove(listener);
	}

	/**
	 * Gets an enumeration with all listener for this drawing.
	 */
	protected Iterator drawingChangeListeners() {
		return fListeners.iterator();
	}

	/**
	 * Removes a figure from the figure list, but
	 * doesn't release it. Use this method to temporarily
	 * manipulate a figure outside of the drawing.
	 *
	 * @param figure that is part of the drawing and should be added
	 */
/*	public synchronized Figure orphan(Figure figure) {
		return super.orphan(figure);
	}*/

/*	public synchronized Figure add(Figure figure) {
		Figure addedFigure = super.add(figure);
		addedFigure.update();
		return addedFigure;
	}*/
	
	/**
	 * Causes the drawing to requestUpdate
	 */
	public void update() {
		//super.update(); //nobody is expecting us to behave like a CompositeFigure so this is ok to mask.
		fireDrawingRequestUpdate();
	}

	/**
	 * One of the contained figures is announcing that part of it has been
	 * invalidated.  We announce to our listeners that this same part of us has
	 * been invalidated.
	 *
	 * @see FigureChangeListener
	 * @see DrawingChangeListener
	 */
	protected void figureInvalidated(FigureChangeEvent e) {
		//super.figureInvalidated(e);
		fireDrawingInvalidated(e.getInvalidatedRectangle());
	}
	
	/**
	 * Forces an update of the drawing change listeners.
	 * this is error? its overriding the compositeFigure basic behavior.
	 * should leave composite figure basic behavior alone, and fire drawingrequestupdate
	 * as well as.
	 *
	 * this is fired when the figures we are listening to change.
	 * 
	 */
	protected void figureRequestUpdate(FigureChangeEvent e) {
		//super.figureRequestUpdate(e);
		fireDrawingRequestUpdate(); //this will cause the drawing to be redrawn 
	}	
	/**
	 * 
	 */
	protected void fireDrawingInvalidated(Rectangle invalidRectangle) {
		if (fListeners != null) {
			for (int i = 0; i < fListeners.size(); i++) {
				DrawingChangeListener l = (DrawingChangeListener)fListeners.get(i);
				l.drawingInvalidated(new DrawingChangeEvent(this, invalidRectangle));
			}
		}
	}

	/**
	 * Forces an update of the drawing change listeners.
	 */
	protected void fireDrawingTitleChanged() {
		if (fListeners != null) {
			for (int i = 0; i < fListeners.size(); i++) {
				DrawingChangeListener l = (DrawingChangeListener)fListeners.get(i);
				l.drawingTitleChanged(new DrawingChangeEvent(this, null));
			}
		}
	}

	/**
	 *  Sent when the drawing wants to be refreshed
	 */
	protected void fireDrawingRequestUpdate() {
		if (fListeners != null) {
			for (int i = 0; i < fListeners.size(); i++) {
				DrawingChangeListener l = (DrawingChangeListener)fListeners.get(i);
				l.drawingRequestUpdate(new DrawingChangeEvent(this, null));
			}
		}
	}

	/**
	 * Return's the figure's handles. This is only used when a drawing
	 * is nested inside another drawing.
	 */
	public HandleEnumeration handles() {
		List handles = CollectionsFactory.current().createList();
		handles.add(new NullHandle(this, RelativeLocator.northWest()));
		handles.add(new NullHandle(this, RelativeLocator.northEast()));
		handles.add(new NullHandle(this, RelativeLocator.southWest()));
		handles.add(new NullHandle(this, RelativeLocator.southEast()));
		return new HandleEnumerator(handles);
	}

	/**
	 * Gets the display box. This is the union of all figures.
	 */
	public Rectangle displayBox() {
		if (figureCount() > 0) {
			FigureEnumeration fe = figures();

			Rectangle r = fe.nextFigure().displayBox();

			while (fe.hasNextFigure()) {
				r.add(fe.nextFigure().displayBox());
			}
			return r;
		}
		return new Rectangle(0, 0, 0, 0);
	}

	public void basicDisplayBox(Point p1, Point p2) {
	}

	/**
	 * Acquires the drawing lock.
	 */
	public synchronized void lock() {
		// recursive lock
		Thread current = Thread.currentThread();
		if (fDrawingLockHolder == current) {
			return;
		}
		while (fDrawingLockHolder != null) {
			try {
				wait();
			}
			catch (InterruptedException ex) { }
		}
		fDrawingLockHolder = current;
	}

	/**
	 * Releases the drawing lock.
	 */
	public synchronized void unlock() {
		if (fDrawingLockHolder != null) {
			fDrawingLockHolder = null;
			notify();
		}
	}

	private void readObject(ObjectInputStream s)
		throws ClassNotFoundException, IOException {

		s.defaultReadObject();

		fListeners = CollectionsFactory.current().createList(2);
	}

	public String getTitle() {
		return myTitle;
	}

	public void setTitle(String newTitle) {
		myTitle = newTitle;
        fireDrawingTitleChanged();
	}
}
