/* @(#)ShapeableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssInsetsStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;

public interface PaddableFigure extends Figure {
    CssSizeStyleableFigureKey PADDING_BOTTOM = new CssSizeStyleableFigureKey("paddingBottom", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    CssSizeStyleableFigureKey PADDING_LEFT = new CssSizeStyleableFigureKey("paddingLeft", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    CssSizeStyleableFigureKey PADDING_RIGHT = new CssSizeStyleableFigureKey("paddingRight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    CssSizeStyleableFigureKey PADDING_TOP = new CssSizeStyleableFigureKey("paddingTop", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    CssInsetsStyleableMapAccessor PADDING = new CssInsetsStyleableMapAccessor("padding", PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT);


}
