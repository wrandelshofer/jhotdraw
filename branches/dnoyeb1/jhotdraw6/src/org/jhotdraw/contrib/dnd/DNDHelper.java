/*
 * @(#)DNDHelper.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
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
 * Changes made in hopes of eventually cleaning up the functionality and 
 * distributing it sensibly. 1/10/02
 * @author  C.L.Gilbert <dnoyeb@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public abstract class DNDHelper {
	public static DataFlavor ASCIIFlavor = new DataFlavor("text/plain; charset=ascii", "ASCII text");
	private DragGestureRecognizer dgr;
	private DragGestureListener dragGestureListener;
	private DropTarget dropTarget;
	private DragSourceListener dragSourceListener;
	private DropTargetListener dropTargetListener;
	
	public DNDHelper(){
	}
	/**
	 * Do not call this from the constructor.  its methods are overridable.
	 */
	public void initialize(DragGestureListener dgl) {
		if(isDragSource()) {
			setDragGestureListener( dgl );
			setDragSourceListener( createDragSourceListener() );
			setDragGestureRecognizer(createDragGestureRecognizer(getDragGestureListener()));
		}
		if(isDropTarget()) {
			setDropTargetListener( createDropTargetListener() );
			setDropTarget(createDropTarget());
		}
	}
	public void deinitialize(){
		if(getDragSourceListener() != null){
			destroyDragGestreRecognizer();
			setDragSourceListener( null );
		}
		if(getDropTargetListener() != null){
			setDropTarget( null );
			setDropTargetListener( null );
		}
	}
	public void setDragSourceState(boolean state) {
		if(state == false){
			getDragGestureRecognizer().setSourceActions(DnDConstants.ACTION_NONE);
		}
		else {
			getDragGestureRecognizer().setSourceActions(getDragSourceActions());
		}
	}
	abstract protected DrawingView view();
	abstract protected DrawingEditor editor();
	abstract protected boolean isDragSource();
	abstract protected boolean isDropTarget();

	
	
	
	protected static Object ProcessReceivedData(DataFlavor flavor, Transferable transferable) {
		if (transferable == null) {
			return null;
		}
		try {
		    if (flavor.equals(DataFlavor.stringFlavor)) {
				String str = (String) transferable.getTransferData(DataFlavor.stringFlavor);
				return str;
			}
			else if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				java.util.List aList = (java.util.List)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				File fList [] = new File[aList.size()];
				aList.toArray(fList);
				return fList;
			}
			else if (flavor.equals(ASCIIFlavor)) {
				String txt = null;
				/* this may be too much work for locally received data */
				InputStream is = (InputStream)transferable.getTransferData(ASCIIFlavor);
				int length = is.available();
				byte[] bytes = new byte[length];
				int n = is.read(bytes);
				if (n > 0) {
					/* seems to be a 0 tacked on the end of Windows strings.  I
					 * havent checked other platforms.  This does not happen
					 * with windows socket io.  strange?
					 */
					//for (int i = 0; i < length; i++) {
					//    if (bytes[i] == 0) {
					//        length = i;
					//        break;
					//    }
					//}
					txt = new String(bytes, 0, n);
				}
				return txt;
			}
			else if (flavor.equals(DNDFiguresTransferable.DNDFiguresFlavor)) {
				DNDFigures ff = (DNDFigures) transferable.getTransferData(DNDFiguresTransferable.DNDFiguresFlavor);
				return ff;
			}
			else {
				return null;
			}
		}
		catch (java.io.IOException ioe) {
			System.err.println(ioe);
			return null;
		}
		catch (UnsupportedFlavorException ufe) {
			System.err.println(ufe);
			return null;
		}
		catch (ClassCastException cce) {
			System.err.println(cce);
			return null;
		}
	}
	protected int getDragSourceActions() {
		return DnDConstants.ACTION_COPY_OR_MOVE;
	}
	protected void setDragGestureListener(DragGestureListener dragGestureListener){
		this.dragGestureListener = dragGestureListener;
	}
	protected DragGestureListener getDragGestureListener(){
		return dragGestureListener;
	}
	protected void setDragGestureRecognizer(DragGestureRecognizer dragGestureRecognizer){
		dgr = dragGestureRecognizer;
	}
	protected DragGestureRecognizer getDragGestureRecognizer(){
		return dgr;
	}
	protected void setDropTarget(DropTarget dropTarget){
		if((dropTarget == null) && (this.dropTarget != null)){
			this.dropTarget.setComponent(null);
			this.dropTarget.removeDropTargetListener( getDropTargetListener() );
		}
		this.dropTarget = dropTarget;
	}
	protected DropTarget createDropTarget() {
		DropTarget dt = null;
		if (view() instanceof Component) {
			try {
				dt = new DropTarget((Component)view(), DnDConstants.ACTION_COPY_OR_MOVE, getDropTargetListener());
				System.out.println(view().toString() + " Initialized to DND.");
			}
			catch (java.lang.NullPointerException npe) {
				System.err.println("View Failed to initialize to DND.");
				System.err.println("Container likely did not have peer before the DropTarget was added");
				System.err.println(npe);
				npe.printStackTrace();
			}
		}
		return dt;
	}

	/**
	 * Used to create the gesture recognizer which in effect turns on draggability.
	 */
	protected DragGestureRecognizer createDragGestureRecognizer(DragGestureListener dgl) {
		DragGestureRecognizer aDgr = null;
		if (view() instanceof Component) {
			Component c = (Component)view();
			aDgr =	DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
					c,
					getDragSourceActions(),
					dgl);
			System.out.println("DragGestureRecognizer created: " + view());
		}
		return aDgr;
	}

	/**
	 * Used to destroy the gesture listener which in effect turns off dragability.
	 */
	protected void destroyDragGestreRecognizer() {
		System.out.println("Destroying DGR " + view());
		if (getDragGestureRecognizer() != null) {
			getDragGestureRecognizer().removeDragGestureListener(getDragGestureListener());
	    	getDragGestureRecognizer().setComponent(null);
			setDragGestureRecognizer(null);
		}
	}

	
	protected void setDropTargetListener(DropTargetListener dropTargetListener){
		this.dropTargetListener = dropTargetListener;
	}
	protected DropTargetListener getDropTargetListener(){
		return dropTargetListener;
	}
	protected DropTargetListener createDropTargetListener(){
		return new JHDDropTargetListener(editor(),view());
	}
	public DragSourceListener getDragSourceListener(){
		return dragSourceListener;
	}
	protected void setDragSourceListener(DragSourceListener dragSourceListener){
		this.dragSourceListener = dragSourceListener;
	}
	protected DragSourceListener createDragSourceListener(){
		return new JHDDragSourceListener(editor(),view());
	}
}
	/**
	 * These transferable objects are used to package your data when you want
	 * to initiate a transfer.  They are not used when you only want to receive
	 * data.  Formating the data is the responsibility of the sender primarily.
	 * Untested.  Used for dragging ASCII text out of JHotDraw
	 */
/*	public class ASCIIText implements Transferable
	{
		String s = new String("This is ASCII text");
		byte[] bytes;

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { ASCIIFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
			return dataFlavor.equals(ASCIIFlavor);
		}

		public Object getTransferData(DataFlavor dataFlavor)
			throws UnsupportedFlavorException, IOException  {
			if (!isDataFlavorSupported(dataFlavor))
						throw new UnsupportedFlavorException(dataFlavor);

			bytes = new byte[s.length() + 1];
			for (int i = 0; i < s.length(); i++)
				bytes = s.getBytes();
			bytes[s.length()] = 0;
			return new ByteArrayInputStream(bytes);
		}
	}*/