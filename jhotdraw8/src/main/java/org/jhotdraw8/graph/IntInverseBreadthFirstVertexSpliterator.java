/* @(#)BreadthFirstIntegerertexIterator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import org.jhotdraw8.collection.IntArrayDeque;

/**
 * BreadthFirstIntegerertexIterator.
 * <p>
 * Naive implementation of a breadth first iterator which uses a hash set to
 * determine if a node has been visited.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class IntInverseBreadthFirstVertexSpliterator implements Spliterator.OfInt, Iterator<Integer> {

    private final IntBidiDirectedGraph graph;
    private final IntArrayDeque queue;
    private final IntPredicate visited;

    /**
     * Creates a new instance.
     *
     * @param graph the graph
     * @param root the root vertex
     */
    public IntInverseBreadthFirstVertexSpliterator(IntBidiDirectedGraph graph, int root) {
        if (graph == null) {
            throw new IllegalArgumentException("graph==null");
        }

        this.graph = graph;
        queue = new IntArrayDeque(16);
        Set<Integer> vset = new HashSet<>(16);
        visited = vset::add;
        queue.addLast(root);
        visited.test(root);
    }

    /**
     * Creates a new instance.
     *
     * @param graph the graph
     * @param root the root vertex
     * @param visited a predicate with side effect. The predicate returns true if the specified vertex has been visited, and marks
     * the specified vertex as visited.
     */
    public IntInverseBreadthFirstVertexSpliterator(IntBidiDirectedGraph graph, Integer root, IntPredicate visited) {
        if (graph == null) {
            throw new IllegalArgumentException("graph==null");
        }
        if (root == null) {
            throw new IllegalArgumentException("root==null");
        }
        this.graph = graph;
        queue = new IntArrayDeque(16);
        this.visited = visited;
        queue.addLast(root);
        visited.test(root);
    }

    /**
     * Creates a new split off from the current instance..
     */
    private IntInverseBreadthFirstVertexSpliterator(IntBidiDirectedGraph graph, IntArrayDeque queue, IntPredicate visited) {
        this.graph = graph;
        this.queue = queue;
        this.visited = visited;
    }
    @Override
    public int characteristics() {
        return ORDERED | DISTINCT | NONNULL;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public void forEachRemaining(Consumer<? super Integer> action) {
        Spliterator.OfInt.super.forEachRemaining(action);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public Integer next() {
        int current = queue.removeFirst();
        for (final PrimitiveIterator.OfInt i = graph.getPrevVertexIndicesIterator(current); i.hasNext();) {
            int next = i.nextInt();
            if (visited.test(next)) {
                queue.addLast(next);
            }
        }
        return current;
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
        if (queue.isEmpty()) {
            return false;
        }
        int current = queue.removeFirst();
        for (final PrimitiveIterator.OfInt i = graph.getPrevVertexIndicesIterator(current); i.hasNext();) {
            int next = i.nextInt();
            if (visited.test(next)) {
                queue.addLast(next);
            }
        }
        action.accept(current);
        return true;
    }

    @Override
    public Spliterator.OfInt trySplit() {
        int mid = queue.size() >>> 1;
        if (mid > 0) {
            IntArrayDeque splitQueue = new IntArrayDeque(queue.size());
            for (int i = 0; i < mid; i++) {
                splitQueue.addLast(queue.removeFirst());
            }
            return new IntInverseBreadthFirstVertexSpliterator(graph, splitQueue, visited);
        }
        return null;
    }

}
