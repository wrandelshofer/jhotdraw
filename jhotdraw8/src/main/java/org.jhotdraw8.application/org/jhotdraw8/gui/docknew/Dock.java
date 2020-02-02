package org.jhotdraw8.gui.docknew;

import javafx.collections.ObservableList;
import org.jhotdraw8.annotation.NonNull;

public interface Dock extends DockComponent {
    @NonNull
    ObservableList<DockComponent> getChildComponents();

    boolean isEditable();

    @NonNull
    DockAxis getAxis();
}
