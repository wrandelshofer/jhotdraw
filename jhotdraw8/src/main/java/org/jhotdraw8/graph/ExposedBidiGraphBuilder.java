/* @(#)BidiGraphBuilder.java
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Provides an API for building a {@code BidiGraph}.
 * <p>
 * Performance:
 * <p>
 * This builder exposes its vertex and arrow data objects.
 * This can improve performance, because this allows to save one memory access per vertex and per arrow.
 * Also, there is no need for looking up data objects in a hash map.
 * <p>
 * A Vertex or an Arrow may only be added to one graph at a time.
 * For performance reasons, this class does not check if a Vertex or
 * an Arrow has been accidentally added to multiple graphs.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public class ExposedBidiGraphBuilder<V extends ExposedBidiGraphBuilder.Vertex<V,A>, A extends ExposedBidiGraphBuilder.Arrow<V,A>> implements BidiGraph<V, A> {

    @NotNull
    private final Set<A> arrows;
    @NotNull
    private final Set<V> vertices;

    /**
     * Creates a new instance with default capacity for nextArrows and arrows.
     */
    public ExposedBidiGraphBuilder() {
        this(10, 10);
    }

    /**
     * Creates a new instance with the specified capacities.
     *
     * @param vertexCapacity the vertex capacity
     * @param arrowCapacity the arrow capaicty
     */
    public ExposedBidiGraphBuilder(int vertexCapacity, int arrowCapacity) {
        arrows = new LinkedHashSet<>(arrowCapacity);
        vertices = new LinkedHashSet<>(vertexCapacity);
    }

    /**
     * Creates a new instance which is a clone of the specified graph.
     *
     * @param that another graph
     */
    public ExposedBidiGraphBuilder(@NotNull DirectedGraph<V, A> that) {
        this(that, Function.identity(), Function.identity());
    }

    /**
     * Creates a new instance which is a clone of the specified graph using the
     * provided mapping functions.
     *
     * @param <VV> the vertex type of that
     * @param <AA> the arrow type of that
     * @param that another graph
     * @param vertexMapper a mapping function from that vertex type to the this
     * vertex type
     * @param arrowMapper a mapping function from that arrow type to the this
     * arrow type
     */
    public <VV, AA> ExposedBidiGraphBuilder(DirectedGraph<VV, AA> that, @NotNull Function<VV, V> vertexMapper, @NotNull Function<AA, A> arrowMapper) {
        arrows = new LinkedHashSet<>(that.getArrowCount());
        vertices = new LinkedHashSet<>(that.getVertexCount());

        for (VV vv : that.getVertices()) {
            addVertex(vertexMapper.apply(vv));
        }
        for (VV vv : that.getVertices()) {
            V v = vertexMapper.apply(vv);
            for (int i = 0, n = that.getNextCount(vv); i < n; i++) {
                addArrow(v, vertexMapper.apply(that.getNext(vv, i)), arrowMapper.apply(that.getNextArrow(vv, i)));
            }
        }
    }

    /**
     * Adds the specified arrow from vertex 'from' to vertex 'to'.
     *
     * @param start the vertex
     * @param end the vertex
     * @param arrow the arrow, can be null
     */
    public void addArrow(@NotNull V start, @NotNull V end, @Nullable A arrow) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start=" + start + ", end=" + end + ", arrow=" + arrow);
        }
        if (arrow.getStart()!=start  ||arrow.getEnd()!= end) {
            throw new IllegalArgumentException("start=" + start + ", end=" + end + ", arrow=" + arrow);
        }
        start.next.add(arrow);
        end.prev.add(arrow);
        arrows.add(arrow);
    }


    /**
     * Adds the specified vertex to the graph.
     *
     * @param v the vertex
     */
    public void addVertex(V v) {
        vertices.add(v);
    }

    public void clear() {
        vertices.clear();
        arrows.clear();
    }

    @Override
    public int getArrowCount() {
        return arrows.size();
    }

    @Override
    public V getNext(V vertex, int i) {
        return (V) getVertexDataNotNull(vertex).next.get(i).end;
    }

    @Override
    public A getNextArrow(V vertex, int index) {
        return (A) getVertexDataNotNull(vertex).next.get(index);
    }

    @Override
    public int getNextCount(V vertex) {
        return getVertexDataNotNull(vertex).next.size();
    }

    @Override
    public V getPrev(V vertex, int i) {
        return getVertexDataNotNull(vertex).prev.get(i).start;
    }

    @Override
    public A getPrevArrow(V vertex, int index) {
        return getVertexDataNotNull(vertex).prev.get(index);
    }

    @Override
    public int getPrevCount(V vertex) {
        return getVertexDataNotNull(vertex).prev.size();
    }

    @Override
    public int getVertexCount() {
        return vertices.size();
    }

    @Override
    public Collection<V> getVertices() {
        return Collections.unmodifiableCollection(vertices);
    }

    @Override
    public @Nonnull Collection<A> getArrows() {
        return Collections.unmodifiableCollection(arrows);
    }

    private V getVertexDataNotNull(V vertex) {
        return vertex;
    }

    /**
     * Removes the specified "next" arrow.
     *
     * @param v a vertex
     * @param a an arrow starting at the vertex, must not be null
     */
    @SuppressWarnings("unused")
    public void removeArrow(V v, @NotNull A a) {
        for (int i = 0, n = getNextCount(v); i < n; i++) {
            if (a.equals(getNextArrow(v, i))) {
                removeNext(v, i);
                break;
            }
        }
    }

    /**
     * Removes the specified arrow from the graph.
     *
     * @param start the start vertex of the arrow
     * @param i the index of the i-th next vertex
     */
    @SuppressWarnings("WeakerAccess")
    public void removeNext(V start, int i) {
        A a = start.next.get(i);
        final V endData = a.end;
        start.next.remove(i);
        endData.prev.remove(a);
        arrows.remove(a);
    }

    /**
     * Removes the specified arrow from the graph.
     *
     * @param start the start vertex of the arrow
     * @param end the end vertex of the arrow
     */
    @SuppressWarnings("unused")
    public void removeNext(V start, V end) {
        for (int i = 0, n = getNextCount(start); i < n; i++) {
            if (getNext(start, i).equals(end)) {
                removeNext(start, i);
                return;
            }
        }
    }

    /**
     * Removes the specified vertex from the graph and all arrows that start or
     * end at the vertex.
     *
     * @param v the vertex
     */
    @SuppressWarnings("unused")
    public void removeVertex(V v) {
        if (v == null) {
            return;
        }
        for (int i = v.next.size() - 1; i >= 0; i--) {
            removeNext(v, i);
        }
        for (int i = v.prev.size() - 1; i >= 0; i--) {
            A arrow = v.prev.get(i);
            removeNext(arrow.start, (arrow.start).next.indexOf(arrow));
        }
        vertices.remove(v);
    }

    /**
     * Represents an arrow data object.
     *
     * @param <V> the vertex data object type
     * @param <A> the arrow data object type
     */
    public static class Arrow<V extends Vertex<V,A>, A extends Arrow<V,A>> {

        @NotNull final V start;
        @NotNull final V end;

        public Arrow(@NotNull final V start, @NotNull final V end) {
            this.start = start;
            this.end = end;
        }

        @NotNull
        public V getStart() {
            return start;
        }

        @NotNull
        public V getEnd() {
            return end;
        }
    }

    /**
     * Represents a vertex data object.
     *
     * @param <V> the vertex data object type
     * @param <A> the arrow data object type
     */
    public static class Vertex<V extends Vertex<V,A>, A extends Arrow<V,A>> {

        @NotNull final List<A> next = new ArrayList<>();
        @NotNull final List<A> prev = new ArrayList<>();

        public Vertex() {
        }

        List<A> getNext() {
            return next;
        }

        List<A> getPrev() {
            return prev;
        }
    }

    @NotNull
    @Override
    public Stream<V> breadthFirstSearchBackward(final V start, final Predicate<V> visited) {
        return StreamSupport.stream(new BidiBreadthFirstSpliterator(Vertex::getPrev, Arrow::getStart, getVertexDataNotNull(start), visited), false);
    }

    @NotNull
    @Override
    public Stream<V> breadthFirstSearch(final V start, final Predicate<V> visited) {
        return StreamSupport.stream(new BidiBreadthFirstSpliterator(Vertex::getNext, Arrow::getEnd, getVertexDataNotNull(start), visited), false);
    }

    /**
     * This is a performance-optimized implementation which does not need to call a hash function for
     * every vertex.
     */
    private  class BidiBreadthFirstSpliterator extends Spliterators.AbstractSpliterator<V> {

        @NotNull
        private final Function<V, Iterable<A>> nextNodesFunction;
        @NotNull
        private final Function<A, V> arrowEndFunction;
        @NotNull
        private final Queue<V> queue;
        @NotNull
        private final Predicate<V> visited;

        /**
         * Creates a new instance.
         *
         * @param nextNodesFunction the nextNodesFunction
         * @param root              the root vertex
         * @param visited           a predicate with side effect. The predicate returns true
         *                          if the specified vertex has been visited, and marks the specified vertex
         *                          as visited.
         */
        public BidiBreadthFirstSpliterator(@NotNull final Function<V, Iterable<A>> nextNodesFunction,
                                           @NotNull final Function<A, V> arrowEndFunction,
                                           @NotNull final V root, @NotNull final Predicate<V> visited) {
            super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
            Objects.requireNonNull(nextNodesFunction, "nextNodesFunction");
            Objects.requireNonNull(root, "root");
            Objects.requireNonNull(visited, "vistied");
            this.nextNodesFunction = nextNodesFunction;
            this.arrowEndFunction = arrowEndFunction;
            queue = new ArrayDeque<>(16);
            this.visited = visited;
            queue.add(root);
            visited.test(root);
        }


        @Override
        public boolean tryAdvance(@NotNull final Consumer<? super V> action) {
            final V current = queue.poll();
            if (current == null) {
                return false;
            }
            for (final A next : nextNodesFunction.apply(current)) {
                final V endData = arrowEndFunction.apply(next);
                if (visited.test(endData)) {
                    queue.add(endData);
                }
            }
            action.accept(current);
            return true;
        }
    }
}
