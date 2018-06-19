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
     * Holds the arrow heads pointing to the previous vertex. Odd indices point
     * to next vertex, even indices to previous vertex.
     */
    protected final int[] arrowHeads;

    /**
     * Holds offsets into the arrowHeads table for each vertex. Odd indices
     * point to next vertex, even indices to previous vertex.
     */
    protected final int[] vertices;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public IntImmutableBidiGraph(IntBidiGraph graph) {
        int arrowCount = 0;

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.arrowHeads = new int[arrowCapacity * 2];
        this.vertices = new int[vertexCapacity * 2];

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            vertices[vIndex * 2] = arrowCount;
            for (int i = 0, n = graph.getNextCount(vIndex); i < n; i++) {
                arrowHeads[arrowCount * 2] = graph.getNext(vIndex, i);
                arrowCount++;
            }
            vertices[vIndex * 2 + 1] = arrowCount;
            for (int i = 0, n = graph.getPrevCount(vIndex); i < n; i++) {
                arrowHeads[arrowCount * 2 + 1] = graph.getPrev(vIndex, i);
                arrowCount++;
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

        this.arrowHeads = new int[arrowCapacity * 2];
        this.vertices = new int[vertexCapacity * 2];

        Map<V, Integer> vertexToIndexMap = new HashMap<>(vertexCapacity);
        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = graph.getVertex(vIndex);
            vertexToIndexMap.put(vObject, vIndex);
        }

        int arrowCount = 0;
        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = graph.getVertex(vIndex);

            vertices[vIndex * 2] = arrowCount;
            for (int i = 0, n = graph.getNextCount(vObject); i < n; i++) {
                arrowHeads[arrowCount * 2] = vertexToIndexMap.get(graph.getNext(vObject, i));
                arrowCount++;
            }
            vertices[vIndex * 2 + 1] = arrowCount;
            for (int i = 0, n = graph.getNextCount(vObject); i < n; i++) {
                arrowHeads[arrowCount * 2 + 1] = vertexToIndexMap.get(graph.getPrev(vObject, i));
                arrowCount++;
            }
        }
    }

    @Override
    public int getArrowCount() {
        return arrowHeads.length / 2;
    }

    @Override
    public int getNext(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNext(vi, i));
        }
        return arrowHeads[vertices[vi * 2] + i];
    }

    @Override
    public int getNextCount(int vi) {
        final int vertexCount = getVertexCount();
        if (vi < 0 || vi >= vertexCount) {
            throw new IllegalArgumentException("vi(" + vi + ") < 0 || vi >= " + vertexCount);
        }
        final int offset = vertices[vi * 2];
        final int nextOffset = (vi == vertexCount - 1) ? arrowHeads.length : vertices[vi + 1];
        return nextOffset - offset;
    }

    @Override
    public int getPrev(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNext(vi, i));
        }
        return arrowHeads[vertices[vi * 2 + 1] + i];
    }

    @Override
    public int getPrevCount(int vi) {
        final int vertexCount = getVertexCount();
        if (vi < 0 || vi >= vertexCount) {
            throw new IllegalArgumentException("vi(" + vi + ") < 0 || vi >= " + vertexCount);
        }
        final int offset = vertices[vi * 2 * 1];
        final int nextOffset = (vi == vertexCount - 1) ? arrowHeads.length : vertices[(vi + 1) * 2 + 1];
        return nextOffset - offset;
    }

    @Override
    public int getVertexCount() {
        return vertices.length / 2;
    }
}
