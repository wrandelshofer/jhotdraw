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
import CH.ifa.draw.figures.NullFigure;
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
	private List fFigures;
	private transient Map orphanMap;
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
	private transient FigureChangeListener figureChangeListener;
	
	private class innerFigureChangeListener implements FigureChangeListener, java.io.Serializable,Cloneable {
		public void figureInvalidated(FigureChangeEvent e){
			CompositeFigure.this.figureInvalidated(e);
		}
		public void figureChanged(FigureChangeEvent e){
			CompositeFigure.this.figureChanged(e);
		}
		public void figureRequestUpdate(FigureChangeEvent e){
			CompositeFigure.this.figureRequestUpdate(e);
		}
	};

	protected CompositeFigure() {
		fFigures = CollectionsFactory.current().createList();
		orphanMap = CollectionsFactory.current().createMap();
		figureChangeListener = new innerFigureChangeListener();
		_nLowestZ = 0;
		_nHighestZ = 0;
	}

	protected final List getFigures(){
		return fFigures;
	}

	protected final void setFigures(List figures){
		fFigures = figures;
	}
	/**
	 * Adds a figure to the list of figures. Initializes the
	 * the figure's container.
	 *
	 * @param figure to be added to the drawing
	 */
	public void add(Figure figure) {
		if(orphanMap.containsKey(figure)){
			System.out.println("Figure restored to " + this);
			restore(figure);
		}
		else {
			DEBUG_validateContainment(figure,false);
			figure.setZValue(++_nHighestZ);
			getFigures().add(figure);
			figure.addToContainer(figureChangeListener);  //add a figure to this CompositeFigure
			_addToQuadTree(figure);
		}
	}

	/**
	 * Adds a list of figures.
	 *
	 * @see #add
	 * @deprecated use {@link #addAll(FigureEnumeration)
	 *             addAll(FigureEnumeration fe)} instead.
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
	 * This will remove a figure.  orphan will be called first if necessary.
	 * It is the removers responsibility to call {@link Figure#release release()}
	 * if disposing the figure.
	 *
	 * @param figure that is part of the drawing and should be removed
	 */
	public void remove(Figure figure) {
		//figure must be orphaned already
		if(!orphanMap.containsKey(figure)){
			orphan(figure);
		}
		Figure nf = (Figure)orphanMap.remove(figure);
		getFigures().remove(nf);
	}
	/**
	 * Puts a figure back into the CompositeFigure in its old place.
	 * Figure must have already been orphaned.
	 * @todo investigate removing the event firing from here, that way we can use add
	 * to add to container and quadtree
	 */
	protected void restore(Figure figure){
		if(orphanMap.containsKey(figure)){
			Rectangle r = figure.displayBox();
			Figure nf = (Figure) orphanMap.remove(figure);
			int index = getFigures().indexOf(nf);
			getFigures().set(index,figure);
			figure.addToContainer( this.figureChangeListener );
			_addToQuadTree(figure);
			//need to do something here to repaint the restored area? probably unnecessary, just behave like add does
			if (listener() != null) {
				listener().figureInvalidated(new FigureChangeEvent( this, r ));
				listener().figureRequestUpdate(new FigureChangeEvent(this));
				//preferably whoever ordered the remove should order the update so 
				//we dont redraw too frequently and unnecessarily
			}
		}
	}
	
	/**
	 * Removes a list of figures from the CompositeFigure.
	 * calls orphan first if necessary.
	 *
	 * @see #remove
	 * @deprecated use {@link #removeAll(FigureEnumeration fe) 
	 *             removeAll(FigureEnumeration fe)} instead.
	 */
	public void removeAll(List figures) {
		removeAll(new FigureEnumerator(figures));
	}

	/**
	 * Removes all figures from this container and releases it from the undo/redo
	 * architecture.  calls orphan on the figures first if necessary
	 *
	 * @see #remove
	 */
	public void removeAll(FigureEnumeration fe) {
		while (fe.hasNextFigure()) {
			remove( fe.nextFigure());
		}
	}

	/**
	 * Removes all figures from this container.  calls orphan on the figures first
	 * if necessary.  Ignores any currently orphaned figures.
	 * 
	 *
	 * @see #remove
	 * @deprecated This method does not release the figures as required for
	 *             removed figures.  It also does not return them so the caller
	 *             has no way of knowing the list of figures that now require
	 *             release.
	 */
	public void removeAll() {
		removeAll( new FigureEnumerator(getFigures()));
	}

	/**
	 * Orphans a figure from the container, but doesn't release it. Use this 
	 * method to temporarily manipulate a figure outside of the drawing.  This
	 * method is to be used to support the undo/redo architecture.  
	 *
	 * An orphaned figure may only be added back to the container it was orphaned
	 * from.  If you need to add the figure to a different container, first 
	 * remove it from the container it has been orphaned from.
	 *
	 * @param figure that is part of the drawing and should be removed
	 */
	public synchronized void orphan(Figure figure) {
		DEBUG_validateContainment(figure,true);		
		Rectangle r = figure.displayBox();
		figure.removeFromContainer( figureChangeListener ); //removes the figure from being contained in this CompositeFigure
		//replace the figure with a null figure
		int index = getFigures().indexOf(figure);
		Figure nf = new NullFigure();
		getFigures().set(index,nf);
		//store a reference to the NullFfigure and the actual figure
		orphanMap.put(figure,nf);
		//remove figure from quad tree
		_removeFromQuadTree(figure);

		//need to do something here to repaint the removed area.
		if (listener() != null) {
			listener().figureInvalidated(new FigureChangeEvent( this, r ));
			listener().figureRequestUpdate(new FigureChangeEvent(this));
			//preferably whoever ordered the remove should order the update so 
			//we dont redraw too frequently and unnecessarily
		}
	}

	/**
	 * @deprecated use {@link #orphanAll(FigureEnumeration fe) 
	 *             orphanAll(FigureEnumeration fe)} instead.
	 */	
	public void orphanAll(List figures) {
		orphanAll( new FigureEnumerator(figures) );
	}

	/**
	 * Orphans a FigureEnumeration of figures.  These figures must be removed
	 * before they are eligible for release.
	 *
	 * @see #removeAll
	 * @see #orphan
	 */	
	public void orphanAll(FigureEnumeration fe) {
		while (fe.hasNextFigure()) {
			orphan(fe.nextFigure());
		}
		_clearQuadTree();
		_nLowestZ = 0;
		_nHighestZ = 0;
	}

	/**
	 * Replaces a figure in the drawing without
	 * removing it from the drawing.
	 *
	 * @param figure figure to be replaced
	 * @param replacement figure that should replace the specified figure
	 * @return the figure that has been inserted (might be different from the figure specified)
	 * @todo determine what we are doign here.  is this an orphan with an add or what?
	 */
	public synchronized Figure replace(Figure figure, Figure replacement) {
		DEBUG_validateContainment(figure,true);		
		int index = getFigures().indexOf(figure);
		if (index != -1) {
			replacement.setZValue(figure.getZValue());
			replacement.addToContainer(figureChangeListener);   // will invalidate figure
			figure.removeFromContainer(figureChangeListener);
			getFigures().set(index, replacement);
			figure.changed();
			replacement.changed();
		}
		return replacement;
	}

	/**
	 * Sends a figure to the back of the drawing.  
	 * @param figure that is part of the drawing
	 */
	public synchronized void sendToBack(Figure figure) {
		DEBUG_validateContainment(figure,true);		
		getFigures().remove(figure);
		getFigures().add(0, figure);
		_nLowestZ--;
		figure.setZValue(_nLowestZ);
		figure.changed();
	}

	/**
	 * Brings a figure to the front.
	 *
	 * @param figure that is part of the drawing
	 */
	public synchronized void bringToFront(Figure figure) {
		DEBUG_validateContainment(figure,true);		
		getFigures().remove(figure);
		getFigures().add(figure);
		_nHighestZ++;
		figure.setZValue(_nHighestZ);
		figure.changed();
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
		DEBUG_validateContainment(figure,true);
		if (layerNr >= getFigures().size()) {
			layerNr = getFigures().size() - 1;
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

		getFigures().remove(figure);
		getFigures().add(layerNr, figure);
		figure.setZValue(layerFigureZValue);
		figure.changed();
	}

	private void assignFiguresToPredecessorZValue(int lowerBound, int upperBound) {
		// cannot shift figures to a lower layer if the lower bound is
		// already the first layer.
		if (upperBound >= getFigures().size()) {
			upperBound = getFigures().size() - 1;
		}

		for (int i = upperBound; i >= lowerBound; i--) {
			Figure currentFigure = (Figure)getFigures().get(i);
			Figure predecessorFigure = (Figure)getFigures().get(i - 1);
			currentFigure.setZValue(predecessorFigure.getZValue());
		}
	}

	private void assignFiguresToSuccessorZValue(int lowerBound, int upperBound) {
		if (upperBound >= getFigures().size()) {
			upperBound = getFigures().size() - 1;
		}

		for (int i = upperBound; i >= lowerBound; i--) {
			Figure currentFigure = (Figure)getFigures().get(i);
			Figure successorFigure = (Figure)getFigures().get(i + 1);
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
			return getFigures().indexOf(figure);
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
		if ((layerNr >= 0) && (layerNr < getFigures().size())) {
			return (Figure)getFigures().get(layerNr);
		}
		else {
			return null;
		}
	}

	/**
	 * Draws all the contained figures.
	 * @todo Make this respect Z values in some way.
	 * @see Figure#draw
	 */
	public void draw(Graphics g) {
		draw(g, new FigureEnumerator(getFigures()));
	}

	/**
	* Draws only the given figures
	* if we are asked to draw figures that we do not contain, Exception?
	* maybe this should be protected?
	*
	* @see Figure#draw
	*/
	public void draw(Graphics g, FigureEnumeration fe) {
		while (fe.hasNextFigure()) {
			fe.nextFigure().drawAll(g);
		}
	}

	/**
	 * Gets a figure at the given index.
	 */
	public Figure figureAt(int i) {
		return (Figure)getFigures().get(i);
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
		return new FigureEnumerator(CollectionsFactory.current().createList(getFigures()));
	}

	protected List basicIncludedFigures(){
		List figures = CollectionsFactory.current().createList();
		FigureEnumeration fe = figures();
		while(fe.hasNextFigure()){
			Figure f = fe.nextFigure();
			figures.add( f );
			if(f instanceof CompositeFigure) {
				List innerFigures = ((CompositeFigure)f).basicIncludedFigures();
				if(innerFigures.size() > 0){
					figures.addAll( innerFigures );
					continue;
				}
			}
		}
		return figures;
	}
	protected List basicIncludedFigures(int uptoDepth, boolean exclusive){
		List figures = CollectionsFactory.current().createList();
		FigureEnumeration fe = figures();
		while(fe.hasNextFigure()){
			Figure f = fe.nextFigure();
			if(exclusive == false) {
				figures.add( f );
			}
			if(uptoDepth > 0){
				if(f instanceof CompositeFigure) {
					List innerFigures = ((CompositeFigure)f).basicIncludedFigures(uptoDepth - 1,exclusive);
					if(innerFigures.size() > 0){
						figures.addAll( innerFigures );
						continue;
					}
				}
			}
		}
		return figures;
	}
	public FigureEnumeration includedFigures() {
		return new FigureEnumerator(basicIncludedFigures());
	}

	/**
	 * Returns all figures upto the specified depth, with 0 being only the topmost
	 * level.  if exclusive is true, it only returns figures present at the depth
	 * specified.  This is used for tools that work on figures within figures.
	 *
	 */
	public FigureEnumeration includedFigures(int depth, boolean exclusive){
		return new FigureEnumerator(basicIncludedFigures(depth,exclusive));
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
				//int z = getFigures().indexOf(f);
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
	 * should use figures()
	 */
	public int figureCount() {
		return getFigures().size();
	}

	/**
	 * Check whether a given <b>figure</b> is contained within this <b>CompositeFigure.</b>
	 * Should use figures()?
	 */
	public boolean containsFigure(Figure checkFigure) {
		return getFigures().contains(checkFigure);
	}

    /**
	 * Returns an enumeration for accessing the contained {@link Figure Figure}s
	 * in the reverse {@link Drawing Drawing} order.
	 * Should use figures()
	 */
	public final FigureEnumeration figuresReverse() {
		return new ReverseFigureEnumerator(CollectionsFactory.current().createList(getFigures()));
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
	 *
	 * final because it relies on findFigure.  change that to change behavior.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param without the <b>Figure</b> to be ignored during
	 * the find.
	 */
	public Figure findFigureWithout(int x, int y, Figure without) {
		DEBUG_validateContainment(without,true);		
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
		DEBUG_validateContainment(without,true);		
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
	 */
	public Figure findFigureInside(int x, int y) {
		Figure figure = findFigure(x,y);
		if(figure instanceof CompositeFigure){
			Figure figure2 = figure.findFigureInside(x,y);
			if(figure2 != null) {
				return figure2;
			}
		}
		return figure;
	}
	
	/**
	 * Finds a figure but descends into a figure's
	 * children. It supresses the passed
	 * in figure. Use this method to ignore a figure
	 * that is temporarily inserted into the drawing.
	 * @see #findFigureInside for my error comments.
	 */
	public Figure findFigureInsideWithout(int x, int y, Figure without) {
		DEBUG_validateContainment(without,true);		
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
	 * should
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
		FigureEnumeration fe = new FigureEnumerator(getFigures());
		while (fe.hasNextFigure()) {
			fe.nextFigure().moveBy(x,y);
		}
	}

	/**
	 * Removes all contained figures from this container.
	 * Releases all contained figures.
	 * Releases itself.
	 * This figure can not be released until its orphans are removed.
	 *
	 * @see #remove
	 * @see Figure#release
	 */
	public void release() {
		if( getContainer() != null ) {
			//This will become ASSERT in JDK 1.4
			//This represents an avoidable error on the programmers part.			
			throw new JHotDrawRuntimeException("Figure can not be released, it has not been removed yet.");
		}
		if(orphanMap.size() > 0){
			throw new JHotDrawRuntimeException("Figure can not be released until its orphans are removed.");
		}
		FigureEnumeration feRemove = figures();
		FigureEnumeration feRelease = figures();
		removeAll(feRemove);
		while(feRelease.hasNextFigure()){
			feRelease.nextFigure().release();
		}
		super.release();
	}

	/**
	 * Called when one of the contained figures has been invalidated.  As a 
	 * result, we will invalidate this rectangle on our container.
	 * @see FigureChangeListener
	 */
	protected void figureInvalidated(FigureChangeEvent e) {
		if (listener() != null) {
			listener().figureInvalidated( new FigureChangeEvent(this,e.getInvalidatedRectangle()));
		}
	}

	/**
	 * Called when one of the contained figures is requesting to be redrawn.
	 * As a result, we will request to be redrawn from our container in order
	 * to satisfy the request of our containee.
	 *
	 * @see FigureChangeListener
	 */
	protected void figureRequestUpdate(FigureChangeEvent e) {
		if (listener() != null) {
			listener().figureRequestUpdate(new FigureChangeEvent(this));
		}
	}

	/**
	 * This event is handled internally by the container !?!dnoyeb!?!
	 */
	protected void figureChanged(FigureChangeEvent e) {
		_removeFromQuadTree(e.getFigure());
		_addToQuadTree(e.getFigure());
		if (listener() != null) {
			listener().figureChanged(new FigureChangeEvent(this));
		}
	}

	/**
	 * Writes the contained figures to the StorableOutput.
	 * The storing process is assumed to be serial and not in need of synchronization.
	 */
	public void write(StorableOutput dw) {
		super.write(dw);
		//store figures
		dw.writeInt(figureCount());
		FigureEnumeration fe = new FigureEnumerator(getFigures());
		while (fe.hasNextFigure()) {
			Figure f = fe.nextFigure();
			if(f instanceof NullFigure){
				System.err.println("Warning, writing NullFigure " + f);
			}
			dw.writeStorable(f);
		}
	}
	
	/**
	 * Shallow serialization of the object and the figures it contains
	 */
	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
	}

	/**
	 * Reads the contained figures from StorableInput.
	 * The loading process is assumed to be serial and not in need of synchronization.
	 */
	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		//
		figureChangeListener = new innerFigureChangeListener();
		int size = dr.readInt();
		fFigures = CollectionsFactory.current().createList(size);
		orphanMap = CollectionsFactory.current().createMap(); 
		//what about z value reset?
		for (int i=0; i<size; i++) {
			Figure f = (Figure)dr.readStorable();
			if(f instanceof NullFigure){
				System.err.println("Warning, reading NullFigure." + f);
			}
			add(f);
		}
		init(displayBox()); //this is a bad call.  calling unitialized subclasses possible here.
	}

	/**
	 * Used for the cloning mechanism.
	 * Seems to be a copy of those within us, but nothing to do with those we
	 * connect to. 
	 * @todo Verify and specify the functionality to be here.
	 */
	private void readObject(ObjectInputStream s)
		throws ClassNotFoundException, IOException {

		//the figures are not transient so they get deserialized here
		s.defaultReadObject();
		orphanMap = CollectionsFactory.current().createMap(); 
		//the listener is transient and not deserialized
		figureChangeListener = new innerFigureChangeListener();
		//so we need to establish listening to our new figures
		FigureEnumeration fe = new FigureEnumerator(getFigures());
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

		FigureEnumeration fe = new FigureEnumerator(getFigures());
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
	/**
	 * This is development level code that should not be released in a non
	 * development edition.  Don't know how to accomplish this.  ASSERT seems
	 * like a good way in JDK1.4
	 */
	protected void DEBUG_validateContainment(Figure figure, boolean want){
		//This will become ASSERT in JDK 1.4
		//This represents an avoidable error on the programmers part.
		if(want == true){
			if(!containsFigure(figure)){
				throw new JHotDrawRuntimeException("Figure is not part of this CompositeFigure.");
			}
		}
		else{
			if(containsFigure(figure)){
				throw new JHotDrawRuntimeException("Figure is already part of this CompositeFigure.");
			}			
		}
	}
}
