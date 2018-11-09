package org.jhotdraw8.collection;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

/**
 * WritableLinkedHashSet supports most of the Set API, but does not allow
 * to change the set in iterators, unless a writable iterator is requested,
 * so that it does not violate the contract of ReadableSet.
 *
 * @param <E> the element type
 */
public class WritableLinkedHashSet<E> extends LinkedHashSet<E> implements ReadableSet<E> {
    public WritableLinkedHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public WritableLinkedHashSet(int initialCapacity) {
        super(initialCapacity);
    }

    public WritableLinkedHashSet() {
    }

    public WritableLinkedHashSet(@NotNull Collection<? extends E> c) {
        super(c);
    }

    public WritableLinkedHashSet(@NotNull ReadableCollection<? extends E> c) {
        super(c.size());
        for (E e : c) {
            add(e);
        }
    }
    public WritableLinkedHashSet(@NotNull Object[] array) {
        this(array,0,array.length);
    }
    public WritableLinkedHashSet(@NotNull Object[] array, int offset, int length) {
        super(length);
        for (int i=offset,n=offset+length;i<n;i++) {
            //noinspection unchecked
            add((E)array[i]);
        }
    }

    @Nonnull
    @Override
    public Iterator<E> iterator() {
        class ReadableIterator implements Iterator<E> {
            private final Iterator<E> iter;

            public ReadableIterator(Iterator<E> iter) {
                this.iter = iter;
            }

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public E next() {
                return iter.next();
            }
        }
        return new ReadableIterator(super.iterator());
    }

    @Override
    public Stream<E> stream() {
        return super.stream();
    }
}
