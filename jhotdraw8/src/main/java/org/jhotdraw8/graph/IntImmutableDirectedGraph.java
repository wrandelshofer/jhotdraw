/* @(#)IntImmutableDirectedGraph.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IntImmutableDirectedGraph.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntImmutableDirectedGraph implements IntDirectedGraph {

    /**
     * Holds the edges.
     */
    protected final int[] edges;

    /**
     * Holds offsets into the edges table for each vertex.
     */
    protected final int[] vertices;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public IntImmutableDirectedGraph(IntDirectedGraph graph) {
        int edgeCount = 0;

        final int edgeCapacity = graph.getEdgeCount();
        final int vertexCapacity = graph.getVertexCount();

        this.edges = new int[edgeCapacity];
        this.vertices = new int[vertexCapacity];

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            vertices[vIndex] = edgeCount;
            for (int i = 0, n = graph.getNextCount(vIndex); i < n; i++) {
                edges[edgeCount++] = graph.getNext(vIndex, i);
            }
        }
    }

    /**
     * Creates a new instance from the specified graph.
     *
     * @param <V> the vertex type
     * @param graph a graph
     */
    public <V> IntImmutableDirectedGraph(DirectedGraph<V> graph) {

        final int edgeCapacity = graph.getEdgeCount();
        final int vertexCapacity = graph.getVertexCount();

        this.edges = new int[edgeCapacity];
        this.vertices = new int[vertexCapacity];

        Map<V, Integer> vertexToIndexMap = new HashMap<>(vertexCapacity);
        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = graph.getVertex(vIndex);
            vertexToIndexMap.put(vObject, vIndex);
        }

        int edgeCount = 0;
        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = graph.getVertex(vIndex);

            vertices[vIndex] = edgeCount;
            for (int i = 0, n = graph.getNextCount(vObject); i < n; i++) {
                edges[edgeCount++] = vertexToIndexMap.get(graph.getNext(vObject, i));
            }
        }
    }

    protected IntImmutableDirectedGraph(int vertexCount, int edgeCount) {
        this.edges = new int[edgeCount];
        this.vertices = new int[vertexCount];
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
