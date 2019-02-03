/* @(#)DirectedGraphPathBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * DirectedGraphPathBuilder.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DirectedGraphPathBuilder<V, A> {
    @Nonnull
    private final Function<V, Iterable<V>> nextNodesFunction;
    private Queue<BackLink<V>> queue;
    private Set<V> visitedSet;

    /**
     * Creates a new instance.
     *
     * @param graph a graph
     */
    public DirectedGraphPathBuilder(@Nonnull DirectedGraph<V,A> graph) {
        this.nextNodesFunction = graph::getNextVertices;
    }
    /**
     * Creates a new instance.
     *
     * @param nextNodesFunction Accessor function to next nodes in graph.
     */
    public DirectedGraphPathBuilder(@Nonnull Function<V, Iterable<V>> nextNodesFunction) {
        this.nextNodesFunction = nextNodesFunction;
    }

    /**
     * Breadth-first-search.
     *
     * @param root      the starting point of the search
     * @param goal      the goal of the search
     * @param visited   a predicate with side effect. The predicate returns true
     *                  if the specified vertex has been visited, and marks the specified vertex
     *                  as visited.
     * @param maxLength the maximal path length
     * @return a back link on success, null on failure
     */
    private BackLink<V> bfs(@Nonnull V root,
                            @Nonnull Predicate<V> goal,
                            @Nonnull Predicate<V> visited,
                            int maxLength) {
        if (queue == null) {
            queue = new ArrayDeque<>(16);
        }
        BackLink<V> rootBackLink = new BackLink<>(root, null, maxLength);
        visited.test(root);
        queue.add(rootBackLink);
        BackLink<V> current = null;
        while (!queue.isEmpty()) {
            current = queue.remove();
            if (goal.test(current.vertex)) {
                break;
            }
            if (current.depth > 0) {
                for (V next : nextNodesFunction.apply(current.vertex)) {
                    if (visited.test(next)) {
                        BackLink<V> backLink = new BackLink<>(next, current, current.depth - 1);
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
     * @param root      the starting point of the search
     * @param goal      the goal of the search
     * @param maxLength the maximal path length
     * @return the path elements. Returns an empty list if there is no path. The
     * list is mutable.
     */
    @Nullable
    private BackLink<V> bfs(@Nonnull V root,
                            @Nonnull Predicate<V> goal,
                            int maxLength) {
        if (visitedSet == null) {
            visitedSet = new HashSet<>();
        }
        BackLink<V> result = bfs(root, goal, visitedSet::add, maxLength);
        visitedSet.clear();
        return result;
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
     * @param start the start vertex
     * @param goal  the goal vertex
     * @return a VertexPath if traversal is possible, null otherwise
     */
    @Nullable
    public VertexPath<V> findVertexPath(@Nonnull V start, @Nonnull V goal) {
        return findVertexPath(start, goal::equals, Integer.MAX_VALUE);
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
     * @param start         the start vertex
     * @param goalPredicate the goal predicate
     * @param maxLength     the maximal path length
     * @return a VertexPath if traversal is possible, null otherwise
     */
    @Nullable
    public VertexPath<V> findVertexPath(@Nonnull V start, @Nonnull Predicate<V> goalPredicate, int maxLength) {
        Deque<V> vertices = new ArrayDeque<>();
        BackLink<V> current = bfs(start, goalPredicate, maxLength);
        if (current == null) {
            return null;
        }
        for (BackLink<V> i = current; i != null; i = i.parent) {
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
     * @param waypoints waypoints, the iteration sequence of this collection
     *                  determines how the waypoints are traversed
     * @param maxLength the maximal path length
     * @return a VertexPath if traversal is possible, null otherwise
     */
    @Nullable
    public VertexPath<V> findVertexPathOverWaypoints(@Nonnull Collection<? extends V> waypoints,
                                                     int maxLength) {
        Iterator<? extends V> i = waypoints.iterator();
        List<V> pathElements = new ArrayList<>(16);
        if (!i.hasNext()) {
            return null;
        }
        V start = i.next();
        pathElements.add(start); // root element
        while (i.hasNext()) {
            V goal = i.next();
            BackLink<V> back = bfs(start, goal::equals, maxLength);
            if (back == null) {
                return null;
            } else {
                int index = pathElements.size();
                for (; back.parent != null; back = back.parent) {
                    pathElements.add(index, back.vertex);
                }
            }
            start = goal;
        }
        return new VertexPath<>(pathElements);
    }

    /**
     * Enumerates all vertex paths from start to goal up to the specified maximal path length.
     *
     * @param start     the start vertex
     * @param goal      the goal predicate
     * @param maxLength the maximal length of a path
     * @return the enumerated paths
     */
    public List<VertexPath<V>> findAllVertexPaths(@Nonnull V start,
                                                  @Nonnull Predicate<V> goal,
                                                  int maxLength) {
        List<BackLink<V>> backlinks = new ArrayList<>();
        dfsFindAllPaths(new BackLink<>(start, null, 1), goal, backlinks, maxLength);
        List<VertexPath<V>> vertexPaths = new ArrayList<>(backlinks.size());
        Deque<V> path = new ArrayDeque<>();
        for (BackLink<V> list : backlinks) {
            path.clear();
            for (BackLink<V> backlink = list; backlink != null; backlink = backlink.parent) {
                path.addFirst(backlink.vertex);
            }
            vertexPaths.add(new VertexPath<V>(path));
        }
        return vertexPaths;
    }

    private void dfsFindAllPaths(@Nonnull BackLink<V> current, @Nonnull Predicate<V> goal,
                                 @Nonnull List<BackLink<V>> backlinks, int maxDepth) {
        if (goal.test(current.vertex)) {
            backlinks.add(current);
            return;
        }

        if (current.depth < maxDepth) {
            for (V v : nextNodesFunction.apply(current.vertex)) {
                BackLink<V> newPath = new BackLink<>(v, current, current.depth + 1);
                dfsFindAllPaths(newPath, goal, backlinks, maxDepth);
            }
        }
    }

    private static class BackLink<VV> {

        final BackLink<VV> parent;
        final VV vertex;
        final int depth;

        public BackLink(VV vertex, BackLink<VV> parent, int depth) {
            this.vertex = vertex;
            this.parent = parent;
            this.depth = depth;
        }

    }

}
