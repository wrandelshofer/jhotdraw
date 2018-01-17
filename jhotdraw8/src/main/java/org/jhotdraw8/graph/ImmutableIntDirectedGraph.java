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
 * @param <A> the arrow type
 */
public class ImmutableIntDirectedGraph<A> implements IntDirectedGraph<A> {

    /**
     * Holds the indices of the vertices at the arrow heads.
     */
    protected final int[] arrowHeads;

    /**
     * Holds offsets into the arrowHeads table for each vertex.
     */
    protected final int[] vertices;
    
    /** Holds the arrows. */
    protected final Object[] arrows;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public ImmutableIntDirectedGraph(IntDirectedGraph<A> graph) {
        int arrowCount = 0;

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.arrowHeads = new int[arrowCapacity];
        this.arrows = new Object[arrowCapacity];
        this.vertices = new int[vertexCapacity];

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            vertices[vIndex] = arrowCount;
            for (int i = 0, n = graph.getNextCount(vIndex); i < n; i++) {
                arrowHeads[arrowCount] = graph.getNext(vIndex, i);
                arrows[arrowCount] = graph.getArrow(vIndex, i);
                arrowCount++;
            }
        }
    }

    /**
     * Creates a new instance from the specified graph.
     *
     * @param <V> the vertex type
     * @param graph a graph
     */
    public <V> ImmutableIntDirectedGraph(DirectedGraph<V,A> graph) {

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.arrowHeads = new int[arrowCapacity];
        this.arrows = new Object[arrowCapacity];
        this.vertices = new int[vertexCapacity];

        Map<V, Integer> vertexToIndexMap = new HashMap<>(vertexCapacity);
        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = graph.getVertex(vIndex);
            vertexToIndexMap.put(vObject, vIndex);
        }

        int arrowCount = 0;
        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = graph.getVertex(vIndex);

            vertices[vIndex] = arrowCount;
            for (int i = 0, n = graph.getNextCount(vObject); i < n; i++) {
                arrowHeads[arrowCount] = vertexToIndexMap.get(graph.getNext(vObject, i));
                arrows[arrowCount] = graph.getArrow(vObject, i);
                arrowCount++;
            }
        }
    }

    protected ImmutableIntDirectedGraph(int vertexCount, int arrowCount) {
        this.arrowHeads = new int[arrowCount];
        this.vertices = new int[vertexCount];
        this.arrows = new Object[arrowCount];
    }

    @Override
    @SuppressWarnings("unchecked")
    public A getArrow(int index) {
       return (A) arrows[index];
    }

    @Override
    @SuppressWarnings("unchecked")
    public A getArrow(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNextCount(vi));
        }
        return (A)arrows[vertices[vi] + i];
    }

    @Override
    public int getArrowCount() {
        return arrowHeads.length;
    }

    @Override
    public int getNext(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNextCount(vi));
        }
        return arrowHeads[vertices[vi] + i];
    }
    
    protected int getArrowIndex(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNextCount(vi));
        }
        return vertices[vi]+i;
    }

    @Override
    public int getNextCount(int vi) {
        final int offset = vertices[vi];
        final int nextOffset = (vi == vertices.length - 1) ? arrowHeads.length : vertices[vi + 1];
        return nextOffset - offset;
    }

    @Override
    public int getVertexCount() {
        return vertices.length;
    }
}
