/*
 * @(#)DecoratorFigure.java
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
import java.util.List;
import java.util.Iterator;
import java.awt.*;
import java.io.*;

/**
 * DecoratorFigure can be used to decorate other figures with
 * decorations like borders. Decorator forwards all the
 * methods to their contained figure. Subclasses can selectively
 * override these methods to extend and filter their behavior.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld014.htm>Decorator</a></b><br>
 * DecoratorFigure is a decorator.
 *
 * The main problem is DecoratorFigure's inability to decorate a figure without
 * knowing all the methods that figure implements.  If it does not know all of
 * the decorated figures methods, it can not forward all the calls.  This hides
 * the internal figure from those <code>Tool</code>s and <code>Command</code>s
 * that would like to work on it.  It forces every <code>Tool</code> and <code>
 * Command</code> to check for its presence, and when found, query <code>
 * DecoratorFigure</code> for its decorated figure.  This is only a problem for
 * <code>Tool</code>s and <code>Command</code>s that have a particular class of
 * <code>Figure</code> they are able to work on.  This constitutes quite a few
 * of the <code>Tool</code>s and <code>Command</code>s.  Therefore, this pattern
 * does not integrate well with its intended role.  dnoyeb 1/15/03
 *
 * I have currently made DecoratorFigure implement Figure as opposed to extending
 * AbstractFigure.  This way AbstractFigure's implementation can not hide new
 * methods added to the Figure interface from being properly added to 
 * DecoratorFigure and forwarded to the decorated figure.  This solves part of
 * the problem.  I think the rest will actually be solved if document-view
 * seperation is implemented. 1/20/03
 *
 * @see Figure
 *
 * @version <$CURRENT_VERSION$>
 */

