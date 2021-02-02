/*
 * @(#)AttributedIntBidiGraph.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * This interface provides read-only indexed access to a directed graph {@code G = (V, A) } with
 * vertex and arrow attributes of the generic types {@code V} and {@code A}.
 * <ul>
 * <li>{@code G} is a tuple {@code (V, A) }.</li>
 * <li>{@code V} is the set of vertices with elements {@code v_i ∈ V. i ∈ {0, ..., vertexCount - 1} }.</li>
 * <li>{@code A} is the set of ordered pairs with elements {@code  (v_i, v_j)_k ∈ A. i,j ∈ {0, ..., vertexCount - 1}. k ∈ {0, ..., arrowCount - 1} }.</li>
 * </ul>
 * <p>
 * This interface provides access to the following data in addition to the interface {@link IntDirectedGraph}:
 * <ul>
 * <li>The vertex {@code v_i ∈ V} .</li>
 * <li>The arrow {@code a_k ∈ A}.</li>
 * <li>The arrow {@code a_(i,j) ∈ A}.</li>
 * </ul>
 *
 * @author wr
 */
public interface AttributedIntBidiGraph<V, A> extends IntBidiGraph, AttributedIntDirectedGraph<V, A> {


    /**
     * Returns the specified predecessor (previous) arrow of the specified vertex.
     *
     * @param vertex a vertex
     * @param index  index of next arrow
     * @return the specified arrow
     */
    A getPrevArrow(int vertex, int index);


    /**
     * Returns the direct predecessor vertices of the specified vertex.
     *
     * @param vertexIndex a vertex
     * @return a collection view on the direct predecessor vertices of vertex
     * with the arrow pointing away from the vertex
     */
    @NonNull
    default Collection<Map.Entry<Integer, A>> getPrevIntEntries(int vertexIndex) {
        class PrevVertexAndArrowIterator implements Iterator<Map.Entry<Integer, A>> {

            private int index;
            private final int vertex;
            private final int prevCount;

            public PrevVertexAndArrowIterator(int vertex) {
                this.vertex = vertex;
                this.prevCount = getPrevCount(vertex);
            }

            @Override
            public boolean hasNext() {
                return index < prevCount;
            }

            @NonNull
            @Override
            public Map.Entry<Integer, A> next() {
                int i = index++;
                return new AbstractMap.SimpleEntry<>(
                        getPrev(vertex, i),
                        getPrevArrow(vertex, i)
                );
            }

        }
        return new AbstractCollection<Map.Entry<Integer, A>>() {
            @NonNull
            @Override
            public Iterator<Map.Entry<Integer, A>> iterator() {
                return new PrevVertexAndArrowIterator(vertexIndex);
            }

            @Override
            public int size() {
                return getPrevCount(vertexIndex);
            }
        };
    }

}
