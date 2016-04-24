/* @(#)IndexedSet.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import javafx.collections.ObservableListBase;

/**
 * A {@code Set} that provides precise control where each element is inserted.
 * <p>
 * The set is backed by a list.
 *
 * @author Werner Randelshofer
 */
public class IndexedSet<E> extends ObservableListBase<E> implements Set<E>, Deque<E> {

    /**
     * The underlying list.
     */
    private final List<E> list;

    /** Creates a new instance which is backed by an array list. */
    public IndexedSet() {
        this(new ArrayList<>(), null);
    }

    /** Creates a new instance and adds all elements of the specified collection to it.
     * @param col A collection.
     */
    public IndexedSet(Collection<? extends E> col) {
        this(new ArrayList<>(), col);
    }

    /** Creates a new instance with the specified backing list, clears the
     * backing list and the adds all elements of the specified collection to it.
     * 
     * 
     * @param backingList the backing list
     * @param col A collection.
     */
    public IndexedSet(List<E> backingList, Collection<? extends E> col) {
        list = backingList;
        list.clear();
        if (col != null) {
            addAll(col);
        }
    }

    @Override
    public boolean setAll(Collection<? extends E> col) {
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
    public boolean addAll(Collection<? extends E> c) {
        beginChange();
        try {
            boolean res = super.addAll(c);
            return res;
        } finally {
            endChange();
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        beginChange();
        try {
            boolean res = super.addAll(index, c);
            return res;
        } finally {
            endChange();
        }
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        beginChange();
        try {
            super.removeRange(fromIndex, toIndex);
        } finally {
            endChange();
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        beginChange();
        try {
            boolean res = super.removeAll(c);
            return res;
        } finally {
            endChange();
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
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
        E element = list.remove(oldIndex);
        list.add(newIndex, element);
        beginChange();
        int from = Math.min(oldIndex, newIndex);
        int to = Math.max(oldIndex, newIndex) + 1;
        int[] perm = new int[to - from];
        for (int i = 1; i < perm.length - 1; i++) {
            perm[i] = from + i;
        }
        perm[oldIndex - from] = newIndex;
        perm[newIndex - from] = oldIndex;
        nextPermutation(from, to, perm);
        endChange();

    }

    protected boolean doAdd(int index, E element, boolean checkForDuplicates) {
        int oldIndex = checkForDuplicates ? list.indexOf(element):-1; // linear search!
        if (oldIndex == -1) {
            list.add(index, element);
            beginChange();
            nextAdd(index, index + 1);
            onAdded(element);
            ++modCount;
            endChange();
            return true;
        } else if (oldIndex == index || oldIndex == index - 1) {
            // the element is already in the list at the desired index
            return false;
        } else {
            // the old element is permuted from the old index to the desired index
            move(oldIndex, oldIndex < index ? index - 1 : index);
            return false;
        }
    }

    @Override
    public E set(int index, E element) {
        int oldIndex = list.indexOf(element);
        if (oldIndex == -1) {
            E old = list.set(index, element);
            beginChange();
            nextSet(index, old);
            onRemoved(old);
            endChange();
            return old;
        } else if (oldIndex == index) {
            // the element is replaced by itself
            return element;
        } else {
            // the element at the index is removed
            beginChange();
            E old = list.remove(index);
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

    @Override
    public boolean remove(Object o) {
        int i = indexOf(o);
        if (i != - 1) {
            remove(i);
            return true;
        }
        return false;
    }

    @Override
    public E remove(int index) {
        E old = list.remove(index);
        beginChange();
        nextRemove(index, old);
        ++modCount;
        onRemoved(old);
        endChange();
        return old;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return new SubObservableList(super.subList(fromIndex, toIndex));
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean add(E e) {
        return doAdd(size(), e, true);
    }

    public Iterator<E> descendingIterator(int index) {
        return new ObservableDescendingIterator(index);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingIterator(size());
    }

    public Iterable<E> descending() {
        return descending(size());
    }

    public Iterable<E> descending(int index) {
        return new Iterable<E>() {

            @Override
            public Iterator<E> iterator() {
                return descendingIterator(index);
            }

        };
    }

    public Iterable<E> ascending() {
        return ascending(size());
    }

    public Iterable<E> ascending(int index) {
        return new Iterable<E>() {

            @Override
            public Iterator<E> iterator() {
                return listIterator(index);
            }

        };
    }

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

        private ListIterator<E> iter;
        private E lastReturned;
        private int nextIndex;

        public ObservableListIterator(int index) {
            this.iter = list.listIterator(index);
            this.nextIndex = index;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public E next() {
            lastReturned = iter.next();
            nextIndex++;
            return lastReturned;
        }

        @Override
        public boolean hasPrevious() {
            return iter.hasPrevious();
        }

        @Override
        public E previous() {
            lastReturned = iter.previous();
            nextIndex--;
            return lastReturned;
        }

        @Override
        public int nextIndex() {
            return iter.nextIndex();
        }

        @Override
        public int previousIndex() {
            return iter.previousIndex();
        }

        @Override
        public void remove() {
            iter.remove();
            beginChange();
            nextRemove(nextIndex - 1, lastReturned);
            onRemoved(lastReturned);
            endChange();
        }

        @Override
        public void set(E e) {
            if (contains(e)) {
                throw new UnsupportedOperationException("Can not permute element in iterator");
            }
            E oldValue = lastReturned;
            lastReturned = e;
            iter.set(e);
            beginChange();
            nextSet(nextIndex - 1, oldValue);
            endChange();
        }

        @Override
        public void add(E e) {
            if (contains(e)) {
                throw new UnsupportedOperationException("Can not permute element in iterator");
            }
            iter.add(e);
            nextIndex++;
            beginChange();
            nextAdd(nextIndex, nextIndex + 1);
            onAdded(e);
            endChange();
        }

    }

    public int indexOf(Object o) {
        return list.indexOf(o);
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
        return list.get(0);
    }

    @Override
    public E getLast() {
        return list.get(list.size() - 1);
    }

    @Override
    public final boolean offerFirst(E e) {
        return doAdd(0, e, true);
    }

    @Override
    public final boolean offerLast(E e) {
        return doAdd(size(), e, true);
    }

    @Override
    public final E pollFirst() {
        return isEmpty() ? null : removeFirst();
    }

    @Override
    public final E pollLast() {
        return isEmpty() ? null : removeLast();
    }

    @Override
    public final E peekFirst() {
        return isEmpty() ? null : getFirst();
    }

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

    @Override
    public final E poll() {
        return pollFirst();
    }

    @Override
    public final E element() {
        return getFirst();
    }

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

        @Override
        public Iterator<E> iterator() {
            return sublist.iterator();
        }

        @Override
        public Object[] toArray() {
            return sublist.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
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
        public boolean containsAll(Collection<?> c) {
            return sublist.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            beginChange();
            try {
                boolean res = sublist.addAll(c);
                return res;
            } finally {
                endChange();
            }
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            beginChange();
            try {
                boolean res = sublist.addAll(index, c);
                return res;
            } finally {
                endChange();
            }
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            beginChange();
            try {
                boolean res = sublist.removeAll(c);
                return res;
            } finally {
                endChange();
            }
        }

        @Override
        public boolean retainAll(Collection<?> c) {
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

        @Override
        public ListIterator<E> listIterator() {
            return sublist.listIterator();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return sublist.listIterator(index);
        }

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

    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.ORDERED);
    }

}
