/*
 * @(#)Locator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.locator;

import javafx.geometry.Point2D;
import org.jhotdraw8.draw.figure.Figure;

/**
 * A <em>locator</em> encapsulates a strategy for locating a point on a
 * {@link Figure}.
 *
 * @author Werner Randelshofer
 * @design.pattern Locator Strategy, Strategy. {@link Locator} encapsulates a
 * strategy for locating a point on a {@link Figure}.
 */
public interface Locator {

    /**
     * Locates a position on the provided figure.
     *
     * @param owner provided figure
     * @return a point on the figure in local coordinates.
     */
    Point2D locate(Figure owner);

    /**
     * Locates a position on the provided figure relative to the dependent
     * figure.
     *
     * @param owner     provided figure
     * @param dependent dependent figure
     * @return a point on the figure in local coordinates.
     */
    Point2D locate(Figure owner, Figure dependent);
}
