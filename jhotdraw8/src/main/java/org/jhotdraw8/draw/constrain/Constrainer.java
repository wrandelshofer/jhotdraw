/* @(#)Constrainer.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.constrain;

import javafx.beans.Observable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;

/**
 * A <em>constrainer</em> constrains editing operations performed by
 * {@link org.jhotdraw8.draw.tool.Tool}s and
 * {@link org.jhotdraw8.draw.handle.Handle}s on a
 * {@link org.jhotdraw8.draw.DrawingView}.
 * <p>
 * {@code Constrainer} objects are associated to {@code DrawingView}'s.
 * <p>
 * Constrainers can draw themselves onto the drawing view to visualize the
 * constraints that they impose. Typically by drawing a grid of some kind.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Constrainer extends Observable {

    /**
     * Style class for constrainers which draw a grid.
     */
    String STYLECLASS_CONSTRAINER_MINOR_GRID = "constrainer-minor-grid";
    /**
     * Style class for constrainers which draw a grid.
     */
    String STYLECLASS_CONSTRAINER_MAJOR_GRID = "constrainer-major-grid";

    // ---
    // constant declarations
    // ---
    /**
     * A direction vector with distance of zero.
     */
    CssPoint2D DIRECTION_NEAREST = CssPoint2D.ZERO;

    // ---
    // behavior methods
    // ---
    /**
     * Constrains the placement of a point towards the closest constraint in any
     * direction.
     * <p>
     * This method changes the point which is passed as a parameter.
     *
     * @param f The figure for which the point is to be constrained.
     * @param p A point on the drawing.
     * @return Returns the constrained point.
     */ 
    default CssPoint2D constrainPoint( Figure f,  CssPoint2D p) {
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
     * nearest constrained location is used.
     * @return Returns the constrained point.
     */
    CssPoint2D translatePoint(Figure f, CssPoint2D p, CssPoint2D dir);

    /**
     * Constrains the placement of a rectangle towards the closest constraint in
     * any direction.
     * <p>
     * This method changes the location of the rectangle which is passed as a
     * parameter. This method does not change the size of the rectangle.
     *
     * @param f The figure for which the rectangle is to be constrained.
     * @param r A rectangle on the drawing.
     * @return Returns the constrained rectangle.
     */ 
    default CssRectangle2D constrainRectangle( Figure f,  CssRectangle2D r) {
        return translateRectangle(f, r, DIRECTION_NEAREST);
    }

    /**
     * Snaps a rectangle into the the closest constraint position in the given
     * direction.
     * <p>
     * This method changes the location of the rectangle which is passed as a
     * parameter. This method does not change the size of the rectangle.
     *
     * @param f The figure for which points are to be constrained.
     * @param r A rectangle on the drawing.
     * @param dir A direction vector. If the vector length is zero, then the
     * nearest constrained location is used.
     * @return Returns the constrained rectangle.
     */
    CssRectangle2D translateRectangle(Figure f, CssRectangle2D r, CssPoint2D dir);

    /**
     * Constrains the given angle (in degrees). This method changes the angle
     * which is passed as a parameter.
     *
     * @param f The figure for which the angle is to be constrained.
     * @param angle The angle (in degrees).
     * @return The closest constrained angle (in radians).
     */
    default double constrainAngle( Figure f, double angle) {
        return translateAngle(f, angle, 0);
    }

    /**
     * Snaps an angle (in degrees) to the closest constrained orientation in the
     * specified direction.
     *
     * @param f The figure for which the angle is to be constrained.
     * @param angle The angle (in degrees).
     * @param dir A direction. If the direction is zero, then the nearest
     * constrained location is used.
     * @return The closest constrained angle (in radians) in the specified
     * direction.
     */
    double translateAngle(Figure f, double angle, double dir);

    /**
     * Returns a node that renders the grid in view coordinates.
     *
     * @return the node
     */
    Node getNode();

    /**
     * Updates the node.
     *
     * @param drawingView the drawing view
     */
    void updateNode(DrawingView drawingView);
}
