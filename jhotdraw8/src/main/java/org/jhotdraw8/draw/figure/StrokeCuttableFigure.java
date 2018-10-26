/* @(#)StrokeCuttableFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.css.text.CssDimension;
import org.jhotdraw8.draw.key.CssDimensionStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SymmetricSize2DStyleableMapAccessor;

/**
 * A figure which supports cutting off the start and end of a stroked path.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface StrokeCuttableFigure extends Figure {
    /** Cuts off the specified number of pixels from the start of the stroked path. */
    public final static CssDimensionStyleableFigureKey STROKE_CUT_START = new CssDimensionStyleableFigureKey("stroke-cut-start", DirtyMask.of(DirtyBits.NODE), CssDimension.ZERO);
    /** Cuts off the specified number of pixels from the end of the stroked path. */
    public final static CssDimensionStyleableFigureKey STROKE_CUT_END = new CssDimensionStyleableFigureKey("stroke-cut-end", DirtyMask.of(DirtyBits.NODE), CssDimension.ZERO);
    /** Cuts off the specified number of pixels from the start and the end of the stroked path. */
    public static SymmetricSize2DStyleableMapAccessor STROKE_CUT = new SymmetricSize2DStyleableMapAccessor("stroke-cut", STROKE_CUT_START, STROKE_CUT_END);

    default double getStrokeCutStart() {
       return getStyled(STROKE_CUT_START).getValue();
    }
    default double getStrokeCutEnd() {
       return getStyled(STROKE_CUT_END).getValue();
    }
}
