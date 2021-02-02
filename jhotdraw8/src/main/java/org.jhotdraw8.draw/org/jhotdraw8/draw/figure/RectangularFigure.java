/*
 * @(#)RectangularFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssRectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;

/**
 * Defines properties and default methods for figures which have a rectangular shape.
 */
public interface RectangularFigure extends Figure {
    @NonNull CssSizeStyleableKey X = new CssSizeStyleableKey("x", CssSize.ZERO);
    @NonNull CssSizeStyleableKey Y = new CssSizeStyleableKey("y", CssSize.ZERO);
    @NonNull CssSizeStyleableKey WIDTH = new CssSizeStyleableKey("width", CssSize.ZERO);
    @NonNull CssSizeStyleableKey HEIGHT = new CssSizeStyleableKey("height", CssSize.ZERO);
    @NonNull CssRectangle2DStyleableMapAccessor BOUNDS = new CssRectangle2DStyleableMapAccessor("bounds", X, Y, WIDTH, HEIGHT);

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
    default CssRectangle2D getCssLayoutBounds() {
        return getNonNull(BOUNDS);
    }
}
