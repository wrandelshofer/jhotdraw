/*
 * @(#)ImageableFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.key.NullableUriStyleableKey;

/**
 * Mixin interface for figures that have an image source.
 */
public interface ImageableFigure extends Figure {
    /**
     * The URI of the image.
     * <p>
     * This property is also set on the ImageView node, so that
     * {@link org.jhotdraw8.draw.io.SvgExportOutputFormat} can pick it up.
     */
    @NonNull NullableUriStyleableKey IMAGE_URI = new NullableUriStyleableKey("src", null);
}
