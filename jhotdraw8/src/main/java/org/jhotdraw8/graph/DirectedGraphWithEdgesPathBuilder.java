/* @(#)DirectedGraphPathBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import static java.lang.Math.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * DirectedGraphPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 */
public class DirectedGraphWithEdgesPathBuilder<V, E> {

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
     * @throws org.jhotdraw8.graph.PathBuilderException if traversal is not
     * possible
     */
    @Nullable
    public VertexPath<V> findShortestVertexPath(@Nonnull DirectedGraphWithEdges<V, E> graph, 
            @Nonnull V start, @Nonnull V goal, @Nonnull ToDoubleFunction<E> costf)  {
        
        NodeWithCost<V,E> node = findShortestPath(graph, start, goal, costf);
        if (node==null)return null;
        // 
        LinkedList<V> vertices=new LinkedList<>();
        for (NodeWithCost<V,E> parent = node; parent != null; parent = parent.parent) {
            vertices.addFirst(parent.vertex);
        }
        return new VertexPath<>(vertices);
    }
    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex.
     * <p>
     * This method uses a breadth first search and returns the first result that it finds.
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
    public VertexPath<V> findAnyVertexPath(@Nonnull DirectedGraphWithEdges<V, E> graph, 
            @Nonnull V start, @Nonnull V goal)  {
        Deque<V> vertices=new ArrayDeque<>();
        BackLinkWithEdge<V,E> current = breadthFirstSearchWithEdges(graph, start, goal);
        if (current==null)return null;
        for (BackLinkWithEdge<V, E> i = current; i != null; i = i.parent) {
            vertices.addFirst(i.vertex);
        }
        return new VertexPath<>(vertices);
    }
    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex.
     * <p>
     * This method uses a breadth first search and returns the first result that it finds.
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
    public EdgePath<E> findAnyEdgePath(@Nonnull DirectedGraphWithEdges<V, E> graph, 
            @Nonnull V start, @Nonnull V goal)  {
        Deque<E> edges=new ArrayDeque<>();
        BackLinkWithEdge<V,E> current = breadthFirstSearchWithEdges(graph, start, goal);
        if (current==null)return null;
        for (BackLinkWithEdge<V, E> i = current; i.edge != null; i = i.parent) {
            edges.addFirst(i.edge);
        }
        return new EdgePath<>(edges);
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
    public EdgePath<E> findShortestEdgePath(@Nonnull DirectedGraphWithEdges<V, E> graph, 
            @Nonnull V start, @Nonnull V goal, @Nonnull ToDoubleFunction<E> costf)  {
        
        NodeWithCost<V,E> node = findShortestPath(graph, start, goal, costf);
        if (node==null)return null;
        // 
        LinkedList<E> edges=new LinkedList<>();
        for (NodeWithCost<V,E> parent = node; parent != null&&parent.edge!=null; parent = parent.parent) {
            edges.addFirst(parent.edge);
        }
        return new EdgePath<>(edges);
    }
    
    @Nullable
    private NodeWithCost<V,E> findShortestPath(@Nonnull DirectedGraphWithEdges<V, E> graph, 
            @Nonnull V start, @Nonnull V goal, @Nonnull ToDoubleFunction<E> costf)  {
        NodeWithCost<V,E> node = new NodeWithCost<>(start, 0.0, null,null);
        PriorityQueue< NodeWithCost<V,E>> frontier = new PriorityQueue<>();
        frontier.add(node);
        Set<V> explored = new HashSet<>(graph.getVertexCount());
        Map<V, NodeWithCost<V,E>> frontierMap = new HashMap<>(graph.getVertexCount());
        while (true) {
            if (frontier.isEmpty()) {
                return null;
            }
            node = frontier.poll();
            final V vertex = node.vertex;
            frontierMap.remove(vertex);
            if (vertex== goal) {
                break;
            }
            explored.add(node.getVertex());
            for (int i = 0, count = graph.getNextCount(vertex); i < count; i++) {
                V next = graph.getNext(vertex, i);
                final E edge = graph.getEdge(vertex, i);
                double cost = node.cost + costf.applyAsDouble(edge);
                NodeWithCost<V,E> nwc = new NodeWithCost<>(next, cost, node, edge);
               
                boolean isInFrontier = frontierMap.containsKey(next);
                if (!explored.contains(next) && !isInFrontier) {
                    frontier.add(nwc);
                    frontierMap.put(next,nwc);
                } else if (isInFrontier) {
                    NodeWithCost<V,E> nwcInFrontier = frontierMap.put(next, nwc);
                    if (nwcInFrontier.cost > cost) {
                        frontier.remove(nwcInFrontier);
                        frontier.add(nwc);
                    }
                }
            }
        }

        return node;
    }

    private static class NodeWithCost<V,E> implements Comparable<NodeWithCost<V,E>> {

        private NodeWithCost<V,E> parent;
        private final V vertex;
        private double cost;
        private E edge;

        public NodeWithCost(V node, double cost, NodeWithCost<V,E> parent, E edge) {
            this.vertex = node;
            this.cost = cost;
            this.parent = parent;
            this.edge=edge;
        }

        @Override
        public int compareTo(NodeWithCost<V,E> that) {
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
            final NodeWithCost<?,?> other = (NodeWithCost<?,?>) obj;
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

        public NodeWithCost<V,E> getParent() {
            return parent;
        }

        public void setParent(NodeWithCost<V,E> parent) {
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
    private static <V, E> BackLinkWithEdge<V, E> breadthFirstSearchWithEdges(DirectedGraphWithEdges<V, E> graph, V root, V goal, Predicate<V> visited) {
        Queue<BackLinkWithEdge<V, E>> queue = new ArrayDeque<>(max(1, min(graph.getVertexCount(), graph.getEdgeCount())));
        BackLinkWithEdge<V, E> rootBackLink = new BackLinkWithEdge<>(root, null, null);// temporaly allocated objects producing lots of garbage
        visited.test(root);
        queue.add(rootBackLink);
        BackLinkWithEdge<V, E> current = null;
        while (!queue.isEmpty()) {
            current = queue.remove();
            if (current.vertex == goal) {
                break;
            }
            for (int i = 0, n = graph.getNextCount(current.vertex); i < n; i++) {
                V next = graph.getNext(current.vertex, i);
                E edge = graph.getEdge(current.vertex, i);
                if (visited.test(next)) {
                    BackLinkWithEdge<V, E> backLink = new BackLinkWithEdge<>(next, current, edge);
                    queue.add(backLink);
                }
            }
        }
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
    private static <V, E> BackLinkWithEdge<V,E> breadthFirstSearchWithEdges(DirectedGraphWithEdges<V, E> graph, V root, V goal) {
        Set<V> visitedSet = new HashSet<>(graph.getVertexCount());// HashSet has a large O(1) cost.
       return breadthFirstSearchWithEdges(graph, root, goal, visitedSet::add);
    }
    
    

    private static class BackLinkWithEdge<VV, EE> {

        final EE edge;
        final BackLinkWithEdge<VV, EE> parent;
        final VV vertex;

        public BackLinkWithEdge(VV vertex, BackLinkWithEdge<VV, EE> parent, EE edge) {
            this.vertex = vertex;
            this.parent = parent;
            this.edge = edge;
        }

    }



}
