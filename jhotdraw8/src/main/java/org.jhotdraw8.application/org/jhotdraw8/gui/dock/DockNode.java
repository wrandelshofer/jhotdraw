package org.jhotdraw8.gui.dock;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ReadOnlyList;

public interface DockNode {
    @NonNull
    ObjectProperty<Dock> dockParentProperty();

    @NonNull
    ReadOnlyList<DockNode> getDockChildrenReadOnly();

    @Nullable
    default DockPane getDockPane() {
        for (DockNode node = this; node != null; node = node.getDockParent()) {
            if (node instanceof DockPane) {
                return (DockPane) node;
            }
        }
        return null;
    }

    default @Nullable Dock getDockParent() {
        return dockParentProperty().get();
    }

    default void setDockParent(Dock value) {
        dockParentProperty().set(value);
    }

    @NonNull
    default Node getNode() {
        return nodeProperty().get();
    }

    @NonNull
    ReadOnlyObjectProperty<Node> nodeProperty();

}
