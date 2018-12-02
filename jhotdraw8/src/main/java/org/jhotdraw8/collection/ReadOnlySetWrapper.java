/* @(#)ReadOnlySetWrapper.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Iterator;
import java.util.Set;

/**
 * Wraps a {@link Set} in the {@link ReadOnlySet} API.
 * <p>
 * The underlying Set is referenced - not copied. This allows to pass a
 * set to a client while preventing that the client can modify the set directly.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public final class ReadOnlySetWrapper<E> extends AbstractReadOnlySet<E> {

    private final Set<? extends E> backingSet;

    public ReadOnlySetWrapper(Set<? extends E> backingSet) {
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
