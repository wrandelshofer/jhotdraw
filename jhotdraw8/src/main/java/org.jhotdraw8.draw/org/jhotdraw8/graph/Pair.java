/* @(#)Pair.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;


/**
 * Pair.
 *
 * @param <V> the type of the objects which form a pair
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Pair<V> {

    V getStart();

    V getEnd();
}
