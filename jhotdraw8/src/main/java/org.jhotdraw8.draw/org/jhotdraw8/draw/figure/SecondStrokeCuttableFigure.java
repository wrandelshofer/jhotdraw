/*
 * @(#)SecondStrokeCuttableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SymmetricCssPoint2DStyleableMapAccessor;

/**
 * A figure which supports cutting off the start and end of a stroked path.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface SecondStrokeCuttableFigure extends Figure {
    /**
     * Cuts off the specified number of pixels from the start of the stroked path.
     */
    CssSizeStyleableKey SECOND_STROKE_CUT_START = new CssSizeStyleableKey("second-stroke-cut-start", DirtyMask.of(DirtyBits.NODE), CssSize.ZERO);
    /**
     * Cuts off the specified number of pixels from the end of the stroked path.
     */
    CssSizeStyleableKey SECOND_STROKE_CUT_END = new CssSizeStyleableKey("second-stroke-cut-end", DirtyMask.of(DirtyBits.NODE), CssSize.ZERO);
    /**
     * Cuts off the specified number of pixels from the start and the end of the stroked path.
     */
    SymmetricCssPoint2DStyleableMapAccessor SECOND_STROKE_CUT = new SymmetricCssPoint2DStyleableMapAccessor("second-stroke-cut", SECOND_STROKE_CUT_START, SECOND_STROKE_CUT_END);
}