public abstract class DecoratorFigure
				implements Figure, FigureChangeListener {
	/**
	 * The listeners for a figure's changes.
	 * It is only one listener but this one can be a (chained) MultiCastFigureChangeListener
	 *
	 * Need to figure out who restores this connection when the figure is reloaded !!!dnoyeb!!!
	 *
	 * @see #invalidate
	 * @see #changed
	 * @see #willChange
	 */
	private transient FigureChangeListener fListener;
	
	/**
	 * The container of this figure.  Used to prevent a figure from being added
	 * to more than one container at a time.
	 */
	private transient FigureChangeListener container;
	
	/**
	 * The dependent figures which have been added to this container.
	 * This is an ordered collection.  The figures should be stored in the order
	 * in which they were added.  The figures should be loaded in the order in
	 * which they were stored.
	 * do dependent figures depend on us, or do we depend on them? ???dnoyeb???
	 * @see #read
	 * @see #write
	 */
	private List myDependentFigures;

	private List fFigureManipulators;

	private List fFigureDecorators;
	/**
	 * The decorated figure.
	 */
	private Figure fComponent;

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 8993011151564573288L;
	private int decoratorFigureSerializedDataVersion = 1;

	public DecoratorFigure() {
		initialize();
	}

	/**
	 * Constructs a DecoratorFigure and decorates the passed in figure.
	 */
	public DecoratorFigure(Figure figure) {
		this();
		decorate(figure);
	}

	/**
	 * Performs additional initialization code before the figure is decorated.
	 * Subclasses may override this method.
	 */
	protected void initialize() {
		myDependentFigures = CollectionsFactory.current().createList();
		fFigureManipulators = CollectionsFactory.current().createList();
		fFigureDecorators = CollectionsFactory.current().createList();		
	}

	/**
	 * Forwards the connection insets to its contained figure..
	 */
	public Insets connectionInsets() {
		return getDecoratedFigure().connectionInsets();
	}

	/**
	 * Forwards the canConnect to its contained figure..
	 */
	public boolean canConnect() {
		return getDecoratedFigure().canConnect();
	}

	/**
	 * Forwards containsPoint to its contained figure.
	 */
	public boolean containsPoint(int x, int y) {
		return getDecoratedFigure().containsPoint(x, y);
	}

	/**
	 * Decorates the given figure.
	 * Note the decorated figure is not a dependent figure.  it is a contained
	 * figure.  dependent figures are figures <i>outside</i> of the figure that
	 * they depend upon.
	 */
	public void decorate(Figure figure) {
		fComponent = figure;
		getDecoratedFigure().addToContainer(this);
		addDependendFigure( getDecoratedFigure() );
	}

	/**
	 * Removes the decoration from the contained figure.
	 */
	public Figure peelDecoration() {
		removeDependendFigure( getDecoratedFigure() );
		getDecoratedFigure().removeFromContainer( this );
		return getDecoratedFigure();
	}

	public Figure getDecoratedFigure() {
		return fComponent;
	}

	/**
	 * Forwards displayBox to its contained figure.
	 */
	public Rectangle displayBox() {
		return getDecoratedFigure().displayBox();
	}

	/**
	 * Forwards basicDisplayBox to its contained figure.
	 */
	public void basicDisplayBox(Point origin, Point corner) {
		getDecoratedFigure().basicDisplayBox(origin, corner);
	}

	/**
	 * Forwards draw to its contained figure.
	 */
	public void draw(Graphics g) {
		getDecoratedFigure().draw(g);
	}

	/**
	 * Forwards findFigureInside to its contained figure.
	 */
	public Figure findFigureInside(int x, int y) {
		return getDecoratedFigure().findFigureInside(x, y);
	}

	/**
	 * Forwards handles to its contained figure.
	 */
	public HandleEnumeration handles() {
		return getDecoratedFigure().handles();
	}

	/**
	 * Forwards includes to its contained figure.
	 */
	public boolean includes(Figure figure) {
		return ((figure == this) || getDecoratedFigure().includes(figure));
	}

	/**
	 * Forwards moveBy to its contained figure.
	 */
	public void moveBy(int x, int y) {
		getDecoratedFigure().moveBy(x, y);
	}

	/**
	 * Forwards basicMoveBy to its contained figure.
	 */
	protected void basicMoveBy(int x, int y) {
		// this will never be called
	}

	/**
	 * Releases itself. removes then releases its containees.
	 * This is broken.  peel does not set contained figure to null.  unsure how
	 * to fix that.
	 */
	public void release() {
		//getDecoratedFigure().release(); //decorator is dependent, it will be released by the architecture
		if( getContainer() != null ) {
			//This will become ASSERT in JDK 1.4
			//This represents an avoidable error on the programmers part.			
			throw new JHotDrawRuntimeException("Figure can note be released, it has not been removed yet.");
		}
		if(getDecoratedFigure() != null){
			removeDependendFigure( getDecoratedFigure() );
			getDecoratedFigure().removeFromContainer( this ); //this is of course not notifying any listeners. improper.
			getDecoratedFigure().release();
		}
	}
	
	/**
	 * Propagates invalidate up the container chain.
	 * @see FigureChangeListener
	 */
	public void figureInvalidated(FigureChangeEvent e) {
		if (listener() != null) {
			listener().figureInvalidated( new FigureChangeEvent(this,e.getInvalidatedRectangle()));
		}
	}

	public void figureChanged(FigureChangeEvent e) {
		if(listener() != null) {
			listener().figureChanged( new FigureChangeEvent(this));
		}
	}

	/**
     * Informs our container that we need to be updated.  We request this on 
	 * behalf of the decorated figure.
	 *
	 * @see FigureChangeListener
	 */
	public  void figureRequestUpdate(FigureChangeEvent e) {
		if (listener() != null) {
			listener().figureRequestUpdate(new FigureChangeEvent(this));
		}
	}

	/**
	 * Forwards figures to its contained figure.
	 */
	public FigureEnumeration figures() {
		return getDecoratedFigure().figures();
	}

	/**
	 * Forwards decompose to its contained figure.
	 */
	public FigureEnumeration decompose() {
		return getDecoratedFigure().decompose();
	}

	/**
	 * Forwards setAttribute to its contained figure.
	 *
	 * @deprecated use setAttribute(FigureAttributeConstant, Object) instead
	 */
	public void setAttribute(String name, Object value) {
		getDecoratedFigure().setAttribute(name, value);
	}

	/**
	 * Forwards setAttribute to its contained figure.
	 */
	public void setAttribute(FigureAttributeConstant attributeConstant, Object value) {
		getDecoratedFigure().setAttribute(attributeConstant, value);
	}

	/**
	 * Forwards getAttribute to its contained figure.
	 *
	 * @deprecated use getAttribute(FigureAttributeConstant) instead
	 */
	public Object getAttribute(String name) {
		return getDecoratedFigure().getAttribute(name);
	}

	/**
	 * Forwards getAttribute to its contained figure.
	 */
	public Object getAttribute(FigureAttributeConstant attributeConstant) {
		return getDecoratedFigure().getAttribute(attributeConstant);
	}

	/**
	 * Returns the locator used to located connected text.
	 */
	public Locator connectedTextLocator(Figure text) {
		return getDecoratedFigure().connectedTextLocator(text);
	}

	/**
	 * Returns the Connector for the given location.
	 */
	public Connector connectorAt(int x, int y) {
		return getDecoratedFigure().connectorAt(x, y);
	}

	/**
	 * Forwards the connector visibility request to its component.
	 */
	public void connectorVisibility(boolean isVisible, ConnectionFigure courtingConnection) {
		getDecoratedFigure().connectorVisibility(isVisible, null);
	}

	/**
	 * Writes itself and the contained figure to the StorableOutput.
	 */
	public void write(StorableOutput dw) {
		dw.writeInt( getZValue() );
		//store dependentFigures
		int size = myDependentFigures.size();
		FigureEnumeration fe = getDependendFigures();
		dw.writeInt( size );
		while (fe.hasNextFigure()) {
			dw.writeStorable(fe.nextFigure());
		}
		//store figuremanipulators
		dw.writeInt( fFigureManipulators.size() );
		for(Iterator it= fFigureManipulators.iterator();it.hasNext();) {
			dw.writeStorable( (FigureManipulator)it.next() );
		}
		//store figuredecorators
		dw.writeInt( fFigureDecorators.size() );
		for(Iterator it= fFigureDecorators.iterator();it.hasNext();) {
			dw.writeStorable( (Figure)it.next() );
		}	
		dw.writeStorable(getDecoratedFigure());
	}

	/**
	 * Reads itself and the contained figure from the StorableInput.
	 */
	public void read(StorableInput dr) throws IOException {
		setZValue( dr.readInt() );
		//load dependentFigures
		int size = dr.readInt();
		myDependentFigures = CollectionsFactory.current().createList(size);
		for (int i=0; i<size; i++) {
			myDependentFigures.add( (Figure)dr.readStorable()) ;
		}

		//load figureManipulators
		int manipSize = dr.readInt();
		fFigureManipulators = CollectionsFactory.current().createList(manipSize);
		for (int i=0; i<manipSize; i++) {
			fFigureManipulators.add( (FigureManipulator)dr.readStorable()) ;
		}
		//load figureDecorators
		int decSize = dr.readInt();
		fFigureDecorators = CollectionsFactory.current().createList(decSize);
		for (int i=0; i<decSize; i++) {
			fFigureDecorators.add( (Figure)dr.readStorable()) ;
		}
		decorate((Figure)dr.readStorable());
	}

	private void readObject(ObjectInputStream s)
		throws ClassNotFoundException, IOException {

		//since dependent figures are not transient, they get deserialized here
		//since FigureManipulators are not transient, they get deserialized here
		s.defaultReadObject();//Read the non-static and non-transient fields of the current class from this stream.
		getDecoratedFigure().addFigureChangeListener(this);
	}
	/**
	 * @todo Verify implementation.
	 */
	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
	}
	/**
	 * Make sure all dependent figures get visited.  Make sure all contained
	 * figures get visited.
	 */
	public void visit(FigureVisitor visitor) {
		//if we are already deleted, do not allow visit.
		if(visitor instanceof DeleteFromDrawingVisitor){
			if(getContainer() == null){
				return;
			}
		}
		
		
		// remember original listener as listeners might be changed by a visitor
		// (e.g. by calling addToContainer() or removeFromContainer())
		//FigureChangeListener originalListener = listener();
		FigureEnumeration fe = getDependendFigures();

		//visit this figure.
		visitor.visitFigure(this);
		//do not visit dependencies on insert
		if(visitor instanceof InsertIntoDrawingVisitor){
			return;
		}
		
		
		FigureEnumeration visitFigures = figures();
		while (visitFigures.hasNextFigure()) {
			visitFigures.nextFigure().visit(visitor);//visitor now visits the contained figures
		}

		HandleEnumeration visitHandles = handles();
		while (visitHandles.hasNextHandle()) {
			visitor.visitHandle(visitHandles.nextHandle()); //visiting handles
		}
/*
		originalListener = listener();
		if (originalListener != null) {
			visitor.visitFigureChangeListener(originalListener);
		}
*/

		while (fe.hasNextFigure()) {
			fe.nextFigure().visit(visitor);
		}
		//getDecoratedFigure().visit(visitor);
	}

	public TextHolder getTextHolder() {
		return getDecoratedFigure().getTextHolder();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * needs cloning unless the z order is preserved in the context/order of
	 * the saved figures somehow.
	 */
	private int _nZ;

	/**
	 * Changes the display box of a figure. Clients usually
	 * call this method. It changes the display box
	 * and announces the corresponding change.
	 * @param origin the new origin
	 * @param corner the new corner
	 * @see #displayBox
	 */
	public void displayBox(Point origin, Point corner) {
		willChange();
		basicDisplayBox(origin, corner);
		changed();
	}

	/**
	 * Gets the size of the figure. A convenience method.
	 */
	public Dimension size() {
		return new Dimension(displayBox().width, displayBox().height);
	}

	/**
	 * Checks if the figure is empty. The default implementation returns
	 * true if the width or height of its display box is < 3
	 * @see Figure#isEmpty
	 */
	public boolean isEmpty() {
		return (size().width < 3) || (size().height < 3);
	}


	/**
	 * Changes the display box of a figure. This is a
	 * convenience method. Implementors should only
	 * have to override basicDisplayBox
	 * @see #displayBox
	 */
	public void displayBox(Rectangle r) {
		displayBox(new Point(r.x, r.y), new Point(r.x+r.width, r.y+r.height));
	}

	/**
	 * Sets the Figure's container and registers the container
	 * as a figure change listener. A figure's container can be
	 * any kind of FigureChangeListener. A figure may have only a single container.
	 *
	 * @see Figure
	 */
	public void addToContainer(FigureChangeListener c) {
		if(getContainer() != null){
			//This will become ASSERT in JDK 1.4
			//This represents an avoidable error on the programmers part.
			throw new JHotDrawRuntimeException("This figure is already contained.");
		}
		if(c == null){
			//This will become ASSERT in JDK 1.4
			//This represents an avoidable error on the programmers part.
			throw new JHotDrawRuntimeException("Container parameter can not be null.");
		}
		setContainer( c );
		addFigureChangeListener(getContainer());
		invalidate();		
	}

	/**
	 * A callback from the container in response to the event fired by {@link
	 * #remove remove}.  This method does the actual removal from the container.
	 * 
	 *
	 * @see Figure#removeFromContainer
	 */
	public void removeFromContainer(FigureChangeListener c) {
		if( getContainer() == null ) {
			//This will become ASSERT in JDK 1.4
			//This represents an avoidable error on the programmers part.			
			throw new JHotDrawRuntimeException("This figure is not contained.");
		}
		if(c == null){
			//This will become ASSERT in JDK 1.4
			//This represents an avoidable error on the programmers part.
			throw new JHotDrawRuntimeException("Container parameter can not be null.");
		}
		invalidate();
		removeFigureChangeListener( getContainer() );
		setContainer( null );
	}

	/**
	 * Adds a listener for this figure.
	 */
	public synchronized void addFigureChangeListener(FigureChangeListener l) {
		fListener = FigureChangeEventMulticaster.add(listener(), l);
	}

	/**
	 * Removes a listener for this figure.
	 */
	public synchronized void removeFigureChangeListener(FigureChangeListener l) {
		fListener = FigureChangeEventMulticaster.remove(listener(), l);
	}

	/**
	 * Gets the figure's listners.
	 */
	protected synchronized FigureChangeListener listener() {
		return fListener;
	}

	/**
	 * Invalidates the figure. This method informs the listeners
	 * that the figure's current display box is invalid and should be
	 * refreshed.
	 */
	public void invalidate() {
		if (listener() != null) {
			Rectangle r = invalidateRectangle(displayBox());
			listener().figureInvalidated(new FigureChangeEvent(this, r));
		}
	}

	public void update() {
		invalidate();
		if (listener() != null) {
			listener().figureRequestUpdate(new FigureChangeEvent(this));
		}		
	}
	/**
	 * Hook method to change the rectangle that will be invalidated
	 */
	protected Rectangle invalidateRectangle(Rectangle r) {
		r.grow(Handle.HANDLESIZE, Handle.HANDLESIZE);
		return r;
	}

	/**
	 * Informes that a figure is about to change something that
	 * affects the contents of its display box.
	 * Causes the current display box to be marked as dirty and in need of
	 * redraw.  The redraw does not occur as a result of this method call.
	 *
	 * @see Figure#willChange
	 * @see Figure#invalidate
	 */
	public void willChange() {
		invalidate();
	}

	/**
	 * Informs that a figure changed the area of its display box.
	 * Causes the current display box to be marked as dirty and in need of
	 * redraw.  The redraw does not occur as a result of this method call.
	 *
	 * @see FigureChangeEvent
	 * @see FigureChangeListener
	 * @see Figure#changed
	 * @see Figure#invalidate
	 */
	public void changed() {
		invalidate();
		if (listener() != null) {
			listener().figureChanged(new FigureChangeEvent(this));
		}
	}

	/**
	 * Gets the center of a figure. A convenice
	 * method that is rarely overridden.
	 */
	public Point center() {
		return Geom.center(displayBox());
	}

	/**
	 * Clones a figure. Creates a clone by using the storable
	 * mechanism to flatten the Figure to stream followed by
	 * resurrecting it from the same stream.
	 *
	 * @see Figure#clone
	 */
	public Object clone() {
		Object clone = null;
		ByteArrayOutputStream output = new ByteArrayOutputStream(200);
		try {
			ObjectOutput writer = new ObjectOutputStream(output);
			writer.writeObject(this);
			writer.close();
		}
		catch (IOException e) {
			System.err.println("Class not found: " + e);
		}

		InputStream input = new ByteArrayInputStream(output.toByteArray());
		try {
			ObjectInput reader = new ObjectInputStream(input);
			clone = reader.readObject();
		}
		catch (IOException e) {
			System.err.println(e.toString());
		}
		catch (ClassNotFoundException e) {
			System.err.println("Class not found: " + e);
		}
		return clone;
	}




	/**
	 * Gets the z value (back-to-front ordering) of this figure.
	 */
	public int getZValue() {
	  return _nZ;
	}

	/**
	 * Sets the z value (back-to-front ordering) of this figure.
	 */
	public void setZValue(int z) {
	  _nZ = z;
	}

	public FigureEnumeration getDependendFigures() {
		synchronized(myDependentFigures){
			return new FigureEnumerator(myDependentFigures);
		}
	}

	public void addDependendFigure(Figure newDependendFigure) {
		synchronized(myDependentFigures){
			myDependentFigures.add(newDependendFigure);
		}
	}

	public synchronized void removeDependendFigure(Figure oldDependentFigure) {
		synchronized(myDependentFigures){
			myDependentFigures.remove(oldDependentFigure);
		}
	}

	protected FigureChangeListener getContainer() {
		return container;
	}
	protected void setContainer(FigureChangeListener container){
		this.container = container;
	}
	
	public void addFigureManipulator(FigureManipulator fm) {
		synchronized(fFigureManipulators) {		
			fm.AttachFigure(this);
			fFigureManipulators.add( fm );
		}
	}
	
	public void removeFigureManipulator(FigureManipulator fm) {
		synchronized(fFigureManipulators) {
			fFigureManipulators.remove( fm );
			fm.DetachFigure(this);
		}
	}
	
	public void addFigureDecorator(FigureDecorator fd){
		synchronized(fFigureDecorators){
			fFigureDecorators.add( fd );
			fd.decorateFigure( this );
		}
		invalidate();
	}
	public void removeFigureDecorator(FigureDecorator fd){
		synchronized(fFigureDecorators){
			fd.undecorateFigure( this );
			fFigureDecorators.remove( fd );
		}
		invalidate();		
	}
	/**
	 * The returned iterator needs to be fail fast or something !?!dnoyeb!?!
	 */
	public java.util.Iterator figureDecorators(){
		return fFigureDecorators.iterator();
	}
	public void drawAll(Graphics g){
		draw(g);
		drawDecorators(g);
	}
	public void drawDecorators(Graphics g){
		for(Iterator it= fFigureDecorators.iterator();it.hasNext();) {
			((FigureDecorator)it.next()).draw(g);
		}		
	}	
}
