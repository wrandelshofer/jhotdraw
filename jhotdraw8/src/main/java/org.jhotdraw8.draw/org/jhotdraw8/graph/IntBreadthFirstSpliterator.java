/*
 * @(#)IntBreadthFirstSpliterator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.IntArrayDeque;

import java.util.BitSet;
import java.util.Spliterator;
import java.util.Spliterators.AbstractIntSpliterator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

/**
 * IntBreadthFirstSpliterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class IntBreadthFirstSpliterator extends AbstractIntSpliterator {

    private final IntFunction<Spliterator.OfInt> graph;
    @NonNull
    private final IntArrayDeque queue;
    @NonNull
    private final IntPredicate visited;

    /**
     * Creates a new instance.
     *
     * @param graph the graph
     * @param root  the root vertex
     */
    public IntBreadthFirstSpliterator(IntFunction<Spliterator.OfInt> graph, int root) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        this.graph = graph;
        this.queue = new IntArrayDeque(16);
        BitSet visitedSet = new BitSet();
        this.visited = v -> {
            if (!visitedSet.get(v)) {
                visitedSet.set(v);
                return true;
            } else {
                return false;
            }
        };
        queue.addLast(root);
        visited.test(root);
    }

    @Override
    public boolean tryAdvance(@NonNull IntConsumer action) {
        if (queue.isEmpty()) {
            return false;
        }
        int current = queue.removeFirst();
        final OfInt it = graph.apply(current);
        while (it.tryAdvance((int next) -> {
            if (visited.test(next)) {
                queue.addLast(next);
            }
        })) {
            ;
        }
        action.accept(current);
        return true;
    }

}
