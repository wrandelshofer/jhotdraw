/*
 * @(#)ExposedBidiGraphBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.SpliteratorIterable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 */
public class ExposedBidiGraphBuilder<V extends ExposedBidiGraphBuilder.Vertex<V, A>, A extends ExposedBidiGraphBuilder.Arrow<V, A>> implements BidiGraph<V, A> {

    @NonNull
    private final Set<A> arrows;
    @NonNull
    private final Set<V> vertices;

    /**
     * Creates a new instance with default capacity for vertices and arrows.
     */
    public ExposedBidiGraphBuilder() {
        this(10, 10);
    }

    /**
     * Creates a new instance with the specified capacities.
     *
     * @param vertexCapacity the vertex capacity
     * @param arrowCapacity  the arrow capaicty
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
    public ExposedBidiGraphBuilder(@NonNull DirectedGraph<V, A> that) {
        this(that, Function.identity(), Function.identity());
    }

    /**
     * Creates a new instance which is a clone of the specified graph using the
     * provided mapping functions.
     *
     * @param <VV>         the vertex type of that
     * @param <AA>         the arrow type of that
     * @param that         another graph
     * @param vertexMapper a mapping function from that vertex type to the this
     *                     vertex type
     * @param arrowMapper  a mapping function from that arrow type to the this
     *                     arrow type
     */
    public <VV, AA> ExposedBidiGraphBuilder(@NonNull DirectedGraph<VV, AA> that, @NonNull Function<VV, V> vertexMapper, @NonNull Function<AA, A> arrowMapper) {
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
     * @param end   the vertex
     * @param arrow the arrow, can be null
     */
    public void addArrow(@NonNull V start, @NonNull V end, @Nullable A arrow) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start=" + start + ", end=" + end + ", arrow=" + arrow);
        }
        if (arrow.getStart() != start || arrow.getEnd() != end) {
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

    @NonNull
    @Override
    public V getNext(@NonNull V vertex, int i) {
        return getVertexDataNonNull(vertex).next.get(i).end;
    }

    @NonNull
    @Override
    public A getNextArrow(@NonNull V vertex, int index) {
        return getVertexDataNonNull(vertex).next.get(index);
    }

    @Override
    public int getNextCount(@NonNull V vertex) {
        return getVertexDataNonNull(vertex).next.size();
    }

    @NonNull
    @Override
    public V getPrev(@NonNull V vertex, int i) {
        return getVertexDataNonNull(vertex).prev.get(i).start;
    }

    @NonNull
    @Override
    public A getPrevArrow(@NonNull V vertex, int index) {
        return getVertexDataNonNull(vertex).prev.get(index);
    }

    @Override
    public int getPrevCount(@NonNull V vertex) {
        return getVertexDataNonNull(vertex).prev.size();
    }

    @Override
    public int getVertexCount() {
        return vertices.size();
    }

    @NonNull
    @Override
    public Collection<V> getVertices() {
        return Collections.unmodifiableCollection(vertices);
    }

    @Override
    public @NonNull Collection<A> getArrows() {
        return Collections.unmodifiableCollection(arrows);
    }

    private V getVertexDataNonNull(V vertex) {
        return vertex;
    }

    /**
     * Removes the specified "next" arrow.
     *
     * @param v a vertex
     * @param a an arrow starting at the vertex, must not be null
     */
    @SuppressWarnings("unused")
    public void removeArrow(@NonNull V v, @NonNull A a) {
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
     * @param i     the index of the i-th next vertex
     */
    @SuppressWarnings("WeakerAccess")
    public void removeNext(@NonNull V start, int i) {
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
     * @param end   the end vertex of the arrow
     */
    @SuppressWarnings("unused")
    public void removeNext(@NonNull V start, V end) {
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
    public void removeVertex(@Nullable V v) {
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
    public static class Arrow<V extends Vertex<V, A>, A extends Arrow<V, A>> {

        @NonNull
        final V start;
        @NonNull
        final V end;

        public Arrow(@NonNull final V start, @NonNull final V end) {
            this.start = start;
            this.end = end;
        }

        @NonNull
        public V getStart() {
            return start;
        }

        @NonNull
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
    public static class Vertex<V extends Vertex<V, A>, A extends Arrow<V, A>> {

        @NonNull
        final List<A> next = new ArrayList<>();
        @NonNull
        final List<A> prev = new ArrayList<>();

        public Vertex() {
        }

        @NonNull List<A> getNext() {
            return next;
        }

        @NonNull List<A> getPrev() {
            return prev;
        }
    }

    @NonNull
    @Override
    public Iterable<V> breadthFirstSearchBackward(final V start, @NonNull final Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new BidiBreadthFirstSpliterator(Vertex::getPrev, Arrow::getStart, getVertexDataNonNull(start), visited));
    }

    @NonNull
    @Override
    public Iterable<V> breadthFirstSearch(final V start, @NonNull final Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new BidiBreadthFirstSpliterator(Vertex::getNext, Arrow::getEnd, getVertexDataNonNull(start), visited));
    }

    @NonNull
    @Override
    public Iterable<V> depthFirstSearchBackward(final V start, @NonNull final Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new BidiDepthFirstSpliterator(Vertex::getPrev, Arrow::getStart, getVertexDataNonNull(start), visited));
    }

    @NonNull
    @Override
    public Iterable<V> depthFirstSearch(final V start, @NonNull final Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new BidiDepthFirstSpliterator(Vertex::getNext, Arrow::getEnd, getVertexDataNonNull(start), visited));
    }

    /**
     * This is a performance-optimized implementation which does not need to call a hash function for
     * every vertex.
     */
    private abstract class AbstractBidiSpliterator extends Spliterators.AbstractSpliterator<V> {

        @NonNull
        protected final Function<V, Iterable<A>> nextNodesFunction;
        @NonNull
        protected final Function<A, V> arrowEndFunction;
        @NonNull
        protected final Deque<V> deque;
        @NonNull
        protected final Predicate<V> visited;

        /**
         * Creates a new instance.
         *
         * @param nextNodesFunction the nextNodesFunction
         * @param root              the root vertex
         * @param visited           a predicate with side effect. The predicate returns true
         *                          if the specified vertex has been visited, and marks the specified vertex
         *                          as visited.
         */
        public AbstractBidiSpliterator(@NonNull final Function<V, Iterable<A>> nextNodesFunction,
                                       @NonNull final Function<A, V> arrowEndFunction,
                                       @NonNull final V root, @NonNull final Predicate<V> visited) {
            super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
            Objects.requireNonNull(nextNodesFunction, "nextNodesFunction");
            Objects.requireNonNull(root, "root");
            Objects.requireNonNull(visited, "vistied");
            this.nextNodesFunction = nextNodesFunction;
            this.arrowEndFunction = arrowEndFunction;
            deque = new ArrayDeque<>(16);
            this.visited = visited;
            deque.add(root);
            visited.test(root);
        }


    }

    /**
     * This is a performance-optimized implementation which does not need to call a hash function for
     * every vertex.
     */
    private class BidiBreadthFirstSpliterator extends AbstractBidiSpliterator {

        /**
         * Creates a new instance.
         *
         * @param nextNodesFunction the nextNodesFunction
         * @param root              the root vertex
         * @param visited           a predicate with side effect. The predicate returns true
         *                          if the specified vertex has been visited, and marks the specified vertex
         *                          as visited.
         */
        public BidiBreadthFirstSpliterator(@NonNull final Function<V, Iterable<A>> nextNodesFunction,
                                           @NonNull final Function<A, V> arrowEndFunction,
                                           @NonNull final V root, @NonNull final Predicate<V> visited) {
            super(nextNodesFunction, arrowEndFunction, root, visited);
        }


        @Override
        public boolean tryAdvance(@NonNull final Consumer<? super V> action) {
            final V current = deque.pollFirst();
            if (current == null) {
                return false;
            }
            for (final A next : nextNodesFunction.apply(current)) {
                final V endData = arrowEndFunction.apply(next);
                if (visited.test(endData)) {
                    deque.addLast(endData);
                }
            }
            action.accept(current);
            return true;
        }
    }

    /**
     * This is a performance-optimized implementation which does not need to call a hash function for
     * every vertex.
     */
    private class BidiDepthFirstSpliterator extends AbstractBidiSpliterator {

        /**
         * Creates a new instance.
         *
         * @param nextNodesFunction the nextNodesFunction
         * @param root              the root vertex
         * @param visited           a predicate with side effect. The predicate returns true
         *                          if the specified vertex has been visited, and marks the specified vertex
         *                          as visited.
         */
        public BidiDepthFirstSpliterator(@NonNull final Function<V, Iterable<A>> nextNodesFunction,
                                         @NonNull final Function<A, V> arrowEndFunction,
                                         @NonNull final V root, @NonNull final Predicate<V> visited) {
            super(nextNodesFunction, arrowEndFunction, root, visited);
        }


        @Override
        public boolean tryAdvance(@NonNull final Consumer<? super V> action) {
            final V current = deque.pollLast();
            if (current == null) {
                return false;
            }
            for (final A next : nextNodesFunction.apply(current)) {
                final V endData = arrowEndFunction.apply(next);
                if (visited.test(endData)) {
                    deque.addLast(endData);
                }
            }
            action.accept(current);
            return true;
        }
    }
}
