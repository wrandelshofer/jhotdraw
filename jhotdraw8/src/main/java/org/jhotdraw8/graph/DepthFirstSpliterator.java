/* @(#)DepthFirstSpliterator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import static java.util.Spliterator.DISTINCT;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.ORDERED;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * DepthFirstSpliterator.
 *
 * @author Werner Randelshofer
 * @param <V> the vertex type
 */
public class DepthFirstSpliterator<V> extends AbstractSpliterator<V> {

    private final Function<V, Iterable<V>>graph;
    private final Deque<V> stack;
    private final Predicate<V> visited;

    /**
     * Creates a new instance.
     *
     * @param graph the graph
     * @param root the root vertex
     */
    public DepthFirstSpliterator(Function<V, Iterable<V>> graph, V root) {
                super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);

        if (graph == null) {
            throw new IllegalArgumentException("graph==null");
        }
        if (root == null) {
            throw new IllegalArgumentException("root==null");
        }
        this.graph = graph;
        stack = new ArrayDeque<>(16);
        Set<V> vset = new HashSet<>(16);
        visited = vset::add;
        stack.push(root);
        visited.test(root);
    }

    /**
     * Creates a new instance.
     *
     * @param graph the graph
     * @param root the root vertex
     * @param visited a predicate with side effect. The predicate returns true
     * if the specified vertex has been visited, and marks the specified vertex
     * as visited.
     */
    public DepthFirstSpliterator(Function<V, Iterable<V>> graph, V root, Predicate<V> visited) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        if (graph == null) {
            throw new IllegalArgumentException("graph==null");
        }
        if (root == null) {
            throw new IllegalArgumentException("root==null");
        }
        if (visited == null) {
            throw new IllegalArgumentException("visited==null");
        }
        this.graph = graph;
        stack = new ArrayDeque<>(16);
        this.visited = visited;
        stack.push(root);
        visited.test(root);
    }


    @Override
    public boolean tryAdvance(Consumer<? super V> action) {
        V current = stack.pop();
        if (current == null) {
            return false;
        }
        for (V next : graph.apply(current)) {
            if (visited.test(next)) {
                stack.push(next);
            }
        }
        action.accept(current);
        return true;
    }
}
