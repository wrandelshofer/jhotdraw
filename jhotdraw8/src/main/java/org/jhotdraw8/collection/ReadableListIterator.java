/* @(#)ReadableListIterator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Iterator;
import java.util.ListIterator;

public class ReadableListIterator<E> implements Iterator<E>, ListIterator<E> {
    private final ReadOnlyList<E> list;
    int index = 0;
    final int size;

    public ReadableListIterator(ReadOnlyList<E> list) {
        this(list,0);
    }
    public ReadableListIterator(ReadOnlyList<E> list, int index) {
        this.list = list;
        this.size = list.size();
        this.index=index;
    }

    @Override
    public boolean hasNext() {
        return index < size;
    }

    @Override
    public E next() {
        return list.get(index++);
    }

    @Override
    public boolean hasPrevious() {
        return index>0;
    }

    @Override
    public E previous() {
        return list.get(--index);
    }

    @Override
    public int nextIndex() {
        return index;
    }

    @Override
    public int previousIndex() {
        return index-1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(E e) {
        throw new UnsupportedOperationException();
    }

}
