/*
 * @(#)ImmutableCollection.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * Provides query methods to a collection. The state of the
 * collection does not change.
 *
 * @param <E> the element type
 */
public interface ImmutableCollection<E> extends ReadOnlyCollection<E> {
}
