/* @(#)ImmutableDirectedGraph.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ImmutableDirectedGraph.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public class ImmutableDirectedGraph<V, A> extends ImmutableAttributedIntDirectedGraph<V,A>
        implements DirectedGraph<V, A> {

    /**
     * Maps a vertex index to a vertex object.
     */
    @NonNull
    private final List<V> indexToVertexMap;
    /**
     * Maps a vertex object to a vertex index.
     */
    @NonNull
    private final Map<V, Integer> vertexToIndexMap;

    private Object[] arrowData;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public ImmutableDirectedGraph(DirectedGraph<V, A> graph) {
        super(graph.getVertexCount(), graph.getArrowCount());
        int vertexCapacity = graph.getVertexCount();

        indexToVertexMap = new ArrayList<>(vertexCapacity);
        vertexToIndexMap = new HashMap<>(vertexCapacity);

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = graph.getVertex(vIndex);
            vertexToIndexMap.put(vObject, vIndex);
            indexToVertexMap.add(vObject);
        }

        arrowData = new Object[graph.getArrowCount()];
        int arrowCount = 0;
        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = indexToVertexMap.get(vIndex);

            vertices[vIndex] = arrowCount;
            for (int i = 0, n = graph.getNextCount(vObject); i < n; i++) {
                arrowHeads[arrowCount] = vertexToIndexMap.get(graph.getNext(vObject, i));
                arrowData[arrowCount] = graph.getNextArrow(vObject, i);
                ++arrowCount;
            }
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public A getArrow(int indexOfArrow) {
        return (A) arrowData[indexOfArrow];
    }

    @Override
    public V getNext(V vertex, int i) {
        return indexToVertexMap.get(getNext(vertexToIndexMap.get(vertex), i));
    }

    @Override
    public int getNextCount(V vertex) {
        return getNextCount(vertexToIndexMap.get(vertex));
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public A getNextArrow(V vertex, int index) {
        return (A) arrowData[getArrowIndex(vertexToIndexMap.get(vertex), index)];
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public A getArrow(int vertex, int index) {
        return (A) arrowData[getArrowIndex(vertex, index)];
    }

    @Override
    public V getVertex(int indexOfVertex) {
        return indexToVertexMap.get(indexOfVertex);
    }
}
