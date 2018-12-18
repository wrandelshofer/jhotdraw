/* @(#)DirectedGraphCostPathBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.util.ToDoubleTriFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
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
 * DirectedGraphCostPathBuilder.
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

    public DirectedGraphCostPathBuilder(@Nonnull final Function<V, Iterable<Map.Entry<V, A>>> nextNodesFunction,
                                        @Nonnull final ToDoubleFunction<A> costf) {
        this.nextNodesFunction = nextNodesFunction;
        this.costf = (v1, v2, a) -> costf.applyAsDouble(a);
    }

    public DirectedGraphCostPathBuilder(@Nonnull final Function<V, Iterable<Map.Entry<V, A>>> nextNodesFunction,
                                        @Nonnull final ToDoubleTriFunction<V, V, A> costf) {
        this.nextNodesFunction = nextNodesFunction;
        this.costf = costf;
    }


    /**
     * Breadth-first-search.
     *
     * @param root    the starting point of the search
     * @param goal    the goal of the search
     * @param visited a predicate with side effect. The predicate returns true
     *                if the specified vertex has been visited, and marks the specified vertex
     *                as visited.
     * @return a back link on success, null on failure
     */
    private BackLinkWithArrow<V, A> bfs(@Nonnull V root,
                                        @Nonnull Predicate<V> goal,
                                        @Nonnull Predicate<V> visited,
                                        double maxCost) {
        if (queue == null) {
            queue = new ArrayDeque<>(16);
        }
        BackLinkWithArrow<V, A> rootBackLink = new BackLinkWithArrow<>(root, null, null, maxCost);
        visited.test(root);
        queue.add(rootBackLink);
        BackLinkWithArrow<V, A> current = null;
        while (!queue.isEmpty()) {
            current = queue.remove();
            if (goal.test(current.vertex)) {
                break;
            }
            if (current.maxCost > 0) {
                for (Map.Entry<V, A> entry : nextNodesFunction.apply(current.vertex)) {
                    V next = entry.getKey();
                    if (visited.test(next)) {
                        A arrow = entry.getValue();
                        BackLinkWithArrow<V, A> backLink = new BackLinkWithArrow<>(next, current, arrow,
                                current.maxCost - costf.applyAsDouble(current.vertex, next, arrow));
                        queue.add(backLink);
                    }
                }
            }
        }
        queue.clear();
        if (current == null || !goal.test(current.vertex)) {
            return null;
        }
        return current;
    }

    /**
     * Breadth-first-search.
     *
     * @param root the starting point of the search
     * @param goal the goal of the search
     * @return the path elements. Returns an empty list if there is no path. The
     * list is mutable.
     */
    @Nullable
    private BackLinkWithArrow<V, A> bfs(@Nonnull V root,
                                        @Nonnull Predicate<V> goal,
                                        double maxCost) {
        if (visitedSet == null) {
            visitedSet = new HashSet<>();
        }
        BackLinkWithArrow<V, A> result = bfs(root, goal, visitedSet::add, maxCost);
        visitedSet.clear();
        return result;
    }


    private NodeWithCost<V, A> findShortestPath(@Nonnull V start,
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
                        if (nwcInFrontier.cost > cost) {
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
     * Builds an EdgePath through the graph which goes from the specified start
     * vertex to the specified goal vertex.
     * <p>
     * This method uses a breadth first search and returns the first result that
     * it finds.
     * <p>
     * References:<br>
     * <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Practical_optimizations_and_infinite_graphs">
     * Wikipedia</a>
     *
     * @param start the start vertex
     * @param goal  the goal predicate
     * @return a path if traversal is possible, null otherwise
     */
    @Nullable
    public EdgePath<A> findEdgePath(@Nonnull V start, @Nonnull Predicate<V> goal, double maxCost) {
        Deque<A> arrows = new ArrayDeque<>();
        BackLinkWithArrow<V, A> current = bfs(start, goal, maxCost);
        if (current == null) {
            return null;
        }
        for (BackLinkWithArrow<V, A> i = current; i.arrow != null; i = i.parent) {
            arrows.addFirst(i.arrow);
        }
        return new EdgePath<>(arrows);
    }

    /**
     * Builds an EdgePath through the graph which goes from the specified start
     * vertex to the specified goal vertex.
     * <p>
     * This method uses a breadth first search and returns the first result that
     * it finds.
     * <p>
     * References:<br>
     * <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Practical_optimizations_and_infinite_graphs">
     * Wikipedia</a>
     *
     * @param start the start vertex
     * @param goal  the goal predicate
     * @return a path if traversal is possible, null otherwise
     */
    @Nullable
    public VertexPath<V> findVertexPath(@Nonnull V start, @Nonnull Predicate<V> goal, double maxCost) {
        Deque<V> vertices = new ArrayDeque<>();
        BackLinkWithArrow<V, A> current = bfs(start, goal, maxCost);
        if (current == null) {
            return null;
        }
        for (BackLinkWithArrow<V, A> i = current; i != null; i = i.parent) {
            vertices.addFirst(i.vertex);
        }
        return new VertexPath<>(vertices);
    }

    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest maxCost.
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
    public VertexPath<V> findShortestVertexPath(@Nonnull V start,
                                                @Nonnull V goal) {
        return findShortestVertexPath(start, goal::equals, Double.POSITIVE_INFINITY);
    }

    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest maxCost.
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
    public VertexPath<V> findShortestVertexPath(@Nonnull V start,
                                                @Nonnull Predicate<V> goalPredicate, double maxCost) {

        NodeWithCost<V, A> node = findShortestPath(start, goalPredicate, maxCost);
        if (node == null) {
            return null;
        }
        //
        ArrayDeque<V> vertices = new ArrayDeque<>();
        for (NodeWithCost<V, A> parent = node; parent != null; parent = parent.parent) {
            vertices.addFirst(parent.vertex);
        }
        return new VertexPath<>(vertices);
    }

    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest maxCost.
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
    public EdgePath<A> findShortestEdgePath(@Nonnull V start, @Nonnull V goal) {
        return findShortestEdgePath(start, goal::equals, Double.POSITIVE_INFINITY);
    }

    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex at the lowest maxCost.
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
    public EdgePath<A> findShortestEdgePath(@Nonnull V start, @Nonnull Predicate<V> goalPredicate, double maxCost) {
        NodeWithCost<V, A> node = findShortestPath(start, goalPredicate, maxCost);
        if (node == null) {
            return null;
        }
        //
        ArrayDeque<A> edges = new ArrayDeque<>();
        for (NodeWithCost<V, A> parent = node; parent.arrow != null; parent = parent.parent) {
            edges.addFirst(parent.arrow);
        }
        return new EdgePath<>(edges);
    }

    @Nullable
    private NodeWithCost<V, A> findShortestPath(@Nonnull V start,
                                                @Nonnull Predicate<V> goalPredicate,
                                                double maxCost) {
        // Size of priority deque and frontierMap is the expected size of the frontier.
        // We use a size that is smaller than 256 bytes (assuming 12 bytes for object header).
        PriorityQueue<NodeWithCost<V, A>> frontier = new PriorityQueue<>(61);
        Map<V, NodeWithCost<V, A>> frontierMap = new HashMap<>(61);
        // Size of explored is the expected number of vertices that we need to explore.
        Set<V> explored = new HashSet<>(61);
        return findShortestPath(start, frontier, frontierMap, goalPredicate, explored, maxCost);
    }

    /**
     * Finds the shortest path via the specified waypoints.
     *
     * @param waypoints the waypoints
     * @return the shortest path
     */
    @Nullable
    public VertexPath<V> findShortestVertexPathOverWaypoints(@Nonnull Collection<? extends V> waypoints, double maxCost) {
        List<V> combinedPath = new ArrayList<>();
        V start = null;
        for (V via : waypoints) {
            if (start != null) {
                VertexPath<V> path = findShortestVertexPath(start, via::equals, maxCost);
                if (path == null) {
                    return null;
                }
                List<V> vertices = path.getVertices();
                for (int i = combinedPath.isEmpty() ? 0 : 1, n = vertices.size(); i < n; i++) {
                    combinedPath.add(vertices.get(i));
                }
            }
            start = via;
        }
        return new VertexPath<V>(combinedPath);
    }

    /**
     * Finds the shortest path via the specified waypoints.
     *
     * @param waypoints the waypoints
     * @return the shortest path
     */
    @Nullable
    public EdgePath<A> findShortestEdgePathOverWayponits(Collection<? extends V> waypoints, double maxCost) {
        List<A> combinedPath = new ArrayList<>();
        V start = null;
        for (V via : waypoints) {
            if (start != null) {
                EdgePath<A> path = findShortestEdgePath(start, via::equals, maxCost);
                if (path == null) {
                    return null;
                }
                List<A> edges = path.getEdges();
                for (int i = combinedPath.isEmpty() ? 0 : 1, n = edges.size(); i < n; i++) {
                    combinedPath.add(edges.get(i));
                }
            }
            start = via;
        }
        return new EdgePath<A>(combinedPath);
    }


    /**
     * Enumerates all vertex paths from start to goal up to the specified maximal path maxCost.
     *
     * @param start   the start vertex
     * @param goal    the goal predicate
     * @param maxCost the maximal maxCost of a path
     * @return the enumerated paths
     */
    public <T> List<VertexPath<V>> findAllVertexPaths(@Nonnull V start,
                                                      @Nonnull Predicate<V> goal,
                                                      double maxCost) {
        List<BackLinkWithArrow<V, A>> backlinks = new ArrayList<>();
        dfsFindAllPaths(new BackLinkWithArrow<>(start, null, null, maxCost), goal, backlinks);
        List<VertexPath<V>> vertexPaths = new ArrayList<>(backlinks.size());
        Deque<V> path = new ArrayDeque<>();
        for (BackLinkWithArrow<V, A> list : backlinks) {
            path.clear();
            for (BackLinkWithArrow<V, A> backlink = list; backlink != null; backlink = backlink.parent) {
                path.addFirst(backlink.vertex);
            }
            vertexPaths.add(new VertexPath<V>(path));
        }
        return vertexPaths;
    }

    private void dfsFindAllPaths(BackLinkWithArrow<V, A> current, Predicate<V> goal,
                                 List<BackLinkWithArrow<V, A>> backlinks) {
        if (goal.test(current.vertex)) {
            backlinks.add(current);
            return;
        }

        if (current.maxCost > 0) {
            for (Map.Entry<V, A> entry : nextNodesFunction.apply(current.vertex)) {
                V v = entry.getKey();
                A a = entry.getValue();
                BackLinkWithArrow<V, A> newPath = new BackLinkWithArrow<>(v, current, a,
                        current.maxCost - costf.applyAsDouble(current.vertex, v, a));
                dfsFindAllPaths(newPath, goal, backlinks);
            }
        }
    }

    private static class BackLinkWithArrow<VV, AA> {

        final BackLinkWithArrow<VV, AA> parent;
        final VV vertex;
        final AA arrow;
        final double maxCost;

        public BackLinkWithArrow(VV vertex, BackLinkWithArrow<VV, AA> parent, AA arrow, double maxCost) {
            this.vertex = vertex;
            this.parent = parent;
            this.arrow = arrow;
            this.maxCost = maxCost;
        }

    }

    private static class NodeWithCost<V, E> implements Comparable<NodeWithCost<V, E>> {

        private final V vertex;
        @Nullable
        private NodeWithCost<V, E> parent;
        private E arrow;
        private double cost;

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
