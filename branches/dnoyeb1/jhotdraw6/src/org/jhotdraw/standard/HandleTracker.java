/*
 * @(#)HandleTracker.java
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
 * HandleTracker implements interactions with the handles of a Figure.
 *
 * @see SelectionTool
 *
 * @version <$CURRENT_VERSION$>
 */
public class HandleTracker extends AbstractTool {

	private Handle  fAnchorHandle;

	public HandleTracker(DrawingEditor newDrawingEditor, Handle anchorHandle) {
		super(newDrawingEditor);
		fAnchorHandle = anchorHandle;
	}

	public void mouseDown(DrawingViewMouseEvent dvme) {
		super.mouseDown(dvme);
		// use event coordinates to supress any kind of
		// transformations like constraining points to a grid, why?
		setAnchorX( dvme.getMouseEvent().getX() );
		setAnchorY( dvme.getMouseEvent().getY() );
		fAnchorHandle.invokeStart(getAnchorX(), getAnchorY(), view());
	}

	public void mouseDrag(DrawingViewMouseEvent dvme) {
		super.mouseDrag( dvme );
		fAnchorHandle.invokeStep(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY(), getAnchorX(), getAnchorY(), view());
	}

	public void mouseUp(DrawingViewMouseEvent dvme) {
		fAnchorHandle.invokeEnd(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY(), getAnchorX(), getAnchorY(), view());
		super.mouseUp(dvme);		
	}

	public void activate() {
		// suppress clearSelection() and tool-activation-notification
		// in superclass by providing an empty implementation
	}
}
