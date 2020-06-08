/*
 * @(#)Pair.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;


import org.jhotdraw8.annotation.Nullable;

/**
 * Pair.
 *
 * @param <V> the type of the objects which form a pair
 * @author Werner Randelshofer
 */
public interface Pair<V> {

    @Nullable V getStart();

    @Nullable V getEnd();
}
