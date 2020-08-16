/*
 * @(#)IndexedSet.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.collections.ObservableListBase;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A {@code Set} that provides precise control where each element is inserted.
 * <p>
 * The set is backed by a list.
 *
 * @author Werner Randelshofer
 */
public class IndexedSet<E> extends ObservableListBase<E> implements Set<E>, Deque<E> {

    private static final Object[] EMPTY_ARRAY = new Object[0];
    /**
     * The underlying list.
     */
    private Object[] data;
    private int size;

    /**
     * Creates a new instance.
     */
    public IndexedSet() {
        data = EMPTY_ARRAY;
    }

    /**
     * Creates a new instance and adds all elements of the specified collection
     * to it.
     *
     * @param col A collection.
     */
    public IndexedSet(Collection<? extends E> col) {
        this.size = col.size();
        data = Arrays.copyOf(col.toArray(), size, Object[].class);
    }

    @Override
    public boolean setAll(@NonNull Collection<? extends E> col) {
        beginChange();
        try {
            clear();
            addAll(col);
        } finally {
            endChange();
        }
        return true;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        beginChange();
        try {
            return super.addAll(c);
        } finally {
            endChange();
        }
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends E> c) {
        beginChange();
        try {
            return super.addAll(index, c);
        } finally {
            endChange();
        }
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        beginChange();
        try {
            boolean res = super.removeAll(c);
            return res;
        } finally {
            endChange();
        }
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        beginChange();
        try {
            boolean res = super.retainAll(c);
            return res;
        } finally {
            endChange();
        }
    }

    @Override
    public void add(int index, E element) {
        doAdd(index, element, true);
    }

