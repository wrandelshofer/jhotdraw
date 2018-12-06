/* @(#)PreorderSpliterator.java
 *  Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import javax.annotation.Nonnull;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * PreorderSpliterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PreorderSpliterator<T> extends AbstractSpliterator<T> {
    private final Function<T, Iterable<T>> getChildrenFunction;
    private final Deque<Iterator<T>> stack = new ArrayDeque<>();

    public PreorderSpliterator(Function<T, Iterable<T>> getChildrenFunction, T root) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        stack.push(Collections.singleton(root).iterator());
        this.getChildrenFunction = getChildrenFunction;
    }

    @Override
    public boolean tryAdvance(@Nonnull Consumer<? super T> consumer) {
        Iterator<T> iter = stack.peek();
        if (iter == null) {
            return false;
        }

        T node = iter.next();
        if (!iter.hasNext()) {
            stack.pop();
        }
        Iterator<T> children = getChildrenFunction.apply(node).iterator();
        if (children.hasNext()) {
            stack.push(children);
        }
        consumer.accept(node);
        return true;
    }
}
