/*
 * @(#)UngroupCommand.java
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
import CH.ifa.draw.util.UndoableAdapter;
import CH.ifa.draw.util.Undoable;
import java.util.*;
import CH.ifa.draw.util.CollectionsFactory;
/**
 * Command to ungroup the selected figures.
 *
 * @see GroupCommand
 *
 * @version <$CURRENT_VERSION$>
 */
public  class UngroupCommand extends AbstractCommand {

	/**
	 * Constructs a group command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public UngroupCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		// selection of group figures
		getUndoActivity().setAffectedFigures(view().selection());
		view().clearSelection();

		((UngroupCommand.UndoActivity)getUndoActivity()).ungroupFigures();
		view().drawing().update();
	}

	public boolean isExecutableWithView() {
		FigureEnumeration fe = view().selection();
		while (fe.hasNextFigure()) {
			Figure currentFigure = fe.nextFigure();
			if (currentFigure instanceof DecoratorFigure) {
				currentFigure = ((DecoratorFigure)currentFigure).getDecoratedFigure();
			}
			if (!(currentFigure instanceof GroupFigure)) {
				return false;
			}
		}

		return view().selectionCount() > 0;

	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new UngroupCommand.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter {
		private Map groupFigureMap = CollectionsFactory.current().createMap();
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (!super.undo()) {
				return false;
			}
			List affectedFigures = CollectionsFactory.current().createList();
			getDrawingView().clearSelection();
			//get the figures to regroup from the Map
			Set keySet = groupFigureMap.keySet();
			for(Iterator it = keySet.iterator();it.hasNext();){
				//get the former group figure
				GroupFigure groupFigure = (GroupFigure)it.next();
				//get the figures it used to contain, and empty the map too
				FigureEnumeration fe = (FigureEnumeration)groupFigureMap.remove(groupFigure);
				while(fe.hasNextFigure()){
					Figure f2 = fe.nextFigure();
					//orphan the figure from the drawing
					getDrawingView().drawing().orphan(f2);
					//restore the figure to the group figure
					groupFigure.add( f2 );
				}
				//restore the groupFigure to the drawing
				getDrawingView().drawing().add(groupFigure);
				//add the group figure to the selection
				getDrawingView().addToSelection(groupFigure);
				//add the figure to the affected figures.
				affectedFigures.add( groupFigure );
			}
			setAffectedFigures(new FigureEnumerator( affectedFigures ));
			//groupFigureMap should be empty, no need to manipulate it.
			return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				getDrawingView().clearSelection();
				ungroupFigures();
				return true;
			}
			return false;
		}

		/**
		 *
		 */
		protected void ungroupFigures() {
			//get the GroupFigures
			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				List affectedFigures = CollectionsFactory.current().createList();
				//remove the GroupFigure from the drawing
				GroupFigure groupFigure = (GroupFigure) fe.nextFigure(); //Note:  isExecutableWithView() guarantees this cast
				getDrawingView().drawing().orphan(groupFigure);
				//get a copy of the contained figures to add to the drawing
				FigureEnumeration feToAdd = groupFigure.figures();
				//add the freshly removed figures to the Drawing and select them, and add them to affected figures
				while(feToAdd.hasNextFigure()){
					Figure f2 = feToAdd.nextFigure();
					//remove f2 from current container.
					groupFigure.orphan( f2 );
					//add f2 to drawing
					getDrawingView().drawing().add(f2);
					getDrawingView().addToSelection(f2);
					affectedFigures.add(f2);
				}
				//add the groupFigure, along with the figures it used to own to the map.
				groupFigureMap.put(groupFigure,new FigureEnumerator(affectedFigures));
			}
		}
		
		/**
		 * If undo was last their should be nothing to release, the keySet should be empty
		 */
		public void release() {
			//if we have group figures stored prepared for an undo/regroup type action, remove them
			Set keySet = groupFigureMap.keySet();
			for(Iterator it = keySet.iterator();it.hasNext();){
				//get the group figure
				GroupFigure gf = (GroupFigure) (GroupFigure) it.next();
				//get its former figures
				FigureEnumeration fe = (FigureEnumeration)groupFigureMap.remove( gf );
				//remove the figures it used to contain permanently from it
				gf.removeAll( fe );
				//Permanently remove the groupFigure from the drawing
				getDrawingView().drawing().remove( gf );
			}
			groupFigureMap = null; //so we throw NPE if this command is reexecuted after release (better would be JHDillegalstateException)
			setDrawingView(null);
			setAffectedFigures(FigureEnumerator.getEmptyEnumeration());
		}
	}
}
