/* @(#)SetWrapper.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Wraps a {@link ReadableSet} in the {@link Set} API.
 * <p>
 * The underlying ReadableSet is referenced - not copied. This allows to pass a
 * ReadableSet to a client who does not understand the ReadableSet APi.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SetWrapper<E> extends AbstractSet<E> {
    private final ReadableSet<E> backingSet;

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
        return backingSet.contains(o);
    }
}
