/*
 * @(#)BorderTool.java
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
import CH.ifa.draw.util.*;


import java.awt.event.InputEvent;
import java.util.List;
import CH.ifa.draw.samples.javadraw.AnimationDecorator;
/**
 * BorderTool decorates the clicked figure with a BorderDecorator.
 *
 * @see BorderDecorator
 * @see CH.ifa.draw.contrib.DecoratorTool
 * @version <$CURRENT_VERSION$>
 */
public  class BorderTool extends ActionTool {

	public BorderTool(DrawingEditor editor) {
		super(editor);
	}

	/**
	 * Add the touched figure to the selection of an invoke action.
	 * Overrides ActionTool's mouseDown to allow for peeling the border
	 * if there is one already.
	 * This is done by CTRLing the click
	 *
	 * @see #action
	 */
	public void mouseDown(DrawingViewMouseEvent dvme) {
		// use event coordinates to supress any kind of
		// transformations like constraining points to a grid
		setAnchorX( dvme.getX() );
		setAnchorY( dvme.getY() );		
		setView( dvme.getDrawingView() );
		// if not CTRLed then proceed normally
		if ((dvme.getMouseEvent().getModifiers() & InputEvent.CTRL_MASK) == 0) {
			super.mouseDown(dvme);
		}
		else {
			Figure target = drawing().findFigure( getAnchorX(), getAnchorY());
			if (target != null && target instanceof DecoratorFigure) {
				view().addToSelection(target);
				reverseAction(target);
			}
		}
	}

	/**
	* Decorates the clicked figure with a border.
	*/
	public void action(Figure figure) {
//    	Figure replaceFigure = drawing().replace(figure, new BorderDecorator(figure));
		
		setUndoActivity(createUndoActivity());
		List l = CollectionsFactory.current().createList();
		l.add(figure);
		getUndoActivity().setAffectedFigures(new FigureEnumerator(l));
		((BorderTool.UndoActivity)getUndoActivity()).borderizeFigures();
	}

	/**
	* Peels off the border from the clicked figure.
	*/
	public void reverseAction(Figure figure) {
		setUndoActivity(createUndoActivity());
		List l = CollectionsFactory.current().createList();
		l.add(figure);
		//l.add(((DecoratorFigure)figure).peelDecoration());
		getUndoActivity().setAffectedFigures(new FigureEnumerator(l));
		((BorderTool.UndoActivity)getUndoActivity()).borderizeFigures();
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new BorderTool.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter {
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (!super.undo()) {
				return false;
			}
			if(getAffectedFigures().hasNextFigure()){
				getDrawingView().clearSelection();
				Figure f = getAffectedFigures().nextFigure();
				getDrawingView().drawing().orphan(f);
				
				if(f instanceof AnimationDecorator){
					f = ((AnimationDecorator)f).peelDecoration();
				}
				Figure innerf = ((DecoratorFigure)f).peelDecoration();
				
				List l = CollectionsFactory.current().createList(1);
				getDrawingView().drawing().add( innerf );
				l.add( innerf );
				setAffectedFigures(new FigureEnumerator( l ));
				return true;
			}
			return false;
		}

		public boolean redo() {
			if (!isRedoable()) {
				return false;
			}
			return borderizeFigures();
		}
		
//		public boolean replaceAffectedFigures() {
//			FigureEnumeration fe = getAffectedFigures();
//			if (!fe.hasNextFigure()) {
//				return false;
//			}
//			Figure oldFigure = fe.nextFigure();
//
//			if (!fe.hasNextFigure()) {
//				return false;
//			}
//			Figure replaceFigure = fe.nextFigure();
//			
//			replaceFigure = getDrawingView().drawing().replace(oldFigure, replaceFigure);
//			List l = CollectionsFactory.current().createList();
//			l.add(replaceFigure);
//			l.add(oldFigure);
//			setAffectedFigures(new FigureEnumerator(l));
//			
//			return true;
//		}
		public boolean borderizeFigures() {
			if(getAffectedFigures().hasNextFigure()){
				Figure f = getAffectedFigures().nextFigure();
				getDrawingView().drawing().orphan(f);
				getDrawingView().clearSelection();

				BorderDecorator bd = new BorderDecorator(f);
				getDrawingView().add( bd );
				List l = CollectionsFactory.current().createList(1);
				l.add(bd);
				setAffectedFigures( new FigureEnumerator(l));
				return true;
			}
			return false;
		}
	}
}
