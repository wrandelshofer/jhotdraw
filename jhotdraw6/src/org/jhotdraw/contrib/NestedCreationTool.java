/*
 * @(#)CreationTool2.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.standard.CreationTool;
import CH.ifa.draw.standard.CompositeFigure;
import CH.ifa.draw.standard.DecoratorFigure;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.framework.DrawingViewMouseEvent;

/**
 * @author	Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public class NestedCreationTool extends CreationTool {
	private CompositeFigure myContainerFigure;

	public NestedCreationTool(DrawingEditor newDrawingEditor, Figure prototype) {
		super(newDrawingEditor, prototype);
	}

	public void mouseDown(DrawingViewMouseEvent dvme) {
		setView( dvme.getDrawingView() );
		setAnchorX( dvme.getX() );
		setAnchorY( dvme.getY() );
		
		Figure figure = getFigureWithoutDecoration(drawing().findFigure( getAnchorX(), getAnchorY()));
		if ((figure != null) && (figure instanceof CompositeFigure)) {
			setContainerFigure((CompositeFigure)figure);
			super.mouseDown(dvme);
		}
		else {
			toolDone();
		}
	}

	private Figure getFigureWithoutDecoration(Figure peelFigure) {
		if (peelFigure instanceof DecoratorFigure) {
			return getFigureWithoutDecoration(((DecoratorFigure)peelFigure).getDecoratedFigure());
		}
		else {
			return peelFigure;
		}
	}

	public void mouseMove(DrawingViewMouseEvent dvme) {
		if ((getContainerFigure() != null) && !getContainerFigure().containsPoint(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY())) {
			// here you might want to constrain the mouse movements to the size of the
			// container figure: not sure whether this works...
			//why would you cancel the tool just because it ventured outside of container?
			toolDone();
		}
		else {
			super.mouseMove(dvme);
		}
	}

	public void mouseUp(DrawingViewMouseEvent dvme) {
		if ((getContainerFigure() != null) && (getCreatedFigure() != null)
				&& getContainerFigure().containsPoint(dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY())) {
			getContainerFigure().add(getCreatedFigure());
		}
		toolDone();
	}

	protected void setContainerFigure(CompositeFigure newContainerFigure) {
		myContainerFigure = newContainerFigure;
	}

	public CompositeFigure getContainerFigure() {
		return myContainerFigure;
	}

	protected void toolDone() {
		setCreatedFigure(null);
		setAddedFigure(null);
		setContainerFigure(null);
		editor().toolDone();
	}
}
