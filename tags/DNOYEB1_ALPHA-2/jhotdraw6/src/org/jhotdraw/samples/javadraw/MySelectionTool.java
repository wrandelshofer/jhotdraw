/*
 * @(#)MySelectionTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.javadraw;


import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * A SelectionTool that interprets double clicks to inspect the clicked figure
 *
 * @version <$CURRENT_VERSION$>
 */
public  class MySelectionTool extends SelectionTool {

	public MySelectionTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	/**
	 * Handles mouse down events and starts the corresponding tracker.
	 */
	public void mouseDown(DrawingViewMouseEvent dvme) {
		setView( dvme.getDrawingView() );
		// use event coordinates to supress any kind of
		// transformations like constraining points to a grid
		setAnchorX( dvme.getMouseEvent().getX() );
		setAnchorY( dvme.getMouseEvent().getY() );
		
		if (dvme.getMouseEvent().getClickCount() == 2) {
			Figure figure = drawing().findFigure(getAnchorX(), getAnchorY());
			if (figure != null) {
				inspectFigure(figure);
				return;
			}
		}
		super.mouseDown(dvme);
	}

	protected void inspectFigure(Figure f) {
		System.out.println("inspect figure"+f);
	}
}
