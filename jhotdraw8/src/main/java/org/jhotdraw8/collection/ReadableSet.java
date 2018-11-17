/* @(#)ReadableSet.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.collections.ObservableSet;

import java.util.Set;

/**
 * Provides a read-only API for a set collection.
 * <p>
 * Note: a ReadableSet is not equal to a non-readable {@link Set}.
 * To compare a ReadableSet to a Set, you have to wrap the Set into a {@link ReadableSetWrapper}.
 *
 * @param <E> the element type
 */
public interface ReadableSet<E> extends ReadableCollection<E> {
    /**
     * Wraps this set in the Set API - without copying.
     *
     * @return the wrapped set
     */
    default Set<E> asSet() {
        return new SetWrapper<>(this);
    }
    /**
     * Wraps this set in the ObservableSet API - without copying.
     *
     * @return the wrapped set
     */
    default ObservableSet<E> asObservableSet() {
        return new ObservableSetWrapper<>(this);
    }
}
