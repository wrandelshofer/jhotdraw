/*
 * @(#)ImmutableBidiGraph.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;

public class ImmutableBidiGraph<V, A> extends ImmutableDirectedGraph<V, A>
        implements BidiGraph<V, A>, AttributedIntBidiGraph<V, A> {
    /**
     * Holds the indices to the prev vertices.
     */
    protected final @NonNull int[] prev;

    /**
     * Holds offsets into the {@link #prev} table for each vertex.
     */
    protected final @NonNull int[] prevOffsets;

    /**
     * Holds the arrow objects.
     */
    protected final @NonNull A[] prevArrows;

    public ImmutableBidiGraph(@NonNull BidiGraph<V, A> graph) {
        super(graph);

        this.prev = new int[next.length];
        this.prevOffsets = new int[nextOffsets.length];
        @SuppressWarnings("unchecked")
        A[] uncheckedArrows = (A[]) new Object[prev.length];
        this.prevArrows = uncheckedArrows;

        int arrowCount = 0;
        {
            int i = 0;
            for (V v : graph.getVertices()) {

                nextOffsets[i] = arrowCount;
                this.vertices[i] = v;
                for (int j = 0, n = graph.getPrevCount(v); j < n; j++) {
                    prev[arrowCount] = vertexToIndexMap.get(graph.getPrev(v, j));
                    this.prevArrows[arrowCount] = graph.getPrevArrow(v, j);
                    arrowCount++;
                }
                i++;
            }
        }
    }


    @Override
    public int getPrev(int vi, int i) {
        if (i < 0 || i >= getPrevCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNextCount(vi));
        }
        return prev[prevOffsets[vi] + i];
    }

    @Override
    public @NonNull V getPrev(@NonNull V vertex, int i) {
        return vertices[getPrev(vertexToIndexMap.get(vertex), i)];
    }

    @Override
    public @NonNull A getPrevArrow(int vi, int i) {
        if (i < 0 || i >= getPrevCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getPrevCount(vi));
        }
        return prevArrows[prevOffsets[vi] + i];
    }

    @Override
    public @NonNull A getPrevArrow(@NonNull V v, int i) {
        return getPrevArrow(getVertexIndex(v), i);
    }

    @Override
    public int getPrevCount(int vi) {
        final int offset = prevOffsets[vi];
        final int nextOffset = (vi == prevOffsets.length - 1) ? prev.length : prevOffsets[vi + 1];
        return nextOffset - offset;
    }

    @Override
    public int getPrevCount(@NonNull V vertex) {
        return getPrevCount(vertexToIndexMap.get(vertex));
    }
}
