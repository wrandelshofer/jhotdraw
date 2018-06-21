/* @(#)PreorderSpliterator.java
 *  Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Spliterator;
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

    private final Deque<Iterator<T>> stack = new ArrayDeque<>();
    private final Function<T, Iterable<T>> getChildrenFunction;

    public PreorderSpliterator(T root, Function<T, Iterable<T>> getChildrenFunction) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        stack.push(Collections.singleton(root).iterator());
        this.getChildrenFunction = getChildrenFunction;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> consumer) {
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
