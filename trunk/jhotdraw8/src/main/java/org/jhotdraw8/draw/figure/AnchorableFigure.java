/* @(#)AnchorableFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.SymmetricPoint2DStyleableMapAccessor;

/**
 * AnchorableFigure has an anchor attribute which is a relative location
 * to the bounds of the figure. The anchor is used when the figure is aligned
 * with the grid constrainer.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface AnchorableFigure extends Figure {
    /** Relative position of the x-anchor. 0.0 = left, 0.5 = center, 1.0 = right. */
    public final static DoubleStyleableFigureKey ANCHOR_X = new DoubleStyleableFigureKey("anchorX", DirtyMask.of(), 0.0);
    /** Relative position of the y-anchor. 0.0 = top, 0.5 = center, 1.0 = bottom. */
    public final static DoubleStyleableFigureKey ANCHOR_Y = new DoubleStyleableFigureKey("anchorY", DirtyMask.of(), 0.0);
    
    /** Combined anchor value. */
   public final static SymmetricPoint2DStyleableMapAccessor ANCHOR = new SymmetricPoint2DStyleableMapAccessor("anchor", ANCHOR_X, ANCHOR_Y);

}
