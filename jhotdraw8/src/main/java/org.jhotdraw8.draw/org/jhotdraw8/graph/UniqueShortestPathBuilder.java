/*
 * @(#)UniqueShortestPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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
 * This path builder can be used to find the unique shortest path between
 * to vertices in a directed graph.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 */
public class UniqueShortestPathBuilder<V, A> extends AbstractShortestPathBuilder<V, A> {
    public UniqueShortestPathBuilder(@NonNull DirectedGraph<V, A> graph, @NonNull ToDoubleFunction<A> costf) {
        super(graph, costf);
    }

    public UniqueShortestPathBuilder(@NonNull DirectedGraph<V, A> graph, @NonNull ToDoubleTriFunction<V, V, A> costf) {
        super(graph, costf);
    }

    public UniqueShortestPathBuilder(@NonNull Function<V, Iterable<Arc<V, A>>> nextNodesFunction, @NonNull ToDoubleFunction<A> costf) {
        super(nextNodesFunction, costf);
    }

    public UniqueShortestPathBuilder(@NonNull Function<V, Iterable<Arc<V, A>>> nextNodesFunction, @NonNull ToDoubleTriFunction<V, V, A> costf) {
        super(nextNodesFunction, costf);
    }

    @Nullable
    protected BackLink<V, A> search(@NonNull V start,
                                    @NonNull Predicate<V> goalPredicate,
                                    double maxCost,
                                    @NonNull Function<V, Iterable<Arc<V, A>>> nextf,
                                    @NonNull ToDoubleTriFunction<V, V, A> costf) {

        // Priority queue: back-links with shortest distance from start come first.
        PriorityQueue<MyBackLink<V, A>> queue = new PriorityQueue<>();

        // Map with numbers of paths to a vertex
        Map<V, Integer> numPathsMap = new HashMap<>();

        // Map with best known costs from start to a vertex. If an entry is missing, we assume infinity.
        Map<V, Double> costMap = new HashMap<>();

        // Insert start itself in priority queue and initialize its cost as 0,
        // and number of paths with 1.
        queue.add(new MyBackLink<>(start, 0.0, null, null));
        numPathsMap.put(start, 1);
        costMap.put(start, 0.0);

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
            double costToU = node.cost;

            for (Arc<V, A> entry : nextf.apply(u)) {
                V v = entry.getEnd();
                A a = entry.getData();
                double bestKnownCost = costMap.getOrDefault(v, Double.POSITIVE_INFINITY);
                double costThroughU = costToU + costf.applyAsDouble(u, v, a);

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
        @Nullable
        protected MyBackLink<VV, AA> parent;
        protected AA arrow;
        /**
         * Accumulated cost up to this node.
         * Must increase monotonically.
         */
        protected double cost;

        public MyBackLink(VV node, double cost, MyBackLink<VV, AA> parent, AA arrow) {
            this.vertex = node;
            this.cost = cost;
            this.parent = parent;
            this.arrow = arrow;
        }

        public double getCost() {
            return cost;
        }

        @Override
        public long getCostLong() {
            return (long) cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        @Nullable
        public MyBackLink<VV, AA> getParent() {
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

        @NonNull
        @Override
        public String toString() {
            return "BackLink{" +
                    "vertex=" + vertex +
                    ", cost=" + cost +

                    '}';
        }
    }

}
