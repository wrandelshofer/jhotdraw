/*
 * @(#)AbstractPathBuilder.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.function.AddToSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractPathBuilder<V, A> {
    @NonNull
    private final Function<V, Iterable<V>> nextNodesFunction;
    private int maxLength = Integer.MAX_VALUE;

    public AbstractPathBuilder(@NonNull Function<V, Iterable<V>> nextNodesFunction) {
        this.nextNodesFunction = nextNodesFunction;
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
    public VertexPath<V> findVertexPath(@NonNull V start, @NonNull V goal) {
        return findVertexPath(start, goal::equals);
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
     * @return a VertexPath if traversal is possible, null otherwise
     */
    @Nullable
    public VertexPath<V> findVertexPath(@NonNull V start, @NonNull Predicate<V> goalPredicate) {
        Deque<V> vertices = new ArrayDeque<>();
        BackLink<V, A> current = search(start, goalPredicate, new LinkedHashSet<>()::add);
        if (current == null) {
            return null;
        }
        for (BackLink<V, A> i = current; i != null; i = i.getParent()) {
            vertices.addFirst(i.getVertex());
        }
        return new VertexPath<>(vertices);
    }

    /**
     * Builds a VertexPath through the graph which traverses the specified
     * waypoints.
     * <p>
     * This method uses a breadth first path search between waypoints.
     *
     * @param waypoints waypoints, the iteration sequence of this collection
     *                  determines how the waypoints are traversed
     * @return a VertexPath if traversal is possible, null otherwise
     */
    @Nullable
    public VertexPath<V> findVertexPathOverWaypoints(@NonNull Iterable<? extends V> waypoints) {
        try {
            return findVertexPathOverWaypointsNonNull(waypoints);
        } catch (PathBuilderException e) {
            return null;
        }
    }

    /**
     * Builds a VertexPath through the graph which traverses the specified
     * waypoints.
     * <p>
     * This method uses a breadth first path search between waypoints.
     *
     * @param waypoints waypoints, the iteration sequence of this collection
     *                  determines how the waypoints are traversed
     * @return a VertexPath
     * @throws PathBuilderException if the path cannot be constructed
     */
    @Nullable
    public VertexPath<V> findVertexPathOverWaypointsNonNull(@NonNull Iterable<? extends V> waypoints) throws PathBuilderException {
        Iterator<? extends V> i = waypoints.iterator();
        List<V> pathElements = new ArrayList<>(16);
        if (!i.hasNext()) {
            return null;
        }
        V start = i.next();
        pathElements.add(start); // root element
        while (i.hasNext()) {
            V goal = i.next();
            BackLink<V, A> back = search(start, goal::equals,
                    new LinkedHashSet<>()::add);
            if (back == null) {
                throw new PathBuilderException("Could not find path from " + start + " to " + goal + ".");
            } else {
                int index = pathElements.size();
                for (; back.getParent() != null; back = back.getParent()) {
                    pathElements.add(index, back.getVertex());
                }
            }
            start = goal;
        }
        return new VertexPath<>(pathElements);
    }

    @NonNull
    public Function<V, Iterable<V>> getNextNodesFunction() {
        return nextNodesFunction;
    }

    @Nullable
    private BackLink<V, A> search(@NonNull V start,
                                  @NonNull Predicate<V> goalPredicate,
                                  @NonNull AddToSet<V> visited) {
        return search(start, goalPredicate, nextNodesFunction, visited, maxLength);
    }

    @Nullable
    protected abstract BackLink<V, A> search(V start,
                                             Predicate<V> goal,
                                             Function<V, Iterable<V>> nextNodesFunction,
                                             @NonNull AddToSet<V> visited, int maxLength);

    protected static abstract class BackLink<VV, AA> {
        abstract BackLink<VV, AA> getParent();

        abstract VV getVertex();
    }

}
