/*
 * @(#)FigureVisitor.java
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
 * 1.  Visit all figures reachable from the hostFigure.  Contained figures and
 *     dependent figures.  Search the complete tree of figures reachable through
 *     either the dependent figures, or the contained figures.
 * 2.  Delete all figures directly contained in the Drawing. (e.g.<code>containsFigure()</code>)
 * 3.  Return a list of all deleted figures.
 *
 * You must visit figures that are dependent upon figures dependent upon the hostFigure.
 * Full tree search of dependent figures is necessary.
 *
 * dependent figures does not dictate drawing order, but currently the order of
 * readd to the drawing is dependent upon when a figure gets visited.  And this
 * has the effect of dictating drawing order.  Z needs work.
 *
 *
 * Note: do not delete figures contained in other figures, only those directly
 * contained in the drawing.
 *
 *
 *1
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public interface FigureVisitor {
	public void visitFigure(Figure hostFigure);
	public void visitHandle(Handle hostHandle);
	public void visitFigureChangeListener(FigureChangeListener hostFigureChangeListener);
}
