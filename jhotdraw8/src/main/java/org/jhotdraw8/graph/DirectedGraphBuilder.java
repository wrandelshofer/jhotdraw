/* @(#)DirectedGraphBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


/**
 * DirectedGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public class DirectedGraphBuilder<V, A> extends AbstractDirectedGraphBuilder
        implements DirectedGraph<V, A>, AttributedIntDirectedGraph<V, A> {

    /**
     * Creates a builder which contains a copy of the specified graph with all arrows inverted.
     *
     * @param <V> the vertex type
     * @param <A> the arrow type
     * @param graph a graph
     * @return a new graph with inverted arrows
     */
    public static <V, A> DirectedGraphBuilder<V, A> inverseOfDirectedGraph(DirectedGraph<V, A> graph) {
        final int arrowCount = graph.getArrowCount();

        DirectedGraphBuilder<V, A> b = new DirectedGraphBuilder<>(graph.getVertexCount(), arrowCount);
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            V v = graph.getVertex(i);
            b.addVertex(v);
        }
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            V v = graph.getVertex(i);
            for (int j = 0, m = graph.getNextCount(v); j < m; j++) {
                b.addArrow(graph.getNext(v, j), v, graph.getArrow(v, j));
            }
        }
        return b;
    }

    /**
     * Creates a builder which contains a copy of the specified graph.
     *
     * @param <V> the vertex type
     * @param <A> the arrow type
     * @param graph a graph
     * @return a new graph
     */
    public static <V, A> DirectedGraphBuilder<V, A> ofDirectedGraph(DirectedGraph<V, A> graph) {
        DirectedGraphBuilder<V, A> b = new DirectedGraphBuilder<>();
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            V v = graph.getVertex(i);
            b.addVertex(v);
            for (int j = 0, m = graph.getNextCount(v); j < m; j++) {
                b.addArrow(v, graph.getNext(v, j), graph.getArrow(v, j));
            }
        }
        return b;
    }

    /**
     * Creates a builder which contains the specified vertices, and only arrows
     * from the directed graph, for the specified vertices.
     *
     * @param <V> the vertex type
     * @param <A> the arrow type
     * @param graph a graph
     * @param vertexPredicate a predicate for the vertices
     * @return a subset of the directed graph
     */
    public static <V, A> DirectedGraphBuilder<V, A> subsetOfDirectedGraph(DirectedGraph<V, A> graph, Predicate<V> vertexPredicate) {
        DirectedGraphBuilder<V, A> b = new DirectedGraphBuilder<>();
        for (V v : graph.getVertices()) {
            if (vertexPredicate.test(v)) 
            b.addVertex(v);
        }
        for (V v : graph.getVertices()) {
            for (int j = 0, m = graph.getNextCount(v); j < m; j++) {
                final V u = graph.getNext(v, j);
                if (vertexPredicate.test(u)) {
                    b.addArrow(v, u, graph.getArrow(v, j));
                }
            }
        }
        return b;
    }

    /**
     * Maps a vertex to a vertex index.
     */
    private final Map<V, Integer> vertexMap;
    /**
     * Maps a vertex index to a vertex object.
     */
    private final List<V> vertices;
    /**
     * Maps an arrow index to an arrow object.
     */
    private final List<A> arrows;

    /** Creates a new instance with an initial capacity for 16 vertices and 16 arrows.
     */
    public DirectedGraphBuilder() {
        this(16, 16);
    }

    /** Creates a new instance with the specified initial capacities.
     * @param vertexCapacity the initial capacity for vertices
     * @param arrowCapacity the initial capacity for arrows
     */
    public DirectedGraphBuilder(int vertexCapacity, int arrowCapacity) {
        super(vertexCapacity, arrowCapacity);
        this.vertexMap = new HashMap<>(vertexCapacity);
        this.vertices = new ArrayList<>(vertexCapacity);
        this.arrows = new ArrayList<>(arrowCapacity);
    }

    /**
     * Creates a new instance which contains a copy of the specified graph.
     * 
     * @param graph a graph
     */
    public DirectedGraphBuilder(DirectedGraph<V, A> graph) {
        super(graph.getVertexCount(), graph.getArrowCount());
        final int vcount = graph.getVertexCount();
        this.vertexMap = new HashMap<>(vcount);
        this.vertices = new ArrayList<>(vcount);
        this.arrows = new ArrayList<>(graph.getArrowCount());
        final int ecount = graph.getArrowCount();

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
     * Adds a directed arrow from va to vb.
     *
     * @param va vertex a
     * @param vb vertex b
     * @param arrow the arrow
     */
    public void addArrow(V va, V vb, A arrow) {
        if (va == null) {
            throw new IllegalArgumentException("va=null");
        }
        if (vb == null) {
            throw new IllegalArgumentException("vb=null");
        }
        int a = vertexMap.get(va);
        int b = vertexMap.get(vb);
        super.buildAddArrow(a, b);
        arrows.add(arrow);
    }

    /**
     * Adds an arrow from 'va' to 'vb' and an arrow from 'vb' to 'va'.
     *
     * @param va vertex a
     * @param vb vertex b
     * @param arrow the arrow
     */
    public void addBidiArrow(V va, V vb, A arrow) {
        addArrow(va, vb, arrow);
        addArrow(vb, va, arrow);
    }

    /**
     * Adds a vertex.
     *
     * @param v vertex
     */
    public void addVertex(V v) {
        if (v == null) {
            throw new IllegalArgumentException("v=null");
        }
        vertexMap.computeIfAbsent(v, k -> {
            vertices.add(v);
            buildAddVertex();
            return vertices.size() - 1;
        });
    }

    /**
     * Builds an ImmutableDirectedGraph from this builder.
     * 
     * @return the created graph
     */
    public ImmutableDirectedGraph<V, A> build() {
        final ImmutableDirectedGraph<V, A> graph = new ImmutableDirectedGraph<>(this);
        return graph;
    }

    @Override
    public void clear() {
        super.clear();
        vertexMap.clear();
        vertices.clear();
        arrows.clear();
    }

    @Override
    public A getArrow(V vertex, int index) {
        int arrowId = getArrowIndex(getVertexIndex(vertex), index);
        return getArrow(arrowId);
    }

    @Override
    public V getNext(V v, int i) {
        return getVertex(getNext(getVertexIndex(v), i));
    }

    @Override
    public int getNextCount(V v) {
        return getNextCount(getVertexIndex(v));
    }

    @Override
    public V getVertex(int vi) {
        if (vertices.get(vi) == null) {
            System.err.println("DIrectedGraphBuilder is broken");
        }
        return vertices.get(vi);
    }

    protected int getVertexIndex(V v) {
        Integer index = vertexMap.get(v);
        return (int) index;
    }

    @SuppressWarnings("unchecked")
    public A getArrow(int index) {
        return arrows.get(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public A getArrow(int vi, int i) {
        int arrowId = getArrowIndex(vi, i);
        return arrows.get(arrowId);
    }

}
