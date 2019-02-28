/* @(#)InorderSpliterator.java
 *  Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * InorderSpliterator.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class InorderSpliterator<T> extends AbstractSpliterator<T> {
    @Nonnull
    private final Function<T, Iterable<T>> getChildrenFunction;
    @Nullable
    private T root;
    private Spliterator<T> subtree;
    private Iterator<T> children;

    public InorderSpliterator(Function<T, Iterable<T>> getChildrenFunction, T root) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        this.getChildrenFunction = getChildrenFunction;
        this.root = root;
        children = getChildrenFunction.apply(root).iterator();
        if (children.hasNext()) {
            subtree = new InorderSpliterator<>(getChildrenFunction, children.next());
        } else {
            subtree = Spliterators.emptySpliterator();
        }
    }

    @Override
    public boolean tryAdvance(@Nonnull Consumer<? super T> action) {
        if (root == null) {
            return false;
        }

        //noinspection StatementWithEmptyBody
        if (subtree.tryAdvance(action)) {
            // empty
        } else if (children.hasNext()) {
            subtree = new InorderSpliterator<>(getChildrenFunction, children.next());
            subtree.tryAdvance(action);
        } else {
            action.accept(root);
            root = null;
        }
        return true;
    }
}
