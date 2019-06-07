/* @(#)DirectedGraphCostPathBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.util.ToDoubleTriFunction;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

/**
 * This path builder can be used to find any shortest path between
 * to vertices in a directed.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DirectedGraphCostPathBuilder<V, A> {
    @Nonnull
    private final Function<V, Iterable<Map.Entry<V, A>>> nextNodesFunction;
    @Nonnull
    private final ToDoubleTriFunction<V, V, A> costf;
    private Queue<BackLinkWithArrow<V, A>> queue;
    private Set<V> visitedSet;

    public DirectedGraphCostPathBuilder(@Nonnull final DirectedGraph<V, A> graph,
                                        @Nonnull final ToDoubleFunction<A> costf) {
        this(graph::getNextEntries, costf);
    }

    public DirectedGraphCostPathBuilder(@Nonnull final DirectedGraph<V, A> graph,
                                        @Nonnull final ToDoubleTriFunction<V, V, A> costf) {
        this(graph::getNextEntries, costf);
    }

    public DirectedGraphCostPathBuilder(@Nonnull final Function<V, Iterable<Map.Entry<V, A>>> nextNodesFunction,
                                        @Nonnull final ToDoubleFunction<A> costf) {
        this(nextNodesFunction, (v1, v2, a) -> costf.applyAsDouble(a));
    }

    public DirectedGraphCostPathBuilder(@Nonnull final Function<V, Iterable<Map.Entry<V, A>>> nextNodesFunction,
                                        @Nonnull final ToDoubleTriFunction<V, V, A> costf) {
        this.nextNodesFunction = nextNodesFunction;
        this.costf = costf;
    }


    private NodeWithCost<V, A> findAnyShortestPath(@Nonnull V start,
                                                   @Nonnull PriorityQueue<NodeWithCost<V, A>> frontier,
                                                   @Nonnull Map<V, NodeWithCost<V, A>> frontierMap,
                                                   @Nonnull Predicate<V> goalPredicate,
                                                   @Nonnull Set<V> explored,
                                                   double maxCost) {
        NodeWithCost<V, A> node = new NodeWithCost<>(start, 0.0, null, null);
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
                for (Map.Entry<V, A> entry : nextNodesFunction.apply(vertex)) {
                    V next = entry.getKey();
                    A arrow = entry.getValue();
                    double cost = node.cost + costf.applyAsDouble(vertex, next, arrow);

                    boolean isInFrontier = frontierMap.containsKey(next);
                    if (!explored.contains(next) && !isInFrontier) {
                        NodeWithCost<V, A> nwc = new NodeWithCost<>(next, cost, node, arrow);
                        frontier.add(nwc);
                        frontierMap.put(next, nwc);
                    } else if (isInFrontier) {
                        NodeWithCost<V, A> nwcInFrontier = frontierMap.get(next);
                        if (cost < nwcInFrontier.cost) {
                            frontier.remove(nwcInFrontier);
                            nwcInFrontier.cost = cost;
                            nwcInFrontier.parent = node;
                            nwcInFrontier.arrow = arrow;
                            frontier.add(nwcInFrontier);
                        }
                    }
                }
            }
        }

        return node;
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
     * @param start the start vertex
     * @param goal  the goal vertex
     * @return a VertexPath if traversal is possible
     */
    @Nullable
    public Map.Entry<VertexPath<V>, Double> findAnyShortestVertexPath(@Nonnull V start,
                                                                      @Nonnull V goal) {
        return findAnyShortestVertexPath(start, goal::equals, Double.POSITIVE_INFINITY);
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
     * @param maxCost       the maximal path cost
     * @return a VertexPath if traversal is possible
     */
    @Nullable
    public Map.Entry<VertexPath<V>, Double> findAnyShortestVertexPath(@Nonnull V start,
                                                                      @Nonnull Predicate<V> goalPredicate, double maxCost) {

        NodeWithCost<V, A> node = findAnyShortestPath(start, goalPredicate, maxCost);
        if (node == null) {
            return null;
        }
        //
        ArrayDeque<V> vertices = new ArrayDeque<>();
        for (NodeWithCost<V, A> parent = node; parent != null; parent = parent.parent) {
            vertices.addFirst(parent.vertex);
        }
        return new AbstractMap.SimpleEntry<>(new VertexPath<>(vertices), node.cost);
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
     * @param start the start vertex
     * @param goal  the goal vertex
     * @return a VertexPath if traversal is possible
     */
    @Nullable
    public Map.Entry<EdgePath<A>, Double> findAnyShortestEdgePath(@Nonnull V start, @Nonnull V goal) {
        return findAnyShortestEdgePath(start, goal::equals, Double.POSITIVE_INFINITY);
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
     * @param maxCost       the maximal cost
     * @return a VertexPath if traversal is possible
     */
    @Nullable
    public Map.Entry<EdgePath<A>, Double> findAnyShortestEdgePath(@Nonnull V start, @Nonnull Predicate<V> goalPredicate, double maxCost) {
        NodeWithCost<V, A> node = findAnyShortestPath(start, goalPredicate, maxCost);
        if (node == null) {
            return null;
        }
        //
        ArrayDeque<A> edges = new ArrayDeque<>();
        for (NodeWithCost<V, A> parent = node; parent.arrow != null; parent = parent.parent) {
            edges.addFirst(parent.arrow);
        }
        return new AbstractMap.SimpleEntry<>(new EdgePath<>(edges), node.cost);
    }

    // Size of priority deque and frontierMap is the expected size of the frontier.
    // We use a size that is smaller than 256 bytes (assuming 12 bytes for object header).
    private PriorityQueue<NodeWithCost<V, A>> frontier = new PriorityQueue<>(61);
    private Map<V, NodeWithCost<V, A>> frontierMap = new HashMap<>(61);
    // Size of explored is the expected number of vertices that we need to explore.
    private Set<V> explored = new HashSet<>(61);

    @Nullable
    private NodeWithCost<V, A> findAnyShortestPath(@Nonnull V start,
                                                   @Nonnull Predicate<V> goalPredicate,
                                                   double maxCost) {
        frontier.clear();
        frontierMap.clear();
        explored.clear();
        return findAnyShortestPath(start, frontier, frontierMap, goalPredicate, explored, maxCost);
    }

    /**
     * Finds the shortest path via the specified waypoints.
     *
     * @param waypoints the waypoints
     * @param maxCost   the maximal cost of a path
     * @return the shortest path
     */
    @Nullable
    public Map.Entry<VertexPath<V>, Double> findAnyShortestVertexPathOverWaypoints(@Nonnull Collection<? extends V> waypoints, double maxCost) {
        List<V> combinedPath = new ArrayList<>();
        V start = null;
        double cost = 0.0;
        for (V via : waypoints) {
            if (start != null) {
                Map.Entry<VertexPath<V>, Double> pathWithCost = findAnyShortestVertexPath(start, via::equals, maxCost);
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

    /**
     * Finds the shortest path via the specified waypoints.
     *
     * @param waypoints the waypoints
     * @param maxCost   the maximal cost of a path between two waypoints
     * @return the shortest path
     */
    @Nullable
    public Map.Entry<EdgePath<A>, Double> findAnyShortestEdgePathOverWaypoints(Collection<? extends V> waypoints, double maxCost) {
        List<A> combinedPath = new ArrayList<>();
        V start = null;
        double cost = 0.0;
        for (V via : waypoints) {
            if (start != null) {
                Map.Entry<EdgePath<A>, Double> pathWithCost = findAnyShortestEdgePath(start, via::equals, maxCost);
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


    private void dfsFindAllPaths(BackLinkWithArrow<V, A> current, Predicate<V> goal,
                                 List<BackLinkWithArrow<V, A>> backlinks) {
        if (goal.test(current.vertex)) {
            backlinks.add(current);
            return;
        }

        if (current.cost > 0) {
            for (Map.Entry<V, A> entry : nextNodesFunction.apply(current.vertex)) {
                V v = entry.getKey();
                A a = entry.getValue();
                BackLinkWithArrow<V, A> newPath = new BackLinkWithArrow<>(v, current, a,
                        current.cost - costf.applyAsDouble(current.vertex, v, a));
                dfsFindAllPaths(newPath, goal, backlinks);
            }
        }
    }

    private static class BackLinkWithArrow<VV, AA> {

        final BackLinkWithArrow<VV, AA> parent;
        final VV vertex;
        final AA arrow;
        final double cost;

        public BackLinkWithArrow(VV vertex, BackLinkWithArrow<VV, AA> parent, AA arrow, double cost) {
            this.vertex = vertex;
            this.parent = parent;
            this.arrow = arrow;
            this.cost = cost;
        }

    }

    private static class NodeWithCost<V, E> implements Comparable<NodeWithCost<V, E>> {

        protected final V vertex;
        @Nullable
        protected NodeWithCost<V, E> parent;
        protected E arrow;
        protected double cost;

        public NodeWithCost(V node, double cost, NodeWithCost<V, E> parent, E arrow) {
            this.vertex = node;
            this.cost = cost;
            this.parent = parent;
            this.arrow = arrow;
        }

        @Override
        public int compareTo(NodeWithCost<V, E> that) {
            return Double.compare(this.cost, that.cost);
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
            final NodeWithCost<?, ?> other = (NodeWithCost<?, ?>) obj;
            if (!Objects.equals(this.vertex, other.vertex)) {
                return false;
            }
            return true;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        @Nullable
        public NodeWithCost<V, E> getParent() {
            return parent;
        }

        public void setParent(NodeWithCost<V, E> parent) {
            this.parent = parent;
        }

        public V getVertex() {
            return vertex;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.vertex);
            return hash;
        }
    }

}
