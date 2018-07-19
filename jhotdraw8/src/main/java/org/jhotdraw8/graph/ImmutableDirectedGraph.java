/* @(#)ImmutableDirectedGraph.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

        {
            int i = 0;
            for (V v : graph.getVertices()) {
                vertexToIndexMap.put(v, i);
                indexToVertexMap.add(v);
                i++;
            }
        }

        arrowData = new Object[graph.getArrowCount()];
        int arrowCount = 0;
        {
            int i = 0;
            for (V v : graph.getVertices()) {
            vertices[i] = arrowCount;
            for (int j = 0, n = graph.getNextCount(v); j < n; j++) {
                arrowHeads[arrowCount] = vertexToIndexMap.get(graph.getNext(v, j));
                arrowData[arrowCount] = graph.getNextArrow(v, j);
                ++arrowCount;
            }
            i++;
        }}
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

    @Override
    public Collection<V> getVertices() {
        return Collections.unmodifiableCollection(indexToVertexMap);

    }

    @Override
    public @NonNull Collection<A> getArrows() {
        return null;
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
