/*
 * @(#)Dock.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.jhotdraw8.annotation.Nonnull;

/**
 * A {@code Dock} contains one or more {@link DockItem}s.
 *
 * @author Werner Randelshofer
 */
public interface Dock {

    ObservableList<DockItem> getItems();

    @Nonnull
    default Node getNode() {
        return (Node) this;
    }

    /**
     * Returns true if the user may add and remove items.
     *
     * @return true if editable by user
     */
    boolean isEditable();

    ObjectProperty<Track> trackProperty();

    default Track getTrack() {
        return trackProperty().get();
    }

    default void setTrack(Track value) {
        trackProperty().set(value);
    }
}
