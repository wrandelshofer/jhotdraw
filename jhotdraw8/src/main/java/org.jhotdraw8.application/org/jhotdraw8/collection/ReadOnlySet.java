/*
 * @(#)ReadOnlySet.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.collections.ObservableSet;
import org.jhotdraw8.annotation.NonNull;

import java.util.Set;

/**
 * Provides query methods to a set. The state of the set may
 * change.
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
    @NonNull
    default Set<E> asSet() {
        return new SetWrapper<>(this);
    }

    /**
     * Wraps this set in the ObservableSet API - without copying.
     *
     * @return the wrapped set
     */
    @NonNull
    default ObservableSet<E> asObservableSet() {
        return new ObservableSetWrapper<>(this);
    }
}
