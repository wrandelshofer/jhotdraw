/* @(#)StrokeCuttableFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SymmetricCssPoint2DStyleableMapAccessor;

/**
 * A figure which supports cutting off the start and end of a stroked path.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface StrokeCuttableFigure extends Figure {
    /** Cuts off the specified number of pixels from the start of the stroked path. */
    public final static CssSizeStyleableFigureKey STROKE_CUT_START = new CssSizeStyleableFigureKey("stroke-cut-start", DirtyMask.of(DirtyBits.NODE), CssSize.ZERO);
    /** Cuts off the specified number of pixels from the end of the stroked path. */
    public final static CssSizeStyleableFigureKey STROKE_CUT_END = new CssSizeStyleableFigureKey("stroke-cut-end", DirtyMask.of(DirtyBits.NODE), CssSize.ZERO);
    /** Cuts off the specified number of pixels from the start and the end of the stroked path. */
    public static SymmetricCssPoint2DStyleableMapAccessor STROKE_CUT = new SymmetricCssPoint2DStyleableMapAccessor("stroke-cut", STROKE_CUT_START, STROKE_CUT_END);

    default double getStrokeCutStart() {
       return getStyledNonnull(STROKE_CUT_START).getConvertedValue();
    }
    default double getStrokeCutEnd() {
       return getStyledNonnull(STROKE_CUT_END).getConvertedValue();
    }
}
