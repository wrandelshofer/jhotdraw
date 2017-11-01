/* @(#)DirectedGraphBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DirectedGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 */
public class DirectedGraphBuilder<V> extends AbstractDirectedGraphBuilder
        implements DirectedGraph<V> {
    /**
     * Maps a vertex to a vertex index.
     */
    private final Map<V, Integer> vertexMap;
    /**
     * Maps a vertex index to a vertex object.
     */
    private final List<V> vertices;

    public DirectedGraphBuilder() {
        this(16, 16);
    }

    public DirectedGraphBuilder(int vertexCapacity, int edgeCapacity) {
        super(vertexCapacity, edgeCapacity);
        this.vertexMap = new HashMap<>(vertexCapacity + vertexCapacity * 40 / 100, 0.75f);
        this.vertices = new ArrayList<>(vertexCapacity);
    }

    /**
     * Builder-method: adds an edge.
     *
     * @param va vertex a
     * @param vb vertex b
     */
    public void addEdge(V va, V vb) {
        if (va == null) {
            throw new IllegalArgumentException("va=null");
        }
        if (vb == null) {
            throw new IllegalArgumentException("vb=null");
        }
        int a = vertexMap.get(va);
        int b = vertexMap.get(vb);
        buildAddEdge(a, b);
    }

    /**
     * Builder-method: adds a vertex.
     *
     * @param v vertex
     */
public void addVertex(V v) {
        if (v == null) {
            throw new IllegalArgumentException("v=null");
        }
        vertexMap.computeIfAbsent(v, k -> {
            vertices.add(v);
            buildAddVertex();
            return vertices.size() - 1;
        });
    }

    /**
     * Creates a graph with all edges inverted.
     *
     * @param <X> the vertex type
     * @param graph a graph
     * @return a new graph with inverted edges
     */
    public static <X> DirectedGraphBuilder<X> inverseOfDirectedGraph(DirectedGraph<X> graph) {
        final int edgeCount = graph.getEdgeCount();

        DirectedGraphBuilder<X> b = new DirectedGraphBuilder<>(graph.getVertexCount(), edgeCount);
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            X v = graph.getVertex(i);
            b.addVertex(v);
        }
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            X v = graph.getVertex(i);
            for (int j = 0, m = graph.getNextCount(v); j < m; j++) {
                b.addEdge(graph.getNext(v, j), v);
            }
        }
        return b;
    }

    public static <X> DirectedGraphBuilder<X> ofDirectedGraph(DirectedGraph<X> model) {
        DirectedGraphBuilder<X> b = new DirectedGraphBuilder<>();
        for (int i = 0, n = model.getVertexCount(); i < n; i++) {
            X v = model.getVertex(i);
            b.addVertex(v);
            for (int j = 0, m = model.getNextCount(v); j < m; j++) {
                b.addEdge(v, model.getNext(v, j));
            }
        }
        return b;
    }

    /**
     * Creates a builder which contains the specified vertices, and only edges
     * from the directed graph, for the specified vertices.
     *
     * @param <X> the vertex type
     * @param model a graph
     * @param vertices a set of vertices
     * @return a subset of the directed graph
     */
    public static <X> DirectedGraphBuilder<X> subsetOfDirectedGraph(DirectedGraph<X> model, Set<X> vertices) {
        DirectedGraphBuilder<X> b = new DirectedGraphBuilder<>();
        for (X v : vertices) {
            b.addVertex(v);
        }
        for (X v : vertices) {
            for (int j = 0, m = model.getNextCount(v); j < m; j++) {
                final X u = model.getNext(v, j);
                if (vertices.contains(u)) {
                    b.addEdge(v, u);
                }
            }
        }
        return b;
    }
    
    
    public DirectedGraph<V> build() {
        final ImmutableDirectedGraph<V> graph = new ImmutableDirectedGraph<V>(this);
        if (!new DirectedGraphValidator<V>().validate(graph)) {
            throw new IllegalArgumentException("graph is not valid");
        }
        return graph;
    }
    @Override
    public V getNext(V v, int i) {
        return getVertex(getNext(getIndexOfVertex(v), i));
    }

    @Override
    public int getNextCount(V v) {
        return getNextCount(getIndexOfVertex(v));
    }

    @Override
    public V getVertex(int vi) {
        if (vertices.get(vi) == null) {
            System.err.println("DIrectedGraphBuilder is broken");
        }
        return vertices.get(vi);
    }
    protected int getIndexOfVertex(V v) {
        return vertexMap.get(v);
    }



}
