package CH.ifa.draw.framework;
import CH.ifa.draw.framework.DrawingView;
import java.util.EventListener;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author      C.L.Gilbert <dnoyeb@users.sourceforge.net>
 * @version     1.0
 * @since       10/14/01
 */

 /**
  * This event is fired by the {@link DrawingEditor DrawingEditor}.
  * @see javax.swing.event.EventListenerList
  * @see CH.ifa.draw.framework.DrawingEditor
  */
public interface ViewChangeListener extends EventListener {

	/**
	 * Sent when the selected view has changed.
	 * @param newView The view being changed to.
	 * @param oldView The view being changed from.
	 */
	public void viewSelectionChanged(DrawingView oldView, DrawingView newView);
	
	/**
	 * Sent when a new view is created.  This should only be fired after the view
	 * has a peer.  I believe that means the view has a component chain all the way
	 * to a heavyweight container.
	 * @param view The <b>DrawingView</b> being activated.
	 */
	public void viewCreated(DrawingView view);

	/**
	 * Send when an existing view is about to be destroyed.  After processing this
	 * event, the view is not guaranteed to contain any appropriate data.  You
	 * must not use it.
	 * @param view The <b>DrawingView</b> being deactivated.
	 */
	public void viewDestroying(DrawingView view);
}
