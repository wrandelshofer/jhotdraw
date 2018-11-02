/* @(#)ReadableList.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides an API for reading a list.
 *
 * @param <E> the element type
 */
public interface ReadableList<E> extends ReadableCollection<E> {

    E get(int index);

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Nonnull
    @Override
    default Iterator<E> iterator() {
        return new ReadableListIterator<>(this);
    }


    default ArrayList<E> toList() {
        return new ArrayList<>(new ListWrapper<>(this));
    }
}
