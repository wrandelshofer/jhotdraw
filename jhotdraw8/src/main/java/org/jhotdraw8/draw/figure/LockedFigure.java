/* @(#)LockedFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.collection.BooleanKey;

/**
 * Interface for figures which are always locked.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface LockedFigure extends Figure {

    /**
     * Whether this figure is not locked and all its parents are editable.
     *
     * @return false
     */
    @Override
    default boolean isEditable() {
        return false;
    }

    /**
     * Whether the figure is not locked and all its parents are editable.
     *
     * @return false
     */
    @Override
    default boolean isDeletable() {
        return false;
    }

    /**
     * Whether the figure is not locked and all its parents are editable.
     *
     * @return false
     */
    @Override
    default boolean isSelectable() {
        return false;
    }
}
