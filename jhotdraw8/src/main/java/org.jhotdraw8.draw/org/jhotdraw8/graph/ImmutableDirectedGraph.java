/*
 * @(#)ImmutableDirectedGraph.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * ImmutableIntDirectedGraph.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 */
public class ImmutableDirectedGraph<V, A> implements AttributedIntDirectedGraph<V, A>, DirectedGraph<V, A> {

    /**
     * Holds the indices of the vertices at the arrow heads.
     */
    @NonNull
    protected final int[] arrowHeads;

    /**
     * Holds offsets into the nextArrowHeads table for each vertex.
     */
    @NonNull
    protected final int[] vertices;

    /**
     * Holds the arrows.
     */
    @NonNull
    protected final A[] arrows;
    /**
     * Holds the vertices.
     */
    @NonNull
    protected final V[] vertexObjects;
    /**
     * Holds the indices.
     */
    @NonNull
    protected final Map<V, Integer> vertexToIndexMap;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public ImmutableDirectedGraph(@NonNull AttributedIntDirectedGraph<V, A> graph) {
        int arrowCount = 0;

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.arrowHeads = new int[arrowCapacity];

        @SuppressWarnings("unchecked")
        A[] uncheckedArrows = (A[]) new Object[arrowCapacity];
        this.arrows = uncheckedArrows;
        this.vertices = new int[vertexCapacity];
        @SuppressWarnings("unchecked")
        V[] uncheckedVertices = (V[]) new Object[vertexCapacity];
        this.vertexObjects = uncheckedVertices;
        this.vertexToIndexMap = new HashMap<>(vertexCapacity);

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            vertices[vIndex] = arrowCount;
            V vertex = graph.getVertex(vIndex);
            this.vertexObjects[vIndex] = vertex;
            vertexToIndexMap.put(vertex, vIndex);
            for (int i = 0, n = graph.getNextCount(vIndex); i < n; i++) {
                arrowHeads[arrowCount] = graph.getNext(vIndex, i);
                this.arrows[arrowCount] = graph.getNextArrow(vIndex, i);
                arrowCount++;
            }
        }
    }

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public ImmutableDirectedGraph(@NonNull DirectedGraph<V, A> graph) {

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.arrowHeads = new int[arrowCapacity];
        @SuppressWarnings("unchecked")
        A[] uncheckedArrows = (A[]) new Object[arrowCapacity];
        this.arrows = uncheckedArrows;
        this.vertices = new int[vertexCapacity];
        @SuppressWarnings("unchecked")
        V[] uncheckedVertices = (V[]) new Object[vertexCapacity];
        this.vertexObjects = uncheckedVertices;
        this.vertexToIndexMap = new HashMap<>(vertexCapacity);

        //    Map<V, Integer> vertexToIndexMap = new HashMap<>(vertexCapacity);
        {
            int i = 0;
            for (V v : graph.getVertices()) {
                vertexToIndexMap.put(v, i);
                i++;
            }
        }

        int arrowCount = 0;
        {
            int i = 0;
            for (V v : graph.getVertices()) {

                vertices[i] = arrowCount;
                this.vertexObjects[i] = v;
                for (int j = 0, n = graph.getNextCount(v); j < n; j++) {
                    arrowHeads[arrowCount] = vertexToIndexMap.get(graph.getNext(v, j));
                    this.arrows[arrowCount] = graph.getNextArrow(v, j);
                    arrowCount++;
                }
                i++;
            }
        }
    }


    @NonNull
    @Override
    public A getArrow(int index) {
        return arrows[index];
    }

    @NonNull
    @Override
    public A getNextArrow(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNextCount(vi));
        }
        return arrows[vertices[vi] + i];
    }

    @NonNull
    @Override
    public A getNextArrow(@NonNull V v, int i) {
        return getNextArrow(getVertexIndex(v), i);
    }

    @Override
    public int getArrowCount() {
        return arrowHeads.length;
    }

    @Override
    public int getNext(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNextCount(vi));
        }
        return arrowHeads[vertices[vi] + i];
    }

    @NonNull
    @Override
    public V getNext(@NonNull V vertex, int i) {
        return vertexObjects[getNext(vertexToIndexMap.get(vertex), i)];
    }

    protected int getArrowIndex(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNextCount(vi));
        }
        return vertices[vi] + i;
    }

    @Override
    public int getNextCount(int vi) {
        final int offset = vertices[vi];
        final int nextOffset = (vi == vertices.length - 1) ? arrowHeads.length : vertices[vi + 1];
        return nextOffset - offset;
    }

    @Override
    public int getVertexCount() {
        return vertices.length;
    }

    @NonNull
    @Override
    public V getVertex(int index) {
        return vertexObjects[index];
    }

    @Override
    public int getVertexIndex(V vertex) {
        Integer index = vertexToIndexMap.get(vertex);
        return index == null ? -1 : index;
    }


    @Override
    public int getNextCount(@NonNull V vertex) {
        return getNextCount(vertexToIndexMap.get(vertex));
    }

    @NonNull
    @Override
    public Collection<V> getVertices() {
        return Arrays.asList(vertexObjects);

    }

    @Override
    public @NonNull Collection<A> getArrows() {
        return Arrays.asList(arrows);
    }


    @NonNull
    public A getArrow(int vertex, int index) {
        return arrows[getArrowIndex(vertex, index)];
    }

}
