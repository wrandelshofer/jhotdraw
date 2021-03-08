/*
 * @(#)AbstractPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.function.AddToSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractPathBuilder<V, A> {
    private final @NonNull Function<V, Iterable<V>> nextNodesFunction;
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
     *
     * @param start the start vertex
     * @param goal  the goal vertex
     * @return a VertexPath if traversal is possible, null otherwise
     */
    public @Nullable VertexPath<V> findVertexPath(@NonNull V start, @NonNull V goal) {
        return findVertexPath(start, goal::equals);
    }

    public boolean isReachable(@NonNull V start, @NonNull V goal) {
        return isReachable(start, goal::equals);
    }

    /**
     * Builds a VertexPath through the graph which goes from the specified start
     * vertex to the specified goal vertex.
     * <p>
     * This method uses a breadth first search and returns the first result that
     * it finds.
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
     * @return a VertexPath if traversal is possible, null otherwise
     */
    public @Nullable VertexPath<V> findVertexPath(@NonNull V start, @NonNull Predicate<V> goalPredicate) {
        return findVertexPath(start, goalPredicate, new HashSet<>()::add);
    }

    public @Nullable VertexPath<V> findVertexPath(@NonNull V start, @NonNull Predicate<V> goalPredicate, @NonNull AddToSet<V> visited) {
        BackLink<V, A> current = search(start, goalPredicate, visited);
        if (current == null) {
            return null;
        }
        Deque<V> vertices = new ArrayDeque<>();
        for (BackLink<V, A> i = current; i != null; i = i.getParent()) {
            vertices.addFirst(i.getVertex());
        }
        return new VertexPath<>(vertices);
    }

    public boolean isReachable(@NonNull V start, @NonNull Predicate<V> goalPredicate) {
        BackLink<V, A> current = search(start, goalPredicate, new HashSet<>()::add);
        return current != null;
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
    public @Nullable VertexPath<V> findVertexPathOverWaypoints(@NonNull Iterable<? extends V> waypoints) {
        try {
            return findVertexPathOverWaypointsNonNull(waypoints);
        } catch (PathBuilderException e) {
            return null;
        }
    }

    public @Nullable VertexPath<V> findVertexPathOverWaypoints(@NonNull Iterable<? extends V> waypoints,
                                                               Runnable clearVisitedSet,
                                                               AddToSet<V> addToVisitedSet) {
        try {
            return findVertexPathOverWaypointsNonNull(waypoints, clearVisitedSet, addToVisitedSet);
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
    public @Nullable VertexPath<V> findVertexPathOverWaypointsNonNull(@NonNull Iterable<? extends V> waypoints) throws PathBuilderException {
        HashSet<Object> visitedSet = new HashSet<>();
        return findVertexPathOverWaypointsNonNull(waypoints, visitedSet::clear, visitedSet::add);
    }

    /**
     * Builds a VertexPath through the graph which traverses the specified
     * waypoints.
     * <p>
     * This method uses a breadth first path search between waypoints.
     *
     * @param waypoints       waypoints, the iteration sequence of this collection
     *                        determines how the waypoints are traversed
     * @param clearVisitedSet a method that clears the visited set
     * @param addToVisitedSet a method that adds a vertex to the visited set
     *                        and returns true if the vertex was not in the visited set
     * @return a VertexPath
     * @throws PathBuilderException if the path cannot be constructed
     */
    public @Nullable VertexPath<V> findVertexPathOverWaypointsNonNull(@NonNull Iterable<? extends V> waypoints,
                                                                      Runnable clearVisitedSet,
                                                                      AddToSet<V> addToVisitedSet) throws PathBuilderException {

        Iterator<? extends V> i = waypoints.iterator();
        List<V> pathElements = new ArrayList<>(16);
        if (!i.hasNext()) {
            return null;
        }
        V start = i.next();
        pathElements.add(start); // root element
        while (i.hasNext()) {
            V goal = i.next();
            clearVisitedSet.run();
            BackLink<V, A> back = search(start, goal::equals, addToVisitedSet);
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

    public @NonNull Function<V, Iterable<V>> getNextNodesFunction() {
        return nextNodesFunction;
    }

    private @Nullable BackLink<V, A> search(@NonNull V start,
                                            @NonNull Predicate<V> goalPredicate,
                                            @NonNull AddToSet<V> visited) {
        return search(start, goalPredicate, nextNodesFunction, visited, maxLength);
    }

    protected abstract @Nullable BackLink<V, A> search(V start,
                                                       Predicate<V> goal,
                                                       Function<V, Iterable<V>> nextNodesFunction,
                                                       @NonNull AddToSet<V> visited, int maxLength);

    protected abstract static class BackLink<VV, AA> {
        abstract BackLink<VV, AA> getParent();

        abstract VV getVertex();
    }

}
