/*
 * @(#)PreorderSpliterator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import org.jhotdraw8.collection.AbstractEnumeratorSpliterator;
import org.jhotdraw8.collection.Enumerator;
import org.jhotdraw8.collection.SingletonEnumerator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

/**
 * PreorderSpliterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PreorderEnumerator<T> extends AbstractEnumeratorSpliterator<T> {
    private final Function<T, Enumerator<T>> getChildrenFunction;
    private final Deque<Enumerator<T>> stack = new ArrayDeque<>();

    public PreorderEnumerator(Function<T, Enumerator<T>> getChildrenFunction, T root) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        SingletonEnumerator<T> e = new SingletonEnumerator<>(root);
        e.moveNext();
        stack.push(e);
        this.getChildrenFunction = getChildrenFunction;
    }

    @Override
    public boolean moveNext() {
        Enumerator<T> iter = stack.peek();
        if (iter == null) {
            return false;
        }

        current = iter.current();
        if (!iter.moveNext()) {
            stack.pop();
        }
        Enumerator<T> children = getChildrenFunction.apply(current);
        if (children.moveNext()) {
            stack.push(children);
        }
        return true;
    }
}
