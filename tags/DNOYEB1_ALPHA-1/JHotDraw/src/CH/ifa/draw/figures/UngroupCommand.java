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
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (!super.undo()) {
				return false;
			}
			getDrawingView().clearSelection();

			FigureEnumeration groupFigures = getAffectedFigures();
			while (groupFigures.hasNextFigure()) {
				Figure groupFigure = groupFigures.nextFigure();
				// orphan individual figures from the group
				getDrawingView().drawing().orphanAll(groupFigure.figures());
				//This tool is now responsible for the release or readd of the figures it just orphaned
				//!!!dnoyeb!!!
				Figure figure = getDrawingView().drawing().add(groupFigure);
				getDrawingView().addToSelection(figure);
			}

			return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				getDrawingView().drawing().orphanAll(getAffectedFigures());
				//this tool is now responsible for the release or readd of the figures it orphaned
				//!!!dnoyeb!!!
				getDrawingView().clearSelection();
				ungroupFigures();
				return true;
			}
			return false;
		}

		/**
		 * @todo fix this because components can not belong to more than 1 figure.
		 */
		protected void ungroupFigures() {
			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				Figure selected = fe.nextFigure();
				getDrawingView().drawing().orphan(selected);
				//this tool is now responsible for the release or readd of the figure it orphaned
				//!!!dnoyeb!!!
				FigureEnumeration feToRemove = selected.figures();
				FigureEnumeration feToAdd = selected.figures();
				FigureEnumeration feToSelect = selected.figures();
				if(selected instanceof CompositeFigure){
					((CompositeFigure)selected).orphanAll( feToRemove );
				}
				getDrawingView().drawing().addAll(feToAdd);
				getDrawingView().addToSelectionAll(feToSelect);
			}
		}
	}
}
