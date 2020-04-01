package org.jhotdraw8.gui.dock;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jhotdraw8.annotation.NonNull;

import java.util.function.Predicate;

public abstract class AbstractDockPane
        extends AbstractDock
        implements DockPane {

    private ObjectProperty<Predicate<Dockable>> dockablePredicate = new SimpleObjectProperty<>(d -> true);

    @Override
    public @NonNull ObjectProperty<Predicate<Dockable>> dockablePredicateProperty() {
        return dockablePredicate;
    }
}
