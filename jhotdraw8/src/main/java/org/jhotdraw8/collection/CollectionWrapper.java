package org.jhotdraw8.collection;

import javax.annotation.Nonnull;
import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * CollectionWrapper.
 *
 * @author Werner Randelshofer
 */
public class CollectionWrapper<E> extends AbstractCollection<E> {
    private final ReadableCollection<E> backingCollection;

    public CollectionWrapper(ReadableCollection<E> backingCollection) {
        this.backingCollection = backingCollection;
    }

    @Override
    @Nonnull
    public Iterator<E> iterator() {
        return backingCollection.iterator();
    }

    @Override
    public int size() {
        return backingCollection.size();
    }

    @Override
    public boolean contains(Object o) {
        //noinspection unchecked
        return backingCollection.contains((E)o);
    }
}