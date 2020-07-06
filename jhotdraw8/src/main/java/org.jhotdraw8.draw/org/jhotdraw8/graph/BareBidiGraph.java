/*
 * @(#)BareBidiGraph.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;

/**
 * Adds methods to the API defined in {@link BareDirectedGraph}
 * that allow to follow arrows in backward direction.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public interface BareBidiGraph<V, A> extends BareDirectedGraph<V, A> {
    /**
     * Returns the previous vertex associated with the specified vertex and
     * incoming arrow index.
     *
     * @param vertex a vertex
     * @param index  index of incoming arrow
     * @return the previous vertex
     * @see #getPrevCount
     */
    @NonNull
    V getPrev(@NonNull V vertex, int index);

    /**
     * Returns the arrow associated with the specified vertex and incoming arrow
     * index.
     *
     * @param vertex a vertex
     * @param index  index of incoming arrow
     * @return the arrow
     * @see #getPrevCount
     */
    @NonNull
    A getPrevArrow(@NonNull V vertex, int index);

    /**
     * Returns the number of previous vertices at the specified vertex.
     * <p>
     * This number is the same as the number of incoming arrows at the
     * specified vertex.
     *
     * @param vertex a vertex
     * @return the number of previous vertices
     */
    int getPrevCount(@NonNull V vertex);
}
