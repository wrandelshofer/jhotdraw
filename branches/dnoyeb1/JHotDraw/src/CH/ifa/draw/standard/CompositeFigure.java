/*
 * @(#)CompositeFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.io.*;

/**
 * A Figure that is composed of several figures. A CompositeFigure
 * doesn't define any layout behavior. It is up to subclassers to
 * arrange the contained figures.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld012.htm>Composite</a></b><br>
 * CompositeFigure enables to treat a composition of figures like
 * a single figure.<br>
 *
 * Orphan removes a figure but does not release it.  We need a description of what that means
 * and what the implications of that are.
 *
 *
 * @see Figure
 *
 * @version <$CURRENT_VERSION$>
 */

public abstract class CompositeFigure extends AbstractFigure {

	/**
	 * The figures that this figure is composed of
	 * @see #add
	 * @see #remove
	 */
	protected List fFigures;

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 7408153435700021866L;
	private int compositeFigureSerializedDataVersion = 1;
	private transient QuadTree  _theQuadTree;
	protected int _nLowestZ;
	protected int _nHighestZ;
	
	/**
	 * Encapsulate the FigureChangeListener implementation
	 */
	
	private FigureChangeListener figureChangeListener = new innerFigureChangeListener();

	private class innerFigureChangeListener implements FigureChangeListener, java.io.Serializable, Cloneable {
		public void figureInvalidated(FigureChangeEvent e){
			CompositeFigure.this.figureInvalidated(e);
		}
		public void figureChanged(FigureChangeEvent e){
			CompositeFigure.this.figureChanged(e);
		}
		public void figureRemoved(FigureChangeEvent e){
			CompositeFigure.this.figureRemoved(e);
		}
		public void figureRequestRemove(FigureChangeEvent e){
			CompositeFigure.this.figureRequestRemove(e);
		}
		public void figureRequestUpdate(FigureChangeEvent e){
			CompositeFigure.this.figureRequestUpdate(e);
		}
	};

	protected CompositeFigure() {
		fFigures = CollectionsFactory.current().createList();
		_nLowestZ = 0;
		_nHighestZ = 0;
	}
	
	/**
	 * Adds a figure to the list of figures. Initializes the
	 * the figure's container.
	 *
	 * @param figure to be added to the drawing
	 * @return the figure that was inserted (might be different from the figure specified).
	 */
	public Figure add(Figure figure) {
		if (!containsFigure(figure)) {
			figure.setZValue(++_nHighestZ);
			fFigures.add(figure);
			figure.addToContainer(figureChangeListener);
			_addToQuadTree(figure);
		}
		return figure;
	}

	/**
	 * Adds a list of figures.
	 *
	 * @see #add
	 * @deprecated use {@link #addAll(FigureEnumeration) 
	 * addAll(FigureEnumeration fe)} instead.
	 */
	public void addAll(List newFigures) {
		addAll(new FigureEnumerator(newFigures));
	}

	/**
	 * Adds a FigureEnumeration of figures.
	 *
	 * @see #add
	 * @param fe An enumeration containing all figures to be added.
	 */
	public void addAll(FigureEnumeration fe) {
		while (fe.hasNextFigure()) {
			add(fe.nextFigure());
		}
	}

	/**
	 * Removes a figure from the composite.
	 *
	 * @param figure that is part of the drawing and should be removed
	 * @return the figure that has been removed (might be different from the figure specified)
	 * @see #removeAll
	 */
	public Figure remove(Figure figure) {
		Figure orphanedFigure = orphan(figure);
		if (orphanedFigure != null) {
			orphanedFigure.release();
		}
		return orphanedFigure;
	}

	/**
	 * Removes a list of figures.
	 *
	 * @see #remove
	 * @deprecated use {@link #removeAll(FigureEnumeration) 
	 *             removeAll(FigureEnumeration fe)} instead.
	 */
	public void removeAll(List figures) {
		removeAll(new FigureEnumerator(figures));
	}

