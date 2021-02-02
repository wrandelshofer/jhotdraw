/*
 * @(#)StrokeCuttableFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.SymmetricCssPoint2DStyleableMapAccessor;

/**
 * A figure which supports cutting off the start and end of a stroked path.
 *
 * @author Werner Randelshofer
 */
public interface StrokeCuttableFigure extends Figure {
    /**
     * Cuts off the specified number of pixels from the start of the stroked path.
     */
    @Nullable CssSizeStyleableKey STROKE_CUT_START = new CssSizeStyleableKey("stroke-cut-start", CssSize.ZERO);
    /**
     * Cuts off the specified number of pixels from the end of the stroked path.
     */
    @Nullable CssSizeStyleableKey STROKE_CUT_END = new CssSizeStyleableKey("stroke-cut-end", CssSize.ZERO);
    /**
     * Cuts off the specified number of pixels from the start and the end of the stroked path.
     */
    @Nullable SymmetricCssPoint2DStyleableMapAccessor STROKE_CUT = new SymmetricCssPoint2DStyleableMapAccessor("stroke-cut", STROKE_CUT_START, STROKE_CUT_END);

    default double getStrokeCutStart() {
        return getStyledNonNull(STROKE_CUT_START).getConvertedValue();
    }

    default double getStrokeCutEnd() {
        return getStyledNonNull(STROKE_CUT_END).getConvertedValue();
    }
}
