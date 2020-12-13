/*
 * @(#)PushBackEnumerator.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.collection;

/**
 * An enumerator that can push back the current element.
 *
 * @param <E> the element type
 */
public interface PushBackEnumerator<E> extends Enumerator<E>{
    /**
     * Pushes the current element back into the enumeration.
     * <p>
     * So that a subsequent call to {@link Enumerator#moveNext()}
     * will have the same effect as the call to
     * {@link Enumerator#moveNext()} that was done before this
     * method has been called.
     */
    void pushBack();
}
