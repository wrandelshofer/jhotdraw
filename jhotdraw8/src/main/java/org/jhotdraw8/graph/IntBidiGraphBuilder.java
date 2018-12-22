/* @(#)IntBidiGraphBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;

/**
 * IntBidiGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntBidiGraphBuilder extends AbstractBidiGraphBuilder {

    /**
     * Creates a builder which contains a copy of the specified graph with all
     * arrows inverted.
     *
     * @param graph a graph
     * @return a new graph with inverted arrows
     */
    @Nonnull
    public static IntBidiGraphBuilder inverseOfIntBidiGraph(IntBidiGraph graph) {
        int arrowCount = graph.getArrowCount();

        IntBidiGraphBuilder b = new IntBidiGraphBuilder(graph.getVertexCount(), arrowCount);
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
    public static IntBidiGraphBuilder ofIntBidiGraph(IntBidiGraph graph) {
        int arrowCount = graph.getArrowCount();

        IntBidiGraphBuilder b = new IntBidiGraphBuilder(graph.getVertexCount(), arrowCount);
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            int v = i;
            for (int j = 0, m = graph.getNextCount(v); j < m; j++) {
                b.addArrow(v, graph.getNext(v, j));
            }
        }
        return b;
    }

    public IntBidiGraphBuilder() {
        this(16, 16);
    }

    public IntBidiGraphBuilder(int vertexCapacity, int arrowCapacity) {
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
     * @param vidxa vertex a
     * @param vidxb vertex b
     */
    public void addArrow(int vidxa, int vidxb) {
        buildAddArrow(vidxa, vidxb);
    }

    /**
     * Adds a vertex.
     */
    public void addVertex() {
        buildAddVertex();
    }

    /**
     * Builds an ImmutableIntBidiGraph from this builder.
     * 
     * @return the created graph
     */
    @Nonnull
    public IntImmutableBidiGraph build() {
        return new IntImmutableBidiGraph(this);
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
     * Removes the i-th arrow from the given vertex.
     *
     * @param vidx vertex
     * @param i the index of an arrow from the vertex.
     */
    public void removeArrow(int vidx, int i) {
        buildRemoveArrow(vidx, i);
    }

}
