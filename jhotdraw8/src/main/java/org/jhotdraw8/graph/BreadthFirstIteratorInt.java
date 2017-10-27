/* @(#)BreadthFirstIterator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

/**
 * BreadthFirstIterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <V> the vertex type
 */
public class BreadthFirstIteratorInt implements Iterator<Integer> {

    private final DirectedGraphInt graph;
    private final Queue<Integer> queue;// FIXME should be ArrayQueueInt.
    private final BitSet visited;

    public BreadthFirstIteratorInt(DirectedGraphInt graph, int root) {
        this.graph = graph;
        queue = new ArrayDeque<>(graph.getEdgeCount());
        visited = new BitSet(graph.getVertexCount());
        queue.add(root);
        visited.set(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public Integer next() {
        Integer current = queue.remove();
        for (int i=0,n=graph.getNextCount(current);i<n;i++) {
            int next=graph.getNext(current, i);
            if (!visited.get(next)) {
                visited.set(next);
                queue.add(next);
            }
        }
        return current;
    }

}
