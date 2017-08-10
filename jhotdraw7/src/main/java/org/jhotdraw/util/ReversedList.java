/* @(#)ReversedList.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */

package org.jhotdraw.util;

import java.util.*;
/**
 * A ReversedList provides in unmodifiable view on a List in reverse order.
 *
 * @author wrandels
 */
public class ReversedList<T> extends AbstractList<T> {
    private List<T> target;
    
    /** Creates a new instance of ReversedList */
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
