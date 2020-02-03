package org.jhotdraw8.gui.docknew;

import javafx.collections.ObservableList;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.collection.ReadOnlyListWrapper;

public interface Dock extends DockNode {
    @NonNull
    DockAxis getAxis();

    @NonNull
    ObservableList<DockNode> getDockChildren();

    @NonNull
    @Override
    default ReadOnlyList<DockNode> getDockChildrenReadOnly() {
        return new ReadOnlyListWrapper<>(getDockChildren());
    }

    default boolean isEditable() {
        return true;
    }


    /**
     * Returns true if this track resizes the items. If this method returns
     * true, an item of the track should not provide resize controls.
     *
     * @return true if the track resizes items.
     */
    default boolean isResizesItems() {
        return true;
    }
}
