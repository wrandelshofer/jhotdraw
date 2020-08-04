/*
 * @(#)AbstractDockParent.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.gui.CustomSkin;

/**
 * Abstract base class for implementations of {@link DockParent}.
 */
public abstract class AbstractDockParent
        extends Control
        implements DockParent {
    protected final BooleanProperty showing = new SimpleBooleanProperty(this, SHOWING_PROPERTY);
    protected final BooleanProperty editable = new SimpleBooleanProperty(this, EDITABLE_PROPERTY, true);
    protected final ObjectProperty<DockParent> dockParent = new SimpleObjectProperty<>(this, DOCK_PARENT_PROPERTY);
    protected final ObservableList<DockChild> dockChildren = FXCollections.observableArrayList();
    private final ReadOnlyObjectProperty<Node> node = new ReadOnlyObjectWrapper<Node>(this, NODE_PROPERTY, this).getReadOnlyProperty();

    public AbstractDockParent() {
        setSkin(new CustomSkin<>(this));
        dockChildren.addListener((ListChangeListener.Change<? extends DockItem> change) -> {
            while (change.next()) {
                for (DockItem removed : change.getRemoved()) {
                    removed.setDockParent(null);
                }
                for (DockItem added : change.getAddedSubList()) {
                    if (added.getDockParent() != null) {
                        throw new IllegalStateException("Added still has parent " + added);
                    }
                    if (added instanceof DockRoot) {
                        throw new IllegalStateException("Added DockPane cannot have parent " + added);
                    }
                    added.setDockParent(this);
                }
            }
        });
        setMinHeight(10);
        setMinWidth(10);
        setMaxHeight(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
    }

    @Override
    public @NonNull ObjectProperty<DockParent> dockParentProperty() {
        return dockParent;
    }


    @Override
    public @NonNull ObservableList<DockChild> getDockChildren() {
        return dockChildren;
    }


    @Override
    public @NonNull ReadOnlyObjectProperty<Node> nodeProperty() {
        return node;
    }


    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        for (Node child : getChildren()) {
            if (child.isManaged()) {
                child.resizeRelocate(0, 0, getWidth(), getHeight());
            }
        }
    }


    @NonNull
    @Override
    public Parent getNode() {
        return this;
    }

    @Override
    public @NonNull BooleanProperty showingProperty() {
        return showing;
    }

    @Override
    public @NonNull BooleanProperty editableProperty() {
        return editable;
    }
}
