/* @(#)ImmutableList.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

/**
 * An immutable observable list.
 *
 * @param <E> element type
 * @author Werner Randelshofer
 * @version $Id$
 */
public final class ImmutableList<E> extends AbstractReadOnlyList<E> {

    private final static ImmutableList<Object> EMPTY = new ImmutableList<>(true, new Object[0]);

    private final Object[] array;

    private ImmutableList(@javax.annotation.Nullable Collection<E> copyItems) {
        this.array = copyItems == null || copyItems.isEmpty() ? new Object[0] : copyItems.toArray();
    }

    private ImmutableList(@Nonnull Object[] array) {
        this(array, 0, array.length);
    }

    private ImmutableList(@Nonnull Object[] a, int offset, int length) {
        this.array = new Object[length];
        System.arraycopy(a, offset, array, 0, length);
    }

    private ImmutableList(boolean privateMethod, Object[] array) {
        this.array = array;
    }

    public void copyInto(@Nonnull Object[] out, int offset) {
        System.arraycopy(array, 0, out, offset, array.length);
    }

    @Override
    public boolean contains(E o) {
        for (int i = 0, n = array.length; i < n; i++)
            if (array[i].equals(o)) {
                return true;
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

    @Nonnull
    public static <T> ImmutableList<T> add(@Nullable ReadOnlyCollection<T> collection, T item) {
        if (collection == null || collection.isEmpty()) {
            return ImmutableList.of(item);
        }
        Object[] a = new Object[collection.size() + 1];
        a = collection.toArray(a);
        a[a.length - 1] = item;
        return new ImmutableList<>(true, a);
    }

    @Nonnull
    public static <T> ImmutableList<T> add(@Nullable Collection<T> collection, T item) {
        if (collection == null || collection.isEmpty()) {
            return ImmutableList.of(item);
        }
        Object[] a = new Object[collection.size() + 1];
        a = collection.toArray(a);
        a[a.length - 1] = item;
        return new ImmutableList<>(true, a);
    }

    @Nonnull
    public static <T> ImmutableList<T> add(@javax.annotation.Nullable Collection<T> collection, int index, T item) {
        if (collection == null || collection.isEmpty() && index == 0) {
            return ImmutableList.of(item);
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length + 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index, b, index + 1, a.length - index);
        b[index] = item;
        return new ImmutableList<>(true, b);
    }

    @Nonnull
    public static <T> ImmutableList<T> add(@javax.annotation.Nullable ReadOnlyCollection<T> collection, int index, T item) {
        if (collection == null || collection.isEmpty() && index == 0) {
            return ImmutableList.of(item);
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length + 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index, b, index + 1, a.length - index);
        b[index] = item;
        return new ImmutableList<>(true, b);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> ImmutableList<T> emptyList() {
        return (ImmutableList<T>) EMPTY;
    }

    @Nonnull
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> ImmutableList<T> of(T... items) {
        // FIXME we should copy the items array, because caller might keep reference on mutable items array
        return items.length == 0 ? emptyList() : new ImmutableList<>(true, items);
    }

    @Nonnull
    public static <T> ImmutableList<T> ofCollection(Collection<T> collection) {
        return collection.isEmpty() ? emptyList() : new ImmutableList<>(collection);
    }

    @Nonnull
    public static <T> ImmutableList<T> ofArray(@Nonnull Object[] a, int offset, int length) {
        return length == 0 ? emptyList() : new ImmutableList<>(a, offset, length);
    }

    @Nonnull
    public static <T> ImmutableList<T> remove(@javax.annotation.Nullable ReadOnlyCollection<T> collection, int index) {
        if (collection == null || collection.size() == 1 && index == 0) {
            return ImmutableList.emptyList();
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length - 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index + 1, b, index, b.length - index);
        return new ImmutableList<>(true, b);
    }

    @Nonnull
    public static <T> ImmutableList<T> remove(@javax.annotation.Nullable Collection<T> collection, int index) {
        if (collection == null || collection.size() == 1 && index == 0) {
            return ImmutableList.emptyList();
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length - 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index + 1, b, index, b.length - index);
        return new ImmutableList<>(true, b);
    }

    @Nonnull
    public static <T> ImmutableList<T> remove(@javax.annotation.Nullable Collection<T> collection, T item) {
        if (collection == null || collection.size() == 1 && collection.contains(item)) {
            return ImmutableList.emptyList();
        }
        if (collection instanceof List) {
            @SuppressWarnings("unchecked")
            List<T> list = (List) collection;
            return remove(list, list.indexOf(item));
        } else {
            List<T> a = new ArrayList<>(collection);// linear
            a.remove(item);// linear
            return new ImmutableList<>(a);// linear
        }
    }

    public static <T> ImmutableList<T> set(ReadOnlyCollection<T> collection, int index, T item) {
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        a[index] = item;
        return new ImmutableList<>(true, a);
    }

    public static <T> ImmutableList<T> set(Collection<T> collection, int index, T item) {
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        a[index] = item;
        return new ImmutableList<>(true, a);
    }

    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size)
        // Make a new array of a's runtime type, but my contents:
        //noinspection unchecked
        {
            return (T[]) Arrays.copyOf(array, size, a.getClass());
        }
        System.arraycopy(array, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
}
