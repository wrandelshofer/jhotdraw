/*
 * @(#)Constrainer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.constrain;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;



/**
 * A <em>constrainer</em> constrains editing operations performed by
 * {@link org.jhotdraw.draw.tool.Tool}s and
 * {@link org.jhotdraw.draw.handle.Handle}s on a {@link DrawingView}.
 * <p>
 * {@code Constrainer} objects are associated to {@code DrawingView}'s.
 * <p>
 * Constrainers can draw themselves onto the drawing view to visualize the
 * constraints that they impose. Typically by drawing a grid of some kind.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public interface Constrainer {
    /**
     * Constrains the placement of a point towards the closest constraint
     * in any direction.
     * <p>
     * This method changes the point which is passed as a parameter.
     *
     * @param p A point on the drawing.
     * @return Returns the constrained point.
     */
    default Point2D constrainPoint(Point2D p) {
        return constrainPoint(p, Point2D.ZERO);
    }

    /**
     * Moves a point to the closest constrained location in the specified
     * direction.
     * <p>
     * This method changes the point which is passed as a parameter.
     *
     * @param p A point on the drawing.
     * @param dir A direction vector. If the vector length is zero, then the
     *            nearest constrained location is used.
     * @return Returns the constrained point.
     */
    public Point2D constrainPoint(Point2D p, Point2D dir);

    /**
     * Constrains the placement of a rectangle towards the closest constraint
     * in any direction.
     * <p>
     * This method changes the location of the rectangle which is passed as a
     * parameter. This method does not change the size of the rectangle.
     *
     * @param r A rectangle on the drawing.
     * @return Returns the constrained rectangle.
     */
    default Rectangle2D constrainRectangle(Rectangle2D r) {
        return constrainRectangle(r, Point2D.ZERO);
    }
    /**
     * Constrains the placement of a rectangle towards the closest constraint
     * in the given direction.
     * <p>
     * This method changes the location of the rectangle which is passed as a
     * parameter. This method does not change the size of the rectangle.
     *
     * @param r A rectangle on the drawing.
     * @param dir A direction vector. If the vector length is zero, then the
     *            nearest constrained location is used.
     * @return Returns the constrained rectangle.
     */
    public Rectangle2D constrainRectangle(Rectangle2D r, Point2D dir);


    /**
     * Constrains the given angle (in radians).
     * This method changes the angle which is passed as a parameter.
     * 
     * @param angle The angle (in degrees).
     * @return The closest constrained angle (in radians).
     */
    default double constrainAngle(double angle) {
        return constrainAngle(angle,Point2D.ZERO);
    }

    /**
     * Moves the given angle (in radians) to the closest constrained orientation
     * in the specified direction.
     * 
     * @param angle The angle (in degrees).
     * @param dir A direction vector. If the vector length is zero, then the
     *            nearest constrained location is used.
     * @return The closest constrained angle (in radians) in the specified
     * direction.
     */
    public double constrainAngle(double angle, Point2D dir);

}
