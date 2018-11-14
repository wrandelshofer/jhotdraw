/* @(#)ImmutableSet.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

/**
 * ImmutableSet preserves insertion order of items.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public final class ReadableListWrapper<E> extends AbstractReadableList<E> {

    private final List<? extends E> backingList;

    public ReadableListWrapper(List<? extends E> backingList) {
        this.backingList = backingList;
    }

    @Override
    public boolean contains(Object o) {
        return backingList.contains(o);
    }

    @Override
    public E get(int index) {
        return backingList.get(index);
    }

    @Nonnull
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<? extends E> i = backingList.iterator();

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
        return backingList.size();
    }

    public void copyInto(Object[] out, int offset) {
        int i = offset;
        for (E e : this) {
            out[i++] = e;
        }
    }

}
