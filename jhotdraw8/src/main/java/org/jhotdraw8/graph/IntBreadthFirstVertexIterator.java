/* @(#)BreadthFirstIterator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.BitSet;
import java.util.Iterator;
import java.util.function.IntPredicate;
import org.jhotdraw8.collection.IntArrayDeque;

/**
 * IntBreadthFirstVertexIterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class IntBreadthFirstVertexIterator implements Iterator<Integer> {

    private final IntDirectedGraph graph;
    private final IntArrayDeque queue;
    private final IntPredicate visited;

    public IntBreadthFirstVertexIterator(IntDirectedGraph graph, int root) {
        this.graph = graph;
        queue = new IntArrayDeque(16);
        queue.addLast(root);

        BitSet vset = new BitSet(graph.getVertexCount());
        visited = next -> {
            if (!vset.get(next)) {
                vset.set(next);
                return true;
            } else {
                return false;
            }
        };
        visited.test(root);
    }

    public IntBreadthFirstVertexIterator(IntDirectedGraph graph, int root, IntPredicate visited) {
        this.graph = graph;
        queue = new IntArrayDeque(16);
        queue.addLast(root);
        this.visited = visited;
        visited.test(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public Integer next() {
        Integer current = queue.removeFirst();
        for (int i = 0, n = graph.getNextCount(current); i < n; i++) {
            int next = graph.getNext(current, i);
            if (!visited.test(next)) {
                queue.addLast(next);
            }
        }
        return current;
    }

}
