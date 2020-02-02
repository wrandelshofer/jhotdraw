/*
 * @(#)AbstractTreePresentationModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.NonNullProperty;

/**
 * The {@code TreePresentationModel} can be used to present a {@code TreeModel}
 * in a {@code TreeView} or a {@code TreeTableView}.
 * <p>
 * Maps {@code TreeModel} to a {@code TreeItem&lt;E&gt;} hierarchy.
 * <p>
 * Note: for performance reasons we do not expand the tree nodes by default.
 *
 * @param <N> the node type
 * @author Werner Randelshofer
 */
public abstract class AbstractTreePresentationModel<N> implements TreePresentationModel<N> {
    /**
     * Holds the underlying model.
     */
    @Nullable
    private final NonNullProperty<TreeModel<N>> treeModel //
            = new NonNullProperty<TreeModel<N>>(this, MODEL_PROPERTY, new SimpleTreeModel<>()) {
        @Nullable
        private TreeModel<N> oldValue = null;

        @Override
        protected void fireValueChangedEvent() {
            TreeModel<N> newValue = get();
            super.fireValueChangedEvent();
            onTreeModelChanged(oldValue, newValue);
            oldValue = newValue;
        }
    };

    @Nullable
    public NonNullProperty<TreeModel<N>> treeModelProperty() {
        return treeModel;
    }


    protected abstract void onTreeModelChanged(TreeModel<N> oldValue, TreeModel<N> newValue);
}
