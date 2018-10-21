package org.jhotdraw8.collection;

import java.util.AbstractList;

public class ListWrapper<E> extends AbstractList<E> {
    private final ReadOnlyList<E> backingList;

    public ListWrapper(ReadOnlyList<E> backingList) {
        this.backingList = backingList;
    }

    @Override
    public E get(int index) {
        return backingList.get(index);
    }

    @Override
    public int size() {
        return backingList.size();
    }
}
