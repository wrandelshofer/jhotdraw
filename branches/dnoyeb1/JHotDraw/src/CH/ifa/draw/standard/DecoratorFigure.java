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
 * @see Figure
 *
 * @version <$CURRENT_VERSION$>
 */

public abstract class DecoratorFigure
				extends AbstractFigure
				implements FigureChangeListener {

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
		initialize();
		decorate(figure);
	}

	/**
	 * Performs additional initialization code before the figure is decorated.
	 * Subclasses may override this method.
	 */
	protected void initialize() {
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
	 */
	public void decorate(Figure figure) {
		fComponent = figure;
		getDecoratedFigure().addFigureChangeListener( this );
		//addDependendFigure( getDecoratedFigure() );
	}

	/**
	 * Removes the decoration from the contained figure.
	 */
	public Figure peelDecoration() {
		getDecoratedFigure().removeFigureChangeListener( this );
		//removeDependendFigure(getDecoratedFigure());
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
		return (super.includes(figure) || getDecoratedFigure().includes(figure));
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
	 * 
	 */
	public void release() {
		getDecoratedFigure().remove();
		getDecoratedFigure().release();
		super.release();
	}
	/**
	 * Do not remove containees in response to this event.
	 */
	public void remove(){
		super.remove();
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
	}

	public void figureRemoved(FigureChangeEvent e) {
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
	 * Propagates the removeFromDrawing request up to the container.
	 * This is the only justified propagation of a request remove event.  This is
	 * also implemented correctly by repackaging the event so it appears to come
	 * from this figure.
	 * The decorator figure requests removal on behalf of the figure it is
	 * decorating.  the decorator figure is not allowed to exist without its
	 * decorated figure.
	 * @see FigureChangeListener
	 */
	public void figureRequestRemove(FigureChangeEvent e) {
		e.getFigure().removeFigureChangeListener(this);
		removeDependendFigure( e.getFigure() );
		if (listener() != null) {
			listener().figureRequestRemove(new FigureChangeEvent(this));
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
		super.write(dw);
		dw.writeStorable(getDecoratedFigure());
	}

	/**
	 * Reads itself and the contained figure from the StorableInput.
	 */
	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		decorate((Figure)dr.readStorable());
	}

	private void readObject(ObjectInputStream s)
		throws ClassNotFoundException, IOException {

		s.defaultReadObject();

		getDecoratedFigure().addFigureChangeListener(this);
	}

	public void visit(FigureVisitor visitor) {
		super.visit(visitor);
//		getDecoratedFigure().visit(visitor); !!!dnoyeb!!! this is a must right?
	}

	public TextHolder getTextHolder() {
		return getDecoratedFigure().getTextHolder();
	}
}
