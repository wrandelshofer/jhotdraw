/* @(#)Locator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */


package org.jhotdraw.draw.locator;

import org.jhotdraw.draw.Figure;

import java.awt.geom.Point2D;

/**
 * A <em>locator</em> encapsulates a strategy for locating a point on a
 * {@link Figure}.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Strategy</em><br>
 * {@code Locator} encapsulates a strategy for locating a point on a
 * {@code Figure}.<br>
 * Strategy: {@link Locator}; Context: {@link Figure}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Locator {

	/**
	 * Locates a position on the provided figure.
	 * @return a point on the figure.
	 */
	public Point2D.Double locate(Figure owner);
	/**
	 * Locates a position on the provided figure relative to the dependent
         * figure.
	 * @return a point on the figure.
	 */
	public Point2D.Double locate(Figure owner, Figure dependent);
}