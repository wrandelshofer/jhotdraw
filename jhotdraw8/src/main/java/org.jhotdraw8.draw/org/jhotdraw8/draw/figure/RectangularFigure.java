/*
 * @(#)RectangularFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssRectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;

/**
 * Defines properties and default methods for figures which have a rectangular shape.
 */
public interface RectangularFigure extends Figure {
    @Nullable CssSizeStyleableKey X = new CssSizeStyleableKey("x", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    @Nullable CssSizeStyleableKey Y = new CssSizeStyleableKey("y", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    @Nullable CssSizeStyleableKey WIDTH = new CssSizeStyleableKey("width", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    @Nullable CssSizeStyleableKey HEIGHT = new CssSizeStyleableKey("height", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    @Nullable CssRectangle2DStyleableMapAccessor BOUNDS = new CssRectangle2DStyleableMapAccessor("bounds", X, Y, WIDTH, HEIGHT);

    default void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        set(X, width.getValue() < 0 ? x.add(width) : x);
        set(Y, height.getValue() < 0 ? y.add(height) : y);
        set(WIDTH, width.abs());
        set(HEIGHT, height.abs());
    }

    @Override
    default void translateInLocal(@NonNull CssPoint2D t) {
        set(X, getNonNull(X).add(t.getX()));
        set(Y, getNonNull(Y).add(t.getY()));
    }

    @NonNull
    @Override
    default CssRectangle2D getCssBoundsInLocal() {
        return getNonNull(BOUNDS);
    }
}
