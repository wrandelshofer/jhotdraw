/*
 * @(#)IntBidiGraph.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.PrimitiveIterator;

/**
 * This interface provides read access to a directed graph {@code G = (int, A) }.
 * <p>
 * This interface provides access to the following data in addition to the data
 * that interface {@link DirectedGraph} provides:
 * <ul>
 * <li>The previous count {@code prevCount_i} of the vertex {@code v_i}.</li>
 * <li>The {@code k}-th previous vertex of the vertex {@code v_i}, with
 * {@code k ∈ {0, ..., getPrevCount(i) - 1}}.</li>
 * </ul>
 *
 * @author Werner Randelshofer
 */
public interface IntBidiGraph extends IntDirectedGraph {

    /**
     * Returns the i-th direct predecessor vertex of v.
     *
     * @param vertex a vertex
     * @param i      index of next vertex
     * @return the i-th next vertex of v
     */
    int getPrev(int vertex, int i);

    /**
     * Returns the number of direct predecessor vertices of v.
     *
     * @param vertex a vertex
     * @return the number of next vertices of v.
     */
    int getPrevCount(int vertex);

    /**
     * Returns the direct predecessor vertices of the specified vertex.
     *
     * @param vertex a vertex
     * @return an iterable for the direct predecessor vertices of vertex
     */
    @NonNull
    default PrimitiveIterator.OfInt getPrevVertexIndicesIterator(int vertex) {
        class PrevVertexIterator implements PrimitiveIterator.OfInt {

            private int index;
            private final int vertex;
            private final int prevCount;

            public PrevVertexIterator(int vertex) {
                this.vertex = vertex;
                this.prevCount = getPrevCount(vertex);
            }

            @Override
            public boolean hasNext() {
                return index < prevCount;
            }

            @Override
            public int nextInt() {
                return getPrev(vertex, index++);
            }

        }
        return new PrevVertexIterator(vertex);
    }

    /**
     * Returns the direct predecessor vertices of the specified vertex.
     *
     * @param vertex a vertex
     * @return a collection view on the direct predecessor vertices of vertex
     */
    @NonNull
    default Collection<Integer> getPrevVertices(int vertex) {

        return new AbstractCollection<Integer>() {
            @NonNull
            @Override
            public Iterator<Integer> iterator() {
                return getPrevVertexIndicesIterator(vertex);
            }

            @Override
            public int size() {
                return getPrevCount(vertex);
            }
        };
    }

}
