/*
 * @(#)BidiGraphBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.SpliteratorIterable;

import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Provides an API for building a {@code BidiGraph}.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 */
public class BidiGraphBuilder<V, A> implements BidiGraph<V, A> {

    private int arrowCount;
    @Nonnull
    private final Map<V, VertexData<V, A>> vertices;

    /**
     * Creates a new instance with default capacity for vertices and arrows.
     */
    public BidiGraphBuilder() {
        this(10, 10);
    }

    /**
     * Creates a new instance with the specified capacities.
     *
     * @param vertexCapacity the vertex capacity
     * @param arrowCapacity  the arrow capaicty
     */
    public BidiGraphBuilder(int vertexCapacity, int arrowCapacity) {
        arrowCount = 0;
        vertices = new LinkedHashMap<>(vertexCapacity);
    }

    /**
     * Creates a new instance which is a clone of the specified graph.
     *
     * @param that another graph
     */
    public BidiGraphBuilder(@Nonnull DirectedGraph<V, A> that) {
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
    public <VV, AA> BidiGraphBuilder(DirectedGraph<VV, AA> that, @Nonnull Function<VV, V> vertexMapper, @Nonnull Function<AA, A> arrowMapper) {
        arrowCount = that.getArrowCount();
        vertices = new LinkedHashMap<>(that.getVertexCount());

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
    public void addArrow(@Nonnull V start, @Nonnull V end, @Nullable A arrow) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start=" + start + ", end=" + end + ", arrow=" + arrow);
        }
        final VertexData<V, A> startData = vertices.get(start);
        final VertexData<V, A> endData = vertices.get(end);
        if (startData == null || endData == null) {
            throw new IllegalArgumentException(
                    "start=" + start + ", end=" + end + ", arrow=" + arrow + ", fromVertex=" + startData + ", toVertex=" + endData);
        }
        ArrowData<V, A> a = new ArrowData<>(startData, endData, arrow);
        startData.next.add(a);
        endData.prev.add(a);
        arrowCount++;
    }

    /**
     * Adds an arrow from 'va' to 'vb' and an arrow from 'vb' to 'va'.
     *
     * @param va    vertex a
     * @param vb    vertex b
     * @param arrow the arrow
     */
    @SuppressWarnings("unused")
    public void addBidiArrow(V va, V vb, A arrow) {
        addArrow(va, vb, arrow);
        addArrow(vb, va, arrow);
    }

    /**
     * Adds the specified vertex to the graph.
     *
     * @param v the vertex
     */
    public void addVertex(V v) {
        if (vertices.containsKey(v)) {
            return;
        }
        final VertexData<V, A> data = new VertexData<>(v);
        vertices.put(v, data);
    }

    public void clear() {
        vertices.clear();
    }

    @Override
    public int getArrowCount() {
        return arrowCount;
    }

    @Nonnull
    @Override
    public V getNext(@Nonnull V vertex, int i) {
        return getVertexDataNonnull(vertex).next.get(i).end.v;
    }

    @Nonnull
    @Override
    public A getNextArrow(@Nonnull V vertex, int index) {
        return getVertexDataNonnull(vertex).next.get(index).arrow;
    }

    @Override
    public int getNextCount(@Nonnull V vertex) {
        return getVertexDataNonnull(vertex).next.size();
    }

    @Nonnull
    @Override
    public V getPrev(@Nonnull V vertex, int i) {
        return getVertexDataNonnull(vertex).prev.get(i).start.v;
    }

    @Nonnull
    @Override
    public A getPrevArrow(@Nonnull V vertex, int index) {
        return getVertexDataNonnull(vertex).prev.get(index).arrow;
    }

    @Override
    public int getPrevCount(@Nonnull V vertex) {
        return getVertexDataNonnull(vertex).prev.size();
    }

    @Override
    public int getVertexCount() {
        return vertices.size();
    }

    @Nonnull
    @Override
    public Collection<V> getVertices() {
        return Collections.unmodifiableCollection(vertices.keySet());
    }

