/*
 * @(#)IntDirectedGraphBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;

/**
 * IntDirectedGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntDirectedGraphBuilder extends AbstractDirectedGraphBuilder {

    /**
     * Creates a builder which contains a copy of the specified graph with all
     * arrows inverted.
     *
     * @param graph a graph
     * @return a new graph with inverted arrows
     */
    @Nonnull
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

    /**
     * Creates a builder which contains a copy of the specified graph.
     *
     * @param graph a graph
     * @return a new graph
     */
    @Nonnull
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

    public IntDirectedGraphBuilder() {
        this(16, 16);
    }

    public IntDirectedGraphBuilder(int vertexCapacity, int arrowCapacity) {
        super(vertexCapacity, arrowCapacity);
    }

    /**
     * Adds a directed arrow from 'a' to 'b' and another arrow from 'b' to 'a'.
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
     * Adds a directed arrow from vertex 'a' to vertex 'b'.
     * <p>
     * Before you may call this method, you must have called
     * {@link #setVertexCount(int)}.
     *
     * @param a vertex a
     * @param b vertex b
     */
    public void addArrow(int a, int b) {
        buildAddArrow(a, b);
    }

    /**
     * Adds a vertex.
     */
    public void addVertex() {
        buildAddVertex();
    }

    /**
     * Builds an ImmutableIntDirectedGraph from this builder.
     *
     * @return the created graph
     */
    @Nonnull
    public IntImmutableDirectedGraph build() {
        return new IntImmutableDirectedGraph(this);
    }

    /**
     * Sets the vertex count.
     *
     * @param newValue the new vertex count, must be larger or equal the current vertex count.
     */
    public void setVertexCount(int newValue) {
        buildSetVertexCount(newValue);
    }

    /**
     * Removes the i-th arrow from vertex 'a'.
     *
     * @param a vertex 'a'
     * @param i the index of an arrow from vertex 'a'.
     */
    public void removeArrow(int a, int i) {
        buildRemoveArrow(a, i);
    }

}
