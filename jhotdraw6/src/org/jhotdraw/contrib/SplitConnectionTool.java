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

package CH.ifa.draw.contrib;

import CH.ifa.draw.standard.ConnectionTool;
import CH.ifa.draw.standard.SingleFigureEnumerator;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.figures.*;


import java.awt.*;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class SplitConnectionTool extends ConnectionTool {
	public SplitConnectionTool(DrawingEditor newDrawingEditor, ConnectionFigure newPrototype) {
		super(newDrawingEditor, newPrototype);
	}

	public void mouseDown(DrawingViewMouseEvent dvme) {
		setView( dvme.getDrawingView() );
		setAnchorX( dvme.getMouseEvent().getX() );
		setAnchorY( dvme.getMouseEvent().getY() );
		if (getTargetFigure() == null) {
			setTargetFigure(findConnectableFigure(getAnchorX(), getAnchorY(), drawing()));
		}
		else {
			if (getAddedFigure() == null) {
				setConnection(createConnection());
				setStartConnector(findConnector(getAnchorX(), getAnchorY(), getTargetFigure()));
				getConnection().connectStart(getStartConnector());
				getConnection().startPoint(getAnchorX(), getAnchorY());
				view().add(getConnection());
				setAddedFigure(getConnection());
			}
			Figure c = findTarget(getAnchorX(), getAnchorY(), drawing());
			if (c != null) {
				// end connection figure found
				setEndConnector(findConnector(getAnchorX(), getAnchorY(), c));
				getConnection().connectEnd(getEndConnector());
				getConnection().endPoint(getAnchorX(), getAnchorY());
				setUndoActivity(createUndoActivity());
				getUndoActivity().setAffectedFigures(
					new SingleFigureEnumerator(getAddedFigure()));
				getConnection().updateConnection();
				init();
				editor().toolDone();
			}
			else {
				// split connection where the mouse click took place
				if (getEndConnector() == null) {
					Figure tempEndFigure = new NullFigure();
					tempEndFigure.basicDisplayBox(new Point(getAnchorX(), getAnchorY()), new Point(getAnchorX(), getAnchorY()));
					setEndConnector(new NullConnector(tempEndFigure));
					getConnection().connectEnd(getEndConnector());
					getConnection().endPoint(getAnchorX(), getAnchorY());
					getConnection().updateConnection();
				}
				else {
					((PolyLineFigure)getConnection()).addPoint(getAnchorX(), getAnchorY());
				}
			}
		}
	}

	public void mouseUp(DrawingViewMouseEvent dvme) {
		// usually do nothing: tool is still active
		if (dvme.getMouseEvent().getClickCount() == 2) {
			init();
			editor().toolDone();
		}
	}

	public void mouseMove(DrawingViewMouseEvent dvme) {
		// avoid tracking connectors
	}

	public void mouseDrag(DrawingViewMouseEvent dvme) {
		// avoid tracking connectors
	}

	public void deactivate() {
		if (getConnection() != null) {
			view().remove(getConnection());
		}
		super.deactivate();
		init();
	}

	protected void init() {
		setConnection(null);
		setStartConnector(null);
		setEndConnector(null);
		setAddedFigure(null);
		setTargetFigure(null);
	}
}
