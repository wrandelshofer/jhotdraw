/* @(#)ReversedList.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.util;

import java.util.*;
/**
 * A ReversedList provides an unmodifiable view on a List in reverse order.
 *
 * @author wrandels
 */
public class ReversedList<T> extends AbstractList<T> {
    private List<T> target;
    
    /** Creates a new instance of ReversedList.
     * @param target the target list
     */
    public ReversedList(List<T> target) {
        this.target = target;
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
