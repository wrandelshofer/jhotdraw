/*
 * @(#)DrawingEditor.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;
import CH.ifa.draw.contrib.Desktop;
import CH.ifa.draw.util.UndoManager;
import java.awt.*;

/**
 * DrawingEditor defines the interface for coordinating
 * the different objects that participate in a drawing editor.
 * It does things like holds the currently selected Tool.  Holds the currently
 * selected color and pen size. (i think-dnoyeb)
 *
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld022.htm>Mediator</a></b><br>
 * DrawingEditor is the mediator. It decouples the participants
 * of a drawing editor.
 *
 * @see Tool
 * @see DrawingView
 * @see Drawing
 *
 * @version <$CURRENT_VERSION$>
 */
public interface DrawingEditor extends FigureSelectionListener {

	/**
	 * Gets the application <b>Desktop</b>.
	 */
	public Desktop getDesktop();
	/**
	 * Gets the editor's drawing view.
	 * @deprecated use {@link #getDesktop() getDesktop().getActiveDrawingView()}
	 *  instead.
	 */
	public DrawingView view();

	/**
	 * @deprecated use {@link #getDesktop() getDesktop()} instead.
	 */
	public DrawingView[] views();

	/**
	 * Gets the editor's current tool.
	 */
	public Tool tool();

	/**
	 * Informs the editor that a tool has done its interaction.
	 * This method can be used to switch back to the default tool.
	 */
	public void toolDone();

	/**
	 * Called when the current figure selection has changed.
	 * Override this method to handle selection changes.
	 * @todo we need to seperate the listener interface from those that manually
	 *       call this method by adding a new method of nearly the same name?
	 */
	public void figureSelectionChanged(DrawingView view);
	/**
	 * Figure selection events are sent when the figure selection of the active
	 * {@link DrawingView DrawingView} has changed.  It is guaranteed never to
	 * be sent from inactive {@link DrawingView DrawingView}s.  Specific {@link
	 * DrawingView DrawingView}s can also be registered with.  Active Tools may 
	 * prefer to get their Figure selection events from the {@link DrawingView
	 * DrawingView} they work on.  But this should not be necessary.  Honestly, 
	 * I would prefer if the editor had its own listener and did not reuse 
	 * <b>FigureSelectionListener</b> eventhough its functionality is the same.
	 */
	public void addFigureSelectionListener(FigureSelectionListener fsl);
	public void removeFigureSelectionListener(FigureSelectionListener fsl);
	public void addViewChangeListener(ViewChangeListener vsl);
	public void removeViewChangeListener(ViewChangeListener vsl);

	/**
	 * Shows a status message in the editor's user interface
	 */
	public void showStatus(String string);

	public UndoManager getUndoManager();
}
