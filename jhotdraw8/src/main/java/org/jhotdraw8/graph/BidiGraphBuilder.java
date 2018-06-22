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

/**
 * Provides an API for building a {@code BidiGraph}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public class BidiGraphBuilder<V, A> implements BidiGraph<V, A> {

    @NonNull
    private final List<ArrowData<V, A>> arrows;
    @NonNull
    private final List<V> vertexList;
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
        arrows = new ArrayList<>(arrowCapacity);
        vertices = new LinkedHashMap<>(vertexCapacity);
        vertexList = new ArrayList<>(vertexCapacity);
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
        arrows = new ArrayList<>(that.getArrowCount());
        vertices = new LinkedHashMap<>(that.getVertexCount());
        vertexList = new ArrayList<>(that.getVertexCount());

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
     * @param from the vertex
     * @param to the vertex
     * @param arrow the arrow, can be null
     */
    public void addArrow(@NonNull V from, @NonNull V to, @Nullable A arrow) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from=" + from + ", to=" + to + ", arrow=" + arrow);
        }
        final VertexData<V, A> fromData = vertices.get(from);
        final VertexData<V, A> toData = vertices.get(to);
        if (fromData == null || toData == null) {
            throw new IllegalArgumentException(
                    "from=" + from + ", to=" + to + ", arrow=" + arrow + ", fromVertex=" + fromData + ", toVertex=" + toData);
        }
        ArrowData<V, A> a = new ArrowData<>(fromData, toData, arrow);
        fromData.next.add(a);
        toData.prev.add(a);
        arrows.add(a);
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
        vertices.put(v, new VertexData<>(v));
        vertexList.add(v);
    }

    public void clear() {
        vertices.clear();
        vertexList.clear();
        arrows.clear();
    }

    @Override
    public A getArrow(int index) {
        return arrows.get(index).arrow;
    }

    @Override
    public int getArrowCount() {
        return arrows.size();
    }

    @Override
    public V getNext(V vertex, int i) {
        return getVertexDataNotNull(vertex).next.get(i).to.v;
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
        return getVertexDataNotNull(vertex).prev.get(i).from.v;
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
    public V getVertex(int indexOfVertex) {
        return vertexList.get(indexOfVertex);
    }

    @Override
    public int getVertexCount() {
        return vertexList.size();
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
     * @param from the start vertex of the arrow
     * @param i the index of the i-th next vertex
     */
    @SuppressWarnings("WeakerAccess")
    public void removeNext(V from, int i) {
        final VertexData<V, A> fromData = vertices.get(from);
        removeNext(fromData,i);
    }

    private void removeNext(VertexData<V,A> fromData, int i) {
        ArrowData<V, A> a = fromData.next.get(i);
        final VertexData<V, A> toData = a.to;
        fromData.next.remove(i);
        toData.prev.remove(a);
        arrows.remove(a);
    }

    /**
     * Removes the specified arrow from the graph.
     *
     * @param from the start vertex of the arrow
     * @param to the end vertex of the arrow
     */
    @SuppressWarnings("unused")
    public void removeNext(V from, V to) {
        for (int i = 0, n = getNextCount(from); i < n; i++) {
            if (getNext(from, i).equals(to)) {
                removeNext(from, i);
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
        final VertexData<V, A> vertex = vertices.get(v);
        if (vertex == null) {
            return;
        }
        for (int i = vertex.next.size() - 1; i >= 0; i--) {
            removeNext(v, i);
        }
        for (int i = vertex.prev.size() - 1; i >= 0; i--) {
            ArrowData<V, A> arrowData = vertex.prev.get(i);
            removeNext(arrowData.from, (arrowData.from).next.indexOf(arrowData));
        }
        vertices.remove(v);
        vertexList.remove(v);
    }

    private static class ArrowData<V, A> {

        final  VertexData<V, A> from;
        final  VertexData<V, A> to;
        final A arrow;

        ArrowData( VertexData<V, A> from,  VertexData<V, A> to, A arrow) {
            this.from = from;
            this.to = to;
            this.arrow = arrow;
        }

    }

    private static class VertexData<V, A> extends ArrayList<ArrowData<V,A>>{

        final V v;
        final List<ArrowData<V, A>> next = this;
        final List<ArrowData<V, A>> prev = new ArrayList<>();

        VertexData(V v) {
            this.v = v;
        }

        public List<ArrowData<V, A>> getNext() {
            return next;
        }

        public List<ArrowData<V, A>> getPrev() {
            return prev;
        }
    }

    @Override
    public Stream<V> breadthFirstSearchBackwards(V start, Predicate<V> visited) {
        return StreamSupport.stream(new BidiBreadthFirstSpliterator(VertexData::getPrev, getVertexDataNotNull(start), visited), false);
    }

    @Override
    public Stream<V> breadthFirstSearch(V start, Predicate<V> visited) {
        return StreamSupport.stream(new BidiBreadthFirstSpliterator(VertexData::getNext, getVertexDataNotNull(start), visited), false);
    }

    /**
     * This is a performance-optimized implementation which does not need to call a hash function for
     * every vertex.
     */
    private class BidiBreadthFirstSpliterator extends Spliterators.AbstractSpliterator<V> {

        @Nullable
        private final Function<VertexData<V, A>, Iterable<ArrowData<V, A>>> nextNodesFunction;
        @NonNull
        private final Queue<VertexData<V, A>> queue;
        @Nullable
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
        public BidiBreadthFirstSpliterator(@NonNull Function<VertexData<V, A>, Iterable<ArrowData<V, A>>> nextNodesFunction,
                                           @NonNull VertexData<V, A> root, @NonNull Predicate<V> visited) {
            super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
            Objects.requireNonNull(nextNodesFunction, "nextNodesFunction");
            Objects.requireNonNull(root, "root");
            Objects.requireNonNull(visited, "vistied");
            this.nextNodesFunction = nextNodesFunction;
            queue = new ArrayDeque<>(16);
            this.visited = visited;
            queue.add(root);
            visited.test(root.v);
        }


        @Override
        public boolean tryAdvance(@NonNull Consumer<? super V> action) {
            VertexData<V, A> current = queue.poll();
            if (current == null) {
                return false;
            }
            for (ArrowData<V, A> next : nextNodesFunction.apply(current)) {
                if (visited.test(next.to.v)) {
                    queue.add(next.to);
                }
            }
            action.accept(current.v);
            return true;
        }
    }

}
