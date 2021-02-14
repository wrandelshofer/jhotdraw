/*
 * @(#)PostorderSpliterator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
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
    private final @NonNull Function<T, Iterable<T>> getChildrenFunction;
    private @Nullable T root;
    private Spliterator<T> subtree;
    private Iterator<T> children;

    public PostorderSpliterator(@NonNull Function<T, Iterable<T>> getChildrenFunction, T root) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        this.getChildrenFunction = getChildrenFunction;
        this.root = root;
        children = getChildrenFunction.apply(root).iterator();
        subtree = Spliterators.emptySpliterator();
    }

    @Override
    public boolean tryAdvance(@NonNull Consumer<? super T> consumer) {
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
