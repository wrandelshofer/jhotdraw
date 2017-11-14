/* @(#)DirectedGraphBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

/**
 * DirectedGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AbstractDirectedGraphBuilder implements IntDirectedGraph {

    private final static int EDGES_NEXT_FIELD = 1;
    private final static int EDGES_NUM_FIELDS = 2;
    private final static int EDGES_VERTEX_FIELD = 0;
    private final static int LASTEDGE_COUNT_FIELD = 0;
    private final static int LASTEDGE_NUM_FIELDS = 2;
    private final static int LASTEDGE_POINTER_FIELD = 1;
    private final static int SENTINEL = -1;

    private int edgeCount;
    /**
     * Table of edges.
     * <p>
     * {@code edges[i * EDGES_NUM_FIELDS+EDGES_VERTEX_FIELD} contains the index
     * of the vertex of the i-th edge.
     * <p>
     * {@code edges[i * EDGES_NUM_FIELDS+EDGES_NEXT_FIELD} contains the index of
     * the next edge.
     */
    private int[] edges;

    /**
     * Table of last edges.
     * <p>
     * {@code lastEdge[i * LASTEDGE_NUM_FIELDS+LASTEDGE_POINTER_FIELD} contains
     * the index of the last edge of the i-th vertex.
     * <p>
     * {@code lastEdge[i * LASTEDGE_NUM_FIELDS+LASTEDGE_COUNT_FIELD} contains
     * the number of edges of the i-th vertex.
     */
    private int[] lastEdge;
    /**
     * The vertex count.
     */
    private int vertexCount;

    public AbstractDirectedGraphBuilder() {
        this(16, 16);
    }

    public AbstractDirectedGraphBuilder(int vertexCapacity, int edgeCapacity) {
        if (vertexCapacity < 0) {
            throw new IllegalArgumentException("vertexCapacity: " + vertexCapacity);
        }
        if (edgeCapacity < 0) {
            throw new IllegalArgumentException("edgeCapacity: " + edgeCapacity);
        }
        this.edges = new int[edgeCapacity * EDGES_NUM_FIELDS];
        this.lastEdge = new int[vertexCapacity * LASTEDGE_NUM_FIELDS];
    }

    /**
     * Builder-method: adds a directed edge from 'a' to 'b'.
     *
     * @param a vertex a
     * @param b vertex b
     */
    protected void buildAddEdge(int a, int b) {
        if (edges.length <= edgeCount * EDGES_NUM_FIELDS) {
            int[] tmp = edges;
            edges = new int[edges.length * EDGES_NUM_FIELDS];
            System.arraycopy(tmp, 0, edges, 0, tmp.length);
        }

        int edgeCountOfA = lastEdge[a * LASTEDGE_NUM_FIELDS + LASTEDGE_COUNT_FIELD];
        int lastEdgeIdOfA = edgeCountOfA == 0 ? SENTINEL : lastEdge[a * LASTEDGE_NUM_FIELDS + LASTEDGE_POINTER_FIELD];

        int newLastEdgeIdOfA = edgeCount;
        edges[newLastEdgeIdOfA * EDGES_NUM_FIELDS + EDGES_VERTEX_FIELD] = b;
        edges[newLastEdgeIdOfA * EDGES_NUM_FIELDS + EDGES_NEXT_FIELD] = lastEdgeIdOfA;

        lastEdge[a * LASTEDGE_NUM_FIELDS + LASTEDGE_COUNT_FIELD] = edgeCountOfA + 1;
        lastEdge[a * LASTEDGE_NUM_FIELDS + LASTEDGE_POINTER_FIELD] = newLastEdgeIdOfA;

        edgeCount++;
    }

    /**
     * Builder-method: adds a vertex.
     */
    protected void buildAddVertex() {
        vertexCount++;
        if (lastEdge.length < vertexCount * LASTEDGE_NUM_FIELDS) {
            int[] tmp = lastEdge;
            lastEdge = new int[lastEdge.length * 2 * LASTEDGE_NUM_FIELDS];
            System.arraycopy(tmp, 0, lastEdge, 0, tmp.length);
        }
    }

    @Override
    public int getEdgeCount() {
        return edgeCount;
    }

    protected void buildSetVertexCount(int newValue) {
        if (newValue < vertexCount) {
            throw new IllegalArgumentException("can only add vertices:" + newValue);
        }
        vertexCount = newValue;
        if (lastEdge.length < vertexCount * LASTEDGE_NUM_FIELDS) {
            int[] tmp = lastEdge;
            lastEdge = new int[lastEdge.length * LASTEDGE_NUM_FIELDS];
            System.arraycopy(tmp, 0, lastEdge, 0, tmp.length);
        }
        
    }
    
    /**
     * Removes the i-th edge of vertex vi.
     * <p>
     * This implementation has a time complexity of O(V+E).
     *
     * @param vi a vertex
     * @param i the i-th edge of vertex vi
     */
    protected void buildRemoveEdge(int vi, int i) {
        if (vi < 0 || vi >= getVertexCount()) {
            throw new IllegalArgumentException("vi:" + i);
        }
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i:" + i);
        }

        // find the edgeId and the previous edgeId
        int prevEdgeId = SENTINEL;
        int edgeId = lastEdge[vi * LASTEDGE_NUM_FIELDS + LASTEDGE_POINTER_FIELD];
        for (int j = i - 1; j >= 0; j--) {
            prevEdgeId = edgeId;
            edgeId = edges[edgeId * EDGES_NUM_FIELDS + EDGES_NEXT_FIELD];
        }

        if (prevEdgeId == SENTINEL) {
            // if there is no previous edgeId => make the point from lastEdge point to the edge after edgeId.
            lastEdge[vi * LASTEDGE_NUM_FIELDS + LASTEDGE_POINTER_FIELD] = edges[edgeId * EDGES_NUM_FIELDS + EDGES_NEXT_FIELD];
        } else {
            // if there is a previous edgeId => make the pointer from prevEdgeId point to the edge after edgeId.
            edges[prevEdgeId * EDGES_NUM_FIELDS + EDGES_NEXT_FIELD] = edges[edgeId * EDGES_NUM_FIELDS + EDGES_NEXT_FIELD];
        }
        // Decrease number of edges for vertex vi
        lastEdge[vi * LASTEDGE_NUM_FIELDS + LASTEDGE_COUNT_FIELD]--;

        // Decrease total number of edges
        edgeCount--;

        int moveEdgeId = edgeCount;
        if (edgeId != moveEdgeId) {
            // move moveEdgeId to edgeId
            edges[edgeId * EDGES_NUM_FIELDS + EDGES_VERTEX_FIELD] = edges[moveEdgeId * EDGES_NUM_FIELDS + EDGES_VERTEX_FIELD];
            edges[edgeId * EDGES_NUM_FIELDS + EDGES_NEXT_FIELD] = edges[moveEdgeId * EDGES_NUM_FIELDS + EDGES_NEXT_FIELD];
            // if there is a pointer in lastEdges to moveEdgeId, make it point to edgeId. 
            boolean fixed = false;
            for (int v = 0; v < vertexCount; v++) {
                if (lastEdge[v * LASTEDGE_NUM_FIELDS + LASTEDGE_COUNT_FIELD] > 0
                        && lastEdge[v * LASTEDGE_NUM_FIELDS + LASTEDGE_POINTER_FIELD] == moveEdgeId) {
                    lastEdge[v * LASTEDGE_NUM_FIELDS + LASTEDGE_POINTER_FIELD] = edgeId;
                    fixed = true;
                    break;
                }
            }
            // if there is a pointer in edges to moveEdgeId, make it point to edgeId. 
            if (!fixed) {
                for (int e = 0; e < edgeCount; e++) {
                    if (edges[e * EDGES_NUM_FIELDS + EDGES_NEXT_FIELD] == moveEdgeId) {
                        edges[e * EDGES_NUM_FIELDS + EDGES_NEXT_FIELD] = edgeId;
                        fixed = true;
                        break;
                    }
                }
            }
        }
    }

    protected int getIndexOfEdge(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("0 <= i(" + i + ") <= " + getNextCount(vi));
        }
        int edgeId = lastEdge[vi * LASTEDGE_NUM_FIELDS + LASTEDGE_POINTER_FIELD];
        int nextCount = lastEdge[vi * LASTEDGE_NUM_FIELDS + LASTEDGE_COUNT_FIELD];
        for (int j = nextCount - 1; j > i; j--) {
            edgeId = edges[edgeId * EDGES_NUM_FIELDS + EDGES_NEXT_FIELD];
        }
        return edgeId;
    }

    @Override
    public int getNext(int vi, int i) {
        int edgeId = getIndexOfEdge(vi, i);
        return edges[edgeId * EDGES_NUM_FIELDS + EDGES_VERTEX_FIELD];
    }

    @Override
    public int getNextCount(int vi) {
        return lastEdge[vi * LASTEDGE_NUM_FIELDS + LASTEDGE_COUNT_FIELD];
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

}
