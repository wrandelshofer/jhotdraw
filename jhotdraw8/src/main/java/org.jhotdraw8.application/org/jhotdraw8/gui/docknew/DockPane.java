package org.jhotdraw8.gui.docknew;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.function.Predicate;

public interface DockPane extends Dock {

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

    @NonNull
    static ObjectProperty<Dockable> draggedDockableProperty() {
        return draggedDockable;
    }

    @Nullable
    static Dockable getDraggedDockable() {
        return draggedDockable.get();
    }

    static void setDraggedDockable(@Nullable Dockable value) {
        draggedDockable.set(value);
    }


    /**
     * Only {@link Dockable}s accepted by this filter can be docked.
     *
     * @return filter for accepting {@link Dockable}s
     */
    @NonNull
    ObjectProperty<Predicate<Dockable>> dockablePredicateProperty();

    @NonNull
    default Predicate<Dockable> getDockablePredicate() {
        return dockablePredicateProperty().get();
    }

    default void setDockablePredicate(@NonNull Predicate<Dockable> value) {
        dockablePredicateProperty().set(value);
    }

    @NonNull
    Parent getNode();

}
