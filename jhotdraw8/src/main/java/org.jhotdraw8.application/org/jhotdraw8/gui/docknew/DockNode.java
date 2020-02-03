package org.jhotdraw8.gui.docknew;

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
    ReadOnlyObjectProperty<Node> nodeProperty();

    @NonNull
    ReadOnlyList<? extends DockNode> getDockChildrenReadOnly();

    @Nullable
    default Dock getDockParent() {
        return dockParentProperty().get();
    }

    default void setDockParent(@Nullable Dock value) {
        dockParentProperty().set(value);
    }

    @NonNull
    default Node getNode() {
        return nodeProperty().get();
    }

    @Nullable
    default DockPane getRoot() {
        for (DockNode node = this; node != null; node = node.getDockParent()) {
            if (node instanceof DockPane) {
                return (DockPane) node;
            }
        }
        return null;
    }

}
