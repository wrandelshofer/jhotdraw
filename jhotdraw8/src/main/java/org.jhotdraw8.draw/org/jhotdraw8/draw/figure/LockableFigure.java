/*
 * @(#)LockableFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.styleable.BooleanStyleableKey;

/**
 * LockableFigure.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface LockableFigure extends Figure {

    /**
     * Whether the figure is locked. Default value: {@code false}.
     * <p>
     * A locked figure can not be selected or changed by the user, unless the
     * user explicity unlocks the figure.
     * <p>
     * Locking a figure also locks all its child figures.
     * <p>
     * This key can be used by the user to prevent accidental selection or
     * editing of a figure.
     */
    BooleanStyleableKey LOCKED = new BooleanStyleableKey("locked", false);

    /**
     * Whether this figure is not locked and all its parents are editable.
     *
     * @return true if this figure is not locked and all parents are editable
     */
    @Override
    default boolean isEditable() {
        if (get(LOCKED)) {
            return false;
        }
        Figure node = getParent();
        while (node != null) {
            if (!node.isEditable()) {
                return false;
            }
            node = node.getParent();
        }
        return true;
    }

    /**
     * Whether the figure is not locked and all its parents are editable.
     *
     * @return true if this figure is not locked and all parents are deletable.
     */
    @Override
    default boolean isDeletable() {
        return isEditable();
        /*
        if (get(LOCKED)) {
            return false;
        }
        Figure node = getParent();
        while (node != null) {
            if (!node.isDeletable()) {
                return false;
            }
            node = node.getParent();
        }
        return true;*/
    }

    /**
     * Whether the figure is not locked and all its parents are editable.
     *
     * @return true if this figure is not locked and all parents are selectable.
     */
    @Override
    default boolean isSelectable() {
        return isEditable();
        /*
        if (get(LOCKED)) {
            return false;
        }
        Figure node = getParent();
        while (node != null) {
            if (!node.isSelectable()) {
                return false;
            }
            node = node.getParent();
        }
        return true;*/
    }
}
