/* @(#)ReversedList.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util;

import java.util.*;

/**
 * A ReversedList provides an unmodifiable view on a List in reverse order.
 *
 * @author wrandels
 * @param <T> the list type
 */
public class ReversedList<T> extends AbstractList<T> {

    private List<T> target;

    /**
     * Creates a new instance of ReversedList.
     *
     * @param target the target list
     */
        @SuppressWarnings("unchecked")
    public ReversedList(Collection<T> target) {
        this.target = (target instanceof List) ? (List)target: new ArrayList<>(target); 
    }

    @Override
    public T get(int index) {
        return target.get(target.size() - 1 - index);
    }

    @Override
    public int size() {
        return target.size();
    }

}
