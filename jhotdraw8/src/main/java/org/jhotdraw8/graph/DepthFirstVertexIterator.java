/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * <p>
 * Naive implementation of a depth first iterator which uses a hash set to
 * determine if a node has been visited.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
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
                for (V next : graph.getNextVertices(current)) {
                    stack.push(next);
                }
                next = current;
                return;
            }
        }
        next = (V) SENTINEL;
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
        next = (V) NEEDS_LOOKAHEAD;
        return result;
    }

}
