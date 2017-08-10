/* @(#)SimpleTreePresentationModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.tree;

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
public class SimpleTreePresentationModel<N> extends AbstractTreePresentationModel<N> {

      private final Listener<TreeModelEvent<N>> modelHandler = new Listener<TreeModelEvent<N>>() {
        @Override
        public void handle(TreeModelEvent<N> event) {
            updating++;
            try {

                boolean structuralChange = false;
                N f = event.getNode();
                switch (event.getEventType()) {
                    case NODE_ADDED_TO_PARENT:
                        handleNodeAdded(f, event.getParent(), event.getIndex());
                        structuralChange = true;
                        break;
                    case NODE_REMOVED_FROM_PARENT:
                        handleNodeRemoved(f, event.getParent(), event.getIndex());
                        structuralChange = true;
                        break;
                    case NODE_ADDED_TO_TREE:
                        handleNodeAddedToTree(f, event.getParent(), event.getIndex());
                        structuralChange = true;
                        break;
                    case NODE_REMOVED_FROM_TREE:
                        handleNodeRemovedFromTree(f);
                        structuralChange = true;
                        break;
                    case NODE_CHANGED:
                        handleNodeInvalidated(f);
                        break;
                    case ROOT_CHANGED:
                        handleRootChanged();
                        structuralChange = true;
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


       protected void handleNewTreeModel(TreeModel<N> oldValue, TreeModel<N> newValue) {
        if (oldValue != null) {
            oldValue.removeTreeModelListener(modelHandler);
        }
        newValue.addTreeModelListener(modelHandler);
        handleRootChanged();
    }

}