    @Nonnull
    @Override
    public Collection<A> getArrows() {
        class ArrowIterator implements Iterator<A> {
            private final Iterator<V> vertexIterator;
            private Iterator<A> nextArrowIterator;

            public ArrowIterator() {
                arrowCount = getArrowCount();
                vertexIterator = getVertices().iterator();
                nextArrowIterator = Collections.emptyIterator();
            }

            @Override
            public boolean hasNext() {
                return nextArrowIterator.hasNext() || vertexIterator.hasNext();
            }

            @Override
            @Nullable
            public A next() {
                while (!nextArrowIterator.hasNext()) {
                    V v = vertexIterator.next();
                    nextArrowIterator = getNextArrows(v).iterator();
                }
                return nextArrowIterator.next();
            }

        }
        return new AbstractCollection<A>() {
            @Nonnull
            @Override
            public Iterator<A> iterator() {
                return new ArrowIterator();
            }

            @Override
            public int size() {
                return getArrowCount();
            }

        };
    }

    private VertexData<V, A> getVertexDataNonnull(V vertex) {
        VertexData<V, A> vertexData = vertices.get(vertex);
        if (vertexData == null) {
            throw new NullPointerException("vertex is not in graph. vertex=" + vertex);
        }
        return vertexData;
    }

    /**
     * Removes the specified "next" arrow.
     *
     * @param v a vertex
     * @param a an arrow starting at the vertex, must not be null
     */
    @SuppressWarnings("unused")
    public void removeArrow(V v, @Nonnull A a) {
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
    public void removeNext(V start, int i) {
        final VertexData<V, A> startData = vertices.get(start);
        removeNext(startData, i);
    }

    private void removeNext(VertexData<V, A> startData, int i) {
        ArrowData<V, A> a = startData.next.get(i);
        final VertexData<V, A> endData = a.end;
        startData.next.remove(i);
        endData.prev.remove(a);
        arrowCount--;
    }

    /**
     * Removes the specified arrow from the graph.
     *
     * @param start the start vertex of the arrow
     * @param end   the end vertex of the arrow
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
        final VertexData<V, A> data = vertices.get(v);
        if (data == null) {
            return;
        }
        for (int i = data.next.size() - 1; i >= 0; i--) {
            removeNext(v, i);
        }
        for (int i = data.prev.size() - 1; i >= 0; i--) {
            ArrowData<V, A> arrowData = data.prev.get(i);
            removeNext(arrowData.start, (arrowData.start).next.indexOf(arrowData));
        }
        vertices.remove(v);
    }

    private static class ArrowData<V, A> {

        @Nonnull
        final VertexData<V, A> start;
        @Nonnull
        final VertexData<V, A> end;
        @Nullable
        final A arrow;

        ArrowData(@Nonnull final VertexData<V, A> start, @Nonnull final VertexData<V, A> end, @Nullable final A arrow) {
            this.start = start;
            this.end = end;
            this.arrow = arrow;
        }

        @Nonnull
        public VertexData<V, A> getStart() {
            return start;
        }

        @Nonnull
        public VertexData<V, A> getEnd() {
            return end;
        }
    }


    private static class VertexData<V, A> {

        final V v;
        final List<ArrowData<V, A>> next = new ArrayList<>();
        final List<ArrowData<V, A>> prev = new ArrayList<>();

        VertexData(final V v) {
            this.v = v;
        }

        public List<ArrowData<V, A>> getNext() {
            return next;
        }

        public List<ArrowData<V, A>> getPrev() {
            return prev;
        }
    }

    @Nonnull
    @Override
    public Iterable<V> breadthFirstSearchBackward(final V start, final Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new BidiBreadthFirstSpliterator<>(VertexData::getPrev, ArrowData::getStart, getVertexDataNonnull(start), visited));
    }

    @Nonnull
    @Override
    public Iterable<V> breadthFirstSearch(final V start, final Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new BidiBreadthFirstSpliterator<>(VertexData::getNext, ArrowData::getEnd, getVertexDataNonnull(start), visited));
    }

    @Nonnull
    @Override
    public Iterable<V> depthFirstSearchBackward(final V start, final Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new BidiDepthFirstSpliterator<>(VertexData::getPrev, ArrowData::getStart, getVertexDataNonnull(start), visited));
    }

    @Nonnull
    @Override
    public Iterable<V> depthFirstSearch(final V start, final Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new BidiDepthFirstSpliterator<>(VertexData::getNext, ArrowData::getEnd, getVertexDataNonnull(start), visited));
    }

    /**
     * This is a performance-optimized implementation which does not need to call a hash function for
     * every vertex.
     */
    private static abstract class BidiSpliterator<V, A> extends Spliterators.AbstractSpliterator<V> {

        @Nonnull
        protected final Function<VertexData<V, A>, Iterable<ArrowData<V, A>>> nextNodesFunction;
        @Nonnull
        protected final Function<ArrowData<V, A>, VertexData<V, A>> arrowEndFunction;
        @Nonnull
        protected final Deque<VertexData<V, A>> deque;
        @Nonnull
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
        public BidiSpliterator(@Nonnull final Function<VertexData<V, A>, Iterable<ArrowData<V, A>>> nextNodesFunction,
                               @Nonnull final Function<ArrowData<V, A>, VertexData<V, A>> arrowEndFunction,
                               @Nonnull final VertexData<V, A> root, @Nonnull final Predicate<V> visited) {
            super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
            Objects.requireNonNull(nextNodesFunction, "nextNodesFunction");
            Objects.requireNonNull(root, "root");
            Objects.requireNonNull(visited, "vistied");
            this.nextNodesFunction = nextNodesFunction;
            this.arrowEndFunction = arrowEndFunction;
            deque = new ArrayDeque<>(16);
            this.visited = visited;
            deque.add(root);
            visited.test(root.v);
        }

    }

    private static class BidiBreadthFirstSpliterator<V, A> extends BidiSpliterator<V, A> {


        /**
         * Creates a new instance.
         *
         * @param nextNodesFunction the nextNodesFunction
         * @param root              the root vertex
         * @param visited           a predicate with side effect. The predicate returns true
         *                          if the specified vertex has been visited, and marks the specified vertex
         *                          as visited.
         */
        public BidiBreadthFirstSpliterator(@Nonnull final Function<VertexData<V, A>, Iterable<ArrowData<V, A>>> nextNodesFunction,
                                           @Nonnull final Function<ArrowData<V, A>, VertexData<V, A>> arrowEndFunction,
                                           @Nonnull final VertexData<V, A> root, @Nonnull final Predicate<V> visited) {
            super(nextNodesFunction, arrowEndFunction, root, visited);
        }


        @Override
        public boolean tryAdvance(@Nonnull final Consumer<? super V> action) {
            final VertexData<V, A> current = deque.pollFirst();
            if (current == null) {
                return false;
            }
            for (final ArrowData<V, A> next : nextNodesFunction.apply(current)) {
                final VertexData<V, A> endData = arrowEndFunction.apply(next);
                if (visited.test(endData.v)) {
                    deque.addLast(endData);
                }
            }
            action.accept(current.v);
            return true;
        }
    }

    private static class BidiDepthFirstSpliterator<V, A> extends BidiSpliterator<V, A> {


        /**
         * Creates a new instance.
         *
         * @param nextNodesFunction the nextNodesFunction
         * @param root              the root vertex
         * @param visited           a predicate with side effect. The predicate returns true
         *                          if the specified vertex has been visited, and marks the specified vertex
         *                          as visited.
         */
        public BidiDepthFirstSpliterator(@Nonnull final Function<VertexData<V, A>, Iterable<ArrowData<V, A>>> nextNodesFunction,
                                         @Nonnull final Function<ArrowData<V, A>, VertexData<V, A>> arrowEndFunction,
                                         @Nonnull final VertexData<V, A> root, @Nonnull final Predicate<V> visited) {
            super(nextNodesFunction, arrowEndFunction, root, visited);
        }


        @Override
        public boolean tryAdvance(@Nonnull final Consumer<? super V> action) {
            final VertexData<V, A> current = deque.pollLast();
            if (current == null) {
                return false;
            }
            for (final ArrowData<V, A> next : nextNodesFunction.apply(current)) {
                final VertexData<V, A> endData = arrowEndFunction.apply(next);
                if (visited.test(endData.v)) {
                    deque.addLast(endData);
                }
            }
            action.accept(current.v);
            return true;
        }
    }
}
