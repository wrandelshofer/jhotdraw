/* @(#)DirectedGraphValidator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.graph;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DirectedGraphValidator.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DirectedGraphValidator<V,A> {

    public DirectedGraphValidator() {
    }
    public boolean validate(@NonNull DirectedGraph<V,A> graph) {
        Set<V> vertices=new LinkedHashSet<>();
        for (V v:graph.getVertices()) {
            if (!vertices.add(v)) {
                return false;// duplicate vertex
            }
        }
        for (V v:vertices) {
            for (int j=0,m=graph.getNextCount(v);j<m;j++) {
                if (!vertices.contains(graph.getNext(v, j))) {
                    return false;// arrow points to vertex which is nit part of graph
                }
            }
        }
        return true;
    }
}
