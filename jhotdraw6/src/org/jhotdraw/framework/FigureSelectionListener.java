/*
 * @(#)FigureSelectionListener.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

/**
 * Listener interested in DrawingView selection changes.
 * This event is fired by the {@link DrawingView DrawingView}.
 * Yes I know, I'm in violation for extending java.util.EventListener.  I'm 
 * too lazy to write my own event dispatcher right now so I'm using swings...
 *
 * @version <$CURRENT_VERSION$>
 */
public interface FigureSelectionListener extends java.util.EventListener{
	/**
	 * Sent when the figure selection has changed.
	 * @param view DrawingView
	 */
	public void figureSelectionChanged(DrawingView view);
}
