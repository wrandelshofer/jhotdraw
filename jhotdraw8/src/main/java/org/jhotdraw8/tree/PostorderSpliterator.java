/* @(#)PostorderSpliterator.java
 *  Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import java.util.*;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * PreorderSpliterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PostorderSpliterator<T> extends AbstractSpliterator<T> {
    @Nonnull
    private final Function<T, Iterable<T>> getChildrenFunction;
    @Nullable
    private T root;
    private Spliterator<T> subtree;
    private Iterator<T> children;

    public PostorderSpliterator(Function<T, Iterable<T>> getChildrenFunction, T root) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        this.getChildrenFunction = getChildrenFunction;
        this.root = root;
        children = getChildrenFunction.apply(root).iterator();
        subtree = Spliterators.emptySpliterator();
    }

    @Override
    public boolean tryAdvance(@Nonnull Consumer<? super T> consumer) {
        if (root == null) {
            return false;
        }

        //noinspection StatementWithEmptyBody
        if (subtree.tryAdvance(consumer)) {
            // empty
        } else if (children.hasNext()) {
            subtree = new PostorderSpliterator<>(getChildrenFunction, children.next());
            subtree.tryAdvance(consumer);
        } else {
            consumer.accept(root);
            root = null;
        }
        return true;
    }
}
