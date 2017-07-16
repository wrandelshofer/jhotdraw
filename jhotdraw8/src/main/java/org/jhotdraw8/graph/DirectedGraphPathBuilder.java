/* @(#)DirectedGraphPathBuilder.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.graph.DirectedGraph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

/**
 * DirectedGraphPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <V> the vertex type
 */
public class DirectedGraphPathBuilder<V> {

    /**
     * Builds a VertexPath through the graph which traverses the specified
     * waypoints.
     *
     * @param graph a graph
     * @param waypoints waypoints, the iteration sequence of this collection
     * determines how the waypoints are traversed
     * @return a VertexPath if traversion is possible, null otherwise
     */
    public VertexPath<V> buildPath(DirectedGraph<V> graph, Collection<V> waypoints) {
        Iterator<V> i = waypoints.iterator();
        ArrayList<V> pathElements = new ArrayList<>();
        if (!i.hasNext()) {
            return null;
        }
        V prev = i.next();
        while (i.hasNext()) {
            V current = i.next();
            if (!breadthFirstSearch(graph, prev, current, pathElements)) {
                System.out.println("  DirectedGraphPathBuilder could not build path. found only:" + pathElements + " waypoints:" + waypoints);
                return null;
            }
            prev = current;
        }
        return new VertexPath<>(pathElements);
    }

    private static class BFSData<VV> {

        VV parent;
        VV node;
        boolean discovered;

        public BFSData(VV node) {
            this.node = node;
        }

    }

    /**
     * Breadth-first-search as described in Wikipedia.
     *
     * @param graph a graph
     * @param root the starting point of the search
     * @param goal the goal of the search
     * @param pathElements Adds the resulting path to the provided list of path
     * elements
     * @return true on success
     */
    private boolean breadthFirstSearch(DirectedGraph<V> graph, V root, V goal, ArrayList<V> pathElements) {
        Map<V, BFSData<V>> bfsData = new HashMap<>();
        Queue<V> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            V current = queue.remove();
            if (current == goal) {
                break;
            }
            for (int i = 0, n = graph.getNextCount(current); i < n; i++) {
                V next = graph.getNext(current, i);
                BFSData<V> nextData = bfsData.computeIfAbsent(next, BFSData::new);
                if (!nextData.discovered) {
                    nextData.discovered = true;
                    nextData.parent = current;
                }
                queue.add(next);
            }
        }
        BFSData<V> data = bfsData.get(goal);
        if (data == null) {
            return false;
        }
        int insertionPoint = pathElements.size();
        for (; data != null; data = bfsData.get(data.parent)) {
            pathElements.add(insertionPoint, data.node);
        }
        pathElements.add(insertionPoint, root);
        return true;
    }
}
