/*
 * @(#)ImmutableHashSet.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * ImmutableHashSet preserves insertion order of items.
 *
 * @author Werner Randelshofer
 */
public final class ImmutableSingletonSet<E> extends AbstractReadOnlySet<E> implements ImmutableSet<E> {

    private final E element;

    public ImmutableSingletonSet(@NonNull E element) {
        this.element = element;
    }


    @Override
    public boolean contains(Object o) {
        return Objects.equals(element, o);
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            boolean consumed;

            @Override
            public boolean hasNext() {
                return !consumed;
            }

            @Override
            public E next() {
                if (!consumed) {
                    consumed = true;
                    return element;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int size() {
        return 1;
    }

    public void copyInto(Object[] out, int offset) {
        out[offset] = element;
    }
}
