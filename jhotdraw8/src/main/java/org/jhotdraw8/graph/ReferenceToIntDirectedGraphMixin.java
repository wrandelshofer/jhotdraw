/* @(#)ReferenceToIntDirectedGraphMixin.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.graph.DirectedGraph;

/**
 * ReferenceToIntDirectedGraphMixin.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <V> vertex type
 */
public interface ReferenceToIntDirectedGraphMixin<V> extends DirectedGraph<V>,
        IntDirectedGraph {

    @Override
    default V getNext(V v, int i) {
        return getVertex(getNext(indexOfVertex(v), i));
    }
    
    @Override
    default int getNextCount(V v) {
        return getNextCount(indexOfVertex(v));
    }
    
    int indexOfVertex(V v);

}
