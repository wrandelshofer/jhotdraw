/* @(#)ShapeableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssInsetsStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;

public interface PaddableFigure extends Figure {
    CssSizeStyleableKey PADDING_BOTTOM = new CssSizeStyleableKey("paddingBottom", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    CssSizeStyleableKey PADDING_LEFT = new CssSizeStyleableKey("paddingLeft", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    CssSizeStyleableKey PADDING_RIGHT = new CssSizeStyleableKey("paddingRight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    CssSizeStyleableKey PADDING_TOP = new CssSizeStyleableKey("paddingTop", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    CssInsetsStyleableMapAccessor PADDING = new CssInsetsStyleableMapAccessor("padding", PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT);


}
