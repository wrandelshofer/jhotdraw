/*
 * @(#)AbstractDockable.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.gui.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;

/**
 * Abstract base class for implementations of {@link Dockable}.
 */
public abstract class AbstractDockable implements Dockable {
    protected final ObjectProperty<DockParent> dockParent = new SimpleObjectProperty<>(this, DOCK_PARENT_PROPERTY);
    protected final ObjectProperty<Node> graphic = new SimpleObjectProperty<>(this, GRAPHIC_PROPERTY);
    protected final StringProperty text = new SimpleStringProperty(this, TEXT_PROPERTY);
    protected final BooleanProperty showing = new SimpleBooleanProperty(this, SHOWING_PROPERTY);

    @Override
    public @NonNull ObjectProperty<Node> graphicProperty() {
        return graphic;
    }

    @Override
    public @NonNull StringProperty textProperty() {
        return text;
    }

    @Override
    public @NonNull BooleanProperty showingProperty() {
        return showing;
    }

    @Override
    public @NonNull ObjectProperty<DockParent> dockParentProperty() {
        return dockParent;
    }

    @Override
    public @NonNull ReadOnlyList<DockChild> getDockChildrenReadOnly() {
        return ImmutableLists.emptyList();
    }

}
