/* @(#)ReadOnlyListWrapper.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

/**
 * Wraps a {@link List} in the {@link ReadOnlyList} API.
 * <p>
 * The underlying List is referenced - not copied. This allows to pass a
 * list to a client while preventing that the client can modify the list directly.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public final class ReadOnlyListWrapper<E> extends AbstractReadOnlyList<E> {

    private final List<? extends E> backingList;

    public ReadOnlyListWrapper(List<? extends E> backingList) {
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
