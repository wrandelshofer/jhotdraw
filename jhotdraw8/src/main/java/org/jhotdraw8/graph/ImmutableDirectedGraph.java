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
 */
public class ImmutableDirectedGraph<V> extends ImmutableIntDirectedGraph
        implements DirectedGraph<V> {

    /**
     * Maps a vertex index to a vertex object.
     */
    private final List<V> indexToVertexMap;
    /**
     * Maps a vertex object to a vertex index.
     */
    private final Map<V, Integer> vertexToIndexMap;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public ImmutableDirectedGraph(DirectedGraph<V> graph) {
        super(graph.getVertexCount(), graph.getEdgeCount());
        int vertexCapacity = graph.getVertexCount();

        indexToVertexMap = new ArrayList<>(vertexCapacity);
        vertexToIndexMap = new HashMap<>(vertexCapacity);

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = graph.getVertex(vIndex);
            vertexToIndexMap.put(vObject, vIndex);
            indexToVertexMap.add(vObject);
        }

        int edgeCount = 0;
        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            V vObject = indexToVertexMap.get(vIndex);

            vertices[vIndex] = edgeCount;
            for (int i = 0, n = graph.getNextCount(vObject); i < n; i++) {
                edges[edgeCount++] = vertexToIndexMap.get(graph.getNext(vObject, i));
            }
        }
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
    public V getVertex(int indexOfVertex) {
        return indexToVertexMap.get(indexOfVertex);
    }
}
