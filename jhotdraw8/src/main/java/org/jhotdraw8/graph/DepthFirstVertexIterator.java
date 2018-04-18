/* @(#)DepthFirstVertexIterator.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;

/**
 * DepthFirstVertexIterator.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 */
public class DepthFirstVertexIterator<V> implements Iterator<V> {

    private final static Object NEEDS_LOOKAHEAD = new Object();
    private final static Object SENTINEL = new Object();
    private final DirectedGraph<V, ?> graph;
    private final Deque<V> stack;
    private final Predicate<V> visited;
    @SuppressWarnings("unchecked")
    private V next = (V) NEEDS_LOOKAHEAD;

    /**
     * Creates a new instance.
     *
     * @param graph the graph
     * @param root the root vertex
     */
    public DepthFirstVertexIterator(DirectedGraph<V, ?> graph, V root) {
        this.graph = graph;
        stack = new ArrayDeque<>(16);
        Set<V> vset = new HashSet<>(16);
        visited = vset::add;
        stack.push(root);
    }

    /**
     * Creates a new instance.
     *
     * @param graph the graph
     * @param root the root vertex
     * @param visited a predicate with side effect. The predicate returns true if the specified vertex has been visited, and marks
     * the specified vertex as visited.
     */
    public DepthFirstVertexIterator(DirectedGraph<V, ?> graph, V root, Predicate<V> visited) {
        this.graph = graph;
        stack = new ArrayDeque<>(16);
        this.visited = visited;
        stack.push(root);
    }

    @Override
    public boolean hasNext() {
        if (next == NEEDS_LOOKAHEAD) {
            lookahead();
        }
        return next != SENTINEL;
    }

    private void lookahead() {
        while (!stack.isEmpty()) {
            V current = stack.pop();
            if (visited.test(current)) {
                for (V v : graph.getNextVertices(current)) {
                    stack.push(v);
                }
                next = current;
                return;
            }
        }
        @SuppressWarnings("unchecked")
        V tmp = (V) SENTINEL;
        next = tmp;
    }

    @Override
    public V next() {
        if (next == NEEDS_LOOKAHEAD) {
            lookahead();
        }
        if (next == SENTINEL) {
            throw new NoSuchElementException();
        }
        V result = next;
        @SuppressWarnings("unchecked")
        V tmp = (V) NEEDS_LOOKAHEAD;
        next = tmp;
        return result;
    }

}
