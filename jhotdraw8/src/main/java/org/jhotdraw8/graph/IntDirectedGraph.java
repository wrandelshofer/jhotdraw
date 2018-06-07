/* @(#)IntDirectedGraph.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;

/**
 * Provides indexed read access to a directed graph {@code G = (V, A) }.
 * <ul>
 * <li>{@code G} is a tuple {@code (V, A) }.</li>
 * <li>{@code V} is the set of vertices with elements {@code v_i ∈ V. i ∈ {0, ..., vertexCount - 1} }.</li>
 * <li>{@code A} is the set of ordered pairs with elements {@code  (v_i, v_j)_k ∈ A. i,j ∈ {0, ..., vertexCount - 1}. k ∈ {0, ..., arrowCount - 1} }.</li>
 * </ul>
 * <p>
 * The API of this class provides access to the following data:
 * <ul>
 * <li>The vertex count {@code vertexCount}.</li>
 * <li>The arrow count {@code arrowCount}.</li>
 * <li>The index {@code i} of each vertex {@code v_i ∈ V}.</li>
 * <li>The index {@code k} of each arrow {@code a_k ∈ A}.</li>
 * <li>The next count {@code nextCount_i} of the vertex with index {@code i}.</li>
 * <li>The index of the {@code k}-th next vertex of the vertex with index {@code i}, and with {@code k ∈ {0, ..., nextCount_i - 1}}.</li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface IntDirectedGraph {

    /**
     * Returns the number of arrows.
     *
     * @return arrow count
     */
    int getArrowCount();

    /**
     * Returns the k-th next vertex of v.
     *
     * @param v a vertex index
     * @param k the index of the desired next vertex, {@code k ∈ {0, ..., getNextCount(v) -1 }}.
     * @return the index of the k-th next vertex of v.
     */
    int getNext(int v, int k);

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
            if (b == getNext(a, i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns true if b is next of a.
     *
     * @param a a vertex
     * @param b another vertex
     * @return true if b is next of a.
     */
    default boolean isNext(int a, int b) {
        return findIndexOfNext(a, b) != -1;
    }

    /**
     * Returns true if b is reachable from a.
     *
     * @param a a vertex
     * @param b another vertex
     * @return true if b is next of a.
     */
    default boolean isReachable(int a, int b) {
        Deque<Integer> stack = new ArrayDeque<>(16);
        BitSet vset = new BitSet(getVertexCount());
        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (!vset.get(current)) {
                vset.set(current);
                if (current == b) {
                    return true;
                }
                for (int i = 0, n = this.getNextCount(current); i < n; i++) {
                    int next = this.getNext(current, i);
                    stack.push(next);
                }
            }
        }
        return false;
    }
}
