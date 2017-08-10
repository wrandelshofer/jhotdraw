/* @(#)Dock.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.ObjectProperty;
import org.jhotdraw8.gui.dock.DockItem;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * A {@code Dock} contains one or more {@link DockItem}s.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Dock {

    ObservableList<DockItem> getItems();

    default Node getNode() {
        return (Node) this;
    }
    
    /** Returns true if the user may add and remove items.
     * @return  true if editable by user
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
