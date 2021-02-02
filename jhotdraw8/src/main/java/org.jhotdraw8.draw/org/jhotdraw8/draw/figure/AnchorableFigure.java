/*
 * @(#)AnchorableFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.draw.key.DoubleStyleableKey;
import org.jhotdraw8.draw.key.SymmetricPoint2DStyleableMapAccessor;

/**
 * AnchorableFigure has an anchor attribute which is a relative location
 * to the bounds of the figure. The anchor is used when the figure is aligned
 * with the grid constrainer.
 *
 * @author Werner Randelshofer
 */
public interface AnchorableFigure extends Figure {
    /**
     * Relative position of the x-anchor. 0.0 = left, 0.5 = center, 1.0 = right.
     */
    DoubleStyleableKey ANCHOR_X = new DoubleStyleableKey("anchorX", 0.0);
    /**
     * Relative position of the y-anchor. 0.0 = top, 0.5 = center, 1.0 = bottom.
     */
    DoubleStyleableKey ANCHOR_Y = new DoubleStyleableKey("anchorY", 0.0);

    /**
     * Combined anchor value.
     */
    SymmetricPoint2DStyleableMapAccessor ANCHOR = new SymmetricPoint2DStyleableMapAccessor("anchor", ANCHOR_X, ANCHOR_Y);

}
