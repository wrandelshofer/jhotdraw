/*
 * @(#)ReadOnlyCollection.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Provides a read-only API for a collection.
 *
 * @param <E> the element type
 */
public interface ReadOnlyCollection<E> extends Iterable<E> {

    /**
     * Returns the size of the collection.
     *
     * @return the size
     */
    int size();

    /**
     * Returns true if the collection is empty.
     *
     * @return true if empty
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    default Object[] toArray() {
        return toArray(new Object[size()]);
    }

    /**
     * Converts the collection to an array.
     *
     * @param <T> the element type
     * @param a   a template array
     * @return an array
     */
    default <T> T[] toArray(T[] a) {
        // Estimate size of array; be prepared to see more or fewer elements
        int size = size();
        @SuppressWarnings("unchecked")
        T[] r = a.length >= size ? a : (T[]) Arrays.copyOf(a, size, a.getClass());
        Iterator<E> it = iterator();

        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext()) { // fewer elements than expected
                throw new ConcurrentModificationException("fewer elements than expected. expected=" + size);
            }
            @SuppressWarnings("unchecked")
            T t = (T) it.next();
            r[i] = t;
        }
        if (it.hasNext()) {
            throw new ConcurrentModificationException("more elements than expected. expected=" + size);
        }
        return r;
    }

    /**
     * Returns a stream.
     *
     * @return a stream
     */
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default void copyInto(Object[] out, int offset) {
        int i = offset;
        for (E e : this) {
            out[i++] = e;
        }
    }

    boolean contains(Object e);

    /**
     * Returns true if this collection contains all elements of that collection.
     *
     * @param c another collection
     * @return true if this collection contains all of c
     */
    default boolean containsAll(Iterable<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Wraps this collection in the Collection API - without copying.
     *
     * @return the wrapped collection
     */
    default Collection<E> asCollection() {
        return new CollectionWrapper<>(this);
    }
}
