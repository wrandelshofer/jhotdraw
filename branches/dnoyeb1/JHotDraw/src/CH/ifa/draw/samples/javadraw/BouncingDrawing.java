/*
 * @(#)BouncingDrawing.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.javadraw;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.Animatable;
/**
 * @todo Needs validation and testing.  The DecoratorFigure AnimationDecorator
 *       causes some problems with knowing what is and is not in the drawing.
 *
 * <b>NOTE: I attempted to hide the internal DecoratorFigures that bouncing
 * Drawing uses, but unfortunately the current lack of document-view seperation
 * causes problems with selection and other things.  Normally when you select a figure
 * with a tool, it will be a AnimationDecorator, but we are supposed to hide that.
 * so selections will be the the mapped figure.  Its a problem because  internally
 * the drawing needs to use the animation decorator to draw it.  Its difficuly
 * to decide which methods should expose the real figure, and which should expose the
 * decorator.  In the end I think the decision will lead to a form of 
 * document-view seperation anyway.  So I am not fixing these problems, and
 * will tend to them when the document view seperation occurs.
 * If you are having issues, try using <code>StandardDrawing</code> instead.
 *
 * @version <$CURRENT_VERSION$>
 *  @deprecated Completely does not work in dnoyeb1  no way to hide the decorations
 *              which is required.  I have quit trying to fix it for now.  I think
 *              it can probably return with document-view seperation.
 */
public class BouncingDrawing extends StandardDrawing implements Animatable {
	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -8566272817418441758L;
	private int bouncingDrawingSerializedDataVersion = 1;

	public synchronized void add(Figure figure) {
		if (!(figure instanceof AnimationDecorator) &&
			!(figure instanceof ConnectionFigure)) {
			figure = new AnimationDecorator(figure);
		}
		super.add(figure);
	}

//	public synchronized void remove(Figure figure) {
//		Figure f = super.remove(figure);
//		if (f instanceof AnimationDecorator) {
//			return ((AnimationDecorator) f).peelDecoration();
//		}
//		return f;
//	}

	/**
	 * @param figure figure to be replaced
	 * @param replacement figure that should replace the specified figure
	 * @return the figure that has been inserted (might be different from the figure specified)
	 */
	public synchronized Figure replace(Figure figure, Figure replacement) {
		if (!(replacement instanceof AnimationDecorator) &&
			!(replacement instanceof ConnectionFigure)) {
			replacement = new AnimationDecorator(replacement);
		}
		return super.replace(figure, replacement);
	}

	public void animationStep() {
		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			Figure f = fe.nextFigure();
			
			if(!(f instanceof ConnectionFigure)) {
				((AnimationDecorator) f).animationStep();
			}
		}
	}
		}
