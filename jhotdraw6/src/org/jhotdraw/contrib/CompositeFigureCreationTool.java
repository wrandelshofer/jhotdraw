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
import CH.ifa.draw.standard.DecoratorFigure;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 *	Tool must be created before all views it will be used on.
 *  dnoyeb changed this tool to only be useable when a CompositeFigure is selected.
 *  Otherwise, one must try to work on every figure he/she sees without knowing which
 *	is a CompositeFigure until the tool fails to work.  This way at least when you click
 *	on one, the tool will become useable and you can see that because the button will
 *	no longer be greyed out.  I think it helps when you have several tools, to know
 *	which ones you can use when.
 *
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
	
	public void mouseDown(MouseEvent e, int x, int y) {
		setView((DrawingView)e.getSource());
		Figure figure = getFigureWithoutDecoration(drawing().findFigure(e.getX(), e.getY()));
		if ((figure != null) && (figure instanceof CompositeFigure)) {
			setContainerFigure((CompositeFigure)figure);
			setCreatedFigure(createFigure());
			setAddedFigure((getContainerFigure().add(getCreatedFigure())));

			setAnchorPoint(new Point(x, y));
			getAddedFigure().displayBox(getAnchorPoint(), getAnchorPoint());
		}
		else {
			toolDone();
		}
	}

	protected Figure getFigureWithoutDecoration(Figure peelFigure) {
		if (peelFigure instanceof DecoratorFigure) {
			return getFigureWithoutDecoration(((DecoratorFigure)peelFigure).getDecoratedFigure());
		}
		else {
			return peelFigure;
		}
	}
	/**
	 *	
	 *	No this does not work because their is no container figure until the mouseDown.
	 *	This container only lasts until mouseUp when toolDone() is called.  Mousemove
	 *	is never called between mouse down and mouse up.  mouseDrag is...dnoyeb.
	 *	Perhaps what you want to do here is alter the cursor when over the proper
	 *	figure type???
	 *
	 */
	public void mouseMove(MouseEvent e, int x, int y) {
		DrawingView v = (DrawingView)e.getSource();
		Figure f = getFigureWithoutDecoration( v.drawing().findFigure(e.getX(), e.getY()) );
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
	public void mouseDrag(MouseEvent e, int x, int y) {
		if ((getContainerFigure() != null) && getContainerFigure().containsPoint(e.getX(), e.getY())) {
			super.mouseDrag(e,x, y);
		}
	}
	
	public void mouseUp(MouseEvent e, int x, int y) {
		if ((getContainerFigure() != null) && (getCreatedFigure() != null)
				&& getContainerFigure().containsPoint(e.getX(), e.getY())) {
			getContainerFigure().add(getCreatedFigure());
		}
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
					Figure f = getFigureWithoutDecoration( fe.nextFigure() );
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
