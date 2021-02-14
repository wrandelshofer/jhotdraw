/*
 * @(#)IndexedHashSet.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@code Set} that provides precise control where each element is inserted.
 * <p>
 * The set is backed by an array and a hash set.
 *
 * @author Werner Randelshofer
 */
public class IndexedHashSet<E> extends AbstractIndexedArraySet<E> {
    /**
     * The hash set.
     * <p>
     * FIXME implement this as an inline hash array.
     */
    private final @NonNull Set<E> set = new HashSet<>();

    public IndexedHashSet() {
    }

    public IndexedHashSet(Collection<? extends E> col) {
        setAll(col);
    }


    @Override
    protected void onAdded(@NonNull E e) {
        set.add(e);
    }

    @Override
    protected Boolean onContains(E e) {
        return set.contains(e);
    }

    @Override
    protected boolean mayBeAdded(@NonNull E e) {
        return true;
    }

    @Override
    protected void onRemoved(@NonNull E e) {
        set.remove(e);
    }
}
