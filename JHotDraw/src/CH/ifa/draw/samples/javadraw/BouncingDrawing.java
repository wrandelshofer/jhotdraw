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
import java.util.*;
import CH.ifa.draw.util.*;
import java.awt.Graphics;

/**
 * @version <$CURRENT_VERSION$>
 */
public class BouncingDrawing extends StandardDrawing implements Animatable {
	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -8566272817418441758L;
	private int bouncingDrawingSerializedDataVersion = 1;
	private HashMap animManips = new HashMap();
	private HashMap orphanedAnimManips = new HashMap();
	
	/**
	 * NOTE: Everything added to a figure within the drawing must never be exposed
	 * outside of the drawing.  During remove this adornments must be stripped
	 * away!  Their states are not preservable.  For preservation you must add
	 * your modifications before the Figure is added to the drawing.
	 */
	public synchronized void add(Figure figure) {
		super.add(figure);

		if (/*!(figure instanceof AnimationDecorator) && */  //if figurestrategy does not include AnimationStrategy?
			!(figure instanceof ConnectionFigure)) {
			FigureManipulator fm = new AnimationManipulator();
			figure.addFigureManipulator( fm );
			animManips.put(figure, fm );
		}
	}

	protected void figureRequestRemove(FigureChangeEvent e) {
		Figure f = e.getFigure();
		if(animManips.containsKey( f )) {
			AnimationManipulator am = (AnimationManipulator)animManips.remove( f );
		}
		super.figureRequestRemove(e);
	}

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
		for(Iterator it= animManips.values().iterator();it.hasNext();){
			Animatable am = (Animatable)it.next();
			am.animationStep();
		}
	}
}
