package org.jhotdraw8.collection;

import java.util.AbstractSet;
import java.util.Iterator;

public class SetWrapper<E> extends AbstractSet<E> {
    private final ReadableSet<E> backingSet;

    public SetWrapper(ReadableCollection<E> backingSet) {
        this.backingSet = ImmutableSet.ofCollection(backingSet);
    }
    public SetWrapper(ReadableSet<E> backingSet) {
        this.backingSet = backingSet;
    }

    @Override
    public Iterator<E> iterator() {
        return backingSet.iterator();
    }

    @Override
    public int size() {
        return backingSet.size();
    }

    @Override
    public boolean contains(Object o) {
        //noinspection unchecked
        return backingSet.contains((E)o);
    }
}
