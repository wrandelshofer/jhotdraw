/*
 * @(#)Track.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import org.jhotdraw8.annotation.Nonnull;

/**
 * A Track provides horizontal or vertical space for {@link Dock}s and other
 * {@code Track}s.
 * <p>
 * Actually, any JavaFX Node can be added to a Track, but only Docks and Tracks
 * will provide the drag and drop interactions with {@link DockItem}s and
 * {@link DockRoot}s.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Track {

    /**
     * A track can contain Track and Dock items.
     * <p>
     * By convention a track updates the trackProperty of a Dock if it is added
     * or removed from its items list.
     *
     * @return the items
     */
    ObservableList<Node> getItems();

    /**
     * Must return this.
     *
     * @return this
     */
    @Nonnull
    default Node getNode() {
        return (Node) this;
    }

    Orientation getOrientation();

    /**
     * Returns true if this track resizes the items. If this method returns
     * true, an item of the track should not provide resize controls.
     *
     * @return true if the track resizes items.
     */
    default boolean resizesItems() {
        return true;
    }
}
