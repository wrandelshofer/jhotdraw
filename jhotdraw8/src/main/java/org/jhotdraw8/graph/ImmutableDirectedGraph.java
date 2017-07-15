/* @(#)ImmutableDirectedGraph.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.graph.ReferenceToIntDirectedGraphMixin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * ImmutableDirectedGraph.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ImmutableDirectedGraph<V> extends ImmutableIntDirectedGraph
        implements ReferenceToIntDirectedGraphMixin<V> {

    /**
     * Maps a vertex to a vertex index.
     */
    private final Map<V, Integer> vertexMap;

    /**
     * Table of vertices.
     */
    private final List<V> vertices;

    public ImmutableDirectedGraph(int vertexCapacity, int edgeCapacity) {
        super(vertexCapacity, edgeCapacity);
        this.vertexMap = new HashMap<>(vertexCapacity + vertexCapacity * 40 / 100, 0.75f);
        this.vertices = new ArrayList<>(vertexCapacity);
    }

    /**
     * Builder-method: adds a vertex.
     *
     * @param v vertex
     */
    void buildAddVertex(V v) {
        vertexMap.put(v, vertices.size());
        vertices.add(v);
    }

    /**
     * Builder-method: adds an adjacent edge.
     *
     * @param va vertex a
     * @param vb vertex b
     */
    void buildAddEdge(V va, V vb) {
        int a = vertexMap.get(va);
        int b = vertexMap.get(vb);
        buildAddEdge(a, b);
    }

    @Override
    public V getVertex(int vi) {
        return vertices.get(vi);
    }

    @Override
    public int indexOfVertex(Object v) {
        return vertexMap.get(v);
    }

  
}
