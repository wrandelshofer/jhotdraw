/*
 * @(#)PreorderSpliterator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import org.jhotdraw8.collection.AbstractEnumeratorSpliterator;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Function;

/**
 * PreorderSpliterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PreorderSpliterator<T> extends AbstractEnumeratorSpliterator<T> {
    private final Function<T, Iterable<T>> getChildrenFunction;
    private final Deque<Iterator<T>> stack = new ArrayDeque<>();

    public PreorderSpliterator(Function<T, Iterable<T>> getChildrenFunction, T root) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        stack.push(Collections.singleton(root).iterator());
        this.getChildrenFunction = getChildrenFunction;
    }

    @Override
    public boolean moveNext() {
        Iterator<T> iter = stack.peek();
        if (iter == null) {
            return false;
        }

        current = iter.next();
        if (!iter.hasNext()) {
            stack.pop();
        }
        Iterator<T> children = getChildrenFunction.apply(current).iterator();
        if (children.hasNext()) {
            stack.push(children);
        }
        return true;
    }
}
