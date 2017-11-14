/* @(#)ImmutableDirectedGraph.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

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
 * @param <E> the edge type
 */
public class ImmutableDirectedGraphWithEdges<V, E> extends ImmutableIntDirectedGraph
        implements DirectedGraphWithEdges<V, E>, IntDirectedGraphWithEdges<E> {

    /**
     * Maps a vertex index to a vertex object.
     */
    private final List<V> indexToVertexMap;
    /**
     * Maps a vertex object to a vertex index.
     */
    private final Map<V, Integer> vertexToIndexMap;

    private Object[] edgeData;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public ImmutableDirectedGraphWithEdges(DirectedGraphWithEdges<V, E> graph) {
        super(graph.getVertexCount(), graph.getEdgeCount());
        int vertexCapacity = graph.getVertexCount();

        indexToVertexMap = new ArrayList<>(vertexCapacity);
        vertexToIndexMap = new HashMap<>(vertexCapacity);

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = graph.getVertex(vIndex);
            vertexToIndexMap.put(vObject, vIndex);
            indexToVertexMap.add(vObject);
        }

        edgeData = new Object[graph.getEdgeCount()];
        int edgeCount = 0;
        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = indexToVertexMap.get(vIndex);

            vertices[vIndex] = edgeCount;
            for (int i = 0, n = graph.getNextCount(vObject); i < n; i++) {
                edges[edgeCount] = vertexToIndexMap.get(graph.getNext(vObject, i));
                edgeData[edgeCount] = graph.getNextEdge(vObject, i);
                ++edgeCount;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E getEdge(int indexOfEdge) {
        return (E) edgeData[indexOfEdge];
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
    public E getNextEdge(V vertex, int index) {
        return (E) edgeData[getEdgeIndex(vertexToIndexMap.get(vertex), index)];
    }

    @Override
    public E getNextEdge(int vertex, int index) {
        return (E) edgeData[getEdgeIndex(vertex, index)];
    }

    @Override
    public V getVertex(int indexOfVertex) {
        return indexToVertexMap.get(indexOfVertex);
    }
}
