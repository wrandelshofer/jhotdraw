package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.Nonnull;
import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * CollectionWrapper.
 *
 * @author Werner Randelshofer
 */
public class CollectionWrapper<E> extends AbstractCollection<E> {
    private final ReadOnlyCollection<E> backingCollection;

    public CollectionWrapper(ReadOnlyCollection<E> backingCollection) {
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
        return backingCollection.contains(o);
    }
}