	/**
	 * Removes a <b>FigureEnumeration</b> of figures.
	 * @see #remove
	 */
	public void removeAll(FigureEnumeration fe) {
		while (fe.hasNextFigure()) {
			remove(fe.nextFigure());
		}
		_clearQuadTree();
		_nLowestZ = 0;
		_nHighestZ = 0;
	}

	/**
	 * Removes all contained figures.
	 * @see #remove
	 */
	public void removeAll() {
		FigureEnumeration fe = figures();
		removeAll(fe);
//		fFigures.clear();
	}

	/**
	 * Removes a figure from the figure list, but
	 * doesn't release it. Use this method to temporarily
	 * manipulate a figure outside of the drawing.
	 *
	 * I think this is a good place to throw a runtime exception if the Figure
	 * is not part of the drawing, dnoyeb!!! BadParameterException
	 *
	 * @param figure that is part of the drawing and should be added
	 */
	public synchronized Figure orphan(Figure figure) {
		figure.removeFromContainer(figureChangeListener);
		fFigures.remove(figure);
		_removeFromQuadTree(figure);
		return figure;
	}

	/**
	 * Removes a list of figures from the figure's list
	 * without releasing the figures.
	 *
	 * @see #orphan
	 * @deprecated use {@link #orphanAll(FigureEnumeration)
	 *             orphanAll(FigureEnumeration fe)} instead
	 */
	public void orphanAll(List newFigures) {
		orphanAll(new FigureEnumerator(newFigures));
	}

	public void orphanAll(FigureEnumeration fe) {
		while (fe.hasNextFigure()) {
			orphan(fe.nextFigure());
		}
	}

	/**
	 * Replaces a figure in the drawing without
	 * removing it from the drawing.
	 *
	 * @param figure figure to be replaced
	 * @param replacement figure that should replace the specified figure
	 * @return the figure that has been inserted (might be different from the figure specified)
	 */
	public synchronized Figure replace(Figure figure, Figure replacement) {
		int index = fFigures.indexOf(figure);
		if (index != -1) {
			replacement.setZValue(figure.getZValue());
			replacement.addToContainer(figureChangeListener);   // will invalidate figure
			figure.removeFromContainer(figureChangeListener);
			fFigures.set(index, replacement);
			figure.changed();
			replacement.changed();
		}
		return replacement;
	}

	/**
	 * Sends a figure to the back of the drawing.  
	 * I think this is a good place to throw a runtime exception if the Figure
	 * is not part of the drawing, dnoyeb!!! BadParameterException
	 *
	 * @param figure that is part of the drawing
	 */
	public synchronized void sendToBack(Figure figure) {
		if (containsFigure(figure)) {
			fFigures.remove(figure);
			fFigures.add(0, figure);
			_nLowestZ--;
			figure.setZValue(_nLowestZ);
			figure.changed();
		}
	}

	/**
	 * Brings a figure to the front.
	 * I think this is a good place to throw a runtime exception if the Figure
	 * is not part of the drawing, dnoyeb!!! BadParameterException
	 * I have spoken on BadParameterException several times, I won't mention it
	 * all the way through the file...
	 *
	 * @param figure that is part of the drawing
	 */
	public synchronized void bringToFront(Figure figure) {
		if (containsFigure(figure)) {
			fFigures.remove(figure);
			fFigures.add(figure);
			_nHighestZ++;
			figure.setZValue(_nHighestZ);
			figure.changed();
		}
	}

