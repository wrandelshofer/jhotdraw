/* @(#)IntDirectedGraphs.java
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

import javax.annotation.Nonnull;

import java.util.function.IntFunction;

/**
 * IntDirectedGraphs.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntDirectedGraphs {
    /**
     * Dumps the graph for debugging purposes.
     *
     * @param graph the graph to be dumped
     * @return a String representation of the graph
     */
    public static String dumpAsAdjacencyMap(@Nonnull IntDirectedGraph graph) {
        return dumpAsAdjacencyMap(graph, Integer::toString);
    }

    /**
     * Dumps the graph for debugging purposes.
     *
     * @param graph the graph to be dumped
     * @param toStringFunction a function which converts a vertex to a string
     * @return a String representation of the graph
     */
    public static String dumpAsAdjacencyMap(IntDirectedGraph graph, @Nonnull IntFunction<String> toStringFunction) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0, nn = graph.getVertexCount(); i < nn; i++) {
            int v = i;
            if (buf.length() != 0) {
                buf.append("\n");
            }
            buf.append(toStringFunction.apply(v)).append(" -> ");
            for (int j = 0, n = graph.getNextCount(v); j < n; j++) {
                if (j != 0) {
                    buf.append(", ");
                }
                buf.append(toStringFunction.apply(graph.getNext(v, j)));
            }
            buf.append('.');
        }
        return buf.toString();
    }

    
    /**
     * Dumps a directed graph into a String which can be rendered with the "dot"
     * tool.
     *
     * @param g the graph to be dumped
     * @return a String representation of the graph
     */
    public static String dumpAsDot(@Nonnull IntDirectedGraph g) {
        return dumpAsDot(g, Integer::toString);
    }

    /**
     * Dumps a directed graph into a String which can be rendered with the "dot"
     * tool.
     *
     * @param g the graph to be dumped
     * @param toStringFunction a function which converts a vertex to a string
     * @return a String representation of the graph
     */
    public static String dumpAsDot(IntDirectedGraph g, @Nonnull IntFunction<String> toStringFunction) {
        StringBuilder b = new StringBuilder();

        for (int i = 0, n = g.getVertexCount(); i < n; i++) {
            int v = i;
            if (g.getNextCount(v) == 0) {
                b.append(toStringFunction.apply(v))
                        .append('\n');

            } else {
                for (int j = 0, m = g.getNextCount(v); j < m; j++) {
                    b.append(v)
                            .append(" -> ")
                            .append(toStringFunction.apply(g.getNext(v, j)))
                            .append('\n');
                }
            }
        }
        return b.toString();
    }
}
