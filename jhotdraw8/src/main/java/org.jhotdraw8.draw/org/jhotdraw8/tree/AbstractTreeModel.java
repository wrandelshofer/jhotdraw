/*
 * @(#)AbstractTreeModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import javafx.beans.InvalidationListener;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.event.Listener;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * AbstractTreeModel.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractTreeModel<E> implements TreeModel<E> {

    private final CopyOnWriteArrayList<Listener<TreeModelEvent<E>>> treeModelListeners = new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<InvalidationListener> invalidationListeners = new CopyOnWriteArrayList<>();

    @Nonnull
    @Override
    final public CopyOnWriteArrayList<Listener<TreeModelEvent<E>>> getTreeModelListeners() {
        return treeModelListeners;
    }

    @Nonnull
    @Override
    final public CopyOnWriteArrayList<InvalidationListener> getInvalidationListeners() {
        return invalidationListeners;
    }
}
