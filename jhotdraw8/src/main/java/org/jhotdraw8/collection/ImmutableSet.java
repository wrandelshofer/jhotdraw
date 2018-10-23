/* @(#)ImmutableSet.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * ImmutableSet preserves insertion order of items.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public final class ImmutableSet<E> extends AbstractReadableSet<E> {

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
    private ImmutableSet(ReadableCollection<E> copyMe) {
        switch (copyMe.size()) {
            case 0:
                backingSet = Collections.emptySet();
                break;
            case 1:
                backingSet = Collections.singleton(copyMe.iterator().next());
                break;
            default:
                this.backingSet = new LinkedHashSet<>(new SetWrapper<>(copyMe));
        }
    }

    private ImmutableSet(@Nonnull Object[] array) {
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
    public boolean contains(E o) {
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
    public int size() {
        return backingSet.size();
    }

    public void copyInto(Object[] out, int offset) {
        int i = offset;
        for (E e : this) {
            out[i++] = e;
        }
    }

    public static <T> ImmutableSet<T> add(Collection<T> collection, T item) {
        switch (collection.size()) {
            case 0:
                return new ImmutableSet<>(Collections.singleton(item));
            default:
                Set<T> a = new LinkedHashSet<>(collection);
                a.add(item);
                return new ImmutableSet<>(true, a);
        }
    }
    public static <T> ImmutableSet<T> add(ReadableCollection<T> collection, T item) {
        switch (collection.size()) {
            case 0:
                return new ImmutableSet<>(Collections.singleton(item));
            default:
                Set<T> a = new LinkedHashSet<>(new SetWrapper<>(collection));
                a.add(item);
                return new ImmutableSet<>(true, a);
        }
    }
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> ImmutableSet<T> emptySet() {
        return (ImmutableSet<T>) EMPTY;
    }

    @Nonnull
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> ImmutableSet<T> of(T... items) {
        return items.length == 0 ? emptySet() : new ImmutableSet<>(items);
    }

    @Nonnull
    public static <T> ImmutableSet<T> ofCollection(Collection<T> collection) {
        return collection.isEmpty() ? emptySet() : new ImmutableSet<>(collection);
    }
    @Nonnull
    public static <T> ImmutableSet<T> ofCollection(ReadableCollection<T> collection) {
        return collection.isEmpty() ? emptySet() : new ImmutableSet<>(collection);
    }

    @Nonnull
    public static <T> ImmutableSet<T> ofArray(Object[] a, int offset, int length) {
        return length == 0 ? emptySet() : new ImmutableSet<>(a, offset, length);
    }

    @Nonnull
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <T> ImmutableSet<T> remove(Collection<T> collection, T item) {
        switch (collection.size()) {
            case 0:
                return (ImmutableSet<T>) EMPTY;
            case 1:
                if (collection.contains(item)) {
                    return (ImmutableSet<T>) EMPTY;
                } else {
                    return new ImmutableSet(true, Collections.singleton(item));
                }
            case 2:
                if (collection.contains(item)) {
                    Iterator<T> iter = collection.iterator();
                    T one = iter.next();
                    T two = iter.next();
                    return new ImmutableSet(true, Collections.singleton(one.equals(item) ? two : one));

                } else {
                    return new ImmutableSet(collection);
                }
            default:
                if (collection.contains(item)) {
                    Set<T> a = new LinkedHashSet<>(collection);
                    a.remove(item);
                    return new ImmutableSet<>(true, a);
                } else {
                    return new ImmutableSet(collection);
                }
        }
    }
    @Nonnull
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <T> ImmutableSet<T> remove(ReadableCollection<T> collection, T item) {
        switch (collection.size()) {
            case 0:
                return (ImmutableSet<T>) EMPTY;
            case 1:
                if (collection.contains(item)) {
                    return (ImmutableSet<T>) EMPTY;
                } else {
                    return new ImmutableSet(true, Collections.singleton(item));
                }
            case 2:
                if (collection.contains(item)) {
                    Iterator<T> iter = collection.iterator();
                    T one = iter.next();
                    T two = iter.next();
                    return new ImmutableSet(true, Collections.singleton(one.equals(item) ? two : one));

                } else {
                    return new ImmutableSet(collection);
                }
            default:
                if (collection.contains(item)) {
                    Set<T> a = new LinkedHashSet<>(new SetWrapper<>(collection));
                    a.remove(item);
                    return new ImmutableSet<>(true, a);
                } else {
                    return new ImmutableSet(collection);
                }
        }
    }
}
