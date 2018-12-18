/* @(#)DirectedGraph.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.collection.SpliteratorIterable;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;

/**
 * This interface provides read access to a directed graph {@code G = (V, A) }.
 * <ul>
 * <li>{@code G} is a tuple {@code (V, A) }.</li>
 * <li>{@code V} is the set of vertices with elements
 * {@code v_i ∈ V. i ∈ {0, ..., vertexCount - 1} }.</li>
 * <li>{@code A} is the set of ordered pairs with elements
 * {@code  (v_i, v_j)_k ∈ A. i,j ∈ {0, ..., vertexCount - 1}. k ∈ {0, ..., arrowCount - 1}
 * }.</li>
 * </ul>
 * <p>
 * This interface provides access to the following data:
 * <ul>
 * <li>The vertex count {@code vertexCount}.</li>
 * <li>The arrow count {@code arrowCount}.</li>
 * <li>The vertex {@code v_i ∈ V} .</li>
 * <li>The arrow {@code a_k ∈ A}.</li>
 * <li>The next count {@code nextCount_i} of the vertex {@code v_i}.</li>
 * <li>The {@code k}-th next vertex of the vertex {@code v_i}, with
 * {@code k ∈ {0, ..., getNextCount(i) - 1}}.</li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public interface DirectedGraph<V, A> {

    /**
     * Returns the arrow if b is successor of a.
     *
     * @param a a vertex
     * @param b a vertex
     * @return the arrow or null if b is not next of a
     */
    @Nullable
    default A findArrow(V a, @Nonnull V b) {
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
    default int findIndexOfNext(V a, @Nonnull V b) {
        for (int i = 0, n = getNextCount(a); i < n; i++) {
            if (b.equals(getNext(a, i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the specified successor (next) arrow of the specified vertex.
     *
     * @param vertex a vertex
     * @param index index of next arrow
     * @return the specified arrow
     */
    @Nullable
    A getNextArrow(V vertex, int index);

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
     * @param index index of next vertex
     * @return the i-th next vertex
     */
    V getNext(V vertex, int index);

    /**
     * Returns the number of direct successor vertices of v.
     *
     * @param vertex a vertex
     * @return the number of next vertices of v.
     */
    int getNextCount(V vertex);

    /**
     * Returns the direct successor vertices of the specified vertex.
     *
     * @param vertex a vertex
     * @return a collection view on the direct successor vertices of vertex
     */
    @Nonnull
    default Collection<V> getNextVertices(V vertex) {
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
        return new AbstractCollection<V>() {
            @Nonnull
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

    /**
     * Returns the direct successor vertices of the specified vertex.
     *
     * @param vertex a vertex
     * @return a collection view on the direct successor vertices of vertex
     *  with the arrow pointing to the vertex
     */
    @Nonnull
    default Collection<Map.Entry<V,A>> getNextEntries(V vertex) {
        class NextVertexAndArrowIterator implements Iterator<Map.Entry<V,A>> {

            private int index;
            private final V vertex;
            private final int nextCount;

            public NextVertexAndArrowIterator(V vertex) {
                this.vertex = vertex;
                this.nextCount = getNextCount(vertex);
            }

            @Override
            public boolean hasNext() {
                return index < nextCount;
            }

            @Override
            public Map.Entry<V,A> next() {
                int i=index++;
                return new AbstractMap.SimpleEntry<>(
                        getNext(vertex, i),
                        getNextArrow(vertex,i)
                );
            }

        }
        return new AbstractCollection<Map.Entry<V,A>>() {
            @Nonnull
            @Override
            public Iterator<Map.Entry<V,A>> iterator() {
                return new NextVertexAndArrowIterator(vertex);
            }

            @Override
            public int size() {
                return getNextCount(vertex);
            }
        };
    }
    /**
     * Returns the direct successor arrows of the specified vertex.
     *
     * @param vertex a vertex
     * @return a collection view on the direct successor arrows of vertex
     */
    @Nonnull
    default Collection<A> getNextArrows(V vertex) {
        class NextArrowIterator implements Iterator<A> {

            private int index;
            private final V vertex;
            private final int nextCount;

            public NextArrowIterator(V vertex) {
                this.vertex = vertex;
                this.nextCount = getNextCount(vertex);
            }

            @Override
            public boolean hasNext() {
                return index < nextCount;
            }

            @Override
            public A next() {
                return getNextArrow(vertex, index++);
            }
        }

        return new AbstractCollection<A>() {
            @Nonnull
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
     * Returns the number of vertices {@code V}.
     *
     * @return vertex count
     */
    int getVertexCount();

    /**
     * Returns all vertices.
     *
     * @return a collection view on all vertices
     */
    Collection<V> getVertices();

    /**
     * Returns all arrows.
     *
     * @return a collection view on all arrows
     */
    @Nonnull
    Collection<A> getArrows();

    /**
     * Returns all arrows between two vertices.
     *
     * @param v1 vertex 1
     * @param v2 vertex 2
     * @return a collection view on all arrows
     */
    @Nonnull
    default Collection<A> getArrows(V v1, V v2) {
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
    default boolean isNext(V a, @Nonnull V b) {
        return findIndexOfNext(a, b) != -1;
    }

    /**
     * Returns true if b is reachable from a.
     *
     * @param a a vertex
     * @param b another vertex
     * @return true if b is reachable from a.
     */
    default boolean isReachable(V a, V b) {
        return StreamSupport.stream(breadthFirstSearch(a).spliterator(),false).anyMatch(v -> Objects.equals(v, b));
    }

    /**
     * Returns true if b is reachable from a.
     *
     * @param a a vertex
     * @param b another vertex
     * @param visited a predicate with side effect. The predicate returns true
     * if the specified vertex has been visited, and marks the specified vertex
     * as visited.
     * @return true if b is reachable from a.
     */
    default boolean isReachable(V a, V b, Predicate<V> visited) {
        return StreamSupport.stream(breadthFirstSearch(a, visited).spliterator(),false).anyMatch(v -> Objects.equals(v, b));
    }

    /**
     * Returns a {@link Iterable} which performs a breadth first search
     * starting at the given vertex.
     *
     * @param start the start vertex
     * @param visited a predicate with side effect. The predicate returns true
     * if the specified vertex has been visited, and marks the specified vertex
     * as visited.
     * @return breadth first search
     */
    @Nonnull
    default Iterable<V> breadthFirstSearch(V start, Predicate<V> visited) {
        return new SpliteratorIterable<>(()->new BreadthFirstSpliterator<>(this::getNextVertices, start, visited));
    }
    /**
     * Returns a {@link Stream} which performs a depth first search
     * starting at the given vertex.
     *
     * @param start the start vertex
     * @param visited a predicate with side effect. The predicate returns true
     * if the specified vertex has been visited, and marks the specified vertex
     * as visited.
     * @return breadth first search
     */
    @Nonnull
    default Iterable<V> depthFirstSearch(final V start, final Predicate<V> visited) {
        return new SpliteratorIterable<>(()->new DepthFirstSpliterator<>(this::getNextVertices, start, visited));
    }

    /**
     * Returns an {@link Iterable} which performs a breadth first search
     * starting at the given vertex.
     *
     * @param start the start vertex
     * @return breadth first search
     */
    @Nonnull
    default Iterable<V> breadthFirstSearch(V start) {
        Set<V> visited=new HashSet<>();
        return breadthFirstSearch(start, visited::add);
    }
    /**
     * Returns an {@link Iterable} which performs a breadth first search
     * starting at the given vertex.
     *
     * @param start the start vertex
     * @return breadth first search
     */
    @Nonnull
    default Iterable<V> depthFirstSearch(V start) {
        Set<V> visited=new HashSet<>();
        return depthFirstSearch(start, visited::add);
    }
}
