/*
 * @(#)Pair.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;


/**
 * This interface defines a read-only API for a pair of values -
 * the pair can be ordered or unordered.
 *
 * @param <U> the type of the first element
 * @param <V> the type of the second element
 * @author Werner Randelshofer
 */
public interface Pair<U, V> {

     U first();

     V second();
}
