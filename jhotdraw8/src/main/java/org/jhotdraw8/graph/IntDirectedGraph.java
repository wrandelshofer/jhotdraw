/* @(#)IntDoubleVertexGraphModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import javax.annotation.Nonnull;

/**
 * A facade for a directed graph where the vertices are integers from {@code 0}
 * to {@code vertexCount - 1}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface IntDirectedGraph {

    /**
     * Returns the number of edges.
     *
     * @return edge count
     */
    int getEdgeCount();

    /**
     * Returns the i-th next vertex of v.
     *
     * @param v a vertex
     * @param i the index of the desired next vertex
     * @return i the index
     */
    int getNext(int v, int i);

    /**
     * Returns the number of next vertices of v.
     *
     * @param v a vertex
     * @return the number of next vertices of v.
     */
    int getNextCount(int v);

    /**
     * Returns the number of vertices {@code V}.
     *
     * @return vertex count
     */
    int getVertexCount();

    
        /**
     * Returns the index of vertex b.
     *
     * @param a a vertex
     * @param b another vertex
     * @return index of vertex b. Returns -1 if b is not next index of a.
     */
    default int findIndexOfNext(int a, int b) {
        for (int i = 0, n = getNextCount(a); i < n; i++) {
            if (b==getNext(a, i)) {
                return i;
            }
        }
        return -1;
    }
}
