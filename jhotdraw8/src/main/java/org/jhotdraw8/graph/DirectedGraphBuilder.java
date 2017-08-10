/* @(#)DirectedGraphBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.graph.ImmutableDirectedGraph;
import org.jhotdraw8.graph.GraphWithKnownEdgeCount;
import org.jhotdraw8.graph.IntDirectedGraphBuilder;
import org.jhotdraw8.graph.DirectedGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DirectedGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <V> vertex type
 */
public class DirectedGraphBuilder<V> implements DirectedGraph<V>, IntDirectedGraph, GraphWithKnownEdgeCount {

    private final static int SENTINEL = -1;
    private final static int EDGES_NUM_FIELDS = 2;
    private final static int EDGES_POINTER_FIELD = 1;
    private final static int EDGES_VERTEX_FIELD = 0;
    private final static int LASTEDGE_NUM_FIELDS = 2;
    private final static int LASTEDGE_COUNT_FIELD = 0;
    private final static int LASTEDGE_POINTER_FIELD = 1;

    private int edgeCount;
    /**
     * Table of edges.
     * <p>
     * {@code edges[i * 2} contains the index of the vertex of the i-th edge.
     * <p>
     * {@code edges[i * 2 + 1} contains the index of the next edge.
     */
    private int[] edges;

    /**
     * Table of last edges.
     * <p>
     * {@code lastEdge[i * 2+1} contains the index of the last edge of the i-th
     * vertex.
     * <p>
     * {@code lastEdge[i * 2} contains the number of edges of the i-th vertex.
     */
    private int[] lastEdge;
    private int vertexCount;
    /**
     * Maps a vertex to a vertex index.
     */
    private final Map<V, Integer> vertexMap;
    /**
     * Maps a vertex index to a vertex..
     */
    private final List<V> vertices;

    public DirectedGraphBuilder() {
        this(16, 16);
    }

    public DirectedGraphBuilder(int vertexCapacity, int edgeCapacity) {
        if (vertexCapacity < 0) {
            throw new IllegalArgumentException("vertexCapacity: " + vertexCapacity);
        }
        if (edgeCapacity < 0) {
            throw new IllegalArgumentException("edgeCapacity: " + edgeCapacity);
        }
        this.vertexMap = new HashMap<>(vertexCapacity + vertexCapacity * 40 / 100, 0.75f);
        this.vertices = new ArrayList<>(vertexCapacity);
        this.edges = new int[edgeCapacity * EDGES_NUM_FIELDS];
        this.lastEdge = new int[vertexCapacity * LASTEDGE_NUM_FIELDS];
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
            vertexCount++;
            if (lastEdge.length < vertexCount * LASTEDGE_NUM_FIELDS) {
                int[] tmp = lastEdge;
                lastEdge = new int[lastEdge.length * 2 * LASTEDGE_NUM_FIELDS];
                System.arraycopy(tmp, 0, lastEdge, 0, tmp.length);
            }
            return vertices.size() - 1;
        });
    }

    /**
     * Builds the model.
     *
     * @return the model
     */
    public ImmutableDirectedGraph<V> build() {
        ImmutableDirectedGraph<V> model = new ImmutableDirectedGraph<>(vertexCount, edgeCount);
        for (V v : vertices) {
            model.buildAddVertex(v);
        }

        for (int i = 0; i < vertexCount; i++) {
            V v = getVertex(i);
            for (int j = 0, m = getNextCount(i); j < m; j++) {
                model.buildAddEdge(v, getVertex(getNext(i, j)));
            }
        }
        return model;
    }

    /**
     * Builder-method: adds a directed edge from 'a' to 'b'.
     * <p>
     * Before you may call this method, you must have called {@link #buildSetVertexCount(int)
     * }
     * and {@link #buildSetEdgeCount(int) }.
     *
     * @param a vertex a
     * @param b vertex b
     */
    private void buildAddEdge(int a, int b) {
        if (edges.length <= edgeCount * EDGES_NUM_FIELDS) {
            int[] tmp = edges;
            edges = new int[edges.length * EDGES_NUM_FIELDS];
            System.arraycopy(tmp, 0, edges, 0, tmp.length);
        }

        int edgeCountOfA = lastEdge[a * LASTEDGE_NUM_FIELDS + LASTEDGE_COUNT_FIELD];
        int lastEdgeIdOfA = lastEdge[a * LASTEDGE_NUM_FIELDS + LASTEDGE_POINTER_FIELD];

        int newLastEdgeIdOfA = edgeCount;
        edges[newLastEdgeIdOfA * EDGES_NUM_FIELDS + EDGES_VERTEX_FIELD] = b;
        edges[newLastEdgeIdOfA * EDGES_NUM_FIELDS + EDGES_POINTER_FIELD] = (edgeCountOfA != 0) ? lastEdgeIdOfA : SENTINEL;

        lastEdge[a * LASTEDGE_NUM_FIELDS + LASTEDGE_COUNT_FIELD] = edgeCountOfA + 1;
        lastEdge[a * LASTEDGE_NUM_FIELDS + LASTEDGE_POINTER_FIELD] = newLastEdgeIdOfA;

        edgeCount++;
    }

    @Override
    public int getEdgeCount() {
        return edgeCount;
    }

    @Override
    public V getNext(V v, int i) {
        return getVertex(getNext(indexOfVertex(v), i));
    }

    @Override
    public int getNext(int vi, int i) {
        int edgeId = lastEdge[vi * LASTEDGE_NUM_FIELDS + LASTEDGE_POINTER_FIELD];
        for (int j = i - 1; j >= 0; j--) {
            edgeId = edges[edgeId * EDGES_NUM_FIELDS + EDGES_POINTER_FIELD];
        }
        return edges[edgeId * EDGES_NUM_FIELDS + EDGES_VERTEX_FIELD];
    }

    @Override
    public int getNextCount(V v) {
        return getNextCount(indexOfVertex(v));
    }

    @Override
    public int getNextCount(int vi) {
        return lastEdge[vi * LASTEDGE_NUM_FIELDS + LASTEDGE_COUNT_FIELD];
    }

    @Override
    public V getVertex(int vi) {
        return vertices.get(vi);
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    public int indexOfVertex(V v) {
        return vertexMap.get(v);
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

    /**
     * Creates a graph with all edges inverted.
     *
     * @param <X> the vertex type
     * @param graph a graph
     * @return a new graph with inverted edges
     */
    public static <X> DirectedGraphBuilder<X> inverseOfDirectedGraph(DirectedGraph<X> graph) {
        int edgeCount = countEdges(graph);

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

    /**
     * Counts the edges of the provided graph.
     *
     * @param <X> the vertex type
     * @param graph a graph
     * @return the number of edges
     */
    public static <X> int countEdges(DirectedGraph<X> graph) {
        if (graph instanceof IntDirectedGraph) {
            return IntDirectedGraphBuilder.countEdges((IntDirectedGraph) graph);
        }

        int edgeCount;
        if (graph instanceof GraphWithKnownEdgeCount) {
            edgeCount = ((GraphWithKnownEdgeCount) graph).getEdgeCount();
        } else {
            edgeCount = 0;
            for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
                X v = graph.getVertex(i);
                edgeCount += graph.getNextCount(v);
            }
        }
        return edgeCount;
    }

}
