/*
 * @(#)TreePresentationModel.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import javafx.scene.control.TreeItem;
import org.jhotdraw8.annotation.NonNull;
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
 * @author Werner Randelshofer
 */
public interface TreePresentationModel<N> {

    /**
     * The name of the model property.
     */
    String MODEL_PROPERTY = "model";

    default TreeModel<N> getTreeModel() {
        return treeModelProperty().get();
    }

    default void setTreeModel(TreeModel<N> newValue) {
        treeModelProperty().set(newValue);
    }

    @Nullable NonNullProperty<TreeModel<N>> treeModelProperty();

    @NonNull TreeItem<N> getRoot();

    boolean isUpdating();

    /**
     * Returns the tree item associated to the specified node.
     *
     * @param value the node value
     * @return a TreeItem. Returns null if no tree item has been associated to
     * the node because the tree is not expanded yet.
     */
    @Nullable
    TreeItem<N> getTreeItem(N value);

}