    /**
     * Moves an element at {@code oldIndex} to {@code newIndex}.
     * <p>
     * So that {@code indexOf(element) == newIndex};
     *
     * @param oldIndex the current index of the element
     * @param newIndex the desired new index of the element
     */
    public void move(int oldIndex, int newIndex) {
        if (oldIndex == newIndex) {
            return;
        }
        beginChange();
        doSet(newIndex, doSet(oldIndex, doGet(newIndex)));
        int from = min(oldIndex, newIndex);
        int to = max(oldIndex, newIndex) + 1;
        int[] perm = new int[to - from];
        for (int i = 1; i < perm.length - 1; i++) {
            perm[i] = from + i;
        }
        perm[oldIndex - from] = newIndex;
        perm[newIndex - from] = oldIndex;
        nextPermutation(from, to, perm);
        endChange();

    }

    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) data[index];
    }

    private E doSet(int index, E newValue) {
        Preconditions.checkIndex(index, size);
        E oldValue = elementData(index);
        data[index] = newValue;
        return oldValue;
    }

    protected boolean doAdd(int index, E element, boolean checkForDuplicates) {
        int oldIndex = checkForDuplicates ? indexOf(element) : -1; // linear search!
        int clampedIndex = min(index, size() - 1);
        if (oldIndex == -1) {
            dataDoAdd(index, element);
            beginChange();
            nextAdd(index, index + 1);
            onAdded(element);
            ++modCount;
            endChange();
            return true;
        } else if (oldIndex == clampedIndex || index - oldIndex == 1) {
            return false;
        } else {
            // the old element is moved from the old index to the desired index
            beginChange();
            dataDoRemove(oldIndex);
            nextRemove(oldIndex, element);
            int addIndex = oldIndex < index ? index - 1 : index;
            dataDoAdd(addIndex, element);
            nextAdd(addIndex, addIndex + 1);
            ++modCount;
            endChange();
            return false;
        }
    }

    private void dataDoAdd(int index, E element) {
        Preconditions.checkIndex(index, size + 1);
        ensureCapacity(size + 1);
        System.arraycopy(data, index,
                data, index + 1,
                size - index);
        data[index] = element;
        size++;
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = data.length;
        if (minCapacity > oldCapacity) {
            int newCapacity = max(minCapacity + 1, oldCapacity + oldCapacity);
            if (newCapacity < 0) {
                throw new IllegalStateException("Array too large.");
            }
            data = Arrays.copyOf(data, max(16, newCapacity));
        }
    }

    @Override
    public E set(int index, E element) {
        int oldIndex = indexOf(element);
        if (oldIndex == -1) {
            beginChange();
            E old = doSet(index, element);
            onRemoved(old);
            nextSet(index, old);
            onAdded(element);
            endChange();
            return old;
        } else if (oldIndex == index) {
            // the element is replaced by itself
            return element;
        } else {
            // the element at the index is removed
            beginChange();
            E old = dataDoRemove(index);
            nextRemove(index, old);
            onRemoved(old);
            // the old element is permuted
            if (oldIndex > index) {
                oldIndex--;
            }
            move(oldIndex, oldIndex < index ? index - 1 : index);
            endChange();
            return old;
        }
    }

    private E dataDoRemove(int index) {
        Preconditions.checkIndex(index, size);
        E oldValue = elementData(index);
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        data[--size] = null;
        return oldValue;
    }

    @SuppressWarnings("unchecked")
    private List<E> dataDoRemoveRange(int fromIndex, int toIndex) {
        Preconditions.checkFromToIndex(fromIndex, toIndex, size);
        int removedCount = toIndex - fromIndex;
        ArrayList<E> removed = new ArrayList<>(removedCount);
        if (removedCount > 0) {
            for (int i = fromIndex; i < toIndex; i++) {
                removed.add((E) data[i]);
            }
            System.arraycopy(data, fromIndex, data, toIndex, size - fromIndex - 1);
            for (int i = size - removedCount; i < size; i++) {
                data[i] = null;
            }
            size -= removedCount;
        }
        return removed;
    }


    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0; // linear time!
    }

    @Override
    public boolean remove(Object o) {
        int i = indexOf(o);
        if (i != -1) {
            remove(i);
            return true;
        }
        return false;
    }

    @Override
    public E remove(int index) {
        E old = dataDoRemove(index);
        beginChange();
        nextRemove(index, old);
        ++modCount;
        onRemoved(old);
        endChange();
        return old;
    }

    @Override
    public void removeRange(int fromIndex, int toIndex) {
        List<E> removed = dataDoRemoveRange(fromIndex, toIndex);
        beginChange();
        nextRemove(fromIndex, removed);
        ++modCount;
        for (E old : removed)
            onRemoved(old);
        endChange();
    }

    @NonNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return new SubObservableList(super.subList(fromIndex, toIndex));
    }

    @Override
    public E get(int index) {
        return doGet(index);
    }

    private E doGet(int index) {
        Preconditions.checkIndex(index, size);
        return elementData(index);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(E e) {
        return doAdd(size(), e, true);
    }

    @NonNull
    public Iterator<E> descendingIterator(int index) {
        return new ObservableDescendingIterator(index);
    }

    @NonNull
    @Override
    public Iterator<E> descendingIterator() {
        return descendingIterator(size());
    }

    @NonNull
    public Iterable<E> descending() {
        return descending(size());
    }

    @NonNull
    public Iterable<E> descending(int index) {
        return new Iterable<E>() {

            @NonNull
            @Override
            public Iterator<E> iterator() {
                return descendingIterator(index);
            }

        };
    }

    @NonNull
    public Iterable<E> ascending() {
        return ascending(size());
    }

    @NonNull
    public Iterable<E> ascending(int index) {
        return new Iterable<E>() {

            @NonNull
            @Override
            public Iterator<E> iterator() {
                return listIterator(index);
            }

        };
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator(int index) {
        return new ObservableListIterator(index);
    }

    /**
     * This method is invoked after an element has been removed. Subclasses can
     * override this method to perform an action. This implementation is empty.
     *
     * @param e the removed element
     */
    protected void onRemoved(E e) {

    }

    /**
     * This method is invoked after an element has been added. Subclasses can
     * override this method to perform an action. This implementation is empty.
     *
     * @param e the added element
     */
    protected void onAdded(E e) {

    }

    private class ObservableDescendingIterator implements ListIterator<E> {

        private ObservableListIterator iter;

        public ObservableDescendingIterator(int index) {
            this.iter = new ObservableListIterator(index);
        }

        @Override
        public boolean hasNext() {
            return iter.hasPrevious();
        }

        @Override
        public E next() {
            return iter.previous();
        }

        @Override
        public boolean hasPrevious() {
            return iter.hasNext();
        }

        @Override
        public E previous() {
            return iter.next();
        }

        @Override
        public int nextIndex() {
            return iter.previousIndex();
        }

        @Override
        public int previousIndex() {
            return iter.nextIndex();
        }

        @Override
        public void remove() {
            iter.remove();
        }

        @Override
        public void set(E e) {
            iter.set(e);
        }

        @Override
        public void add(E e) {
            iter.add(e);
        }

    }

    private class ObservableListIterator implements ListIterator<E> {

        private E lastReturned;
        private int index;
        private final int from;
        private int to;

        private int expectedModCount;

        public ObservableListIterator(int index) {
            this.index = index;
            from = 0;
            to = size;
            expectedModCount = modCount;
        }

        @Override
        public boolean hasNext() {
            checkModCount();
            return index < from;
        }

        private void checkModCount() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public E next() {
            checkModCount();
            lastReturned = iterGet(index);
            index++;
            return lastReturned;
        }

        @Override
        public boolean hasPrevious() {
            checkModCount();
            return index > to;
        }

        @Override
        public E previous() {
            checkModCount();
            lastReturned = iterGet(index - 1);
            index--;
            return lastReturned;
        }

        private E iterGet(int index) {
            checkModCount();
            if (index < from || index >= to) {
                throw new NoSuchElementException("index out of bounds:" + index);
            }
            return doGet(index);
        }

        @Override
        public int nextIndex() {
            checkModCount();
            return index;
        }

        @Override
        public int previousIndex() {
            checkModCount();
            return index - 1;
        }

        @Override
        public void remove() {
            checkModCount();
            IndexedSet.this.remove(index - 1);
            index--;
            to--;
            expectedModCount = modCount;
        }

        @Override
        public void set(E e) {
            checkModCount();
            if (contains(e)) {
                throw new UnsupportedOperationException("Can not permute element in iterator");
            }
            IndexedSet.this.set(index - 1, e);
            expectedModCount = modCount;
        }

        @Override
        public void add(E e) {
            checkModCount();
            if (contains(e)) {
                throw new UnsupportedOperationException("Can not permute element in iterator");
            }
            IndexedSet.this.add(index, e);
            index++;
            to++;
            expectedModCount = modCount;
        }

    }

    public int indexOf(Object o) {
        int start = 0, end = size;
        if (o == null) {
            for (int i = start; i < end; i++) {
                if (data[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (o.equals(data[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public final void addFirst(E e) {
        add(0, e);
    }

    @Override
    public final void addLast(E e) {
        add(size(), e);
    }

    @Override
    public final E removeFirst() {
        return remove(0);
    }

    @Override
    public final E removeLast() {
        return remove(size() - 1);
    }

    @Override
    public final E getFirst() {
        return doGet(0);
    }

    @Override
    public E getLast() {
        return doGet(size - 1);
    }

    @Override
    public final boolean offerFirst(E e) {
        return doAdd(0, e, true);
    }

    @Override
    public final boolean offerLast(E e) {
        return doAdd(size(), e, true);
    }

    @Nullable
    @Override
    public final E pollFirst() {
        return isEmpty() ? null : removeFirst();
    }

    @Nullable
    @Override
    public final E pollLast() {
        return isEmpty() ? null : removeLast();
    }

    @Nullable
    @Override
    public final E peekFirst() {
        return isEmpty() ? null : getFirst();
    }

    @Nullable
    @Override
    public final E peekLast() {
        return isEmpty() ? null : getLast();
    }

    @Override
    public final boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public final boolean removeLastOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public final boolean offer(E e) {
        return offerLast(e);
    }

    @Override
    public final E remove() {
        return removeFirst();
    }

    @Nullable
    @Override
    public final E poll() {
        return pollFirst();
    }

    @Override
    public final E element() {
        return getFirst();
    }

    @Nullable
    @Override
    public final E peek() {
        return peekFirst();
    }

    @Override
    public final void push(E e) {
        addFirst(e);
    }

    @Override
    public final E pop() {
        return removeFirst();
    }

    private class SubObservableList implements List<E> {

        public SubObservableList(List<E> sublist) {
            this.sublist = sublist;
        }

        private List<E> sublist;

        @Override
        public int size() {
            return sublist.size();
        }

        @Override
        public boolean isEmpty() {
            return sublist.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return sublist.contains(o);
        }

        @NonNull
        @Override
        public Iterator<E> iterator() {
            return sublist.iterator();
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return sublist.toArray();
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] a) {
            return sublist.toArray(a);
        }

        @Override
        public boolean add(E e) {
            return sublist.add(e);
        }

        @Override
        public boolean remove(Object o) {
            return sublist.remove(o);
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            return sublist.containsAll(c);
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends E> c) {
            beginChange();
            try {
                boolean res = sublist.addAll(c);
                return res;
            } finally {
                endChange();
            }
        }

        @Override
        public boolean addAll(int index, @NonNull Collection<? extends E> c) {
            beginChange();
            try {
                boolean res = sublist.addAll(index, c);
                return res;
            } finally {
                endChange();
            }
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            beginChange();
            try {
                boolean res = sublist.removeAll(c);
                return res;
            } finally {
                endChange();
            }
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            beginChange();
            try {
                boolean res = sublist.retainAll(c);
                return res;
            } finally {
                endChange();
            }
        }

        @Override
        public void clear() {
            beginChange();
            try {
                sublist.clear();
            } finally {
                endChange();
            }
        }

        @Override
        public E get(int index) {
            return sublist.get(index);
        }

        @Override
        public E set(int index, E element) {
            return sublist.set(index, element);
        }

        @Override
        public void add(int index, E element) {
            sublist.add(index, element);
        }

        @Override
        public E remove(int index) {
            return sublist.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return sublist.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return sublist.lastIndexOf(o);
        }

        @NonNull
        @Override
        public ListIterator<E> listIterator() {
            return sublist.listIterator();
        }

        @NonNull
        @Override
        public ListIterator<E> listIterator(int index) {
            return sublist.listIterator(index);
        }

        @NonNull
        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return new SubObservableList(sublist.subList(fromIndex, toIndex));
        }

        @Override
        public boolean equals(Object obj) {
            return sublist.equals(obj);
        }

        @Override
        public int hashCode() {
            return sublist.hashCode();
        }

        @Override
        public String toString() {
            return sublist.toString();
        }
    }

    @NonNull
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.ORDERED);
    }

    @NonNull
    @Override
    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public void fireItemUpdated(int index) {
        beginChange();
        nextUpdate(index);
        endChange();
    }

    public boolean hasChangeListeners() {
        return super.hasListeners();
    }
}