	/**
	 * Sends a figure to a certain layer within a drawing. Each figure
	 * lays in a unique layer and the layering order decides which
	 * figure is drawn on top of another figure. Figures with a higher
	 * layer number have usually been added later and may overlay
	 * figures in lower layers. Layers are counted from to (the number
	 * of figures - 1).
	 * The figure is removed from its current layer (if it has been already
	 * part of this drawing) and is transferred to the specified layers after
	 * all figures between the original layer and the new layer are shifted to
	 * one layer below to fill the layer sequence. It is not possible to skip a
	 * layer number and if the figure is sent to a layer beyond the latest layer
	 * it will be added as the last figure to the drawing and its layer number
	 * will be set to the be the one beyond the latest layer so far.
	 *
	 * @param figure Figure to be sent to a certain layer
	 * @param layerNr target layer of the figure
	 */
	public void sendToLayer(Figure figure, int layerNr) {
		if (containsFigure(figure)) {
			if (layerNr >= fFigures.size()) {
				layerNr = fFigures.size() - 1;
			}
			Figure layerFigure = getFigureFromLayer(layerNr);
			int layerFigureZValue = layerFigure.getZValue();
			int figureLayer = getLayer(figure);
			// move figure forward
			if (figureLayer < layerNr) {
				assignFiguresToPredecessorZValue(figureLayer + 1, layerNr);
			}
			else if (figureLayer > layerNr) {
				assignFiguresToSuccessorZValue(layerNr, figureLayer - 1);
			}

			fFigures.remove(figure);
			fFigures.add(layerNr, figure);
			figure.setZValue(layerFigureZValue);
			figure.changed();
		}
	}

	private void assignFiguresToPredecessorZValue(int lowerBound, int upperBound) {
		// cannot shift figures to a lower layer if the lower bound is
		// already the first layer.
		if (upperBound >= fFigures.size()) {
			upperBound = fFigures.size() - 1;
		}

		for (int i = upperBound; i >= lowerBound; i--) {
			Figure currentFigure = (Figure)fFigures.get(i);
			Figure predecessorFigure = (Figure)fFigures.get(i - 1);
			currentFigure.setZValue(predecessorFigure.getZValue());
		}
	}

	private void assignFiguresToSuccessorZValue(int lowerBound, int upperBound) {
		if (upperBound >= fFigures.size()) {
			upperBound = fFigures.size() - 1;
		}

		for (int i = upperBound; i >= lowerBound; i--) {
			Figure currentFigure = (Figure)fFigures.get(i);
			Figure successorFigure = (Figure)fFigures.get(i + 1);
			currentFigure.setZValue(successorFigure.getZValue());
		}
	}

	/**
	 * Gets the layer for a certain figure (first occurrence). The number
	 * returned is the number of the layer in which the figure is placed.
	 *
	 * @param figure figure to be queried for its layering place
	 * @return number of the layer in which the figure is placed and -1 if the
	 *			figure could not be found.
	 * @see #sendToLayer
	 */
	public int getLayer(Figure figure) {
		if (!containsFigure(figure)) {
			return -1;
		}
		else {
			return fFigures.indexOf(figure);
		}
	}

	/**
	 * Gets the figure from a certain layer.
	 *
	 * @param layerNr number of the layer which figure should be returned
	 * @return figure from the layer specified, null, if the layer nr was outside
	 *			the number of possible layer (0...(number of figures - 1))
	 * @see #sendToLayer
	 */
	public Figure getFigureFromLayer(int layerNr) {
		if ((layerNr >= 0) && (layerNr < fFigures.size())) {
			return (Figure)fFigures.get(layerNr);
		}
		else {
			return null;
		}
	}

	/**
	 * Draws all the contained figures
	 * @see Figure#draw
	 */
	public void draw(Graphics g) {
		draw(g, figures());
	}

	/**
	* Draws only the given figures
	* @todo mrfloppy to ensure that only figures contained within this
	*        <b>CompositeFigure</b> get drawn.  dnoyeb's opinion is that this 
	*         method is unnecessary and if used is a symptom of some other issue.
	* Likely {@link #draw(Graphics) draw} is enough and we don't need this.
	*
	* if we are asked to draw figures that we do not contain, Exception?
	*
	* @see Figure#draw
	*/
	public void draw(Graphics g, FigureEnumeration fe) {
		while (fe.hasNextFigure()) {
			fe.nextFigure().draw(g);
		}
	}

