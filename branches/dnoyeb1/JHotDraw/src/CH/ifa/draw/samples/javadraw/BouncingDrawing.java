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
import CH.ifa.draw.figures.TextFigure;
import java.io.IOException;
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
 * 
 */
public class BouncingDrawing extends StandardDrawing implements Animatable {
	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -8566272817418441758L;
	private int bouncingDrawingSerializedDataVersion = 1;
//	private HashMap animManips;
//	private HashMap orphanedAnimManips;
	private HashMap decorations;
	
	BouncingDrawing(){
//		decorations = new HashMap();
//		animManips = new HashMap();
//		orphanedAnimManips = new HashMap();
	}
	/**
	 *
	 */
	public synchronized void add(Figure figure) {
		if (!(figure instanceof AnimationDecorator) &&
			!(figure instanceof ConnectionFigure)) {
			Figure ad = new AnimationDecorator(figure);
//			decorations.put( figure,  ad);
			super.add(ad);
		}
		else {
			super.add(figure);
		}
		
//		super.add(figure);
//		if (
//			!(figure instanceof ConnectionFigure) &&
//			!(figure instanceof TextFigure) //Hack to keep Connected test from flying all over (stops unconnected text too though...)
//			) {
//			FigureManipulator fm = new AnimationManipulator();
//			figure.addFigureManipulator( fm );
//			animManips.put(figure, fm );
//		}
	}

	/**
	 * Peeling will make undo difficult.  The whole undo system cant handle the
	 * DecoratorFigures anyway so expect problems.
	 */
	protected void basicOrphan(Figure figure){
//		if(figure instanceof AnimationDecorator){
//			((AnimationDecorator)figure).peelDecoration();
//		}
//		if(decorations.containsKey( figure )){
//			decorations.remove( figure );
//		}
		super.basicOrphan(figure);
	}
//	protected void figureRequestRemove(FigureChangeEvent e) {
//		Figure f = e.getFigure();
//		if(animManips.containsKey( f )) {
//			AnimationManipulator am = (AnimationManipulator)animManips.remove( f );
//		}
//		super.figureRequestRemove(e);
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
		FigureEnumeration fe = new FigureEnumerator(getFigures());
		while (fe.hasNextFigure()) {
			Figure f = fe.nextFigure();

			if(!(f instanceof ConnectionFigure)) {
				((AnimationDecorator) f).animationStep();
			}
		}
//		for(Iterator it= animManips.values().iterator();it.hasNext();){
//			Animatable am = (Animatable)it.next();
//			am.animationStep();
//		}
	}
	public void write(StorableOutput dw) {
		super.write(dw);
		//store mappings
		dw.writeInt( decorations.size() );
		Iterator it = decorations.keySet().iterator();
		while (it.hasNext()) {
			Figure key = (Figure) it.next();
			Figure value = (Figure) decorations.get( key );
			dw.writeStorable( key );
			dw.writeStorable( value );
		}
	}
	
	/**
	 * Reads the contained figures from StorableInput.
	 * The loading process is assumed to be serial and not in need of synchronization.
	 */
	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		//load mappings
//		int size = dr.readInt();
//		decorations = new HashMap( size );
//		for (int i=0; i<size; i++) {
//			Figure key = (Figure) dr.readStorable();
//			Figure value = (Figure) dr.readStorable();
//			decorations.put( key, value);
//		}
	}
//	/**
//	 * Returns all the figures minus the decorations added in the add method
//	 */
//	public FigureEnumeration figures() {
//		List figs = CollectionsFactory.current().createList(decorations.size());
//		figs.addAll( decorations.keySet() );
//		
//		FigureEnumeration fe = new FigureEnumerator(CollectionsFactory.current().createList(getFigures()));
//		while(fe.hasNextFigure()){
//			Figure f = fe.nextFigure();
//			if(!decorations.containsValue( f )){
//				figs.add( f );
//			}
//		}
//		return new FigureEnumerator(CollectionsFactory.current().createList(figs));
//	}
}
