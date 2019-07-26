/*
 * @(#)IntImmutableBidiGraph.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

/**
 * IntImmutableBidiGraph.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntImmutableBidiGraph implements IntBidiGraph {


    /**
     * Holds the arrow heads.
     */
    @Nonnull
    protected final int[] nextArrowHeads;

    /**
     * Holds offsets into the nextArrowHeads table for each vertex.
     */
    @Nonnull
    protected final int[] nextArrowOffsets;
    /**
     * Holds the arrow heads.
     */
    @Nonnull
    protected final int[] prevArrowHeads;

    /**
     * Holds offsets into the nextArrowHeads table for each vertex.
     */
    @Nonnull
    protected final int[] prevArrowOffsets;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public IntImmutableBidiGraph(IntBidiGraph graph) {
        int nextArrowCount = 0;
        int prevArrowCount = 0;

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.nextArrowHeads = new int[arrowCapacity];
        this.nextArrowOffsets = new int[vertexCapacity];
        this.prevArrowHeads = new int[arrowCapacity];
        this.prevArrowOffsets = new int[vertexCapacity];

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            nextArrowOffsets[vIndex] = nextArrowCount;
            for (int i = 0, n = graph.getNextCount(vIndex); i < n; i++) {
                nextArrowHeads[nextArrowCount] = graph.getNext(vIndex, i);
                nextArrowCount++;
            }
            prevArrowOffsets[vIndex] = prevArrowCount;
            for (int i = 0, n = graph.getPrevCount(vIndex); i < n; i++) {
                prevArrowHeads[prevArrowCount] = graph.getPrev(vIndex, i);
                prevArrowCount++;
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
    public <V, A> IntImmutableBidiGraph(BidiGraph<V, A> graph) {

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.nextArrowHeads = new int[arrowCapacity];
        this.nextArrowOffsets = new int[vertexCapacity];
        this.prevArrowHeads = new int[arrowCapacity];
        this.prevArrowOffsets = new int[vertexCapacity];

        Map<V, Integer> vertexToIndexMap = new HashMap<>(vertexCapacity);
        {
            int i = 0;
            for (V v : graph.getVertices()) {
                vertexToIndexMap.put(v, i);
                i++;
            }
        }

        int prevArrowCount = 0;
        int nextArrowCount = 0;
        {
            int i = 0;
            for (V v : graph.getVertices()) {

                nextArrowOffsets[i] = nextArrowCount;
                for (int j = 0, n = graph.getNextCount(v); j < n; j++) {
                    nextArrowHeads[nextArrowCount] = vertexToIndexMap.get(graph.getNext(v, j));
                    nextArrowCount++;
                }
                prevArrowOffsets[i] = prevArrowCount;
                for (int j = 0, n = graph.getPrevCount(v); j < n; j++) {
                    prevArrowHeads[prevArrowCount] = vertexToIndexMap.get(graph.getPrev(v, j));
                    prevArrowCount++;
                }
                i++;
            }
        }
    }

    @Override
    public int getArrowCount() {
        return nextArrowHeads.length;
    }

    @Override
    public int getNext(int vi, int i) {
        return getNextPrev(vi, i, nextArrowOffsets, nextArrowHeads);
    }

    @Override
    public int getPrev(int vi, int i) {
        return getNextPrev(vi, i, prevArrowOffsets, prevArrowHeads);
    }

    private int getNextPrev(int vi, int i, int[] arrows, int[] arrowHeads) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNext(vi, i));
        }
        return arrowHeads[arrows[vi] + i];
    }

    @Override
    public int getNextCount(int vi) {
        return getNextPrevCount(vi, nextArrowOffsets, nextArrowHeads);
    }

    @Override
    public int getPrevCount(int vi) {
        return getNextPrevCount(vi, prevArrowOffsets, prevArrowHeads);
    }

    private int getNextPrevCount(int vi, int[] arrows, @Nonnull int[] arrowHeads) {
        final int vertexCount = getVertexCount();
        if (vi < 0 || vi >= vertexCount) {
            throw new IllegalArgumentException("vi(" + vi + ") < 0 || vi >= " + vertexCount);
        }
        final int offset = arrows[vi];
        final int nextOffset = (vi == vertexCount - 1) ? arrowHeads.length : arrows[vi + 1];
        return nextOffset - offset;
    }

    @Override
    public int getVertexCount() {
        return nextArrowOffsets.length;
    }
}
