/* @(#)ImmutableObservableList.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

import java.util.ArrayList;
import java.util.Collection;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;

/**
 * An immutable observable list.
 *
 * @author Werner Randelshofer
 * @version $$Id: ImmutableObservableList.java 1239 2016-12-20 11:19:20Z
 * rawcoder $$
 */
public final class ImmutableObservableList<T> extends ObservableListBase<T> implements ObservableList<T> {

    private final Object[] array;

    private final static ImmutableObservableList<Object> EMPTY = new ImmutableObservableList<Object>(true, new Object[0]);

    public ImmutableObservableList(Collection<T> copyItems) {
        this.array = copyItems.toArray();
    }

    private ImmutableObservableList(boolean isPrivate, Object... array) {
        this.array = array;
    }

    public static <T> ImmutableObservableList<T> of(T item) {
        return new ImmutableObservableList<>(true, item);
    }

    public static <T> ImmutableObservableList<T> add(Collection<T> collection, T item) {
        Object[] a = new Object[collection.size() + 1];
        a = collection.toArray(a);
        a[a.length - 1] = item;
        return new ImmutableObservableList<>(true, a);
    }

    public static <T> ImmutableObservableList<T> add(Collection<T> collection, int index, T item) {
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length + 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index + 1, b, index + 1, b.length - index);
        b[index] = item;
        return new ImmutableObservableList<>(true, b);
    }

    public static <T> ImmutableObservableList<T> set(Collection<T> collection, int index, T item) {
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        a[index] = item;
        return new ImmutableObservableList<>(true, a);
    }

    public static <T> ImmutableObservableList<T> remove(Collection<T> collection, int index) {
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length - 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index + 1, b, index, b.length - index);
        return new ImmutableObservableList<>(true, b);
    }

    public static <T> ImmutableObservableList<T> remove(Collection<T> collection, T item) {
        ArrayList<T> a = new ArrayList<T>(collection);
        a.remove(item);
        return new ImmutableObservableList<>(a);
    }

    @SuppressWarnings("unchecked")
    public static <T> ImmutableObservableList<T> emptyList() {
        return (ImmutableObservableList<T>) EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <T> ImmutableObservableList<T> of() {
        return emptyList();
    }

    @Override
    public T get(int index) {
        @SuppressWarnings("unchecked")
        T value = (T) array[index];
        return value;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    @SafeVarargs
    public final boolean addAll(T... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SafeVarargs
    public final boolean setAll(T... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setAll(Collection<? extends T> col) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SafeVarargs
    public final boolean removeAll(T... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SafeVarargs
    public final boolean retainAll(T... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int from, int to) {
        throw new UnsupportedOperationException();
    }

}
