/*
 * @(#)SingletonEnumerator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.collection;

public class SingletonEnumerator<T> implements Enumerator<T> {
    private final T current;
    private boolean canMove = true;

    public SingletonEnumerator(T singleton) {
        current = singleton;
    }

    @Override
    public boolean moveNext() {
        boolean hasMoved = canMove;
        canMove = false;
        return hasMoved;
    }

    @Override
    public T current() {
        return current;
    }
}
