/*
 * @(#)DeleteCommand.java
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
import CH.ifa.draw.util.Undoable;
import CH.ifa.draw.util.UndoableAdapter;

/**
 * Command to delete the selection.
 *
 * @version <$CURRENT_VERSION$>
 */
public class DeleteCommand extends FigureTransferCommand {

   /**
	* Constructs a delete command.
	* @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	*/
	public DeleteCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		getUndoActivity().setAffectedFigures( deleteFigures( view().selection() ));
		view().drawing().update();
	}

	protected boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new DeleteCommand.UndoActivity(this);
	}

	public static class UndoActivity extends UndoableAdapter {
		private FigureTransferCommand myCommand;
		private boolean undone = false;
		public UndoActivity(FigureTransferCommand newCommand) {
			super(newCommand.view());
			myCommand = newCommand;
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (super.undo() && getAffectedFigures().hasNextFigure()) {
				getDrawingView().clearSelection();
				setAffectedFigures(myCommand.insertFigures(getAffectedFigures(), 0, 0));
				undone = true;
				return true;
			}

			return false;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				myCommand.deleteFigures(getAffectedFigures());
				getDrawingView().clearSelection();
				undone = false;
				return true;
			}

			return false;
		}
		/**
		 * Releases all resources related to an undoable activity
		 * Since cut duplicates the figures, it is ok to release the originals
		 * because the pasted figures are completely new objects.
		 * But we can only release if the action has not been undone.
		 */
		public void release() {
			if(undone == false){
				FigureEnumeration fe = getAffectedFigures();
				while (fe.hasNextFigure()) {
					Figure f = fe.nextFigure();
					getDrawingView().drawing().remove(f);
					f.release();
				}
			}
			super.release();
		}		
	}
}
