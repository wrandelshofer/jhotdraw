/* @(#)ImmutableIntDirectedGraph.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.graph.GraphWithKnownEdgeCount;

/**
 * ImmutableIntDirectedGraph.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ImmutableIntDirectedGraph implements IntDirectedGraph, GraphWithKnownEdgeCount {
// FIXME  The arrays can be condensed by factor 2.
    private final int vertexCount;

    /**
     * Table of edges.
     * <p>
     * {@code edges[i * 2} contains the index of the vertex of the i-th edge.
     * <p>
     * {@code edges[i * 2 + 1} contains the index of the next edge.
     */
    private final int[] edges;

    private int edgeCount;

    /**
     * Table of last edges.
     * <p>
     * {@code lastEdge[i * 2+1} contains the index of the last edge of the i-th
     * vertex.
     * <p>
     * {@code lastEdge[i * 2} contains the number of edges of the i-th vertex.
     */
    private final int[] lastEdge;

    /**
     * Builder-constructor.
     *
     * @param vertexCapacity
     * @param edgeCapacity
     */
    ImmutableIntDirectedGraph(int vertexCapacity, int edgeCapacity) {
        this.vertexCount = vertexCapacity;
        this.edges = new int[edgeCapacity * 2];
        this.lastEdge = new int[vertexCapacity * 2];
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
    void buildAddEdge(int a, int b) {
        int edgeCountOfA = lastEdge[a * 2];
        int lastEdgeIdOfA = lastEdge[a * 2 + 1];

        int newLastEdgeIdOfA = edgeCount;

        edges[newLastEdgeIdOfA * 2] = b;
        edges[newLastEdgeIdOfA * 2 + 1] = (edgeCountOfA != 0) ? lastEdgeIdOfA : -1;

        lastEdge[a * 2] = edgeCountOfA + 1;
        lastEdge[a * 2 + 1] = newLastEdgeIdOfA;

        edgeCount++;
    }

    @Override
    public int getEdgeCount() {
        return edgeCount;
    }

    @Override
    public int getNext(int vi, int i) {
        int edgeId = lastEdge[vi * 2 + 1];
        for (int j = i - 1; j >= 0; j--) {
            edgeId = edges[edgeId * 2 + 1];
        }
        return edges[edgeId * 2];
    }

    @Override
    public int getNextCount(int vi) {
        return lastEdge[vi * 2];
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }
}
