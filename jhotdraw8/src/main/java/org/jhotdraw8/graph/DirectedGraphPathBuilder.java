/* @(#)DirectedGraphPathBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import static java.lang.Math.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * DirectedGraphPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the vertex type
 */
public class DirectedGraphPathBuilder<V> {

    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex.
     * <p>
     * This method uses breadth first search. It returns the first path that it
     * finds with this search strategy.
     *
     * @param graph a graph
     * @param start the start vertex
     * @param goal the goal vertex
     * @return a VertexPath if traversal is possible
     * @throws org.jhotdraw8.graph.PathBuilderException if traversal is not
     * possible
     */
    @Nonnull
    public VertexPath<V> buildAnyVertexPath(@Nonnull DirectedGraph<V> graph,
            @Nonnull V start, @Nonnull V goal) throws PathBuilderException {
        VertexPath<V> result = findAnyVertexPath(graph, start, goal);
        if (result == null) {
            throw new PathBuilderException("Breadh first search stalled at vertex: " + start + ".");
        }
        return result;
    }

    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex.
     * <p>
     * This method uses breadth first search. It returns the first path that it
     * finds with this search strategy.
     *
     * @param graph a graph
     * @param start the start vertex
     * @param goal the goal vertex
     * @return a VertexPath if traversal is possible, null otherwise
     */
    @Nullable
    public VertexPath<V> findAnyVertexPath(@Nonnull DirectedGraph<V> graph,
            @Nonnull V start,@Nonnull  V goal)  {
                Deque<V> pathElements = new ArrayDeque<>(graph.getVertexCount());
            BackLink<V> back = breadthFirstSearch(graph, start, goal);
            if (back==null) {
                return null;
            }else{
                for (;back!=null;back=back.parent) {
                    pathElements.addFirst( back.vertex);
                }
            }
        return new VertexPath<>(pathElements);
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
    @Nonnull
    public VertexPath<V> buildAnyVertexPath(@Nonnull DirectedGraph<V> graph, @Nonnull Collection<V> waypoints) throws PathBuilderException {
        Iterator<V> i = waypoints.iterator();
        Deque<V> pathElements = new ArrayDeque<>(graph.getVertexCount());
        if (!i.hasNext()) {
            throw new PathBuilderException("No waypoints provided");
        }
        V start = i.next();
        while (i.hasNext()) {
            V goal = i.next();
            BackLink<V> back = breadthFirstSearch(graph, start, goal);
            if (back==null) {
                throw new PathBuilderException("Breadh first search stalled at vertex: " + goal
                        + " waypoints: " + waypoints.stream().map(Object::toString).collect(Collectors.joining(", ")) + ".");
            }else{
                for (;back!=null;back=back.parent) {
                    pathElements.addFirst(back.vertex);
                }
            }
            
            start = goal;
        }
        return new VertexPath<>(pathElements);
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
    public VertexPath<V> findAnyVertexPath(@Nonnull DirectedGraph<V> graph, @Nonnull Collection<V> waypoints) {
        Iterator<V> i = waypoints.iterator();
        List<V> pathElements = new ArrayList<>(graph.getVertexCount());
        if (!i.hasNext()) {
            return null;
        }
        V start = i.next();
        pathElements.add(start); // root element
        while (i.hasNext()) {
            V goal = i.next();
            BackLink<V> back = breadthFirstSearch(graph, start, goal);
            if (back==null) {
                return null;
            }else{
                int index=pathElements.size();
                for (;back!=null;back=back.parent) {
                    pathElements.add(index, back.vertex);
                }
            }
            start = goal;
        }
        return new VertexPath<>(pathElements);
    }
    
    /**
     * Breadth-first-search.
     *
     * @param <V> the vertex type
     * @param graph a graph
     * @param root the starting point of the search
     * @param goal the goal of the search
     * @return non-null value on success,null on failure
     */
    @Nullable
    private static <V> BackLink<V> breadthFirstSearch(@Nonnull DirectedGraph<V> graph, @Nonnull V root, @Nonnull V goal) {
        Set<V> visitedSet = new HashSet<>(graph.getVertexCount());// HashSet has a large O(1) cost.
        return breadthFirstSearch(graph, root, goal, visitedSet::add);
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
     * @return true on success
     */
    private static <V> BackLink<V> breadthFirstSearch(DirectedGraph<V> graph, V root, V goal, Predicate<V> visited) {
        Queue<BackLink<V>> queue = new ArrayDeque<>(max(1, min(graph.getVertexCount(), graph.getEdgeCount())));
        BackLink<V> rootBackLink = new BackLink<>(root, null);// temporaly allocated objects producing lots of garbage
        visited.test(root);
        queue.add(rootBackLink);
        BackLink<V> current = null;
        while (!queue.isEmpty()) {
            current = queue.remove();
            if (current.vertex == goal) {
                break;
            }
            for (int i = 0, n = graph.getNextCount(current.vertex); i < n; i++) {
                V next = graph.getNext(current.vertex, i);
                if (visited.test(next)) {
                    BackLink<V> backLink = new BackLink<>(next, current);
                    queue.add(backLink);
                }
            }
        }
        if (current == null || current.vertex != goal) {
            return null;
        }
        return current;
    }

        private static class BackLink<VV> {

        final BackLink<VV> parent;
        final VV vertex;

        public BackLink(VV vertex, BackLink<VV> parent) {
            this.vertex = vertex;
            this.parent = parent;
        }

    }
}
