/*
 * @(#)Constrainer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.constrain;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.draw.Figure;

/**
 * A <em>constrainer</em> constrains editing operations performed by
 * {@link org.jhotdraw.draw.tool.Tool}s and
 * {@link org.jhotdraw.draw.handle.Handle}s on a
 * {@link org.jhotdraw.draw.DrawingView}.
 * <p>
 * {@code Constrainer} objects are associated to {@code DrawingView}'s.
 * <p>
 * Constrainers can draw themselves onto the drawing view to visualize the
 * constraints that they impose. Typically by drawing a grid of some kind.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Constrainer {
    // ---
    // constant declarations
    // ---
    /**
     * A direction vector with distance of zero.
     */
    public final static Point2D DIRECTION_NEAREST = Point2D.ZERO;

    // ---
    // behavior methods
    // ---
    /**
     * Constrains the placement of a point towards the closest constraint
     * in any direction.
     * <p>
     * This method changes the point which is passed as a parameter.
     *
     * @param f The figure for which the point is to be constrained.
     * @param p A point on the drawing.
     * @return Returns the constrained point.
     */
    default Point2D constrainPoint(Figure f, Point2D p) {
        return translatePoint(f, p, DIRECTION_NEAREST);
    }

    /**
     * Snaps a point to the next constrained location in the specified
     * direction.
     * <p>
     * This method changes the point which is passed as a parameter.
     *
     * @param f The figure for which the point is to be constrained.
     * @param p A point on the drawing.
     * @param dir A direction vector. If the vector length is zero, then the
     *            nearest constrained location is used.
     * @return Returns the constrained point.
     */
    public Point2D translatePoint(Figure f, Point2D p, Point2D dir);

    /**
     * Constrains the placement of a rectangle towards the closest constraint
     * in any direction.
     * <p>
     * This method changes the location of the rectangle which is passed as a
     * parameter. This method does not change the size of the rectangle.
     *
     * @param f The figure for which the rectangle is to be constrained.
     * @param r A rectangle on the drawing.
     * @return Returns the constrained rectangle.
     */
    default Rectangle2D constrainRectangle(Figure f, Rectangle2D r) {
        return translateRectangle(f, r, DIRECTION_NEAREST);
    }
    /**
     * Snaps a rectangle into the the closest constraint position
     * in the given direction.
     * <p>
     * This method changes the location of the rectangle which is passed as a
     * parameter. This method does not change the size of the rectangle.
     *
     * @param f The figure for which points are to be constrained.
     * @param r A rectangle on the drawing.
     * @param dir A direction vector. If the vector length is zero, then the
     *            nearest constrained location is used.
     * @return Returns the constrained rectangle.
     */
    public Rectangle2D translateRectangle(Figure f, Rectangle2D r, Point2D dir);


    /**
     * Constrains the given angle (in degrees).
     * This method changes the angle which is passed as a parameter.
     * 
     * @param f The figure for which the angle is to be constrained.
     * @param angle The angle (in degrees).
     * @return The closest constrained angle (in radians).
     */
    default double constrainAngle(Figure f, double angle) {
        return translateAngle(f, angle,0);
    }

    /**
     * Snaps an angle (in degrees) to the closest constrained orientation
     * in the specified direction.
     * 
     * @param f The figure for which the angle is to be constrained.
     * @param angle The angle (in degrees).
     * @param dir A direction. If the direction is zero, then the
     *            nearest constrained location is used.
     * @return The closest constrained angle (in radians) in the specified
     * direction.
     */
    public double translateAngle(Figure f, double angle, double dir);
}
