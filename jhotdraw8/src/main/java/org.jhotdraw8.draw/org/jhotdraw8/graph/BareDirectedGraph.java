/*
 * @(#)BareDirectedGraph.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;

import java.util.Collection;

/**
 * Provides a minimal read-only API for a directed graph.
 * <p>
 * A directed graph is a tuple {@code G = (V, A)} where {@code V} is a set of
 * vertices and {@code A} is a set or bag of arrows.
 * <p>
 * This facade supports a data object for each arrow. The type of the arrow data
 * object is provided by the type parameter {@literal <A>}.
 * <p>
 * Users of this interface may define {@literal <A>} as a tuple
 * {@code (v_i, v_j)} but are not required to do so, because this interface
 * provides methods for accessing the next vertex of a given vertex without
 * having to deal with the arrow object.
 *
 * @param <V> the vertex type
 * @param <A> the arrow data type
 */
public interface BareDirectedGraph<V, A> {
    /**
     * Returns the next vertex associated with
     * the specified vertex and outgoing arrow index.
     *
     * @param vertex a vertex
     * @param index  index of outgoing arrow
     * @return the next vertex
     * @see #getNextCount
     */
    @NonNull
    V getNext(@NonNull V vertex, int index);

    /**
     * Returns the arrow data associated with the specified vertex and outgoing arrow
     * index.
     *
     * @param vertex a vertex
     * @param index  index of outgoing arrow
     * @return the next arrow data
     * @see #getNextCount
     */
    @NonNull
    A getNextArrow(@NonNull V vertex, int index);

    /**
     * Returns the number of next vertices at the specified vertex.
     * <p>
     * This number is the same as the number of outgoing arrows at the specified
     * vertex.
     *
     * @param vertex a vertex
     * @return the number of next vertices
     */
    int getNextCount(@NonNull V vertex);

    /**
     * Returns all vertices.
     *
     * @return a collection view on all vertices
     */
    @NonNull
    Collection<V> getVertices();

    /**
     * Returns all arrow data objects.
     *
     * @return a collection view on all arrow data objects
     */
    @NonNull
    Collection<A> getArrows();
}
