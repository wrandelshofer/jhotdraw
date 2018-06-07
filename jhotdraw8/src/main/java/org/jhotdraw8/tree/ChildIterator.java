/* @(#)ChildIterator.java
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.tree;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.IntFunction;

/**
 * Iterates over the children of a tree node.
 * 
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ChildIterator<T> implements Iterator<T> {
    private final IntFunction<T> getElementFunction;
    private final int size;
    private int next;

    /**
     * Creates a new instance.
     * 
     * @param childCount the number of children
     * @param getElementFunction return a child element given an index
     */
    public ChildIterator(int childCount, IntFunction<T> getElementFunction) {
        this.size=childCount;
        this.getElementFunction = getElementFunction;
        this.next=0;
    }
    
    @Override
    public boolean hasNext() {
        return next<size;
    }

    @Override
    public T next() {
       if (next<size) {
           next++;
           return getElementFunction.apply(next);
       }
       throw new NoSuchElementException();
    }

}
