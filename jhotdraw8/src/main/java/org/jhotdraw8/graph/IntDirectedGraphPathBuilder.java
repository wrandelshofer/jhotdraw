/* @(#)IntDirectedGraphPathBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.collection.IntArrayList;

/**
 * IntDirectedGraphPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntDirectedGraphPathBuilder {

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
    public VertexPath<Integer> buildAnyVertexPath(IntDirectedGraph graph,
            int start, int goal) throws PathBuilderException {
        VertexPath<Integer> pathElements = IntDirectedGraphPathBuilder.this.findAnyVertexPath(graph, start, goal);
        if (pathElements == null) {
            throw new PathBuilderException("Breadh first search stalled at vertex: " + start + ".");
        }
        return pathElements;
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
     * @return a VertexPath if traversal is possible
     * @throws org.jhotdraw8.graph.PathBuilderException if traversal is not
     * possible
     */
    @Nullable
    public VertexPath<Integer> findAnyVertexPath(IntDirectedGraph graph,
            int start, int goal) throws PathBuilderException {
        IntArrayList pathElements = new IntArrayList(graph.getVertexCount());
        pathElements.add(start);
        boolean success = breadthFirstSearchInt(graph, start, goal, pathElements);
        if (!success) {
            return null;
        } else {
            ArrayList<Integer> vertices = new ArrayList<>(pathElements.size());
            for (int i = 0, n = pathElements.size(); i < n; i++) {
                vertices.add(pathElements.get(i));
            }
            return new VertexPath<>(vertices);
        }
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
    @Nullable
    public VertexPath<Integer> findAnyVertexPath(IntDirectedGraph graph, Collection<Integer> waypoints) throws PathBuilderException {
        Iterator<Integer> i = waypoints.iterator();
        IntArrayList pathElements = new IntArrayList();
        if (!i.hasNext()) {
            return null;
        }
        int start = i.next();
        pathElements.add(start); // root element
        while (i.hasNext()) {
            int goal = i.next();
            boolean back = breadthFirstSearchInt(graph, start, goal, pathElements);
            if (back == false) {
                return null;
            }
            start = goal;
        }
        return new VertexPath<>(pathElements.addAllInto(new ArrayList<>(pathElements.size())));
    }

    /**
     * Breadth-first-search for IntDirectedGraph.
     *
     * @param graph a graph
     * @param start the starting point of the search
     * @param goal the goal of the search
     * @param pathElements Adds the resulting path to the provided list of path
     * elements. Does not add the root element.
     * @return true on success
     */
    private static <A> boolean breadthFirstSearchInt(IntDirectedGraph graph, int start, int goal, IntArrayList pathElements) {
        BitSet visited = new BitSet(graph.getVertexCount());
        QueueWithBackLinks queue = new QueueWithBackLinks(max(1, min(graph.getVertexCount(), graph.getArrowCount())));
        queue.add(start, SENTINEL);
        visited.set(start);
        int current = SENTINEL;
        while (!queue.isEmpty()) {
            current = queue.remove();
            if (current == goal) {
                break;
            }
            for (int i = 0, n = graph.getNextCount(current); i < n; i++) {
                int next = graph.getNext(current, i);
                if (!visited.get(next)) {
                    visited.set(next);
                    queue.add(next, queue.getIndexOfRemoved());
                }
            }
        }
        if (current == SENTINEL) {
            return false;
        }

        // Add the path to the pathElements list.
        // Part 1. Make room for the path elements.
        int insertionPoint = pathElements.size();
        for (int i = queue.getIndexOfRemoved(); queue.getVertex(i) != start; i = queue.getParentIndex(i)) {
            ++insertionPoint;
        }
        pathElements.setSize(insertionPoint);
        // Part 2. Add the path elements.
        for (int i = queue.getIndexOfRemoved(); queue.getVertex(i) != start; i = queue.getParentIndex(i)) {
            pathElements.set(--insertionPoint, queue.getVertex(i));
        }
        return true;
    }

    /**
     * Queue with back link store.
     * <p>
     * The back links are stored in the same data structure as the queue and can
     * be retrieved by index even after they have been removed from the queue.
     */
    private static class QueueWithBackLinks {

        final static int Q_NUM_FIELDS = 2;
        final static int Q_PARENT_INDEX = 1;
        final static int Q_VERTEX = 0;
        /**
         * Index at which the next element will be removed from the queue.
         */
        private int first = 0;
        /**
         * Index at which the next element will be added to the queue.
         */
        private int last = 0;

        private final int[] queue;

        public QueueWithBackLinks(int capacity) {
            this.queue = new int[capacity * Q_NUM_FIELDS];
        }

        /**
         * Adds an element to the end of the queue queue.
         *
         * @param vertex the value of the vertex property of the element
         * @param parentIndex the value of the parent index property of the
         * element
         * @throws IndexOutOfBoundsException if not enough capacity
         */
        public void add(int vertex, int parentIndex) {
            queue[last * Q_NUM_FIELDS + Q_VERTEX] = vertex;
            queue[last * Q_NUM_FIELDS + Q_PARENT_INDEX] = parentIndex;
            ++last;
        }

        /**
         * Gets the index of the last removed element.
         *
         * @return index or SENTINEL if no element has ever been removed
         */
        public int getIndexOfRemoved() {
            return first - 1;
        }

        /**
         * Gets the value of the parent index property of the specified element.
         *
         * @param index the index of an element
         * @return parent index (can be SENTINEL)
         * @throws IndexOutOfBoundsException if index is outside of bounds
         */
        public int getParentIndex(int index) {
            if (index >= last) {
                throw new IndexOutOfBoundsException("index(" + index + ") >= last(" + last + ")");
            }
            return queue[index * Q_NUM_FIELDS + Q_PARENT_INDEX];
        }

        /**
         * Gets the value of the vertex property of the specified element.
         *
         * @param index the index of an element
         * @return vertex
         * @throws IndexOutOfBoundsException if index is outside of bounds
         */
        public int getVertex(int index) {
            if (index >= last) {
                throw new IndexOutOfBoundsException("index(" + index + ") >= last(" + last + ")");
            }
            return queue[index * Q_NUM_FIELDS + Q_VERTEX];
        }

        /**
         * Tests if the queue is empty.
         *
         * @return true if empty.
         */
        public boolean isEmpty() {
            return last == first;
        }

        /**
         * Removes an element from the beginning of the queue.
         *
         * @return the index of the removed back link.
         * @throws NoSuchElementException if the queue is empty
         */
        public int remove() {
            if (isEmpty()) {
                throw new NoSuchElementException();
            }
            int vertex = queue[first * Q_NUM_FIELDS + Q_VERTEX];
            ++first;
            return vertex;
        }
    }
    private final static int SENTINEL = -1;

}
