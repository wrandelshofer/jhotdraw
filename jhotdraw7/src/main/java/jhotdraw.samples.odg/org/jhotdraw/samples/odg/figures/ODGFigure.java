/* @(#)ODGFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.odg.figures;

import org.jhotdraw.draw.Figure;

/**
 * ODGFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ODGFigure extends Figure {
    /**
     * Returns true, if this figure is empty for one of the following
     * reasons:
     * <ul>
     * <li>A group has no children</li>
     * <li>A path has less than two points</li>
     * <li>An ellipse or a rectangle has a width or a height of 0</li>
     * <li>A text has no characters</li>
     * </ul>
     */
    public boolean isEmpty();
}
