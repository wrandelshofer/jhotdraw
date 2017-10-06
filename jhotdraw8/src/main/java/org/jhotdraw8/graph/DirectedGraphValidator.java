/* @(#)DirectedGraphValidator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DirectedGraphValidator.
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DirectedGraphValidator<V> {

    public DirectedGraphValidator() {
    }
    public boolean validate(DirectedGraph<V> graph) {
        Set<V> vertices=new LinkedHashSet<>();
        for (int i=0,n=graph.getVertexCount();i<n;i++) {
            if (!vertices.add(graph.getVertex(i))) {
                return false;// duplicate vertex
            }
        }
        for (V v:vertices) {
            for (int j=0,m=graph.getNextCount(v);j<m;j++) {
                if (!vertices.contains(graph.getNext(v, j))) {
                    return false;// edge points to vertex which is nit part of graph
                }
            }
        }
        return true;
    }
}
