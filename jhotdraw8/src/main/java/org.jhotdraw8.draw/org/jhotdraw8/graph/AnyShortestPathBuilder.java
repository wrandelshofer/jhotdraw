/* @(#)AnyShortestPathBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.ToDoubleTriFunction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

/**
 * This path builder can be used to find any shortest path between
 * to vertices in a directed.
 * <p>
 * Uses Dijkstra's alorithm for finding the shortest path.
 * <p>
 * The provided cost function must return a value greater than zero
 * for every edge in the graph.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AnyShortestPathBuilder<V, A> extends AbstractShortestPathBuilder<V, A> {
    public AnyShortestPathBuilder() {
    }

    public AnyShortestPathBuilder(@Nonnull DirectedGraph<V, A> graph, @Nonnull ToDoubleFunction<A> costf) {
        super(graph, costf);
    }

    public AnyShortestPathBuilder(@Nonnull DirectedGraph<V, A> graph, @Nonnull ToDoubleTriFunction<V, V, A> costf) {
        super(graph, costf);
    }

    public AnyShortestPathBuilder(@Nonnull Function<V, Iterable<Map.Entry<V, A>>> nextNodesFunction, @Nonnull ToDoubleFunction<A> costf) {
        super(nextNodesFunction, costf);
    }

    public AnyShortestPathBuilder(@Nonnull Function<V, Iterable<Map.Entry<V, A>>> nextNodesFunction, @Nonnull ToDoubleTriFunction<V, V, A> costf) {
        super(nextNodesFunction, costf);
    }

    protected BackLink<V, A> search(@Nonnull V start,
                                    @Nonnull Predicate<V> goalPredicate,
                                    double maxCost,
                                    Function<V, Iterable<Map.Entry<V, A>>> nextf,
                                    ToDoubleTriFunction<V, V, A> costf) {
        PriorityQueue<MyBackLink<V, A>> frontier = new PriorityQueue<>(61);
        Map<V, MyBackLink<V, A>> frontierMap = new HashMap<>(61);
        Set<V> explored = new HashSet<>(61);

        MyBackLink<V, A> node = new MyBackLink<>(start, 0.0, null, null);
        frontier.add(node);
        while (true) {
            if (frontier.isEmpty()) {
                return null;
            }
            node = frontier.poll();
            final V vertex = node.vertex;
            frontierMap.remove(vertex);
            if (goalPredicate.test(vertex)) {
                break;
            }
            explored.add(node.getVertex());

            if (node.cost < maxCost) {
                for (Map.Entry<V, A> entry : nextf.apply(vertex)) {
                    V next = entry.getKey();
                    A arrow = entry.getValue();
                    double cost = node.cost + costf.applyAsDouble(vertex, next, arrow);

                    boolean isInFrontier = frontierMap.containsKey(next);
                    if (!explored.contains(next) && !isInFrontier) {
                        MyBackLink<V, A> lnk = new MyBackLink<>(next, cost, node, arrow);
                        frontier.add(lnk);
                        frontierMap.put(next, lnk);
                    } else if (isInFrontier) {
                        MyBackLink<V, A> lnk = frontierMap.get(next);
                        if (cost < lnk.cost) {
                            frontier.remove(lnk);
                            lnk.cost = cost;
                            lnk.parent = node;
                            lnk.arrow = arrow;
                            frontier.add(lnk);
                        }
                    }
                }
            }
        }

        return node;
    }


    protected static class MyBackLink<VV, AA> extends BackLink<VV, AA> {

        protected final VV vertex;
        @Nullable
        protected MyBackLink<VV, AA> parent;
        protected AA arrow;
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
    }

}
