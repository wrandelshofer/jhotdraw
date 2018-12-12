/* @(#)BidiGraph.java
 *  Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.collection.SpliteratorIterable;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;

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
 * @version $Id$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public interface BidiGraph<V, A> extends DirectedGraph<V, A> {

    /**
     * Returns an {@link Iterable} which performs a backwards breadth first
     * search starting at the given vertex.
     * <p>
     * The default implementation provided by this interface is not optimized for performance.
     *
     * @param start the start vertex
     * @param visited a predicate with side effect. The predicate returns true
     * if the specified vertex has been visited, and marks the specified vertex
     * as visited.
     * @return breadth first search
     */
    @Nonnull
    default Iterable<V> breadthFirstSearchBackward(V start, Predicate<V> visited) {
        return new SpliteratorIterable<>(()->new BreadthFirstSpliterator<>(this::getPrevVertices, start, visited));
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
        return new SpliteratorIterable<>(()->new BreadthFirstSpliterator<>(this::getPrevVertices, start));
    }
    /**
     * Returns an {@link Iterable} which performs a backwards breadth first
     * search starting at the given vertex.
     * <p>
     * The default implementation provided by this interface is not optimized for performance.
     *
     * @param start the start vertex
     * @param visited a predicate with side effect. The predicate returns true
     * if the specified vertex has been visited, and marks the specified vertex
     * as visited.
     * @return breadth first search
     */
    @Nonnull
    default Iterable<V> depthFirstSearchBackward(V start, Predicate<V> visited) {
        return new SpliteratorIterable<>(()->new DepthFirstSpliterator<>(this::getPrevVertices, start, visited));
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
        return new SpliteratorIterable<>(()->new DepthFirstSpliterator<>(this::getPrevVertices, start));
    }
    /**
     * Returns the i-th direct predecessor vertex of v.
     *
     * @param vertex a vertex
     * @param i index of next vertex
     * @return the i-th next vertex of v
     */
    V getPrev(V vertex, int i);

    /**
     * Returns the specified predecessor (prev) arrow of the specified vertex.
     *
     * @param vertex a vertex
     * @param index index of prev arrow
     * @return the specified arrow
     */
    @Nullable
    A getPrevArrow(V vertex, int index);

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
