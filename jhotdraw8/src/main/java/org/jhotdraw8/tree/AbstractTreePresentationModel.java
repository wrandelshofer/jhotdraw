/* @(#)TreePresentationModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import javax.annotation.Nullable;
import org.jhotdraw8.beans.NonnullProperty;

/**
 * The {@code TreePresentationModel} can be used to present a {@code TreeModel}
 * in a {@code TreeView} or a {@code TreeTableView}.
 * <p>
 * Maps {@code TreeModel} to a {@code TreeItem&lt;E&gt;} hierarchy.
 * <p>
 * Note: for performance reasons we do not expand the tree nodes by default.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <N> the node type
 */
public abstract class AbstractTreePresentationModel<N> implements TreePresentationModel<N> {
    /**
     * Holds the underlying model.
     */
    @Nullable
    private final NonnullProperty<TreeModel<N>> treeModel //
            = new NonnullProperty<TreeModel<N>>(this, MODEL_PROPERTY, new SimpleTreeModel<N>()) {
        @Nullable
        private TreeModel<N> oldValue = null;

        @Override
        protected void fireValueChangedEvent() {
            TreeModel<N> newValue = get();
            super.fireValueChangedEvent();
            handleTreeModelChanged(oldValue, newValue);
            oldValue = newValue;
        }
    };

    @Nullable
    public NonnullProperty<TreeModel<N>> treeModelProperty() {
        return treeModel;
    }


       protected abstract void handleTreeModelChanged(TreeModel<N> oldValue, TreeModel<N> newValue);
}
