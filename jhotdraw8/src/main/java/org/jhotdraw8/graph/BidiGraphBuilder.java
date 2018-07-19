/* @(#)BidiGraphBuilder.java
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Provides an API for building a {@code BidiGraph}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public class BidiGraphBuilder<V, A> implements BidiGraph<V, A> {

    private int arrowCount;
    @NonNull
    private final Map<V, VertexData<V, A>> vertices;

    /**
     * Creates a new instance with default capacity for nextArrows and arrows.
     */
    public BidiGraphBuilder() {
        this(10, 10);
    }

    /**
     * Creates a new instance with the specified capacities.
     *
     * @param vertexCapacity the vertex capacity
     * @param arrowCapacity the arrow capaicty
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
    public BidiGraphBuilder(@NonNull DirectedGraph<V, A> that) {
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
    public <VV, AA> BidiGraphBuilder(DirectedGraph<VV, AA> that, @NonNull Function<VV, V> vertexMapper, @NonNull Function<AA, A> arrowMapper) {
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
     * @param end the vertex
     * @param arrow the arrow, can be null
     */
    public void addArrow(@NonNull V start, @NonNull V end, @Nullable A arrow) {
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
     * @param va vertex a
     * @param vb vertex b
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

    @Override
    public V getNext(V vertex, int i) {
        return getVertexDataNotNull(vertex).next.get(i).end.v;
    }

    @Override
    public A getNextArrow(V vertex, int index) {
        return getVertexDataNotNull(vertex).next.get(index).arrow;
    }

    @Override
    public int getNextCount(V vertex) {
        return getVertexDataNotNull(vertex).next.size();
    }

    @Override
    public V getPrev(V vertex, int i) {
        return getVertexDataNotNull(vertex).prev.get(i).start.v;
    }

    @Override
    public A getPrevArrow(V vertex, int index) {
        return getVertexDataNotNull(vertex).prev.get(index).arrow;
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
        return Collections.unmodifiableCollection(vertices.keySet());
    }

    @Override
    public Collection<A> getArrows() {
        class ArrowIterator implements Iterator<A> {
            private final Iterator<V> vertexIterator;
            private Iterator<A> nextArrowIterator;

            public ArrowIterator() {
                arrowCount = getArrowCount();
                vertexIterator=getVertices().iterator();
                nextArrowIterator=Collections.emptyIterator();
            }

            @Override
            public boolean hasNext() {
                return nextArrowIterator.hasNext() || vertexIterator.hasNext();
            }

            @Override
            @Nullable
            public A next() {
                while (!nextArrowIterator.hasNext()) {
                  V  v = vertexIterator.next();
                    nextArrowIterator = getNextArrows(v).iterator();
                }
                return nextArrowIterator.next();
            }

        }
        return new AbstractCollection<A>() {
            @NonNull
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

    private VertexData<V, A> getVertexDataNotNull(V vertex) {
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
    public void removeArrow(V v, @NonNull A a) {
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
        final VertexData<V, A> startData = vertices.get(start);
        removeNext(startData,i);
    }

    private void removeNext(VertexData<V,A> startData, int i) {
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

        @NonNull final  VertexData<V, A> start;
        @NonNull final  VertexData<V, A> end;
        @Nullable final A arrow;

        ArrowData(@NonNull final VertexData<V, A> start, @NonNull final VertexData<V, A> end, @Nullable final A arrow) {
            this.start = start;
            this.end = end;
            this.arrow = arrow;
        }

        @NotNull
        public VertexData<V, A> getStart() {
            return start;
        }

        @NotNull
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

    @NonNull
    @Override
    public Stream<V> breadthFirstSearchBackwards(final V start, final Predicate<V> visited) {
        return StreamSupport.stream(new BidiBreadthFirstSpliterator(VertexData::getPrev, ArrowData::getStart, getVertexDataNotNull(start), visited), false);
    }

    @NonNull
    @Override
    public Stream<V> breadthFirstSearch(final V start, final Predicate<V> visited) {
        return StreamSupport.stream(new BidiBreadthFirstSpliterator(VertexData::getNext, ArrowData::getEnd, getVertexDataNotNull(start), visited), false);
    }

    /**
     * This is a performance-optimized implementation which does not need to call a hash function for
     * every vertex.
     */
    private class BidiBreadthFirstSpliterator extends Spliterators.AbstractSpliterator<V> {

        @NonNull
        private final Function<VertexData<V, A>, Iterable<ArrowData<V, A>>> nextNodesFunction;
        @NonNull
        private final Function<ArrowData<V, A>, VertexData<V, A>> arrowEndFunction;
        @NonNull
        private final Queue<VertexData<V, A>> queue;
        @NonNull
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
        public BidiBreadthFirstSpliterator(@NonNull final Function<VertexData<V, A>, Iterable<ArrowData<V, A>>> nextNodesFunction,
                                           @NonNull final Function<ArrowData<V, A>, VertexData<V, A>> arrowEndFunction,
                                           @NonNull final VertexData<V, A> root, @NonNull final Predicate<V> visited) {
            super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
            Objects.requireNonNull(nextNodesFunction, "nextNodesFunction");
            Objects.requireNonNull(root, "root");
            Objects.requireNonNull(visited, "vistied");
            this.nextNodesFunction = nextNodesFunction;
            this.arrowEndFunction = arrowEndFunction;
            queue = new ArrayDeque<>(16);
            this.visited = visited;
            queue.add(root);
            visited.test(root.v);
        }


        @Override
        public boolean tryAdvance(@NonNull final Consumer<? super V> action) {
            final VertexData<V, A> current = queue.poll();
            if (current == null) {
                return false;
            }
            for (final ArrowData<V, A> next : nextNodesFunction.apply(current)) {
                final VertexData<V, A> endData = arrowEndFunction.apply(next);
                if (visited.test(endData.v)) {
                    queue.add(endData);
                }
            }
            action.accept(current.v);
            return true;
        }
    }
}
