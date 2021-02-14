/*
 * @(#)ImmutableArrayList.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;

/**
 * An immutable list.
 *
 * @param <E> element type
 * @author Werner Randelshofer
 */
public class ImmutableArrayList<E> extends AbstractReadOnlyList<E> implements ImmutableList<E> {

    static final ImmutableArrayList<Object> EMPTY = new ImmutableArrayList<>(true, new Object[0]);

    private static final Object[] EMPTY_ARRAY = new Object[0];

    private final Object[] array;

    public ImmutableArrayList(@Nullable Collection<? extends E> copyItems) {
        this.array = copyItems == null || copyItems.isEmpty() ? EMPTY_ARRAY : copyItems.toArray();
    }

    public ImmutableArrayList(@Nullable ReadOnlyCollection<? extends E> copyItems) {
        this.array = copyItems == null || copyItems.isEmpty() ? EMPTY_ARRAY : copyItems.toArray();
    }

    ImmutableArrayList(@NonNull Object[] array) {
        this(array, 0, array.length);
    }

    ImmutableArrayList(@NonNull Object[] a, int offset, int length) {
        if (offset < 0) {
            throw new IndexOutOfBoundsException("offset = " + offset);
        }
        if (length > a.length) {
            throw new IndexOutOfBoundsException("length = " + length);
        }
        this.array = length == 0 ? EMPTY_ARRAY : new Object[length];
        System.arraycopy(a, offset, array, 0, length);
    }

    ImmutableArrayList(boolean privateMethod, Object[] array) {
        this.array = array;
    }


    public void copyInto(@NonNull Object[] out, int offset) {
        System.arraycopy(array, 0, out, offset, array.length);
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0, n = array.length; i < n; i++) {
            if (array[i].equals(o)) {
                return true;
            }
        }
        return false;
    }

    public @NonNull E get(int index) {
        @SuppressWarnings("unchecked")
        E value = (E) array[index];
        return value;
    }

    public int size() {
        return array.length;
    }

    public @NonNull <T> T[] toArray(@NonNull T[] a) {
        int size = size();
        if (a.length < size) {
            @SuppressWarnings("unchecked")
            T[] t = (T[]) Arrays.copyOf(array, size, a.getClass());
            return t;
        }
        System.arraycopy(array, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public @NonNull Iterator<E> iterator() {
        return new ArrayIterator<>(array);
    }

    public @NonNull Spliterator<E> spliterator() {
        return new ArrayIterator<>(array);
    }

    public @NonNull Enumerator<E> enumerator() {
        return new ArrayIterator<>(array);
    }

    @Override
    public @NonNull ImmutableList<E> readOnlySubList(int fromIndex, int toIndex) {
        return new ImmutableArraySubList<E>(true, this.array, fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return array.clone();
    }
}