	/**
	 * Gets a figure at the given index.
	 */
	public Figure figureAt(int i) {
		return (Figure)fFigures.get(i);
	}

	/**
	* Returns an enumeration for accessing the contained figures.
	* The enumeration is a snapshot of the current contained <b>Figure</b>s
	* and is not a "live" enumeration and does not take subsequent
	* changes of the <b>CompositeFigure</b> into account.
	* The figures are returned in the drawing order.
	*
	*/
	public FigureEnumeration figures() {
		return new FigureEnumerator(CollectionsFactory.current().createList(fFigures));
	}

	/**
	 * Returns an enumeration to iterate in
	 * Z-order back to front over the {@link Figure Figure}s
	 * that lie within the given bounds.
	 */
	public FigureEnumeration figures(Rectangle viewRectangle) {
		if (_theQuadTree != null) {

			FigureEnumeration fe =
				_theQuadTree.getAllWithin(new Bounds(viewRectangle).asRectangle2D());

			List l2 = CollectionsFactory.current().createList();

			while (fe.hasNextFigure()) {
				Figure f = fe.nextFigure();
				//int z = fFigures.indexOf(f);
				l2.add(new OrderedFigureElement(f, f.getZValue()));
			}

			Collections.sort(l2);

			List l3 = CollectionsFactory.current().createList();

			for (Iterator iter = l2.iterator(); iter.hasNext(); ) {
				OrderedFigureElement ofe = (OrderedFigureElement)iter.next();
				l3.add(ofe.getFigure());
			}

			return new FigureEnumerator(l3);
		}

		return figures();
	}

	/**
	 * Gets number of contained {@link Figure Figure}s.
	 */
	public int figureCount() {
		return fFigures.size();
	}

	/**
	 * Check whether a given <b>figure</b> is contained within this <b>CompositeFigure.</b>
	 */
	public boolean containsFigure(Figure checkFigure) {
		return fFigures.contains(checkFigure);
	}

    /**
	 * Returns an enumeration for accessing the contained {@link Figure Figure}s
	 * in the reverse {@link Drawing Drawing} order.
	 */
	public final FigureEnumeration figuresReverse() {
		return new ReverseFigureEnumerator(CollectionsFactory.current().createList(fFigures));
	}

	/**
	 * Finds a top level Figure. Use this call for hit detection that
	 * should not descend into the contained <b>Figure</b>s.
	 */
	public Figure findFigure(int x, int y) {
		FigureEnumeration fe = figuresReverse();
		while (fe.hasNextFigure()) {
			Figure figure = fe.nextFigure();
			if (figure.containsPoint(x, y)) {
				return figure;
			}
		}
		return null;
	}

	/**
	 * Finds a top level <b>Figure</b> that intersects the given rectangle.
	 * @return	Returns the found <b>Figure</b>, or <b>null</b> if not found.
	 */
	public Figure findFigure(Rectangle r) {
		FigureEnumeration fe = figuresReverse();
		while (fe.hasNextFigure()) {
			Figure figure = fe.nextFigure();
			Rectangle fr = figure.displayBox();
			if (r.intersects(fr)) {
				return figure;
			}
		}
		return null;
	}

	/**
	 * Finds a top level <b>Figure</b>, but supresses the passed
	 * in <b>Figure</b>. Use this method to ignore a figure
	 * that is temporarily inserted into the drawing.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param without the <b>Figure</b> to be ignored during
	 * the find.
	 */
	public Figure findFigureWithout(int x, int y, Figure without) {
		if (without == null)
			return findFigure(x, y);
		FigureEnumeration fe = figuresReverse();
		while (fe.hasNextFigure()) {
			Figure figure = fe.nextFigure();
			if (figure.containsPoint(x, y) && !figure.includes(without)) {
				return figure;
			}
		}
		return null;
	}

