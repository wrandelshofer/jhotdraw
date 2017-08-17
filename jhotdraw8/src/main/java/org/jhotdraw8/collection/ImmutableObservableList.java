/* @(#)ImmutableObservableList.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * An immutable observable list.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <E> element type
 */
public final class ImmutableObservableList<E> extends AbstractList<E> implements ObservableList<E> {

    private final static ImmutableObservableList<Object> EMPTY = new ImmutableObservableList<Object>(true, new Object[0]);

    private final Object[] array;

    public ImmutableObservableList(Collection<E> copyItems) {
        this.array = copyItems == null ? new Object[0] : copyItems.toArray();
    }

    public ImmutableObservableList(Object[] array) {
        this(array, 0, array.length);
    }

    public ImmutableObservableList(Object[] a, int offset, int length) {
        this.array = new Object[length];
        System.arraycopy(a, offset, array, 0, length);
    }

    private ImmutableObservableList(boolean privateMethod, Object[] array) {
        this.array = array;
    }

    @Override
    @SafeVarargs
    public final boolean addAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(ListChangeListener<? super E> listener) {
        // nothing to do
    }

    @Override
    public void addListener(InvalidationListener listener) {
        // nothing to do
    }

    public void copyInto(Object[] out, int offset) {
        System.arraycopy(array, 0, out, offset, array.length);
    }

    @Override
    public E get(int index) {
        @SuppressWarnings("unchecked")
        E value = (E) array[index];
        return value;
    }

    @Override
    public void remove(int from, int to) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SafeVarargs
    public final boolean removeAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeListener(ListChangeListener<? super E> listener) {
        // nothing to do
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        // nothing to do
    }

    @Override
    @SafeVarargs
    public final boolean retainAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SafeVarargs
    public final boolean setAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setAll(Collection<? extends E> col) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return array.length;
    }

    public static <T> ImmutableObservableList<T> add(Collection<T> collection, T item) {
        if (collection.isEmpty()) {
            return ImmutableObservableList.of(item);
        }
        Object[] a = new Object[collection.size() + 1];
        a = collection.toArray(a);
        a[a.length - 1] = item;
        return new ImmutableObservableList<>(true, a);
    }

    public static <T> ImmutableObservableList<T> add(Collection<T> collection, int index, T item) {
        if (collection.isEmpty() && index == 0) {
            return ImmutableObservableList.of(item);
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length + 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index, b, index + 1, a.length - index);
        b[index] = item;
        return new ImmutableObservableList<>(true, b);
    }

    @SuppressWarnings("unchecked")
    public static <T> ImmutableObservableList<T> emptyList() {
        return (ImmutableObservableList<T>) EMPTY;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> ImmutableObservableList<T> of(T... items) {
        // FIXME we should copy the items array, because caller might keep reference on mutable items array
        return items.length == 0 ? emptyList() : new ImmutableObservableList<>(true, items);
    }

    public static <T> ImmutableObservableList<T> remove(Collection<T> collection, int index) {
        if (collection.size() == 1 && index == 0) {
            return ImmutableObservableList.emptyList();
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length - 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index + 1, b, index, b.length - index);
        return new ImmutableObservableList<>(true, b);
    }

    public static <T> ImmutableObservableList<T> remove(Collection<T> collection, T item) {
        if (collection.size() == 1 && collection.contains(item)) {
            return ImmutableObservableList.emptyList();
        }
        if (collection instanceof List) {
            @SuppressWarnings("unchecked")
            List<T> list = (List) collection;
            return remove(list, list.indexOf(item));
        } else {
            List<T> a = new ArrayList<T>(collection);// linear
            a.remove(item);// linear
            return new ImmutableObservableList<>(a);// linear
        }
    }

    public static <T> ImmutableObservableList<T> set(Collection<T> collection, int index, T item) {
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        a[index] = item;
        return new ImmutableObservableList<>(true, a);
    }
}
