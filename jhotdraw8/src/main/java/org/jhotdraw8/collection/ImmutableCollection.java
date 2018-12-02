/* @(#)ImmutableCollection.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * Classes with this interface guarantee that the content of the collection is immutable.
 *
 * @param <E> the element type
 */
public interface ImmutableCollection<E> extends ReadOnlyCollection<E> {
}
