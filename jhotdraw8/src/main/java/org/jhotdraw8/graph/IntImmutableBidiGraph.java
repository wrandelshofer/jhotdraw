/* @(#)IntImmutableBidiGraph.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

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
    protected final int[] nextArrowHeads;

    /**
     * Holds offsets into the nextArrowHeads table for each vertex.
     */
    protected final int[] nextArrows;
    /**
     * Holds the arrow heads.
     */
    protected final int[] prevArrowHeads;

    /**
     * Holds offsets into the nextArrowHeads table for each vertex.
     */
    protected final int[] prevArrows;

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

        this.nextArrowHeads = new int[arrowCapacity ];
        this.nextArrows = new int[vertexCapacity ];
        this.prevArrowHeads = new int[arrowCapacity ];
        this.prevArrows = new int[vertexCapacity];

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            nextArrows[vIndex] = nextArrowCount;
            for (int i = 0, n = graph.getNextCount(vIndex); i < n; i++) {
                nextArrowHeads[nextArrowCount] = graph.getNext(vIndex, i);
                nextArrowCount++;
            }
            prevArrows[vIndex] = prevArrowCount;
            for (int i = 0, n = graph.getPrevCount(vIndex); i < n; i++) {
                prevArrowHeads[prevArrowCount] = graph.getPrev(vIndex, i);
                prevArrowCount++;
            }
        }
    }

    /**
     * Creates a new instance from the specified graph.
     *
     * @param <V> the vertex type
     * @param <A> the arrow type
     * @param graph a graph
     */
    public <V, A> IntImmutableBidiGraph(BidiGraph<V, A> graph) {

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.nextArrowHeads = new int[arrowCapacity ];
        this.nextArrows = new int[vertexCapacity ];
        this.prevArrowHeads = new int[arrowCapacity ];
        this.prevArrows = new int[vertexCapacity ];

        Map<V, Integer> vertexToIndexMap = new HashMap<>(vertexCapacity);
        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = graph.getVertex(vIndex);
            vertexToIndexMap.put(vObject, vIndex);
        }

        int prevArrowCount = 0;
        int nextArrowCount = 0;
        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = graph.getVertex(vIndex);

            nextArrows[vIndex] = nextArrowCount;
            for (int i = 0, n = graph.getNextCount(vObject); i < n; i++) {
                nextArrowHeads[nextArrowCount] = vertexToIndexMap.get(graph.getNext(vObject, i));
                nextArrowCount++;
            }
            prevArrows[vIndex] = prevArrowCount;
            for (int i = 0, n = graph.getPrevCount(vObject); i < n; i++) {
                prevArrowHeads[prevArrowCount] = vertexToIndexMap.get(graph.getPrev(vObject, i));
                prevArrowCount++;
            }
        }
    }

    @Override
    public int getArrowCount() {
        return nextArrowHeads.length;
    }

    @Override
    public int getNext(int vi, int i) {
        return getNextPrev(vi,i,nextArrows,nextArrowHeads);
    }
    @Override
    public int getPrev(int vi, int i) {
        return getNextPrev(vi,i,prevArrows,prevArrowHeads);
    }
       private int getNextPrev(int vi, int i, int[] arrows, int[] arrowHeads) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNext(vi, i));
        }
        return arrowHeads[arrows[vi] + i];
    }

    @Override
    public int getNextCount(int vi) {
        return getNextPrevCount(vi,nextArrows,nextArrowHeads);
    }
    @Override
    public int getPrevCount(int vi) {
        return getNextPrevCount(vi,prevArrows,prevArrowHeads);
    }
        private int getNextPrevCount(int vi, int[] arrows, int[] arrowHeads) {
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
        return nextArrows.length;
    }
}
