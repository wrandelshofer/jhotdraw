/* @(#)IntImmutableDirectedGraph.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

/**
 * IntImmutableDirectedGraph.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntImmutableDirectedGraph implements IntDirectedGraph {

    /**
     * Holds the arrow heads.
     */
    @Nonnull
    protected final int[] arrowHeads;

    /**
     * Holds offsets into the nextArrowHeads table for each vertex.
     */
    @Nonnull
    protected final int[] arrowOffsets;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public IntImmutableDirectedGraph(IntDirectedGraph graph) {
        int arrowCount = 0;

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.arrowHeads = new int[arrowCapacity];
        this.arrowOffsets = new int[vertexCapacity];

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            arrowOffsets[vIndex] = arrowCount;
            for (int i = 0, n = graph.getNextCount(vIndex); i < n; i++) {
                arrowHeads[arrowCount] = graph.getNext(vIndex, i);
                arrowCount++;
            }
        }
    }

    /**
     * Creates a new instance from the specified graph.
     *
     * @param <V>   the vertex type
     * @param <A>   the arrow type
     * @param graph a graph
     */
    public <V, A> IntImmutableDirectedGraph(DirectedGraph<V, A> graph) {

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.arrowHeads = new int[arrowCapacity];
        this.arrowOffsets = new int[vertexCapacity];

        Map<V, Integer> vertexToIndexMap = new HashMap<>(vertexCapacity);
        {
            int i = 0;
            for (V v : graph.getVertices()) {
                vertexToIndexMap.put(v, i);
                i++;
            }
        }

        int arrowCount = 0;
        {
            int i = 0;
            for (V v : graph.getVertices()) {

                arrowOffsets[i] = arrowCount;
                for (int j = 0, n = graph.getNextCount(v); j < n; j++) {
                    arrowHeads[arrowCount] = vertexToIndexMap.get(graph.getNext(v, j));
                    arrowCount++;
                }
                i++;
            }
        }
    }


    @Override
    public int getArrowCount() {
        return arrowHeads.length;
    }

    @Override
    public int getNext(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNext(vi, i));
        }
        return arrowHeads[arrowOffsets[vi] + i];
    }

    @Override
    public int getNextCount(int vi) {
        final int vertexCount = getVertexCount();
        if (vi < 0 || vi >= vertexCount) {
            throw new IllegalArgumentException("vi(" + vi + ") < 0 || vi >= " + vertexCount);
        }
        final int offset = arrowOffsets[vi];
        final int nextOffset = (vi == vertexCount - 1) ? arrowHeads.length : arrowOffsets[vi + 1];
        return nextOffset - offset;
    }

    @Override
    public int getVertexCount() {
        return arrowOffsets.length;
    }
}
