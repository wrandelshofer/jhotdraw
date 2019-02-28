/* @(#)DirectedGraphCostPathBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.util.IntIntVToDoubleTriFunction;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.ToDoubleFunction;

/**
 * DirectedGraphCostPathBuilder.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntDirectedGraphCostPathBuilder<V, A> {
    @Nonnull
    private final IntFunction<Iterable<Map.Entry<Integer, A>>> nextNodesFunction;
    @Nonnull
    private final IntIntVToDoubleTriFunction<A> costf;
    private Queue<BackLinkWithArrow<V, A>> queue;
    private Set<Integer> visitedSet;
    private final int vertexCount;


    public IntDirectedGraphCostPathBuilder(@Nonnull final AttributedIntDirectedGraph<V, A> graph,
                                           @Nonnull final ToDoubleFunction<A> costf) {
        this(graph.getVertexCount(), graph::getNextIntEntries, costf);
    }

    public IntDirectedGraphCostPathBuilder(int vertexCount, @Nonnull final AttributedIntDirectedGraph<V, A> graph,
                                           @Nonnull final IntIntVToDoubleTriFunction<A> costf) {
        this(vertexCount, graph::getNextIntEntries, costf);
    }

    public IntDirectedGraphCostPathBuilder(int vertexCount, @Nonnull final IntFunction<Iterable<Map.Entry<Integer, A>>> nextNodesFunction,
                                           @Nonnull final ToDoubleFunction<A> costf) {
        this(vertexCount, nextNodesFunction, (v1, v2, a) -> costf.applyAsDouble(a));
    }

    public IntDirectedGraphCostPathBuilder(int vertexCount, @Nonnull final IntFunction<Iterable<Map.Entry<Integer, A>>> nextNodesFunction,
                                           @Nonnull final IntIntVToDoubleTriFunction<A> costf) {
        this.vertexCount = vertexCount;
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
    private BackLinkWithArrow<V, A> bfs(int root,
                                        @Nonnull IntPredicate goal,
                                        @Nonnull IntPredicate visited,
                                        double maxCost) {
        if (queue == null) {
            queue = new ArrayDeque<>(16);
        }
        BackLinkWithArrow<V, A> rootBackLink = new BackLinkWithArrow<>(root, null, null, 0.0);
        visited.test(root);
        queue.add(rootBackLink);
        BackLinkWithArrow<V, A> current = null;
        while (!queue.isEmpty()) {
            current = queue.remove();
            if (goal.test(current.vertex)) {
                break;
            }
            if (current.cost < maxCost) {
                for (Map.Entry<Integer, A> entry : nextNodesFunction.apply(current.vertex)) {
                    int next = entry.getKey();
                    if (visited.test(next)) {
                        A arrow = entry.getValue();
                        BackLinkWithArrow<V, A> backLink = new BackLinkWithArrow<>(next, current, arrow,
                                current.cost + costf.applyAsDouble(current.vertex, next, arrow));
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
    private BackLinkWithArrow<V, A> bfs(int root,
                                        @Nonnull IntPredicate goal,
                                        double maxCost) {
        if (visitedSet == null) {
            visitedSet = new HashSet<>();
        }
        BackLinkWithArrow<V, A> result = bfs(root, goal, v -> visitedSet.add(v), maxCost);
        visitedSet.clear();
        return result;
    }


    private NodeWithCost<V, A> findShortestPath(int start,
                                                @Nonnull PriorityQueue<NodeWithCost<V, A>> frontier,
                                                NodeWithCost<V, A>[] frontierMap,
                                                @Nonnull IntPredicate goalPredicate,
                                                @Nonnull BitSet explored,
                                                double maxCost) {
        NodeWithCost<V, A> node = new NodeWithCost<>(start, 0.0, null, null);
        frontier.add(node);
        while (true) {
            if (frontier.isEmpty()) {
                return null;
            }
            node = frontier.poll();
            final int vertex = node.vertex;
            frontierMap[vertex] = null;
            if (goalPredicate.test(vertex)) {
                break;
            }
            explored.set(node.getVertex());

            if (node.cost < maxCost) {
                for (Map.Entry<Integer, A> entry : nextNodesFunction.apply(vertex)) {
                    int next = entry.getKey();
                    A arrow = entry.getValue();
                    double cost = node.cost + costf.applyAsDouble(vertex, next, arrow);

                    boolean isInFrontier = frontierMap[next] != null;
                    if (!explored.get(next) && !isInFrontier) {
                        NodeWithCost<V, A> nwc = new NodeWithCost<>(next, cost, node, arrow);
                        frontier.add(nwc);
                        frontierMap[next] = nwc;
                    } else if (isInFrontier) {
                        NodeWithCost<V, A> nwcInFrontier = frontierMap[next];
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
     * @param maxCost the search aborts if there is no shorter path than maxCost
     * @return a path if traversal is possible, null otherwise
     */
    @Nullable
    public EdgePath<A> findEdgePath(int start, @Nonnull IntPredicate goal, double maxCost) {
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
     * @param maxCost the search aborts if there is no shorter path than maxCost
     * @return a path if traversal is possible, null otherwise
     */
    @Nullable
    public VertexPath<Integer> findVertexPath(int start, @Nonnull IntPredicate goal, double maxCost) {
        Deque<Integer> vertices = new ArrayDeque<>();
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
    public Map.Entry<VertexPath<Integer>, Double> findShortestVertexPath(int start,
                                                                         int goal) {
        return findShortestVertexPath(start, a -> goal == a, Double.POSITIVE_INFINITY);
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
     * @param graph the graph
     * @param start the start vertex
     * @param goal  the goal vertex
     * @return a VertexPath if traversal is possible
     */
    @Nullable
    public Map.Entry<VertexPath<V>, Double> findShortestVertexPath(AttributedIntDirectedGraph<V, A> graph,
                                                                   V start,
                                                                   V goal) {
        int startIdx = graph.getVertexIndex(start);
        int goalIdx = graph.getVertexIndex(goal);
        Map.Entry<VertexPath<Integer>, Double> entry = findShortestVertexPath(startIdx, a -> goalIdx == a, Double.POSITIVE_INFINITY);
        if (entry == null) {
            return null;
        }
        ImmutableList<Integer> indices = entry.getKey().getVertices();
        ArrayList<V> vertices = new ArrayList<>(indices.size());
        for (int idx : indices) {
            vertices.add(graph.getVertex(idx));
        }
        return new AbstractMap.SimpleImmutableEntry<>(new VertexPath<>(vertices), entry.getValue());
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
    public Map.Entry<VertexPath<Integer>, Double> findShortestVertexPath(int start,
                                                                         @Nonnull IntPredicate goalPredicate, double maxCost) {

        NodeWithCost<V, A> node = findShortestPath(start, goalPredicate, maxCost);
        if (node == null) {
            return null;
        }
        //
        ArrayDeque<Integer> vertices = new ArrayDeque<>();
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
    public Map.Entry<EdgePath<A>, Double> findShortestEdgePath(int start, int goal) {
        return findShortestEdgePath(start, a -> goal == a, Double.POSITIVE_INFINITY);
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
    public Map.Entry<EdgePath<A>, Double> findShortestEdgePath(int start, @Nonnull IntPredicate goalPredicate, double maxCost) {
        NodeWithCost<V, A> node = findShortestPath(start, goalPredicate, maxCost);
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    private NodeWithCost<V, A>[] frontierMap = new NodeWithCost[0];
    // Size of explored is the expected number of vertices that we need to explore.
    private BitSet explored = new BitSet(61);

    @Nullable
    private NodeWithCost<V, A> findShortestPath(int start,
                                                @Nonnull IntPredicate goalPredicate,
                                                double maxCost) {
        frontier.clear();
        if (frontierMap.length != vertexCount) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            NodeWithCost<V, A>[] tmp = new NodeWithCost[vertexCount];
            this.frontierMap = tmp;
        } else {
            Arrays.fill(frontierMap, null);
        }
        explored.clear();
        return findShortestPath(start, frontier, frontierMap, goalPredicate, explored, maxCost);
    }

    /**
     * Finds the shortest path via the specified waypoints.
     *
     * @param waypoints the waypoints
     * @param maxCost the search aborts if there is no shorter path than maxCost
     * @return the shortest path
     */
    @Nullable
    public Map.Entry<VertexPath<Integer>, Double> findShortestVertexPathOverWaypoints(@Nonnull Collection<Integer> waypoints, double maxCost) {
        List<Integer> combinedPath = new ArrayList<>();
        int start = -1;
        double cost = 0.0;
        for (int via : waypoints) {
            if (start != -1) {
                Map.Entry<VertexPath<Integer>, Double> pathWithCost = findShortestVertexPath(start, v -> via == v, maxCost);
                if (pathWithCost == null) {
                    return null;
                }
                VertexPath<Integer> path = pathWithCost.getKey();
                cost += pathWithCost.getValue();
                ImmutableList<Integer> vertices = path.getVertices();
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
     * @param maxCost the search aborts if there is no shorter path than maxCost
     * @return the shortest path
     */
    @Nullable
    public Map.Entry<EdgePath<A>, Double> findShortestEdgePathOverWaypoints(Collection<Integer> waypoints, double maxCost) {
        List<A> combinedPath = new ArrayList<>();
        Integer start = null;
        double cost = 0.0;
        for (int via : waypoints) {
            if (start != null) {
                Map.Entry<EdgePath<A>, Double> pathWithCost = findShortestEdgePath(start, a -> via == a, maxCost);
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
     * Enumerates all vertex paths from start to goal up to the specified maximal path cost.
     *
     * @param start   the start vertex
     * @param goal    the goal predicate
     * @param maxCost the maximal cost of a path
     * @return the enumerated paths
     */
    public List<VertexPath<Integer>> findAllVertexPaths(int start,
                                                        @Nonnull IntPredicate goal,
                                                        double maxCost) {
        List<BackLinkWithArrow<V, A>> backlinks = new ArrayList<>();
        dfsFindAllPaths(new BackLinkWithArrow<>(start, null, null, maxCost), goal, backlinks);
        List<VertexPath<Integer>> vertexPaths = new ArrayList<>(backlinks.size());
        Deque<Integer> path = new ArrayDeque<>();
        for (BackLinkWithArrow<V, A> list : backlinks) {
            path.clear();
            for (BackLinkWithArrow<V, A> backlink = list; backlink != null; backlink = backlink.parent) {
                path.addFirst(backlink.vertex);
            }
            vertexPaths.add(new VertexPath<>(path));
        }
        return vertexPaths;
    }

    private void dfsFindAllPaths(BackLinkWithArrow<V, A> current, IntPredicate goal,
                                 List<BackLinkWithArrow<V, A>> backlinks) {
        if (goal.test(current.vertex)) {
            backlinks.add(current);
            return;
        }

        if (current.cost > 0) {
            for (Map.Entry<Integer, A> entry : nextNodesFunction.apply(current.vertex)) {
                int v = entry.getKey();
                A a = entry.getValue();
                BackLinkWithArrow<V, A> newPath = new BackLinkWithArrow<>(v, current, a,
                        current.cost - costf.applyAsDouble(current.vertex, v, a));
                dfsFindAllPaths(newPath, goal, backlinks);
            }
        }
    }

    private static class BackLinkWithArrow<VV, AA> {

        final BackLinkWithArrow<VV, AA> parent;
        final int vertex;
        final AA arrow;
        final double cost;

        public BackLinkWithArrow(int vertex, BackLinkWithArrow<VV, AA> parent, AA arrow, double cost) {
            this.vertex = vertex;
            this.parent = parent;
            this.arrow = arrow;
            this.cost = cost;
        }

    }

    private static class NodeWithCost<VV, AA> implements Comparable<NodeWithCost<VV, AA>> {

        private final int vertex;
        @Nullable
        private NodeWithCost<VV, AA> parent;
        private AA arrow;
        private double cost;

        public NodeWithCost(int node, double cost, NodeWithCost<VV, AA> parent, AA arrow) {
            this.vertex = node;
            this.cost = cost;
            this.parent = parent;
            this.arrow = arrow;
        }

        @Override
        public int compareTo(NodeWithCost<VV, AA> that) {
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
        public NodeWithCost<VV, AA> getParent() {
            return parent;
        }

        public void setParent(NodeWithCost<VV, AA> parent) {
            this.parent = parent;
        }

        public int getVertex() {
            return vertex;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + this.vertex;
            return hash;
        }
    }

}
