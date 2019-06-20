/* @(#)ReadOnlyList.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.collections.ObservableList;
import org.jhotdraw8.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;

/**
 * Provides a read-only API for a list collection.
 * <p>
 * Note: To compare a ReadOnlyList to a List, you must either
 * wrap the ReadOnlyList into a List using {@link ListWrapper},
 * or wrap the List into a ReadOnlyList using {@link ReadOnlyListWrapper}.
 *
 * @param <E> the element type
 */
public interface ReadOnlyList<E> extends ReadOnlyCollection<E> {

    E get(int index);

    /**
     * Returns an iterator over elements of type {@code E}.
     *
     * @return an iterator.
     */
    @Nonnull
    @Override
    default Iterator<E> iterator() {
        return new ReadOnlyListIterator<>(this);
    }

    /**
     * Returns a spliterator over elements of type {@code E}.
     *
     * @return an iterator.
     */
    @Nonnull
    @Override
    default Spliterator<E> spliterator() {
        return new ReadOnlyListIterator<>(this);
    }

    /**
     * Returns a list iterator over elements of type {@code E}.
     *
     * @return a list iterator.
     */
    @Nonnull
    default ListIterator<E> listIterator() {
        return new ReadOnlyListIterator<>(this);
    }

    /**
     * Copies this list into an ArrayList.
     *
     * @return a new ArrayList.
     */
    default ArrayList<E> toArrayList() {
        return new ArrayList<>(this.asList());
    }

    /**
     * Wraps this list in the List API - without copying.
     *
     * @return the wrapped list
     */
    default List<E> asList() {
        return new ListWrapper<>(this);
    }

    /**
     * Wraps this list in the ObservableList API - without copying.
     *
     * @return the wrapped list
     */
    default ObservableList<E> asObservableList() {
        return new ObservableListWrapper<>(this);
    }

    /**
     * Returns a view of the portion of this list between the specified
     * * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.
     *
     * @param fromIndex the from index
     * @param toIndex   the to index (exclusive)
     * @return the sub list
     */
    ReadOnlyList<E> subList(int fromIndex, int toIndex);
}
