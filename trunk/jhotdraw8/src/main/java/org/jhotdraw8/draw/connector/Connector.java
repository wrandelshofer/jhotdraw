/* @(#)Connector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.connector;

import javafx.geometry.Point2D;
import org.jhotdraw8.draw.figure.Figure;

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
    Point2D getPositionInLocal(Figure connection, Figure target);

    /**
     * Returns a point on the target figure for the specified connection figure
     * in world coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @return A point on the target figure in drawing coordinates.
     */
    default Point2D getPositionInWorld(Figure connection, Figure target) {
        return target.localToWorld(getPositionInLocal(connection, target));
    }

    /**
     * Chops the start of the provided line given in drawing coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @param startX x-coordinate at the start of the line
     * @param startY x-coordinate at the start of the line
     * @param endX x-coordinate at the end of the line
     * @param endY y-coordinate at the end of the line
     * @return the new start point in drawing coordinates
     */
    Point2D chopStart(Figure connection, Figure target, double startX, double startY, double endX, double endY);

    /**
     * Chops the end of the provided line.
     *
     * @param connection a connection figure
     * @param target the target
     * @param startX x-coordinate at the start of the line
     * @param startY x-coordinate at the start of the line
     * @param endX x-coordinate at the end of the line
     * @param endY y-coordinate at the end of the line
     * @return the new end point
     */
    default Point2D chopEnd(Figure connection, Figure target, double startX, double startY, double endX, double endY) {
        return chopStart(connection, target, endX, endY, startX, startY);
    }

    /**
     * Chops the start of the provided line in world coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @param start the start of the line
     * @param end the end of the line
     * @return the new start point in drawing coordinates
     */
    default Point2D chopStart(Figure connection, Figure target, Point2D start, Point2D end) {
        return chopStart(connection, target, start.getX(), start.getY(), end.getX(), end.getY());
    }

    /**
     * Chops the end of the provided line in world coordinates.
     *
     * @param connection a connection figure
     * @param target the target
     * @param start the start of the line
     * @param end the end of the line
     * @return the new end point in drawing coordinates
     */
    default Point2D chopEnd(Figure connection, Figure target, Point2D start, Point2D end) {
        return chopStart(connection, target, end.getX(), end.getY(), start.getX(), start.getY());
    }
}
