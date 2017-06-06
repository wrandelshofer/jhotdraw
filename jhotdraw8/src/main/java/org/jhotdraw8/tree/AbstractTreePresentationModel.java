/* @(#)TreePresentationModel.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.tree;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.TreeItem;
import org.jhotdraw8.beans.NonnullProperty;
import org.jhotdraw8.event.Listener;

/**
 * The {@code ETreePresentationModel} can be used to present a
 * {@code TreeModel} in a {@code TreeView} or a {@code TreeTableView}.
 * <p>
 * Maps {@code TreeModel} to a {@code TreeItem&lt;E&gt;} hierarchy.
 * <p>
 * Note: for performance reasons we do not expand the tree nodes by default.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <N> the node type
 */
public abstract class AbstractTreePresentationModel<N> implements TreePresentationModel<N> {

    private final TreeItem<N> root = new TreeItem<N>();

    private final Map<N, TreeItem<N>> items = new HashMap<>();

    private boolean reversed = true;

    protected int updating;

    /**
     * The drawingProperty holds the drawing that is presented by this drawing
     * view.
     */
    private final NonnullProperty<TreeModel<N>> treeModel //
            = new NonnullProperty<TreeModel<N>>(this, MODEL_PROPERTY, new SimpleTreeModel<N>()) {
        private TreeModel<N> oldValue = null;

        @Override
        protected void fireValueChangedEvent() {
            TreeModel<N> newValue = get();
            super.fireValueChangedEvent();
            handleNewTreeModel(oldValue, newValue);
            oldValue = newValue;
        }
    };

    public TreeModel<N> getTreeModel() {
        return treeModel.get();
    }

    public void setTreeModel(TreeModel<N> newValue) {
        treeModel.set(newValue);
    }

    public NonnullProperty<TreeModel<N>> treeModelProperty() {
        return treeModel;
    }

    protected abstract void handleNewTreeModel(TreeModel<N> oldValue, TreeModel<N> newValue);

    protected void handleRootChanged() {
        TreeModel<N> m=getTreeModel();
        N drawing = m.getRoot();
        root.setValue(drawing);
        root.getChildren().clear();
        items.clear();
        items.put(drawing, root);
        int childIndex = 0;
        if (drawing!=null) {
        for (int i=0,n=m.getChildCount(drawing);i<n;i++) {
         N child =m.getChildAt(drawing, i);
            handleNodeAddedToTree(child, drawing, childIndex);
            handleNodeAdded(child, drawing, childIndex);
            childIndex++;
        }}
    }

    protected void handleNodeAdded(N f, N parentE, int index) {
        TreeItem<N> item = items.get(f);
        TreeItem<N> newParent = items.get(parentE);
        if (reversed) {
            newParent.getChildren().add(newParent.getChildren().size() - index, item);
        } else {
            newParent.getChildren().add(index, item);
        }
    }

    protected void handleNodeRemoved(N f, N parentE, int index) {
        TreeItem<N> parent = items.get(parentE);
        if (reversed) {
            parent.getChildren().remove(parent.getChildren().size() - 1 - index);
        } else {
            parent.getChildren().remove(index);
        }
    }

    protected void handleNodeAddedToTree(N f, N parent, int index) {
        TreeModel<N> m=getTreeModel();
        TreeItem<N> item = new TreeItem<>(f);
        item.setExpanded(false);
        items.put(f, item);
        int childIndex = 0;
        for (int i=0,n=m.getChildCount(f);i<n;i++) {
         N child =m.getChildAt(f, i);
            handleNodeAddedToTree(child, f, childIndex);
            handleNodeAdded(child, f, childIndex);
            childIndex++;
        }
    }

    protected void handleNodeRemovedFromTree(N f) {
        items.remove(f);
    }

    protected void handleNodeInvalidated(N f) {
        TreeItem<N> node = items.get(f);
        if (node != null) {
            node.setValue(f);
        }
    }

    @Override
    public TreeItem<N> getRoot() {
        return root;
    }

    public TreeItem<N> getTreeItem(N f) {
        return items.get(f);
    }

    public N getValue(TreeItem<N> item) {
        return item.getValue();
    }

    public boolean isUpdating() {
        return updating > 0;
    }
}
