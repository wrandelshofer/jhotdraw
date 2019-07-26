/*
 * @(#)BidiGraph.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.SpliteratorIterable;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Adds convenience methods to the API defined in {@link BareBidiGraph}.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 */
public interface BidiGraph<V, A> extends DirectedGraph<V, A>, BareBidiGraph<V, A> {

    /**
     * Returns an {@link Iterable} which performs a backwards breadth first
     * search starting at the given vertex.
     * <p>
     * The default implementation provided by this interface is not optimized for performance.
     *
     * @param start   the start vertex
     * @param visited a predicate with side effect. The predicate returns true
     *                if the specified vertex has been visited, and marks the specified vertex
     *                as visited.
     * @return breadth first search
     */
    @Nonnull
    default Iterable<V> breadthFirstSearchBackward(V start, Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new BreadthFirstSpliterator<>(this::getPrevVertices, start, visited));
    }

    /**
     * Returns an {@link Iterable} which performs a backwards breadth first
     * search starting at the given vertex.
     * <p>
     * The default implementation provided by this interface is not optimized for performance.
     *
     * @param start the start vertex
     * @return breadth first search
     */
    @Nonnull
    default Iterable<V> breadthFirstSearchBackward(V start) {
        return new SpliteratorIterable<>(() -> new BreadthFirstSpliterator<>(this::getPrevVertices, start));
    }

    /**
     * Returns an {@link Iterable} which performs a backwards breadth first
     * search starting at the given vertex.
     * <p>
     * The default implementation provided by this interface is not optimized for performance.
     *
     * @param start   the start vertex
     * @param visited a predicate with side effect. The predicate returns true
     *                if the specified vertex has been visited, and marks the specified vertex
     *                as visited.
     * @return breadth first search
     */
    @Nonnull
    default Iterable<V> depthFirstSearchBackward(V start, Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new DepthFirstSpliterator<>(this::getPrevVertices, start, visited));
    }

    /**
     * Returns an {@link Iterable} which performs a backwards breadth first
     * search starting at the given vertex.
     * <p>
     * The default implementation provided by this interface is not optimized for performance.
     *
     * @param start the start vertex
     * @return breadth first search
     */
    @Nonnull
    default Iterable<V> depthFirstSearchBackward(V start) {
        return new SpliteratorIterable<>(() -> new DepthFirstSpliterator<>(this::getPrevVertices, start));
    }

    /**
     * Returns the direct predecessor arrows of the specified vertex.
     *
     * @param vertex a vertex
     * @return a collection view on the direct predecessor arrows of vertex
     */
    @Nonnull
    default Collection<A> getPrevArrows(V vertex) {
        class PrevArrowIterator implements Iterator<A> {

            private int index;
            private final V vertex;
            private final int prevCount;

            public PrevArrowIterator(V vertex) {
                this.vertex = vertex;
                this.prevCount = getPrevCount(vertex);
            }

            @Override
            public boolean hasNext() {
                return index < prevCount;
            }

            @Override
            public A next() {
                return getPrevArrow(vertex, index++);
            }
        }

        return new AbstractCollection<A>() {
            @Nonnull
            @Override
            public Iterator<A> iterator() {
                return new PrevArrowIterator(vertex);
            }

            @Override
            public int size() {
                return getPrevCount(vertex);
            }
        };
    }

    /**
     * Returns the direct predecessor vertices of the specified vertex.
     *
     * @param vertex a vertex
     * @return a collection view on the direct predecessor vertices of vertex
     */
    @Nonnull
    default Collection<V> getPrevVertices(V vertex) {
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
        return new AbstractCollection<V>() {
            @Nonnull
            @Override
            public Iterator<V> iterator() {
                return new PrevVertexIterator(vertex);
            }

            @Override
            public int size() {
                return getPrevCount(vertex);
            }
        };
    }
}
