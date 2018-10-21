package org.jhotdraw8.collection;

import java.util.Iterator;

public class ReadOnlyListIterator<E> implements Iterator<E> {
    private final ReadOnlyList<E> list;
    int index = 0;
    final int size;

    public ReadOnlyListIterator(ReadOnlyList<E> list) {
        this.list = list;
        this.size = list.size();
    }

    @Override
    public boolean hasNext() {
        return index < size;
    }

    @Override
    public E next() {
        return list.get(index++);
    }

}
