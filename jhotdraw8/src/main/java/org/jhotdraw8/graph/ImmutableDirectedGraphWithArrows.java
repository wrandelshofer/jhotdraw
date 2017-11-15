/* @(#)ImmutableDirectedGraphWithArrows.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ImmutableDirectedGraphWithArrows.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <E> the arrow type
 */
public class ImmutableDirectedGraphWithArrows<V, E> extends ImmutableIntDirectedGraph
        implements DirectedGraphWithArrows<V, E>, IntDirectedGraphWithArrows<E> {

    /**
     * Maps a vertex index to a vertex object.
     */
    private final List<V> indexToVertexMap;
    /**
     * Maps a vertex object to a vertex index.
     */
    private final Map<V, Integer> vertexToIndexMap;

    private Object[] arrowData;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public ImmutableDirectedGraphWithArrows(DirectedGraphWithArrows<V, E> graph) {
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
                arrows[arrowCount] = vertexToIndexMap.get(graph.getNext(vObject, i));
                arrowData[arrowCount] = graph.getArrow(vObject, i);
                ++arrowCount;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E getArrow(int indexOfArrow) {
        return (E) arrowData[indexOfArrow];
    }

    @Override
    public V getNext(V vertex, int i) {
        return indexToVertexMap.get(getNext(vertexToIndexMap.get(vertex), i));
    }

    @Override
    public int getNextCount(V vertex) {
        return getNextCount(vertexToIndexMap.get(vertex));
    }

    @Override
    @SuppressWarnings("unchecked")
    public E getArrow(V vertex, int index) {
        return (E) arrowData[getArrowIndex(vertexToIndexMap.get(vertex), index)];
    }

    @Override
    @SuppressWarnings("unchecked")
    public E getArrow(int vertex, int index) {
        return (E) arrowData[getArrowIndex(vertex, index)];
    }

    @Override
    public V getVertex(int indexOfVertex) {
        return indexToVertexMap.get(indexOfVertex);
    }
}
