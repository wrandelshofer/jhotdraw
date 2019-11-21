/*
 * @(#)IntDirectedGraph.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;
import java.util.Spliterator;
import java.util.Spliterators.AbstractIntSpliterator;
import java.util.function.IntConsumer;

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
     * @param vids a vertex index
     * @param k    the index of the desired next vertex, {@code k ∈ {0, ..., getNextCount(v) -1 }}.
     * @return the index of the k-th next vertex of v.
     */
    int getNext(int vids, int k);

    /**
     * Returns the number of next vertices of v.
     *
     * @param vids a vertex
     * @return the number of next vertices of v.
     */
    int getNextCount(int vids);

    /**
     * Returns the number of vertices {@code V}.
     *
     * @return vertex count
     */
    int getVertexCount();

    /**
     * Returns the index of vertex b.
     *
     * @param vidxa a vertex
     * @param vidxb another vertex
     * @return index of vertex b. Returns -1 if b is not next index of a.
     */
    default int findIndexOfNext(int vidxa, int vidxb) {
        for (int i = 0, n = getNextCount(vidxa); i < n; i++) {
            if (vidxb == getNext(vidxa, i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns true if b is next of a.
     *
     * @param vidxa a vertex
     * @param vidxb another vertex
     * @return true if b is next of a.
     */
    default boolean isNext(int vidxa, int vidxb) {
        return findIndexOfNext(vidxa, vidxb) != -1;
    }

    /**
     * Returns true if b is reachable from a.
     *
     * @param vidxa a vertex
     * @param vidxb another vertex
     * @return true if b is next of a.
     */
    default boolean isReachable(int vidxa, int vidxb) {
        Deque<Integer> stack = new ArrayDeque<>(16);
        stack.push(vidxb);
        BitSet vset = new BitSet(getVertexCount());
        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (!vset.get(current)) {
                vset.set(current);
                if (current == vidxb) {
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

    /**
     * Returns the direct successor vertices of the specified vertex.
     *
     * @param vidx a vertex index
     * @return a collection view on the direct successor vertices of vertex
     */
    @NonNull
    default Spliterator.OfInt getNextVertices(int vidx) {
        class MySpliterator extends AbstractIntSpliterator {
            int index;
            int limit;

            public MySpliterator(int vidx, int lo, int hi) {
                super(hi, ORDERED | NONNULL | SIZED | SUBSIZED);
                limit = hi;
                index = lo;
            }

            @Override
            public boolean tryAdvance(@NonNull IntConsumer action) {
                if (index < limit) {
                    action.accept(getNext(vidx, index++));
                    return true;
                }
                return false;
            }

            @Nullable
            public MySpliterator trySplit() {
                int hi = limit, lo = index, mid = (lo + hi) >>> 1;
                return (lo >= mid) ? null : // divide range in half unless too small
                        new MySpliterator(vidx, lo, index = mid);
            }

        }
        return new MySpliterator(vidx, 0, getNextCount(vidx));
    }

}
