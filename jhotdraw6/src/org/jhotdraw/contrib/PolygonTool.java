/*
 * @(#)PolygonTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.Undoable;
import java.awt.*;


/**
 * Based on ScribbleTool
 *
 * @author Doug Lea  (dl at gee) - Fri Feb 28 07:47:05 1997
 * @version <$CURRENT_VERSION$>
 */
public class PolygonTool extends AbstractTool {

	private PolygonFigure  fPolygon;
	private int            fLastX, fLastY;

	/**
	 * the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	private Figure myAddedFigure;

	public PolygonTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	public void activate() {
		super.activate();
		fPolygon = null;
	}

	public void deactivate() {
		if (fPolygon != null) {
			fPolygon.smoothPoints();
			if (fPolygon.pointCount() < 3 ||
					fPolygon.size().width < 4 || fPolygon.size().height < 4) {
				getActiveView().drawing().remove(fPolygon);
				// nothing to undo
				setUndoActivity(null);
			}
		}
		fPolygon = null;
		super.deactivate();
	}

	private void addPoint(int x, int y) {
		if (fPolygon == null) {
			fPolygon = new PolygonFigure(x, y);
			setAddedFigure(view().add(fPolygon));
			fPolygon.addPoint(x, y);
		}
		else if (fLastX != x || fLastY != y) {
			fPolygon.addPoint(x, y);
		}

		fLastX = x;
		fLastY = y;
	}

	public void mouseDown(DrawingViewMouseEvent dvme) {
		super.mouseDown(dvme);
		// use event coordinates to supress any kind of
		// transformations like constraining points to a grid
		setAnchorX( dvme.getMouseEvent().getX() );
		setAnchorY( dvme.getMouseEvent().getY() );

		if (dvme.getMouseEvent().getClickCount() >= 2) {
			if (fPolygon != null) {
				fPolygon.smoothPoints();

				// use undo activity from paste command...
				setUndoActivity(createUndoActivity());

				// put created figure into a figure enumeration
				getUndoActivity().setAffectedFigures(new SingleFigureEnumerator(getAddedFigure()));

				editor().toolDone();
			}
			fPolygon = null;

		}
		else {
			// use original event coordinates to avoid
			// supress that the scribble is constrained to
			// the grid
			addPoint( getAnchorX(), getAnchorY() );
		}
	}

	public void mouseMove(DrawingViewMouseEvent dvme) {
		if (dvme.getDrawingView() == getActiveView()) {
			if (fPolygon != null) {
				if (fPolygon.pointCount() > 1) {
					fPolygon.setPointAt(new Point(dvme.getX(), dvme.getY()), fPolygon.pointCount()-1);
					getActiveView().checkDamage();
				}
			}
		}
	}

	public void mouseDrag(DrawingViewMouseEvent dvme) {
		// replace pts by actual event pts
		addPoint(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY());
	}

	public void mouseUp(DrawingViewMouseEvent dvme) {
	}

	/**
	 * Gets the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	protected Figure getAddedFigure() {
		return myAddedFigure;
	}

	private void setAddedFigure(Figure newAddedFigure) {
		myAddedFigure = newAddedFigure;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
	}
}
