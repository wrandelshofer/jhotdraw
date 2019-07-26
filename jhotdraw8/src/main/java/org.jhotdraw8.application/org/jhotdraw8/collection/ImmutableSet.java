/*
 * @(#)ImmutableSet.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * Classes with this interface guarantee that the content of the set is immutable.
 *
 * @param <E> the element type
 */
public interface ImmutableSet<E> extends ReadOnlySet<E>, ImmutableCollection<E> {

}
