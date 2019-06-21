package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImmutableLists {
    @Nonnull
    public static <T> ImmutableList<T> add(@Nullable ReadOnlyCollection<T> collection, T item) {
        if (collection == null || collection.isEmpty()) {
            return of(item);
        }
        Object[] a = new Object[collection.size() + 1];
        a = collection.toArray(a);
        a[a.length - 1] = item;
        return new ImmutableArrayList<>(true, a);
    }

    @Nonnull
    public static <T> ImmutableList<T> add(@Nullable Collection<T> collection, T item) {
        if (collection == null || collection.isEmpty()) {
            return of(item);
        }
        Object[] a = new Object[collection.size() + 1];
        a = collection.toArray(a);
        a[a.length - 1] = item;
        return new ImmutableArrayList<>(true, a);
    }

    @Nonnull
    public static <T> ImmutableList<T> add(@Nonnull Collection<T> collection, int index, T item) {
        if (collection == null || collection.isEmpty() && index == 0) {
            return of(item);
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length + 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index, b, index + 1, a.length - index);
        b[index] = item;
        return new ImmutableArrayList<>(true, b);
    }

    @Nonnull
    public static <T> ImmutableList<T> add(@Nullable ReadOnlyCollection<T> collection, int index, T item) {
        if (collection == null || collection.isEmpty() && index == 0) {
            return of(item);
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length + 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index, b, index + 1, a.length - index);
        b[index] = item;
        return new ImmutableArrayList<>(true, b);
    }

    @Nonnull
    public static <T> ImmutableList<T> addAll(@Nullable ReadOnlyCollection<T> first, @Nonnull ReadOnlyCollection<T> second) {
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
        return new ImmutableArrayList<>(true, a);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> ImmutableList<T> emptyList() {
        return (ImmutableArrayList<T>) ImmutableArrayList.EMPTY;
    }

    @Nonnull
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> ImmutableList<T> of(T... items) {
        return items.length == 0 ? emptyList() : new ImmutableArrayList<>(items, 0, items.length);
    }

    @Nonnull
    public static <T> ImmutableList<T> ofCollection(Collection<? extends T> collection) {
        return collection.isEmpty() ? emptyList() : new ImmutableArrayList<>(collection);
    }

    @Nonnull
    public static <T> ImmutableList<T> ofIterable(Iterable<? extends T> iterable) {
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

    @Nonnull
    public static <T> ImmutableList<T> ofCollection(ReadOnlyCollection<? extends T> collection) {
        if (collection instanceof ImmutableList) {
            @SuppressWarnings("unchecked")
            ImmutableList<T> unchecked = (ImmutableList<T>) collection;
            return unchecked;
        }
        return collection.isEmpty() ? emptyList() : new ImmutableArrayList<>(collection);
    }

    @Nonnull
    public static <T> ImmutableList<T> ofArray(@Nonnull Object[] a, int offset, int length) {
        return length == 0 ? emptyList() : new ImmutableArrayList<>(a, offset, length);
    }

    @Nonnull
    public static <T> ImmutableList<T> remove(@Nullable ReadOnlyCollection<T> collection, int index) {
        if (collection == null || collection.size() == 1 && index == 0) {
            return emptyList();
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length - 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index + 1, b, index, b.length - index);
        return new ImmutableArrayList<>(true, b);
    }

    @Nonnull
    public static <T> ImmutableList<T> remove(@Nullable Collection<T> collection, int index) {
        if (collection == null || collection.size() == 1 && index == 0) {
            return emptyList();
        }
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        Object[] b = new Object[a.length - 1];
        System.arraycopy(a, 0, b, 0, index);
        System.arraycopy(a, index + 1, b, index, b.length - index);
        return new ImmutableArrayList<>(true, b);
    }

    @Nonnull
    public static <T> ImmutableList<T> remove(@Nullable Collection<T> collection, T item) {
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

    public static <T> ImmutableList<T> set(ReadOnlyCollection<T> collection, int index, T item) {
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        a[index] = item;
        return new ImmutableArrayList<>(true, a);
    }

    public static <T> ImmutableList<T> set(Collection<T> collection, int index, T item) {
        Object[] a = new Object[collection.size()];
        a = collection.toArray(a);
        a[index] = item;
        return new ImmutableArrayList<>(true, a);
    }

    public static <E> ImmutableList<E> removeAll(ReadOnlyCollection<E> list, Collection<? extends E> collection) {
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

    public static <E> ImmutableList<E> reverse(ReadOnlyCollection<E> list) {
        int n = list.size();
        Object[] a = new Object[n];
        int j = n - 1;
        for (E e : list) {
            a[j--] = e;
        }
        return new ImmutableArrayList<>(a, 0, n);
    }

    public static <E> ImmutableList<E> reverse(Collection<E> list) {
        int n = list.size();
        Object[] a = new Object[n];
        int j = n - 1;
        for (E e : list) {
            a[j--] = e;
        }
        return new ImmutableArrayList<>(a, 0, n);
    }

    public static <E> ImmutableList<E> subList(List<E> list, int fromIndex, int toIndex) {
        return new ImmutableArrayList<>(true, list.subList(fromIndex, toIndex).toArray());
    }

    public static <E> ImmutableList<E> subList(ReadOnlyList<E> list, int fromIndex, int toIndex) {
        return new ImmutableArrayList<>(true, list.subList(fromIndex, toIndex).toArray());
    }
}
