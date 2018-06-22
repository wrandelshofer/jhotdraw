/* @(#)IteratorEnumerator.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Enumerator wrapper for Iterator.
 *
 * @author Werner Randelshofer
 */
public class IteratorEnumerator<E> implements Enumerator<E> {
    @NotNull
    private final Iterator<? extends E> iterator;

    private E current;

    public IteratorEnumerator(final @NotNull Iterator<? extends E> iterator) {
        this.iterator = iterator;
    }


    @Override
    public boolean moveNext() {
        if (iterator.hasNext()) {
            current = iterator.next();
            return true;
        }
        return false;
    }

    @Override
    public E current() {
        return current;
    }
}
