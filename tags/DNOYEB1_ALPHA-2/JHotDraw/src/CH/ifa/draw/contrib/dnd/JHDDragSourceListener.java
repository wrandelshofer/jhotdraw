/*
 * JHDDragSource.java
 *
 * Created on January 28, 2003, 4:49 PM
 */

package CH.ifa.draw.contrib.dnd;
import java.awt.dnd.*;
import java.awt.*;
import java.util.*;
import CH.ifa.draw.framework.*;
import java.awt.datatransfer.*;
import java.io.*;
import javax.swing.JComponent;
import CH.ifa.draw.standard.DeleteFromDrawingVisitor;

import CH.ifa.draw.util.*;
/**
 *
 * @author  Administrator
 */
public class JHDDragSourceListener implements DragSourceListener {
	private Undoable sourceUndoable;
	private Boolean autoscrollState;
//	private DrawingView dv;
	private DrawingEditor editor;
	
	/** Creates a new instance of JHDDragSource */
	public JHDDragSourceListener(DrawingEditor editor, DrawingView view) {
		this.editor = editor;
	}
//	protected DrawingView view(){
//		return dv;
//	}
	protected DrawingEditor editor(){
		return editor;
	}
	/**
	 * This method is invoked to signify that the Drag and Drop operation is complete.
	 * This is the last method called in the process.
	 */
	public void dragDropEnd(DragSourceDropEvent dsde) {
		DrawingView view = (DrawingView) dsde.getDragSourceContext().getComponent();
		log("DragSourceDropEvent-dragDropEnd");
		if (dsde.getDropSuccess() == true) {
			if (dsde.getDropAction() == DnDConstants.ACTION_MOVE) {
                log("DragSourceDropEvent-ACTION_MOVE");
				//get the flavor in order of ease of use here.
				setSourceUndoActivity(  createSourceUndoActivity( view ) );
				DNDFigures df = (DNDFigures)DNDHelper.ProcessReceivedData(DNDFiguresTransferable.DNDFiguresFlavor, dsde.getDragSourceContext().getTransferable());
				getSourceUndoActivity().setAffectedFigures( df.getFigures() );

				//all this visitation needs to be hidden in a view method.
				DeleteFromDrawingVisitor deleteVisitor = new DeleteFromDrawingVisitor(view.drawing());
				FigureEnumeration fe = getSourceUndoActivity().getAffectedFigures();
				while (fe.hasNextFigure()) {
					fe.nextFigure().visit(deleteVisitor);
				}
				view.clearSelection();
				view.drawing().update();//we made a change to the drawing, so update it.

				editor().getUndoManager().pushUndo( getSourceUndoActivity() );
				editor().getUndoManager().clearRedos();
				// update menus
				editor().figureSelectionChanged( view );
			}
			else if (dsde.getDropAction() == DnDConstants.ACTION_COPY) {
				log("DragSourceDropEvent-ACTION_COPY not implemented?");
			}
		}

		if (autoscrollState != null) {
			Component c = dsde.getDragSourceContext().getComponent();
			if (JComponent.class.isInstance( c )) {
				JComponent jc = (JComponent)c;
				jc.setAutoscrolls(autoscrollState.booleanValue());
				autoscrollState= null;
			}
		}
	}
	/**
	 * Called as the hotspot enters a platform dependent drop site.
	 */
	public void dragEnter(DragSourceDragEvent dsde) {
		log("DragSourceDragEvent-dragEnter");
		if (autoscrollState == null) {
			Component c = dsde.getDragSourceContext().getComponent();
			if (JComponent.class.isInstance( c )) {
				JComponent jc = (JComponent)c;
				autoscrollState= new Boolean(jc.getAutoscrolls());
				jc.setAutoscrolls(false);
			}
		}

//		dsde.getDragSourceContext().
	}
	/**
	 * Called as the hotspot exits a platform dependent drop site.
	 */
	public void dragExit(DragSourceEvent dse) {
	}
	/**
	 * Called as the hotspot moves over a platform dependent drop site.
	 */
	public void dragOver(DragSourceDragEvent dsde) {
		log("DragSourceDragEvent-dragOver");
	}
	/**
	 * Called when the user has modified the drop gesture.
	 */
	public void dropActionChanged(DragSourceDragEvent dsde) {
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Factory method for undo activity
	 */
	protected Undoable createSourceUndoActivity(DrawingView drawingView) {
		return new RemoveUndoActivity( drawingView );
	}
	protected void setSourceUndoActivity(Undoable undoable){
		sourceUndoable = undoable;
	}
	protected Undoable getSourceUndoActivity(){
		return sourceUndoable;
	}
	public static class RemoveUndoActivity extends UndoableAdapter {
		public RemoveUndoActivity(DrawingView view) {
			super( view );
			log("RemoveUndoActivity created " + view);
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (super.undo() && getAffectedFigures().hasNextFigure()) {
				log("RemoveUndoActivity undo");
				getDrawingView().clearSelection();
				setAffectedFigures( getDrawingView().insertFigures(getAffectedFigures(), 0, 0,false));
				return true;
			}
			return false;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				log("RemoveUndoActivity redo");
				DeleteFromDrawingVisitor deleteVisitor = new DeleteFromDrawingVisitor( getDrawingView().drawing());
				FigureEnumeration fe = getAffectedFigures();
				while (fe.hasNextFigure()) {
					fe.nextFigure().visit(deleteVisitor);
				}
				getDrawingView().clearSelection();
				setAffectedFigures( deleteVisitor.getDeletedFigures() );
				return true;
			}
			return false;
		}
		/**
		 * Releases all resources related to an undoable activity
		 * @todo investigate if this release is proper.
		 */
		public void release() {
			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				fe.nextFigure().release();
			}
			setAffectedFigures(CH.ifa.draw.standard.FigureEnumerator.getEmptyEnumeration());
		}		
	}
	
	
	
	
	
	
	
	private static void log(String message){
		//System.out.println("JHDDragSourceListener: " + message);
	}
	
	
	
	
	
	
	
}
