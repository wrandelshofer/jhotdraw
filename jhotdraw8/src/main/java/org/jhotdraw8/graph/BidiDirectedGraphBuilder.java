/* @(#)BidiDirectedGraphBuilder.java
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Provides an API for building a {@code BidiDirectedGraph}.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public class BidiDirectedGraphBuilder<V, A> implements BidiDirectedGraph<V, A> {

    private final List<ArrowData<V, A>> arrows;
    private final Map<V, VertexData<V, A>> vertices;
    private final List<V> vertexList;

    public BidiDirectedGraphBuilder() {
        arrows = new ArrayList<>();
        vertices = new LinkedHashMap<>();
        vertexList = new ArrayList<>();
    }

    public BidiDirectedGraphBuilder(DirectedGraph<V, A> that) {
        arrows = new ArrayList<>(that.getArrowCount());
        vertices = new LinkedHashMap<>(that.getVertexCount());
        vertexList = new ArrayList<>(that.getVertexCount());

        for (V v : that.getVertices()) {
            addVertex(v);
        }
        for (V v : that.getVertices()) {
            for (int i = 0, n = that.getNextCount(v); i < n; i++) {
                addArrow(v, that.getNext(v, i), that.getArrow(v, i));
            }
        }
    }

    public void clear() {
        vertices.clear();
        vertexList.clear();
        arrows.clear();
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

    @Override
    public V getPrev(V vertex, int i) {
        return vertices.get(vertex).prev.get(i).from;
    }

    @Override
    public int getPrevCount(V vertex) {
        return vertices.get(vertex).prev.size();
    }

    @Override
    public A getArrow(int index) {
        return arrows.get(index).arrow;
    }

    @Override
    public A getArrow(V vertex, int index) {
        return vertices.get(vertex).next.get(index).arrow;
    }

    @Override
    public int getArrowCount() {
        return arrows.size();
    }

    @Override
    public V getNext(V vertex, int i) {
        return vertices.get(vertex).next.get(i).to;
    }

    @Override
    public int getNextCount(V vertex) {
        return vertices.get(vertex).next.size();
    }

    @Override
    public V getVertex(int indexOfVertex) {
        return vertexList.get(indexOfVertex);
    }

    @Override
    public int getVertexCount() {
        return vertexList.size();
    }

    public void addVertex(V v) {
        if (vertices.containsKey(v)) {
            return;
        }
        vertices.put(v, new VertexData<>(v));
        vertexList.add(v);
    }

    public void removeArrow(V v, A a) {
        for (int i = 0, n = getNextCount(v); i < n; i++) {
            if (a.equals(getArrow(v, i))) {
                removeNext(v, i);
                break;
            }
        }
    }

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

    public void removeNext(V from, int i) {
        final VertexData<V, A> fromVertex = vertices.get(from);
        ArrowData<V, A> a = fromVertex.next.get(i);
        final VertexData<V, A> toVertex = vertices.get(a.to);
        fromVertex.next.remove(i);
        toVertex.prev.remove(a);
        arrows.remove(a);
    }

    private static class ArrowData<V, A> {

        final V from;
        final V to;
        final A arrow;

        public ArrowData(V from, V to, A arrow) {
            this.from = from;
            this.to = to;
            this.arrow = arrow;
        }

    }

    private static class VertexData<V, A> {

        final V v;
        final List<ArrowData<V, A>> next = new ArrayList<>();
        final List<ArrowData<V, A>> prev = new ArrayList<>();

        public VertexData(V v) {
            this.v = v;
        }

    }
}
