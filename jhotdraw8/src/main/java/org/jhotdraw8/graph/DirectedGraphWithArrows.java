/* @(#)DirectedGraphWithArrows.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import javax.annotation.Nonnull;

/**
 * DirectedGraphWithArrows.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public interface DirectedGraphWithArrows<V, A> extends DirectedGraph<V> {

    /**
     * Returns the specified arrow.
     *
     * @param index index of arrow
     * @return arrow
     */
    @Nonnull
    A getArrow(int index);

    /**
     * Returns the specified successor (next) arrow of the specified vertex.
     *
     * @param vertex a vertex
     * @param index index of next arrow
     * @return the specified arrow
     */
    @Nonnull
    A getArrow(@Nonnull V vertex, int index);

    /**
     * Returns the arrow if b is successor of a.
     *
     * @param a a vertex
     * @param b a vertex
     * @return the arrow or null if b is not next of a
     */
    @Nonnull
    default A findArrow(@Nonnull V a, @Nonnull V b) {
        int index = findIndexOfNext(a, b);
        return index == -1 ? null : getArrow(a, index);
    }

}
