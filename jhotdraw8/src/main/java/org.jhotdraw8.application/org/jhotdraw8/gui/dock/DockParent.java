/*
 * @(#)DockParent.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.collection.ReadOnlyListWrapper;

/**
 * A DockParent provides screen space for one or more {@link DockChild}ren.
 * <p>
 * The DockParent lays out the screen space along an implementation-specific
 * {@link TrackAxis}.
 */
public interface DockParent extends DockChild {
    /**
     * The name of the {@link #editableProperty()}.
     */
    @NonNull
    String EDITABLE_PROPERTY = "editable";

    @NonNull
    ObservableList<DockChild> getDockChildren();

    @Override
    default @NonNull ReadOnlyList<DockChild> getDockChildrenReadOnly() {
        return new ReadOnlyListWrapper<>(getDockChildren());
    }

    /**
     * Whether this dock parent is editable.
     *
     * @return true if this dock parent is editable.
     */
    @NonNull
    BooleanProperty editableProperty();

    /**
     * Returns whether the user can edit this dock parent.
     *
     * @return true if the user can edit this dock parent.
     */
    default boolean isEditable() {
        return editableProperty().get();
    }

    /**
     * Sets whether the user can edit this dock parent.
     *
     * @param value true if the user can edit this dock parent.
     */
    default void setEditable(boolean value) {
        editableProperty().set(value);
    }

    @NonNull
    TrackAxis getDockAxis();

    /**
     * Returns true if this parent dock resizes the items. If this method returns
     * true, a dock child should not provide resize controls.
     *
     * @return true if the track resizes items.
     */
    boolean isResizesDockChildren();
}
