/*
 * @(#)IndexedArraySet.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.Collection;

public class IndexedArraySet<E> extends AbstractIndexedArraySet<E>{
    public IndexedArraySet() {
    }

    public IndexedArraySet(Collection<? extends E> col) {
        super(col);
    }

    @Override
    protected void onRemoved(E e) {
        // empty
    }

    @Override
    protected void onAdded(E e) {
        // empty
    }

    @Override
    protected Boolean onContains(E e) {
        // we do not have a fast implementation
        return null;
    }

    @Override
    protected boolean mayBeAdded(@NonNull E e) {
        return true;
    }
}
