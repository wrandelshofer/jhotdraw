/* @(#)ImmutableIntDirectedGraph.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * ImmutableIntDirectedGraph.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ImmutableIntDirectedGraph implements IntDirectedGraph {

    /**
     * Holds the edges.
     */
    private final int[] edges;

    /**
     * Holds offsets into the edges table for each vertex.
     */
    private final int[] vertices;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public ImmutableIntDirectedGraph(IntDirectedGraph graph) {
        int edgeCount = 0;

        final int edgeCapacity = graph.getEdgeCount();
        final int vertexCapacity = graph.getVertexCount();

        this.edges = new int[edgeCapacity];
        this.vertices = new int[vertexCapacity];

        for (int a = 0; a < vertexCapacity; a++) {
            vertices[a] = edgeCount;
            for (int i = 0, n = graph.getNextCount(a); i < n; i++) {
                edges[edgeCount++] = graph.getNext(a, i);
            }
        }
    }

    /**
     * Creates a new instance from the specified graph.
     *
     * @param <V> the vertex type
     * @param graph a graph
     */
    public <V> ImmutableIntDirectedGraph(DirectedGraph<V> graph) {
        int edgeCount = 0;

        final int edgeCapacity = graph.getEdgeCount();
        final int vertexCapacity = graph.getVertexCount();

        this.edges = new int[edgeCapacity];
        this.vertices = new int[vertexCapacity];

        Map<V, Integer> vertexMap = new HashMap<>(vertexCapacity);
        for (int a = 0; a < vertexCapacity; a++) {
            vertexMap.put(graph.getVertex(a), a);
        }

        for (int a = 0; a < vertexCapacity; a++) {
            V va = graph.getVertex(a);
            vertices[a] = edgeCount;
            for (int i = 0, n = graph.getNextCount(va); i < n; i++) {
                edges[edgeCount++] = vertexMap.get(graph.getNext(va, i));
            }
        }
    }

    @Override
    public int getEdgeCount() {
        return edges.length;
    }

    @Override
    public int getNext(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNext(vi, i));
        }
        return edges[vertices[vi] + i];
    }

    @Override
    public int getNextCount(int vi) {
        final int vertexCount = getVertexCount();
        if (vi < 0 || vi >= vertexCount) {
            throw new IllegalArgumentException("vi(" + vi + ") < 0 || vi >= " + vertexCount);
        }
        final int offset = vertices[vi];
        final int nextOffset = (vi == vertexCount - 1) ? edges.length : vertices[vi + 1];
        return nextOffset - offset;
    }

    @Override
    public int getVertexCount() {
        return vertices.length;
    }
}
