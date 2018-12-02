/* @(#)ImmutableSet.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Classes with this interface guarantee that the content of the set is immutable.
 *
 * @param <E> the element type
 */
public interface ImmutableSet<E> extends ReadOnlySet<E>, ImmutableCollection<E> {
    public static <T> ImmutableSet<T> add(Collection<T> collection, T item) {
        switch (collection.size()) {
            case 0:
                return new ImmutableHashSet<>(Collections.singleton(item));
            default:
                Set<T> a = new LinkedHashSet<>(collection);
                a.add(item);
                return new ImmutableHashSet<>(true, a);
        }
    }
    public static <T> ImmutableSet<T> add(ReadOnlyCollection<T> collection, T item) {
        switch (collection.size()) {
            case 0:
                return new ImmutableHashSet<>(Collections.singleton(item));
            default:
                Set<T> a = new LinkedHashSet<T>(new CollectionWrapper<T>(collection));
                a.add(item);
                return new ImmutableHashSet<>(true, a);
        }
    }
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> ImmutableSet<T> emptySet() {
        return (ImmutableSet<T>) ImmutableHashSet.EMPTY;
    }

    @Nonnull
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> ImmutableSet<T> of(T... items) {
        //noinspection unchecked
        return items.length == 0 ? emptySet() : new ImmutableHashSet<T>(items);
    }

    @Nonnull
    public static <T> ImmutableSet<T> ofCollection(Collection<T> collection) {
        return collection.isEmpty() ? emptySet() : new ImmutableHashSet<>(collection);
    }
    @Nonnull
    public static <T> ImmutableSet<T> ofCollection(ReadOnlyCollection<T> collection) {
        return collection.isEmpty() ? emptySet() : new ImmutableHashSet<>(collection);
    }

    @Nonnull
    public static <T> ImmutableSet<T> ofArray(Object[] a, int offset, int length) {
        return length == 0 ? emptySet() : new ImmutableHashSet<>(a, offset, length);
    }

    @Nonnull
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <T> ImmutableSet<T> remove(Collection<T> collection, T item) {
        switch (collection.size()) {
            case 0:
                return (ImmutableSet<T>) emptySet();
            case 1:
                if (collection.contains(item)) {
                    return (ImmutableSet<T>) emptySet();
                } else {
                    return new ImmutableHashSet<>(true, Collections.singleton(item));
                }
            case 2:
                if (collection.contains(item)) {
                    Iterator<T> iter = collection.iterator();
                    T one = iter.next();
                    T two = iter.next();
                    return new ImmutableHashSet<>(true, Collections.singleton(one.equals(item) ? two : one));

                } else {
                    return new ImmutableHashSet<>(collection);
                }
            default:
                if (collection.contains(item)) {
                    Set<T> a = new LinkedHashSet<>(collection);
                    a.remove(item);
                    return new ImmutableHashSet<>(true, a);
                } else {
                    return new ImmutableHashSet<>(collection);
                }
        }
    }
    @Nonnull
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <T> ImmutableSet<T> remove(ReadOnlyCollection<T> collection, T item) {
        switch (collection.size()) {
            case 0:
                return (ImmutableSet<T>) emptySet();
            case 1:
                if (collection.contains(item)) {
                    return (ImmutableSet<T>) emptySet();
                } else {
                    return new ImmutableHashSet<>(true, Collections.singleton(item));
                }
            case 2:
                if (collection.contains(item)) {
                    Iterator<T> iter = collection.iterator();
                    T one = iter.next();
                    T two = iter.next();
                    return new ImmutableHashSet<>(true, Collections.singleton(one.equals(item) ? two : one));

                } else {
                    return new ImmutableHashSet<>(collection);
                }
            default:
                if (collection.contains(item)) {
                    Set<T> a = new LinkedHashSet<>(new CollectionWrapper<>(collection));
                    a.remove(item);
                    return new ImmutableHashSet<>(true, a);
                } else {
                    return new ImmutableHashSet<>(collection);
                }
        }
    }

}
