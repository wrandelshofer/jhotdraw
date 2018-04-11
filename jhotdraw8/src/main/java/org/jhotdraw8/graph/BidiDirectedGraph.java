/* @(#)BidiDirectedGraph
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.Iterator;

/**
 * This interface provides read access to a directed graph {@code G = (V, A) }.
 * <p>
 * This interface provides access to the following data in addition to the data
 * that interface {@link DirectedGraph} provides:
 * <ul>
 * <li>The previous count {@code prevCount_i} of the vertex {@code v_i}.</li>
 * <li>The {@code k}-th previous vertex of the vertex {@code v_i}, with
 * {@code k ∈ {0, ..., getPrevCount(i) - 1}}.</li>
 * </ul>
 *
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public interface BidiDirectedGraph<V, A> extends DirectedGraph<V, A> {

    /**
     * Returns the i-th direct predecessor vertex of v.
     *
     * @param vertex a vertex
     * @param i index of next vertex
     * @return the i-th next vertex of v
     */
    V getPrev(V vertex, int i);

    /**
     * Returns the number of direct predecessor vertices of v.
     *
     * @param vertex a vertex
     * @return the number of next vertices of v.
     */
    int getPrevCount(V vertex);

    /**
     * Returns the direct predecessor vertices of the specified vertex.
     *
     * @param vertex a vertex
     * @return an iterable for the direct predecessor vertices of vertex
     */
    default Iterable<V> getPrevVertices(V vertex) {
        class PrevVertexIterator implements Iterator<V> {

            private int index;
            private final V vertex;
            private final int prevCount;

            public PrevVertexIterator(V vertex) {
                this.vertex = vertex;
                this.prevCount = getPrevCount(vertex);
            }

            @Override
            public boolean hasNext() {
                return index < prevCount;
            }

            @Override
            public V next() {
                return getPrev(vertex, index++);
            }

        }
        return () -> new PrevVertexIterator(vertex);
    }

    /**
     * Returns an {@link Iterable} which performs a backwards breadth first
     * search starting at the given vertex.
     *
     * @param start the start vertex
     * @return backwards breadth first search
     */
    default Iterable<V> breadthFirstSearchBackwards(V start) {
        return () -> new InverseBreadthFirstVertexSpliterator<>(this, start);
    }
}
