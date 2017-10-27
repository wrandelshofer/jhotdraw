/* @(#)BreadthFirstIterator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

/**
 * BreadthFirstIterator.
 * <p>
 * Naive implementation of a breadth first iterator which uses a hash set to
 * determine if a node has been visited.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <V> the vertex type
 */
public class BreadthFirstIterator<V> implements Iterator<V> {

    private final DirectedGraph<V> graph;
    private final Queue<V> queue;
    private final Set<V> visited;

    public BreadthFirstIterator(DirectedGraph<V> graph, V root) {
        this.graph = graph;
        queue = new ArrayDeque<>(graph.getEdgeCount());
        visited = new HashSet<>(graph.getVertexCount());
        queue.add(root);
        visited.add(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public V next() {
        V current = queue.remove();
        for (V next : graph.getNextVertices(current)) {
            if (visited.add(next)) {
                queue.add(next);
            }
        }
        return current;
    }

}
