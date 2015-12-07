/* @(#)Locator.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.locator;

import javafx.geometry.Point2D;
import org.jhotdraw.draw.figure.Figure;

/**
 * A <em>locator</em> encapsulates a strategy for locating a point on a
 * {@link Figure}.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p>
 * <em>Strategy</em><br> {@code Locator} encapsulates a strategy for locating a
 * point on a {@code Figure}.<br>
 * Strategy: {@link Locator}; Context: {@link Figure}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Locator {

    /**
     * Locates a position on the provided figure.
     *
     * @param owner provided figure
     * @return a point on the figure in local coordinates.
     */
    public Point2D locate(Figure owner);

    /**
     * Locates a position on the provided figure relative to the dependent
     * figure.
     *
     * @param owner provided figure
     * @param dependent dependent figure
     * @return a point on the figure in local coordinates.
     */
    public Point2D locate(Figure owner, Figure dependent);
}
