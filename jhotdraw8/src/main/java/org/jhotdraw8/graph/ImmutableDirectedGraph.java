/* @(#)ImmutableDirectedGraph.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * ImmutableDirectedGraph.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 */
public class ImmutableDirectedGraph<V> implements DirectedGraph<V> {

    /**
     * Holds the number of edges.
     */
    private final int edgeCount;

    /**
     * Holds the edges.
     * <p>
     * Contains no entry if a vertex has no edges. Contains an array if a vertex
     * has one or more edges.
     */
    private final Map<V, Object[]> edges;

    /**
     * Holds the vertices.
     */
    private final Object[] vertices;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public ImmutableDirectedGraph(DirectedGraph<V> graph) {
        edgeCount = graph.getEdgeCount();
        final int vertexCapacity = graph.getVertexCount();

        this.vertices = new Object[vertexCapacity];
        this.edges = new HashMap<>(vertexCapacity);

        for (int a = 0; a < vertexCapacity; a++) {
            V va = graph.getVertex(a);
            vertices[a] = va;
            final int nextCount = graph.getNextCount(va);
            if (nextCount > 0) {
                Object[] edgeList = new Object[nextCount];
                for (int i = 0; i < nextCount; i++) {
                    edgeList[i++] = graph.getNext(va, i);
                }
                edges.put(va, edgeList);
            }
        }
    }

    @Override
    public int getEdgeCount() {
        return edgeCount;
    }

    @Override
    public V getNext(V v, int i) {
        final Object[] edgeList = edges.get(v);
        if (edgeList == null) {
            throw new IllegalArgumentException("vertex v(" + v + ") has no edges.");
        }
        @SuppressWarnings("unchecked")
        final V b = (V) edgeList[i];
        return b;
    }

    @Override
    public int getNextCount(V v) {
        final Object[] edgeList = edges.get(v);
        return edgeList == null ? 0 : edgeList.length;
    }

    @Override
    public V getVertex(int indexOfVertex) {
        @SuppressWarnings("unchecked")
        final V v = (V) vertices[indexOfVertex];
        return v;
    }

    @Override
    public int getVertexCount() {
        return vertices.length;
    }
}
