/*
 * @(#)Desktop.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.DrawingView;

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public interface Desktop {
	public final static int PRIMARY = 0;
	public final static int SECONDARY = 1;
	public final static int TERTIARY = 2;

	/**
	 *  Use this to get what the <code>Desktop</code> considers to be the active
	 *  <code>DrawingView</code>.
	 */
	public DrawingView getActiveDrawingView();
	/**
	 * Use this to add a <code>DrawingView</code> to the <code>Desktop</code>.
	 * If you are using nested <code>Desktop</code>s, use the id to specify
	 * the correct one.
	 */
	public void addToDesktop(DrawingView dv, int id);
	
	/**
	 * Use this to remove a <code>DrawingView</code> from the <code>Desktop</code>.
	 * If you are using nested <code>Desktop</code>s, use the id to specify which
	 * one.
	 */
	public void removeFromDesktop(DrawingView dv, int id);
	
	/**
	 * Empties the desktop of all <code>DrawingView</code>s. If you are using 
	 * nested <code>Desktop</code>s, use the id to specify the target.
	 */
	public void removeAllFromDesktop(int id);
	
	/**
	 * Returns all <code>DrawingView</code>s from the desktop. If you are using 
	 * nested <code>Desktop</code>s, use the id to specify the target.
	 */
	public DrawingView[] getAllFromDesktop(int id);

    /**
     *  desktops must listen to drawings for name changes if they have 
     *  titles that need updating.
     *
     * @deprecated
     */
	public void updateTitle(String newDrawingTitle);

	/**
	 * This is just like CompositeFigure.add()  desktops can be contained in other desktops.
	 */
	public void addDesktopListener(DesktopListener dpl);
	public void removeDesktopListener(DesktopListener dpl);
}