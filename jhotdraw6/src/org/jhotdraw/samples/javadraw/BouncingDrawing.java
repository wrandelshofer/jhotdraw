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
	private HashMap decFigs = new HashMap();
	
	public void draw(Graphics g) {
		draw(g, getAnimatableFigures() );
	}
	public FigureEnumeration getAnimatableFigures() {
		return new FigureEnumerator(CollectionsFactory.current().createList(decFigs.values()));
	}
	
	public synchronized void add(Figure figure) {
		Figure decorFigure;
		if (!(figure instanceof AnimationDecorator) &&
			!(figure instanceof ConnectionFigure)) {
			decorFigure = new AnimationDecorator(figure);
			decFigs.put(figure, decorFigure);
		}
		else {
			decFigs.put(figure, figure);
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
	protected void figureRequestRemove(FigureChangeEvent e) {
		Figure f = e.getFigure();
		if(decFigs.containsKey( f )) {
			decFigs.remove(f);
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
		//FigureEnumeration fe = figures();
		FigureEnumeration fe = getAnimatableFigures();
		while (fe.hasNextFigure()) {
			Figure f = fe.nextFigure();
			
			if(!(f instanceof ConnectionFigure)) {
				((AnimationDecorator) f).animationStep(); // seems like a rather assuming cast !!!dnoyeb!!!
			}
		}
	}
}
