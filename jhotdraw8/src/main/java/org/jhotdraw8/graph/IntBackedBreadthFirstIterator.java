/* @(#)BreadthFirstIterator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.function.IntFunction;

/**
 * BreadthFirstIterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <V> the vertex type
 */
public class IntBackedBreadthFirstIterator<V> implements Iterator<V> {

    private final IntDirectedGraph graph;
    private final IntFunction<V> intToV;
    private final Queue<Integer> queue;// FIXME should be ArrayQueueInt.
    private final BitSet visited;

    public IntBackedBreadthFirstIterator(IntDirectedGraph graph, IntFunction<V> intToV, int root) {
        this.graph = graph;
        this.intToV=intToV;
        queue = new ArrayDeque<>(graph.getArrowCount());
        visited = new BitSet(graph.getVertexCount());
        queue.add(root);
        visited.set(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public V next() {
        Integer current = queue.remove();
        for (int i=0,n=graph.getNextCount(current);i<n;i++) {
            int next=graph.getNext(current, i);
            if (!visited.get(next)) {
                visited.set(next);
                queue.add(next);
            }
        }
        return intToV.apply(current);
    }

}
