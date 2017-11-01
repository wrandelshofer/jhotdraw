/* @(#)Pair.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jetbrains.annotations.NotNull;


/**
 * Pair.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <V> the type of the objects which form a pair
 */
public interface Pair<V> {

    @NotNull
    V getStart();

    @NotNull
    V getEnd();
}
