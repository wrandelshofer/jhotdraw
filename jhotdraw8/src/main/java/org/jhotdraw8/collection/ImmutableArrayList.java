/* @(#)ImmutableArrayList.java
 * Copyright © by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.annotation.Nonnull;

/**
 * An immutable observable list.
 *
 * @param <E> element type
 * @author Werner Randelshofer
 * @version $Id$
 */
public final class ImmutableArrayList<E> extends AbstractReadOnlyList<E> implements ImmutableList<E> {

    final static ImmutableArrayList<Object> EMPTY = new ImmutableArrayList<>(true, new Object[0]);

    private final Object[] array;

    public ImmutableArrayList(@Nullable Collection<? extends E> copyItems) {
        this.array = copyItems == null || copyItems.isEmpty() ? new Object[0] : copyItems.toArray();
    }

    public ImmutableArrayList(@Nullable ReadOnlyCollection<? extends E> copyItems) {
        this.array = copyItems == null || copyItems.isEmpty() ? new Object[0] : copyItems.toArray();
    }

    ImmutableArrayList(@Nonnull Object[] array) {
        this(array, 0, array.length);
    }

    ImmutableArrayList(@Nonnull Object[] a, int offset, int length) {
        this.array = new Object[length];
        System.arraycopy(a, offset, array, 0, length);
    }

    ImmutableArrayList(boolean privateMethod, Object[] array) {
        this.array = array;
    }


    public void copyInto(@Nonnull Object[] out, int offset) {
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

    @Nonnull
    public E get(int index) {
        @SuppressWarnings("unchecked")
        E value = (E) array[index];
        return value;
    }

    public int size() {
        return array.length;
    }

    public <T> T[] toArray(T[] a) {
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


    public Spliterator spliterator() {
        return Spliterators.spliterator(array, 0, array.length, Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

}
