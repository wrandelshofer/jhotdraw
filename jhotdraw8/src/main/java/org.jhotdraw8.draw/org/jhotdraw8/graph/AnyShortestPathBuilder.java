/*
 * @(#)AnyShortestPathBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.ToDoubleTriFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

/**
 * This path builder can be used to find any shortest path between
 * to vertices in a directed graph.
 * <p>
 * Uses Dijkstra's alorithm for finding the shortest path.
 * <p>
 * The provided cost function must return a positive value
 * for every arrow in the graph.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 */
public class AnyShortestPathBuilder<V, A> extends AbstractShortestPathBuilder<V, A> {
    public AnyShortestPathBuilder() {
    }

    public AnyShortestPathBuilder(@NonNull DirectedGraph<V, A> graph, @NonNull ToDoubleFunction<A> costf) {
        super(graph, costf);
    }

    public AnyShortestPathBuilder(@NonNull DirectedGraph<V, A> graph, @NonNull ToDoubleTriFunction<V, V, A> costf) {
        super(graph, costf);
    }

    public AnyShortestPathBuilder(@NonNull Function<V, Iterable<Arc<V, A>>> nextNodesFunction, @NonNull ToDoubleFunction<A> costf) {
        super(nextNodesFunction, costf);
    }

    public AnyShortestPathBuilder(@NonNull Function<V, Iterable<Arc<V, A>>> nextNodesFunction, @NonNull ToDoubleTriFunction<V, V, A> costf) {
        super(nextNodesFunction, costf);
    }

    /**
     * Searches shortest path using Dijkstra.
     * <p>
     * This algorithm does not update an entry in the priority queue.
     * Whenever a distance of a vertex is reduced, we add one more
     * entry to the priority queue. Even if there are multiple entries
     * in the priority queue, we only consider the one with the minimum
     * distance.
     * <p>
     * References: <a href="https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-using-priority_queue-stl/"
     * geeksforgeeks.org</a>
     */
    @Nullable
    protected BackLink<V, A> search(@NonNull V start,
                                    @NonNull Predicate<V> goalPredicate,
                                    double maxCost,
                                    @NonNull Function<V, Iterable<Arc<V, A>>> nextf,
                                    @NonNull ToDoubleTriFunction<V, V, A> costf) {

        // Priority queue: back-links with shortest distance from start come first.
        PriorityQueue<MyBackLink<V, A>> queue = new PriorityQueue<>();

        // Map with known costs from start. If an entry is missing, we assume infinity.
        Map<V, Double> costMap = new HashMap<>();

        // Insert start itself in priority queue and initialize its cost as 0.
        queue.add(new MyBackLink<>(start, 0.0, null, null));
        costMap.put(start, 0.0);

        // Loop until we have reached the goal, or queue is exhausted.
        while (!queue.isEmpty()) {
            MyBackLink<V, A> node = queue.remove();
            final V u = node.vertex;
            if (goalPredicate.test(u)) {
                return node;
            }
            double ucost = node.cost;

            for (Arc<V, A> entry : nextf.apply(u)) {
                V v = entry.getEnd();
                A a = entry.getData();
                double oldCost = costMap.computeIfAbsent(v, k -> Double.POSITIVE_INFINITY);
                double newCost = ucost + costf.applyAsDouble(u, v, a);

                // If there is a shorter path to v through u.
                if (newCost < oldCost && newCost <= maxCost) {
                    // Update cost of v.
                    costMap.put(v, newCost);
                    queue.add(new MyBackLink<>(v, newCost, node, a));
                }
            }
        }

        return null;
    }


    protected static class MyBackLink<VV, AA> extends BackLink<VV, AA> {

        protected final VV vertex;
        @Nullable
        protected final MyBackLink<VV, AA> parent;
        protected final AA arrow;
        protected final double cost;
        protected final int length;

        public MyBackLink(VV node, double cost, @Nullable MyBackLink<VV, AA> parent, AA arrow) {
            this.vertex = node;
            this.cost = cost;
            this.parent = parent;
            this.arrow = arrow;
            this.length = parent == null ? 0 : parent.length + 1;
        }


        public double getCost() {
            return cost;
        }

        @Nullable
        public MyBackLink<VV, AA> getParent() {
            return parent;
        }

        public VV getVertex() {
            return vertex;
        }

        @Override
        public AA getArrow() {
            return arrow;
        }

        @Override
        public int getLength() {
            return length;
        }

        @Override
        public int compareTo(@NonNull BackLink<VV, AA> that) {
            int result = Double.compare(this.getCost(), that.getCost());
            return result == 0
                    ? Integer.compare(this.length, that.getLength())
                    : result;
        }
    }

}
