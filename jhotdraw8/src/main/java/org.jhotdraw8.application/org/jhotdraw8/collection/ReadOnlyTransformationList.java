/*
 * @(#)ReadOnlyListWrapper.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Function;

/**
 * Wraps a {@link List} in the {@link ReadOnlyList} API.
 * <p>
 * The underlying List is referenced - not copied. This allows to pass a
 * list to a client while preventing that the client can modify the list directly.
 *
 * @author Werner Randelshofer
 */
public final class ReadOnlyTransformationList<E, F> extends AbstractReadOnlyList<E> {

    private final ReadOnlyList<F> backingList;
    private final Function<F, E> mapf;

    public ReadOnlyTransformationList(ReadOnlyList<F> backingList, Function<F, E> mapf) {
        this.backingList = backingList;
        this.mapf = mapf;
    }

    @Override
    public boolean contains(Object o) {
        return backingList.contains(o);
    }

    @Override
    public E get(int index) {
        return mapf.apply(backingList.get(index));
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<F> i = backingList.iterator();

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public E next() {
                return mapf.apply(i.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public Spliterator<E> spliterator() {
        return (Spliterator<E>) backingList.spliterator();
    }

    @Override
    public int size() {
        return backingList.size();
    }

    public void copyInto(Object[] out, int offset) {
        int i = offset;
        for (E e : this) {
            out[i++] = e;
        }
    }

    @NonNull
    public ReadOnlyList<E> readOnlySubList(int fromIndex, int toIndex) {
        return new ReadOnlyTransformationList<>(backingList.readOnlySubList(fromIndex, toIndex), mapf);
    }
}
