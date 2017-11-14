/* @(#)IntDirectedGraphBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

/**
 * IntDirectedGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntDirectedGraphBuilder extends AbstractDirectedGraphBuilder {


    public IntDirectedGraphBuilder() {
        this(16, 16);
    }

    public IntDirectedGraphBuilder(int vertexCapacity, int edgeCapacity) {
        super(vertexCapacity,edgeCapacity);
    }

    /**
     * Adds a directed edge from 'a' to 'b' and another edge
     * from 'b' to 'a'.
     * <p>
     * Before you may call this method, you must have called
     * {@link #setVertexCount(int)}.
     *
     * @param a vertex a
     * @param b vertex b
     */
    public void addBidiEdge(int a, int b) {
        addEdge(a, b);
        addEdge(b, a);

    }

    /**
     * Builder-method: adds a directed edge from 'a' to 'b'.
     * <p>
     * Before you may call this method, you must have called
     * {@link #setVertexCount(int)}.
     *
     * @param a vertex a
     * @param b vertex b
     */
    public void addEdge(int a, int b) {
        buildAddEdge(a,b);
    }

    /**
     * Builder-method: adds a vertex.
     */
    public void addVertex() {
        buildAddVertex();
    }

    public ImmutableIntDirectedGraph build() {
        return new ImmutableIntDirectedGraph(this);
    }


    public void setVertexCount(int newValue) {
        buildSetVertexCount(newValue);
    }
public void removeEdge(int vi, int i) {
    buildRemoveEdge(vi,i);
}
    /**
     * Creates a graph with all edges inverted.
     *
     * @param graph a graph
     * @return a new graph with inverted edges
     */
    public static IntDirectedGraphBuilder inverseOfIntDirectedGraph(IntDirectedGraph graph) {
        int edgeCount = graph.getEdgeCount();

        IntDirectedGraphBuilder b = new IntDirectedGraphBuilder(graph.getVertexCount(), edgeCount);
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            int v = i;
            for (int j = 0, m = graph.getNextCount(v); j < m; j++) {
                b.addEdge(graph.getNext(v, j), v);
            }
        }
        return b;
    }

    public static IntDirectedGraphBuilder ofIntDirectedGraph(IntDirectedGraph graph) {
        int edgeCount = graph.getEdgeCount();

        IntDirectedGraphBuilder b = new IntDirectedGraphBuilder(graph.getVertexCount(), edgeCount);
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            int v = i;
            for (int j = 0, m = graph.getNextCount(v); j < m; j++) {
                b.addEdge(v, graph.getNext(v, j));
            }
        }
        return b;
    }
}
