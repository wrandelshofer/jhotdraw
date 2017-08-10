/* @(#)ReferenceToIntDirectedGraphMixin.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.graph.DirectedGraph;

/**
 * ReferenceToIntDirectedGraphMixin.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
