/* @(#)DirectedGraphPathBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import static org.jhotdraw8.graph.DirectedGraphs.breadthFirstSearch;

/**
 * DirectedGraphPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
     * @return a VertexPath if traversal is possible
     * @throws org.jhotdraw8.graph.PathBuilderException if traversal is not
     * possible
     */
    public VertexPath<V> buildPath(DirectedGraph<V> graph, Collection<V> waypoints) throws PathBuilderException {
        Iterator<V> i = waypoints.iterator();
        List<V> pathElements = new ArrayList<>(graph.getVertexCount());
        if (!i.hasNext()) {
            throw new PathBuilderException("No waypoints provided");
        }
        V prev = i.next();
        pathElements.add(prev); // root element
        while (i.hasNext()) {
            V current = i.next();
            if (!breadthFirstSearch(graph, prev, current, pathElements)) {
                throw new PathBuilderException("Breadh first search stalled at vertex: " + current
                        + " waypoints: " + waypoints.stream().map(Object::toString).collect(Collectors.joining(", ")) + ".");
            }
            prev = current;
        }
        return new VertexPath<>(pathElements);
    }
}
