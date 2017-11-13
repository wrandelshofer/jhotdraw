/* @(#)DirectedGraphBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * DirectedGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <E> the edge type
 */
public class DirectedGraphWithEdgesBuilder<V, E> extends AbstractDirectedGraphBuilder
        implements DirectedGraphWithEdges<V, E>, IntDirectedGraphWithEdges<E> {

    /**
     * Maps a vertex to a vertex index.
     */
    private final Map<V, Integer> vertexMap;
    /**
     * Maps a vertex index to a vertex object.
     */
    private final List<V> vertices;

    private final List<E> edgeData;

    public DirectedGraphWithEdgesBuilder() {
        this(16, 16);
    }

    public DirectedGraphWithEdgesBuilder(int vertexCapacity, int edgeCapacity) {
        super(vertexCapacity, edgeCapacity);
        this.vertexMap = new HashMap<>(vertexCapacity);
        this.vertices = new ArrayList<>(vertexCapacity);
        this.edgeData = new ArrayList<>();
    }

    public DirectedGraphWithEdgesBuilder(DirectedGraphWithEdges<V, E> graph) {
        super(graph.getVertexCount(), graph.getEdgeCount());
        final int vcount = graph.getVertexCount();
        this.vertexMap = new HashMap<>(vcount);
        this.vertices = new ArrayList<>(vcount);
        final int ecount = graph.getEdgeCount();
        this.edgeData = new ArrayList<>(ecount);

        for (int i = 0; i < vcount; i++) {
            addVertex(graph.getVertex(i));
        }
        for (int i = 0; i < vcount; i++) {
            V v = graph.getVertex(i);
            for (int j = 0, n = graph.getNextCount(v); j < n; j++) {
                addEdge(v, graph.getNext(v, j), graph.getNextEdge(v, j));
            }
        }
    }

    /**
     * Builder-method: adds a directed edge (arrow from va to vb).
     *
     * @param va vertex a
     * @param vb vertex b
     * @param edge the edge
     */
    public void addEdge(@Nonnull V va, @Nonnull V vb, @Nonnull E edge) {
        if (va == null) {
            throw new IllegalArgumentException("va=null");
        }
        if (vb == null) {
            throw new IllegalArgumentException("vb=null");
        }
        int a = vertexMap.get(va);
        int b = vertexMap.get(vb);
        buildAddEdge(a, b);

        edgeData.add(edge);
    }

    /**
     * Builder-method: adds two edges (arrow from va to vb and arrow from vb to
     * va).
     *
     * @param va vertex a
     * @param vb vertex b
     * @param edge the edge
     */
    public void addBidiEdge(@Nonnull V va, @Nonnull V vb, @Nonnull E edge) {
        addEdge(va, vb, edge);
        addEdge(vb, va, edge);
    }

    /**
     * Builder-method: adds a vertex.
     *
     * @param v vertex
     */
    public void addVertex(@Nonnull V v) {
        if (v == null) {
            throw new IllegalArgumentException("v=null");
        }
        vertexMap.computeIfAbsent(v, k -> {
            vertices.add(v);
            buildAddVertex();
            return vertices.size() - 1;
        });
    }

    @Override
    public E getEdge(int indexOfEdge) {
        return edgeData.get(indexOfEdge);
    }

    @Override
    public E getNextEdge(@Nonnull V vertex, int i) {
        int edgeId = getIndexOfEdge(getIndexOfVertex(vertex), i);
        return edgeData.get(edgeId);
    }

    @Override
    public E getNextEdge(int vertex, int index) {
        int edgeId = getIndexOfEdge(vertex, index);
        return edgeData.get(edgeId);
    }

    @Override
    @Nonnull
    public V getVertex(int vi) {
        if (vertices.get(vi) == null) {
            System.err.println("DIrectedGraphBuilder is broken");
        }
        return vertices.get(vi);
    }

    @Override
    public int getNextCount(@Nonnull V v) {
        return getNextCount(getIndexOfVertex(v));
    }

    @Override
    public V getNext(@Nonnull V v, int i) {
        return getVertex(getNext(getIndexOfVertex(v), i));
    }

    protected int getIndexOfVertex(@Nonnull V v) {
        return vertexMap.get(v);
    }

}
