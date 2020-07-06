/*
 * @(#)CollectionWrapper.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * CollectionWrapper.
 *
 * @author Werner Randelshofer
 */
public class CollectionWrapper<E> extends AbstractCollection<E> {
    private final ReadOnlyCollection<E> backingCollection;

    public CollectionWrapper(ReadOnlyCollection<E> backingCollection) {
        this.backingCollection = backingCollection;
    }

    @Override
    @NonNull
    public Iterator<E> iterator() {
        return backingCollection.iterator();
    }

    @Override
    public int size() {
        return backingCollection.size();
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked") E e = (E) o;
        return backingCollection.contains(e);
    }
}