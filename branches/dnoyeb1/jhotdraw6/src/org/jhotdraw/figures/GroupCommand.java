/*
 * @(#)GroupCommand.java
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
import java.util.*;
import CH.ifa.draw.util.CollectionsFactory;
import java.util.List;

/**
 * Command to group the selection into a GroupFigure.
 *
 * @see GroupFigure
 *
 * @version <$CURRENT_VERSION$>
 */
public  class GroupCommand extends AbstractCommand {

   /**
	 * Constructs a group command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public GroupCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		getUndoActivity().setAffectedFigures(view().selection());
		((GroupCommand.UndoActivity)getUndoActivity()).groupFigures();
		view().drawing().update();
	}

	public boolean isExecutableWithView() {
		return view().selectionCount() > 1;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new GroupCommand.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter {
		private GroupFigure fGroupFigure=null;
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			setUndoable(true);
			setRedoable(true);
		}
		protected void setGroupFigure(GroupFigure figure){
			fGroupFigure = figure;
		}
		protected GroupFigure getGroupFigure(){
			return fGroupFigure;
		}
		public boolean undo() {
			if (!super.undo()) {
				return false;
			}
			getDrawingView().clearSelection();
			List affectedFigures = CollectionsFactory.current().createList();
			// orphan group figure
			getDrawingView().drawing().orphan(getGroupFigure());
			//get the figures from within the group figure.
			FigureEnumeration feToAdd = getGroupFigure().figures();
			while(feToAdd.hasNextFigure()){
				Figure addFig = feToAdd.nextFigure();
				//orphan figure from group figure
				getGroupFigure().orphan(addFig);
				//remove figure permanently from group figure.  This is required
				//because we recreate the GroupFigure for the groupFigures action
				getGroupFigure().remove(addFig);	
				//restore figure to the drawing
				getDrawingView().drawing().add(addFig);
				//add figure to selection
				getDrawingView().addToSelection(addFig);
				//add figure to affected figures
				affectedFigures.add(addFig);
			}
			//destroy the group figure since upon redo we create a new one
			getDrawingView().drawing().remove( getGroupFigure() );
			setGroupFigure(null);
			//update affected figures for redo if necessary
			setAffectedFigures(new FigureEnumerator(affectedFigures));
			return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				groupFigures();
				return true;
			}
			return false;
		}
		/**
		 * Take selected figures and add to a GroupFigure
		 */
		public void groupFigures() {
			getDrawingView().clearSelection();
			//orphan all selected figures from the drawing
			getDrawingView().drawing().orphanAll(getAffectedFigures());
			FigureEnumeration fe = getAffectedFigures();
			// add new group figure instead
			GroupFigure group = new GroupFigure();
			//add all the orphaned figures to the group figure
			group.addAll(fe);
			//add group figure to the drawing
			getDrawingView().drawing().add(group);
			//add group figure to the selection
			getDrawingView().addToSelection(group);
			//store groupFigure
			setGroupFigure(group);
		}
		/**
		 * If undo was last their should be nothing to release
		 */
		public void release() {
			//1. if groupFigures/redo was our last action
				//permanently remove all figures that were grouped from the drawing
			//2. if undo undo was our last action and their are lots of figures in the drawing waiting to be regrouped by a redo
				//nothing to do since its permanently removed in the undo action
			
			if(getGroupFigure() != null){
				getDrawingView().drawing().removeAll(getAffectedFigures());
			}
			setDrawingView(null);
			setGroupFigure(null);
			setAffectedFigures(FigureEnumerator.getEmptyEnumeration());
		}
	}
}
