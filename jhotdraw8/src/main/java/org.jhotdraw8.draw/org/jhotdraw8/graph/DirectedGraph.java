/*
 * @(#)DirectedGraph.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Adds convenience methods to the API defined in {@link BareDirectedGraph}.
 *
 * @param <V> the vertex type
 * @param <A> the arrow data type
 * @author Werner Randelshofer
 */
public interface DirectedGraph<V, A> extends BareDirectedGraph<V, A> {

    /**
     * Returns the arrow if b is successor of a.
     *
     * @param a a vertex
     * @param b a vertex
     * @return the arrow or null if b is not next of a
     */
    @Nullable
    default A findArrow(@NonNull V a, @NonNull V b) {
        int index = findIndexOfNext(a, b);
        return index == -1 ? null : getNextArrow(a, index);
    }

    /**
     * Returns the index of vertex b.
     *
     * @param a a vertex
     * @param b another vertex
     * @return index of vertex b. Returns -1 if b is not next index of a.
     */
    default int findIndexOfNext(@NonNull V a, @NonNull V b) {
        for (int i = 0, n = getNextCount(a); i < n; i++) {
            if (b.equals(getNext(a, i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the direct successor vertices of the specified vertex.
     *
     * @param vertex a vertex
     * @return a collection view on the direct successor vertices of vertex
     */
    @NonNull
    default Collection<V> getNextVertices(@NonNull V vertex) {
        class NextVertexIterator implements Iterator<V> {

            private int index;
            @NonNull
            private final V vertex;
            private final int nextCount;

            public NextVertexIterator(@NonNull V vertex) {
                this.vertex = vertex;
                this.nextCount = getNextCount(vertex);
            }

            @Override
            public boolean hasNext() {
                return index < nextCount;
            }

            @NonNull
            @Override
            public V next() {
                return getNext(vertex, index++);
            }

        }
        return new AbstractCollection<V>() {
            @NonNull
            @Override
            public Iterator<V> iterator() {
                return new NextVertexIterator(vertex);
            }

            @Override
            public int size() {
                return getNextCount(vertex);
            }
        };
    }

    @NonNull
    default Arc<V, A> getNextArc(@NonNull V v, int index) {
        return new Arc<>(v, getNext(v, index), getNextArrow(v, index));
    }

    /**
     * Returns the direct successor arrow datas of the specified vertex.
     *
     * @param vertex a vertex
     * @return a collection view on the direct successor arrows of vertex
     */
    @NonNull
    default Collection<A> getNextArrows(@NonNull V vertex) {
        class NextArrowIterator implements Iterator<A> {

            private int index;
            @NonNull
            private final V vertex;
            private final int nextCount;

            public NextArrowIterator(@NonNull V vertex) {
                this.vertex = vertex;
                this.nextCount = getNextCount(vertex);
            }

            @Override
            public boolean hasNext() {
                return index < nextCount;
            }

            @NonNull
            @Override
            public A next() {
                return getNextArrow(vertex, index++);
            }
        }

        return new AbstractCollection<A>() {
            @NonNull
            @Override
            public Iterator<A> iterator() {
                return new NextArrowIterator(vertex);
            }

            @Override
            public int size() {
                return getNextCount(vertex);
            }
        };
    }

    /**
     * Returns the direct successor arcs of the specified vertex.
     *
     * @param vertex a vertex
     * @return a collection view on the direct successor arcs of vertex
     */
    @NonNull
    default Collection<Arc<V, A>> getNextArcs(@NonNull V vertex) {
        class NextArcIterator implements Iterator<Arc<V, A>> {

            private int index;
            @NonNull
            private final V vertex;
            private final int nextCount;

            public NextArcIterator(@NonNull V vertex) {
                this.vertex = vertex;
                this.nextCount = getNextCount(vertex);
            }

            @Override
            public boolean hasNext() {
                return index < nextCount;
            }

            @NonNull
            @Override
            public Arc<V, A> next() {
                return getNextArc(vertex, index++);
            }
        }

        return new AbstractCollection<Arc<V, A>>() {
            @NonNull
            @Override
            public Iterator<Arc<V, A>> iterator() {
                return new NextArcIterator(vertex);
            }

            @Override
            public int size() {
                return getNextCount(vertex);
            }
        };
    }
    /**
     * Returns the number of vertices {@code V}.
     *
     * @return vertex count
     */
    default int getVertexCount() {
        return getVertices().size();
    }

    /**
     * Returns the number of arrows.
     *
     * @return arrow count
     */
    default int getArrowCount() {
        return getArrows().size();
    }

    /**
     * Returns all arrow datas between two vertices.
     *
     * @param v1 vertex 1
     * @param v2 vertex 2
     * @return a collection view on all arrows
     */
    @NonNull
    default Collection<A> getArrows(@NonNull V v1, V v2) {
        List<A> arrows = new ArrayList<>();
        for (int i = 0, n = getNextCount(v1); i < n; i++) {
            if (getNext(v1, i).equals(v2)) {
                arrows.add(getNextArrow(v1, i));
            }
        }
        return Collections.unmodifiableList(arrows);
    }

    /**
     * Returns true if b is next of a.
     *
     * @param a a vertex
     * @param b another vertex
     * @return true if b is next of a.
     */
    default boolean isNext(@NonNull V a, @NonNull V b) {
        return findIndexOfNext(a, b) != -1;
    }


}
