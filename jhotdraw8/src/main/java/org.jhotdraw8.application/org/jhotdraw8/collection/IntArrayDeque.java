/* @(#)IntArrayDeque
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.Nonnull;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * IntArrayDeque.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntArrayDeque {
    /**
     * The length of this array is always a power of 2.
     */
    private int[] elements;

    /**
     * Index of the element at the head of the deque.
     */
    private int head;

    /**
     * Index at which the next element would be added to the tail of the deque.
     */
    private int tail;

    public IntArrayDeque() {
        this(8);
    }

    public IntArrayDeque(int capacity) {
        elements = new int[capacity];
    }

    /**
     * Inserts the specified element at the head of this deque.
     *
     * @param e the element to add
     */
    public void addFirst(int e) {
        head = (head - 1) & (elements.length - 1);
        elements[head] = e;
        if (head == tail) {
            doubleCapacity();
        }
    }

    /**
     * Inserts the specified element at the tail of this deque.
     *
     * @param e the element
     */
    public void addLast(int e) {
        elements[tail] = e;
        tail = (tail + 1) & (elements.length - 1);
        if (tail == head) {
            doubleCapacity();
        }
    }

    /**
     * Removes the element at the head of the deque.
     *
     * @throws NoSuchElementException {@inheritDoc}
     */
    public int removeFirst() {
        if (head == tail) {
            throw new NoSuchElementException();
        }
        int result = elements[head];
        elements[head] = 0;
        head = (head == elements.length - 1) ? 0 : head + 1;
        return result;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public int removeLast() {
        if (head == tail) {
            throw new NoSuchElementException();
        }
        tail = (tail == 0) ? elements.length - 1 : tail - 1;
        int result = elements[tail];
        elements[tail] = 0;
        return result;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public int getFirst() {
        if (head == tail) {
            throw new NoSuchElementException();
        }
        int result = elements[head];
        return result;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public int getLast() {
        if (head == tail) {
            throw new NoSuchElementException();
        }
        int result = elements[tail == 0 ? elements.length - 1 : tail - 1];
        return result;
    }

    /**
     * Increases the capacity of this deque.
     */
    private void doubleCapacity() {
        assert head == tail;
        int p = head;
        int n = elements.length;
        int r = n - p; // number of elements to the right of p
        int newCapacity = n << 1;
        if (newCapacity < 0) {
            throw new IllegalStateException("Sorry, deque too big");
        }
        int[] a = new int[newCapacity];
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        elements = a;
        head = 0;
        tail = n;
    }

    @Nonnull
    public Iterator<Integer> iterator() {
        return new DeqIterator();
    }

    private class DeqIterator implements Iterator<Integer> {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        private int cursor = head;

        /**
         * Tail recorded at construction (also in remove), to stop
         * iterator and also to check for comodification.
         */
        private int fence = tail;

        /**
         * Index of element returned by most recent call to next.
         * Reset to -1 if element is deleted by a call to remove.
         */
        private int lastRet = -1;

        public boolean hasNext() {
            return cursor != fence;
        }

        public Integer next() {
            if (cursor == fence) {
                throw new NoSuchElementException();
            }
            int result = elements[cursor];
            // This check doesn't catch all possible comodifications,
            // but does catch the ones that corrupt traversal
            if (tail != fence) {
                throw new ConcurrentModificationException();
            }
            lastRet = cursor;
            cursor = (cursor + 1) & (elements.length - 1);
            return result;
        }


    }


    /**
     * Returns the number of elements in this deque.
     *
     * @return the number of elements in this deque
     */
    public int size() {
        return (tail - head) & (elements.length - 1);
    }

    /**
     * Returns true if this deque is empty.
     *
     * @return {@code true} if this deque contains no elements
     */
    public boolean isEmpty() {
        return head == tail;
    }

    @Nonnull
    public String toString() {
        Iterator<Integer> it = iterator();
        if (!it.hasNext()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (; ; ) {
            Integer e = it.next();
            sb.append(e);
            if (!it.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(',').append(' ');
        }
    }
}
