/* @(#)DirectedGraphPathBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * DirectedGraphPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 * @param <A> the arrow type
 */
public class DirectedGraphPathBuilder<V, A> {

        private ToDoubleFunction<A> costFunction;
    private BitSet intExplored;
    private PriorityQueue< IntNodeWithCost<A>> intFrontier;
    @SuppressWarnings({"rawtypes"})
    private IntNodeWithCost[] intFrontierMap;
    private Queue<BackLinkWithArrow<V, A>> queue;
    private Set<V> visitedSet;

    public DirectedGraphPathBuilder(ToDoubleFunction<A> costFunction) {
        this.costFunction = costFunction;
    }

    public DirectedGraphPathBuilder() {
        this.costFunction = arrow -> 1.0;
    }

    /**
     * Breadth-first-search.
     *
     * @param <V> the vertex type
     * @param graph a graph
     * @param root the starting point of the search
     * @param goal the goal of the search
     * @param visited a predicate with side effect. The predicate returns true
     * if the specified vertex has been visited, and marks the specified vertex
     * as visited.
     * @return a back link on success, null on failure
     */
    private BackLinkWithArrow<V, A> breadthFirstSearch(DirectedGraph<V, A> graph, V root, V goal, Predicate<V> visited) {
        if (queue == null) {
            queue = new ArrayDeque<>(16);
        }
        BackLinkWithArrow<V, A> rootBackLink = new BackLinkWithArrow<>(root, null, null);// temporaly allocated objects producing lots of garbage
        visited.test(root);
        queue.add(rootBackLink);
        BackLinkWithArrow<V, A> current = null;
        while (!queue.isEmpty()) {
            current = queue.remove();
            if (current.vertex == goal) {
                break;
            }
            for (int i = 0, n = graph.getNextCount(current.vertex); i < n; i++) {
                V next = graph.getNext(current.vertex, i);
                A arrow = graph.getNextArrow(current.vertex, i);
                if (visited.test(next)) {
                    BackLinkWithArrow<V, A> backLink = new BackLinkWithArrow<>(next, current, arrow);
                    queue.add(backLink);
                }
            }
        }
        queue.clear();
        if (current == null || current.vertex != goal) {
            return null;
        }
        return current;
    }

    /**
     * Breadth-first-search.
     *
     * @param <V> the vertex type
     * @param graph a graph
     * @param root the starting point of the search
     * @param goal the goal of the search
     * @return the path elements. Returns an empty list if there is no path. The
     * list is mutable.
     */
    private BackLinkWithArrow<V, A> breadthFirstSearch(DirectedGraph<V, A> graph, V root, V goal) {
        if (visitedSet == null) {
            visitedSet = new HashSet<>();
        }
        BackLinkWithArrow<V, A> result = breadthFirstSearch(graph, root, goal, visitedSet::add);
        visitedSet.clear();
        return result;
    }

    /**
     * Builds a VertexPath through the graph which traverses the specified
     * waypoints.
     * <p>
     * This method uses breadth first search. It returns the first path that it
     * finds with this search strategy.
     *
     * @param graph a graph
     * @param waypoints waypoints, the iteration sequence of this collection
     * determines how the waypoints are traversed
     * @return a VertexPath if traversal is possible
     * @throws org.jhotdraw8.graph.PathBuilderException if traversal is not
     * possible
     */
        public VertexPath<V> buildAnyVertexPath( DirectedGraph<V, A> graph,  Collection<V> waypoints) throws PathBuilderException {
        Iterator<V> i = waypoints.iterator();
        List<V> pathElements = new ArrayList<>(16);
        if (!i.hasNext()) {
            throw new PathBuilderException("No waypoints provided");
        }
        V start = i.next();
        pathElements.add(start);
        while (i.hasNext()) {
            V goal = i.next();
            BackLinkWithArrow<V, A> back = breadthFirstSearch(graph, start, goal);
            if (back == null) {
                throw new PathBuilderException("Breadh first search stalled at vertex: " + goal
                        + " waypoints: " + waypoints.stream().map(Object::toString).collect(Collectors.joining(", ")) + ".");
            } else {
                for (BackLinkWithArrow<V, A> b = back; b.vertex != start; b = b.parent) {
                    pathElements.add(null);
                }
                int index = pathElements.size();
                for (BackLinkWithArrow<V, A> b = back; b.vertex != start; b = b.parent) {
                    pathElements.set(--index, b.vertex);
                }
            }

            start = goal;
        }
        return new VertexPath<>(pathElements);
    }

