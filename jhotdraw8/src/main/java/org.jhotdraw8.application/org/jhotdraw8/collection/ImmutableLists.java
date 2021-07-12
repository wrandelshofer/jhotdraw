/*
 * @(#)ImmutableLists.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImmutableLists {
    public static @NonNull <T> ImmutableList<T> add(@Nullable ReadOnlyCollection<T> collection, T item) {
        if (collection == null || collection.isEmpty()) {
            return of(item);
        }
        Object[] a = new Object[collection.size() + 1];
        a = collection.toArray(a);
        a[a.length - 1] = item;
        return new ImmutableArrayList<>(a);
    }

    public static @NonNull <T> ImmutableList<T> add(@Nullable Collection<T> collection, T item) {
        if (collection == null || collection.isEmpty()) {
            return of(item);
        }
        Object[] a = new Object[collection.size() + 1];
        a = collection.toArray(a);
        a[a.length - 1] = item;
        return new ImmutableArrayList<>(a);
    }

    public static @NonNull <T> ImmutableList<T> add(@NonNull Collection<T> collection, int index, T item) {
        if (collection.isEmpty() && index == 0) {
            return of(item);
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length + 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index, b, index + 1, a.length - index);
        b[index] = item;
        return new ImmutableArrayList<>(b);
    }

    public static @NonNull <T> ImmutableList<T> add(@Nullable ReadOnlyCollection<T> collection, int index, T item) {
        if (collection == null || collection.isEmpty() && index == 0) {
            return of(item);
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length + 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index, b, index + 1, a.length - index);
        b[index] = item;
        return new ImmutableArrayList<>(b);
    }

    public static @NonNull <T> ImmutableList<T> addAll(@Nullable ReadOnlyCollection<T> first, @NonNull ReadOnlyCollection<T> second) {
        if (first == null || first.isEmpty()) {
            return ofCollection(second);
        }
        Object[] a = new Object[first.size() + second.size()];
        int i = 0;
        for (T t : first) {
            a[i++] = t;
        }
        for (T t : second) {
            a[i++] = t;
        }
        return new ImmutableArrayList<>(a);
    }

    @SuppressWarnings("unchecked")
    public static @NonNull <T> ImmutableList<T> emptyList() {
        return (ImmutableArrayList<T>) ImmutableArrayList.EMPTY;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static @NonNull <T> ImmutableList<T> of(@NonNull T... items) {
        return items.length == 0 ? emptyList() : new ImmutableArrayList<>(items, 0, items.length);
    }

    public static @NonNull <T> ImmutableList<T> ofCollection(@NonNull Collection<? extends T> collection) {
        return collection.isEmpty() ? emptyList() : new ImmutableArrayList<>(collection);
    }

    public static @NonNull <T> ImmutableList<T> ofIterable(Iterable<? extends T> iterable) {
        if (iterable instanceof ImmutableList) {
            @SuppressWarnings("unchecked")
            ImmutableList<T> unchecked = (ImmutableList<T>) iterable;
            return unchecked;
        } else if (iterable instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<T> unchecked = (Collection<T>) iterable;
            return ofCollection(unchecked);
        }
        ArrayList<T> list = new ArrayList<>();
        for (T t : iterable) {
            list.add(t);
        }
        return new ImmutableArrayList<>(list);
    }

    public static @NonNull <T> ImmutableList<T> ofCollection(ReadOnlyCollection<? extends T> collection) {
        if (collection instanceof ImmutableList) {
            @SuppressWarnings("unchecked")
            ImmutableList<T> unchecked = (ImmutableList<T>) collection;
            return unchecked;
        }
        return collection.isEmpty() ? emptyList() : new ImmutableArrayList<>(collection);
    }

    public static @NonNull <T> ImmutableList<T> ofArray(@NonNull Object[] a) {
        return ofArray(a, 0, a.length);
    }

    public static @NonNull <T> ImmutableList<T> ofArray(@NonNull Object[] a, int offset, int length) {
        return length == 0 ? emptyList() : new ImmutableArrayList<>(a, offset, length);
    }

    public static @NonNull <T> ImmutableList<T> remove(@Nullable ReadOnlyCollection<T> collection, int index) {
        if (collection == null || collection.size() == 1 && index == 0) {
            return emptyList();
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length - 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index + 1, b, index, b.length - index);
        return new ImmutableArrayList<>(b);
    }

    public static @NonNull <T> ImmutableList<T> remove(@Nullable Collection<T> collection, int index) {
        if (collection == null || collection.size() == 1 && index == 0) {
            return emptyList();
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length - 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index + 1, b, index, b.length - index);
        return new ImmutableArrayList<>(b);
    }

    public static @NonNull <T> ImmutableList<T> remove(@Nullable Collection<T> collection, T item) {
        if (collection == null || collection.size() == 1 && collection.contains(item)) {
            return emptyList();
        }
        if (collection instanceof List) {
            @SuppressWarnings("unchecked")
            List<T> list = (List) collection;
            return remove(list, list.indexOf(item));
        } else {
            List<T> a = new ArrayList<>(collection);// linear
            a.remove(item);// linear
            return new ImmutableArrayList<>(a);// linear
        }
    }

    public static @NonNull <T> ImmutableList<T> set(@NonNull ReadOnlyCollection<T> collection, int index, T item) {
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        a[index] = item;
        return new ImmutableArrayList<>(a);
    }

    public static @NonNull <T> ImmutableList<T> set(@NonNull Collection<T> collection, int index, T item) {
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        a[index] = item;
        return new ImmutableArrayList<>(a);
    }

    public static @NonNull <E> ImmutableList<E> removeAll(@NonNull ReadOnlyCollection<E> list, @NonNull Collection<? extends E> collection) {
        int n = list.size();
        Object[] a = new Object[n];
        int j = 0;
        for (E e : list) {
            if (!collection.contains(e)) {
                a[j++] = e;
            }
        }

        return new ImmutableArrayList<>(a, 0, j);
    }

    public static @NonNull <E> ImmutableList<E> reverse(@NonNull ReadOnlyCollection<E> list) {
        int n = list.size();
        Object[] a = new Object[n];
        int j = n - 1;
        for (E e : list) {
            a[j--] = e;
        }
        return new ImmutableArrayList<>(a, 0, n);
    }

    public static @NonNull <E> ImmutableList<E> reverse(@NonNull Collection<E> list) {
        int n = list.size();
        Object[] a = new Object[n];
        int j = n - 1;
        for (E e : list) {
            a[j--] = e;
        }
        return new ImmutableArrayList<>(a, 0, n);
    }

    public static @NonNull <E> ImmutableList<E> subList(@NonNull List<E> list, int fromIndex, int toIndex) {
        return new ImmutableArrayList<>(list.subList(fromIndex, toIndex).toArray());
    }

    public static @NonNull <E> ImmutableList<E> subList(@NonNull ReadOnlyList<E> list, int fromIndex, int toIndex) {
        return new ImmutableArrayList<>(list.readOnlySubList(fromIndex, toIndex).toArray());
    }
}
