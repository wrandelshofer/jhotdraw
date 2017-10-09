/* @(#)Connector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javax.annotation.Nonnull;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Intersection;
import org.jhotdraw8.geom.Transforms;

/**
 * A <em>connector</em> encapsulates a strategy for locating a connection point
 * for a connection figure on a target figure.
 *
 * @design.pattern Connector Strategy, Strategy. {@link Connector} encapsulates
 * a strategy for locating a connection point on a {@link Figure}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Connector {

    /**
     * Returns a point on the target figure for the specified connection figure
     * in local coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @return A point on the target figure in local coordinates of the target
     * figure.
     */
    @Nonnull
    Point2D getPositionInLocal(@Nonnull Figure connection, @Nonnull Figure target);

    /**
     * Returns the tangent vector on the target figure for the specified
     * connection figure in local coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @return A tangent vector on the target figure in local coordinates of the
     * target figure.
     */
    @Nonnull
    default Point2D getTangentInLocal(@Nonnull Figure connection, @Nonnull Figure target) {
        return new Point2D(1.0, 0.0);
    }

    /**
     * Returns a point on the target figure for the specified connection figure
     * in world coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @return A point on the target figure in world coordinates.
     */
    @Nonnull
    default Point2D getPositionInWorld(@Nonnull Figure connection, @Nonnull Figure target) {
        return target.localToWorld(getPositionInLocal(connection, target));
    }

    /**
     * Returns a point on the target figure for the specified connection figure
     * in parent coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @return A point on the target figure in parent coordinates.
     */
    @Nonnull
    default Point2D getPositionInParent(@Nonnull Figure connection, @Nonnull Figure target) {
        return Transforms.transform(target.getLocalToParent(), getPositionInLocal(connection, target));
    }

    /**
     * Returns a tangent vector on the target figure for the specified
     * connection figure in world coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @return A point on the target figure in world coordinates.
     */
    @Nonnull
    default Point2D getTangentInWorld(@Nonnull Figure connection, @Nonnull Figure target) {
        return Transforms.deltaTransform(target.getLocalToWorld(),
                getTangentInLocal(connection, target));
    }

    /**
     * Returns a tangent vector on the target figure for the specified
     * connection figure in parent coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @return A point on the target figure in parent coordinates.
     */
    @Nonnull
    default Point2D getTangentInParent(@Nonnull Figure connection, @Nonnull Figure target) {
        return Transforms.deltaTransform(target.getLocalToParent(),
                getTangentInLocal(connection, target));
    }

    /**
     * Clips the start of the provided line at the bounds of the target figure.
     * The line must be given in world coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @param sx x-coordinate at the start of the line
     * @param sy x-coordinate at the start of the line
     * @param ex x-coordinate at the end of the line
     * @param ey y-coordinate at the end of the line
     * @return the new start point in world coordinates
     */
    @Nonnull
    default Point2D chopStart(@Nonnull Figure connection, @Nonnull Figure target, double sx, double sy, double ex, double ey) {
        return chopStart(connection, target, new Point2D(sx, sy), new Point2D(ex, ey));
    }

    /**
     * Clips the start of the provided line at the bounds of the target figure.
     * The line must be given in world coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @param start the start of the line, should be inside the target figure
     * @param end the end of the line, should be outside the target figure
     * @return the new start point in world coordinates
     */
    @Nonnull
    default Point2D chopStart(@Nonnull Figure connection, @Nonnull Figure target, @Nonnull Point2D start, @Nonnull Point2D end) {
        Double t = intersect(connection, target, start, end);
        return t == null ? start : Geom.lerp(start, end, t);
    }

    /**
     * Clips the end of the provided line at the bounds of the target figure.
     * The line must be given in world coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @param start the start of the line
     * @param end the end of the line
     * @return the new end point in world coordinates
     */
    @Nonnull
    default Point2D chopEnd(@Nonnull Figure connection, @Nonnull Figure target, @Nonnull Point2D start, @Nonnull Point2D end) {
        return chopStart(connection, target, end, start);
    }

    /**
     * Returns the intersection of the line going from start to end with the
     * target figure. The line must be given in world coordinates.
     *
     * @param connection the connection figure
     * @param target the target figure
     * @param start the start point of the line in world coordinates, should be
     * inside the target figure
     * @param end the end point of the line in world coordinates, should be
     * outside the target figure
     * @return the intersection in the interval [0,1], null if no intersection.
     * In case of multiple intersections returns the largest value.
     */
    @Nonnull
    default Double intersect(@Nonnull Figure connection, @Nonnull Figure target, @Nonnull Point2D start, @Nonnull Point2D end) {
        Point2D s = target.worldToLocal(start);
        Point2D e = target.worldToLocal(end);
        Bounds b = target.getBoundsInLocal();
        Intersection i = Intersection.intersectLineRectangle(s, e, b);
        return i.isEmpty() ? null : i.getIntersections().lastKey();
    }
}
