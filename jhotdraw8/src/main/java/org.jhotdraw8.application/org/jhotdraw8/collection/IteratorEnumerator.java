/*
 * @(#)IteratorEnumerator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.Iterator;

/**
 * Enumerator wrapper for Iterator.
 *
 * @author Werner Randelshofer
 */
public class IteratorEnumerator<E> implements Enumerator<E> {
    @NonNull
    private final Iterator<? extends E> iterator;

    private E current;

    public IteratorEnumerator(final @NonNull Iterator<? extends E> iterator) {
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
