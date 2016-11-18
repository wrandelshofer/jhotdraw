/* @(#)LockableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.collection.BooleanKey;

/**
 * LockableFigure.
 *
 * @design.pattern Figure Mixin, Traits.
 * 
 * @author Werner Randelshofer
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
    public static BooleanKey LOCKED = new BooleanKey("locked", false);

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
