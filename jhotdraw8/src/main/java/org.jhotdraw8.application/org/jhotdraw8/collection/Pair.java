/*
 * @(#)Pair.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;


/**
 * Pair.
 *
 * @param <U> the type of the first element
 * @param <V> the type of the second element
 * @author Werner Randelshofer
 */
public interface Pair<U, V> {

     U first();

     V second();
}
