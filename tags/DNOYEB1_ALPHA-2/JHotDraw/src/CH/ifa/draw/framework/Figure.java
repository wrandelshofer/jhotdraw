/*
 * @(#)Figure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

import CH.ifa.draw.util.*;
import CH.ifa.draw.standard.TextHolder;

import java.awt.*;
import java.io.Serializable;

/**
 * The interface of a graphical figure. A figure knows
 * its display box and can draw itself. A figure can be
 * composed of several figures. To interact and manipulate
 * with a figure it can provide Handles and Connectors.<p>
 * A figure has a set of handles to manipulate its shape or attributes.
 * A figure has one or more connectors that define how
 * to locate a connection point.<p>
 * Figures can have an open ended set of attributes.
 * An attribute is identified by a string.<p>
 * Default implementations for the Figure interface are provided
 * by AbstractFigure.
 *
 * @see Handle
 * @see Connector
 * @see CH.ifa.draw.standard.AbstractFigure
 *
 * @version <$CURRENT_VERSION$>
 */
public interface Figure
				extends Storable, Cloneable, Serializable {

	/**
	 * Constant that allows to identify a popup menu assigned
	 * as an attribute.
	 */
	public static String POPUP_MENU = "POPUP_MENU";

	/**
	 * Moves the Figure to a new location.
	 * @param dx the x delta
	 * @param dy the y delta
	 */
	public void moveBy(int dx, int dy);

	/**
	 * Changes the display box of a figure. This method is
	 * always implemented in figure subclasses. It only changes
	 * the displaybox and does not announce any changes. It
	 * is usually not called by the client. Clients typically call
	 * displayBox to change the display box.
	 * @param origin the new origin
	 * @param corner the new corner
	 * @see #displayBox
	 */
	public void basicDisplayBox(Point origin, Point corner);

	/**
	 * Changes the display box of a figure. Clients usually
	 * invoke this method. It changes the display box
	 * and announces the corresponding changes.
	 * @param origin the new origin
	 * @param corner the new corner
	 * @see #displayBox
	 */
	public void displayBox(Point origin, Point corner);

	/**
	 * Gets the display box of a figure
	 * @see #basicDisplayBox
	 */
	public Rectangle displayBox();

	/**
	 * Draws the figure.
	 * @param g the Graphics to draw into
	 */
	public void draw(Graphics g);
	
	/**
	 * This method will call draw first then drawDecorators
	 */
	public void drawAll(Graphics g);

	/**
	 * This method draws the FigureDecorators
	 * @see FigureDecorator#draw
	 */
	public void drawDecorators(Graphics g);

	/**
	 * Returns the handles used to manipulate
	 * the figure. Handles is a Factory Method for
	 * creating handle objects.
	 *
	 * @return an type-safe iterator of handles
	 * @see Handle
	 */
	public HandleEnumeration handles();

	/**
	 * Gets the size of the figure
	 */
	public Dimension size();

	/**
	 * Gets the figure's center
	 */
	public Point center();

	/**
	 * Checks if the Figure should be considered as empty.
	 * For instance, when the figure is being added to the drawing, the <code>
	 * CreationTool</code> will check this to see wether to add the figure or
	 * not.  If the figure is too small to be seen, it will return true for isEmpty
	 * under the above condition.
	 */
	public boolean isEmpty();

	/**
	 * Returns an Enumeration of the figures contained in this figure
	 */
	public FigureEnumeration figures();

	/**
	 * Returns the figure that contains the given point.
	 */
	public Figure findFigureInside(int x, int y);

	/**
	 * Checks if a point is inside the figure.
	 */
	public boolean containsPoint(int x, int y);

	/**
	 * Returns a Clone of this figure
	 */
	public Object clone();

	/**
	 * Changes the display box of a figure. This is a
	 * convenience method. Implementors should only
	 * have to override basicDisplayBox
	 * @see #displayBox
	 */
	public void displayBox(Rectangle r);

	/**
	 * Checks whether the given figure is contained in this figure.
	 */
	public boolean includes(Figure figure);

	/**
	 * Decomposes a figure into its parts. A figure is considered
	 * as a part of itself.
	 */
	public FigureEnumeration decompose();

	/**
	 * Sets the Figure's container and registers the container
	 * as a figure change listener. A figure's container can be
	 * any kind of FigureChangeListener.  A figure may only have a single 
	 * container attempts to add to 2nd container will throw exception.
	 * This is a runtime exception now while its debated wether it should be
	 * a checked exception or ont.
	 */
	public void addToContainer(FigureChangeListener c);

	/**
	 * Removes a figure from its container.
	 */
	public void removeFromContainer(FigureChangeListener c);

	
	public void addDependendFigure(Figure newDependendFigure);
	public void removeDependendFigure(Figure oldDependendFigure);
	public FigureEnumeration getDependendFigures();

	/**
	 * Gets the Figure's listeners.
	 */
	//public FigureChangeListener listener();

	/**
	 * Adds a listener for this figure.
	 */
	public void addFigureChangeListener(FigureChangeListener l);

	/**
	 * Removes a listener for this figure.
	 */
	public void removeFigureChangeListener(FigureChangeListener l);

	/**
	 * Notifies all listeners that this figure has been released.
	 * Figures should first release all their resources such as other figures.
	 * THIS METHOD IS CALLED BY THE CONTAINER THAT IS RELEASING THE FIGURE
	 * need to change event names to released
	 *
	 * @see CompositeFigure#remove(Figure f)
	 */
	public void release();
	
	/**
	 * This informs all listeners that the figure is requesting to be redrawn.
	 *
	 * @see FigureChangeListener#figureRequestUpdate
	 */
	public void update();
	/**
	 * Invalidates the figure. This method informs its listeners
	 * that its current display box is invalid and should be
	 * refreshed.
	 * This will fire a figureInvalidated event to all its listeners.
	 *
	 * @see FigureChangeListener#figureInvalidated
	 */
	public void invalidate();

	/**
	 * Informes that a figure is about to change such that its
	 * display box is affected.
	 * Here is an example of how it is used together with changed()
	 * <pre>
	 * public void move(int x, int y) {
	 *      willChange();
	 *      // change the figure's location
	 *      changed();
	 *  }
	 * </pre>
	 *
	 * Causes the figures current display box to be marked as dirty and in need
	 * of redraw.  The redraw does not occur as a result of this method call.
	 *
	 * @see #invalidate
	 * @see #changed
	 * @see FigureChangeListener#figureInvalidated
	 */
	public void willChange();

	/**
	 * Call this after the figures display box has changed.  It will nottify all
	 * listeners that the figures bounding box has changed and is in need of
	 * redraw.
	 *
	 * Causes the figures current display box to be marked as dirty and in need
	 * of redraw.  The redraw does not occur as a direct result of this method 
	 * call.
	 *
	 * @see #invalidate
	 * @see #willChange
	 * @see FigureChangeListener#figureInvalidated
	 * @see FigureChangeListener#figureChanged
	 * @see FigureChangeEvent
	 */
	public void changed();

	/**
	 * Checks if this figure can be connected
	 */
	public boolean canConnect();

	/**
	 * Gets a connector for this figure at the given location.
	 * A figure can have different connectors at different locations.
	 */
	public Connector connectorAt(int x, int y);

	/**
	 * Sets whether the connectors should be visible.
	 * Connectors can be optionally visible. Implement
	 * this method and react on isVisible to turn the
	 * connectors on or off.
	 */
	public void connectorVisibility(boolean isVisible, ConnectionFigure connection);

	/**
	 * Returns the connection inset. This is only a hint that
	 * connectors can use to determine the connection location.
	 * The inset defines the area where the display box of a
	 * figure should not be connected.
	 *
	 */
	public Insets connectionInsets();

	/**
	 * Returns the locator used to located connected text.
	 */
	public Locator connectedTextLocator(Figure text);

	/**
	 * Returns the named attribute or null if a
	 * a figure doesn't have an attribute.
	 * All figures support the attribute names
	 * FillColor and FrameColor
	 *
	 * @deprecated use getAttribute(FigureAttributeConstant) instead
	 */
	public Object getAttribute(String name);

	/**
	 * Returns the named attribute or null if a
	 * a figure doesn't have an attribute.
	 * All figures support the attribute names
	 * FillColor and FrameColor
	 */
	public Object getAttribute(FigureAttributeConstant attributeConstant);

	/**
	 * Sets the named attribute to the new value
	 *
	 * @deprecated use setAttribute(FigureAttributeConstant, Object) instead
	 */
	public void setAttribute(String name, Object value);

	/**
	 * Sets the named attribute to the new value
	 */
	public void setAttribute(FigureAttributeConstant attributeConstant, Object value);

	/**
	 * Gets the z value (back-to-front ordering) of this figure.
	 * Z values are not guaranteed to not skip numbers.
	 */
	public int getZValue();

	/**
	 * Sets the z value (back-to-front ordering) of this figure.
	 * Z values are not guaranteed to not skip numbers.
	 */
	public void setZValue(int z);

	public void visit(FigureVisitor visitor);

	public TextHolder getTextHolder();
	/**
	 * Add a <code>FigureManipulator</code> to the <code>Figure</code>.
	 */
	public void addFigureManipulator(FigureManipulator fm);
	/**
	 * Remove a <code>FigureManipulator</code> to the <code>Figure</code>.
	 */
	public void removeFigureManipulator(FigureManipulator fm);
	public void addFigureDecorator(FigureDecorator fd);
	public void removeFigureDecorator(FigureDecorator fd);
	/**
	 * Need to create an enumerator/enumeration for this.
	 */
	public java.util.Iterator figureDecorators();
}
