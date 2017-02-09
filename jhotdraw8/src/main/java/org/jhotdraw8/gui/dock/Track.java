/* @(#)Track.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.gui.dock;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * A Track provides horizontal or vertical space for {@link Dock}s and other
 * {@code Track}s.
 * <p>
 * Actually, any JavaFX Node can be added to a Track, but only Docks and Tracks
 * will provide the drag and drop interactions with {@link DockItem}s and
 * {@link DockRoot}s.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface Track {

    ObservableList<Node> getItems();

    /**
     * Must return this.
     * @return this
     */
    default Node getNode() {
        return (Node) this;
    }

    Orientation getOrientation();
}
