/*
 * @(#)ActionTool.java
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


/**
 * A tool that performs an action when it is active and
 * the mouse is clicked.
 *
 * @version <$CURRENT_VERSION$>
 */
public abstract class ActionTool extends AbstractTool {

	public ActionTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	/**
	 * Add the touched figure to the selection an invoke action
	 * @see #action
	 */
	public void mouseDown(DrawingViewMouseEvent dvme) {
		super.mouseDown(dvme);
		setAnchorX( dvme.getMouseEvent().getX() );
		setAnchorY( dvme.getMouseEvent().getY() );
		
		Figure target = drawing().findFigure( getAnchorX() ,getAnchorY() );
		if (target != null) {
			view().addToSelection(target);
			action(target);
		}
	}

	public void mouseUp(DrawingViewMouseEvent dvme) {
		editor().toolDone();
	}

	/**
	 * Performs an action with the touched figure.
	 */
	public abstract void action(Figure figure);
}
