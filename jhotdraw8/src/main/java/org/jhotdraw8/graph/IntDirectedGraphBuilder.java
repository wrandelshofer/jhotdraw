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
public class IntDirectedGraphBuilder<A> extends AbstractDirectedGraphBuilder<A> {

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
     * @param arrow the arrow from 'a' to 'b' and from 'b' to 'a'
     */
    public void addBidiArrow(int a, int b, A arrow) {
        addArrow(a, b, arrow);
        addArrow(b, a, arrow);

    }

    /**
     * Builder-method: adds a directed arrow from 'a' to 'b'.
     * <p>
     * Before you may call this method, you must have called
     * {@link #setVertexCount(int)}.
     *
     * @param a vertex a
     * @param b vertex b
     * @param arrow the arrow from 'a' to 'b'
     */
    public void addArrow(int a, int b, A arrow) {
        buildAddArrow(a, b, arrow);
    }

    /**
     * Builder-method: adds a vertex.
     */
    public void addVertex() {
        buildAddVertex();
    }

    public ImmutableIntDirectedGraph<A> build() {
        return new ImmutableIntDirectedGraph<>(this);
    }

    public void setVertexCount(int newValue) {
        buildSetVertexCount(newValue);
    }

    public void removeArrow(int vi, int i) {
        buildRemoveArrow(vi, i);
    }

    /**
     * Creates a graph with all arrows inverted.
     *
     * @param <A> the arrow type
     * @param graph a graph
     * @return a new graph with inverted arrows
     */
    public static <A> IntDirectedGraphBuilder<A> inverseOfIntDirectedGraph(IntDirectedGraph<A> graph) {
        int arrowCount = graph.getArrowCount();

        IntDirectedGraphBuilder<A> b = new IntDirectedGraphBuilder<>(graph.getVertexCount(), arrowCount);
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            int v = i;
            for (int j = 0, m = graph.getNextCount(v); j < m; j++) {
                b.addArrow(graph.getNext(v, j), v, graph.getArrow(v, j));
            }
        }
        return b;
    }

    public static <A> IntDirectedGraphBuilder<A> ofIntDirectedGraph(IntDirectedGraph<A> graph) {
        int arrowCount = graph.getArrowCount();

        IntDirectedGraphBuilder<A> b = new IntDirectedGraphBuilder<>(graph.getVertexCount(), arrowCount);
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            int v = i;
            for (int j = 0, m = graph.getNextCount(v); j < m; j++) {
                b.addArrow(v, graph.getNext(v, j), graph.getArrow(v, j));
            }
        }
        return b;
    }
}
