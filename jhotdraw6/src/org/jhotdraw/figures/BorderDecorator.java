/*
 * @(#)BorderDecorator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.figures;

import java.awt.*;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * BorderDecorator decorates an arbitrary Figure with
 * a border.  Therein lies the problem.  It can not handle an arbitrary Figure.
 * It will not forward calls which do not come on the Figure interface.  Therefore,
 * it can only handle a plain Figure.  Any Tool or Command seeking a specific
 * Figure type will have to try and determine if DecoratorFigure contains such a
 * type.  This is doable, but burdens every occurance of Figure with this question.
 *
 * This figure has been changed to a CompositeFigure since most Tools and Commands
 * are prepared to deal with CompositeFigures.  Should produce the same results.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class BorderDecorator extends CompositeFigure {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 1205601808259084917L;
	private int borderDecoratorSerializedDataVersion = 1;

	private Point myBorderOffset;
	private Color myBorderColor;
	private Color myShadowColor;

	protected BorderDecorator() {
		initialize();
	}

	public BorderDecorator(Figure figure) {
		initialize();
		add(figure);
	}

	/**
	 * Performs additional initialization code before the figure is decorated
	 * Subclasses may override this method.
	 */
	protected void initialize() {
		setBorderOffset(new Point(3,3));
	}

	public void setBorderOffset(Point newBorderOffset) {
		myBorderOffset = newBorderOffset;
	}
		
	public Point getBorderOffset() {
		if (myBorderOffset == null) {
			return new Point(0,0);
		}
		else {
			return myBorderOffset;
		}
	}

	/**
	 * Draws a the figure and decorates it with a border.
	 */
	public void draw(Graphics g) {
		Rectangle r = displayBox();
		super.draw(g);
		g.setColor(Color.white);
		g.drawLine(r.x, r.y, r.x, r.y + r.height);
		g.drawLine(r.x, r.y, r.x + r.width, r.y);
		g.setColor(Color.gray);
		g.drawLine(r.x + r.width, r.y, r.x + r.width, r.y + r.height);
		g.drawLine(r.x , r.y + r.height, r.x + r.width, r.y + r.height);
	}

	/**
	 * Gets the displaybox including the border.
	 */
	public Rectangle displayBox() {
		Rectangle r = getDecoratedFigure().displayBox();
		r.grow(getBorderOffset().x, getBorderOffset().y);
		return r;
	}

	/**
	 * Invalidates the figure extended by its border.
	 */
	protected void figureInvalidated(FigureChangeEvent e) {
		Rectangle rect = e.getInvalidatedRectangle();
		rect.grow(getBorderOffset().x, getBorderOffset().y);
		super.figureInvalidated(new FigureChangeEvent(this, rect, e));
	}

	public Insets connectionInsets() {
		Insets i = super.connectionInsets();
		i.top -= getBorderOffset().y;
		i.bottom -= getBorderOffset().y;
		i.left -= getBorderOffset().x;
		i.right -= getBorderOffset().x;

		return i;
	}
	
	/**
	 * Forwards basicDisplayBox to its contained figure.
	 * What about the size of the decoration? ???dnoyeb???
	 */
	public void basicDisplayBox(Point origin, Point corner) {
		getDecoratedFigure().basicDisplayBox(origin, corner);
	}
	
	/**
	 * Forwards handles to its contained figure.
	 * What about the size of the border ???dnoyeb???
	 */
	public HandleEnumeration handles() {
		return getDecoratedFigure().handles();
	}
	public Figure getDecoratedFigure() {
		return (Figure)getFigures().get(0);
	}	
}
