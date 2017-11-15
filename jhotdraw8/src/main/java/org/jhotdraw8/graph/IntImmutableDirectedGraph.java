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
     * Holds the arrows.
     */
    protected final int[] arrows;

    /**
     * Holds offsets into the arrows table for each vertex.
     */
    protected final int[] vertices;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public IntImmutableDirectedGraph(IntDirectedGraph graph) {
        int arrowCount = 0;

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.arrows = new int[arrowCapacity];
        this.vertices = new int[vertexCapacity];

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            vertices[vIndex] = arrowCount;
            for (int i = 0, n = graph.getNextCount(vIndex); i < n; i++) {
                arrows[arrowCount++] = graph.getNext(vIndex, i);
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

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.arrows = new int[arrowCapacity];
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
                arrows[arrowCount++] = vertexToIndexMap.get(graph.getNext(vObject, i));
            }
        }
    }

    protected IntImmutableDirectedGraph(int vertexCount, int arrowCount) {
        this.arrows = new int[arrowCount];
        this.vertices = new int[vertexCount];
    }

    @Override
    public int getArrowCount() {
        return arrows.length;
    }

    @Override
    public int getNext(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNext(vi, i));
        }
        return arrows[vertices[vi] + i];
    }

    @Override
    public int getNextCount(int vi) {
        final int vertexCount = getVertexCount();
        if (vi < 0 || vi >= vertexCount) {
            throw new IllegalArgumentException("vi(" + vi + ") < 0 || vi >= " + vertexCount);
        }
        final int offset = vertices[vi];
        final int nextOffset = (vi == vertexCount - 1) ? arrows.length : vertices[vi + 1];
        return nextOffset - offset;
    }

    @Override
    public int getVertexCount() {
        return vertices.length;
    }
}
