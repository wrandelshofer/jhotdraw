/* @(#)DirectedGraph.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * DirectedGraph.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public interface DirectedGraph<V, A> {

    /**
     * Dumps the graph for debugging purposes.
     *
     * @return a dump of the directed graph
     */
    @Nonnull
    default String dump() {
        return dump(Object::toString);
    }

    /**
     * Dumps the graph for debugging purposes.
     *
     * @param toStringFunction a function which converts a vertex to a string
     * @return the dumped graph
     */
    @Nonnull
    default String dump(@Nonnull Function<V, String> toStringFunction) {
        StringBuilder buf = new StringBuilder();
        buf.append("DirectedGraph:");
        for (int ii = 0, nn = getVertexCount(); ii < nn; ii++) {
            V v = getVertex(ii);
            buf.append("\n  ").append(toStringFunction.apply(v)).append(" -> ");
            for (int i = 0, n = getNextCount(v); i < n; i++) {
                if (i != 0) {
                    buf.append(", ");
                }
                buf.append(toStringFunction.apply(getNext(v, i)));
            }
            buf.append('.');
        }
        return buf.toString();
    }

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

    /**
     * Returns the index of vertex b.
     *
     * @param a a vertex
     * @param b another vertex
     * @return index of vertex b. Returns -1 if b is not next index of a.
     */
    default int findIndexOfNext(@Nonnull V a, @Nonnull V b) {
        for (int i = 0, n = getNextCount(a); i < n; i++) {
            if (b.equals(getNext(a, i))) {
                return i;
            }
        }
        return -1;
    }

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
     * Returns the number of arrows.
     *
     * @return arrow count
     */
    int getArrowCount();

    /**
     * Returns the i-th direct successor vertex of v.
     *
     * @param vertex a vertex
     * @param i index of next vertex
     * @return the i-th next vertex of v
     */
    @Nonnull
    V getNext(@Nonnull V vertex, int i);

    /**
     * Returns the number of direct successor vertices of v.
     *
     * @param vertex a vertex
     * @return the number of next vertices of v.
     */
    int getNextCount(@Nonnull V vertex);

    /**
     * Returns the next successor vertices after the specified vertex.
     *
     * @param vertex a vertex
     * @return an iterable for the next vertices after vertex
     */
    @Nonnull
    default Iterable<V> getNextVertices(@Nonnull V vertex) {
        class NextVertexIterator implements Iterator<V> {

            private int index;
            private final V vertex;
            private final int nextCount;

            public NextVertexIterator(V vertex) {
                this.vertex = vertex;
                this.nextCount = getNextCount(vertex);
            }

            @Override
            public boolean hasNext() {
                return index < nextCount;
            }

            @Override
            public V next() {
                return getNext(vertex, index++);
            }

        }
        return () -> new NextVertexIterator(vertex);
    }

    /**
     * Returns the specified Vertex.
     *
     * @param indexOfVertex index of vertex
     * @return vertex
     */
    @Nonnull
    V getVertex(int indexOfVertex);

    /**
     * Returns the number of vertices {@code V}.
     *
     * @return vertex count
     */
    int getVertexCount();

    /**
     * Dumps the graph for debugging purposes.
     *
     * /** Returns all vertices.
     *
     * @return an iterable for all vertice
     */
    @Nonnull
    default Collection<V> getVertices() {
        class VertexIterator implements Iterator<V> {

            private int index;
            private final int vertexCount;

            public VertexIterator() {
                vertexCount = getVertexCount();
            }

            @Override
            public boolean hasNext() {
                return index < vertexCount;
            }

            @Override
            public V next() {
                return getVertex(index++);
            }

        }
        return new AbstractCollection<V>() {
            @Override
            public Iterator<V> iterator() {
                return new VertexIterator();
            }

            @Override
            public int size() {
                return getVertexCount();
            }

        };
    }

    /**
     * Returns true if b is next of a.
     *
     * @param a a vertex
     * @param b another vertex
     * @return true if b is next of a.
     */
    default boolean isNext(@Nonnull V a, @Nonnull V b) {
        return findIndexOfNext(a, b) != -1;
    }

}
