/*
 * @(#)DrawingChangeListener.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

import java.util.EventListener;

/**
 * Listener interested in Drawing changes.
 *
 * @version <$CURRENT_VERSION$>
 */
public interface DrawingChangeListener extends EventListener {

	/**
	 *  Sent when an area is invalid
	 */
	public void drawingInvalidated(DrawingChangeEvent e);
    /**
     *  Sent when the drawing Title has changed
	 *  investigate seperating this event out because the other 2 occur often
	 *  and you end up listening to events frequently, that you are not 
	 *  interested in.
	 *  Likely titles should be simple properties and stored in the attributes.
	 *  Then we add an attribute listener, and voila.
     */
    public void drawingTitleChanged(DrawingChangeEvent e);
	/**
	 *  Sent when the drawing wants to be refreshed
	 */
	public void drawingRequestUpdate(DrawingChangeEvent e);
}
