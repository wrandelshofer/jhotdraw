/*
 * @(#)AbstractShortestPathBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.util.ToDoubleTriFunction;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

public abstract class AbstractShortestPathBuilder<V, A> {
    @NonNull
    private Function<V, Iterable<Arc<V, A>>> nextNodesFunction;
    @NonNull
    private ToDoubleTriFunction<V, V, A> costf;
    private double maxCost = Double.MAX_VALUE;

    public AbstractShortestPathBuilder() {
    }

    public AbstractShortestPathBuilder(@NonNull final DirectedGraph<V, A> graph,
                                       @NonNull final ToDoubleFunction<A> costf) {
        this(graph::getNextArcs, costf);
    }

    public AbstractShortestPathBuilder(@NonNull final DirectedGraph<V, A> graph,
                                       @NonNull final ToDoubleTriFunction<V, V, A> costf) {
        this(graph::getNextArcs, costf);
    }

    public AbstractShortestPathBuilder(@NonNull final Function<V, Iterable<Arc<V, A>>> nextNodesFunction,
                                       @NonNull final ToDoubleFunction<A> costf) {
        this(nextNodesFunction, (v1, v2, a) -> costf.applyAsDouble(a));
    }

    public AbstractShortestPathBuilder(@NonNull final Function<V, Iterable<Arc<V, A>>> nextNodesFunction,
                                       @NonNull final ToDoubleTriFunction<V, V, A> costf) {
        this.nextNodesFunction = nextNodesFunction;
        this.costf = costf;
    }

    /**
     * Builds an EdgePath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest cost.
     * <p>
     * This method implements the Uniform Cost Search algorithm.
     * <p>
     * References:<br>
     * <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Practical_optimizations_and_infinite_graphs">
     * Wikipedia</a>
     *
     * @param start the start vertex
     * @param goal  the goal vertex
     * @return a VertexPath if traversal is possible
     */
    @Nullable
    public Map.Entry<EdgePath<A>, Double> findEdgePath(@NonNull V start, @NonNull V goal) {
        return findEdgePath(start, goal::equals);
    }

    /**
     * Builds an EdgePath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest cost.
     * <p>
     * This method implements the Uniform Cost Search algorithm.
     * <p>
     * References:<br>
     * <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Practical_optimizations_and_infinite_graphs">
     * Wikipedia</a>
     *
     * @param start         the start vertex
     * @param goalPredicate the goal predicate
     * @return a VertexPath if traversal is possible
     */
    @Nullable
    public Map.Entry<EdgePath<A>, Double> findEdgePath(@NonNull V start, @NonNull Predicate<V> goalPredicate) {
        BackLink<V, A> node = search(start, goalPredicate);
        if (node == null) {
            return null;
        }
        //
        ArrayDeque<A> edges = new ArrayDeque<>();
        for (BackLink<V, A> parent = node; parent.getArrow() != null; parent = parent.getParent()) {
            edges.addFirst(parent.getArrow());
        }
        return new AbstractMap.SimpleEntry<>(new EdgePath<>(edges), node.getCost());
    }

    /**
     * Finds the shortest path via the specified waypoints.
     *
     * @param waypoints the waypoints
     * @param maxCost   the maximal cost of a path between two waypoints
     * @return the shortest path
     */
    @Nullable
    public Map.Entry<EdgePath<A>, Double> findEdgePathOverWaypoints(@NonNull Collection<? extends V> waypoints, double maxCost) {
        List<A> combinedPath = new ArrayList<>();
        V start = null;
        double cost = 0.0;
        for (V via : waypoints) {
            if (start != null) {
                Map.Entry<EdgePath<A>, Double> pathWithCost = findEdgePath(start, via::equals);
                if (pathWithCost == null) {
                    return null;
                }
                EdgePath<A> path = pathWithCost.getKey();
                cost += pathWithCost.getValue();
                ImmutableList<A> edges = path.getEdges();
                for (int i = 0, n = edges.size(); i < n; i++) {
                    combinedPath.add(edges.get(i));
                }
            }
            start = via;
        }
        return new AbstractMap.SimpleEntry<>(new EdgePath<>(combinedPath), cost);
    }

    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest cost.
     * <p>
     * This method implements the Uniform Cost Search algorithm.
     * <p>
     * References:<br>
     * <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Practical_optimizations_and_infinite_graphs">
     * Wikipedia</a>
     *
     * @param start         the start vertex
     * @param goalPredicate the goal predicate
     * @return a VertexPath if traversal is possible
     */
    @Nullable
    public Map.Entry<VertexPath<V>, Double> findVertexPath(@NonNull V start,
                                                           @NonNull Predicate<V> goalPredicate) {

        BackLink<V, A> node = search(start, goalPredicate
        );
        if (node == null) {
            return null;
        }
        //
        ArrayDeque<V> vertices = new ArrayDeque<>();
        for (BackLink<V, A> parent = node; parent != null; parent = parent.getParent()) {
            vertices.addFirst(parent.getVertex());
        }
        return new AbstractMap.SimpleEntry<>(new VertexPath<>(vertices), node.getCost());
    }

    /**
     * Finds the shortest path via the specified waypoints.
     *
     * @param waypoints the waypoints
     * @return the shortest path
     */
    @Nullable
    public Map.Entry<VertexPath<V>, Double> findVertexPathOverWaypoints(@NonNull Collection<? extends V> waypoints) {
        List<V> combinedPath = new ArrayList<>();
        V start = null;
        double cost = 0.0;
        for (V via : waypoints) {
            if (start != null) {
                Map.Entry<VertexPath<V>, Double> pathWithCost = findVertexPath(start, via::equals);
                if (pathWithCost == null) {
                    return null;
                }
                VertexPath<V> path = pathWithCost.getKey();
                cost += pathWithCost.getValue();
                ImmutableList<V> vertices = path.getVertices();
                for (int i = combinedPath.isEmpty() ? 0 : 1, n = vertices.size(); i < n; i++) {
                    combinedPath.add(vertices.get(i));
                }
            }
            start = via;
        }
        return new AbstractMap.SimpleEntry<>(new VertexPath<>(combinedPath), cost);
    }

    @Nullable
    private BackLink<V, A> search(@NonNull V start,
                                  @NonNull Predicate<V> goalPredicate) {
        return search(start, goalPredicate, maxCost, nextNodesFunction, costf);
    }

    /**
     * Search algorithm.
     *
     * @param start             the start vertex
     * @param goalPredicate     the predicate for the goal vertex
     * @param maxCost           abort if cost exceeds max cost - this prevents searching the entire graph
     * @param nextNodesFunction returns the next node entrys, key=vertex, value=arrow pointing to vertex
     * @param costf             the cost function. Given a vertex pair and the arrow connecting the pair as input,
     *                          returns the cost &gt; 0.
     * @return the back links that were found from goal to start
     */
    @Nullable
    protected abstract BackLink<V, A> search(@NonNull V start,
                                             @NonNull Predicate<V> goalPredicate,
                                             double maxCost,
                                             Function<V, Iterable<Arc<V, A>>> nextNodesFunction,
                                             ToDoubleTriFunction<V, V, A> costf);

    public static abstract class BackLink<VV, AA> implements Comparable<BackLink<VV, AA>> {
        @Override
        public int compareTo(@NonNull BackLink<VV, AA> that) {
            return Double.compare(this.getCost(), that.getCost());
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final BackLink<?, ?> other = (BackLink<?, ?>) obj;
            if (!Objects.equals(this.getVertex(), other.getVertex())) {
                return false;
            }
            return true;
        }

        public abstract AA getArrow();

        public abstract double getCost();

        /**
         * Return the path length up to this back link.
         */
        protected int getLength() {
            int length = 0;
            for (BackLink<VV, AA> node = getParent(); node != null; node = node.getParent()) {
                length++;
            }
            return length;
        }

        @Nullable
        public abstract BackLink<VV, AA> getParent();

        public abstract VV getVertex();

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.getVertex());
            return hash;
        }

    }

    public void setCostFunction(@NonNull ToDoubleTriFunction<V, V, A> costf) {
        this.costf = costf;
    }

    @NonNull
    public ToDoubleTriFunction<V, V, A> getCostFunction() {
        return costf;
    }

    @NonNull
    public Function<V, Iterable<Arc<V, A>>> getNextNodesFunction() {
        return nextNodesFunction;
    }

    public void setNextNodesFunction(@NonNull Function<V, Iterable<Arc<V, A>>> nextNodesFunction) {
        this.nextNodesFunction = nextNodesFunction;
    }
}
