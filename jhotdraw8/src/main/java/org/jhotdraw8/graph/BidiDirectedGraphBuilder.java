/* @(#)BidiDirectedGraphBuilder.java
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Provides an API for building a {@code BidiDirectedGraph}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public class BidiDirectedGraphBuilder<V, A> implements BidiDirectedGraph<V, A> {

    private final List<ArrowData<V, A>> arrows;
    private final List<V> vertexList;
    private final Map<V, VertexData<V, A>> vertices;

    /**
     * Creates a new instance with default capacity for vertices and arrows.
     */
    public BidiDirectedGraphBuilder() {
        this(10, 10);
    }

    /**
     * Creates a new instance with the specified capacities.
     *
     * @param vertexCapacity the vertex capacity
     * @param arrowCapacity the arrow capaicty
     */
    public BidiDirectedGraphBuilder(int vertexCapacity, int arrowCapacity) {
        arrows = new ArrayList<>(arrowCapacity);
        vertices = new LinkedHashMap<>(vertexCapacity);
        vertexList = new ArrayList<>(vertexCapacity);
    }

    /**
     * Creates a new instance which is a clone of the specified graph.
     *
     * @param that another graph
     */
    public BidiDirectedGraphBuilder(DirectedGraph<V, A> that) {
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
    public <VV, AA> BidiDirectedGraphBuilder(DirectedGraph<VV, AA> that, Function<VV, V> vertexMapper, Function<AA, A> arrowMapper) {
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
    public void addArrow(V from, V to, @Nullable A arrow) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from=" + from + ", to=" + to + ", arrow=" + arrow);
        }
        final VertexData<V, A> fromVertex = vertices.get(from);
        final VertexData<V, A> toVertex = vertices.get(to);
        if (fromVertex == null || toVertex == null) {
            throw new IllegalArgumentException(
                    "from=" + from + ", to=" + to + ", arrow=" + arrow + ", fromVertex=" + fromVertex + ", toVertex=" + toVertex);
        }
        ArrowData<V, A> a = new ArrowData<>(from, to, arrow);
        fromVertex.next.add(a);
        toVertex.prev.add(a);
        arrows.add(a);
    }

    /**
     * Adds an arrow from 'va' to 'vb' and an arrow from 'vb' to 'va'.
     *
     * @param va vertex a
     * @param vb vertex b
     * @param arrow the arrow
     */
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
        return getVertexDataNotNull(vertex).next.get(i).to;
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
        return getVertexDataNotNull(vertex).prev.get(i).from;
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
    public void removeArrow(V v, A a) {
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
    public void removeNext(V from, int i) {
        final VertexData<V, A> fromVertex = vertices.get(from);
        ArrowData<V, A> a = fromVertex.next.get(i);
        final VertexData<V, A> toVertex = vertices.get(a.to);
        fromVertex.next.remove(i);
        toVertex.prev.remove(a);
        arrows.remove(a);
    }

    /**
     * Removes the specified arrow from the graph.
     *
     * @param from the start vertex of the arrow
     * @param to the end vertex of the arrow
     */
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
    public void removeVertex(V v) {
        final VertexData<V, A> vertex = vertices.get(v);
        if (vertex == null) {
            return;
        }
        for (int i = vertex.next.size() - 1; i >= 0; i--) {
            removeNext(v, i);
        }
        for (int i = vertex.prev.size() - 1; i >= 0; i--) {
            ArrowData<V, A> arrow = vertex.prev.get(i);
            removeNext(arrow.from, vertices.get(arrow.from).next.indexOf(arrow));
        }
        vertices.remove(v);
        vertexList.remove(v);
    }

    private static class ArrowData<V, A> {

        final V from;
        final V to;
        final A arrow;

        ArrowData(V from, V to, A arrow) {
            this.from = from;
            this.to = to;
            this.arrow = arrow;
        }

    }

    private static class VertexData<V, A> {

        final V v;
        final List<ArrowData<V, A>> next = new ArrayList<>();
        final List<ArrowData<V, A>> prev = new ArrayList<>();

        VertexData(V v) {
            this.v = v;
        }

    }
}