    @SuppressWarnings("rawtypes")
    private IntNodeWithCost<A> doFindIntShortestPath(int start, PriorityQueue<IntNodeWithCost<A>> frontier, IntNodeWithCost[] frontierMap, int goal, BitSet explored, AttributedIntDirectedGraph<?,A> graph, ToDoubleFunction<A> costf) {
        IntNodeWithCost<A> node = new IntNodeWithCost<>(start, 0.0, null, null);
        frontier.add(node);
        while (true) {
            if (frontier.isEmpty()) {
                return null;
            }
            node = frontier.poll();
            final int vertex = node.vertex;
            frontierMap[vertex] = null;
            if (vertex == goal) {
                break;
            }
            explored.set(node.getVertex());
            for (int i = 0, count = graph.getNextCount(vertex); i < count; i++) {
                int next = graph.getNext(vertex, i);
                final A arrow = graph.getArrow(vertex, i);
                double cost = node.cost + costf.applyAsDouble(arrow);

                @SuppressWarnings("unchecked")
                IntNodeWithCost<A> nwcInFrontier = frontierMap[next];
                if (!explored.get(next) && nwcInFrontier == null) {
                    IntNodeWithCost<A> nwc = new IntNodeWithCost<>(next, cost, node, arrow);
                    frontier.add(nwc);
                    frontierMap[next] = nwc;
                } else if (nwcInFrontier != null) {
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

        return node;
    }

    private VertexPath<V> doFindIntShortestVertexPath(DirectedGraph<V, A> graph, V start, V goal, ToDoubleFunction<A> costf) {
        @SuppressWarnings("unchecked")
        AttributedIntDirectedGraph<V,A> intGraph = (AttributedIntDirectedGraph<V,A>) graph;
        int startIndex = -1, goalIndex = -1;
        for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
            V v = graph.getVertex(i);
            if (v == start) {
                startIndex = i;
                if (goalIndex != -1) {
                    break;
                }
            }
            if (v == goal) {
                goalIndex = i;
                if (startIndex != -1) {
                    break;
                }
            }
        }
        VertexPath<Integer> intPath = findIntShortestVertexPath(intGraph, startIndex, goalIndex, costf);
        if (intPath == null) {
            return null;
        }
        ArrayList<V> elements = new ArrayList<>(intPath.getVertices().size());
        for (Integer vi : intPath.getVertices()) {
            elements.add(graph.getVertex(vi));
        }
        return new VertexPath<>(elements);
    }

    @Nullable
    private EdgePath<A> doFindShortestEdgePath( DirectedGraph<V, A> graph,
             V start,  V goal,  ToDoubleFunction<A> costf) {

        NodeWithCost<V, A> node = findShortestPath(graph, start, goal, costf);
        if (node == null) {
            return null;
        }
        //
        ArrayDeque<A> arrows = new ArrayDeque<>();
        for (NodeWithCost<V, A> parent = node; parent != null && parent.arrow != null; parent = parent.parent) {
            arrows.addFirst(parent.arrow);
        }
        return new EdgePath<>(arrows);
    }

    private NodeWithCost<V, A> doFindShortestPath(V start,
            PriorityQueue<NodeWithCost<V, A>> frontier, 
            Map<V, NodeWithCost<V, A>> frontierMap, V goal, 
            Set<V> explored, DirectedGraph<V, A> graph, ToDoubleFunction<A> costf) {
        NodeWithCost<V, A> node = new NodeWithCost<>(start, 0.0, null, null);
        frontier.add(node);
        while (true) {
            if (frontier.isEmpty()) {
                return null;
            }
            node = frontier.poll();
            final V vertex = node.vertex;
            frontierMap.remove(vertex);
            if (vertex == goal) {
                break;
            }
            explored.add(node.getVertex());
            for (int i = 0, count = graph.getNextCount(vertex); i < count; i++) {
                V next = graph.getNext(vertex, i);
                final A arrow = graph.getNextArrow(vertex, i);
                double cost = node.cost + costf.applyAsDouble(arrow);

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

        return node;
    }

    @Nullable
    private VertexPath<V> doFindShortestVertexPath( DirectedGraph<V, A> graph,
             V start,  V goal,  ToDoubleFunction<A> costf) {

        NodeWithCost<V, A> node = findShortestPath(graph, start, goal, costf);
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
     * vertex to the specified goal vertex.
     * <p>
     * This method uses a breadth first search and returns the first result that
     * it finds.
     * <p>
     * References:<br>
     * <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Practical_optimizations_and_infinite_graphs">
     * Wikipedia</a>
     *
     * @param graph a graph
     * @param start the start vertex
     * @param goal the goal vertex
     * @return a path if traversal is possible, null otherwise
     */
    @Nullable
    public EdgePath<A> findAnyEdgePath( DirectedGraph<V, A> graph,
             V start,  V goal) {
        Deque<A> arrows = new ArrayDeque<>();
        BackLinkWithArrow<V, A> current = breadthFirstSearch(graph, start, goal);
        if (current == null) {
            return null;
        }
        for (BackLinkWithArrow<V, A> i = current; i.arrow != null; i = i.parent) {
            arrows.addFirst(i.arrow);
        }
        return new EdgePath<>(arrows);
    }

    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex.
     * <p>
     * This method uses a breadth first search and returns the first result that
     * it finds.
     * <p>
     * References:<br>
     * <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Practical_optimizations_and_infinite_graphs">
     * Wikipedia</a>
     *
     * @param graph a graph
     * @param start the start vertex
     * @param goal the goal vertex
     * @return a VertexPath if traversal is possible, null otherwise
     */
    @Nullable
    public VertexPath<V> findAnyVertexPath( DirectedGraph<V, A> graph,
             V start,  V goal) {
        Deque<V> vertices = new ArrayDeque<>();
        BackLinkWithArrow<V, A> current = breadthFirstSearch(graph, start, goal);
        if (current == null) {
            return null;
        }
        for (BackLinkWithArrow<V, A> i = current; i != null; i = i.parent) {
            vertices.addFirst(i.vertex);
        }
        return new VertexPath<>(vertices);
    }

    /**
     * Builds a VertexPath through the graph which traverses the specified
     * waypoints.
     * <p>
     * This method uses breadth first search. It returns the first path that it
     * finds with this search strategy.
     *
     * @param graph a graph
     * @param waypoints waypoints, the iteration sequence of this collection
     * determines how the waypoints are traversed
     * @return a VertexPath if traversal is possible, null otherwise
     */
    @Nullable
    public VertexPath<V> findAnyVertexPath( DirectedGraph<V, A> graph,  Collection<V> waypoints) {
        Iterator<V> i = waypoints.iterator();
        List<V> pathElements = new ArrayList<>(16);
        if (!i.hasNext()) {
            return null;
        }
        V start = i.next();
        pathElements.add(start); // root element
        while (i.hasNext()) {
            V goal = i.next();
            BackLinkWithArrow<V, A> back = breadthFirstSearch(graph, start, goal);
            if (back == null) {
                return null;
            } else {
                int index = pathElements.size();
                for (; back != null; back = back.parent) {
                    pathElements.add(index, back.vertex);
                }
            }
            start = goal;
        }
        return new VertexPath<>(pathElements);
    }

    @Nullable
    public EdgePath<A> findIntShortestEdgePath( AttributedIntDirectedGraph<?,A> graph,
            int start, int goal,  ToDoubleFunction<A> costf) {

        IntNodeWithCost<A> node = findIntShortestPath(graph, start, goal, costf);
        if (node == null) {
            return null;
        }
        // 
        ArrayDeque<A> arrows = new ArrayDeque<>();
        for (IntNodeWithCost<A> parent = node; parent != null && parent.arrow != null; parent = parent.parent) {
            arrows.addFirst(parent.arrow);
        }
        return new EdgePath<>(arrows);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Nullable
    private IntNodeWithCost<A> findIntShortestPath( AttributedIntDirectedGraph<?, A> graph,
             int start,  int goal,  ToDoubleFunction<A> costf) {
        final int vertexCount = graph.getVertexCount();
        if (vertexCount == 0) {
            return null;
        }

        if (intFrontierMap == null || intFrontierMap.length < vertexCount) {
            intFrontierMap = new IntNodeWithCost[vertexCount];
            intExplored = new BitSet(vertexCount);
        }
        if (intFrontier == null) {
            intFrontier = new PriorityQueue<>(16);
        }

        IntNodeWithCost<A> result = doFindIntShortestPath(start, intFrontier, intFrontierMap, goal, intExplored, graph, costf);
        intFrontier.clear();
        intExplored.clear();
        Arrays.fill(intFrontierMap, null);// clears array to prevent build-up of garbage
        return result;
    }

    @Nullable
    public VertexPath<Integer> findIntShortestVertexPath( AttributedIntDirectedGraph<?,A> graph,
            int start, int goal,  ToDoubleFunction<A> costf) {

        IntNodeWithCost<A> node = findIntShortestPath(graph, start, goal, costf);
        if (node == null) {
            return null;
        }
        //
        ArrayDeque<Integer> vertices = new ArrayDeque<>();
        for (IntNodeWithCost<A> parent = node; parent != null; parent = parent.parent) {
            vertices.addFirst(parent.vertex);
        }
        return new VertexPath<>(vertices);
    }

    @Nullable
    public EdgePath<A> findShortestEdgePath( DirectedGraph<V, A> graph,
             V start,  V goal) {
        return findShortestEdgePath(graph, start, goal, costFunction);
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
     * @param graph a graph
     * @param start the start vertex
     * @param goal the goal vertex
     * @param costf the cost function
     * @return a VertexPath if traversal is possible
     */
    @Nullable
    public EdgePath<A> findShortestEdgePath( DirectedGraph<V, A> graph,
             V start,  V goal,  ToDoubleFunction<A> costf) {

        if (graph instanceof IntDirectedGraph) {
            @SuppressWarnings("unchecked")
            AttributedIntDirectedGraph<V,A> intGraph = (AttributedIntDirectedGraph<V,A>) graph;
            int startIndex = -1, goalIndex = -1;
            for (int i = 0, n = graph.getVertexCount(); i < n; i++) {
                V v = graph.getVertex(i);
                if (v == start) {
                    startIndex = i;
                    if (goalIndex != -1) {
                        break;
                    }
                }
                if (v == goal) {
                    goalIndex = i;
                    if (startIndex != -1) {
                        break;
                    }
                }
            }
            return findIntShortestEdgePath(intGraph, startIndex, goalIndex, costf);
        } else {
            return doFindShortestEdgePath(graph, start, goal, costf);
        }
    }

    @Nullable
    private NodeWithCost<V, A> findShortestPath( DirectedGraph<V, A> graph,
             V start,  V goal,  ToDoubleFunction<A> costf) {
        // Size of priority queue and frontierMap is the expected size of the frontier.
        // We use a size that is smaller than 256 bytes (assuming 12 bytes for object header).
        PriorityQueue< NodeWithCost<V, A>> frontier = new PriorityQueue<>(61);
        Map<V, NodeWithCost<V, A>> frontierMap = new HashMap<>(61);
        // Size of explored is the expected number of vertices that we need to explore.
        Set<V> explored = new HashSet<>(61);
        return doFindShortestPath(start, frontier, frontierMap, goal, explored, graph, costf);
    }

    @Nullable
    public VertexPath<V> findShortestVertexPath( DirectedGraph<V, A> graph,
             V start,  V goal) {
        return findShortestVertexPath(graph, start, goal, costFunction);
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
     * @param graph a graph
     * @param start the start vertex
     * @param goal the goal vertex
     * @param costf the cost function
     * @return a VertexPath if traversal is possible
     */
    @Nullable
    public VertexPath<V> findShortestVertexPath( DirectedGraph<V, A> graph,
             V start,  V goal,  ToDoubleFunction<A> costf) {
        if (graph instanceof IntDirectedGraph) {
            return doFindIntShortestVertexPath(graph, start, goal, costf);
        } else {
            return doFindShortestVertexPath(graph, start, goal, costf);
        }
    }

    private static class BackLinkWithArrow<VV, EE> {

        final BackLinkWithArrow<VV, EE> parent;
        final VV vertex;
        final EE arrow;

        public BackLinkWithArrow(VV vertex, BackLinkWithArrow<VV, EE> parent, EE arrow) {
            this.vertex = vertex;
            this.parent = parent;
            this.arrow = arrow;
        }

    }

    private static class IntNodeWithCost<E> implements Comparable<IntNodeWithCost<E>> {

        private IntNodeWithCost<E> parent;
        private final int vertex;
        private double cost;
        private E arrow;

        public IntNodeWithCost(int node, double cost, IntNodeWithCost<E> parent, E arrow) {
            this.vertex = node;
            this.cost = cost;
            this.parent = parent;
            this.arrow = arrow;
        }

        @Override
        public int compareTo(IntNodeWithCost<E> that) {
            return Double.compare(this.cost, that.cost);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IntNodeWithCost<?> other = (IntNodeWithCost<?>) obj;
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

        public IntNodeWithCost<E> getParent() {
            return parent;
        }

        public void setParent(IntNodeWithCost<E> parent) {
            this.parent = parent;
        }

        public int getVertex() {
            return vertex;
        }

        @Override
        public int hashCode() {
            return vertex;
        }
    }

    private static class NodeWithCost<V, E> implements Comparable<NodeWithCost<V, E>> {

        private NodeWithCost<V, E> parent;
        private final V vertex;
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
        public boolean equals(Object obj) {
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
