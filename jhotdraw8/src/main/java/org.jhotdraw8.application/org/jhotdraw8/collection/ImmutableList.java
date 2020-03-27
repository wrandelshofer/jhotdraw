/*
 * @(#)ImmutableList.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

/**
 * Provides query methods to a list. The state of the list
 * does not change.
 *
 * @param <E> the element type
 */
public interface ImmutableList<E> extends ReadOnlyList<E>, ImmutableCollection<E> {
    @NonNull
    @Override
    ImmutableList<E> subList(int fromIndex, int toIndex);
}
