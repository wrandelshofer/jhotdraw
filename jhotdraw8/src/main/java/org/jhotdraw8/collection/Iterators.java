/* @(#)Collections.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.ArrayList;

/**
 * Collections.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Iterators {

    private Iterators() {
    }

    /**
     * Creates an eagerly copied list from an iterator.
     *
     * @param <T> the value type
     * @param iterable the iterable
     * @return the list
     */
    public static <T> ArrayList<T> toList(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
