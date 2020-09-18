/*
 * @(#)ListWrapper.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.AbstractList;
import java.util.List;

/**
 * Wraps a {@link ReadOnlyList} in the {@link List} API.
 * <p>
 * The underlying ReadOnlyList is referenced - not copied. This allows to pass a
 * ReadOnlyList to a client who does not understand the ReadOnlyList APi.
 *
 * @author Werner Randelshofer
 */
public class ListWrapper<E> extends AbstractList<E> {
    private final ReadOnlyList<E> backingList;

    public ListWrapper(ReadOnlyList<E> backingList) {
        this.backingList = backingList;
    }

    @Override
    public E get(int index) {
        return backingList.get(index);
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    @NonNull
    public Object[] toArray() {
        return backingList.toArray();
    }

    @Override
    @NonNull
    public <T> T[] toArray(@NonNull T[] a) {
        return backingList.toArray(a);
    }
}
