/*
 * @(#)ViewBoxableDrawing.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssRectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;

public interface ViewBoxableDrawing extends Drawing {
    /**
     * Defines the x-coordinate of the view-box.
     * <p>
     * FIXME This is currently a mix-up of the width and height of the bounds=(0,0,width,height)
     * with x- and y-coordinates of the viewBox=(viewbox-x,viewbox-y,viewbox-width,viewbox-height).
     */
    @NonNull CssSizeStyleableKey VIEW_BOX_X = new CssSizeStyleableKey("x", CssSize.ZERO);
    /**
     * Defines the y-coordinate of the view-box.
     * <p>
     * FIXME This is currently a mix-up of the width and height of the bounds=(0,0,width,height)
     * with x- and y-coordinates of the viewBox=(viewbox-x,viewbox-y,viewbox-width,viewbox-height).
     */
    @NonNull CssSizeStyleableKey VIEW_BOX_Y = new CssSizeStyleableKey("y", CssSize.ZERO);

    /**
     * Defines the view-box of the drawing.
     * <p>
     * See <a href="https://www.w3.org/TR/SVG11/coords.html#ViewBoxAttribute>w3.org</a>.
     * <p>
     * FIXME This is currently a mix-up of the width and height of the bounds=(0,0,width,height)
     * with x- and y-coordinates of the viewBox=(viewbox-x,viewbox-y,viewbox-width,viewbox-height).
     */
    @NonNull CssRectangle2DStyleableMapAccessor VIEW_BOX = new CssRectangle2DStyleableMapAccessor("bounds",
            VIEW_BOX_X, VIEW_BOX_Y, WIDTH, HEIGHT);
}
