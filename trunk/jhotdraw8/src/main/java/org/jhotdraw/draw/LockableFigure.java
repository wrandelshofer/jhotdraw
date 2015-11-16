/* @(#)LockableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw;

import org.jhotdraw.collection.BooleanKey;

/**
 * LockableFigure.
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
     * This key is used by the user to prevent accidental selection or editing
     * of a figure.
     */
    public static BooleanKey LOCKED = new BooleanKey("locked", false);
    /**
     * Whether the figure is editable by the user. Default value: {@code true}.
     * <p>
     * A non-editable figure can not be selected or changed by the user, unless the
     * application makes the figure editable.
     * <p>
     * Making a figure non-editable also makes all its child figures non-editable.
     * <p>
     * This key is used to programmatically prevent that a user can select or
     * edit a figure.
     */
    public static BooleanKey USER_EDITABLE = new BooleanKey("userEditable", true);
    
    /**
     * Whether the figure or one if its ancestors is disabled or locked.
     *
     * @return true if the user may select the figure
     */
    @Override
    default boolean isDisabledOrUneditable() {
        Figure node = this;
        while (node != null) {
            if (!node.get(USER_EDITABLE) || node.get(LOCKED)) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }
    
    /**
     * Whether the figure or one if its ancestors is locked.
     *
     * @return true if the figure or one its ancestors is locked
     */
    default boolean isLocked() {
        Figure node = this;
        while (node != null) {
            if (node.get(LOCKED)) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    /**
     * Whether the figure or one if its ancestors is uneditable.
     *
     * @return true if the figure or one its ancestors is uneditable.
     */
    default boolean isUneditable() {
        Figure node = this;
        while (node != null) {
            if (!node.get(USER_EDITABLE)) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }    
}
