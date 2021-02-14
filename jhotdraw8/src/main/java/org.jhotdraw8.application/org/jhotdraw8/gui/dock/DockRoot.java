/*
 * @(#)DockRoot.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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
 * The root node manages drag and drop of {@link Dockable} nodes, and
 * creates or destroys {@link Track} nodes that hold the {@link Dockable}s.
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
    ObjectProperty<Dockable> draggedDockable = new SimpleObjectProperty<>();

    static @NonNull ObjectProperty<Dockable> draggedDockableProperty() {
        return draggedDockable;
    }

    static @Nullable Dockable getDraggedDockable() {
        return draggedDockable.get();
    }

    static void setDraggedDockable(@Nullable Dockable value) {
        draggedDockable.set(value);
    }


    /**
     * Only {@link Dockable}s accepted by this filter can be docked.
     * <p>
     * This can be used to restrict docking to dockables that belong
     * to the same {@link Activity}.
     *
     * @return filter for accepting {@link Dockable}s
     */
    @NonNull
    ObjectProperty<Predicate<Dockable>> dockablePredicateProperty();

    default @NonNull Predicate<Dockable> getDockablePredicate() {
        return dockablePredicateProperty().get();
    }

    default void setDockablePredicate(@NonNull Predicate<Dockable> value) {
        dockablePredicateProperty().set(value);
    }

    @NonNull
    Parent getNode();
}
