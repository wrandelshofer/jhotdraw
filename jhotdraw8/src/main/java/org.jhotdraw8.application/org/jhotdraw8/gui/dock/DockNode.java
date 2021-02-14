/*
 * @(#)DockNode.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ReadOnlyList;

/**
 * Represents a node in a tree structure.
 */
public interface DockNode {
    /**
     * The name of the {@link #dockParentProperty()}.
     */
    @NonNull
    String DOCK_PARENT_PROPERTY = "dockParent";

    /**
     * Gets the parent of this node.
     */
    @NonNull
    ObjectProperty<DockParent> dockParentProperty();

    /**
     * Gets the children of this node.
     *
     * @return the children
     */
    @NonNull
    ReadOnlyList<DockChild> getDockChildrenReadOnly();

    default @Nullable DockRoot getDockRoot() {
        for (DockNode node = this; node != null; node = node.getDockParent()) {
            if (node instanceof DockRoot) {
                return (DockRoot) node;
            }
        }
        return null;
    }

    default @Nullable DockParent getDockParent() {
        return dockParentProperty().get();
    }

    default void setDockParent(@Nullable DockParent value) {
        dockParentProperty().set(value);
    }

    @NonNull
    Node getNode();
}
