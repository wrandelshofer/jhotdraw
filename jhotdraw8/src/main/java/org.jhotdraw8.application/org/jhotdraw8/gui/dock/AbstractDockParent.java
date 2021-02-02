/*
 * @(#)AbstractDockParent.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import org.jhotdraw8.annotation.NonNull;

import java.util.List;


/**
 * Abstract base class for implementations of {@link DockParent}.
 */
public abstract class AbstractDockParent
        extends Region
        implements DockParent {
    protected final BooleanProperty showing = new SimpleBooleanProperty(this, SHOWING_PROPERTY);
    protected final BooleanProperty editable = new SimpleBooleanProperty(this, EDITABLE_PROPERTY, true);
    protected final ObjectProperty<DockParent> dockParent = new SimpleObjectProperty<>(this, DOCK_PARENT_PROPERTY);
    protected final ObservableList<DockChild> dockChildren = FXCollections.observableArrayList();

    public AbstractDockParent() {
        dockChildren.addListener((ListChangeListener.Change<? extends DockChild> change) -> {
            while (change.next()) {
                for (DockChild removed : change.getRemoved()) {
                    removed.setDockParent(null);
                }
                for (DockChild added : change.getAddedSubList()) {
                    if (added.getDockParent() != null) {
                        added.getDockParent().getDockChildren().remove(added);
                    }
                    added.setDockParent(this);
                }
            }
        });
    }

    @Override
    public @NonNull ObjectProperty<DockParent> dockParentProperty() {
        return dockParent;
    }


    @Override
    public @NonNull ObservableList<DockChild> getDockChildren() {
        return dockChildren;
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

    @Override
    protected void layoutChildren() {
        List<Node> managed = getManagedChildren();
        final double width = getWidth();
        double height = getHeight();
        Insets insets = getInsets();
        double top = insets.getTop();
        double right = insets.getRight();
        double left = insets.getLeft();
        double bottom = insets.getBottom();
        double contentWidth = width - left - right;
        double contentHeight = height - top - bottom;
        double baselineOffset = 0;
        HPos alignHpos = HPos.LEFT;
        VPos alignVpos = VPos.TOP;

        for (int i = 0, size = managed.size(); i < size; i++) {
            Node child = managed.get(i);
            layoutInArea(child, left, top,
                    contentWidth, contentHeight,
                    baselineOffset, null,
                    alignHpos,
                    alignVpos);
        }
    }
}
