/*
 * @(#)UniqueShortestPathBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;
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
    public UniqueShortestPathBuilder(@Nonnull DirectedGraph<V, A> graph, @Nonnull ToDoubleFunction<A> costf) {
        super(graph, costf);
    }

    public UniqueShortestPathBuilder(@Nonnull DirectedGraph<V, A> graph, @Nonnull ToDoubleTriFunction<V, V, A> costf) {
        super(graph, costf);
    }

    public UniqueShortestPathBuilder(@Nonnull Function<V, Iterable<Arc<V, A>>> nextNodesFunction, @Nonnull ToDoubleFunction<A> costf) {
        super(nextNodesFunction, costf);
    }

    public UniqueShortestPathBuilder(@Nonnull Function<V, Iterable<Arc<V, A>>> nextNodesFunction, @Nonnull ToDoubleTriFunction<V, V, A> costf) {
        super(nextNodesFunction, costf);
    }

    protected BackLink<V, A> search(@Nonnull V start,
                                    @Nonnull Predicate<V> goalPredicate,
                                    double maxCost,
                                    Function<V, Iterable<Arc<V, A>>> nextf,
                                    ToDoubleTriFunction<V, V, A> costf) {

        // Priority queue: back-links with shortest distance from start come first.
        PriorityQueue<MyBackLink<V, A>> queue = new PriorityQueue<>();

        // Map with numbers of paths to a vertex
        Map<V, Integer> numPathsMap = new HashMap<>();

        // Map with known costs from start. If an entry is missing, we assume infinity.
        Map<V, Double> cost = new HashMap<>();
        ToDoubleFunction<V> getCost = v -> cost.computeIfAbsent(v, k -> Double.POSITIVE_INFINITY);

        // Insert start itself in priority queue and initialize its cost as 0 and numpaths with 1.
        queue.add(new MyBackLink<>(start, 0.0, null, null));
        numPathsMap.put(start, 1);
        cost.put(start, 0.0);

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
            double ucost = node.cost;

            for (Arc<V, A> entry : nextf.apply(u)) {
                V v = entry.getEnd();
                A a = entry.getArrow();
                double weight = costf.applyAsDouble(u, v, a);
                double oldvcost = getCost.applyAsDouble(v);
                double newvcost = ucost + weight;

                // If there is a shorter path to v through u.
                if (newvcost < oldvcost && newvcost <= maxCost) {
                    // Update cost of v.
                    cost.put(v, newvcost);
                    MyBackLink<V, A> e = new MyBackLink<>(v, newvcost, node, a);
                    queue.add(e);
                    // Update num paths to v
                    numPathsMap.computeIfAbsent(v, k -> numPathsMap.get(u));
                } else if (newvcost == oldvcost) {
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

        @Override
        public String toString() {
            return "BackLink{" +
                    "vertex=" + vertex +
                    ", cost=" + cost +

                    '}';
        }
    }

}
