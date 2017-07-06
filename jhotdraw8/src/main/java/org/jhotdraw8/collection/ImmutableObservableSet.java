/* @(#)ImmutableObservableSet.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
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

    private final static ImmutableObservableSet<Object> EMPTY = new ImmutableObservableSet<>(Collections.emptySet());
    private final Set<E> backingSet;

    public ImmutableObservableSet(Collection<E> copyMe) {
        switch (copyMe.size()) {
            case 0:
                backingSet = Collections.emptySet();
                break;
            case 1:
                backingSet = Collections.singleton(copyMe.iterator().next());
                break;
            default:
                this.backingSet = new LinkedHashSet<>(copyMe);
        }
    }

    public ImmutableObservableSet(Object[] array) {
        this(array, 0, array.length);
    }

    @SuppressWarnings("unchecked")
    public ImmutableObservableSet(Object[] a, int offset, int length) {
        switch (length) {
            case 0:
                backingSet = Collections.emptySet();
                break;
            case 1:
                backingSet = Collections.singleton((E) a[offset]);
                break;
            default:
                this.backingSet = new HashSet<>(Math.max(2 * length, 11));
                for (int i = offset, n = offset + length; i < n; i++) {
                    backingSet.add((E) a[i]);
                }
        }

    }

    private ImmutableObservableSet(boolean privateConstructor, Set<E> backingSet) {
        this.backingSet = backingSet;
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(InvalidationListener listener) {
    }

    @Override
    public void addListener(SetChangeListener<? super E> listener) {
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
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
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeListener(InvalidationListener listener) {
    }

    @Override
    public void removeListener(SetChangeListener<? super E> listener) {
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return backingSet.size();
    }
    public void copyInto(Object[] out, int offset) {
        int i=offset;
        for (E e :this) {
            out[i++]=e;
        }
    }

    public static <T> ImmutableObservableSet<T> add(Collection<T> collection, T item) {
        Set<T> a = new HashSet<T>(collection);
        a.add(item);
        return new ImmutableObservableSet<T>(true, a);
    }

    @SuppressWarnings("unchecked")
    public static <T> ImmutableObservableSet<T> emptySet() {
        return (ImmutableObservableSet<T>) EMPTY;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> ImmutableObservableSet<T> of(T... items) {
        return items.length == 0 ? emptySet() : new ImmutableObservableSet<T>(items);
    }

    public static <T> ImmutableObservableSet<T> remove(Collection<T> collection, T item) {
        Set<T> a = new HashSet<T>(collection);
        a.remove(item);
        return new ImmutableObservableSet<T>(true, a);
    }
}
