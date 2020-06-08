/*
 * @(#)Dock.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.collections.ObservableList;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.collection.ReadOnlyListWrapper;

public interface Dock extends DockNode {
    @NonNull
    DockAxis getDockAxis();

    @NonNull
    ObservableList<DockNode> getDockChildren();

    @Override
    default @NonNull ReadOnlyList<DockNode> getDockChildrenReadOnly() {
        return new ReadOnlyListWrapper<>(getDockChildren());
    }

    /**
     * Returns true if this track resizes the items. If this method returns
     * true, an item of the track should not provide resize controls.
     *
     * @return true if the track resizes items.
     */
    boolean isResizesDockChildren();

    default boolean isEditable() {
        return true;
    }


}