	/**
	 * Finds a top level Figure that intersects the given rectangle.
	 * It supresses the passed
	 * in figure. Use this method to ignore a figure
	 * that is temporarily inserted into the drawing.
	 */
	public Figure findFigure(Rectangle r, Figure without) {
		if (without == null)
			return findFigure(r);
		FigureEnumeration fe = figuresReverse();
		while (fe.hasNextFigure()) {
			Figure figure = fe.nextFigure();
			Rectangle fr = figure.displayBox();
			if (r.intersects(fr) && !figure.includes(without)) {
				return figure;
			}
		}
		return null;
	}

	/**
	 * Finds a figure but descends into a figure's
	 * children. Use this method to implement <i>click-through</i>
	 * hit detection, that is, you want to detect the inner most
	 * figure containing the given point.
	 *
	 * This is improved but still <b>broken.</b>  Its achiles heel is the evil
	 * {@link CH.ifa.draw.standard.DecoratorFigure DecoratorFigure} that is
	 * "decorating" (read masking) the <b>CompositeFigures</b> it decorates.
	 * mrfloppy is working on a "Strategy pattern" fix for this.  Still I
	 * believe it would be better all around if {@link 
	 * CH.ifa.draw.standard.DecoratorFigure DecoratorFigure} actually used the
	 * "Decorator pattern." 
	 * Currently this method can not dig inside decorator figures.
	 * dnoyeb@users.sourceforge.net
	 */
	public Figure findFigureInside(int x, int y) {
		Figure figure = findFigure(x,y);
		if(figure instanceof CompositeFigure){
			Figure figure2 = figure.findFigureInside(x,y);
			if(figure2 != null)
				return figure2;
			else
				return figure;
		}
		else if(figure instanceof DecoratorFigure){
			return getFigureWithoutDecoration(figure).findFigureInside(x,y);
		}
		else
			return figure;
	}
	
	protected Figure getFigureWithoutDecoration(Figure peelFigure) {
		if (peelFigure instanceof DecoratorFigure) {
			return getFigureWithoutDecoration(((DecoratorFigure)peelFigure).getDecoratedFigure());
		}
		else {
			return peelFigure;
		}
	}
	
