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
     * Holds the indices to the next vertices.
     */
    protected final @NonNull int[] next;

    /**
     * Holds offsets into the {@link #next} table for each vertex.
     */
    protected final @NonNull int[] nextOffsets;

    /**
     * Holds the arrow objects for each corresponding entry in the
     * {@link #next} array.
     */
    protected final @NonNull A[] nextArrows;
    /**
     * Holds the vertex objects.
     */
    protected final @NonNull V[] vertices;
    /**
     * Holds the indices.
     */
    protected final @NonNull Map<V, Integer> vertexToIndexMap;

    /**
     * Creates a new instance from the specified graph.
     *
     * @param graph a graph
     */
    public ImmutableDirectedGraph(@NonNull AttributedIntDirectedGraph<V, A> graph) {
        int arrowCount = 0;

        final int arrowCapacity = graph.getArrowCount();
        final int vertexCapacity = graph.getVertexCount();

        this.next = new int[arrowCapacity];

        @SuppressWarnings("unchecked")
        A[] uncheckedArrows = (A[]) new Object[arrowCapacity];
        this.nextArrows = uncheckedArrows;
        this.nextOffsets = new int[vertexCapacity];
        @SuppressWarnings("unchecked")
        V[] uncheckedVertices = (V[]) new Object[vertexCapacity];
        this.vertices = uncheckedVertices;
        this.vertexToIndexMap = new HashMap<>(vertexCapacity);

        for (int vIndex = 0; vIndex < vertexCapacity; vIndex++) {
            nextOffsets[vIndex] = arrowCount;
            V vertex = graph.getVertex(vIndex);
            this.vertices[vIndex] = vertex;
            vertexToIndexMap.put(vertex, vIndex);
            for (int i = 0, n = graph.getNextCount(vIndex); i < n; i++) {
                next[arrowCount] = graph.getNext(vIndex, i);
                this.nextArrows[arrowCount] = graph.getNextArrow(vIndex, i);
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

        this.next = new int[arrowCapacity];
        @SuppressWarnings("unchecked")
        A[] uncheckedArrows = (A[]) new Object[arrowCapacity];
        this.nextArrows = uncheckedArrows;
        this.nextOffsets = new int[vertexCapacity];
        @SuppressWarnings("unchecked")
        V[] uncheckedVertices = (V[]) new Object[vertexCapacity];
        this.vertices = uncheckedVertices;
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

                nextOffsets[i] = arrowCount;
                this.vertices[i] = v;
                for (int j = 0, n = graph.getNextCount(v); j < n; j++) {
                    next[arrowCount] = vertexToIndexMap.get(graph.getNext(v, j));
                    this.nextArrows[arrowCount] = graph.getNextArrow(v, j);
                    arrowCount++;
                }
                i++;
            }
        }
    }


    @Override
    public @NonNull A getArrow(int index) {
        return nextArrows[index];
    }

    @Override
    public @NonNull A getNextArrow(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNextCount(vi));
        }
        return nextArrows[nextOffsets[vi] + i];
    }

    @Override
    public @NonNull A getNextArrow(@NonNull V v, int i) {
        return getNextArrow(getVertexIndex(v), i);
    }

    @Override
    public int getArrowCount() {
        return next.length;
    }

    @Override
    public int getNext(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNextCount(vi));
        }
        return next[nextOffsets[vi] + i];
    }

    @Override
    public @NonNull V getNext(@NonNull V vertex, int i) {
        return vertices[getNext(vertexToIndexMap.get(vertex), i)];
    }

    protected int getArrowIndex(int vi, int i) {
        if (i < 0 || i >= getNextCount(vi)) {
            throw new IllegalArgumentException("i(" + i + ") < 0 || i >= " + getNextCount(vi));
        }
        return nextOffsets[vi] + i;
    }

    @Override
    public int getNextCount(int vi) {
        final int offset = nextOffsets[vi];
        final int nextOffset = (vi == nextOffsets.length - 1) ? next.length : nextOffsets[vi + 1];
        return nextOffset - offset;
    }

    @Override
    public int getVertexCount() {
        return nextOffsets.length;
    }

    @Override
    public @NonNull V getVertex(int index) {
        return vertices[index];
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

    @Override
    public @NonNull Collection<V> getVertices() {
        return Arrays.asList(vertices);

    }

    @Override
    public @NonNull Collection<A> getArrows() {
        return Arrays.asList(nextArrows);
    }


    public @NonNull A getArrow(int vertex, int index) {
        return nextArrows[getArrowIndex(vertex, index)];
    }

}
