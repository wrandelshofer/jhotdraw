/* @(#)AbstractTreeModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import javax.annotation.Nonnull;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import org.jhotdraw8.event.Listener;

/**
 * AbstractTreeModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
