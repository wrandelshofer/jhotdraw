/*
 * @(#)DockRoot.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;

import java.util.function.Predicate;

/**
 * The root node of a docking hierarchy.
 * <p>
 * The root node manages drag and drop of {@link DraggableDockChild} nodes, and
 * creates or destroys {@link Dock} nodes that hold the {@link DraggableDockChild}s.
 */
public interface DockRoot extends DockParent {
    /**
     * Data format used for dragging a DockItem with the drag board.
     * The value of this data format is the {@link System#identityHashCode(Object)}
     * of the dragged leaf.
     */
    DataFormat DOCKABLE_DATA_FORMAT = new DataFormat("application/x-jhotdraw8-dragged-dock-leaf");
    /**
     * We store the dragged item here, because we move the <i>reference</i>
     * of a DockItem with the drag board rather than a value of the DockItem.
     */
    ObjectProperty<DraggableDockChild> draggedDockable = new SimpleObjectProperty<>();

    @NonNull
    static ObjectProperty<DraggableDockChild> draggedDockableProperty() {
        return draggedDockable;
    }

    @Nullable
    static DraggableDockChild getDraggedDockable() {
        return draggedDockable.get();
    }

    static void setDraggedDockable(@Nullable DraggableDockChild value) {
        draggedDockable.set(value);
    }


    /**
     * Only {@link DraggableDockChild}s accepted by this filter can be docked.
     * <p>
     * This can be used to restrict docking to dockables that belong
     * to the same {@link Activity}.
     *
     * @return filter for accepting {@link DraggableDockChild}s
     */
    @NonNull
    ObjectProperty<Predicate<DraggableDockChild>> dockablePredicateProperty();

    @NonNull
    default Predicate<DraggableDockChild> getDockablePredicate() {
        return dockablePredicateProperty().get();
    }

    default void setDockablePredicate(@NonNull Predicate<DraggableDockChild> value) {
        dockablePredicateProperty().set(value);
    }

    @NonNull
    Parent getNode();

}
