/* @(#)IntBreadthFirstSpliterator.java
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Spliterator;
import static java.util.Spliterator.DISTINCT;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.ORDERED;
import java.util.Spliterators.AbstractIntSpliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import org.jhotdraw8.collection.IntArrayDeque;

/**
 * IntBreadthFirstSpliterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class IntBreadthFirstSpliterator extends AbstractIntSpliterator implements Iterator<Integer> {

    private final IntFunction< Spliterator.OfInt> graph;
    private final IntArrayDeque queue;
    private final IntPredicate visited;

    /**
     * Creates a new instance.
     *
     * @param graph the graph
     * @param root the root vertex
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
            }else{
                return false;
            }
        };
        queue.addLast(root);
        visited.test(root);
    }

    @Override
    public void forEachRemaining(Consumer<? super Integer> action) {
        super.forEachRemaining(action);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public Integer next() {
        int current = queue.removeFirst();
        final OfInt it = graph.apply(current);
        while (it.tryAdvance((int next) -> {
            if (visited.test(next)) {
                queue.addLast(next);
            }
        }));
        return current;
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
        if (queue.isEmpty()) {
            return false;
        }
        int current = queue.removeFirst();
        final OfInt it = graph.apply(current);
        while (it.tryAdvance((int next) -> {
            if (visited.test(next)) {
                queue.addLast(next);
            }
        }));
        action.accept(current);
        return true;
    }

}
