/*
 * @(#)ImmutableHashSet.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.Nonnull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ImmutableHashSet preserves insertion order of items.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public final class ImmutableHashSet<E> extends AbstractReadOnlySet<E> implements ImmutableSet<E> {

    final static ImmutableHashSet<Object> EMPTY = new ImmutableHashSet<>(Collections.emptySet());
    private final Set<E> backingSet;

    public ImmutableHashSet(Collection<? extends E> copyMe) {
        switch (copyMe.size()) {
            case 0:
                backingSet = Collections.emptySet();
                break;
            case 1:
                backingSet = Collections.singleton(copyMe.iterator().next());
                break;
            default:
                this.backingSet = new LinkedHashSet<>(copyMe);
        }
    }

    public ImmutableHashSet(ReadOnlyCollection<? extends E> copyMe) {
        switch (copyMe.size()) {
            case 0:
                backingSet = Collections.emptySet();
                break;
            case 1:
                backingSet = Collections.singleton(copyMe.iterator().next());
                break;
            default:
                this.backingSet = new LinkedHashSet<>(Math.max(2 * copyMe.size(), 11));
                for (E e : copyMe) {
                    backingSet.add(e);
                }
        }
    }

    ImmutableHashSet(@Nonnull Object[] array) {
        this(array, 0, array.length);
    }

    @SuppressWarnings("unchecked")
    ImmutableHashSet(Object[] a, int offset, int length) {
        switch (length) {
            case 0:
                backingSet = Collections.emptySet();
                break;
            case 1:
                backingSet = Collections.singleton((E) a[offset]);
                break;
            default:
                this.backingSet = new LinkedHashSet<>(Math.max(2 * length, 11));
                for (int i = offset, n = offset + length; i < n; i++) {
                    backingSet.add((E) a[i]);
                }
        }

    }

    ImmutableHashSet(boolean privateConstructor, Set<E> backingSet) {
        this.backingSet = backingSet;
    }

    @Override
    public boolean contains(Object o) {
        return backingSet.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<? extends E> i = backingSet.iterator();

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public E next() {
                return i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int size() {
        return backingSet.size();
    }

    public void copyInto(Object[] out, int offset) {
        int i = offset;
        for (E e : this) {
            out[i++] = e;
        }
    }

}
