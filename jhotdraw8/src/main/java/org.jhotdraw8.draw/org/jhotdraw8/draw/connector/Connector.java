/*
 * @(#)Connector.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.isect.IntersectionPoint;
import org.jhotdraw8.geom.isect.IntersectionResult;
import org.jhotdraw8.geom.isect.Intersections;

import java.awt.geom.Rectangle2D;

/**
 * A <em>connector</em> encapsulates a strategy for locating a connection point
 * for a connection figure on a target figure.
 *
 * @author Werner Randelshofer
 */
public interface Connector {

    /**
     * Returns a point on the target figure for the specified connection figure
     * in local coordinates.
     *
     * @param connection a connection figure
     * @param target     the target
     * @return A point on the target figure in local coordinates of the target
     * figure.
     */
    @NonNull Point2D getPositionInLocal(Figure connection, Figure target);

    /**
     * Returns the tangent vector on the target figure for the specified
     * connection figure in local coordinates.
     *
     * @param connection a connection figure
     * @param target     the target
     * @return A tangent vector on the target figure in local coordinates of the
     * target figure.
     */
    default Point2D getTangentInLocal(Figure connection, Figure target) {
        return new Point2D(1.0, 0.0);
    }

    /**
     * Returns a point on the target figure for the specified connection figure
     * in world coordinates.
     *
     * @param connection a connection figure
     * @param target     the target
     * @return A point on the target figure in world coordinates.
     */
    @NonNull
    default Point2D getPositionInWorld(Figure connection, @NonNull Figure target) {
        return target.localToWorld(getPositionInLocal(connection, target));
    }

    /**
     * Returns a point on the target figure for the specified connection figure
     * in parent coordinates.
     *
     * @param connection a connection figure
     * @param target     the target
     * @return A point on the target figure in parent coordinates.
     */
    @NonNull
    default Point2D getPositionInParent(Figure connection, @NonNull Figure target) {
        return FXTransforms.transform(target.getLocalToParent(), getPositionInLocal(connection, target));
    }

    /**
     * Returns a tangent vector on the target figure for the specified
     * connection figure in world coordinates.
     *
     * @param connection a connection figure
     * @param target     the target
     * @return A point on the target figure in world coordinates.
     */
    default Point2D getTangentInWorld(Figure connection, @NonNull Figure target) {
        return FXTransforms.deltaTransform(target.getLocalToWorld(),
                getTangentInLocal(connection, target));
    }

    /**
     * Returns a tangent vector on the target figure for the specified
     * connection figure in parent coordinates.
     *
     * @param connection a connection figure
     * @param target     the target
     * @return A point on the target figure in parent coordinates.
     */
    default Point2D getTangentInParent(Figure connection, @NonNull Figure target) {
        return FXTransforms.deltaTransform(target.getLocalToParent(),
                getTangentInLocal(connection, target));
    }

    /**
     * Clips the start of the provided line at the bounds of the target figure.
     * The line must be given in world coordinates.
     *
     * @param ctx
     * @param connection a connection figure
     * @param target     the target
     * @param sx         x-coordinate at the start of the line
     * @param sy         x-coordinate at the start of the line
     * @param ex         x-coordinate at the end of the line
     * @param ey         y-coordinate at the end of the line
     * @return the new start point in world coordinates
     */
    default IntersectionPoint chopStart(RenderContext ctx, Figure connection, @NonNull Figure target, double sx, double sy, double ex, double ey) {
        return chopStart(ctx, connection, target, new Point2D(sx, sy), new Point2D(ex, ey));
    }

    /**
     * Clips the start of the provided line at the bounds of the target figure.
     * The line must be given in world coordinates.
     *
     * @param ctx        the render context
     * @param connection a connection figure
     * @param target     the target
     * @param start      the start of the line, should be inside the target figure
     * @param end        the end of the line, should be outside the target figure
     * @return the new start point in world coordinates
     */
    default IntersectionPoint chopStart(RenderContext ctx, Figure connection, @NonNull Figure target, @NonNull Point2D start, @NonNull Point2D end) {
        IntersectionPoint ip = intersect(ctx, connection, target, start, end);
        Point2D tangent = end.subtract(start);
        return ip == null ? new IntersectionPoint(start.getX(), start.getY(), 0, tangent.getX(), tangent.getY(), 0, tangent.getX(), tangent.getY()) :
                new IntersectionPoint(Geom.lerp(start.getX(), start.getY(), end.getX(), end.getY(), ip.getParameterA()), ip.getParameterA(), ip.getTangentA(), ip.getParameterB(), ip.getTangentB());
    }

    /**
     * Clips the end of the provided line at the bounds of the target figure.
     * The line must be given in world coordinates.
     *
     * @param ctx
     * @param connection a connection figure
     * @param target     the target
     * @param start      the start of the line
     * @param end        the end of the line
     * @return the new end point in world coordinates
     */
    default IntersectionPoint chopEnd(RenderContext ctx, Figure connection, @NonNull Figure target, @NonNull Point2D start, @NonNull Point2D end) {
        return chopStart(ctx, connection, target, end, start);
    }

    /**
     * Returns the intersection of the line going from start to end with the
     * target figure. The line must be given in world coordinates.
     *
     *
     * @param ctx
     * @param connection the connection figure
     * @param target     the target figure
     * @param start      the start point of the line in world coordinates, should be
     *                   inside the target figure
     * @param end        the end point of the line in world coordinates, should be
     *                   outside the target figure
     * @return the intersection in the interval [0,1], null if no intersection.
     * In case of multiple intersections returns the largest value.
     */
    default IntersectionPoint intersect(RenderContext ctx, Figure connection, @NonNull Figure target, @NonNull Point2D start, @NonNull Point2D end) {
        Point2D s = target.worldToLocal(start);
        Point2D e = target.worldToLocal(end);
        Bounds b = target.getLayoutBounds();
        IntersectionResult i = Intersections.intersectLineRectangle(
                new java.awt.geom.Point2D.Double(s.getX(), s.getY()),
                new java.awt.geom.Point2D.Double(e.getX(), e.getY()),
                new Rectangle2D.Double(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight()));
        return i.getLastIntersectionPoint();
    }
}
