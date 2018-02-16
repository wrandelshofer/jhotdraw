/* @(#)BidiDirectedGraphBuilder
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * BidiDirectedGraphBuilder.
 * <p>
 * This is a simple and inefficient implementation.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class BidiDirectedGraphBuilder<V, A> implements BidiDirectedGraph<V, A> {

    public BidiDirectedGraphBuilder() {
    }
    public BidiDirectedGraphBuilder(DirectedGraph<V,A> that) {
        for (V v:that.getVertices()) {
            addVertex(v);
        }
        for (V v:that.getVertices()) {
            for (int i=0,n=that.getNextCount(v);i<n;i++) {
                addArrow(v, that.getNext(v, i), that.getArrow(v, i));
            }
        }
    }

    public void clear() {
        vertices.clear();
        vertexList.clear();
        arrows.clear();
    }

    @Override
    public V getPrev(V vertex, int i) {
        return vertices.get(vertex).prev.get(i).from;
    }

    @Override
    public int getPrevCount(V vertex) {
        return vertices.get(vertex).prev.size();
    }

    private static class Arrow<V, A> {

        final V from;
        final V to;
        final A arrow;

        public Arrow(V from, V to, A arrow) {
            this.from = from;
            this.to = to;
            this.arrow = arrow;
        }

    }

    private static class Vertex<V, A> {

        final V v;
        final List<Arrow<V, A>> next = new ArrayList<>();
        final List<Arrow<V, A>> prev = new ArrayList<>();

        public Vertex(V v) {
            this.v = v;
        }

    }
    private List<Arrow<V, A>> arrows = new ArrayList<>();
    private Map<V, Vertex<V, A>> vertices = new LinkedHashMap<>();
    private List<V> vertexList = new ArrayList<>();

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
        vertices.put(v, new Vertex<>(v));
        vertexList.add(v);
    }

    public void removeArrow(V v, A a) {
        for (int i=0,n=getNextCount(v);i<n;i++) {
            if (a.equals(getArrow(v, i))) {
                removeNext(v, i);
                break;
            }
        }
    }
    
    public void removeVertex(V v) {
        final Vertex<V, A> vertex = vertices.get(v);
        if (vertex==null)return;
        for (int i = vertex.next.size() - 1; i >= 0; i--) {
            removeNext(v, i);
        }
        for (int i = vertex.prev.size() - 1; i >= 0; i--) {
            Arrow<V, A> arrow = vertex.prev.get(i);
            removeNext(arrow.from, vertices.get(arrow.from).next.indexOf(arrow));
        }
        vertices.remove(v);
        vertexList.remove(v);
    }

    public void addArrow(V from, V to, A arrow) {
        if (from==null||to==null||arrow==null)throw new IllegalArgumentException("from="+from+", to="+to+", arrow="+arrow);
        final Vertex<V, A> fromVertex = vertices.get(from);
        final Vertex<V, A> toVertex = vertices.get(to);
        Arrow<V, A> a = new Arrow<>(from, to, arrow);
        fromVertex.next.add(a);
        toVertex.prev.add(a);
        arrows.add(a);
    }

    public void removeNext(V from, int i) {
        final Vertex<V, A> fromVertex = vertices.get(from);
        Arrow<V, A> a = fromVertex.next.get(i);
        final Vertex<V, A> toVertex = vertices.get(a.to);
        fromVertex.next.remove(i);
        toVertex.prev.remove(a);
        arrows.remove(a);
    }
}
