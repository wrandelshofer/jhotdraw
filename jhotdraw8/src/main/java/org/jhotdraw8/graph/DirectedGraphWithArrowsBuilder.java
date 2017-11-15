/* @(#)DirectedGraphBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * DirectedGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public class DirectedGraphWithArrowsBuilder<V, A> extends AbstractDirectedGraphBuilder
        implements DirectedGraphWithArrows<V, A>, IntDirectedGraphWithArrows<A> {

    /**
     * Maps a vertex to a vertex index.
     */
    private final Map<V, Integer> vertexMap;
    /**
     * Maps a vertex index to a vertex object.
     */
    private final List<V> vertices;

    private final List<A> arrowData;

    public DirectedGraphWithArrowsBuilder() {
        this(16, 16);
    }

    public DirectedGraphWithArrowsBuilder(int vertexCapacity, int arrowCapacity) {
        super(vertexCapacity, arrowCapacity);
        this.vertexMap = new HashMap<>(vertexCapacity);
        this.vertices = new ArrayList<>(vertexCapacity);
        this.arrowData = new ArrayList<>();
    }

    public DirectedGraphWithArrowsBuilder(DirectedGraphWithArrows<V, A> graph) {
        super(graph.getVertexCount(), graph.getArrowCount());
        final int vcount = graph.getVertexCount();
        this.vertexMap = new HashMap<>(vcount);
        this.vertices = new ArrayList<>(vcount);
        final int ecount = graph.getArrowCount();
        this.arrowData = new ArrayList<>(ecount);

        for (int i = 0; i < vcount; i++) {
            addVertex(graph.getVertex(i));
        }
        for (int i = 0; i < vcount; i++) {
            V v = graph.getVertex(i);
            for (int j = 0, n = graph.getNextCount(v); j < n; j++) {
                addArrow(v, graph.getNext(v, j), graph.getArrow(v, j));
            }
        }
    }

    /**
     * Builder-method: adds a directed arrow (arrow from va to vb).
     *
     * @param va vertex a
     * @param vb vertex b
     * @param arrow the arrow
     */
    public void addArrow(@Nonnull V va, @Nonnull V vb, @Nonnull A arrow) {
        if (va == null) {
            throw new IllegalArgumentException("va=null");
        }
        if (vb == null) {
            throw new IllegalArgumentException("vb=null");
        }
        int a = vertexMap.get(va);
        int b = vertexMap.get(vb);
        buildAddArrow(a, b);

        arrowData.add(arrow);
    }

    /**
     * Builder-method: adds two arrows (arrow from va to vb and arrow from vb to
     * va).
     *
     * @param va vertex a
     * @param vb vertex b
     * @param arrow the arrow
     */
    public void addBidiArrow(@Nonnull V va, @Nonnull V vb, @Nonnull A arrow) {
        addArrow(va, vb, arrow);
        addArrow(vb, va, arrow);
    }

    /**
     * Builder-method: adds a vertex.
     *
     * @param v vertex
     */
    public void addVertex(@Nonnull V v) {
        if (v == null) {
            throw new IllegalArgumentException("v=null");
        }
        vertexMap.computeIfAbsent(v, k -> {
            vertices.add(v);
            buildAddVertex();
            return vertices.size() - 1;
        });
    }

    @Override
    public A getArrow(int indexOfArrow) {
        return arrowData.get(indexOfArrow);
    }

    @Override
    public A getArrow(@Nonnull V vertex, int i) {
        int arrowId = getIndexOfArrow(getIndexOfVertex(vertex), i);
        return arrowData.get(arrowId);
    }

    @Override
    public A getArrow(int vertex, int index) {
        int arrowId = getIndexOfArrow(vertex, index);
        return arrowData.get(arrowId);
    }

    @Override
    @Nonnull
    public V getVertex(int vi) {
        if (vertices.get(vi) == null) {
            System.err.println("DIrectedGraphBuilder is broken");
        }
        return vertices.get(vi);
    }

    @Override
    public int getNextCount(@Nonnull V v) {
        return getNextCount(getIndexOfVertex(v));
    }

    @Override
    public V getNext(@Nonnull V v, int i) {
        return getVertex(getNext(getIndexOfVertex(v), i));
    }

    protected int getIndexOfVertex(@Nonnull V v) {
        return vertexMap.get(v);
    }

}
