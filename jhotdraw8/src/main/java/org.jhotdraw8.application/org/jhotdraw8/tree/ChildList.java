/*
 * @(#)ChildList.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.tree;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.IndexedArraySet;

/**
 * A child list for implementations of the {@link TreeNode} interface.
 * <p>
 * This list maintains the parent of tree nodes that are added/removed
 * from the child list, as described in {@link TreeNode#getChildren()}.
 */
public class ChildList<E extends TreeNode<E>> extends IndexedArraySet<E> {

    private final E parent;

    public ChildList(E parent) {
        this.parent = parent;

    }


    @SuppressWarnings("unchecked")
    @Override
    public int indexOf(Object o) {
        if (((E) o).getParent() == parent) {
            return super.indexOf(o);// linear search!
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return ((E) o).getParent() == parent;
    }

    @Override
    protected void onAdded(@NonNull E e) {
        E oldParent = e.getParent();
        if (oldParent != null && oldParent != parent) {
            oldParent.getChildren().remove(e);
        }
        e.setParent(parent);
    }

    @Override
    protected void onRemoved(@NonNull E e) {
        e.setParent(null);
    }

    @Override
    protected boolean doAdd(int index, @NonNull E element, boolean checkForDuplicates) {
        if (parent.isSuitableChild(element) &&
                element.isSuitableParent(parent)) {
            E oldParent = element.getParent();
            if (oldParent != parent) {
                return super.doAdd(index, element, false);
            } else {
                return super.doAdd(index, element, true);// linear search!
            }
        } else {
            return false;
        }
    }

}
