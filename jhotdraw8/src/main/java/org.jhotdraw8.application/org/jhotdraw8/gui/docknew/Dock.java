package org.jhotdraw8.gui.docknew;

import javafx.collections.ObservableList;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.collection.ReadOnlyListWrapper;

public interface Dock extends DockComponent {
    @NonNull
    ObservableList<DockComponent> getChildComponents();

    default boolean isEditable() {
        return true;
    }

    @NonNull
    DockAxis getAxis();


    @Override
    default ReadOnlyList<DockComponent> getChildComponentsReadOnly() {
        return new ReadOnlyListWrapper<>(getChildComponents());
    }
}
