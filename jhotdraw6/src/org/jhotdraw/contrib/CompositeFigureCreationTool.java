/*
 * @(#)CompositeFigureCreationTool.java
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

import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

import java.awt.*;

/**
 *	Tool must be created before all views it will be used on.
 *
 * @author	Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public class CompositeFigureCreationTool extends CreationTool {
	private CompositeFigure myContainerFigure;
	private final FigureSelectionListener figureSelectionListener = new FigureSelectionListener() {
			public void figureSelectionChanged(DrawingView view){
				checkUsable();
			}
		};
	
	public CompositeFigureCreationTool(DrawingEditor newDrawingEditor, Figure prototype) {
		super(newDrawingEditor, prototype);
	}

	public void activate() {
		if (getActiveView() != null) {
			//getActiveView().clearSelection(); //do not clear selection as it is needed. see class level comments 
			getActiveView().checkDamage();
			getEventDispatcher().fireToolActivatedEvent();
		}
		if (isUsable()) {
			getActiveView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
	}
	
	protected void viewCreated(DrawingView view){
		super.viewCreated(view);
		view.addFigureSelectionListener(figureSelectionListener);
	}
	
	protected void viewDestroying(DrawingView view){
		view.removeFigureSelectionListener(figureSelectionListener);
		super.viewDestroying(view);
	}

	public void mouseDown(DrawingViewMouseEvent dvme) {
		setView( dvme.getDrawingView() );
		Figure figure = drawing().findFigure(dvme.getX(), dvme.getY());
		if ((figure != null) && (figure instanceof CompositeFigure)) {
			setContainerFigure((CompositeFigure)figure);
			setCreatedFigure(createFigure());
			getContainerFigure().add(getCreatedFigure());
			setAddedFigure(getCreatedFigure());
			setAnchorPoint(new Point(dvme.getX(), dvme.getY()));
			getAddedFigure().displayBox(getAnchorPoint(), getAnchorPoint());
		}
		else {
			toolDone();
		}
	}

	/**
	 *	
	 *	Perhaps what you want to do here is alter the cursor when over the proper
	 *	figure type???
	 *
	 */
	public void mouseMove(DrawingViewMouseEvent dvme) {
		DrawingView v = dvme.getDrawingView();
		Figure f = v.drawing().findFigure(dvme.getX(), dvme.getY());
		
		if(f instanceof CompositeFigure) {
			getActiveView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
		else {
			getActiveView().setCursor(Cursor.getDefaultCursor());//need the NO cursor, but where do I find it?
		}
	}
	/**
	 * Adjusts the extent of the created figure
	 * 
	 * Limited size to size of {@link CompositeFigure CompositeFigure}.  alternatly the container figure can
	 * stretch (if shift is down?)
	 *	Need better limiting system.  Like the one DrawingView uses so that x and y are
	 *	limited independently, and both dont stop operating the second one violates its
	 *	parameters.
	 */
	public void mouseDrag(DrawingViewMouseEvent dvme) {
		if ((getContainerFigure() != null) && getContainerFigure().containsPoint(dvme.getX(), dvme.getY())) {
			super.mouseDrag(dvme);
		}
	}
	/**
	 * When creation tool is modified to know of only ContainerFigures and is Drawing
	 * agnostic, we can call super.mouseUp here.
	 */
	public void mouseUp(DrawingViewMouseEvent dvme) {
		//figure is already added, no need for this.
//		if ((getContainerFigure() != null) && (getCreatedFigure() != null)
//				&& getContainerFigure().containsPoint(dvme.getX(), dvme.getY())) {
//			getContainerFigure().add(getCreatedFigure());
//		}
		view().drawing().update();//we made a change to the drawing, so update it.
		toolDone();
	}
	/**
	 *	This generates the required context sensitivity for this tool.
	 *	This tool is only useable when exactly one (1) CompositeFigure is selected.
	 *  I took this out because it requires an extra mouse push when you want to
	 * keep working with composite figures.  select figure, hit button, work.
	 * select figure, hit button work.  without this, you just hit button and work.
	 * i think its worth it to go back.
	 */
	protected void checkUsable() {
		super.checkUsable();
/*		if (isEnabled()) {
			DrawingView adv = getActiveView();
			if(adv != null && adv.isInteractive()){
				if(adv.selectionCount() == 1) {
					FigureSelection fs = adv.getFigureSelection();
					FigureEnumerator fe = (FigureEnumerator) fs.getData(StandardFigureSelection.TYPE);
					Figure f = fe.nextFigure();
					if(CompositeFigure.class.isInstance( f )){
						setUsable( true );
						return;
					}
				}
			}
			setUsable( false );
		}*/
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
