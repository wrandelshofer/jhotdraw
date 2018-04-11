/* @(#)BreadthFirstIterator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Iterator;
import org.jhotdraw8.collection.IntArrayDeque;

/**
 * BreadthFirstIterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class IntBreadthFirstIterator implements Iterator<Integer> {

    private final IntDirectedGraph graph;
    private final IntArrayDeque queue;
    private final BitSet visited;

    public IntBreadthFirstIterator(IntDirectedGraph graph, int root) {
        this.graph = graph;
        queue = new IntArrayDeque(16);
        visited = new BitSet(graph.getVertexCount());
        queue.addLast(root);
        visited.set(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public Integer next() {
        Integer current = queue.removeFirst();
        for (int i=0,n=graph.getNextCount(current);i<n;i++) {
            int next=graph.getNext(current, i);
            if (!visited.get(next)) {
                visited.set(next);
                queue.addLast(next);
            }
        }
        return current;
    }

}
