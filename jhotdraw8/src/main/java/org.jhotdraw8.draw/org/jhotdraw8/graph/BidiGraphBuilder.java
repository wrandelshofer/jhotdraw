/*
 * @(#)BidiGraphBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Provides an API for building a {@code BidiGraph}.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 */
public class BidiGraphBuilder<V, A> implements BidiGraph<V, A> {

    private int arrowCount;
    private final @NonNull Map<V, VertexData<V, A>> vertices;

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
    public BidiGraphBuilder(@NonNull DirectedGraph<V, A> that) {
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
    public <VV, AA> BidiGraphBuilder(@NonNull DirectedGraph<VV, AA> that, @NonNull Function<VV, V> vertexMapper, @NonNull Function<AA, A> arrowMapper) {
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
    public void addArrow(@NonNull V start, @NonNull V end, @Nullable A arrow) {
        Objects.requireNonNull(start, "start is null");
        Objects.requireNonNull(end, "end is null");
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
    public void addBidiArrow(@NonNull V va, @NonNull V vb, A arrow) {
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
    public @NonNull V getNext(@NonNull V vertex, int i) {
        return getVertexDataNonNull(vertex).next.get(i).end.v;
    }

    @Override
    public @NonNull A getNextArrow(@NonNull V vertex, int index) {
        return getVertexDataNonNull(vertex).next.get(index).arrow;
    }

    @Override
    public int getNextCount(@NonNull V vertex) {
        return getVertexDataNonNull(vertex).next.size();
    }

    @Override
    public @NonNull V getPrev(@NonNull V vertex, int i) {
        return getVertexDataNonNull(vertex).prev.get(i).start.v;
    }

    @Override
    public @NonNull A getPrevArrow(@NonNull V vertex, int index) {
        return getVertexDataNonNull(vertex).prev.get(index).arrow;
    }

    @Override
    public int getPrevCount(@NonNull V vertex) {
        return getVertexDataNonNull(vertex).prev.size();
    }

    @Override
    public int getVertexCount() {
        return vertices.size();
    }

    @Override
    public @NonNull Set<V> getVertices() {
        return Collections.unmodifiableSet(vertices.keySet());
    }

    @Override
    public @NonNull Collection<A> getArrows() {
        class ArrowIterator implements Iterator<A> {
            private final @NonNull Iterator<V> vertexIterator;
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
            public @Nullable A next() {
                while (!nextArrowIterator.hasNext()) {
                    V v = vertexIterator.next();
                    nextArrowIterator = getNextArrows(v).iterator();
                }
                return nextArrowIterator.next();
            }

        }
        return new AbstractCollection<A>() {
            @Override
            public @NonNull Iterator<A> iterator() {
                return new ArrowIterator();
            }

            @Override
            public int size() {
                return getArrowCount();
            }

        };
    }

    private VertexData<V, A> getVertexDataNonNull(V vertex) {
        VertexData<V, A> vertexData = vertices.get(vertex);
        Objects.requireNonNull(vertexData, "vertex is not in graph. vertex=" + vertex);
        return vertexData;
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
    public void removeNext(V start, int i) {
        final VertexData<V, A> startData = vertices.get(start);
        removeNext(startData, i);
    }

    private void removeNext(@NonNull VertexData<V, A> startData, int i) {
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

        final @NonNull VertexData<V, A> start;
        final @NonNull VertexData<V, A> end;
        final @Nullable A arrow;

        ArrowData(final @NonNull VertexData<V, A> start, final @NonNull VertexData<V, A> end, final @Nullable A arrow) {
            this.start = start;
            this.end = end;
            this.arrow = arrow;
        }

        public @NonNull VertexData<V, A> getStart() {
            return start;
        }

        public @NonNull VertexData<V, A> getEnd() {
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

        public @NonNull List<ArrowData<V, A>> getNext() {
            return next;
        }

        public @NonNull List<ArrowData<V, A>> getPrev() {
            return prev;
        }
    }

}
