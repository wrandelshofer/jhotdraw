/* @(#)Slice.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

/**
 * Defines a slice of a drawing.
 * <p>
 * The parent of a slice must be a {@link Layer} or a {@link Clipping} .
 * <p>
 * A slice may not have children.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface Slice extends Figure {

    @Override
    default boolean isSuitableParent(Figure newParent) {
        return newParent == null || (newParent instanceof Layer) || (newParent instanceof Clipping);
    }

    @Override
    default boolean isAllowsChildren() {
        return false;
    }

}
