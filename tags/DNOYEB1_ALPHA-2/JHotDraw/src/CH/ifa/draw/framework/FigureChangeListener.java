/*
 * @(#)FigureChangeListener.java
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
 * Listener interested in Figure changes.
 *
 * @version <$CURRENT_VERSION$>
 */
public interface FigureChangeListener extends EventListener {

	/**
	 * Sent when the whole figure, or a portion of it is not invalid.  This is
	 * an indication that the figure needs to be redrawn.
	 */
	public void figureInvalidated(FigureChangeEvent e);

	/**
	 * Sent when a figure changed
	 * Should be fired when shape / layout has changed
	 * This is different from JFC/Swing terminology in which this would be called invalidate.
	 * Here invalidate means a portion is in need of repaint.
	 * A composite figure would fire this after it has removed one of its containees
	 */
	public void figureChanged(FigureChangeEvent e);

	/**
	 * Sent when the figure requests to be redrawn immediately.
	 * This is of primary use to figures that change themselves without outside
	 * intervention.  An example of that would be an animated figure where the
	 * engine for change is within the figure.  In this case when the figure's 
	 * displayed image changes those that contain it need to know so they can
	 * redraw it immediately.  Typically the drawing will listen for this event
	 * and upon receiving it, notify all of its DrawingViews of the area in need
	 * of redraw.  The area to be redrawn is within a <code>figureIntalidated
	 * </code> event.
	 * This is not the case for {@link CH.ifa.draw.samples.javadraw.AnimationDecorator
	 * AnimationDecorator} since it is manipulated by an external {@link 
	 * CH.ifa.draw.samples.javadraw.Animator Animator} that calls {@link
	 * CH.ifa.draw.framework.DrawingView#checkDamage checkDamage()} for it.
	 *
	 * @see #figureInvalidated
	 * @see DrawingChangeListener#drawingRequestUpdate
	 */
	public void figureRequestUpdate(FigureChangeEvent e);
}
