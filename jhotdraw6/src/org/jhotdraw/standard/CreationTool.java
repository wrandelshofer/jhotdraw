/*
 * @(#)CreationTool.java
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
import CH.ifa.draw.util.Undoable;
import java.awt.*;


/**
 * A tool to create new figures. The figure to be
 * created is specified by a prototype.
 *
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld029.htm>Prototype</a></b><br>
 * CreationTool creates new figures by cloning a prototype.
 * <hr>
 *
 * @see Figure
 * @see Object#clone
 *
 * @version <$CURRENT_VERSION$>
 */


public class CreationTool extends AbstractTool {

	/**
	 * the anchor point of the interaction
	 * This is redundant. AbstractTool already has AnchorX and AnchorY which all
	 * other tools are using.
	 * @deprecated
	 */
	private Point   fAnchorPoint;

	/**
	 * the currently created figure
	 */
	private Figure  fCreatedFigure;

	/**
	 * the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	private Figure myAddedFigure;

	/**
	 * the prototypical figure that is used to create new figures.
	 */
	private Figure  fPrototype;


	/**
	 * Initializes a CreationTool with the given prototype.
	 */
	public CreationTool(DrawingEditor newDrawingEditor, Figure prototype) {
		super(newDrawingEditor);
		fPrototype = prototype;
	}

	/**
	 * Constructs a CreationTool without a prototype.
	 * This is for subclassers overriding createFigure.
	 */
	protected CreationTool(DrawingEditor newDrawingEditor) {
		this(newDrawingEditor, null);
	}

	/**
	 * Sets the cross hair cursor.
	 */
	public void activate() {
		super.activate();
		if (isUsable()) {
			getActiveView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
	}

	/**
	 * Creates a new figure by cloning the prototype.
	 */
	public void mouseDown(DrawingViewMouseEvent dvme) {
		setView( dvme.getDrawingView() );
		setAnchorPoint(new Point(dvme.getX(), dvme.getY()));
		setCreatedFigure(createFigure());
		setAddedFigure(view().add(getCreatedFigure()));
		getAddedFigure().displayBox(getAnchorPoint(), getAnchorPoint());
	}

	/**
	 * Creates a new figure by cloning the prototype.
	 */
	protected Figure createFigure() {
		if (fPrototype == null) {	//should be ASSERT and not runtime exception because this is a condition that should never happen!!! dnoyeb
			throw new JHotDrawRuntimeException("No protoype defined");
		}
		return (Figure) fPrototype.clone();
	}

	/**
	 * Adjusts the extent of the created figure
	 */
	public void mouseDrag(DrawingViewMouseEvent dvme) {
		if (getAddedFigure() != null) {
			getAddedFigure().displayBox(getAnchorPoint(), new Point(dvme.getX(),dvme.getY()));
		}
	}

	/**
	 * Checks if the created figure is empty. If it is, the figure
	 * is removed from the drawing.
	 * @see Figure#isEmpty
	 */
	public void mouseUp(DrawingViewMouseEvent dvme) {
		if (getAddedFigure() != null) {
			if (getCreatedFigure().isEmpty()) {
				drawing().remove(getAddedFigure());
				// nothing to undo
				setUndoActivity(null);
			}
			else {
				// use undo activity from paste command...
				setUndoActivity(createUndoActivity());

				// put created figure into a figure enumeration
				getUndoActivity().setAffectedFigures(new SingleFigureEnumerator(getAddedFigure()));
			}
			setAddedFigure(null);
		}
		setCreatedFigure(null);
		editor().toolDone();
	}

	/**
	 * Gets the currently created figure
	 */
	protected Figure getCreatedFigure() {
		return fCreatedFigure;
	}

	/**
	 * Sets the createdFigure attribute of the CreationTool object
	 */
	protected void setCreatedFigure(Figure newCreatedFigure) {
		fCreatedFigure = newCreatedFigure;
	}

	/**
	 * Gets the figure that was actually added
	 * Note, this can be a different figure from the one which has been created.
	 */
	protected Figure getAddedFigure() {
		return myAddedFigure;
	}

	/**
	 * Sets the addedFigure attribute of the CreationTool object
	 */
	protected void setAddedFigure(Figure newAddedFigure) {
		myAddedFigure = newAddedFigure;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
	}

	/**
	 * The anchor point is usually the first mouse click performed with this tool.
	 *
	 * @return the anchor point for the interaction
	 * @see #mouseDown
	 * @deprecated use {@link AbstractTool#getAnchorX() 
	 *			   AbstractTool.getAnchorX()} and {@link AbstractTool#getAnchorY()
	 *             AbstractTool.getAnchorY()} instead.
	 *
	 */
	protected Point getAnchorPoint() {
		// SF bug-report id: #490752
		return fAnchorPoint;
	}


	/**
	 * Sets the anchorPoint attribute of the CreationTool object
	 * @deprecated use {@link AbstractTool#setAnchorX() 
	 *			   AbstractTool.setAnchorX()} and {@link AbstractTool#setAnchorY()
	 *             AbstractTool.setAnchorY()} instead.
	 * @param newAnchorPoint  The new anchorPoint value
	 */
	protected void setAnchorPoint(Point newAnchorPoint) {
		fAnchorPoint = newAnchorPoint;
	}
}
