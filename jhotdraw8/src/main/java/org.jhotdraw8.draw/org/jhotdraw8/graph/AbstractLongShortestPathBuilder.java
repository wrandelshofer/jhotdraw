/*
 * @(#)AbstractShortestPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.util.ToLongTriFunction;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

public abstract class AbstractLongShortestPathBuilder<V, A> {
    private @NonNull Function<V, Iterable<Arc<V, A>>> nextNodesFunction;
    private @NonNull ToLongTriFunction<V, V, A> costf;

    public AbstractLongShortestPathBuilder() {
    }

    public AbstractLongShortestPathBuilder(final @NonNull DirectedGraph<V, A> graph,
                                           final @NonNull ToLongFunction<A> costf) {
        this(graph::getNextArcs, costf);
    }

    public AbstractLongShortestPathBuilder(final @NonNull DirectedGraph<V, A> graph,
                                           final @NonNull ToLongTriFunction<V, V, A> costf) {
        this(graph::getNextArcs, costf);
    }

    public AbstractLongShortestPathBuilder(final @NonNull Function<V, Iterable<Arc<V, A>>> nextNodesFunction,
                                           final @NonNull ToLongFunction<A> costf) {
        this(nextNodesFunction, (v1, v2, a) -> costf.applyAsLong(a));
    }

    public AbstractLongShortestPathBuilder(final @NonNull Function<V, Iterable<Arc<V, A>>> nextNodesFunction,
                                           final @NonNull ToLongTriFunction<V, V, A> costf) {
        this.nextNodesFunction = nextNodesFunction;
        this.costf = costf;
    }

    /**
     * Builds an ArrowPath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest cost.
     * <p>
     * This method implements the Uniform Cost Search algorithm.
     *
     * @param start the start vertex
     * @param goal  the goal vertex
     * @return a VertexPath if traversal is possible
     */
    public @Nullable Map.Entry<ArrowPath<A>, Long> findArrowPath(@NonNull V start, @NonNull V goal) {
        return findArrowPath(start, goal::equals);
    }

    /**
     * Builds an ArrowPath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest cost.
     * <p>
     * This method implements the Uniform Cost Search algorithm.
     *
     * @param start         the start vertex
     * @param goalPredicate the goal predicate
     * @return a VertexPath if traversal is possible
     */
    public @Nullable Map.Entry<ArrowPath<A>, Long> findArrowPath(@NonNull V start, @NonNull Predicate<V> goalPredicate) {
        return findArrowPath(start, goalPredicate, Long.MAX_VALUE);
    }

    /**
     * Builds an ArrowPath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest cost.
     * <p>
     * This method implements the Uniform Cost Search algorithm.
     * <dl>
     *     <dt>Wikipedia, Dijkstra's algorithm, Practical optimizations and infinite graphs</dt>
     *     <dd><a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Practical_optimizations_and_infinite_graphs">
     *      wikipedia.org</a></dd>
     * </dl>
     *
     * @param start         the start vertex
     * @param goalPredicate the goal predicate
     * @return a VertexPath if traversal is possible
     */
    public @Nullable Map.Entry<ArrowPath<A>, Long> findArrowPath(@NonNull V start, @NonNull Predicate<V> goalPredicate, long maxCost) {
        BackLink<V, A> node = search(start, goalPredicate, maxCost);
        return toArrowPath(node);
    }

    public static <V, A> Map.@Nullable Entry<ArrowPath<A>, Long> toArrowPath(BackLink<V, A> node) {
        if (node == null) {
            return null;
        }
        //
        ArrayDeque<A> edges = new ArrayDeque<>();
        for (BackLink<V, A> parent = node; parent.getParent() != null; parent = parent.getParent()) {
            A arrow = parent.getArrow();
            assert arrow != null;
            edges.addFirst(arrow);
        }
        return new AbstractMap.SimpleEntry<>(new ArrowPath<>(edges), node.getCost());
    }

    /**
     * Finds the shortest path via the specified waypoints.
     *
     * @param waypoints the waypoints
     * @param maxCost   the maximal cost of the path
     * @return a ArrowPath if traversal is possible and if the past does not exceed the max cost
     */
    public @Nullable Map.Entry<ArrowPath<A>, Long> findArrowPathOverWaypoints(@NonNull Collection<? extends V> waypoints, long maxCost) {
        List<A> combinedPath = new ArrayList<>();
        V start = null;
        long cost = 0;
        for (V via : waypoints) {
            if (start != null) {
                Map.Entry<ArrowPath<A>, Long> pathWithCost = findArrowPath(start, via::equals, maxCost - cost);
                if (pathWithCost == null) {
                    return null;
                }
                ArrowPath<A> path = pathWithCost.getKey();
                cost += pathWithCost.getValue();
                ImmutableList<A> edges = path.getArrows();
                for (int i = 0, n = edges.size(); i < n; i++) {
                    combinedPath.add(edges.get(i));
                }
            }
            start = via;
        }
        return new AbstractMap.SimpleEntry<>(new ArrowPath<>(combinedPath), cost);
    }

    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest cost.
     * <p>
     * This method implements the Uniform Cost Search algorithm.
     *
     * @param start         the start vertex
     * @param goalPredicate the goal predicate
     * @return a VertexPath if traversal is possible
     */
    public @Nullable Map.Entry<VertexPath<V>, Long> findVertexPath(@NonNull V start,
                                                                   @NonNull Predicate<V> goalPredicate) {
        return findVertexPath(start, goalPredicate, Long.MAX_VALUE);
    }

    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest cost.
     * <p>
     * This method implements the Uniform Cost Search algorithm.
     * <p>
     * References:
     * <dl>
     *     <dt>Wikipedia, Dijkstra's algorithm, Practical optimizations and infinite graphs</dt>
     *     <dd><a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Practical_optimizations_and_infinite_graphs">
     *      wikipedia.org</a></dd>
     * </dl>
     *
     * @param start         the start vertex
     * @param goalPredicate the goal predicate
     * @param maxCost       the maximal cost
     * @return a VertexPath if traversal is possible and if the past does not exceed the max cost
     */
    public @Nullable Map.Entry<VertexPath<V>, Long> findVertexPath(@NonNull V start,
                                                                   @NonNull Predicate<V> goalPredicate, long maxCost) {

        BackLink<V, A> node = search(start, goalPredicate, maxCost);
        return toVertexPath(node);
    }

    public static <V, A> Map.@Nullable Entry<VertexPath<V>, Long> toVertexPath(BackLink<V, A> node) {
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

    public static <V, A> Map.@Nullable Entry<VertexPath<V>, Long> toVertexPathLong(BackLink<V, A> node) {
        if (node == null) {
            return null;
        }
        //
        ArrayDeque<V> vertices = new ArrayDeque<>();
        for (BackLink<V, A> parent = node; parent != null; parent = parent.getParent()) {
            vertices.addFirst(parent.getVertex());
        }
        return new AbstractMap.SimpleEntry<>(new VertexPath<>(vertices), node.getCostLong());
    }

    /**
     * Finds the shortest path via the specified waypoints.
     *
     * @param waypoints the waypoints
     * @return the shortest path
     */
    public @Nullable Map.Entry<VertexPath<V>, Long> findVertexPathOverWaypoints(@NonNull Collection<? extends V> waypoints) {
        return findVertexPathOverWaypoints(waypoints, Long.MAX_VALUE);
    }

    /**
     * Finds the shortest path via the specified waypoints.
     *
     * @param waypoints the waypoints
     * @param maxCost   the maximal cost of the path
     * @return the shortest path that does not exceed maxCost
     */
    public @Nullable Map.Entry<VertexPath<V>, Long> findVertexPathOverWaypoints(@NonNull Collection<? extends V> waypoints, long maxCost) {
        List<V> combinedPath = new ArrayList<>();
        V start = null;
        long cost = 0;
        for (V via : waypoints) {
            if (start != null) {
                Map.Entry<VertexPath<V>, Long> pathWithCost = findVertexPath(start, via::equals, maxCost - cost);
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

    private @Nullable BackLink<V, A> search(@NonNull V start,
                                            @NonNull Predicate<V> goalPredicate, long maxCost) {
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
    protected abstract @Nullable BackLink<V, A> search(@NonNull V start,
                                                       @NonNull Predicate<V> goalPredicate,
                                                       long maxCost,
                                                       Function<V, Iterable<Arc<V, A>>> nextNodesFunction,
                                                       ToLongTriFunction<V, V, A> costf);

    public abstract static class BackLink<VV, AA> implements Comparable<BackLink<VV, AA>> {
        @Override
        public int compareTo(@NonNull BackLink<VV, AA> that) {
            return Long.compare(this.getCost(), that.getCost());
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
            return Objects.equals(this.getVertex(), other.getVertex());
        }

        public abstract @Nullable AA getArrow();

        public abstract long getCost();

        public abstract long getCostLong();

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

        public abstract @Nullable BackLink<VV, AA> getParent();

        public abstract @NonNull VV getVertex();

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.getVertex());
            return hash;
        }

    }

    public void setCostFunction(@NonNull ToLongTriFunction<V, V, A> costf) {
        this.costf = costf;
    }

    public @NonNull ToLongTriFunction<V, V, A> getCostFunction() {
        return costf;
    }

    public @NonNull Function<V, Iterable<Arc<V, A>>> getNextNodesFunction() {
        return nextNodesFunction;
    }

    public void setNextNodesFunction(@NonNull Function<V, Iterable<Arc<V, A>>> nextNodesFunction) {
        this.nextNodesFunction = nextNodesFunction;
    }
}
