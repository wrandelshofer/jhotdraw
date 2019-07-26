/*
 * @(#)ImmutableList.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * Classes with this interface guarantee that the content of the list is immutable.
 *
 * @param <E> the element type
 */
public interface ImmutableList<E> extends ReadOnlyList<E>, ImmutableCollection<E> {
    @Override
    ImmutableList<E> subList(int fromIndex, int toIndex);
}
