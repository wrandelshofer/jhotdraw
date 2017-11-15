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

    public IntDirectedGraphBuilder(int vertexCapacity, int arrowCapacity) {
        super(vertexCapacity,arrowCapacity);
    }

    /**
     * Adds a directed arrow from 'a' to 'b' and another arrow
     * from 'b' to 'a'.
     * <p>
     * Before you may call this method, you must have called
     * {@link #setVertexCount(int)}.
     *
     * @param a vertex a
     * @param b vertex b
     */
    public void addBidiArrow(int a, int b) {
        addArrow(a, b);
        addArrow(b, a);

    }

    /**
     * Builder-method: adds a directed arrow from 'a' to 'b'.
     * <p>
     * Before you may call this method, you must have called
     * {@link #setVertexCount(int)}.
     *
     * @param a vertex a
     * @param b vertex b
     */
    public void addArrow(int a, int b) {
        buildAddArrow(a,b);
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
public void removeArrow(int vi, int i) {
    buildRemoveArrow(vi,i);
}
    /**
     * Creates a graph with all arrows inverted.
     *
     * @param graph a graph
     * @return a new graph with inverted arrows
     */
    public static IntDirectedGraphBuilder inverseOfIntDirectedGraph(IntDirectedGraph graph) {
        int arrowCount = graph.getArrowCount();

        IntDirectedGraphBuilder b = new IntDirectedGraphBuilder(graph.getVertexCount(), arrowCount);
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            int v = i;
            for (int j = 0, m = graph.getNextCount(v); j < m; j++) {
                b.addArrow(graph.getNext(v, j), v);
            }
        }
        return b;
    }

    public static IntDirectedGraphBuilder ofIntDirectedGraph(IntDirectedGraph graph) {
        int arrowCount = graph.getArrowCount();

        IntDirectedGraphBuilder b = new IntDirectedGraphBuilder(graph.getVertexCount(), arrowCount);
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            int v = i;
            for (int j = 0, m = graph.getNextCount(v); j < m; j++) {
                b.addArrow(v, graph.getNext(v, j));
            }
        }
        return b;
    }
}
