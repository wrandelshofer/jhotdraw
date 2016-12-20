/* @(#)ImmutableObservableSet.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 * ImmutableObservableSet.
 *
 * @author Werner Randelshofer
 * @version $$Id: ImmutableObservableSet.java 1238 2016-12-20 10:38:27Z rawcoder
 * $$
 */
public final class ImmutableObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {

    private final LinkedHashSet<E> backingSet;

    private final static ImmutableObservableSet<Object> EMPTY = new ImmutableObservableSet<Object>(new LinkedHashSet<Object>());

    public ImmutableObservableSet(Collection<E> copyMe) {
        this.backingSet = new LinkedHashSet<>(copyMe);
    }

    private ImmutableObservableSet(LinkedHashSet<E> backingSet, boolean privateConstructor) {
        this.backingSet = backingSet;
    }

    public static <T> ImmutableObservableSet<T> add(Collection<T> collection, T item) {
        LinkedHashSet<T> a = new LinkedHashSet<T>(collection);
        a.remove(item);
        return new ImmutableObservableSet<T>(a, true);
    }

    public static <T> ImmutableObservableSet<T> remove(Collection<T> collection, T item) {
        LinkedHashSet<T> a = new LinkedHashSet<T>(collection);
        a.remove(item);
        return new ImmutableObservableSet<T>(a, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> ImmutableObservableSet<T> emptySet() {
        return (ImmutableObservableSet<T>) EMPTY;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<? extends E> i = backingSet.iterator();

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public E next() {
                return i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int size() {
        return backingSet.size();
    }

    @Override
    public void addListener(InvalidationListener listener) {
    }

    @Override
    public void removeListener(InvalidationListener listener) {
    }

    @Override
    public void addListener(SetChangeListener<? super E> listener) {
    }

    @Override
    public void removeListener(SetChangeListener<? super E> listener) {
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
