/*
 * @(#)ReadOnlySet.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.collections.ObservableSet;

import java.util.Set;

/**
 * Provides a read-only API for a set collection.
 * <p>
 * Note: a ReadOnlySet is not equal to a non-readable {@link Set}.
 * To compare a ReadOnlySet to a Set, you have to wrap the Set into a {@link ReadOnlySetWrapper}.
 *
 * @param <E> the element type
 */
public interface ReadOnlySet<E> extends ReadOnlyCollection<E> {
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
