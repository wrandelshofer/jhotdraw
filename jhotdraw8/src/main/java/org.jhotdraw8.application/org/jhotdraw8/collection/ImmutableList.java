/*
 * @(#)ImmutableList.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

/**
 * Classes with this interface guarantee that the content of the list is immutable.
 *
 * @param <E> the element type
 */
public interface ImmutableList<E> extends ReadOnlyList<E>, ImmutableCollection<E> {
    @NonNull
    @Override
    ImmutableList<E> subList(int fromIndex, int toIndex);
}