	/**
	 * Finds a figure but descends into a figure's
	 * children. It supresses the passed
	 * in figure. Use this method to ignore a figure
	 * that is temporarily inserted into the drawing.
	 * @see #findFigureInside for my error comments.
	 */
	public Figure findFigureInsideWithout(int x, int y, Figure without) {
		FigureEnumeration fe = figuresReverse();
		while (fe.hasNextFigure()) {
			Figure figure = fe.nextFigure();
			if (figure != without) {
				Figure found = figure.findFigureInside(x, y);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	/**
	 * Checks if the composite figure has the argument as one of its contained
	 * figures.
	 * @return true if the figure is part of this CompositeFigure, else otherwise
	 */
	public boolean includes(Figure figure) {
		if (super.includes(figure)) {
			return true;
		}

		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			Figure f = fe.nextFigure();
			if (f.includes(figure)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Moves all the given figures by x and y. Doesn't announce
	 * any changes. Subclassers override
	 * basicMoveBy. Clients usually call moveBy.
	 * @see #moveBy
	 */
	protected void basicMoveBy(int x, int y) {
		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			fe.nextFigure().moveBy(x,y);
		}
	}

	/**
	 * Releases the figure and all its contained figures.
	 * Should we release the figures in the order they were added?
	 * i.e. should we first release the contained figures, THEN call super.release()??
	 * !!!dnoyeb!!!
	 */
	public void release() {
		super.release();
		//removeAll
		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			Figure figure = fe.nextFigure();
			figure.release();
		}
	}

	/**
	 * Propagates the figureInvalidated event to my listener.
	 * @see FigureChangeListener
	 */
	protected void figureInvalidated(FigureChangeEvent e) {
		if (listener() != null) {
			listener().figureInvalidated(e);
		}
	}

	/**
	 * Propagates the removeFromDrawing request up to the container.
	 * Why does a contained figures remove request end up being the compositeFigures
	 * remove request? can we not remove just the contained figure?
	 * This seems bizarre !!!dnoyeb!!!
	 * It seems liek if a contained figure wants out, the whole compositefigure
	 * ends up requesting removal from its container too.
	 * I think those listening to this event should know who wants to be removed
	 * by querying the <code>FigureChangeEvent</code>.
	 * Still sees wrong here.
	 *
	 * Certainly this is wrong, we must handle our containees.
	 *
	 * 
	 * @see FigureChangeListener
	 */
	protected void figureRequestRemove(FigureChangeEvent e) {
		if (listener() != null) {
			listener().figureRequestRemove(new FigureChangeEvent(CompositeFigure.this));
		}
	}

	/**
	 * Propagates the requestUpdate request up to the container.
	 * This is a refiring of the event fired on the figures we are listening to.
	 * This passes this event onto those who are listening to us.
	 * we shouldnt pass it on.  those we are passing too never registered to hear
	 * events on the figure we are passing them.
	 * We should be repackaging this, since as far as our listeners are concerned
	 * we are firing it.
	 *
	 * @see FigureChangeListener
	 */
	protected void figureRequestUpdate(FigureChangeEvent e) {
		if (listener() != null) {
			listener().figureRequestUpdate(e);
		}
	}

	protected void figureChanged(FigureChangeEvent e) {
		_removeFromQuadTree(e.getFigure());
		_addToQuadTree(e.getFigure());
	}

	protected void figureRemoved(FigureChangeEvent e) {
		if (listener() != null) {
			listener().figureRemoved(e);
		}
	}

	/**
	 * Writes the contained figures to the StorableOutput.
	 */
	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeInt(figureCount());
		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			dw.writeStorable(fe.nextFigure());
		}
	}

	/**
	 * Reads the contained figures from StorableInput.
	 */
	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		int size = dr.readInt();
		fFigures = CollectionsFactory.current().createList(size);
		//what about z value reset?
		for (int i=0; i<size; i++) {
			add((Figure)dr.readStorable());
		}
		init(displayBox());
	}

	private void readObject(ObjectInputStream s)
		throws ClassNotFoundException, IOException {

		s.defaultReadObject();

		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			Figure figure = fe.nextFigure();
			figure.addToContainer(figureChangeListener);
		}

		init(new Rectangle(0, 0));
	}

	/**
	 * Used to optimize rendering.  Rendering of many objects may
	 * be slow until this method is called.  The view rectangle
	 * should at least approximately enclose the CompositeFigure.
	 * If the view rectangle is too small or too large, performance
	 * may suffer.
	 *
	 * Don't forget to call this after loading or creating a
	 * new CompositeFigure.  If you forget, drawing performance may
	 * suffer.
	 */
	public void init(Rectangle viewRectangle) {
		_theQuadTree = new QuadTree(new Bounds(viewRectangle).asRectangle2D());

		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			_addToQuadTree(fe.nextFigure());
		}
	}

	private void _addToQuadTree(Figure f) {
		if (_theQuadTree != null) {
			// Bugfix: Make sure the rectangle is not zero width or height.
			// Otherwise, the quadTree search in this.figures(Rectangle)
			// will be incorrect. [John Yu, 2002/05/23]
			Rectangle r = f.displayBox();
			if (r.height == 0) {
				r.grow(0, 1);
			}
			if (r.width == 0) {
				r.grow(1, 0);
			}

			_theQuadTree.add(f, new Bounds(r).asRectangle2D());
		}
	}

	private void _removeFromQuadTree(Figure f) {
		if (_theQuadTree != null) {
			_theQuadTree.remove(f);
		}
	}

	private void _clearQuadTree() {
		if (_theQuadTree != null) {
			_theQuadTree.clear();
		}
	}
}
