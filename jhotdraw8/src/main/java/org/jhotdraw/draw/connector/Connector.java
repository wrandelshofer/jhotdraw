/* @(#)Connector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import javafx.geometry.Point2D;
import org.jhotdraw.draw.Figure;

/**
 * A <em>connector</em> encapsulates a strategy for locating a connection point
 * on a target {@code Figure}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Connector {

    /**
     * Returns a point on the target figure for the specified connection figure
     * in drawing coordinates.
     *
     * @param target the target figure
     * @param connection a connection figure
     * @return A point on the target figure.
     */
    Point2D getPosition(Figure target, Figure connection);

    /**
     * Chops the start of the provided line given in drawing coordinates.
     *
     * @param target the target figure
     * @param connection a connection figure
     * @param startX x-coordinate at the start of the line
     * @param startY x-coordinate at the start of the line
     * @param endX x-coordinate at the end of the line
     * @param endY y-coordinate at the end of the line
     * @return the new start point in drawing coordinates
     */
    Point2D chopStart(Figure target, Figure connection, double startX, double startY, double endX, double endY);

    /**
     * Chops the end of the provided line.
     *
     * @param target the target figure
     * @param connection a connection figure
     * @param startX x-coordinate at the start of the line
     * @param startY x-coordinate at the start of the line
     * @param endX x-coordinate at the end of the line
     * @param endY y-coordinate at the end of the line
     * @return the new end point
     */
    default Point2D chopEnd(Figure target, Figure connection, double startX, double startY, double endX, double endY) {
        return chopStart(target, connection, endX, endY, startX, startY);
    }

    /**
     * Chops the start of the provided line in drawing coordinates.
     *
     * @param target the target figure
     * @param connection a connection figure
     * @param start the start of the line
     * @param end the end of the line
     * @return the new start point in drawing coordinates
     */
    default Point2D chopStart(Figure target, Figure connection, Point2D start, Point2D end) {
        return chopStart(target, connection, start.getX(), start.getY(), end.getX(), end.getY());
    }

    /**
     * Chops the end of the provided line in drawing coordinates.
     *
     * @param target the target figure
     * @param connection a connection figure
     * @param start the start of the line
     * @param end the end of the line
     * @return the new end point in drawing coordinates
     */
    default Point2D chopEnd(Figure target, Figure connection, Point2D start, Point2D end) {
        return chopStart(target, connection, end.getX(), end.getY(), start.getX(), start.getY());
    }
}
