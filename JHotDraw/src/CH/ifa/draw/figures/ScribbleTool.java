/*
 * @(#)ScribbleTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.Undoable;

/**
 * Tool to scribble a PolyLineFigure
 *
 * @see PolyLineFigure
 *
 * @version <$CURRENT_VERSION$>
 */
public class ScribbleTool extends AbstractTool {

	private PolyLineFigure  fScribble;
	private int             fLastX, fLastY;

	/**
	 * the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	private Figure myAddedFigure;

	public ScribbleTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	public void activate() {
		super.activate();
	}

	public void deactivate() {
		super.deactivate();
		if (fScribble != null) {
			if (fScribble.size().width < 4 || fScribble.size().height < 4) {
				getActiveDrawing().remove(fScribble);
				//or fScribble.remove();fScribble.release();
				// nothing to undo
				setUndoActivity(null);
			}
			fScribble = null;
		}
	}

	private void point(int x, int y) {
		if (fScribble == null) {
			fScribble = new PolyLineFigure(x, y);
			view().add(fScribble);
			setAddedFigure(fScribble);
		}
		else if (fLastX != x || fLastY != y) {
			fScribble.addPoint(x, y);
		}

		fLastX = x;
		fLastY = y;
	}

	public void mouseDown(DrawingViewMouseEvent dvme) {
		super.mouseDown(dvme);
		if (dvme.getMouseEvent().getClickCount() >= 2) {
			// use undo activity from paste command...
			setUndoActivity(createUndoActivity());

			// put created figure into a figure enumeration
			getUndoActivity().setAffectedFigures(new SingleFigureEnumerator(getAddedFigure()));
		}
		else {
			// use original event coordinates to avoid
			// supress that the scribble is constrained to
			// the grid - why shouldnt the scribble be constraind to grid as all other Tools?
			point(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY());
		}
	}

	public void mouseDrag(DrawingViewMouseEvent dvme) {
		if (fScribble != null) {
			point(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY());
		}
	}

	public void mouseUp(DrawingViewMouseEvent dvme) {
		// deactivate tool only when mouseUp was also fired, needs testing. is deactive the right word here?
		if (dvme.getMouseEvent().getClickCount() >= 2) {
			editor().toolDone();
		}
		super.mouseUp(dvme);		
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
