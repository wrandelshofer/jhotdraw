/*
 * @(#)ImmutableSet.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * Provides query methods to a set. The state of the set
 * does not change.
 *
 * @param <E> the element type
 */
public interface ImmutableSet<E> extends ReadOnlySet<E>, ImmutableCollection<E> {

}
