/* @(#)PreorderSpliterator.java
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * PreorderSpliterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PreorderSpliterator<T> implements Iterator<T>, Spliterator<T> {

    private final Deque<Iterator<T>> stack = new ArrayDeque<>();
    private final Function<T, Iterator<T>> getChildrenFunction;

    public PreorderSpliterator(T root, Function<T, Iterator<T>> getChildrenFunction) {
        stack.push(Collections.singleton(root).iterator());
        this.getChildrenFunction = getChildrenFunction;
    }

    @Override
    public int characteristics() {
        return ORDERED|NONNULL;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean hasNext() {
        return (!stack.isEmpty() && stack.peek().hasNext());
    }

    @Override
    public T next() {
        Iterator<T> iter = stack.peek();
        T node = iter.next();
        Iterator<T> children = getChildrenFunction.apply(node);

        if (!iter.hasNext()) {
            stack.pop();
        }
        if (children.hasNext()) {
            stack.push(children);
        }
        return node;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> consumer) {
        if (hasNext()){
            consumer.accept(next());
            return true;
        }
        return false;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> arg0) {
        Spliterator.super.forEachRemaining(arg0);
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }
}
