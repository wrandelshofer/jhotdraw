/*
 * @(#)UniqueShortestPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.ToLongTriFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

/**
 * This path builder can be used to find the unique shortest path between
 * to vertices in a directed graph.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 */
public class UniqueLongShortestPathBuilder<V, A> extends AbstractLongShortestPathBuilder<V, A> {
    public UniqueLongShortestPathBuilder(@NonNull DirectedGraph<V, A> graph, @NonNull ToLongFunction<A> costf) {
        super(graph, costf);
    }

    public UniqueLongShortestPathBuilder(@NonNull DirectedGraph<V, A> graph, @NonNull ToLongTriFunction<V, V, A> costf) {
        super(graph, costf);
    }

    public UniqueLongShortestPathBuilder(@NonNull Function<V, Iterable<Arc<V, A>>> nextNodesFunction, @NonNull ToLongFunction<A> costf) {
        super(nextNodesFunction, costf);
    }

    public UniqueLongShortestPathBuilder(@NonNull Function<V, Iterable<Arc<V, A>>> nextNodesFunction, @NonNull ToLongTriFunction<V, V, A> costf) {
        super(nextNodesFunction, costf);
    }

    protected @Nullable BackLink<V, A> search(@NonNull V start,
                                              @NonNull Predicate<V> goalPredicate,
                                              long maxCost,
                                              @NonNull Function<V, Iterable<Arc<V, A>>> nextf,
                                              @NonNull ToLongTriFunction<V, V, A> costf) {

        // Priority queue: back-links with shortest distance from start come first.
        PriorityQueue<MyBackLink<V, A>> queue = new PriorityQueue<>();

        // Map with numbers of paths to a vertex
        Map<V, Integer> numPathsMap = new HashMap<>();

        // Map with best known costs from start to a vertex. If an entry is missing, we assume infinity.
        Map<V, Long> costMap = new HashMap<>();

        // Insert start itself in priority queue and initialize its cost as 0,
        // and number of paths with 1.
        queue.add(new MyBackLink<>(start, 0L, null, null));
        numPathsMap.put(start, 1);
        costMap.put(start, 0L);

        // Loop until we have reached the goal, or queue is exhausted.
        MyBackLink<V, A> found = null;
        while (!queue.isEmpty()) {
            MyBackLink<V, A> node = queue.remove();
            final V u = node.vertex;
            if (goalPredicate.test(u)) {
                if (found == null) {
                    found = node;
                    maxCost = node.cost;
                } else if (node.cost == maxCost) {
                    return null;
                }
            }
            long costToU = node.cost;

            for (Arc<V, A> entry : nextf.apply(u)) {
                V v = entry.getEnd();
                A a = entry.getData();
                long bestKnownCost = costMap.getOrDefault(v, Long.MAX_VALUE);
                long costThroughU = costToU + costf.applyAsLong(u, v, a);

                // If there is a shorter path to v through u.
                if (costThroughU < bestKnownCost && costThroughU <= maxCost) {
                    // Update cost to v, and number of paths to v.
                    costMap.put(v, costThroughU);
                    queue.add(new MyBackLink<>(v, costThroughU, node, a));
                    numPathsMap.put(v, numPathsMap.get(u));
                } else if (costThroughU == bestKnownCost) {
                    // Path to v is not unique
                    numPathsMap.merge(v, 1, Integer::sum);
                }
            }
        }

        return found != null && numPathsMap.get(found.vertex) == 1 ? found : null;
    }


    protected static class MyBackLink<VV, AA> extends BackLink<VV, AA> {

        protected final VV vertex;
        protected @Nullable MyBackLink<VV, AA> parent;
        protected AA arrow;
        /**
         * Accumulated cost up to this node.
         * Must increase monotonically.
         */
        protected long cost;

        public MyBackLink(VV node, long cost, MyBackLink<VV, AA> parent, AA arrow) {
            this.vertex = node;
            this.cost = cost;
            this.parent = parent;
            this.arrow = arrow;
        }

        public long getCost() {
            return cost;
        }

        @Override
        public long getCostLong() {
            return cost;
        }

        public void setCost(long cost) {
            this.cost = cost;
        }

        public @Nullable MyBackLink<VV, AA> getParent() {
            return parent;
        }

        public void setParent(MyBackLink<VV, AA> parent) {
            this.parent = parent;
        }

        public VV getVertex() {
            return vertex;
        }

        @Override
        public AA getArrow() {
            return arrow;
        }

        @Override
        public @NonNull String toString() {
            return "BackLink{" +
                    "vertex=" + vertex +
                    ", cost=" + cost +

                    '}';
        }
    }

}
