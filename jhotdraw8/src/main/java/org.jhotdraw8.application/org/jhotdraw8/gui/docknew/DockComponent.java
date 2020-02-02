package org.jhotdraw8.gui.docknew;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ReadOnlyList;

public interface DockComponent {
    ReadOnlyList<DockComponent> getChildComponentsReadOnly();

    Node getContent();

    @NonNull
    ReadOnlyObjectProperty<Node> contentReadOnlyProperty();

    @NonNull
    ObjectProperty<Dock> parentComponentProperty();

    default Dock getParentComponent() {
        return parentComponentProperty().get();
    }

    default void setParentComponent(Dock value) {
        parentComponentProperty().set(value);
    }

    @Nullable
    default RootDock getRoot() {
        for (DockComponent node = this; node != null; node = node.getParentComponent()) {
            if (node instanceof RootDock) {
                return (RootDock) node;
            }
        }
        return null;
    }
}
