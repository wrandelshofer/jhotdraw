/* @(#)DirectedGraphBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * DirectedGraphBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public class DirectedGraphBuilder<V, A> extends AbstractDirectedGraphBuilder<A>
        implements DirectedGraph<V, A>, IntDirectedGraph<A> {

    /**
     * Maps a vertex to a vertex index.
     */
    private final Map<V, Integer> vertexMap;
    /**
     * Maps a vertex index to a vertex object.
     */
    private final List<V> vertices;

    public DirectedGraphBuilder() {
        this(16, 16);
    }

    public DirectedGraphBuilder(int vertexCapacity, int arrowCapacity) {
        super(vertexCapacity, arrowCapacity);
        this.vertexMap = new HashMap<>(vertexCapacity);
        this.vertices = new ArrayList<>(vertexCapacity);
    }

    public DirectedGraphBuilder(DirectedGraph<V, A> graph) {
        super(graph.getVertexCount(), graph.getArrowCount());
        final int vcount = graph.getVertexCount();
        this.vertexMap = new HashMap<>(vcount);
        this.vertices = new ArrayList<>(vcount);
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
        buildAddArrow(a, b,arrow);

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

    /**
     * Creates a graph with all arrows inverted.
     *
     * @param <V,A> the vertex type
     * @param graph a graph
     * @return a new graph with inverted arrows
     */
    public static <V,A> DirectedGraphBuilder<V,A> inverseOfDirectedGraph(DirectedGraph<V,A> graph) {
        final int arrowCount = graph.getArrowCount();
 
        DirectedGraphBuilder<V,A> b = new DirectedGraphBuilder<>(graph.getVertexCount(), arrowCount);
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

    public static <V,A> DirectedGraphBuilder<V,A> ofDirectedGraph(DirectedGraph<V,A> model) {
        DirectedGraphBuilder<V,A> b = new DirectedGraphBuilder<>();
        for (int i = 0, n = model.getVertexCount(); i < n; i++) {
            V v = model.getVertex(i);
            b.addVertex(v);
            for (int j = 0, m = model.getNextCount(v); j < m; j++) {
                b.addArrow(v, model.getNext(v, j),model.getArrow(v,j));
            }
        }
        return b;
    }

    /**
     * Creates a builder which contains the specified vertices, and only arrows
     * from the directed graph, for the specified vertices.
     *
     * @param <V,A> the vertex type
     * @param model a graph
     * @param vertices a set of vertices
     * @return a subset of the directed graph
     */
    public static <V,A> DirectedGraphBuilder<V,A> subsetOfDirectedGraph(DirectedGraph<V,A> model, Set<V> vertices) {
        DirectedGraphBuilder<V,A> b = new DirectedGraphBuilder<>();
        for (V v : vertices) {
            b.addVertex(v);
        }
        for (V v : vertices) {
            for (int j = 0, m = model.getNextCount(v); j < m; j++) {
                final V u = model.getNext(v, j);
                if (vertices.contains(u)) {
                    b.addArrow(v, u,model.getArrow(v,j));
                }
            }
        }
        return b;
    }
    
    
    public DirectedGraph<V,A> build() {
        final ImmutableDirectedGraph<V,A> graph = new ImmutableDirectedGraph<>(this);
        return graph;
    }
    
    @Override
    public A getArrow(V vertex, int index) {
        int arrowId = getArrowIndex(getVertexIndex(vertex), index);
        return getArrow(arrowId);
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
        return getNextCount(getVertexIndex(v));
    }

    @Override
    public V getNext(@Nonnull V v, int i) {
        return getVertex(getNext(getVertexIndex(v), i));
    }

    protected int getVertexIndex(@Nonnull V v) {
        return vertexMap.get(v);
    }
    
    
}
