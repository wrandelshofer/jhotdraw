/* @(#)SimpleTreePresentationModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.TreeItem;
import org.jhotdraw8.beans.NonnullProperty;
import org.jhotdraw8.event.Listener;
import static org.jhotdraw8.tree.TreePresentationModel.MODEL_PROPERTY;

/**
 * The {@code ETreePresentationModel} can be used to present a
 * {@code TreeModel} in a {@code TreeView} or a {@code TreeTableView}.
 * <p>
 * Maps {@code TreeModel} to a {@code TreeItem&lt;E&gt;} hierarchy.
 * <p>
 * Note: for performance reasons we do not expand the tree nodes by default.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleTreePresentationModel<N> extends AbstractTreePresentationModel<N> {


    private final TreeItem<N> root = new TreeItem<>();

    private final Map<N, TreeItem<N>> items = new HashMap<>();

    private boolean reversed = true;

    protected int updating;


    
    protected void handleRootChanged() {
        TreeModel<N> m = getTreeModel();
        N drawing = m.getRoot();
        root.setValue(drawing);
        root.getChildren().clear();
        items.clear();
        items.put(drawing, root);
        int childIndex = 0;
        if (drawing != null) {
            for (int i = 0, n = m.getChildCount(drawing); i < n; i++) {
                N child = m.getChildAt(drawing, i);
                handleNodeAddedToTree(child, drawing, childIndex);
                handleNodeAdded(child, drawing, childIndex);
                childIndex++;
            }
        }
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
        TreeModel<N> m = getTreeModel();
        TreeItem<N> item = new TreeItem<>(f);
        item.setExpanded(false);
        items.put(f, item);
        int childIndex = 0;
        for (int i = 0, n = m.getChildCount(f); i < n; i++) {
            N child = m.getChildAt(f, i);
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
    
    
      private final Listener<TreeModelEvent<N>> modelHandler = new Listener<TreeModelEvent<N>>() {
        @Override
        public void handle(TreeModelEvent<N> event) {
            updating++;
            try {

               N f = event.getNode();
                switch (event.getEventType()) {
                    case NODE_ADDED_TO_PARENT:
                        handleNodeAdded(f, event.getParent(), event.getIndex());
                        break;
                    case NODE_REMOVED_FROM_PARENT:
                        handleNodeRemoved(f, event.getParent(), event.getIndex());
                        break;
                    case NODE_ADDED_TO_TREE:
                        handleNodeAddedToTree(f, event.getParent(), event.getIndex());
                        break;
                    case NODE_REMOVED_FROM_TREE:
                        handleNodeRemovedFromTree(f);
                        break;
                    case NODE_CHANGED:
                        handleNodeInvalidated(f);
                        break;
                    case ROOT_CHANGED:
                        handleRootChanged();
                        break;
                    case SUBTREE_NODES_CHANGED:
                        break;
                    default:
                        throw new UnsupportedOperationException(event.getEventType()
                                + " not supported");
                }
            } finally {
                updating--;
            }
        }
    };



      @Override
       protected void handleTreeModelChanged(TreeModel<N> oldValue, TreeModel<N> newValue) {
        if (oldValue != null) {
            oldValue.removeTreeModelListener(modelHandler);
        }
        newValue.addTreeModelListener(modelHandler);
        handleRootChanged();
    }

}
