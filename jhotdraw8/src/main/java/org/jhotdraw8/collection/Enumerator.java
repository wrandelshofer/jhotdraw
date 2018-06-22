/* @(#)Enumerator.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * Provides a C#-style Enumerator API.
 *
 * @param <E> the element type
 * @author Werner Randelshofer
 */
public interface Enumerator<E> {
    /**
     * Advances the enumerator to the next element of the collection..
     *
     * @return true if the enumerator was successfully advanced to the next element;
     * false if the enumerator has passed the end of the collection.
     */
    boolean moveNext();

    /**
     * Gets the element in the collection at the current position of the enumerator.
     * <p>
     * Current is undefined under any of the following conditions:
     * <ul>
     * <li> The enumerator is positioned before the first element in the collection,
     * immediately after the enumerator is created moveNext must be called to advance
     * the enumerator to the first element of the collection before reading the value of Current.</li>
     *
     * <li>The last call to MoveNext returned false, which indicates the end
     * of the collection.</li>
     *
     * <li>The enumerator is invalidated due to changes made in the collection,
     * such as adding, modifying, or deleting elements.</li>
     * </ul>
     * Current returns the same object until MoveNext is called.MoveNext
     * sets Current to the next element.
     *
     * @return current
     * @throws IllegalStateException
     */
    E current();
}
