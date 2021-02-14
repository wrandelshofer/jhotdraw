/*
 * @(#)AbstractIntPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.function.AddToIntSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.IntPredicate;

public abstract class AbstractIntPathBuilder {
    private final @NonNull Function<Integer, Spliterator.OfInt> nextNodesFunction;
    private int maxLength = Integer.MAX_VALUE;

    public AbstractIntPathBuilder(@NonNull Function<Integer, Spliterator.OfInt> nextNodesFunction) {
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
    public @Nullable VertexPath<Integer> findVertexPath(int start, int goal) {
        return findVertexPath(start, i -> i == goal);
    }

    public boolean isReachable(int start, int goal) {
        return isReachable(start, i -> i == goal);
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
    public @Nullable VertexPath<Integer> findVertexPath(int start, @NonNull IntPredicate goalPredicate) {
        BackLink current = search(start, goalPredicate, addToBitSet(new BitSet()));
        if (current == null) {
            return null;
        }
        Deque<Integer> vertices = new ArrayDeque<Integer>();
        for (BackLink i = current; i != null; i = i.getParent()) {
            vertices.addFirst(i.getVertex());
        }
        return new VertexPath<>(vertices);
    }

    private static AddToIntSet addToBitSet(BitSet bitSet) {
        return i -> {
            boolean b = bitSet.get(i);
            if (!b) {
                bitSet.set(i);
            }
            return !b;
        };
    }

    public boolean isReachable(int start, @NonNull IntPredicate goalPredicate) {
        BackLink current = search(start, goalPredicate, addToBitSet(new BitSet()));
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
    public @Nullable VertexPath<Integer> findVertexPathOverWaypoints(@NonNull Iterable<Integer> waypoints) {
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
    public @Nullable VertexPath<Integer> findVertexPathOverWaypointsNonNull(@NonNull Iterable<Integer> waypoints) throws PathBuilderException {
        Iterator<Integer> i = waypoints.iterator();
        List<Integer> pathElements = new ArrayList<>(16);
        if (!i.hasNext()) {
            return null;
        }
        int start = i.next();
        pathElements.add(start); // root element
        while (i.hasNext()) {
            int goal = i.next();
            BackLink back = search(start, vi -> vi == goal,
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

    public @NonNull Function<Integer, Spliterator.OfInt> getNextNodesFunction() {
        return nextNodesFunction;
    }

    private @Nullable BackLink search(int start,
                                      @NonNull IntPredicate goalPredicate,
                                      @NonNull AddToIntSet visited) {
        return search(start, goalPredicate, nextNodesFunction, visited, maxLength);
    }

    protected abstract @Nullable BackLink search(int start,
                                                 IntPredicate goal,
                                                 Function<Integer, Spliterator.OfInt> nextNodesFunction,
                                                 @NonNull AddToIntSet visited, int maxLength);

    protected abstract static class BackLink {
        abstract BackLink getParent();

        abstract int getVertex();
    }

}
