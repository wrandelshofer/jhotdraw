/* @(#)DirectedGraphWithEdges.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

import javax.annotation.Nonnull;

/**
 * DirectedGraphWithEdges.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface DirectedGraphWithEdges<V,E> extends DirectedGraph<V> {
    /**
     * Returns the specified edge.
     *
     * @param indexOfEdge index of edge
     * @return edge
     */
    E getEdge(int indexOfEdge);
    /**
     * Returns the i-th outgoing (next) edge of the specified vertex.
     *
     * @param vertex a vertex
     * @param i index of edge
     * @return the i-th outgoing (next) edge
     */
    @Nonnull
    E getEdge(@Nonnull V vertex, int i);

}
