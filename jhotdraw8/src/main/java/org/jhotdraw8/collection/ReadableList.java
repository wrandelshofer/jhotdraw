/* @(#)ReadableList.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Provides a read-only API for a list collection.
 * <p>
 * Note: a ReadbleList is not equal to a non-readable {@link List}.
 * To compare a ReadbleList to a List, you have to wrap the List into a {@link ReadableListWrapper}.
 *
 * @param <E> the element type
 */
public interface ReadableList<E> extends ReadableCollection<E> {

    E get(int index);

    /**
     * Returns an iterator over elements of type {@code E}.
     *
     * @return an iterator.
     */
    @Nonnull
    @Override
    default Iterator<E> iterator() {
        return new ReadableListIterator<>(this);
    }

    /**
     * Returns a list iterator over elements of type {@code E}.
     *
     * @return a list iterator.
     */
    @Nonnull
    default ListIterator<E> listIterator() {
        return new ReadableListIterator<>(this);
    }


    default ArrayList<E> toList() {
        return new ArrayList<>(new ListWrapper<>(this));
    }
}
