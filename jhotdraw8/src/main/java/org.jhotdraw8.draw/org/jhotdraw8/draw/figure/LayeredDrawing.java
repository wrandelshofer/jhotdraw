/*
 * @(#)LayeredDrawing.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;

/**
 * A layered drawing only accepts {@link Layer}s as children.
 */
public interface LayeredDrawing extends Drawing {
    /**
     * Only returns true if newChild is a {@link Layer}.
     *
     * @param newChild The new child figure.
     * @return true if instanceof Layer
     */
    @Override
    default boolean isSuitableChild(@NonNull Figure newChild) {
        return newChild instanceof Layer;
    }
}
