/*
 * @(#)PaddableFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssInsetsStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;

public interface PaddableFigure extends Figure {
    @Nullable CssSizeStyleableKey PADDING_BOTTOM = new CssSizeStyleableKey("paddingBottom", CssSize.ZERO);
    @Nullable CssSizeStyleableKey PADDING_LEFT = new CssSizeStyleableKey("paddingLeft", CssSize.ZERO);
    @Nullable CssSizeStyleableKey PADDING_RIGHT = new CssSizeStyleableKey("paddingRight", CssSize.ZERO);
    @Nullable CssSizeStyleableKey PADDING_TOP = new CssSizeStyleableKey("paddingTop", CssSize.ZERO);
    @Nullable CssInsetsStyleableMapAccessor PADDING = new CssInsetsStyleableMapAccessor("padding", PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT);


}
