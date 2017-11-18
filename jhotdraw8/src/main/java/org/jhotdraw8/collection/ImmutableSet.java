/* @(#)ImmutableSet.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
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
import static org.jhotdraw8.collection.ImmutableList.emptyList;

/**
 * ImmutableSet preserves insertion order of items.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public final class ImmutableSet<E> extends AbstractSet<E> implements ObservableSet<E> {

    private final static ImmutableSet<Object> EMPTY = new ImmutableSet<>(Collections.emptySet());
    private final Set<E> backingSet;

    private ImmutableSet(Collection<E> copyMe) {
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

    private ImmutableSet(Object[] array) {
        this(array, 0, array.length);
    }

    @SuppressWarnings("unchecked")
    private ImmutableSet(Object[] a, int offset, int length) {
        switch (length) {
            case 0:
                backingSet = Collections.emptySet();
                break;
            case 1:
                backingSet = Collections.singleton((E) a[offset]);
                break;
            default:
                this.backingSet = new LinkedHashSet<>(Math.max(2 * length, 11));
                for (int i = offset, n = offset + length; i < n; i++) {
                    backingSet.add((E) a[i]);
                }
        }

    }

    private ImmutableSet(boolean privateConstructor, Set<E> backingSet) {
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
    public boolean contains(Object o) {
        return backingSet.contains(o);
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

    public static <T> ImmutableSet<T> add(Collection<T> collection, T item) {
        Set<T> a = new HashSet<T>(collection);
        a.add(item);
        return new ImmutableSet<T>(true, a);
    }

    @SuppressWarnings("unchecked")
    public static <T> ImmutableSet<T> emptySet() {
        return (ImmutableSet<T>) EMPTY;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> ImmutableSet<T> of(T... items) {
        return items.length == 0 ? emptySet() : new ImmutableSet<T>(items);
    }

    public static <T> ImmutableSet<T> ofCollection(Collection<T> collection) {
        return collection.isEmpty() ? emptySet() : new ImmutableSet<T>(collection);
    }
    public static <T> ImmutableSet<T> ofArray(  Object[] a, int offset, int length) {
        return length==0?emptySet():new ImmutableSet<>(a,offset,length);
    }


    public static <T> ImmutableSet<T> remove(Collection<T> collection, T item) {
        Set<T> a = new HashSet<T>(collection);
        a.remove(item);
        return new ImmutableSet<T>(true, a);
    }
}
